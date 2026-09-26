package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.insn.InstructionUsageFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;
import com.zelix.klassmaster.util.SetMultiMap;

import java.util.HashSet;

public class LocalVarTargetEntry implements LabelTargetHolder {
    public int endPc;
    private int localIndex;
    public int startPc;
    public LabelInstruction startLabel;
    public LabelInstruction endLabel;
    public final TypeAnnotation typeAnnotation;

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        setMultiMap.addValue(this.startLabel, this);
        setMultiMap.addValue(this.endLabel, this);
    }

    public void setLocalIndex(int localIndex) {
        this.localIndex = localIndex;
    }

    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    public int getLocalIndex() {
        return this.localIndex;
    }

    public boolean hasLabels() {
        return this.startLabel != null && this.endLabel != null;
    }

    public int getLength() {
        return this.startLabel != null && this.endLabel != null ? this.endLabel.getOffset() - this.startLabel.getOffset() : this.endPc - this.startPc;
    }

    @Override
    public String getHolderTypeName(Object object, Object object1, Object object2) {
        return this.typeAnnotation.getEnclosingAttribute().getAttributeName();
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_ANNOTATION;
    }

    @Override
    public void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        if (this.startPc == integer) {
            this.startLabel = labelInstruction;
        }

        if (this.endPc == integer) {
            this.endLabel = labelInstruction;
        }
    }

    public int getStartPc() {
        return this.startPc;
    }

    public boolean unlinkIfLabelRemoved(HashSet hashSet, SetMultiMap setMultiMap) {
        SetMultiMap setMultiMap1;
        LabelInstruction labelInstruction;
        if (!hashSet.contains(this.startLabel)) {
            if (!hashSet.contains(this.endLabel)) {
                return false;
            }

            setMultiMap1 = setMultiMap;
            labelInstruction = this.startLabel;
        } else {
            setMultiMap1 = setMultiMap;
            labelInstruction = this.startLabel;
        }

        setMultiMap1.removeValue(labelInstruction, this);
        setMultiMap.removeValue(this.endLabel, this);
        TypeAnnotation.clearUnsharedLabelUsageOf(this.typeAnnotation, this.startLabel, setMultiMap);
        TypeAnnotation.clearUnsharedLabelUsageOf(this.typeAnnotation, this.endLabel, setMultiMap);
        return true;
    }

    public LabelInstruction getStartLabel() {
        return this.startLabel;
    }

    public void setEndPc(int endPc) {
        this.endPc = endPc;
    }

    public int getEndPc() {
        return this.endPc;
    }

    public LocalVarTargetEntry(TypeAnnotation typeAnnotation1) {
        this.typeAnnotation = typeAnnotation1;
    }
}
