package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.util.NonNullList;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NaturalLoop implements Comparable {
    private List latchBlocks = new ArrayList();
    private Set loopBlocks = ZkmUtils.createHashSet();
    private BasicBlock header;
    private BasicBlock exitBranchTarget;
    public BasicBlock followBlock;

    public int compareHeaderPosition(NaturalLoop naturalLoop1) {
        return this.header.getStartIndex() - naturalLoop1.header.getStartIndex();
    }

    private void addMarkedBlocks(BitSet bitSet, List list1) {
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            if (bitSet.get(i)) {
                this.loopBlocks.add(list1.get(i));
            }
        }
    }

    public NaturalLoop(BasicBlock basicBlock, BasicBlock basicBlock1) {
        this.header = basicBlock;
        this.latchBlocks.add(basicBlock1);
        if (basicBlock1.isFirstOfTwoSuccessors(basicBlock)) {
            this.exitBranchTarget = basicBlock1.getSecondSuccessor();
            if (basicBlock.getLastSuccessor() != this.exitBranchTarget) {
                this.followBlock = basicBlock.getLastSuccessor();
            } else {
                this.followBlock = basicBlock;
            }
        } else {
            this.exitBranchTarget = basicBlock.getSecondSuccessor();
            this.followBlock = basicBlock.getFirstSuccessor();
        }
    }

    public List getLatchBlocks() {
        return new NonNullList(this.latchBlocks);
    }

    public BasicBlock getExitBranchTarget() {
        return this.exitBranchTarget;
    }

    public boolean containsBlock(Object object) {
        return this.loopBlocks.contains(object);
    }

    public boolean containsInstruction(Integer integer) {
        Iterator iterator = this.loopBlocks.iterator();

        while (iterator.hasNext()) {
            if (((BasicBlock) iterator.next()).containsIndex(integer)) {
                return true;
            }
        }

        return false;
    }

    public BasicBlock getFirstLatchBlock() {
        return (BasicBlock) this.latchBlocks.get(0);
    }

    public void computeLoopBlocks(List list1, TwoKeyMap twoKeyMap) {
        HashSet hashSet = ZkmUtils.createHashSet();
        BitSet bitSet = new BitSet(list1.size());
        Iterator iterator = this.latchBlocks.iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock = (BasicBlock) iterator.next();
            bitSet.clear();
            this.collectBlocksToHeader(basicBlock, bitSet, hashSet, list1);
        }

        if (twoKeyMap != null) {
            iterator = twoKeyMap.keySet().iterator();

            while (iterator.hasNext()) {
                BasicBlock basicBlock3 = (BasicBlock) iterator.next();
                if (this.loopBlocks.contains(basicBlock3)) {
                    Iterator iterator1 = twoKeyMap.getInnerMap(basicBlock3).keySet().iterator();

                    while (iterator1.hasNext()) {
                        BasicBlock basicBlock1 = (BasicBlock) iterator1.next();
                        Iterator iterator2 = this.latchBlocks.iterator();

                        while (iterator2.hasNext()) {
                            BasicBlock basicBlock2 = (BasicBlock) iterator2.next();
                            bitSet.clear();
                            if (basicBlock1.canReach(basicBlock2, bitSet)) {
                                this.loopBlocks.add(basicBlock1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addLatchBlock(Object object) {
        this.latchBlocks.add(object);
    }

    public BasicBlock getFollowBlock() {
        return this.followBlock;
    }

    private void collectBlocksToHeader(BasicBlock basicBlock, BitSet bitSet, Set set1, List list1) {
        if (set1.add(basicBlock)) {
            bitSet.set(basicBlock.getIndex());
            if (basicBlock == this.header) {
                this.addMarkedBlocks(bitSet, list1);
            } else {
                List list2 = basicBlock.getPredecessors();
                if (list2 != null) {
                    int ba = list2.size();
                    Iterator iterator = list2.iterator();

                    while (iterator.hasNext()) {
                        BasicBlock basicBlock1 = (BasicBlock) iterator.next();
                        this.collectBlocksToHeader(basicBlock1, ba > 0 ? (BitSet) bitSet.clone() : bitSet, set1, list1);
                    }
                }
            }
        } else {
            this.addMarkedBlocks(bitSet, list1);
        }
    }

    public BasicBlock getHeader() {
        return this.header;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareHeaderPosition((NaturalLoop) object);
    }
}
