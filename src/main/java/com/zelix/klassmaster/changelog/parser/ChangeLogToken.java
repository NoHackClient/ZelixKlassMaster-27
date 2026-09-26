package com.zelix.klassmaster.changelog.parser;

import com.zelix.klassmaster.util.ZkmAssert;

import java.io.Serializable;

public class ChangeLogToken implements Serializable {
    private static final String b = " NULL >";
    public int B;
    public ChangeLogToken h;
    public int n;
    public int D;
    public ChangeLogToken R;
    public int q;
    public int H;
    public String k;

    public ChangeLogToken(int ba) {
        this.H = ba;
        this.k = null;
    }

    public ChangeLogToken() {
    }

    public static ChangeLogToken newToken(int ba) {
        switch (ba) {
            default:
                return new ChangeLogToken(ba);
        }
    }

    public static ChangeLogToken newToken_sg9(int ba) {
        return newToken(ba);
    }

    @Override
    public String toString() {
        return this.k == null ? "<" + ZkmAssert.getSimpleClassName(this) + b : this.k;
    }
}
