package com.zelix.klassmaster.util;

import java.io.Serializable;

public class ObjectPair implements Serializable {
    private Object E;
    private Object w;

    public Object setSecond(Object object) {
        Object object1 = this.w;
        this.w = object;
        return object1;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectPair)) {
            return false;
        }

        ObjectPair objectPair1 = (ObjectPair) object;
        return this.E.equals(objectPair1.E)
                && (this.w == null && objectPair1.w == null || this.w != null && objectPair1.w != null && this.w.equals(objectPair1.w));
    }

    public Object setFirst(Object object) {
        if (object == null) {
            throw new IllegalArgumentException(this.getClass().getName());
        }

        Object object1 = this.E;
        this.E = object;
        return object1;
    }

    public Object getFirst() {
        return this.E;
    }

    public Object getSecond() {
        return this.w;
    }

    public ObjectPair(Object object, Object object1) {
        if (object == null) {
            throw new IllegalArgumentException(this.getClass().getName());
        }

        this.E = object;
        this.w = object1;
    }

    @Override
    public int hashCode() {
        return this.w == null ? this.E.hashCode() : this.E.hashCode() ^ this.w.hashCode();
    }
}
