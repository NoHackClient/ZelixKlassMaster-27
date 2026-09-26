package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;

public class IndexedLocalSlot {
    private final LocalVariableSlot slot;
    private final int index;

    public IndexedLocalSlot(LocalVariableSlot localVariableSlot1, int index) {
        this.slot = localVariableSlot1;
        this.index = index;
    }

    public LocalVariableSlot getSlot() {
        return this.slot;
    }
}
