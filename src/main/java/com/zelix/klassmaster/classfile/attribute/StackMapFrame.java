package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.insn.InstructionUsageFlag;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LabelTargetHolder;
import com.zelix.klassmaster.util.SetMultiMap;

public abstract class StackMapFrame extends ClassFileComponent implements LabelTargetHolder {
    public int offset;
    public LabelInstruction label;
    public boolean valid = true;

    public LabelInstruction getLabel() {
        return this.label;
    }

    @Override
    public abstract void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc);

    @Override
    public void registerLabelTargets(SetMultiMap setMultiMap) {
        setMultiMap.addValue(this.label, this);
    }

    public abstract int getFrameSize();

    @Override
    public final void bindLabel(Integer integer, LabelInstruction labelInstruction) {
        this.label = labelInstruction;
    }

    public StackMapFrame(ClassFileComponent classFileComponent) {
        super(classFileComponent);
    }

    public final boolean isValid() {
        return this.valid;
    }

    @Override
    public InstructionUsageFlag getUsageFlag() {
        return InstructionUsageFlag.SET_USAGE_STACK_MAP;
    }
}
