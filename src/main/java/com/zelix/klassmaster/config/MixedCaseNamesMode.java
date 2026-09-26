package com.zelix.klassmaster.config;

import java.io.Serializable;

public class MixedCaseNamesMode implements Serializable {
    public static final MixedCaseNamesMode e = new MixedCaseNamesMode(0);
    public static final MixedCaseNamesMode a = new MixedCaseNamesMode(1);
    public static final MixedCaseNamesMode d = new MixedCaseNamesMode(2);
    public static final MixedCaseNamesMode[] b = new MixedCaseNamesMode[]{e, a, d};
    public final int c;

    public int getValue() {
        return this.c;
    }

    public static MixedCaseNamesMode fromValue(int ba) {
        if (ba >= 0 && ba < b.length) {
            return b[ba];
        } else {
            throw new IllegalArgumentException();
        }
    }

    private MixedCaseNamesMode(int ba) {
        this.c = ba;
    }

    @Override
    public String toString() {
        switch (this.c) {
            case 0:
                return "ALWAYS";
            case 1:
                return "NEVER";
            case 2:
                return "IF_IN_ARCHIVE";
            default:
                return "ERROR";
        }
    }
}
