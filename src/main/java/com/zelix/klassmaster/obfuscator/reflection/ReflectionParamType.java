package com.zelix.klassmaster.obfuscator.reflection;

public enum ReflectionParamType {
    CLASS_NAME_PARAM_TYPE,
    CLASS_OR_PROPERTIES_NAME_PARAM_TYPE,
    PACKAGE_NAME_PARAM_TYPE,
    FIELD_NAME_PARAM_TYPE,
    METHOD_NAME_PARAM_TYPE,
    CLASS_PARAM_TYPE,
    METHOD_TYPE_PARAM_TYPE,
    OBJECT_PARAM_TYPE;

    public static final ReflectionParamType[] ALL_VALUES = new ReflectionParamType[]{
            CLASS_NAME_PARAM_TYPE,
            CLASS_OR_PROPERTIES_NAME_PARAM_TYPE,
            PACKAGE_NAME_PARAM_TYPE,
            FIELD_NAME_PARAM_TYPE,
            METHOD_NAME_PARAM_TYPE,
            CLASS_PARAM_TYPE,
            METHOD_TYPE_PARAM_TYPE,
            OBJECT_PARAM_TYPE
    };

    public static ReflectionParamType[] allValues() {
        return ALL_VALUES.clone();
    }
}
