package com.zelix.klassmaster.obfuscator.constants;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ExclusionHandlerBase;
import com.zelix.klassmaster.obfuscator.exclude.RenameFilterParamComparator;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.EmptyEnumeration;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class LongEncryptionExclusionHandler extends ExclusionHandlerBase {
    public ListMultimap excludedFieldsByClass = new ListMultimap();
    public ListMultimap excludedMethodsByClass = new ListMultimap();

    public boolean hasExcludedMethods(Object object) {
        return this.excludedMethodsByClass.containsKey(object);
    }

    public Enumeration getExcludedFieldsOf(Object object) {
        List list1 = this.excludedFieldsByClass.getValues(object);
        return list1 != null ? Collections.enumeration(list1) : new EmptyEnumeration();
    }

    public LongEncryptionExclusionHandler(ClassRepository classRepository1, List list1, List list2, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        if (classRepository1.hasProgramClasses()) {
            this.initializeCandidateMaps(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            this.processExclusionParameters();
        }
    }

    public boolean hasExcludedFields(Object object) {
        return this.excludedFieldsByClass.containsKey(object);
    }

    @Override
    public final void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        Object object = super.excludedClasses.remove(programClass1);
        ListMultimap listMultimap;
        if (object != null) {
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println("\tUNEXCLUDING from Long Constant Encryption class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }

            super.includedClasses.put(programClass1, programClass1);
            listMultimap = this.excludedFieldsByClass;
        } else {
            listMultimap = this.excludedFieldsByClass;
        }

        List list1 = listMultimap.removeKey(programClass1);
        if (object == null && list1 != null && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
            super.logWriter
                    .println(
                            "\tUNEXCLUDING from Long Constant Encryption initialization values of all fields of class \""
                                    + this.describeClass(programClass1)
                                    + "\" because of \""
                                    + string
                                    + "\""
                    );
        }

        ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

        while (arrayEnumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
            if (fieldInfo.isLongType()) {
                Object object1 = super.excludedFields.remove(fieldInfo);
                if (object1 != null) {
                    super.includedFields.put(fieldInfo, programClass1);
                }
            }
        }

        List list2 = this.excludedMethodsByClass.removeKey(programClass1);
        if (object == null && list2 != null && super.scriptEnvironment.isVerbose() && super.logWriter != null) {
            super.logWriter
                    .println(
                            "\tUNEXCLUDING from Long Constant Encryption all long constants referenced by methods of class \""
                                    + this.describeClass(programClass1)
                                    + "\" because of \""
                                    + string
                                    + "\""
                    );
        }

        ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

        while (arrayEnumeration1.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
            if (this.excludedMethods.remove(methodInfo1) != null) {
                this.includedMethods.put(methodInfo1, programClass1);
            }
        }
    }

    public Enumeration getExcludedMethodsOf(Object object) {
        List list1 = this.excludedMethodsByClass.getValues(object);
        return list1 != null ? Collections.enumeration(list1) : new EmptyEnumeration();
    }

    public final void unexcludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        if (super.excludedClasses.containsKey(programClass1)) {
            super.scriptEnvironment
                    .logWarning(
                            "Cannot UNexclude Long constants in method \""
                                    + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                    + "\" in class \""
                                    + this.describeClass(programClass1)
                                    + "\" because class is excluded from all Long Constant Encryption."
                    );
        } else {
            ProgramClass programClass2 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
            if (programClass2 != null) {
                this.includedMethods.put(methodInfo1, programClass2);
                this.excludedMethodsByClass.removeValue(methodInfo1.getOwnerProgramClass(), methodInfo1);
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    super.logWriter
                            .println(
                                    "\tUNEXCLUDING all Long constants in method \""
                                            + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                            + "\" in class \""
                                            + this.describeClass(programClass1)
                                            + "\" from Long Constant Encryption because of \""
                                            + string
                                            + "\""
                            );
                }
            }
        }
    }

    public final void excludeField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        if (fieldInfo.isLongType()) {
            ProgramClass programClass1 = (ProgramClass) super.includedFields.remove(fieldInfo);
            if (programClass1 != null) {
                super.excludedFields.put(fieldInfo, programClass1);
                this.excludedFieldsByClass.addValue(programClass1, fieldInfo);
                if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                    ProgramClass programClass2 = fieldInfo.getProgramClass();
                    super.logWriter
                            .println(
                                    "\tExcluding initialization value of field \""
                                            + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                            + "\" in class \""
                                            + this.describeClass(programClass2)
                                            + "\" from Long Constant Encryption because of \""
                                            + string
                                            + "\""
                            );
                }
            }
        }
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        Object object = super.includedClasses.remove(programClass1);
        if (object != null) {
            super.excludedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println("\tExcluding from Long Constant Encryption class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }

        ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();

        while (arrayEnumeration.hasMoreElements()) {
            FieldInfo fieldInfo = (FieldInfo) arrayEnumeration.nextElement();
            if (fieldInfo.isLongType()) {
                this.excludedFieldsByClass.addValue(programClass1, fieldInfo);
                Object object1 = super.includedFields.remove(fieldInfo);
                if (object1 != null) {
                    super.excludedFields.put(fieldInfo, programClass1);
                }
            }
        }

        ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();

        while (arrayEnumeration1.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
            this.excludedMethodsByClass.addValue(programClass1, methodInfo1);
            if (this.includedMethods.remove(methodInfo1) != null) {
                this.excludedMethods.put(methodInfo1, programClass1);
            }
        }

        return object != null;
    }

    public final void excludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
            this.excludedMethodsByClass.addValue(programClass1, methodInfo1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                super.logWriter
                        .println(
                                "\tExcluding all Long constants in method \""
                                        + AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass2)
                                        + "\" from Long Constant Encryption because of \""
                                        + string
                                        + "\""
                        );
            }
        }
    }

    public final void unexcludeField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        if (fieldInfo.isLongType()) {
            ProgramClass programClass1 = fieldInfo.getProgramClass();
            if (super.excludedClasses.containsKey(programClass1)) {
                super.scriptEnvironment
                        .logWarning(
                                "Cannot UNexclude initialization value of field \""
                                        + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                        + "\" in class \""
                                        + this.describeClass(programClass1)
                                        + "\" because class is excluded from all Long encryption."
                        );
            } else {
                ProgramClass programClass2 = (ProgramClass) super.excludedFields.remove(fieldInfo);
                if (programClass2 != null) {
                    super.includedFields.put(fieldInfo, programClass2);
                    this.excludedFieldsByClass.removeValue(fieldInfo.getProgramClass(), fieldInfo);
                    if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                        super.logWriter
                                .println(
                                        "\tUNEXCLUDING initialization value of field \""
                                                + AbstractExclusionSpec.formatFieldWithModifiers(fieldInfo, this)
                                                + "\" in class \""
                                                + this.describeClass(programClass1)
                                                + "\" from Long Constant Encryption because of \""
                                                + string
                                                + "\""
                                );
                    }
                }
            }
        }
    }

    public final void processExclusionParameters() throws ZkmException, IOException {
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

        Collections.sort(vector, new RenameFilterParamComparator());
        if (super.scriptEnvironment.isVerbose() && vector.size() > 0) {
            super.logWriter.println("\tLong Constant Encryption Exclusion parameters:");
            Iterator iterator = vector.iterator();

            while (iterator.hasNext()) {
                ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) iterator.next();
                super.logWriter.println("\t\t\"" + aSTRenameFilterParameter2 + "\"");
            }
        }

        Iterator iterator1 = vector.iterator();

        while (iterator1.hasNext()) {
            ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) iterator1.next();
            if (!aSTRenameFilterParameter3.isClassSpecifier() && !aSTRenameFilterParameter3.isFieldSpecifier() && !aSTRenameFilterParameter3.isMethodSpecifier()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'encryptLongConstantsExclude' statement is not a class, field or method exclusion parameter. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter3.isClassSpecifier() && aSTRenameFilterParameter3.hasLinkClassName()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'encryptLongConstantsExclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                true
                        );
            } else if (aSTRenameFilterParameter3.isFieldSpecifier() && aSTRenameFilterParameter3.hasClassCaretTag()) {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '" + aSTRenameFilterParameter3 + "' in 'encryptLongConstantsExclude' statement cannot use the '^' syntax. It will be ignored.",
                                true
                        );
            } else {
                if (aSTRenameFilterParameter3.isClassSpecifier() && aSTRenameFilterParameter3.isClassPlusSpec()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '"
                                            + aSTRenameFilterParameter3
                                            + "' in 'encryptLongConstantsExclude' cannot use the '"
                                            + "+"
                                            + "' tag. The tag will be ignored.",
                                    true
                            );
                }

                aSTRenameFilterParameter3.applyLongEncryptionExclusion(this);
            }
        }

        int bc;
        if (super.unexclusionStatements == null) {
            bc = 0;
        } else {
            bc = super.unexclusionStatements.size();
        }

        HashMap hashMap1 = ZkmUtils.createHashMap();
        Vector vector1 = new Vector();

        for (int i = 0; i < bc; i++) {
            ParameterListStatement parameterListStatement1 = (ParameterListStatement) super.unexclusionStatements.get(i);
            Enumeration enumeration1 = parameterListStatement1.getRenameFilterParameters();

            while (enumeration1.hasMoreElements()) {
                ASTRenameFilterParameter aSTRenameFilterParameter1 = (ASTRenameFilterParameter) enumeration1.nextElement();
                if (!hashMap1.containsKey(aSTRenameFilterParameter1)) {
                    hashMap1.put(aSTRenameFilterParameter1, aSTRenameFilterParameter1);
                    vector1.addElement(aSTRenameFilterParameter1);
                }
            }
        }

        if (vector1.size() > 0 && vector.size() == 0) {
            super.scriptEnvironment
                    .logWarning(
                            "A 'encryptLongConstantsUnexclude' statement can only operate on the results of a 'encryptLongConstantsExclude' statement. No 'encryptLongConstantsExclude' statement is in effect so the 'encryptLongConstantsUnexclude' statement will be ignored.",
                            true
                    );
        } else {
            Collections.sort(vector1, new RenameFilterParamComparator());
            if (super.scriptEnvironment.isVerbose() && vector1.size() > 0) {
                super.logWriter.println("\tLong Constant Encryption Unexclusion parameters:");
                Iterator iterator2 = vector1.iterator();

                while (iterator2.hasNext()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter4 = (ASTRenameFilterParameter) iterator2.next();
                    super.logWriter.println("\t\t\"" + aSTRenameFilterParameter4 + "\"");
                }
            }

            Iterator iterator3 = vector1.iterator();

            while (iterator3.hasNext()) {
                ASTRenameFilterParameter aSTRenameFilterParameter5 = (ASTRenameFilterParameter) iterator3.next();
                if (!aSTRenameFilterParameter5.isClassSpecifier()
                        && !aSTRenameFilterParameter5.isFieldSpecifier()
                        && !aSTRenameFilterParameter5.isMethodSpecifier()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '"
                                            + aSTRenameFilterParameter5
                                            + "' in 'encryptLongConstantsUnexclude' statement is not a class, field or method exclusion parameter. It will be ignored.",
                                    true
                            );
                } else if (aSTRenameFilterParameter5.isClassSpecifier() && aSTRenameFilterParameter5.hasLinkClassName()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '"
                                            + aSTRenameFilterParameter5
                                            + "' in 'encryptLongConstantsUnexclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                    true
                            );
                } else if (aSTRenameFilterParameter5.isFieldSpecifier() && aSTRenameFilterParameter5.hasClassCaretTag()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '"
                                            + aSTRenameFilterParameter5
                                            + "' in 'encryptLongConstantsUnexclude' statement cannot use the '^' syntax. It will be ignored.",
                                    true
                            );
                } else {
                    if (aSTRenameFilterParameter5.isClassSpecifier() && aSTRenameFilterParameter5.isClassPlusSpec()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '"
                                                + aSTRenameFilterParameter5
                                                + "' in 'encryptLongConstantsUnexclude' cannot use the '"
                                                + "+"
                                                + "' tag. The tag will be ignored.",
                                        true
                                );
                    }

                    aSTRenameFilterParameter5.applyLongEncryptionUnexclusion(this);
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
                this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }
    }
}
