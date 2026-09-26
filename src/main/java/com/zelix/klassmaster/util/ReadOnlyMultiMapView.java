package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class ReadOnlyMultiMapView implements MultiMap {
    public final ListMultimap delegate;

    @Override
    public Enumeration allValues() {
        return this.delegate.allValues();
    }

    @Override
    public synchronized Enumeration keys() {
        return this.delegate.keys();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public ReadOnlyMultiMapView(ListMultimap listMultimap) {
        this.delegate = listMultimap;
    }

    @Override
    public int getKeyCount() {
        return this.delegate.getKeyCount();
    }

    @Override
    public List getValues(Object object) {
        List list1 = this.delegate.getValues(object);
        return list1 != null ? new ArrayList(list1) : null;
    }

    @Override
    public int getValueCount() {
        return this.delegate.getValueCount();
    }

    @Override
    public boolean removeValue(Object object, Object object1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object object) {
        return this.delegate.containsKey(object);
    }

    @Override
    public synchronized Set keySet() {
        return ZkmUtils.createHashSetFrom(this.delegate.keySet());
    }

    @Override
    public void addValue(Object object, Object object1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set entrySet() {
        return ZkmUtils.createHashSetFrom(this.delegate.entrySet());
    }

    @Override
    public boolean containsValue(Object object, Object object1) {
        return this.delegate.containsValue(object, object1);
    }

    @Override
    public List removeKey(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration valuesOf(Object object) {
        return this.delegate.valuesOf(object);
    }
}
