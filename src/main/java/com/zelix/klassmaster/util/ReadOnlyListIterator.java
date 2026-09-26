package com.zelix.klassmaster.util;

import java.util.ListIterator;

public class ReadOnlyListIterator implements ListIterator {
    public final NonNullList list;
    public ListIterator delegate;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public Object next() {
        return this.delegate.next();
    }

    @Override
    public int previousIndex() {
        return this.delegate.previousIndex();
    }

    public ReadOnlyListIterator(NonNullList nonNullList) {
        this.list = nonNullList;
        this.delegate = NonNullList.getDelegate(this.list).listIterator();
        this.delegate = NonNullList.getDelegate(nonNullList).listIterator();
    }

    @Override
    public boolean hasPrevious() {
        return this.delegate.hasPrevious();
    }

    @Override
    public void set(Object object) {
        throw new UnsupportedOperationException();
    }

    public ReadOnlyListIterator(NonNullList nonNullList, int ba) {
        this.list = nonNullList;
        this.delegate = NonNullList.getDelegate(this.list).listIterator();
        this.delegate = NonNullList.getDelegate(nonNullList).listIterator(ba);
    }

    @Override
    public Object previous() {
        return this.delegate.previous();
    }

    @Override
    public int nextIndex() {
        return this.delegate.nextIndex();
    }
}
