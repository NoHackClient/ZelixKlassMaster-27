package com.zelix.klassmaster.util;

import com.zelix.klassmaster.classfile.insn.MethodBytecode;

import java.util.Comparator;

public class RankedEntryComparator implements Comparator {
    public final MethodBytecode methodBytecode;

    public RankedEntryComparator(MethodBytecode methodBytecode1) {
        this.methodBytecode = methodBytecode1;
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByRank((ObjectPair) object, (ObjectPair) object1);
    }

    public int compareByRank(ObjectPair objectPair, ObjectPair objectPair1) {
        return ((RankedValue) objectPair.getFirst()).compareByRank((RankedValue) objectPair1.getFirst());
    }
}
