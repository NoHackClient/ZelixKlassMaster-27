package com.zelix.klassmaster.util;

import java.util.IdentityHashMap;
import java.util.Set;

public class IdentityMapWrapper {
    private final IdentityHashMap map;

    public IdentityMapWrapper() {
        this.map = new IdentityHashMap();
    }

    public Object remove(Object object) {
        return this.map.remove(object);
    }

    public int size() {
        return this.map.size();
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public void clear() {
        this.map.clear();
    }

    public IdentityMapWrapper(int ba) {
        this.map = new IdentityHashMap(ZkmUtils.getPrimeCapacity(ba));
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public Object put(Object object, Object object1) {
        return this.map.put(object, object1);
    }

    public Object get(Object object) {
        return this.map.get(object);
    }
}
