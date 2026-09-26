package com.zelix.klassmaster.classfile.constpool;

public class ConstantPoolTagSwitchMap {
    public static final int[] TAG_SWITCH_TABLE = new int[ConstantPoolTag.getAllTags().length];

    static {
        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.CLASS.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError16) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.NAME_AND_TYPE.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError15) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.METHODREF.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError14) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.INTERFACE_METHODREF.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError13) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.FIELDREF.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError12) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.UTF8.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.STRING.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.INTEGER.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.LONG.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.FLOAT.ordinal()] = 10;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.DOUBLE.ordinal()] = 11;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.METHOD_HANDLE.ordinal()] = 12;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.METHOD_TYPE.ordinal()] = 13;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.CONSTANT_DYNAMIC.ordinal()] = 14;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.INVOKE_DYNAMIC.ordinal()] = 15;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.MODULE.ordinal()] = 16;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TAG_SWITCH_TABLE[ConstantPoolTag.PACKAGE.ordinal()] = 17;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ConstantPoolTagSwitchMap() {
    }
}
