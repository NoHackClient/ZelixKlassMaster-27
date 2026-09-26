package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodType;

public class StringConstantValue implements TrackedValue {
    public ResolvedMethodType methodType;
    private static final String NULL_TEXT = "null";

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public int hashCode() {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        ResolvedMethodType resolvedMethodType;
        if (!strictMergeDisabled) {
            if (this.methodType == null) {
                return 0;
            }

            resolvedMethodType = this.methodType;
        } else {
            resolvedMethodType = this.methodType;
        }

        return resolvedMethodType.hashCode();
    }

    @Override
    public boolean isPending() {
        return false;
    }

    public StringConstantValue(ResolvedMethodType resolvedMethodType) {
        if (resolvedMethodType == null) {
            throw new IllegalArgumentException();
        }

        this.methodType = resolvedMethodType;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public String getStringValue() {
        return this.methodType != null ? this.methodType.getDescriptor() : null;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        boolean bl1 = object instanceof StringConstantValue;
        if (!strictMergeDisabled) {
            if (bl1) {
                StringConstantValue stringConstantValue1 = (StringConstantValue) object;
                StringConstantValue stringConstantValue2 = this;
                if (!strictMergeDisabled) {
                    if (this.methodType != null) {
                        ResolvedMethodType resolvedMethodType;
                        if (!strictMergeDisabled) {
                            if (stringConstantValue1.methodType == null) {
                                return false;
                            }

                            resolvedMethodType = this.methodType;
                        } else {
                            resolvedMethodType = stringConstantValue1.methodType;
                        }

                        return resolvedMethodType.equals(stringConstantValue1.methodType);
                    }

                    stringConstantValue2 = stringConstantValue1;
                }

                return stringConstantValue2.methodType == null;
            }

            bl1 = false;
        }

        return bl1;
    }

    @Override
    public String getDisplayText() {
        return this.methodType != null ? this.getNormalizedName() : NULL_TEXT;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
