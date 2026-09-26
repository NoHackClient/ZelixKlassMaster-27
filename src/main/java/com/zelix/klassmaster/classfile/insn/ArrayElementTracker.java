package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionArgumentKind;
import com.zelix.klassmaster.obfuscator.reflection.ValueFlowTracker;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ArrayElementTracker implements ValueFlowTracker {
    public final MethodFlowAnalyzer flowAnalyzer;
    public Map elementValuesByIndex;
    public List warnings;
    public NestedMultiMap visitedStates;

    public HashSet getElementValues(int ba) {
        return (HashSet) this.elementValuesByIndex.get(MethodFlowAnalyzer.INTEGER_CACHE.valueOf(ba));
    }

    @Override
    public ValueFlowTracker getTrackerFrom(MethodFlowAnalyzer methodFlowAnalyzer) {
        return methodFlowAnalyzer.createArrayElementTracker();
    }

    public ArrayElementTracker(MethodFlowAnalyzer methodFlowAnalyzer) {
        this.flowAnalyzer = methodFlowAnalyzer;
        this.elementValuesByIndex = ZkmUtils.createHashMap();
        this.warnings = new ArrayList();
        this.visitedStates = new NestedMultiMap(5, 97, 7);
    }

    public List getWarnings() {
        return this.warnings;
    }

    @Override
    public void markVisited(int ba, int bb, int bc) {
        this.visitedStates
                .addValue(MethodFlowAnalyzer.INTEGER_CACHE.valueOf(ba), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bb), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bc));
    }

    @Override
    public boolean traceAtInstruction(ValueTraceFrame valueTraceFrame, int ba, BasicBlock basicBlock) throws ZkmException, IOException {
        if (((Instruction) MethodFlowAnalyzer.getInstructions(this.flowAnalyzer).get(ba)).getOpcode() != 83) {
            return false;
        }

        StackFrameState stackFrameState = MethodFlowAnalyzer.getFrameStates(this.flowAnalyzer)[ba];
        ArrayList arrayList = new ArrayList();
        MethodFlowAnalyzer.collectIntConstantSources(
                this.flowAnalyzer, arrayList, basicBlock, ba, stackFrameState.getStack().length + 1, ZkmUtils.createHashSet()
        );
        int bb = stackFrameState.getStack().length + 2;
        HashSet hashSet = ZkmUtils.createHashSet(13);
        ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(this.flowAnalyzer, valueTraceFrame, ReflectionArgumentKind.CLASS_REFERENCE, 0);
        valueTraceFrame1.swapResults(hashSet);
        if (ba > basicBlock.getStartIndex()) {
            this.flowAnalyzer.traceStackValueSource(valueTraceFrame1, basicBlock, ba, bb);
        } else {
            List list1 = basicBlock.getPredecessors();

            for (int i = 0; i < list1.size(); i++) {
                BasicBlock basicBlock1 = (BasicBlock) list1.get(i);
                this.flowAnalyzer.traceStackValueSource(valueTraceFrame1, basicBlock1, basicBlock1.getEndIndex() + 1, bb);
            }
        }

        Integer integer = null;

        for (int i = 0; i < arrayList.size(); i++) {
            Object object = arrayList.get(i);
            if (!(object instanceof Integer)) {
                this.warnings.add("Could not determine element position at AALOAD : " + object);
                break;
            }

            if (integer == null) {
                integer = (Integer) object;
            } else if (!object.equals(integer)) {
                this.warnings.add("Multiple element positions possible at AALOAD : " + integer + ", " + object);
                break;
            }
        }

        if (integer != null) {
            this.elementValuesByIndex.put(integer, hashSet);
        } else {
            this.warnings.add("Could not determine element position at AALOAD");
        }

        return true;
    }

    @Override
    public boolean isVisited(int ba, int bb, int bc) {
        return this.visitedStates
                .containsValue(
                        MethodFlowAnalyzer.INTEGER_CACHE.valueOf(ba), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bb), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bc)
                );
    }
}
