package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.RankedValue;

import java.util.Comparator;

public class InstructionPositionComparator implements Comparator {
    public final MethodBytecode methodBytecode;

    public InstructionPositionComparator(MethodBytecode methodBytecode1) {
        this.methodBytecode = methodBytecode1;
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByRank((ObjectPair) object, (ObjectPair) object1);
    }

    public int compareByRank(ObjectPair objectPair, ObjectPair objectPair1) {
        long ba = 123201644296177L;
        ba = 37463384023572L ^ ba;
        int bb = (int) ((ba ^ 109877096430452L) >>> 32);
        long bc = (ba ^ 109877096430452L) << 32 >>> 32;
        RankedValue rankedValue1 = (RankedValue) objectPair.getFirst();
        RankedValue rankedValue = (RankedValue) objectPair1.getFirst();
        return rankedValue1.compareByRank(rankedValue);
    }
}
