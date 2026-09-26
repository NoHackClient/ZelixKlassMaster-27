package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SwitchInstruction extends Instruction implements JumpingInstruction {
    public int switchOffset;
    public List targetOffsets;
    public LabelInstruction[] targetLabels;
    public int paddingSize;

    @Override
    public final boolean consumesStackSlot(int ba, int bb) {
        return ba >= bb;
    }

    @Override
    public int getStackDelta() {
        return -1;
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_JUMP_DESTINATION;
    }

    public List getTargetLabels() {
        ArrayList arrayList = new ArrayList(this.targetLabels.length);

        for (LabelInstruction labelInstruction : this.targetLabels) {
            arrayList.add(labelInstruction);
        }

        return arrayList;
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return this.getMnemonic();
    }

    @Override
    public boolean isJump() {
        return true;
    }

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        int ba = this.targetLabels.length;
        labelInstruction.setVisible();

        for (int i = 0; i < ba; i++) {
            if (((Integer) this.targetOffsets.get(i)).equals(integer) && this.targetLabels[i] == null) {
                this.targetLabels[i] = labelInstruction;
            }
        }
    }

    public SwitchInstruction() {
        super(170);
    }

    @Override
    public final boolean isExit() {
        return false;
    }

    @Override
    public void setOffset(int switchOffset) {
        this.switchOffset = switchOffset;
        int bb = computePadding(switchOffset);
        this.paddingSize = bb;
    }

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        for (LabelInstruction labelInstruction : this.targetLabels) {
            setMultiMap.addValue(labelInstruction, this);
        }
    }

    @Override
    public boolean pushesWithoutPopping() {
        return false;
    }

    public SwitchInstruction(int ba, ClassFileInputStream classFileInputStream, int switchOffset) throws IOException {
        super(ba);
        this.switchOffset = switchOffset;
        this.paddingSize = computePadding(switchOffset);
        classFileInputStream.skip(this.paddingSize);
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        super.writeTo(dataOutputStream);
        int ba = 0;
        int bb = 0;

        for (int i = this.paddingSize; bb < i; i = this.paddingSize) {
            dataOutputStream.writeByte(0);
            bb = ++ba;
        }

        LabelInstruction labelInstruction = this.targetLabels[this.targetLabels.length - 1];
        dataOutputStream.writeInt(labelInstruction.getOffset() - this.switchOffset);
    }

    @Override
    public final boolean canFallThrough() {
        return false;
    }

    public static int computePadding(int ba) {
        return (4 - (ba + 1) % 4) % 4;
    }

    @Override
    public boolean pushesWideValue() {
        return false;
    }

    @Override
    public final boolean requiresTypedValueAt(Object object, Object object2, Object object1) {
        int ba = (Integer) object1;
        return (Integer) object >= ba;
    }

    @Override
    public boolean pushesValue() {
        return false;
    }

    @Override
    public final StackFrameState computeFrameAfter(StackFrameState stackFrameState, Object object, Object object1, Object object2) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState.getStack();
        VerifierType[] verifierTypes1 = stackFrameState.getLocals();
        int ba = verifierTypes.length;
        VerifierType[] verifierTypes2 = VerifierType.createArray(ba - 1);
        System.arraycopy(verifierTypes, 0, verifierTypes2, 0, ba - 1);
        return new StackFrameState(verifierTypes2, verifierTypes1, stackFrameState.getSubroutineLocals(), stackFrameState.getHeldMonitors());
    }

    @Override
    public final void addSuccessorBlocks(Map map1, ListMultimap listMultimap, List list1) {
        this.targetOffsets = null;

        for (int i = 0; i < this.targetLabels.length; i++) {
            LabelInstruction labelInstruction = this.targetLabels[i];
            BasicBlock basicBlock;
            if ((basicBlock = (BasicBlock) map1.get(labelInstruction)) == null) {
                basicBlock = new BasicBlock();
                list1.add(basicBlock);
                map1.put(labelInstruction, basicBlock);
            }

            listMultimap.addValue(this, basicBlock);
        }
    }
}
