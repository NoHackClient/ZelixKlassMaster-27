package com.zelix.klassmaster.classfile.insn;

public interface TrackedValue {
    String getDisplayText();

    boolean isPending();

    boolean isKnown();

    String getStringValue();

    String getCombinedValueKey();

    int getPendingInstructionIndex();

    boolean isConstant();

    String getNormalizedName();
}
