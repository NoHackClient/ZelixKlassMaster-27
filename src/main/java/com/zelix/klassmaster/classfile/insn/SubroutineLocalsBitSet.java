package com.zelix.klassmaster.classfile.insn;

import java.util.BitSet;

public class SubroutineLocalsBitSet extends BitSet {
    public LabelInstruction subroutineEntry;

    public boolean isSameSubroutine(SubroutineLocalsBitSet subroutineLocalsBitSet1) {
        return this.subroutineEntry == subroutineLocalsBitSet1.subroutineEntry;
    }

    public SubroutineLocalsBitSet(LabelInstruction labelInstruction, int ba) {
        super(ba);
        this.subroutineEntry = labelInstruction;
    }

    public LabelInstruction getSubroutineEntry() {
        return this.subroutineEntry;
    }

    @Override
    public Object clone() {
        SubroutineLocalsBitSet subroutineLocalsBitSet1 = (SubroutineLocalsBitSet) super.clone();
        subroutineLocalsBitSet1.subroutineEntry = this.subroutineEntry;
        return subroutineLocalsBitSet1;
    }
}
