package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;

import java.util.Comparator;

public class MethodNameDescComparator implements Comparator {
    public final StaticInitCalleeAnalyzer staticInitCalleeAnalyzer;

    public int compareByClassThenName(AbstractMethodInfo abstractMethodInfo, AbstractMethodInfo abstractMethodInfo1) {
        int ba = abstractMethodInfo.getClassName().compareTo(abstractMethodInfo1.getClassName());
        return ba != 0 ? ba : abstractMethodInfo.getJvmName().compareTo(abstractMethodInfo1.getJvmName());
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareByClassThenName((AbstractMethodInfo) object, (AbstractMethodInfo) object1);
    }

    public MethodNameDescComparator(StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1) {
        this.staticInitCalleeAnalyzer = staticInitCalleeAnalyzer1;
    }
}
