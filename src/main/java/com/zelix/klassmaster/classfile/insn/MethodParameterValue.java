package com.zelix.klassmaster.classfile.insn;

public class MethodParameterValue implements TrackedValue {
    private static final String DISPLAY_PREFIX = "<method parameter ";
    public int parameterIndex = -1;
    public MethodBytecode methodBytecode;

    public MethodBytecode getMethodBytecode() {
        return this.methodBytecode;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        boolean bl1 = object instanceof MethodParameterValue;
        if (!strictMergeDisabled) {
            if (bl1) {
                MethodParameterValue methodParameterValue1 = (MethodParameterValue) object;
                MethodParameterValue methodParameterValue2 = this;
                if (!strictMergeDisabled) {
                    if (this.methodBytecode != methodParameterValue1.methodBytecode) {
                        return false;
                    }

                    methodParameterValue2 = this;
                }

                if (strictMergeDisabled) {
                    return methodParameterValue2.parameterIndex != 0;
                }

                if (methodParameterValue2.parameterIndex == methodParameterValue1.parameterIndex) {
                    return true;
                }

                return false;
            }

            bl1 = false;
        }

        return bl1;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    public MethodParameterValue(int parameterIndex, MethodBytecode methodBytecode1) {
        this.parameterIndex = parameterIndex;
        this.methodBytecode = methodBytecode1;
    }

    @Override
    public int hashCode() {
        return this.methodBytecode.hashCode() ^ this.parameterIndex;
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public String getDisplayText() {
        return DISPLAY_PREFIX + this.parameterIndex + ">";
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public boolean isConstant() {
        return false;
    }
}
