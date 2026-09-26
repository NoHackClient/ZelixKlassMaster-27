package com.zelix.klassmaster.classfile.constpool;

public class ConstantKindSwitchMap {
    public static final int[] TAG_SWITCH = new int[ConstantPoolTag.getAllTags().length];

    static {
        try {
            TAG_SWITCH[ConstantPoolTag.INTEGER.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.FLOAT.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.LONG.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.DOUBLE.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.STRING.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.CLASS.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.METHOD_HANDLE.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.METHOD_TYPE.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TAG_SWITCH[ConstantPoolTag.CONSTANT_DYNAMIC.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ConstantKindSwitchMap() {
    }
}
