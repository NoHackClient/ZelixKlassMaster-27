package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ExclusionHandlerBase;
import com.zelix.klassmaster.obfuscator.exclude.RenameFilterParamComparator;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ParameterListStatement;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class FlowObfuscationExclusions extends ExclusionHandlerBase {
    public ChangeLogMapping changeLogMapping;

    public final void unexcludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.includedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                super.logWriter
                        .println(
                                "\tUNEXCLUDING from flow obfuscation method \""
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

    public final void excludeMethod(MethodInfo methodInfo1, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) this.includedMethods.remove(methodInfo1);
        if (programClass1 != null) {
            this.excludedMethods.put(methodInfo1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                ProgramClass programClass2 = methodInfo1.getOwnerProgramClass();
                String string1 = AbstractExclusionSpec.formatMethodWithModifiers(methodInfo1, this)
                        + "\" in class \""
                        + this.describeClass(programClass2)
                        + "\" because of \""
                        + string
                        + "\"";
                super.logWriter.println("\tExcluding from flow obfuscation method \"" + string1);
                if (HiddenOptionFlags.FLOW_EXCLUDES_EXCEPTION_OBFUSCATION) {
                    super.logWriter.println("\tExcluding from exception obfuscation method \"" + string1);
                }
            }
        }
    }

    public final void initializeCandidateMaps(Enumeration enumeration, int ba) {
        super.includedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        super.excludedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        this.includedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));
        this.excludedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba * 5));

        while (enumeration.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration.nextElement();
            super.includedClasses.put(programClass1, programClass1);
            ArrayEnumeration arrayEnumeration = programClass1.enumerateMethods();

            while (arrayEnumeration.hasMoreElements()) {
                MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration.nextElement();
                this.includedMethods.put(methodInfo1, methodInfo1.getOwnerProgramClass());
            }
        }
    }

    public FlowObfuscationExclusions(
            ClassRepository classRepository1, List list1, List list2, ChangeLogMapping changeLogMapping1, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        super(classRepository1, list1, list2, scriptEnvironment1);
        this.changeLogMapping = changeLogMapping1;
        if (classRepository1.hasProgramClasses()) {
            this.initializeCandidateMaps(classRepository1.enumerateProgramClasses(), classRepository1.getClassCount());
            this.processExclusionParameters();
        }
    }

    public void excludeFromPredicateFields(ProgramClass programClass1, String string) throws ZkmException, IOException {
        if (super.includedClasses.containsKey(programClass1)) {
            if (this.changeLogMapping != null && this.changeLogMapping.hasFlowData(programClass1.getClassName())) {
                if (super.logWriter != null) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Could NOT exclude class \""
                                            + this.describeClass(programClass1)
                                            + "\" from getting flow obfuscation fields because change log \""
                                            + this.changeLogMapping.getChangeLogName()
                                            + "\" specifies that it does : \""
                                            + string
                                            + "\"."
                            );
                }

                return;
            }

            ProgramClass programClass2 = (ProgramClass) super.includedClasses.remove(programClass1);
            super.excludedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println("\tExcluding from getting flow obfuscation fields class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }
    }

    @Override
    public final void unexcludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        if (super.excludedClasses.remove(programClass1) != null) {
            super.includedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose()) {
                super.logWriter.println("\tUNEXCLUDING from flow obfuscation class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }

        ArrayEnumeration arrayEnumeration = programClass1.enumerateMethods();

        while (arrayEnumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration.nextElement();
            ProgramClass programClass2 = (ProgramClass) this.excludedMethods.remove(methodInfo1);
            if (programClass2 != null) {
                this.includedMethods.put(methodInfo1, programClass2);
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
            super.logWriter.println("\tObfuscate Flow Exclusion parameters:");
            Iterator iterator = vector.iterator();

            while (iterator.hasNext()) {
                ASTRenameFilterParameter aSTRenameFilterParameter2 = (ASTRenameFilterParameter) iterator.next();
                super.logWriter.println("\t\t\"" + aSTRenameFilterParameter2 + "\"");
            }
        }

        Iterator iterator1 = vector.iterator();

        while (iterator1.hasNext()) {
            ASTRenameFilterParameter aSTRenameFilterParameter3 = (ASTRenameFilterParameter) iterator1.next();
            if (aSTRenameFilterParameter3.isMethodSpecifier()
                    || aSTRenameFilterParameter3.isClassSpecifier()
                    || aSTRenameFilterParameter3.isFieldSpecifier() && aSTRenameFilterParameter3.isAnyFieldWithoutType()) {
                if (aSTRenameFilterParameter3.isClassSpecifier() && aSTRenameFilterParameter3.hasLinkClassName()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '" + aSTRenameFilterParameter3 + "' in 'obfuscateFlowExclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                    true
                            );
                } else if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasLinkMethodSignature()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '" + aSTRenameFilterParameter3 + "' in 'obfuscateFlowExclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                    true
                            );
                } else if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasClassCaretTag()) {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '" + aSTRenameFilterParameter3 + "' in 'obfuscateFlowExclude' statement cannot use the '^' syntax. It will be ignored.", true
                            );
                } else {
                    if (aSTRenameFilterParameter3.isMethodSpecifier() && aSTRenameFilterParameter3.hasPlusSignatureClasses()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '"
                                                + aSTRenameFilterParameter3
                                                + "' in 'obfuscateFlowExclude' cannot use the '"
                                                + "+signatureClasses"
                                                + "' keyword. It will be ignored.",
                                        true
                                );
                    } else if (aSTRenameFilterParameter3.isClassSpecifier() && aSTRenameFilterParameter3.isClassPlusSpec()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '" + aSTRenameFilterParameter3 + "' in 'obfuscateFlowExclude' cannot use the '" + "+" + "' tag. The tag will be ignored.",
                                        true
                                );
                    }

                    aSTRenameFilterParameter3.applyFlowExclusion(this);
                }
            } else {
                super.scriptEnvironment
                        .logWarning(
                                "Parameter '"
                                        + aSTRenameFilterParameter3
                                        + "' in 'obfuscateFlowExclude' statement is not a class or method exclusion parameter. It will be ignored.",
                                true
                        );
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
                            "An 'obfuscateFlowUnexclude' statement can only operate on the results of an 'obfuscateFlowExclude' statement. No 'obfuscateFlowExclude' statement is in effect so the 'obfuscateFlowUnexclude' statement will be ignored.",
                            true
                    );
        } else {
            Collections.sort(vector1, new RenameFilterParamComparator());
            if (super.scriptEnvironment.isVerbose() && vector1.size() > 0) {
                super.logWriter.println("\tObfuscate Flow Unexclusion parameters:");
                Iterator iterator2 = vector1.iterator();

                while (iterator2.hasNext()) {
                    ASTRenameFilterParameter aSTRenameFilterParameter4 = (ASTRenameFilterParameter) iterator2.next();
                    super.logWriter.println("\t\t\"" + aSTRenameFilterParameter4 + "\"");
                }
            }

            Iterator iterator3 = vector1.iterator();

            while (iterator3.hasNext()) {
                ASTRenameFilterParameter aSTRenameFilterParameter5 = (ASTRenameFilterParameter) iterator3.next();
                if (aSTRenameFilterParameter5.isMethodSpecifier()
                        || aSTRenameFilterParameter5.isClassSpecifier()
                        || aSTRenameFilterParameter5.isFieldSpecifier() && aSTRenameFilterParameter5.isAnyFieldWithoutType()) {
                    if (aSTRenameFilterParameter5.isClassSpecifier() && aSTRenameFilterParameter5.hasLinkClassName()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '"
                                                + aSTRenameFilterParameter5
                                                + "' in 'obfuscateFlowUnexclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                        true
                                );
                    } else if (aSTRenameFilterParameter5.isMethodSpecifier() && aSTRenameFilterParameter5.hasLinkMethodSignature()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '"
                                                + aSTRenameFilterParameter5
                                                + "' in 'obfuscateFlowUnexclude' statement cannot use the '<link>' syntax. It will be ignored.",
                                        true
                                );
                    } else if (aSTRenameFilterParameter5.isMethodSpecifier() && aSTRenameFilterParameter5.hasClassCaretTag()) {
                        super.scriptEnvironment
                                .logWarning(
                                        "Parameter '" + aSTRenameFilterParameter5 + "' in 'obfuscateFlowUnexclude' statement cannot use the '^' syntax. It will be ignored.",
                                        true
                                );
                    } else {
                        if (aSTRenameFilterParameter5.isMethodSpecifier() && aSTRenameFilterParameter5.hasPlusSignatureClasses()) {
                            super.scriptEnvironment
                                    .logWarning(
                                            "Parameter '"
                                                    + aSTRenameFilterParameter5
                                                    + "' in 'obfuscateFlowUnexclude' cannot use the '"
                                                    + "+signatureClasses"
                                                    + "' keyword. It will be ignored.",
                                            true
                                    );
                        } else if (aSTRenameFilterParameter5.isClassSpecifier() && aSTRenameFilterParameter5.isClassPlusSpec()) {
                            super.scriptEnvironment
                                    .logWarning(
                                            "Parameter '"
                                                    + aSTRenameFilterParameter5
                                                    + "' in 'obfuscateFlowUnexclude' cannot use the '"
                                                    + "+"
                                                    + "' tag. The tag will be ignored.",
                                            true
                                    );
                        }

                        aSTRenameFilterParameter5.applyFlowUnexclusion(this);
                    }
                } else {
                    super.scriptEnvironment
                            .logWarning(
                                    "Parameter '"
                                            + aSTRenameFilterParameter5
                                            + "' in 'obfuscateFlowUnexclude' statement is not a class or method exclusion parameter. It will be ignored.",
                                    true
                            );
                }
            }
        }
    }

    public void unexcludeFromPredicateFields(ProgramClass programClass1, String string) throws ZkmException, IOException {
        if (super.excludedClasses.containsKey(programClass1)) {
            ProgramClass programClass2 = (ProgramClass) super.excludedClasses.remove(programClass1);
            super.includedClasses.put(programClass1, programClass1);
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                super.logWriter
                        .println("\tUNEXCLUDING from getting flow obfuscation fields class \"" + this.describeClass(programClass1) + "\" because of \"" + string + "\"");
            }
        }
    }

    public final boolean excludeClassInternal(ProgramClass programClass1, String string, boolean bl) throws ZkmException, IOException {
        Object object = super.includedClasses.remove(programClass1);
        if (object != null) {
            super.excludedClasses.put(programClass1, programClass1);
            label34:
            if (super.scriptEnvironment.isVerbose() && super.logWriter != null) {
                String string1 = this.describeClass(programClass1) + "\" because of \"" + string + "\"";
                super.logWriter.println("\tExcluding from flow obfuscation class \"" + string1);
                PrintWriter printWriter;
                if (!bl) {
                    if (!HiddenOptionFlags.FLOW_EXCLUDES_EXCEPTION_OBFUSCATION) {
                        break label34;
                    }

                    printWriter = super.logWriter;
                } else {
                    printWriter = super.logWriter;
                }

                printWriter.println("\tExcluding from exception obfuscation class \"" + string1);
            }
        }

        ArrayEnumeration arrayEnumeration = programClass1.enumerateMethods();

        while (arrayEnumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration.nextElement();
            ProgramClass programClass2 = (ProgramClass) this.includedMethods.remove(methodInfo1);
            if (programClass2 != null) {
                this.excludedMethods.put(methodInfo1, programClass2);
            }
        }

        return object != null;
    }

    @Override
    public final boolean excludeClass(ProgramClass programClass1, String string) throws ZkmException, IOException {
        return this.excludeClassInternal(programClass1, string, false);
    }

    public boolean isExcludedFromPredicateFields(Object object) {
        return super.excludedClasses.containsKey(object);
    }
}
