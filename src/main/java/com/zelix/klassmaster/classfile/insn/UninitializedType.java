package com.zelix.klassmaster.classfile.insn;

public class UninitializedType extends UninitializedThisValueType {
    public TypeInstruction newInstruction;

    public TypeInstruction getNewInstruction() {
        return this.newInstruction;
    }

    public UninitializedType(String string, TypeInstruction typeInstruction) {
        super(string);
        this.newInstruction = typeInstruction;
    }

    @Override
    public boolean isUninitializedThis() {
        return false;
    }
}
