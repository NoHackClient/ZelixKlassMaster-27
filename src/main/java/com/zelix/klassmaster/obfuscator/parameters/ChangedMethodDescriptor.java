package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChangedMethodDescriptor {
    public Map localSlotsByMethod;
    private final String newDescriptor;
    private final AddedParameter[] addedParameters;

    public int countByteParams() {
        int ba = 0;
        AddedParameter[] addedParameters1 = this.addedParameters;
        int bb = addedParameters1.length;

        for (int i = 0; i < bb; i++) {
            if (addedParameters1[i].getTypeChar() == 'B') {
                ba++;
            }
        }

        return ba;
    }

    public AddedParameter[] getAddedParameters() {
        return this.addedParameters.clone();
    }

    public int getKeyBitCount(int ba) {
        char typeChar = this.addedParameters[ba].getTypeChar();
        byte bb;
        if (typeChar == 'B') {
            bb = 8;
        } else {
            byte bg;
            if (typeChar != 'S') {
                if (typeChar != 'C') {
                    if (typeChar == 'J') {
                        return 64 - (ba == 0 ? this.addedParameters[1].getBitWidth() : this.addedParameters[0].getBitWidth());
                    }

                    if (this.addedParameters.length == 2) {
                        return 32;
                    }

                    if (ba == 0) {
                        return 32;
                    }

                    int bd = this.countByteParams();
                    int be = this.countIntParams();
                    int bf = this.indexOfIntParam();
                    if (ba == bf) {
                        return 32;
                    }

                    if (be == 3) {
                        return 16;
                    }

                    if (bd == 1) {
                        bb = 24;
                    } else {
                        bb = 16;
                    }

                    return bb;
                }

                bg = 16;
            } else {
                bg = 16;
            }

            bb = bg;
        }

        return bb;
    }

    public ChangedMethodDescriptor(String string, AddedParameter[] addedParameters1) {
        String string1 = MethodSignature.getReturnPart(string);
        List list1 = MethodSignature.splitParameterDescriptors(string);
        int ba = list1.size() + addedParameters1.length;
        int bb = addedParameters1.length;
        int bc = 0;
        Iterator iterator = list1.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');

        for (int i = 0; i < ba; i++) {
            while (bc < bb && addedParameters1[bc].getIndex() == i) {
                stringBuilder.append(addedParameters1[bc].getTypeChar());
                bc++;
                i++;
            }

            if (iterator.hasNext()) {
                String string2 = (String) iterator.next();
                stringBuilder.append(string2);
            }
        }

        stringBuilder.append(')');
        stringBuilder.append(string1);
        this.newDescriptor = stringBuilder.toString();
        this.addedParameters = addedParameters1;
    }

    public void registerLocalSlot(MethodBytecode methodBytecode1, int ba, LocalVariableSlot localVariableSlot1) {
        if (this.localSlotsByMethod == null) {
            if (HiddenOptionFlags.USE_PARALLEL) {
                this.localSlotsByMethod = new ConcurrentHashMap(13);
            } else {
                this.localSlotsByMethod = ZkmUtils.createHashMap(13);
            }
        }

        IndexedLocalSlot[] indexedLocalSlots = (IndexedLocalSlot[]) this.localSlotsByMethod.get(methodBytecode1);
        if (indexedLocalSlots == null) {
            indexedLocalSlots = new IndexedLocalSlot[this.addedParameters.length];
            this.localSlotsByMethod.put(methodBytecode1, indexedLocalSlots);
        }

        indexedLocalSlots[ba] = new IndexedLocalSlot(localVariableSlot1, ba);
    }

    public int getBitOffset(int ba) {
        int bb = 0;
        int bc = this.getKeyBitCount(0);
        if (ba == 1) {
            bb = bc;
        } else if (ba == 2) {
            int bd = this.getKeyBitCount(1);
            bb = bc + bd;
        }

        return bb;
    }

    public ChangedMethodDescriptor(List list1, String string, char[] ba, int[] bb) {
        this.addedParameters = new AddedParameter[ba.length];

        for (int i = 0; i < ba.length; i++) {
            this.addedParameters[i] = new AddedParameter(bb[i], ba[i]);
        }

        int bg = ba.length;
        int bd = list1.size() + ba.length;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        int be = 0;
        Iterator iterator = list1.iterator();

        for (int i = 0; i < bd; i++) {
            while (be < bg && bb[be] == i) {
                stringBuilder.append(ba[be]);
                be++;
                i++;
            }

            if (iterator.hasNext()) {
                String string1 = (String) iterator.next();
                stringBuilder.append(string1);
            }
        }

        stringBuilder.append(')');
        stringBuilder.append(string);
        this.newDescriptor = stringBuilder.toString();
    }

    public static int[] computeLocalIndices(ChangedMethodDescriptor changedMethodDescriptor, boolean bl) {
        List list1 = ConstantPoolEntry.getParameterTypes(changedMethodDescriptor.newDescriptor);
        int ba = list1.size();
        int bb = changedMethodDescriptor.addedParameters.length;
        int[] bc = new int[bb];
        int bd = bl ? 0 : 1;
        int be = 0;
        int index = changedMethodDescriptor.addedParameters[0].getIndex();

        for (int i = 0; i < ba; i++) {
            if (i == index) {
                bc[be] = bd;
                if (++be < bb) {
                    index = changedMethodDescriptor.addedParameters[be].getIndex();
                } else {
                    index = -1;
                }
            }

            String string = (String) list1.get(i);
            bd++;
            if (string.equals("J") || string.equals("D")) {
                bd++;
            }
        }

        return bc;
    }

    public char getTypeCharAt(int ba) {
        return this.addedParameters[ba].getTypeChar();
    }

    public boolean hasMultipleParams() {
        return this.addedParameters.length > 1;
    }

    public String getDescriptor() {
        return this.newDescriptor;
    }

    public int indexOfIntParam() {
        int ba = 0;
        AddedParameter[] addedParameters1 = this.addedParameters;
        int bb = addedParameters1.length;

        for (int i = 0; i < bb; i++) {
            if (addedParameters1[i].getTypeChar() == 'I') {
                return ba;
            }

            ba++;
        }

        return -1;
    }

    public IndexedLocalSlot[] getLocalSlots(Object object) {
        return (IndexedLocalSlot[]) this.localSlotsByMethod.get(object);
    }

    public int getUnusedBitCount(int ba) {
        return 64 - this.getKeyBitCount(ba);
    }

    public AddedParameter getLastParam() {
        return this.addedParameters[this.addedParameters.length - 1];
    }

    public int getParamIndex(int ba) {
        return this.addedParameters[ba].getIndex();
    }

    public int getParamCount() {
        return ConstantPoolEntry.getParameterTypes(this.newDescriptor).size();
    }

    public boolean hasNoAddedParams() {
        return this.addedParameters.length == 0;
    }

    public boolean isLongParam(int ba) {
        return this.addedParameters[ba].getTypeChar() == 'J';
    }

    public int[] getParamIndices() {
        int ba = this.addedParameters.length;
        int[] bb = new int[ba];

        for (int i = 0; i < ba; i++) {
            bb[i] = this.addedParameters[i].getIndex();
        }

        return bb;
    }

    public int getShiftCount(int ba) {
        return 64 - this.getKeyBitCount(ba);
    }

    public AddedParameter getFirstParam() {
        return this.addedParameters[0];
    }

    public int getAddedParamCount() {
        return this.addedParameters.length;
    }

    public int getKeyBitOffset(int ba) {
        int bb;
        if (ba == 0) {
            bb = 0;
        } else {
            int bc = this.getKeyBitCount(0);
            if (ba == 1) {
                bb = bc;
            } else {
                int bd = this.getKeyBitCount(1);
                bb = bc + bd;
            }
        }

        return bb;
    }

    public int countIntParams() {
        int ba = 0;
        AddedParameter[] addedParameters1 = this.addedParameters;
        int bb = addedParameters1.length;

        for (int i = 0; i < bb; i++) {
            if (addedParameters1[i].getTypeChar() == 'I') {
                ba++;
            }
        }

        return ba;
    }

    public static String buildParameterDescriptor(List list1, AddedParameter[] addedParameters1) {
        int ba = list1.size() + addedParameters1.length;
        int bb = addedParameters1.length;
        StringBuilder stringBuilder = new StringBuilder();
        int bc = 0;
        int bd = 0;
        AddedParameter addedParameter;
        StringBuilder stringBuilder1;
        char bf;
        if (addedParameters1.length == 0) {
            addedParameter = null;
            stringBuilder1 = stringBuilder;
            bf = '(';
        } else {
            addedParameter = addedParameters1[0];
            stringBuilder1 = stringBuilder;
            bf = '(';
        }

        stringBuilder1.append(bf);

        for (int i = 0; i < ba; i++) {
            if (addedParameter != null && i == addedParameter.getIndex()) {
                stringBuilder.append(addedParameter.getTypeChar());
                if (++bd < bb) {
                    addedParameter = addedParameters1[bd];
                } else {
                    addedParameter = null;
                }
            } else {
                String string = (String) list1.get(bc);
                stringBuilder.append(string);
                bc++;
            }
        }

        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
