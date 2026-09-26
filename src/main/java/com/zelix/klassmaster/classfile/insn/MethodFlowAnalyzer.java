package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.attribute.CodeAttributeBody;
import com.zelix.klassmaster.classfile.attribute.ExceptionRangeField;
import com.zelix.klassmaster.classfile.attribute.ExceptionTableEntry;
import com.zelix.klassmaster.classfile.attribute.StackMapAttribute;
import com.zelix.klassmaster.classfile.attribute.StackMapEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodType;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.MethodAnalysisException;
import com.zelix.klassmaster.exceptions.StackAnalysisException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.reflection.ClassDescriptorStringValue;
import com.zelix.klassmaster.obfuscator.reflection.InstructionReasonNote;
import com.zelix.klassmaster.obfuscator.reflection.LiteralStringValue;
import com.zelix.klassmaster.obfuscator.reflection.NullReflectionValue;
import com.zelix.klassmaster.obfuscator.reflection.PendingReflectionValue;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionAnalysisSwitchMap;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionApiMethod;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionArgumentKind;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionParamDetail;
import com.zelix.klassmaster.obfuscator.reflection.ValueFlowTracker;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerationBackedList;
import com.zelix.klassmaster.util.GenericTree;
import com.zelix.klassmaster.util.IdentityMapWrapper;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.NonNullList;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObjectTreeNode;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.UniqueWorkQueue;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class MethodFlowAnalyzer {
    public static final IntegerCache INTEGER_CACHE = IntegerCache.getInstance();
    private StackFrameState[] frameStates;
    public TwoKeyMap retReturnTargets;
    private BasicBlock[] blockByIndexCache;
    private List catchHandlerBlocks = new ArrayList();
    private List basicBlocks = new ArrayList();
    private ListMultimap tryCatchInfosByHandler = null;
    private TwoKeyMap handlerCoveredBlocks = null;
    private boolean closed = false;
    private MethodBytecode methodBytecode;
    private String methodDescription;
    private CommonSuperTypeResolver superTypeResolver;
    private ClassHierarchyQuery hierarchyQuery;
    private boolean isStatic;
    private boolean isConstructor;
    private NonNullList instructions;
    private ExceptionTableEntry[] exceptionTable;
    private int maxLocals;
    private boolean livenessAnalysisEnabled;
    private VerifierType[] initialLocals;
    private int parameterSlotCount;
    public boolean hasSubroutineCalls;
    private BasicBlock entryBlock;
    private List naturalLoops;
    private int shortestReturnPathLength;
    public boolean localLivenessConsistent;

    public void linkSubroutineReturns(BasicBlock basicBlock, BasicBlock basicBlock1, HashSet hashSet) {
        if (!hashSet.contains(basicBlock1)) {
            hashSet.add(basicBlock1);
            BasicBlock basicBlock4 = basicBlock1.getJsrReturnBlock();
            if (basicBlock4 != null) {
                this.linkSubroutineReturns(basicBlock, basicBlock4, hashSet);
            } else {
                Enumeration enumeration = basicBlock1.enumerateSuccessors();
                if (enumeration != null && enumeration.hasMoreElements()) {
                    while (enumeration.hasMoreElements()) {
                        BasicBlock basicBlock8 = (BasicBlock) enumeration.nextElement();
                        HashSet hashSet1 = hashSet;
                        BasicBlock basicBlock2 = basicBlock8;
                        BasicBlock basicBlock3 = basicBlock;
                        this.linkSubroutineReturns(basicBlock3, basicBlock2, hashSet1);
                    }
                } else {
                    Instruction instruction1 = (Instruction) this.instructions.get(basicBlock1.getEndIndex());
                    if (instruction1.isRet() && basicBlock1.getSubroutineEntry() == null) {
                        LocalVariableInstruction localVariableInstruction = (LocalVariableInstruction) instruction1;
                        Iterator iterator = basicBlock.getJsrCallers().iterator();

                        while (iterator.hasNext()) {
                            BasicBlock basicBlock5 = (BasicBlock) iterator.next();
                            if (this.retReturnTargets == null) {
                                this.retReturnTargets = new TwoKeyMap(13, 13);
                            }

                            Integer integer = INTEGER_CACHE.valueOf(basicBlock5.getJsrReturnBlock().getStartIndex());
                            this.retReturnTargets.putValue(localVariableInstruction, integer, integer);
                        }

                        basicBlock1.setSubroutineEntry(basicBlock);
                        basicBlock.addRetBlock(basicBlock1);
                    }
                }
            }

            Enumeration enumeration1 = basicBlock1.enumerateExceptionHandlers();

            while (enumeration1.hasMoreElements()) {
                BasicBlock basicBlock9 = (BasicBlock) enumeration1.nextElement();
                HashSet hashSet2 = hashSet;
                BasicBlock basicBlock6 = basicBlock9;
                BasicBlock basicBlock7 = basicBlock;
                this.linkSubroutineReturns(basicBlock7, basicBlock6, hashSet2);
            }
        }
    }

    public MethodFlowAnalyzer(
            NonNullList nonNullList,
            ExceptionTableEntry[] exceptionTableEntrys,
            int maxLocals,
            List list1,
            boolean isStatic,
            boolean isConstructor,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            MethodBytecode methodBytecode1,
            boolean livenessAnalysisEnabled
    ) throws ZkmException, IOException {
        this.methodBytecode = methodBytecode1;
        this.methodDescription = methodBytecode1.getQualifiedMethodName();
        this.superTypeResolver = commonSuperTypeResolver1;
        this.hierarchyQuery = classHierarchyQuery;
        this.isStatic = isStatic;
        this.isConstructor = isConstructor;
        this.instructions = nonNullList;
        this.exceptionTable = exceptionTableEntrys;
        this.maxLocals = maxLocals;
        this.livenessAnalysisEnabled = livenessAnalysisEnabled;
        MutableInt mutableInt = new MutableInt(0);

        try {
            this.initialLocals = createInitialLocals(maxLocals, this.isStatic, list1, this.isConstructor, methodBytecode1.getClassName(), mutableInt);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            String string = "Method '"
                    + this.methodDescription
                    + "' appears to be corrupt. : Invalid max_locals="
                    + maxLocals
                    + " : "
                    + list1
                    + " : "
                    + (this.isStatic ? "static " : "instance ")
                    + arrayIndexOutOfBoundsException.getMessage();
            throw new StackAnalysisException(string);
        }

        this.parameterSlotCount = mutableInt.getValue();
        int bb = 0;
        int bc = nonNullList.size();
        HashMap hashMap = ZkmUtils.createHashMap();
        ListMultimap listMultimap = new ListMultimap();

        for (int i = 0; i < bc; i++) {
            Instruction instruction1 = (Instruction) nonNullList.get(i);
            if (instruction1 instanceof JumpingInstruction) {
                ((JumpingInstruction) instruction1).addSuccessorBlocks(hashMap, listMultimap, this.basicBlocks);
                if (instruction1.isJsr()) {
                    this.hasSubroutineCalls = true;
                }
            } else if (instruction1 instanceof LabelInstruction) {
                ((LabelInstruction) instruction1).setInstructionIndex(i);
            }
        }

        TryCatchBlockInfo[] tryCatchBlockInfos;
        if (exceptionTableEntrys != null) {
            tryCatchBlockInfos = new TryCatchBlockInfo[exceptionTableEntrys.length];
            this.tryCatchInfosByHandler = new ListMultimap(exceptionTableEntrys.length);
            int bf = 0;

            for (int i = 0; i < exceptionTableEntrys.length; i++) {
                LabelInstruction labelInstruction = exceptionTableEntrys[i].getLabel(ExceptionRangeField.START_PC);
                LabelInstruction labelInstruction1 = exceptionTableEntrys[i].getLabel(ExceptionRangeField.END_PC);
                LabelInstruction labelInstruction2 = exceptionTableEntrys[i].getLabel(ExceptionRangeField.HANDLER_PC);
                BasicBlock basicBlock;
                if ((basicBlock = (BasicBlock) hashMap.get(labelInstruction2)) == null) {
                    basicBlock = new BasicBlock();
                    this.basicBlocks.add(basicBlock);
                    hashMap.put(labelInstruction2, basicBlock);
                    basicBlock.setLabel("catchNode" + bf++);
                    this.catchHandlerBlocks.add(basicBlock);
                } else if (!this.tryCatchInfosByHandler.containsKey(basicBlock) && !this.catchHandlerBlocks.contains(basicBlock)) {
                    this.catchHandlerBlocks.add(basicBlock);
                    basicBlock.setLabel("catchNode" + bf++);
                }

                tryCatchBlockInfos[i] = new TryCatchBlockInfo(
                        this,
                        i,
                        labelInstruction.getInstructionIndex(),
                        labelInstruction1.getInstructionIndex(),
                        labelInstruction2.getInstructionIndex(),
                        exceptionTableEntrys[i].getCatchTypeName(),
                        exceptionTableEntrys[i].isCatchAny(),
                        basicBlock,
                        exceptionTableEntrys[i]
                );
                this.tryCatchInfosByHandler.addValue(basicBlock, tryCatchBlockInfos[i]);
            }
        } else {
            tryCatchBlockInfos = new TryCatchBlockInfo[0];
        }

        int bg = 0;

        for (Instruction instruction4 = (Instruction) nonNullList.get(bg); instruction4.isLabel(); instruction4 = (Instruction) nonNullList.get(++bg)) {
            BasicBlock basicBlock3 = (BasicBlock) hashMap.get(instruction4);
            if (basicBlock3 != null) {
                this.entryBlock = basicBlock3;
                break;
            }
        }

        if (this.entryBlock == null) {
            this.entryBlock = new BasicBlock();
            this.basicBlocks.add(this.entryBlock);
        }

        this.entryBlock.setLabel("header");
        BasicBlock basicBlock4 = this.entryBlock;
        basicBlock4.setStartIndex(0);
        LinkedHashSet linkedHashSet = new LinkedHashSet();

        for (int i = bg; i < bc; i++) {
            Instruction instruction5 = (Instruction) nonNullList.get(i);
            if (instruction5 instanceof JumpingInstruction) {
                basicBlock4.setEndIndex(i);
                List list3 = listMultimap.getValues((JumpingInstruction) instruction5);

                for (int j = 0; j < list3.size(); j++) {
                    basicBlock4.addSuccessor((BasicBlock) list3.get(j));
                }

                if (i + 1 < bc) {
                    Instruction instruction3 = (Instruction) nonNullList.get(i + 1);
                    BasicBlock basicBlock8;
                    if (!(instruction3 instanceof LabelInstruction) || (basicBlock8 = (BasicBlock) hashMap.get(instruction3)) == null) {
                        basicBlock8 = new BasicBlock();
                        this.basicBlocks.add(basicBlock8);
                    }

                    if (basicBlock8.getLabel() == null) {
                        basicBlock8.setLabel("node" + bb++);
                    }

                    basicBlock8.setStartIndex(i + 1);
                    if (instruction5.canFallThrough()) {
                        basicBlock4.addSuccessor(basicBlock8);
                    } else if (instruction5.isJsr()) {
                        basicBlock4.setJsrReturnBlock(basicBlock8);
                        BasicBlock basicBlock2 = (BasicBlock) list3.get(0);
                        basicBlock2.addJsrCaller(basicBlock4);
                        linkedHashSet.add(basicBlock2);
                    }

                    basicBlock4 = basicBlock8;
                }
            } else if (instruction5 instanceof LabelInstruction) {
                BasicBlock basicBlock6 = (BasicBlock) hashMap.get(instruction5);
                if (basicBlock6 != null && basicBlock4 != basicBlock6) {
                    Instruction instruction6 = (Instruction) nonNullList.get(i - 1);
                    basicBlock4.setEndIndex(i - 1);
                    basicBlock6.setStartIndex(i);
                    if (basicBlock6.getLabel() == null) {
                        basicBlock6.setLabel("node" + bb++);
                    }

                    if (instruction6.canFallThrough()) {
                        basicBlock4.addSuccessor(basicBlock6);
                    }

                    basicBlock4 = basicBlock6;
                }
            } else if (instruction5.isExit()) {
                basicBlock4.setEndIndex(i);
                if (i + 1 < bc) {
                    Instruction instruction2 = (Instruction) nonNullList.get(i + 1);
                    BasicBlock basicBlock1;
                    if (!(instruction2 instanceof LabelInstruction) || (basicBlock1 = (BasicBlock) hashMap.get(instruction2)) == null) {
                        basicBlock1 = new BasicBlock();
                        this.basicBlocks.add(basicBlock1);
                    }

                    if (basicBlock1.getLabel() == null) {
                        basicBlock1.setLabel("node" + bb++);
                    }

                    basicBlock1.setStartIndex(i + 1);
                    basicBlock4 = basicBlock1;
                }
            }
        }

        Collections.sort(this.catchHandlerBlocks);
        Collections.sort(this.basicBlocks);

        for (int i = 0; i < this.basicBlocks.size(); i++) {
            BasicBlock basicBlock5 = (BasicBlock) this.basicBlocks.get(i);
            if (basicBlock5.getStartIndex() > basicBlock5.getEndIndex()) {
                if (i < this.basicBlocks.size() - 1) {
                    BasicBlock basicBlock7 = (BasicBlock) this.basicBlocks.get(i + 1);
                    basicBlock5.setEndIndex(basicBlock7.getStartIndex() - 1);
                } else {
                    basicBlock5.setEndIndex(bc - 1);
                }
            }
        }

        TryCatchBlockInfo[][] tryCatchBlockInfos1 = new TryCatchBlockInfo[bc][];
        if (this.catchHandlerBlocks.size() > 0) {
            this.handlerCoveredBlocks = new TwoKeyMap(this.catchHandlerBlocks.size());
            this.assignExceptionHandlers(tryCatchBlockInfos1, tryCatchBlockInfos);
        }

        List list2 = this.orderSubroutines(linkedHashSet);
        Iterator iterator = list2.iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock9 = (BasicBlock) iterator.next();
            this.linkSubroutineReturns(basicBlock9, basicBlock9, ZkmUtils.createHashSet(13));
            if (!basicBlock9.hasRetBlocks()) {
                List list4 = basicBlock9.getJsrCallers();
                Iterator iterator1 = list4.iterator();

                while (iterator1.hasNext()) {
                    ((BasicBlock) iterator1.next()).setJsrReturnBlock((BasicBlock) null);
                }
            }
        }

        this.removeUnreachableBlocks(this.handlerCoveredBlocks);

        try {
            this.naturalLoops = this.findNaturalLoops();
        } catch (StackOverflowError stackOverflowError2) {
            throw new ZkmProcessingException("StackOverflowError : Try increasing the thread stack size (-Xss) (A)");
        }

        try {
            this.shortestReturnPathLength = this.computeShortestReturnPath();
        } catch (StackOverflowError stackOverflowError1) {
            throw new ZkmProcessingException(
                    "StackOverflowError : Try increasing the thread stack size (-Xss) (B) : '"
                            + methodBytecode1.getOriginalDottedName()
                            + "' '"
                            + methodBytecode1.getMethodName()
                            + "'"
            );
        }

        if (methodBytecode1.getOwningClass().hasReleaseVersion()) {
            commonSuperTypeResolver1.setThreadVersion(methodBytecode1.getOwningClass().getReleaseVersion());
        } else {
            commonSuperTypeResolver1.clearThreadVersion();
        }

        String string1 = "analyzing the control flow of method '"
                + methodBytecode1.getMethod().getOriginalNameWithParameters()
                + "' in class '"
                + methodBytecode1.getLocationName()
                + "' : '"
                + methodBytecode1.getClassName()
                + "' (A)";

        try {
            this.computeFrameStates(
                    commonSuperTypeResolver1,
                    tryCatchBlockInfos1,
                    HiddenOptionFlags.STRICT_TRY_CATCH_ANALYSIS && !HiddenOptionFlags.LENIENT_TRY_CATCH_ANALYSIS,
                    string1
            );
        } catch (AssertionFailedException assertionFailedException1) {
            throw assertionFailedException1;
        }

        if (livenessAnalysisEnabled && !HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("1")) {
            try {
                this.localLivenessConsistent = this.computeLocalLiveness();
            } catch (AssertionFailedException assertionFailedException) {
                throw assertionFailedException;
            } catch (Exception exception) {
            } catch (StackOverflowError stackOverflowError) {
                throw stackOverflowError;
            }
        }

        int bk = this.computeMaxStack();
        int maxStack = ((CodeAttributeBody) methodBytecode1.getParent()).getMaxStack();
        if (bk != maxStack) {
            ((CodeAttributeBody) methodBytecode1.getParent()).setMaxStack(bk);
        }

        commonSuperTypeResolver1.clearThreadVersion();
    }

    private void removeUnreachableBlocks(TwoKeyMap twoKeyMap) {
        if (this.basicBlocks.size() != 1) {
            int ba = this.basicBlocks.size();
            ArrayList arrayList = null;

            for (int i = 0; i < ba; i++) {
                BasicBlock basicBlock = (BasicBlock) this.basicBlocks.get(i);
                if (basicBlock.getLabel() == null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }

                    arrayList.add(basicBlock);
                }
            }

            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    this.basicBlocks.remove(arrayList.get(i));
                }
            }

            HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.basicBlocks.size()));
            this.collectReachableBlocks(this.entryBlock, hashSet);
            UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue();

            for (int i = 0; i < this.catchHandlerBlocks.size(); i++) {
                uniqueWorkQueue.enqueue(this.catchHandlerBlocks.get(i));
            }

            boolean bl1 = true;

            while (!uniqueWorkQueue.isEmpty() && bl1) {
                int bd = uniqueWorkQueue.size();
                int be = hashSet.size();

                for (int i = 0; i < bd; i++) {
                    BasicBlock basicBlock1 = (BasicBlock) uniqueWorkQueue.dequeue();
                    Map map1 = twoKeyMap.getInnerMap(basicBlock1);
                    boolean bl = false;
                    if (map1 != null) {
                        Iterator iterator = map1.keySet().iterator();

                        while (iterator.hasNext()) {
                            BasicBlock basicBlock2 = (BasicBlock) iterator.next();
                            if (hashSet.contains(basicBlock2)) {
                                this.collectReachableBlocks(basicBlock1, hashSet);
                                bl = true;
                                break;
                            }
                        }
                    }

                    if (!bl) {
                        uniqueWorkQueue.enqueue(basicBlock1);
                    }
                }

                bl1 = be != hashSet.size();
            }

            while (!uniqueWorkQueue.isEmpty()) {
                BasicBlock basicBlock3 = (BasicBlock) uniqueWorkQueue.dequeue();
                this.catchHandlerBlocks.remove(basicBlock3);
                this.tryCatchInfosByHandler.removeKey(basicBlock3);
            }

            if (ba > hashSet.size()) {
                this.basicBlocks.clear();
                this.basicBlocks.addAll(hashSet);
                Collections.sort(this.basicBlocks);
                this.blockByIndexCache = null;
            }
        }
    }

    public Integer[] getFrameLabelIndices() {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.basicBlocks.size()), 3.0F);

        for (int i = 0; i < this.instructions.size(); i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isLabel()) {
                LabelInstruction labelInstruction = (LabelInstruction) this.instructions.get(i);
                if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(1024)) {
                    hashMap.put(INTEGER_CACHE.valueOf(labelInstruction.getOffset()), INTEGER_CACHE.valueOf(i));
                }
            }
        }

        Collection collection1 = hashMap.values();
        Integer[] integers = ((java.lang.Integer[]) (collection1.toArray(new Integer[collection1.size()])));
        Arrays.sort(integers);
        return integers;
    }

    public HashSet findDirectNestedLoops(Object object, Set set1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1) {
        HashSet hashSet = ZkmUtils.createHashSet();
        Set set2 = setMultiMap.getValues(object);
        if (set2 != null) {
            Iterator iterator = set2.iterator();

            while (iterator.hasNext()) {
                NaturalLoop naturalLoop = (NaturalLoop) iterator.next();
                Set set3 = setMultiMap1.getValues(naturalLoop);
                if (set1.containsAll(set3)) {
                    hashSet.add(naturalLoop);
                }
            }
        }

        return hashSet;
    }

    public static StackFrameState[] getFrameStates(MethodFlowAnalyzer methodFlowAnalyzer) {
        return methodFlowAnalyzer.frameStates;
    }

    public boolean isGetComponentTypeCall(ResolvedMethodRef resolvedMethodRef, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        try {
            if (resolvedMethodRef.getSignatureString().equals("getComponentType()Ljava/lang/Class;")) {
                String string = resolvedMethodRef.getReferencedClassName();
                return string.equals("java/lang/Class") || classMemberLookup1.isSubclass(string, "java/lang/Class");
            } else {
                return false;
            }
        } catch (ClassFileLoadException classFileLoadException) {
            return false;
        }
    }

    public PropertiesLoadTracker createPropertiesLoadTracker() {
        return new PropertiesLoadTracker(this);
    }

    public void traceFieldReadSource(ValueTraceFrame valueTraceFrame, int ba) throws IOException {
        ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(ba);
        ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) constantRefInstruction.getConstantPoolEntry();
        AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
        if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
            MemberRefKey memberRefKey = abstractFieldInfo.toMemberRefKey();
            HashSet hashSet;
            if (valueTraceFrame.fieldValueCache.containsKey(memberRefKey)) {
                hashSet = (HashSet) valueTraceFrame.fieldValueCache.get(memberRefKey);
            } else {
                hashSet = ZkmUtils.createHashSet(13);
                ListMultimap listMultimap = valueTraceFrame.fieldStoreSites.getMultimap(memberRefKey);
                if (listMultimap != null) {
                    Enumeration enumeration = listMultimap.keys();

                    while (enumeration.hasMoreElements()) {
                        MethodBytecode methodBytecode1 = (MethodBytecode) enumeration.nextElement();
                        List list1 = listMultimap.getValues(methodBytecode1);

                        try {
                            MethodFlowAnalyzer methodFlowAnalyzer1 = methodBytecode1.getCachedFlowAnalyzer(
                                    valueTraceFrame.flowAnalyzerCache, valueTraceFrame.superTypeResolver, this.hierarchyQuery
                            );
                            HashSet hashSet1 = methodFlowAnalyzer1.traceFieldStoreValues(valueTraceFrame, (FieldInfo) abstractFieldInfo, list1);
                            Iterator iterator = hashSet1.iterator();

                            while (iterator.hasNext()) {
                                if (iterator.next() instanceof NullReflectionValue) {
                                    iterator.remove();
                                }
                            }

                            hashSet.addAll(hashSet1);
                        } catch (ZkmException zkmException) {
                            valueTraceFrame.results.add(new InstructionReasonNote(constantRefInstruction, "Exception '" + zkmException.getMessage() + "'"));
                        }
                    }
                }

                valueTraceFrame.fieldValueCache.put(memberRefKey, hashSet);
            }

            if (hashSet != null) {
                valueTraceFrame.results.addAll(hashSet);
            }
        } else {
            String string = resolvedFieldRef.getReferencedClassName();
            if (resolvedFieldRef.getMemberName().equals("TYPE")) {
                if (string.equals("java/lang/Integer")) {
                    valueTraceFrame.results.add(new LiteralStringValue("I"));
                } else if (string.equals("java/lang/Byte")) {
                    valueTraceFrame.results.add(new LiteralStringValue("B"));
                } else if (string.equals("java/lang/Character")) {
                    valueTraceFrame.results.add(new LiteralStringValue("C"));
                } else if (string.equals("java/lang/Short")) {
                    valueTraceFrame.results.add(new LiteralStringValue("S"));
                } else if (string.equals("java/lang/Long")) {
                    valueTraceFrame.results.add(new LiteralStringValue("J"));
                } else if (string.equals("java/lang/Float")) {
                    valueTraceFrame.results.add(new LiteralStringValue("F"));
                } else if (string.equals("java/lang/Double")) {
                    valueTraceFrame.results.add(new LiteralStringValue("D"));
                } else if (string.equals("java/lang/Boolean")) {
                    valueTraceFrame.results.add(new LiteralStringValue("Z"));
                } else if (string.equals("java/lang/Void")) {
                    valueTraceFrame.results.add(new LiteralStringValue("V"));
                } else {
                    valueTraceFrame.results.add(new InstructionReasonNote(constantRefInstruction, "Source field is external: " + string + "." + "TYPE"));
                }
            } else {
                valueTraceFrame.results
                        .add(new InstructionReasonNote(constantRefInstruction, "Source field is external: " + string + "." + resolvedFieldRef.getMemberName()));
            }
        }
    }

    public boolean hasUninitializedThisAt(int ba) {
        return this.frameStates[ba].hasUninitializedThis();
    }

    public void traceFieldWriteUses(ValueTraceFrame valueTraceFrame, ValueFlowTracker valueFlowTracker, int ba) throws ZkmException, IOException {
        ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantRefInstruction) this.instructions.get(ba)).getConstantPoolEntry();
        AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
        HashSet hashSet;
        if (abstractFieldInfo != null) {
            if (abstractFieldInfo.isProgramMember()) {
                MemberRefKey memberRefKey = abstractFieldInfo.toMemberRefKey();
                ListMultimap listMultimap = valueTraceFrame.fieldStoreSites.getMultimap(memberRefKey);
                if (listMultimap != null) {
                    if (listMultimap.getValueCount() == 1) {
                        ListMultimap listMultimap1 = valueTraceFrame.fieldLoadSites.getMultimap(memberRefKey);
                        if (listMultimap1 != null) {
                            Enumeration enumeration = listMultimap1.keys();

                            while (enumeration.hasMoreElements()) {
                                MethodBytecode methodBytecode1 = (MethodBytecode) enumeration.nextElement();
                                if (methodBytecode1 == this.methodBytecode) {
                                    List list2 = listMultimap1.getValues(methodBytecode1);

                                    for (int i = 0; i < list2.size(); i++) {
                                        int be = (Integer) list2.get(i);
                                        StackFrameState stackFrameState1 = this.frameStates[be];
                                        if (stackFrameState1 != null) {
                                            this.traceStackValueUses(
                                                    valueTraceFrame, valueFlowTracker, this.getBlockContaining(be), be + 1, stackFrameState1.getStack().length - 1
                                            );
                                        }
                                    }
                                } else {
                                    try {
                                        MethodFlowAnalyzer methodFlowAnalyzer1 = methodBytecode1.getCachedFlowAnalyzer(
                                                valueTraceFrame.flowAnalyzerCache, valueTraceFrame.superTypeResolver, this.hierarchyQuery
                                        );
                                        List list1 = listMultimap1.getValues(methodBytecode1);

                                        for (int i = 0; i < list1.size(); i++) {
                                            int bc = (Integer) list1.get(i);
                                            StackFrameState stackFrameState = methodFlowAnalyzer1.frameStates[bc];
                                            if (stackFrameState != null) {
                                                ValueFlowTracker valueFlowTracker1 = valueFlowTracker.getTrackerFrom(methodFlowAnalyzer1);
                                                methodFlowAnalyzer1.traceStackValueUses(
                                                        valueTraceFrame,
                                                        valueFlowTracker1,
                                                        methodFlowAnalyzer1.getBlockContaining(bc),
                                                        bc + 1,
                                                        stackFrameState.getStack().length - 1
                                                );
                                            }
                                        }
                                    } catch (ZkmException zkmException) {
                                        valueTraceFrame.results.add(new InstructionReasonNote(null, "Error while tracing field use : " + zkmException.getMessage()));
                                    }
                                }
                            }
                        }
                    } else {
                        valueTraceFrame.results.add(new InstructionReasonNote(null, "Field set more than once"));
                    }

                    return;
                }

                return;
            }

            hashSet = valueTraceFrame.results;
        } else {
            hashSet = valueTraceFrame.results;
        }

        hashSet.add(new InstructionReasonNote(null, "Field is external : " + resolvedFieldRef.getValueString()));
    }

    public boolean isGetPropertyCall(ResolvedMethodRef resolvedMethodRef, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        return this.isPropertiesMethodCall(resolvedMethodRef, "getProperty(Ljava/lang/String;)Ljava/lang/String;", classMemberLookup1);
    }

    public void collectNestedSubroutineCalls(BasicBlock basicBlock, BasicBlock basicBlock1, TwoKeyMap twoKeyMap, Set set1, boolean bl) {
        if (!set1.contains(basicBlock1)) {
            set1.add(basicBlock1);
            BasicBlock basicBlock2 = basicBlock1.getJsrReturnBlock();
            if (basicBlock2 != null) {
                BranchInstruction branchInstruction = (BranchInstruction) this.instructions.get(basicBlock1.getEndIndex());
                LabelInstruction labelInstruction = branchInstruction.getTargetLabel();
                BasicBlock basicBlock3 = this.findBlockStartingAt(labelInstruction.getInstructionIndex());
                Boolean boolean1 = bl ? Boolean.TRUE : Boolean.FALSE;
                if (basicBlock != basicBlock3) {
                    Boolean boolean2 = (Boolean) twoKeyMap.putValue(basicBlock, basicBlock3, boolean1);
                    if (boolean2 != null && !boolean2 && boolean1) {
                        twoKeyMap.putValue(basicBlock, basicBlock3, boolean2);
                    }
                }

                this.collectNestedSubroutineCalls(basicBlock, basicBlock2, twoKeyMap, set1, bl);
            } else {
                Enumeration enumeration = basicBlock1.enumerateSuccessors();
                if (enumeration != null) {
                    while (enumeration.hasMoreElements()) {
                        BasicBlock basicBlock4 = (BasicBlock) enumeration.nextElement();
                        this.collectNestedSubroutineCalls(basicBlock, basicBlock4, twoKeyMap, set1, bl);
                    }
                }
            }

            Enumeration enumeration1 = basicBlock1.enumerateExceptionHandlers();

            while (enumeration1.hasMoreElements()) {
                BasicBlock basicBlock5 = (BasicBlock) enumeration1.nextElement();
                this.collectNestedSubroutineCalls(basicBlock, basicBlock5, twoKeyMap, set1, true);
            }
        }
    }

    public boolean hasPendingPredecessor(int ba, TreeSet treeSet) {
        BasicBlock basicBlock = this.getBlockContaining(ba);
        if (basicBlock.hasPredecessors()) {
            Iterator iterator = basicBlock.getPredecessors().iterator();

            while (iterator.hasNext()) {
                int startIndex = ((BasicBlock) iterator.next()).getStartIndex();
                if (treeSet.contains(INTEGER_CACHE.valueOf(startIndex)) || this.frameStates[startIndex] == null) {
                    return true;
                }
            }
        }

        return false;
    }

    public int computeShortestReturnPath() {
        int ba = Integer.MAX_VALUE;

        for (int i = 0; i < this.basicBlocks.size(); i++) {
            BasicBlock basicBlock = (BasicBlock) this.basicBlocks.get(i);
            if (((Instruction) this.instructions.get(basicBlock.getEndIndex())).isReturn()) {
                int minDistanceFromEntry = basicBlock.getMinDistanceFromEntry();
                if (minDistanceFromEntry < ba) {
                    ba = minDistanceFromEntry;
                }
            }
        }

        return ba;
    }

    public boolean findStackValueProducers(BasicBlock basicBlock, int ba, int bb, Set set1, Set set2) {
        int bc = bb;
        if (!set2.add(basicBlock)) {
            return true;
        }

        boolean bl = false;
        int startIndex = basicBlock.getStartIndex();

        for (int i = ba - 1; !bl && i >= startIndex; i += -1) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            StackFrameState stackFrameState = this.frameStates[i];
            if (instruction1.isStackManipulation()) {
                VerifierType[] verifierTypes = stackFrameState.getStack();
                if (instruction1.affectsStackSlot(verifierTypes, bc)) {
                    return false;
                }

                bc = instruction1.mapStackSlotBackward(verifierTypes, bc);
            } else {
                if (instruction1.isJump()) {
                    return false;
                }

                if (instruction1.isLabel()
                        && (
                        ((LabelInstruction) instruction1).hasUsageBits(1)
                                || ((LabelInstruction) instruction1).hasUsageBits(256)
                                || ((LabelInstruction) instruction1).hasUsageBits(512)
                                || ((LabelInstruction) instruction1).hasUsageBits(1024)
                )) {
                    return false;
                }

                if (stackFrameState.getStackDepth() - 1 == bc && instruction1.pushesValue()) {
                    set1.add(INTEGER_CACHE.valueOf(i));
                    bl = true;
                }
            }
        }

        if (!bl) {
            List list1 = basicBlock.getPredecessors();
            if (list1 != null && list1.size() > 0) {
                for (int i = 0; i < list1.size(); i++) {
                    BasicBlock basicBlock1 = (BasicBlock) list1.get(i);
                    boolean bl1 = this.findStackValueProducers(basicBlock1, basicBlock1.getEndIndex() + 1, bc, set1, set2);
                    if (!bl1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void traceLocalVariableUses(ValueTraceFrame valueTraceFrame, ValueFlowTracker valueFlowTracker, BasicBlock basicBlock, int ba, int bb) throws ZkmException, IOException {
        int bc = bb;
        int bd = ba;
        if (!valueFlowTracker.isVisited(2, bd, bc)) {
            valueFlowTracker.markVisited(2, ba, bb);
            int endIndex = basicBlock.getEndIndex();

            for (int i = ba; i < endIndex; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                StackFrameState stackFrameState = this.frameStates[i];
                bc = bb;
                if (instruction1.isStoreTo(bc)) {
                    return;
                }

                if (instruction1.isLoadFrom(bb)) {
                    int bg = stackFrameState.getStack().length - 1;
                    this.traceStackValueUses(valueTraceFrame, valueFlowTracker, basicBlock, i + 1, bg);
                }
            }

            List list1 = basicBlock.getSuccessorsCopy();
            if (list1 != null && list1.size() > 0) {
                for (int i = 0; i < list1.size(); i++) {
                    BasicBlock basicBlock1 = (BasicBlock) list1.get(i);
                    this.traceLocalVariableUses(valueTraceFrame, valueFlowTracker, basicBlock1, basicBlock1.getStartIndex(), bb);
                }
            } else if (basicBlock.hasRetBlocks()) {
                List list2 = basicBlock.getRetBlocks();

                for (int i = 0; i < list2.size(); i++) {
                    BasicBlock basicBlock2 = (BasicBlock) list2.get(i);
                    this.traceLocalVariableUses(valueTraceFrame, valueFlowTracker, basicBlock2, basicBlock2.getStartIndex(), bb);
                }
            }
        }
    }

    public BasicBlock findBlockStartingAt(int ba) {
        int bb = 0;
        int bc = this.basicBlocks.size() - 1;

        while (bb <= bc) {
            int bd = (bb + bc) / 2;
            BasicBlock basicBlock = (BasicBlock) this.basicBlocks.get(bd);
            int startIndex = basicBlock.getStartIndex();
            if (ba < startIndex) {
                bc = bd - 1;
            } else {
                if (ba <= startIndex) {
                    return basicBlock;
                }

                bb = bd + 1;
            }
        }

        return null;
    }

    public void buildLoopTree(
            NaturalLoop naturalLoop, HashSet hashSet, GenericTree genericTree, SetMultiMap setMultiMap, SetMultiMap setMultiMap1, Set set1, boolean bl
    ) {
        hashSet.add(naturalLoop);
        Set set2;
        if (bl) {
            set2 = this.findDirectNestedLoops(naturalLoop, hashSet, setMultiMap, setMultiMap1);
        } else {
            set2 = setMultiMap.getValues(naturalLoop);
        }

        if (set2 != null) {
            Iterator iterator = set2.iterator();

            while (iterator.hasNext()) {
                NaturalLoop naturalLoop1 = (NaturalLoop) iterator.next();
                if (!set1.contains(naturalLoop1)) {
                    genericTree.moveToValue(naturalLoop);
                    genericTree.addChild(naturalLoop1);
                    set1.add(naturalLoop1);
                    this.buildLoopTree(naturalLoop1, ZkmUtils.createHashSetFrom(hashSet), genericTree, setMultiMap, setMultiMap1, set1, bl);
                }
            }
        }
    }

    public boolean hasFrameLabels() {
        for (int i = 0; i < this.instructions.size(); i++) {
            if (((Instruction) this.instructions.get(i)).isLabel()) {
                LabelInstruction labelInstruction = (LabelInstruction) this.instructions.get(i);
                if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(1024)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void traceLocalThroughCatchHandler(ValueTraceFrame valueTraceFrame, BasicBlock basicBlock, int ba) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        List list1 = this.tryCatchInfosByHandler.getValues(basicBlock);
        BitSet bitSet = new BitSet(this.basicBlocks.size());

        for (int i = 0; i < list1.size(); i++) {
            TryCatchBlockInfo tryCatchBlockInfo = (TryCatchBlockInfo) list1.get(i);
            ExceptionTableEntry exceptionTableEntry1 = tryCatchBlockInfo.exceptionTableEntry;
            LabelInstruction labelInstruction = exceptionTableEntry1.getLabel(ExceptionRangeField.START_PC);
            LabelInstruction labelInstruction1 = exceptionTableEntry1.getLabel(ExceptionRangeField.END_PC);
            int instructionIndex = labelInstruction.getInstructionIndex();
            int bd = labelInstruction1.getInstructionIndex();
            BasicBlock basicBlock1 = null;
            HashSet hashSet1 = ZkmUtils.createHashSet();
            Iterator iterator = this.basicBlocks.iterator();

            while (iterator.hasNext()) {
                BasicBlock basicBlock2 = (BasicBlock) iterator.next();
                if (basicBlock2.getStartIndex() >= instructionIndex && basicBlock2.getStartIndex() < bd) {
                    hashSet1.add(basicBlock2);
                } else if (basicBlock2.getStartIndex() < instructionIndex && basicBlock2.getEndIndex() >= instructionIndex) {
                    basicBlock1 = basicBlock2;
                }
            }

            for (int j = instructionIndex; j < bd; j++) {
                Instruction instruction1 = (Instruction) this.instructions.get(j);
                if (instruction1.isStoreTo(ba)) {
                    BasicBlock basicBlock3 = this.getBlockContaining(j);
                    if (j > basicBlock3.getStartIndex()) {
                        this.traceStackValueSource(valueTraceFrame, basicBlock3, j, this.frameStates[j].getStackDepth());
                        return;
                    }

                    List list2 = basicBlock3.getPredecessors();
                    if (list2 != null && list2.size() > 0) {
                        for (int k = 0; k < list2.size(); k++) {
                            BasicBlock basicBlock4 = (BasicBlock) list2.get(k);
                            this.traceStackValueSource(valueTraceFrame, basicBlock4, basicBlock4.getEndIndex() + 1, this.frameStates[j].getStackDepth());
                        }

                        return;
                    }
                }
            }

            Iterator iterator2;
            label107:
            if (basicBlock1 != null) {
                bitSet.clear();
                HashSet hashSet2;
                if (basicBlock != basicBlock1) {
                    if (!basicBlock.canReach(basicBlock1, bitSet)) {
                        this.traceLocalVariableSource(valueTraceFrame, basicBlock1, instructionIndex, ba);
                        iterator2 = hashSet1.iterator();
                        break label107;
                    }

                    hashSet2 = valueTraceFrame.results;
                } else {
                    hashSet2 = valueTraceFrame.results;
                }

                hashSet2.add(new InstructionReasonNote((Instruction) this.instructions.get(basicBlock.getStartIndex()), "Loop through catch"));
                iterator2 = hashSet1.iterator();
            } else {
                iterator2 = hashSet1.iterator();
            }

            iterator = iterator2;

            while (iterator.hasNext()) {
                BasicBlock basicBlock6 = (BasicBlock) iterator.next();
                int startIndex = basicBlock6.getStartIndex();
                if (!((Instruction) this.instructions.get(startIndex)).isStoreTo(ba) && !StackFrameState.isTopOrMissing(this.frameStates[startIndex].getLocals()[ba])) {
                    List list3 = basicBlock6.getPredecessors();
                    if (list3 != null && list3.size() > 0) {
                        for (int j = 0; j < list3.size(); j++) {
                            BasicBlock basicBlock7 = (BasicBlock) list3.get(j);
                            if (!hashSet1.contains(basicBlock7) && (basicBlock1 == null || basicBlock7 != basicBlock1)) {
                                hashSet.add(basicBlock7);
                            }
                        }
                    }
                }
            }
        }

        Iterator iterator1 = hashSet.iterator();

        while (iterator1.hasNext()) {
            BasicBlock basicBlock5 = (BasicBlock) iterator1.next();
            bitSet.clear();
            HashSet hashSet3;
            if (basicBlock != basicBlock5) {
                if (!basicBlock.canReach(basicBlock5, bitSet)) {
                    this.traceLocalVariableSource(valueTraceFrame, basicBlock5, basicBlock5.getEndIndex() + 1, ba);
                    continue;
                }

                hashSet3 = valueTraceFrame.results;
            } else {
                hashSet3 = valueTraceFrame.results;
            }

            hashSet3.add(new InstructionReasonNote((Instruction) this.instructions.get(basicBlock.getStartIndex()), "Loop through catch"));
        }
    }

    public void traceStackValueSource(ValueTraceFrame valueTraceFrame, BasicBlock basicBlock, int ba, int bb) throws ZkmException, IOException {
        int bc = bb;
        Triple triple = new Triple(this.methodBytecode, INTEGER_CACHE.valueOf(ba), INTEGER_CACHE.valueOf(bc));
        HashSet hashSet = (HashSet) valueTraceFrame.stackSourceCache.get(triple);
        byte bj;
        if (hashSet != null) {
            if (valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_1) {
                if (valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_2) {
                    valueTraceFrame.results.addAll(hashSet);
                    return;
                }

                long bi = 56257057901662L;
                bj = 13;
            } else {
                long bg = 56257057901662L;
                bj = 13;
            }
        } else {
            long bh = 56257057901662L;
            bj = 13;
        }

        Integer integer = Integer.valueOf(bj);
        hashSet = ZkmUtils.createHashSet(integer);
        HashSet hashSet1 = valueTraceFrame.swapResults(hashSet);
        if (valueTraceFrame.getNestingLevel() > 3) {
            valueTraceFrame.results
                    .add(new InstructionReasonNote(null, "Max Nesting Level Exceeded: " + valueTraceFrame.getNestingLevel() + " " + valueTraceFrame.argumentKind));
        } else {
            boolean bl = false;
            int startIndex = basicBlock.getStartIndex();

            for (int i = ba - 1; !bl && i >= startIndex; i += -1) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                StackFrameState stackFrameState = this.frameStates[i];
                if (valueTraceFrame.argumentKind == ReflectionArgumentKind.OBJECT_TYPE) {
                    String string = stackFrameState.getStack()[bc].getDescriptor();
                    string = CommonSuperTypeResolver.stripClassDescriptor(string);
                    valueTraceFrame.results.add(new TracedObjectType(instruction1, string));
                    bl = true;
                    break;
                }

                if (instruction1.isStackManipulation()) {
                    bc = instruction1.mapStackSlotBackward(stackFrameState.getStack(), bc);
                } else if (stackFrameState.getStackDepth() - 1 == bc) {
                    if (instruction1.pushesValue()) {
                        switch (instruction1.getOpcode()) {
                            case 1:
                                valueTraceFrame.results.add(new NullReflectionValue());
                                break;
                            case 18:
                                LdcInstruction ldcInstruction = (LdcInstruction) instruction1;
                                ConstantPoolEntry constantPoolEntry = ldcInstruction.getConstantPoolEntry();
                                if (constantPoolEntry instanceof ResolvedStringConstant) {
                                    valueTraceFrame.results.add(new StringConstantNameRef((ResolvedStringConstant) constantPoolEntry));
                                } else if (constantPoolEntry instanceof ResolvedClassConstant) {
                                    valueTraceFrame.results.add(new ClassConstantValue((ResolvedClassConstant) constantPoolEntry));
                                } else if (constantPoolEntry instanceof ResolvedMethodType) {
                                    valueTraceFrame.results.add(new StringConstantValue((ResolvedMethodType) constantPoolEntry));
                                }
                                break;
                            case 19:
                                ConstantPoolEntry constantPoolEntry1 = ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                if (constantPoolEntry1 instanceof ResolvedStringConstant) {
                                    valueTraceFrame.results.add(new StringConstantNameRef((ResolvedStringConstant) constantPoolEntry1));
                                } else if (constantPoolEntry1 instanceof ResolvedClassConstant) {
                                    valueTraceFrame.results.add(new ClassConstantValue((ResolvedClassConstant) constantPoolEntry1));
                                } else if (constantPoolEntry1 instanceof ResolvedMethodType) {
                                    valueTraceFrame.results.add(new StringConstantValue((ResolvedMethodType) constantPoolEntry1));
                                }
                                break;
                            case 178:
                            case 180:
                                this.traceFieldReadSource(valueTraceFrame, i);
                                break;
                            case 182:
                            case 183:
                            case 184:
                            case 185:
                                this.traceMethodCallSource(valueTraceFrame, basicBlock, i);
                                break;
                            case 187:
                                this.traceNewInstanceSource(valueTraceFrame, basicBlock, i);
                                break;
                            case 189:
                                this.traceNewArraySource(valueTraceFrame, basicBlock, i);
                                break;
                            case 255:
                                LocalVariableInstruction localVariableInstruction = (LocalVariableInstruction) instruction1;
                                if (localVariableInstruction.getAccessKind() == LocalVariableAccessKind.OBJECT_LOAD) {
                                    this.traceLocalVariableSource(valueTraceFrame, basicBlock, i, localVariableInstruction.getLocalIndex());
                                } else {
                                    valueTraceFrame.results.add(new InstructionReasonNote(instruction1, "Instruction not handled: " + valueTraceFrame.argumentKind));
                                }
                                break;
                            default:
                                valueTraceFrame.results.add(new InstructionReasonNote(instruction1, "Instruction not handled: " + valueTraceFrame.argumentKind));
                        }

                        bl = true;
                    }
                } else if (instruction1.isJsr()) {
                    valueTraceFrame.results.add(new InstructionReasonNote(instruction1, "Instruction not handled: " + valueTraceFrame.argumentKind));
                    bl = true;
                }
            }

            if (!bl) {
                List list1 = basicBlock.getPredecessors();
                if (list1 != null && list1.size() > 0) {
                    for (int i = 0; i < list1.size(); i++) {
                        BasicBlock basicBlock1 = (BasicBlock) list1.get(i);
                        this.traceStackValueSource(valueTraceFrame, basicBlock1, basicBlock1.getEndIndex() + 1, bc);
                    }
                } else {
                    Instruction instruction2 = (Instruction) this.instructions.get(startIndex);
                    valueTraceFrame.results.add(new InstructionReasonNote(instruction2, "No source found 1):" + valueTraceFrame.argumentKind));
                }
            }
        }

        hashSet = valueTraceFrame.swapResults(hashSet1);
        hashSet1.addAll(hashSet);
        if (valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_1
                && valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_2) {
            HashSet hashSet2 = ((java.util.HashSet) (valueTraceFrame.stackSourceCache.put(triple, hashSet)));
            if (hashSet2 != null) {
                hashSet.addAll(hashSet2);
            }
        }
    }

    public ArrayList buildLoopNestingTrees() {
        HashMap hashMap = ZkmUtils.createHashMap();
        Iterator iterator = this.naturalLoops.iterator();

        while (iterator.hasNext()) {
            NaturalLoop naturalLoop = (NaturalLoop) iterator.next();
            hashMap.put(naturalLoop.getHeader(), naturalLoop);
        }

        SetMultiMap setMultiMap = new SetMultiMap();
        Iterator iterator5 = this.naturalLoops.iterator();

        while (iterator5.hasNext()) {
            NaturalLoop naturalLoop1 = (NaturalLoop) iterator5.next();
            Iterator iterator1 = this.naturalLoops.iterator();

            while (iterator1.hasNext()) {
                NaturalLoop naturalLoop2 = (NaturalLoop) iterator1.next();
                if (naturalLoop1 != naturalLoop2 && naturalLoop1.containsBlock(naturalLoop2.getHeader())) {
                    setMultiMap.addValue(naturalLoop1, naturalLoop2);
                }
            }
        }

        SetMultiMap setMultiMap1 = new SetMultiMap();
        Iterator iterator6 = setMultiMap.entrySet().iterator();

        while (iterator6.hasNext()) {
            Entry entry = (Entry) iterator6.next();
            NaturalLoop naturalLoop6 = (NaturalLoop) entry.getKey();
            Iterator iterator2 = ((Set) entry.getValue()).iterator();

            while (iterator2.hasNext()) {
                NaturalLoop naturalLoop3 = (NaturalLoop) iterator2.next();
                setMultiMap1.addValue(naturalLoop3, naturalLoop6);
            }
        }

        ArrayList arrayList = new ArrayList();
        Iterator iterator7 = this.naturalLoops.iterator();

        while (iterator7.hasNext()) {
            NaturalLoop naturalLoop7 = (NaturalLoop) iterator7.next();
            if (!setMultiMap1.containsKey(naturalLoop7)) {
                arrayList.add(naturalLoop7);
            }
        }

        ArrayList arrayList1 = new ArrayList();
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator8 = arrayList.iterator();

        while (iterator8.hasNext()) {
            NaturalLoop naturalLoop8 = (NaturalLoop) iterator8.next();
            GenericTree genericTree = new GenericTree(naturalLoop8);
            hashSet.add(naturalLoop8);
            arrayList1.add(genericTree);
            this.buildLoopTree(naturalLoop8, ZkmUtils.createHashSet(), genericTree, setMultiMap, setMultiMap1, hashSet, true);
        }

        if (hashSet.size() < this.naturalLoops.size()) {
            iterator8 = setMultiMap.keySet().iterator();

            while (iterator8.hasNext()) {
                NaturalLoop naturalLoop9 = (NaturalLoop) iterator8.next();
                if (!hashSet.contains(naturalLoop9)) {
                    GenericTree genericTree1 = new GenericTree(naturalLoop9);
                    hashSet.add(naturalLoop9);
                    arrayList1.add(genericTree1);
                    Iterator iterator3 = setMultiMap.getValues(naturalLoop9).iterator();

                    while (iterator3.hasNext()) {
                        NaturalLoop naturalLoop4 = (NaturalLoop) iterator3.next();
                        genericTree1.resetToRoot();
                        genericTree1.addChild(naturalLoop4);
                        hashSet.add(naturalLoop9);
                    }
                }
            }
        }

        if (hashSet.size() < this.naturalLoops.size()) {
            HashSet hashSet1 = ZkmUtils.createHashSet();
            Iterator iterator9 = arrayList1.iterator();

            while (iterator9.hasNext()) {
                GenericTree genericTree2 = (GenericTree) iterator9.next();
                hashSet1.addAll(new EnumerationBackedList(genericTree2.values()));
            }

            HashSet hashSet2 = ZkmUtils.createHashSetFrom(this.naturalLoops);
            hashSet2.removeAll(hashSet1);
            NaturalLoop naturalLoop10 = null;
            Iterator iterator10 = hashSet2.iterator();

            while (iterator10.hasNext()) {
                NaturalLoop naturalLoop11 = (NaturalLoop) iterator10.next();
                Set set1 = setMultiMap1.getValues(naturalLoop11);
                if (set1 != null) {
                    TreeSet treeSet = new TreeSet(set1);
                    Iterator iterator4 = treeSet.iterator();

                    while (iterator4.hasNext()) {
                        NaturalLoop naturalLoop5 = (NaturalLoop) iterator4.next();
                        if (setMultiMap1.containsKey(naturalLoop5)) {
                            naturalLoop10 = naturalLoop5;
                            break;
                        }
                    }

                    if (naturalLoop10 == null) {
                        naturalLoop10 = (NaturalLoop) treeSet.iterator().next();
                    }

                    iterator4 = arrayList1.iterator();

                    while (iterator4.hasNext()) {
                        GenericTree genericTree3 = (GenericTree) iterator4.next();
                        if (genericTree3.moveToValue(naturalLoop10)) {
                            genericTree3.addChild(naturalLoop11);
                            break;
                        }
                    }
                }
            }
        }

        return arrayList1;
    }

    private void o(int ba, int bb, TreeSet treeSet, boolean bl, String string) throws ZkmException, IOException {
        Instruction instruction1 = (Instruction) this.instructions.get(bb);

        StackFrameState stackFrameState;
        try {
            ObservableHolder observableHolder = new ObservableHolder();
            StackFrameState stackFrameState2 = this.frameStates[ba];
            CommonSuperTypeResolver commonSuperTypeResolver1 = this.superTypeResolver;
            stackFrameState = instruction1.computeFrameAfter(stackFrameState2, bl, commonSuperTypeResolver1, string);
            if (!observableHolder.isValueNull()) {
                AssertionFailedException assertionFailedException = (AssertionFailedException) observableHolder.getValue();
                if (!this.hasPendingPredecessor(ba, treeSet)) {
                    throw assertionFailedException;
                }
            }
        } catch (MethodAnalysisException methodAnalysisException) {
            throw methodAnalysisException;
        } catch (StackAnalysisException stackAnalysisException) {
            throw stackAnalysisException;
        } catch (AssertionFailedException assertionFailedException1) {
            throw assertionFailedException1;
        }

        if (this.frameStates[bb] == null) {
            this.frameStates[bb] = stackFrameState;
            treeSet.add(INTEGER_CACHE.valueOf(bb));
        } else {
            BooleanFlag booleanFlag = new BooleanFlag();
            StackFrameState stackFrameState1 = this.frameStates[bb].merge(this.superTypeResolver, stackFrameState, booleanFlag, string);
            if (booleanFlag.getValue()) {
                this.frameStates[bb] = stackFrameState1;
                treeSet.add(INTEGER_CACHE.valueOf(bb));
            }
        }
    }

    public void assignExceptionHandlers(TryCatchBlockInfo[][] tryCatchBlockInfos, TryCatchBlockInfo[] tryCatchBlockInfos1) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.basicBlocks.iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock = (BasicBlock) iterator.next();
            int startIndex = basicBlock.getStartIndex();
            int endIndex = basicBlock.getEndIndex();

            for (int i = startIndex; i <= endIndex; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                TryCatchBlockInfo[] tryCatchBlockInfos2 = this.getActiveHandlersAt(i, tryCatchBlockInfos1);

                for (int j = 0; j < tryCatchBlockInfos2.length; j++) {
                    if (this.handlerCoveredBlocks.putValue(tryCatchBlockInfos2[j].handlerBlock, basicBlock, basicBlock) == null) {
                        basicBlock.addExceptionHandler(tryCatchBlockInfos2[j].handlerBlock);
                    }

                    if (i == tryCatchBlockInfos2[j].startIndex || i == startIndex || instruction1.isStore() || !arrayList.contains(tryCatchBlockInfos2[j])) {
                        arrayList.add(tryCatchBlockInfos2[j]);
                    }
                }

                if (arrayList.size() > 0) {
                    TryCatchBlockInfo[] tryCatchBlockInfos3 = new TryCatchBlockInfo[arrayList.size()];
                    tryCatchBlockInfos[i] = ((com.zelix.klassmaster.classfile.insn.TryCatchBlockInfo[]) (arrayList.toArray(tryCatchBlockInfos3)));
                    arrayList.clear();
                }
            }
        }
    }

    private List orderSubroutines(Set set1) throws MethodAnalysisException {
        ArrayList arrayList = new ArrayList(set1.size());
        if (set1.size() <= 1) {
            if (set1.size() == 1) {
                arrayList.add(set1.iterator().next());
            }

            return arrayList;
        } else {
            TwoKeyMap twoKeyMap = new TwoKeyMap();
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                BasicBlock basicBlock = (BasicBlock) iterator.next();
                this.collectNestedSubroutineCalls(basicBlock, basicBlock, twoKeyMap, ZkmUtils.createHashSet(13), false);
            }

            HashMap hashMap = ZkmUtils.createHashMap(Math.max(ZkmUtils.getPrimeCapacity(set1.size()), 7));
            TwoKeyMap twoKeyMap1 = ZkmUtils.copyTwoKeyMap(twoKeyMap);
            Enumeration enumeration = twoKeyMap1.keys();

            while (enumeration.hasMoreElements()) {
                BasicBlock basicBlock1 = (BasicBlock) enumeration.nextElement();
                Iterator iterator1 = twoKeyMap1.getInnerMap(basicBlock1).keySet().iterator();

                while (iterator1.hasNext()) {
                    BasicBlock basicBlock2 = (BasicBlock) iterator1.next();
                    BasicBlock basicBlock3 = ((com.zelix.klassmaster.classfile.insn.BasicBlock) (hashMap.put(basicBlock2, basicBlock1)));
                    if (basicBlock3 != null) {
                        boolean bl = (Boolean) twoKeyMap1.getValue(basicBlock1, basicBlock2);
                        boolean bl1 = (Boolean) twoKeyMap1.getValue(basicBlock3, basicBlock2);
                        if (bl1 && !bl) {
                            twoKeyMap.removeValue(basicBlock3, basicBlock2);
                        } else if (!bl1 && bl) {
                            hashMap.put(basicBlock2, basicBlock3);
                            twoKeyMap.removeValue(basicBlock1, basicBlock2);
                        } else {
                            if (bl1 && bl) {
                                throw new MethodAnalysisException(
                                        "Method '" + this.methodDescription + "' could not be analyzed (C). No action required.", this.methodDescription
                                );
                            }

                            if (!bl1 && !bl) {
                                ZkmAssert.assertTrue(false, new String[]{"Method '" + this.methodDescription + "' could not be analyzed (D)"});
                            }
                        }
                    }
                }
            }

            Iterator iterator2 = set1.iterator();

            while (iterator2.hasNext()) {
                BasicBlock basicBlock4 = (BasicBlock) iterator2.next();
                if (!hashMap.containsKey(basicBlock4)) {
                    this.collectSubroutineOrder(basicBlock4, twoKeyMap, arrayList);
                }
            }

            if (arrayList.size() != set1.size()) {
                throw new MethodAnalysisException("Method '" + this.methodDescription + "' could not be analyzed (E). No action required.", this.methodDescription);
            } else {
                return arrayList;
            }
        }
    }

    public boolean propagateHandlerLiveness(Collection collection1) {
        boolean bl = false;
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock = (BasicBlock) iterator.next();
            BitSet bitSet = this.frameStates[basicBlock.getStartIndex()].getLiveLocals();
            ArrayList arrayList = new ArrayList(this.handlerCoveredBlocks.getInnerMap(basicBlock).keySet());
            Collections.sort(arrayList);
            Collections.reverse(arrayList);
            Iterator iterator1 = arrayList.iterator();

            while (iterator1.hasNext()) {
                BasicBlock basicBlock1 = (BasicBlock) iterator1.next();
                int ba = this.getLastCoveredInstructionIndex(basicBlock1, basicBlock);
                if (this.propagateLivenessBackward(basicBlock1, ba, (BitSet) bitSet.clone())) {
                    bl = true;
                }
            }
        }

        return bl;
    }

    public static NonNullList getInstructions(MethodFlowAnalyzer methodFlowAnalyzer) {
        return methodFlowAnalyzer.instructions;
    }

    public Set getLivenessStartBlocks() {
        TreeSet<BasicBlock> treeSet = new TreeSet<>((basicBlockx, basicBlock1x) -> basicBlock1x.compareStart(basicBlockx));
        Iterator iterator = this.basicBlocks.iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock = (BasicBlock) iterator.next();
            if (!basicBlock.hasSuccessors()) {
                treeSet.add(basicBlock);
            } else {
                List list1 = basicBlock.getSuccessorsCopy();
                boolean bl = false;
                Iterator iterator1 = list1.iterator();

                while (iterator1.hasNext()) {
                    BasicBlock basicBlock1 = (BasicBlock) iterator1.next();
                    if (basicBlock1.hasPredecessors() && basicBlock1.getPredecessors().contains(basicBlock)) {
                        bl = true;
                    }
                }

                if (!bl) {
                    treeSet.add(basicBlock);
                }
            }
        }

        return treeSet;
    }

    public boolean isLocalLivenessConsistent() {
        return this.localLivenessConsistent;
    }

    public BasicBlock getBlockContaining(int ba) {
        if (this.blockByIndexCache == null) {
            this.blockByIndexCache = new BasicBlock[this.methodBytecode.getInstructionCount()];
        } else if (this.blockByIndexCache[ba] != null) {
            return this.blockByIndexCache[ba];
        }

        int bb = 0;
        int bc = this.basicBlocks.size() - 1;

        while (bb <= bc) {
            int bd = (bb + bc) / 2;
            BasicBlock basicBlock = (BasicBlock) this.basicBlocks.get(bd);
            if (ba < basicBlock.getStartIndex()) {
                bc = bd - 1;
            } else {
                if (ba <= basicBlock.getEndIndex()) {
                    this.blockByIndexCache[ba] = basicBlock;
                    return basicBlock;
                }

                bb = bd + 1;
            }
        }

        return null;
    }

    public static void collectIntConstantSources(MethodFlowAnalyzer methodFlowAnalyzer, List list1, BasicBlock basicBlock, int ba, int bb, Set set1) {
        methodFlowAnalyzer.collectIntConstantSourcesFrom(list1, basicBlock, ba, bb, set1);
    }

    public void traceNewInstanceSource(ValueTraceFrame valueTraceFrame, BasicBlock basicBlock, int ba) throws ZkmException, IOException {
        ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(ba);
        String string = ((ResolvedClassConstant) constantRefInstruction.getConstantPoolEntry()).getClassName();
        if (string.equals("java/util/Properties")) {
            StackFrameState stackFrameState = this.frameStates[ba];
            PropertiesLoadTracker propertiesLoadTracker = this.createPropertiesLoadTracker();
            this.traceStackValueUses(valueTraceFrame, propertiesLoadTracker, basicBlock, ba + 1, stackFrameState.getStack().length - 1);
            HashSet hashSet = propertiesLoadTracker.getFileNameValues();
            HashSet hashSet1;
            if (hashSet.size() == 0) {
                hashSet.add(new InstructionReasonNote(constantRefInstruction, "Couldn't determine property file name"));
                hashSet1 = valueTraceFrame.results;
            } else {
                hashSet1 = valueTraceFrame.results;
            }

            hashSet1.addAll(hashSet);
        } else {
            valueTraceFrame.results.add(new InstructionReasonNote(constantRefInstruction, "NEW instruction not handled with : " + string));
        }
    }

    public void traceStackValueUses(ValueTraceFrame valueTraceFrame, ValueFlowTracker valueFlowTracker, BasicBlock basicBlock, int ba, int bb) throws ZkmException, IOException {
        int bc = bb;
        int bd = bc;
        int be = ba;
        if (!valueFlowTracker.isVisited(1, be, bd)) {
            valueFlowTracker.markVisited(1, ba, bc);
            boolean bl = false;
            int endIndex = basicBlock.getEndIndex();

            for (int i = ba; !bl && i <= endIndex; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                StackFrameState stackFrameState = this.frameStates[i];
                if (instruction1.isStackManipulation()) {
                    int[] bh = instruction1.mapStackSlotForward(this.frameStates[i - 1].getStack(), stackFrameState.getStack(), bc);
                    if (bh.length == 0) {
                        bl = true;
                    } else if (bh.length == 2) {
                        this.traceStackValueUses(valueTraceFrame, valueFlowTracker, basicBlock, i + 1, bh[1]);
                    }

                    if (bh.length > 0) {
                        bc = bh[0];
                    }
                } else if (instruction1.consumesStackSlot(bc, stackFrameState.getStack().length)) {
                    if (!valueFlowTracker.traceAtInstruction(valueTraceFrame, i, basicBlock)) {
                        switch (instruction1.getOpcode()) {
                            case 179:
                            case 181:
                                this.traceFieldWriteUses(valueTraceFrame, valueFlowTracker, i);
                                break;
                            case 255:
                                LocalVariableInstruction localVariableInstruction = (LocalVariableInstruction) instruction1;
                                LocalVariableAccessKind localVariableAccessKind = localVariableInstruction.getAccessKind();
                                localVariableInstruction.getAccessKind();
                                if (localVariableAccessKind == LocalVariableAccessKind.OBJECT_STORE) {
                                    this.traceLocalVariableUses(valueTraceFrame, valueFlowTracker, basicBlock, i + 1, localVariableInstruction.getLocalIndex());
                                }
                        }
                    }

                    bl = true;
                }
            }

            if (!bl) {
                List list1 = basicBlock.getSuccessorsCopy();
                if (list1 != null && list1.size() > 0) {
                    for (int i = 0; i < list1.size(); i++) {
                        BasicBlock basicBlock1 = (BasicBlock) list1.get(i);
                        this.traceStackValueUses(valueTraceFrame, valueFlowTracker, basicBlock1, basicBlock1.getStartIndex(), bc);
                    }
                } else if (basicBlock.hasRetBlocks()) {
                    List list2 = basicBlock.getRetBlocks();

                    for (int i = 0; i < list2.size(); i++) {
                        BasicBlock basicBlock2 = (BasicBlock) list2.get(i);
                        this.traceStackValueUses(valueTraceFrame, valueFlowTracker, basicBlock2, basicBlock2.getStartIndex(), bc);
                    }
                } else {
                    Instruction instruction2 = (Instruction) this.instructions.get(endIndex);
                }
            }
        }
    }

    public int getStackDepthAt(int ba) {
        return this.frameStates[ba].getStackDepth();
    }

    public BasicBlock getBlock() {
        if (0 >= this.basicBlocks.size()) {
            throw new IllegalArgumentException("Invalid index : " + 0 + " > " + this.basicBlocks.size());
        } else {
            return (BasicBlock) this.basicBlocks.get(0);
        }
    }

    public void traceLocalVariableSource(ValueTraceFrame valueTraceFrame, BasicBlock basicBlock, int ba, int bb) throws ZkmException, IOException {
        Triple triple = new Triple(this.methodBytecode, INTEGER_CACHE.valueOf(ba), INTEGER_CACHE.valueOf(bb));
        valueTraceFrame.visitCounts.add(triple);
        if (valueTraceFrame.visitCounts.getCount(triple) > 9) {
            valueTraceFrame.results.add(new InstructionReasonNote(null, "Possible loop"));
        } else {
            HashSet hashSet = (HashSet) valueTraceFrame.localSourceCache.get(triple);
            int startIndex = basicBlock.getStartIndex();
            if (hashSet != null
                    && ba - 1 >= startIndex
                    && valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_1
                    && valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_2) {
                valueTraceFrame.results.addAll(hashSet);
            } else {
                boolean bl = false;
                hashSet = ZkmUtils.createHashSet(13);
                HashSet hashSet1 = valueTraceFrame.swapResults(hashSet);

                for (int i = ba - 1; i >= startIndex; i += -1) {
                    Instruction instruction1 = (Instruction) this.instructions.get(i);
                    StackFrameState stackFrameState = this.frameStates[i];
                    if (instruction1.isStoreTo(bb)) {
                        if (i > startIndex) {
                            this.traceStackValueSource(valueTraceFrame, basicBlock, i, stackFrameState.getStackDepth());
                            bl = true;
                            break;
                        }

                        List list1 = basicBlock.getPredecessors();
                        if (list1 != null && list1.size() > 0) {
                            for (int j = 0; j < list1.size(); j++) {
                                BasicBlock basicBlock1 = (BasicBlock) list1.get(j);
                                this.traceStackValueSource(valueTraceFrame, basicBlock1, basicBlock1.getEndIndex() + 1, stackFrameState.getStackDepth());
                            }

                            bl = true;
                            break;
                        }
                    }
                }

                if (!bl) {
                    List list2 = basicBlock.getPredecessors();
                    if (list2 != null && list2.size() > 0) {
                        for (int i = 0; i < list2.size(); i++) {
                            BasicBlock basicBlock2 = (BasicBlock) list2.get(i);
                            this.traceLocalVariableSource(valueTraceFrame, basicBlock2, basicBlock2.getEndIndex() + 1, bb);
                        }
                    } else if (basicBlock.isCatchBlock()) {
                        this.traceLocalThroughCatchHandler(valueTraceFrame, basicBlock, bb);
                    } else if (basicBlock.isHeader()) {
                        int bf;
                        if (valueTraceFrame.isStaticMethod) {
                            bf = bb;
                        } else {
                            bf = bb - 1;
                        }

                        if (bf < 0) {
                            valueTraceFrame.results
                                    .add(new InstructionReasonNote((Instruction) this.instructions.get(startIndex), "Source object reference : " + this.methodDescription));
                        } else if (bf < this.parameterSlotCount) {
                            switch (ReflectionAnalysisSwitchMap.ARGUMENT_KIND_SWITCH[valueTraceFrame.argumentKind.ordinal()]) {
                                case 1:
                                case 2:
                                    MethodInfo methodInfo1 = (MethodInfo) this.methodBytecode.getMethod();
                                    if (!methodInfo1.isStrictlyPrivate()) {
                                        valueTraceFrame.setComplete();
                                    }

                                    this.traceParameterFromCallers(valueTraceFrame, bf);
                                    break;
                                case 3:
                                case 4:
                                    valueTraceFrame.results
                                            .add(
                                                    new InstructionReasonNote(
                                                            (Instruction) this.instructions.get(startIndex), "Source is method parameter: " + valueTraceFrame.argumentKind
                                                    )
                                            );
                                    break;
                                case 5:
                                    valueTraceFrame.results.add(new MethodParameterValue(bf, this.methodBytecode));
                            }
                        } else {
                            valueTraceFrame.results
                                    .add(
                                            new InstructionReasonNote(
                                                    (Instruction) this.instructions.get(startIndex),
                                                    "Variable Uninitialized : "
                                                            + bf
                                                            + " "
                                                            + this.parameterSlotCount
                                                            + " "
                                                            + valueTraceFrame.argumentKind
                                                            + " "
                                                            + this.methodDescription
                                            )
                                    );
                        }
                    } else {
                        Instruction instruction2 = (Instruction) this.instructions.get(startIndex);
                        valueTraceFrame.results.add(new InstructionReasonNote(instruction2, "No source found 2) : " + valueTraceFrame.argumentKind));
                    }
                }

                hashSet = valueTraceFrame.swapResults(hashSet1);
                hashSet1.addAll(hashSet);
                if (ba - 1 >= startIndex
                        && valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_1
                        && valueTraceFrame.argumentKind != ReflectionArgumentKind.USER_DEFINED_METHOD_2) {
                    HashSet hashSet2 = ((java.util.HashSet) (valueTraceFrame.localSourceCache.put(triple, hashSet)));
                    if (hashSet2 != null && hashSet2.size() > 0) {
                        hashSet.addAll(hashSet2);
                    }
                }
            }
        }
    }

    public void collectSubroutineOrder(BasicBlock basicBlock, TwoKeyMap twoKeyMap, List list1) {
        list1.add(basicBlock);
        Map map1 = twoKeyMap.getInnerMap(basicBlock);
        if (map1 != null) {
            Iterator iterator = map1.keySet().iterator();

            while (iterator.hasNext()) {
                this.collectSubroutineOrder((BasicBlock) iterator.next(), twoKeyMap, list1);
            }
        }
    }

    public Set getMonitorTypes() {
        IdentityMapWrapper identityMapWrapper = new IdentityMapWrapper();
        Iterator iterator = this.instructions.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            if (instruction1.isMonitor()) {
                VerifierType verifierType = ((MonitorInstruction) instruction1).getLockedObjectType();
                identityMapWrapper.put(verifierType, verifierType);
            }
        }

        return identityMapWrapper.keySet();
    }

    public List getBlocksCopy() {
        return new ArrayList(this.basicBlocks);
    }

    public void reorderSwitchSuccessors() {
        NonNullList nonNullList = new NonNullList(this.instructions);

        for (int i = 0; i < this.basicBlocks.size(); i++) {
            BasicBlock basicBlock = (BasicBlock) this.basicBlocks.get(i);
            if (basicBlock.getLastInstruction(nonNullList) instanceof SwitchInstruction) {
                basicBlock.sortSuccessors();
            }
        }
    }

    public boolean computeLocalLiveness() {
        Iterator iterator = this.getLivenessStartBlocks().iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock = (BasicBlock) iterator.next();
            BitSet bitSet = new BitSet(this.maxLocals);
            if (this.methodBytecode.isConstructor()) {
                bitSet.set(0);
            }

            this.propagateLivenessBackward(basicBlock, basicBlock.getEndIndex(), bitSet);
        }

        this.propagateHandlerLiveness(this.catchHandlerBlocks);
        ArrayList arrayList = this.buildLoopNestingTrees();
        Iterator iterator3 = arrayList.iterator();

        while (iterator3.hasNext()) {
            GenericTree genericTree = (GenericTree) iterator3.next();
            Iterator iterator1 = genericTree.nodeIterator();

            while (iterator1.hasNext()) {
                ObjectTreeNode objectTreeNode = (ObjectTreeNode) iterator1.next();
                NaturalLoop naturalLoop = (NaturalLoop) objectTreeNode.getValue();
                BasicBlock basicBlock1 = naturalLoop.getHeader();
                BitSet bitSet1 = this.frameStates[basicBlock1.getStartIndex()].getLiveLocals();
                List list1 = naturalLoop.getLatchBlocks();
                Iterator iterator2 = list1.iterator();

                while (iterator2.hasNext()) {
                    BasicBlock basicBlock2 = (BasicBlock) iterator2.next();
                    this.propagateLivenessBackward(basicBlock2, basicBlock2.getEndIndex(), (BitSet) bitSet1.clone());
                }
            }
        }

        int bc = -1;

        boolean bl;
        do {
            bl = this.propagateHandlerLiveness(this.catchHandlerBlocks);
        } while (bl && ++bc < 10);

        boolean bl1 = !bl;
        if (bl1) {
            Iterator iterator4 = this.naturalLoops.iterator();

            label145:
            while (iterator4.hasNext()) {
                NaturalLoop naturalLoop1 = (NaturalLoop) iterator4.next();
                BasicBlock basicBlock4 = naturalLoop1.getHeader();
                BitSet bitSet4 = this.frameStates[basicBlock4.getStartIndex()].getLiveLocals();
                Iterator iterator6 = naturalLoop1.getLatchBlocks().iterator();

                while (iterator6.hasNext()) {
                    BasicBlock basicBlock5 = (BasicBlock) iterator6.next();
                    BitSet bitSet5 = this.frameStates[basicBlock5.getEndIndex()].getLiveLocals();

                    for (int i = 0; i < bitSet5.size(); i++) {
                        if (bitSet4.get(i) && !bitSet5.get(i)) {
                            bl1 = false;
                            break label145;
                        }
                    }
                }
            }
        }

        if (bl1) {
            Iterator iterator5 = this.catchHandlerBlocks.iterator();

            label125:
            while (iterator5.hasNext()) {
                BasicBlock basicBlock3 = (BasicBlock) iterator5.next();
                BitSet bitSet3 = this.frameStates[basicBlock3.getStartIndex()].getLiveLocals();
                List list2 = this.tryCatchInfosByHandler.getValues(basicBlock3);
                Iterator iterator7 = list2.iterator();

                while (iterator7.hasNext()) {
                    TryCatchBlockInfo tryCatchBlockInfo = (TryCatchBlockInfo) iterator7.next();

                    for (int i = tryCatchBlockInfo.startIndex; i < tryCatchBlockInfo.endIndex; i++) {
                        if (this.frameStates[i] != null) {
                            BitSet bitSet6 = this.frameStates[i].getLiveLocals();

                            for (int j = 0; j < bitSet3.size(); j++) {
                                if (bitSet3.get(j) && !bitSet6.get(j)) {
                                    bl1 = false;
                                    break label125;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (bl1 && !this.isStatic && !this.isConstructor) {
            boolean bl2 = true;

            for (int i = 0; i < this.frameStates.length; i++) {
                StackFrameState stackFrameState = this.frameStates[i];
                if (stackFrameState != null && !stackFrameState.getLocals()[0].isClassType()) {
                    bl2 = false;
                    break;
                }
            }

            if (bl2) {
                BitSet bitSet2 = new BitSet(this.maxLocals);
                bitSet2.set(0);

                for (StackFrameState stackFrameState1 : this.frameStates) {
                    if (stackFrameState1 != null) {
                        stackFrameState1.addLiveLocals(bitSet2);
                    }
                }
            }
        }

        if (!bl1) {
        }

        if (!bl1) {
        }

        return bl1;
    }

    public CallArgumentValues traceReflectionCallArguments(
            int ba,
            ReflectionApiMethod reflectionApiMethod,
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            Map map1,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            ClassMemberLookup classMemberLookup1,
            Map map6
    ) throws ZkmException, IOException {
        CallArgumentValues callArgumentValues = new CallArgumentValues(reflectionApiMethod);
        ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(ba);
        BasicBlock basicBlock = this.getBlockContaining(ba);
        StackFrameState stackFrameState = this.frameStates[ba];
        if (stackFrameState == null) {
            HashSet hashSet3 = ZkmUtils.createHashSet(5);
            hashSet3.add(new InstructionReasonNote(constantRefInstruction, "Appears in DEAD code. Can be ignored."));
            callArgumentValues.addArgumentValues(hashSet3);
            return callArgumentValues;
        }

        int stackDepth = stackFrameState.getStackDepth();
        boolean bl = this.methodBytecode.getMethod().isStatic();
        if (reflectionApiMethod.isObjectNameLookup()) {
            HashSet hashSet = ZkmUtils.createHashSet(5);
            hashSet.add(new InstructionReasonNote(constantRefInstruction, "Analysis not yet implemented"));
            callArgumentValues.addArgumentValues(hashSet);
        } else if (reflectionApiMethod.isFunctionalInterfaceLookup()) {
            if (this.methodBytecode.getMethod().getSourceName().equals("$deserializeLambda$")) {
                HashSet hashSet4 = ZkmUtils.createHashSet(5);
                HashSet hashSet1 = ZkmUtils.createHashSet(5);
                HashSet hashSet2 = ZkmUtils.createHashSet(5);
                ObservableHolder observableHolder = new ObservableHolder();
                ObservableHolder observableHolder1 = new ObservableHolder();
                ObservableHolder observableHolder2 = new ObservableHolder();
                this.traceDeserializeLambdaArguments(ba, observableHolder, observableHolder1, observableHolder2);
                hashSet4.add(observableHolder.getValue());
                hashSet1.add(observableHolder1.getValue());
                hashSet2.add(observableHolder2.getValue());
                callArgumentValues.addArgumentValues(hashSet4);
                callArgumentValues.addArgumentValues(hashSet1);
                callArgumentValues.addArgumentValues(hashSet2);
            } else {
                HashSet hashSet5 = ZkmUtils.createHashSet(5);
                hashSet5.add(new InstructionReasonNote(constantRefInstruction, "Unexpected usage (A) : " + this.methodDescription));
                callArgumentValues.addArgumentValues(hashSet5);
            }
        } else {
            Iterator iterator = reflectionApiMethod.getParamDetails().iterator();

            while (iterator.hasNext()) {
                ReflectionParamDetail reflectionParamDetail = (ReflectionParamDetail) iterator.next();
                int bd = stackDepth + reflectionApiMethod.getArgumentOffset() + reflectionParamDetail.getPosition();
                HashSet hashSet7 = ZkmUtils.createHashSet(13);
                ReflectionArgumentKind reflectionArgumentKind;
                switch (ReflectionAnalysisSwitchMap.PARAM_TYPE_SWITCH[reflectionParamDetail.getParamType().ordinal()]) {
                    case 1:
                        reflectionArgumentKind = ReflectionArgumentKind.CLASS_REFERENCE;
                        break;
                    case 2:
                        reflectionArgumentKind = ReflectionArgumentKind.METHODTYPE_REFERENCE;
                        break;
                    case 3:
                        reflectionArgumentKind = ReflectionArgumentKind.OBJECT_TYPE;
                        break;
                    default:
                        reflectionArgumentKind = ReflectionArgumentKind.NORMAL;
                }

                ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(
                        this,
                        hashSet7,
                        reflectionParamDetail.isArrayParam(),
                        reflectionArgumentKind,
                        nestedMultiMap,
                        nestedMultiMap1,
                        nestedMultiMap2,
                        this.superTypeResolver,
                        classMemberLookup1,
                        map1,
                        bl,
                        map2,
                        map3,
                        map4,
                        map5,
                        map6
                );
                this.traceStackValueSource(valueTraceFrame1, basicBlock, ba, bd);
                callArgumentValues.addArgumentValues(hashSet7);
                if (!valueTraceFrame1.isComplete()) {
                    callArgumentValues.setValid();
                }
            }

            if (reflectionApiMethod.isReceiverTargetClass()) {
                int bc = stackDepth - 1;
                HashSet hashSet6 = ZkmUtils.createHashSet(13);
                ValueTraceFrame valueTraceFrame = new ValueTraceFrame(
                        this,
                        hashSet6,
                        false,
                        ReflectionArgumentKind.CLASS_REFERENCE,
                        nestedMultiMap,
                        nestedMultiMap1,
                        nestedMultiMap2,
                        this.superTypeResolver,
                        classMemberLookup1,
                        map1,
                        bl,
                        map2,
                        map3,
                        map4,
                        map5,
                        map6
                );
                this.traceStackValueSource(valueTraceFrame, basicBlock, ba, bc);
                callArgumentValues.addArgumentValues(hashSet6);
            }
        }

        return callArgumentValues;
    }

    public List findNaturalLoops() {
        this.assignBlockIndices();
        this.reorderSwitchSuccessors();
        int ba = ZkmUtils.getPrimeCapacity(this.basicBlocks.size());
        TreeMap treeMap = new TreeMap();
        HashSet hashSet = ZkmUtils.createHashSet(ba);
        HashMap hashMap = ZkmUtils.createHashMap(ba);
        BitSet bitSet = new BitSet(this.basicBlocks.size());
        bitSet.set(this.entryBlock.getIndex());
        hashSet.add(this.entryBlock);
        BlockBitSet blockBitSet = new BlockBitSet(this.entryBlock, bitSet);
        hashMap.put(this.entryBlock, blockBitSet);
        new NonNullList(this.instructions);
        BasicBlock.findNaturalLoops(blockBitSet, treeMap, hashSet, hashMap);

        for (int i = 0; i < this.catchHandlerBlocks.size(); i++) {
            BasicBlock basicBlock = (BasicBlock) this.catchHandlerBlocks.get(i);
            if (!hashSet.contains(basicBlock)) {
                BitSet bitSet1 = new BitSet(this.basicBlocks.size());
                hashSet.add(basicBlock);
                BlockBitSet blockBitSet1 = new BlockBitSet(basicBlock, bitSet1);
                hashMap.put(basicBlock, blockBitSet1);
                bitSet1.set(basicBlock.getIndex());
                new NonNullList(this.instructions);
                BasicBlock.findNaturalLoops(blockBitSet1, treeMap, hashSet, hashMap);
            }
        }

        ArrayList arrayList = new ArrayList();
        Iterator iterator = treeMap.values().iterator();

        while (iterator.hasNext()) {
            arrayList.add(iterator.next());
        }

        iterator = arrayList.iterator();

        while (iterator.hasNext()) {
            NaturalLoop naturalLoop = (NaturalLoop) iterator.next();
            naturalLoop.computeLoopBlocks(new NonNullList(this.basicBlocks), this.handlerCoveredBlocks);
        }

        return arrayList;
    }

    private TryCatchBlockInfo[] getActiveHandlersAt(int ba, TryCatchBlockInfo[] tryCatchBlockInfos) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();

        label33:
        for (int i = 0; i < tryCatchBlockInfos.length; i++) {
            TryCatchBlockInfo tryCatchBlockInfo = tryCatchBlockInfos[i];
            if (ba >= tryCatchBlockInfo.startIndex && ba < tryCatchBlockInfo.endIndex) {
                for (int j = 0; j < arrayList.size(); j++) {
                    TryCatchBlockInfo tryCatchBlockInfo1 = (TryCatchBlockInfo) arrayList.get(j);
                    if (tryCatchBlockInfo.catchTypeDescriptor.equals(tryCatchBlockInfo1.catchTypeDescriptor)
                            || this.hierarchyQuery
                            .isSubclass(
                                    tryCatchBlockInfo.catchTypeDescriptor.substring(1, tryCatchBlockInfo.catchTypeDescriptor.length() - 1),
                                    tryCatchBlockInfo1.catchTypeDescriptor.substring(1, tryCatchBlockInfo1.catchTypeDescriptor.length() - 1)
                            )) {
                        continue label33;
                    }
                }

                arrayList.add(tryCatchBlockInfo);
            }
        }

        TryCatchBlockInfo[] tryCatchBlockInfos1 = new TryCatchBlockInfo[arrayList.size()];
        return ((com.zelix.klassmaster.classfile.insn.TryCatchBlockInfo[]) (arrayList.toArray(tryCatchBlockInfos1)));
    }

    public void traceNewArraySource(ValueTraceFrame valueTraceFrame, BasicBlock basicBlock, int ba) throws ZkmException, IOException {
        ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(ba);
        if (ba > basicBlock.getStartIndex()) {
            Instruction instruction1 = (Instruction) this.instructions.get(ba - 1);
            int bb;
            switch (instruction1.getOpcode()) {
                case 3:
                    bb = 0;
                    break;
                case 4:
                    bb = 1;
                    break;
                case 5:
                    bb = 2;
                    break;
                case 6:
                    bb = 3;
                    break;
                case 7:
                    bb = 4;
                    break;
                case 8:
                    bb = 5;
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                default:
                    valueTraceFrame.results
                            .add(new InstructionReasonNote(constantRefInstruction, "Instruction not handled 1) : " + instruction1 + " " + valueTraceFrame.argumentKind));
                    return;
                case 16:
                    bb = ((BipushInstruction) instruction1).getByteValue();
            }

            StackFrameState stackFrameState = this.frameStates[ba];
            if (bb > 0) {
                ArrayElementTracker arrayElementTracker = this.createArrayElementTracker();
                this.traceStackValueUses(valueTraceFrame, arrayElementTracker, basicBlock, ba + 1, stackFrameState.getStack().length - 1);
                TracedArrayValue tracedArrayValue = new TracedArrayValue(bb, arrayElementTracker.getWarnings());

                for (int i = 0; i < bb; i++) {
                    HashSet hashSet = arrayElementTracker.getElementValues(i);
                    if (hashSet == null || hashSet.size() == 0) {
                        hashSet = ZkmUtils.createHashSet(3);
                        hashSet.add(new InstructionReasonNote(constantRefInstruction, arrayElementTracker.getWarnings().toString()));
                    }

                    tracedArrayValue.setElementValues(i, hashSet);
                }

                valueTraceFrame.results.add(tracedArrayValue);
            } else {
                valueTraceFrame.results.add(new TracedArrayValue(bb));
            }
        } else {
            valueTraceFrame.results.add(new InstructionReasonNote(constantRefInstruction, "Instruction not handled 2) : " + valueTraceFrame.argumentKind));
        }
    }

    public StackFrameState[] copyFrameStates() {
        return this.frameStates.clone();
    }

    public FrameStateKey getFrameStateKey(int ba) {
        return this.frameStates[ba] != null ? this.frameStates[ba].createKey() : null;
    }

    public StackFrameState[] computeStackMapFrameStates() throws ZkmException, IOException {
        Integer[] integers = this.getFrameLabelIndices();
        StackFrameState[] stackFrameStates = new StackFrameState[this.frameStates.length];
        System.arraycopy(this.frameStates, 0, stackFrameStates, 0, this.frameStates.length);
        BooleanFlag booleanFlag = new BooleanFlag();
        Integer[] integers1 = integers;
        int ba = integers1.length;

        for (int i = 0; i < ba; i++) {
            int bc = integers1[i];
            if (((LabelInstruction) this.instructions.get(bc)).hasUsageBits(1024)) {
                BasicBlock basicBlock = this.findBlockStartingAt(bc);
                Iterator iterator = this.tryCatchInfosByHandler.getValues(basicBlock).iterator();

                while (iterator.hasNext()) {
                    TryCatchBlockInfo tryCatchBlockInfo = (TryCatchBlockInfo) iterator.next();
                    Integer[] integers2 = integers;
                    int bd = integers2.length;

                    for (int j = 0; j < bd; j++) {
                        int bf = integers2[j];
                        if (bf >= tryCatchBlockInfo.startIndex && bf < tryCatchBlockInfo.endIndex) {
                            StackFrameState.mergeLocals(
                                    this.superTypeResolver,
                                    stackFrameStates[bc].getLocals(),
                                    stackFrameStates[bf].getLocals(),
                                    false,
                                    booleanFlag,
                                    "Building special StackMapTable"
                            );
                        }
                    }
                }
            }
        }

        return stackFrameStates;
    }

    public boolean isGetPropertyWithDefaultCall(ResolvedMethodRef resolvedMethodRef, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        return this.isPropertiesMethodCall(resolvedMethodRef, "getProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", classMemberLookup1);
    }

    private int getLastCoveredInstructionIndex(BasicBlock basicBlock, BasicBlock basicBlock1) {
        int startIndex = basicBlock.getStartIndex();
        Iterator iterator = this.tryCatchInfosByHandler.getValues(basicBlock1).iterator();

        while (iterator.hasNext()) {
            TryCatchBlockInfo tryCatchBlockInfo = (TryCatchBlockInfo) iterator.next();
            if (basicBlock.getStartIndex() < tryCatchBlockInfo.endIndex && basicBlock.getEndIndex() >= tryCatchBlockInfo.startIndex) {
                int bb;
                if (tryCatchBlockInfo.endIndex > basicBlock.getEndIndex()) {
                    bb = basicBlock.getEndIndex();
                } else {
                    bb = Math.max(0, tryCatchBlockInfo.endIndex - 1);
                }

                if (bb > startIndex) {
                    startIndex = bb;
                }
            }
        }

        return startIndex;
    }

    public int getBlockCount() {
        return this.basicBlocks.size();
    }

    public List getNaturalLoops() {
        return this.naturalLoops;
    }

    public int computeMaxStack() {
        if (!this.closed && this.frameStates != null) {
            int ba = 0;

            for (StackFrameState stackFrameState : this.frameStates) {
                if (stackFrameState != null) {
                    int stackSlotCount = stackFrameState.getStackSlotCount();
                    if (stackSlotCount > ba) {
                        ba = stackSlotCount;
                    }
                }
            }

            return ba;
        } else {
            throw new IllegalStateException("Closed object");
        }
    }

    private void collectReachableBlocks(BasicBlock basicBlock, Set set1) {
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        hashSet.add(basicBlock);

        do {
            hashSet1.clear();
            Iterator iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                BasicBlock basicBlock1 = (BasicBlock) iterator.next();
                if (set1.add(basicBlock1)) {
                    Enumeration enumeration = basicBlock1.enumerateSuccessors();
                    if (enumeration != null) {
                        while (enumeration.hasMoreElements()) {
                            BasicBlock basicBlock2 = (BasicBlock) enumeration.nextElement();
                            hashSet1.add(basicBlock2);
                        }
                    }

                    BasicBlock basicBlock3 = basicBlock1.getJsrReturnBlock();
                    if (basicBlock3 != null) {
                        hashSet1.add(basicBlock3);
                    }
                }
            }

            hashSet.clear();
            hashSet.addAll(hashSet1);
        } while (hashSet.size() > 0);
    }

    public static VerifierType[] createInitialLocals(int ba, boolean bl, List list1, boolean bl1, String string, MutableInt mutableInt) {
        VerifierType[] verifierTypes = VerifierType.createArray(ba);
        int bb = 0;
        if (!bl) {
            bb++;
            verifierTypes[0] = VerifierType.forDescriptor("L" + string + ";", !bl1);
        }

        if (list1 != null) {
            for (int i = 0; i < list1.size(); i++) {
                String string1 = (String) list1.get(i);
                verifierTypes[bb++] = VerifierType.forDescriptor(string1);
                mutableInt.incrementAndGet();
                if (string1.equals("J") || string1.equals("D")) {
                    verifierTypes[bb++] = VerifierType.WIDE_SECOND_SLOT;
                    mutableInt.incrementAndGet();
                }
            }
        }

        return verifierTypes;
    }

    public ArrayElementTracker createArrayElementTracker() {
        return new ArrayElementTracker(this);
    }

    public void traceDeserializeLambdaArguments(
            int ba, ObservableHolder observableHolder, ObservableHolder observableHolder1, ObservableHolder observableHolder2
    ) throws ZkmException, IOException {
        Instruction instruction1 = (Instruction) this.instructions.get(ba);
        if (this.instructions.size() > ba + 1) {
            Instruction instruction2 = (Instruction) this.instructions.get(ba + 1);
            if (instruction2.isStringConstantLoad()) {
                ResolvedStringConstant resolvedStringConstant;
                if (instruction2.getOpcode() == 18) {
                    resolvedStringConstant = (ResolvedStringConstant) ((LdcInstruction) instruction2).getConstantPoolEntry();
                } else {
                    resolvedStringConstant = (ResolvedStringConstant) ((ConstantRefInstruction) instruction2).getConstantPoolEntry();
                }

                observableHolder.setValue(new StringConstantNameRef(resolvedStringConstant));
                ResolvedStringConstant resolvedStringConstant1 = null;

                for (int i = ba - 1; i > 0; i += -1) {
                    Instruction instruction3 = (Instruction) this.instructions.get(i);
                    if (instruction3.getOpcode() == 182) {
                        ResolvedMethodRefConstant resolvedMethodRefConstant = (ResolvedMethodRefConstant) ((ConstantRefInstruction) instruction3)
                                .getConstantPoolEntry();
                        if (resolvedMethodRefConstant.getMemberName().equals("getFunctionalInterfaceClass")) {
                            Instruction instruction4 = (Instruction) this.instructions.get(i + 1);
                            if (instruction4.isStringConstantLoad()) {
                                if (instruction4.getOpcode() == 18) {
                                    resolvedStringConstant1 = (ResolvedStringConstant) ((LdcInstruction) instruction4).getConstantPoolEntry();
                                } else {
                                    resolvedStringConstant1 = (ResolvedStringConstant) ((ConstantRefInstruction) instruction4).getConstantPoolEntry();
                                }

                                observableHolder2.setValue(new StringConstantNameRef(resolvedStringConstant1));
                            }
                            break;
                        }
                    }
                }

                if (resolvedStringConstant1 != null) {
                    int bc = this.instructions.size();

                    for (int i = ba + 2; i < bc; i++) {
                        Instruction instruction6 = (Instruction) this.instructions.get(i);
                        if (instruction6.getOpcode() == 182
                                && ((ResolvedMethodRefConstant) ((ConstantRefInstruction) instruction6).getConstantPoolEntry())
                                .getMemberName()
                                .equals("getFunctionalInterfaceMethodSignature")) {
                            if (i + 1 < bc) {
                                Instruction instruction5 = (Instruction) this.instructions.get(i + 1);
                                if (instruction5.isStringConstantLoad()) {
                                    ResolvedStringConstant resolvedStringConstant2;
                                    if (instruction5.getOpcode() == 18) {
                                        resolvedStringConstant2 = (ResolvedStringConstant) ((LdcInstruction) instruction5).getConstantPoolEntry();
                                    } else {
                                        resolvedStringConstant2 = (ResolvedStringConstant) ((ConstantRefInstruction) instruction5).getConstantPoolEntry();
                                    }

                                    observableHolder1.setValue(new StringConstantNameRef(resolvedStringConstant2));
                                }
                            }
                            break;
                        }
                    }

                    if (observableHolder1.isValueNull()) {
                        observableHolder1.setValue(
                                new InstructionReasonNote(instruction1, "Unexpected usage (B) : " + instruction1.getMnemonic() + " : " + this.methodDescription)
                        );
                    }
                } else {
                    observableHolder1.setValue(
                            new InstructionReasonNote(instruction1, "Unexpected usage (C) : " + instruction1.getMnemonic() + " : " + this.methodDescription)
                    );
                    observableHolder2.setValue(new InstructionReasonNote(instruction1, "Unexpected usage (D) "));
                }
            } else {
                observableHolder.setValue(
                        new InstructionReasonNote(instruction2, "Unexpected instruction : " + instruction2.getMnemonic() + " : " + this.methodDescription)
                );
                observableHolder1.setValue(new InstructionReasonNote(instruction1, "Unexpected usage (E) "));
                observableHolder2.setValue(new InstructionReasonNote(instruction1, "Unexpected usage (F) "));
            }
        } else {
            observableHolder.setValue(
                    new InstructionReasonNote(instruction1, "Unexpected usage (G) : " + this.methodDescription + " : " + ba + " : " + this.instructions.size())
            );
            observableHolder1.setValue(new InstructionReasonNote(instruction1, "Unexpected usage (H) "));
            observableHolder2.setValue(new InstructionReasonNote(instruction1, "Unexpected usage (I) "));
        }
    }

    public void traceParameterFromCallers(ValueTraceFrame valueTraceFrame, int ba) throws IOException {
        MethodInfo methodInfo1 = (MethodInfo) this.methodBytecode.getMethod();
        int callStackOffset = methodInfo1.getCallStackOffset();
        MethodKey methodKey = methodInfo1.toMethodKey();
        if (valueTraceFrame.enterMethod(methodKey)) {
            ListMultimap listMultimap = valueTraceFrame.methodCallSites.getMultimap(methodKey);
            if (listMultimap != null) {
                Enumeration enumeration = listMultimap.keys();

                while (enumeration.hasMoreElements()) {
                    MethodBytecode methodBytecode1 = (MethodBytecode) enumeration.nextElement();

                    try {
                        MethodFlowAnalyzer methodFlowAnalyzer1 = methodBytecode1.getCachedFlowAnalyzer(
                                valueTraceFrame.flowAnalyzerCache, valueTraceFrame.superTypeResolver, this.hierarchyQuery
                        );
                        List list1 = listMultimap.getValues(methodBytecode1);

                        for (int i = 0; i < list1.size(); i++) {
                            int bd = (Integer) list1.get(i);
                            StackFrameState stackFrameState = methodFlowAnalyzer1.frameStates[bd];
                            if (stackFrameState != null) {
                                int be = stackFrameState.getStackDepth() + callStackOffset + methodInfo1.getParameterIndexForSlot(ba);
                                ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(this, valueTraceFrame, methodBytecode1.getMethod().isStatic());
                                BasicBlock basicBlock = methodFlowAnalyzer1.getBlockContaining(bd);
                                methodFlowAnalyzer1.traceStackValueSource(valueTraceFrame1, basicBlock, bd, be);
                            }
                        }
                    } catch (ZkmException zkmException) {
                        valueTraceFrame.results
                                .add(new InstructionReasonNote(null, "Exception '" + zkmException.getMessage() + "' : " + methodBytecode1.getClassName()));
                    }
                }
            }

            valueTraceFrame.exitMethod(methodKey);
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    public StackMapEntry[] buildStackMapEntries(StackMapAttribute stackMapAttribute, ConstantPool constantPool1, Set set1, List list1, Boolean boolean1) throws MethodAnalysisException {
        if (this.hasSubroutineCalls) {
            throw new MethodAnalysisException("'" + this.methodDescription + "' not already preverified", this.methodDescription);
        }

        Integer[] integers = this.getFrameLabelIndices();
        HashMap hashMap = ZkmUtils.createHashMap();
        HashMap hashMap1 = ZkmUtils.createHashMap();
        StackMapEntry[] stackMapEntrys = new StackMapEntry[integers.length];

        for (int i = 0; i < integers.length; i++) {
            int bb = integers[i];
            LabelInstruction labelInstruction = (LabelInstruction) this.instructions.get(bb);
            StackFrameState stackFrameState = this.frameStates[bb];
            stackMapEntrys[i] = new StackMapEntry(stackMapAttribute, labelInstruction, stackFrameState, constantPool1, set1, list1, boolean1, hashMap, hashMap1);
        }

        return stackMapEntrys;
    }

    public int getShortestReturnPathLength() {
        return this.shortestReturnPathLength;
    }

    public void traceMethodCallSource(ValueTraceFrame valueTraceFrame, BasicBlock basicBlock, int ba) throws ZkmException, IOException {
        ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(ba);
        ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) constantRefInstruction.getConstantPoolEntry();
        switch (ReflectionAnalysisSwitchMap.ARGUMENT_KIND_SWITCH[valueTraceFrame.argumentKind.ordinal()]) {
            case 1:
                if (this.isGetPropertyWithDefaultCall(resolvedMethodRef, valueTraceFrame.memberLookup)) {
                    if (valueTraceFrame.tracingPropertiesLoad) {
                        valueTraceFrame.results.add(new InstructionReasonNote(constantRefInstruction, "Possible \"getProperty\" call loop"));
                    } else {
                        HashSet hashSet1 = ZkmUtils.createHashSet(13);
                        HashSet hashSet4 = valueTraceFrame.swapResults(hashSet1);
                        int stackDepth = this.frameStates[ba].getStackDepth();
                        this.traceStackValueSource(valueTraceFrame, basicBlock, ba, stackDepth);
                        HashSet hashSet7 = ZkmUtils.createHashSet(13);
                        valueTraceFrame.swapResults(hashSet7);
                        this.traceStackValueSource(valueTraceFrame, basicBlock, ba, stackDepth + 1);
                        HashSet hashSet9 = ZkmUtils.createHashSet(13);
                        valueTraceFrame.swapResults(hashSet9);
                        this.traceStackValueSource(valueTraceFrame, basicBlock, ba, --stackDepth);
                        hashSet4.add(new PropertyLookupValue(hashSet1, hashSet7, hashSet9));
                        valueTraceFrame.swapResults(hashSet4);
                    }
                } else if (this.isGetPropertyCall(resolvedMethodRef, valueTraceFrame.memberLookup)) {
                    if (valueTraceFrame.tracingPropertiesLoad) {
                        valueTraceFrame.results.add(new InstructionReasonNote(constantRefInstruction, "Possible \"getProperty\" call loop"));
                    } else {
                        HashSet hashSet2 = ZkmUtils.createHashSet(13);
                        HashSet hashSet5 = valueTraceFrame.swapResults(hashSet2);
                        int bf = this.frameStates[ba].getStackDepth();
                        this.traceStackValueSource(valueTraceFrame, basicBlock, ba, bf);
                        HashSet hashSet8 = ZkmUtils.createHashSet(13);
                        valueTraceFrame.swapResults(hashSet8);
                        this.traceStackValueSource(valueTraceFrame, basicBlock, ba, --bf);
                        hashSet5.add(new PropertyLookupValue(hashSet2, hashSet8));
                        valueTraceFrame.swapResults(hashSet5);
                    }
                } else {
                    valueTraceFrame.results
                            .add(
                                    new InstructionReasonNote(
                                            constantRefInstruction, "Method call not handled: '" + resolvedMethodRef.getValueString() + "' : " + valueTraceFrame.argumentKind
                                    )
                            );
                }
                break;
            case 2:
            case 3:
                if (valueTraceFrame.pendingReflectionCalls.containsKey(resolvedMethodRef)) {
                    valueTraceFrame.results.add(new PendingReflectionValue(ba, this.methodBytecode));
                } else {
                    AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                    if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember()) {
                        MethodInfo methodInfo1 = (MethodInfo) abstractMethodInfo;
                        MethodKey methodKey = methodInfo1.toMethodKey();
                        ReturnedNameSource returnedNameSource = null;
                        if (valueTraceFrame.returnedNameSourceCache.containsKey(methodKey)) {
                            returnedNameSource = (ReturnedNameSource) valueTraceFrame.returnedNameSourceCache.get(methodKey);
                        } else {
                            MethodBytecode methodBytecode1 = methodInfo1.getBytecode();
                            if (methodBytecode1 != null) {
                                try {
                                    MethodFlowAnalyzer methodFlowAnalyzer1 = methodBytecode1.getCachedFlowAnalyzer(
                                            valueTraceFrame.flowAnalyzerCache, valueTraceFrame.superTypeResolver, this.hierarchyQuery
                                    );
                                    returnedNameSource = methodFlowAnalyzer1.resolveReturnedNameSource(valueTraceFrame);
                                    valueTraceFrame.returnedNameSourceCache.put(methodKey, returnedNameSource);
                                } catch (ZkmException zkmException) {
                                    valueTraceFrame.results
                                            .add(
                                                    new InstructionReasonNote(
                                                            constantRefInstruction,
                                                            "Exception '" + zkmException.getMessage() + "' : " + methodInfo1.getClassName() + "." + methodInfo1.getJvmName()
                                                    )
                                            );
                                }
                            } else {
                                valueTraceFrame.returnedNameSourceCache.put(methodKey, null);
                            }
                        }

                        if (returnedNameSource != null) {
                            if (returnedNameSource.hasStringConstant()) {
                                returnedNameSource.getStringConstant();
                                valueTraceFrame.results.add(new StringConstantNameRef(returnedNameSource.getStringConstant()));
                            } else if (returnedNameSource.isParameter()) {
                                int parameterIndex = returnedNameSource.getParameterIndex();
                                int bi = this.frameStates[ba].getStackDepth() + methodInfo1.getCallStackOffset() + methodInfo1.getParameterIndexForSlot(parameterIndex);
                                ValueTraceFrame valueTraceFrame3 = new ValueTraceFrame(this, valueTraceFrame, ReflectionArgumentKind.NORMAL);
                                this.traceStackValueSource(valueTraceFrame3, basicBlock, ba, bi);
                            } else if (returnedNameSource.hasFailureReason()) {
                                valueTraceFrame.results
                                        .add(
                                                new InstructionReasonNote(
                                                        constantRefInstruction,
                                                        returnedNameSource.getFailureReason() + " : " + constantRefInstruction.getConstantPoolEntry().getValueString()
                                                )
                                        );
                            } else if (returnedNameSource.hasArrayValue()) {
                                TracedArrayValue tracedArrayValue = returnedNameSource.getArrayValue();
                                if (tracedArrayValue.hasSingleValuePerElement()) {
                                    ArrayList arrayList = tracedArrayValue.getFirstValues();

                                    for (int i = 0; i < arrayList.size(); i++) {
                                        TrackedValue trackedValue2 = (TrackedValue) arrayList.get(i);
                                        if (trackedValue2 instanceof MethodParameterValue) {
                                            MethodParameterValue methodParameterValue1 = (MethodParameterValue) trackedValue2;
                                            if (methodParameterValue1.getMethodBytecode() == returnedNameSource.getMethodBytecode()) {
                                                int bk = methodParameterValue1.getParameterIndex();
                                                int bd = this.frameStates[ba].getStackDepth() + methodInfo1.getCallStackOffset() + methodInfo1.getParameterIndexForSlot(bk);
                                                ValueTraceFrame valueTraceFrame2 = new ValueTraceFrame(this, valueTraceFrame, ReflectionArgumentKind.NORMAL);
                                                this.traceStackValueSource(valueTraceFrame2, basicBlock, ba, bd);
                                            } else {
                                                valueTraceFrame.results
                                                        .add(
                                                                new InstructionReasonNote(
                                                                        constantRefInstruction,
                                                                        "Method Parameter : "
                                                                                + methodParameterValue1.getParameterIndex()
                                                                                + " in "
                                                                                + methodParameterValue1.getMethodBytecode().getMethod().buildDeclaration()
                                                                                + " : "
                                                                                + constantRefInstruction.getConstantPoolEntry().getValueString()
                                                                )
                                                        );
                                            }
                                        } else {
                                            valueTraceFrame.results.add(trackedValue2);
                                        }
                                    }
                                } else {
                                    valueTraceFrame.results
                                            .add(
                                                    new InstructionReasonNote(
                                                            constantRefInstruction, "Multiple sources : " + constantRefInstruction.getConstantPoolEntry().getValueString()
                                                    )
                                            );
                                }
                            }
                        } else {
                            valueTraceFrame.results
                                    .add(
                                            new InstructionReasonNote(
                                                    constantRefInstruction,
                                                    "Method was abstract or in an interface: "
                                                            + valueTraceFrame.argumentKind
                                                            + " : "
                                                            + constantRefInstruction.getConstantPoolEntry().getValueString()
                                            )
                                    );
                        }
                    } else {
                        HashSet hashSet10;
                        if (resolvedMethodRef.getReferencedClassName().equals("java/lang/Object")) {
                            if (resolvedMethodRef.getSignatureString().equals("getClass()Ljava/lang/Class;")) {
                                if (ba > basicBlock.getStartIndex()) {
                                    StackFrameState stackFrameState1 = this.frameStates[ba - 1];
                                    String string = stackFrameState1.getStack()[this.frameStates[ba].getStackDepth() - 1].getDescriptor();
                                    string = CommonSuperTypeResolver.stripClassDescriptor(string);
                                    valueTraceFrame.results.add(new TracedObjectType(constantRefInstruction, string));
                                    break;
                                }

                                hashSet10 = valueTraceFrame.results;
                            } else {
                                hashSet10 = valueTraceFrame.results;
                            }
                        } else {
                            hashSet10 = valueTraceFrame.results;
                        }

                        hashSet10.add(new InstructionReasonNote(constantRefInstruction, "Method call not internal: " + valueTraceFrame.argumentKind));
                    }
                }
                break;
            case 4:
                ReflectionApiMethod reflectionApiMethod = resolvedMethodRef.getExactReflectionApiMethod();
                if (reflectionApiMethod != null) {
                    List list1 = reflectionApiMethod.getParamDetails();

                    for (int i = 0; i < list1.size(); i++) {
                        ReflectionParamDetail reflectionParamDetail = (ReflectionParamDetail) list1.get(i);
                        if (reflectionParamDetail.isClassNameParam() || reflectionParamDetail.isClassOrPropertiesNameParam()) {
                            StackFrameState stackFrameState = this.frameStates[ba];
                            int bc = stackFrameState.getStackDepth() + reflectionApiMethod.getArgumentOffset() + reflectionParamDetail.getPosition();
                            ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(
                                    this, valueTraceFrame, ReflectionArgumentKind.USER_DEFINED_METHOD_2, valueTraceFrame.nestingLevel
                            );
                            this.traceStackValueSource(valueTraceFrame1, basicBlock, ba, bc);
                            return;
                        }
                    }
                } else if (this.isGetComponentTypeCall(resolvedMethodRef, valueTraceFrame.memberLookup)) {
                    HashSet hashSet3 = ZkmUtils.createHashSet(13);
                    HashSet hashSet6 = valueTraceFrame.swapResults(hashSet3);
                    int bg = this.frameStates[ba].getStackDepth() - 1;
                    this.traceStackValueSource(valueTraceFrame, basicBlock, ba, bg);
                    valueTraceFrame.swapResults(hashSet6);
                    if (hashSet3.size() == 0) {
                        hashSet6.add(new InstructionReasonNote(constantRefInstruction, "Unknown object for '" + resolvedMethodRef.getValueString() + "' call"));
                    } else {
                        Iterator iterator1 = hashSet3.iterator();

                        while (iterator1.hasNext()) {
                            TrackedValue trackedValue1 = (TrackedValue) iterator1.next();
                            if (trackedValue1 instanceof MethodParameterValue) {
                                MethodParameterValue methodParameterValue = (MethodParameterValue) trackedValue1;
                                HashSet hashSet = ZkmUtils.createHashSet(13);
                                hashSet6 = valueTraceFrame.swapResults(hashSet);
                                this.traceParameterFromCallers(valueTraceFrame, methodParameterValue.getParameterIndex());
                                valueTraceFrame.swapResults(hashSet6);
                                Iterator iterator = hashSet.iterator();

                                while (iterator.hasNext()) {
                                    TrackedValue trackedValue = (TrackedValue) iterator.next();
                                    if (trackedValue instanceof StringConstantNameRef) {
                                        hashSet6.add(new ClassDescriptorStringValue(((StringConstantNameRef) trackedValue).getStringConstant()));
                                    } else if (trackedValue instanceof InstructionReasonNote) {
                                        hashSet6.add(trackedValue1);
                                    }
                                }
                            } else if (trackedValue1 instanceof StringConstantNameRef) {
                                hashSet6.add(new ClassDescriptorStringValue(((StringConstantNameRef) trackedValue1).getStringConstant()));
                            } else if (trackedValue1 instanceof InstructionReasonNote) {
                                hashSet6.add(trackedValue1);
                            }
                        }
                    }
                } else {
                    valueTraceFrame.results
                            .add(
                                    new InstructionReasonNote(
                                            constantRefInstruction,
                                            "Source is method: " + constantRefInstruction.getConstantPoolEntry().getValueString() + " : " + valueTraceFrame.argumentKind
                                    )
                            );
                }
                break;
            case 5:
                valueTraceFrame.results
                        .add(
                                new InstructionReasonNote(
                                        constantRefInstruction,
                                        "Source is method: " + constantRefInstruction.getConstantPoolEntry().getValueString() + " : " + valueTraceFrame.argumentKind
                                )
                        );
        }
    }

    public HashSet traceFieldStoreValues(ValueTraceFrame valueTraceFrame, FieldInfo fieldInfo, List list1) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(list1.size() + 2));
        boolean bl = this.methodBytecode.getMethod().isStatic();

        for (int i = 0; i < list1.size(); i++) {
            int bb = (Integer) list1.get(i);
            BasicBlock basicBlock = this.getBlockContaining(bb);
            if (this.frameStates[bb] != null) {
                int bc = this.frameStates[bb].getStackDepth() + (fieldInfo.isStatic() ? 0 : 1);
                ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(
                        this,
                        hashSet,
                        false,
                        valueTraceFrame.argumentKind,
                        valueTraceFrame.fieldLoadSites,
                        valueTraceFrame.fieldStoreSites,
                        valueTraceFrame.methodCallSites,
                        valueTraceFrame.superTypeResolver,
                        valueTraceFrame.memberLookup,
                        valueTraceFrame.pendingReflectionCalls,
                        bl,
                        valueTraceFrame.returnedNameSourceCache,
                        valueTraceFrame.fieldValueCache,
                        valueTraceFrame.stackSourceCache,
                        valueTraceFrame.localSourceCache,
                        valueTraceFrame.getNestingLevel() + 1,
                        valueTraceFrame.flowAnalyzerCache
                );
                this.traceStackValueSource(valueTraceFrame1, basicBlock, bb, bc);
            }
        }

        return hashSet;
    }

    public void release() {
        if (!this.closed) {
            for (int i = 0; i < this.basicBlocks.size(); i++) {
                ((BasicBlock) this.basicBlocks.get(i)).clearLinks();
            }

            this.instructions = null;
            this.frameStates = null;
            this.exceptionTable = null;
            this.entryBlock = null;
            this.catchHandlerBlocks.clear();
            this.catchHandlerBlocks = null;
            this.basicBlocks.clear();
            this.basicBlocks = null;
            this.tryCatchInfosByHandler.clear();
            this.tryCatchInfosByHandler = null;
            this.initialLocals = null;
            if (this.retReturnTargets != null) {
                this.retReturnTargets.clear();
            }

            this.blockByIndexCache = null;
            this.naturalLoops.clear();
            this.naturalLoops = null;
            this.closed = true;
        }
    }

    public ObjectPair findLocalVariableScope(int ba, ObservableHolder observableHolder) throws ZkmException, IOException {
        int bb = -1;
        int bc = -1;
        String string = null;
        int bd = 0;

        for (int i = 0; i < this.frameStates.length; i++) {
            StackFrameState stackFrameState = this.frameStates[i];
            if (stackFrameState != null && stackFrameState.isLocalOccupied(ba)) {
                if (bb == -1) {
                    bb = bd;
                }

                String string1 = stackFrameState.getLocalTypeDescriptor(ba);
                if (string == null && string1 != null) {
                    string = string1;
                    observableHolder.setValue(string1);
                } else if (string1 != null && !string1.equals(string)) {
                    break;
                }

                bc = bd;
            }

            bd += ((Instruction) this.instructions.get(i)).getLength();
        }

        return new ObjectPair(bb, bc);
    }

    public ReturnedNameSource resolveReturnedNameSource(ValueTraceFrame valueTraceFrame) throws ZkmException, IOException {
        ReturnedNameSource returnedNameSource = null;
        boolean bl = this.methodBytecode.getMethod().isStatic();
        int ba = -1;
        int bb = this.instructions.size();

        for (int i = 0; i < bb; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.getOpcode() == 176) {
                if (ba == -1) {
                    ba = i;
                } else {
                    returnedNameSource = new ReturnedNameSource("Multiple ARETURNs", this.methodBytecode);
                }
            }
        }

        if (returnedNameSource == null) {
            if (ba > -1) {
                BasicBlock basicBlock = this.getBlockContaining(ba);
                StackFrameState stackFrameState = this.frameStates[ba];
                int stackDepth = stackFrameState.getStackDepth();
                HashSet hashSet = ZkmUtils.createHashSet(13);
                ValueTraceFrame valueTraceFrame1 = new ValueTraceFrame(
                        this,
                        hashSet,
                        false,
                        ReflectionArgumentKind.USER_DEFINED_METHOD_1,
                        valueTraceFrame.fieldLoadSites,
                        valueTraceFrame.fieldStoreSites,
                        valueTraceFrame.methodCallSites,
                        valueTraceFrame.superTypeResolver,
                        valueTraceFrame.memberLookup,
                        valueTraceFrame.pendingReflectionCalls,
                        bl,
                        valueTraceFrame.returnedNameSourceCache,
                        valueTraceFrame.fieldValueCache,
                        valueTraceFrame.stackSourceCache,
                        valueTraceFrame.localSourceCache,
                        valueTraceFrame.getNestingLevel() + 1,
                        valueTraceFrame.flowAnalyzerCache
                );
                this.traceStackValueSource(valueTraceFrame1, basicBlock, ba, stackDepth);
                if (hashSet.size() == 0) {
                    returnedNameSource = new ReturnedNameSource("No source found", this.methodBytecode);
                } else if (hashSet.size() == 1) {
                    Object object = hashSet.iterator().next();
                    if (object instanceof MethodParameterValue) {
                        MethodParameterValue methodParameterValue = (MethodParameterValue) object;
                        if (this.methodBytecode == methodParameterValue.getMethodBytecode()) {
                            returnedNameSource = new ReturnedNameSource(methodParameterValue.getParameterIndex(), this.methodBytecode);
                        } else {
                            returnedNameSource = new ReturnedNameSource(
                                    "NESTING TOO DEEP. "
                                            + this.methodBytecode.getQualifiedMethodName()
                                            + " calls "
                                            + methodParameterValue.getMethodBytecode().getQualifiedMethodName(),
                                    this.methodBytecode
                            );
                        }
                    } else if (object instanceof StringConstantNameRef) {
                        StringConstantNameRef stringConstantNameRef = (StringConstantNameRef) object;
                        returnedNameSource = new ReturnedNameSource(stringConstantNameRef.getStringConstant(), this.methodBytecode);
                    } else if (object instanceof TracedArrayValue) {
                        returnedNameSource = new ReturnedNameSource((TracedArrayValue) object, this.methodBytecode);
                    } else if (object instanceof InstructionReasonNote) {
                        InstructionReasonNote instructionReasonNote = (InstructionReasonNote) object;
                        returnedNameSource = new ReturnedNameSource(instructionReasonNote.getReason(), this.methodBytecode);
                    } else if (object instanceof PendingReflectionValue) {
                        PendingReflectionValue pendingReflectionValue = (PendingReflectionValue) object;
                        returnedNameSource = new ReturnedNameSource("Pending '" + pendingReflectionValue.getMethodDescription() + "'", this.methodBytecode);
                    } else {
                        returnedNameSource = new ReturnedNameSource("UNEXPECTED NAME SOURCE", this.methodBytecode);
                    }
                } else {
                    TracedArrayValue tracedArrayValue = new TracedArrayValue(hashSet.size());
                    int be = 0;
                    Iterator iterator = hashSet.iterator();

                    while (iterator.hasNext()) {
                        TrackedValue trackedValue = (TrackedValue) iterator.next();
                        tracedArrayValue.setElementValue(be++, trackedValue);
                    }

                    returnedNameSource = new ReturnedNameSource(tracedArrayValue, this.methodBytecode);
                }
            } else {
                returnedNameSource = new ReturnedNameSource("No ARETURN", this.methodBytecode);
            }
        } else {
            returnedNameSource = new ReturnedNameSource("Multiple ARETURNs", this.methodBytecode);
        }

        return returnedNameSource;
    }

    private void computeFrameStates(CommonSuperTypeResolver commonSuperTypeResolver1, TryCatchBlockInfo[][] tryCatchBlockInfos, boolean bl, String string) throws ZkmException, IOException {
        int bc = this.instructions.size();
        this.frameStates = new StackFrameState[bc];
        StackFrameState stackFrameState = new StackFrameState(this.initialLocals);
        Instruction instruction1 = (Instruction) this.instructions.get(0);
        CommonSuperTypeResolver commonSuperTypeResolver2 = commonSuperTypeResolver1;
        this.frameStates[0] = instruction1.computeFrameAfter(stackFrameState, bl, commonSuperTypeResolver2, string);
        TreeSet treeSet1 = new TreeSet();
        TreeSet treeSet2 = new TreeSet();
        UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue();
        treeSet1.add(INTEGER_CACHE.valueOf(0));
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        HashSet hashSet = null;
        HashSet hashSet1 = null;

        do {
            Integer integer;
            int bd;
            if (!treeSet1.isEmpty()) {
                integer = (Integer) treeSet1.first();
                bd = integer;
                treeSet1.remove(integer);
            } else if (!treeSet2.isEmpty()) {
                integer = (Integer) treeSet2.first();
                bd = integer;
                treeSet2.remove(integer);
            } else {
                integer = (Integer) uniqueWorkQueue.dequeue();
                bd = integer;
            }

            Instruction instruction2 = (Instruction) this.instructions.get(bd);
            if (instruction2 instanceof SwitchInstruction) {
                List list1 = ((SwitchInstruction) instruction2).getTargetLabels();
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    LabelInstruction labelInstruction1 = (LabelInstruction) iterator.next();
                    int instructionIndex = labelInstruction1.getInstructionIndex();
                    String string1 = string;
                    boolean bl1 = bl;
                    TreeSet treeSet = treeSet1;
                    int ba = instructionIndex;
                    int bb = bd;
                    this.o(bb, ba, treeSet, bl1, string1);
                }
            } else if (instruction2 instanceof BranchInstruction && !instruction2.isJsr()) {
                LabelInstruction labelInstruction = ((BranchInstruction) instruction2).getTargetLabel();
                int be = labelInstruction.getInstructionIndex();
                this.o(bd, be, treeSet1, bl, string);
            }

            if (instruction2.canFallThrough()) {
                if (instruction2.isRet()) {
                    twoKeyMap.removeInnerMap(instruction2);
                }

                int bo = bd + 1;
                String string2 = string;
                boolean bl2 = bl;
                TreeSet treeSet3 = treeSet1;
                int bg = bo;
                int bh = bd;
                this.o(bh, bg, treeSet3, bl2, string2);
            }

            TryCatchBlockInfo[] tryCatchBlockInfos1 = tryCatchBlockInfos[bd];
            if (tryCatchBlockInfos1 != null) {
                for (int i = 0; i < tryCatchBlockInfos1.length; i++) {
                    int bj = tryCatchBlockInfos1[i].handlerIndex;
                    SubroutineLocalsBitSet subroutineLocalsBitSet1 = this.frameStates[bd].getSubroutineLocals();
                    SubroutineLocalsBitSet subroutineLocalsBitSet = null;
                    if (this.frameStates[bj] != null) {
                        subroutineLocalsBitSet = this.frameStates[bj].getSubroutineLocals();
                    }

                    StackFrameState stackFrameState1;
                    if ((subroutineLocalsBitSet1 != null || subroutineLocalsBitSet != null)
                            && (subroutineLocalsBitSet1 == null || subroutineLocalsBitSet == null || !subroutineLocalsBitSet1.isSameSubroutine(subroutineLocalsBitSet))) {
                        if (subroutineLocalsBitSet1 != null && subroutineLocalsBitSet != null && !subroutineLocalsBitSet1.isSameSubroutine(subroutineLocalsBitSet)) {
                            stackFrameState1 = StackFrameState.createWithSubroutineLocals(
                                    this.frameStates[bd], this.frameStates[bj].getLocals(), subroutineLocalsBitSet, this.frameStates[bj].getHeldMonitors()
                            );
                        } else {
                            if (subroutineLocalsBitSet1 == null || subroutineLocalsBitSet != null) {
                                throw new MethodAnalysisException(
                                        "Method '" + this.methodDescription + "' could not be analyzed (A). No action required.", this.methodDescription
                                );
                            }

                            if (this.frameStates[bj] != null) {
                                stackFrameState1 = StackFrameState.createWithSubroutineLocals(
                                        this.frameStates[bd], this.frameStates[bj].getLocals(), subroutineLocalsBitSet, this.frameStates[bj].getHeldMonitors()
                                );
                            } else {
                                stackFrameState1 = this.frameStates[bd];
                            }
                        }
                    } else {
                        stackFrameState1 = this.frameStates[bd];
                    }

                    StackFrameState stackFrameState2 = new StackFrameState(
                            tryCatchBlockInfos1[i].catchTypeDescriptor,
                            stackFrameState1.getLocals(),
                            stackFrameState1.getSubroutineLocals(),
                            this.frameStates[bd].getHeldMonitors()
                    );
                    Instruction instruction3 = (Instruction) this.instructions.get(bj);
                    CommonSuperTypeResolver commonSuperTypeResolver3 = commonSuperTypeResolver1;
                    StackFrameState stackFrameState3 = instruction3.computeFrameAfter(stackFrameState2, bl, commonSuperTypeResolver3, string);
                    if (this.frameStates[bj] == null) {
                        this.frameStates[bj] = stackFrameState3;
                        treeSet1.add(INTEGER_CACHE.valueOf(bj));
                    } else {
                        BooleanFlag booleanFlag = new BooleanFlag();
                        StackFrameState stackFrameState4 = this.frameStates[bj].merge(commonSuperTypeResolver1, stackFrameState3, booleanFlag, string);
                        if (booleanFlag.getValue()) {
                            this.frameStates[bj] = stackFrameState4;
                            treeSet1.add(INTEGER_CACHE.valueOf(bj));
                        }
                    }
                }
            }

            if (instruction2.isJsr()) {
                BranchInstruction branchInstruction = (BranchInstruction) instruction2;
                LabelInstruction labelInstruction2 = branchInstruction.getTargetLabel();
                int bk = labelInstruction2.getInstructionIndex();
                StackFrameState stackFrameState6 = new StackFrameState(
                        this.frameStates[bd].getStack(),
                        this.frameStates[bd].getLocals(),
                        new SubroutineLocalsBitSet(labelInstruction2, this.maxLocals),
                        this.frameStates[bd].getHeldMonitors()
                );
                CommonSuperTypeResolver commonSuperTypeResolver4 = commonSuperTypeResolver1;
                StackFrameState stackFrameState7 = labelInstruction2.computeFrameAfter(stackFrameState6, bl, commonSuperTypeResolver4, string);
                if (this.frameStates[bk] == null) {
                    this.frameStates[bk] = stackFrameState7;
                    treeSet2.add(INTEGER_CACHE.valueOf(bk));
                } else {
                    StackFrameState stackFrameState8 = this.frameStates[bk];
                    BooleanFlag booleanFlag1 = new BooleanFlag();
                    StackFrameState stackFrameState10 = stackFrameState8.merge(commonSuperTypeResolver1, stackFrameState6, booleanFlag1, string);
                    if (booleanFlag1.getValue()) {
                        this.frameStates[bk] = stackFrameState10;
                        treeSet2.add(INTEGER_CACHE.valueOf(bk));
                    }
                }
            } else if (instruction2.isRet()) {
                LocalVariableInstruction localVariableInstruction = (LocalVariableInstruction) instruction2;
                boolean bl4 = this.retReturnTargets != null;
                String[] strings = new String[]{this.methodDescription + " " + bd};
                ZkmAssert.assertTrue(bl4, strings);
                Iterator iterator1 = this.retReturnTargets.getInnerMap(localVariableInstruction).keySet().iterator();
                boolean bl3 = false;

                while (iterator1.hasNext()) {
                    Integer integer1 = (Integer) iterator1.next();
                    int bm = integer1;
                    int bn = bm - 1;
                    if (this.frameStates[bn] == null) {
                        BasicBlock basicBlock = this.getBlockContaining(bn);
                        if (hashSet1 == null) {
                            hashSet1 = ZkmUtils.createHashSetFrom(this.basicBlocks);
                        }

                        if (hashSet1.contains(basicBlock)) {
                            uniqueWorkQueue.enqueue(integer);
                            bl3 = true;
                        }
                    } else if (!twoKeyMap.containsKeys(localVariableInstruction, integer1)) {
                        StackFrameState stackFrameState9 = StackFrameState.createWithSubroutineLocals(
                                this.frameStates[bd], this.frameStates[bn].getLocals(), this.frameStates[bn].getSubroutineLocals(), this.frameStates[bn].getHeldMonitors()
                        );
                        Instruction instruction4 = (Instruction) this.instructions.get(bm);
                        CommonSuperTypeResolver commonSuperTypeResolver5 = commonSuperTypeResolver1;
                        StackFrameState stackFrameState11 = instruction4.computeFrameAfter(stackFrameState9, bl, commonSuperTypeResolver5, string);
                        BooleanFlag booleanFlag2 = new BooleanFlag();
                        if (this.frameStates[bm] == null) {
                            this.frameStates[bm] = stackFrameState11;
                            treeSet1.add(integer1);
                        } else {
                            StackFrameState stackFrameState5 = this.frameStates[bm].merge(commonSuperTypeResolver1, stackFrameState11, booleanFlag2, string);
                            if (booleanFlag2.getValue()) {
                                this.frameStates[bm] = stackFrameState5;
                                treeSet1.add(integer1);
                            }
                        }

                        twoKeyMap.putValue(localVariableInstruction, integer1, integer1);
                    }
                }

                if (bl3) {
                    if (hashSet == null) {
                        hashSet = ZkmUtils.createHashSet();
                    }

                    if (treeSet1.size() == 0) {
                        Integer integer2 = uniqueWorkQueue.hashCode();
                        if (hashSet.contains(integer2)) {
                            throw new MethodAnalysisException(
                                    "Method '" + this.methodDescription + "' could not be analyzed (B). Please report this message to bugs@zelix.com.",
                                    this.methodDescription
                            );
                        }

                        hashSet.add(integer2);
                    } else {
                        hashSet.clear();
                    }
                }
            }
        } while (!treeSet1.isEmpty() || !treeSet2.isEmpty() || !uniqueWorkQueue.isEmpty());
    }

    public boolean hasNaturalLoops() {
        return this.naturalLoops != null && this.naturalLoops.size() > 0;
    }

    public void assignBlockIndices() {
        for (int i = 0; i < this.basicBlocks.size(); i++) {
            ((BasicBlock) this.basicBlocks.get(i)).setIndex(i);
        }
    }

    public boolean isPropertiesMethodCall(ResolvedMethodRef resolvedMethodRef, Object object, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        try {
            if (resolvedMethodRef.getSignatureString().equals(object)) {
                String string = resolvedMethodRef.getReferencedClassName();
                return string.equals("java/util/Properties") || classMemberLookup1.isSubclass(string, "java/util/Properties");
            } else {
                return false;
            }
        } catch (ClassFileLoadException classFileLoadException) {
            return false;
        }
    }

    public int[] findDeadCodeIndices() {
        ArrayList arrayList = new ArrayList();
        int ba = this.instructions.size();
        boolean bl = false;

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (this.frameStates[i] != null) {
                bl = false;
            } else {
                boolean bl1 = false;
                if (instruction1.isLabel()) {
                    LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                    if (i == ba - 1) {
                        bl1 = true;
                    } else if (labelInstruction.hasUsageBits(512)) {
                        bl1 = true;
                    } else {
                        for (int j = i + 1; j < ba; j++) {
                            if (!((Instruction) this.instructions.get(j)).isLabel()) {
                                if (this.frameStates[j] != null) {
                                    bl1 = true;
                                }
                                break;
                            }
                        }
                    }
                }

                if (bl || !bl1) {
                    arrayList.add(INTEGER_CACHE.valueOf(i));
                    bl = true;
                }
            }
        }

        int[] bd = new int[arrayList.size()];

        for (int i = 0; i < bd.length; i++) {
            bd[i] = (Integer) arrayList.get(i);
        }

        return bd;
    }

    public boolean hasHeldMonitorsAt(int ba) {
        if (this.frameStates != null && this.frameStates[ba] != null) {
            Set set1 = this.frameStates[ba].getHeldMonitors();
            return set1 != null && set1.size() > 0;
        } else {
            return false;
        }
    }

    private boolean propagateLivenessBackward(BasicBlock basicBlock, int ba, BitSet bitSet) {
        boolean bl = false;
        PendingBlockVisit pendingBlockVisit = new PendingBlockVisit(basicBlock, ba, bitSet);
        UniqueWorkQueue uniqueWorkQueue = null;
        boolean bl1 = true;

        label72:
        while (bl1 || uniqueWorkQueue != null && !uniqueWorkQueue.isEmpty()) {
            if (!bl1) {
                pendingBlockVisit = (PendingBlockVisit) uniqueWorkQueue.dequeue();
            }

            bl1 = false;
            BasicBlock basicBlock1 = pendingBlockVisit.block;
            int bb = pendingBlockVisit.startIndex;
            BitSet bitSet1 = pendingBlockVisit.liveLocals;

            for (int i = bb; i >= basicBlock1.getStartIndex(); i += -1) {
                StackFrameState stackFrameState = this.frameStates[i];
                if (stackFrameState != null) {
                    Instruction instruction1 = (Instruction) this.instructions.get(i);

                    for (int j = 0; j < this.maxLocals; j++) {
                        if (stackFrameState.isLocalDefined(j)) {
                            if (instruction1.accessesLocal(j)) {
                                bitSet1.set(j);
                            }
                        } else {
                            bitSet1.clear(j);
                        }
                    }

                    if (stackFrameState.hasLivenessInfo()) {
                        stackFrameState.getLiveLocals();
                    }

                    if (!stackFrameState.addLiveLocals(bitSet1)) {
                        continue label72;
                    }

                    bl = true;
                }
            }

            if (basicBlock1.hasPredecessors()) {
                if (uniqueWorkQueue == null) {
                    uniqueWorkQueue = new UniqueWorkQueue(Math.max(5, this.basicBlocks.size()));
                }

                Iterator iterator = basicBlock1.getUnmodifiablePredecessors().iterator();

                while (iterator.hasNext()) {
                    BasicBlock basicBlock2 = (BasicBlock) iterator.next();
                    uniqueWorkQueue.enqueue(new PendingBlockVisit(basicBlock2, basicBlock2.getEndIndex(), (BitSet) bitSet1.clone()));
                }
            }
        }

        return bl;
    }

    public void collectIntConstantSourcesFrom(List list1, BasicBlock basicBlock, int ba, int bb, Set set1) {
        int bc = bb;
        if (!set1.contains(basicBlock)) {
            set1.add(basicBlock);
            boolean bl = false;
            int startIndex = basicBlock.getStartIndex();

            for (int i = ba - 1; !bl && i >= startIndex; i += -1) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                StackFrameState stackFrameState = this.frameStates[i];
                if (instruction1.isStackManipulation()) {
                    bc = instruction1.mapStackSlotBackward(stackFrameState.getStack(), bc);
                } else if (stackFrameState.getStackDepth() - 1 == bc && instruction1.pushesValue()) {
                    switch (instruction1.getOpcode()) {
                        case 3:
                            list1.add(INTEGER_CACHE.valueOf(0));
                            break;
                        case 4:
                            list1.add(INTEGER_CACHE.valueOf(1));
                            break;
                        case 5:
                            list1.add(INTEGER_CACHE.valueOf(2));
                            break;
                        case 6:
                            list1.add(INTEGER_CACHE.valueOf(3));
                            break;
                        case 7:
                            list1.add(INTEGER_CACHE.valueOf(4));
                            break;
                        case 8:
                            list1.add(INTEGER_CACHE.valueOf(5));
                            break;
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        default:
                            list1.add("Instruction not handled: " + instruction1.getMnemonic());
                            break;
                        case 16:
                            BipushInstruction bipushInstruction = (BipushInstruction) instruction1;
                            list1.add(INTEGER_CACHE.valueOf(bipushInstruction.getByteValue()));
                    }

                    bl = true;
                }
            }

            if (!bl) {
                List list2 = basicBlock.getPredecessors();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        BasicBlock basicBlock1 = (BasicBlock) list2.get(i);
                        this.collectIntConstantSourcesFrom(list1, basicBlock1, basicBlock1.getEndIndex() + 1, bc, set1);
                    }
                } else {
                    list1.add("No value found");
                }
            }
        }
    }
}
