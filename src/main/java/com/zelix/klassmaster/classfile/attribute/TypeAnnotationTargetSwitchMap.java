package com.zelix.klassmaster.classfile.attribute;

public class TypeAnnotationTargetSwitchMap {
    public static final int[] TARGET_TYPE_SWITCH = new int[TypeAnnotationTargetType.getValues().length];

    static {
        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.CLASS_TYPE_PARAMETER.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError21) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_TYPE_PARAMETER.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError20) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.CLASS_EXTENDS.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError19) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.CLASS_TYPE_PARAMETER_BOUND.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError18) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_TYPE_PARAMETER_BOUND.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError17) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.FIELD.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError16) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_RETURN.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError15) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_RECEIVER.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError14) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_FORMAL_PARAMETER.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError13) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.THROWS.ordinal()] = 10;
        } catch (NoSuchFieldError noSuchFieldError12) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.LOCAL_VARIABLE.ordinal()] = 11;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.RESOURCE_VARIABLE.ordinal()] = 12;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.EXCEPTION_PARAMETER.ordinal()] = 13;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.INSTANCEOF.ordinal()] = 14;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.NEW.ordinal()] = 15;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_REFERENCE_0.ordinal()] = 16;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_REFERENCE_1.ordinal()] = 17;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.CAST.ordinal()] = 18;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT.ordinal()] = 19;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_INVOCATION_TYPE_ARGUMENT.ordinal()] = 20;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT.ordinal()] = 21;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TARGET_TYPE_SWITCH[TypeAnnotationTargetType.METHOD_REFERENCE_TYPE_ARGUMENT.ordinal()] = 22;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private TypeAnnotationTargetSwitchMap() {
    }
}
