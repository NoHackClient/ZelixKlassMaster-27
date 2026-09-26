package com.zelix.klassmaster.util;

import java.io.Serializable;

public class Quadruple implements Serializable {
    public Object G;
    public Object i;
    public Object m;
    public Object V;

    public Object getThird() {
        return this.m;
    }

    @Override
    public int hashCode() {
        int ba = 0;
        if (this.G != null) {
            ba = 0 ^ this.G.hashCode();
        }

        if (this.i != null) {
            ba ^= this.i.hashCode();
        }

        Object object;
        if (this.m != null) {
            ba ^= this.m.hashCode();
            object = this.V;
        } else {
            object = this.V;
        }

        if (object != null) {
            ba ^= this.V.hashCode();
        }

        return ba;
    }

    public Quadruple(Object object, Object object1, Object object2, Object object3) {
        this.G = object;
        this.i = object1;
        this.m = object2;
        this.V = object3;
    }

    public Object getFirst() {
        return this.G;
    }

    public Object getFourth() {
        return this.V;
    }

    public Object getSecond() {
        return this.i;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Quadruple)) {
            return false;
        }

        Quadruple quadruple1 = (Quadruple) object;
        return (this.G == null && quadruple1.G == null || this.G != null && quadruple1.G != null && this.G.equals(quadruple1.G))
                && (this.i == null && quadruple1.i == null || this.i != null && quadruple1.i != null && this.i.equals(quadruple1.i))
                && (this.m == null && quadruple1.m == null || this.m != null && quadruple1.m != null && this.m.equals(quadruple1.m))
                && (this.V == null && quadruple1.V == null || this.V != null && quadruple1.V != null && this.V.equals(quadruple1.V));
    }
}
