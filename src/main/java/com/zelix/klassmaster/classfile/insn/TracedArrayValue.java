package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.obfuscator.reflection.InstructionReasonNote;
import com.zelix.klassmaster.obfuscator.reflection.NullReflectionValue;
import com.zelix.klassmaster.obfuscator.reflection.PendingReflectionValue;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TracedArrayValue implements TrackedValue {
    private static final String NULL_TEXT = "null";
    public int length;
    public HashSet[] elementValues;
    public List failureReasons;

    public TracedArrayValue(int length) {
        this.length = length;
        this.elementValues = new HashSet[length];
        this.failureReasons = new ArrayList();
    }

    @Override
    public String getNormalizedName() {
        return this.getStringValue();
    }

    public void resolvePendingValues(HashMap hashMap, IntegerCache integerCache1, MethodBytecode methodBytecode1) {
        int ba = 0;
        int bb = 0;

        for (HashSet[] hashSets = this.elementValues; bb < hashSets.length; hashSets = this.elementValues) {
            HashSet hashSet = this.elementValues[ba];
            Iterator iterator = ZkmUtils.copyHashSet(hashSet).iterator();

            while (iterator.hasNext()) {
                TrackedValue trackedValue = (TrackedValue) iterator.next();
                if (trackedValue.isPending()
                        && trackedValue instanceof PendingReflectionValue
                        && ((PendingReflectionValue) trackedValue).getMethodBytecode() == methodBytecode1) {
                    CallArgumentValues callArgumentValues = (CallArgumentValues) hashMap.get(integerCache1.valueOf(trackedValue.getPendingInstructionIndex()));
                    if (callArgumentValues != null) {
                        Set set1 = callArgumentValues.getArgumentValues(0);
                        hashSet.remove(trackedValue);
                        hashSet.addAll(set1);
                    }
                }
            }

            bb = ++ba;
        }
    }

    @Override
    public boolean isConstant() {
        if (this.failureReasons.size() > 0) {
            return false;
        }

        int ba = 0;
        int bb = ba;

        for (HashSet[] hashSets = this.elementValues; bb < hashSets.length; hashSets = this.elementValues) {
            Iterator iterator = this.elementValues[ba].iterator();

            while (iterator.hasNext()) {
                if (!((TrackedValue) iterator.next()).isConstant()) {
                    return false;
                }
            }

            bb = ++ba;
        }

        return true;
    }

    public String formatElements(boolean bl) {
        StringBuffer stringBuffer = new StringBuffer();
        int ba = 0;
        int bb = 0;

        for (HashSet[] hashSets = this.elementValues; bb < hashSets.length; hashSets = this.elementValues) {
            HashSet hashSet = this.elementValues[ba];
            stringBuffer.append("[");
            Iterator iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                TrackedValue trackedValue = (TrackedValue) iterator.next();
                stringBuffer.append(bl && trackedValue.isConstant() && trackedValue.isKnown() ? trackedValue.getStringValue() : trackedValue.getDisplayText());
                if (iterator.hasNext()) {
                    stringBuffer.append(",");
                }
            }

            stringBuffer.append("]");
            bb = ++ba;
        }

        return stringBuffer.toString();
    }

    public ArrayList getAllValues() {
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bb = 0;

        for (HashSet[] hashSets = this.elementValues; bb < hashSets.length; hashSets = this.elementValues) {
            Iterator iterator = this.elementValues[ba].iterator();

            while (iterator.hasNext()) {
                TrackedValue trackedValue = (TrackedValue) iterator.next();
                arrayList.add(trackedValue);
            }

            bb = ++ba;
        }

        return arrayList;
    }

    public ArrayList getFirstValues() {
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bb = 0;

        for (HashSet[] hashSets = this.elementValues; bb < hashSets.length; hashSets = this.elementValues) {
            TrackedValue trackedValue = null;
            Iterator iterator = this.elementValues[ba].iterator();
            if (iterator.hasNext()) {
                trackedValue = (TrackedValue) iterator.next();
            }

            arrayList.add(trackedValue);
            bb = ++ba;
        }

        return arrayList;
    }

    public String[][] getValueCombinations() {
        if (this.elementValues.length == 0) {
            return new String[0][0];
        }

        int ba = 1;
        HashSet[] hashSets = new HashSet[this.elementValues.length];
        int bb = 0;
        int bc = 0;

        for (HashSet[] hashSets1 = this.elementValues; bc < hashSets1.length; hashSets1 = this.elementValues) {
            HashSet hashSet = this.elementValues[bb];
            if (hashSet.size() == 0) {
                return (String[][]) null;
            }

            HashSet hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(hashSet.size()));
            Iterator iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                TrackedValue trackedValue = (TrackedValue) iterator.next();
                trackedValue.getNormalizedName();
                hashSet1.add(trackedValue.getStringValue());
            }

            ba *= hashSet1.size();
            hashSets[bb] = hashSet1;
            bc = ++bb;
        }

        String[][] strings = new String[ba][hashSets.length];
        MutableInt mutableInt = new MutableInt(0);
        this.collectCombinations(strings, hashSets, mutableInt, 0, new String[hashSets.length]);
        return strings;
    }

    public HashSet getElementValues(int ba) {
        return this.elementValues[ba];
    }

    @Override
    public String getDisplayText() {
        return this.formatElements(false);
    }

    public void setElementValue(int ba, TrackedValue trackedValue) {
        HashSet hashSet = ZkmUtils.createHashSet(3);
        hashSet.add(trackedValue);
        this.elementValues[ba] = hashSet;
        if (trackedValue instanceof InstructionReasonNote) {
            this.failureReasons.add(((InstructionReasonNote) trackedValue).getReason());
        }
    }

    public boolean hasSingleValuePerElement() {
        int ba = 0;
        int bc = 0;

        for (HashSet[] hashSets = this.elementValues; bc < hashSets.length; hashSets = this.elementValues) {
            HashSet hashSet = this.elementValues[ba];
            int bb = hashSet.size();
            if (bb == 0) {
                return false;
            }

            if (bb > 1) {
                String string = null;
                Iterator iterator = hashSet.iterator();

                while (iterator.hasNext()) {
                    TrackedValue trackedValue = (TrackedValue) iterator.next();
                    if (!trackedValue.isConstant()) {
                        return false;
                    }

                    String string1 = trackedValue.getStringValue();
                    if (string == null) {
                        string = string1;
                    } else if (!string.equals(string1)) {
                        return false;
                    }
                }
            }

            bc = ++ba;
        }

        return true;
    }

    @Override
    public boolean isKnown() {
        return this.length == 1 && this.elementValues[0].size() == 1 && this.elementValues[0].iterator().next() instanceof NullReflectionValue;
    }

    public void setElementValues(int ba, HashSet hashSet) {
        this.elementValues[ba] = hashSet;
    }

    public void collectCombinations(String[][] strings, HashSet[] hashSets, MutableInt mutableInt, int ba, String[] strings1) {
        Iterator iterator = hashSets[ba].iterator();

        while (iterator.hasNext()) {
            String[] strings2 = new String[strings1.length];
            System.arraycopy(strings1, 0, strings2, 0, ba);
            strings2[ba] = (String) iterator.next();
            if (ba + 1 < strings1.length) {
                this.collectCombinations(strings, hashSets, mutableInt, ba + 1, strings2);
            } else {
                strings[mutableInt.getValue()] = strings2;
                mutableInt.incrementAndGet();
            }
        }
    }

    @Override
    public int hashCode() {
        return this.elementValues.hashCode() ^ this.failureReasons.hashCode();
    }

    public TracedArrayValue(int length, List list1) {
        this.length = length;
        this.elementValues = new HashSet[length];
        this.failureReasons = list1;
    }

    @Override
    public String getStringValue() {
        return this.length == 1 && this.elementValues[0].size() == 1 ? ((TrackedValue) this.elementValues[0].iterator().next()).getStringValue() : null;
    }

    @Override
    public boolean equals(Object object) {
        boolean strictMergeEnabled = StackFrameState.isStrictMergeEnabled();
        boolean bl1 = object instanceof TracedArrayValue;
        if (strictMergeEnabled) {
            if (bl1) {
                TracedArrayValue tracedArrayValue1 = (TracedArrayValue) object;
                TracedArrayValue tracedArrayValue2 = this;
                if (strictMergeEnabled) {
                    if (this.elementValues != tracedArrayValue1.elementValues) {
                        return false;
                    }

                    tracedArrayValue2 = this;
                }

                bl1 = tracedArrayValue2.failureReasons.equals(tracedArrayValue1.failureReasons);
                if (!strictMergeEnabled) {
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

    public int getLength() {
        return this.length;
    }

    @Override
    public int getPendingInstructionIndex() {
        return -1;
    }

    @Override
    public boolean isPending() {
        int ba = 0;
        int bb = 0;

        for (HashSet[] hashSets = this.elementValues; bb < hashSets.length; hashSets = this.elementValues) {
            Iterator iterator = this.elementValues[ba].iterator();

            while (iterator.hasNext()) {
                if (((TrackedValue) iterator.next()).isPending()) {
                    return true;
                }
            }

            bb = ++ba;
        }

        return false;
    }

    @Override
    public String getCombinedValueKey() {
        int ba = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (HashSet hashSet : this.elementValues) {
            TreeSet treeSet = new TreeSet();
            Iterator iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                TrackedValue trackedValue = (TrackedValue) iterator.next();
                String string = trackedValue.getStringValue();
                treeSet.add(string != null ? string : NULL_TEXT);
            }

            int bb = treeSet.size();
            int bc = 0;
            Iterator iterator1 = treeSet.iterator();

            while (iterator1.hasNext()) {
                String string1 = (String) iterator1.next();
                stringBuilder.append(string1);
                if (bc++ < bb) {
                    stringBuilder.append('!');
                }
            }

            if (ba++ < this.length) {
                stringBuilder.append('~');
            }
        }

        return stringBuilder.toString();
    }
}
