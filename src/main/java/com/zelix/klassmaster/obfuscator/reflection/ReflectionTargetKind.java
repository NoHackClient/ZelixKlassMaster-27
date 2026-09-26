package com.zelix.klassmaster.obfuscator.reflection;

public enum ReflectionTargetKind {
    CLASS_TYPE,
    PACKAGE_TYPE,
    METHOD_TYPE,
    FIELD_TYPE,
    OBJECT_TYPE,
    BUNDLE_TYPE,
    OBJECT_NAME_TYPE,
    DEFAULT_CONSTRUCTOR_CALL_TYPE,
    SPECIFIC_CONSTRUCTOR_CALL_TYPE,
    ALL_CONSTRUCTORS_CALL_TYPE,
    METHOD_HANDLE_TYPE,
    METHOD_TYPE_TYPE,
    FUNCTIONAL_INTERFACE_METHOD_TYPE;

    public static final ReflectionTargetKind[] ALL_VALUES = new ReflectionTargetKind[]{
            CLASS_TYPE,
            PACKAGE_TYPE,
            METHOD_TYPE,
            FIELD_TYPE,
            OBJECT_TYPE,
            BUNDLE_TYPE,
            OBJECT_NAME_TYPE,
            DEFAULT_CONSTRUCTOR_CALL_TYPE,
            SPECIFIC_CONSTRUCTOR_CALL_TYPE,
            ALL_CONSTRUCTORS_CALL_TYPE,
            METHOD_HANDLE_TYPE,
            METHOD_TYPE_TYPE,
            FUNCTIONAL_INTERFACE_METHOD_TYPE
    };

    public static ReflectionTargetKind[] allValues() {
        return ALL_VALUES.clone();
    }
}
