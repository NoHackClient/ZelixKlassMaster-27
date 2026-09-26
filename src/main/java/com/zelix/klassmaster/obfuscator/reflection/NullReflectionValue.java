package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.insn.StackFrameState;
import com.zelix.klassmaster.classfile.insn.TrackedValue;

public class NullReflectionValue implements TrackedValue {
    public int cachedHashCode = this.getClass().getName().hashCode();
    private static final String NULL_DISPLAY = "<null>";

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public String getDisplayText() {
        return NULL_DISPLAY;
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        boolean bl1 = object instanceof NullReflectionValue;
        if (!strictMergeDisabled) {
            if (bl1) {
                return true;
            }

            bl1 = false;
        }

        return bl1;
    }
}
