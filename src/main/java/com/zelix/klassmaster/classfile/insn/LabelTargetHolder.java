package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.util.SetMultiMap;

public interface LabelTargetHolder {
    InstructionUsageFlag getUsageFlag();

    void bindLabel(Integer integer, LabelInstruction labelInstruction);

    void registerLabelTargets(SetMultiMap setMultiMap);

    String getHolderTypeName(Object object, Object object1, Object object2);
}
