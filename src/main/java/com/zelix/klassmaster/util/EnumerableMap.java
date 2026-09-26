package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

public class EnumerableMap implements Map {
    public Map map;

    @Override
    public Object remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Object get(Object object) {
        return this.map.get(object);
    }

    @Override
    public final int size() {
        return this.map.size();
    }

    @Override
    public void putAll(Map map1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set keySet() {
        return new NonNullSet(this.map.keySet());
    }

    @Override
    public Object put(Object object, Object object1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public EnumerableMap() {
        this.map = ZkmUtils.createHashMap(13);
    }

    @Override
    public final boolean containsValue(Object object) {
        return this.map.containsValue(object);
    }

    @Override
    public Set entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    @Override
    public final boolean isEmpty() {
        return this.map.isEmpty();
    }

    public final Map copyMap() {
        return ZkmUtils.copyToHashMap(this.map);
    }

    @Override
    public final synchronized Collection values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    public EnumerableMap(Map map1) {
        if (map1 == null) {
            this.map = ZkmUtils.createHashMap(13);
        } else {
            this.map = map1;
        }
    }

    public final synchronized Enumeration keys() {
        return Collections.enumeration(this.map.keySet());
    }
}
