package com.zelix.klassmaster.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

public class ArrayCollection implements Collection {
    public Object[] elements;

    @Override
    public boolean retainAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        int ba = 0;
        int bb = 0;

        for (Object[] objects = this.elements; bb < objects.length; objects = this.elements) {
            if (this.elements[ba].equals(object)) {
                return true;
            }

            bb = ++ba;
        }

        return false;
    }

    @Override
    public boolean addAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return this.elements.length == 0;
    }

    @Override
    public boolean containsAll(Collection collection1) {
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            if (!this.contains(iterator.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator iterator() {
        return new ArrayCollectionIterator(this);
    }

    @Override
    public Object[] toArray() {
        return ZkmUtils.copyArray(this.elements);
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.elements.length;
    }

    @Override
    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    public static Object[] getElements(ArrayCollection arrayCollection) {
        return arrayCollection.elements;
    }

    public ArrayCollection(Object[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException();
        }

        this.elements = objects;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray(Object[] objects) {
        if (objects.length < this.elements.length) {
            objects = (Object[]) Array.newInstance(objects.getClass().getComponentType(), this.elements.length);
        }

        System.arraycopy(this.elements, 0, objects, 0, this.elements.length);
        if (objects.length > this.elements.length) {
            objects[this.elements.length] = null;
        }

        return objects;
    }
}
