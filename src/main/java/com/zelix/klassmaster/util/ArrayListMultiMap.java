package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ArrayListMultiMap {
    public int initialListCapacity = 5;
    public OrderedIndexedMap map;

    @Override
    public Object clone() {
        int ba = this.map.size();
        ArrayListMultiMap arrayListMultiMap1 = new ArrayListMultiMap(ba);
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ArrayList arrayList = new ArrayList((Collection) entry.getValue());
            arrayListMultiMap1.putValues(entry.getKey(), arrayList);
        }

        return arrayListMultiMap1;
    }

    public synchronized Enumeration keys() {
        return this.map.keyEnumeration();
    }

    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public ArrayListMultiMap(int ba, int bb) {
        this.initialListCapacity = 5;
        this.map = new OrderedIndexedMap(ba);
    }

    public ArrayListMultiMap(int ba) {
        this(ba, 5);
    }

    public void addValue(Object object, Object object1) {
        java.util.List arrayList = (List) this.map.get(object);
        if (arrayList == null) {
            arrayList = new ArrayList(this.initialListCapacity);
            arrayList.add(object1);
            this.map.put(object, arrayList);
        } else {
            arrayList.add(object1);
        }
    }

    public void putValues(Object object, Object object1) {
        this.map.put(object, object1);
    }

    public int getKeyCount() {
        return this.map.size();
    }

    public ArrayListMultiMap() {
        this(75, 5);
    }

    public List getValues(Object object) {
        return (List) this.map.get(object);
    }
}
