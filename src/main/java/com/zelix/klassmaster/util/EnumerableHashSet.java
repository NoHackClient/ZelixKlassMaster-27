package com.zelix.klassmaster.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

public class EnumerableHashSet {
    public final HashSet set;

    public EnumerableHashSet(HashSet hashSet) {
        this.set = hashSet;
    }

    public synchronized Enumeration elements() {
        return Collections.enumeration(this.set);
    }

    public Iterator iterator() {
        return this.set.iterator();
    }

    public int size() {
        return this.set.size();
    }

    @Override
    public Object clone() {
        return new EnumerableHashSet(ZkmUtils.copyHashSet(this.set));
    }

    public boolean add(Object object) {
        return this.set.add(object);
    }

    public boolean contains(Object object) {
        return this.set.contains(object);
    }

    public EnumerableHashSet() {
        this.set = ZkmUtils.createHashSet();
    }
}
