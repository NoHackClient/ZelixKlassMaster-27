package com.zelix.klassmaster.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CountingBag implements Collection {
    public Map counts;

    @Override
    public Object[] toArray() {
        Object[] objects = new Object[this.counts.size()];
        int ba = 0;
        Iterator iterator = this.counts.keySet().iterator();

        while (iterator.hasNext()) {
            objects[ba++] = iterator.next();
        }

        return objects;
    }

    @Override
    public boolean containsAll(Collection collection1) {
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            if (!this.counts.containsKey(iterator.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public synchronized Iterator iterator() {
        return this.counts.keySet().iterator();
    }

    @Override
    public synchronized boolean add(Object object) {
        MutableInt mutableInt = (MutableInt) this.counts.get(object);
        if (mutableInt == null) {
            mutableInt = new MutableInt(1);
            this.counts.put(object, mutableInt);
        } else {
            mutableInt.incrementAndGet();
        }

        return true;
    }

    @Override
    public synchronized boolean retainAll(Collection collection1) {
        int ba = this.counts.size();
        Iterator iterator = this.counts.keySet().iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (!collection1.contains(object)) {
                iterator.remove();
            }
        }

        return ba != this.counts.size();
    }

    @Override
    public void clear() {
        this.counts.clear();
    }

    public synchronized void addCount(Object object, int ba) {
        MutableInt mutableInt = (MutableInt) this.counts.get(object);
        if (mutableInt == null) {
            this.counts.put(object, new MutableInt(ba));
        } else {
            mutableInt.setValue(ba);
        }
    }

    public synchronized List getSortedByCount() {
        int ba = this.counts.size();
        RankedValue[] rankedValues = new RankedValue[ba];
        int bb = 0;
        Iterator iterator = this.counts.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();
            MutableInt mutableInt = (MutableInt) entry.getValue();
            rankedValues[bb++] = new RankedValue(mutableInt.getValue(), object);
        }

        Arrays.sort(rankedValues);
        ArrayList arrayList = new ArrayList(ba);

        for (int i = ba - 1; i >= 0; i += -1) {
            arrayList.add(rankedValues[i].getValue());
        }

        return arrayList;
    }

    @Override
    public boolean isEmpty() {
        return this.counts.size() == 0;
    }

    public CountingBag() {
        this(75);
    }

    @Override
    public synchronized Object clone() {
        CountingBag countingBag1 = new CountingBag(this.counts.size());
        Iterator iterator = this.counts.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            MutableInt mutableInt = (MutableInt) entry.getValue();
            countingBag1.addCount(entry.getKey(), mutableInt.getValue());
        }

        return countingBag1;
    }

    @Override
    public synchronized boolean remove(Object object) {
        MutableInt mutableInt = (MutableInt) this.counts.get(object);
        if (mutableInt != null) {
            if (mutableInt.getValue() == 1) {
                this.counts.remove(object);
            } else {
                mutableInt.decrementAndGet();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean contains(Object object) {
        return this.counts.containsKey(object);
    }

    @Override
    public synchronized boolean removeAll(Collection collection1) {
        int ba = this.counts.size();
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            this.counts.remove(iterator.next());
        }

        return ba != this.counts.size();
    }

    @Override
    public synchronized boolean addAll(Collection collection1) {
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            this.add(iterator.next());
        }

        return true;
    }

    @Override
    public Object[] toArray(Object[] objects) {
        if (objects.length < this.counts.size()) {
            objects = (Object[]) Array.newInstance(objects.getClass().getComponentType(), this.counts.size());
        }

        int ba = 0;
        Iterator iterator = this.counts.keySet().iterator();

        while (iterator.hasNext()) {
            objects[ba++] = iterator.next();
        }

        if (objects.length > this.counts.size()) {
            objects[this.counts.size()] = null;
        }

        return objects;
    }

    @Override
    public int size() {
        return this.counts.size();
    }

    public synchronized int getCount(Object object) {
        MutableInt mutableInt = (MutableInt) this.counts.get(object);
        return mutableInt == null ? 0 : mutableInt.getValue();
    }

    public CountingBag(int ba) {
        this.counts = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
