package com.zelix.klassmaster.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ThreeKeyMultiMap {
    public boolean concurrent;
    public int nestedCapacity;
    public int innerCapacity;
    public int listCapacity;
    public AbstractMap map;

    public ThreeKeyMultiMap(int ba) {
        this(ba, 10, 5, 5);
    }

    public NestedMultiMap getNestedMultiMap(Object object) {
        return (NestedMultiMap) this.map.get(object);
    }

    public Enumeration keys() {
        return new CollectionSnapshotEnumeration(this.map.keySet());
    }

    public ThreeKeyMultiMap(int ba, int nestedCapacity, int innerCapacity, boolean bl) {
        this.nestedCapacity = nestedCapacity;
        this.innerCapacity = innerCapacity;
        this.listCapacity = 5;
        if (bl) {
            this.map = new ConcurrentHashMap(ZkmUtils.getPrimeCapacity(ba));
        } else {
            this.map = new LinkedHashMap(ZkmUtils.getPrimeCapacity(ba));
        }
    }

    public synchronized boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public ThreeKeyMultiMap(int ba, int bb, int bc) {
        this(75, 10, 5, 5);
    }

    public synchronized boolean containsKeys(Object object, Object object1, Object object2) {
        NestedMultiMap nestedMultiMap = (NestedMultiMap) this.map.get(object);
        return nestedMultiMap == null ? false : nestedMultiMap.containsInnerKey(object1, object2);
    }

    public ThreeKeyMultiMap() {
        this(75, 10, 5);
    }

    public synchronized Set getEntrySnapshot() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            linkedHashSet.add(new SimpleMapEntry(entry.getKey(), entry.getValue()));
        }

        return Collections.unmodifiableSet(linkedHashSet);
    }

    public synchronized void addValue(Object object, Object object1, Object object2, Object object3) {
        NestedMultiMap nestedMultiMap = (NestedMultiMap) this.map.get(object);
        if (nestedMultiMap == null) {
            nestedMultiMap = new NestedMultiMap(this.nestedCapacity, this.innerCapacity, this.listCapacity, this.concurrent);
            nestedMultiMap.addValue(object1, object2, object3);
            this.map.put(object, nestedMultiMap);
        } else {
            nestedMultiMap.addValue(object1, object2, object3);
        }
    }

    public synchronized List getValues(Object object, Object object1, Object object2) {
        NestedMultiMap nestedMultiMap = (NestedMultiMap) this.map.get(object);
        if (nestedMultiMap == null) {
            return null;
        }

        ListMultimap listMultimap = nestedMultiMap.getMultimap(object1);
        return listMultimap == null ? null : listMultimap.getValues(object2);
    }

    public ThreeKeyMultiMap(int ba, int bb, int bc, int bd) {
        this(ba, 10, 5, true);
    }

    public int getKeyCount() {
        return this.map.size();
    }
}
