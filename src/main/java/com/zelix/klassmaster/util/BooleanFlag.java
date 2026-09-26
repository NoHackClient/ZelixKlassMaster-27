package com.zelix.klassmaster.util;

public class BooleanFlag {
    public boolean value;

    @Override
    public Object clone() {
        return new BooleanFlag(this.value);
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public BooleanFlag(boolean value) {
        this.value = value;
    }

    public BooleanFlag() {
        this(false);
    }
}
