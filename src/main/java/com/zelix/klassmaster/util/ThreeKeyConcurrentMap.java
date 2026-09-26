package com.zelix.klassmaster.util;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreeKeyConcurrentMap {
    public int outerCapacity;
    public int innerCapacity;
    public ConcurrentHashMap map;

    public TwoKeyMap getTwoKeyMap(Object object) {
        return (TwoKeyMap) this.map.get(object);
    }

    public boolean containsKeys(Object object, Object object1, Object object2) {
        TwoKeyMap twoKeyMap = (TwoKeyMap) this.map.get(object);
        return twoKeyMap == null ? false : twoKeyMap.containsKeys(object1, object2);
    }

    public ThreeKeyConcurrentMap(int ba, int outerCapacity, int bc) {
        this.outerCapacity = outerCapacity;
        this.innerCapacity = 5;
        this.map = new ConcurrentHashMap(ZkmUtils.getPrimeCapacity(ba));
    }

    public TwoKeyMap removeTwoKeyMap(Object object) {
        return (TwoKeyMap) this.map.remove(object);
    }

    public Object getValue(Object object, Object object1, Object object2) {
        TwoKeyMap twoKeyMap = (TwoKeyMap) this.map.get(object);
        if (twoKeyMap == null) {
            return null;
        }

        Map map1 = twoKeyMap.getInnerMap(object1);
        return map1 == null ? null : map1.get(object2);
    }

    public Object removeValue(Object object, Object object1, Object object2) {
        TwoKeyMap twoKeyMap = (TwoKeyMap) this.map.get(object);
        if (twoKeyMap != null) {
            Object object3 = twoKeyMap.removeValue(object1, object2);
            if (twoKeyMap.getKeyCount() == 0) {
                this.map.remove(object);
            }

            return object3;
        } else {
            return null;
        }
    }

    public Enumeration keys() {
        return this.map.keys();
    }

    public ThreeKeyConcurrentMap(int ba) {
        this(ba, 10, 5);
    }

    public boolean containsKeyPair(Object object, Object object1) {
        TwoKeyMap twoKeyMap = (TwoKeyMap) this.map.get(object);
        return twoKeyMap == null ? false : twoKeyMap.containsKey(object1);
    }

    public ThreeKeyConcurrentMap(int ba, int bb) {
        this(ba, 5, 5);
    }

    public Map getInnerMap(Object object, Object object1) {
        TwoKeyMap twoKeyMap = (TwoKeyMap) this.map.get(object);
        return twoKeyMap == null ? null : twoKeyMap.getInnerMap(object1);
    }

    public Object putValue(Object object, Object object1, Object object2, Object object3) {
        TwoKeyMap twoKeyMap = (TwoKeyMap) this.map.get(object);
        if (twoKeyMap == null) {
            twoKeyMap = new TwoKeyMap(this.outerCapacity, this.innerCapacity);
            Object object4 = twoKeyMap.putValue(object1, object2, object3);
            this.map.put(object, twoKeyMap);
            return object4;
        } else {
            return twoKeyMap.putValue(object1, object2, object3);
        }
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
