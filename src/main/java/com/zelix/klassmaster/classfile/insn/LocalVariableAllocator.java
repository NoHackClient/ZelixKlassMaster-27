package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.MethodInfo;

public class LocalVariableAllocator implements IntCounter {
    private final MethodInfo methodInfo;
    public int nextIndex;
    public int startIndex;
    private final LocalVariableList localVariableList;

    public LocalVariableAllocator(MethodInfo methodInfo1) {
        this.methodInfo = methodInfo1;
        this.nextIndex = methodInfo1.getLocalVariableCount();
        this.startIndex = this.nextIndex;
        this.localVariableList = methodInfo1.getLocalVariableList();
    }

    @Override
    public int getAndIncrement() {
        return this.nextIndex++;
    }

    @Override
    public int getValue() {
        return this.nextIndex;
    }

    @Override
    public int incrementAndGet() {
        return ++this.nextIndex;
    }

    @Override
    public int addAndGet(int ba) {
        this.nextIndex += ba;
        return this.nextIndex;
    }
}
