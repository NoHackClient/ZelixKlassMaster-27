package com.zelix.klassmaster.obfuscator.constants;

public class IntEncryptionModeSwitchMap {
    public static final int[] STORAGE_KIND_SWITCH = new int[IntEncryptionStorageKind.getAllKinds().length];

    static {
        try {
            STORAGE_KIND_SWITCH[IntEncryptionStorageKind.FIELD_ARRAY_AND_INDEX.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            STORAGE_KIND_SWITCH[IntEncryptionStorageKind.LOCAL_ARRAY_AND_INDEX.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            STORAGE_KIND_SWITCH[IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            STORAGE_KIND_SWITCH[IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            STORAGE_KIND_SWITCH[IntEncryptionStorageKind.INDY_ENTRY_AND_INDEX.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private IntEncryptionModeSwitchMap() {
    }
}
