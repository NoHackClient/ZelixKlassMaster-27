package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolProvider;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Instruction implements InstructionMarker {
    private static int[] flowObfKeys;
    public static final String[] mnemonics = new String[202];
    public static final String[] opcodeDescriptions = new String[202];
    public static IntegerCache integerCache = IntegerCache.getInstance();
    public int opcode = -1;

    public boolean isStackManipulation() {
        return false;
    }

    public boolean affectsStackSlot(Object object, Object object1) {
        return false;
    }

    public static Instruction createLongLoad(LocalVariableSlot localVariableSlot1) {
        return new LocalVariableInstruction(localVariableSlot1, LocalVariableAccessKind.LONG_LOAD);
    }

    public abstract String toAssembly();

    public abstract void printDisassembly(PrintWriter printWriter, StringBuilder stringBuilder) throws UnknownOpcodeException;

    public boolean isMonitor() {
        return false;
    }

    public static void setFlowObfKeys(int[] ba) {
        flowObfKeys = ba;
    }

    public static int appendIntConstant(int ba, List list1, ConstantPool constantPool1, List list2) {
        int bb = 0;
        int bh;
        if (ba >= -32768) {
            if (ba <= 32767) {
                Instruction instruction1 = createIntPush(ba);
                list1.add(instruction1);
                return instruction1.getLength();
            }

            bh = 131070;
        } else {
            bh = 131070;
        }

        int bg = bh;
        int bc = ba % bg;
        int bd = ba / bg;
        boolean bl = true;
        int be;
        int bf;
        if (Math.abs(bc) > 65535) {
            if (bd == 0) {
                be = bg * (bc > 0 ? 1 : -1);
            } else if (bd > 0) {
                be = bg * (bd + 1);
            } else {
                be = bg * (bd - 1);
            }

            bl = false;
            bf = bc > 0 ? bg - bc : (bg + bc) * -1;
        } else {
            be = bg * bd;
            bf = bc;
        }

        ArrayList arrayList = new ArrayList();
        if (be != 0) {
            ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(19, constantPool1.getOrAddIntegerConstant_s_0(be, list2));
            arrayList.add(constantRefInstruction);
            bb += constantRefInstruction.getLength();
        }

        label61:
        {
            if (bf >= -32768) {
                if (bf <= 32767) {
                    Instruction instruction2 = createIntPush(bf);
                    bb += instruction2.getLength();
                    arrayList.add(instruction2);
                    break label61;
                }

                bh = Math.abs(bf);
            } else {
                bh = Math.abs(bf);
            }

            Instruction instruction3 = createIntPush((short) (bh & 65535));
            arrayList.add(instruction3);
            bb += instruction3.getLength();
            arrayList.add(SimpleInstruction.forOpcode(146));
            bb++;
            if (bf < 0) {
                arrayList.add(SimpleInstruction.forOpcode(116));
                bb++;
            }
        }

        if (be != 0) {
            if (bl) {
                arrayList.add(SimpleInstruction.forOpcode(96));
                bb++;
            } else {
                arrayList.add(SimpleInstruction.forOpcode(100));
                bb++;
            }
        }

        list1.addAll(arrayList);
        if (bl) {
            bh = be + bf;
        } else {
            bh = be - bf;
        }

        return bb;
    }

    public boolean isStore() {
        return false;
    }

    public static int[] getFlowObfKeys() {
        return flowObfKeys;
    }

    public boolean isLabel() {
        return false;
    }

    public int getLength() {
        return 1;
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.opcode);
    }

    public boolean collectStringConstant(MultiMapTable multiMapTable, Set set1, MethodInfo methodInfo1, int ba) {
        return false;
    }

    public boolean isFieldStore() {
        return false;
    }

    public boolean isLocalVariableAccess() {
        return false;
    }

    public Instruction(int opcode) {
        this.opcode = opcode;
    }

    public static int appendLongConstant(long ba, List list1, ConstantPool constantPool1, List list2) {
        Instruction instruction1 = createLongConstantLoad(ba, constantPool1, list2);
        list1.add(instruction1);
        return instruction1.getLength();
    }

    public boolean accessesLocal(int ba) {
        return this.isStoreTo(ba) || this.isLoadFrom(ba);
    }

    public static Instruction createIntIncrement(int ba, int bb, LocalVariableProvider localVariableProvider, int bc) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.INT_INC, localVariableProvider, bb, bc);
    }

    public boolean isRet() {
        return false;
    }

    public static Instruction createFloatStore(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.FLOAT_STORE, localVariableProvider, bb);
    }

    public abstract boolean isExit();

    public boolean isIntConstantPush() {
        return false;
    }

    public Instruction adjustLdcWidth(Map map1) {
        return null;
    }

    public boolean isIntConstantLdc() {
        return false;
    }

    public abstract boolean consumesStackSlot(int ba, int bb);

    public abstract boolean requiresTypedValueAt(Object object, Object object1, Object object2);

    public static Instruction createLoadForType(String string, int ba, LocalVariableProvider localVariableProvider) {
        if (string.length() == 1) {
            switch (string.charAt(0)) {
                case 'B':
                case 'C':
                case 'I':
                case 'S':
                case 'Z':
                    return createIntLoad(ba, localVariableProvider, 5);
                case 'D':
                    return createDoubleLoad(ba, localVariableProvider, 5);
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
                    return createFloatLoad(ba, localVariableProvider, 5);
                case 'J':
                    return createLongLoad(ba, localVariableProvider, 5);
            }
        } else {
            return createObjectLoad(ba, localVariableProvider, 5);
        }
    }

    public static Instruction createObjectLoad(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.OBJECT_LOAD, localVariableProvider, bb);
    }

    public boolean collectIntConstant(Object object, Object object1, Object object2, Object object3) {
        return false;
    }

    public static String formatDescription(String string, String string1) {
        int ba = string.indexOf(37);
        if (ba != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string.substring(0, ba));
            if (string1.length() > 200) {
                stringBuilder.append(string1.substring(0, 50));
                stringBuilder.append("...<");
                stringBuilder.append(string1.length() - 100);
                stringBuilder.append(" CHARS>...");
                stringBuilder.append(string1.substring(string1.length() - 50));
            } else {
                stringBuilder.append(string1);
            }

            stringBuilder.append(string.substring(ba + 1));
            return stringBuilder.toString();
        } else {
            return string;
        }
    }

    public static int appendLongConstantAsShorts(long ba, List list1, ConstantPool constantPool1, List list2, ObservableHolder observableHolder) throws ZkmException, IOException {
        int bb = 0;
        int bc = (int) (ba >>> 48);
        int bd = (int) (ba << 16 >>> 48);
        int be = (int) (ba << 32 >>> 48);
        int bf = (int) (ba << 48 >>> 48);
        ArrayList arrayList = new ArrayList(17);
        boolean bl = false;
        if (bc != 0) {
            arrayList.add(createIntPush((short) bc));
            arrayList.add(SimpleInstruction.forOpcode(133));
            arrayList.add(new BipushInstruction(48));
            arrayList.add(SimpleInstruction.forOpcode(121));
            bl = true;
        }

        ConstantLong constantLong = (ConstantLong) observableHolder.getValue();
        if (bd != 0) {
            short bg = (short) bd;
            arrayList.add(createIntPush(bg));
            arrayList.add(SimpleInstruction.forOpcode(133));
            if (bg < 0) {
                if (constantLong == null) {
                    constantLong = constantPool1.getOrAddLongConstant(65535L, list2);
                    observableHolder.setValue(constantLong);
                }

                arrayList.add(new ConstantRefInstruction(20, constantLong));
                arrayList.add(SimpleInstruction.forOpcode(127));
            }

            arrayList.add(new BipushInstruction(32));
            arrayList.add(SimpleInstruction.forOpcode(121));
            if (bl) {
                arrayList.add(SimpleInstruction.forOpcode(129));
            } else {
                bl = true;
            }
        }

        if (be != 0) {
            short bh = (short) be;
            arrayList.add(createIntPush(bh));
            arrayList.add(SimpleInstruction.forOpcode(133));
            if (bh < 0) {
                if (constantLong == null) {
                    constantLong = constantPool1.getOrAddLongConstant(65535L, list2);
                    observableHolder.setValue(constantLong);
                }

                arrayList.add(new ConstantRefInstruction(20, constantLong));
                arrayList.add(SimpleInstruction.forOpcode(127));
            }

            arrayList.add(new BipushInstruction(16));
            arrayList.add(SimpleInstruction.forOpcode(121));
            if (bl) {
                arrayList.add(SimpleInstruction.forOpcode(129));
            } else {
                bl = true;
            }
        }

        if (bf != 0) {
            short bi = (short) bf;
            arrayList.add(createIntPush(bi));
            arrayList.add(SimpleInstruction.forOpcode(133));
            if (bi < 0) {
                if (constantLong == null) {
                    constantLong = constantPool1.getOrAddLongConstant(65535L, list2);
                    observableHolder.setValue(constantLong);
                }

                arrayList.add(new ConstantRefInstruction(20, constantLong));
                arrayList.add(SimpleInstruction.forOpcode(127));
            }

            if (bl) {
                arrayList.add(SimpleInstruction.forOpcode(129));
            }
        }

        Iterator iterator = arrayList.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            bb += instruction1.getLength();
        }

        list1.addAll(arrayList);
        return bb;
    }

    public static Instruction createClassConstantLoad(ConstantPool constantPool1, List list1) {
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("[Ljava/lang/Object;", list1);
        return new ConstantRefInstruction(19, resolvedClassConstant);
    }

    public boolean isLongConstantLoad() {
        return false;
    }

    public int[] mapStackSlotForward(VerifierType[] verifierTypes, VerifierType[] verifierTypes1, int ba) {
        return new int[]{ba};
    }

    public int mapStackSlotBackward(Object object, int ba) {
        return ba;
    }

    public List expandWideJump() {
        return null;
    }

    public boolean isLoadFrom(int ba) {
        return false;
    }

    public abstract int getStackDelta();

    public boolean collectLongConstant(Object object, Object object1, Object object2, Object object3) {
        return false;
    }

    public boolean isMethodInvoke() {
        return false;
    }

    public static Instruction createObjectStore(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.OBJECT_STORE, localVariableProvider, bb);
    }

    public static Instruction createIntStore(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.INT_STORE, localVariableProvider, bb);
    }

    public static Instruction createIntLoad(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.INT_LOAD, localVariableProvider, bb);
    }

    public boolean isLoad() {
        return false;
    }

    public static Instruction createStoreForType(String string, int ba, LocalVariableProvider localVariableProvider) {
        if (string.length() == 1) {
            switch (string.charAt(0)) {
                case 'B':
                case 'C':
                case 'I':
                case 'S':
                case 'Z':
                    return createIntStore(ba, localVariableProvider, 5);
                case 'D':
                    return createDoubleStore(ba, localVariableProvider, 5);
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
                    return createFloatStore(ba, localVariableProvider, 5);
                case 'J':
                    return createLongStore(ba, localVariableProvider, 5);
            }
        } else {
            return createObjectStore(ba, localVariableProvider, 5);
        }
    }

    public abstract boolean pushesWideValue();

    public boolean isReturn() {
        return false;
    }

    public static Instruction createFloatLoad(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.FLOAT_LOAD, localVariableProvider, bb);
    }

    public boolean isLongConstant() {
        return false;
    }

    public static Instruction createLongStore(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.LONG_STORE, localVariableProvider, bb);
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        this.writeTo(dataOutputStream);
    }

    public abstract boolean pushesValue();

    public abstract StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException;

    public boolean isStringConstantLoad() {
        return false;
    }

    public static Instruction createRet(int ba, LocalVariableProvider localVariableProvider) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.ADDRESS, localVariableProvider, 1);
    }

    public boolean continuesToNext() {
        return this.canFallThrough();
    }

    public boolean loadsConstantInteger() {
        return false;
    }

    public static Instruction createLongStore(LocalVariableSlot localVariableSlot1) {
        return new LocalVariableInstruction(localVariableSlot1, LocalVariableAccessKind.LONG_STORE);
    }

    public boolean isJump() {
        return false;
    }

    public static Instruction createIntPush(int ba) {
        Instruction instruction1 = null;
        switch (ba) {
            case -1:
                instruction1 = SimpleInstruction.forOpcode(2);
                break;
            case 0:
                instruction1 = SimpleInstruction.forOpcode(3);
                break;
            case 1:
                instruction1 = SimpleInstruction.forOpcode(4);
                break;
            case 2:
                instruction1 = SimpleInstruction.forOpcode(5);
                break;
            case 3:
                instruction1 = SimpleInstruction.forOpcode(6);
                break;
            case 4:
                instruction1 = SimpleInstruction.forOpcode(7);
                break;
            case 5:
                instruction1 = SimpleInstruction.forOpcode(8);
                break;
            default:
                if (ba >= -128 && ba <= 127) {
                    instruction1 = new BipushInstruction(ba);
                } else if (ba >= -32768 && ba <= 32767) {
                    instruction1 = new SipushInstruction(ba);
                }
        }

        return instruction1;
    }

    public void setOffset(int ba) {
    }

    public static Instruction readInstruction(
            ClassFileInputStream classFileInputStream,
            int ba,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            ConstantPoolProvider constantPoolProvider,
            LocalVariableProvider localVariableProvider
    ) throws UnknownOpcodeException, IOException {
        int bb = classFileInputStream.read();
        switch (bb) {
            case 0:
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
            case 12:
            case 13:
            case 14:
            case 15:
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
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 190:
            case 191:
                return SimpleInstruction.forOpcode(bb);
            case 16:
                return new BipushInstruction(classFileInputStream);
            case 17:
                return new SipushInstruction(classFileInputStream);
            case 18:
                return new LdcInstruction(classFileInputStream, constantPoolProvider, listMultimap1, listMultimap2, listMultimap4);
            case 19:
            case 20:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 189:
            case 192:
            case 193:
                return new ConstantRefInstruction(
                        bb, classFileInputStream, constantPoolProvider, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5
                );
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
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
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
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
            case 132:
            case 169:
            case 196:
                return LocalVariableInstruction.readLocalVariableInstruction(bb, classFileInputStream, localVariableProvider);
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
            case 198:
            case 199:
                return new BranchInstruction(bb, classFileInputStream, ba, listMultimap);
            case 167:
                return new GotoInstruction(classFileInputStream, ba, listMultimap);
            case 168:
                return new JsrInstruction(classFileInputStream, ba, listMultimap);
            case 170:
                return new TableSwitchInstruction(classFileInputStream, ba, listMultimap);
            case 171:
                return new LookupSwitchInstruction(classFileInputStream, ba, listMultimap);
            case 185:
                return new InvokeInterfaceInstruction(
                        classFileInputStream, constantPoolProvider, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5
                );
            case 186:
                return new InvokeDynamicInstruction(
                        classFileInputStream, constantPoolProvider, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5, listMultimap6
                );
            case 187:
                return new TypeInstruction(
                        classFileInputStream, constantPoolProvider, ba, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5
                );
            case 188:
                return new NewArrayInstruction(classFileInputStream);
            case 194:
                return new MonitorEnterInstruction();
            case 195:
                return new MonitorExitInstruction();
            case 197:
                return new MultiANewArrayInstruction(
                        classFileInputStream, constantPoolProvider, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5
                );
            case 200:
            case 201:
                return new WideJumpInstruction(bb, classFileInputStream, ba, listMultimap);
            default:
                throw new UnknownOpcodeException("Error: unknown opcode=" + bb + " (A)");
        }
    }

    public static Instruction createDoubleStore(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.DOUBLE_STORE, localVariableProvider, bb);
    }

    public final String getDescriptionComment() {
        String string = opcodeDescriptions[this.getOpcode()];
        return string != null && string.length() > 0 ? "//" + string : "";
    }

    public static String getDescriptionComment(int ba) {
        String string = opcodeDescriptions[ba];
        return string != null && string.length() > 0 ? "//" + string : "";
    }

    public int getOpcode() {
        return this.opcode;
    }

    public static Instruction createIntConstantPush(int ba, ConstantPool constantPool1, List list1) {
        return ba >= -32768 && ba <= 32767 ? createIntPush(ba) : new ConstantRefInstruction(19, constantPool1.getOrAddIntegerConstant_s_0(ba, list1));
    }

    public abstract boolean canFallThrough();

    public String getMnemonic() {
        return mnemonics[this.getOpcode()];
    }

    public static Instruction createIntLoad(LocalVariableSlot localVariableSlot1) {
        return new LocalVariableInstruction(localVariableSlot1, LocalVariableAccessKind.INT_LOAD);
    }

    public static Instruction createDoubleLoad(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.DOUBLE_LOAD, localVariableProvider, bb);
    }

    static {
        setFlowObfKeys(new int[3]);
        opcodeDescriptions[50] = "push reference from array";
        opcodeDescriptions[83] = "pop reference into array";
        opcodeDescriptions[1] = "push null object reference";
        opcodeDescriptions[25] = "push reference from local%";
        opcodeDescriptions[42] = "push reference from local0";
        opcodeDescriptions[43] = "push reference from local1";
        opcodeDescriptions[44] = "push reference from local2";
        opcodeDescriptions[45] = "push reference from local3";
        opcodeDescriptions[189] = "push a reference to a new array of %";
        opcodeDescriptions[176] = "return reference";
        opcodeDescriptions[190] = "push length of array";
        opcodeDescriptions[58] = "pop reference into local%";
        opcodeDescriptions[75] = "pop reference into local0";
        opcodeDescriptions[76] = "pop reference into local1";
        opcodeDescriptions[77] = "pop reference into local2";
        opcodeDescriptions[78] = "pop reference into local3";
        opcodeDescriptions[191] = "throw reference currently on stack";
        opcodeDescriptions[51] = "push byte or boolean from array";
        opcodeDescriptions[84] = "pop into byte or boolean array";
        opcodeDescriptions[16] = "push % onto stack";
        opcodeDescriptions[52] = "push char from array";
        opcodeDescriptions[85] = "pop into char array";
        opcodeDescriptions[192] = "cast to %";
        opcodeDescriptions[144] = "convert double to float";
        opcodeDescriptions[142] = "convert double to int";
        opcodeDescriptions[143] = "convert double to long";
        opcodeDescriptions[99] = "add the two doubles on the stack";
        opcodeDescriptions[49] = "push double from array";
        opcodeDescriptions[82] = "pop into double array";
        opcodeDescriptions[151] = "compare two doubles on stack";
        opcodeDescriptions[152] = "compare two doubles on stack";
        opcodeDescriptions[14] = "push double 0.0";
        opcodeDescriptions[15] = "push double 1.0";
        opcodeDescriptions[111] = "divide two doubles";
        opcodeDescriptions[24] = "push double from local%";
        opcodeDescriptions[38] = "push double from local0";
        opcodeDescriptions[39] = "push double from local1";
        opcodeDescriptions[40] = "push double from local2";
        opcodeDescriptions[41] = "push double from local3";
        opcodeDescriptions[107] = "multiply two doubles";
        opcodeDescriptions[119] = "negate a double";
        opcodeDescriptions[115] = "push remainder of two doubles";
        opcodeDescriptions[175] = "return double";
        opcodeDescriptions[57] = "pop double into local%";
        opcodeDescriptions[71] = "pop double into local0";
        opcodeDescriptions[72] = "pop double into local1";
        opcodeDescriptions[73] = "pop double into local2";
        opcodeDescriptions[74] = "pop double into local3";
        opcodeDescriptions[103] = "subtract double at stack top from double below it";
        opcodeDescriptions[89] = "duplicate top stack word";
        opcodeDescriptions[90] = "duplicate top stack word & put 2 down";
        opcodeDescriptions[91] = "duplicate top stack word & put 3 down";
        opcodeDescriptions[92] = "duplicate top 2 stack words";
        opcodeDescriptions[93] = "duplicate top 2 stack words & put 3 down";
        opcodeDescriptions[94] = "duplicate top 2 stack words & put 4 down";
        opcodeDescriptions[141] = "convert float to double";
        opcodeDescriptions[139] = "convert float to int";
        opcodeDescriptions[140] = "convert float to long";
        opcodeDescriptions[98] = "add the two floats on the stack";
        opcodeDescriptions[48] = "push float from array";
        opcodeDescriptions[81] = "pop into float array";
        opcodeDescriptions[149] = "compare two floats on stack";
        opcodeDescriptions[150] = "compare two floats on stack";
        opcodeDescriptions[11] = "push float 0.0";
        opcodeDescriptions[12] = "push float 1.0";
        opcodeDescriptions[13] = "push float 2.0";
        opcodeDescriptions[110] = "divide two floats";
        opcodeDescriptions[23] = "push float from local%";
        opcodeDescriptions[34] = "push float from local0";
        opcodeDescriptions[35] = "push float from local1";
        opcodeDescriptions[36] = "push float from local2";
        opcodeDescriptions[37] = "push float from local3";
        opcodeDescriptions[106] = "multiply two floats";
        opcodeDescriptions[118] = "negate a float";
        opcodeDescriptions[114] = "push remainder of two floats";
        opcodeDescriptions[174] = "return float";
        opcodeDescriptions[56] = "pop float into local%";
        opcodeDescriptions[67] = "pop float into local0";
        opcodeDescriptions[68] = "pop float into local1";
        opcodeDescriptions[69] = "pop float into local2";
        opcodeDescriptions[70] = "pop float into local3";
        opcodeDescriptions[102] = "subtract float at stack top from float below it";
        opcodeDescriptions[180] = "push contents of field %";
        opcodeDescriptions[178] = "push contents of field %";
        opcodeDescriptions[167] = "goto %";
        opcodeDescriptions[200] = "goto %";
        opcodeDescriptions[145] = "convert int to byte";
        opcodeDescriptions[146] = "convert int to char";
        opcodeDescriptions[135] = "convert int to double";
        opcodeDescriptions[133] = "convert int to long";
        opcodeDescriptions[147] = "convert int to short";
        opcodeDescriptions[96] = "add the two ints on the stack";
        opcodeDescriptions[46] = "push int from array";
        opcodeDescriptions[126] = "bitwise AND of two ints";
        opcodeDescriptions[79] = "pop into int array";
        opcodeDescriptions[2] = "push -1 onto stack";
        opcodeDescriptions[3] = "push 0 onto stack";
        opcodeDescriptions[4] = "push 1 onto stack";
        opcodeDescriptions[5] = "push 2 onto stack";
        opcodeDescriptions[6] = "push 3 onto stack";
        opcodeDescriptions[7] = "push 4 onto stack";
        opcodeDescriptions[8] = "push 5 onto stack";
        opcodeDescriptions[108] = "divide two ints";
        opcodeDescriptions[165] = "goto % if ref1 == ref2";
        opcodeDescriptions[166] = "goto % if ref1 != ref2";
        opcodeDescriptions[159] = "goto % if int1 == int2";
        opcodeDescriptions[160] = "goto % if int1 != int2";
        opcodeDescriptions[161] = "goto % if int1 < int2";
        opcodeDescriptions[162] = "goto % if int1 >= int2";
        opcodeDescriptions[163] = "goto % if int1 > int2";
        opcodeDescriptions[164] = "goto % if int1 <= int2";
        opcodeDescriptions[153] = "goto % if int == 0";
        opcodeDescriptions[154] = "goto % if int != 0";
        opcodeDescriptions[155] = "goto % if int < 0";
        opcodeDescriptions[156] = "goto % if int >= 0";
        opcodeDescriptions[157] = "goto % if int > 0";
        opcodeDescriptions[158] = "goto % if int <= 0";
        opcodeDescriptions[199] = "goto % if objectref != null";
        opcodeDescriptions[198] = "goto % if objectref == null";
        opcodeDescriptions[132] = "increment local% by %";
        opcodeDescriptions[21] = "push int from local%";
        opcodeDescriptions[26] = "push int from local0";
        opcodeDescriptions[27] = "push int from local1";
        opcodeDescriptions[28] = "push int from local2";
        opcodeDescriptions[29] = "push int from local3";
        opcodeDescriptions[104] = "multiply two ints";
        opcodeDescriptions[116] = "negate an int";
        opcodeDescriptions[193] = "is objectref of class %?";
        opcodeDescriptions[185] = "invoke % with % operands";
        opcodeDescriptions[186] = "invoke % dynamically using %";
        opcodeDescriptions[183] = "invoke %";
        opcodeDescriptions[184] = "invoke %";
        opcodeDescriptions[182] = "invoke %";
        opcodeDescriptions[128] = "bitwise OR of two ints";
        opcodeDescriptions[112] = "push remainder of two ints";
        opcodeDescriptions[172] = "return int";
        opcodeDescriptions[120] = "shift int1 left by low 5 bits of int2";
        opcodeDescriptions[122] = "shift int1 right by low 5 bits of int2";
        opcodeDescriptions[54] = "pop int from stack into local%";
        opcodeDescriptions[59] = "pop int from stack into local0";
        opcodeDescriptions[60] = "pop int from stack into local1";
        opcodeDescriptions[61] = "pop int from stack into local2";
        opcodeDescriptions[62] = "pop int from stack into local3";
        opcodeDescriptions[100] = "subtract int at stack top from int below it";
        opcodeDescriptions[124] = "logical shift int1 right by low 5 bits of int2";
        opcodeDescriptions[130] = "bitwise XOR of two ints";
        opcodeDescriptions[168] = "push address of next opcode then goto %";
        opcodeDescriptions[201] = "push address of next opcode then goto %";
        opcodeDescriptions[138] = "convert long to double";
        opcodeDescriptions[137] = "convert long to float";
        opcodeDescriptions[136] = "convert long to int";
        opcodeDescriptions[97] = "add the two longs on the stack";
        opcodeDescriptions[47] = "push long from array";
        opcodeDescriptions[127] = "bitwise AND of two longs";
        opcodeDescriptions[80] = "pop into long array";
        opcodeDescriptions[148] = "compare two longs on stack";
        opcodeDescriptions[9] = "push long 0";
        opcodeDescriptions[10] = "push long 1";
        opcodeDescriptions[18] = "push % from constant pool";
        opcodeDescriptions[19] = "push % from constant pool";
        opcodeDescriptions[20] = "push long or double % from constant pool";
        opcodeDescriptions[109] = "divide two longs";
        opcodeDescriptions[22] = "push long from local%";
        opcodeDescriptions[30] = "push long from local0";
        opcodeDescriptions[31] = "push long from local1";
        opcodeDescriptions[32] = "push long from local2";
        opcodeDescriptions[33] = "push long from local3";
        opcodeDescriptions[105] = "multiply two longs";
        opcodeDescriptions[117] = "negate a long";
        opcodeDescriptions[129] = "bitwise OR of two longs";
        opcodeDescriptions[113] = "push remainder of two longs";
        opcodeDescriptions[173] = "return long";
        opcodeDescriptions[121] = "shift long left by low 6 bits of int";
        opcodeDescriptions[123] = "shift long right by low 6 bits of int";
        opcodeDescriptions[55] = "pop long from stack into local%";
        opcodeDescriptions[63] = "pop long from stack into local0";
        opcodeDescriptions[64] = "pop long from stack into local1";
        opcodeDescriptions[65] = "pop long from stack into local2";
        opcodeDescriptions[66] = "pop long from stack into local3";
        opcodeDescriptions[101] = "subtract long at stack top from long below it";
        opcodeDescriptions[125] = "logical shift long right by low 6 bits of int";
        opcodeDescriptions[131] = "bitwise XOR of two longs";
        opcodeDescriptions[194] = "enter monitor for objectref";
        opcodeDescriptions[195] = "exit monitor for objectref";
        opcodeDescriptions[197] = "new array of % with % dimensions";
        opcodeDescriptions[187] = "new %";
        opcodeDescriptions[188] = "new array of %";
        opcodeDescriptions[0] = "do nothing";
        opcodeDescriptions[87] = "pop 1 word";
        opcodeDescriptions[88] = "pop 2 words";
        opcodeDescriptions[181] = "set field %";
        opcodeDescriptions[179] = "set field %";
        opcodeDescriptions[169] = "return from subroutine. Use address in local%";
        opcodeDescriptions[177] = "return void from method";
        opcodeDescriptions[53] = "push a short from an array";
        opcodeDescriptions[86] = "pop a short into an array";
        opcodeDescriptions[17] = "push % onto stack";
        opcodeDescriptions[95] = "swap two stack words";
        opcodeDescriptions[196] = "modifies behaviour of next opcode. NB: has 2 formats";
        mnemonics[0] = "nop";
        mnemonics[1] = "aconst_null";
        mnemonics[2] = "iconst_m1";
        mnemonics[3] = "iconst_0";
        mnemonics[4] = "iconst_1";
        mnemonics[5] = "iconst_2";
        mnemonics[6] = "iconst_3";
        mnemonics[7] = "iconst_4";
        mnemonics[8] = "iconst_5";
        mnemonics[9] = "lconst_0";
        mnemonics[10] = "lconst_1";
        mnemonics[11] = "fconst_0";
        mnemonics[12] = "fconst_1";
        mnemonics[13] = "fconst_2";
        mnemonics[14] = "dconst_0";
        mnemonics[15] = "dconst_1";
        mnemonics[16] = "bipush";
        mnemonics[17] = "sipush";
        mnemonics[18] = "ldc";
        mnemonics[19] = "ldc_w";
        mnemonics[20] = "ldc2_w";
        mnemonics[21] = "iload";
        mnemonics[22] = "lload";
        mnemonics[23] = "fload";
        mnemonics[24] = "dload";
        mnemonics[25] = "aload";
        mnemonics[26] = "iload_0";
        mnemonics[27] = "iload_1";
        mnemonics[28] = "iload_2";
        mnemonics[29] = "iload_3";
        mnemonics[30] = "lload_0";
        mnemonics[31] = "lload_1";
        mnemonics[32] = "lload_2";
        mnemonics[33] = "lload_3";
        mnemonics[34] = "fload_0";
        mnemonics[35] = "fload_1";
        mnemonics[36] = "fload_2";
        mnemonics[37] = "fload_3";
        mnemonics[38] = "dload_0";
        mnemonics[39] = "dload_1";
        mnemonics[40] = "dload_2";
        mnemonics[41] = "dload_3";
        mnemonics[42] = "aload_0";
        mnemonics[43] = "aload_1";
        mnemonics[44] = "aload_2";
        mnemonics[45] = "aload_3";
        mnemonics[46] = "iaload";
        mnemonics[47] = "laload";
        mnemonics[48] = "faload";
        mnemonics[49] = "daload";
        mnemonics[50] = "aaload";
        mnemonics[51] = "baload";
        mnemonics[52] = "caload";
        mnemonics[53] = "saload";
        mnemonics[54] = "istore";
        mnemonics[55] = "lstore";
        mnemonics[56] = "fstore";
        mnemonics[57] = "dstore";
        mnemonics[58] = "astore";
        mnemonics[59] = "istore_0";
        mnemonics[60] = "istore_1";
        mnemonics[61] = "istore_2";
        mnemonics[62] = "istore_3";
        mnemonics[63] = "lstore_0";
        mnemonics[64] = "lstore_1";
        mnemonics[65] = "lstore_2";
        mnemonics[66] = "lstore_3";
        mnemonics[67] = "fstore_0";
        mnemonics[68] = "fstore_1";
        mnemonics[69] = "fstore_2";
        mnemonics[70] = "fstore_3";
        mnemonics[71] = "dstore_0";
        mnemonics[72] = "dstore_1";
        mnemonics[73] = "dstore_2";
        mnemonics[74] = "dstore_3";
        mnemonics[75] = "astore_0";
        mnemonics[76] = "astore_1";
        mnemonics[77] = "astore_2";
        mnemonics[78] = "astore_3";
        mnemonics[79] = "iastore";
        mnemonics[80] = "lastore";
        mnemonics[81] = "fastore";
        mnemonics[82] = "dastore";
        mnemonics[83] = "aastore";
        mnemonics[84] = "bastore";
        mnemonics[85] = "castore";
        mnemonics[86] = "sastore";
        mnemonics[87] = "pop";
        mnemonics[88] = "pop2";
        mnemonics[89] = "dup";
        mnemonics[90] = "dup_x1";
        mnemonics[91] = "dup_x2";
        mnemonics[92] = "dup2";
        mnemonics[93] = "dup2_x1";
        mnemonics[94] = "dup2_x2";
        mnemonics[95] = "swap";
        mnemonics[96] = "iadd";
        mnemonics[97] = "ladd";
        mnemonics[98] = "fadd";
        mnemonics[99] = "dadd";
        mnemonics[100] = "isub";
        mnemonics[101] = "lsub";
        mnemonics[102] = "fsub";
        mnemonics[103] = "dsub";
        mnemonics[104] = "imul";
        mnemonics[105] = "lmul";
        mnemonics[106] = "fmul";
        mnemonics[107] = "dmul";
        mnemonics[108] = "idiv";
        mnemonics[109] = "ldiv";
        mnemonics[110] = "fdiv";
        mnemonics[111] = "ddiv";
        mnemonics[112] = "irem";
        mnemonics[113] = "lrem";
        mnemonics[114] = "frem";
        mnemonics[115] = "drem";
        mnemonics[116] = "ineg";
        mnemonics[117] = "lneg";
        mnemonics[118] = "fneg";
        mnemonics[119] = "dneg";
        mnemonics[120] = "ishl";
        mnemonics[121] = "lshl";
        mnemonics[122] = "ishr";
        mnemonics[123] = "lshr";
        mnemonics[124] = "iushr";
        mnemonics[125] = "lushr";
        mnemonics[126] = "iand";
        mnemonics[127] = "land";
        mnemonics[128] = "ior";
        mnemonics[129] = "lor";
        mnemonics[130] = "ixor";
        mnemonics[131] = "lxor";
        mnemonics[132] = "iinc";
        mnemonics[133] = "i2l";
        mnemonics[134] = "i2f";
        mnemonics[135] = "i2d";
        mnemonics[136] = "l2i";
        mnemonics[137] = "l2f";
        mnemonics[138] = "l2d";
        mnemonics[139] = "f2i";
        mnemonics[140] = "f2l";
        mnemonics[141] = "f2d";
        mnemonics[142] = "d2i";
        mnemonics[143] = "d2l";
        mnemonics[144] = "d2f";
        mnemonics[145] = "i2b";
        mnemonics[146] = "i2c";
        mnemonics[147] = "i2s";
        mnemonics[148] = "lcmp";
        mnemonics[149] = "fcmpl";
        mnemonics[150] = "fcmpg";
        mnemonics[151] = "dcmpl";
        mnemonics[152] = "dcmpg";
        mnemonics[153] = "ifeq";
        mnemonics[154] = "ifne";
        mnemonics[155] = "iflt";
        mnemonics[156] = "ifge";
        mnemonics[157] = "ifgt";
        mnemonics[158] = "ifle";
        mnemonics[159] = "if_icmpeq";
        mnemonics[160] = "if_icmpne";
        mnemonics[161] = "if_icmplt";
        mnemonics[162] = "if_icmpge";
        mnemonics[163] = "if_icmpgt";
        mnemonics[164] = "if_icmple";
        mnemonics[165] = "if_acmpeq";
        mnemonics[166] = "if_acmpne";
        mnemonics[167] = "goto";
        mnemonics[168] = "jsr";
        mnemonics[169] = "ret";
        mnemonics[170] = "tableswitch";
        mnemonics[171] = "lookupswitch";
        mnemonics[172] = "ireturn";
        mnemonics[173] = "lreturn";
        mnemonics[174] = "freturn";
        mnemonics[175] = "dreturn";
        mnemonics[176] = "areturn";
        mnemonics[177] = "return";
        mnemonics[178] = "getstatic";
        mnemonics[179] = "putstatic";
        mnemonics[180] = "getfield";
        mnemonics[181] = "putfield";
        mnemonics[182] = "invokevirtual";
        mnemonics[183] = "invokespecial";
        mnemonics[184] = "invokestatic";
        mnemonics[185] = "invokeinterface";
        mnemonics[186] = "invokedynamic";
        mnemonics[187] = "new";
        mnemonics[188] = "newarray";
        mnemonics[189] = "anewarray";
        mnemonics[190] = "arraylength";
        mnemonics[191] = "athrow";
        mnemonics[192] = "checkcast";
        mnemonics[193] = "instanceof";
        mnemonics[194] = "monitorenter";
        mnemonics[195] = "monitorexit";
        mnemonics[196] = "wide";
        mnemonics[197] = "multianewarray";
        mnemonics[198] = "ifnull";
        mnemonics[199] = "ifnonnull";
        mnemonics[200] = "goto_w";
        mnemonics[201] = "jsr_w";
    }

    public boolean isJsr() {
        return false;
    }

    public static Instruction createLongLoad(int ba, LocalVariableProvider localVariableProvider, int bb) {
        return new LocalVariableInstruction(ba, LocalVariableAccessKind.LONG_LOAD, localVariableProvider, bb);
    }

    public boolean isFieldAccess() {
        return false;
    }

    public static Instruction createLongConstantLoad(long ba, ConstantPool constantPool1, List list1) {
        return new ConstantRefInstruction(20, constantPool1.getOrAddLongConstant(ba, list1));
    }

    public abstract boolean pushesWithoutPopping();

    public static Instruction createStringConstantLoad(String string, ConstantPool constantPool1, List list1) {
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(string, list1, false);
        return new ConstantRefInstruction(19, resolvedStringConstant);
    }

    public boolean isStoreTo(int ba) {
        return false;
    }
}
