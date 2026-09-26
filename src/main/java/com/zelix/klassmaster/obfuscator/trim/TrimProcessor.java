package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.SignatureTypeReferences;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.LibraryOverrideCollector;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ExclusionHandlerBase;
import com.zelix.klassmaster.obfuscator.exclude.ExistingSerializedClassesHandler;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultTrimExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.UniqueWorkQueue;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TrimProcessor extends ExclusionHandlerBase {
    public static final String PUBLIC_API_TRIM_EXCLUDE = "trimExclude  public *.* and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* public * and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* public *(*);"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String PUBLIC_PROTECTED_API_TRIM_EXCLUDE = "trimExclude  public *.* and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* public * and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* protected * and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* public *(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* protected *(*);"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String MIDLET_TRIM_EXCLUDE = "trimExclude *.* extends javax.microedition.midlet.MIDlet;" + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String PACKAGE_INFO_TRIM_EXCLUDE = "trimExclude *.package-info;" + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String DEFAULT_TRIM_EXCLUDE = "trimExclude  *.* <init>() and //No-arg constructors.  Some APIs access them indirectly.  Eg. java.io.Externalizable "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* extends java.rmi.server.RemoteStub <init>(java.rmi.server.RemoteRef) and //RMI"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*_Skel implements java.rmi.server.Skeleton and //RMI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*_Stub extends java.rmi.server.RemoteStub and //RMI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*_Stub extends javax.rmi.CORBA.Stub and //RMI with iiop "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*_Tie implements javax.rmi.CORBA.Tie and //RMI with iiop "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             interface *.*^ implements java.rmi.Remote *(*) throws java.rmi.RemoteException and //RMI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* private readObject(java.io.ObjectInputStream) and   //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* private writeObject(java.io.ObjectOutputStream) and //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* static final long serialVersionUID and              //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* writeReplace() and                                  //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* readResolve() and                                   //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* readObjectNoData() throws java.io.ObjectStreamException and //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* java.io.ObjectStreamField[] serialPersistentFields and  //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* implements javax.servlet.Servlet and  //Servlets "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* extends java.lang.Enum public * and     //enum "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* extends java.lang.Enum public values() and  //enum "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* extends java.lang.Enum public valueOf(java.lang.String) and  //enum "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EJBObject public *(*) and               //EJB Remote "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EJBLocalObject public *(*) and          //EJB Local "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EJBHome public create(*) and            //EJB Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EJBHome public find*(*) and             //EJB Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EJBLocalHome public create(*) and       //EJB Local Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EJBLocalHome public find*(*) and        //EJB Local Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*^ implements javax.ejb.EnterpriseBean public *(*) and          //EJB Bean "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.ejb.Remote *.*^ public *(*) and                              "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.ejb.RemoteHome *.*^ public *(*) and                          "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.ejb.Stateless *.*^ public *(*) and                           "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.ejb.Stateful *.*^ public *(*) and                            "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.ejb.MessageDriven *.*^ public *(*) and                       "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.persistence.Entity *.*^ public *(*) and                      "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.persistence.Table *.*^ public *(*) and                       "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.persistence.Entity *.* @javax.persistence.Column * and       "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* @javax.persistence.Id * and                                     "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* @javax.persistence.Id *(*) and                                  "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.persistence.IdClass(value=*.*^) public *(*) and              "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.jws.WebService *.*^ @javax.jws.WebMethod public *(*) and     "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* static createUI(javax.swing.JComponent) and                  //GUI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* extends javax.swing.JComponent getUI()  and                  //Pluggable L&F"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlType *.* * and             //JAXB"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlType *.* public get*() and "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlType *.* public is*() and "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlType *.* public set*(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlRootElement *.* private * and       //JAXB"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlRootElement *.* public get*() and "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlRootElement *.* public is*() and "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             @javax.xml.bind.annotation.XmlRootElement *.* public set*(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* @javax.annotation.PostConstructor *(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* @javax.annotation.PreDestroy *(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*MBean public *(*) and   //JMX"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.*MXBean public *(*) and   //JMX"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* containing{@java.beans.ConstructorProperties *(*)} public get*() and //JMX"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* @javafx.fxml.FXML * and //FXML"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* @javafx.fxml.FXML *(*) and //FXML"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* $deserializeLambda$(java.lang.invoke.SerializedLambda) and //Serialized lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             *.* lambda$*(*) //lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "             ;"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public TwoKeyMap pendingInstanceFields;
    public ProgramClass[] allClasses;
    public boolean trimPerformed;
    public MethodInfo[] allMethods;
    public TwoKeyMap pendingStaticMethods;
    public FieldInfo[] allFields;
    public List keptFields;
    public Set annotationTypes;
    public TwoKeyMap trimmedFieldsByClass;
    public List trimmedFields;
    public TwoKeyMap pendingInstanceMethods;
    public TwoKeyMap pendingStaticFields;
    public List keptMethods;
    public List trimmedMethods;
    public HashSet instantiatedClasses;
    public TwoKeyMap trimmedMethodsByClass;
    public Set annotationRetainedClasses;
    public final boolean deleteAnnotationAttributes;
    public ListMultimap extraDependencies;
    public ClasspathClassLoader classpathClassLoader;
    public LibraryOverrideCollector libraryOverrideCollector;

    public void markFieldReachable(FieldInfo fieldInfo, UniqueWorkQueue uniqueWorkQueue) throws ZkmException, IOException {
        ProgramClass programClass1 = fieldInfo.getProgramClass();
        FieldSignature fieldSignature = fieldInfo.getSignature();
        if (programClass1.hasVersionedVariants()) {
            this.matchField(fieldInfo, (String) null);
            Iterator iterator = programClass1.getVersionedVariants().iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                FieldInfo fieldInfo1 = (FieldInfo) classFileBase.findField(fieldSignature);
                if (fieldInfo1 != null) {
                    this.matchField(fieldInfo1, (String) null);
                }
            }
        } else if (programClass1.isVersionedVariant()) {
            ProgramClass programClass2 = (ProgramClass) programClass1.getBaseVersionClass();
            FieldInfo fieldInfo3 = programClass2.findFieldBySignature(fieldSignature);
            if (fieldInfo3 != null) {
                this.matchField(fieldInfo3, (String) null);
            }

            Iterator iterator1 = programClass2.getVersionedVariants().iterator();

            while (iterator1.hasNext()) {
                FieldInfo fieldInfo2 = (FieldInfo) ((ClassFileBase) iterator1.next()).findField(fieldSignature);
                if (fieldInfo2 != null) {
                    this.matchField(fieldInfo2, (String) null);
                }
            }
        } else {
            this.matchField(fieldInfo, (String) null);
        }

        ProgramClass programClass3 = fieldInfo.getTypeProgramClass();
        if (programClass3 != null) {
            this.markClassReachable(programClass3, uniqueWorkQueue, true);
        }

        SignatureTypeReferences signatureTypeReferences = fieldInfo.getSignatureTypeReferences();
        this.markSignatureReferencesReachable(signatureTypeReferences, uniqueWorkQueue);
    }

    public void propagateReachability() throws ZkmException, IOException {
        UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue();
        Iterator iterator = new ArrayList(super.excludedFields.keySet()).iterator();

        while (iterator.hasNext()) {
            FieldInfo fieldInfo = (FieldInfo) iterator.next();
            ProgramClass programClass1 = fieldInfo.getProgramClass();
            if (super.excludedClasses.containsKey(programClass1)) {
                ProgramClass programClass2 = fieldInfo.getTypeProgramClass();
                if (programClass2 != null) {
                    this.markClassReachable(programClass2, uniqueWorkQueue, true);
                }

                SignatureTypeReferences signatureTypeReferences = fieldInfo.getSignatureTypeReferences();
                this.markSignatureReferencesReachable(signatureTypeReferences, uniqueWorkQueue);
            } else if (!fieldInfo.isStatic()) {
                this.pendingInstanceFields.putValue(programClass1, fieldInfo, fieldInfo);
            } else {
                this.pendingStaticFields.putValue(programClass1, fieldInfo, fieldInfo);
            }
        }

        Iterator iterator3 = this.excludedMethods.keySet().iterator();

        while (iterator3.hasNext()) {
            MethodInfo methodInfo4 = (MethodInfo) iterator3.next();
            ProgramClass programClass6 = methodInfo4.getOwnerProgramClass();
            if (this.instantiatedClasses.contains(programClass6)) {
                uniqueWorkQueue.enqueue(methodInfo4);
            } else if (super.excludedClasses.containsKey(programClass6) && methodInfo4.isStatic()) {
                uniqueWorkQueue.enqueue(methodInfo4);
            } else if (methodInfo4.isStatic()) {
                this.pendingStaticMethods.putValue(programClass6, methodInfo4, methodInfo4);
            } else {
                this.pendingInstanceMethods.putValue(programClass6, methodInfo4, methodInfo4);
            }
        }

        HashSet hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.allClasses.length + 5));

        while (!uniqueWorkQueue.isEmpty()) {
            MethodInfo methodInfo5 = (MethodInfo) uniqueWorkQueue.dequeue();
            ProgramClass programClass7 = methodInfo5.getOwnerProgramClass();
            if (hashSet1.add(methodInfo5)) {
                LibraryOverrideCollector libraryOverrideCollector1;
                if (!programClass7.hasVersionedVariants() && !programClass7.isVersionedVariant()) {
                    libraryOverrideCollector1 = this.libraryOverrideCollector;
                } else {
                    MethodSignature methodSignature1 = methodInfo5.getSignature();
                    Iterator iterator1 = programClass7.getAllVersions().iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                        MethodInfo methodInfo1 = (MethodInfo) classFileBase.findMethod(methodSignature1);
                        if (methodInfo1 != null && !hashSet1.contains(methodInfo1) && !uniqueWorkQueue.contains(methodInfo1)) {
                            if (!methodInfo1.isStatic()) {
                                this.instantiatedClasses.add(methodInfo1.getOwnerProgramClass());
                            }

                            this.markMethodReachable(methodInfo1, uniqueWorkQueue);
                        }
                    }

                    libraryOverrideCollector1 = this.libraryOverrideCollector;
                }

                Enumeration enumeration = libraryOverrideCollector1.enumerateOverridingMethods(methodInfo5);

                while (enumeration.hasMoreElements()) {
                    MethodInfo methodInfo6 = (MethodInfo) enumeration.nextElement();
                    ProgramClass programClass8 = methodInfo6.getOwnerProgramClass();
                    if (programClass7.isVersionedVariant() && programClass8.hasVersionedVariants()) {
                        ProgramClass programClass9 = programClass8;
                        programClass8 = (ProgramClass) programClass8.selectVersionForRelease(programClass7.getReleaseVersion());
                        if (programClass9 != programClass8) {
                            MethodInfo methodInfo2 = methodInfo6;
                            methodInfo6 = programClass8.findMethodBySignature(methodInfo6.getSignature());
                            ZkmAssert.assertNull(
                                    methodInfo6,
                                    "Multi-version class '"
                                            + programClass8.getLocationName()
                                            + "' does not contain a method '"
                                            + methodInfo2.toOriginalDisplayString()
                                            + "' while processing class '"
                                            + programClass7.getLocationName()
                                            + "' (A)."
                            );
                        }
                    }

                    if (this.instantiatedClasses.contains(programClass8)) {
                        this.markMethodReachable(methodInfo6, uniqueWorkQueue);
                    } else {
                        this.matchMethod(methodInfo6);
                        this.pendingInstanceMethods.putValue(programClass8, methodInfo6, methodInfo6);
                    }
                }

                Set set1 = this.libraryOverrideCollector.getOverriddenMethods(methodInfo5);
                Iterator iterator4 = set1.iterator();

                while (iterator4.hasNext()) {
                    MethodInfo methodInfo7 = (MethodInfo) iterator4.next();
                    ProgramClass programClass10 = methodInfo7.getOwnerProgramClass();
                    if (programClass7.isVersionedVariant() && programClass10.hasVersionedVariants()) {
                        ProgramClass programClass3 = programClass10;
                        programClass10 = (ProgramClass) programClass10.selectVersionForRelease(programClass7.getReleaseVersion());
                        if (programClass3 != programClass10) {
                            MethodInfo methodInfo3 = methodInfo7;
                            methodInfo7 = programClass10.findMethodBySignature(methodInfo7.getSignature());
                            ZkmAssert.assertNull(
                                    methodInfo7,
                                    "Multi-version class '"
                                            + programClass10.getLocationName()
                                            + "' does not contain a method '"
                                            + methodInfo3.toOriginalDisplayString()
                                            + "' while processing class '"
                                            + programClass7.getLocationName()
                                            + "' (B)."
                            );
                        }
                    }

                    if (this.instantiatedClasses.contains(programClass10)) {
                        this.markMethodReachable(methodInfo7, uniqueWorkQueue);
                    } else {
                        this.matchMethod(methodInfo7);
                        this.pendingInstanceMethods.putValue(programClass10, methodInfo7, methodInfo7);
                    }
                }

                ArrayList arrayList = methodInfo5.getDescriptorProgramClasses();
                Iterator iterator5 = arrayList.iterator();

                while (iterator5.hasNext()) {
                    ProgramClass programClass11 = (ProgramClass) iterator5.next();
                    if (programClass7.isVersionedVariant() && programClass11.hasVersionedVariants()) {
                        programClass11 = (ProgramClass) programClass11.selectVersionForRelease(programClass7.getReleaseVersion());
                    }

                    this.markClassReachable(programClass11, uniqueWorkQueue, true);
                }

                SignatureTypeReferences signatureTypeReferences1 = methodInfo5.getSignatureTypeReferences();
                this.markSignatureReferencesReachable(signatureTypeReferences1, uniqueWorkQueue);
                HashSet hashSet2 = ZkmUtils.createHashSet();
                HashSet hashSet3 = ZkmUtils.createHashSet();
                HashSet hashSet4 = ZkmUtils.createHashSet();
                HashSet hashSet = ZkmUtils.createHashSet();
                methodInfo5.collectReferencedProgramClasses(hashSet2, hashSet3, hashSet4, hashSet);
                hashSet3.removeAll(hashSet2);
                Iterator iterator2 = hashSet2.iterator();

                while (iterator2.hasNext()) {
                    ProgramClass programClass4 = (ProgramClass) iterator2.next();
                    this.markClassReachable(programClass4, uniqueWorkQueue, true);
                }

                iterator2 = hashSet3.iterator();

                while (iterator2.hasNext()) {
                    ProgramClass programClass12 = (ProgramClass) iterator2.next();
                    this.markClassReachable(programClass12, uniqueWorkQueue, false);
                }

                iterator2 = hashSet4.iterator();

                while (iterator2.hasNext()) {
                    FieldInfo fieldInfo1 = (FieldInfo) iterator2.next();
                    this.markFieldReachable(fieldInfo1, uniqueWorkQueue);
                }

                iterator2 = hashSet.iterator();

                while (iterator2.hasNext()) {
                    MethodInfo methodInfo8 = (MethodInfo) iterator2.next();
                    this.markMethodReachable(methodInfo8, uniqueWorkQueue);
                }

                List list1 = this.extraDependencies.getValues(methodInfo5);
                if (list1 != null) {
                    ClassBeforeMemberComparator classBeforeMemberComparator = new ClassBeforeMemberComparator(this);
                    Collections.sort(list1, classBeforeMemberComparator);

                    for (int i = 0; i < list1.size(); i++) {
                        Object object = list1.get(i);
                        if (object instanceof ProgramClass) {
                            ProgramClass programClass5 = (ProgramClass) object;
                            this.markClassReachable(programClass5, uniqueWorkQueue, true);
                        } else if (object instanceof FieldInfo) {
                            FieldInfo fieldInfo2 = (FieldInfo) object;
                            this.markFieldReachable(fieldInfo2, uniqueWorkQueue);
                        } else if (object instanceof MethodInfo) {
                            MethodInfo methodInfo9 = (MethodInfo) object;
                            this.markMethodReachable(methodInfo9, uniqueWorkQueue);
                        }
                    }
                }
            }
        }
    }

    public void matchMethod(MethodInfo methodInfo1) throws ZkmException, IOException {
        this.matchMethod(methodInfo1, (String) null);
    }

    public boolean addAnnotationRetainedClass(ClassFileBase classFileBase, String string) throws ZkmException, IOException {
        if (this.annotationRetainedClasses == null) {
            this.annotationRetainedClasses = ZkmUtils.createHashSet();
        }

        boolean bl = this.annotationRetainedClasses.add(classFileBase);
        if (bl && super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
            super.logWriter
                    .println("\tMatching for annotation attribute exclusion class \"" + this.describeClass(classFileBase) + "\" because of \"" + string + "\"");
        }

        return bl;
    }

    public final void unmatchMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        if (!methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.includedMethods.put(methodInfo1, programClass1);
                if (super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
                    super.logWriter
                            .println(
                                    "\tUnmatching method \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                            + "\" in class \""
                                            + this.describeClass(methodInfo1.getOwnerProgramClass())
                                            + "\" for trim because of \""
                                            + string
                                            + "\""
                            );
                }
            }
        }
    }

    public void reportTrimmedMembers(PrintWriter printWriter, ClassHierarchyQuery classHierarchyQuery, boolean bl) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList(super.includedClasses.size());
        Iterator iterator = super.includedClasses.keySet().iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            arrayList.add(programClass1);
        }

        Collections.sort(arrayList);
        String string3 = bl ? "Would trim" : "Trimmed";
        int bb = arrayList.size();

        for (int i = 0; i < bb; i++) {
            ProgramClass programClass2 = (ProgramClass) arrayList.get(i);
            String string = programClass2.hasReleaseVersion() ? " (multirelease version " + programClass2.getReleaseVersion() + ")" : "";
            if (!bl && super.scriptEnvironment.isVerbose()) {
                super.logWriter.println("\tTrimmed class \"" + AbstractExclusionSpec.formatClass(programClass2, classHierarchyQuery, false) + "\"" + string);
            }

            if (printWriter != null) {
                printWriter.println(string3 + " class \"" + AbstractExclusionSpec.formatClass(programClass2, classHierarchyQuery, false) + "\"" + string);
            }
        }

        MemberNameComparator memberNameComparator = new MemberNameComparator(this);
        boolean bl1 = false;
        ArrayList arrayList1 = new ArrayList(super.includedFields.size());
        Enumeration enumeration = this.enumerateTrimmedFieldsByClass();

        while (enumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
            ProgramClass programClass3 = fieldInfo.getProgramClass();
            if (this.isClassExcluded(programClass3)) {
                if (fieldInfo.isStatic()) {
                    bl1 = true;
                }

                arrayList1.add(fieldInfo);
            }
        }

        Collections.sort(arrayList1, memberNameComparator);
        if (bl1) {
            if (!bl && super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println(
                                "\tNB: Some \"static\" fields have been trimmed. Compilers can optimize accesses to the values of \"static\" fields such that the fields are never directly accessed."
                        );
            }

            if (printWriter != null) {
                printWriter.println(
                        "NB: Some \"static\" fields "
                                + (bl ? "would be" : "have been")
                                + " trimmed. Compilers can optimize accesses to the values of \"static\" fields such that the fields are never directly accessed."
                );
            }
        }

        int bc = arrayList1.size();

        for (int i = 0; i < bc; i++) {
            FieldInfo fieldInfo1 = (FieldInfo) arrayList1.get(i);
            ProgramClass programClass4 = fieldInfo1.getProgramClass();
            String string1 = programClass4.hasReleaseVersion() ? " (multirelease version " + programClass4.getReleaseVersion() + ")" : "";
            if (!bl && super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println(
                                "\tTrimmed field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo1, this)
                                        + "\" in class \""
                                        + AbstractExclusionSpec.formatClass(programClass4, classHierarchyQuery, false)
                                        + "\""
                                        + string1
                        );
            }

            if (printWriter != null) {
                printWriter.println(
                        string3
                                + " field \""
                                + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo1, this)
                                + "\" in class \""
                                + AbstractExclusionSpec.formatClass(programClass4, classHierarchyQuery, false)
                                + "\""
                                + string1
                );
            }
        }

        ArrayList arrayList2 = new ArrayList(this.includedMethods.size());
        Enumeration enumeration1 = this.enumerateTrimmedMethodsByClass();

        while (enumeration1.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration1.nextElement();
            ProgramClass programClass6 = methodInfo1.getOwnerProgramClass();
            if (this.isClassExcluded(programClass6)) {
                arrayList2.add(methodInfo1);
            }
        }

        Collections.sort(arrayList2, memberNameComparator);
        int be = arrayList2.size();

        for (int i = 0; i < be; i++) {
            MethodInfo methodInfo2 = (MethodInfo) arrayList2.get(i);
            ProgramClass programClass5 = methodInfo2.getOwnerProgramClass();
            String string2 = programClass5.hasReleaseVersion() ? " (multirelease version " + programClass5.getReleaseVersion() + ")" : "";
            if (!bl && super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println(
                                "\tTrimmed method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo2, this)
                                        + "\" in class \""
                                        + AbstractExclusionSpec.formatClass(programClass5, classHierarchyQuery, false)
                                        + "\""
                                        + string2
                        );
            }

            if (printWriter != null) {
                printWriter.println(
                        string3
                                + " method \""
                                + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo2, this)
                                + "\" in class \""
                                + AbstractExclusionSpec.formatClass(programClass5, classHierarchyQuery, false)
                                + "\""
                                + string2
                );
            }
        }
    }

    public boolean matchClass(ProgramClass programClass1) throws ZkmException, IOException {
        return this.excludeClass(programClass1, (String) null);
    }

    public static ParameterListStatement parsePublicApiTrimExclude(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseTrimExcludeText(PUBLIC_API_TRIM_EXCLUDE, scriptEnvironment1);
    }

    public ArrayList getKeptClassesWithTrimmedFields() {
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.trimmedFieldsByClass.keys();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            if (super.excludedClasses.containsKey(programClass1)) {
                arrayList.add(programClass1);
            }
        }

        return arrayList;
    }

    public void markClassVersionReachable(ProgramClass programClass1, UniqueWorkQueue uniqueWorkQueue, boolean bl) throws ZkmException, IOException {
        boolean bl1 = this.matchClass(programClass1);
        boolean bl2 = false;
        if (bl) {
            bl2 = this.instantiatedClasses.add(programClass1);
        }

        if (bl1 || bl2) {
            this.markClassHierarchyReachable(programClass1, uniqueWorkQueue);
        }

        if (bl1) {
            this.releasePendingFields(uniqueWorkQueue, this.pendingStaticFields, programClass1);
            this.releasePendingMethods(uniqueWorkQueue, this.pendingStaticMethods, programClass1);
        }

        if (bl2) {
            this.releasePendingFields(uniqueWorkQueue, this.pendingInstanceFields, programClass1);
            this.releasePendingMethods(uniqueWorkQueue, this.pendingInstanceMethods, programClass1);
        }
    }

    public boolean hasAnnotationRetainedClasses() {
        return this.annotationRetainedClasses != null && this.annotationRetainedClasses.size() > 0;
    }

    public void registerCandidateClass(ProgramClass programClass1, List list1) {
        super.includedClasses.put(programClass1, programClass1);
        list1.add(programClass1);
        ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

        while (arrayEnumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
            super.includedFields.put(fieldInfo, fieldInfo.getProgramClass());
        }

        ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

        while (arrayEnumeration1.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
            if (!methodInfo1.isStaticInitializer()) {
                this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            } else {
                this.excludedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }
    }

    public void enqueueClassVersionReachable(ProgramClass programClass1, UniqueWorkQueue uniqueWorkQueue, UniqueWorkQueue uniqueWorkQueue1, boolean bl) throws ZkmException, IOException {
        boolean bl1 = this.matchClass(programClass1);
        boolean bl2 = false;
        if (bl) {
            bl2 = this.instantiatedClasses.add(programClass1);
        }

        if (bl1) {
            this.releasePendingFields(uniqueWorkQueue1, this.pendingStaticFields, programClass1);
            this.releasePendingMethods(uniqueWorkQueue1, this.pendingStaticMethods, programClass1);
        }

        if (bl2) {
            this.releasePendingFields(uniqueWorkQueue1, this.pendingInstanceFields, programClass1);
            this.releasePendingMethods(uniqueWorkQueue1, this.pendingInstanceMethods, programClass1);
        }

        if (bl1 || bl2) {
            uniqueWorkQueue.enqueue(programClass1);
        }
    }

    public void performTrim() throws ZkmException, IOException {
        if (this.classRepository.hasProgramClasses()) {
            this.propagateReachability();
            this.matchOverridingMethods();
            this.collectTrimmedMembers();
            this.trimPerformed = true;
        }
    }

    public EnumerableMap getTrimmedFieldsOfClass(Object object) {
        Map map1 = this.trimmedFieldsByClass.getInnerMap(object);
        return map1 != null ? new EnumerableMap(map1) : null;
    }

    public static ParameterListStatement parseInternalDefaultTrimExclude(ScriptEnvironment scriptEnvironment1, Throwable throwable) throws ZkmException, IOException {
        String string = scriptEnvironment1.getDefaultTrimExcludeFile();
        if (throwable != null) {
            System.err.println("\"" + string + "\" had a parse error and will be ignored. See \"" + scriptEnvironment1.getLogFileName() + "\" for more detail.");
            scriptEnvironment1.logWarning(
                    "\""
                            + string
                            + "\" had a parse error :"
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + throwable.getMessage()
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + "Will use the internal default trim statement and ignore \""
                            + string
                            + "\"."
            );
        }

        BufferedReader bufferedReader = new BufferedReader(new StringReader(DEFAULT_TRIM_EXCLUDE));

        try {
            return parseDefaultTrimExcludeInput(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
        }

        return null;
    }

    public final void matchField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) super.includedFields.remove(fieldInfo);
        if (programClass1 != null) {
            super.excludedFields.put(fieldInfo, programClass1);
            if (super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tMatching field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + this.describeClass(fieldInfo.getProgramClass())
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public void markSignatureReferencesReachable(SignatureTypeReferences signatureTypeReferences, UniqueWorkQueue uniqueWorkQueue) throws ZkmException, IOException {
        Enumeration enumeration = signatureTypeReferences.enumerateAnnotationClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            this.markClassReachable(programClass1, uniqueWorkQueue, true);
        }

        enumeration = signatureTypeReferences.enumerateReferencedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass2 = (ProgramClass) enumeration.nextElement();
            this.markClassReachable(programClass2, uniqueWorkQueue, false);
        }

        enumeration = signatureTypeReferences.enumerateReferencedFields();

        while (enumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
            this.markFieldReachable(fieldInfo, uniqueWorkQueue);
        }

        enumeration = signatureTypeReferences.enumerateReferencedMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
            this.markMethodReachable(methodInfo1, uniqueWorkQueue);
        }
    }

    public final void matchMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tMatching method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(methodInfo1.getOwnerProgramClass())
                                        + (string != null && string.length() > 0 ? "\" because of \"" + string + "\"" : "\"")
                        );
            }
        }
    }

    public Enumeration enumerateAnnotationTypes() {
        return Collections.enumeration(this.annotationTypes);
    }

    public final Enumeration enumerateTrimmedMethods() {
        return Collections.enumeration(this.trimmedMethods);
    }

    public static ParameterListStatement parsePackageInfoTrimExclude(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseTrimExcludeText(PACKAGE_INFO_TRIM_EXCLUDE, scriptEnvironment1);
    }

    public void markClassReachable(ProgramClass programClass1, UniqueWorkQueue uniqueWorkQueue, boolean bl) throws ZkmException, IOException {
        if (programClass1.hasVersionedVariants()) {
            this.markClassVersionReachable(programClass1, uniqueWorkQueue, bl);
            Iterator iterator = programClass1.getVersionedVariants().iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                this.markClassVersionReachable((ProgramClass) classFileBase, uniqueWorkQueue, bl);
            }
        } else if (programClass1.isVersionedVariant()) {
            ProgramClass programClass2 = (ProgramClass) programClass1.getBaseVersionClass();
            this.markClassVersionReachable(programClass2, uniqueWorkQueue, bl);
            Iterator iterator1 = programClass2.getVersionedVariants().iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                this.markClassVersionReachable((ProgramClass) classFileBase1, uniqueWorkQueue, bl);
            }
        } else {
            this.markClassVersionReachable(programClass1, uniqueWorkQueue, bl);
        }
    }

    @Override
    public final Enumeration getCandidateMethods() {
        return new ArrayEnumeration(this.allMethods);
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        Object object = super.includedClasses.remove(programClass1);
        if (object != null) {
            super.excludedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
                super.logWriter.println("\tMatching class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }

        return object != null;
    }

    public void matchOverridingMethods() throws ZkmException, IOException {
        Iterator iterator = ZkmUtils.createHashSetFrom(this.excludedMethods.keySet()).iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            Iterator iterator1 = this.libraryOverrideCollector.getOverriddenMethods(methodInfo1).iterator();

            while (iterator1.hasNext()) {
                MethodInfo methodInfo2 = (MethodInfo) iterator1.next();
                if (this.includedMethods.containsKey(methodInfo2)) {
                    this.matchMethod(methodInfo2);
                }
            }
        }
    }

    @Override
    public final Enumeration getCandidateFields() {
        return new ArrayEnumeration(this.allFields);
    }

    public void matchLibraryOverrideMethods() throws ZkmException, IOException {
        MethodInfo[] methodInfos = this.libraryOverrideCollector.getLibraryOverridingMethods();

        for (int i = 0; i < methodInfos.length; i++) {
            MethodInfo methodInfo1 = methodInfos[i];
            this.matchMethod(methodInfo1);
            ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
            if (methodInfo1.isStatic()) {
                if (!super.excludedClasses.containsKey(programClass1)) {
                    this.pendingStaticMethods.putValue(programClass1, methodInfo1, methodInfo1);
                }
            } else if (!this.instantiatedClasses.contains(programClass1)) {
                this.pendingInstanceMethods.putValue(programClass1, methodInfo1, methodInfo1);
            }
        }
    }

    public static ParameterListStatement parsePublicProtectedApiTrimExclude(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseTrimExcludeText(PUBLIC_PROTECTED_API_TRIM_EXCLUDE, scriptEnvironment1);
    }

    public void collectAnnotationTypes() {
        this.annotationTypes = ZkmUtils.createHashSet();

        for (ProgramClass programClass1 : this.allClasses) {
            ProgramClass programClass2;
            Set set1;
            if (programClass1.isAnnotation()) {
                this.annotationTypes.add(programClass1);
                programClass2 = programClass1;
                set1 = this.annotationTypes;
            } else {
                programClass2 = programClass1;
                set1 = this.annotationTypes;
            }

            programClass2.collectAttributeReferencedClasses(set1);
        }
    }

    public static ParameterListStatement parseTrimExcludeText(String string, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(string));

        try {
            return parseDefaultTrimExcludeInput(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
        }

        return null;
    }

    public EnumerableMap getTrimmedMethodsOfClass(Object object) {
        Map map1 = this.trimmedMethodsByClass.getInnerMap(object);
        return map1 != null ? new EnumerableMap(map1) : null;
    }

    public Enumeration enumerateTrimmedFieldsByClass() {
        return this.trimmedFieldsByClass.distinctValues();
    }

    public void markFieldAndTypesReachable(FieldInfo fieldInfo, UniqueWorkQueue uniqueWorkQueue) throws ZkmException, IOException {
        this.matchField(fieldInfo, (String) null);
        ProgramClass programClass1 = fieldInfo.getTypeProgramClass();
        if (programClass1 != null) {
            this.matchClass(programClass1);
            uniqueWorkQueue.enqueue(programClass1);
        }

        SignatureTypeReferences signatureTypeReferences = fieldInfo.getSignatureTypeReferences();
        Enumeration enumeration = signatureTypeReferences.enumerateAnnotationClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass2 = (ProgramClass) enumeration.nextElement();
            this.instantiatedClasses.add(programClass2);
            this.matchClass(programClass2);
            uniqueWorkQueue.enqueue(programClass2);
        }

        enumeration = signatureTypeReferences.enumerateReferencedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass3 = (ProgramClass) enumeration.nextElement();
            this.matchClass(programClass3);
            uniqueWorkQueue.enqueue(programClass3);
        }

        enumeration = signatureTypeReferences.enumerateReferencedFields();

        while (enumeration.hasMoreElements()) {
            FieldInfo fieldInfo1 = (FieldInfo) enumeration.nextElement();
            this.markFieldAndTypesReachable(fieldInfo1, uniqueWorkQueue);
        }

        enumeration = signatureTypeReferences.enumerateReferencedMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
            this.matchMethod(methodInfo1);
        }
    }

    public void propagateKeptClasses() throws ZkmException, IOException {
        UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue();
        Iterator iterator = super.excludedClasses.keySet().iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            uniqueWorkQueue.enqueue(programClass1);
        }

        HashSet hashSet1 = ZkmUtils.createHashSet();
        HashSet hashSet = ZkmUtils.createHashSet();

        while (!uniqueWorkQueue.isEmpty()) {
            ProgramClass programClass2 = (ProgramClass) uniqueWorkQueue.dequeue();
            boolean bl = this.instantiatedClasses.contains(programClass2);
            boolean bl1 = hashSet1.add(programClass2);
            boolean bl2 = false;
            if (bl) {
                bl2 = hashSet.add(programClass2);
            }

            if (bl1 || bl2) {
                hashSet1.add(programClass2);
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass2.getClassName());
                SignatureTypeReferences signatureTypeReferences = programClass2.getSignatureTypeReferences();
                Enumeration enumeration = signatureTypeReferences.enumerateAnnotationClasses();

                while (enumeration.hasMoreElements()) {
                    ProgramClass programClass3 = (ProgramClass) enumeration.nextElement();
                    boolean bl3 = this.matchClass(programClass3);
                    boolean bl4 = this.instantiatedClasses.add(programClass3);
                    if (bl3 || bl4) {
                        uniqueWorkQueue.enqueue(programClass3);
                    }
                }

                enumeration = signatureTypeReferences.enumerateReferencedClasses();

                while (enumeration.hasMoreElements()) {
                    ProgramClass programClass5 = (ProgramClass) enumeration.nextElement();
                    boolean bl7 = this.matchClass(programClass5);
                    if (bl7) {
                        uniqueWorkQueue.enqueue(programClass5);
                    }
                }

                enumeration = signatureTypeReferences.enumerateReferencedFields();

                while (enumeration.hasMoreElements()) {
                    FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
                    this.markFieldAndTypesReachable(fieldInfo, uniqueWorkQueue);
                }

                enumeration = signatureTypeReferences.enumerateReferencedMethods();

                while (enumeration.hasMoreElements()) {
                    MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
                    this.matchMethod(methodInfo1);
                }

                ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getEnclosingNode();
                if (classHierarchyNode1 != null) {
                    ProgramClass programClass6 = classHierarchyNode1.getProgramClass();
                    if (programClass6 != null) {
                        boolean bl8 = this.matchClass(programClass6);
                        if (bl8) {
                            uniqueWorkQueue.enqueue(programClass6);
                        }
                    }
                }

                ProgramClass programClass7 = classHierarchyNode.getSuperProgramClass();
                if (programClass7 != null) {
                    boolean bl9 = this.matchClass(programClass7);
                    boolean bl10 = false;
                    if (bl) {
                        bl10 = this.instantiatedClasses.add(programClass7);
                    }

                    if (bl9 || bl10) {
                        uniqueWorkQueue.enqueue(programClass7);
                    }
                }

                Enumeration enumeration1 = classHierarchyNode.enumerateInterfaces();
                if (enumeration1 != null) {
                    while (enumeration1.hasMoreElements()) {
                        ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration1.nextElement();
                        if (classHierarchyNode2.isProgramClass()) {
                            ProgramClass programClass4 = classHierarchyNode2.getProgramClass();
                            boolean bl5 = this.matchClass(programClass4);
                            boolean bl6 = false;
                            if (bl) {
                                bl6 = this.instantiatedClasses.add(programClass4);
                            }

                            if (bl5 || bl6) {
                                uniqueWorkQueue.enqueue(programClass4);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isAnnotationRetained(Object object) {
        return this.annotationRetainedClasses != null && this.annotationRetainedClasses.contains(object);
    }

    @Override
    public boolean isMethodExcluded(Object object) {
        MethodInfo methodInfo1 = (MethodInfo) object;
        return methodInfo1.isStatic()
                ? this.excludedMethods.containsKey(methodInfo1) && super.excludedClasses.containsKey(methodInfo1.getOwnerProgramClass())
                : this.excludedMethods.containsKey(methodInfo1) && this.instantiatedClasses.contains(methodInfo1.getOwnerProgramClass());
    }

    public Enumeration enumerateTrimmedMethodsByClass() {
        return this.trimmedMethodsByClass.distinctValues();
    }

    @Override
    public final Enumeration getCandidateClasses() {
        return new ArrayEnumeration(this.allClasses);
    }

    public ArrayList getKeptClassesWithTrimmedMethods() {
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.trimmedMethodsByClass.keys();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            if (super.excludedClasses.containsKey(programClass1)) {
                arrayList.add(programClass1);
            }
        }

        return arrayList;
    }

    public boolean removeAnnotationRetainedClass(ClassFileBase classFileBase, String string) throws ZkmException, IOException {
        if (this.annotationRetainedClasses == null) {
            this.annotationRetainedClasses = ZkmUtils.createHashSet();
        }

        boolean bl = this.annotationRetainedClasses.remove(classFileBase);
        if (bl && super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
            super.logWriter
                    .println("\tUnmatching for annotation attribute exclusion class \"" + this.describeClass(classFileBase) + "\" because of \"" + string + "\"");
        }

        return bl;
    }

    public static ParameterListStatement parseDefaultTrimExcludeInput(ScriptEnvironment scriptEnvironment1, BufferedReader bufferedReader) throws ZkmException, ZkmScriptParseException, IOException {
        ZkmScriptParser zkmScriptParser = new ZkmScriptParser(bufferedReader);

        ZkmScriptSimpleNode zkmScriptSimpleNode;
        try {
            zkmScriptSimpleNode = zkmScriptParser.DefaultTrimExcludeInput();
            zkmScriptSimpleNode.execute(null, scriptEnvironment1);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException iOException) {
            }
        }

        return ((ASTDefaultTrimExcludeInput) zkmScriptSimpleNode).getParameterListStatement();
    }

    public void releasePendingFields(UniqueWorkQueue uniqueWorkQueue, TwoKeyMap twoKeyMap, ProgramClass programClass1) throws ZkmException, IOException {
        Map map1 = twoKeyMap.getInnerMap(programClass1);
        if (map1 != null) {
            Iterator iterator = map1.keySet().iterator();

            while (iterator.hasNext()) {
                FieldInfo fieldInfo = (FieldInfo) iterator.next();
                this.markFieldReachable(fieldInfo, uniqueWorkQueue);
            }

            twoKeyMap.removeInnerMap(programClass1);
        }
    }

    @Override
    public final boolean hasNoIncludedFields() {
        return this.trimmedFields.size() == 0;
    }

    public final void initCandidates(Enumeration enumeration, int ba) {
        ArrayList arrayList = new ArrayList();
        int bb = ZkmUtils.getPrimeCapacity(ba);
        super.includedClasses = ZkmUtils.createHashMap(bb);
        super.excludedClasses = ZkmUtils.createHashMap(bb);
        super.includedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        super.excludedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.includedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.excludedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.instantiatedClasses = ZkmUtils.createHashSet(bb);
        this.pendingStaticFields = new TwoKeyMap(bb, ZkmUtils.getPrimeCapacity(5));
        this.pendingInstanceFields = new TwoKeyMap(bb, ZkmUtils.getPrimeCapacity(5));
        this.pendingStaticMethods = new TwoKeyMap(bb, ZkmUtils.getPrimeCapacity(5));
        this.pendingInstanceMethods = new TwoKeyMap(bb, ZkmUtils.getPrimeCapacity(5));
        this.trimmedFieldsByClass = new TwoKeyMap(bb);
        this.trimmedMethodsByClass = new TwoKeyMap(bb);

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            this.registerCandidateClass(programClass1, arrayList);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                    this.registerCandidateClass((ProgramClass) classFileBase, arrayList);
                }
            }
        }

        this.allClasses = ((com.zelix.klassmaster.classfile.ProgramClass[]) (arrayList.toArray(new ProgramClass[arrayList.size()])));
        this.allFields = new FieldInfo[super.includedFields.size()];
        int bc = 0;
        Iterator iterator3 = super.includedFields.keySet().iterator();

        while (iterator3.hasNext()) {
            this.allFields[bc++] = (FieldInfo) iterator3.next();
        }

        this.allMethods = new MethodInfo[this.includedMethods.size() + this.excludedMethods.size()];
        int bd = 0;
        Iterator iterator1 = this.includedMethods.keySet().iterator();

        while (iterator1.hasNext()) {
            this.allMethods[bd++] = (MethodInfo) iterator1.next();
        }

        Iterator iterator2 = this.excludedMethods.keySet().iterator();

        while (iterator2.hasNext()) {
            this.allMethods[bd++] = (MethodInfo) iterator2.next();
        }
    }

    public final void unmatchField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) super.excludedFields.remove(fieldInfo);
        if (programClass1 != null) {
            super.includedFields.put(fieldInfo, programClass1);
            if (super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tUnmatching field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + this.describeClass(fieldInfo.getProgramClass())
                                        + "\" for trim because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public void markClassHierarchyReachable(ProgramClass programClass1, UniqueWorkQueue uniqueWorkQueue) throws ZkmException, IOException {
        UniqueWorkQueue uniqueWorkQueue1 = new UniqueWorkQueue();
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        uniqueWorkQueue1.enqueue(programClass1);

        while (!uniqueWorkQueue1.isEmpty()) {
            ProgramClass programClass2 = (ProgramClass) uniqueWorkQueue1.dequeue();
            boolean bl = this.instantiatedClasses.contains(programClass2);
            boolean bl1 = hashSet.add(programClass2);
            boolean bl2 = false;
            if (bl) {
                bl2 = hashSet1.add(programClass2);
            }

            if (bl1 || bl2) {
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass2.getClassName());
                if (bl1) {
                    SignatureTypeReferences signatureTypeReferences = programClass2.getSignatureTypeReferences();
                    Enumeration enumeration = signatureTypeReferences.enumerateAnnotationClasses();

                    while (enumeration.hasMoreElements()) {
                        ProgramClass programClass3 = (ProgramClass) enumeration.nextElement();
                        this.enqueueClassReachable(programClass3, uniqueWorkQueue1, uniqueWorkQueue, true);
                    }

                    enumeration = signatureTypeReferences.enumerateReferencedClasses();

                    while (enumeration.hasMoreElements()) {
                        ProgramClass programClass6 = (ProgramClass) enumeration.nextElement();
                        this.enqueueClassReachable(programClass6, uniqueWorkQueue1, uniqueWorkQueue, false);
                    }

                    enumeration = signatureTypeReferences.enumerateReferencedFields();

                    while (enumeration.hasMoreElements()) {
                        FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
                        this.markFieldReachable(fieldInfo, uniqueWorkQueue);
                    }

                    enumeration = signatureTypeReferences.enumerateReferencedMethods();

                    while (enumeration.hasMoreElements()) {
                        MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
                        this.markMethodReachable(methodInfo1, uniqueWorkQueue);
                    }

                    ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getEnclosingNode();
                    if (classHierarchyNode1 != null) {
                        ProgramClass programClass7 = classHierarchyNode1.getProgramClass();
                        if (programClass7 != null) {
                            this.enqueueClassReachable(programClass7, uniqueWorkQueue1, uniqueWorkQueue, false);
                        }
                    }
                }

                ProgramClass programClass5 = classHierarchyNode.getSuperProgramClass();
                if (programClass5 != null) {
                    if (programClass1.isVersionedVariant() && programClass5.hasVersionedVariants()) {
                        programClass5 = (ProgramClass) programClass5.selectVersionForRelease(programClass1.getReleaseVersion());
                    }

                    this.enqueueClassReachable(programClass5, uniqueWorkQueue1, uniqueWorkQueue, bl);
                }

                Enumeration enumeration1 = classHierarchyNode.enumerateInterfaces();
                if (enumeration1 != null) {
                    while (enumeration1.hasMoreElements()) {
                        ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration1.nextElement();
                        if (classHierarchyNode2.isProgramClass()) {
                            ProgramClass programClass4 = classHierarchyNode2.getProgramClass();
                            if (programClass1.isVersionedVariant() && programClass4.hasVersionedVariants()) {
                                programClass4 = (ProgramClass) programClass4.selectVersionForRelease(programClass1.getReleaseVersion());
                            }

                            this.enqueueClassReachable(programClass4, uniqueWorkQueue1, uniqueWorkQueue, bl);
                        }
                    }
                }
            }
        }
    }

    public void markMethodReachable(MethodInfo methodInfo1, UniqueWorkQueue uniqueWorkQueue) throws ZkmException, IOException {
        this.matchMethod(methodInfo1, (String) null);
        uniqueWorkQueue.enqueue(methodInfo1);
    }

    public TrimProcessor(
            ClassRepository classRepository1,
            ClassHierarchy classHierarchy1,
            List list1,
            List list2,
            TrimOptions trimOptions1,
            ScriptEnvironment scriptEnvironment1,
            ListMultimap listMultimap,
            ExistingSerializedClassesHandler existingSerializedClassesHandler,
            FixedClassesExclusionSet fixedClassesExclusionSet1
    ) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        this.deleteAnnotationAttributes = trimOptions1.d;
        if (classRepository1.hasProgramClasses()) {
            this.extraDependencies = listMultimap;
            this.classpathClassLoader = classRepository1.getClasspathLoader();
            this.libraryOverrideCollector = new LibraryOverrideCollector(classRepository1, classHierarchy1, this.classpathClassLoader);
            this.initCandidates(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            HashMap hashMap = ZkmUtils.createHashMap();
            ArrayList arrayList = new ArrayList();
            int ba = 0;
            int bl = 0;

            for (List list4 = super.exclusionStatements; bl < list4.size(); list4 = super.exclusionStatements) {
                ParameterListStatement parameterListStatement = (ParameterListStatement) super.exclusionStatements.get(ba);
                Enumeration enumeration = parameterListStatement.getRenameFilterParameters();

                while (enumeration.hasMoreElements()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                    if (!hashMap.containsKey(aSTRenameFilterParameter)) {
                        hashMap.put(aSTRenameFilterParameter, aSTRenameFilterParameter);
                        arrayList.add(aSTRenameFilterParameter);
                        if (aSTRenameFilterParameter.isStandaloneAnnotation() && trimOptions1.d && this.annotationTypes == null) {
                            this.collectAnnotationTypes();
                        }
                    }
                }

                bl = ++ba;
            }

            if (scriptEnvironment1.isVerbose() && arrayList.size() > 0) {
                super.logWriter.println("\tTrim exclusion parameters:");

                for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                    super.logWriter.println("\t\t" + arrayList.get(i));
                }
            }

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) arrayList.get(i);
                if (aSTRenameFilterParameter3.isPackageSpecifier()) {
                    scriptEnvironment1.logMessage(
                            "Parameter '" + aSTRenameFilterParameter3 + "' in 'trimExclude' statement is a 'package' level parameter. It will be ignored.", true
                    );
                } else if (aSTRenameFilterParameter3.isClassSpecifier()
                        && aSTRenameFilterParameter3.hasLinkClassName()
                        && aSTRenameFilterParameter3.isClassPlusSpec()) {
                    scriptEnvironment1.logWarning(
                            "Parameter '" + aSTRenameFilterParameter3 + "' in 'trimExclude' statement cannot use both the '<link>' and '+' syntax. It will be ignored.",
                            true
                    );
                } else if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasLinkMethodSignature()) {
                    scriptEnvironment1.logWarning(
                            "Parameter '" + aSTRenameFilterParameter3 + "' in 'trimExclude' statement cannot use the '<link>' syntax. It will be ignored.", true
                    );
                } else {
                    if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasPlusSignatureClasses()) {
                        scriptEnvironment1.logWarning(
                                "Parameter '" + aSTRenameFilterParameter3 + "' in 'trimExclude' cannot use the '" + "+signatureClasses" + "' keyword. It will be ignored.",
                                true
                        );
                    }

                    aSTRenameFilterParameter3.applyTrimExclusion(this);
                }
            }

            if (super.unexclusionStatements != null) {
                HashMap hashMap1 = ZkmUtils.createHashMap();
                ArrayList arrayList1 = new ArrayList();
                int be = 0;
                bl = 0;

                for (List list3 = super.unexclusionStatements; bl < list3.size(); list3 = super.unexclusionStatements) {
                    ParameterListStatement parameterListStatement2 = (ParameterListStatement) super.unexclusionStatements.get(be);
                    Enumeration enumeration1 = parameterListStatement2.getRenameFilterParameters();

                    while (enumeration1.hasMoreElements()) {
                        ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) enumeration1.nextElement();
                        if (!hashMap1.containsKey(aSTRenameFilterParameter1)) {
                            hashMap1.put(aSTRenameFilterParameter1, aSTRenameFilterParameter1);
                            arrayList1.add(aSTRenameFilterParameter1);
                            if (aSTRenameFilterParameter1.isStandaloneAnnotation() && trimOptions1.d && this.annotationTypes == null) {
                                this.collectAnnotationTypes();
                            }
                        }
                    }

                    bl = ++be;
                }

                if (scriptEnvironment1.isVerbose() && arrayList1.size() > 0) {
                    super.logWriter.println("\tTrim unexclusion parameters:");

                    for (int i = arrayList1.size() - 1; i >= 0; i += -1) {
                        super.logWriter.println("\t\t\"" + arrayList1.get(i) + "\"");
                    }
                }

                if (arrayList1.size() > 0 && arrayList.size() == 0) {
                    scriptEnvironment1.logWarning(
                            "A 'trimUnexclude' statement can only operate on the results of a 'trimExclude' statement. No 'trimExclude' statement is in effect so the 'trimUnexclude' statement will be ignored.",
                            true
                    );
                } else {
                    for (int i = arrayList1.size() - 1; i >= 0; i += -1) {
                        ASTRenameFilterParameter aSTRenameFilterParameter4 = (ASTRenameFilterParameter) arrayList1.get(i);
                        if (aSTRenameFilterParameter4.isPackageSpecifier()) {
                            scriptEnvironment1.logWarning(
                                    "Parameter '" + aSTRenameFilterParameter4 + "' in 'trimUnexclude' statement is a 'package' level parameter. It will be ignored.", true
                            );
                        } else if (aSTRenameFilterParameter4.isClassSpecifier() && aSTRenameFilterParameter4.hasLinkClassName()) {
                            scriptEnvironment1.logWarning(
                                    "Parameter '" + aSTRenameFilterParameter4 + "' in 'trimUnexclude' statement cannot use the '<link>' syntax. It will be ignored.", true
                            );
                        } else if (aSTRenameFilterParameter4.isMethodSpecifier() && aSTRenameFilterParameter4.hasLinkMethodSignature()) {
                            scriptEnvironment1.logWarning(
                                    "Parameter '" + aSTRenameFilterParameter4 + "' in 'trimUnexclude' statement cannot use the '<link>' syntax. It will be ignored.", true
                            );
                        } else {
                            if (aSTRenameFilterParameter4.isMethodSpecifier() && aSTRenameFilterParameter4.hasPlusSignatureClasses()) {
                                scriptEnvironment1.logWarning(
                                        "Parameter '"
                                                + aSTRenameFilterParameter4
                                                + "' in 'trimUnexclude' cannot use the '"
                                                + "+signatureClasses"
                                                + "' keyword. It will be ignored.",
                                        true
                                );
                            }

                            aSTRenameFilterParameter4.applyTrimUnexclusion(this);
                        }
                    }
                }
            }

            ParameterListStatement parameterListStatement1 = loadDefaultTrimExclude(scriptEnvironment1);
            ArrayList arrayList2 = new ArrayList();
            if (parameterListStatement1 != null) {
                HashMap hashMap2 = ZkmUtils.createHashMap();
                Enumeration enumeration2 = parameterListStatement1.getRenameFilterParameters();

                while (enumeration2.hasMoreElements()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter5 = (ASTRenameFilterParameter) enumeration2.nextElement();
                    if (!hashMap2.containsKey(aSTRenameFilterParameter5)) {
                        hashMap2.put(aSTRenameFilterParameter5, aSTRenameFilterParameter5);
                        arrayList2.add(aSTRenameFilterParameter5);
                    }
                }

                if (scriptEnvironment1.isVerbose() && arrayList2.size() > 0) {
                    super.logWriter.println("\tDefault trim exclusion parameters:");

                    for (int i = arrayList2.size() - 1; i >= 0; i += -1) {
                        super.logWriter.println("\t\t\"" + arrayList2.get(i) + "\"");
                    }
                }

                for (int i = arrayList2.size() - 1; i >= 0; i += -1) {
                    ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) arrayList2.get(i);
                    if (aSTRenameFilterParameter2.isPackageSpecifier()) {
                        scriptEnvironment1.logWarning(
                                "Parameter '" + aSTRenameFilterParameter2 + "' in default 'trimExclude' is a 'package' level parameter. It will be ignored.", true
                        );
                    } else if (aSTRenameFilterParameter2.isClassSpecifier() && aSTRenameFilterParameter2.hasLinkClassName()) {
                        scriptEnvironment1.logWarning(
                                "Parameter '" + aSTRenameFilterParameter2 + "' in default 'trimExclude' cannot use the '<link>' syntax. It will be ignored.", true
                        );
                    } else if (aSTRenameFilterParameter2.isMethodSpecifier() && aSTRenameFilterParameter2.hasLinkMethodSignature()) {
                        scriptEnvironment1.logWarning(
                                "Parameter '" + aSTRenameFilterParameter2 + "' in 'trimExclude' statement cannot use the '<link>' syntax. It will be ignored.", true
                        );
                    } else {
                        if (aSTRenameFilterParameter2.isMethodSpecifier() && aSTRenameFilterParameter2.hasPlusSignatureClasses()) {
                            scriptEnvironment1.logWarning(
                                    "Parameter '"
                                            + aSTRenameFilterParameter2
                                            + "' in default 'trimExclude' cannot use the '"
                                            + "+signatureClasses"
                                            + "' keyword. It will be ignored.",
                                    true
                            );
                        }

                        aSTRenameFilterParameter2.applyTrimExclusion(this);
                    }
                }
            }

            if (!trimOptions1.d) {
                ParameterListStatement parameterListStatement3 = parsePackageInfoTrimExclude(scriptEnvironment1);
                Enumeration enumeration3 = parameterListStatement3.getRenameFilterParameters();

                while (enumeration3.hasMoreElements()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter6 = (ASTRenameFilterParameter) enumeration3.nextElement();
                    aSTRenameFilterParameter6.applyTrimExclusion(this);
                }
            }

            ClassMemberSets classMemberSets = new ClassMemberSets(super.excludedClasses.keySet(), super.excludedFields.keySet(), this.excludedMethods.keySet());
            scriptEnvironment1.setTrimExclusionMemberSets(classMemberSets);
            if (fixedClassesExclusionSet1 != null) {
                fixedClassesExclusionSet1.applyTrimExclusions(this, scriptEnvironment1.getLogWriter());
            }

            if (existingSerializedClassesHandler != null) {
                existingSerializedClassesHandler.applyTrimExclusions(this, scriptEnvironment1.getLogWriter());
            }

            Map map1 = classRepository1.getResourceReferencedClasses();
            if (map1 != null) {
                ArrayList arrayList3 = new ArrayList(map1.entrySet());
                ClassReasonComparator classReasonComparator = new ClassReasonComparator(this);
                Collections.sort(arrayList3, classReasonComparator);

                for (int i = 0; i < arrayList3.size(); i++) {
                    Entry entry = (Entry) arrayList3.get(i);
                    this.excludeClass((ProgramClass) entry.getKey(), (String) entry.getValue());
                }
            }

            TrimmedMemberComparator trimmedMemberComparator = new TrimmedMemberComparator(this);
            Map map2 = classRepository1.getResourceReferencedFields();
            if (map2 != null) {
                ArrayList arrayList4 = new ArrayList(map2.entrySet());
                Collections.sort(arrayList4, trimmedMemberComparator);

                for (int i = 0; i < arrayList4.size(); i++) {
                    Entry entry1 = (Entry) arrayList4.get(i);
                    this.matchField((FieldInfo) entry1.getKey(), (String) entry1.getValue());
                }
            }

            Map map3 = classRepository1.getResourceReferencedMethods();
            if (map3 != null) {
                ArrayList arrayList5 = new ArrayList(map3.entrySet());
                Collections.sort(arrayList5, trimmedMemberComparator);

                for (int i = 0; i < arrayList5.size(); i++) {
                    Entry entry2 = (Entry) arrayList5.get(i);
                    this.matchMethod((MethodInfo) entry2.getKey(), (String) entry2.getValue());
                }
            }

            TwoKeyMap twoKeyMap = classRepository1.getXmlReferencedMethods();
            Map map4;
            if (twoKeyMap == null) {
                map4 = super.excludedClasses;
            } else {
                Iterator iterator = twoKeyMap.keySet().iterator();

                while (iterator.hasNext()) {
                    MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                    this.matchMethod(methodInfo1, (String) null);
                }

                map4 = super.excludedClasses;
            }

            Iterator iterator1 = map4.keySet().iterator();

            while (iterator1.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator1.next();
                this.instantiatedClasses.add(programClass1);
            }

            this.propagateKeptClasses();
            this.matchLibraryOverrideMethods();
        }
    }

    public void releasePendingMethods(UniqueWorkQueue uniqueWorkQueue, TwoKeyMap twoKeyMap, ProgramClass programClass1) {
        Map map1 = twoKeyMap.getInnerMap(programClass1);
        if (map1 != null) {
            Iterator iterator = map1.keySet().iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                uniqueWorkQueue.enqueue(methodInfo1);
            }

            twoKeyMap.removeInnerMap(programClass1);
        }
    }

    public static ParameterListStatement loadDefaultTrimExclude(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        String string = scriptEnvironment1.getDefaultTrimExcludeFile();
        boolean bl = false;

        BufferedReader bufferedReader;
        try {
            File file1 = new File(string);
            bufferedReader = ZkmFileUtils.openReader(file1, HiddenOptionFlags.SCRIPT_ENCODING);
            scriptEnvironment1.logLine("Using '" + string + "' for default trim exclusions");
        } catch (FileNotFoundException fileNotFoundException) {
            bufferedReader = new BufferedReader(new StringReader(DEFAULT_TRIM_EXCLUDE));
            bl = true;
            scriptEnvironment1.logLine("Using internal default trim exclusions. File '" + string + "' not found");
        } catch (IOException iOException) {
            bufferedReader = new BufferedReader(new StringReader(DEFAULT_TRIM_EXCLUDE));
            bl = true;
            scriptEnvironment1.logLine("Using internal default trim exclusions. Error opening file '" + string + "' : " + iOException);
        }

        try {
            return parseDefaultTrimExcludeInput(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
            if (!bl) {
                return parseInternalDefaultTrimExclude(scriptEnvironment1, zkmScriptParseException);
            }
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            if (!bl) {
                return parseInternalDefaultTrimExclude(scriptEnvironment1, zkmScriptTokenMgrError);
            }
        }

        return null;
    }

    public boolean isDeleteAnnotationAttributes() {
        return this.deleteAnnotationAttributes;
    }

    public void enqueueClassReachable(ProgramClass programClass1, UniqueWorkQueue uniqueWorkQueue, UniqueWorkQueue uniqueWorkQueue1, boolean bl) throws ZkmException, IOException {
        if (programClass1.hasVersionedVariants()) {
            this.enqueueClassVersionReachable(programClass1, uniqueWorkQueue, uniqueWorkQueue1, bl);
            Iterator iterator = programClass1.getVersionedVariants().iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                this.enqueueClassVersionReachable((ProgramClass) classFileBase, uniqueWorkQueue, uniqueWorkQueue1, bl);
            }
        } else if (programClass1.isVersionedVariant()) {
            ProgramClass programClass2 = (ProgramClass) programClass1.getBaseVersionClass();
            this.enqueueClassVersionReachable(programClass2, uniqueWorkQueue, uniqueWorkQueue1, bl);
            Iterator iterator1 = programClass2.getVersionedVariants().iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                this.enqueueClassVersionReachable((ProgramClass) classFileBase1, uniqueWorkQueue, uniqueWorkQueue1, bl);
            }
        } else {
            this.enqueueClassVersionReachable(programClass1, uniqueWorkQueue, uniqueWorkQueue1, bl);
        }
    }

    public final Enumeration enumerateTrimmedFields() {
        return Collections.enumeration(this.trimmedFields);
    }

    @Override
    public final void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        if (super.excludedClasses.remove(programClass1) != null) {
            super.includedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && string != null && super.logWriter != null) {
                super.logWriter.println("\tUnmatching class \"" + this.describeClass(programClass1) + "\" for trim because of \"" + string + "\"");
            }
        }
    }

    @Override
    public final boolean hasNoIncludedMethods() {
        return this.trimmedMethods.size() == 0;
    }

    public void collectTrimmedMembers() {
        this.trimmedFields = new ArrayList(super.includedFields.size() * 2);
        this.trimmedMethods = new ArrayList(this.includedMethods.size() * 2);
        this.keptFields = new ArrayList(super.excludedFields.size());
        this.keptMethods = new ArrayList(this.excludedMethods.size());
        int ba = 0;
        int bb = 0;

        for (FieldInfo[] fieldInfos = this.allFields; bb < fieldInfos.length; fieldInfos = this.allFields) {
            FieldInfo fieldInfo = this.allFields[ba];
            if (this.isFieldExcluded(this.allFields[ba])) {
                this.keptFields.add(fieldInfo);
            } else {
                this.trimmedFieldsByClass.putValue(fieldInfo.getProgramClass(), fieldInfo, fieldInfo);
                this.trimmedFields.add(fieldInfo);
            }

            bb = ++ba;
        }

        ba = 0;
        bb = 0;

        for (MethodInfo[] methodInfos = this.allMethods; bb < methodInfos.length; methodInfos = this.allMethods) {
            MethodInfo methodInfo1 = this.allMethods[ba];
            if (this.isMethodExcluded(methodInfo1)) {
                this.keptMethods.add(methodInfo1);
            } else {
                this.trimmedMethodsByClass.putValue(methodInfo1.getOwnerProgramClass(), methodInfo1, methodInfo1);
                this.trimmedMethods.add(methodInfo1);
            }

            bb = ++ba;
        }
    }

    @Override
    public boolean isFieldExcluded(Object object) {
        FieldInfo fieldInfo = (FieldInfo) object;
        return fieldInfo.isStatic()
                ? super.excludedFields.containsKey(fieldInfo) && super.excludedClasses.containsKey(fieldInfo.getProgramClass())
                : super.excludedFields.containsKey(fieldInfo) && this.instantiatedClasses.contains(fieldInfo.getProgramClass());
    }

    public static ParameterListStatement parseMidletTrimExclude(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseTrimExcludeText(MIDLET_TRIM_EXCLUDE, scriptEnvironment1);
    }
}
