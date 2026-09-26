package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;

public class ExceptionHandlerSpec {
    private final ResolvedClassConstant catchType;
    private final LabelInstruction startLabel;
    private final LabelInstruction endLabel;
    private final LabelInstruction handlerLabel;

    public ExceptionHandlerSpec(
            ResolvedClassConstant resolvedClassConstant, LabelInstruction labelInstruction, LabelInstruction labelInstruction1, LabelInstruction labelInstruction2
    ) {
        this.catchType = resolvedClassConstant;
        this.startLabel = labelInstruction;
        this.endLabel = labelInstruction1;
        this.handlerLabel = labelInstruction2;
    }

    public ResolvedClassConstant getCatchType() {
        return this.catchType;
    }

    public LabelInstruction getStartLabel() {
        return this.startLabel;
    }

    public LabelInstruction getHandlerLabel() {
        return this.handlerLabel;
    }

    public LabelInstruction getEndLabel() {
        return this.endLabel;
    }
}
