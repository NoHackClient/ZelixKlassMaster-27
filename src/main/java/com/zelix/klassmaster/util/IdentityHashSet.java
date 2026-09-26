package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class IdentityHashSet implements Set {
    private final IdentityMapWrapper map = new IdentityMapWrapper();

    @Override
    public boolean retainAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    public IdentityHashSet() {
    }

    @Override
    public boolean remove(Object object) {
        return this.map.remove(object) != null;
    }

    @Override
    public boolean contains(Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public Iterator iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public Object[] toArray(Object[] objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection collection1) {
        for (Object object : collection1) {
            if (!this.map.containsKey(object)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection collection1) {
        boolean bl = false;

        for (Object object : collection1) {
            if (this.map.put(object, object) == null) {
                bl = true;
            }
        }

        return bl;
    }

    @Override
    public boolean add(Object object) {
        return this.map.put(object, object) == null;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean removeAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    public IdentityHashSet(int ba) {
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
