package com.zelix.klassmaster.util;

import com.zelix.klassmaster.classfile.insn.IntCounter;

import java.io.Serializable;

public class MutableInt implements IntCounter, Comparable, Serializable {
    private int Y;

    public int decrementAndGet() {
        this.Y--;
        return this.Y;
    }

    public int compareValue(MutableInt mutableInt1) {
        if (this.Y < mutableInt1.Y) {
            return -1;
        } else {
            return this.Y == mutableInt1.Y ? 0 : 1;
        }
    }

    public void setValue(int ba) {
        this.Y = ba;
    }

    @Override
    public boolean equals(Object object) {
        return object != null && object instanceof MutableInt ? this.Y == ((MutableInt) object).Y : false;
    }

    @Override
    public int getValue() {
        return this.Y;
    }

    @Override
    public int addAndGet(int ba) {
        this.Y += ba;
        return this.Y;
    }

    public MutableInt(int ba) {
        this.Y = ba;
    }

    @Override
    public int getAndIncrement() {
        return this.Y++;
    }

    @Override
    public int hashCode() {
        return this.Y;
    }

    public MutableInt() {
        this(0);
    }

    @Override
    public int incrementAndGet() {
        return ++this.Y;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareValue((MutableInt) object);
    }
}
