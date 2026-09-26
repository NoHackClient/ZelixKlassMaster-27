package com.zelix.klassmaster.util;

import java.util.Map;

public class ThreeKeySetMultiMap {
    public final int nestedCapacity = 10;
    public final int innerCapacity = 16;
    public final int valueSetCapacity = 16;
    public Map map = ZkmUtils.createHashMap(75);

    public void clear() {
        this.map.clear();
    }

    public SetMultiMap getSetMultiMap(Object object, Object object1) {
        TwoKeySetMultiMap twoKeySetMultiMap = (TwoKeySetMultiMap) this.map.get(object);
        return twoKeySetMultiMap == null ? null : twoKeySetMultiMap.getSetMultiMap(object1);
    }

    public boolean addValue(Object object, Object object1, Object object2, Object object3) {
        TwoKeySetMultiMap twoKeySetMultiMap = (TwoKeySetMultiMap) this.map.get(object);
        if (twoKeySetMultiMap == null) {
            twoKeySetMultiMap = new TwoKeySetMultiMap(this.nestedCapacity, this.innerCapacity, this.valueSetCapacity);
            boolean bl = twoKeySetMultiMap.addValue(object1, object2, object3);
            this.map.put(object, twoKeySetMultiMap);
            return bl;
        } else {
            return twoKeySetMultiMap.addValue(object1, object2, object3);
        }
    }

    public ThreeKeySetMultiMap(int ba, int bb, int bc, int bd) {
    }

    public ThreeKeySetMultiMap(int ba, int bb) {
        this(75, 10, 16, 16);
    }

    public ThreeKeySetMultiMap() {
        this(75, 10);
    }
}
