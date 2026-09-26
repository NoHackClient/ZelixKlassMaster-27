package com.zelix.klassmaster.obfuscator.constants;

import com.zelix.klassmaster.obfuscator.string.StringEncryptionTechnique;

public class LookupStrategySwitchMap {
    public static final int[] LONG_STORAGE_KIND_SWITCH = new int[LongEncryptionStorageKind.allValues().length];
    public static final int[] INT_STORAGE_KIND_SWITCH;
    public static final int[] STRING_TECHNIQUE_SWITCH;

    static {
        try {
            LONG_STORAGE_KIND_SWITCH[LongEncryptionStorageKind.LOCAL_ARRAY_AND_INDEX.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError16) {
        }

        try {
            LONG_STORAGE_KIND_SWITCH[LongEncryptionStorageKind.FIELD_ARRAY_AND_INDEX.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError15) {
        }

        try {
            LONG_STORAGE_KIND_SWITCH[LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError14) {
        }

        try {
            LONG_STORAGE_KIND_SWITCH[LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError13) {
        }

        try {
            LONG_STORAGE_KIND_SWITCH[LongEncryptionStorageKind.INDY_ENTRY_AND_INDEX.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError12) {
        }

        INT_STORAGE_KIND_SWITCH = new int[IntEncryptionStorageKind.getAllKinds().length];

        try {
            INT_STORAGE_KIND_SWITCH[IntEncryptionStorageKind.LOCAL_ARRAY_AND_INDEX.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            INT_STORAGE_KIND_SWITCH[IntEncryptionStorageKind.FIELD_ARRAY_AND_INDEX.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            INT_STORAGE_KIND_SWITCH[IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            INT_STORAGE_KIND_SWITCH[IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            INT_STORAGE_KIND_SWITCH[IntEncryptionStorageKind.INDY_ENTRY_AND_INDEX.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        STRING_TECHNIQUE_SWITCH = new int[StringEncryptionTechnique.allValues().length];

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.LOCAL_ARRAY_AND_INDEX.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.FIELD_ARRAY_AND_INDEX.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.SEPARATE_METHODS.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.LABEL_JUMP.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX_DES.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            STRING_TECHNIQUE_SWITCH[StringEncryptionTechnique.INDY_ENTRY_AND_INDEX.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private LookupStrategySwitchMap() {
    }
}
