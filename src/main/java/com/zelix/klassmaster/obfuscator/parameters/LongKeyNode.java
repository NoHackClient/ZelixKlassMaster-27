package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.util.ListMultimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LongKeyNode implements EncryptionKeyChain {
    public static int nextId = 0;
    public LongKeyNode previous;
    public long xorMask;
    public int index;
    public LongKeyNode next;
    private final List keyHistory = new ArrayList();
    private final int[] bitLayout;
    private final int id;

    @Override
    public int hashCode() {
        return (int) MethodParameterChanger.extractLowPermutedBits(this.getCurrentKey(), 8, this.bitLayout);
    }

    public long getMiddleBitsAt(int ba) {
        return MethodParameterChanger.extractPermutedBits(this.getKeyAt(ba), 8, 55, this.bitLayout);
    }

    public long getLow56Bits() {
        return MethodParameterChanger.extractLowPermutedBits(this.getCurrentKey(), 56, this.bitLayout);
    }

    @Override
    public long pushKey(long ba) {
        long currentKey = this.getCurrentKey();
        int[] bitLayout = this.bitLayout;
        long bd = MethodParameterChanger.extractPermutedBits(currentKey, 8, 55, bitLayout);
        long be = currentKey ^ this.xorMask ^ ba;
        this.keyHistory.add(be);
        if (this.next != null) {
            this.next.pushKey(ba);
        }

        return bd;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LongKeyNode)) {
            return false;
        }
        LongKeyNode other = (LongKeyNode) object;
        return MethodParameterChanger.extractLowPermutedBits(this.getCurrentKey(), 56, this.bitLayout)
                == MethodParameterChanger.extractLowPermutedBits(other.getCurrentKey(), 56, other.bitLayout);
    }

    public int getHighByte() {
        return (int) MethodParameterChanger.extractPermutedBits(this.getCurrentKey(), 56, 63, this.bitLayout);
    }

    public boolean hasPrevious() {
        return this.previous != null;
    }

    public Long getKeyAt(int ba) {
        return (Long) this.keyHistory.get(ba);
    }

    @Override
    public void appendToChain(Object object) {
        LongKeyNode longKeyNode1 = (LongKeyNode) object;
        if (this != longKeyNode1) {
            if (this.next == null) {
                this.next = longKeyNode1;
                longKeyNode1.previous = this;
            } else {
                this.next.appendToChain(longKeyNode1);
            }
        }
    }

    public boolean hasSuccessorIn(Set set1) {
        for (LongKeyNode longKeyNode1 = (LongKeyNode) this.getNext(); longKeyNode1 != null; longKeyNode1 = (LongKeyNode) longKeyNode1.getNext()) {
            if (set1.contains(longKeyNode1)) {
                return true;
            }
        }

        return false;
    }

    public void addKey(long ba) {
        this.keyHistory.add(ba);
    }

    public String toKeyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getMixedKey());
        stringBuilder.append(',');
        stringBuilder.append(this.hashCode());
        return stringBuilder.toString();
    }

    public void copyBitLayout(Object object) {
        System.arraycopy(object, 0, this.bitLayout, 0, this.bitLayout.length);
    }

    @Override
    public long getMixedKey() {
        return MethodParameterChanger.extractPermutedBits(this.getCurrentKey(), 8, 55, this.bitLayout);
    }

    public int getIndex() {
        return this.index;
    }

    public long getXorMask() {
        return this.xorMask;
    }

    @Override
    public long getCurrentKey() {
        return (Long) this.keyHistory.get(this.keyHistory.size() - 1);
    }

    public boolean hasSuccessor(LongKeyNode longKeyNode1) {
        for (LongKeyNode longKeyNode2 = (LongKeyNode) this.getNext(); longKeyNode2 != null; longKeyNode2 = (LongKeyNode) longKeyNode2.getNext()) {
            if (longKeyNode2 == longKeyNode1) {
                return true;
            }
        }

        return false;
    }

    public LongKeyNode(long ba, int[] bitLayout, ListMultimap listMultimap, Set set1) {
        this.bitLayout = bitLayout;
        this.keyHistory.add(ba);
        this.id = nextId++;
        listMultimap.addValue(bitLayout, this);
        set1.add(bitLayout);
    }

    public LongKeyNode(LongKeyNode longKeyNode1, int[] bitLayout) {
        this.bitLayout = bitLayout;
        int highByte = longKeyNode1.getHighByte();
        long mixedKey = longKeyNode1.getMixedKey();
        int bd = longKeyNode1.hashCode();
        long be = MethodParameterChanger.composeKey(highByte, mixedKey, bd, bitLayout);
        this.keyHistory.add(be);
        this.id = nextId++;
    }

    public long extractBits() {
        long ba = 75742086517046L;
        ba = 105801543202538L ^ ba;
        int bb = (int) ((ba ^ 114453955205632L) >>> 32);
        int bc = (int) ((ba ^ 114453955205632L) << 32 >>> 48);
        int[] bitLayout = this.bitLayout;
        return MethodParameterChanger.extractPermutedBits(this.getCurrentKey(), 0, 6, bitLayout);
    }

    public LongKeyNode(int ba, long bb, int bc, int[] bitLayout, ListMultimap listMultimap, Set set1) {
        long be = MethodParameterChanger.composeKey(ba, bb, bc, bitLayout);
        this.bitLayout = bitLayout;
        this.keyHistory.add(be);
        this.id = nextId++;
        listMultimap.addValue(bitLayout, this);
        set1.add(bitLayout);
    }

    public EncryptionKeyChain getNext() {
        return this.next;
    }

    public void setXorMask(long xorMask) {
        this.xorMask = xorMask;
    }

    @Override
    public boolean isKeyed() {
        return true;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[] getBitLayout() {
        return this.bitLayout;
    }

    @Override
    public boolean isOrderedBefore(EncryptionKeyChain encryptionKeyChain) {
        if (this == encryptionKeyChain) {
            return true;
        } else if (encryptionKeyChain instanceof LongKeyNode) {
            LongKeyNode longKeyNode1 = (LongKeyNode) encryptionKeyChain;
            return this.getHighByte() - longKeyNode1.getHighByte() <= 0;
        } else {
            return true;
        }
    }
}
