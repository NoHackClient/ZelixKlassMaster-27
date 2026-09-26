package com.zelix.klassmaster.obfuscator.reflection;

public class ReflectionAnalysisSwitchMap {
    public static final int[] ARGUMENT_KIND_SWITCH = new int[ReflectionArgumentKind.allValues().length];
    public static final int[] PARAM_TYPE_SWITCH;

    static {
        try {
            ARGUMENT_KIND_SWITCH[ReflectionArgumentKind.NORMAL.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            ARGUMENT_KIND_SWITCH[ReflectionArgumentKind.CLASS_REFERENCE.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            ARGUMENT_KIND_SWITCH[ReflectionArgumentKind.METHODTYPE_REFERENCE.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            ARGUMENT_KIND_SWITCH[ReflectionArgumentKind.USER_DEFINED_METHOD_1.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            ARGUMENT_KIND_SWITCH[ReflectionArgumentKind.USER_DEFINED_METHOD_2.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        PARAM_TYPE_SWITCH = new int[ReflectionParamType.allValues().length];

        try {
            PARAM_TYPE_SWITCH[ReflectionParamType.CLASS_PARAM_TYPE.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            PARAM_TYPE_SWITCH[ReflectionParamType.METHOD_TYPE_PARAM_TYPE.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            PARAM_TYPE_SWITCH[ReflectionParamType.OBJECT_PARAM_TYPE.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ReflectionAnalysisSwitchMap() {
    }
}
