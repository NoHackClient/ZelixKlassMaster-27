package com.zelix.klassmaster.util;

import java.io.Serializable;

public class MutableLong implements Comparable, Serializable {
    public long f = 0L;

    public long getValue() {
        return this.f;
    }

    public int compareValue(MutableLong mutableLong1) {
        if (this.f < mutableLong1.f) {
            return -1;
        } else {
            return this.f == mutableLong1.f ? 0 : 1;
        }
    }

    @Override
    public boolean equals(Object object) {
        return object != null && object instanceof MutableLong ? this.f == ((MutableLong) object).f : false;
    }

    @Override
    public int hashCode() {
        return (int) this.f;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareValue((MutableLong) object);
    }

    public MutableLong(long ba) {
    }

    public void setValue(long ba) {
        this.f = ba;
    }

    public MutableLong() {
        this(0L);
    }
}
