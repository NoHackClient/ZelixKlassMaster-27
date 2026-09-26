package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;

import java.util.Comparator;

public class LocalVariableEntryComparator implements Comparator {
    @Override
    public int compare(Object object, Object object1) {
        return this.compareLocalVariablesFirst((LabelTargetHolder) object, (LabelTargetHolder) object1);
    }

    public int compareLocalVariablesFirst(LabelTargetHolder labelTargetHolder, LabelTargetHolder labelTargetHolder1) {
        if (labelTargetHolder instanceof LocalVariableEntry) {
            return labelTargetHolder1 instanceof LocalVariableEntry ? 0 : -1;
        } else {
            return labelTargetHolder1 instanceof LocalVariableEntry ? 1 : 0;
        }
    }
}
