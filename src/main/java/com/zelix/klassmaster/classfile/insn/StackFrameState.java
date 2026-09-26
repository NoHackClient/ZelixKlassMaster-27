package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.exceptions.StackAnalysisException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.BooleanFlag;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class StackFrameState {
    private static boolean strictMergeEnabled;
    private BitSet liveLocals;
    private VerifierType[] locals;
    private VerifierType[] stack;
    private final SubroutineLocalsBitSet subroutineLocals;
    private Set heldMonitors;

    public static boolean isStrictMergeEnabled() {
        return strictMergeEnabled;
    }

    public boolean isCompatibleWithOrUnchecked(
            CommonSuperTypeResolver commonSuperTypeResolver1, StackFrameState stackFrameState1, Set set1, boolean bl, String string, int ba
    ) throws ZkmException, IOException {
        return this.areStacksCompatible(commonSuperTypeResolver1, stackFrameState1, string)
                && this.areLocalsCompatible(commonSuperTypeResolver1, stackFrameState1, string)
                && (!bl || this.hasMatchingMonitors(stackFrameState1, set1))
                || ba == -1;
    }

    public LabelInstruction getSubroutineEntryLabel() {
        return this.subroutineLocals != null ? this.subroutineLocals.getSubroutineEntry() : null;
    }

    public MonitorEnterInstruction popMatchingMonitorEnter(MonitorExitInstruction monitorExitInstruction) {
        if (this.heldMonitors != null && this.heldMonitors.size() > 0) {
            int ba = this.heldMonitors.size();
            int bb = -1;
            MonitorEnterInstruction monitorEnterInstruction = null;
            Iterator iterator = this.heldMonitors.iterator();

            while (iterator.hasNext()) {
                bb++;
                MonitorEnterInstruction monitorEnterInstruction1 = (MonitorEnterInstruction) iterator.next();
                if (monitorEnterInstruction1.getLockedObjectType() == monitorExitInstruction.getLockedObjectType()) {
                    monitorEnterInstruction = monitorEnterInstruction1;
                    iterator.remove();
                    break;
                }

                if (bb == ba - 1) {
                    monitorEnterInstruction = monitorEnterInstruction1;
                    iterator.remove();
                }
            }

            if (this.heldMonitors.size() == 0) {
                this.heldMonitors = null;
            }

            return monitorEnterInstruction;
        } else {
            return null;
        }
    }

    public static boolean isIntType(VerifierType verifierType) {
        return verifierType.hasDescriptor("I");
    }

    public int getStackSlotCount() {
        int ba = 0;

        for (VerifierType verifierType : this.stack) {
            ba += verifierType.getSlotSize();
        }

        return ba;
    }

    public boolean isFullyInitialized() {
        int ba = this.stack.length;

        for (int i = 0; i < ba; i++) {
            if (!this.stack[i].isInitialized()) {
                return false;
            }
        }

        int bd = this.locals.length;

        for (int i = 0; i < bd; i++) {
            if (!this.locals[i].isInitialized()) {
                return false;
            }
        }

        return true;
    }

    public VerifierType[] getStack() {
        return this.stack.clone();
    }

    public static boolean isReferenceType(VerifierType verifierType) {
        return verifierType.descriptorStartsWith("[")
                || verifierType.descriptorStartsWith("L") && verifierType.descriptorEndsWith()
                || verifierType.hasDescriptor("n");
    }

    public VerifierType[] getLocalsMaskedByLiveness() {
        if (this.liveLocals == null) {
            return this.locals.clone();
        }

        VerifierType[] verifierTypes = new VerifierType[this.locals.length];

        for (int i = 0; i < this.locals.length; i++) {
            VerifierType verifierType;
            if (!this.liveLocals.get(i)
                    && (this.locals[i] != VerifierType.WIDE_SECOND_SLOT || verifierTypes[i - 1] != VerifierType.LONG && verifierTypes[i - 1] != VerifierType.DOUBLE)
            ) {
                verifierType = VerifierType.TOP;
            } else {
                verifierType = this.locals[i];
            }

            verifierTypes[i] = verifierType;
        }

        return verifierTypes;
    }

    public StackFrameState(VerifierType[] verifierTypes, VerifierType[] verifierTypes1, Set set1) {
        this(verifierTypes, verifierTypes1, null, set1);
    }

    private static VerifierType[] mergeStacks(
            CommonSuperTypeResolver commonSuperTypeResolver1, VerifierType[] verifierTypes, VerifierType[] verifierTypes1, BooleanFlag booleanFlag, String string
    ) throws ZkmException, IOException {
        booleanFlag.setValue(false);
        if (verifierTypes.length != verifierTypes1.length) {
            throw new StackAnalysisException("Inconsistent stack heights " + verifierTypes.length + " " + verifierTypes1.length + " : '" + string + "'");
        }

        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2 = verifierTypes.clone();

        for (int i = 0; i < ba; i++) {
            if (!verifierTypes1[i].equals(verifierTypes2[i])) {
                booleanFlag.setValue(true);
                if (!isReferenceType(verifierTypes1[i]) || !isReferenceType(verifierTypes2[i])) {
                    throw new StackAnalysisException(
                            "Stack mismatch with types: " + verifierTypes2[i].getDescriptor() + " " + verifierTypes1[i].getDescriptor() + " : '" + string + "'"
                    );
                }

                if (verifierTypes2[i].hasDescriptor("n")) {
                    verifierTypes2[i] = verifierTypes1[i];
                } else if (!verifierTypes1[i].hasDescriptor("n")) {
                    verifierTypes2[i] = VerifierType.forDescriptor(commonSuperTypeResolver1.mergeVerifierTypes(verifierTypes1[i], verifierTypes2[i], string));
                }
            }
        }

        return verifierTypes2;
    }

    public static VerifierType[] mergeSubroutineLocals(
            VerifierType[] verifierTypes, VerifierType[] verifierTypes1, SubroutineLocalsBitSet subroutineLocalsBitSet, BooleanFlag booleanFlag
    ) {
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2 = verifierTypes.clone();
        booleanFlag.setValue(false);
        if (subroutineLocalsBitSet != null) {
            for (int i = 0; i < ba; i++) {
                if (!verifierTypes1[i].equals(verifierTypes2[i]) && subroutineLocalsBitSet.get(i)) {
                    booleanFlag.setValue(true);
                    verifierTypes2[i] = verifierTypes1[i];
                }
            }
        }

        return verifierTypes2;
    }

    private boolean areLocalsCompatible(CommonSuperTypeResolver commonSuperTypeResolver1, StackFrameState stackFrameState1, String string) throws ZkmException, IOException {
        int ba = this.locals.length;
        VerifierType[] verifierTypes = stackFrameState1.locals;
        if (ba != verifierTypes.length) {
            throw new ZkmProcessingException("Mismatched local variable counts: " + ba + " " + verifierTypes.length);
        }

        for (int i = 0; i < ba; i++) {
            if (!this.locals[i].equals(verifierTypes[i]) && !verifierTypes[i].hasDescriptor("?")) {
                if (this.locals[i].hasDescriptor("?")) {
                    return false;
                }

                if (!isReferenceType(this.locals[i]) || !isReferenceType(verifierTypes[i])) {
                    return false;
                }

                if (this.locals[i].isInitialized() != verifierTypes[i].isInitialized()) {
                    return false;
                }

                if (verifierTypes[i].hasDescriptor("n")) {
                    return false;
                }

                if (this.locals[i].hasDescriptor("n")) {
                    return false;
                }

                if (!commonSuperTypeResolver1.isVerifierTypeAssignable(this.locals[i], verifierTypes[i], string)) {
                    return false;
                }
            }
        }

        return true;
    }

    public MonitorEnterInstruction pushMonitorEnter(MonitorEnterInstruction monitorEnterInstruction) {
        if (this.heldMonitors == null) {
            this.heldMonitors = new LinkedHashSet();
        }

        this.heldMonitors.add(monitorEnterInstruction);
        return monitorEnterInstruction;
    }

    public boolean hasLivenessInfo() {
        return this.liveLocals != null;
    }

    public StackFrameState(VerifierType[] verifierTypes, SubroutineLocalsBitSet subroutineLocalsBitSet, Set set1) {
        this(VerifierType.createArray(0), verifierTypes, subroutineLocalsBitSet, set1);
    }

    public boolean hasMatchingMonitors(StackFrameState stackFrameState1, Set set1) {
        for (VerifierType verifierType : this.getStack()) {
            if (set1.contains(verifierType)) {
                return false;
            }
        }

        for (VerifierType verifierType1 : stackFrameState1.getStack()) {
            if (set1.contains(verifierType1)) {
                return false;
            }
        }

        if (this.heldMonitors == null && stackFrameState1.heldMonitors == null) {
            return true;
        }

        if (this.heldMonitors != null && stackFrameState1.heldMonitors != null) {
            if (this.heldMonitors.size() != stackFrameState1.heldMonitors.size()) {
                return false;
            }

            Iterator iterator = this.heldMonitors.iterator();

            label47:
            while (iterator.hasNext()) {
                MonitorEnterInstruction monitorEnterInstruction = (MonitorEnterInstruction) iterator.next();
                Iterator iterator1 = stackFrameState1.heldMonitors.iterator();

                while (iterator1.hasNext()) {
                    MonitorEnterInstruction monitorEnterInstruction1 = (MonitorEnterInstruction) iterator1.next();
                    if (monitorEnterInstruction == monitorEnterInstruction1) {
                        continue label47;
                    }
                }

                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean hasUninitializedThis() {
        for (VerifierType verifierType : this.stack) {
            if (verifierType.isUninitializedThis()) {
                return true;
            }
        }

        for (VerifierType verifierType1 : this.locals) {
            if (verifierType1 != null && verifierType1.isUninitializedThis()) {
                return true;
            }
        }

        return false;
    }

    public boolean isLocalDefined(int ba) {
        return this.locals[ba] != null && !this.locals[ba].hasDescriptor("?");
    }

    public String getLocalTypeDescriptor(int ba) {
        return !this.locals[ba].isWideSecondSlot()
                && this.locals[ba] != VerifierType.NULL
                && this.locals[ba] != VerifierType.RETURN_ADDRESS
                && this.locals[ba] != VerifierType.TOP
                ? this.locals[ba].getDescriptor()
                : null;
    }

    public boolean isLocalLive(int ba) {
        return this.liveLocals.get(ba);
    }

    public final int getStackDepth() {
        return this.stack.length;
    }

    public StackFrameState(VerifierType[] verifierTypes) {
        this(verifierTypes, (SubroutineLocalsBitSet) null, null);
    }

    public static VerifierType[] mergeLocals(
            CommonSuperTypeResolver commonSuperTypeResolver1,
            VerifierType[] verifierTypes,
            VerifierType[] verifierTypes1,
            boolean bl,
            BooleanFlag booleanFlag,
            String string
    ) throws ZkmException, IOException {
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2;
        if (bl) {
            verifierTypes2 = verifierTypes.clone();
        } else {
            verifierTypes2 = verifierTypes;
        }

        booleanFlag.setValue(false);

        for (int i = 0; i < ba; i++) {
            if (verifierTypes1[i].equals(verifierTypes2[i])) {
                if (verifierTypes2[i].hasDescriptor("~")
                        && !verifierTypes2[i - 1].equals(VerifierType.LONG)
                        && !verifierTypes2[i - 1].equals(VerifierType.DOUBLE)) {
                    verifierTypes2[i] = VerifierType.TOP;
                }
            } else {
                booleanFlag.setValue(true);
                if (verifierTypes1[i].hasDescriptor("?") || verifierTypes2[i].hasDescriptor("?")) {
                    verifierTypes2[i] = VerifierType.TOP;
                } else if (isReferenceType(verifierTypes1[i]) && isReferenceType(verifierTypes2[i])) {
                    if (verifierTypes2[i].hasDescriptor("n")) {
                        verifierTypes2[i] = verifierTypes1[i];
                    } else if (!verifierTypes1[i].hasDescriptor("n")) {
                        verifierTypes2[i] = VerifierType.forDescriptor(commonSuperTypeResolver1.mergeVerifierTypes(verifierTypes1[i], verifierTypes2[i], string));
                    }
                } else {
                    verifierTypes2[i] = VerifierType.TOP;
                }
            }
        }

        return verifierTypes2;
    }

    public StackFrameState merge(CommonSuperTypeResolver commonSuperTypeResolver1, StackFrameState stackFrameState1, BooleanFlag booleanFlag, String string) throws ZkmException, IOException {
        BooleanFlag booleanFlag1 = new BooleanFlag();
        BooleanFlag booleanFlag2 = new BooleanFlag();
        VerifierType[] verifierTypes = mergeStacks(commonSuperTypeResolver1, this.stack, stackFrameState1.stack, booleanFlag1, string);
        VerifierType[] verifierTypes1 = mergeLocals(commonSuperTypeResolver1, this.locals, stackFrameState1.locals, true, booleanFlag2, string);
        booleanFlag.setValue(booleanFlag1.getValue() || booleanFlag2.getValue());
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (this.heldMonitors != null) {
            linkedHashSet.addAll(this.heldMonitors);
        }

        if (stackFrameState1.heldMonitors != null) {
            linkedHashSet.addAll(stackFrameState1.heldMonitors);
        }

        if (linkedHashSet.size() > 0) {
            if (this.heldMonitors == null || !linkedHashSet.equals(this.heldMonitors)) {
                booleanFlag.setValue(true);
            }
        } else {
            linkedHashSet = null;
        }

        StackFrameState stackFrameState2;
        if (this.subroutineLocals == null || stackFrameState1.subroutineLocals == null) {
            stackFrameState2 = new StackFrameState(verifierTypes, verifierTypes1, linkedHashSet);
        } else if (this.subroutineLocals.equals(stackFrameState1.subroutineLocals)) {
            stackFrameState2 = new StackFrameState(verifierTypes, verifierTypes1, (SubroutineLocalsBitSet) this.subroutineLocals.clone(), linkedHashSet);
        } else {
            SubroutineLocalsBitSet subroutineLocalsBitSet = (SubroutineLocalsBitSet) this.subroutineLocals.clone();
            subroutineLocalsBitSet.or(stackFrameState1.subroutineLocals);
            stackFrameState2 = new StackFrameState(verifierTypes, verifierTypes1, subroutineLocalsBitSet, linkedHashSet);
            booleanFlag.setValue(true);
        }

        return stackFrameState2;
    }

    public static boolean isAssignable(VerifierType verifierType, VerifierType verifierType1, CommonSuperTypeResolver commonSuperTypeResolver1, String string) throws ZkmException, IOException {
        if (!isReferenceType(verifierType) || !isReferenceType(verifierType1)) {
            return areTypesCompatible(verifierType, verifierType1);
        } else if (verifierType.hasDescriptor("n")) {
            return true;
        } else {
            return commonSuperTypeResolver1.isVerifierTypeAssignable(verifierType, verifierType1, string)
                    ? true
                    : commonSuperTypeResolver1.isInterfaceType(verifierType1, string);
        }
    }

    public StackFrameState(VerifierType[] verifierTypes, VerifierType[] verifierTypes1, SubroutineLocalsBitSet subroutineLocalsBitSet, Set set1) {
        this.locals = verifierTypes1;
        this.stack = verifierTypes;
        this.subroutineLocals = subroutineLocalsBitSet;
        this.heldMonitors = set1;

        for (int i = 0; i < this.locals.length; i++) {
            if (this.locals[i] == null) {
                this.locals[i] = VerifierType.TOP;
            }
        }
    }

    public Set getHeldMonitors() {
        return this.heldMonitors != null ? new LinkedHashSet(this.heldMonitors) : null;
    }

    public BitSet getLiveLocals() {
        return (BitSet) this.liveLocals.clone();
    }

    public StackFrameState(String string, VerifierType[] verifierTypes, SubroutineLocalsBitSet subroutineLocalsBitSet, Set set1) {
        this.locals = verifierTypes;
        this.stack = VerifierType.createArray(1);
        this.subroutineLocals = subroutineLocalsBitSet;
        this.heldMonitors = set1;
        this.stack[0] = VerifierType.forDescriptor(string);

        for (int i = 0; i < this.locals.length; i++) {
            if (this.locals[i] == null) {
                this.locals[i] = VerifierType.TOP;
            }
        }
    }

    public static boolean isWideType(VerifierType verifierType) {
        return verifierType.hasDescriptor("J") || verifierType.hasDescriptor("D");
    }

    public boolean isCompatibleWith(CommonSuperTypeResolver commonSuperTypeResolver1, StackFrameState stackFrameState1, Set set1, boolean bl, String string) throws ZkmException, IOException {
        return this.areStacksCompatible(commonSuperTypeResolver1, stackFrameState1, string)
                && this.areLocalsCompatible(commonSuperTypeResolver1, stackFrameState1, string)
                && (!bl || this.hasMatchingMonitors(stackFrameState1, set1));
    }

    public static StackFrameState createWithSubroutineLocals(
            StackFrameState stackFrameState, VerifierType[] verifierTypes, SubroutineLocalsBitSet subroutineLocalsBitSet, Set set1
    ) {
        BooleanFlag booleanFlag = new BooleanFlag();
        VerifierType[] verifierTypes1 = mergeSubroutineLocals(verifierTypes, stackFrameState.locals, stackFrameState.subroutineLocals, booleanFlag);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (set1 != null) {
            linkedHashSet.addAll(set1);
        }

        if (stackFrameState.heldMonitors != null) {
            linkedHashSet.addAll(stackFrameState.heldMonitors);
        }

        if (linkedHashSet.size() == 0) {
            linkedHashSet = null;
        }

        StackFrameState stackFrameState1;
        if (subroutineLocalsBitSet != null) {
            if (stackFrameState.subroutineLocals.equals(subroutineLocalsBitSet)) {
                stackFrameState1 = new StackFrameState(stackFrameState.stack, verifierTypes1, subroutineLocalsBitSet, linkedHashSet);
            } else {
                SubroutineLocalsBitSet subroutineLocalsBitSet1 = (SubroutineLocalsBitSet) subroutineLocalsBitSet.clone();
                subroutineLocalsBitSet1.or(stackFrameState.subroutineLocals);
                stackFrameState1 = new StackFrameState(stackFrameState.stack, verifierTypes1, subroutineLocalsBitSet1, linkedHashSet);
            }
        } else {
            stackFrameState1 = new StackFrameState(stackFrameState.stack, verifierTypes1, linkedHashSet);
        }

        return stackFrameState1;
    }

    public boolean addLiveLocals(BitSet bitSet) {
        BitSet bitSet1 = null;
        boolean bl;
        if (this.liveLocals == null) {
            this.liveLocals = new BitSet(this.locals.length);
            bl = true;
        } else {
            bitSet1 = (BitSet) this.liveLocals.clone();
            bl = false;
        }

        this.liveLocals.or(bitSet);
        return bl || !this.liveLocals.equals(bitSet1);
    }

    public static boolean isStrictMergeDisabled() {
        return !isStrictMergeEnabled();
    }

    private boolean areStacksCompatible(CommonSuperTypeResolver commonSuperTypeResolver1, StackFrameState stackFrameState1, String string) throws ZkmException, IOException {
        int ba = this.stack.length;
        if (ba != stackFrameState1.getStackDepth()) {
            return false;
        }

        for (int i = 0; i < ba; i++) {
            if (!this.stack[i].equals(stackFrameState1.stack[i])) {
                if (!isReferenceType(this.stack[i]) || !isReferenceType(stackFrameState1.stack[i])) {
                    return false;
                }

                if (this.stack[i].isInitialized() != stackFrameState1.stack[i].isInitialized()) {
                    return false;
                }

                if (this.stack[i].hasDescriptor("n")) {
                    return false;
                }

                if (stackFrameState1.stack[i].hasDescriptor("n")) {
                    return false;
                }

                if (!commonSuperTypeResolver1.isVerifierTypeAssignable(this.stack[i], stackFrameState1.stack[i], string)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void setStrictMergeEnabled() {
        strictMergeEnabled = true;
    }

    public VerifierType[] getLocals() {
        return this.locals.clone();
    }

    public static boolean areTypesCompatible(VerifierType verifierType, VerifierType verifierType1) {
        if (verifierType.equals(verifierType1)) {
            return true;
        } else if (isIntType(verifierType) && isIntType(verifierType1)) {
            return true;
        } else if (verifierType1.hasDescriptor("F")) {
            return verifierType.hasDescriptor("I");
        } else {
            return !verifierType1.hasDescriptor("D")
                    ? isReferenceType(verifierType) && isReferenceType(verifierType1)
                    : verifierType.hasDescriptor("F") || verifierType.hasDescriptor("I");
        }
    }

    public static boolean isTopOrMissing(VerifierType verifierType) {
        return verifierType == null || verifierType.hasDescriptor("?");
    }

    public boolean isLocalOccupied(int ba) {
        return !this.locals[ba].isWideSecondSlot() && this.locals[ba] != VerifierType.RETURN_ADDRESS && this.locals[ba] != VerifierType.TOP;
    }

    public boolean hasWideLocal() {
        for (int i = 0; i < this.locals.length; i++) {
            if (isWideType(this.locals[i])) {
                return true;
            }
        }

        return false;
    }

    public SubroutineLocalsBitSet getSubroutineLocals() {
        return this.subroutineLocals;
    }

    public FrameStateKey createKey() {
        return new FrameSignatureKey(this);
    }

    static {
        setStrictMergeEnabled();
    }
}
