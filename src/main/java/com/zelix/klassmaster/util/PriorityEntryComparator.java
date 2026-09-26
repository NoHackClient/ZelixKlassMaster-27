package com.zelix.klassmaster.util;

import com.zelix.klassmaster.classfile.insn.MethodBytecode;

import java.util.Comparator;

public class PriorityEntryComparator implements Comparator {
    public final MethodBytecode methodBytecode;

    public int compareByRank(ObjectPair objectPair, ObjectPair objectPair1) {
        long ba = 103576194117595L;
        ba = 85835464310795L ^ ba;
        int bb = (int) ((ba ^ 37193726607681L) >>> 32);
        long bc = (ba ^ 37193726607681L) << 32 >>> 32;
        RankedValue rankedValue1 = (RankedValue) objectPair.getFirst();
        RankedValue rankedValue = (RankedValue) objectPair1.getFirst();
        return rankedValue1.compareByRank(rankedValue);
    }

    public PriorityEntryComparator(MethodBytecode methodBytecode1) {
        this.methodBytecode = methodBytecode1;
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByRank((ObjectPair) object, (ObjectPair) object1);
    }
}
