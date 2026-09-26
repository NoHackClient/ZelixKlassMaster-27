package com.zelix.klassmaster.classfile.insn;

import java.util.BitSet;

public class PendingBlockVisit {
    public final BasicBlock block;
    public final int startIndex;
    public final BitSet liveLocals;

    public PendingBlockVisit(BasicBlock basicBlock, int startIndex, BitSet bitSet) {
        this.block = basicBlock;
        this.startIndex = startIndex;
        this.liveLocals = bitSet;
    }
}
