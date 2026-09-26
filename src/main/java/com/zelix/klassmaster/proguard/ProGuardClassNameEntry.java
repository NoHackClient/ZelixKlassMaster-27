package com.zelix.klassmaster.proguard;

public class ProGuardClassNameEntry {
    public final String className;
    public final boolean negated;
    private final boolean inList;

    public String getClassName() {
        return this.className;
    }

    public ProGuardClassNameEntry(String string, boolean negated, boolean inList) {
        this.className = string;
        this.negated = negated;
        this.inList = inList;
    }

    public boolean isNegated() {
        return this.negated;
    }
}
