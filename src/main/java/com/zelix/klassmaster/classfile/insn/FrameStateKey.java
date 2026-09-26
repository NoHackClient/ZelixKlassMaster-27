package com.zelix.klassmaster.classfile.insn;

public abstract class FrameStateKey {
    public String keyString;
    public int stackSlotCount;
    public boolean hasUninitializedThis;

    @Override
    public int hashCode() {
        return this.keyString.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        boolean bl1 = object instanceof FrameStateKey;
        if (strictMergeEnabled) {
            if (bl1) {
                return this.keyString.equals(((FrameStateKey) object).keyString);
            }

            bl1 = false;
        }

        return bl1;
    }

    public boolean hasUninitializedThisType() {
        return this.hasUninitializedThis;
    }
}
