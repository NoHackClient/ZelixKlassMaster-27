package com.zelix.klassmaster.classfile;

import java.util.Comparator;

public class MethodSignatureComparator implements Comparator {
    @Override
    public int compare(Object object, Object object1) {
        return this.compareBySignature((MethodInfo) object, (MethodInfo) object1);
    }

    public int compareBySignature(MethodInfo methodInfo1, MethodInfo methodInfo2) {
        String string = methodInfo1.getJvmName() + methodInfo1.getDescriptor();
        String string1 = methodInfo2.getJvmName() + methodInfo2.getDescriptor();
        return string.compareTo(string1);
    }
}
