package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;

public class ReturnedNameSource {
    public ResolvedStringConstant stringConstant;
    public String failureReason;
    public int parameterIndex = Integer.MIN_VALUE;
    public TracedArrayValue arrayValue;
    public MethodBytecode methodBytecode;

    public TracedArrayValue getArrayValue() {
        return this.arrayValue;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public boolean hasArrayValue() {
        return this.arrayValue != null;
    }

    public ReturnedNameSource(TracedArrayValue tracedArrayValue, MethodBytecode methodBytecode1) {
        this.arrayValue = tracedArrayValue;
        this.methodBytecode = methodBytecode1;
    }

    public boolean hasStringConstant() {
        return this.stringConstant != null;
    }

    public ResolvedStringConstant getStringConstant() {
        return this.stringConstant;
    }

    public boolean isParameter() {
        return this.parameterIndex != Integer.MIN_VALUE;
    }

    public boolean hasFailureReason() {
        return this.failureReason != null;
    }

    public ReturnedNameSource(String string, MethodBytecode methodBytecode1) {
        this.failureReason = string;
        this.methodBytecode = methodBytecode1;
    }

    public ReturnedNameSource(int parameterIndex, MethodBytecode methodBytecode1) {
        this.parameterIndex = parameterIndex;
        this.methodBytecode = methodBytecode1;
    }

    public ReturnedNameSource(ResolvedStringConstant resolvedStringConstant, MethodBytecode methodBytecode1) {
        this.stringConstant = resolvedStringConstant;
        this.methodBytecode = methodBytecode1;
    }

    public MethodBytecode getMethodBytecode() {
        return this.methodBytecode;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }
}
