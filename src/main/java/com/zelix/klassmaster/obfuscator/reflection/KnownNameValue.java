package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.insn.TrackedValue;

public class KnownNameValue implements TrackedValue {
    public final String name;

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public String getNormalizedName() {
        return this.name;
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public String getStringValue() {
        return this.name;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public String getDisplayText() {
        return this.name;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    public KnownNameValue(String string) {
        this.name = string;
    }
}
