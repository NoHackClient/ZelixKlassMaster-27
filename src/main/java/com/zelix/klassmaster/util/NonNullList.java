package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NonNullList implements List {
    private final List delegate;
    private static final String NULL_ARGUMENT_MESSAGE = "Null argument not allowed";

    @Override
    public boolean addAll(int ba, Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        return this.delegate.size();
    }

    @Override
    public boolean addAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean containsAll(Collection collection1) {
        return this.delegate.containsAll(collection1);
    }

    @Override
    public boolean retainAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int indexOf(Object object) {
        return this.delegate.indexOf(object);
    }

    @Override
    public void add(int ba, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean contains(Object object) {
        return this.delegate.contains(object);
    }

    @Override
    public Object[] toArray(Object[] objects) {
        return this.delegate.toArray(objects);
    }

    @Override
    public ListIterator listIterator(int ba) {
        return new ReadOnlyListIterator(this, ba);
    }

    @Override
    public ListIterator listIterator() {
        return new ReadOnlyListIterator(this);
    }

    @Override
    public Object remove(int ba) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public final boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public NonNullList(List list1) {
        if (list1 == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }

        this.delegate = list1;
    }

    @Override
    public List subList(int ba, int bb) {
        return new NonNullList(this.delegate.subList(ba, bb));
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public static List getDelegate(NonNullList nonNullList) {
        return nonNullList.delegate;
    }

    @Override
    public Object set(int ba, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        return new UnmodifiableListIterator(this);
    }

    @Override
    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int lastIndexOf(Object object) {
        return this.delegate.lastIndexOf(object);
    }

    @Override
    public Object get(int ba) {
        return this.delegate.get(ba);
    }

    @Override
    public boolean removeAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }
}
