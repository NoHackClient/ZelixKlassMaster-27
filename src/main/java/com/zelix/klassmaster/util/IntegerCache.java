package com.zelix.klassmaster.util;

public class IntegerCache {
    private static volatile IntegerCache instance;
    public static final Integer MINUS_ONE = -1;
    private Integer[] cache = new Integer[1000];
    private int capacity = 1000;

    public Integer valueOf(int ba) {
        if (ba == -1) {
            return MINUS_ONE;
        }

        if (ba > 32767) {
            return ba;
        }

        synchronized (this) {
            if (ba >= this.capacity) {
                int bb = ba * 2;
                Integer[] integers = new Integer[bb];
                System.arraycopy(this.cache, 0, integers, 0, this.capacity);
                this.cache = integers;
                this.capacity = bb;
            }

            Integer integer = this.cache[ba];
            if (integer == null) {
                integer = ba;
                this.cache[ba] = integer;
            }

            return integer;
        }
    }

    private IntegerCache() {
    }

    public static IntegerCache getInstance() {
        if (instance == null) {
            instance = new IntegerCache();
        }

        return instance;
    }
}
