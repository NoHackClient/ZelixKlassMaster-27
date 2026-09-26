package com.zelix.klassmaster.obfuscator.reflection;

public enum ReflectionArgumentKind {
    NORMAL,
    CLASS_REFERENCE,
    METHODTYPE_REFERENCE,
    USER_DEFINED_METHOD_1,
    USER_DEFINED_METHOD_2,
    OBJECT_TYPE;

    public static final ReflectionArgumentKind[] ALL_VALUES = new ReflectionArgumentKind[]{
            NORMAL, CLASS_REFERENCE, METHODTYPE_REFERENCE, USER_DEFINED_METHOD_1, USER_DEFINED_METHOD_2, OBJECT_TYPE
    };

    public static ReflectionArgumentKind[] allValues() {
        return ALL_VALUES.clone();
    }
}
