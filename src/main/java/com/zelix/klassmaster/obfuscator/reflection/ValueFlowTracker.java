package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.insn.BasicBlock;
import com.zelix.klassmaster.classfile.insn.MethodFlowAnalyzer;
import com.zelix.klassmaster.classfile.insn.ValueTraceFrame;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface ValueFlowTracker {
    void markVisited(int ba, int bb, int bc);

    boolean traceAtInstruction(ValueTraceFrame valueTraceFrame, int ba, BasicBlock basicBlock) throws ZkmException, IOException;

    ValueFlowTracker getTrackerFrom(MethodFlowAnalyzer methodFlowAnalyzer);

    boolean isVisited(int ba, int bb, int bc);
}
