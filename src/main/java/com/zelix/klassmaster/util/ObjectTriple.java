package com.zelix.klassmaster.util;

public class ObjectTriple {
    public Object first;
    public Object second;
    public Object third;

    public Object getSecond() {
        return this.second;
    }

    @Override
    public int hashCode() {
        int ba = 0;
        if (this.first != null) {
            ba = 0 ^ this.first.hashCode();
        }

        Object object;
        if (this.second != null) {
            ba ^= this.second.hashCode();
            object = this.third;
        } else {
            object = this.third;
        }

        if (object != null) {
            ba ^= this.third.hashCode();
        }

        return ba;
    }

    public Object getThird() {
        return this.third;
    }

    public Object setAt(Object object, int ba) {
        Object object1;
        switch (ba) {
            case 0:
                object1 = this.first;
                this.first = object;
                break;
            case 1:
                object1 = this.second;
                this.second = object;
                break;
            case 2:
                object1 = this.third;
                this.third = object;
                break;
            default:
                throw new IllegalArgumentException("Index must be between 0 and 1 inclusive : " + ba);
        }

        return object1;
    }

    public Object setSecond(Object object) {
        Object object1 = this.second;
        this.second = object;
        return object1;
    }

    public Object getAt(int ba) {
        switch (ba) {
            case 0:
                return this.first;
            case 1:
                return this.second;
            case 2:
                return this.third;
            default:
                throw new IllegalArgumentException("Index must be between 0 and 1 inclusive : " + ba);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectTriple)) {
            return false;
        }

        ObjectTriple objectTriple1 = (ObjectTriple) object;
        return (this.first == null && objectTriple1.first == null || this.first != null && objectTriple1.first != null && this.first.equals(objectTriple1.first))
                && (
                this.second == null && objectTriple1.second == null
                        || this.second != null && objectTriple1.second != null && this.second.equals(objectTriple1.second)
        )
                && (this.third == null && objectTriple1.third == null || this.third != null && objectTriple1.third != null && this.third.equals(objectTriple1.third));
    }

    public Object setThird(Object object) {
        Object object1 = this.third;
        this.third = object;
        return object1;
    }

    public Object getFirst() {
        return this.first;
    }

    public ObjectTriple(Object object, Object object1, Object object2) {
        this.first = object;
        this.second = object1;
        this.third = object2;
    }
}
