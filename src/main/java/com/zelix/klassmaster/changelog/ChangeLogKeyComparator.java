package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.util.ObjectPair;

import java.util.Comparator;

public class ChangeLogKeyComparator implements Comparator {
    public final ChangeLogWriter writer;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareFlowEntries((ObjectPair) object, (ObjectPair) object1);
    }

    public int compareFlowEntries(ObjectPair objectPair, ObjectPair objectPair1) {
        String string = ((OpaquePredicateField) objectPair.getSecond()).getOwnerClassName();
        String string1 = ((OpaquePredicateField) objectPair1.getSecond()).getOwnerClassName();
        int ba = string.compareTo(string1);
        if (ba == 0) {
            String string2 = (String) objectPair.getFirst();
            String string3 = (String) objectPair1.getFirst();
            return string2.compareTo(string3);
        } else {
            return ba;
        }
    }

    public ChangeLogKeyComparator(ChangeLogWriter changeLogWriter1) {
        this.writer = changeLogWriter1;
    }
}
