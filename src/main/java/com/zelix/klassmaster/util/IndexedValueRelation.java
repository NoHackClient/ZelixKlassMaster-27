package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class IndexedValueRelation implements SetValuedMap {
    public static IntegerCache integerCache = IntegerCache.getInstance();
    public Object[] values;
    public Map valueBitsByKey;
    public Map indexByValue;
    private int valueCount;
    public final boolean ownsValueArray;

    @Override
    public Set keySet() {
        return ZkmUtils.createHashSetFrom(this.valueBitsByKey.keySet());
    }

    @Override
    public void clear() {
        this.valueBitsByKey.clear();
    }

    @Override
    public boolean containsKey(Object object) {
        return this.valueBitsByKey.containsKey(object);
    }

    @Override
    public boolean containsValue(Object object, Object object1) {
        Integer integer = (Integer) this.indexByValue.get(object1);
        if (integer == null) {
            throw new IllegalArgumentException("Value '" + object1 + "' doesn't exist in value array (C)");
        }

        BitSet bitSet = (BitSet) this.valueBitsByKey.get(object);
        return bitSet == null ? false : bitSet.get(integer);
    }

    @Override
    public List getValueList(Object object) {
        BitSet bitSet = (BitSet) this.valueBitsByKey.get(object);
        if (bitSet == null) {
            return null;
        }

        ArrayList arrayList = new ArrayList(bitSet.cardinality());
        this.collectValues(bitSet, arrayList);
        return arrayList;
    }

    public Collection collectValues(BitSet bitSet, Collection collection1) {
        int ba = bitSet.cardinality();
        int bb = 0;

        for (int i = 0; i < this.valueCount; i++) {
            if (bitSet.get(i)) {
                collection1.add(this.values[i]);
                bb++;
            }

            if (bb >= ba) {
                break;
            }
        }

        return collection1;
    }

    @Override
    public boolean removeValue(Object object, Object object1) {
        Integer integer = (Integer) this.indexByValue.get(object1);
        if (integer == null) {
            throw new IllegalArgumentException("Value '" + object1 + "' doesn't exist in value array (B)");
        }

        BitSet bitSet = (BitSet) this.valueBitsByKey.get(object);
        if (bitSet == null) {
            return false;
        }

        int ba = integer;
        boolean bl = bitSet.get(ba);
        bitSet.clear(ba);
        if (bitSet.cardinality() == 0) {
            this.valueBitsByKey.remove(object);
        }

        return bl;
    }

    @Override
    public Set removeKey(Object object) {
        BitSet bitSet = (BitSet) this.valueBitsByKey.remove(object);
        if (bitSet == null) {
            return null;
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(bitSet.cardinality()));
        this.collectValues(bitSet, hashSet);
        return hashSet;
    }

    public IndexedValueRelation(IndexedValueRelation indexedValueRelation1, int ba) {
        this(indexedValueRelation1.values, ba, false);
        this.mergeFrom(indexedValueRelation1);
    }

    @Override
    public void retainValues(Object[] objects) {
        int ba = objects.length;
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));

        for (int i = 0; i < ba; i++) {
            Object object = objects[i];
            hashMap.put(object, integerCache.valueOf(i));
        }

        HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.valueBitsByKey.size()));
        int bd = this.values.length;
        Iterator iterator = this.valueBitsByKey.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object1 = entry.getKey();
            BitSet bitSet = (BitSet) entry.getValue();
            BitSet bitSet1 = new BitSet(objects.length);
            hashMap1.put(object1, bitSet1);

            for (int i = 0; i < bd; i++) {
                if (bitSet.get(i)) {
                    Object object2 = this.values[i];
                    Integer integer = (Integer) hashMap.get(object2);
                    if (integer != null) {
                        bitSet1.set(integer);
                    }
                }
            }
        }

        if (this.ownsValueArray) {
            this.values = new Object[ba];
            System.arraycopy(objects, 0, this.values, 0, ba);
            this.valueBitsByKey = hashMap1;
        } else {
            this.values = objects;
            this.valueBitsByKey = hashMap1;
        }

        this.indexByValue = hashMap;
        this.valueCount = this.values.length;
    }

    @Override
    public int getKeyCount() {
        return this.valueBitsByKey.size();
    }

    public void mergeFrom(IndexedValueRelation indexedValueRelation1) {
        if (indexedValueRelation1.values != this.values) {
            throw new IllegalArgumentException(
                    "Must be based upon the exact same array. : " + System.identityHashCode(this.values) + "!=" + System.identityHashCode(indexedValueRelation1.values)
            );
        }

        Iterator iterator = indexedValueRelation1.valueBitsByKey.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();
            BitSet bitSet = (BitSet) entry.getValue();
            BitSet bitSet1 = (BitSet) this.valueBitsByKey.get(object);
            if (bitSet1 == null) {
                this.valueBitsByKey.put(object, (BitSet) bitSet.clone());
            } else {
                bitSet1.or(bitSet);
            }
        }
    }

    @Override
    public Set getValues(Object object) {
        BitSet bitSet = (BitSet) this.valueBitsByKey.get(object);
        if (bitSet == null) {
            return null;
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(bitSet.cardinality()));
        this.collectValues(bitSet, hashSet);
        return hashSet;
    }

    public IndexedValueRelation(Object[] objects, int ba) {
        this(objects, ba, true);
    }

    @Override
    public boolean isEmpty() {
        return this.valueBitsByKey.isEmpty();
    }

    @Override
    public Enumeration keys() {
        return Collections.enumeration(this.valueBitsByKey.keySet());
    }

    public IndexedValueRelation(Object[] objects, int ba, boolean ownsValueArray) {
        if (ownsValueArray) {
            this.values = new Object[objects.length];
            System.arraycopy(objects, 0, this.values, 0, objects.length);
        } else {
            this.values = objects;
        }

        this.valueBitsByKey = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        this.indexByValue = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.values.length));
        this.valueCount = this.values.length;

        for (int i = 0; i < this.valueCount; i++) {
            this.indexByValue.put(this.values[i], integerCache.valueOf(i));
        }

        this.ownsValueArray = ownsValueArray;
    }

    @Override
    public boolean addValue(Object object, Object object1) {
        Integer integer = (Integer) this.indexByValue.get(object1);
        if (integer == null) {
            throw new IllegalArgumentException("Value '" + object1 + "' doesn't exist in value array (A)");
        }

        BitSet bitSet = (BitSet) this.valueBitsByKey.get(object);
        if (bitSet == null) {
            bitSet = new BitSet(this.valueCount);
            this.valueBitsByKey.put(object, bitSet);
        }

        int ba = integer;
        boolean bl = bitSet.get(ba);
        bitSet.set(ba);
        return bl;
    }
}
