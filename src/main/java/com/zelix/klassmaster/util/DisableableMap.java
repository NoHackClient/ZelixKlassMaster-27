package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DisableableMap implements Map {
    public boolean disabled;
    public Map map;

    @Override
    public Object put(Object object, Object object1) {
        return this.disabled ? null : this.map.put(object, object1);
    }

    @Override
    public final synchronized Collection values() {
        if (this.disabled) {
            return Collections.emptyList();
        }

        ArrayList arrayList = new ArrayList(this.map.size());
        Iterator iterator = this.map.entrySet().iterator();

        while (iterator.hasNext()) {
            arrayList.add(((Entry) iterator.next()).getValue());
        }

        return arrayList;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @Override
    public Object remove(Object object) {
        return this.disabled ? null : this.map.remove(object);
    }

    @Override
    public final boolean containsKey(Object object) {
        return this.disabled ? false : this.map.containsKey(object);
    }

    @Override
    public final int size() {
        return this.disabled ? 0 : this.map.size();
    }

    public DisableableMap(DisableableMap disableableMap1) {
        this.disabled = disableableMap1.disabled;
        if (!this.disabled) {
            this.map = ZkmUtils.copyToHashMap(disableableMap1);
        }
    }

    @Override
    public Set entrySet() {
        return this.disabled ? Collections.emptySet() : this.map.entrySet();
    }

    @Override
    public final Object get(Object object) {
        return this.disabled ? null : this.map.get(object);
    }

    @Override
    public void clear() {
        if (!this.disabled) {
            this.map.clear();
        }
    }

    @Override
    public final boolean isEmpty() {
        return this.disabled ? true : this.map.isEmpty();
    }

    @Override
    public void putAll(Map map1) {
        if (!this.disabled) {
            this.map.putAll(map1);
        }
    }

    public DisableableMap(boolean disabled) {
        this.disabled = disabled;
        if (!disabled) {
            this.map = ZkmUtils.createHashMap(13);
        }
    }

    @Override
    public Set keySet() {
        return this.disabled ? Collections.emptySet() : this.map.keySet();
    }

    @Override
    public final boolean containsValue(Object object) {
        return this.disabled ? false : this.map.containsValue(object);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
