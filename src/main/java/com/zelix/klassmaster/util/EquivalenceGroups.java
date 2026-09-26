package com.zelix.klassmaster.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class EquivalenceGroups {
    public final Map groupByElement = ZkmUtils.createHashMap();

    public Set getGroups() {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.groupByElement.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            hashSet.add(entry.getValue());
        }

        return hashSet;
    }

    public Set getElements() {
        return Collections.unmodifiableSet(this.groupByElement.keySet());
    }

    public void makeEquivalent(Object object, Object object1) {
        EquivalenceGroup equivalenceGroup = (EquivalenceGroup) this.groupByElement.get(object);
        EquivalenceGroup equivalenceGroup1 = (EquivalenceGroup) this.groupByElement.get(object1);
        if (equivalenceGroup == null && equivalenceGroup1 == null) {
            EquivalenceGroup equivalenceGroup2 = new EquivalenceGroup();
            equivalenceGroup2.add(object);
            equivalenceGroup2.add(object1);
            this.groupByElement.put(object, equivalenceGroup2);
            this.groupByElement.put(object1, equivalenceGroup2);
        } else if (equivalenceGroup != null && equivalenceGroup1 != null) {
            for (Object object2 : equivalenceGroup1) {
                this.groupByElement.remove(object2);
                this.groupByElement.put(object2, equivalenceGroup);
            }

            equivalenceGroup.addAll(equivalenceGroup1);
        } else if (equivalenceGroup != null) {
            equivalenceGroup.add(object1);
            this.groupByElement.put(object1, equivalenceGroup);
        } else {
            equivalenceGroup1.add(object);
            this.groupByElement.put(object, equivalenceGroup1);
        }
    }

    public boolean containsElement(Object object) {
        return this.groupByElement.containsKey(object);
    }

    public Set getEntries() {
        return Collections.unmodifiableSet(this.groupByElement.entrySet());
    }

    public EquivalenceGroup getGroup(Object object) {
        return (EquivalenceGroup) this.groupByElement.get(object);
    }
}
