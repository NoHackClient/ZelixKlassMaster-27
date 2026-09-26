package com.zelix.klassmaster.util;

import java.util.Iterator;

public class UnmodifiableListIterator implements Iterator {
    public final NonNullList list;
    private Iterator delegate;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object next() {
        return this.delegate.next();
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    public UnmodifiableListIterator(NonNullList nonNullList) {
        this.list = nonNullList;
        this.delegate = NonNullList.getDelegate(this.list).iterator();
    }
}
