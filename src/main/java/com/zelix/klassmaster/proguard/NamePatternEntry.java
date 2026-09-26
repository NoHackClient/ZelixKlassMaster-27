package com.zelix.klassmaster.proguard;

public class NamePatternEntry {
    public String pattern;
    public final boolean negated;
    public final boolean inList;

    public String getPattern() {
        return this.pattern;
    }

    public boolean isNegated() {
        return this.negated;
    }

    public boolean isInList() {
        return this.inList;
    }

    public NamePatternEntry(String string, boolean negated, boolean inList) {
        this.pattern = string;
        this.negated = negated;
        this.inList = inList;
    }

    public void setPattern(String string) {
        this.pattern = string;
    }
}
