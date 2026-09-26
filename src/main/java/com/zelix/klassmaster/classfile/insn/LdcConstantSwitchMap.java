package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolTag;

public class LdcConstantSwitchMap {
    public static final int[] CONSTANT_TAG_SWITCH_MAP = new int[ConstantPoolTag.getAllTags().length];

    static {
        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.CLASS.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.STRING.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.INTEGER.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.LONG.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.FIELDREF.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.METHODREF.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            CONSTANT_TAG_SWITCH_MAP[ConstantPoolTag.INTERFACE_METHODREF.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private LdcConstantSwitchMap() {
    }
}
