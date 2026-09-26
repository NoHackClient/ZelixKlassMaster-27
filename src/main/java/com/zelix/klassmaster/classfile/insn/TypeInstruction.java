package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolProvider;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class TypeInstruction extends ConstantRefInstruction {
    private int bytecodeOffset = -1;

    @Override
    public void setOffset(int bytecodeOffset) {
        this.bytecodeOffset = bytecodeOffset;
    }

    public int getBytecodeOffset() {
        return this.bytecodeOffset;
    }

    public TypeInstruction(ConstantPoolEntry constantPoolEntry) {
        super(187, constantPoolEntry);
    }

    public TypeInstruction(
            ClassFileInputStream classFileInputStream,
            ConstantPoolProvider constantPoolProvider,
            int bytecodeOffset,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4
    ) throws IOException {
        super(187, classFileInputStream, constantPoolProvider, listMultimap, listMultimap1, listMultimap2, listMultimap3, listMultimap4);
        this.bytecodeOffset = bytecodeOffset;
    }
}
