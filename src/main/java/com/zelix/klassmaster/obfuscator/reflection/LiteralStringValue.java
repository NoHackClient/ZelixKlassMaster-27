package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.insn.StackFrameState;
import com.zelix.klassmaster.classfile.insn.TrackedValue;

public class LiteralStringValue implements TrackedValue {
    public final String value;

    public LiteralStringValue(String string) {
        this.value = string;
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
        return this.value;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        boolean bl1 = object instanceof LiteralStringValue;
        if (strictMergeEnabled) {
            if (bl1) {
                LiteralStringValue literalStringValue1 = (LiteralStringValue) object;
                LiteralStringValue literalStringValue2 = this;
                if (strictMergeEnabled) {
                    if (this.value != null) {
                        String string;
                        if (strictMergeEnabled) {
                            if (literalStringValue1.value == null) {
                                return false;
                            }

                            string = this.value;
                        } else {
                            string = literalStringValue1.value;
                        }

                        return string.equals(literalStringValue1.value);
                    }

                    literalStringValue2 = literalStringValue1;
                }

                return literalStringValue2.value == null;
            }

            bl1 = false;
        }

        return bl1;
    }

    @Override
    public int hashCode() {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        String string;
        if (!strictMergeDisabled) {
            if (this.value == null) {
                return 0;
            }

            string = this.value;
        } else {
            string = this.value;
        }

        return string.hashCode();
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public String getDisplayText() {
        return this.value;
    }

    @Override
    public boolean isPending() {
        return false;
    }
}
