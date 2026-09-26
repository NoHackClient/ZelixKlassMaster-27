package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.attribute.ExceptionTableEntry;

public class TryCatchBlockInfo {
    public final MethodFlowAnalyzer analyzer;
    public final int entryIndex;
    public final int startIndex;
    public final int endIndex;
    public final int handlerIndex;
    public final String catchTypeDescriptor;
    public final boolean isCatchAll;
    public final BasicBlock handlerBlock;
    public final ExceptionTableEntry exceptionTableEntry;

    public TryCatchBlockInfo(
            MethodFlowAnalyzer methodFlowAnalyzer,
            int entryIndex,
            int startIndex,
            int endIndex,
            int handlerIndex,
            String string,
            boolean isCatchAll,
            BasicBlock basicBlock,
            ExceptionTableEntry exceptionTableEntry1
    ) {
        this.analyzer = methodFlowAnalyzer;
        this.entryIndex = entryIndex;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.handlerIndex = handlerIndex;
        this.catchTypeDescriptor = "L" + string + ";";
        this.isCatchAll = isCatchAll;
        this.handlerBlock = basicBlock;
        this.exceptionTableEntry = exceptionTableEntry1;
    }
}
