package com.zelix.klassmaster.util;

import java.util.Iterator;

public class ReadOnlySetIterator implements Iterator {
    public final NonNullSet set;
    private Iterator delegate;

    @Override
    public Object next() {
        return this.delegate.next();
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public ReadOnlySetIterator(NonNullSet nonNullSet) {
        this.set = nonNullSet;
        this.delegate = NonNullSet.getDelegate(this.set).iterator();
    }
}
