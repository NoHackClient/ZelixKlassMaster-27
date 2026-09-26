package com.zelix.klassmaster.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TwoKeyMap {
    public boolean linked;
    private boolean concurrent;
    private int outerCapacity;
    private int innerCapacity;
    private Map map;

    public TwoKeyMap deepCopy() {
        int ba = this.map.size();
        TwoKeyMap twoKeyMap1 = new TwoKeyMap(this.linked, ba * 2 + 1, this.innerCapacity, this.concurrent);
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Map map1 = (Map) entry.getValue();
            Map map2 = this.copyInnerMap(map1);
            twoKeyMap1.putInnerMap(entry.getKey(), map2);
        }

        return twoKeyMap1;
    }

    public int getValueCount() {
        int ba = 0;
        Iterator iterator = this.map.values().iterator();

        while (iterator.hasNext()) {
            Map map1 = (Map) iterator.next();
            if (map1 != null) {
                ba += map1.size();
            }
        }

        return ba;
    }

    public Object getValue(Object object, Object object1) {
        Map map1 = (Map) this.map.get(object);
        return map1 == null ? null : map1.get(object1);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Enumeration innerMaps() {
        return Collections.enumeration(this.map.values());
    }

    public TwoKeyMap(boolean bl) {
        this(75, 15, bl);
    }

    public Map getInnerMap(Object object) {
        return (Map) this.map.get(object);
    }

    public int getKeyCount() {
        return this.map.size();
    }

    public TwoKeyMap(boolean bl, boolean bl1) {
        this(true, 75, 15, false);
    }

    private Map createInnerMap() {
        if (this.linked) {
            return new LinkedHashMap(this.innerCapacity);
        } else {
            return this.concurrent ? new ConcurrentHashMap(this.innerCapacity) : ZkmUtils.createHashMap(this.innerCapacity);
        }
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public TwoKeyMap() {
        this(75, 15, false);
    }

    public TwoKeyMap(int ba, int bb, boolean bl) {
        this(false, ba, bb, bl);
    }

    public TwoKeyMap(int ba, int bb) {
        this(ba, bb, false);
    }

    public TwoKeyMap(int ba, boolean bl) {
        this(ba, 15, false);
    }

    public Map copyOf(Map map1) {
        return this.copyInnerMap(map1);
    }

    public Object removeValue(Object object, Object object1) {
        Object object2 = null;
        Map map1 = (Map) this.map.get(object);
        if (map1 != null) {
            object2 = map1.remove(object1);
            if (map1.size() == 0) {
                this.map.remove(object);
            }
        }

        return object2;
    }

    public boolean containsKeys(Object object, Object object1) {
        Map map1 = (Map) this.map.get(object);
        return map1 == null ? false : map1.containsKey(object1);
    }

    public Map removeInnerMap(Object object) {
        return (Map) this.map.remove(object);
    }

    public Enumeration keys() {
        return Collections.enumeration(this.map.keySet());
    }

    public Enumeration distinctValues() {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.map.values().iterator();

        while (iterator.hasNext()) {
            Iterator iterator1 = ((Map) iterator.next()).values().iterator();

            while (iterator1.hasNext()) {
                hashSet.add(iterator1.next());
            }
        }

        return Collections.enumeration(hashSet);
    }

    public TwoKeyMap(boolean linked, int ba, int bb, boolean concurrent) {
        this.linked = linked;
        this.concurrent = concurrent;
        this.outerCapacity = ZkmUtils.getPrimeCapacity(ba);
        this.innerCapacity = ZkmUtils.getPrimeCapacity(bb);
        if (linked) {
            this.map = new LinkedHashMap(this.outerCapacity);
        } else if (concurrent) {
            this.map = new ConcurrentHashMap(this.outerCapacity);
        } else {
            this.map = ZkmUtils.createHashMap(this.outerCapacity);
        }
    }

    public Map copyInnerMap(Map map1) {
        if (this.linked) {
            return new LinkedHashMap(map1);
        } else {
            return this.concurrent ? new ConcurrentHashMap(map1) : ZkmUtils.copyToHashMap(map1);
        }
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public TwoKeyMap(int ba) {
        this(ba, 15, false);
    }

    public void clear() {
        Iterator iterator = this.map.values().iterator();

        while (iterator.hasNext()) {
            ((Map) iterator.next()).clear();
        }

        this.map.clear();
    }

    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public Map putInnerMap(Object object, Map map1) {
        return ((java.util.Map) (this.map.put(object, this.copyInnerMap(map1))));
    }

    public Object putValue(Object object, Object object1, Object object2) {
        Object object3 = null;
        Map map1 = (Map) this.map.get(object);
        if (map1 == null) {
            map1 = this.createInnerMap();
            map1.put(object1, object2);
            this.map.put(object, map1);
        } else {
            object3 = map1.put(object1, object2);
        }

        return object3;
    }
}
