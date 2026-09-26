package com.zelix.klassmaster.util;

public class MutablePair {
    public final Object key;
    public Object value;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MutablePair)) {
            return false;
        }

        MutablePair mutablePair1 = (MutablePair) object;
        return this.key.equals(mutablePair1.key)
                && (this.value == null && mutablePair1.value == null || this.value != null && mutablePair1.value != null && this.value.equals(mutablePair1.value));
    }

    public Object setValue(Object object) {
        Object object1 = this.value;
        this.value = object;
        return object1;
    }

    @Override
    public int hashCode() {
        return this.value == null ? this.key.hashCode() : this.key.hashCode() ^ this.value.hashCode();
    }

    public MutablePair(Object object, Object object1) {
        if (object == null) {
            throw new IllegalArgumentException(this.getClass().getName());
        }

        this.key = object;
        this.value = object1;
    }

    public Object getValue() {
        return this.value;
    }

    public Object getKey() {
        return this.key;
    }

    public MutablePair(Object object) {
        if (object == null) {
            throw new IllegalArgumentException(this.getClass().getName());
        }

        this.key = object;
    }
}
