package com.zelix.klassmaster.util;

import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MultiMapValueEnumeration implements Enumeration {
    public int index;
    public final ListMultimap multimap;
    public boolean invalidated;
    public final SyncIndexedSet values;

    public MultiMapValueEnumeration(ListMultimap listMultimap, long ba, SyntheticAccessorTag syntheticAccessorTag) {
        this(listMultimap);
    }

    public void reset() {
        if (this.invalidated) {
            throw new ConcurrentModificationException();
        }

        this.index = 0;
    }

    private MultiMapValueEnumeration(ListMultimap listMultimap) {
        this.multimap = listMultimap;
        this.invalidated = false;
        this.values = new SyncIndexedSet(listMultimap.map.size() * 5);
        Iterator iterator = listMultimap.map.values().iterator();

        while (iterator.hasNext()) {
            List list1 = (List) iterator.next();
            this.values.addAll(list1);
        }

        this.reset();
    }

    @Override
    public boolean hasMoreElements() {
        if (this.invalidated) {
            throw new ConcurrentModificationException();
        } else {
            return this.index < this.values.size();
        }
    }

    public void invalidate() {
        this.invalidated = true;
    }

    @Override
    public Object nextElement() {
        if (this.invalidated) {
            throw new ConcurrentModificationException();
        } else {
            return this.values.getElementAt(this.index++);
        }
    }

    public static void invalidateEnumeration(MultiMapValueEnumeration multiMapValueEnumeration) {
        multiMapValueEnumeration.invalidate();
    }

    public static void resetEnumeration(MultiMapValueEnumeration multiMapValueEnumeration) {
        multiMapValueEnumeration.reset();
    }
}
