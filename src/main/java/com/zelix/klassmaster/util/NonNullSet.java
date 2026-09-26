package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class NonNullSet implements Set {
    private Set delegate;
    private static final String NULL_ARGUMENT_MESSAGE = "Null argument not allowed";

    @Override
    public boolean retainAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        return new ReadOnlySetIterator(this);
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public boolean removeAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public static Set getDelegate(NonNullSet nonNullSet) {
        return nonNullSet.delegate;
    }

    public NonNullSet(Set set1) {
        if (set1 == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }

        this.delegate = set1;
    }

    @Override
    public final boolean containsAll(Collection collection1) {
        return this.delegate.containsAll(collection1);
    }

    @Override
    public boolean addAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        return this.delegate.size();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean contains(Object object) {
        return this.delegate.contains(object);
    }

    @Override
    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray(Object[] objects) {
        return this.delegate.toArray(objects);
    }

    @Override
    public final boolean isEmpty() {
        return this.delegate.isEmpty();
    }
}
