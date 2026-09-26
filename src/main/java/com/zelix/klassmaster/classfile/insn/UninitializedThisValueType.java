package com.zelix.klassmaster.classfile.insn;

public class UninitializedThisValueType extends VerifierType {
    @Override
    public final boolean isInitialized() {
        return false;
    }

    public UninitializedThisValueType(String string) {
        super(string);
    }
}
