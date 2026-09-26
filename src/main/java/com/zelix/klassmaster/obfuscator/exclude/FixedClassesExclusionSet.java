package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.constants.IntegerEncryptionExclusions;
import com.zelix.klassmaster.obfuscator.constants.LongEncryptionExclusionHandler;
import com.zelix.klassmaster.obfuscator.exceptions.ExceptionObfuscationExclusions;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationExclusions;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.obfuscator.string.StringEncryptionExclusionSpec;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FixedClassesExclusionSet extends FixedClassMatchHandler {
    public final HashMap brokenFixedClassReasons = ZkmUtils.createHashMap();
    public final TwoKeyMap packageDependents;
    public final TwoKeyMap classDependents;
    public final TwoKeyMap fieldDependents;
    public final TwoKeyMap methodDependents;

    public void collectFixedDependencies() throws ZkmProcessingException {
        Enumeration enumeration = this.getExcludedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            String string = programClass1.getSuperclassName();
            if (string != null) {
                ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string);
                if (programClass2 != null) {
                    this.registerClassDependency(programClass2, programClass1);
                    if (!this.isClassExcluded(programClass2)) {
                        this.markClassExcluded(programClass2, programClass1);
                    }
                }
            }

            String[] strings = programClass1.getInterfaceNames();

            for (int i = 0; i < strings.length; i++) {
                String string1 = strings[i];
                ProgramClass programClass3 = ClassHierarchyNode.findProgramClass(string1);
                if (programClass3 != null) {
                    this.registerClassDependency(programClass3, programClass1);
                    if (!this.isClassExcluded(programClass3)) {
                        this.markClassExcluded(programClass3, programClass1);
                    }
                }
            }

            FieldInfo[] fieldInfos = programClass1.getFieldInfos();

            for (int i = 0; i < fieldInfos.length; i++) {
                this.excludeDependentField(fieldInfos[i], programClass1);
            }

            MethodInfo[] methodInfos = programClass1.getMethodInfos();

            for (int i = 0; i < methodInfos.length; i++) {
                this.excludeDependentMethod(methodInfos[i], programClass1);
            }

            HashSet hashSet2 = ZkmUtils.createHashSet();
            HashSet hashSet = ZkmUtils.createHashSet();
            HashSet hashSet1 = ZkmUtils.createHashSet();
            programClass1.collectReferencedClasses(hashSet2, hashSet, hashSet1);
            Iterator iterator = hashSet2.iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass4 = (ProgramClass) iterator.next();
                if (programClass4.isProgramClass()) {
                    this.registerClassDependency(programClass4, programClass1);
                    if (!this.isClassExcluded(programClass4)) {
                        this.markClassExcluded(programClass4, programClass1);
                    }
                }
            }

            iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                FieldInfo fieldInfo = (FieldInfo) iterator.next();
                if (!this.isFieldExcluded(fieldInfo)) {
                    this.excludeDependentField(fieldInfo, programClass1);
                }
            }

            iterator = hashSet1.iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                if (!this.isMethodExcluded(methodInfo1)) {
                    this.excludeDependentMethod(methodInfo1, programClass1);
                }
            }
        }
    }

    public final void initializeCandidateMaps(Enumeration enumeration, int ba) {
        super.includedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.excludedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.includedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        super.excludedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.includedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.excludedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.includedClasses.put(programClass1, programClass1);
            ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

            while (arrayEnumeration.hasMoreElements()) {
                FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
                super.includedFields.put(fieldInfo, fieldInfo.getProgramClass());
            }

            ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

            while (arrayEnumeration1.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
                if (!methodInfo1.isConstructor() && !methodInfo1.isStaticInitializer()) {
                    this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
                }
            }
        }
    }

    public final void processExclusionParameters() throws ZkmException, IOException {
        int ba = super.exclusionStatements.size();
        HashMap hashMap = ZkmUtils.createHashMap();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            ParameterListStatement parameterListStatement = (ParameterListStatement) super.exclusionStatements.get(i);
            Enumeration enumeration = parameterListStatement.getRenameFilterParameters();

            while (enumeration.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) enumeration.nextElement();
                if (!hashMap.containsKey(aSTRenameFilterParameter)) {
                    hashMap.put(aSTRenameFilterParameter, aSTRenameFilterParameter);
                    arrayList.add(aSTRenameFilterParameter);
                }
            }
        }

        if (super.scriptEnvironment.isVerbose() && arrayList.size() > 0) {
            super.logWriter.println("\tFixed classes parameters:");

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + arrayList.get(i));
            }
        }

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) arrayList.get(i);
            if (!aSTRenameFilterParameter1.isClassSpecifier()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter1 + "' in 'fixedClasses' statement is not a class exclusion parameter. It will be ignored.", true
                        );
            } else if (aSTRenameFilterParameter1.hasLinkClassName()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter1 + "' in 'fixedClasses' statement cannot use the '<link>' syntax. It will be ignored.", true
                        );
            } else {
                if (aSTRenameFilterParameter1.isClassPlusSpec()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '" + aSTRenameFilterParameter1 + "' in 'fixedClasses' statement cannot use the '" + "+" + "' tag. The tag will be ignored.",
                                    true
                            );
                }

                aSTRenameFilterParameter1.applyFixedClassesExclusion(this);
            }
        }
    }

    public final void excludeDependentMethod(MethodInfo methodInfo1, ProgramClass programClass1) {
        if ((ProgramClass) this.includedMethods.remove(methodInfo1) != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
            ArrayList arrayList = methodInfo1.getDescriptorProgramClasses();
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    ProgramClass programClass2 = (ProgramClass) arrayList.get(i);
                    if (!this.isClassExcluded(programClass2)) {
                        this.markClassExcluded(programClass2, programClass1);
                    }
                }
            }
        }

        this.methodDependents.putValue(methodInfo1, programClass1, programClass1);
    }

    public FixedClassesExclusionSet(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        super(classRepository1, list1, scriptEnvironment1);
        int classCount = classRepository1.getClassCount();
        this.packageDependents = new TwoKeyMap();
        this.classDependents = new TwoKeyMap(classCount, 13);
        this.fieldDependents = new TwoKeyMap(classCount * 5, 13);
        this.methodDependents = new TwoKeyMap(classCount * 5, 13);
        if (HiddenOptionFlags.FIXED_TOTALLY_UNCHANGED) {
            scriptEnvironment1.logMessage("Fixed classes will be saved completely unchanged because 'ZKM_FIXED_TOTALLY_UNCHANGED' set.");
        }

        if (classRepository1.getClassCount() > 0) {
            this.initializeCandidateMaps(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            this.processExclusionParameters();
            this.collectFixedDependencies();
        }
    }

    public final void applyTrimExclusions(TrimProcessor trimProcessor1, PrintWriter printWriter) throws ZkmException, IOException {
        printWriter.println("\tApplying trim exclusions specified by fixedClasses statement");
        Enumeration enumeration = this.getMatchedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            String string = "Fixed class '" + this.describeClass(programClass1) + "'";
            trimProcessor1.excludeClass(programClass1, string);
            HashSet hashSet = ZkmUtils.createHashSet();
            HashSet hashSet1 = ZkmUtils.createHashSet();
            HashSet hashSet2 = ZkmUtils.createHashSet();
            HashSet hashSet3 = ZkmUtils.createHashSet();
            programClass1.collectFixedClassReferences(hashSet, hashSet1, hashSet2, hashSet3);
            if (!hashSet.isEmpty()) {
                Iterator iterator = hashSet.iterator();

                while (iterator.hasNext()) {
                    ProgramClass programClass2 = (ProgramClass) iterator.next();
                    trimProcessor1.excludeClass(programClass2, string);
                }
            }

            if (!hashSet1.isEmpty()) {
                Iterator iterator1 = hashSet1.iterator();

                while (iterator1.hasNext()) {
                    ProgramClass programClass3 = (ProgramClass) iterator1.next();
                    trimProcessor1.excludeClass(programClass3, string);
                }
            }

            if (!hashSet2.isEmpty()) {
                Iterator iterator2 = hashSet2.iterator();

                while (iterator2.hasNext()) {
                    FieldInfo fieldInfo = (FieldInfo) iterator2.next();
                    trimProcessor1.matchField(fieldInfo, string);
                }
            }

            if (!hashSet3.isEmpty()) {
                Iterator iterator3 = hashSet3.iterator();

                while (iterator3.hasNext()) {
                    MethodInfo methodInfo2 = (MethodInfo) iterator3.next();
                    trimProcessor1.matchMethod(methodInfo2, string);
                }
            }

            ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

            while (arrayEnumeration.hasMoreElements()) {
                FieldInfo fieldInfo1 = (FieldInfo) arrayEnumeration.nextElement();
                trimProcessor1.matchField(fieldInfo1, string);
            }

            ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

            while (arrayEnumeration1.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
                trimProcessor1.matchMethod(methodInfo1, string);
            }
        }
    }

    public void reportClassRenamed(ProgramClass programClass1, String string) {
        String string1 = programClass1.getDottedClassName();
        if (this.isMatchedClass(programClass1) && !this.brokenFixedClassReasons.containsKey(programClass1)) {
            this.brokenFixedClassReasons.put(programClass1, "'" + string1 + "' given a new name by the input change log '" + string + "'");
            super.scriptEnvironment
                    .logWarning("Class '" + string1 + "' was specified as 'fixed' but it has been given a new name by the input change log '" + string + "'");
        }

        if (this.hasClassDependents(programClass1)) {
            Map map1 = this.classDependents.getInnerMap(programClass1);
            if (map1 != null) {
                Iterator iterator = map1.keySet().iterator();

                while (iterator.hasNext()) {
                    ProgramClass programClass2 = (ProgramClass) iterator.next();
                    if (!this.brokenFixedClassReasons.containsKey(programClass2)) {
                        this.brokenFixedClassReasons.put(programClass2, "Class '" + string1 + "' given a new name by the input change log '" + string + "'");
                        super.scriptEnvironment
                                .logWarning(
                                        "Class '"
                                                + programClass2.getDottedClassName()
                                                + "' was specified as 'fixed' but it has had to be changed because class '"
                                                + programClass1.getDottedClassName()
                                                + "' given a new name by the input change log '"
                                                + string
                                                + "'"
                                );
                    }
                }
            }
        }
    }

    public boolean hasClassDependents(Object object) {
        Map map1 = this.classDependents.getInnerMap(object);
        return map1 != null && map1.size() > 0;
    }

    public void reportFieldRenamed(FieldInfo fieldInfo, String string) {
        Map map1 = this.fieldDependents.getInnerMap(fieldInfo);
        if (map1 != null) {
            Iterator iterator = map1.keySet().iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator.next();
                if (!this.brokenFixedClassReasons.containsKey(programClass1)) {
                    this.brokenFixedClassReasons
                            .put(
                                    programClass1,
                                    "Field '"
                                            + fieldInfo.toDisplayString()
                                            + "' in class '"
                                            + fieldInfo.getDottedClassName()
                                            + "' given a new name by the input change log '"
                                            + string
                                            + "'"
                            );
                    super.scriptEnvironment
                            .logWarning(
                                    "Class '"
                                            + programClass1.getDottedClassName()
                                            + "' was specified as 'fixed' but it has had to be changed because field '"
                                            + fieldInfo.toDisplayString()
                                            + "' in class '"
                                            + fieldInfo.getDottedClassName()
                                            + "' given a new name by the input change log '"
                                            + string
                                            + "'"
                            );
                }
            }
        }
    }

    public void registerClassDependency(ProgramClass programClass1, ProgramClass programClass2) {
        String string = programClass1.getPackagePath();
        TwoKeyMap twoKeyMap;
        if (string.length() > 0) {
            this.packageDependents.putValue(string, programClass2, programClass1);
            String[] strings = RootPackageNode.getParentPackagePaths(string);

            for (int i = 0; i < strings.length; i++) {
                this.packageDependents.putValue(strings[i], programClass2, programClass1);
            }

            twoKeyMap = this.classDependents;
        } else {
            twoKeyMap = this.classDependents;
        }

        twoKeyMap.putValue(programClass1, programClass2, programClass2);
    }

    public void reportMethodChanged(MethodInfo methodInfo1, String string, boolean bl) {
        Map map1 = this.methodDependents.getInnerMap(methodInfo1);
        if (map1 != null) {
            Iterator iterator = map1.keySet().iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator.next();
                if (!this.brokenFixedClassReasons.containsKey(programClass1)) {
                    String string1 = bl ? "given a new name" : "given changed parameters";
                    this.brokenFixedClassReasons
                            .put(
                                    programClass1,
                                    "Method '"
                                            + methodInfo1.toDisplayString()
                                            + "' in class '"
                                            + methodInfo1.getDottedClassName()
                                            + "' "
                                            + string1
                                            + " by the input change log '"
                                            + string
                                            + "'"
                            );
                    super.scriptEnvironment
                            .logWarning(
                                    "Class '"
                                            + programClass1.getDottedClassName()
                                            + "' was specified as 'fixed' but it has had to be changed because method '"
                                            + methodInfo1.toDisplayString()
                                            + "' in class '"
                                            + methodInfo1.getDottedClassName()
                                            + "' "
                                            + string1
                                            + " by the input change log '"
                                            + string
                                            + "'"
                            );
                }
            }
        }
    }

    public String getBrokenReason(Object object) {
        return (String) this.brokenFixedClassReasons.get(object);
    }

    public void applyExclusions(
            NameExclusionSet nameExclusionSet,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ExceptionObfuscationExclusions exceptionObfuscationExclusions,
            StringEncryptionExclusionSpec stringEncryptionExclusionSpec,
            IntegerEncryptionExclusions integerEncryptionExclusions,
            LongEncryptionExclusionHandler longEncryptionExclusionHandler,
            PrintWriter printWriter
    ) throws ZkmException, IOException {
        printWriter.println("\tApplying exclusions specified by fixedClasses statement");
        Enumeration enumeration = this.getExcludedClasses();

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            ProgramClass programClass2 = (ProgramClass) super.excludedClasses.get(programClass1);
            String string = "Fixed class '" + this.describeClass(programClass2) + "'";
            String string1 = programClass1.getPackagePath();
            if (string1 != null && string1.length() > 0) {
                nameExclusionSet.excludePackage(string1, string);
            }

            nameExclusionSet.excludeClass(programClass1, string);
        }

        Enumeration enumeration1 = this.getExcludedFields();

        while (enumeration1.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) enumeration1.nextElement();
            ProgramClass programClass3 = (ProgramClass) super.excludedFields.get(fieldInfo);
            String string3 = "Fixed class '" + this.describeClass(programClass3) + "'";
            nameExclusionSet.excludeField(fieldInfo, string3);
        }

        Enumeration enumeration2 = this.getExcludedMethods();

        while (enumeration2.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration2.nextElement();
            ProgramClass programClass4 = (ProgramClass) this.excludedMethods.get(methodInfo1);
            String string2 = "Fixed class '" + this.describeClass(programClass4) + "'";
            nameExclusionSet.excludeMethod(methodInfo1, string2);
        }

        Enumeration enumeration3 = this.getMatchedClasses();

        while (enumeration3.hasMoreElements()) {
            ProgramClass programClass5 = (ProgramClass) enumeration3.nextElement();
            String string4 = "Fixed class '" + this.describeClass(programClass5) + "'";
            if (flowObfuscationExclusions != null) {
                flowObfuscationExclusions.excludeClassInternal(programClass5, string4, true);
            }

            if (exceptionObfuscationExclusions != null) {
                exceptionObfuscationExclusions.excludeClassInternal(programClass5, string4, true);
            }

            if (stringEncryptionExclusionSpec != null) {
                stringEncryptionExclusionSpec.excludeClass(programClass5, string4);
            }

            if (integerEncryptionExclusions != null) {
                integerEncryptionExclusions.excludeClass(programClass5, string4);
            }

            if (longEncryptionExclusionHandler != null) {
                longEncryptionExclusionHandler.excludeClass(programClass5, string4);
            }
        }
    }

    public boolean isFixedClassBroken(Object object) {
        return this.brokenFixedClassReasons.containsKey(object);
    }

    public final void excludeDependentField(FieldInfo fieldInfo, ProgramClass programClass1) {
        if ((ProgramClass) super.includedFields.remove(fieldInfo) != null) {
            super.excludedFields.put(fieldInfo, programClass1);
            ProgramClass programClass2 = fieldInfo.getTypeProgramClass();
            if (programClass2 != null && !this.isClassExcluded(programClass2)) {
                this.markClassExcluded(programClass2, programClass1);
            }
        }

        this.fieldDependents.putValue(fieldInfo, programClass1, programClass1);
    }

    public void reportPackageRenamed(String string, String string1) {
        if (this.hasPackageDependents(string)) {
            Map map1 = this.packageDependents.getInnerMap(string);
            if (map1 != null) {
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    ProgramClass programClass1 = (ProgramClass) entry.getKey();
                    ProgramClass programClass2 = (ProgramClass) entry.getValue();
                    if (!this.brokenFixedClassReasons.containsKey(programClass1)) {
                        this.brokenFixedClassReasons
                                .put(
                                        programClass1,
                                        "Class '"
                                                + programClass2.getDottedClassName()
                                                + "' has been given a new fully qualified name by the input change log '"
                                                + string1
                                                + "'"
                                );
                        super.scriptEnvironment
                                .logWarning(
                                        "Class '"
                                                + programClass1.getDottedClassName()
                                                + "' was specified as 'fixed' but it has had to be changed because class '"
                                                + programClass2.getDottedClassName()
                                                + "' has been given a new fully qualified name by the input change log '"
                                                + string1
                                                + "'"
                                );
                    }
                }
            }
        }
    }

    public boolean hasPackageDependents(Object object) {
        Map map1 = this.packageDependents.getInnerMap(object);
        return map1 != null && map1.size() > 0;
    }
}
