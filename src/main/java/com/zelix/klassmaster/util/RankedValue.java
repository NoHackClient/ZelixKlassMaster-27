package com.zelix.klassmaster.util;

public class RankedValue implements Comparable {
    private int rank;
    private Object value;

    public int compareByRank(RankedValue rankedValue1) {
        if (this.rank < rankedValue1.rank) {
            return -1;
        } else if (this.rank == rankedValue1.rank) {
            return this.value instanceof Comparable && rankedValue1.value instanceof Comparable
                    ? ((Comparable) this.value).compareTo((Comparable) rankedValue1.value)
                    : 0;
        } else {
            return 1;
        }
    }

    public final Object getValue() {
        return this.value;
    }

    public final int getRank() {
        return this.rank;
    }

    public RankedValue(int rank, Object object) {
        this.rank = rank;
        this.value = object;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByRank((RankedValue) object);
    }
}
