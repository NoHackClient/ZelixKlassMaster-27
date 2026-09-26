package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class EquivalenceGroup implements Set {
    public final Set members = ZkmUtils.createHashSet();

    @Override
    public boolean add(Object object) {
        return this.members.add(object);
    }

    public Set getMembers() {
        return this.members;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else {
            return !(object instanceof EquivalenceGroup) ? false : this.members == ((EquivalenceGroup) object).getMembers();
        }
    }

    @Override
    public void clear() {
        this.members.clear();
    }

    @Override
    public Object[] toArray() {
        return this.members.toArray();
    }

    @Override
    public Object[] toArray(Object[] objects) {
        return this.members.toArray(objects);
    }

    @Override
    public boolean containsAll(Collection collection1) {
        return this.members.containsAll(collection1);
    }

    @Override
    public int size() {
        return this.members.size();
    }

    @Override
    public boolean addAll(Collection collection1) {
        return this.members.addAll(collection1);
    }

    @Override
    public boolean contains(Object object) {
        return this.members.contains(object);
    }

    @Override
    public Iterator iterator() {
        return this.members.iterator();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this.members);
    }

    @Override
    public boolean removeAll(Collection collection1) {
        return this.members.removeAll(collection1);
    }

    @Override
    public boolean retainAll(Collection collection1) {
        return this.members.retainAll(collection1);
    }

    @Override
    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    @Override
    public boolean remove(Object object) {
        return this.members.remove(object);
    }
}
