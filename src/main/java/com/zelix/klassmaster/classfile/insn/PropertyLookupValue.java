package com.zelix.klassmaster.classfile.insn;

import java.util.HashSet;
import java.util.Iterator;

public class PropertyLookupValue implements TrackedValue {
    public HashSet keyValues;
    public HashSet defaultValues;
    public HashSet fileValues;

    public String formatValueSet(HashSet hashSet) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            TrackedValue trackedValue = (TrackedValue) iterator.next();
            stringBuffer.append(trackedValue.getDisplayText());
            if (iterator.hasNext()) {
                stringBuffer.append(", ");
            }
        }

        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    public PropertyLookupValue(HashSet hashSet, HashSet hashSet1, HashSet hashSet2) {
        this.keyValues = hashSet;
        this.defaultValues = hashSet1;
        this.fileValues = hashSet2;
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public int hashCode() {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        int ba = 0;
        boolean bl = strictMergeEnabled;
        PropertyLookupValue propertyLookupValue1 = this;
        if (bl) {
            if (this.keyValues != null) {
                ba = this.keyValues.hashCode();
            }

            propertyLookupValue1 = this;
        }

        HashSet hashSet;
        if (bl) {
            if (propertyLookupValue1.defaultValues != null) {
                ba ^= this.defaultValues.hashCode();
            }

            hashSet = this.fileValues;
        } else {
            hashSet = propertyLookupValue1.fileValues;
        }

        if (hashSet != null) {
            ba ^= this.fileValues.hashCode();
        }

        return ba;
    }

    public PropertyLookupValue(HashSet hashSet, HashSet hashSet1) {
        this.keyValues = hashSet;
        this.fileValues = hashSet1;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public String getDisplayText() {
        return "<Properties key="
                + this.formatValueSet(this.keyValues)
                + (this.defaultValues != null && this.defaultValues.size() != 0 ? " default=" + this.formatValueSet(this.defaultValues) : "")
                + ", file="
                + this.formatValueSet(this.fileValues)
                + ">";
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        boolean bl1 = object instanceof PropertyLookupValue;
        if (!strictMergeDisabled) {
            if (bl1) {
                PropertyLookupValue propertyLookupValue1 = (PropertyLookupValue) object;
                PropertyLookupValue propertyLookupValue2 = this;
                if (!strictMergeDisabled) {
                    if (!this.areSetsEqual(this.keyValues, propertyLookupValue1.keyValues)) {
                        return false;
                    }

                    propertyLookupValue2 = this;
                }

                HashSet hashSet = this.defaultValues;
                HashSet hashSet1 = propertyLookupValue1.defaultValues;
                if (!strictMergeDisabled) {
                    if (!propertyLookupValue2.areSetsEqual(this.defaultValues, propertyLookupValue1.defaultValues)) {
                        return false;
                    }

                    propertyLookupValue2 = this;
                    hashSet = this.fileValues;
                    hashSet1 = propertyLookupValue1.fileValues;
                }

                bl1 = propertyLookupValue2.areSetsEqual(hashSet, hashSet1);
                if (strictMergeDisabled) {
                    return bl1;
                }

                if (bl1) {
                    return true;
                }

                return false;
            }

            bl1 = false;
        }

        return bl1;
    }

    public boolean areSetsEqual(HashSet hashSet, HashSet hashSet1) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        HashSet hashSet2 = hashSet;
        if (strictMergeEnabled) {
            if (hashSet != null) {
                hashSet2 = hashSet1;
                if (strictMergeEnabled) {
                    if (hashSet1 == null) {
                        return false;
                    }

                    hashSet2 = hashSet;
                }

                return hashSet2.equals(hashSet1);
            }

            hashSet2 = hashSet1;
        }

        return hashSet2 == null;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    @Override
    public String getCombinedValueKey() {
        return this.getStringValue();
    }
}
