package com.zelix.klassmaster.util;

public class WeightedObject implements Comparable {
    public float weight;
    public Object value;

    public int compareByWeight(WeightedObject weightedObject1) {
        return Float.compare(this.weight, weightedObject1.weight);
    }

    public WeightedObject(float weight, Object object) {
        this.weight = weight;
        this.value = object;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByWeight((WeightedObject) object);
    }
}
