package com.zelix.klassmaster.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderedIndexedMap implements Serializable, Map {
    public Map Z;
    public List B;

    @Override
    public boolean isEmpty() {
        return this.Z.isEmpty();
    }

    public OrderedIndexedMap(int ba) {
        this.Z = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        this.B = new ArrayList(ba);
    }

    @Override
    public synchronized Collection values() {
        return this.getValueList();
    }

    public OrderedIndexedMap() {
        this.Z = ZkmUtils.createHashMap();
        this.B = new ArrayList();
    }

    public List getKeyList() {
        int ba = this.B.size();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            ObjectPair objectPair = (ObjectPair) this.B.get(i);
            arrayList.add(objectPair.getFirst());
        }

        return arrayList;
    }

    public synchronized Object getKeyAt(int ba) {
        if (ba >= 0 && ba < this.B.size()) {
            return ((ObjectPair) this.B.get(ba)).getFirst();
        } else {
            throw new IllegalArgumentException("Index " + ba + " out of range : " + this.B.size());
        }
    }

    @Override
    public synchronized Object remove(Object object) {
        Object object1 = null;
        ObjectPair objectPair = (ObjectPair) this.Z.remove(object);
        if (objectPair != null) {
            int ba = this.B.size();

            for (int i = 0; i < ba; i++) {
                if (((ObjectPair) this.B.get(i)).getFirst().equals(object)) {
                    this.B.remove(i);
                    break;
                }
            }

            object1 = objectPair.getSecond();
        }

        return object1;
    }

    @Override
    public synchronized void putAll(Map map1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Object clone() {
        return new OrderedIndexedMap(this);
    }

    public int indexOfKey(Object object) {
        if (this.Z.containsKey(object)) {
            int ba = this.B.size();

            for (int i = 0; i < ba; i++) {
                if (((ObjectPair) this.B.get(i)).getFirst().equals(object)) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public synchronized Set keySet() {
        return Collections.unmodifiableSet(this.Z.keySet());
    }

    @Override
    public synchronized void clear() {
        this.Z.clear();
        this.B.clear();
    }

    @Override
    public synchronized Set entrySet() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.B.iterator();

        while (iterator.hasNext()) {
            ObjectPair objectPair = (ObjectPair) iterator.next();
            arrayList.add(new SimpleMapEntry(objectPair));
        }

        return new NonNullSet(new SyncIndexedSet(arrayList));
    }

    @Override
    public int size() {
        return this.B.size();
    }

    @Override
    public synchronized boolean containsValue(Object object) {
        int ba = this.B.size();

        for (int i = 0; i < ba; i++) {
            if (((ObjectPair) this.B.get(i)).getSecond().equals(object)) {
                return true;
            }
        }

        return false;
    }

    public synchronized Enumeration keyEnumeration() {
        return Collections.enumeration(this.getKeyList());
    }

    public List getValueList() {
        int ba = this.B.size();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            ObjectPair objectPair = (ObjectPair) this.B.get(i);
            arrayList.add(objectPair.getSecond());
        }

        return arrayList;
    }

    public synchronized Object removeAt(int ba) {
        if (ba >= 0 && ba < this.B.size()) {
            ObjectPair objectPair = (ObjectPair) this.B.remove(ba);
            return ((ObjectPair) this.Z.remove(objectPair.getFirst())).getSecond();
        } else {
            throw new IllegalArgumentException("Index " + ba + " out of range : " + this.B.size());
        }
    }

    public synchronized Object replaceEntryAt(int ba, Object object, Object object1) {
        if (ba < 0 || ba >= this.B.size()) {
            throw new IllegalArgumentException("Index " + ba + " out of range : " + this.B.size());
        }

        if (this.Z.containsKey(object)) {
            if (((ObjectPair) this.Z.get(object)).equals(this.B.get(ba))) {
                return ((ObjectPair) this.B.get(ba)).getSecond();
            } else {
                throw new IllegalArgumentException("'" + object + "' is not a unique key");
            }
        } else {
            ObjectPair objectPair = new ObjectPair(object, object1);
            ObjectPair objectPair1 = ((com.zelix.klassmaster.util.ObjectPair) (this.B.set(ba, objectPair)));
            ObjectPair objectPair2 = (ObjectPair) this.Z.remove(objectPair1.getFirst());
            this.Z.put(object, objectPair);
            return objectPair2.getSecond();
        }
    }

    @Override
    public synchronized Object get(Object object) {
        ObjectPair objectPair = (ObjectPair) this.Z.get(object);
        return objectPair == null ? null : objectPair.getSecond();
    }

    @Override
    public synchronized Object put(Object object, Object object1) {
        Object object2 = null;
        ObjectPair objectPair = (ObjectPair) this.Z.get(object);
        if (objectPair == null) {
            objectPair = new ObjectPair(object, object1);
            this.Z.put(object, objectPair);
            this.B.add(objectPair);
        } else {
            object2 = objectPair.setSecond(object1);
        }

        return object2;
    }

    public OrderedIndexedMap(OrderedIndexedMap orderedIndexedMap1) {
        this(orderedIndexedMap1.size());
        synchronized (orderedIndexedMap1) {
            int ba = orderedIndexedMap1.size();

            for (int i = 0; i < ba; i++) {
                ObjectPair objectPair = (ObjectPair) orderedIndexedMap1.B.get(i);
                Object object = objectPair.getFirst();
                Object object1 = objectPair.getSecond();
                ObjectPair objectPair1 = new ObjectPair(object, object1);
                this.Z.put(object, objectPair1);
                this.B.add(objectPair1);
            }
        }
    }

    @Override
    public synchronized boolean containsKey(Object object) {
        return this.Z.containsKey(object);
    }
}
