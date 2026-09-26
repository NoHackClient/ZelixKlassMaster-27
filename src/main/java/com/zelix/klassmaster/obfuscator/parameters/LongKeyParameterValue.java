package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;

public class LongKeyParameterValue extends KeyParameterValue {
    private final long keyValue;

    public long getKeyValue() {
        return this.keyValue;
    }

    @Override
    public boolean isLongKey() {
        return true;
    }

    public LongKeyParameterValue(LocalVariableSlot localVariableSlot1, long keyValue) {
        super(localVariableSlot1);
        this.keyValue = keyValue;
    }
}
