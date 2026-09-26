package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;

public class ClassConstantValue implements TrackedValue {
    public ResolvedClassConstant classConstant;
    private static String nullText;
    private static long arrayPrefixChar;

    @Override
    public int hashCode() {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        ResolvedClassConstant resolvedClassConstant;
        if (!strictMergeDisabled) {
            if (this.classConstant == null) {
                return 0;
            }

            resolvedClassConstant = this.classConstant;
        } else {
            resolvedClassConstant = this.classConstant;
        }

        return resolvedClassConstant.hashCode();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    public ClassConstantValue(ResolvedClassConstant resolvedClassConstant) {
        if (resolvedClassConstant == null) {
            throw new IllegalArgumentException();
        }

        this.classConstant = resolvedClassConstant;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public String getStringValue() {
        return this.classConstant != null ? this.classConstant.getClassName() : null;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public String getNormalizedName() {
        String string = this.getStringValue();
        if (string == null) {
            return null;
        }

        int ba = string.lastIndexOf((int) arrayPrefixChar);
        if (ba > -1) {
            string = string.substring(ba + 1);
        }

        if (string.endsWith(";")) {
            string = string.substring(1, string.length() - 1);
        }

        return string;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        boolean bl1 = object instanceof ClassConstantValue;
        if (!strictMergeDisabled) {
            if (bl1) {
                ClassConstantValue classConstantValue1 = (ClassConstantValue) object;
                ClassConstantValue classConstantValue2 = this;
                if (!strictMergeDisabled) {
                    if (this.classConstant != null) {
                        ResolvedClassConstant resolvedClassConstant;
                        if (!strictMergeDisabled) {
                            if (classConstantValue1.classConstant == null) {
                                return false;
                            }

                            resolvedClassConstant = this.classConstant;
                        } else {
                            resolvedClassConstant = classConstantValue1.classConstant;
                        }

                        return resolvedClassConstant.equals(classConstantValue1.classConstant);
                    }

                    classConstantValue2 = classConstantValue1;
                }

                return classConstantValue2.classConstant == null;
            }

            bl1 = false;
        }

        return bl1;
    }

    @Override
    public String getDisplayText() {
        return this.classConstant != null ? this.getNormalizedName() : nullText;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        nullText = "null";
        arrayPrefixChar = 3827090850312093787L;
    }
}
