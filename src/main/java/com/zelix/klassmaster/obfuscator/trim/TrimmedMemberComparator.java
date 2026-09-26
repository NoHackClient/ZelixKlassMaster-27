package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MemberInfo;

import java.util.Comparator;
import java.util.Map.Entry;

public class TrimmedMemberComparator implements Comparator {
    public final TrimProcessor trimProcessor;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareEntries((Entry) object, (Entry) object1);
    }

    public TrimmedMemberComparator(TrimProcessor trimProcessor1) {
        this.trimProcessor = trimProcessor1;
    }

    public int compareEntries(Entry entry, Entry entry1) {
        long ba = 34098694697362L;
        ba = 118424667771482L ^ ba;
        long bb = ba ^ 39262501989113L;
        int bc = ((String) entry.getValue()).compareTo((String) entry1.getValue());
        if (bc == 0) {
            MemberInfo memberInfo1 = (MemberInfo) entry.getKey();
            MemberInfo memberInfo2 = (MemberInfo) entry1.getKey();
            ClassFileBase classFileBase1 = memberInfo1.getOwningClass();
            ClassFileBase classFileBase = memberInfo2.getOwningClass();
            int bd = classFileBase1.compareByName(classFileBase);
            return bd == 0 ? memberInfo1.compareByLowerCaseName(memberInfo2) : bd;
        } else {
            return bc;
        }
    }
}
