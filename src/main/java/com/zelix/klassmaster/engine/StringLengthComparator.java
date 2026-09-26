package com.zelix.klassmaster.engine;

import java.util.Comparator;

public class StringLengthComparator implements Comparator {
    public final ObfuscationEngine engine;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByLength((String) object, (String) object1);
    }

    public int compareByLength(String string, String string1) {
        if (string.length() == string1.length()) {
            return 0;
        } else {
            return string.length() < string1.length() ? -1 : 1;
        }
    }

    public StringLengthComparator(ObfuscationEngine obfuscationEngine) {
        this.engine = obfuscationEngine;
    }
}
