package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.classfile.MemberInfo;

import java.util.Comparator;

public class MemberNameComparator implements Comparator {
    public final TrimProcessor trimProcessor;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByName((MemberInfo) object, (MemberInfo) object1);
    }

    public MemberNameComparator(TrimProcessor trimProcessor1) {
        this.trimProcessor = trimProcessor1;
    }

    public int compareByName(MemberInfo memberInfo1, MemberInfo memberInfo2) {
        String string = memberInfo1.getSourceName();
        String string1 = memberInfo2.getSourceName();
        return string.compareTo(string1);
    }
}
