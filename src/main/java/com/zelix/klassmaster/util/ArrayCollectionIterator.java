package com.zelix.klassmaster.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayCollectionIterator implements Iterator {
    private static final String GREATER_OR_EQUAL = ">=";
    public int index;
    public final ArrayCollection collection;

    @Override
    public boolean hasNext() {
        return this.index < ArrayCollection.getElements(this.collection).length;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public ArrayCollectionIterator(ArrayCollection arrayCollection) {
        this.collection = arrayCollection;
    }

    @Override
    public Object next() {
        if (this.index >= ArrayCollection.getElements(this.collection).length) {
            throw new NoSuchElementException(this.index + GREATER_OR_EQUAL + ArrayCollection.getElements(this.collection).length);
        } else {
            return ArrayCollection.getElements(this.collection)[this.index++];
        }
    }
}
