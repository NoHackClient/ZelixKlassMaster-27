package com.zelix.klassmaster.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class NamedHashSet extends HashSet implements NamedSet {
    public String name;

    @Override
    public String getSetName() {
        return this.name;
    }

    public NamedHashSet(String string, Collection collection1) {
        super(collection1);
        this.name = string;
    }

    public NamedHashSet(String string, Map map1) {
        this(string, map1.keySet());
    }

    public NamedHashSet() {
    }

    public NamedHashSet(Collection collection1) {
        super(collection1);
    }

    @Override
    public Object clone() {
        return super.clone();
    }
}
