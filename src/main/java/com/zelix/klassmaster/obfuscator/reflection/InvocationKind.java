package com.zelix.klassmaster.obfuscator.reflection;

public enum InvocationKind {
    CONSTRUCTOR(1),
    INSTANCE(0),
    STATIC(-1);

    public final int argumentOffset;

    InvocationKind(int argumentOffset) {
        this.argumentOffset = argumentOffset;
    }

    public int getArgumentOffset() {
        return this.argumentOffset;
    }
}
