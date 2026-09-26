package com.zelix.klassmaster.classfile.insn;

import java.util.BitSet;

public class BlockBitSet {
    public final BasicBlock block;
    public final BitSet bits;

    public BlockBitSet(BasicBlock basicBlock, BitSet bitSet) {
        this.block = basicBlock;
        this.bits = bitSet;
    }
}
