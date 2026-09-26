package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.MethodInfo;

import java.util.Comparator;

public class MethodNameComparator implements Comparator {
    public final MethodParameterChangeSet changeSet;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareMethods((MethodInfo) object, (MethodInfo) object1);
    }

    public MethodNameComparator(MethodParameterChangeSet methodParameterChangeSet) {
        this.changeSet = methodParameterChangeSet;
    }

    public int compareMethods(MethodInfo methodInfo1, MethodInfo methodInfo2) {
        int ba = methodInfo1.getOriginalMemberName().toLowerCase().compareTo(methodInfo2.getOriginalMemberName().toLowerCase());
        return ba == 0 ? methodInfo1.getOriginalDescriptor().toLowerCase().compareTo(methodInfo2.getOriginalDescriptor().toLowerCase()) : ba;
    }
}
