package com.zelix.klassmaster.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class SnapshotEnumeration implements Enumeration {
    public Enumeration delegate;

    @Override
    public final Object nextElement() {
        return this.delegate.nextElement();
    }

    @Override
    public final boolean hasMoreElements() {
        return this.delegate.hasMoreElements();
    }

    public SnapshotEnumeration(Enumeration enumeration) {
        ArrayList arrayList = new ArrayList();

        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }

        this.delegate = Collections.enumeration(arrayList);
    }
}
