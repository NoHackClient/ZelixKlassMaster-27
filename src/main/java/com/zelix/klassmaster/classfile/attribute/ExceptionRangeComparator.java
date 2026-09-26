package com.zelix.klassmaster.classfile.attribute;

import java.util.Comparator;

public class ExceptionRangeComparator implements Comparator {
    @Override
    public int compare(Object object, Object object1) {
        return this.compareRanges((ExceptionTableEntry) object, (ExceptionTableEntry) object1);
    }

    public int compareRanges(ExceptionTableEntry exceptionTableEntry1, ExceptionTableEntry exceptionTableEntry2) {
        int ba = exceptionTableEntry1.getOffset(0) - exceptionTableEntry2.getOffset(0);
        return ba == 0 ? exceptionTableEntry1.getOffset(1) - exceptionTableEntry2.getOffset(1) : ba;
    }
}
