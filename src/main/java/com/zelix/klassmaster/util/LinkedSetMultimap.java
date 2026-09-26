package com.zelix.klassmaster.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LinkedSetMultimap extends SetMultiMap {
    @Override
    public Map createMap(int ba) {
        return new LinkedHashMap(ba);
    }

    @Override
    public SetMultiMap deepCopy() {
        int ba = this.map.size();
        LinkedSetMultimap linkedSetMultimap1 = new LinkedSetMultimap(ba * 2 + 1);
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Set set1 = (Set) entry.getValue();
            LinkedHashSet linkedHashSet = new LinkedHashSet(set1);
            linkedSetMultimap1.putValueSet(entry.getKey(), linkedHashSet);
        }

        return linkedSetMultimap1;
    }

    public LinkedSetMultimap() {
        super(new LinkedHashMap(ZkmUtils.getPrimeCapacity(75)));
    }

    private LinkedSetMultimap(int ba) {
        super(new LinkedHashMap(ZkmUtils.getPrimeCapacity(ba)));
    }

    @Override
    public Set createValueSet(int ba) {
        return new LinkedHashSet(ba);
    }
}
