package com.zelix.klassmaster.obfuscator.trim;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;

import java.util.Comparator;
import java.util.Map.Entry;

public class ClassReasonComparator implements Comparator {
    public final TrimProcessor trimProcessor;

    public ClassReasonComparator(TrimProcessor trimProcessor1) {
        this.trimProcessor = trimProcessor1;
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareEntries((Entry) object, (Entry) object1);
    }

    public int compareEntries(Entry entry, Entry entry1) {
        long ba = 37711697487566L;
        ba = 53698748699779L ^ ba;
        long bb = ba ^ 76521707240316L;
        int bc = ((String) entry.getValue()).compareTo((String) entry1.getValue());
        if (bc == 0) {
            ProgramClass programClass1 = (ProgramClass) entry.getKey();
            ClassFileBase classFileBase = (ClassFileBase) entry1.getKey();
            return programClass1.compareByName(classFileBase);
        } else {
            return bc;
        }
    }
}
