package com.zelix.klassmaster.classfile.insn;

public enum LocalVariableAccessKind {
    INT_LOAD,
    INT_STORE,
    INT_INC,
    LONG_LOAD(true),
    LONG_STORE(true),
    FLOAT_LOAD,
    FLOAT_STORE,
    DOUBLE_LOAD(true),
    DOUBLE_STORE(true),
    OBJECT_LOAD,
    OBJECT_STORE,
    ADDRESS;

    public static final LocalVariableAccessKind[] VALUES = new LocalVariableAccessKind[]{
            INT_LOAD, INT_STORE, INT_INC, LONG_LOAD, LONG_STORE, FLOAT_LOAD, FLOAT_STORE, DOUBLE_LOAD, DOUBLE_STORE, OBJECT_LOAD, OBJECT_STORE, ADDRESS
    };
    private final boolean wide;

    public static LocalVariableAccessKind[] allValues() {
        return VALUES.clone();
    }

    LocalVariableAccessKind() {
        this(false);
    }

    LocalVariableAccessKind(boolean wide) {
        this.wide = wide;
    }

    public boolean isWide() {
        return this.wide;
    }
}
