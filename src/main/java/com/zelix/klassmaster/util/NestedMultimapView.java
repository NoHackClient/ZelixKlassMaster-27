package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

public class NestedMultimapView {
    public final NestedMultiMap delegate;

    public NestedMultimapView(NestedMultiMap nestedMultiMap) {
        this.delegate = nestedMultiMap;
    }

    public boolean containsKey(Object object) {
        return this.delegate.containsKey(object);
    }

    public ListMultimap copyMultimap(Object object) {
        ListMultimap listMultimap = this.delegate.getMultimap(object);
        return listMultimap != null ? new ListMultimap(listMultimap) : null;
    }

    public ListMultimap flattenToMultimap() {
        ListMultimap listMultimap = new ListMultimap(this.delegate.getKeyCount() * 2);
        Iterator iterator = this.delegate.entrySet().iterator();

        while (iterator.hasNext()) {
            Iterator iterator1 = ((ListMultimap) ((Entry) iterator.next()).getValue()).entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry = (Entry) iterator1.next();
                listMultimap.appendValues(entry.getKey(), (Collection) entry.getValue());
            }
        }

        return listMultimap;
    }
}
