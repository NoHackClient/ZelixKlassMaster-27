package com.zelix.klassmaster.obfuscator.string;

public enum StringEncryptionTechnique {
    LOCAL_ARRAY_AND_INDEX,
    FIELD_ARRAY_AND_INDEX,
    SEPARATE_METHODS,
    LABEL_JUMP,
    LOOKUP_METHOD_AND_INDEX,
    LOOKUP_METHOD_AND_INDEX_DES,
    INDY_ENTRY_AND_INDEX;

    private static int flowCounter;
    public static final StringEncryptionTechnique[] ALL_VALUES = new StringEncryptionTechnique[]{
            LOCAL_ARRAY_AND_INDEX, FIELD_ARRAY_AND_INDEX, SEPARATE_METHODS, LABEL_JUMP, LOOKUP_METHOD_AND_INDEX, LOOKUP_METHOD_AND_INDEX_DES, INDY_ENTRY_AND_INDEX
    };

    static {
        if (getFlowCounter() != 0) {
            setFlowCounter();
        }
    }

    public static int getFlowCounter() {
        return flowCounter;
    }

    public static StringEncryptionTechnique[] allValues() {
        return ALL_VALUES.clone();
    }

    public static void setFlowCounter() {
        flowCounter = 7;
    }

    public static int getDefaultBatchSize() {
        return 20;
    }
}
