package com.zelix.klassmaster.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SyncIndexedSet implements Serializable, Set {
    public Set m;
    private List y;

    @Override
    public boolean retainAll(Collection collection1) {
        boolean bl = this.m.retainAll(collection1);
        if (bl) {
            this.y.retainAll(collection1);
        }

        return bl;
    }

    @Override
    public boolean removeAll(Collection collection1) {
        boolean bl = this.m.removeAll(collection1);
        if (bl) {
            this.y.removeAll(collection1);
        }

        return bl;
    }

    @Override
    public synchronized void clear() {
        this.m.clear();
        this.y.clear();
    }

    public int indexOfElement(Object object) {
        return this.m.contains(object) ? this.y.indexOf(object) : -1;
    }

    @Override
    public synchronized boolean add(Object object) {
        if (this.m.add(object)) {
            this.y.add(object);
            return true;
        } else {
            return false;
        }
    }

    public synchronized Object getElementAt(int ba) {
        if (ba >= 0 && ba < this.y.size()) {
            return this.y.get(ba);
        } else {
            throw new IllegalArgumentException("Index " + ba + " out of range : " + this.y.size());
        }
    }

    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }

    @Override
    public synchronized boolean remove(Object object) {
        boolean bl = this.m.remove(object);
        if (bl) {
            this.y.remove(object);
        }

        return bl;
    }

    @Override
    public boolean containsAll(Collection collection1) {
        return this.m.containsAll(collection1);
    }

    @Override
    public Object[] toArray(Object[] objects) {
        return this.y.toArray(objects);
    }

    @Override
    public Object[] toArray() {
        Object[] objects = new Object[this.y.size()];
        return this.y.toArray(objects);
    }

    public SyncIndexedSet(SyncIndexedSet syncIndexedSet1) {
        this(syncIndexedSet1.size());
        synchronized (syncIndexedSet1) {
            this.m.addAll(syncIndexedSet1);
            this.y.addAll(syncIndexedSet1);
        }
    }

    public SyncIndexedSet() {
        this.m = ZkmUtils.createHashSet();
        this.y = new ArrayList();
    }

    @Override
    public boolean addAll(Collection collection1) {
        boolean bl = false;

        for (Object object : collection1) {
            if (this.m.add(object)) {
                bl = true;
                this.y.add(object);
            }
        }

        return bl;
    }

    public SyncIndexedSet(Collection collection1) {
        this(collection1.size());
        synchronized (collection1) {
            for (Object object : collection1) {
                if (this.m.add(object)) {
                    this.y.add(object);
                }
            }
        }
    }

    @Override
    public synchronized boolean contains(Object object) {
        return this.m.contains(object);
    }

    @Override
    public synchronized Object clone() {
        return new SyncIndexedSet(this);
    }

    public static List getElementList(SyncIndexedSet syncIndexedSet) {
        return syncIndexedSet.y;
    }

    public SyncIndexedSet(int ba) {
        this.m = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba));
        this.y = new ArrayList(ba);
    }

    @Override
    public int size() {
        return this.y.size();
    }

    @Override
    public Iterator iterator() {
        return new ArraySetIterator(this);
    }
}
