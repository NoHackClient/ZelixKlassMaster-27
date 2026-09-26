package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ExclusionHandlerBase;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ReflectionAccessMatcher extends ExclusionHandlerBase {
    @Override
    public final void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        if (super.excludedClasses.remove(programClass1) != null) {
            super.includedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose()) {
                super.logWriter
                        .println("\tMatched as NOT accessed by Reflection class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }
    }

    public void initMatchMaps(int ba) {
        super.includedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.excludedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.includedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        super.excludedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.includedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.excludedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
    }

    public final void markFieldAccessed(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) super.includedFields.remove(fieldInfo);
        if (programClass1 != null) {
            super.excludedFields.put(fieldInfo, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = fieldInfo.getProgramClass();
                super.logWriter
                        .println(
                                "\tMatched as accessed by Reflection field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass2)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public final void initAsNotAccessed(Enumeration enumeration, int ba) {
        this.initMatchMaps(ba);

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
                this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }
    }

    public ReflectionAccessMatcher(ClassRepository classRepository1, List list1, List list2, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        if (classRepository1.hasProgramClasses()) {
            if (list1 != null && list1.size() != 0) {
                this.initAsNotAccessed(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            } else {
                this.initAsAccessed(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            }

            this.applyAccessStatements();
        }
    }

    public final void initAsAccessed(Enumeration enumeration, int ba) {
        this.initMatchMaps(ba);

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.excludedClasses.put(programClass1, programClass1);
            ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

            while (arrayEnumeration.hasMoreElements()) {
                FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
                super.excludedFields.put(fieldInfo, fieldInfo.getProgramClass());
            }

            ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

            while (arrayEnumeration1.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
                this.excludedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }
    }

    public final void applyAccessStatements() throws ZkmException, IOException {
        int ba;
        if (super.exclusionStatements == null) {
            ba = 0;
        } else {
            ba = super.exclusionStatements.size();
        }

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
            super.logWriter.println("\taccessedByReflection parameters:");

            for (int i = vector.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t" + vector.elementAt(i));
            }
        }

        for (int i = vector.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) vector.elementAt(i);
            if (!aSTRenameFilterParameter2.isClassSpecifier() && !aSTRenameFilterParameter2.isMethodSpecifier() && !aSTRenameFilterParameter2.isFieldSpecifier()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter2
                                        + "' in 'accessedByReflection' statement is not a class, field or method exclusion parameter. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter2.isClassSpecifier() && aSTRenameFilterParameter2.hasLinkClassName()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter2
                                        + "' in 'accessedByReflection' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter2.isMethodSpecifier() && aSTRenameFilterParameter2.hasLinkMethodSignature()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter2
                                        + "' in 'accessedByReflection' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter2.isMethodSpecifier() && aSTRenameFilterParameter2.hasPlusSignatureClasses()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter2
                                        + "' in 'accessedByReflection' cannot use the '"
                                        + "+signatureClasses"
                                        + "' keyword. It will be ignored.",
                                true
                        );
            } else {
                aSTRenameFilterParameter2.applyReflectionExclusion(this);
            }
        }

        int be;
        if (super.unexclusionStatements == null) {
            be = 0;
        } else {
            be = super.unexclusionStatements.size();
        }

        HashMap hashMap1 = ZkmUtils.createHashMap();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < be; i++) {
            ParameterListStatement parameterListStatement1 = (ParameterListStatement) super.unexclusionStatements.get(i);
            Enumeration enumeration1 = parameterListStatement1.getRenameFilterParameters();

            while (enumeration1.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) enumeration1.nextElement();
                if (!hashMap1.containsKey(aSTRenameFilterParameter1)) {
                    hashMap1.put(aSTRenameFilterParameter1, aSTRenameFilterParameter1);
                    arrayList.add(aSTRenameFilterParameter1);
                }
            }
        }

        if (super.scriptEnvironment.isVerbose() && arrayList.size() > 0) {
            super.logWriter.println("\taccessedByReflectionExclude parameters:");

            for (int i = arrayList.size() - 1; i >= 0; i += -1) {
                super.logWriter.println("\t\t\"" + arrayList.get(i) + "\"");
            }
        }

        for (int i = arrayList.size() - 1; i >= 0; i += -1) {
            ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) arrayList.get(i);
            if (!aSTRenameFilterParameter3.isClassSpecifier() && !aSTRenameFilterParameter3.isMethodSpecifier() && !aSTRenameFilterParameter3.isFieldSpecifier()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'accessedByReflectionExclude' statement is not a class, field or method exclusion parameter. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter3.isClassSpecifier() && aSTRenameFilterParameter3.hasLinkClassName()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'accessedByReflectionExclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasLinkMethodSignature()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'accessedByReflectionExclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasPlusSignatureClasses()) {
                super.scriptEnvironment
                        .logWarning(
                                "Auto Reflection Handling : Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'accessedByReflectionExclude' cannot use the '"
                                        + "+signatureClasses"
                                        + "' keyword. It will be ignored.",
                                true
                        );
            } else {
                aSTRenameFilterParameter3.applyReflectionUnexclusion(this);
            }
        }
    }

    public final void markMethodNotAccessed(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.includedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                super.logWriter
                        .println(
                                "\tMatched as NOT accessed by Reflection method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass2)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        Object object = super.includedClasses.remove(programClass1);
        if (object != null) {
            super.excludedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter.println("\tMatched as accessed by Reflection class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }

        return object != null;
    }

    public final void markMethodAccessed(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                super.logWriter
                        .println(
                                "\tMatched as accessed by Reflection method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass2)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public final void markFieldNotAccessed(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = fieldInfo.getProgramClass();
        ProgramClass programClass2 = (ProgramClass) super.excludedFields.remove(fieldInfo);
        if (programClass2 != null) {
            super.includedFields.put(fieldInfo, programClass2);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println(
                                "\tMatched as NOT accessed by Reflection field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass1)
                                        + "\" because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }
}
