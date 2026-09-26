package com.zelix.klassmaster.obfuscator.constants;

public enum LongEncryptionStorageKind {
    LOCAL_ARRAY_AND_INDEX,
    FIELD_ARRAY_AND_INDEX,
    LOOKUP_METHOD_AND_INDEX,
    LOOKUP_METHOD_AND_INDEX_DES,
    INDY_ENTRY_AND_INDEX;

    public static final LongEncryptionStorageKind[] ALL_VALUES = new LongEncryptionStorageKind[]{
            LOCAL_ARRAY_AND_INDEX, FIELD_ARRAY_AND_INDEX, LOOKUP_METHOD_AND_INDEX, LOOKUP_METHOD_AND_INDEX_DES, INDY_ENTRY_AND_INDEX
    };

    public static LongEncryptionStorageKind[] allValues() {
        return ALL_VALUES.clone();
    }
}
