package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;
import com.zelix.klassmaster.script.parser.ZkmScriptParseException;
import com.zelix.klassmaster.script.parser.ZkmScriptParser;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.EquivalenceGroup;
import com.zelix.klassmaster.util.EquivalenceGroups;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

public class NameExclusionSet extends ModuleExclusionTracker {
    public static final String PUBLIC_EXCLUDE_SCRIPT = "exclude  public *.^* and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "         *.* public * and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "         *.* public *(*);"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String PUBLIC_PROTECTED_EXCLUDE_SCRIPT = "exclude  public *.^* and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "         *.* public * and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "         *.* protected * and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "         *.* public *(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "         *.* protected *(*);"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String MIDLET_EXCLUDE_SCRIPT = "exclude  *.* extends javax.microedition.midlet.MIDlet;" + HiddenOptionFlags.LINE_SEPARATOR;
    public static final String DEFAULT_EXCLUDE_SCRIPT = "exclude *.<link>_Skel implements java.rmi.server.Skeleton and //RMI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.<link>_Stub extends java.rmi.server.RemoteStub and //RMI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *._<link>_Stub extends javax.rmi.CORBA.Stub and //RMI with iiop "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *._<link>_Tie implements javax.rmi.CORBA.Tie and //RMI with iiop "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      interface *.* implements java.rmi.Remote *(*) throws java.rmi.RemoteException +signatureClasses and //RMI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.<link>BeanInfo and   //JavaBeans "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.<link>Customizer and //JavaBeans "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* private readObject(java.io.ObjectInputStream) and   //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* private writeObject(java.io.ObjectOutputStream) and //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* static final long serialVersionUID and              //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* writeReplace() and                                  //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* readResolve() and                                   //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* readObjectNoData() throws java.io.ObjectStreamException and //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* java.io.ObjectStreamField[] serialPersistentFields and  //Serialization "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* extends java.lang.Enum public * and     //enum "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* extends java.lang.Enum public values() and  //enum "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* extends java.lang.Enum public valueOf(java.lang.String) and  //enum "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EJBObject public *(*) and               //EJB Remote "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EJBLocalObject public *(*) and          //EJB Local "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EJBHome public create(*) and            //EJB Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EJBHome public find*(*) and             //EJB Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EJBLocalHome public create(*) and       //EJB Local Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EJBLocalHome public find*(*) and        //EJB Local Home "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements javax.ejb.EnterpriseBean public *(*) and          //EJB Bean "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.*Key implements java.io.Serializable public * and              //EJB Primary Key "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.ejb.Remote *.^*^ public *(*) and                       "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.ejb.RemoteHome *.^*^ public *(*) and                   "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.ejb.Stateless *.^*^ public *(*) and                    "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.ejb.Stateful *.^*^ public *(*) and                     "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.ejb.MessageDriven *.*^ public *(*) and                 "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.persistence.Entity *.^*^ public *(*) and               "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.persistence.Table *.^*^ public *(*) and                "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.persistence.Entity *.* @javax.persistence.Column * and "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* @javax.persistence.Id * and                               "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* @javax.persistence.Id *(*) and                            "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.persistence.IdClass(value=*.^*^) public *(*) and       "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.jws.WebService *.*^ @javax.jws.WebMethod *(*) and      "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* implements java.lang.annotation.Annotation public value() and "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* static createUI(javax.swing.JComponent) and          //GUI "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.^*^ native *(*) +signatureClasses and //Don't rename native methods or their packages or classes "
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlType *.* * and    //JAXB"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlType *.* public get<link>() and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlType *.* public is<link>() and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlType *.* public set<link>(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* @javax.xml.bind.annotation.XmlTransient public get<link>() and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* @javax.xml.bind.annotation.XmlTransient public is<link>() and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* @javax.xml.bind.annotation.XmlTransient public set<link>(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlRootElement *.* private * and //JAXB"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlRootElement *.* public get<link>() and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlRootElement *.* public is<link>() and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      @javax.xml.bind.annotation.XmlRootElement *.* public set<link>(*) and"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.<link>MBean and      //JMX"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.<link>MXBean and      //JMX"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* containing{@java.beans.ConstructorProperties *(*)} public get<link>() and //JMX"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* @javafx.fxml.FXML initialize() and //FXML"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      com.javafx.main.Main and //FXML"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* $deserializeLambda$(java.lang.invoke.SerializedLambda) and //Serialized lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* lambda$*(*) and //Serialized lambda"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      *.* extends java.lang.Record <link>() //Records"
            + HiddenOptionFlags.LINE_SEPARATOR
            + "      ;"
            + HiddenOptionFlags.LINE_SEPARATOR;
    public String[] packageNames;
    public FieldInfo[] allFields;
    public ProgramClass[] allClasses;
    public MethodInfo[] allMethods;
    public final Map classLinks = ZkmUtils.createHashMap();
    public final Map methodLinkPrefixes = ZkmUtils.createHashMap();
    public final SetMultiMap methodsByLinkKey = new SetMultiMap();
    public final TwoKeyMap methodFieldLinks = new TwoKeyMap();
    public SetMultiMap accessorMethodsByField = new SetMultiMap();
    public final TwoKeyMap prefixOnlyLinkedMethods = new TwoKeyMap();
    public final EquivalenceGroups methodEquivalenceGroups = new EquivalenceGroups();
    public final TwoKeySetMultiMap groupMethodsByPrefix = new TwoKeySetMultiMap();
    public final SetMultiMap fieldsByMethodGroup = new SetMultiMap();
    public final Map includedPackages = ZkmUtils.createHashMap();
    public final Map excludedPackages = ZkmUtils.createHashMap();
    public ClassFileBase[] inputClassFiles;

    public static ParameterListStatement parseInternalDefaultExcludes(ScriptEnvironment scriptEnvironment1, Throwable throwable) throws ZkmException, IOException {
        if (throwable != null) {
            String string = scriptEnvironment1.getDefaultExcludeFile();
            System.err.println("\"" + string + "\" had a parse error and will be ignored. See \"" + scriptEnvironment1.getLogFileName() + "\" for more detail.");
            scriptEnvironment1.logWarning(
                    "\""
                            + string
                            + "\" had a parse error :"
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + throwable.getMessage()
                            + HiddenOptionFlags.LINE_SEPARATOR
                            + "Will use the internal default exclude statement and ignore \""
                            + string
                            + "\"."
            );
        }

        BufferedReader bufferedReader = new BufferedReader(new StringReader(DEFAULT_EXCLUDE_SCRIPT));

        try {
            return parseDefaultExcludeInput(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
            return null;
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            return null;
        }
    }

    public void excludeInconsistentLinkedMembers(MethodOverrideAnalyzer methodOverrideAnalyzer, HashMap hashMap) throws ZkmException, IOException {
        java.lang.StringBuilder stringBuilder = null;
        Iterator iterator = this.methodsByLinkKey.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Set set1 = (Set) entry.getValue();
            Iterator iterator1 = set1.iterator();

            while (iterator1.hasNext()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator1.next();
                Iterator iterator2 = set1.iterator();

                while (iterator2.hasNext()) {
                    AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator2.next();
                    this.methodEquivalenceGroups.makeEquivalent(abstractMethodInfo, abstractMethodInfo1);
                }
            }
        }

        iterator = this.accessorMethodsByField.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry2 = (Entry) iterator.next();
            Set set2 = (Set) entry2.getValue();
            Iterator iterator6 = set2.iterator();

            while (iterator6.hasNext()) {
                AbstractMethodInfo abstractMethodInfo7 = (AbstractMethodInfo) iterator6.next();
                Iterator iterator10 = set2.iterator();

                while (iterator10.hasNext()) {
                    AbstractMethodInfo abstractMethodInfo12 = (AbstractMethodInfo) iterator10.next();
                    this.methodEquivalenceGroups.makeEquivalent(abstractMethodInfo7, abstractMethodInfo12);
                }
            }
        }

        iterator = ZkmUtils.createHashSetFrom(this.methodEquivalenceGroups.getElements()).iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo5 = (AbstractMethodInfo) iterator.next();
            if (!abstractMethodInfo5.isStrictlyPrivate() && !abstractMethodInfo5.isStatic()) {
                Set set3 = methodOverrideAnalyzer.getOverrideGroup(abstractMethodInfo5);
                if (set3 != null) {
                    Iterator iterator7 = set3.iterator();

                    while (iterator7.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo8 = (AbstractMethodInfo) iterator7.next();
                        this.methodEquivalenceGroups.makeEquivalent(abstractMethodInfo5, abstractMethodInfo8);
                        this.methodLinkPrefixes.put(abstractMethodInfo8, this.methodLinkPrefixes.get(abstractMethodInfo5));
                    }
                }
            }
        }

        iterator = this.accessorMethodsByField.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry3 = (Entry) iterator.next();
            FieldInfo fieldInfo = (FieldInfo) entry3.getKey();
            Set set4 = (Set) entry3.getValue();
            Iterator iterator8 = set4.iterator();

            while (iterator8.hasNext()) {
                AbstractMethodInfo abstractMethodInfo9 = (AbstractMethodInfo) iterator8.next();
                EquivalenceGroup equivalenceGroup1 = this.methodEquivalenceGroups.getGroup(abstractMethodInfo9);
                this.fieldsByMethodGroup.addValue(equivalenceGroup1, fieldInfo);
            }
        }

        iterator = ZkmUtils.createHashSetFrom(this.accessorMethodsByField.entrySet()).iterator();

        while (iterator.hasNext()) {
            Entry entry4 = (Entry) iterator.next();
            FieldInfo fieldInfo4 = (FieldInfo) entry4.getKey();
            Set set5 = (Set) entry4.getValue();
            Iterator iterator9 = ZkmUtils.createHashSetFrom(set5).iterator();

            while (iterator9.hasNext()) {
                AbstractMethodInfo abstractMethodInfo10 = (AbstractMethodInfo) iterator9.next();
                EquivalenceGroup equivalenceGroup2 = this.methodEquivalenceGroups.getGroup(abstractMethodInfo10);
                Iterator iterator3 = equivalenceGroup2.iterator();

                while (iterator3.hasNext()) {
                    set5.add(iterator3.next());
                }
            }
        }

        iterator = this.methodEquivalenceGroups.getEntries().iterator();

        while (iterator.hasNext()) {
            Entry entry5 = (Entry) iterator.next();
            AbstractMethodInfo abstractMethodInfo6 = (AbstractMethodInfo) entry5.getKey();
            String string1 = (String) this.methodLinkPrefixes.get(abstractMethodInfo6);
            this.groupMethodsByPrefix.addValue(entry5.getValue(), string1, abstractMethodInfo6);
        }

        iterator = this.groupMethodsByPrefix.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry6 = (Entry) iterator.next();
            EquivalenceGroup equivalenceGroup = (EquivalenceGroup) entry6.getKey();
            Set set6 = this.fieldsByMethodGroup.getValues(equivalenceGroup);
            FieldInfo fieldInfo1 = null;
            if (set6 != null) {
                Iterator iterator11 = set6.iterator();

                while (iterator11.hasNext()) {
                    FieldInfo fieldInfo2 = (FieldInfo) iterator11.next();
                    if (this.isFieldExcluded(fieldInfo2)) {
                        fieldInfo1 = fieldInfo2;
                        break;
                    }
                }
            }

            AbstractMethodInfo abstractMethodInfo11 = null;
            AbstractMethodInfo abstractMethodInfo13 = null;
            Iterator iterator12 = ((SetMultiMap) entry6.getValue()).entrySet().iterator();

            while (iterator12.hasNext()) {
                Entry entry1 = (Entry) iterator12.next();
                HashSet hashSet = ZkmUtils.createHashSet();
                HashSet hashSet1 = ZkmUtils.createHashSet();
                Iterator iterator4 = ((Set) entry1.getValue()).iterator();

                while (iterator4.hasNext()) {
                    AbstractMethodInfo abstractMethodInfo2 = (AbstractMethodInfo) iterator4.next();
                    if (!abstractMethodInfo2.isProgramMember()) {
                        abstractMethodInfo11 = abstractMethodInfo2;
                    } else if (this.isMethodExcluded((MethodInfo) abstractMethodInfo2)) {
                        abstractMethodInfo13 = abstractMethodInfo2;
                    }

                    AbstractMethodInfo abstractMethodInfo3 = null;
                    if (!abstractMethodInfo2.isStrictlyPrivate() && !abstractMethodInfo2.isStatic()) {
                        abstractMethodInfo3 = methodOverrideAnalyzer.findRootMethod(abstractMethodInfo2);
                    }

                    if (abstractMethodInfo3 == null) {
                        hashSet1.add(abstractMethodInfo2);
                    } else {
                        hashSet.add(abstractMethodInfo3);
                    }
                }

                label164:
                {
                    hashSet1.removeAll(hashSet);
                    stringBuilder = new StringBuilder();
                    StringBuilder stringBuilder1;
                    String string7;
                    if (hashSet.size() <= 1) {
                        if (set6 == null || set6.size() <= 1) {
                            break label164;
                        }

                        stringBuilder1 = stringBuilder;
                        string7 = "Complex property inter-relationships";
                    } else {
                        stringBuilder1 = stringBuilder;
                        string7 = "Complex property inter-relationships";
                    }

                    stringBuilder1.append(string7);
                }

                if (fieldInfo1 != null) {
                    StringBuilder stringBuilder2;
                    String string4;
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(" and ");
                        stringBuilder2 = stringBuilder;
                        string4 = "Field '";
                    } else {
                        stringBuilder2 = stringBuilder;
                        string4 = "Field '";
                    }

                    stringBuilder2.append(string4);
                    stringBuilder.append(AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo1, this));
                    stringBuilder.append("' is excluded");
                }

                if (abstractMethodInfo13 != null) {
                    StringBuilder stringBuilder3;
                    String string5;
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(" and ");
                        stringBuilder3 = stringBuilder;
                        string5 = "Method '";
                    } else {
                        stringBuilder3 = stringBuilder;
                        string5 = "Method '";
                    }

                    stringBuilder3.append(string5);
                    stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo13, this));
                    stringBuilder.append("' is excluded");
                }

                if (abstractMethodInfo11 != null) {
                    StringBuilder stringBuilder4;
                    String string6;
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(" and ");
                        stringBuilder4 = stringBuilder;
                        string6 = "Method '";
                    } else {
                        stringBuilder4 = stringBuilder;
                        string6 = "Method '";
                    }

                    stringBuilder4.append(string6);
                    stringBuilder.append(AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo11, this));
                    stringBuilder.append("' is not opened for obfuscation");
                }

                if (stringBuilder.length() > 0) {
                    String string2 = stringBuilder.toString();
                    String string3 = this.formatMemberList(ZkmUtils.createHashSetFrom(equivalenceGroup), hashMap);
                    String string;
                    if (set6 != null) {
                        string = this.formatMemberList(ZkmUtils.createHashSetFrom(set6), hashMap);
                    } else {
                        string = "";
                    }

                    super.scriptEnvironment
                            .logWarning(
                                    "Excluding method"
                                            + (equivalenceGroup.size() == 1 ? "" : "s")
                                            + " ["
                                            + string3
                                            + "] and field"
                                            + (set6 != null && set6.size() == 1 ? "" : "s")
                                            + "["
                                            + string
                                            + "] from name obfuscation due to \""
                                            + string2
                                            + "\""
                            );
                    Iterator iterator5 = equivalenceGroup.iterator();

                    while (iterator5.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo4 = (AbstractMethodInfo) iterator5.next();
                        if (abstractMethodInfo4.isProgramMember()) {
                            this.excludeMethod((MethodInfo) abstractMethodInfo4, string2);
                        }
                    }

                    if (set6 != null) {
                        iterator5 = set6.iterator();

                        while (iterator5.hasNext()) {
                            FieldInfo fieldInfo3 = (FieldInfo) iterator5.next();
                            if (fieldInfo3.isProgramMember()) {
                                this.excludeField(fieldInfo3, string2);
                            }
                        }
                    }
                }
            }
        }
    }

    public final void initializeCandidateMaps(Enumeration enumeration, int ba) {
        this.allClasses = new ProgramClass[ba];
        super.includedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.excludedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.includedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        super.excludedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.includedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.excludedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        int bb = 0;

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.includedClasses.put(programClass1, programClass1);
            this.allClasses[bb++] = programClass1;
            ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

            while (arrayEnumeration.hasMoreElements()) {
                FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
                super.includedFields.put(fieldInfo, fieldInfo.getProgramClass());
            }

            ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

            while (arrayEnumeration1.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
                this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }

        this.allFields = new FieldInfo[super.includedFields.size()];
        int bc = 0;
        Enumeration enumeration1 = this.getIncludedFields();

        while (enumeration1.hasMoreElements()) {
            this.allFields[bc++] = (FieldInfo) enumeration1.nextElement();
        }

        this.allMethods = new MethodInfo[this.includedMethods.size()];
        int bd = 0;
        Enumeration enumeration2 = this.getIncludedMethods();

        while (enumeration2.hasMoreElements()) {
            this.allMethods[bd++] = (MethodInfo) enumeration2.nextElement();
        }
    }

    public final void excludeField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) super.includedFields.remove(fieldInfo);
        if (programClass1 != null) {
            super.excludedFields.put(fieldInfo, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tExcluding field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + this.describeClass(fieldInfo.getProgramClass())
                                        + "\" from name obfuscation because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public final String getLinkPrefix(AbstractMethodInfo abstractMethodInfo) {
        return abstractMethodInfo != null ? (String) this.methodLinkPrefixes.get(abstractMethodInfo) : null;
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        return this.excludeClassInternal(programClass1, string, false);
    }

    public final void forceUnexcludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        if (!methodInfo1.isConstructor() && !methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.includedMethods.put(methodInfo1, programClass1);
                super.scriptEnvironment
                        .logWarning(
                                "UNEXCLUDING method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(methodInfo1.getOwnerProgramClass())
                                        + "\" for name obfuscation because \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public static ParameterListStatement parsePublicExcludes(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseExcludeScript(PUBLIC_EXCLUDE_SCRIPT, scriptEnvironment1);
    }

    public void unexcludeRenamedPackages(ChangeLogMapping changeLogMapping1, FixedClassesExclusionSet fixedClassesExclusionSet1) throws IOException {
        if (changeLogMapping1 != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList1 = new ArrayList();
            Iterator iterator = this.excludedPackages.keySet().iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                if (changeLogMapping1.hasPackageMapping(string)) {
                    if (changeLogMapping1.hasNewPackageName(string)) {
                        arrayList.add(string);
                    }
                } else {
                    arrayList1.add(string);
                }
            }

            Collections.sort(arrayList);
            Collections.sort(arrayList1, Collections.reverseOrder());

            for (int i = 0; i < arrayList.size(); i++) {
                String string1 = (String) arrayList.get(i);
                this.forceUnexcludePackage(
                        string1,
                        changeLogMapping1,
                        fixedClassesExclusionSet1,
                        "Input ChangeLog specifies a new name for package '" + ZkmUtils.slashesToDots(string1) + "'",
                        arrayList1
                );
            }
        }
    }

    public final void forceUnexcludeField(FieldInfo fieldInfo, String string, HashMap hashMap) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) super.excludedFields.remove(fieldInfo);
        if (programClass1 != null) {
            super.includedFields.put(fieldInfo, programClass1);
            String string1;
            ScriptEnvironment scriptEnvironment1;
            if (hashMap != null && (string1 = (String) hashMap.get(fieldInfo.getClassName())) != null) {
                ZkmUtils.slashesToDots(string1);
                scriptEnvironment1 = super.scriptEnvironment;
            } else {
                fieldInfo.getDottedClassName();
                scriptEnvironment1 = super.scriptEnvironment;
            }

            scriptEnvironment1.logWarning(
                    "UNEXCLUDING field \""
                            + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                            + "\" in class \""
                            + AbstractExclusionSpec.formatClassWithModifiers(fieldInfo.getProgramClass(), this.classRepository)
                            + "\" for name obfuscation because \""
                            + string
                            + "\""
            );
        }
    }

    public final void processExclusionParameters() throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bi = 0;

        for (List list2 = super.exclusionStatements; bi < list2.size(); list2 = super.exclusionStatements) {
            ParameterListStatement parameterListStatement = (ParameterListStatement) super.exclusionStatements.get(ba);
            Enumeration enumeration = parameterListStatement.getRenameFilterParameters();

            while (enumeration.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                if (!hashMap.containsKey(aSTRenameFilterParameter)) {
                    hashMap.put(aSTRenameFilterParameter, aSTRenameFilterParameter);
                    arrayList.add(aSTRenameFilterParameter);
                }
            }

            bi = ++ba;
        }

        Collections.sort(arrayList);
        if (super.scriptEnvironment.isVerbose() && arrayList.size() > 0) {
            super.logWriter.println("\tExclusion parameters:");

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + arrayList.get(i));
            }
        }

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            ((ASTRenameFilterParameter) arrayList.get(i)).applyNameExclusion(this);
        }

        if (super.unexclusionStatements != null) {
            HashMap hashMap1 = ZkmUtils.createHashMap();
            Vector vector = new Vector();
            int bd = 0;
            bi = 0;

            for (List list1 = super.unexclusionStatements; bi < list1.size(); list1 = super.unexclusionStatements) {
                ParameterListStatement parameterListStatement2 = (ParameterListStatement) super.unexclusionStatements.get(bd);
                Enumeration enumeration1 = parameterListStatement2.getRenameFilterParameters();

                while (enumeration1.hasMoreElements()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) enumeration1.nextElement();
                    if (!hashMap1.containsKey(aSTRenameFilterParameter1)) {
                        hashMap1.put(aSTRenameFilterParameter1, aSTRenameFilterParameter1);
                        vector.addElement(aSTRenameFilterParameter1);
                    }
                }

                bi = ++bd;
            }

            if (vector.size() > 0 && arrayList.size() == 0) {
                super.scriptEnvironment
                        .logWarning(
                                "An 'unexclude' statement can only operate on the results of an 'exclude' statement. No 'exclude' statement is in effect so the 'unexclude' statement will be ignored.",
                                true
                        );
            } else {
                Collections.sort(vector);
                if (super.scriptEnvironment.isVerbose() && vector.size() > 0) {
                    super.logWriter.println("\tUnexclusion parameters:");

                    for (int i = vector.size() - 1; i >= 0; i += -1) {
                        super.logWriter.println("\t\t\"" + vector.elementAt(i) + "\"");
                    }
                }

                for (int i = vector.size() - 1; i >= 0; i += -1) {
                    ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) vector.elementAt(i);
                    if (aSTRenameFilterParameter2.isClassSpecifier() && aSTRenameFilterParameter2.hasLinkClassName()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '" + aSTRenameFilterParameter2 + "' in 'unexclude' statement cannot use the '<link>' syntax. It will be ignored.", true
                                );
                    } else if (aSTRenameFilterParameter2.isMethodSpecifier() && aSTRenameFilterParameter2.hasLinkMethodSignature()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '" + aSTRenameFilterParameter2 + "' in 'unexclude' statement cannot use the '<link>' syntax. It will be ignored.", true
                                );
                    } else {
                        aSTRenameFilterParameter2.applyNameUnexclusion(this);
                    }
                }
            }
        }

        ParameterListStatement parameterListStatement1 = loadDefaultExcludes(super.scriptEnvironment);
        boolean bl;
        if (parameterListStatement1 != null) {
            HashMap hashMap2 = ZkmUtils.createHashMap();
            Vector vector1 = new Vector();
            Enumeration enumeration2 = parameterListStatement1.getRenameFilterParameters();

            while (enumeration2.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) enumeration2.nextElement();
                if (!hashMap2.containsKey(aSTRenameFilterParameter3)) {
                    hashMap2.put(aSTRenameFilterParameter3, aSTRenameFilterParameter3);
                    vector1.addElement(aSTRenameFilterParameter3);
                }
            }

            Collections.sort(vector1);
            if (super.scriptEnvironment.isVerbose() && vector1.size() > 0) {
                super.logWriter.println("\tDefault exclusion parameters:");

                for (int i = vector1.size() - 1; i >= 0; i += -1) {
                    super.logWriter.println("\t\t\"" + vector1.elementAt(i) + "\"");
                }
            }

            for (int i = vector1.size() - 1; i >= 0; i += -1) {
                ASTRenameFilterParameter aSTRenameFilterParameter4 = (ASTRenameFilterParameter) vector1.elementAt(i);
                aSTRenameFilterParameter4.applyNameExclusion(this);
            }

            bl = HiddenOptionFlags.APPLY_CHANGE_LOG_EXCLUSIONS;
        } else {
            bl = HiddenOptionFlags.APPLY_CHANGE_LOG_EXCLUSIONS;
        }

        if (bl) {
            TwoKeyMap twoKeyMap = this.classRepository.getXmlReferencedMethods();
            if (twoKeyMap != null && !twoKeyMap.isEmpty()) {
                Map map1 = this.classRepository.getResourceReferencedMethods();
                Iterator iterator1 = twoKeyMap.entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    MethodInfo methodInfo1 = (MethodInfo) entry1.getKey();
                    Iterator iterator = ((Map) entry1.getValue()).entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();
                        String string = (String) entry.getKey();
                        String string1 = (String) map1.get(methodInfo1);
                        this.addMethodLink(methodInfo1, string, entry.getValue(), string1);
                    }
                }
            }
        }
    }

    public final void unexcludeClassInternal(ProgramClass programClass1, String string, boolean bl) throws ZkmException, IOException {
        if (super.excludedClasses.remove(programClass1) != null) {
            super.includedClasses.put(programClass1, programClass1);
            if (bl) {
                super.scriptEnvironment
                        .logWarning("UNEXCLUDING class \"" + this.describeClass(programClass1) + "\" for name obfuscation because \"" + string + "\"");
            } else if (super.scriptEnvironment.isVerbose()) {
                super.logWriter.println("\tUnexcluding class \"" + this.describeClass(programClass1) + "\" for name obfuscation because of \"" + string + "\"");
            }
        }
    }

    public final void collectPackageNames(RootPackageNode rootPackageNode1) {
        List list1 = rootPackageNode1.getAllPackagePaths();
        int ba = list1.size();
        this.packageNames = new String[ba];

        for (int i = 0; i < ba; i++) {
            String string = (String) list1.get(i);
            this.includedPackages.put(string, string);
            this.packageNames[i] = string;
        }
    }

    public static ParameterListStatement parseExcludeScript(String string, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(string));

        try {
            return parseDefaultExcludeInput(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
        }

        return null;
    }

    public final void unexcludeField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) super.excludedFields.remove(fieldInfo);
        if (programClass1 != null) {
            super.includedFields.put(fieldInfo, programClass1);
            if (super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println(
                                "\tUnexcluding field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + AbstractExclusionSpec.formatClassWithModifiers(fieldInfo.getProgramClass(), this.classRepository)
                                        + "\" for name obfuscation because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public static ParameterListStatement parseMidletExcludes(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseExcludeScript(MIDLET_EXCLUDE_SCRIPT, scriptEnvironment1);
    }

    public static ParameterListStatement loadDefaultExcludes(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        String string = scriptEnvironment1.getDefaultExcludeFile();
        boolean bl = false;

        BufferedReader bufferedReader;
        try {
            File file1 = new File(string);
            bufferedReader = ZkmFileUtils.openReader(file1, HiddenOptionFlags.SCRIPT_ENCODING);
            scriptEnvironment1.logLine("Using '" + string + "' for default exclusions");
        } catch (FileNotFoundException fileNotFoundException) {
            bufferedReader = new BufferedReader(new StringReader(DEFAULT_EXCLUDE_SCRIPT));
            bl = true;
            scriptEnvironment1.logLine("Using internal default exclusions. File '" + string + "' not found");
        } catch (IOException iOException) {
            bufferedReader = new BufferedReader(new StringReader(DEFAULT_EXCLUDE_SCRIPT));
            bl = true;
            scriptEnvironment1.logLine("Using internal default exclusions. Error opening file '" + string + "' : " + iOException);
        }

        try {
            return parseDefaultExcludeInput(scriptEnvironment1, bufferedReader);
        } catch (ZkmScriptParseException zkmScriptParseException) {
            if (!bl) {
                return parseInternalDefaultExcludes(scriptEnvironment1, zkmScriptParseException);
            }
        } catch (ZkmScriptTokenMgrError zkmScriptTokenMgrError) {
            if (!bl) {
                return parseInternalDefaultExcludes(scriptEnvironment1, zkmScriptTokenMgrError);
            }
        }

        return null;
    }

    public final void excludeMethodFor(MethodInfo methodInfo1, MethodInfo methodInfo2) throws ZkmException, IOException {
        if (!methodInfo1.isConstructor() && !methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.excludedMethods.put(methodInfo1, programClass1);
                methodInfo1.getOwnerProgramClass().getOriginalDottedName();
                methodInfo2.getOwnerProgramClass().getOriginalDottedName();
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    super.logWriter
                            .println(
                                    "\tExcluding method \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                            + "\" in class \""
                                            + AbstractExclusionSpec.formatClassWithModifiers(methodInfo1.getOwnerProgramClass(), this.classRepository)
                                            + "\" from name obfuscation because method \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo2, this)
                                            + "\" in class \""
                                            + AbstractExclusionSpec.formatClassWithModifiers(methodInfo2.getOwnerProgramClass(), this.classRepository)
                                            + "\" has been excluded (A)"
                            );
                }
            }
        }
    }

    public Set getLinkedFields() {
        return ZkmUtils.createHashSetFrom(this.accessorMethodsByField.keySet());
    }

    public final Enumeration getExcludedPackages() {
        String[] strings = new String[this.excludedPackages.size()];
        Iterator iterator = this.excludedPackages.keySet().iterator();
        int ba = 0;

        while (iterator.hasNext()) {
            strings[ba++] = (String) iterator.next();
        }

        return new ArrayEnumeration(strings);
    }

    public void unexcludeRenamedMethods(
            ChangeLogMapping changeLogMapping1, MethodOverrideAnalyzer methodOverrideAnalyzer, HashMap hashMap, FixedClassesExclusionSet fixedClassesExclusionSet1
    ) throws ZkmException, IOException {
        if (changeLogMapping1 != null) {
            ArrayList arrayList = new ArrayList();
            HashMap hashMap1 = ZkmUtils.createHashMap();
            SetMultiMap setMultiMap = changeLogMapping1.getRenamedMethodSignatures();
            Iterator iterator = this.excludedMethods.keySet().iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                AbstractMethodInfo abstractMethodInfo = null;
                if (!methodInfo1.isStrictlyPrivate() && !methodInfo1.isStatic()) {
                    abstractMethodInfo = methodOverrideAnalyzer.findRootMethod(methodInfo1);
                }

                MethodSignature methodSignature1 = methodInfo1.getSignature();
                String string = methodInfo1.getClassName();
                ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
                if (changeLogMapping1.isMethodRenamed(string, methodSignature1)
                        || abstractMethodInfo != null && changeLogMapping1.isMethodRenamed(abstractMethodInfo.getClassName(), methodSignature1)
                        || setMultiMap.containsValue(string, methodSignature1)) {
                    Boolean boolean1;
                    label223:
                    {
                        Boolean boolean4;
                        label197:
                        if (!changeLogMapping1.isMethodRenamed(string, methodSignature1)) {
                            if (abstractMethodInfo != null) {
                                if (changeLogMapping1.isMethodRenamed(abstractMethodInfo.getClassName(), methodSignature1)) {
                                    boolean4 = Boolean.TRUE;
                                    break label197;
                                }

                                boolean4 = Boolean.FALSE;
                            } else {
                                boolean4 = Boolean.FALSE;
                            }

                            boolean1 = boolean4;
                            break label223;
                        } else {
                            boolean4 = Boolean.TRUE;
                        }

                        boolean1 = boolean4;
                    }

                    arrayList.add(new ObjectPair(methodInfo1, boolean1));
                    if (programClass1.isInterface()) {
                        ObservableHolder observableHolder = methodOverrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
                        if (observableHolder != null) {
                            List list1 = (List) observableHolder.getValue();

                            for (int i = 0; i < list1.size(); i++) {
                                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
                                if (classHierarchyNode1.isProgramClass() && classHierarchyNode1 != classHierarchyNode) {
                                    MethodInfo methodInfo2 = this.classRepository.findDeclaredMethod(classHierarchyNode1.getProgramClass(), methodSignature1);
                                    if (methodInfo2 != null) {
                                        hashMap1.put(methodInfo2, new ObjectPair(methodInfo1, boolean1));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < arrayList.size(); i++) {
                ObjectPair objectPair = (ObjectPair) arrayList.get(i);
                MethodInfo methodInfo3 = (MethodInfo) objectPair.getFirst();
                Boolean boolean2 = (Boolean) objectPair.getSecond();
                if (boolean2) {
                    this.forceUnexcludeMethod(methodInfo3, "Input ChangeLog specifies a new name");
                } else {
                    this.forceUnexcludeMethod(methodInfo3, "Input ChangeLog specifies name unchanged");
                }

                hashMap1.remove(methodInfo3);
                if (fixedClassesExclusionSet1 != null) {
                    fixedClassesExclusionSet1.reportMethodChanged(methodInfo3, changeLogMapping1.getChangeLogName(), true);
                }
            }

            iterator = hashMap1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MethodInfo methodInfo4 = (MethodInfo) entry.getKey();
                ObjectPair objectPair1 = (ObjectPair) entry.getValue();
                MethodInfo methodInfo5 = (MethodInfo) objectPair1.getFirst();
                Boolean boolean3 = (Boolean) objectPair1.getSecond();
                String string5 = AbstractExclusionSpec.formatClassWithModifiers(methodInfo5.getOwnerProgramClass(), this.classRepository);
                if (boolean3) {
                    this.forceUnexcludeMethod(methodInfo4, "Input ChangeLog specifies a new name for a linked method in class '" + string5 + "'");
                } else {
                    this.forceUnexcludeMethod(methodInfo4, "Input ChangeLog specifies name unchanged for linked method in class '" + string5 + "'");
                }

                if (fixedClassesExclusionSet1 != null) {
                    fixedClassesExclusionSet1.reportMethodChanged(methodInfo4, changeLogMapping1.getChangeLogName(), true);
                }
            }

            iterator = this.accessorMethodsByField.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                AbstractMethodInfo abstractMethodInfo1 = null;
                Iterator iterator2 = ((Set) entry1.getValue()).iterator();

                while (iterator2.hasNext()) {
                    AbstractMethodInfo abstractMethodInfo4 = (AbstractMethodInfo) iterator2.next();
                    String string4 = abstractMethodInfo4.getClassName();
                    MethodSignature methodSignature3 = abstractMethodInfo4.getSignature();
                    String string8 = changeLogMapping1.lookupNewMethodName(string4, methodSignature3);
                    if (string8 != null) {
                        abstractMethodInfo1 = abstractMethodInfo4;
                        break;
                    }
                }

                if (abstractMethodInfo1 != null) {
                    String string1 = AbstractExclusionSpec.formatClassWithModifiers(abstractMethodInfo1.getOwningClass(), this.classRepository);
                    Iterator iterator3 = ((Set) entry1.getValue()).iterator();

                    while (iterator3.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo5 = (AbstractMethodInfo) iterator3.next();
                        if (abstractMethodInfo5.isProgramMember() && this.isMethodExcluded((MethodInfo) abstractMethodInfo5)) {
                            this.forceUnexcludeMethod(
                                    (MethodInfo) abstractMethodInfo5, "Input ChangeLog specifies a new name for a linked method in class '" + string1 + "'"
                            );
                        }
                    }
                }
            }

            HashSet hashSet = ZkmUtils.createHashSet();
            Iterator iterator1 = this.methodEquivalenceGroups.getGroups().iterator();

            while (iterator1.hasNext()) {
                EquivalenceGroup equivalenceGroup = (EquivalenceGroup) iterator1.next();
                if (hashSet.add(equivalenceGroup)) {
                    AbstractMethodInfo abstractMethodInfo2 = null;
                    Iterator iterator4 = equivalenceGroup.iterator();

                    while (iterator4.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo6 = (AbstractMethodInfo) iterator4.next();
                        String string6 = abstractMethodInfo6.getClassName();
                        MethodSignature methodSignature4 = abstractMethodInfo6.getSignature();
                        String string10 = changeLogMapping1.lookupNewMethodName(string6, methodSignature4);
                        if (string10 != null) {
                            abstractMethodInfo2 = abstractMethodInfo6;
                            break;
                        }
                    }

                    if (abstractMethodInfo2 != null) {
                        String string2 = AbstractExclusionSpec.formatClassWithModifiers(abstractMethodInfo2.getOwningClass(), this.classRepository);
                        Iterator iterator5 = equivalenceGroup.iterator();

                        while (iterator5.hasNext()) {
                            AbstractMethodInfo abstractMethodInfo7 = (AbstractMethodInfo) iterator5.next();
                            if (abstractMethodInfo7.isProgramMember() && this.isMethodExcluded((MethodInfo) abstractMethodInfo7)) {
                                this.forceUnexcludeMethod(
                                        (MethodInfo) abstractMethodInfo7, "Input ChangeLog specifies a new name for a linked method in class '" + string2 + "'"
                                );
                                this.prefixOnlyLinkedMethods.removeInnerMap(abstractMethodInfo7);
                            }
                        }
                    }
                }
            }

            iterator1 = this.prefixOnlyLinkedMethods.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry2 = (Entry) iterator1.next();
                AbstractMethodInfo abstractMethodInfo3 = (AbstractMethodInfo) entry2.getKey();
                String string3 = abstractMethodInfo3.getClassName();
                MethodSignature methodSignature2 = abstractMethodInfo3.getSignature();
                if (changeLogMapping1.hasMethodMapping(string3, methodSignature2)) {
                    String string7;
                    String string9;
                    ScriptEnvironment scriptEnvironment1;
                    if (hashMap != null && (string9 = (String) hashMap.get(abstractMethodInfo3.getClassName())) != null) {
                        string7 = ZkmUtils.slashesToDots(string9);
                        scriptEnvironment1 = super.scriptEnvironment;
                    } else {
                        string7 = abstractMethodInfo3.getDottedClassName();
                        scriptEnvironment1 = super.scriptEnvironment;
                    }

                    scriptEnvironment1.logWarning(
                            "Prefix exclusion of method \""
                                    + AbstractExclusionSpec.formatMethodWithModifiers(abstractMethodInfo3, this)
                                    + "\" in class \""
                                    + string7
                                    + "\" may not be effective because method appears in input change log \""
                                    + changeLogMapping1.getChangeLogName()
                                    + "\" (2)"
                    );
                }
            }
        }
    }

    public final void excludePackage(String string, String string1) throws IOException {
        if (string.length() != 0) {
            if (this.includedPackages.remove(string) != null) {
                this.excludedPackages.put(string, string);
                String string2 = ZkmUtils.slashesToDots(string);
                if (super.scriptEnvironment.isVerbose()) {
                    super.logWriter.println("\tExcluding package \"" + string2 + "\" from name obfuscation because of \"" + string1 + "\"");
                }

                int ba = string.lastIndexOf("/");

                while (ba > -1) {
                    String string3 = string.substring(0, ba);
                    if (this.includedPackages.remove(string3) != null) {
                        this.excludedPackages.put(string3, string3);
                        if (super.scriptEnvironment.isVerbose()) {
                            super.logWriter
                                    .println(
                                            "\tExcluding package \""
                                                    + ZkmUtils.slashesToDots(string3)
                                                    + "\" from name obfuscation because \""
                                                    + string2
                                                    + "\" has been excluded"
                                    );
                        }
                    }

                    ba = string3.lastIndexOf("/");
                }
            }
        }
    }

    @Override
    public final Enumeration getCandidateClasses() {
        return new ArrayEnumeration(this.allClasses);
    }

    @Override
    public final Enumeration getCandidateMethods() {
        return new ArrayEnumeration(this.allMethods);
    }

    public final Triple getClassLink(Object object) {
        return (Triple) this.classLinks.get(object);
    }

    public Set getLinkedMethodsOfField(Object object) {
        Set set1 = this.accessorMethodsByField.getValues(object);
        if (set1 != null && set1.size() > 0) {
            EquivalenceGroup equivalenceGroup = this.methodEquivalenceGroups.getGroup(set1.iterator().next());
            HashSet hashSet = ZkmUtils.createHashSet();
            Iterator iterator = equivalenceGroup.iterator();

            while (iterator.hasNext()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
                if (abstractMethodInfo.isProgramMember()) {
                    hashSet.add((MethodInfo) abstractMethodInfo);
                }
            }

            return hashSet;
        } else {
            return null;
        }
    }

    public void unexcludeRenamedFields(ChangeLogMapping changeLogMapping1, HashMap hashMap, FixedClassesExclusionSet fixedClassesExclusionSet1) throws ZkmException, IOException {
        if (changeLogMapping1 != null) {
            ArrayList arrayList = new ArrayList();
            Iterator iterator = super.excludedFields.keySet().iterator();

            while (iterator.hasNext()) {
                FieldInfo fieldInfo = (FieldInfo) iterator.next();
                if (changeLogMapping1.isFieldRenamed(fieldInfo.getClassName(), fieldInfo.getSignature())) {
                    arrayList.add(fieldInfo);
                }
            }

            for (int i = 0; i < arrayList.size(); i++) {
                FieldInfo fieldInfo1 = (FieldInfo) arrayList.get(i);
                this.forceUnexcludeField(
                        fieldInfo1,
                        "Input ChangeLog specifies a new name '" + changeLogMapping1.getResolvedNewFieldName(fieldInfo1.getClassName(), fieldInfo1.getSignature()) + "'",
                        hashMap
                );
                if (fixedClassesExclusionSet1 != null) {
                    fixedClassesExclusionSet1.reportFieldRenamed(fieldInfo1, changeLogMapping1.getChangeLogName());
                }
            }
        }
    }

    public final boolean hasNoIncludedPackages() {
        return this.includedPackages.size() == 0;
    }

    public NameExclusionSet(ClassRepository classRepository1, List list1, List list2, ClassFileBase[] classFileBases, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        this.inputClassFiles = classFileBases;
        if (!classRepository1.hasNoClassesOpened()) {
            this.collectModules();
            this.collectPackageNames(classRepository1.getRootPackageNode());
            this.initializeCandidateMaps(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            this.processExclusionParameters();
        }
    }

    public String formatMemberList(Set set1, HashMap hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            MemberInfo memberInfo1 = (MemberInfo) iterator.next();
            if (memberInfo1.isProgramMember()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append("\"");
                stringBuilder.append(((String) ZkmUtils.mapOrSelf(memberInfo1.getClassName(), hashMap)).replace('/', '.'));
                stringBuilder.append(' ');
                stringBuilder.append(memberInfo1.toDisplayString());
                stringBuilder.append("\"");
            }
        }

        return stringBuilder.toString();
    }

    public Set getEquivalentMethods(AbstractMethodInfo abstractMethodInfo) {
        EquivalenceGroup equivalenceGroup = this.methodEquivalenceGroups.getGroup(abstractMethodInfo);
        return equivalenceGroup != null ? ZkmUtils.createHashSetFrom(equivalenceGroup) : null;
    }

    public final boolean excludeClassInternal(ProgramClass programClass1, String string, boolean bl) throws ZkmException, IOException {
        if (this.classLinks.containsKey(programClass1)) {
            Triple triple = this.getClassLink(programClass1);
            ProgramClass programClass2 = (ProgramClass) triple.getFirst();
            if (programClass2 != null) {
                super.scriptEnvironment
                        .logWarning(
                                "Class \""
                                        + this.describeClass(programClass1)
                                        + "\" already linked by a <link> exclusion to \""
                                        + this.describeClass(programClass2)
                                        + "\" so \""
                                        + string
                                        + "\" will have no impact on this class name."
                        );
                return false;
            }
        }

        Object object = super.includedClasses.remove(programClass1);
        if (object != null) {
            super.excludedClasses.put(programClass1, programClass1);
            String string1 = "Excluding class \"" + this.describeClass(programClass1) + "\" from name obfuscation because of \"" + string + "\"";
            if (bl) {
                super.scriptEnvironment.logWarning(string1);
            } else if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter.println("\t" + string1);
            }
        }

        return object != null;
    }

    public final Enumeration getInputClassFiles() {
        return new ArrayEnumeration(this.inputClassFiles);
    }

    public final void addClassLink(ProgramClass programClass1, String string, String string1, List list1, String string2) throws ZkmException, IOException {
        if (super.excludedClasses.containsKey(programClass1)) {
            super.scriptEnvironment
                    .logWarning(
                            "Class \""
                                    + AbstractExclusionSpec.formatClassWithModifiers(programClass1, this.classRepository)
                                    + "\" already subject to an exclusion so \""
                                    + string2
                                    + "\" will have no impact on this class."
                    );
        } else {
            int ba = string.length();
            int bb = string1.length();
            String string3 = programClass1.getSimpleName();
            String string4 = programClass1.getPackagePath();
            if (string4.length() > 0) {
                string4 = string4 + "/";
            }

            String string5 = string4 + string3.substring(ba, string3.length() - bb);
            ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string5);
            if (programClass2 == null && list1.size() > 0) {
                String string6 = string3.substring(ba, string3.length() - bb);

                label85:
                for (int i = 0; i < list1.size(); i++) {
                    String string7 = (String) list1.get(i);
                    if (string7.indexOf(42) == -1) {
                        string5 = string7 + "/" + string6;
                        programClass2 = ClassHierarchyNode.findProgramClass(string5);
                        if (programClass2 != null) {
                            break;
                        }
                    } else {
                        int bd = 0;
                        int be = bd;

                        for (String[] strings = this.packageNames; be < strings.length; strings = this.packageNames) {
                            String string8 = this.packageNames[bd];
                            if (ZkmStringUtils.matchesWildcard(string8, string7)) {
                                string5 = string8 + "/" + string6;
                                programClass2 = ClassHierarchyNode.findProgramClass(string5);
                                if (programClass2 != null) {
                                    break label85;
                                }
                            }

                            be = ++bd;
                        }
                    }
                }
            }

            String string9 = ZkmUtils.slashesToDots(string5);
            boolean bl = false;
            Triple triple = ((com.zelix.klassmaster.util.Triple) (this.classLinks.put(programClass1, new Triple(programClass2, string, string1))));
            if (triple != null) {
                ProgramClass programClass3 = (ProgramClass) triple.getFirst();
                if (programClass3 != null) {
                    if (programClass2 == null) {
                        this.classLinks.put(programClass1, triple);
                        bl = true;
                    } else if (programClass3 != programClass2) {
                        super.scriptEnvironment
                                .logFatalError(
                                        "Class \""
                                                + AbstractExclusionSpec.formatClassWithModifiers(programClass1, this.classRepository)
                                                + "\" matches \""
                                                + string2
                                                + "\" but it is also matched by another <link> exclusion parameter with"
                                                + (((String) triple.getSecond()).length() > 0 ? " prefix \"" + (String) triple.getSecond() + "\" and" : "")
                                                + " suffix \""
                                                + string1
                                                + "\". Cannot satisfy both matches."
                                );
                    }
                }
            }

            if (programClass2 == null) {
                super.scriptEnvironment
                        .logWarning(
                                "Class \""
                                        + AbstractExclusionSpec.formatClassWithModifiers(programClass1, this.classRepository)
                                        + "\" matches \""
                                        + string2
                                        + "\" but no class matching \""
                                        + string3.substring(ba, string3.length() - bb)
                                        + "\" found in the same package"
                                        + (list1.size() == 0 ? "." : " or in the search path.")
                        );
            }

            if (super.scriptEnvironment.isVerbose()) {
                if (programClass2 == null) {
                    if (!bl) {
                        super.logWriter
                                .println(
                                        "\tExcluding"
                                                + (ba > 0 ? " prefix \"" + string + "\" and" : "")
                                                + " suffix \""
                                                + string1
                                                + "\" from renaming of class \""
                                                + this.describeClass(programClass1)
                                                + "\" from name obfuscation because of \""
                                                + string2
                                                + "\"."
                                );
                    }
                } else {
                    super.logWriter
                            .println(
                                    "\tClass \""
                                            + this.describeClass(programClass1)
                                            + "\" will be renamed to match \""
                                            + string9
                                            + "\" with the"
                                            + (ba > 0 ? " prefix \"" + string + "\" and the" : "")
                                            + " suffix \""
                                            + string1
                                            + "\" retained because of \""
                                            + string2
                                            + "\""
                            );
                }
            }
        }
    }

    public final void forceUnexcludePackage(
            String string, ChangeLogMapping changeLogMapping1, FixedClassesExclusionSet fixedClassesExclusionSet1, String string1, ArrayList arrayList
    ) throws IOException {
        if (string.length() != 0) {
            if (this.excludedPackages.remove(string) != null) {
                this.includedPackages.put(string, string);
                String string2 = ZkmUtils.slashesToDots(string);
                super.scriptEnvironment.logWarning("UNEXCLUDING package \"" + string2 + "\" for name obfuscation because \"" + string1 + "\"");
                if (fixedClassesExclusionSet1 != null) {
                    fixedClassesExclusionSet1.reportPackageRenamed(string, changeLogMapping1.getChangeLogName());
                }

                int ba = arrayList.size();

                for (int i = 0; i < ba; i++) {
                    String string3 = (String) arrayList.get(i);
                    if (string3.compareTo(string) <= 0) {
                        break;
                    }

                    if (string3.startsWith(string) && string3.charAt(string.length()) == '/' && this.excludedPackages.remove(string3) != null) {
                        this.includedPackages.put(string3, string3);
                        super.scriptEnvironment
                                .logWarning(
                                        "UNEXCLUDING package \"" + ZkmUtils.slashesToDots(string3) + "\" for name obfuscation because \"" + string2 + "\" has been unexcluded"
                                );
                        if (fixedClassesExclusionSet1 != null) {
                            fixedClassesExclusionSet1.reportPackageRenamed(string, changeLogMapping1.getChangeLogName());
                        }
                    }
                }
            }
        }
    }

    public final void unexcludeMethodSilently(MethodInfo methodInfo1) {
        ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.includedMethods.put(methodInfo1, programClass1);
        }
    }

    public final void excludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        if (!methodInfo1.isConstructor() && !methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.excludedMethods.put(methodInfo1, programClass1);
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    super.logWriter
                            .println(
                                    "\tExcluding method \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                            + "\" in class \""
                                            + this.describeClass(methodInfo1.getOwnerProgramClass())
                                            + "\" from name obfuscation because of \""
                                            + string
                                            + "\" (B)"
                            );
                }
            }
        }
    }

    public final void unexcludePackage(String string, String string1) throws IOException {
        if (string.length() != 0) {
            if (this.excludedPackages.remove(string) != null) {
                this.includedPackages.put(string, string);
                String string2 = ZkmUtils.slashesToDots(string);
                if (super.scriptEnvironment.isVerbose()) {
                    super.logWriter.println("\tUnexcluding package \"" + string2 + "\" for name obfuscation because of \"" + string1 + "\"");
                }

                String string3 = string + "/";
                Iterator iterator = this.excludedPackages.keySet().iterator();

                while (iterator.hasNext()) {
                    String string4 = (String) iterator.next();
                    if (string4.startsWith(string3)) {
                        iterator.remove();
                        this.includedPackages.put(string4, string4);
                        if (super.scriptEnvironment.isVerbose()) {
                            super.logWriter
                                    .println(
                                            "\tUnexcluding package \""
                                                    + ZkmUtils.slashesToDots(string4)
                                                    + "\" for name obfuscation because \""
                                                    + string2
                                                    + "\" has been unexcluded"
                                    );
                        }
                    }
                }
            }
        }
    }

    public AbstractFieldInfo[] findFieldsByName(ClassFileBase classFileBase, String string, String string1) {
        AbstractFieldInfo[] abstractFieldInfos = null;
        if (string1 == null) {
            abstractFieldInfos = classFileBase.findFieldsByName(string);
        } else {
            AbstractFieldInfo abstractFieldInfo = classFileBase.findField(string, string1);
            if (abstractFieldInfo != null) {
                abstractFieldInfos = new AbstractFieldInfo[]{abstractFieldInfo};
            }
        }

        return abstractFieldInfos;
    }

    public final Enumeration getIncludedPackages() {
        String[] strings = new String[this.includedPackages.size()];
        Iterator iterator = this.includedPackages.keySet().iterator();
        int ba = 0;

        while (iterator.hasNext()) {
            strings[ba++] = (String) iterator.next();
        }

        return new ArrayEnumeration(strings);
    }

    public final void addMethodLink(MethodInfo methodInfo1, String string, Object object, String string1) throws ZkmException, IOException {
        String string2 = ((java.lang.String) (this.methodLinkPrefixes.put(methodInfo1, string)));
        if (string2 != null && !string2.equals(string)) {
            this.methodLinkPrefixes.put(methodInfo1, string2);
            super.scriptEnvironment
                    .logWarning(
                            "Method \""
                                    + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                    + "\" in class \""
                                    + AbstractExclusionSpec.formatClassWithModifiers(methodInfo1.getOwnerProgramClass(), this.classRepository)
                                    + "\" matched to more than one prefix : \""
                                    + string
                                    + "\", \""
                                    + string2
                                    + "\". Prefix \""
                                    + string
                                    + "\" will be ignored."
                    );
        }

        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        int ba = string.length();
        String string3 = methodInfo1.getJvmName();
        String string5 = null;
        String string4;
        if (ba > 0) {
            string4 = MethodSignature.toPropertyName(string3, string);
        } else {
            string4 = string3;
        }

        String string6 = methodInfo1.getReturnDescriptor();
        int parameterCount = methodInfo1.getParameterCount();
        if (parameterCount == 0 && !string6.equals("V")) {
            string5 = string6;
        } else if (parameterCount == 1 && string6.equals("V")) {
            String string7 = methodInfo1.getParameterDescriptor();
            String string8 = string7.substring(1, string7.length() - 1);
            string5 = string8;
        }

        ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass1.getClassName());
        AbstractFieldInfo[] abstractFieldInfos = this.findFieldsByName(programClass1, string4, string5);
        if (abstractFieldInfos == null || abstractFieldInfos.length == 0) {
            do {
                ProgramClass programClass2 = classHierarchyNode.getSuperProgramClass();
                if (programClass2 != null) {
                    abstractFieldInfos = this.findFieldsByName(programClass2, string4, string5);
                    classHierarchyNode = ClassHierarchyNode.findNode(programClass2.getClassName());
                } else {
                    classHierarchyNode = null;
                }
            } while (classHierarchyNode != null && (abstractFieldInfos == null || abstractFieldInfos.length == 0));
        }

        if (abstractFieldInfos != null && abstractFieldInfos.length != 0) {
            if (abstractFieldInfos.length == 1) {
                FieldInfo fieldInfo = (FieldInfo) abstractFieldInfos[0];
                this.methodsByLinkKey.addValue(fieldInfo, methodInfo1);
                this.accessorMethodsByField.addValue(fieldInfo, methodInfo1);
                String string9 = (String) this.methodFieldLinks.putValue(methodInfo1, fieldInfo, string);
                if (super.scriptEnvironment.isVerbose()) {
                    super.logWriter
                            .println(
                                    "\tMethod \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                            + "\" in class \""
                                            + AbstractExclusionSpec.formatClassWithModifiers(programClass1, this.classRepository)
                                            + "\" will be renamed to match field \""
                                            + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                            + "\""
                                            + (string.length() > 0 ? " with the prefix \"" + string + "\" retained" : "")
                                            + " because of \""
                                            + string1
                                            + "\""
                            );
                }
            } else {
                Arrays.sort(abstractFieldInfos);
                TwoKeyMap twoKeyMap;
                if (object != null) {
                    this.methodsByLinkKey.addValue(object, methodInfo1);
                    twoKeyMap = this.methodFieldLinks;
                } else {
                    this.methodsByLinkKey.addValue(abstractFieldInfos[0], methodInfo1);
                    twoKeyMap = this.methodFieldLinks;
                }

                twoKeyMap.putValue(methodInfo1, (FieldInfo) abstractFieldInfos[0], string);
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < abstractFieldInfos.length; i++) {
                    this.accessorMethodsByField.addValue((FieldInfo) abstractFieldInfos[i], methodInfo1);
                    stringBuilder.append("\"");
                    stringBuilder.append(abstractFieldInfos[i].toDisplayString());
                    stringBuilder.append("\"");
                    if (i < abstractFieldInfos.length - 1) {
                        stringBuilder.append(", ");
                    }
                }

                super.scriptEnvironment
                        .logWarning(
                                "More than one field found in class \""
                                        + AbstractExclusionSpec.formatClassWithModifiers(programClass1, this.classRepository)
                                        + "\" to match method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" due to '"
                                        + string1
                                        + "' : "
                                        + stringBuilder
                        );
            }
        } else if (ba > 0) {
            if (object != null) {
                this.methodsByLinkKey.addValue(object, methodInfo1);
            } else {
                this.methodsByLinkKey.addValue(methodInfo1.getClassName() + '~' + string4, methodInfo1);
            }

            this.prefixOnlyLinkedMethods.putValue(methodInfo1, string, object);
            if (super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println(
                                "\tMethod \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + AbstractExclusionSpec.formatClassWithModifiers(programClass1, this.classRepository)
                                        + "\" will be renamed with the prefix \""
                                        + string
                                        + "\" retained because of \""
                                        + string1
                                        + "\""
                        );
            }
        } else if (object != null) {
            this.methodsByLinkKey.addValue(object, methodInfo1);
        }
    }

    public static ParameterListStatement parsePublicProtectedExcludes(ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        return parseExcludeScript(PUBLIC_PROTECTED_EXCLUDE_SCRIPT, scriptEnvironment1);
    }

    @Override
    public final void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        this.unexcludeClassInternal(programClass1, string, false);
    }

    public final void unexcludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        if (!methodInfo1.isConstructor() && !methodInfo1.isStaticInitializer()) {
            ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
            if (programClass1 != null) {
                this.includedMethods.put(methodInfo1, programClass1);
                if (super.scriptEnvironment.isVerbose()) {
                    super.logWriter
                            .println(
                                    "\tUnexcluding method \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                            + "\" in class \""
                                            + this.describeClass(methodInfo1.getOwnerProgramClass())
                                            + "\" for name obfuscation because of \""
                                            + string
                                            + "\""
                            );
                }
            }
        }
    }

    @Override
    public boolean hasNoIncludedMethods() {
        if (this.includedMethods.size() == 0) {
            return true;
        }

        Iterator iterator = this.includedMethods.keySet().iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            if (!methodInfo1.isConstructor() && !methodInfo1.isStaticInitializer()) {
                return false;
            }
        }

        return true;
    }

    public boolean isInLinkedGroup(AbstractMethodInfo abstractMethodInfo) {
        return this.methodEquivalenceGroups.containsElement(abstractMethodInfo);
    }

    public Set getPrefixOnlyLinkedMethods() {
        return ZkmUtils.createHashSetFrom(this.prefixOnlyLinkedMethods.keySet());
    }

    public Set getSamePrefixGroupMethods(AbstractMethodInfo abstractMethodInfo) {
        EquivalenceGroup equivalenceGroup = this.methodEquivalenceGroups.getGroup(abstractMethodInfo);
        return equivalenceGroup != null
                ? ZkmUtils.createHashSetFrom(this.groupMethodsByPrefix.getValues(equivalenceGroup, this.methodLinkPrefixes.get(abstractMethodInfo)))
                : null;
    }

    public void unexcludeRenamedClasses(ChangeLogMapping changeLogMapping1, FixedClassesExclusionSet fixedClassesExclusionSet1) throws ZkmException, IOException {
        if (changeLogMapping1 != null) {
            ArrayList arrayList = new ArrayList();
            Iterator iterator = super.excludedClasses.keySet().iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator.next();
                if (changeLogMapping1.isSimpleNameChanged(programClass1.getClassName())) {
                    arrayList.add(programClass1);
                }
            }

            for (int i = 0; i < arrayList.size(); i++) {
                ProgramClass programClass2 = (ProgramClass) arrayList.get(i);
                this.unexcludeClassInternal(programClass2, "Input ChangeLog specifies a new name", true);
                if (fixedClassesExclusionSet1 != null) {
                    fixedClassesExclusionSet1.reportClassRenamed(programClass2, changeLogMapping1.getChangeLogName());
                }
            }
        }
    }

    @Override
    public final Enumeration getCandidateFields() {
        return new ArrayEnumeration(this.allFields);
    }

    public static ParameterListStatement parseDefaultExcludeInput(ScriptEnvironment scriptEnvironment1, BufferedReader bufferedReader) throws ZkmException, ZkmScriptParseException, IOException {
        ZkmScriptParser zkmScriptParser = new ZkmScriptParser(bufferedReader);

        ZkmScriptSimpleNode zkmScriptSimpleNode;
        try {
            zkmScriptSimpleNode = zkmScriptParser.DefaultExcludeInput();
            zkmScriptSimpleNode.execute(null, scriptEnvironment1);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException iOException) {
            }
        }

        return ((ASTDefaultExcludeInput) zkmScriptSimpleNode).getParameterListStatement();
    }

    public final boolean isPackageExcluded(Object object) {
        return this.excludedPackages.containsKey(object);
    }
}
