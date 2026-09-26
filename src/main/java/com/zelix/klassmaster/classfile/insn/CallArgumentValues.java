package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.obfuscator.reflection.PendingReflectionValue;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionApiMethod;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionParamDetail;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CallArgumentValues {
    public boolean valid = true;
    public ReflectionApiMethod reflectionMethod;
    public List argumentValueSets;

    public int getArgumentCount() {
        return this.argumentValueSets.size();
    }

    public Set getArgumentValues(int ba) {
        return (Set) this.argumentValueSets.get(ba);
    }

    public void setValid() {
        this.valid = false;
    }

    public boolean containsArrayValue(HashSet hashSet) {
        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            if ((TrackedValue) iterator.next() instanceof TracedArrayValue) {
                return true;
            }
        }

        return false;
    }

    public String getSingleStringArgument(Integer integer) {
        if (this.argumentValueSets.size() < this.reflectionMethod.getParamDetails().size() + (this.reflectionMethod.isReceiverTargetClass() ? 1 : 0)) {
            return null;
        }

        HashSet hashSet = (HashSet) this.argumentValueSets.get(integer);
        HashSet hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(hashSet.size()));
        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            TrackedValue trackedValue = (TrackedValue) iterator.next();
            if (!trackedValue.isConstant()) {
                return null;
            }

            hashSet1.add(trackedValue.getStringValue());
        }

        return hashSet1.size() == 1 ? (String) hashSet1.iterator().next() : null;
    }

    public void resolvePendingValues(HashMap hashMap, IntegerCache integerCache1, MethodBytecode methodBytecode1) {
        Iterator iterator = new ArrayList(this.argumentValueSets).iterator();

        while (iterator.hasNext()) {
            HashSet hashSet = (HashSet) iterator.next();
            Iterator iterator1 = ZkmUtils.copyHashSet(hashSet).iterator();

            while (iterator1.hasNext()) {
                TrackedValue trackedValue = (TrackedValue) iterator1.next();
                if (trackedValue.isPending()) {
                    if (trackedValue instanceof TracedArrayValue) {
                        TracedArrayValue tracedArrayValue1 = (TracedArrayValue) trackedValue;
                        tracedArrayValue1.resolvePendingValues(hashMap, integerCache1, methodBytecode1);
                    } else if (trackedValue instanceof PendingReflectionValue) {
                        PendingReflectionValue pendingReflectionValue = (PendingReflectionValue) trackedValue;
                        if (pendingReflectionValue.getMethodBytecode() == methodBytecode1) {
                            CallArgumentValues callArgumentValues1 = (CallArgumentValues) hashMap.get(integerCache1.valueOf(trackedValue.getPendingInstructionIndex()));
                            boolean bl = false;
                            if (this.reflectionMethod.isMethodTypeLookup() && callArgumentValues1.reflectionMethod.isMethodTypeLookup()) {
                                bl = true;
                            }

                            if (callArgumentValues1 != null) {
                                List list1 = callArgumentValues1.argumentValueSets;
                                if (list1.size() <= 1) {
                                    if (this.reflectionMethod.isMethodTypeLookup()) {
                                        TracedArrayValue tracedArrayValue2 = new TracedArrayValue(1);
                                        tracedArrayValue2.setElementValues(0, (HashSet) list1.get(0));
                                        hashSet.remove(trackedValue);
                                        hashSet.add(tracedArrayValue2);
                                    } else {
                                        hashSet.remove(trackedValue);
                                        hashSet.addAll((Collection) list1.get(0));
                                    }
                                } else {
                                    ArrayList arrayList = new ArrayList();

                                    for (int i = bl ? 1 : 0; i < list1.size(); i++) {
                                        HashSet hashSet1 = (HashSet) list1.get(i);
                                        if (!this.containsArrayValue(hashSet1)) {
                                            arrayList.add(hashSet1);
                                        } else {
                                            HashSet[] hashSets = null;
                                            Iterator iterator2 = hashSet1.iterator();

                                            while (iterator2.hasNext()) {
                                                TrackedValue trackedValue1 = (TrackedValue) iterator2.next();
                                                if (!(trackedValue1 instanceof TracedArrayValue)) {
                                                    return;
                                                }

                                                TracedArrayValue tracedArrayValue = (TracedArrayValue) trackedValue1;
                                                if (hashSets == null) {
                                                    hashSets = new HashSet[tracedArrayValue.getLength()];
                                                }

                                                if (tracedArrayValue.getLength() != hashSets.length) {
                                                    return;
                                                }

                                                for (int j = 0; j < tracedArrayValue.getLength(); j++) {
                                                    if (hashSets[j] == null) {
                                                        hashSets[j] = tracedArrayValue.getElementValues(j);
                                                    } else {
                                                        hashSets[j].addAll(tracedArrayValue.getElementValues(j));
                                                    }
                                                }

                                                for (HashSet hashSet2 : hashSets) {
                                                    arrayList.add(hashSet2);
                                                }
                                            }
                                        }
                                    }

                                    TracedArrayValue tracedArrayValue3 = new TracedArrayValue(arrayList.size());

                                    for (int i = 0; i < arrayList.size(); i++) {
                                        tracedArrayValue3.setElementValues(i, (HashSet) arrayList.get(i));
                                    }

                                    hashSet.remove(trackedValue);
                                    hashSet.add(tracedArrayValue3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean areAllValuesKnown() {
        if (this.argumentValueSets.size() < this.reflectionMethod.getParamDetails().size() + (this.reflectionMethod.isReceiverTargetClass() ? 1 : 0)) {
            return false;
        }

        Iterator iterator = this.argumentValueSets.iterator();

        while (iterator.hasNext()) {
            Iterator iterator1 = ((HashSet) iterator.next()).iterator();

            while (iterator1.hasNext()) {
                if (!((TrackedValue) iterator1.next()).isConstant()) {
                    return false;
                }
            }
        }

        return true;
    }

    public CallArgumentValues(ReflectionApiMethod reflectionApiMethod) {
        this.reflectionMethod = reflectionApiMethod;
        this.argumentValueSets = new ArrayList(reflectionApiMethod.getParamDetails().size());
    }

    public void addArgumentValues(Object object) {
        this.argumentValueSets.add(object);
    }

    public String getMemberNameArgument() {
        if (this.argumentValueSets.size() < this.reflectionMethod.getParamDetails().size() + (this.reflectionMethod.isReceiverTargetClass() ? 1 : 0)) {
            return null;
        }

        int ba = 0;

        for (Iterator iterator = this.reflectionMethod.getParamDetails().iterator(); iterator.hasNext(); ba++) {
            ReflectionParamDetail reflectionParamDetail = (ReflectionParamDetail) iterator.next();
            if (reflectionParamDetail.isFieldNameParam() || reflectionParamDetail.isMethodNameParam()) {
                return this.getSingleStringArgument(ba);
            }
        }

        return null;
    }
}
