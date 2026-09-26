package com.zelix.klassmaster.classfile.attribute;

public class SignatureKindSwitchMap {
    public static final int[] TYPE_PATH_KIND_SWITCH = new int[TypePathKind.getValues().length];

    static {
        try {
            TYPE_PATH_KIND_SWITCH[TypePathKind.ARRAY.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TYPE_PATH_KIND_SWITCH[TypePathKind.NESTED.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TYPE_PATH_KIND_SWITCH[TypePathKind.WILDCARD.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TYPE_PATH_KIND_SWITCH[TypePathKind.TYPE.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private SignatureKindSwitchMap() {
    }
}
