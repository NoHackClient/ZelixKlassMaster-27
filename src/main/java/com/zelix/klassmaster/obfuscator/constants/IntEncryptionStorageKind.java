package com.zelix.klassmaster.obfuscator.constants;

public enum IntEncryptionStorageKind {
    LOCAL_ARRAY_AND_INDEX,
    FIELD_ARRAY_AND_INDEX,
    LOOKUP_METHOD_AND_INDEX,
    LOOKUP_METHOD_AND_INDEX_DES,
    INDY_ENTRY_AND_INDEX;

    public static final IntEncryptionStorageKind[] ALL_KINDS = new IntEncryptionStorageKind[]{
            LOCAL_ARRAY_AND_INDEX, FIELD_ARRAY_AND_INDEX, LOOKUP_METHOD_AND_INDEX, LOOKUP_METHOD_AND_INDEX_DES, INDY_ENTRY_AND_INDEX
    };

    public static IntEncryptionStorageKind[] getAllKinds() {
        return ALL_KINDS.clone();
    }
}
