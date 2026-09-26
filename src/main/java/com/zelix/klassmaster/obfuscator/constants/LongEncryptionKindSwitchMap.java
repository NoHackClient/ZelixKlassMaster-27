package com.zelix.klassmaster.obfuscator.constants;

public class LongEncryptionKindSwitchMap {
    public static final int[] STORAGE_KIND_SWITCH = new int[LongEncryptionStorageKind.allValues().length];

    static {
        try {
            STORAGE_KIND_SWITCH[LongEncryptionStorageKind.FIELD_ARRAY_AND_INDEX.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            STORAGE_KIND_SWITCH[LongEncryptionStorageKind.LOCAL_ARRAY_AND_INDEX.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            STORAGE_KIND_SWITCH[LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            STORAGE_KIND_SWITCH[LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            STORAGE_KIND_SWITCH[LongEncryptionStorageKind.INDY_ENTRY_AND_INDEX.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private LongEncryptionKindSwitchMap() {
    }
}
