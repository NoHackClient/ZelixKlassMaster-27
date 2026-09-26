package com.zelix.klassmaster.classfile.insn;

public interface LocalVariableIndex {
    boolean isInserted();

    int getIndex();

    boolean isWideFirstHalf();

    int replaceIndex(int ba);

    boolean isWideSecondHalf();
}
