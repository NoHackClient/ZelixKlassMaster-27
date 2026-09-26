package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SetMultiMap implements SetValuedMap {
    public Map map;
    public int initialCapacity;
    public int valueSetCapacity;
    public final boolean concurrent;

    public SetMultiMap(boolean bl) {
        this(75, 16, bl);
    }

    @Override
    public Set getValues(Object object) {
        return (Set) this.map.get(object);
    }

    @Override
    public Set removeKey(Object object) {
        return (Set) this.map.remove(object);
    }

    @Override
    public boolean containsValue(Object object, Object object1) {
        Set set1 = (Set) this.map.get(object);
        return set1 == null ? false : set1.contains(object1);
    }

    public SetMultiMap(int ba, boolean bl) {
        this(ba, 16, bl);
    }

    public Set createValueSet(int ba) {
        return this.concurrent ? Collections.newSetFromMap(new ConcurrentHashMap(ba)) : ZkmUtils.createHashSet(ba);
    }

    public void collectRetainedEntries(Set set1, Map map1) {
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();
            Set set2 = (Set) entry.getValue();
            Set set3 = this.createValueSet(ZkmUtils.getPrimeCapacity(set2.size()));

            for (Object object1 : set2) {
                if (set1.contains(object1)) {
                    set3.add(object1);
                }
            }

            if (!set3.isEmpty()) {
                map1.put(object, set3);
            }
        }
    }

    public SetMultiMap deepCopy() {
        int ba = this.map.size();
        SetMultiMap setMultiMap1 = new SetMultiMap(ba * 2 + 1, this.concurrent);
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Set set1 = (Set) entry.getValue();
            int bb = ZkmUtils.getPrimeCapacity(set1.size());
            Set set2 = this.concurrent ? Collections.newSetFromMap(new ConcurrentHashMap(bb)) : ZkmUtils.createHashSet(bb);
            set2.addAll(set1);
            setMultiMap1.putValueSet(entry.getKey(), set2);
        }

        return setMultiMap1;
    }

    @Override
    public Enumeration keys() {
        return Collections.enumeration(this.map.keySet());
    }

    public SetMultiMap(int ba) {
        this(ba, 16);
    }

    @Override
    public boolean isEmpty() {
        return this.map.size() == 0;
    }

    public Enumeration distinctValues() {
        Set set1 = this.createValueSet(101);
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Set set2 = (Set) ((Entry) iterator.next()).getValue();
            set1.addAll(set2);
        }

        return Collections.enumeration(set1);
    }

    public SetMultiMap() {
        this(75, 16);
    }

    public Set putValueSet(Object object, Object object1) {
        return (Set) this.map.put(object, object1);
    }

    public SetMultiMap(int ba, int bb) {
        this(ba, bb, false);
    }

    @Override
    public List getValueList(Object object) {
        return (Set) this.map.get(object) != null ? new ArrayList((Collection) this.map.get(object)) : null;
    }

    @Override
    public boolean removeValue(Object object, Object object1) {
        boolean bl = false;
        Set set1 = (Set) this.map.get(object);
        if (set1 != null) {
            bl = set1.remove(object1);
            if (set1.size() == 0) {
                this.map.remove(object);
            }
        }

        return bl;
    }

    public void addValues(Object object, Collection collection1) {
        if (collection1 != null) {
            for (Object object1 : collection1) {
                this.addValue(object, object1);
            }
        }
    }

    @Override
    public int getKeyCount() {
        return this.map.size();
    }

    @Override
    public Set keySet() {
        return this.map.keySet();
    }

    @Override
    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public SetMultiMap(Map map1) {
        this.map = map1;
        this.initialCapacity = ZkmUtils.getPrimeCapacity(75);
        this.valueSetCapacity = ZkmUtils.getPrimeCapacity(16);
        this.concurrent = false;
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public Map createMap(int ba) {
        return this.concurrent ? new ConcurrentHashMap(ba) : ZkmUtils.createHashMap(ba);
    }

    @Override
    public final void retainValues(Object[] objects) {
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(objects.length));

        for (Object object : objects) {
            hashSet.add(object);
        }

        Map map1 = this.createMap(ZkmUtils.getPrimeCapacity(this.map.size()));
        if (this.concurrent) {
            synchronized (this.map) {
                this.collectRetainedEntries(hashSet, map1);
            }
        } else {
            this.collectRetainedEntries(hashSet, map1);
        }

        this.map = map1;
    }

    @Override
    public boolean addValue(Object object, Object object1) {
        Set set1 = (Set) this.map.get(object);
        boolean bl;
        if (set1 == null) {
            set1 = this.createValueSet(this.valueSetCapacity);
            set1.add(object1);
            this.map.put(object, set1);
            bl = true;
        } else {
            bl = set1.add(object1);
        }

        return bl;
    }

    public final ListMultimap toListMultimap() {
        int ba = this.map.size();
        ListMultimap listMultimap = new ListMultimap(ba * 2 + 1, this.concurrent);
        if (this.concurrent) {
            synchronized (this.map) {
                Iterator iterator = this.map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    listMultimap.appendValues(entry.getKey(), (Collection) entry.getValue());
                }
            }
        } else {
            Iterator iterator1 = this.map.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                listMultimap.appendValues(entry1.getKey(), (Collection) entry1.getValue());
            }
        }

        return listMultimap;
    }

    @Override
    public Object clone() {
        return this.deepCopy();
    }

    public SetMultiMap(int ba, int bb, boolean concurrent) {
        this.initialCapacity = ZkmUtils.getPrimeCapacity(ba);
        this.valueSetCapacity = ZkmUtils.getPrimeCapacity(bb);
        this.concurrent = concurrent;
        if (concurrent) {
            this.map = new ConcurrentHashMap(this.initialCapacity);
        } else {
            this.map = this.createMap(this.initialCapacity);
        }
    }
}
