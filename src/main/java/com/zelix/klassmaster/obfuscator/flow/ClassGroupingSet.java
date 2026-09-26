package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.util.NamedSet;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClassGroupingSet implements Serializable, Comparable {
    public NamedSet y = ZkmUtils.createNamedSet();
    public NamedSet J;
    public NamedSet o;

    @Override
    public boolean equals(Object object) {
        if (object instanceof ClassGroupingSet) {
            ClassGroupingSet classGroupingSet1 = (ClassGroupingSet) object;
            return this.y.equals(classGroupingSet1.y);
        } else {
            return false;
        }
    }

    public ClassGroupingSet mergeIfOverlapping(ClassGroupingSet classGroupingSet1) {
        NamedSet namedSet = ZkmUtils.copyNamedSet(this.J);
        namedSet.retainAll(classGroupingSet1.J);
        if (namedSet.size() > 0) {
            NamedSet namedSet1 = ZkmUtils.createNamedSet(this.y);
            namedSet1.addAll(classGroupingSet1.y);
            return new ClassGroupingSet(namedSet1, namedSet);
        } else {
            return null;
        }
    }

    public boolean intersectionContains(Object object) {
        return this.J.contains(object);
    }

    public List getSetNames() {
        ArrayList arrayList = new ArrayList(this.y.size());
        Iterator iterator = this.y.iterator();

        while (iterator.hasNext()) {
            NamedSet namedSet = (NamedSet) iterator.next();
            arrayList.add(namedSet.getSetName());
        }

        return arrayList;
    }

    public ClassGroupingSet(NamedSet namedSet) {
        this.y.add(namedSet);
        this.J = ZkmUtils.createNamedSet(namedSet);
        this.o = ZkmUtils.createNamedSet(namedSet);
    }

    public int getIntersectionSize() {
        return this.J.size();
    }

    public NamedSet getSharedSets(ClassGroupingSet classGroupingSet1) {
        NamedSet namedSet = ZkmUtils.createNamedSet();
        NamedSet namedSet1;
        NamedSet namedSet2;
        if (this.y.size() > classGroupingSet1.y.size()) {
            namedSet1 = classGroupingSet1.y;
            namedSet2 = this.y;
        } else {
            namedSet1 = this.y;
            namedSet2 = classGroupingSet1.y;
        }

        Iterator iterator = namedSet1.iterator();

        while (iterator.hasNext()) {
            NamedSet namedSet3 = (NamedSet) iterator.next();
            if (namedSet2.contains(namedSet3)) {
                namedSet.add(namedSet3);
            }
        }

        return namedSet;
    }

    public boolean unionContains(Object object) {
        return this.o.contains(object);
    }

    public int compareGrouping(ClassGroupingSet classGroupingSet1) {
        if (this.getSetCount() < classGroupingSet1.getSetCount()) {
            return -1;
        }

        if (this.getSetCount() == classGroupingSet1.getSetCount()) {
            if (this.getIntersectionSize() < classGroupingSet1.getIntersectionSize()) {
                return -1;
            } else {
                return this.getIntersectionSize() == classGroupingSet1.getIntersectionSize() ? 0 : 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public int compareTo(Object object) {
        return this.compareGrouping((ClassGroupingSet) object);
    }

    public Set getSets() {
        return Collections.unmodifiableSet(this.y);
    }

    public ClassGroupingSet(NamedSet namedSet, NamedSet namedSet1) {
        this.y.addAll(namedSet);
        this.J = namedSet1;
        this.o = ZkmUtils.createNamedSet();
        Iterator iterator = this.y.iterator();

        while (iterator.hasNext()) {
            NamedSet namedSet2 = (NamedSet) iterator.next();
            this.o.addAll(namedSet2);
        }
    }

    public NamedSet getSetsNotIn(ClassGroupingSet classGroupingSet1) {
        NamedSet namedSet = ZkmUtils.createNamedSet();
        NamedSet namedSet1 = classGroupingSet1.y;
        Iterator iterator = this.y.iterator();

        while (iterator.hasNext()) {
            NamedSet namedSet2 = (NamedSet) iterator.next();
            if (!namedSet1.contains(namedSet2)) {
                namedSet.add(namedSet2);
            }
        }

        return namedSet;
    }

    public int getSetCount() {
        return this.y.size();
    }

    public Set getIntersection() {
        return Collections.unmodifiableSet(this.J);
    }

    @Override
    public int hashCode() {
        return this.y.hashCode();
    }
}
