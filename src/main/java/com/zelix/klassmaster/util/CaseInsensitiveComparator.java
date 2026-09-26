package com.zelix.klassmaster.util;

import java.util.Comparator;

public class CaseInsensitiveComparator implements Comparator {
    public static final CaseInsensitiveComparator INSTANCE = new CaseInsensitiveComparator();

    private CaseInsensitiveComparator() {
    }

    public int compareStrings(String string, String string1) {
        return string.compareToIgnoreCase(string1);
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareStrings((String) object, (String) object1);
    }

    public static CaseInsensitiveComparator getInstance() {
        return INSTANCE;
    }
}
