package com.zelix.klassmaster.util;

import java.io.Serializable;

public class IntPair implements Serializable {
    public int a;
    public int b;

    public int getSecond() {
        return this.b;
    }

    public int getFirst() {
        return this.a;
    }

    public IntPair(int ba, int bb) {
        this.a = ba;
        this.b = bb;
    }
}
