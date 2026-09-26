package com.zelix.klassmaster.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArraySetIterator implements Iterator {
    private static final String GREATER_OR_EQUAL = ">=";
    public final SyncIndexedSet set;
    public Object[] elements;
    public int index;

    @Override
    public Object next() {
        if (this.index >= this.elements.length) {
            throw new NoSuchElementException(this.index + GREATER_OR_EQUAL + this.elements.length);
        } else {
            return this.elements[this.index++];
        }
    }

    @Override
    public boolean hasNext() {
        return this.index < this.elements.length;
    }

    public ArraySetIterator(SyncIndexedSet syncIndexedSet) {
        this.set = syncIndexedSet;
        this.elements = SyncIndexedSet.getElementList(this.set).toArray(new Object[SyncIndexedSet.getElementList(this.set).size()]);
        this.index = 0;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
