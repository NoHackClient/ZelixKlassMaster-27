package com.zelix.klassmaster.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayEnumeration implements Enumeration {
    private static final String NULL_COLLECTION_MESSAGE = "Collection cannot be null";
    private int index;
    private Object[] elements;
    private int count;

    public static Enumeration fromCollection(Collection collection1) {
        if (collection1 == null) {
            throw new IllegalArgumentException(NULL_COLLECTION_MESSAGE);
        }

        Object[] objects = new Object[collection1.size()];
        int ba = 0;
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            objects[ba++] = iterator.next();
        }

        return new ArrayEnumeration(objects);
    }

    @Override
    public final Object nextElement() {
        if (this.index >= this.count) {
            throw new NoSuchElementException(this.getClass().getName());
        } else {
            return this.elements[this.index++];
        }
    }

    @Override
    public final boolean hasMoreElements() {
        return this.index < this.count;
    }

    public void sortElements(Comparator comparator1) {
        if (this.index > 0) {
            throw new IllegalStateException();
        }

        this.elements = ZkmUtils.copyArray(this.elements);
        Arrays.sort(this.elements, comparator1);
    }

    public ArrayEnumeration(Object[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException();
        }

        this.elements = objects.clone();
        this.count = this.elements.length;
    }
}
