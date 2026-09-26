package com.zelix.klassmaster.util;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class PairMultiMap {
    public ListMultimap multimap;

    public PairMultiMap(int ba, boolean bl) {
        this.multimap = new ListMultimap(ba, false);
    }

    public void clear() {
        this.multimap.clear();
    }

    public PairMultiMap() {
        this(false);
    }

    public int getKeyCount() {
        return this.multimap.getKeyCount();
    }

    public PairMultiMap(int ba) {
        this(ba, false);
    }

    public Enumeration keys() {
        return new CollectionSnapshotEnumeration(this.multimap.keySet());
    }

    public Set keySet() {
        return this.multimap.keySet();
    }

    public PairMultiMap(boolean bl) {
        this.multimap = new ListMultimap(bl);
    }

    public void addPair(Object object, Object object1, Object object2) {
        ObjectPair objectPair = new ObjectPair(object1, object2);
        this.multimap.addValue(object, objectPair);
    }

    public boolean isEmpty() {
        return this.multimap.isEmpty();
    }

    public List getPairs(Object object) {
        return this.multimap.getValues(object);
    }

    public Set entrySet() {
        return this.multimap.entrySet();
    }

    public int getPairCount() {
        return this.multimap.getValueCount();
    }
}
