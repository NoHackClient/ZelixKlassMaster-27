package com.zelix.klassmaster.classfile.insn;

public class LocalVarAccessKindSwitchMap {
    public static final int[] ACCESS_KIND_SWITCH_MAP = new int[LocalVariableAccessKind.allValues().length];

    static {
        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.INT_LOAD.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.LONG_LOAD.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.FLOAT_LOAD.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.DOUBLE_LOAD.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.OBJECT_LOAD.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.INT_STORE.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.FLOAT_STORE.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.OBJECT_STORE.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.LONG_STORE.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.DOUBLE_STORE.ordinal()] = 10;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.INT_INC.ordinal()] = 11;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            ACCESS_KIND_SWITCH_MAP[LocalVariableAccessKind.ADDRESS.ordinal()] = 12;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private LocalVarAccessKindSwitchMap() {
    }
}
