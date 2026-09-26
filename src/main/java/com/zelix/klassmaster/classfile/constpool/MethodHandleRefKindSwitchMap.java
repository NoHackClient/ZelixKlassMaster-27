package com.zelix.klassmaster.classfile.constpool;

public class MethodHandleRefKindSwitchMap {
    public static final int[] REF_KIND_SWITCH_TABLE = new int[MethodHandleRefKind.getAllKinds().length];

    static {
        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_GET_FIELD.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_GET_STATIC.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_PUT_FIELD.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_PUT_STATIC.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_INVOKE_VIRTUAL.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_INVOKE_STATIC.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_INVOKE_SPECIAL.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_NEW_INVOKE_SPECIAL.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            REF_KIND_SWITCH_TABLE[MethodHandleRefKind.REF_INVOKE_INTERFACE.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private MethodHandleRefKindSwitchMap() {
    }
}
