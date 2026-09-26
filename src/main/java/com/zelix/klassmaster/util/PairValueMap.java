package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PairValueMap implements Map {
    public Map map = ZkmUtils.createHashMap();

    @Override
    public Collection values() {
        return this.map.values();
    }

    @Override
    public boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public Object get(Object object) {
        return this.getPair(object);
    }

    @Override
    public Object put(Object object, Object object1) {
        return this.putPairValue(object, (ObjectPair) object1);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Object remove(Object object) {
        return this.removePair(object);
    }

    @Override
    public Set entrySet() {
        return this.map.entrySet();
    }

    public ObjectPair putPair(Object object, Object object1, Object object2) {
        ObjectPair objectPair = new ObjectPair(object1, object2);
        return ((com.zelix.klassmaster.util.ObjectPair) (this.map.put(object, objectPair)));
    }

    @Override
    public Set keySet() {
        return this.map.keySet();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public void putAll(Map map1) {
        this.map.putAll(map1);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.map.containsValue(object);
    }

    public ObjectPair putPairValue(Object object, Object object1) {
        return (ObjectPair) this.map.put(object, object1);
    }

    public ObjectPair removePair(Object object) {
        return (ObjectPair) this.map.remove(object);
    }

    public ObjectPair getPair(Object object) {
        return (ObjectPair) this.map.get(object);
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
