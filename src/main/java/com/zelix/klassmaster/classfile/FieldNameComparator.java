package com.zelix.klassmaster.classfile;

import java.util.Comparator;

public class FieldNameComparator implements Comparator {
    @Override
    public int compare(Object object, Object object1) {
        return this.compareByName((FieldInfo) object, (FieldInfo) object1);
    }

    public int compareByName(FieldInfo fieldInfo, FieldInfo fieldInfo1) {
        String string = fieldInfo.getSourceName();
        String string1 = fieldInfo1.getSourceName();
        return string.compareTo(string1);
    }
}
