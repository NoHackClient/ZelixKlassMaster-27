package com.zelix.klassmaster.classfile.attribute;

public enum TypePathKind {
    ARRAY(0),
    NESTED(1),
    WILDCARD(2),
    TYPE(3);

    public static final TypePathKind[] VALUES = new TypePathKind[]{ARRAY, NESTED, WILDCARD, TYPE};
    public final int code;

    public static TypePathKind[] getValues() {
        return VALUES.clone();
    }

    public int getCode() {
        return this.code;
    }

    TypePathKind(int code) {
        this.code = code;
    }
}
