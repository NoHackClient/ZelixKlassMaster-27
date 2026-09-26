package com.zelix.klassmaster.obfuscator.string;

import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInvokeDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;

public class EncryptedStringLocation {
    private ResolvedMethodRefConstant firstMethodRef;
    private boolean useJsr;
    private LabelInstruction jumpLabel;
    public long decryptKey;
    public ResolvedInvokeDynamic indyEntry;
    public int lookupKey;
    public ResolvedMethodRef lookupMethodRef;
    private ResolvedMethodRefConstant secondMethodRef;
    public int arrayLocalIndex = -1;
    public final StringEncryptionTechnique technique;
    public ResolvedFieldRef arrayFieldRef;
    private int stringIndex;

    public EncryptedStringLocation(ResolvedFieldRef resolvedFieldRef, int stringIndex) {
        this.technique = StringEncryptionTechnique.FIELD_ARRAY_AND_INDEX;
        this.arrayFieldRef = resolvedFieldRef;
        this.stringIndex = stringIndex;
    }

    public boolean isJsr() {
        return this.useJsr;
    }

    public void setDecryptKey(long decryptKey) {
        this.decryptKey = decryptKey;
    }

    public LabelInstruction getJumpLabel() {
        return this.jumpLabel;
    }

    public ResolvedMethodRefConstant getSecondMethodRef() {
        return this.firstMethodRef;
    }

    public int getLookupKey() {
        return this.lookupKey;
    }

    public int getArrayLocalIndex() {
        return this.arrayLocalIndex;
    }

    public ResolvedInvokeDynamic getIndyEntry() {
        return this.indyEntry;
    }

    public int getStringIndex() {
        return this.stringIndex;
    }

    public ResolvedMethodRefConstant getFirstMethodRef() {
        return this.secondMethodRef;
    }

    public ResolvedMethodRef getLookupMethodRef() {
        return this.lookupMethodRef;
    }

    public StringEncryptionTechnique getTechnique() {
        return this.technique;
    }

    public boolean hasStringIndex() {
        return this.stringIndex > -1;
    }

    public boolean hasArrayLocal() {
        return this.arrayLocalIndex > -1;
    }

    public void setLookupKey(int lookupKey) {
        this.lookupKey = lookupKey;
    }

    public EncryptedStringLocation(int arrayLocalIndex, int stringIndex) {
        this.technique = StringEncryptionTechnique.LOCAL_ARRAY_AND_INDEX;
        this.arrayLocalIndex = arrayLocalIndex;
        this.stringIndex = stringIndex;
    }

    public EncryptedStringLocation(int stringIndex, ResolvedMethodRef resolvedMethodRef) {
        this.technique = StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX_DES;
        this.lookupMethodRef = resolvedMethodRef;
        this.stringIndex = stringIndex;
    }

    public ResolvedFieldRef getArrayFieldRef() {
        return this.arrayFieldRef;
    }

    public boolean hasArrayField() {
        return this.arrayFieldRef != null;
    }

    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    public EncryptedStringLocation(ResolvedMethodRef resolvedMethodRef, int stringIndex) {
        this.technique = StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX;
        this.lookupMethodRef = resolvedMethodRef;
        this.stringIndex = stringIndex;
    }

    public EncryptedStringLocation(ResolvedInvokeDynamic resolvedInvokeDynamic, int stringIndex) {
        this.technique = StringEncryptionTechnique.INDY_ENTRY_AND_INDEX;
        this.indyEntry = resolvedInvokeDynamic;
        this.stringIndex = stringIndex;
    }

    public long getDecryptKey() {
        return this.decryptKey;
    }
}
