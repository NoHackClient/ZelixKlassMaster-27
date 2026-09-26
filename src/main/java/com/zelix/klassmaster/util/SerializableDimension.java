package com.zelix.klassmaster.util;

import java.io.Serializable;

public class SerializableDimension implements Serializable {
    public int a;
    public int b;

    public int getHeight() {
        return this.b;
    }

    public int getWidth() {
        return this.a;
    }

    public SerializableDimension(int ba, int bb) {
        this.a = ba;
        this.b = bb;
    }
}
