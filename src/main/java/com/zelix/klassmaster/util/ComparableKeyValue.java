package com.zelix.klassmaster.util;

import java.io.Serializable;

public class ComparableKeyValue implements Comparable, Serializable {
    public Comparable P;
    public Object B;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComparableKeyValue)) {
            return false;
        }

        ComparableKeyValue comparableKeyValue1 = (ComparableKeyValue) object;
        return this.P.equals(comparableKeyValue1.P)
                && (this.B == null && comparableKeyValue1.B == null || this.B != null && comparableKeyValue1.B != null && this.B.equals(comparableKeyValue1.B));
    }

    public Comparable getKey() {
        return this.P;
    }

    public int compareByKey(ComparableKeyValue comparableKeyValue1) {
        return this.P.compareTo(comparableKeyValue1.P);
    }

    public ComparableKeyValue(Comparable comparable, Object object) {
        if (comparable == null) {
            throw new IllegalArgumentException(this.getClass().getName());
        }

        this.P = comparable;
        this.B = object;
    }

    @Override
    public int hashCode() {
        return this.B == null ? this.P.hashCode() : this.P.hashCode() ^ this.B.hashCode();
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByKey((ComparableKeyValue) object);
    }
}
