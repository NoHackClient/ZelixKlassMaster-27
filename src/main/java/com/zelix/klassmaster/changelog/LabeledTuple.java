package com.zelix.klassmaster.changelog;

public class LabeledTuple implements Comparable {
    private String label;
    public Object first;
    public Object second;
    public Object third;

    public LabeledTuple(String string, Object object, Object object1, Object object2) {
        this.label = string;
        this.first = object;
        this.second = object1;
        this.third = object2;
    }

    public Object getFirst() {
        return this.first;
    }

    public int compareLabels(LabeledTuple labeledTuple1) {
        return this.label.compareTo(labeledTuple1.label);
    }

    public String getLabel() {
        return this.label;
    }

    public LabeledTuple(String string, Object object) {
        this(string, object, null, null);
    }

    @Override
    public int compareTo(Object object) {
        return this.compareLabels((LabeledTuple) object);
    }

    public LabeledTuple(String string, Object object, Object object1) {
        this(string, object, object1, null);
    }

    public Object getSecond() {
        return this.second;
    }

    public Object getThird() {
        return this.third;
    }
}
