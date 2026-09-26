package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

public class NamedLinkedHashSet extends LinkedHashSet implements NamedSet {
    public String name;

    public NamedLinkedHashSet() {
    }

    public NamedLinkedHashSet(String string, Map map1) {
        this(string, map1.keySet());
    }

    public NamedLinkedHashSet(Collection collection1) {
        super(collection1);
    }

    public NamedLinkedHashSet(String string, Collection collection1) {
        super(collection1);
        this.name = string;
    }

    @Override
    public String getSetName() {
        return this.name;
    }

    @Override
    public Object clone() {
        return super.clone();
    }
}
