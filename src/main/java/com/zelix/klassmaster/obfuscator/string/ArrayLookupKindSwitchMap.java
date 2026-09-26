package com.zelix.klassmaster.obfuscator.string;

public class ArrayLookupKindSwitchMap {
    public static final int[] TECHNIQUE_SWITCH = new int[StringEncryptionTechnique.allValues().length];

    static {
        try {
            TECHNIQUE_SWITCH[StringEncryptionTechnique.FIELD_ARRAY_AND_INDEX.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            TECHNIQUE_SWITCH[StringEncryptionTechnique.LOCAL_ARRAY_AND_INDEX.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            TECHNIQUE_SWITCH[StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            TECHNIQUE_SWITCH[StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX_DES.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            TECHNIQUE_SWITCH[StringEncryptionTechnique.INDY_ENTRY_AND_INDEX.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private ArrayLookupKindSwitchMap() {
    }
}
