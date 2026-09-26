package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.insn.MethodBytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterUsageInfo {
    private static final String OBJECT_DESCRIPTOR = "Ljava/lang/Object;";
    public final MethodBytecode methodBytecode;
    public List usageIndices;
    public boolean storedTo;
    public boolean requiresCast;
    public boolean unpackAtEntry;
    private final int parameterIndex;
    public final String typeDescriptor;

    public int getFirstUsageIndex() {
        return (Integer) this.usageIndices.get(0);
    }

    public void addUsage(int ba, boolean bl) {
        this.usageIndices.add(ba);
        if (bl) {
            this.storedTo = true;
        }
    }

    public String getTypeDescriptor() {
        return this.typeDescriptor;
    }

    public ParameterUsageInfo(MethodBytecode methodBytecode1, int parameterIndex, String string) {
        this.methodBytecode = methodBytecode1;
        this.usageIndices = new ArrayList();
        this.storedTo = false;
        this.requiresCast = true;
        this.unpackAtEntry = true;
        this.parameterIndex = parameterIndex;
        this.typeDescriptor = string;
        if (string.equals(OBJECT_DESCRIPTOR)) {
            this.markNoCastRequired();
        }
    }

    public void markUnpackAtUse() {
        this.unpackAtEntry = false;
    }

    public boolean isUnpackedAtEntry() {
        return this.unpackAtEntry;
    }

    public List getUsageIndices() {
        return Collections.unmodifiableList(this.usageIndices);
    }

    public void markNoCastRequired() {
        this.requiresCast = false;
    }

    public boolean hasSingleUsage() {
        return this.usageIndices.size() == 1;
    }

    public boolean isStoredTo() {
        return this.storedTo;
    }

    public boolean isCastRequired() {
        return this.requiresCast;
    }
}
