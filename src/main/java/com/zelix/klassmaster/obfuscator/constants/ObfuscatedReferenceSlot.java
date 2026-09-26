package com.zelix.klassmaster.obfuscator.constants;

import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInvokeDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;

public class ObfuscatedReferenceSlot {
    private static String[] sharedStrings;
    public ResolvedFieldRef arrayField;
    public ResolvedInvokeDynamic invokeDynamic;
    public long encryptionKey;
    public int localVariableIndex = -1;
    public int originalIndex = -1;
    public final LongEncryptionStorageKind storageKind;
    public ResolvedMethodRef lookupMethod;
    public int index;

    public LongEncryptionStorageKind getStorageKind() {
        return this.storageKind;
    }

    public int getLocalVariableIndex() {
        return this.localVariableIndex;
    }

    public ObfuscatedReferenceSlot(int index, ResolvedMethodRef resolvedMethodRef) {
        this.storageKind = LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES;
        this.lookupMethod = resolvedMethodRef;
        this.index = index;
        this.originalIndex = index;
    }

    public boolean hasLocalVariable() {
        return this.localVariableIndex > -1;
    }

    public ObfuscatedReferenceSlot(ResolvedMethodRef resolvedMethodRef, int index) {
        this.storageKind = LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX;
        this.lookupMethod = resolvedMethodRef;
        this.index = index;
        this.originalIndex = index;
    }

    public ResolvedMethodRef getLookupMethod() {
        return this.lookupMethod;
    }

    public long getEncryptionKey() {
        return this.encryptionKey;
    }

    public ResolvedFieldRef getArrayField() {
        return this.arrayField;
    }

    public boolean hasArrayField() {
        return this.arrayField != null;
    }

    public ResolvedInvokeDynamic getInvokeDynamic() {
        return this.invokeDynamic;
    }

    public static void setSharedStrings(String[] strings) {
        sharedStrings = strings;
    }

    public static String[] getSharedStrings() {
        return sharedStrings;
    }

    public ObfuscatedReferenceSlot(ResolvedInvokeDynamic resolvedInvokeDynamic, int index) {
        this.storageKind = LongEncryptionStorageKind.INDY_ENTRY_AND_INDEX;
        this.invokeDynamic = resolvedInvokeDynamic;
        this.index = index;
        this.originalIndex = index;
    }

    public ObfuscatedReferenceSlot(ResolvedFieldRef resolvedFieldRef, int index) {
        this.storageKind = LongEncryptionStorageKind.FIELD_ARRAY_AND_INDEX;
        this.arrayField = resolvedFieldRef;
        this.index = index;
        this.originalIndex = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setEncryptionKey(long encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public boolean hasIndex() {
        return this.index > -1;
    }

    public int getIndex() {
        return this.index;
    }

    public ObfuscatedReferenceSlot(int localVariableIndex, int index) {
        this.storageKind = LongEncryptionStorageKind.LOCAL_ARRAY_AND_INDEX;
        this.localVariableIndex = localVariableIndex;
        this.index = index;
        this.originalIndex = index;
    }

    static {
        if (getSharedStrings() == null) {
            setSharedStrings(new String[1]);
        }
    }
}
