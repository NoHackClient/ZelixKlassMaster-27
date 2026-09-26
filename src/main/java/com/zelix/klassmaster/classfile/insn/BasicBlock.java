package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.license.EncodedKeyTable;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.EmptyEnumeration;
import com.zelix.klassmaster.util.NonNullList;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BasicBlock extends EncodedKeyTable implements Comparable {
    public static int integrityState;
    public List jsrCallers;
    private BasicBlock jsrReturnBlock;
    private BasicBlock subroutineEntry;
    public List retBlocks;
    private int index;
    private int startIndex;
    private List predecessors;
    private int endIndex;
    private List exceptionHandlers;
    private List successors;
    private String label;
    private int minDistanceFromEntry = -1;

    public void setLabel(String string) {
        this.label = string;
    }

    public Enumeration enumerateExceptionHandlers() {
        return this.exceptionHandlers != null ? Collections.enumeration(this.exceptionHandlers) : new EmptyEnumeration();
    }

    public BasicBlock getLastSuccessor() {
        return this.successors != null ? (BasicBlock) this.successors.get(this.successors.size() - 1) : null;
    }

    public boolean searchPredecessors(BasicBlock basicBlock1, BitSet bitSet) {
        if (bitSet.get(basicBlock1.getIndex())) {
            return false;
        }

        bitSet.set(basicBlock1.getIndex());
        if (basicBlock1.predecessors == null) {
            return false;
        }

        boolean bl = false;
        int ba = basicBlock1.predecessors.size();

        for (int i = 0; i < ba; i++) {
            BasicBlock basicBlock2 = (BasicBlock) basicBlock1.predecessors.get(i);
            if (basicBlock2 == this) {
                return true;
            }

            bl = this.searchPredecessors(basicBlock2, bitSet);
            if (bl) {
                break;
            }
        }

        return bl;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public Instruction findLastRealInstruction(List list1) {
        for (int i = this.endIndex; i > this.startIndex; i += -1) {
            if (!((Instruction) list1.get(i)).isLabel()) {
                return (Instruction) list1.get(i);
            }
        }

        return null;
    }

    public List getPredecessors() {
        return this.predecessors;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getInstructionCount() {
        return this.endIndex - this.startIndex + 1;
    }

    public void setJsrReturnBlock(BasicBlock basicBlock1) {
        this.jsrReturnBlock = basicBlock1;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public boolean canReach(BasicBlock basicBlock1, BitSet bitSet) {
        bitSet.clear();
        return this.searchPredecessors(basicBlock1, bitSet);
    }

    public List getUnmodifiablePredecessors() {
        return this.predecessors != null ? Collections.unmodifiableList(this.predecessors) : null;
    }

    private static BitSet processLoopEdge(BasicBlock basicBlock, BlockBitSet blockBitSet, BasicBlock basicBlock1, Map map1, Set set1, Map map2) {
        if (set1.contains(basicBlock1)) {
            if (blockBitSet.bits.get(basicBlock1.getIndex())) {
                if (!map1.containsKey(basicBlock1)) {
                    NaturalLoop naturalLoop = new NaturalLoop(basicBlock1, basicBlock);
                    map1.put(basicBlock1, naturalLoop);
                } else {
                    NaturalLoop naturalLoop1 = (NaturalLoop) map1.get(basicBlock1);
                    naturalLoop1.addLatchBlock(basicBlock);
                }
            } else {
                basicBlock1.addPredecessor(basicBlock);
                ((BlockBitSet) map2.get(basicBlock1)).bits.set(basicBlock.getIndex());
            }

            return null;
        } else {
            set1.add(basicBlock1);
            basicBlock1.addPredecessor(basicBlock);
            BitSet bitSet = (BitSet) blockBitSet.bits.clone();
            bitSet.set(basicBlock.getIndex());
            bitSet.set(basicBlock1.getIndex());
            return bitSet;
        }
    }

    public List getSuccessorsCopy() {
        return this.successors != null ? new ArrayList(this.successors) : null;
    }

    public void addJsrCaller(Object object) {
        if (this.jsrCallers == null) {
            this.jsrCallers = new ArrayList();
        }

        this.jsrCallers.add(object);
    }

    public Enumeration enumerateSuccessors() {
        return this.successors != null ? Collections.enumeration(this.successors) : null;
    }

    public int findMatchingFrame(
            StackFrameState stackFrameState,
            Set set1,
            StackFrameState[] stackFrameStates,
            Set set2,
            BasicBlock basicBlock1,
            BasicBlock basicBlock2,
            BasicBlock basicBlock3,
            NonNullList nonNullList,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            ObservableHolder observableHolder,
            boolean bl,
            String string
    ) throws ZkmException, IOException {
        if (this == basicBlock3) {
            return -1;
        }

        int ba;
        if (this == basicBlock1 && ((Instruction) nonNullList.get(this.startIndex)).isLabel()) {
            ba = this.startIndex + 1;
        } else {
            ba = this.startIndex;
        }

        for (int i = this.endIndex - 1; i >= ba; i += -1) {
            if (stackFrameStates[i].isFullyInitialized()
                    && stackFrameState.isCompatibleWithOrUnchecked(
                    commonSuperTypeResolver1,
                    stackFrameStates[i],
                    set1,
                    bl,
                    string,
                    (
                            HiddenOptionFlags.skipFrameCompatibilityCheck
                                    || HiddenOptionFlags.FRAME_CHECK_MODE == null && !HiddenOptionFlags.ENFORCE_FRAME_CHECK
                                    || HiddenOptionFlags.FORCE_SKIP_FRAME_CHECK
                    )
                            && !this.checkClassDigests(((ClassRepository) commonSuperTypeResolver1.getHierarchyQuery()).getClassDigests(), scriptEnvironment1)
                            ? -1
                            : 1
            )
                    && stackFrameState.getSubroutineEntryLabel() == stackFrameStates[i].getSubroutineEntryLabel()) {
                observableHolder.setValue(this);
                return i;
            }
        }

        if (this != basicBlock2) {
            if (this.successors != null) {
                int be = this.successors.size();

                for (int i = 0; i < be; i++) {
                    BasicBlock basicBlock4 = (BasicBlock) this.successors.get(i);
                    if (set2.add(basicBlock4)) {
                        int bd = basicBlock4.findMatchingFrame(
                                stackFrameState,
                                set1,
                                stackFrameStates,
                                set2,
                                basicBlock1,
                                basicBlock2,
                                basicBlock3,
                                nonNullList,
                                commonSuperTypeResolver1,
                                scriptEnvironment1,
                                observableHolder,
                                bl,
                                string
                        );
                        if (bd != -1) {
                            return bd;
                        }
                    }
                }
            }

            if (this.jsrReturnBlock != null && set2.add(this.jsrReturnBlock)) {
                int bf = this.jsrReturnBlock
                        .findMatchingFrame(
                                stackFrameState,
                                set1,
                                stackFrameStates,
                                set2,
                                basicBlock1,
                                basicBlock2,
                                basicBlock3,
                                nonNullList,
                                commonSuperTypeResolver1,
                                scriptEnvironment1,
                                observableHolder,
                                bl,
                                string
                        );
                if (bf != -1) {
                    return bf;
                }
            }
        }

        return -1;
    }

    public boolean checkClassDigests(Set set1, ScriptEnvironment scriptEnvironment1) {
        synchronized (this.getClass()) {
            int bb;
            if (integrityState == 0) {
                if (set1.size() != 5) {
                    integrityState = -1;
                    scriptEnvironment1.incrementPendingMarkerCount();
                    return false;
                }

                int ba = 0;

                for (Iterator iterator = set1.iterator(); iterator.hasNext(); ba++) {
                    if (!ZkmUtils.xorStringWithKey((String) iterator.next(), "java/lang/Object").equalsIgnoreCase(this.getEncodedKey(ba))) {
                        integrityState = -1;
                        scriptEnvironment1.incrementPendingMarkerCount();
                        return false;
                    }
                }

                integrityState = 1;
                bb = integrityState;
            } else {
                bb = integrityState;
            }

            return bb == 1;
        }
    }

    public boolean hasRetBlocks() {
        return this.retBlocks != null && this.retBlocks.size() > 0;
    }

    public String getLabel() {
        return this.label;
    }

    public int getIndex() {
        return this.index;
    }

    public void addSuccessor(BasicBlock basicBlock1) {
        if (this.successors == null) {
            this.successors = new ArrayList();
        }

        if (!this.successors.contains(basicBlock1)) {
            this.successors.add(basicBlock1);
        }
    }

    public static int getIntegrityState() {
        return integrityState;
    }

    public boolean isHeader() {
        return this.label != null && this.label.equals("header");
    }

    public BasicBlock getSecondSuccessor() {
        return this.successors != null && this.successors.size() == 2 ? (BasicBlock) this.successors.get(1) : null;
    }

    public void setSubroutineEntry(BasicBlock basicBlock1) {
        this.subroutineEntry = basicBlock1;
    }

    public int getMinDistanceFromEntry() {
        if (this.minDistanceFromEntry == -1) {
            int instructionCount = this.getInstructionCount();
            int bb;
            if (this.predecessors != null && (bb = this.predecessors.size()) > 0) {
                int bc = Integer.MAX_VALUE;

                for (int i = 0; i < bb; i++) {
                    int minDistanceFromEntry = ((BasicBlock) this.predecessors.get(i)).getMinDistanceFromEntry();
                    if (minDistanceFromEntry < bc) {
                        bc = minDistanceFromEntry;
                    }
                }

                instructionCount += bc;
            }

            this.minDistanceFromEntry = instructionCount;
        }

        return this.minDistanceFromEntry;
    }

    public boolean hasPredecessors() {
        return this.predecessors != null && this.predecessors.size() > 0;
    }

    public void clearLinks() {
        this.predecessors = null;
        this.successors = null;
        this.jsrReturnBlock = null;
        this.subroutineEntry = null;
        this.exceptionHandlers = null;
    }

    public List getRetBlocks() {
        return this.retBlocks;
    }

    public static void findNaturalLoops(BlockBitSet blockBitSet, Map map1, Set set1, Map map2) {
        LinkedList linkedList = new LinkedList();
        LinkedList linkedList1 = new LinkedList();
        BasicBlock basicBlock = blockBitSet.block;
        if (basicBlock.successors != null) {
            int ba = basicBlock.successors.size();

            for (int i = 0; i < ba; i++) {
                BasicBlock basicBlock1 = (BasicBlock) basicBlock.successors.get(i);
                ObjectPair objectPair = new ObjectPair(basicBlock, basicBlock1);
                if (i == ba - 1) {
                    linkedList.addFirst(objectPair);
                } else {
                    linkedList1.addFirst(objectPair);
                }
            }
        }

        if (basicBlock.jsrReturnBlock != null) {
            ObjectPair objectPair2 = new ObjectPair(basicBlock, basicBlock.jsrReturnBlock);
            linkedList.addFirst(objectPair2);
        }

        while (!linkedList.isEmpty() || !linkedList1.isEmpty()) {
            ObjectPair objectPair3;
            if (!linkedList.isEmpty()) {
                objectPair3 = (ObjectPair) linkedList.remove();
            } else {
                objectPair3 = (ObjectPair) linkedList1.remove();
            }

            BasicBlock basicBlock3 = (BasicBlock) objectPair3.getFirst();
            BasicBlock basicBlock4 = (BasicBlock) objectPair3.getSecond();
            BlockBitSet blockBitSet2 = (BlockBitSet) map2.get(basicBlock3);
            BitSet bitSet = processLoopEdge(basicBlock3, blockBitSet2, basicBlock4, map1, set1, map2);
            if (bitSet != null) {
                BlockBitSet blockBitSet1 = new BlockBitSet(basicBlock4, bitSet);
                map2.put(basicBlock4, blockBitSet1);
                if (basicBlock4.successors != null) {
                    int bc = basicBlock4.successors.size();

                    for (int i = 0; i < bc; i++) {
                        BasicBlock basicBlock2 = (BasicBlock) basicBlock4.successors.get(i);
                        ObjectPair objectPair1 = new ObjectPair(basicBlock4, basicBlock2);
                        if (i == bc - 1) {
                            linkedList.addFirst(objectPair1);
                        } else {
                            linkedList1.addFirst(objectPair1);
                        }
                    }
                }

                if (basicBlock4.jsrReturnBlock != null) {
                    ObjectPair objectPair4 = new ObjectPair(basicBlock4, basicBlock4.jsrReturnBlock);
                    linkedList.addFirst(objectPair4);
                }
            }
        }
    }

    public void addPredecessor(BasicBlock basicBlock1) {
        if (this.predecessors == null) {
            this.predecessors = new ArrayList();
        }

        if (!this.predecessors.contains(basicBlock1)) {
            this.predecessors.add(basicBlock1);
        }
    }

    public Instruction getLastInstruction(NonNullList nonNullList) {
        int endIndex = this.endIndex;

        Instruction instruction1;
        do {
            instruction1 = (Instruction) nonNullList.get(endIndex);
            endIndex += -1;
        } while (instruction1.isLabel() && endIndex > this.startIndex);

        return instruction1;
    }

    public BasicBlock getJsrReturnBlock() {
        return this.jsrReturnBlock;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public List getJsrCallers() {
        return Collections.unmodifiableList(this.jsrCallers);
    }

    public void addRetBlock(Object object) {
        if (this.retBlocks == null) {
            this.retBlocks = new ArrayList();
        }

        this.retBlocks.add(object);
    }

    public boolean lastInstructionFallsThrough(NonNullList nonNullList) {
        int endIndex = this.endIndex;

        Instruction instruction1;
        do {
            instruction1 = (Instruction) nonNullList.get(endIndex);
            endIndex += -1;
        } while (instruction1.isLabel() && endIndex > this.startIndex);

        return instruction1.continuesToNext();
    }

    public boolean containsIndex(int ba) {
        return ba >= this.startIndex && ba <= this.endIndex;
    }

    public void sortSuccessors() {
        if (this.successors.size() > 1) {
            Collections.sort(this.successors);
            int ba = 0;

            for (int i = 0; i < this.successors.size(); i++) {
                if (((BasicBlock) this.successors.get(i)).getStartIndex() > this.getEndIndex()) {
                    ba = i;
                    break;
                }
            }

            BasicBlock basicBlock1 = (BasicBlock) this.successors.remove(ba);
            this.successors.add(basicBlock1);
        }
    }

    public boolean hasSuccessors() {
        return this.successors != null && this.successors.size() > 0;
    }

    public boolean isCatchBlock() {
        return this.label != null && this.label.startsWith("catchNode");
    }

    public int compareStart(BasicBlock basicBlock1) {
        if (this.startIndex < basicBlock1.startIndex) {
            return -1;
        } else {
            return this.startIndex == basicBlock1.startIndex ? 0 : 1;
        }
    }

    public boolean isFirstOfTwoSuccessors(BasicBlock basicBlock1) {
        return this.successors != null && this.successors.size() == 2 && this.successors.get(0) == basicBlock1;
    }

    public BasicBlock getSubroutineEntry() {
        return this.subroutineEntry;
    }

    public void addExceptionHandler(BasicBlock basicBlock1) {
        if (this.exceptionHandlers == null) {
            this.exceptionHandlers = new ArrayList();
        }

        this.exceptionHandlers.add(basicBlock1);
    }

    @Override
    public int compareTo(Object object) {
        return this.compareStart((BasicBlock) object);
    }

    public BasicBlock getFirstSuccessor() {
        return this.successors != null && this.successors.size() > 0 ? (BasicBlock) this.successors.get(0) : null;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
