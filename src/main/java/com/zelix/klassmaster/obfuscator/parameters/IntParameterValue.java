package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;

public class IntParameterValue extends KeyParameterValue {
    private final int intValue;

    public int getIntValue() {
        return this.intValue;
    }

    @Override
    public boolean isLongKey() {
        return false;
    }

    public IntParameterValue(LocalVariableSlot localVariableSlot1, int intValue) {
        super(localVariableSlot1);
        this.intValue = intValue;
    }
}
