package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;

public abstract class KeyParameterValue {
    public final LocalVariableSlot localVariableSlot;

    public KeyParameterValue(LocalVariableSlot localVariableSlot1) {
        this.localVariableSlot = localVariableSlot1;
    }

    public int getSlotIndex() {
        return this.localVariableSlot.getIndex();
    }

    public abstract boolean isLongKey();
}
