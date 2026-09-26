package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.classfile.insn.BasicBlock;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;

public class FlowSplitCandidate {
    public final MethodBytecode methodBytecode;
    public final int startIndex;
    public final int endIndex;
    public final BasicBlock targetBlock;

    public FlowSplitCandidate(MethodBytecode methodBytecode1, int startIndex, int endIndex, BasicBlock basicBlock) {
        this.methodBytecode = methodBytecode1;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.targetBlock = basicBlock;
    }
}
