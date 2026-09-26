package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.MethodSignature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class LocalVariableList implements LocalVariableProvider {
    public int[] shuffledIndices;
    public MethodBytecode methodBytecode;
    private List slots;

    public int getLastSlotIndex() {
        return this.slots.isEmpty() ? -1 : ((LocalVariableSlot) this.slots.get(this.slots.size() - 1)).getIndex();
    }

    public void clearWideFlagsFrom(int ba) {
        for (int i = ba; i < this.slots.size(); i++) {
            LocalVariableSlot localVariableSlot1 = (LocalVariableSlot) this.slots.get(i);
            localVariableSlot1.clearWideFirstHalf();
            localVariableSlot1.clearWideSecondHalf();
        }
    }

    public LocalVariableSlot insertSlot(int ba, boolean bl, int bb) {
        LocalVariableSlot localVariableSlot1;
        if (bl) {
            LocalVariableSlot localVariableSlot2 = new LocalVariableSlot(ba + 1, false, true, bb);
            this.slots.add(ba, localVariableSlot2);
            localVariableSlot1 = new LocalVariableSlot(ba, true, false, bb);
            this.slots.add(ba, localVariableSlot1);
        } else {
            localVariableSlot1 = new LocalVariableSlot(ba, bb);
            this.slots.add(ba, localVariableSlot1);
        }

        return localVariableSlot1;
    }

    public LocalVariableSlot getPenultimateSlot() {
        return this.slots.isEmpty() ? null : (LocalVariableSlot) this.slots.get(this.slots.size() - 2);
    }

    public int renumberSlots() {
        int ba = 0;

        for (int i = 0; i < this.slots.size(); i++) {
            ((LocalVariableSlot) this.slots.get(i)).replaceIndex(ba);
            ba++;
        }

        return ((LocalVariableSlot) this.slots.get(this.slots.size() - 1)).getIndex();
    }

    public int[] getShuffledIndices() {
        return this.shuffledIndices;
    }

    public boolean shuffleSlots(int ba, int bb, Random random1) {
        ArrayList arrayList = new ArrayList(this.slots.size());

        for (int i = ba; i < bb; i++) {
            ArrayList arrayList1 = new ArrayList();
            arrayList.add(arrayList1);
            LocalVariableSlot localVariableSlot1 = (LocalVariableSlot) this.slots.get(i);
            arrayList1.add(localVariableSlot1);
            int bc = i;

            while (localVariableSlot1.isWideFirstHalf() || localVariableSlot1.isWideSecondHalf() && bc < bb - 1) {
                localVariableSlot1 = (LocalVariableSlot) this.slots.get(++bc);
                if (!localVariableSlot1.isWideFirstHalf() && !localVariableSlot1.isWideSecondHalf()) {
                    break;
                }

                arrayList1.add(localVariableSlot1);
            }

            i += arrayList1.size() - 1;
        }

        boolean bl = false;
        if (arrayList.size() > 1) {
            Collections.shuffle(arrayList, random1);
            this.shuffledIndices = new int[bb - ba];
            int be = ba;
            Iterator iterator1 = arrayList.iterator();

            while (iterator1.hasNext()) {
                List list1 = (List) iterator1.next();
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    LocalVariableSlot localVariableSlot2 = (LocalVariableSlot) iterator.next();
                    this.shuffledIndices[be - ba] = localVariableSlot2.getIndex();
                    this.slots.set(be++, localVariableSlot2);
                }
            }

            this.renumberSlots();
            bl = true;
        }

        return bl;
    }

    public LocalVariableList(MethodBytecode methodBytecode1, int ba) {
        this.methodBytecode = methodBytecode1;
        this.slots = new ArrayList(ba);
        String string = this.methodBytecode.getMethod().getDescriptor();
        this.addParameterSlots(this.methodBytecode.isStatic(), string);
    }

    public static boolean isWideType(String string) {
        return string.equals("J") || string.equals("D");
    }

    public int getSlotCount() {
        return this.slots.size();
    }

    public void setMethodBytecode(MethodBytecode methodBytecode1) {
        this.methodBytecode = methodBytecode1;
    }

    @Override
    public LocalVariableSlot lookupSlot(int ba, LocalVariableAccessKind localVariableAccessKind, int bb) {
        LocalVariableSlot localVariableSlot1;
        if (this.slots.size() > ba) {
            localVariableSlot1 = (LocalVariableSlot) this.slots.get(ba);
            if (localVariableAccessKind.isWide()) {
                localVariableSlot1.clearWideSecondHalf();
                localVariableSlot1.markWideFirstHalf();
                if (this.slots.size() > ba + 1) {
                    LocalVariableSlot localVariableSlot2 = (LocalVariableSlot) this.slots.get(ba + 1);
                    localVariableSlot2.clearWideFirstHalf();
                    localVariableSlot2.markWideSecondHalf();
                } else {
                    LocalVariableSlot localVariableSlot4 = new LocalVariableSlot(ba + 1, false, true, bb);
                    this.slots.add(localVariableSlot4);
                }
            } else {
                if (localVariableSlot1.isWideFirstHalf()) {
                    LocalVariableSlot localVariableSlot5 = (LocalVariableSlot) this.slots.get(ba + 1);
                    localVariableSlot5.clearWideSecondHalf();
                    localVariableSlot5.clearWideFirstHalf();
                }

                localVariableSlot1.clearWideFirstHalf();
                localVariableSlot1.clearWideSecondHalf();
            }
        } else {
            for (int i = this.slots.size(); i < ba; i++) {
                LocalVariableSlot localVariableSlot3 = new LocalVariableSlot(i, bb);
                this.slots.add(localVariableSlot3);
            }

            if (localVariableAccessKind.isWide()) {
                localVariableSlot1 = new LocalVariableSlot(ba, true, false, bb);
                this.slots.add(localVariableSlot1);
                LocalVariableSlot localVariableSlot6 = new LocalVariableSlot(ba + 1, false, true, bb);
                this.slots.add(localVariableSlot6);
            } else {
                localVariableSlot1 = new LocalVariableSlot(ba, bb);
                this.slots.add(localVariableSlot1);
            }
        }

        return localVariableSlot1;
    }

    public LocalVariableSlot getSlotAt(int ba) {
        return !this.slots.isEmpty() && ba <= this.slots.size() - 1 ? (LocalVariableSlot) this.slots.get(ba) : null;
    }

    public void moveTrailingSlots(int ba, Integer integer) {
        int bb = this.slots.size();
        int bc = bb - ba;
        if (bc != 0) {
            ArrayList arrayList = new ArrayList(ba);

            for (int i = bc; i < bb; i++) {
                LocalVariableSlot localVariableSlot1 = (LocalVariableSlot) this.slots.remove(bc);
                arrayList.add(localVariableSlot1);
            }

            this.slots.addAll(integer, arrayList);
            this.renumberSlots();
        }
    }

    public LocalVariableList(boolean bl, String string, int ba) {
        this.slots = new ArrayList(ba);
        this.addParameterSlots(bl, string);
    }

    public LocalVariableSlot removeSlot(int ba) {
        LocalVariableSlot localVariableSlot1 = (LocalVariableSlot) this.slots.remove(ba);
        if (localVariableSlot1.isWideFirstHalf()) {
            LocalVariableSlot localVariableSlot2 = (LocalVariableSlot) this.slots.remove(ba);
        }

        return localVariableSlot1;
    }

    public void addParameterSlots(boolean bl, String string) {
        List list1 = MethodSignature.splitParameterDescriptors(string);
        int ba = 0;
        if (!bl) {
            ba++;
            this.slots.add(new LocalVariableSlot(0));
        }

        for (int i = 0; i < list1.size(); i++) {
            if (isWideType((String) list1.get(i))) {
                int bc = ba++;
                this.slots.add(new LocalVariableSlot(bc, true, false));
                int bd = ba++;
                this.slots.add(new LocalVariableSlot(bd, false, true));
            } else {
                int be = ba++;
                this.slots.add(new LocalVariableSlot(be));
            }
        }
    }
}
