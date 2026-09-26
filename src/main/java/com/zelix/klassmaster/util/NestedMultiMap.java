package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NestedMultiMap {
    public int innerInitialCapacity;
    public int innerListCapacity;
    public boolean concurrent;
    public Map map;

    public NestedMultiMap() {
        this(75, 10, 5, false);
    }

    public NestedMultiMap(int ba, boolean bl) {
        this(ba, 5, 5, bl);
    }

    public Enumeration keys() {
        return new CollectionSnapshotEnumeration(this.map.keySet());
    }

    public synchronized void clearAll() {
        Iterator iterator = this.map.values().iterator();

        while (iterator.hasNext()) {
            ((ListMultimap) iterator.next()).clear();
        }

        this.map.clear();
    }

    public ListMultimap putMultimap(Object object, Object object1) {
        return (ListMultimap) this.map.put(object, object1);
    }

    public ListMultimap getMultimap(Object object) {
        return (ListMultimap) this.map.get(object);
    }

    public ListMultimap removeMultimap(Object object) {
        return (ListMultimap) this.map.remove(object);
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public synchronized void addValues(Object object, Object object1, Collection collection1) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        if (listMultimap == null) {
            listMultimap = new ListMultimap(false, this.innerInitialCapacity, collection1.size(), this.concurrent);
            this.map.put(object, listMultimap);
        }

        for (Object object2 : collection1) {
            listMultimap.addValue(object1, object2);
        }
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public NestedMultiMap(int ba, int bb, int bc) {
        this(ba, bb, bc, false);
    }

    public int getKeyCount() {
        return this.map.size();
    }

    public NestedMultiMap(boolean bl) {
        this(75, 10, 5, bl);
    }

    public synchronized boolean containsValue(Object object, Object object1, Object object2) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        return listMultimap == null ? false : listMultimap.containsValue(object1, object2);
    }

    public NestedMultiMap(int ba) {
        this(ba, 10, 5, false);
    }

    public synchronized List getValues(Object object, Object object1) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        return listMultimap == null ? null : listMultimap.getValues(object1);
    }

    public synchronized void addValue(Object object, Object object1, Object object2) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        if (listMultimap == null) {
            listMultimap = new ListMultimap(false, this.innerInitialCapacity, this.innerListCapacity, this.concurrent);
            listMultimap.addValue(object1, object2);
            this.map.put(object, listMultimap);
        } else {
            listMultimap.addValue(object1, object2);
        }
    }

    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public NestedMultiMap(int ba, int innerInitialCapacity, int innerListCapacity, boolean concurrent) {
        this.innerInitialCapacity = innerInitialCapacity;
        this.innerListCapacity = innerListCapacity;
        this.concurrent = concurrent;
        if (concurrent) {
            this.map = new ConcurrentHashMap(ZkmUtils.getPrimeCapacity(ba));
        } else {
            this.map = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        }
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public synchronized List removeInnerKey(Object object, Object object1) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        return listMultimap != null ? listMultimap.removeKey(object1) : null;
    }

    public synchronized boolean containsInnerKey(Object object, Object object1) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        return listMultimap == null ? false : listMultimap.containsKey(object1);
    }

    public synchronized boolean removeValue(Object object, Object object1, Object object2) {
        ListMultimap listMultimap = (ListMultimap) this.map.get(object);
        return listMultimap != null ? listMultimap.removeValue(object1, object2) : false;
    }
}
