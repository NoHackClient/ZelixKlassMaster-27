package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionArgumentKind;
import com.zelix.klassmaster.obfuscator.reflection.ValueFlowTracker;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class PropertiesLoadTracker implements ValueFlowTracker {
    public final MethodFlowAnalyzer analyzer;
    public NestedMultiMap visitedStates;
    public HashSet fileNameValues;

    public HashSet getFileNameValues() {
        return this.fileNameValues;
    }

    @Override
    public void markVisited(int ba, int bb, int bc) {
        this.visitedStates
                .addValue(MethodFlowAnalyzer.INTEGER_CACHE.valueOf(ba), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bb), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bc));
    }

    @Override
    public ValueFlowTracker getTrackerFrom(MethodFlowAnalyzer methodFlowAnalyzer) {
        return methodFlowAnalyzer.createPropertiesLoadTracker();
    }

    @Override
    public boolean isVisited(int ba, int bb, int bc) {
        return this.visitedStates
                .containsValue(
                        MethodFlowAnalyzer.INTEGER_CACHE.valueOf(ba), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bb), MethodFlowAnalyzer.INTEGER_CACHE.valueOf(bc)
                );
    }

    @Override
    public boolean traceAtInstruction(ValueTraceFrame valueTraceFrame, int ba, BasicBlock basicBlock) throws ZkmException, IOException {
        Instruction instruction1 = (Instruction) MethodFlowAnalyzer.getInstructions(this.analyzer).get(ba);
        if (instruction1.getOpcode() != 182) {
            return false;
        }

        ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
        if (resolvedMethodRef.getReferencedClassName().equals("java/util/Properties")
                && resolvedMethodRef.getSignatureString().equals("load(Ljava/io/InputStream;)V")) {
            boolean bl = false;
            int startIndex = basicBlock.getStartIndex();
            int bc = ba;

            while (!bl) {
                if (bc > startIndex) {
                    bc += -1;
                    Instruction instruction2 = (Instruction) MethodFlowAnalyzer.getInstructions(this.analyzer).get(bc);
                    if (instruction2.getOpcode() == 183) {
                        ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) ((ConstantRefInstruction) instruction2).getConstantPoolEntry();
                        String string = resolvedMethodRef1.getReferencedClassName();
                        String string1 = resolvedMethodRef1.getMemberName();

                        try {
                            if (string1.equals("<init>")
                                    && (
                                    valueTraceFrame.memberLookup.isSubclass(string, "java/io/InputStream")
                                            || valueTraceFrame.memberLookup.isSubclass(string, "java/io/Reader")
                            )) {
                                List list1 = resolvedMethodRef1.getParameterTypes();
                                if (list1.size() == 1) {
                                    String string2 = (String) list1.get(0);
                                    if (string2.length() > 2) {
                                        string2 = string2.substring(1, string2.length() - 1);
                                    }

                                    if (!string2.equals("java/io/InputStream") && !string2.equals("java/io/Reader")) {
                                        label81:
                                        if (string2.equals("java/lang/String")) {
                                            MethodFlowAnalyzer methodFlowAnalyzer;
                                            if (!string.equals("java/io/FileInputStream")) {
                                                if (!string.equals("java/io/FileReader")) {
                                                    break label81;
                                                }

                                                methodFlowAnalyzer = this.analyzer;
                                            } else {
                                                methodFlowAnalyzer = this.analyzer;
                                            }

                                            int bd = MethodFlowAnalyzer.getFrameStates(methodFlowAnalyzer)[bc].getStack().length + 1;
                                            ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(this.analyzer, valueTraceFrame, ReflectionArgumentKind.NORMAL, 0, true);
                                            valueTraceFrame1.swapResults(this.fileNameValues);
                                            if (bc > startIndex) {
                                                this.analyzer.traceStackValueSource(valueTraceFrame1, basicBlock, bc, bd);
                                            } else {
                                                List list2 = basicBlock.getPredecessors();

                                                for (int i = 0; i < list2.size(); i++) {
                                                    BasicBlock basicBlock1 = (BasicBlock) list2.get(i);
                                                    this.analyzer.traceStackValueSource(valueTraceFrame1, basicBlock1, basicBlock1.getEndIndex() + 1, bd);
                                                }
                                            }

                                            bl = true;
                                            continue;
                                        }

                                        bl = true;
                                    }
                                }
                            } else {
                                bl = true;
                            }
                        } catch (ClassFileLoadException classFileLoadException) {
                            bl = true;
                        }
                    } else {
                        bl = true;
                    }
                } else {
                    bl = true;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public PropertiesLoadTracker(MethodFlowAnalyzer methodFlowAnalyzer) {
        this.analyzer = methodFlowAnalyzer;
        this.visitedStates = new NestedMultiMap(5, 97, 7);
        this.fileNameValues = ZkmUtils.createHashSet(13);
    }
}
