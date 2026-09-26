package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class EnumerationBackedList implements List {
    private ArrayList elements = new ArrayList();

    @Override
    public Iterator iterator() {
        return this.elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.elements.toArray();
    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        return this.elements.equals(object);
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public Object[] toArray(Object[] objects) {
        return this.elements.toArray(objects);
    }

    @Override
    public boolean retainAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object set(int ba, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List subList(int ba, int bb) {
        return this.elements.subList(ba, bb);
    }

    @Override
    public int indexOf(Object object) {
        return this.elements.indexOf(object);
    }

    @Override
    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(int ba) {
        return this.elements.get(ba);
    }

    @Override
    public boolean removeAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        return this.elements.contains(object);
    }

    @Override
    public boolean addAll(int ba, Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }

    @Override
    public int lastIndexOf(Object object) {
        return this.elements.lastIndexOf(object);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection collection1) {
        return this.elements.containsAll(collection1);
    }

    @Override
    public ListIterator listIterator() {
        return this.elements.listIterator();
    }

    @Override
    public boolean addAll(Collection collection1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator listIterator(int ba) {
        return this.elements.listIterator(ba);
    }

    @Override
    public Object remove(int ba) {
        throw new UnsupportedOperationException();
    }

    public EnumerationBackedList(Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            this.elements.add(enumeration.nextElement());
        }
    }

    @Override
    public void add(int ba, Object object) {
        throw new UnsupportedOperationException();
    }
}
