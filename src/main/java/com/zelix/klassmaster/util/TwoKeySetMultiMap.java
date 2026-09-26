package com.zelix.klassmaster.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TwoKeySetMultiMap {
    public final int innerCapacity;
    public final int valueSetCapacity;
    public Map map;

    public TwoKeySetMultiMap(int ba, int bb) {
        this(75, 10, 16);
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public SetMultiMap flattenToSetMultiMap() {
        SetMultiMap setMultiMap = new SetMultiMap(this.map.size());
        Iterator iterator = this.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();

            for (Object object1 : ((SetMultiMap) entry.getValue()).keySet()) {
                setMultiMap.addValue(object, object1);
            }
        }

        return setMultiMap;
    }

    public Set getValues(Object object, Object object1) {
        SetMultiMap setMultiMap = (SetMultiMap) this.map.get(object);
        return setMultiMap == null ? null : setMultiMap.getValues(object1);
    }

    public boolean addValue(Object object, Object object1, Object object2) {
        SetMultiMap setMultiMap = (SetMultiMap) this.map.get(object);
        if (setMultiMap == null) {
            setMultiMap = new SetMultiMap(this.innerCapacity, this.valueSetCapacity);
            boolean bl = setMultiMap.addValue(object1, object2);
            this.map.put(object, setMultiMap);
            return bl;
        } else {
            return setMultiMap.addValue(object1, object2);
        }
    }

    public TwoKeySetMultiMap(int ba, int innerCapacity, int valueSetCapacity) {
        this.innerCapacity = innerCapacity;
        this.valueSetCapacity = valueSetCapacity;
        this.map = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
    }

    public NestedMultimapView toNestedMultimapView() {
        NestedMultiMap nestedMultiMap = new NestedMultiMap(this.map.size() * 2);
        Iterator iterator = this.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Iterator iterator1 = ((SetMultiMap) entry.getValue()).entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();

                for (Object object : (Set) entry1.getValue()) {
                    nestedMultiMap.addValue(entry.getKey(), entry1.getKey(), object);
                }
            }
        }

        return new NestedMultimapView(nestedMultiMap);
    }

    public SetMultiMap getSetMultiMap(Object object) {
        return (SetMultiMap) this.map.get(object);
    }

    public TwoKeySetMultiMap() {
        this(75, 10);
    }
}
