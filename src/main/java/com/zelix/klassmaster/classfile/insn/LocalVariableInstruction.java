package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.TruncatedStringDisplay;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class LocalVariableInstruction extends Instruction {
    private static final String INVALID_KIND_MESSAGE = "Invalid variable type and action ";
    private final int increment;
    public final LocalVariableAccessKind accessKind;
    public final LocalVariableSlot slot;

    public int getIncrement() {
        return this.increment;
    }

    public LocalVariableInstruction(int ba, LocalVariableAccessKind localVariableAccessKind, LocalVariableProvider localVariableProvider, int bb) {
        this(ba, localVariableAccessKind, localVariableProvider, 0, bb);
    }

    @Override
    public boolean isLoadFrom(int ba) {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return this.slot.getIndex() == ba;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return false;
            default:
                return false;
        }
    }

    public LocalVariableInstruction(int ba, int bb, int bc, ClassFileInputStream classFileInputStream, LocalVariableProvider localVariableProvider) throws IOException {
        super(255);
        if (bb == 132) {
            if (ba == 196) {
                this.increment = classFileInputStream.readShort();
            } else {
                this.increment = classFileInputStream.readByte();
            }
        } else {
            this.increment = 0;
        }

        this.accessKind = getAccessKindForOpcode(bb);
        this.slot = localVariableProvider.lookupSlot(bc, this.accessKind, 0);
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        if (this.requiresWidePrefix()) {
            dataOutputStream.writeByte(196);
        }

        int index = this.slot.getIndex();
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(26);
                        return;
                    case 1:
                        dataOutputStream.writeByte(27);
                        return;
                    case 2:
                        dataOutputStream.writeByte(28);
                        return;
                    case 3:
                        dataOutputStream.writeByte(29);
                        return;
                    default:
                        dataOutputStream.writeByte(21);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 2:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(30);
                        return;
                    case 1:
                        dataOutputStream.writeByte(31);
                        return;
                    case 2:
                        dataOutputStream.writeByte(32);
                        return;
                    case 3:
                        dataOutputStream.writeByte(33);
                        return;
                    default:
                        dataOutputStream.writeByte(22);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 3:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(34);
                        return;
                    case 1:
                        dataOutputStream.writeByte(35);
                        return;
                    case 2:
                        dataOutputStream.writeByte(36);
                        return;
                    case 3:
                        dataOutputStream.writeByte(37);
                        return;
                    default:
                        dataOutputStream.writeByte(23);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 4:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(38);
                        return;
                    case 1:
                        dataOutputStream.writeByte(39);
                        return;
                    case 2:
                        dataOutputStream.writeByte(40);
                        return;
                    case 3:
                        dataOutputStream.writeByte(41);
                        return;
                    default:
                        dataOutputStream.writeByte(24);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 5:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(42);
                        return;
                    case 1:
                        dataOutputStream.writeByte(43);
                        return;
                    case 2:
                        dataOutputStream.writeByte(44);
                        return;
                    case 3:
                        dataOutputStream.writeByte(45);
                        return;
                    default:
                        dataOutputStream.writeByte(25);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 6:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(59);
                        return;
                    case 1:
                        dataOutputStream.writeByte(60);
                        return;
                    case 2:
                        dataOutputStream.writeByte(61);
                        return;
                    case 3:
                        dataOutputStream.writeByte(62);
                        return;
                    default:
                        dataOutputStream.writeByte(54);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 7:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(67);
                        return;
                    case 1:
                        dataOutputStream.writeByte(68);
                        return;
                    case 2:
                        dataOutputStream.writeByte(69);
                        return;
                    case 3:
                        dataOutputStream.writeByte(70);
                        return;
                    default:
                        dataOutputStream.writeByte(56);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 8:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(75);
                        return;
                    case 1:
                        dataOutputStream.writeByte(76);
                        return;
                    case 2:
                        dataOutputStream.writeByte(77);
                        return;
                    case 3:
                        dataOutputStream.writeByte(78);
                        return;
                    default:
                        dataOutputStream.writeByte(58);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 9:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(63);
                        return;
                    case 1:
                        dataOutputStream.writeByte(64);
                        return;
                    case 2:
                        dataOutputStream.writeByte(65);
                        return;
                    case 3:
                        dataOutputStream.writeByte(66);
                        return;
                    default:
                        dataOutputStream.writeByte(55);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 10:
                switch (index) {
                    case 0:
                        dataOutputStream.writeByte(71);
                        return;
                    case 1:
                        dataOutputStream.writeByte(72);
                        return;
                    case 2:
                        dataOutputStream.writeByte(73);
                        return;
                    case 3:
                        dataOutputStream.writeByte(74);
                        return;
                    default:
                        dataOutputStream.writeByte(57);
                        if (this.requiresWidePrefix()) {
                            dataOutputStream.writeShort(index);
                        } else {
                            dataOutputStream.writeByte(index);
                        }

                        return;
                }
            case 11:
                dataOutputStream.writeByte(132);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                    dataOutputStream.writeShort(this.getIncrement());
                } else {
                    dataOutputStream.writeByte(index);
                    dataOutputStream.writeByte(this.getIncrement());
                }
                break;
            case 12:
                dataOutputStream.writeByte(169);
                if (this.requiresWidePrefix()) {
                    dataOutputStream.writeShort(index);
                } else {
                    dataOutputStream.writeByte(index);
                }
        }
    }

    @Override
    public final boolean pushesValue() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean accessesLocal(int ba) {
        if (!this.isStoreTo(ba) && !this.isLoadFrom(ba)) {
            int realOpcode = this.getRealOpcode();
            return realOpcode != 132 && realOpcode != 169 ? false : this.slot.getIndex() == ba;
        } else {
            return true;
        }
    }

    @Override
    public String toAssembly() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMnemonic());
        stringBuilder.append(' ');
        int realOpcode = this.getRealOpcode();
        if (this.requiresWidePrefix()) {
            stringBuilder.append(Instruction.mnemonics[realOpcode]);
            stringBuilder.append(' ');
        }

        stringBuilder.append(this.slot.getIndex());
        if (realOpcode == 132) {
            stringBuilder.append(' ');
            stringBuilder.append(this.increment);
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean isLocalVariableAccess() {
        return true;
    }

    @Override
    public boolean pushesWithoutPopping() {
        return this.pushesValue();
    }

    @Override
    public boolean isLoad() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int getOpcode() {
        return super.getOpcode();
    }

    public LocalVariableInstruction(int ba, LocalVariableAccessKind localVariableAccessKind, LocalVariableProvider localVariableProvider, int increment, int bc) {
        super(255);
        this.accessKind = localVariableAccessKind;
        this.slot = localVariableProvider.lookupSlot(ba, localVariableAccessKind, bc);
        this.increment = increment;
    }

    public LocalVariableIndex getLocalVariableIndex() {
        return this.slot;
    }

    public LocalVariableAccessKind getMatchingLoadKind() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 6:
                return LocalVariableAccessKind.INT_LOAD;
            case 7:
                return LocalVariableAccessKind.FLOAT_LOAD;
            case 8:
                return LocalVariableAccessKind.OBJECT_LOAD;
            case 9:
                return LocalVariableAccessKind.LONG_LOAD;
            case 10:
                return LocalVariableAccessKind.DOUBLE_LOAD;
            default:
                return null;
        }
    }

    @Override
    public StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object2, Object object3, Object object) throws ZkmException, IOException {
        new TruncatedStringDisplay((String) object);
        VerifierType[] verifierTypes = stackFrameState.getLocals();
        VerifierType[] verifierTypes1 = stackFrameState.getStack();
        int ba = verifierTypes1.length;
        SubroutineLocalsBitSet subroutineLocalsBitSet = stackFrameState.getSubroutineLocals();
        Set set1 = stackFrameState.getHeldMonitors();
        int index = this.slot.getIndex();
        SubroutineLocalsBitSet subroutineLocalsBitSet1;
        if (subroutineLocalsBitSet != null) {
            switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
                case 1:
                case 3:
                case 5:
                case 6:
                case 7:
                case 8:
                case 11:
                case 12:
                    if (subroutineLocalsBitSet.get(index)) {
                        subroutineLocalsBitSet1 = subroutineLocalsBitSet;
                    } else {
                        subroutineLocalsBitSet1 = (SubroutineLocalsBitSet) subroutineLocalsBitSet.clone();
                        subroutineLocalsBitSet1.set(index);
                    }
                    break;
                case 2:
                case 4:
                case 9:
                case 10:
                    Object object1;
                    if (subroutineLocalsBitSet.get(index)) {
                        if (subroutineLocalsBitSet.get(index + 1)) {
                            subroutineLocalsBitSet1 = subroutineLocalsBitSet;
                            break;
                        }

                        object1 = subroutineLocalsBitSet.clone();
                    } else {
                        object1 = subroutineLocalsBitSet.clone();
                    }

                    subroutineLocalsBitSet1 = (SubroutineLocalsBitSet) object1;
                    subroutineLocalsBitSet1.set(index);
                    subroutineLocalsBitSet1.set(index + 1);
                    break;
                default:
                    String[] strings = new String[]{this.accessKind.toString()};
                    ZkmAssert.assertTrue(false, strings);
                    subroutineLocalsBitSet1 = null;
            }
        } else {
            subroutineLocalsBitSet1 = null;
        }

        StackFrameState stackFrameState1;
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
                VerifierType[] verifierTypes8 = VerifierType.createArray(ba + 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes8, 0, ba);
                verifierTypes8[ba] = VerifierType.INT;
                stackFrameState1 = new StackFrameState(verifierTypes8, verifierTypes, subroutineLocalsBitSet1, set1);
                break;
            case 2:
                VerifierType[] verifierTypes7 = VerifierType.createArray(ba + 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes7, 0, ba);
                verifierTypes7[ba] = VerifierType.LONG;
                stackFrameState1 = new StackFrameState(verifierTypes7, verifierTypes, subroutineLocalsBitSet1, set1);
                break;
            case 3:
                VerifierType[] verifierTypes6 = VerifierType.createArray(ba + 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes6, 0, ba);
                verifierTypes6[ba] = VerifierType.FLOAT;
                stackFrameState1 = new StackFrameState(verifierTypes6, verifierTypes, subroutineLocalsBitSet1, set1);
                break;
            case 4:
                VerifierType[] verifierTypes5 = VerifierType.createArray(ba + 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes5, 0, ba);
                verifierTypes5[ba] = VerifierType.DOUBLE;
                stackFrameState1 = new StackFrameState(verifierTypes5, verifierTypes, subroutineLocalsBitSet1, set1);
                break;
            case 5:
                VerifierType[] verifierTypes4 = VerifierType.createArray(ba + 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes4, 0, ba);
                verifierTypes4[ba] = verifierTypes[index];
                stackFrameState1 = new StackFrameState(verifierTypes4, verifierTypes, subroutineLocalsBitSet1, set1);
                break;
            case 6:
                stackFrameState1 = this.applyNarrowStore(ba, verifierTypes1, verifierTypes, VerifierType.INT, subroutineLocalsBitSet1, set1);
                break;
            case 7:
                stackFrameState1 = this.applyNarrowStore(ba, verifierTypes1, verifierTypes, VerifierType.FLOAT, subroutineLocalsBitSet1, set1);
                break;
            case 8:
                int bc = verifierTypes.length;
                VerifierType[] verifierTypes3 = VerifierType.createArray(bc);
                System.arraycopy(verifierTypes, 0, verifierTypes3, 0, bc);
                if (verifierTypes3[index].equals(VerifierType.WIDE_SECOND_SLOT)
                        && (verifierTypes3[index - 1].equals(VerifierType.LONG) || verifierTypes3[index - 1].equals(VerifierType.DOUBLE))) {
                    verifierTypes3[index - 1] = VerifierType.TOP;
                    if (subroutineLocalsBitSet1 != null) {
                        subroutineLocalsBitSet1.set(index - 1);
                    }
                }

                if (verifierTypes3[index].equals(VerifierType.LONG) || verifierTypes3[index].equals(VerifierType.DOUBLE)) {
                    verifierTypes3[index + 1] = VerifierType.TOP;
                    if (subroutineLocalsBitSet1 != null) {
                        subroutineLocalsBitSet1.set(index + 1);
                    }
                }

                verifierTypes3[index] = verifierTypes1[ba - 1];
                VerifierType[] verifierTypes2 = VerifierType.createArray(ba - 1);
                System.arraycopy(verifierTypes1, 0, verifierTypes2, 0, ba - 1);
                stackFrameState1 = new StackFrameState(verifierTypes2, verifierTypes3, subroutineLocalsBitSet1, set1);
                break;
            case 9:
                stackFrameState1 = this.applyWideStore(ba, verifierTypes1, verifierTypes, VerifierType.LONG, subroutineLocalsBitSet1, set1);
                break;
            case 10:
                stackFrameState1 = this.applyWideStore(ba, verifierTypes1, verifierTypes, VerifierType.DOUBLE, subroutineLocalsBitSet1, set1);
                break;
            case 11:
                if (subroutineLocalsBitSet1 != null && subroutineLocalsBitSet1 != subroutineLocalsBitSet) {
                    stackFrameState1 = new StackFrameState(stackFrameState.getStack(), stackFrameState.getLocals(), subroutineLocalsBitSet1, set1);
                } else {
                    stackFrameState1 = new StackFrameState(verifierTypes1, verifierTypes, subroutineLocalsBitSet, set1);
                }
                break;
            case 12:
                if (subroutineLocalsBitSet1 != null && subroutineLocalsBitSet1 != subroutineLocalsBitSet) {
                    stackFrameState1 = new StackFrameState(verifierTypes1, verifierTypes, subroutineLocalsBitSet1, set1);
                } else {
                    stackFrameState1 = new StackFrameState(verifierTypes1, verifierTypes, subroutineLocalsBitSet, set1);
                }
                break;
            default:
                String[] strings1 = new String[]{INVALID_KIND_MESSAGE + this.accessKind};
                ZkmAssert.assertTrue(false, strings1);
                stackFrameState1 = null;
        }

        return stackFrameState1;
    }

    private StackFrameState applyWideStore(
            int ba, VerifierType[] verifierTypes, VerifierType[] verifierTypes1, VerifierType verifierType, SubroutineLocalsBitSet subroutineLocalsBitSet, Set set1
    ) {
        int index = this.slot.getIndex();
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba - 1);
        System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba - 1);
        int bc = verifierTypes1.length;
        VerifierType[] verifierTypes3 = VerifierType.createArray(bc);
        System.arraycopy(verifierTypes1, 0, verifierTypes3, 0, bc);
        if (verifierTypes3[index].equals(VerifierType.WIDE_SECOND_SLOT)
                && (verifierTypes3[index - 1].equals(VerifierType.LONG) || verifierTypes3[index - 1].equals(VerifierType.DOUBLE))) {
            verifierTypes3[index - 1] = VerifierType.TOP;
        }

        verifierTypes3[index] = verifierType;
        VerifierType verifierType1 = verifierTypes3[index + 1];
        verifierTypes3[index + 1] = VerifierType.WIDE_SECOND_SLOT;
        if (verifierType1.equals(VerifierType.LONG) || verifierType1.equals(VerifierType.DOUBLE)) {
            verifierTypes3[index + 2] = VerifierType.TOP;
            if (subroutineLocalsBitSet != null) {
                subroutineLocalsBitSet.set(index + 2);
            }
        }

        return new StackFrameState(verifierTypes2, verifierTypes3, subroutineLocalsBitSet, set1);
    }

    public int getRealOpcode() {
        int index = this.slot.getIndex();
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
                switch (index) {
                    case 0:
                        return 26;
                    case 1:
                        return 27;
                    case 2:
                        return 28;
                    case 3:
                        return 29;
                    default:
                        return 21;
                }
            case 2:
                switch (index) {
                    case 0:
                        return 30;
                    case 1:
                        return 31;
                    case 2:
                        return 32;
                    case 3:
                        return 33;
                    default:
                        return 22;
                }
            case 3:
                switch (index) {
                    case 0:
                        return 34;
                    case 1:
                        return 35;
                    case 2:
                        return 36;
                    case 3:
                        return 37;
                    default:
                        return 23;
                }
            case 4:
                switch (index) {
                    case 0:
                        return 38;
                    case 1:
                        return 39;
                    case 2:
                        return 40;
                    case 3:
                        return 41;
                    default:
                        return 24;
                }
            case 5:
                switch (index) {
                    case 0:
                        return 42;
                    case 1:
                        return 43;
                    case 2:
                        return 44;
                    case 3:
                        return 45;
                    default:
                        return 25;
                }
            case 6:
                switch (index) {
                    case 0:
                        return 59;
                    case 1:
                        return 60;
                    case 2:
                        return 61;
                    case 3:
                        return 62;
                    default:
                        return 54;
                }
            case 7:
                switch (index) {
                    case 0:
                        return 67;
                    case 1:
                        return 68;
                    case 2:
                        return 69;
                    case 3:
                        return 70;
                    default:
                        return 56;
                }
            case 8:
                switch (index) {
                    case 0:
                        return 75;
                    case 1:
                        return 76;
                    case 2:
                        return 77;
                    case 3:
                        return 78;
                    default:
                        return 58;
                }
            case 9:
                switch (index) {
                    case 0:
                        return 63;
                    case 1:
                        return 64;
                    case 2:
                        return 65;
                    case 3:
                        return 66;
                    default:
                        return 55;
                }
            case 10:
                switch (index) {
                    case 0:
                        return 71;
                    case 1:
                        return 72;
                    case 2:
                        return 73;
                    case 3:
                        return 74;
                    default:
                        return 57;
                }
            case 11:
                return 132;
            case 12:
                return 169;
            default:
                return -1;
        }
    }

    public int getEmittedOpcode() {
        return this.requiresWidePrefix() ? 196 : this.getRealOpcode();
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object2, Object object1) {
        int ba = (Integer) object;
        int bb = (Integer) object1;
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 11:
            case 12:
                return false;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return ba >= bb;
            default:
                return false;
        }
    }

    public static LocalVariableAccessKind getAccessKindForType(String string) {
        if (string.length() == 1) {
            switch (string.charAt(0)) {
                case 'B':
                case 'C':
                case 'I':
                case 'S':
                case 'Z':
                    return LocalVariableAccessKind.INT_STORE;
                case 'D':
                    return LocalVariableAccessKind.DOUBLE_STORE;
                case 'E':
                case 'G':
                case 'H':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                default:
                    return null;
                case 'F':
                    return LocalVariableAccessKind.FLOAT_STORE;
                case 'J':
                    return LocalVariableAccessKind.LONG_STORE;
            }
        } else {
            return LocalVariableAccessKind.OBJECT_STORE;
        }
    }

    public static LocalVariableInstruction readLocalVariableInstruction(
            int ba, ClassFileInputStream classFileInputStream, LocalVariableProvider localVariableProvider
    ) throws IOException {
        int bb;
        if (ba == 196) {
            int bc = classFileInputStream.read();
            bb = bc;
        } else {
            bb = ba;
        }

        int bd;
        switch (bb) {
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 132:
            case 169:
                if (ba == 196) {
                    bd = classFileInputStream.readUnsignedShort();
                } else {
                    bd = classFileInputStream.readUnsignedByte();
                }
                break;
            case 26:
            case 30:
            case 34:
            case 38:
            case 42:
            case 59:
            case 63:
            case 67:
            case 71:
            case 75:
                bd = 0;
                break;
            case 27:
            case 31:
            case 35:
            case 39:
            case 43:
            case 60:
            case 64:
            case 68:
            case 72:
            case 76:
                bd = 1;
                break;
            case 28:
            case 32:
            case 36:
            case 40:
            case 44:
            case 61:
            case 65:
            case 69:
            case 73:
            case 77:
                bd = 2;
                break;
            case 29:
            case 33:
            case 37:
            case 41:
            case 45:
            case 62:
            case 66:
            case 70:
            case 74:
            case 78:
                bd = 3;
                break;
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            default:
                bd = -1;
        }

        return bd > 3 || bb != 21 && bb != 54 && bb != 22 && bb != 55 && bb != 23 && bb != 56 && bb != 24 && bb != 57 && bb != 25 && bb != 58
                ? new LocalVariableInstruction(ba, bb, bd, classFileInputStream, localVariableProvider)
                : new ExplicitLocalVarInstruction(ba, bb, bd, classFileInputStream, localVariableProvider);
    }

    public static LocalVariableInstruction createForType(String string, LocalVariableSlot localVariableSlot1) {
        LocalVariableAccessKind localVariableAccessKind = getAccessKindForType(string);
        return new LocalVariableInstruction(localVariableSlot1, localVariableAccessKind);
    }

    public LocalVariableInstruction(LocalVariableSlot localVariableSlot1, LocalVariableAccessKind localVariableAccessKind) {
        super(255);
        this.slot = localVariableSlot1;
        this.accessKind = localVariableAccessKind;
        this.increment = 0;
    }

    @Override
    public void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException {
        StringBuilder stringBuilder1 = new StringBuilder(100);
        stringBuilder1.append(this.getMnemonic());
        stringBuilder1.append(' ');
        int realOpcode = this.getRealOpcode();
        if (this.slot.needsWideIndex()) {
            stringBuilder1.append(Instruction.mnemonics[this.getRealOpcode()]);
            stringBuilder1.append(' ');
        }

        stringBuilder1.append(this.slot.getIndex());
        String string = Instruction.getDescriptionComment(realOpcode);
        string = Instruction.formatDescription(string, String.valueOf(this.slot.getIndex()));
        if (realOpcode == 132) {
            string = Instruction.formatDescription(string, String.valueOf(this.increment));
        }

        if (string.length() > 0) {
            stringBuilder1.append("\t" + string);
        }

        printWriter.println(stringBuilder.toString() + stringBuilder.toString() + stringBuilder1);
    }

    public LocalVariableAccessKind getAccessKind() {
        return this.accessKind;
    }

    @Override
    public final boolean isExit() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return false;
            case 12:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getStackDelta() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 3:
            case 5:
                return 1;
            case 2:
            case 4:
                return 2;
            case 6:
            case 7:
            case 8:
                return -1;
            case 9:
            case 10:
                return -2;
            case 11:
            case 12:
                return 0;
            default:
                return 0;
        }
    }

    private StackFrameState applyNarrowStore(
            int ba, VerifierType[] verifierTypes, VerifierType[] verifierTypes1, VerifierType verifierType, SubroutineLocalsBitSet subroutineLocalsBitSet, Set set1
    ) {
        int index = this.slot.getIndex();
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba - 1);
        System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba - 1);
        int bc = verifierTypes1.length;
        VerifierType[] verifierTypes3 = VerifierType.createArray(bc);
        System.arraycopy(verifierTypes1, 0, verifierTypes3, 0, bc);
        if (verifierTypes3[index].equals(VerifierType.WIDE_SECOND_SLOT)
                && (verifierTypes3[index - 1].equals(VerifierType.LONG) || verifierTypes3[index - 1].equals(VerifierType.DOUBLE))) {
            verifierTypes3[index - 1] = VerifierType.TOP;
            if (subroutineLocalsBitSet != null) {
                subroutineLocalsBitSet.set(index - 1);
            }
        }

        if (verifierTypes3[index].equals(VerifierType.LONG) || verifierTypes3[index].equals(VerifierType.DOUBLE)) {
            verifierTypes3[index + 1] = VerifierType.TOP;
            if (subroutineLocalsBitSet != null) {
                subroutineLocalsBitSet.set(index + 1);
            }
        }

        verifierTypes3[index] = verifierType;
        return new StackFrameState(verifierTypes2, verifierTypes3, subroutineLocalsBitSet, set1);
    }

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 11:
            case 12:
                return false;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return ba >= bb;
            default:
                return false;
        }
    }

    @Override
    public int getLength() {
        int ba = 0;
        switch (this.getRealOpcode()) {
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 169:
                ba = 2 + (this.requiresWidePrefix() ? 2 : 0);
                break;
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
                ba = 1;
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            default:
                break;
            case 132:
                ba = 3 + (this.requiresWidePrefix() ? 3 : 0);
        }

        return ba;
    }

    @Override
    public boolean isRet() {
        return this.accessKind == LocalVariableAccessKind.ADDRESS;
    }

    @Override
    public final boolean canFallThrough() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return true;
            case 12:
                return false;
            default:
                return true;
        }
    }

    private static LocalVariableAccessKind getAccessKindForOpcode(int ba) {
        switch (ba) {
            case 21:
            case 26:
            case 27:
            case 28:
            case 29:
                return LocalVariableAccessKind.INT_LOAD;
            case 22:
            case 30:
            case 31:
            case 32:
            case 33:
                return LocalVariableAccessKind.LONG_LOAD;
            case 23:
            case 34:
            case 35:
            case 36:
            case 37:
                return LocalVariableAccessKind.FLOAT_LOAD;
            case 24:
            case 38:
            case 39:
            case 40:
            case 41:
                return LocalVariableAccessKind.DOUBLE_LOAD;
            case 25:
            case 42:
            case 43:
            case 44:
            case 45:
                return LocalVariableAccessKind.OBJECT_LOAD;
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            default:
                return null;
            case 54:
            case 59:
            case 60:
            case 61:
            case 62:
                return LocalVariableAccessKind.INT_STORE;
            case 55:
            case 63:
            case 64:
            case 65:
            case 66:
                return LocalVariableAccessKind.LONG_STORE;
            case 56:
            case 67:
            case 68:
            case 69:
            case 70:
                return LocalVariableAccessKind.FLOAT_STORE;
            case 57:
            case 71:
            case 72:
            case 73:
            case 74:
                return LocalVariableAccessKind.DOUBLE_STORE;
            case 58:
            case 75:
            case 76:
            case 77:
            case 78:
                return LocalVariableAccessKind.OBJECT_STORE;
            case 132:
                return LocalVariableAccessKind.INT_INC;
            case 169:
                return LocalVariableAccessKind.ADDRESS;
        }
    }

    @Override
    public final boolean isStoreTo(int ba) {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 11:
            case 12:
                return false;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return this.slot.getIndex() == ba;
            default:
                return false;
        }
    }

    @Override
    public final boolean isStore() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 11:
            case 12:
                return false;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return true;
            default:
                return false;
        }
    }

    public int getLocalIndex() {
        return this.slot.getIndex();
    }

    public boolean requiresWidePrefix() {
        return this.slot.needsWideIndex() || this.accessKind == LocalVariableAccessKind.INT_INC && (this.increment < -128 || this.increment > 127);
    }

    @Override
    public final boolean pushesWideValue() {
        switch (LocalVarAccessKindSwitchMap.ACCESS_KIND_SWITCH_MAP[this.accessKind.ordinal()]) {
            case 1:
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return false;
            case 2:
            case 4:
                return true;
            default:
                return false;
        }
    }

    @Override
    public final String getMnemonic() {
        return Instruction.mnemonics[this.getEmittedOpcode()];
    }
}
