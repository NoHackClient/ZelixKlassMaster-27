package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.util.ZkmUtils;

import java.util.List;
import java.util.Set;

public class UniqueNameGenerator {
    private static int[] flowCounters;
    public final Set usedNames = ZkmUtils.createHashSet();
    public final SequentialNameGenerator defaultGenerator;
    public final SequentialNameGenerator archiveGenerator;

    public static void setFlowCounters(int[] ba) {
        flowCounters = ba;
    }

    public static int[] getFlowCounters() {
        return flowCounters;
    }

    public final String nextUniqueName(String string, boolean bl) {
        SequentialNameGenerator sequentialNameGenerator;
        if (bl) {
            sequentialNameGenerator = this.archiveGenerator;
        } else {
            sequentialNameGenerator = this.defaultGenerator;
        }

        String string1;
        Set set1;
        do {
            if (string != null) {
                string1 = sequentialNameGenerator.nextPrefixedName(string);
                set1 = this.usedNames;
            } else {
                string1 = sequentialNameGenerator.nextName();
                set1 = this.usedNames;
            }
        } while (!set1.add(string1));

        return string1;
    }

    public UniqueNameGenerator(char[] ba, char[] bb, char[] bc, char[] bd, List list1, boolean bl) {
        this.defaultGenerator = new SequentialNameGenerator(ba, bb, list1, bl);
        this.archiveGenerator = new SequentialNameGenerator(bc, bd, list1, bl);
    }

    static {
        if (getFlowCounters() != null) {
            setFlowCounters(new int[5]);
        }
    }
}
