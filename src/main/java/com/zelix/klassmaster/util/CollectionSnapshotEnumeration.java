package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

public class CollectionSnapshotEnumeration implements Enumeration {
    public Enumeration delegate;

    @Override
    public final Object nextElement() {
        return this.delegate.nextElement();
    }

    @Override
    public final boolean hasMoreElements() {
        return this.delegate.hasMoreElements();
    }

    public CollectionSnapshotEnumeration(Collection collection1) {
        ArrayList arrayList = new ArrayList(collection1);
        this.delegate = Collections.enumeration(arrayList);
    }
}
