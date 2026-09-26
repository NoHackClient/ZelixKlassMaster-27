package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.SerialVersionUidCalculator;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ExistingSerializedClassesHandler extends FixedClassMatchHandler {
    public final FixedClassesExclusionSet fixedClassesExclusionSet;

    public final void processExclusionParameters() throws ZkmException, IOException {
        int ba = super.exclusionStatements.size();
        HashMap hashMap = ZkmUtils.createHashMap();
        Vector vector = new Vector();

        for (int i = 0; i < ba; i++) {
            ParameterListStatement parameterListStatement = (ParameterListStatement) super.exclusionStatements.get(i);
            Enumeration enumeration = parameterListStatement.getRenameFilterParameters();

            while (enumeration.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                if (!hashMap.containsKey(aSTRenameFilterParameter)) {
                    hashMap.put(aSTRenameFilterParameter, aSTRenameFilterParameter);
                    vector.addElement(aSTRenameFilterParameter);
                }
            }
        }

        if (super.scriptEnvironment.isVerbose() && vector.size() > 0) {
            super.logWriter.println("\tExisting Serialized classes parameters:");

            for (int i = vector.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + vector.elementAt(i));
            }
        }

        for (int i = vector.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) vector.elementAt(i);
            if (!aSTRenameFilterParameter1.isClassSpecifier()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter1
                                        + "' in 'existingSerializedClasses' statement is not a class exclusion parameter. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter1.hasLinkClassName()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter1 + "' in 'existingSerializedClasses' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
            } else {
                if (aSTRenameFilterParameter1.isClassPlusSpec()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '" + aSTRenameFilterParameter1 + "' in 'existingSerializedClasses' cannot use the '" + "+" + "' tag. The tag will be ignored.",
                                    true
                            );
                }

                aSTRenameFilterParameter1.applySerializedClassesExclusion(this);
            }
        }
    }

    public void excludeSerializableHierarchies() throws ZkmException, IOException {
        Enumeration enumeration = this.getExcludedClasses();
        HashSet hashSet = ZkmUtils.createHashSet();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();

            try {
                this.excludeSerializableClass(programClass1, hashSet, programClass1, true);
            } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
                super.scriptEnvironment
                        .logWarning(
                                "Error while setting existing serialized classes exclusions : class '"
                                        + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName())
                                        + "' not found."
                        );
            } catch (ClassFileLoadException classFileLoadException) {
                super.scriptEnvironment.logWarning("Error while setting existing serialized classes exclusions : " + classFileLoadException.getMessage());
            }
        }
    }

    public final void initializeCandidateMaps(Enumeration enumeration, int ba) {
        super.includedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.excludedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.includedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        super.excludedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.includedClasses.put(programClass1, programClass1);
            ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

            while (arrayEnumeration.hasMoreElements()) {
                FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
                super.includedFields.put(fieldInfo, fieldInfo.getProgramClass());
            }
        }
    }

    public final void applyTrimExclusions(TrimProcessor trimProcessor1, PrintWriter printWriter) throws ZkmException, IOException {
        printWriter.println("\tApplying trim exclusions specified by existingSerializedClasses statement");
        Enumeration enumeration = this.getExcludedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            ProgramClass programClass2 = (ProgramClass) super.excludedClasses.get(programClass1);
            trimProcessor1.excludeClass(programClass1, "Existing serialized class '" + this.describeClass(programClass2) + "'");
        }

        Enumeration enumeration1 = this.getExcludedFields();

        while (enumeration1.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) enumeration1.nextElement();
            ProgramClass programClass3 = (ProgramClass) super.excludedFields.get(fieldInfo);
            trimProcessor1.matchField(fieldInfo, "Existing serialized class '" + this.describeClass(programClass3) + "'");
        }
    }

    public void excludeSerialVersionUidFields(InheritedMemberAnalyzer inheritedMemberAnalyzer) throws ZkmException, IOException {
        Enumeration enumeration = this.getExcludedClasses();

        try {
            while (enumeration.hasMoreElements()) {
                ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
                String string = programClass1.getClassName();
                if ((this.fixedClassesExclusionSet == null || !this.fixedClassesExclusionSet.isMatchedClass(programClass1))
                        && !this.classRepository.isSubclass(string, "java/lang/Enum")
                        && !this.classRepository.isSubclass(string, "java/lang/reflect/Proxy")) {
                    FieldInfo fieldInfo = this.classRepository.findField(string, "serialVersionUID", "J");
                    Map map1;
                    if (fieldInfo == null) {
                        long ba = SerialVersionUidCalculator.computeSerialVersionUid(programClass1, this.classRepository);
                        fieldInfo = programClass1.addSerialVersionUidField(ba, inheritedMemberAnalyzer, this.classRepository);
                        map1 = super.includedFields;
                    } else {
                        map1 = super.includedFields;
                    }

                    ProgramClass programClass2 = (ProgramClass) map1.remove(fieldInfo);
                    if (programClass2 != null) {
                        super.excludedFields.put(fieldInfo, programClass1);
                        map1 = super.excludedFields;
                    } else {
                        map1 = super.excludedFields;
                    }

                    map1.put(fieldInfo, programClass1);
                }
            }
        } catch (ZkmProcessingException zkmProcessingException) {
            super.scriptEnvironment.logFatalError("Could not process existing serialized classes : '" + zkmProcessingException.getMessage() + "'");
        }
    }

    public final void excludeSerializedField(FieldInfo fieldInfo, Set set1, ProgramClass programClass1) throws ZkmException, IOException {
        if ((ProgramClass) super.includedFields.remove(fieldInfo) != null) {
            super.excludedFields.put(fieldInfo, programClass1);
            ProgramClass programClass2 = fieldInfo.getTypeProgramClass();
            if (programClass2 != null) {
                this.excludeSerializableClass(programClass2, set1, programClass1, true);
            }
        }
    }

    public final void excludeSerializedFields(String string, ProgramClass programClass1, Set set1, ProgramClass programClass2) throws ZkmException, IOException {
        if (this.implementsInterface(string, "java/io/Externalizable")) {
            this.excludeInstanceFields(programClass1, set1, programClass2);
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);

            for (ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                 classHierarchyNode1 != null && classHierarchyNode1.isProgramClass();
                 classHierarchyNode1 = classHierarchyNode1.getSuperclassNode()
            ) {
                ProgramClass programClass3 = classHierarchyNode1.getProgramClass();
                this.excludeInstanceFields(programClass3, set1, programClass2);
            }
        } else {
            FieldInfo fieldInfo1;
            if ((fieldInfo1 = this.classRepository.findField(string, "serialPersistentFields", "[Ljava/io/ObjectStreamField;")) != null && fieldInfo1.isStatic()) {
                ProgramClass programClass4 = (ProgramClass) super.includedFields.remove(fieldInfo1);
                if (programClass4 != null) {
                    super.excludedFields.put(fieldInfo1, programClass1);
                }

                try {
                    List list1 = programClass1.getSerialPersistentFieldNames(this.classRepository);
                    int ba = list1.size();

                    for (int i = 0; i < ba; i++) {
                        FieldInfo fieldInfo = (FieldInfo) list1.get(i);
                        this.excludeSerializedField(fieldInfo, set1, programClass2);
                    }
                } catch (ZkmProcessingException zkmProcessingException) {
                    super.scriptEnvironment
                            .logWarning("Error while analyzing 'serialPersistentFields' in class '" + string + "' : " + zkmProcessingException.getMessage());
                }
            } else {
                this.excludeInstanceFields(programClass1, set1, programClass2);
            }
        }
    }

    public ExistingSerializedClassesHandler(
            ClassRepository classRepository1,
            List list1,
            ScriptEnvironment scriptEnvironment1,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer
    ) throws ZkmException, IOException {
        super(classRepository1, list1, scriptEnvironment1);
        this.fixedClassesExclusionSet = fixedClassesExclusionSet1;
        if (classRepository1.hasProgramClasses()) {
            this.initializeCandidateMaps(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            this.processExclusionParameters();
            this.excludeSerializableHierarchies();
            this.excludeSerialVersionUidFields(inheritedMemberAnalyzer);
        }
    }

    public void applyNameExclusions(NameExclusionSet nameExclusionSet, PrintWriter printWriter) throws ZkmException, IOException {
        printWriter.println("\tApplying exclusions specified by existingSerializedClasses statement");
        Enumeration enumeration = this.getExcludedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            ProgramClass programClass2 = (ProgramClass) super.excludedClasses.get(programClass1);
            String string = programClass1.getPackagePath();
            if (string != null && string.length() > 0) {
                nameExclusionSet.excludePackage(string, "Existing serialized class '" + this.describeClass(programClass2) + "'");
            }

            nameExclusionSet.excludeClass(programClass1, "Existing serialized class '" + this.describeClass(programClass2) + "'");
        }

        Enumeration enumeration1 = this.getExcludedFields();

        while (enumeration1.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) enumeration1.nextElement();
            ProgramClass programClass3 = (ProgramClass) super.excludedFields.get(fieldInfo);
            nameExclusionSet.excludeField(fieldInfo, "Existing serialized class '" + this.describeClass(programClass3) + "'");
        }
    }

    public final void excludeSerializableClass(ProgramClass programClass1, Set set1, ProgramClass programClass2, boolean bl) throws ZkmException, IOException {
        if (programClass1 != null && !set1.contains(programClass1)) {
            set1.add(programClass1);
            String string = programClass1.getClassName();
            if (!programClass1.isInterface()
                    && (
                    this.implementsInterface(programClass1.getClassName(), "java/io/Serializable")
                            || this.implementsInterface(programClass1.getClassName(), "java/io/Externalizable")
            )) {
                this.markClassExcluded(programClass1, programClass2);
                this.excludeSerializedFields(string, programClass1, set1, programClass2);
            }

            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
            ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
            if (classHierarchyNode1 != null && classHierarchyNode1.isProgramClass()) {
                ProgramClass programClass3 = classHierarchyNode1.getProgramClass();
                this.excludeSerializableClass(programClass3, set1, programClass2, false);
            }

            if (bl) {
                Enumeration enumeration = classHierarchyNode.enumerateSubclasses();
                if (enumeration != null) {
                    while (enumeration.hasMoreElements()) {
                        ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration.nextElement();
                        ProgramClass programClass4 = classHierarchyNode2.getProgramClass();
                        this.excludeSerializableClass(programClass4, set1, programClass2, true);
                    }
                }

                Enumeration enumeration1 = classHierarchyNode.enumerateImplementors();
                if (enumeration1 != null) {
                    while (enumeration1.hasMoreElements()) {
                        ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) enumeration1.nextElement();
                        ProgramClass programClass5 = classHierarchyNode3.getProgramClass();
                        this.excludeSerializableClass(programClass5, set1, programClass2, true);
                    }
                }
            }
        }
    }

    public final List excludeInstanceFields(ProgramClass programClass1, Set set1, ProgramClass programClass2) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

        while (arrayEnumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
            if (!fieldInfo.isStatic() && !fieldInfo.isTransient()) {
                this.excludeSerializedField(fieldInfo, set1, programClass2);
            }

            if (!fieldInfo.isStrictlyPrivate() || !fieldInfo.isStatic() && !fieldInfo.isTransient()) {
                arrayList.add(fieldInfo);
            }
        }

        return arrayList;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
