package com.zelix.klassmaster.util;

import java.io.Serializable;

public class Triple implements Serializable {
    public Object N;
    public Object n;
    public Object z;

    public Object getFirst() {
        return this.N;
    }

    @Override
    public int hashCode() {
        int ba = 0;
        if (this.N != null) {
            ba = 0 ^ this.N.hashCode();
        }

        Object object;
        if (this.n != null) {
            ba ^= this.n.hashCode();
            object = this.z;
        } else {
            object = this.z;
        }

        if (object != null) {
            ba ^= this.z.hashCode();
        }

        return ba;
    }

    public Triple(Object object, Object object1, Object object2) {
        this.N = object;
        this.n = object1;
        this.z = object2;
    }

    public Object getThird() {
        return this.z;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Triple)) {
            return false;
        }

        Triple triple1 = (Triple) object;
        return (this.N == null && triple1.N == null || this.N != null && triple1.N != null && this.N.equals(triple1.N))
                && (this.n == null && triple1.n == null || this.n != null && triple1.n != null && this.n.equals(triple1.n))
                && (this.z == null && triple1.z == null || this.z != null && triple1.z != null && this.z.equals(triple1.z));
    }

    public Object getSecond() {
        return this.n;
    }
}
