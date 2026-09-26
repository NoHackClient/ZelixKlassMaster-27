package com.zelix.klassmaster.obfuscator.constants;

import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInvokeDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;

public class EncryptedValueLocation {
    private static int buildTag;
    public ResolvedInvokeDynamic indyEntry;
    public ResolvedMethodRef lookupMethod;
    public long decryptionKey;
    public int localVariableIndex = -1;
    public int originalIndex = -1;
    public final IntEncryptionStorageKind storageKind;
    public ResolvedFieldRef arrayField;
    public int index;

    public static int getMagicNumber() {
        return 88;
    }

    public boolean hasLocalVariable() {
        return this.localVariableIndex > -1;
    }

    public void setDecryptionKey(long decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    public EncryptedValueLocation(ResolvedFieldRef resolvedFieldRef, int index) {
        this.storageKind = IntEncryptionStorageKind.FIELD_ARRAY_AND_INDEX;
        this.arrayField = resolvedFieldRef;
        this.index = index;
        this.originalIndex = index;
    }

    public EncryptedValueLocation(ResolvedInvokeDynamic resolvedInvokeDynamic, int index) {
        this.storageKind = IntEncryptionStorageKind.INDY_ENTRY_AND_INDEX;
        this.indyEntry = resolvedInvokeDynamic;
        this.index = index;
        this.originalIndex = index;
    }

    public ResolvedMethodRef getLookupMethod() {
        return this.lookupMethod;
    }

    public EncryptedValueLocation(int localVariableIndex, int index) {
        this.storageKind = IntEncryptionStorageKind.LOCAL_ARRAY_AND_INDEX;
        this.localVariableIndex = localVariableIndex;
        this.index = index;
        this.originalIndex = index;
    }

    public IntEncryptionStorageKind getStorageKind() {
        return this.storageKind;
    }

    public static int getBuildTag() {
        return buildTag;
    }

    public ResolvedInvokeDynamic getIndyEntry() {
        return this.indyEntry;
    }

    public boolean hasArrayField() {
        return this.arrayField != null;
    }

    public boolean hasIndex() {
        return this.index > -1;
    }

    public long getDecryptionKey() {
        return this.decryptionKey;
    }

    public EncryptedValueLocation(ResolvedMethodRef resolvedMethodRef, int index) {
        this.storageKind = IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX;
        this.lookupMethod = resolvedMethodRef;
        this.index = index;
        this.originalIndex = index;
    }

    public ResolvedFieldRef getArrayField() {
        return this.arrayField;
    }

    public EncryptedValueLocation(int index, ResolvedMethodRef resolvedMethodRef) {
        this.storageKind = IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES;
        this.lookupMethod = resolvedMethodRef;
        this.index = index;
        this.originalIndex = index;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLocalVariableIndex() {
        return this.localVariableIndex;
    }

    public static void setBuildTag() {
        buildTag = 125;
    }

    static {
        if (getBuildTag() != 0) {
            setBuildTag();
        }
    }
}
