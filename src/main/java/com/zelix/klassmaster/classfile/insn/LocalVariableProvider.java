package com.zelix.klassmaster.classfile.insn;

public interface LocalVariableProvider {
    LocalVariableSlot lookupSlot(int ba, LocalVariableAccessKind localVariableAccessKind, int bb);
}
