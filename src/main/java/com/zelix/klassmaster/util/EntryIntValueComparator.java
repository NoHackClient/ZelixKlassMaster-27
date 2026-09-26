package com.zelix.klassmaster.util;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;

import java.util.Comparator;

public class EntryIntValueComparator implements Comparator {
    public final ClassRepository classRepository;

    public EntryIntValueComparator(ClassRepository classRepository1) {
        this.classRepository = classRepository1;
    }

    public int compareIntValues(ObjectPair objectPair, ObjectPair objectPair1) {
        return (Integer) objectPair.getSecond() - (Integer) objectPair1.getSecond();
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareIntValues((ObjectPair) object, (ObjectPair) object1);
    }
}
