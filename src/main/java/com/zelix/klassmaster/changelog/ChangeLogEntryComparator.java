package com.zelix.klassmaster.changelog;

import java.util.Comparator;

public class ChangeLogEntryComparator implements Comparator {
    public final ChangeLogWriter writer;

    @Override
    public int compare(Object object, Object object1) {
        return this.compareEntries((LabeledTuple) object, (LabeledTuple) object1);
    }

    public int compareEntries(LabeledTuple labeledTuple, LabeledTuple labeledTuple1) {
        String string = labeledTuple.getLabel();
        String string1 = labeledTuple1.getLabel();
        String string2 = string.substring(string.indexOf(" ") + 1);
        String string3 = string1.substring(string1.indexOf(" ") + 1);
        int ba = string2.compareTo(string3);
        return ba == 0 ? string.compareTo(string1) : ba;
    }

    public ChangeLogEntryComparator(ChangeLogWriter changeLogWriter1) {
        this.writer = changeLogWriter1;
    }
}
