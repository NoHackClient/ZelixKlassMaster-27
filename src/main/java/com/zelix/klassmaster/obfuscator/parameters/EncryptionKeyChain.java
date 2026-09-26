package com.zelix.klassmaster.obfuscator.parameters;

public interface EncryptionKeyChain {
    long getCurrentKey();

    boolean isKeyed();

    void appendToChain(Object object);

    long pushKey(long ba);

    long getMixedKey();

    boolean isOrderedBefore(EncryptionKeyChain encryptionKeyChain);
}
