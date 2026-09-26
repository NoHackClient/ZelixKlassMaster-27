package com.zelix.klassmaster.util;

import java.util.Map.Entry;

public class SimpleMapEntry implements Entry {
    public Object key;
    public Object value;

    @Override
    public Object getValue() {
        return this.value;
    }

    public SimpleMapEntry(ObjectPair objectPair) {
        this.key = objectPair.getFirst();
        this.value = objectPair.getSecond();
    }

    @Override
    public int hashCode() {
        return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SimpleMapEntry) {
            SimpleMapEntry simpleMapEntry1 = (SimpleMapEntry) object;
            return this.key == null
                    ? simpleMapEntry1.key == null
                    : this.key.equals(simpleMapEntry1.key) && (this.value == null ? simpleMapEntry1.value == null : this.value.equals(simpleMapEntry1.value));
        } else {
            return false;
        }
    }

    public SimpleMapEntry(Object object, Object object1) {
        this.key = object;
        this.value = object1;
    }

    @Override
    public Object setValue(Object object) {
        throw new UnsupportedOperationException();
    }
}
