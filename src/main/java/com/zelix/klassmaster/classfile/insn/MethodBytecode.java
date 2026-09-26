package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ClassMemberRef;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodEntry;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodIndex;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodInfo;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodsAttribute;
import com.zelix.klassmaster.classfile.attribute.CodeAttributeBody;
import com.zelix.klassmaster.classfile.attribute.ConcatRecipePart;
import com.zelix.klassmaster.classfile.attribute.ExceptionTableEntry;
import com.zelix.klassmaster.classfile.attribute.LineNumberEntry;
import com.zelix.klassmaster.classfile.attribute.LocalVariableEntry;
import com.zelix.klassmaster.classfile.attribute.LocalVariableEntryComparator;
import com.zelix.klassmaster.classfile.constpool.AbstractConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantInteger;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.MethodHandleRefKind;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInvokeDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMemberRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodHandleConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedNameAndType;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.engine.ProcessingStatistics;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.CorruptHierarchyException;
import com.zelix.klassmaster.exceptions.MethodAnalysisException;
import com.zelix.klassmaster.exceptions.StackAnalysisException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.obfuscator.constants.EncryptedValueLocation;
import com.zelix.klassmaster.obfuscator.constants.IntegerEncryptionExclusions;
import com.zelix.klassmaster.obfuscator.constants.LongEncryptionExclusionHandler;
import com.zelix.klassmaster.obfuscator.constants.LookupStrategySwitchMap;
import com.zelix.klassmaster.obfuscator.constants.ObfuscatedReferenceSlot;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationManager;
import com.zelix.klassmaster.obfuscator.flow.FlowSplitCandidate;
import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;
import com.zelix.klassmaster.obfuscator.parameters.AddedParameter;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.obfuscator.parameters.IndexedLocalSlot;
import com.zelix.klassmaster.obfuscator.parameters.IntParameterValue;
import com.zelix.klassmaster.obfuscator.parameters.KeyParameterValue;
import com.zelix.klassmaster.obfuscator.parameters.LongKeyParameterValue;
import com.zelix.klassmaster.obfuscator.parameters.MethodParamChangeNode;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterChanger;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterObfuscator;
import com.zelix.klassmaster.obfuscator.parameters.ParameterUsageInfo;
import com.zelix.klassmaster.obfuscator.references.ReferenceObfuscator;
import com.zelix.klassmaster.obfuscator.reflection.KnownNameValue;
import com.zelix.klassmaster.obfuscator.reflection.LiteralStringValue;
import com.zelix.klassmaster.obfuscator.reflection.NullReflectionValue;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionApiMethod;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionCallSite;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionLookupTemplate;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionParamDetail;
import com.zelix.klassmaster.obfuscator.string.EncryptedStringLocation;
import com.zelix.klassmaster.obfuscator.string.StringEncryptionExclusionSpec;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.NonNullList;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.PriorityEntryComparator;
import com.zelix.klassmaster.util.RankedEntryComparator;
import com.zelix.klassmaster.util.RankedValue;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

public class MethodBytecode extends ClassFileComponent {
    private boolean hasLoops;
    private int stringConstantCount;
    public int longConstantCount;
    public int intConstantCount;
    private boolean modified;
    public int minReturnDistance = -1;
    private int codeLength;
    private ArrayList instructions;
    private final LocalVariableList localVariableList;

    public void recordMemberNameValue(String string, TrackedValue trackedValue, TwoKeyMap twoKeyMap, Set set1) {
        String string1 = string;
        if (string1 != null && string1.length() > 0 && trackedValue instanceof StringConstantNameRef) {
            string1 = ZkmUtils.dotsToSlashes(string1);
            twoKeyMap.putValue(string1, trackedValue, trackedValue);
            if (trackedValue instanceof StringConstantNameRef) {
                set1.add(((StringConstantNameRef) trackedValue).getStringConstant());
            }
        }
    }

    public void collectFlowSplitCandidates(
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ObservableHolder observableHolder,
            BasicBlock basicBlock,
            BasicBlock basicBlock1,
            BasicBlock basicBlock2,
            Set set1,
            StackFrameState[] stackFrameStates,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl,
            Random random1,
            String string
    ) throws ZkmException, IOException {
        int startIndex = basicBlock.getStartIndex();
        int endIndex = basicBlock.getEndIndex();
        ArrayList arrayList = new ArrayList();

        for (int i = endIndex - 1; i >= startIndex; i += -1) {
            if (stackFrameStates[i].isFullyInitialized()) {
                int bd = basicBlock1.findMatchingFrame(
                        stackFrameStates[i],
                        set1,
                        stackFrameStates,
                        ZkmUtils.createHashSet(),
                        basicBlock1,
                        basicBlock2,
                        basicBlock,
                        new NonNullList(this.instructions),
                        commonSuperTypeResolver1,
                        scriptEnvironment1,
                        observableHolder,
                        bl,
                        string
                );
                if (bd != -1) {
                    arrayList.add(new FlowSplitCandidate(this, i, bd, (BasicBlock) observableHolder.getValue()));
                    if (HiddenOptionFlags.STOP_AT_FIRST_BLOCK_SPLIT) {
                        break;
                    }
                }
            }
        }

        this.selectFlowSplitCandidate(mutableInt, mutableInt1, observableHolder, arrayList, random1);
    }

    public void collectReferencedMembers(Set set1, Set set2, Set set3, Set set4) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            switch (instruction1.getOpcode()) {
                case 18:
                case 19:
                    ConstantPoolEntry constantPoolEntry = null;
                    if (instruction1 instanceof LdcInstruction) {
                        constantPoolEntry = ((LdcInstruction) instruction1).getConstantPoolEntry();
                    } else if (instruction1 instanceof ConstantRefInstruction) {
                        constantPoolEntry = ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    }

                    if (constantPoolEntry instanceof ResolvedClassConstant) {
                        ResolvedClassConstant resolvedClassConstant1 = (ResolvedClassConstant) constantPoolEntry;
                        ClassFileBase classFileBase1 = resolvedClassConstant1.lookupClass();
                        if (classFileBase1 != null) {
                            set1.add(classFileBase1);
                        }
                    }
                    break;
                case 178:
                case 179:
                    ResolvedFieldRef resolvedFieldRef1 = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectFieldRef(resolvedFieldRef1, set3, set2);
                    break;
                case 180:
                case 181:
                    ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectFieldRef(resolvedFieldRef, set3, set1);
                    break;
                case 182:
                case 183:
                case 185:
                    ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectMethodRef(resolvedMethodRef1, set4, set1);
                    break;
                case 184:
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectMethodRef(resolvedMethodRef, set4, set2);
                    break;
                case 186:
                    ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectInvokeDynamicRefs(resolvedInvokeDynamic, set3, set4, set2, set1);
                    break;
                case 187:
                case 189:
                case 192:
                case 193:
                case 197:
                    ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    ClassFileBase classFileBase = resolvedClassConstant.lookupClass();
                    if (classFileBase != null) {
                        set1.add(classFileBase);
                    }
            }
        }
    }

    public void indexFieldAndMethodUsages(
            NestedMultiMap nestedMultiMap, NestedMultiMap nestedMultiMap1, NestedMultiMap nestedMultiMap2, IntegerCache integerCache1
    ) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            switch (instruction1.getOpcode()) {
                case 178:
                case 180:
                    ConstantRefInstruction constantRefInstruction2 = (ConstantRefInstruction) instruction1;
                    ResolvedFieldRef resolvedFieldRef1 = (ResolvedFieldRef) ((ConstantPoolOperand) constantRefInstruction2).getConstantPoolEntry();
                    AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) resolvedFieldRef1.getResolvedMember();
                    if (abstractFieldInfo1 != null && abstractFieldInfo1.isProgramMember()) {
                        MemberRefKey memberRefKey1 = abstractFieldInfo1.toMemberRefKey();
                        nestedMultiMap.addValue(memberRefKey1, this, integerCache1.valueOf(i));
                    }
                    break;
                case 179:
                case 181:
                    ConstantRefInstruction constantRefInstruction1 = (ConstantRefInstruction) instruction1;
                    ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantPoolOperand) constantRefInstruction1).getConstantPoolEntry();
                    AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
                    if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
                        MemberRefKey memberRefKey = abstractFieldInfo.toMemberRefKey();
                        nestedMultiMap1.addValue(memberRefKey, this, integerCache1.valueOf(i));
                    }
                    break;
                case 182:
                case 183:
                case 184:
                case 185:
                    ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) instruction1;
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                    AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                    if (abstractMethodInfo != null) {
                        MethodKey methodKey = abstractMethodInfo.toMethodKey();
                        nestedMultiMap2.addValue(methodKey, this, integerCache1.valueOf(i));
                    }
            }
        }
    }

    public boolean hasPushOnlyArguments(int ba, AbstractMethodInfo abstractMethodInfo, MutableInt mutableInt) {
        int bb = ba;
        int bc = abstractMethodInfo.getWidenedParameterTypes().size();
        int bd = 1;

        for (int i = bc - 1; i >= 0; i += -1) {
            int bf = bb - bc + i + 1;
            if (!((Instruction) this.instructions.get(bf)).pushesWithoutPopping()) {
                return false;
            }

            bb += -1;
            bd++;
        }

        if (!abstractMethodInfo.isStatic()) {
            Instruction instruction1 = (Instruction) this.instructions.get(bb);
            if (!instruction1.pushesWithoutPopping()) {
                return false;
            }

            bd++;
        }

        mutableInt.setValue(bd);
        return true;
    }

    public void collectLabelReferences(SetMultiMap setMultiMap) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1 instanceof LabelTargetHolder) {
                ((LabelTargetHolder) instruction1).registerLabelTargets(setMultiMap);
            }
        }
    }

    public void indexCalledMethods(ListMultimap listMultimap) {
        int ba = this.instructions.size();
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(Math.min(Math.max(5, ba / 2), 101)));

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) ((ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry())
                        .getResolvedMember();
                if (abstractMethodInfo != null
                        && !abstractMethodInfo.isConstructor()
                        && !abstractMethodInfo.isStaticInitializer()
                        && instruction1.getOpcode() != 183) {
                    hashSet.add(abstractMethodInfo);
                }
            }
        }

        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator.next();
            listMultimap.addValue(abstractMethodInfo1, this);
        }
    }

    public List obfuscateReferences(
            List list1,
            NestedMultiMap nestedMultiMap,
            Map map1,
            Map map2,
            Map map3,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedMethodRef resolvedMethodRef1,
            Map map4,
            ResolvedMethodRef resolvedMethodRef2,
            int[] ba,
            Map map5,
            ObservableHolder observableHolder,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            ReferenceObfuscator referenceObfuscator,
            ConstantPool constantPool1,
            List list2,
            List list3,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        com.zelix.klassmaster.classfile.insn.MethodFlowAnalyzer methodFlowAnalyzer = null;
        HashMap hashMap = ZkmUtils.createHashMap();
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            ClassMemberRef classMemberRef = (ClassMemberRef) iterator.next();
            Boolean boolean1 = bl && ReferenceObfuscator.canUseInvokedynamic(classMemberRef, classMemberLookup1) ? true : false;
            List list4 = nestedMultiMap.getValues(classMemberRef, this);
            if (list4 != null) {
                Iterator iterator1 = list4.iterator();

                while (iterator1.hasNext()) {
                    ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) iterator1.next();
                    hashMap.put(constantRefInstruction, boolean1);
                }
            }
        }

        try {
            methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classMemberLookup1);
        } catch (StackOverflowError stackOverflowError) {
            throw new CorruptHierarchyException("StackOverflowError trapped in '" + this.getQualifiedMethodName() + "' (3)", stackOverflowError);
        }

        ArrayList arrayList = new ArrayList();
        int bb = this.instructions.size();

        for (int i = 0; i < bb; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (hashMap.containsKey(instruction1)) {
                ResolvedMemberRef resolvedMemberRef = (ResolvedMemberRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                ClassMemberRef classMemberRef1 = (ClassMemberRef) map2.get(resolvedMemberRef);
                if (classMemberRef1 != null && map1.containsKey(classMemberRef1)) {
                    Integer integer = (Integer) map1.get(classMemberRef1);
                    Long long2 = (Long) map3.get(integer);
                    this.obfuscateReference(
                            (ConstantRefInstruction) instruction1,
                            i,
                            resolvedMemberRef,
                            classMemberRef1,
                            long2,
                            arrayList,
                            resolvedMethodRef,
                            resolvedMethodRef1,
                            map4,
                            resolvedMethodRef2,
                            ba,
                            map5,
                            observableHolder,
                            long1,
                            localVariableIndex1,
                            referenceObfuscator,
                            methodFlowAnalyzer,
                            hashMap,
                            constantPool1,
                            list2,
                            list3,
                            classMemberLookup1,
                            classResolver1
                    );
                }
            }
        }

        return arrayList;
    }

    public int getMaxStack() {
        return ((CodeAttributeBody) this.getParent()).getMaxStack();
    }

    public void analyzeParameterUsage(ParameterUsageInfo[] parameterUsageInfos) {
        for (int i = 0; i < parameterUsageInfos.length; i++) {
            ParameterUsageInfo parameterUsageInfo = parameterUsageInfos[i];
            if (parameterUsageInfo != null && parameterUsageInfo.hasSingleUsage() && !parameterUsageInfo.isStoredTo()) {
                int bb = (Integer) parameterUsageInfo.getUsageIndices().get(0);
                Instruction instruction1 = (Instruction) this.instructions.get(bb);
                if (instruction1.getOpcode() == 132) {
                    parameterUsageInfo.markUnpackAtUse();
                } else {
                    int stackDelta = instruction1.getStackDelta();

                    for (int j = bb + 1; j < this.instructions.size(); j++) {
                        Instruction instruction2 = (Instruction) this.instructions.get(j);
                        if (instruction2.isStackManipulation()
                                || instruction2.isJump()
                                || instruction2.getOpcode() == 186
                                || instruction2.isLabel() && (((LabelInstruction) instruction2).hasUsageBits(1) || ((LabelInstruction) instruction2).hasUsageBits(1024))
                                || instruction2.isReturn()) {
                            break;
                        }

                        stackDelta += instruction2.getStackDelta();
                        if (instruction2.consumesStackSlot(0, stackDelta)) {
                            if (instruction2.getOpcode() == 192) {
                                parameterUsageInfo.markUnpackAtUse();
                                parameterUsageInfo.markNoCastRequired();
                                break;
                            }

                            if (instruction2.isFieldStore()) {
                                ConstantRefInstruction constantRefInstruction1 = (ConstantRefInstruction) instruction2;
                                ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantPoolOperand) constantRefInstruction1).getConstantPoolEntry();
                                parameterUsageInfo.markUnpackAtUse();
                                if (resolvedFieldRef.getStackFieldType().equals("Ljava/lang/Object;")) {
                                    parameterUsageInfo.markNoCastRequired();
                                }
                                break;
                            }

                            if (!instruction2.isMethodInvoke()) {
                                break;
                            }

                            ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) instruction2;
                            ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                            if (abstractMethodInfo == null) {
                                break;
                            }

                            if (abstractMethodInfo.isNameChanged()) {
                                if (MethodParameterObfuscator.usesPackHelperMethods((ProgramClass) this.getOwningClass())) {
                                    parameterUsageInfo.markUnpackAtUse();
                                }
                                break;
                            }

                            parameterUsageInfo.markUnpackAtUse();
                            int be = 0;
                            if (!abstractMethodInfo.isStatic()) {
                                be += -1;
                            }

                            String string = abstractMethodInfo.getReturnDescriptor();
                            if (!string.equals("V")) {
                                if (ConstantPoolEntry.isWideType(string)) {
                                    be += 2;
                                } else {
                                    be++;
                                }
                            }

                            if (stackDelta > be) {
                                break;
                            }

                            List list1 = abstractMethodInfo.getParameterTypes();
                            int bf = stackDelta;
                            int bg = 0;
                            bg++;

                            String string1;
                            for (string1 = (String) list1.get(0); bf != be; string1 = (String) list1.get(bg++)) {
                                if (ConstantPoolEntry.isWideType(string1)) {
                                    bf += 2;
                                } else {
                                    bf++;
                                }
                            }

                            if (string1.equals("Ljava/lang/Object;")) {
                                parameterUsageInfo.markNoCastRequired();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public MethodFlowAnalyzer ensureFlowAnalyzer(
            MethodFlowAnalyzer methodFlowAnalyzer, CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, Boolean boolean1
    ) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer1 = methodFlowAnalyzer;
        if (methodFlowAnalyzer1 == null) {
            try {
                methodFlowAnalyzer1 = this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery, boolean1);
            } catch (MethodAnalysisException methodAnalysisException) {
            } catch (StackOverflowError stackOverflowError) {
                throw new CorruptHierarchyException("StackOverflowError trapped in '" + this.getQualifiedMethodName() + "' (6)", stackOverflowError);
            }
        }

        return methodFlowAnalyzer1;
    }

    public void addPredicateInsertions(
            List list1,
            Instruction instruction1,
            List list2,
            MethodFlowAnalyzer methodFlowAnalyzer,
            int ba,
            CodeInsertion codeInsertion,
            int bb,
            SyncIndexedSet syncIndexedSet,
            Random random1
    ) {
        BasicBlock basicBlock = methodFlowAnalyzer.getBlock();
        if (syncIndexedSet.size() > 1 && !HiddenOptionFlags.NO_RANDOM_INSERTION_POINTS) {
            int bd = random1.nextInt(syncIndexedSet.size() - 1) + 1;
            int be = random1.nextInt(bd);
            ArrayList arrayList2 = new ArrayList(list1.size());
            arrayList2.addAll(list1);
            CodeInsertion codeInsertion5 = new CodeInsertion(arrayList2, (Integer) syncIndexedSet.getElementAt(be), 0, 1);
            list2.add(codeInsertion5);
            if (instruction1 != null) {
                CodeInsertion codeInsertion3 = new CodeInsertion(instruction1, (Integer) syncIndexedSet.getElementAt(bd), 0, 0);
                list2.add(codeInsertion3);
            }
        } else {
            int bc = 0;

            while (((Instruction) this.instructions.get(bc)).isLabel()) {
                bc++;
            }

            if (HiddenOptionFlags.INSERT_AT_METHOD_START
                    && syncIndexedSet.size() == 1
                    && (Integer) syncIndexedSet.getElementAt(0) == -1
                    && instruction1 != null
                    && (ba > bc || ba == bc && codeInsertion != null)
                    && bb > bc
                    && bc < basicBlock.getEndIndex()
                    && ((Instruction) this.instructions.get(bc)).pushesValue()
                    && !((Instruction) this.instructions.get(bc)).pushesWideValue()
                    && random1.nextInt(10) == 9) {
                CodeInsertion codeInsertion4 = new CodeInsertion(new ArrayList(list1), -1, 0, 1);
                list2.add(codeInsertion4);
                ArrayList arrayList1 = new ArrayList(2);
                arrayList1.add(SimpleInstruction.forOpcode(95));
                arrayList1.add(instruction1);
                CodeInsertion codeInsertion2 = new CodeInsertion(arrayList1, bc, 0, 1);
                codeInsertion2.setSequence(-1);
                list2.add(codeInsertion2);
            } else {
                ArrayList arrayList = new ArrayList(2);
                arrayList.addAll(list1);
                if (instruction1 != null) {
                    arrayList.add(instruction1);
                }

                CodeInsertion codeInsertion1 = new CodeInsertion(arrayList, -1, 0, 1);
                list2.add(codeInsertion1);
            }
        }
    }

    public MethodBytecode(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent);
        this.codeLength = classFileInputStream.readInt();
        this.instructions = new ArrayList(Math.max(5, (int) (this.codeLength / 2.5)));
        this.localVariableList = new LocalVariableList(this, ((CodeAttributeBody) classFileComponent).getMaxLocals());
        this.readInstructions(
                classFileInputStream,
                this.codeLength,
                listMultimap,
                this.localVariableList,
                listMultimap1,
                listMultimap2,
                listMultimap3,
                listMultimap4,
                listMultimap5,
                listMultimap6
        );
    }

    public void addFieldAccessObfuscation(
            Instruction instruction1,
            int ba,
            ResolvedFieldRef resolvedFieldRef,
            ConstantLong constantLong,
            Instruction instruction2,
            List list1,
            ResolvedMethodRef resolvedMethodRef,
            MethodFlowAnalyzer methodFlowAnalyzer,
            Map map1,
            ReferenceObfuscator referenceObfuscator,
            ConstantPool constantPool1,
            List list2,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ConstantRefInstruction(20, constantLong));
        if (instruction2 != null) {
            arrayList.add(instruction2);
        }

        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef));
        if (instruction1.getOpcode() == 178) {
            arrayList.add(SimpleInstruction.forOpcode(1));
        } else if (instruction1.getOpcode() == 180) {
            arrayList.add(SimpleInstruction.forOpcode(95));
        }

        BooleanFlag booleanFlag = new BooleanFlag(false);
        ResolvedMethodRefConstant resolvedMethodRefConstant = referenceObfuscator.getFieldGetterRef(
                resolvedFieldRef, booleanFlag, constantPool1, list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        if (booleanFlag.getValue() && this.needsCheckcast(ba, resolvedFieldRef, map1, methodFlowAnalyzer)) {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(
                    ConstantPoolEntry.stripClassDescriptor(resolvedFieldRef.getStackFieldType()), list2
            );
            arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
        }

        CodeInsertion codeInsertion = new CodeInsertion(arrayList, ba - 1, 1, 2);
        list1.add(codeInsertion);
    }

    public List buildArrayElementLoad(
            Integer integer,
            String string,
            boolean bl,
            boolean bl1,
            ConstantPool constantPool1,
            List list1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList(6);
        if (bl) {
            arrayList.add(SimpleInstruction.forOpcode(89));
        }

        arrayList.add(Instruction.createIntPush(integer));
        arrayList.add(SimpleInstruction.forOpcode(50));
        if (bl1) {
            if (string.length() == 1) {
                String string1 = ConstantPoolEntry.getStackWrapperClassName(string);
                ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(string1, list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
                ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddUnboxMethodRef(string, list1, classMemberLookup1, classResolver1);
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
            } else {
                String string2;
                if (string.startsWith("L") && string.endsWith(";")) {
                    string2 = ConstantPoolEntry.stripClassDescriptor(string);
                } else {
                    string2 = string;
                }

                ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant(string2, list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
            }
        }

        return arrayList;
    }

    public static String buildErasedDescriptor(String string, String string1) {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = string1.indexOf(41);
        if (!HiddenOptionFlags.KEEP_FIELD_ACCESSOR_TYPES) {
            try {
                String string2 = string1.substring(0, ba);
                int bb = 0;

                while (bb < string2.length()) {
                    char bc = string2.charAt(bb++);
                    if (bc == 'L') {
                        do {
                            bc = string2.charAt(bb++);
                        } while (bc != ';');

                        stringBuilder.append("Ljava/lang/Object;");
                    } else if (bc != '[') {
                        stringBuilder.append(bc);
                    } else {
                        do {
                            bc = string2.charAt(bb++);
                        } while (bc == '[');

                        if (bc != 'L') {
                            stringBuilder.append("Ljava/lang/Object;");
                        } else {
                            do {
                                bc = string2.charAt(bb++);
                            } while (bc != ';');

                            stringBuilder.append("Ljava/lang/Object;");
                        }
                    }
                }
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                throw new ZkmRuntimeException("ERROR:Invalid method parameter String '" + string1 + "'.", indexOutOfBoundsException);
            }
        } else {
            stringBuilder.append(string1.substring(0, ba));
        }

        stringBuilder.append(string);
        stringBuilder.append(string1.substring(ba));
        return stringBuilder.toString();
    }

    public boolean resolveReflectedLibraryMethods(ClasspathClassFile classpathClassFile, Set set1, Set set2, boolean bl, boolean bl1) {
        boolean bl2 = bl1;
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            String string = ((TrackedValue) iterator.next()).getNormalizedName();
            if (string != null && string.length() > 0) {
                Iterator iterator1 = set2.iterator();

                while (iterator1.hasNext()) {
                    TrackedValue trackedValue = (TrackedValue) iterator1.next();
                    Set set3 = this.buildReflectedSignatures(string, bl, trackedValue);
                    if (set3 != null) {
                        AbstractMethodInfo abstractMethodInfo = null;
                        Iterator iterator2 = set3.iterator();

                        while (iterator2.hasNext()) {
                            FieldNameTypeSignature fieldNameTypeSignature = (FieldNameTypeSignature) iterator2.next();
                            AbstractMethodInfo[] abstractMethodInfos = classpathClassFile.findMethodsByNameAndType(fieldNameTypeSignature);
                            if (abstractMethodInfos != null && abstractMethodInfos.length == 1) {
                                if (abstractMethodInfo != null) {
                                    abstractMethodInfo = null;
                                    break;
                                }

                                abstractMethodInfo = abstractMethodInfos[0];
                            }
                        }

                        if (abstractMethodInfo == null) {
                            bl2 = false;
                        }
                    } else {
                        bl2 = false;
                    }
                }
            } else {
                bl2 = false;
            }
        }

        return bl2;
    }

    public void recordMemberReference(ConstantRefInstruction constantRefInstruction, NestedMultiMap nestedMultiMap) {
        ClassFileBase classFileBase = this.getOwningClass();
        if (nestedMultiMap != null && ReferenceObfuscator.supportsMethodHandles(classFileBase)) {
            MemberInfo memberInfo1 = ((ResolvedMemberRef) ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry()).getResolvedMember();
            ClassFileBase classFileBase1 = memberInfo1.getOwningClass();
            ClassMemberRef classMemberRef = new ClassMemberRef(memberInfo1, classFileBase1);
            nestedMultiMap.addValue(classMemberRef, this, constantRefInstruction);
        }
    }

    public void setMaxStack(int ba) throws ZkmProcessingException {
        ((CodeAttributeBody) this.getParent()).setMaxStack(ba);
    }

    public boolean traceStringBuilderChain(int ba, String string, MutableInt mutableInt, Set set1) {
        int bb = ba;
        ArrayList arrayList = new ArrayList();
        arrayList.add(string);
        boolean bl = false;
        boolean bl1 = false;
        int bc = 1;

        do {
            bc++;
            Instruction instruction1 = (Instruction) this.instructions.get(bb);
            ResolvedMethodRef resolvedMethodRef = null;
            AbstractMethodInfo abstractMethodInfo = null;
            if (instruction1.isMethodInvoke()) {
                resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
            }

            if (instruction1.getOpcode() == 182 && ((String) arrayList.get(arrayList.size() - 1)).equals(string)) {
                if (resolvedMethodRef.getMemberName().equals("append")) {
                    if (resolvedMethodRef.getArgumentSlotCount() == 1) {
                        arrayList.add("SINGLE_SLOT");
                    } else {
                        arrayList.add("DOUBLE_SLOT");
                    }
                } else {
                    bl1 = true;
                    bl = false;
                }
            } else if (instruction1.getOpcode() == 183 && arrayList.size() == 1 && ((String) arrayList.get(arrayList.size() - 1)).equals(string)) {
                if (!resolvedMethodRef.isConstructor() || !resolvedMethodRef.getDescriptor().equals("()V")) {
                    bl = false;
                } else if (bb - 2 >= 0
                        && arrayList.size() == 1
                        && ((Instruction) this.instructions.get(bb - 1)).getOpcode() == 89
                        && ((Instruction) this.instructions.get(bb - 2)).getOpcode() == 187) {
                    bc += 2;
                    bl = true;
                } else {
                    bl = false;
                }

                bl1 = true;
            } else if (instruction1.pushesWithoutPopping() && arrayList.size() == 2) {
                arrayList.remove(arrayList.size() - 1);
            } else if (instruction1.getOpcode() == 180 && ((Instruction) this.instructions.get(bb - 1)).pushesWithoutPopping()) {
                arrayList.remove(arrayList.size() - 1);
                bc++;
                bb += -1;
            } else if (instruction1.isMethodInvoke()
                    && resolvedMethodRef.getStackReturnType() != null
                    && abstractMethodInfo != null
                    && set1.contains(abstractMethodInfo)
                    && !resolvedMethodRef.getReferencedClassName().equals("java/lang/StringBuffer")
                    && !resolvedMethodRef.getReferencedClassName().equals("java/lang/StringBuilder")) {
                abstractMethodInfo.getWidenedParameterTypes();
                MutableInt mutableInt1 = new MutableInt();
                if (this.hasPushOnlyArguments(bb - 1, abstractMethodInfo, mutableInt1)) {
                    int value = mutableInt1.getValue();
                    bc += value;
                    bb -= value;
                    String string1 = (String) arrayList.remove(arrayList.size() - 1);
                } else {
                    bl = false;
                    bl1 = true;
                }
            } else {
                bl1 = true;
                bl = false;
            }

            bb += -1;
        } while (!bl1);

        mutableInt.setValue(bc);
        return bl;
    }

    private int findMarkerLabelIndex() {
        int ba = -1;
        int bb = this.instructions.size();

        for (int i = 0; i < bb; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isLabel()) {
                LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                if (labelInstruction.hasUsageBits(8192)) {
                    ba = i;
                    break;
                }

                if (labelInstruction.hasUsageBits(256)) {
                    break;
                }
            }
        }

        return ba;
    }

    public LocalVariableSlot buildParamChangePrologue(
            MethodParamChangeNode methodParamChangeNode,
            ChangedMethodDescriptor changedMethodDescriptor,
            long ba,
            boolean bl,
            Map map1,
            Map map2,
            Map map3,
            List list1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            List list3,
            ConstantPool constantPool1,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            Set set1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        if (methodParamChangeNode != null && methodParamChangeNode.getInitMethod() == this.getMethod()) {
            List list4 = methodParamChangeNode.getInitInstructions();
            arrayList.addAll(list4);
        }

        int bg = this.instructions.size();
        boolean bl1 = false;
        LocalVariableSlot localVariableSlot1 = null;
        MethodFlowAnalyzer methodFlowAnalyzer = null;
        int bb = 0;
        ArrayList arrayList1 = new ArrayList();
        HashSet hashSet = ZkmUtils.createHashSet();

        for (int i = 0; i < bg; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke() || instruction1.getOpcode() == 186 && HiddenOptionFlags.FOLLOW_LAMBDA_METHOD_HANDLES) {
                ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) instruction1;
                AbstractMethodInfo abstractMethodInfo;
                if (instruction1.isMethodInvoke()) {
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                    abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                } else {
                    ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                    abstractMethodInfo = resolvedInvokeDynamic.getLambdaImplMethod();
                }

                if (abstractMethodInfo != null) {
                    ChangedMethodDescriptor changedMethodDescriptor1 = (ChangedMethodDescriptor) map2.get(abstractMethodInfo);
                    if (changedMethodDescriptor1 != null && !changedMethodDescriptor1.hasNoAddedParams()) {
                        bl1 = true;
                        arrayList1.add(integerCache.valueOf(i));
                        hashSet.add((MethodInfo) abstractMethodInfo);
                    }
                }
            }
        }

        if (bl || bl1) {
            ObservableHolder observableHolder = new ObservableHolder();
            bb = this.emitMethodKeyInit(
                    methodParamChangeNode, changedMethodDescriptor, ba, map1, observableHolder, arrayList, set1, list1, list3, constantPool1, methodOverrideAnalyzer
            );
            localVariableSlot1 = (LocalVariableSlot) observableHolder.getValue();

            try {
                methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
            } catch (StackOverflowError stackOverflowError) {
                throw new CorruptHierarchyException("StackOverflowError trapped in '" + this.getQualifiedMethodName() + "' (7)", stackOverflowError);
            }
        }

        if (bl1) {
            HashMap hashMap = ZkmUtils.createHashMap();
            int bh = this.emitCalleeKeyComputation(
                    ba, localVariableSlot1, hashSet, map1, map2, map3, arrayList, list1, hashMap, list3, constantPool1, methodOverrideAnalyzer
            );
            bb = Math.max(bb, bh);
            LabelInstruction labelInstruction1 = new LabelInstruction(8192);
            arrayList.add(labelInstruction1);
            CodeInsertion codeInsertion1 = new CodeInsertion(arrayList, -1, 0, bb);
            list2.add(codeInsertion1);
            ListMultimap listMultimap = new ListMultimap();
            CodeAttributeBody codeAttributeBody = this.getCodeAttributeBody();
            int maxLocals = codeAttributeBody.getMaxLocals();
            MutableInt mutableInt = new MutableInt(maxLocals);
            Iterator iterator = arrayList1.iterator();

            while (iterator.hasNext()) {
                Integer integer = (Integer) iterator.next();
                int be = integer;
                ConstantRefInstruction constantRefInstruction1 = (ConstantRefInstruction) this.instructions.get(be);
                AbstractMethodInfo abstractMethodInfo1;
                String string;
                if (constantRefInstruction1.isMethodInvoke()) {
                    ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) ((ConstantPoolOperand) constantRefInstruction1).getConstantPoolEntry();
                    abstractMethodInfo1 = (AbstractMethodInfo) resolvedMethodRef1.getResolvedMember();
                    string = resolvedMethodRef1.getDescriptor();
                } else {
                    ResolvedInvokeDynamic resolvedInvokeDynamic1 = (ResolvedInvokeDynamic) ((ConstantPoolOperand) constantRefInstruction1).getConstantPoolEntry();
                    abstractMethodInfo1 = resolvedInvokeDynamic1.getLambdaImplMethod();
                    string = resolvedInvokeDynamic1.getDescriptor();
                }

                ChangedMethodDescriptor changedMethodDescriptor2 = (ChangedMethodDescriptor) map2.get(abstractMethodInfo1);
                int[] bf = (int[]) hashMap.get(abstractMethodInfo1);
                this.rewriteCallArguments(methodFlowAnalyzer, be, string, changedMethodDescriptor2, bf, listMultimap, list1, maxLocals, mutableInt);
            }

            if (methodFlowAnalyzer != null) {
                methodFlowAnalyzer.release();
            }

            if (mutableInt.getValue() > maxLocals) {
                codeAttributeBody.setMaxLocals(mutableInt.getValue());
            }

            iterator = listMultimap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                CodeInsertion codeInsertion2 = new CodeInsertion((List) entry.getValue(), (Integer) entry.getKey(), 0, 1);
                list2.add(codeInsertion2);
            }
        } else if (arrayList.size() > 0) {
            LabelInstruction labelInstruction = new LabelInstruction(8192);
            arrayList.add(labelInstruction);
            CodeInsertion codeInsertion = new CodeInsertion(arrayList, -1, 0, bb);
            list2.add(codeInsertion);
        }

        return localVariableSlot1;
    }

    public boolean recomputeOffsets() {
        boolean bl = false;

        boolean bl1;
        do {
            bl1 = false;
            int ba = 0;
            int bb = this.instructions.size();

            for (int i = 0; i < bb; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                instruction1.setOffset(ba);
                if (instruction1 instanceof LabelInstruction) {
                    ((LabelInstruction) instruction1).setInstructionIndex(i);
                }

                ba += instruction1.getLength();
            }

            this.codeLength = ba;
            int be = this.instructions.size();

            for (int i = 0; i < be; i++) {
                List list1 = ((Instruction) this.instructions.get(i)).expandWideJump();
                if (list1 != null) {
                    for (int j = 0; j < list1.size(); j++) {
                        Instruction instruction2 = (Instruction) list1.get(j);
                        if (j == 0) {
                            this.instructions.set(i, instruction2);
                        } else {
                            this.instructions.add(++i, instruction2);
                            be = this.instructions.size();
                        }
                    }

                    bl1 = true;
                    bl = true;
                }
            }
        } while (bl1);

        if (bl) {
            this.setModified(true);
        }

        return bl;
    }

    public static ClassFileBase lookupClass(String string, ClasspathClassLoader classpathClassLoader1) throws ZkmException, IOException {
        ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string);
        if (classFileBase == null) {
            try {
                classFileBase = classpathClassLoader1.findClassFile(string);
            } catch (ClassFileLoadException classFileLoadException) {
            }
        }

        return classFileBase;
    }

    public void addRethrowingHandler(int ba, Integer integer, String string, boolean bl, List list1, List list2, List list3) {
        ResolvedClassConstant resolvedClassConstant = ((ConstantPool) this.getOwningClass().getConstantPool()).getOrCreateClassConstant(string, list3);
        LabelInstruction labelInstruction = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 128);
        ExceptionTableEntry exceptionTableEntry1 = new ExceptionTableEntry(
                (CodeAttributeBody) this.getParent(), resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction2
        );
        list1.add(exceptionTableEntry1);
        int bb = -1;
        Instruction instruction1 = (Instruction) this.instructions.get(ba);

        while (instruction1.isLabel()) {
            instruction1 = (Instruction) this.instructions.get(ba + ++bb + 1);
        }

        CodeInsertion codeInsertion = new CodeInsertion(labelInstruction, ba + bb, 0, 0);
        ArrayList arrayList = new ArrayList();
        if (bl) {
            arrayList.add(labelInstruction1);
            LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
            arrayList.add(new GotoInstruction(labelInstruction3));
            arrayList.add(labelInstruction2);
            arrayList.add(SimpleInstruction.forOpcode(191));
            arrayList.add(labelInstruction3);
        } else {
            arrayList.add(labelInstruction2);
            arrayList.add(labelInstruction1);
            arrayList.add(SimpleInstruction.forOpcode(191));
        }

        CodeInsertion codeInsertion1 = new CodeInsertion(arrayList, integer, 0, 0);
        list2.add(codeInsertion);
        list2.add(codeInsertion1);
    }

    public void applyCodeInsertions(List list1, String string, boolean bl) throws ZkmProcessingException {
        this.applyCodeInsertions(list1, string, bl, true);
    }

    public String formatArgumentValues(CallArgumentValues callArgumentValues, int ba, boolean bl, List list1) {
        Set set1 = callArgumentValues.getArgumentValues(ba);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            TrackedValue trackedValue = (TrackedValue) iterator.next();
            stringBuilder.append(
                    bl
                            ? (trackedValue instanceof TracedArrayValue ? ((TracedArrayValue) trackedValue).formatElements(true) : trackedValue.getStringValue())
                            : trackedValue.getDisplayText()
            );
            if (list1 != null) {
                if (trackedValue.isConstant()) {
                    list1.add(trackedValue.getNormalizedName());
                } else {
                    list1.add(null);
                }
            }

            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append("~");
        return stringBuilder.toString();
    }

    public ConstantPoolEntry getLeadingConstant() {
        Instruction instruction1 = (Instruction) this.instructions.get(0);
        switch (instruction1.getOpcode()) {
            case 18:
                return ((LdcInstruction) instruction1).getConstantPoolEntry();
            case 19:
            case 20:
                return ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
            default:
                return null;
        }
    }

    public void applyParameterPacking(
            List list1,
            Map map1,
            ConstantPool constantPool1,
            List list2,
            LocalVariableIndex localVariableIndex1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        if (this.getMethod().isNameChanged()) {
            this.packParametersIntoArray(arrayList, localVariableIndex1, constantPool1, list2, classMemberLookup1, classResolver1, bl);
        }

        if (list1 != null) {
            if (map1 != null) {
                this.insertArgumentArrayViaHelpers(arrayList, list1, map1, constantPool1, list2);
            } else {
                this.insertArgumentArrayBoxing(arrayList, list1, constantPool1, list2, classMemberLookup1, classResolver1);
            }
        }

        if (!arrayList.isEmpty()) {
            this.applyCodeInsertions(arrayList, "Method Parameter Obfuscation", false, false);
        } else {
            this.recomputeOffsets();
            this.setModified(true);
        }

        this.setMaxLocals(this.localVariableList.getLastSlotIndex() + 1);
    }

    public void collectProgramInvokeDynamicRefs(ResolvedInvokeDynamic resolvedInvokeDynamic, Set set1, Set set2, Set set3, Set set4) {
        resolvedInvokeDynamic.collectReferencedProgramClasses(set4, set3, set1, set2);
    }

    public AbstractMethodInfo getMethod() {
        return (AbstractMethodInfo) ((CodeAttributeBody) this.getParent()).getParent();
    }

    public void addMethodCallObfuscation(
            Instruction instruction1,
            int ba,
            Object object,
            ResolvedMethodRef resolvedMethodRef,
            ConstantLong constantLong,
            Instruction instruction2,
            List list1,
            ResolvedMethodRef resolvedMethodRef1,
            Map map1,
            MethodFlowAnalyzer methodFlowAnalyzer,
            Map map2,
            ReferenceObfuscator referenceObfuscator,
            ConstantPool constantPool1,
            List list2,
            List list3,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ResolvedMethodRef resolvedMethodRef2 = (ResolvedMethodRef) map1.get(object);
        if (resolvedMethodRef2 != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef2));
            arrayList.add(new ConstantRefInstruction(20, constantLong));
            if (instruction2 != null) {
                arrayList.add(instruction2);
            }

            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef1));
            switch (instruction1.getOpcode()) {
                case 182:
                case 183:
                case 185:
                    arrayList.add(SimpleInstruction.forOpcode(91));
                    arrayList.add(SimpleInstruction.forOpcode(87));
                    break;
                case 184:
                    arrayList.add(SimpleInstruction.forOpcode(95));
                    arrayList.add(SimpleInstruction.forOpcode(1));
                    arrayList.add(SimpleInstruction.forOpcode(95));
            }

            ResolvedMethodRefConstant resolvedMethodRefConstant = referenceObfuscator.getMethodInvokeRef(constantPool1, list3, classMemberLookup1, classResolver1);
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/reflect/InvocationTargetException", list3);
            LabelInstruction labelInstruction = new LabelInstruction(true, 128);
            LabelInstruction labelInstruction1 = new LabelInstruction(true, 128);
            LabelInstruction labelInstruction2 = new LabelInstruction(true, 128);
            ExceptionTableEntry exceptionTableEntry1 = new ExceptionTableEntry(
                    (CodeAttributeBody) this.getParent(), resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction2
            );
            list2.add(exceptionTableEntry1);
            arrayList.add(labelInstruction);
            arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
            if (this.needsCheckcast(ba, resolvedMethodRef, map2, methodFlowAnalyzer)) {
                List list4 = referenceObfuscator.buildReturnUnboxCode(resolvedMethodRef, constantPool1, list3, classMemberLookup1, classResolver1);
                if (list4 != null) {
                    arrayList.addAll(list4);
                }
            }

            this.appendExceptionUnwrap(
                    arrayList, labelInstruction1, labelInstruction2, referenceObfuscator, list3, constantPool1, classMemberLookup1, classResolver1
            );
            int argumentSlotCount = resolvedMethodRef2.getArgumentSlotCount();
            CodeInsertion codeInsertion = null;
            switch (instruction1.getOpcode()) {
                case 182:
                case 183:
                case 185:
                    int bg = ba - 1;
                    int bb = argumentSlotCount >= 4 ? 0 : 4 - argumentSlotCount;
                    int bc = bg;
                    ArrayList arrayList1 = arrayList;
                    codeInsertion = new CodeInsertion(arrayList1, bc, 1, bb);
                    break;
                case 184:
                    int bh = ba - 1;
                    int bd = argumentSlotCount >= 3 ? 0 : 3 - argumentSlotCount;
                    int be = bh;
                    ArrayList arrayList2 = arrayList;
                    codeInsertion = new CodeInsertion(arrayList2, be, 1, bd);
            }

            list1.add(codeInsertion);
        }
    }

    public void collectReferenceObfuscationSites(
            TwoKeySetMultiMap twoKeySetMultiMap,
            ReferenceObfuscator referenceObfuscator,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
        ClassResolver classResolver1 = commonSuperTypeResolver1.getClassResolver();
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                MemberInfo memberInfo1 = resolvedMethodRef.getResolvedMember();
                if (memberInfo1 != null
                        && !((AbstractMethodInfo) memberInfo1).isConstructor()
                        && !((AbstractMethodInfo) memberInfo1).isStaticInitializer()
                        && !methodFlowAnalyzer.hasUninitializedThisAt(i)
                        && (instruction1.getOpcode() != 183 || memberInfo1.isStrictlyPrivate())
                        && (!memberInfo1.getSourceName().equals("identityHashCode") || !memberInfo1.getClassName().equals("java/lang/System"))
                        && (!memberInfo1.isProtected() && !memberInfo1.isNative() || !memberInfo1.getClassName().equals("java/lang/Object"))
                        && (
                        !memberInfo1.isProtected()
                                || !memberInfo1.isStatic()
                                || this.getPackagePath().equals(memberInfo1.getPackagePath())
                                || memberInfo1.getOwningClass().supportsJava9()
                )
                        && (
                        memberInfo1.getOwningClass().isPublic()
                                || memberInfo1.getOwningClass().getPackagePath().equals(this.getPackagePath())
                                || memberInfo1.getOwningClass().supportsJava6()
                )
                        && !MethodSignature.isSignaturePolymorphic(memberInfo1.getClassName(), memberInfo1.getSourceName(), classHierarchyQuery)
                        && (
                        !memberInfo1.isMethod()
                                || !memberInfo1.getOwningClass().isInterface()
                                || !memberInfo1.isAbstract()
                                || !classHierarchyQuery.isObjectMethodSignature(((AbstractMethodInfo) memberInfo1).getSignature())
                )) {
                    ClassMemberRef classMemberRef = this.createClassMemberRef(memberInfo1, resolvedMethodRef, referenceObfuscator, classResolver1);
                    twoKeySetMultiMap.addValue(classMemberRef, this, (ConstantRefInstruction) instruction1);
                }
            } else if (instruction1.isFieldAccess()) {
                ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                MemberInfo memberInfo2 = resolvedFieldRef.getResolvedMember();
                if (memberInfo2 != null
                        && (!instruction1.isFieldStore() || !memberInfo2.isFinal())
                        && (
                        !memberInfo2.isProtected()
                                || !memberInfo2.isStatic()
                                || this.getPackagePath().equals(memberInfo2.getPackagePath())
                                || memberInfo2.getOwningClass().supportsJava9()
                )) {
                    twoKeySetMultiMap.addValue(
                            this.createClassMemberRef(memberInfo2, resolvedFieldRef, referenceObfuscator, classResolver1), this, (ConstantRefInstruction) instruction1
                    );
                }
            }
        }
    }

    public void collectFieldRef(ResolvedFieldRef resolvedFieldRef, Set set1, Set set2) {
        ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(resolvedFieldRef.getReferencedClassName());
        if (classFileBase != null) {
            set2.add(classFileBase);
        }

        AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
        if (abstractFieldInfo != null && (abstractFieldInfo.isProgramMember() || ClassHierarchyNode.isUnknownClass(abstractFieldInfo.getClassName()))) {
            set1.add(abstractFieldInfo);
            ClassFileBase classFileBase1 = abstractFieldInfo.getOwningClass();
            if ((classFileBase == null || classFileBase1 != classFileBase) && !ClassHierarchyNode.isUnknownClass(classFileBase1.getClassName())) {
                set2.add(classFileBase1);
            }
        }
    }

    public void applyIntegerEncryption(
            List list1,
            boolean bl,
            int[][] ba,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        InstructionPositionComparator instructionPositionComparator = new InstructionPositionComparator(this);
        Collections.sort(list1, instructionPositionComparator);
        int bb = list1.size();
        int bc = -1;
        if (bb != 0) {
            MethodFlowAnalyzer methodFlowAnalyzer = null;
            Iterator iterator = list1.iterator();
            ObjectPair objectPair = (ObjectPair) iterator.next();
            ConstantPoolOperand constantPoolOperand = (ConstantPoolOperand) ((RankedValue) objectPair.getFirst()).getValue();
            int bd = bb;
            this.setModified(true);
            byte be = 0;
            boolean bl1 = false;
            boolean bl2 = false;
            ArrayList arrayList = new ArrayList();
            int bf = this.instructions.size();

            for (int i = 0; i < bf; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if (instruction1.isLabel()) {
                    LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(1024)) {
                        bl1 = false;
                    }
                }

                if (instruction1 == constantPoolOperand) {
                    bd += -1;
                    EncryptedValueLocation encryptedValueLocation = (EncryptedValueLocation) objectPair.getSecond();
                    be = 0;
                    switch (LookupStrategySwitchMap.INT_STORAGE_KIND_SWITCH[encryptedValueLocation.getStorageKind().ordinal()]) {
                        case 1:
                            ArrayList arrayList4 = new ArrayList();
                            if (encryptedValueLocation.hasIndex()) {
                                be = 1;
                                arrayList4.add(Instruction.createObjectLoad(encryptedValueLocation.getLocalVariableIndex(), this.localVariableList, 11));
                                this.appendIntConstant(encryptedValueLocation.getIndex(), arrayList4, constantPool1, list2, bb);
                                arrayList4.add(SimpleInstruction.forOpcode(47));
                                arrayList4.add(SimpleInstruction.forOpcode(136));
                            } else {
                                be = 2;
                                arrayList4.add(Instruction.createLongLoad(encryptedValueLocation.getLocalVariableIndex(), this.localVariableList, 11));
                                arrayList4.add(SimpleInstruction.forOpcode(136));
                            }

                            arrayList.add(new CodeInsertion(arrayList4, i - 1, 1, be));
                            break;
                        case 2:
                            ArrayList arrayList3 = new ArrayList();
                            ResolvedFieldRef resolvedFieldRef = encryptedValueLocation.getArrayField();
                            if (encryptedValueLocation.hasIndex()) {
                                be = 1;
                                if (bl1) {
                                    arrayList3.add(Instruction.createObjectLoad(bc, this.localVariableList, 11));
                                } else {
                                    arrayList3.add(new ConstantRefInstruction(178, resolvedFieldRef));
                                    if (bd > 0 && (ba.length == 0 || !this.isInAnyRange(i, ba))) {
                                        if (!bl2) {
                                            bl2 = true;
                                            bc = intCounter.getValue();
                                            intCounter.incrementAndGet();
                                        }

                                        arrayList3.add(Instruction.createObjectStore(bc, this.localVariableList, 11));
                                        arrayList3.add(Instruction.createObjectLoad(bc, this.localVariableList, 11));
                                        bl1 = true;
                                    }
                                }

                                Instruction.appendIntConstant(encryptedValueLocation.getIndex(), arrayList3, constantPool1, list2);
                                arrayList3.add(SimpleInstruction.forOpcode(47));
                                arrayList3.add(SimpleInstruction.forOpcode(136));
                            } else {
                                arrayList3.add(new ConstantRefInstruction(178, resolvedFieldRef));
                                arrayList3.add(SimpleInstruction.forOpcode(136));
                            }

                            arrayList.add(new CodeInsertion(arrayList3, i - 1, 1, be));
                            break;
                        case 3:
                            ResolvedMethodRef resolvedMethodRef1;
                            ArrayList arrayList6;
                            ArrayList arrayList8;
                            label80:
                            {
                                methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                                resolvedMethodRef1 = encryptedValueLocation.getLookupMethod();
                                arrayList6 = new ArrayList();
                                arrayList8 = new ArrayList();
                                int index = encryptedValueLocation.getIndex();
                                long decryptionKey = encryptedValueLocation.getDecryptionKey();
                                int by;
                                long bz;
                                long ca;
                                if (long1 != null) {
                                    if (localVariableIndex1 != null) {
                                        int bu = index ^ (int) (decryptionKey & 32767L);
                                        this.appendIntConstant(bu, arrayList6, constantPool1, list2, bb);
                                        long bx = decryptionKey ^ long1;
                                        Instruction.appendLongConstant(bx, arrayList8, constantPool1, list2);
                                        arrayList8.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 11));
                                        arrayList8.add(SimpleInstruction.forOpcode(131));
                                        break label80;
                                    }

                                    by = index;
                                    bz = decryptionKey;
                                    ca = 32767L;
                                } else {
                                    by = index;
                                    bz = decryptionKey;
                                    ca = 32767L;
                                }

                                int bv = by ^ (int) (bz & ca);
                                this.appendIntConstant(bv, arrayList6, constantPool1, list2, bb);
                                Instruction.appendLongConstant(decryptionKey, arrayList8, constantPool1, list2);
                            }

                            ConstantRefInstruction constantRefInstruction1 = new ConstantRefInstruction(184, resolvedMethodRef1);
                            this.addSplitCodeInsertions(arrayList6, arrayList8, constantRefInstruction1, arrayList, methodFlowAnalyzer, i);
                            break;
                        case 4:
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ResolvedMethodRef resolvedMethodRef = encryptedValueLocation.getLookupMethod();
                            ArrayList arrayList5 = new ArrayList();
                            ArrayList arrayList7 = new ArrayList();
                            int bn = encryptedValueLocation.getIndex();
                            long bp = encryptedValueLocation.getDecryptionKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int bt = bn ^ (int) (bp & 32767L);
                                this.appendIntConstant(bt, arrayList5, constantPool1, list2, bb);
                                long bw = bp ^ long1;
                                Instruction.appendLongConstant(bw, arrayList7, constantPool1, list2);
                                arrayList7.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 11));
                                arrayList7.add(SimpleInstruction.forOpcode(131));
                            } else {
                                int bs = bn ^ (int) bp;
                                Instruction.appendIntConstant(bs, arrayList5, constantPool1, list2);
                                Instruction.appendLongConstant(bp, arrayList7, constantPool1, list2);
                            }

                            ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(184, resolvedMethodRef);
                            this.addSplitCodeInsertions(arrayList5, arrayList7, constantRefInstruction, arrayList, methodFlowAnalyzer, i);
                            break;
                        case 5:
                            ResolvedInvokeDynamic resolvedInvokeDynamic = encryptedValueLocation.getIndyEntry();
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ArrayList arrayList1 = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int bh = encryptedValueLocation.getIndex();
                            long bi = encryptedValueLocation.getDecryptionKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int br = bh ^ (int) (bi & 32767L);
                                this.appendIntConstant(br, arrayList1, constantPool1, list2, bb);
                                long bk = bi ^ long1;
                                Instruction.appendLongConstant(bk, arrayList2, constantPool1, list2);
                                arrayList2.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 11));
                                arrayList2.add(SimpleInstruction.forOpcode(131));
                            } else {
                                int bj = bh ^ (int) bi;
                                this.appendIntConstant(bj, arrayList1, constantPool1, list2, bb);
                                Instruction.appendLongConstant(bi, arrayList2, constantPool1, list2);
                            }

                            InvokeDynamicInstruction invokeDynamicInstruction = new InvokeDynamicInstruction(resolvedInvokeDynamic);
                            this.addSplitCodeInsertions(arrayList1, arrayList2, invokeDynamicInstruction, arrayList, methodFlowAnalyzer, i);
                    }

                    if (!iterator.hasNext()) {
                        break;
                    }

                    objectPair = (ObjectPair) iterator.next();
                    constantPoolOperand = (ConstantPoolOperand) ((RankedValue) objectPair.getFirst()).getValue();
                }
            }

            this.applyCodeInsertions(arrayList, "Integer Constant Encryption", bl);
            if (!bl) {
                CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
                int maxStack = codeAttributeBody.getMaxStack();
                codeAttributeBody.setMaxStack(maxStack + be);
            }
        }
    }

    public boolean areRelatedTypes(
            String string, String string1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        try {
            return string.equals(string1) || classHierarchyQuery.isSubclass(string, string1) || classHierarchyQuery.isSubclass(string1, string);
        } catch (ClassFileLoadException classFileLoadException) {
            if (ignoreMissingReferencesSpec1 != null
                    && ignoreMissingReferencesSpec1.isMissingClassIgnored(classFileLoadException.getClassName().replace('.', '/'), new ObservableHolder())) {
                return true;
            } else {
                throw classFileLoadException;
            }
        }
    }

    private void collectProgramMethodRef(ResolvedMethodRef resolvedMethodRef, Set set1, Set set2) {
        String string = resolvedMethodRef.getReferencedClassName();
        ProgramClass programClass1 = null;
        if (!string.startsWith("[")) {
            programClass1 = ClassHierarchyNode.findProgramClass(string);
            if (programClass1 != null) {
                set2.add(this.selectMatchingProgramClass(programClass1));
            }
        }

        AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
        if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember()) {
            set1.add((MethodInfo) abstractMethodInfo);
            ProgramClass programClass2 = (ProgramClass) abstractMethodInfo.getOwningClass();
            if (programClass1 == null || programClass2 != programClass1) {
                set2.add(this.selectMatchingProgramClass(programClass2));
            }
        }
    }

    public String getMethodName() {
        CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
        return codeAttributeBody != null ? codeAttributeBody.getMethodDeclaration() : "<NO_PARENT_YET>";
    }

    public void insertArgumentArrayViaHelpers(List list1, List list2, Map map1, ConstantPool constantPool1, List list3) {
        int ba = this.instructions.size();
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Object", list3);

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) ((ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry())
                        .getResolvedMember();
                if (abstractMethodInfo != null && list2.contains(abstractMethodInfo)) {
                    if (instruction1.getOpcode() == 185) {
                        ((InvokeInterfaceInstruction) instruction1).setArgSlotCount(2);
                    }

                    List list4 = abstractMethodInfo.getParameterTypes();
                    int bc = list4.size();
                    ArrayList arrayList = new ArrayList(bc + 2);
                    arrayList.add(Instruction.createIntConstantPush(bc, constantPool1, list3));
                    arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant));

                    for (int j = bc - 1; j >= 0; j += -1) {
                        ResolvedMethodRef resolvedMethodRef = null;
                        String string = (String) list4.get(j);
                        if (string.length() == 1) {
                            switch (string.charAt(0)) {
                                case 'B':
                                case 'C':
                                case 'I':
                                case 'S':
                                    if (j > 1) {
                                        String string1 = (String) list4.get(j - 2);
                                        String string2 = (String) list4.get(j - 1);
                                        if ((string1.equals("B") || string1.equals("S") || string1.equals("C") || string1.equals("I"))
                                                && (string2.equals("B") || string2.equals("S") || string2.equals("C") || string2.equals("I"))) {
                                            arrayList.add(Instruction.createIntConstantPush(j - 2, constantPool1, list3));
                                            resolvedMethodRef = (ResolvedMethodRef) map1.get("III");
                                            j += -2;
                                        }
                                    }

                                    if (resolvedMethodRef == null && j > 0) {
                                        String string3 = (String) list4.get(j - 1);
                                        if (string3.equals("B") || string3.equals("S") || string3.equals("C") || string3.equals("I")) {
                                            arrayList.add(Instruction.createIntConstantPush(j - 1, constantPool1, list3));
                                            resolvedMethodRef = (ResolvedMethodRef) map1.get("II");
                                            j += -1;
                                        }
                                    }

                                    if (resolvedMethodRef == null) {
                                        arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                        resolvedMethodRef = (ResolvedMethodRef) map1.get("I");
                                    }
                                    break;
                                default:
                                    arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                    resolvedMethodRef = (ResolvedMethodRef) map1.get(string);
                            }
                        } else if (j > 1 && ((String) list4.get(j - 1)).length() > 1 && ((String) list4.get(j - 2)).length() > 1) {
                            arrayList.add(Instruction.createIntConstantPush(j - 2, constantPool1, list3));
                            resolvedMethodRef = (ResolvedMethodRef) map1.get("OOO");
                            j += -2;
                        } else if (j > 0 && ((String) list4.get(j - 1)).length() > 1) {
                            arrayList.add(Instruction.createIntConstantPush(j - 1, constantPool1, list3));
                            resolvedMethodRef = (ResolvedMethodRef) map1.get("OO");
                            j += -1;
                        } else {
                            arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                            resolvedMethodRef = (ResolvedMethodRef) map1.get("O");
                        }

                        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef));
                    }

                    list1.add(new CodeInsertion(arrayList, i - 1, 0, 2));
                }
            }
        }
    }

    public void removeDeadInstructions(int[] ba, boolean bl, ScriptEnvironment scriptEnvironment1) {
        int bb = this.instructions.size();
        int bc = 0;
        int bd = 0;

        for (Iterator iterator = this.instructions.iterator(); iterator.hasNext() && bd < ba.length; bc++) {
            Instruction instruction1 = (Instruction) iterator.next();
            if (bc == ba[bd]) {
                if (instruction1.isLabel()) {
                    LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.isUnused() || labelInstruction.isOnlyJumpTarget()) {
                        iterator.remove();
                    } else if (labelInstruction.hasUsageBits(1)) {
                        labelInstruction.clearUsageFlag(InstructionUsageClearFlag.CLEAR_USAGE_JUMP_DESTINATION);
                    }
                } else {
                    iterator.remove();
                }

                bd++;
            }
        }

        if (bl) {
            scriptEnvironment1.logMessage(
                    "Removed "
                            + ba.length
                            + " dead instructions from the method '"
                            + this.getMethod().toDisplayString()
                            + "' in class '"
                            + this.getDisplayLocationName()
                            + "'"
            );
        }

        this.recomputeOffsets();
        this.setModified(true);
        if (bb != this.instructions.size()) {
            this.setModified(true);
        }
    }

    public int emitCalleeKeyComputation(
            long ba,
            LocalVariableSlot localVariableSlot1,
            Set set1,
            Map map1,
            Map map2,
            Map map3,
            List list1,
            List list2,
            Map map4,
            List list3,
            ConstantPool constantPool1,
            MethodOverrideAnalyzer methodOverrideAnalyzer
    ) throws ZkmProcessingException {
        CodeAttributeBody codeAttributeBody = this.getCodeAttributeBody();
        int maxLocals = codeAttributeBody.getMaxLocals();
        list1.add(Instruction.createLongLoad(localVariableSlot1));
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            ChangedMethodDescriptor changedMethodDescriptor = (ChangedMethodDescriptor) map2.get(methodInfo1);
            if (!changedMethodDescriptor.hasNoAddedParams()) {
                long bc = (Long) map3.get(methodInfo1);
                MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) map1.get(methodInfo1.getOwnerProgramClass());
                long bd;
                if (usesKeyField(methodInfo1, methodParamChangeNode, methodOverrideAnalyzer)) {
                    bd = ba ^ bc ^ methodParamChangeNode.getEffectiveKey();
                } else {
                    bd = ba ^ bc;
                }

                list1.add(SimpleInstruction.forOpcode(92));
                list1.add(Instruction.createLongConstantLoad(bd, constantPool1, list3));
                list1.add(SimpleInstruction.forOpcode(131));
                if (changedMethodDescriptor.hasMultipleParams()) {
                    int[] bi = new int[changedMethodDescriptor.getAddedParamCount()];

                    for (int i = 0; i < bi.length; i++) {
                        list1.add(SimpleInstruction.forOpcode(92));
                        int bg = changedMethodDescriptor.getKeyBitOffset(i);
                        if (bg > 0) {
                            list1.add(Instruction.createIntPush(bg));
                            list1.add(SimpleInstruction.forOpcode(121));
                        }

                        int bh = changedMethodDescriptor.getShiftCount(i);
                        list1.add(Instruction.createIntPush(bh));
                        list1.add(SimpleInstruction.forOpcode(125));
                        if (changedMethodDescriptor.isLongParam(i)) {
                            list1.add(Instruction.createLongStore(maxLocals, this.localVariableList, 5));
                            bi[i] = maxLocals;
                            list2.add(integerCache.valueOf(maxLocals));
                            list2.add(integerCache.valueOf(maxLocals + 1));
                            maxLocals += 2;
                        } else {
                            list1.add(SimpleInstruction.forOpcode(136));
                            list1.add(Instruction.createIntStore(maxLocals, this.localVariableList, 5));
                            bi[i] = maxLocals;
                            list2.add(integerCache.valueOf(maxLocals));
                            maxLocals++;
                        }
                    }

                    list1.add(SimpleInstruction.forOpcode(88));
                    map4.put(methodInfo1, bi);
                } else {
                    list1.add(Instruction.createLongStore(maxLocals, this.localVariableList, 5));
                    int[] be = new int[]{maxLocals};
                    list2.add(integerCache.valueOf(maxLocals));
                    list2.add(integerCache.valueOf(maxLocals + 1));
                    maxLocals += 2;
                    map4.put(methodInfo1, be);
                }
            }
        }

        list1.add(SimpleInstruction.forOpcode(88));
        codeAttributeBody.setMaxLocals(maxLocals);
        return 4;
    }

    public boolean isRemovableCall(int ba, ConstantRefInstruction constantRefInstruction, Set set1, MutableInt mutableInt) {
        ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
        AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
        return abstractMethodInfo != null
                && set1.contains(abstractMethodInfo)
                && resolvedMethodRef.getStackReturnType() != null
                && !resolvedMethodRef.getReferencedClassName().equals("java/lang/StringBuffer")
                && !resolvedMethodRef.getReferencedClassName().equals("java/lang/StringBuilder")
                && this.hasPushOnlyArguments(ba - 1, abstractMethodInfo, mutableInt);
    }

    public void addInvokeDynamicObfuscation(
            int ba,
            ConstantLong constantLong,
            Instruction instruction1,
            Long long1,
            String string,
            int bb,
            ObservableHolder observableHolder,
            ResolvedMethodRef resolvedMethodRef,
            Map map1,
            List list1,
            List list2,
            ConstantPool constantPool1
    ) {
        int bc = bb;
        BootstrapMethodsAttribute bootstrapMethodsAttribute1;
        if (observableHolder.isValueNull()) {
            bootstrapMethodsAttribute1 = ((ProgramClass) this.getOwningClass()).getOrCreateBootstrapMethodsAttribute(list2);
        } else {
            bootstrapMethodsAttribute1 = (BootstrapMethodsAttribute) observableHolder.getValue();
        }

        int bd = instruction1 == null ? 2 : 3;
        ArrayList arrayList = new ArrayList(bd);
        arrayList.add(new ConstantRefInstruction(20, constantLong));
        if (instruction1 != null) {
            arrayList.add(instruction1);
            if (HiddenOptionFlags.XOR_INDY_OPCODE_KEY) {
                bc ^= long1.intValue() & 7;
            }
        }

        ResolvedInvokeDynamic resolvedInvokeDynamic = this.getOrCreateInvokeDynamic(
                string, bc, resolvedMethodRef, bootstrapMethodsAttribute1, map1, list2, constantPool1
        );
        arrayList.add(new InvokeDynamicInstruction(resolvedInvokeDynamic));
        CodeInsertion codeInsertion = new CodeInsertion(arrayList, ba - 1, 1, 2);
        list1.add(codeInsertion);
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry constantPoolEntry6 = null;
        ClassFileComponent.getFlowGuardNodes();
        int bd = this.instructions.size();
        int be = 0;

        while (be < bd) {
            Instruction instruction1 = (Instruction) this.instructions.get(be);
            if (bb >= 0) {
                label59:
                {
                    UsedConstantsCollector usedConstantsCollector1;
                    ConstantPoolEntry constantPoolEntry7;
                    Instruction instruction2;
                    label72:
                    {
                        com.zelix.klassmaster.classfile.insn.Instruction constantRefInstruction;
                        label56:
                        {
                            label73:
                            {
                                label52:
                                {
                                    label51:
                                    {
                                        constantRefInstruction = instruction1;
                                        if (bc > 0) {
                                            switch (instruction1.getOpcode()) {
                                                case 18:
                                                    constantRefInstruction = (LdcInstruction) instruction1;
                                                    if (bc > 0) {
                                                        ConstantPoolEntry constantPoolEntry5 = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                                                        usedConstantsCollector1 = usedConstantsCollector;
                                                        constantPoolEntry7 = constantPoolEntry5;
                                                        instruction2 = instruction1;
                                                        long bm = 129613246637798L;
                                                        break label72;
                                                    }
                                                    break label73;
                                                case 19:
                                                    constantRefInstruction = instruction1;
                                                    break label73;
                                                case 20:
                                                    constantRefInstruction = (ConstantRefInstruction) instruction1;
                                                    boolean bl1 = false;
                                                    short bg = 16962;
                                                    int bj = 789836191;
                                                    break label56;
                                                case 178:
                                                case 179:
                                                case 180:
                                                case 181:
                                                case 182:
                                                case 183:
                                                case 184:
                                                case 185:
                                                    constantRefInstruction = (ConstantRefInstruction) instruction1;
                                                    boolean bl = false;
                                                    short bf = 16962;
                                                    int bi = 789836191;
                                                    break label51;
                                                case 186:
                                                    constantPoolEntry6 = ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                                    break label52;
                                                case 187:
                                                case 189:
                                                case 192:
                                                case 193:
                                                case 197:
                                                    constantRefInstruction = instruction1;
                                                    break;
                                                default:
                                                    break label59;
                                            }
                                        }

                                        constantRefInstruction = constantRefInstruction;
                                        boolean bl3 = false;
                                        short bn = 16962;
                                        int bo = 789836191;
                                        if (bb > 0) {
                                            ConstantPoolEntry constantPoolEntry = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                                            usedConstantsCollector.markUsed(constantPoolEntry, instruction1, this);
                                            break label59;
                                        }
                                    }

                                    constantPoolEntry6 = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                                    if (ba >= 0) {
                                        ConstantPoolEntry constantPoolEntry2 = constantPoolEntry6;
                                        usedConstantsCollector.markUsed(constantPoolEntry2, instruction1, this);
                                        break label59;
                                    }
                                }

                                ConstantPoolEntry constantPoolEntry1 = constantPoolEntry6;
                                usedConstantsCollector1 = usedConstantsCollector;
                                constantPoolEntry7 = constantPoolEntry1;
                                instruction2 = instruction1;
                                long bp = 129613246637798L;
                                if (bb >= 0) {
                                    usedConstantsCollector.markUsed(constantPoolEntry1, instruction1, this);
                                    break label59;
                                }
                                break label72;
                            }

                            constantRefInstruction = constantRefInstruction;
                            boolean bl2 = false;
                            short bh = 16962;
                            int bk = 789836191;
                            if (bb >= 0) {
                                ConstantPoolEntry constantPoolEntry4 = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                                usedConstantsCollector.markUsed(constantPoolEntry4, instruction1, this);
                                break label59;
                            }
                        }

                        ConstantPoolEntry constantPoolEntry3 = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                        usedConstantsCollector.markUsed(constantPoolEntry3, instruction1, this);
                        break label59;
                    }

                    usedConstantsCollector1.markUsed(constantPoolEntry7, instruction2, this);
                }

                be++;
            }
        }
    }

    public void obfuscateReference(
            ConstantRefInstruction constantRefInstruction,
            int ba,
            ResolvedMemberRef resolvedMemberRef,
            ClassMemberRef classMemberRef,
            Long long1,
            List list1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedMethodRef resolvedMethodRef1,
            Map map1,
            ResolvedMethodRef resolvedMethodRef2,
            int[] bb,
            Map map2,
            ObservableHolder observableHolder,
            Long long2,
            LocalVariableIndex localVariableIndex1,
            ReferenceObfuscator referenceObfuscator,
            MethodFlowAnalyzer methodFlowAnalyzer,
            Map map3,
            ConstantPool constantPool1,
            List list2,
            List list3,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        Instruction instruction1 = null;
        long bc;
        if (long2 != null && localVariableIndex1 != null) {
            bc = long1 ^ (long2 << 48 | long2);
            instruction1 = Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 4);
        } else {
            bc = long1;
        }

        ConstantLong constantLong = constantPool1.getOrAddLongConstant(bc, list3);
        if ((Boolean) map3.get(constantRefInstruction)) {
            MemberInfo memberInfo1 = classMemberRef.getMember();
            String string = classMemberRef.getDescriptor();
            String string1 = classMemberRef.getOwnerClass().getClassName();
            StringBuilder stringBuilder = new StringBuilder();
            String string2;
            if (memberInfo1.isProtected() && !memberInfo1.getPackagePath().equals(this.getPackagePath())) {
                string2 = this.getClassName();
            } else {
                string2 = string1;
            }

            String string3 = instruction1 == null ? "J" : "JJ";
            int bd;
            switch (constantRefInstruction.getOpcode()) {
                case 178:
                    stringBuilder.append('(');
                    stringBuilder.append(string3);
                    stringBuilder.append(')');
                    stringBuilder.append(string);
                    bd = bb[2];
                    break;
                case 179:
                    stringBuilder.append('(');
                    stringBuilder.append(string);
                    stringBuilder.append(string3);
                    stringBuilder.append(")V");
                    bd = bb[3];
                    break;
                case 180:
                    stringBuilder.append("(L");
                    stringBuilder.append(HiddenOptionFlags.KEEP_FIELD_ACCESSOR_TYPES ? string2 : "java/lang/Object");
                    stringBuilder.append(';');
                    stringBuilder.append(string3);
                    stringBuilder.append(')');
                    stringBuilder.append(string);
                    bd = bb[0];
                    break;
                case 181:
                    stringBuilder.append("(L");
                    stringBuilder.append(HiddenOptionFlags.KEEP_FIELD_ACCESSOR_TYPES ? string2 : "java/lang/Object");
                    stringBuilder.append(";");
                    stringBuilder.append(string);
                    stringBuilder.append(string3);
                    stringBuilder.append(")V");
                    bd = bb[1];
                    break;
                case 182:
                case 185:
                    stringBuilder.append(buildErasedDescriptor(string3, this.prependParameterType(string2, string)));
                    bd = bb[4];
                    break;
                case 183:
                    stringBuilder.append(buildErasedDescriptor(string3, this.prependParameterType(string1, string)));
                    bd = bb[6];
                    break;
                case 184:
                    stringBuilder.append(buildErasedDescriptor(string3, string));
                    bd = bb[5];
                    break;
                default:
                    bd = bb[4];
            }

            this.addInvokeDynamicObfuscation(
                    ba, constantLong, instruction1, long2, stringBuilder.toString(), bd, observableHolder, resolvedMethodRef2, map2, list1, list3, constantPool1
            );
        } else {
            switch (constantRefInstruction.getOpcode()) {
                case 178:
                    this.addFieldAccessObfuscation(
                            constantRefInstruction,
                            ba,
                            (ResolvedFieldRef) resolvedMemberRef,
                            constantLong,
                            instruction1,
                            list1,
                            resolvedMethodRef,
                            methodFlowAnalyzer,
                            map3,
                            referenceObfuscator,
                            constantPool1,
                            list3,
                            classMemberLookup1,
                            classResolver1
                    );
                    break;
                case 179:
                    ArrayList arrayList3 = new ArrayList();
                    ResolvedFieldRef resolvedFieldRef1 = (ResolvedFieldRef) resolvedMemberRef;
                    arrayList3.add(new ConstantRefInstruction(20, constantLong));
                    if (instruction1 != null) {
                        arrayList3.add(instruction1);
                    }

                    arrayList3.add(new ConstantRefInstruction(184, resolvedMethodRef));
                    arrayList3.add(SimpleInstruction.forOpcode(1));
                    boolean bl1 = referenceObfuscator.isWideField(resolvedFieldRef1);
                    if (bl1) {
                        arrayList3.add(SimpleInstruction.forOpcode(94));
                    } else {
                        arrayList3.add(SimpleInstruction.forOpcode(93));
                    }

                    arrayList3.add(SimpleInstruction.forOpcode(88));
                    ResolvedMethodRefConstant resolvedMethodRefConstant1 = referenceObfuscator.getFieldSetterRef(
                            resolvedFieldRef1, constantPool1, list3, classMemberLookup1, classResolver1
                    );
                    arrayList3.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
                    int bi = ba - 1;
                    int be = bl1 ? 6 : 5;
                    int bf = bi;
                    ArrayList arrayList = arrayList3;
                    CodeInsertion codeInsertion1 = new CodeInsertion(arrayList, bf, 1, be);
                    list1.add(codeInsertion1);
                    break;
                case 180:
                    this.addFieldAccessObfuscation(
                            constantRefInstruction,
                            ba,
                            (ResolvedFieldRef) resolvedMemberRef,
                            constantLong,
                            instruction1,
                            list1,
                            resolvedMethodRef,
                            methodFlowAnalyzer,
                            map3,
                            referenceObfuscator,
                            constantPool1,
                            list3,
                            classMemberLookup1,
                            classResolver1
                    );
                    break;
                case 181:
                    ArrayList arrayList2 = new ArrayList();
                    ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) resolvedMemberRef;
                    boolean bl = referenceObfuscator.isWideField(resolvedFieldRef);
                    if (bl) {
                        arrayList2.add(SimpleInstruction.forOpcode(93));
                        arrayList2.add(SimpleInstruction.forOpcode(88));
                        arrayList2.add(new ConstantRefInstruction(20, constantLong));
                        if (instruction1 != null) {
                            arrayList2.add(instruction1);
                        }

                        arrayList2.add(new ConstantRefInstruction(184, resolvedMethodRef));
                        arrayList2.add(SimpleInstruction.forOpcode(95));
                        arrayList2.add(SimpleInstruction.forOpcode(94));
                        arrayList2.add(SimpleInstruction.forOpcode(88));
                    } else {
                        arrayList2.add(new ConstantRefInstruction(20, constantLong));
                        if (instruction1 != null) {
                            arrayList2.add(instruction1);
                        }

                        arrayList2.add(new ConstantRefInstruction(184, resolvedMethodRef));
                        arrayList2.add(SimpleInstruction.forOpcode(91));
                        arrayList2.add(SimpleInstruction.forOpcode(87));
                    }

                    ResolvedMethodRefConstant resolvedMethodRefConstant = referenceObfuscator.getFieldSetterRef(
                            resolvedFieldRef, constantPool1, list3, classMemberLookup1, classResolver1
                    );
                    arrayList2.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
                    int bj = ba - 1;
                    int bg = bl ? 6 : 4;
                    int bh = bj;
                    ArrayList arrayList1 = arrayList2;
                    CodeInsertion codeInsertion = new CodeInsertion(arrayList1, bh, 1, bg);
                    list1.add(codeInsertion);
                    break;
                case 182:
                case 183:
                case 185:
                    this.addMethodCallObfuscation(
                            constantRefInstruction,
                            ba,
                            classMemberRef,
                            (ResolvedMethodRef) resolvedMemberRef,
                            constantLong,
                            instruction1,
                            list1,
                            resolvedMethodRef1,
                            map1,
                            methodFlowAnalyzer,
                            map3,
                            referenceObfuscator,
                            constantPool1,
                            list2,
                            list3,
                            classMemberLookup1,
                            classResolver1
                    );
                    break;
                case 184:
                    this.addMethodCallObfuscation(
                            constantRefInstruction,
                            ba,
                            classMemberRef,
                            (ResolvedMethodRef) resolvedMemberRef,
                            constantLong,
                            instruction1,
                            list1,
                            resolvedMethodRef1,
                            map1,
                            methodFlowAnalyzer,
                            map3,
                            referenceObfuscator,
                            constantPool1,
                            list2,
                            list3,
                            classMemberLookup1,
                            classResolver1
                    );
            }
        }
    }

    public MethodFlowAnalyzer createFlowAnalyzer(CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, boolean bl) throws ZkmException, IOException {
        CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
        NonNullList nonNullList = new NonNullList(this.instructions);
        ExceptionTableEntry[] exceptionTableEntrys = this.copyExceptionTable();
        int maxLocals = codeAttributeBody.getMaxLocals();
        codeAttributeBody.getMaxStack();
        List list2 = codeAttributeBody.getNormalizedParameterTypes();
        boolean methodStatic = codeAttributeBody.isMethodStatic();
        boolean constructor = this.isConstructor();
        boolean bl2 = methodStatic;
        List list1 = list2;
        MethodFlowAnalyzer methodFlowAnalyzer = new MethodFlowAnalyzer(
                nonNullList, exceptionTableEntrys, maxLocals, list1, bl2, constructor, commonSuperTypeResolver1, classHierarchyQuery, this, bl
        );
        this.hasLoops = methodFlowAnalyzer.hasNaturalLoops();
        return methodFlowAnalyzer;
    }

    public void insertArgumentArrayBoxing(
            List list1, List list2, ConstantPool constantPool1, List list3, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        int ba = this.instructions.size();
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Object", list3);

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) ((ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry())
                        .getResolvedMember();
                if (abstractMethodInfo != null && list2.contains(abstractMethodInfo)) {
                    if (instruction1.getOpcode() == 185) {
                        ((InvokeInterfaceInstruction) instruction1).setArgSlotCount(2);
                    }

                    List list4 = abstractMethodInfo.getParameterTypes();
                    int bc = list4.size();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Instruction.createIntConstantPush(bc, constantPool1, list3));
                    arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant));

                    for (int j = bc - 1; j >= 0; j += -1) {
                        String string = (String) list4.get(j);
                        if (string.length() == 1) {
                            switch (string.charAt(0)) {
                                case 'B':
                                case 'C':
                                case 'I':
                                case 'S':
                                    arrayList.add(SimpleInstruction.forOpcode(90));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    boolean bl3 = this.getOwningClass().supportsJava5();
                                    ClassMemberLookup classMemberLookup2 = classMemberLookup1;
                                    ConstantPool constantPool2 = constantPool1;
                                    List list5 = list3;
                                    PrimitiveBoxingGenerator.boxIntOnStack(arrayList, bl3, list5, constantPool2, classMemberLookup2, classResolver1);
                                    arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    arrayList.add(SimpleInstruction.forOpcode(83));
                                    break;
                                case 'D':
                                    arrayList.add(SimpleInstruction.forOpcode(91));
                                    arrayList.add(SimpleInstruction.forOpcode(91));
                                    arrayList.add(SimpleInstruction.forOpcode(87));
                                    boolean bl2 = this.getOwningClass().supportsJava5();
                                    ClassMemberLookup classMemberLookup6 = classMemberLookup1;
                                    ConstantPool constantPool6 = constantPool1;
                                    List list9 = list3;
                                    PrimitiveBoxingGenerator.boxDoubleOnStack(arrayList, bl2, list9, constantPool6, classMemberLookup6, classResolver1);
                                    arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    arrayList.add(SimpleInstruction.forOpcode(83));
                                case 'E':
                                case 'G':
                                case 'H':
                                case 'K':
                                case 'L':
                                case 'M':
                                case 'N':
                                case 'O':
                                case 'P':
                                case 'Q':
                                case 'R':
                                case 'T':
                                case 'U':
                                case 'V':
                                case 'W':
                                case 'X':
                                case 'Y':
                                default:
                                    break;
                                case 'F':
                                    arrayList.add(SimpleInstruction.forOpcode(90));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    boolean bl1 = this.getOwningClass().supportsJava5();
                                    ClassResolver classResolver2 = classResolver1;
                                    ClassMemberLookup classMemberLookup4 = classMemberLookup1;
                                    ConstantPool constantPool4 = constantPool1;
                                    List list7 = list3;
                                    PrimitiveBoxingGenerator.boxFloatOnStack(arrayList, bl1, list7, constantPool4, classMemberLookup4, classResolver2);
                                    arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    arrayList.add(SimpleInstruction.forOpcode(83));
                                    break;
                                case 'J':
                                    arrayList.add(SimpleInstruction.forOpcode(91));
                                    arrayList.add(SimpleInstruction.forOpcode(91));
                                    arrayList.add(SimpleInstruction.forOpcode(87));
                                    boolean bl = this.getOwningClass().supportsJava5();
                                    ClassResolver classResolver3 = classResolver1;
                                    ClassMemberLookup classMemberLookup5 = classMemberLookup1;
                                    ConstantPool constantPool5 = constantPool1;
                                    List list8 = list3;
                                    PrimitiveBoxingGenerator.boxLongOnStack(arrayList, bl, list8, constantPool5, classMemberLookup5, classResolver3);
                                    arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    arrayList.add(SimpleInstruction.forOpcode(83));
                                    break;
                                case 'Z':
                                    arrayList.add(SimpleInstruction.forOpcode(90));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    boolean bl4 = this.getOwningClass().supportsJava5();
                                    ClassMemberLookup classMemberLookup3 = classMemberLookup1;
                                    ConstantPool constantPool3 = constantPool1;
                                    List list6 = list3;
                                    PrimitiveBoxingGenerator.boxBooleanOnStack(arrayList, bl4, list6, constantPool3, classMemberLookup3, classResolver1);
                                    arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                                    arrayList.add(SimpleInstruction.forOpcode(95));
                                    arrayList.add(SimpleInstruction.forOpcode(83));
                            }
                        } else {
                            arrayList.add(SimpleInstruction.forOpcode(90));
                            arrayList.add(SimpleInstruction.forOpcode(95));
                            arrayList.add(Instruction.createIntConstantPush(j, constantPool1, list3));
                            arrayList.add(SimpleInstruction.forOpcode(95));
                            arrayList.add(SimpleInstruction.forOpcode(83));
                        }
                    }

                    list1.add(new CodeInsertion(arrayList, i - 1, 0, 2));
                }
            }
        }
    }

    public NonNullList getInstructions() {
        return new NonNullList(this.instructions);
    }

    public boolean resolveReflectedMethodTargets(
            Set set1,
            Set set2,
            Set set3,
            boolean bl,
            Set set4,
            TwoKeyMap twoKeyMap,
            Set set5,
            ClasspathClassLoader classpathClassLoader1,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmException, IOException {
        boolean bl1 = true;
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            TrackedValue trackedValue = (TrackedValue) iterator.next();
            String string = trackedValue.getNormalizedName();
            if (string != null && string.length() > 0) {
                String string1 = ZkmUtils.dotsToSlashes(string);
                ClassFileBase classFileBase = lookupClass(string1, classpathClassLoader1);
                if (classFileBase != null) {
                    bl1 = this.resolveReflectedMethodsInClass(classFileBase, set2, set3, bl, set4, twoKeyMap, set5, classMemberLookup1, bl1);
                } else if (trackedValue instanceof TracedObjectType) {
                    try {
                        ClasspathClassFile classpathClassFile = classpathClassLoader1.getClassFile(string1);
                        if (classpathClassFile != null) {
                            bl1 = this.resolveReflectedLibraryMethods(classpathClassFile, set2, set3, bl, bl1);
                        } else {
                            bl1 = false;
                        }
                    } catch (ClassFileLoadException classFileLoadException) {
                        bl1 = false;
                    }
                }
            } else {
                bl1 = false;
            }
        }

        return bl1;
    }

    private void collectMethodRef(ResolvedMethodRef resolvedMethodRef, Set set1, Set set2) {
        String string = resolvedMethodRef.getReferencedClassName();
        ClassFileBase classFileBase = null;
        if (!string.startsWith("[")) {
            classFileBase = ClassHierarchyNode.findClassFile(string);
            if (classFileBase != null) {
                if (classFileBase.isMultiRelease()) {
                    set2.addAll(classFileBase.getAllVersions());
                } else {
                    set2.add(classFileBase);
                }
            }
        }

        AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
        if (abstractMethodInfo != null && (abstractMethodInfo.isProgramMember() || !ClassHierarchyNode.isUnknownClass(abstractMethodInfo.getClassName()))) {
            set1.add(abstractMethodInfo);
            ClassFileBase classFileBase1 = abstractMethodInfo.getOwningClass();
            if ((classFileBase == null || classFileBase1 != classFileBase) && !ClassHierarchyNode.isUnknownClass(classFileBase1.getClassName())) {
                if (classFileBase1.isMultiRelease()) {
                    set2.addAll(classFileBase1.getAllVersions());
                } else {
                    set2.add(classFileBase1);
                }
            }
        }
    }

    public void insertMethodKeyLocal(
            MethodParamChangeNode methodParamChangeNode, Long long1, ObservableHolder observableHolder, List list1, ConstantPool constantPool1, String string, int ba
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        ResolvedFieldRef resolvedFieldRef = methodParamChangeNode.getKeyFieldRef();
        long effectiveKey = methodParamChangeNode.getEffectiveKey();
        long bc = long1 ^ effectiveKey;
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(Instruction.createLongConstantLoad(bc, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(131));
        int maxLocals = this.getMaxLocals();
        arrayList.add(Instruction.createLongStore(maxLocals, this.localVariableList, ba));
        observableHolder.setValue(this.localVariableList.getSlotAt(maxLocals));
        this.setMaxLocals(maxLocals + 2);
        if (this.getMaxStack() < 4) {
            this.setMaxStack(4);
        }

        CodeInsertion codeInsertion = new CodeInsertion(arrayList, -1, 0, 4);
        this.applyCodeInsertion(codeInsertion, string);
    }

    public int getMinReturnDistance() {
        return this.minReturnDistance;
    }

    public void collectSerialPersistentFields(List list1, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        String string = ((ProgramClass) this.getOwningClass()).getClassName();
        String string1 = null;
        String string2 = null;
        byte ba = 1;
        int bb = this.instructions.size();
        boolean bl = false;
        int bc = bb - 1;

        while (true) {
            label165:
            {
                if (bc >= 0) {
                    Instruction instruction1 = (Instruction) this.instructions.get(bc);
                    int opcode = instruction1.getOpcode();
                    switch (ba) {
                        case 1:
                            if (opcode == 179) {
                                ResolvedFieldRef resolvedFieldRef2 = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                if (resolvedFieldRef2.getMemberName().trim().equals("serialPersistentFields")
                                        && resolvedFieldRef2.getReferencedClassName().trim().equals(string)) {
                                    ba = 2;
                                }
                            }
                            break label165;
                        case 2:
                            if (opcode == 183) {
                                ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                if (resolvedMethodRef.getMemberName().trim().equals("<init>")
                                        && resolvedMethodRef.getReferencedClassName().trim().equals("java/io/ObjectStreamField")
                                        && resolvedMethodRef.getDescriptor().trim().equals("(Ljava/lang/String;Ljava/lang/Class;)V")) {
                                    ba = 3;
                                }
                            } else if (opcode == 189) {
                                ResolvedClassConstant resolvedClassConstant2 = (ResolvedClassConstant) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                if (resolvedClassConstant2.getClassName().trim().equals("java/io/ObjectStreamField")) {
                                    bl = true;
                                    ba = 4;
                                }
                            } else if (opcode == 178) {
                                ResolvedFieldRef resolvedFieldRef1 = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                if (resolvedFieldRef1.getMemberName().trim().equals("NO_FIELDS")
                                        && resolvedFieldRef1.getReferencedClassName().trim().equals("java/io/ObjectStreamClass")) {
                                    ba = 4;
                                }
                            }
                            break label165;
                        case 3:
                            if (opcode != 18 && opcode != 19) {
                                if (opcode != 178 && opcode != 180) {
                                    if (opcode == 187) {
                                        ResolvedClassConstant resolvedClassConstant1 = (ResolvedClassConstant) ((ConstantRefInstruction) instruction1)
                                                .getConstantPoolEntry();
                                        if (!resolvedClassConstant1.getClassName().trim().equals("java/io/ObjectStreamField")) {
                                            break label165;
                                        }

                                        ba = 2;
                                        if (string1 != null && string2 != null) {
                                            FieldInfo fieldInfo = classMemberLookup1.findField(string, string2, string1);
                                            if (fieldInfo == null) {
                                                throw new ZkmProcessingException("Unexpected structure (06) (" + string1 + "," + string2 + ")");
                                            }

                                            list1.add(fieldInfo);
                                            string1 = null;
                                            string2 = null;
                                            break label165;
                                        }

                                        StringBuilder stringBuilder = new StringBuilder().append("Unexpected structure (07) ('").append(string1).append("', '");
                                        String string3 = (string2 != null ? stringBuilder.append(true) : stringBuilder.append(false)).append("')").toString();
                                        throw new ZkmProcessingException(string3);
                                    }

                                    if (opcode == 189) {
                                        ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) ((ConstantRefInstruction) instruction1)
                                                .getConstantPoolEntry();
                                        if (resolvedClassConstant.getClassName().trim().equals("java/io/ObjectStreamField")) {
                                            ba = 4;
                                        }
                                    }
                                    break label165;
                                }

                                ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                                if (string1 == null) {
                                    if (!resolvedFieldRef.getDescriptor().equals("Ljava/lang/Class;") || !resolvedFieldRef.getMemberName().equals("TYPE")) {
                                        throw new ZkmProcessingException("Unexpected structure (04) : '" + resolvedFieldRef.getValueString() + "'");
                                    }

                                    String string4 = ConstantPoolEntry.getWrapperPrimitiveDescriptor(resolvedFieldRef.getReferencedClassName());
                                    if (string4 == null) {
                                        throw new ZkmProcessingException("Unexpected structure (03) : '" + resolvedFieldRef.getValueString() + "'");
                                    }

                                    string1 = string4;
                                }
                                break label165;
                            }

                            ConstantPoolEntry constantPoolEntry;
                            if (opcode == 18) {
                                LdcInstruction ldcInstruction = (LdcInstruction) instruction1;
                                constantPoolEntry = ldcInstruction.getConstantPoolEntry();
                            } else {
                                ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) instruction1;
                                constantPoolEntry = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                            }

                            if (constantPoolEntry instanceof ResolvedClassConstant) {
                                ResolvedClassConstant resolvedClassConstant3 = (ResolvedClassConstant) constantPoolEntry;
                                if (string1 != null) {
                                    throw new ZkmProcessingException("Unexpected structure (01) : '" + string1 + "' : '" + resolvedClassConstant3.getClassName() + "'");
                                }

                                string1 = resolvedClassConstant3.getClassName();
                            } else if (constantPoolEntry instanceof ResolvedStringConstant) {
                                ConstantUtf8 constantUtf8 = ((ResolvedStringConstant) constantPoolEntry).getValueUtf8();
                                if (string1 == null) {
                                    string1 = constantUtf8.getValue();
                                } else {
                                    if (string2 != null) {
                                        throw new ZkmProcessingException(
                                                "Unexpected structure (02) : '" + string2 + "' : '" + string1 + "' : '" + constantUtf8.getValue() + "'"
                                        );
                                    }

                                    string2 = constantUtf8.getValue();
                                }
                            }
                            break label165;
                        case 4:
                            if (bl) {
                                int be = list1.size();
                                switch (opcode) {
                                    case 3:
                                        if (be != 0) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + ",0)");
                                        }
                                        break;
                                    case 4:
                                        if (be != 1) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + ",1)");
                                        }
                                        break;
                                    case 5:
                                        if (be != 2) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + ",2)");
                                        }
                                        break;
                                    case 6:
                                        if (be != 3) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + ",3)");
                                        }
                                        break;
                                    case 7:
                                        if (be != 4) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + ",4)");
                                        }
                                        break;
                                    case 8:
                                        if (be != 5) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + ",5)");
                                        }
                                    case 9:
                                    case 10:
                                    case 11:
                                    case 12:
                                    case 13:
                                    case 14:
                                    case 15:
                                    default:
                                        break;
                                    case 16:
                                        BipushInstruction bipushInstruction = (BipushInstruction) instruction1;
                                        if (be != bipushInstruction.getByteValue()) {
                                            throw new ZkmProcessingException("Unexpected structure (08) (" + be + "," + bipushInstruction.getByteValue() + ")");
                                        }
                                }
                            }
                            break;
                        default:
                            break label165;
                    }
                }

                if (ba != 4) {
                    throw new ZkmProcessingException("Unexpected structure (09) mode=" + ba);
                }

                return;
            }

            bc += -1;
        }
    }

    public void recordCallGraphEdges(SetMultiMap setMultiMap, Set set1) {
        int ba = this.instructions.size();
        MethodInfo methodInfo1 = (MethodInfo) this.getMethod();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember() && !abstractMethodInfo.isStaticInitializer()) {
                    setMultiMap.addValue((MethodInfo) abstractMethodInfo, methodInfo1);
                    set1.add((MethodInfo) abstractMethodInfo);
                }
            } else if (HiddenOptionFlags.FOLLOW_LAMBDA_METHOD_HANDLES && instruction1.getOpcode() == 186) {
                ResolvedInvokeDynamic resolvedInvokeDynamic = ((InvokeDynamicInstruction) instruction1).getInvokeDynamicRef();
                AbstractMethodInfo abstractMethodInfo1 = resolvedInvokeDynamic.getLambdaImplMethod();
                if (abstractMethodInfo1 != null && abstractMethodInfo1.isProgramMember()) {
                    setMultiMap.addValue((MethodInfo) abstractMethodInfo1, methodInfo1);
                    set1.add((MethodInfo) abstractMethodInfo1);
                }
            }
        }
    }

    public void collectExcludedStringConstants(Set set1, StringEncryptionExclusionSpec stringEncryptionExclusionSpec) {
        ProgramClass programClass1 = (ProgramClass) this.getOwningClass();
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            int opcode = instruction1.getOpcode();
            if (opcode == 179 || opcode == 181) {
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) ((ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry())
                        .getResolvedMember();
                if (abstractFieldInfo != null
                        && abstractFieldInfo.isProgramMember()
                        && abstractFieldInfo.getParent() == programClass1
                        && stringEncryptionExclusionSpec.isFieldExcluded((FieldInfo) abstractFieldInfo)) {
                    Instruction instruction2 = (Instruction) this.instructions.get(i - 1);
                    int bd = instruction2.getOpcode();
                    if (bd == 18 || bd == 19) {
                        ConstantPoolEntry constantPoolEntry;
                        if (bd == 18) {
                            LdcInstruction ldcInstruction = (LdcInstruction) instruction2;
                            constantPoolEntry = ldcInstruction.getConstantPoolEntry();
                        } else {
                            ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) instruction2;
                            constantPoolEntry = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                        }

                        if (constantPoolEntry instanceof ResolvedStringConstant) {
                            set1.add((ResolvedStringConstant) constantPoolEntry);
                        }
                    }
                }
            }
        }
    }

    public void applyCodeInsertion(Object object, String string) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(object);
        this.applyCodeInsertions(arrayList, string, false, true);
    }

    public boolean hasStringConstants() {
        return this.stringConstantCount > 0;
    }

    public int findLastExitIndex() {
        int ba = -1;
        int bb = -1;

        for (int i = this.instructions.size() - 1; i >= 0; i += -1) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isReturn()) {
                return i;
            }

            if (ba == -1 && instruction1.getOpcode() == 191) {
                ba = i;
            }

            if (bb == -1 && instruction1.isJump()) {
                bb = i;
            }
        }

        return ba > -1 ? ba : bb;
    }

    public String prependParameterType(String string, String string1) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        stringBuilder.append("L");
        stringBuilder.append(string);
        stringBuilder.append(";");
        stringBuilder.append(string1.substring(1));
        return stringBuilder.toString();
    }

    public void recordClassNameValue(
            String string,
            boolean bl,
            TrackedValue trackedValue,
            TwoKeyMap twoKeyMap,
            ListMultimap listMultimap,
            Set set1,
            Set set2,
            ClasspathClassLoader classpathClassLoader1,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmException, IOException {
        String string1 = string;
        if (string1 != null
                && string1.length() > 0
                && (Character.isJavaIdentifierStart(string1.charAt(0)) || string1.charAt(0) == '[' && string1.charAt(string1.length() - 1) == ';')
                && !(trackedValue instanceof TracedObjectType)
                && !(trackedValue instanceof LiteralStringValue)) {
            int ba = 0;

            while (string1.charAt(ba) == '[') {
                ba++;
            }

            if (ba > 0) {
                string1 = string1.substring(ba);
                if (string1.length() <= 1 || !string1.endsWith(";")) {
                    return;
                }

                string1 = string1.substring(1, string1.length() - 1);
            }

            String string2 = ZkmUtils.dotsToSlashes(string1);
            ClassFileBase classFileBase = lookupClass(string2, classpathClassLoader1);
            if (classFileBase != null && (!bl || this.isResourceBundleSubclass(string2, classMemberLookup1))) {
                twoKeyMap.putValue(classFileBase, trackedValue, integerCache.valueOf(ba));
                set2.add(classFileBase);
                if (trackedValue instanceof StringConstantNameRef) {
                    set1.add(((StringConstantNameRef) trackedValue).getStringConstant());
                }
            } else if (bl && ba == 0) {
                listMultimap.addValue(string1, trackedValue);
                if (trackedValue instanceof StringConstantNameRef) {
                    set1.add(((StringConstantNameRef) trackedValue).getStringConstant());
                }
            }
        }
    }

    public void collectBlockFlowSplitCandidates(
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ObservableHolder observableHolder,
            BasicBlock basicBlock,
            BasicBlock basicBlock1,
            Set set1,
            StackFrameState[] stackFrameStates,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl,
            Random random1,
            String string
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        if (basicBlock != basicBlock1) {
            int startIndex = basicBlock.getStartIndex();
            if (((Instruction) this.instructions.get(startIndex)).isLabel()) {
                startIndex++;
            }

            for (int i = basicBlock.getEndIndex() - 1; i >= startIndex; i += -1) {
                if (stackFrameStates[i].isFullyInitialized()) {
                    StackFrameState stackFrameState = stackFrameStates[i];
                    HashSet hashSet = ZkmUtils.createHashSet();
                    NonNullList nonNullList1 = new NonNullList(this.instructions);
                    String string1 = string;
                    Boolean boolean1 = bl;
                    ObservableHolder observableHolder1 = observableHolder;
                    ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
                    CommonSuperTypeResolver commonSuperTypeResolver2 = commonSuperTypeResolver1;
                    NonNullList nonNullList = nonNullList1;
                    int bb = basicBlock1.findMatchingFrame(
                            stackFrameState,
                            set1,
                            stackFrameStates,
                            hashSet,
                            basicBlock1,
                            basicBlock1,
                            (BasicBlock) null,
                            nonNullList,
                            commonSuperTypeResolver2,
                            scriptEnvironment2,
                            observableHolder1,
                            boolean1,
                            string1
                    );
                    if (bb != -1 && !((Instruction) this.instructions.get(i)).isLabel()) {
                        arrayList.add(new FlowSplitCandidate(this, i, bb, (BasicBlock) observableHolder.getValue()));
                        if (HiddenOptionFlags.STOP_AT_FIRST_SPLIT_CANDIDATE) {
                            break;
                        }
                    }
                }
            }
        }

        this.selectFlowSplitCandidate(mutableInt, mutableInt1, observableHolder, arrayList, random1);
    }

    public int emitMethodKeyInit(
            MethodParamChangeNode methodParamChangeNode,
            ChangedMethodDescriptor changedMethodDescriptor,
            long ba,
            Map map1,
            ObservableHolder observableHolder,
            List list1,
            Set set1,
            List list2,
            List list3,
            ConstantPool constantPool1,
            MethodOverrideAnalyzer methodOverrideAnalyzer
    ) throws ZkmException, IOException {
        observableHolder.clearValue();
        byte bb = 0;
        if (changedMethodDescriptor != null && !changedMethodDescriptor.hasNoAddedParams()) {
            MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) map1.get(this.getOwningClass());
            IndexedLocalSlot[] indexedLocalSlots = changedMethodDescriptor.getLocalSlots(this);
            if (changedMethodDescriptor.hasMultipleParams()) {
                if (changedMethodDescriptor.isLongParam(0)) {
                    list1.add(Instruction.createLongLoad(indexedLocalSlots[0].getSlot()));
                } else {
                    list1.add(Instruction.createIntLoad(indexedLocalSlots[0].getSlot()));
                    list1.add(SimpleInstruction.forOpcode(133));
                }

                int bd = changedMethodDescriptor.getUnusedBitCount(0);
                list1.add(Instruction.createIntPush(bd));
                list1.add(SimpleInstruction.forOpcode(121));

                for (int i = 1; i < indexedLocalSlots.length; i++) {
                    if (changedMethodDescriptor.isLongParam(i)) {
                        list1.add(Instruction.createLongLoad(indexedLocalSlots[i].getSlot()));
                    } else {
                        list1.add(Instruction.createIntLoad(indexedLocalSlots[i].getSlot()));
                        list1.add(SimpleInstruction.forOpcode(133));
                    }

                    bd = changedMethodDescriptor.getUnusedBitCount(i);
                    list1.add(Instruction.createIntPush(bd));
                    list1.add(SimpleInstruction.forOpcode(121));
                    int bf = changedMethodDescriptor.getBitOffset(i);
                    list1.add(Instruction.createIntPush(bf));
                    list1.add(SimpleInstruction.forOpcode(125));
                    list1.add(SimpleInstruction.forOpcode(129));
                }

                int maxLocals = this.getMaxLocals();
                if (usesKeyField(this.getMethod(), methodParamChangeNode1, methodOverrideAnalyzer)) {
                    set1.add((MethodInfo) this.getMethod());
                    ResolvedFieldRef resolvedFieldRef2 = methodParamChangeNode1.getKeyFieldRef();
                    list1.add(new ConstantRefInstruction(178, resolvedFieldRef2));
                    list1.add(SimpleInstruction.forOpcode(131));
                }

                list1.add(Instruction.createLongStore(maxLocals, this.localVariableList, 5));
                bb = 5;
                list2.add(integerCache.valueOf(maxLocals));
                list2.add(integerCache.valueOf(maxLocals + 1));
                observableHolder.setValue(this.localVariableList.getPenultimateSlot());
                this.setMaxLocals(maxLocals + 2);
            } else if (usesKeyField(this.getMethod(), methodParamChangeNode1, methodOverrideAnalyzer)) {
                set1.add((MethodInfo) this.getMethod());
                ResolvedFieldRef resolvedFieldRef1 = methodParamChangeNode1.getKeyFieldRef();
                list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
                list1.add(Instruction.createLongLoad(indexedLocalSlots[0].getSlot()));
                list1.add(SimpleInstruction.forOpcode(131));
                list1.add(Instruction.createLongStore(indexedLocalSlots[0].getSlot()));
                observableHolder.setValue(indexedLocalSlots[0].getSlot());
            } else {
                observableHolder.setValue(indexedLocalSlots[0].getSlot());
            }
        } else {
            if (methodParamChangeNode != null) {
                ResolvedFieldRef resolvedFieldRef = methodParamChangeNode.getKeyFieldRef();
                long effectiveKey = methodParamChangeNode.getEffectiveKey();
                long be = ba ^ effectiveKey;
                List list4;
                Instruction instruction1;
                if (resolvedFieldRef != null) {
                    list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
                    list4 = list1;
                    instruction1 = Instruction.createLongConstantLoad(be, constantPool1, list3);
                } else {
                    list4 = list1;
                    instruction1 = Instruction.createLongConstantLoad(be, constantPool1, list3);
                }

                list4.add(instruction1);
                list1.add(SimpleInstruction.forOpcode(131));
            } else {
                list1.add(Instruction.createLongConstantLoad(ba, constantPool1, list3));
            }

            int bg = this.getMaxLocals();
            list1.add(Instruction.createLongStore(bg, this.localVariableList, 5));
            bb = 1;
            list2.add(integerCache.valueOf(bg));
            list2.add(integerCache.valueOf(bg + 1));
            observableHolder.setValue(this.localVariableList.getSlotAt(bg));
            this.setMaxLocals(bg + 2);
        }

        return bb;
    }

    public boolean collectSubclassMethods(String string, Set set1, Set set2, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        boolean bl = false;
        ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
        HashMap hashMap = ZkmUtils.createHashMap();
        classHierarchyNode.collectAllSubtypes(hashMap);
        Iterator iterator = hashMap.keySet().iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = ((ClassHierarchyNode) iterator.next()).getProgramClass();
            if (programClass1 != null) {
                Iterator iterator1 = set1.iterator();

                while (iterator1.hasNext()) {
                    FieldNameTypeSignature fieldNameTypeSignature = (FieldNameTypeSignature) iterator1.next();
                    MethodInfo methodInfo1 = (MethodInfo) AbstractMethodInfo.selectMostSpecificReturnType(
                            classMemberLookup1.findMatchingMethods(programClass1, fieldNameTypeSignature), classMemberLookup1
                    );
                    if (methodInfo1 != null) {
                        set2.add(methodInfo1);
                        bl = true;
                    }
                }
            }
        }

        return bl;
    }

    public boolean isResourceBundleSubclass(String string, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        try {
            return classMemberLookup1.isSubclass(string, "java/util/ResourceBundle");
        } catch (ClassFileLoadException classFileLoadException) {
            return true;
        }
    }

    public void collectExcludedLongConstants(Set set1, LongEncryptionExclusionHandler longEncryptionExclusionHandler) {
        ProgramClass programClass1 = (ProgramClass) this.getOwningClass();
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            int opcode = instruction1.getOpcode();
            if (opcode == 179 || opcode == 181) {
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) ((ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry())
                        .getResolvedMember();
                if (abstractFieldInfo != null
                        && abstractFieldInfo.isProgramMember()
                        && abstractFieldInfo.getParent() == programClass1
                        && longEncryptionExclusionHandler.isFieldExcluded((FieldInfo) abstractFieldInfo)) {
                    Instruction instruction2 = (Instruction) this.instructions.get(i - 1);
                    if (instruction2.getOpcode() == 20) {
                        ConstantPoolEntry constantPoolEntry = ((ConstantRefInstruction) instruction2).getConstantPoolEntry();
                        if (constantPoolEntry instanceof ConstantLong) {
                            set1.add((ConstantLong) constantPoolEntry);
                        }
                    }
                }
            }
        }
    }

    public void applyParameterChanges(
            MethodParamChangeNode methodParamChangeNode,
            Map map1,
            Map map2,
            Map map3,
            boolean bl,
            Map map4,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            List list1,
            ConstantPool constantPool1,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            Set set1
    ) throws ZkmException, IOException {
        MethodInfo methodInfo1 = (MethodInfo) this.getMethod();
        ChangedMethodDescriptor changedMethodDescriptor = (ChangedMethodDescriptor) map2.get(methodInfo1);
        Long long1 = (Long) map3.get(methodInfo1);
        if (long1 == null) {
            if (methodParamChangeNode != null && methodParamChangeNode.getInitMethod() == methodInfo1) {
                ArrayList arrayList = new ArrayList(methodParamChangeNode.getInitInstructions());
                LabelInstruction labelInstruction = new LabelInstruction(8192);
                arrayList.add(labelInstruction);
                ArrayList arrayList1 = new ArrayList();
                this.setMaxStack(methodParamChangeNode.getMaxStack());
                this.insertAtMethodStart(arrayList, 0, "Method Parameter List Changing", arrayList1);
            }
        } else {
            long ba = long1;
            CodeAttributeBody codeAttributeBody = this.getCodeAttributeBody();
            boolean bl1 = false;
            if (changedMethodDescriptor != null) {
                int[] bb = ChangedMethodDescriptor.computeLocalIndices(changedMethodDescriptor, this.isStatic());
                if (bb.length > 0) {
                    for (int i = 0; i < bb.length; i++) {
                        LocalVariableSlot localVariableSlot1 = this.localVariableList.insertSlot(bb[i], changedMethodDescriptor.isLongParam(i), 5);
                        changedMethodDescriptor.registerLocalSlot(this, i, localVariableSlot1);
                    }

                    int bd = this.localVariableList.renumberSlots();
                    codeAttributeBody.setMaxLocals(bd + 1);
                    bl1 = true;
                }
            }

            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            LocalVariableSlot localVariableSlot2 = this.buildParamChangePrologue(
                    methodParamChangeNode,
                    changedMethodDescriptor,
                    ba,
                    bl,
                    map1,
                    map2,
                    map3,
                    arrayList3,
                    arrayList2,
                    commonSuperTypeResolver1,
                    classHierarchyQuery,
                    list1,
                    constantPool1,
                    methodOverrideAnalyzer,
                    set1
            );
            if (!arrayList2.isEmpty()) {
                Collections.sort(arrayList2);
                this.applyCodeInsertions(arrayList2, "Method Parameter List Changing", !arrayList3.isEmpty());
                bl1 = true;
            }

            if (!arrayList3.isEmpty() && HiddenOptionFlags.ADJUST_LOCALS) {
                this.relocateAddedLocals(arrayList3);
                bl1 = true;
            }

            if (localVariableSlot2 != null) {
                map4.put(methodInfo1, localVariableSlot2);
            }

            if (bl1) {
                this.recomputeOffsets();
                this.setModified(true);
            }
        }
    }

    public MethodBytecode(
            ClassFileComponent classFileComponent,
            byte[] ba,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent);
        this.codeLength = ba.length;
        this.instructions = new ArrayList(Math.max(5, (int) (this.codeLength / 2.5)));
        this.localVariableList = new LocalVariableList(this, ((CodeAttributeBody) classFileComponent).getMaxLocals());
        this.readInstructions(
                ClassFileInputStream.fromBytes(ba, false),
                this.codeLength,
                listMultimap,
                this.localVariableList,
                listMultimap1,
                listMultimap2,
                listMultimap3,
                listMultimap4,
                listMultimap5,
                listMultimap6
        );
        this.setModified(true);
    }

    public void collectLongConstants(Set set1) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isLongConstantLoad()) {
                ConstantPoolEntry constantPoolEntry = ((ConstantPoolOperand) instruction1).getConstantPoolEntry();
                if (constantPoolEntry instanceof ConstantLong) {
                    set1.add((ConstantLong) constantPoolEntry);
                }
            }
        }
    }

    public boolean isResultValueUsed(Instruction instruction1, SetMultiMap setMultiMap, BooleanFlag booleanFlag) {
        if (instruction1.getOpcode() == 87 || instruction1.getOpcode() == 88) {
            return false;
        }

        if (instruction1.isStore()) {
            LocalVariableInstruction localVariableInstruction = (LocalVariableInstruction) instruction1;
            LocalVariableAccessKind localVariableAccessKind = localVariableInstruction.getMatchingLoadKind();
            if (localVariableAccessKind != null) {
                int localIndex = localVariableInstruction.getLocalIndex();
                if (setMultiMap.containsValue(localVariableAccessKind, integerCache.valueOf(localIndex))) {
                    return true;
                }

                if (booleanFlag.getValue()) {
                    return false;
                }

                Iterator iterator = this.instructions.iterator();

                while (iterator.hasNext()) {
                    Instruction instruction2 = (Instruction) iterator.next();
                    if (instruction2.isLoad()) {
                        LocalVariableInstruction localVariableInstruction1 = (LocalVariableInstruction) instruction2;
                        setMultiMap.addValue(localVariableInstruction1.getAccessKind(), integerCache.valueOf(localVariableInstruction1.getLocalIndex()));
                        if (localVariableInstruction1.getAccessKind() == localVariableAccessKind && localVariableInstruction1.getLocalIndex() == localIndex) {
                            return true;
                        }
                    }
                }

                booleanFlag.setValue(true);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public MethodBytecode(ArrayList arrayList, LocalVariableList localVariableList1, String string) throws ZkmProcessingException {
        super(null);
        this.localVariableList = localVariableList1;
        this.localVariableList.setMethodBytecode(this);
        this.instructions = arrayList;
        Iterator iterator = this.instructions.iterator();

        while (iterator.hasNext()) {
            if (((Instruction) iterator.next()).isStringConstantLoad()) {
                this.stringConstantCount++;
            }
        }

        this.recomputeOffsets();
        if (this.getCodeLength() > 65535) {
            throw new ZkmProcessingException(
                    string
                            + " : Bytecode length greater than "
                            + 65535
                            + " in "
                            + this.getQualifiedMethodName()
                            + " in file '"
                            + this.getLocationName()
                            + "' (1). (new="
                            + this.getCodeLength()
                            + ") : '"
                            + string
                            + "'"
            );
        }

        this.setModified(true);
    }

    public boolean resolveReflectionCallTargets(
            ReflectionApiMethod reflectionApiMethod,
            CallArgumentValues callArgumentValues,
            TwoKeyMap twoKeyMap,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap1,
            TwoKeyMap twoKeyMap2,
            TwoKeyMap twoKeyMap3,
            ClassMemberLookup classMemberLookup1,
            ClasspathClassLoader classpathClassLoader1,
            Set set1,
            Set set2,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        if (reflectionApiMethod.isObjectNameLookup()) {
            return true;
        }

        if (reflectionApiMethod.isFunctionalInterfaceLookup()) {
            if (callArgumentValues.areAllValuesKnown()) {
                boolean bl = true;
                Set set5 = callArgumentValues.getArgumentValues(0);
                StringConstantNameRef stringConstantNameRef = (StringConstantNameRef) set5.iterator().next();
                Set set10 = callArgumentValues.getArgumentValues(1);
                String string1 = ((StringConstantNameRef) set10.iterator().next()).getStringValue();
                Set set15 = callArgumentValues.getArgumentValues(2);
                String string3 = ((StringConstantNameRef) set15.iterator().next()).getStringValue();
                MethodSignature methodSignature1 = new MethodSignature(stringConstantNameRef.getStringValue(), string1);
                ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string3);
                if (programClass2 != null) {
                    MethodInfo methodInfo3 = programClass2.findMethodBySignature(methodSignature1);
                    if (methodInfo3 != null) {
                        twoKeyMap2.putValue(methodInfo3, stringConstantNameRef, stringConstantNameRef);
                        set2.add(methodInfo3);
                        set1.add(stringConstantNameRef.getStringConstant());
                    } else {
                        observableHolder.setValue("'" + string3 + "' not found (A)");
                        bl = false;
                    }
                } else {
                    try {
                        ClasspathClassFile classpathClassFile = classpathClassLoader1.getClassFile(string3);
                        if (classpathClassFile.findMethod(methodSignature1) == null) {
                            observableHolder.setValue("Method not found in unopened class");
                            bl = false;
                        }
                    } catch (ClassFileLoadException classFileLoadException) {
                        observableHolder.setValue("'" + string3 + "' not found (B)");
                        bl = false;
                    }
                }

                return bl;
            } else {
                return false;
            }
        } else {
            int paramDetailCount = reflectionApiMethod.getParamDetailCount();
            int argumentCount = callArgumentValues.getArgumentCount();
            List list1 = reflectionApiMethod.getParamDetails();

            for (int i = 0; i < argumentCount; i += 1) {
                ReflectionParamDetail reflectionParamDetail;
                if (i < paramDetailCount) {
                    reflectionParamDetail = (ReflectionParamDetail) list1.get(i);
                } else {
                    reflectionParamDetail = null;
                }

                Set set3 = callArgumentValues.getArgumentValues(i);
                if ((reflectionParamDetail == null || !reflectionParamDetail.isClassParam())
                        && (reflectionParamDetail == null || !reflectionParamDetail.isClassNameParam())
                        && (reflectionParamDetail == null || !reflectionParamDetail.isClassOrPropertiesNameParam())
                        && reflectionParamDetail != null) {
                    if (reflectionParamDetail.isPackageNameParam()) {
                        Iterator iterator7 = set3.iterator();

                        while (iterator7.hasNext()) {
                            TrackedValue trackedValue7 = (TrackedValue) iterator7.next();
                            if (trackedValue7.isConstant()) {
                                if (trackedValue7 instanceof TracedArrayValue) {
                                    ArrayList arrayList1 = ((TracedArrayValue) trackedValue7).getAllValues();

                                    for (int j = 0; j < arrayList1.size(); j += 1) {
                                        TrackedValue trackedValue12 = (TrackedValue) arrayList1.get(j);
                                        this.recordMemberNameValue(trackedValue12.getNormalizedName(), trackedValue12, twoKeyMap3, set1);
                                    }
                                } else {
                                    String string6 = trackedValue7.getNormalizedName();
                                    this.recordMemberNameValue(string6, trackedValue7, twoKeyMap3, set1);
                                }
                            }
                        }
                    }
                } else {
                    Iterator iterator = set3.iterator();

                    while (iterator.hasNext()) {
                        TrackedValue trackedValue = (TrackedValue) iterator.next();
                        if (trackedValue.isConstant()) {
                            if (trackedValue instanceof TracedArrayValue) {
                                ArrayList arrayList = ((TracedArrayValue) trackedValue).getAllValues();

                                for (int j = 0; j < arrayList.size(); j += 1) {
                                    TrackedValue trackedValue1 = (TrackedValue) arrayList.get(j);
                                    this.recordClassNameValue(
                                            trackedValue1.getStringValue(),
                                            reflectionParamDetail != null && reflectionParamDetail.isClassOrPropertiesNameParam(),
                                            trackedValue1,
                                            twoKeyMap,
                                            listMultimap,
                                            set1,
                                            set2,
                                            classpathClassLoader1,
                                            classMemberLookup1
                                    );
                                }
                            } else {
                                String string = trackedValue.getStringValue();
                                this.recordClassNameValue(
                                        string,
                                        reflectionParamDetail != null && reflectionParamDetail.isClassOrPropertiesNameParam(),
                                        trackedValue,
                                        twoKeyMap,
                                        listMultimap,
                                        set1,
                                        set2,
                                        classpathClassLoader1,
                                        classMemberLookup1
                                );
                            }
                        }
                    }
                }
            }

            if (!callArgumentValues.areAllValuesKnown()) {
                return false;
            }

            ObservableHolder observableHolder1;
            String string10;
            label411:
            {
                label410:
                {
                    label409:
                    {
                        label516:
                        {
                            if (reflectionApiMethod.isFieldLookup()) {
                                Set set9 = callArgumentValues.getArgumentValues(ReflectionApiMethod.getMemberNameParamIndex(reflectionApiMethod));
                                SetMultiMap setMultiMap2 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set9.size()));
                                Iterator iterator6 = set9.iterator();

                                while (iterator6.hasNext()) {
                                    TrackedValue trackedValue6 = (TrackedValue) iterator6.next();
                                    setMultiMap2.addValue(trackedValue6.getStringValue(), trackedValue6);
                                }

                                Set set14 = callArgumentValues.getArgumentValues(ReflectionApiMethod.getTargetClassArgIndex(reflectionApiMethod));
                                Set set17 = null;
                                if (ReflectionApiMethod.getFieldTypeParamIndex(reflectionApiMethod) > -1) {
                                    set17 = callArgumentValues.getArgumentValues(ReflectionApiMethod.getFieldTypeParamIndex(reflectionApiMethod));
                                } else if (ReflectionApiMethod.isPrimitiveFieldUpdater(reflectionApiMethod)) {
                                    set17 = ZkmUtils.createHashSet();
                                    set17.add(new KnownNameValue(ReflectionApiMethod.getPrimitiveFieldTypeDescriptor(reflectionApiMethod)));
                                }

                                SetMultiMap setMultiMap5 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set14.size()));
                                Iterator iterator11 = set14.iterator();

                                while (iterator11.hasNext()) {
                                    TrackedValue trackedValue11 = (TrackedValue) iterator11.next();
                                    setMultiMap5.addValue(trackedValue11.getNormalizedName(), trackedValue11);
                                }

                                if (setMultiMap5.getKeyCount() == 0) {
                                    observableHolder1 = observableHolder;
                                    string10 = "UNRESOLVED : Field inadequately resolved (A)";
                                    break label411;
                                }

                                if (setMultiMap2.getKeyCount() == 0) {
                                    observableHolder1 = observableHolder;
                                    string10 = "UNRESOLVED : Field inadequately resolved (A)";
                                    break label411;
                                }

                                if (setMultiMap5.getKeyCount() > 1 && setMultiMap2.getKeyCount() > 1) {
                                    observableHolder1 = observableHolder;
                                    string10 = "UNRESOLVED : Field inadequately resolved (A)";
                                    break label411;
                                }

                                boolean bl3 = this.resolveReflectedFields(
                                        set14, set9, set17, set1, twoKeyMap1, set2, observableHolder, classpathClassLoader1, classMemberLookup1
                                );
                                if (!bl3) {
                                    return bl3;
                                }
                            } else if (reflectionApiMethod.isMethodLookup()) {
                                Set set8 = callArgumentValues.getArgumentValues(2);
                                SetMultiMap setMultiMap1 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set8.size()));
                                Iterator iterator5 = set8.iterator();

                                while (iterator5.hasNext()) {
                                    TrackedValue trackedValue5 = (TrackedValue) iterator5.next();
                                    setMultiMap1.addValue(trackedValue5.getNormalizedName(), trackedValue5);
                                }

                                Set set13 = callArgumentValues.getArgumentValues(0);
                                SetMultiMap setMultiMap3 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set13.size()));
                                Iterator iterator9 = set13.iterator();

                                while (iterator9.hasNext()) {
                                    TrackedValue trackedValue9 = (TrackedValue) iterator9.next();
                                    setMultiMap3.addValue(trackedValue9.getStringValue(), trackedValue9);
                                }

                                Set set18 = callArgumentValues.getArgumentValues(1);
                                SetMultiMap setMultiMap7 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set18.size()));
                                Iterator iterator13 = set18.iterator();

                                while (iterator13.hasNext()) {
                                    TrackedValue trackedValue14 = (TrackedValue) iterator13.next();
                                    setMultiMap7.addValue(trackedValue14.getCombinedValueKey(), trackedValue14);
                                }

                                if (setMultiMap1.getKeyCount() == 0) {
                                    observableHolder1 = observableHolder;
                                    string10 = "UNRESOLVED : Method inadequately resolved (A).";
                                    break label410;
                                }

                                if (setMultiMap3.getKeyCount() == 0) {
                                    observableHolder1 = observableHolder;
                                    string10 = "UNRESOLVED : Method inadequately resolved (A).";
                                    break label410;
                                }

                                if (setMultiMap7.getKeyCount() == 0) {
                                    observableHolder1 = observableHolder;
                                    string10 = "UNRESOLVED : Method inadequately resolved (A).";
                                    break label410;
                                }

                                if (setMultiMap1.getKeyCount() > 1) {
                                    if (setMultiMap3.getKeyCount() > 1) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Method inadequately resolved (A).";
                                        break label410;
                                    }

                                    if (setMultiMap7.getKeyCount() > 1) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Method inadequately resolved (A).";
                                        break label410;
                                    }
                                }

                                boolean bl6 = this.resolveReflectedMethodTargets(
                                        set8, set13, set18, false, set1, twoKeyMap2, set2, classpathClassLoader1, classMemberLookup1
                                );
                                if (!bl6) {
                                    return bl6;
                                }
                            } else if (reflectionApiMethod.isConstructorLookup()) {
                                Set set7 = callArgumentValues.getArgumentValues(callArgumentValues.getArgumentCount() - 1);
                                if (reflectionApiMethod.isDefaultConstructorCall()) {
                                    boolean bl1 = false;
                                    Iterator iterator4 = set7.iterator();

                                    while (iterator4.hasNext()) {
                                        TrackedValue trackedValue4 = (TrackedValue) iterator4.next();
                                        if (!(trackedValue4 instanceof NullReflectionValue)) {
                                            String string4 = trackedValue4.getNormalizedName();
                                            String string7 = ZkmUtils.dotsToSlashes(string4);
                                            ProgramClass programClass3 = ClassHierarchyNode.findProgramClass(string7);
                                            if (programClass3 != null) {
                                                FieldNameTypeSignature fieldNameTypeSignature1 = new FieldNameTypeSignature("<init>", "()", "V");
                                                MethodInfo[] methodInfos = classMemberLookup1.findMatchingMethods(programClass3, fieldNameTypeSignature1);
                                                if (methodInfos != null) {
                                                    if (methodInfos.length == 1) {
                                                        set2.add(methodInfos[0]);
                                                        bl1 = true;
                                                    }
                                                } else if (trackedValue4 instanceof TracedObjectType) {
                                                    HashSet hashSet = ZkmUtils.createHashSet(3);
                                                    hashSet.add(fieldNameTypeSignature1);
                                                    bl1 = this.collectSubclassMethods(string7, hashSet, set2, classMemberLookup1);
                                                }
                                            }
                                        }
                                    }

                                    if (!bl1) {
                                        observableHolder.setValue("UNRESOLVED : Constructor inadequately resolved (A).");
                                        return false;
                                    }
                                } else if (reflectionApiMethod.isAllConstructorsLookup()) {
                                    Iterator iterator2 = set7.iterator();

                                    while (iterator2.hasNext()) {
                                        TrackedValue trackedValue2 = (TrackedValue) iterator2.next();
                                        if (!(trackedValue2 instanceof NullReflectionValue)) {
                                            String string2 = trackedValue2.getNormalizedName();
                                            String string5 = ZkmUtils.dotsToSlashes(string2);
                                            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string5);
                                            if (programClass1 != null) {
                                                List list2 = programClass1.findMethodsByName("<init>");

                                                for (int i = 0; i < list2.size(); i += 1) {
                                                    MethodInfo methodInfo4 = (MethodInfo) list2.get(i);
                                                    set2.add(methodInfo4);
                                                }
                                            }
                                        }
                                    }
                                } else if (reflectionApiMethod.isSpecificConstructorLookup()) {
                                    Set set11 = callArgumentValues.getArgumentValues(0);
                                    if (set7.size() != 1 && set11.size() != 1) {
                                        observableHolder.setValue("UNRESOLVED : Constructor inadequately resolved (C).");
                                        return false;
                                    }

                                    boolean bl2 = false;
                                    Iterator iterator8 = set7.iterator();

                                    while (iterator8.hasNext()) {
                                        TrackedValue trackedValue8 = (TrackedValue) iterator8.next();
                                        if (!(trackedValue8 instanceof NullReflectionValue)) {
                                            String string8 = trackedValue8.getNormalizedName();
                                            String string9 = ZkmUtils.dotsToSlashes(string8);
                                            ProgramClass programClass4 = ClassHierarchyNode.findProgramClass(string9);
                                            if (programClass4 != null) {
                                                Iterator iterator14 = set11.iterator();

                                                while (iterator14.hasNext()) {
                                                    TrackedValue trackedValue15 = (TrackedValue) iterator14.next();
                                                    Set set4 = this.buildReflectedSignatures("<init>", false, trackedValue15);
                                                    if (set4 != null) {
                                                        MethodInfo methodInfo1 = null;
                                                        Iterator iterator1 = set4.iterator();

                                                        while (iterator1.hasNext()) {
                                                            FieldNameTypeSignature fieldNameTypeSignature = (FieldNameTypeSignature) iterator1.next();
                                                            MethodInfo methodInfo2 = (MethodInfo) AbstractMethodInfo.selectMostSpecificReturnType(
                                                                    classMemberLookup1.findMatchingMethods(programClass4, fieldNameTypeSignature), classMemberLookup1
                                                            );
                                                            if (methodInfo2 != null) {
                                                                if (methodInfo1 != null) {
                                                                    methodInfo1 = null;
                                                                    break;
                                                                }

                                                                methodInfo1 = methodInfo2;
                                                            }
                                                        }

                                                        if (methodInfo1 != null) {
                                                            bl2 = true;
                                                            set2.add(methodInfo1);
                                                        } else if (trackedValue8 instanceof TracedObjectType) {
                                                            bl2 = this.collectSubclassMethods(string9, set4, set2, classMemberLookup1);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!bl2) {
                                        observableHolder.setValue("UNRESOLVED : Constructor inadequately resolved (B).");
                                        return false;
                                    }
                                }
                            } else if (!reflectionApiMethod.isMethodTypeLookup() && reflectionApiMethod.isMethodHandleLookup()) {
                                Set set6 = callArgumentValues.getArgumentValues(0);
                                SetMultiMap setMultiMap = new SetMultiMap(ZkmUtils.getPrimeCapacity(set6.size()));
                                Iterator iterator3 = set6.iterator();

                                while (iterator3.hasNext()) {
                                    TrackedValue trackedValue3 = (TrackedValue) iterator3.next();
                                    setMultiMap.addValue(trackedValue3.getNormalizedName(), trackedValue3);
                                }

                                Set set12;
                                Set set16;
                                if (reflectionApiMethod.isConstructorLookup()) {
                                    set12 = ZkmUtils.createHashSet(3);
                                    set12.add(new KnownNameValue("<init>"));
                                    set16 = callArgumentValues.getArgumentValues(1);
                                } else {
                                    set12 = callArgumentValues.getArgumentValues(1);
                                    set16 = callArgumentValues.getArgumentValues(2);
                                }

                                SetMultiMap setMultiMap4 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set12.size()));
                                Iterator iterator10 = set12.iterator();

                                while (iterator10.hasNext()) {
                                    TrackedValue trackedValue10 = (TrackedValue) iterator10.next();
                                    setMultiMap4.addValue(trackedValue10.getStringValue(), trackedValue10);
                                }

                                SetMultiMap setMultiMap6 = new SetMultiMap(ZkmUtils.getPrimeCapacity(set16.size()));
                                Iterator iterator12 = set16.iterator();

                                while (iterator12.hasNext()) {
                                    TrackedValue trackedValue13 = (TrackedValue) iterator12.next();
                                    setMultiMap6.addValue(trackedValue13.getCombinedValueKey(), trackedValue13);
                                }

                                if (reflectionApiMethod.hasMethodNameParam()) {
                                    if (setMultiMap.getKeyCount() == 0) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Method inadequately resolved (B).";
                                        break label409;
                                    }

                                    if (setMultiMap4.getKeyCount() == 0) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Method inadequately resolved (B).";
                                        break label409;
                                    }

                                    if (setMultiMap6.getKeyCount() == 0) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Method inadequately resolved (B).";
                                        break label409;
                                    }

                                    if (setMultiMap.getKeyCount() > 1) {
                                        if (setMultiMap4.getKeyCount() > 1) {
                                            observableHolder1 = observableHolder;
                                            string10 = "UNRESOLVED : Method inadequately resolved (B).";
                                            break label409;
                                        }

                                        if (setMultiMap6.getKeyCount() > 1) {
                                            observableHolder1 = observableHolder;
                                            string10 = "UNRESOLVED : Method inadequately resolved (B).";
                                            break label409;
                                        }
                                    }

                                    boolean bl4 = this.resolveReflectedMethodTargets(
                                            set6, set12, set16, true, set1, twoKeyMap2, set2, classpathClassLoader1, classMemberLookup1
                                    );
                                    if (!bl4) {
                                        return bl4;
                                    }
                                } else if (reflectionApiMethod.hasFieldNameParam()) {
                                    if (setMultiMap.getKeyCount() == 0) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Field inadequately resolved (B)";
                                        break label516;
                                    }

                                    if (setMultiMap4.getKeyCount() == 0) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Field inadequately resolved (B)";
                                        break label516;
                                    }

                                    if (setMultiMap.getKeyCount() > 1 && setMultiMap4.getKeyCount() > 1) {
                                        observableHolder1 = observableHolder;
                                        string10 = "UNRESOLVED : Field inadequately resolved (B)";
                                        break label516;
                                    }

                                    boolean bl5 = this.resolveReflectedFields(
                                            set6, set12, set16, set1, twoKeyMap1, set2, observableHolder, classpathClassLoader1, classMemberLookup1
                                    );
                                    if (!bl5) {
                                        return bl5;
                                    }
                                }
                            }

                            return true;
                        }

                        observableHolder1.setValue(string10);
                        return false;
                    }

                    observableHolder1.setValue(string10);
                    return false;
                }

                observableHolder1.setValue(string10);
                return false;
            }

            observableHolder1.setValue(string10);
            return false;
        }
    }

    public void applyFlowObfuscation(
            Set set1,
            Set set2,
            OpaquePredicateField opaquePredicateField,
            OpaquePredicateField opaquePredicateField1,
            NestedMultiMap nestedMultiMap,
            List list1,
            ConstantPool constantPool1,
            ListMultimap listMultimap,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        boolean bl = set1.contains(this);
        boolean bl1 = set2.contains(this);
        ArrayList arrayList = new ArrayList();
        this.buildFlowObfuscationCode(
                bl,
                bl1,
                listMultimap,
                opaquePredicateField,
                opaquePredicateField1,
                nestedMultiMap,
                list1,
                arrayList,
                constantPool1,
                map1,
                classMemberLookup1,
                commonSuperTypeResolver1,
                classResolver1,
                random1
        );
        List list2 = listMultimap.getValues(this);
        this.applyCodeInsertions(list2, "Flow Obfuscation", !arrayList.isEmpty() && HiddenOptionFlags.ADJUST_LOCALS);
        if (!arrayList.isEmpty() && HiddenOptionFlags.ADJUST_LOCALS) {
            this.relocateAddedLocals(arrayList);
            int codeLength = this.getCodeLength();
            this.recomputeOffsets();
            this.setModified(true);
            if (this.getCodeLength() > 65535) {
                throw new ZkmProcessingException(
                        "Flow Obfuscation : Bytecode length greater than 65535 in "
                                + this.getQualifiedMethodName()
                                + " in file '"
                                + this.getLocationName()
                                + "' (3). (original="
                                + codeLength
                                + ", new="
                                + this.getCodeLength()
                                + ") : '"
                                + "Flow Obfuscation"
                                + "'"
                );
            }
        }
    }

    public int getCodeLength() {
        return this.codeLength;
    }

    public int[] insertAtMethodStart(List list1, int ba, String string, List list2) throws ZkmProcessingException {
        int bb = ba;
        if (bb == 0) {
            for (int i = 0; i < this.instructions.size(); i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if (instruction1.getOpcode() == 2147483646) {
                    LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.isFixed()) {
                        bb = i + 1;
                    } else if (labelInstruction.hasUsageBits(8192)) {
                        bb = i + 1;
                        break;
                    }
                }
            }
        }

        int bd = list1.size();
        this.instructions.size();
        if (bd > 0) {
            this.setModified(true);
        }

        this.instructions.addAll(bb, list1);
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            Instruction instruction2 = (Instruction) iterator.next();
            if (instruction2.isStringConstantLoad()) {
                this.stringConstantCount++;
            }

            if (instruction2.loadsConstantInteger()) {
                this.intConstantCount++;
            }

            if (instruction2.isLongConstantLoad()) {
                this.longConstantCount++;
            }
        }

        Object object = null;
        if (list2 != null && list2.size() > 0 && HiddenOptionFlags.ADJUST_LOCALS) {
            this.relocateAddedLocals(list2);
        }

        int codeLength = this.getCodeLength();
        this.recomputeOffsets();
        if (this.getCodeLength() > 65535) {
            throw new ZkmProcessingException(
                    string
                            + " : Bytecode length greater than "
                            + 65535
                            + " in "
                            + this.getQualifiedMethodName()
                            + " in file '"
                            + this.getLocationName()
                            + "' (5). (original="
                            + codeLength
                            + ", new="
                            + this.getCodeLength()
                            + ") : '"
                            + string
                            + "'"
            );
        } else {
            return (int[]) object;
        }
    }

    public void addSplitCodeInsertions(List list1, List list2, Instruction instruction1, List list3, MethodFlowAnalyzer methodFlowAnalyzer, int ba) {
        if (methodFlowAnalyzer != null && !HiddenOptionFlags.NO_RANDOM_INSERTION_POINTS) {
            SyncIndexedSet syncIndexedSet = this.findInsertionSyncPoints(list3, methodFlowAnalyzer, ba);
            switch (syncIndexedSet.size()) {
                case 0:
                    ArrayList arrayList2 = new ArrayList();
                    if (list1 != null) {
                        arrayList2.addAll(list1);
                    }

                    if (list2 != null) {
                        arrayList2.addAll(list2);
                    }

                    arrayList2.add(instruction1);
                    list3.add(new CodeInsertion(arrayList2, ba - 1, 1, 2));
                    break;
                case 1:
                    ArrayList arrayList1 = new ArrayList();
                    if (list1 != null) {
                        arrayList1.addAll(list1);
                    }

                    if (list2 != null) {
                        arrayList1.addAll(list2);
                    }

                    list3.add(new CodeInsertion(arrayList1, (Integer) syncIndexedSet.getElementAt(0), 0, 2));
                    list3.add(new CodeInsertion(instruction1, ba - 1, 1, 0));
                    break;
                default:
                    int bb = syncIndexedSet.size() - 1;
                    if (list1 != null && !list1.isEmpty()) {
                        int bd = bb;
                        bb += -1;
                        int bc = (Integer) syncIndexedSet.getElementAt(bd);
                        List list4 = list1;
                        list3.add(new CodeInsertion(list4, bc, 0, 2));
                    }

                    if (list2 != null && !list2.isEmpty()) {
                        list3.add(new CodeInsertion(list2, (Integer) syncIndexedSet.getElementAt(bb), 0, 2));
                    }

                    list3.add(new CodeInsertion(instruction1, ba - 1, 1, 0));
            }
        } else {
            ArrayList arrayList = new ArrayList();
            if (list1 != null) {
                arrayList.addAll(list1);
            }

            if (list2 != null) {
                arrayList.addAll(list2);
            }

            arrayList.add(instruction1);
            list3.add(new CodeInsertion(arrayList, ba - 1, 1, 2));
        }
    }

    public List buildOpaqueGuardedBlock(
            int ba,
            Object object,
            Instruction instruction1,
            OpaquePredicateField opaquePredicateField,
            OpaquePredicateField opaquePredicateField1,
            boolean bl,
            NestedMultiMap nestedMultiMap,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList(11);
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        arrayList.add(object);
        if (bl) {
            arrayList.add(opaquePredicateField.createBranchIfUnset(labelInstruction));
        } else {
            arrayList.add(opaquePredicateField.createBranchIfSet(labelInstruction));
        }

        ConstantRefInstruction constantRefInstruction = opaquePredicateField1.createWriteInstruction(constantPool1, list1);
        this.recordMemberReference(constantRefInstruction, nestedMultiMap);
        if (opaquePredicateField1.isBoolean()) {
            LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
            LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
            arrayList.add(instruction1);
            arrayList.add(new BranchInstruction(153, labelInstruction1));
            arrayList.add(SimpleInstruction.forOpcode(3));
            arrayList.add(new GotoInstruction(labelInstruction2));
            arrayList.add(labelInstruction1);
            arrayList.add(SimpleInstruction.forOpcode(4));
            arrayList.add(labelInstruction2);
            arrayList.add(constantRefInstruction);
        } else if (opaquePredicateField1.isReferenceType()) {
            arrayList.addAll(
                    FlowObfuscationManager.buildFieldInitializer(opaquePredicateField1, list1, constantPool1, false, classMemberLookup1, classResolver1, random1)
            );
            arrayList.add(constantRefInstruction);
        } else {
            arrayList.add(Instruction.createIntIncrement(ba, 1, this.localVariableList, 2));
            arrayList.add(instruction1);
            arrayList.add(constantRefInstruction);
        }

        arrayList.add(labelInstruction);
        return arrayList;
    }

    public void writeCode(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.getCodeLength());

        for (int i = 0; i < this.instructions.size(); i++) {
            ((Instruction) this.instructions.get(i)).writeTo(dataOutputStream);
        }
    }

    public SyncIndexedSet findEmptyStackPositions(MethodFlowAnalyzer methodFlowAnalyzer, int ba, int bb, Integer integer) {
        BasicBlock basicBlock = methodFlowAnalyzer.getBlockContaining(ba);
        StackFrameState[] stackFrameStates = methodFlowAnalyzer.copyFrameStates();
        int bc = this.findMarkerLabelIndex();
        SyncIndexedSet syncIndexedSet = new SyncIndexedSet();
        if (ba > 0 && stackFrameStates[ba].getStack().length == 0 && basicBlock.getStartIndex() > bc) {
            int bd = Math.max(basicBlock.getStartIndex(), bb + 1);

            for (int i = ba - 1; i >= bd; i += -1) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                StackFrameState stackFrameState = stackFrameStates[i];
                LabelInstruction labelInstruction = null;
                if (instruction1.isLabel()) {
                    labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(256)) {
                        break;
                    }
                }

                if (integer != null) {
                    VerifierType verifierType = stackFrameState.getLocals()[integer];
                    if (verifierType.isTop()) {
                        break;
                    }
                }

                boolean bl = labelInstruction != null && labelInstruction.getOffset() == 0;
                if (stackFrameState.getStack().length == 0
                        && !instruction1.isJump()
                        && instruction1.getOpcode() != 191
                        && (labelInstruction == null || labelInstruction.getOffset() <= 0)) {
                    if (bl) {
                        syncIndexedSet.add(integerCache.valueOf(0));
                    } else {
                        syncIndexedSet.add(integerCache.valueOf(i));
                    }
                }
            }
        }

        return syncIndexedSet;
    }

    public void writeCodeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeInt(this.getCodeLength());

        for (int i = 0; i < this.instructions.size(); i++) {
            ((Instruction) this.instructions.get(i)).writeRemapped(dataOutputStream, map1);
        }
    }

    public MethodFlowAnalyzer createFlowAnalyzer(CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        return this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery, false);
    }

    public void buildKeyParameterPredicates(
            Long long1,
            Long long2,
            ChangedMethodDescriptor changedMethodDescriptor,
            List list1,
            List list2,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Random random1,
            ProcessingStatistics processingStatistics1
    ) throws ZkmException, IOException {
        int addedParamCount = changedMethodDescriptor.getAddedParamCount();
        IndexedLocalSlot[] indexedLocalSlots = changedMethodDescriptor.getLocalSlots(this);
        KeyParameterValue[] keyParameterValues = new KeyParameterValue[addedParamCount];
        if (addedParamCount == 1) {
            keyParameterValues[0] = new LongKeyParameterValue(indexedLocalSlots[0].getSlot(), long1);
        } else {
            long bb;
            if (long2 != null) {
                bb = long1 ^ long2;
            } else {
                bb = long1;
            }

            for (int i = 0; i < addedParamCount; i++) {
                int bd = changedMethodDescriptor.getKeyBitOffset(i);
                long be;
                if (bd > 0) {
                    be = bb << bd;
                } else {
                    be = bb;
                }

                int bg = changedMethodDescriptor.getShiftCount(i);
                be >>>= bg;
                if (changedMethodDescriptor.isLongParam(i)) {
                    keyParameterValues[i] = new LongKeyParameterValue(indexedLocalSlots[i].getSlot(), be);
                } else {
                    int bh = -1;
                    switch (changedMethodDescriptor.getTypeCharAt(i)) {
                        case 'B':
                            bh = (byte) be;
                            break;
                        case 'C':
                            bh = (char) be;
                            break;
                        case 'I':
                            bh = (int) be;
                            break;
                        case 'S':
                            bh = (short) be;
                    }

                    keyParameterValues[i] = new IntParameterValue(indexedLocalSlots[i].getSlot(), bh);
                }
            }
        }

        MethodFlowAnalyzer methodFlowAnalyzer = this.getCachedFlowAnalyzer(map1, commonSuperTypeResolver1, classMemberLookup1);
        Collections.sort(list1);
        ConstantPool constantPool1 = (ConstantPool) this.getConstantPool();
        CodeInsertion codeInsertion3 = null;
        ArrayList arrayList6 = new ArrayList(list1);
        int bs = 0;

        for (int i = 0; i < arrayList6.size(); i++) {
            CodeInsertion codeInsertion4 = (CodeInsertion) arrayList6.get(i);
            if (codeInsertion4 instanceof LabeledCodeInsertion) {
                LabeledCodeInsertion labeledCodeInsertion = (LabeledCodeInsertion) codeInsertion4;
                ArrayList arrayList = new ArrayList();
                int bi = random1.nextInt(addedParamCount);
                KeyParameterValue keyParameterValue = keyParameterValues[bi];
                SyncIndexedSet syncIndexedSet = null;
                int bj = -1;
                if (!HiddenOptionFlags.NO_KEY_INSERTION_SPLIT) {
                    int position = labeledCodeInsertion.getPosition();
                    int by = codeInsertion3 != null ? codeInsertion3.getPosition() : -1;
                    Integer integer = keyParameterValue.getSlotIndex();
                    syncIndexedSet = this.findEmptyStackPositions(methodFlowAnalyzer, position, by, integer);
                    bj = syncIndexedSet.size() - 1;
                }

                if (keyParameterValue.isLongKey()) {
                    Instruction instruction4 = Instruction.createLongLoad(indexedLocalSlots[bi].getSlot());
                    Instruction instruction5;
                    long bw;
                    switch (random1.nextInt(processingStatistics1.meetsBasicSizeThreshold() ? 5 : 4)) {
                        case 0:
                        case 1:
                            instruction5 = SimpleInstruction.forOpcode(9);
                            bw = 0L;
                            break;
                        case 2:
                        case 3:
                            instruction5 = SimpleInstruction.forOpcode(10);
                            bw = 1L;
                            break;
                        default:
                            bw = random1.nextLong();
                            instruction5 = Instruction.createLongConstantLoad(bw, constantPool1, list2);
                    }

                    boolean bl = processingStatistics1.meetsFullSizeThreshold() ? random1.nextBoolean() : false;
                    SimpleInstruction simpleInstruction = SimpleInstruction.forOpcode(148);
                    long keyValue = ((LongKeyParameterValue) keyParameterValue).getKeyValue();
                    int bm;
                    if (bl) {
                        bm = bw > keyValue ? 1 : (keyValue == bw ? 0 : -1);
                    } else {
                        bm = keyValue > bw ? 1 : (keyValue == bw ? 0 : -1);
                    }

                    BranchInstruction branchInstruction1 = this.createKeySignBranch(
                            labeledCodeInsertion.isExistingBranchTarget(), bm, labeledCodeInsertion.getTargetLabel(), random1
                    );
                    if (syncIndexedSet != null && !syncIndexedSet.isEmpty()) {
                        if (syncIndexedSet.size() == 1) {
                            ArrayList arrayList2 = new ArrayList();
                            if (bl) {
                                arrayList2.add(instruction5);
                                CodeInsertion codeInsertion1 = new CodeInsertion(arrayList2, (Integer) syncIndexedSet.getElementAt(bj), 0, 1);
                                list1.add(bs, codeInsertion1);
                                bs++;
                                arrayList.add(instruction4);
                            } else {
                                arrayList2.add(instruction4);
                                CodeInsertion codeInsertion8 = new CodeInsertion(arrayList2, (Integer) syncIndexedSet.getElementAt(bj), 0, 1);
                                list1.add(bs, codeInsertion8);
                                bs++;
                                arrayList.add(instruction5);
                            }

                            arrayList.add(simpleInstruction);
                        } else if (syncIndexedSet.size() == 2) {
                            if (bl) {
                                CodeInsertion codeInsertion5 = new CodeInsertion(instruction5, (Integer) syncIndexedSet.getElementAt(bj), 0, 1);
                                list1.add(bs, codeInsertion5);
                                bs++;
                                ArrayList arrayList9 = new ArrayList();
                                arrayList9.add(instruction4);
                                CodeInsertion codeInsertion2 = new CodeInsertion(arrayList9, (Integer) syncIndexedSet.getElementAt(bj), 0, 1);
                                list1.add(bs, codeInsertion2);
                                bs++;
                            } else {
                                ArrayList arrayList7 = new ArrayList();
                                arrayList7.add(instruction4);
                                int bt = bj + -1;
                                int bn = (Integer) syncIndexedSet.getElementAt(bj);
                                ArrayList arrayList3 = arrayList7;
                                CodeInsertion codeInsertion9 = new CodeInsertion(arrayList3, bn, 0, 1);
                                list1.add(bs, codeInsertion9);
                                bs++;
                                CodeInsertion codeInsertion11 = new CodeInsertion(instruction5, (Integer) syncIndexedSet.getElementAt(bt), 0, 1);
                                list1.add(bs, codeInsertion11);
                                bs++;
                            }

                            arrayList.add(simpleInstruction);
                        } else {
                            if (bl) {
                                int bu = bj + -1;
                                int bo = (Integer) syncIndexedSet.getElementAt(bj);
                                Instruction instruction2 = instruction5;
                                CodeInsertion codeInsertion6 = new CodeInsertion(instruction2, bo, 0, 1);
                                list1.add(bs, codeInsertion6);
                                bs++;
                                ArrayList arrayList10 = new ArrayList();
                                arrayList10.add(instruction4);
                                bj = bu + -1;
                                int bp = (Integer) syncIndexedSet.getElementAt(bu);
                                ArrayList arrayList4 = arrayList10;
                                CodeInsertion codeInsertion12 = new CodeInsertion(arrayList4, bp, 0, 1);
                                list1.add(bs, codeInsertion12);
                                bs++;
                            } else {
                                ArrayList arrayList8 = new ArrayList();
                                arrayList8.add(instruction4);
                                int bv = bj + -1;
                                int bq = (Integer) syncIndexedSet.getElementAt(bj);
                                ArrayList arrayList5 = arrayList8;
                                CodeInsertion codeInsertion10 = new CodeInsertion(arrayList5, bq, 0, 1);
                                list1.add(bs, codeInsertion10);
                                bs++;
                                bj = bv + -1;
                                int br = (Integer) syncIndexedSet.getElementAt(bv);
                                Instruction instruction3 = instruction5;
                                CodeInsertion codeInsertion13 = new CodeInsertion(instruction3, br, 0, 1);
                                list1.add(bs, codeInsertion13);
                                bs++;
                            }

                            CodeInsertion codeInsertion7 = new CodeInsertion(simpleInstruction, (Integer) syncIndexedSet.getElementAt(bj), 0, 1);
                            list1.add(bs, codeInsertion7);
                            bs++;
                        }
                    } else {
                        if (bl) {
                            arrayList.add(instruction5);
                            arrayList.add(instruction4);
                        } else {
                            arrayList.add(instruction4);
                            arrayList.add(instruction5);
                        }

                        arrayList.add(simpleInstruction);
                    }

                    arrayList.add(branchInstruction1);
                } else {
                    Instruction instruction1 = Instruction.createIntLoad(indexedLocalSlots[bi].getSlot());
                    BranchInstruction branchInstruction = this.createKeySignBranch(
                            labeledCodeInsertion.isExistingBranchTarget(),
                            ((IntParameterValue) keyParameterValue).getIntValue(),
                            labeledCodeInsertion.getTargetLabel(),
                            random1
                    );
                    if (syncIndexedSet != null && !syncIndexedSet.isEmpty()) {
                        ArrayList arrayList1 = new ArrayList();
                        arrayList1.add(instruction1);
                        CodeInsertion codeInsertion = new CodeInsertion(arrayList1, (Integer) syncIndexedSet.getElementAt(syncIndexedSet.size() - 1), 0, 1);
                        list1.add(bs, codeInsertion);
                        bs++;
                    } else {
                        arrayList.add(instruction1);
                    }

                    arrayList.add(branchInstruction);
                }

                labeledCodeInsertion.prependInstructions(arrayList);
            }

            codeInsertion3 = codeInsertion4;
            bs++;
        }
    }

    public void selectFlowSplitCandidate(MutableInt mutableInt, MutableInt mutableInt1, ObservableHolder observableHolder, List list1, Random random1) throws ZkmException, IOException {
        if (!list1.isEmpty()) {
            FlowSplitCandidate flowSplitCandidate;
            switch (list1.size()) {
                case 1:
                    flowSplitCandidate = (FlowSplitCandidate) list1.get(0);
                    break;
                case 2:
                    flowSplitCandidate = (FlowSplitCandidate) list1.get(1);
                    break;
                default:
                    int ba = random1.nextInt(list1.size() - 1) + 1;
                    flowSplitCandidate = (FlowSplitCandidate) list1.get(ba);
            }

            mutableInt.setValue(flowSplitCandidate.startIndex);
            mutableInt1.setValue(flowSplitCandidate.endIndex);
            observableHolder.setValue(flowSplitCandidate.targetBlock);
        } else {
            mutableInt.setValue(-1);
            mutableInt1.setValue(-1);
            observableHolder.setValue(null);
        }
    }

    public int getSerializedLength() {
        return 4 + this.getCodeLength();
    }

    public int indexOfInstruction(Object object) {
        return this.instructions.indexOf(object);
    }

    public boolean needsCheckcast(int ba, ResolvedMemberRef resolvedMemberRef, Map map1, MethodFlowAnalyzer methodFlowAnalyzer) {
        if (HiddenOptionFlags.OBFUSCATE_ALL_MEMBER_REFERENCES) {
            return true;
        }

        int opcode = ((Instruction) this.instructions.get(ba)).getOpcode();
        if (opcode != 182 && opcode != 183 && opcode != 184 && opcode != 185) {
            String string1 = ((ResolvedFieldRef) resolvedMemberRef).getDescriptor();
            if (!string1.startsWith("[") && (!string1.startsWith("L") || !string1.endsWith(";"))) {
                return false;
            }
        } else {
            String string = ConstantPoolEntry.getReturnDescriptor(((ResolvedMethodRef) resolvedMemberRef).getDescriptor());
            if (string.equals("V")) {
                return true;
            }
        }

        if (ba >= this.instructions.size() - 1) {
            return false;
        }

        StackFrameState[] stackFrameStates = methodFlowAnalyzer.copyFrameStates();
        int bc = stackFrameStates[ba].getStackDepth() - 1;
        VerifierType verifierType = stackFrameStates[ba].getStack()[bc];

        for (int i = ba + 1; i < this.instructions.size(); i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            StackFrameState stackFrameState = stackFrameStates[i];
            if (instruction1.isJump()) {
                return true;
            }

            if (instruction1.isStackManipulation()) {
                int[] be = instruction1.mapStackSlotForward(stackFrameStates[i - 1].getStack(), stackFrameState.getStack(), bc);
                if (be.length == 0) {
                    return verifierType.isWide();
                }

                if (be.length != 1) {
                    return true;
                }

                bc = be[0];
            } else {
                if (instruction1.consumesStackSlot(bc, stackFrameStates[i].getStackDepth())) {
                    if (map1.containsKey(instruction1) && !(Boolean) map1.get(instruction1)) {
                        if (verifierType.isClassType()) {
                            return false;
                        }

                        return true;
                    }

                    Integer integer = stackFrameStates[i].getStackDepth();
                    VerifierType verifierType1 = verifierType;
                    return instruction1.requiresTypedValueAt(bc, verifierType1, integer);
                }

                if (instruction1.isLabel()) {
                    if (i == this.instructions.size() - 1) {
                        return false;
                    }

                    if (((LabelInstruction) instruction1).hasUsageBits(1) || ((LabelInstruction) instruction1).hasUsageBits(1024)) {
                        return true;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String getLocationName() {
        CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
        return codeAttributeBody != null ? codeAttributeBody.getLocationName() : "<NO_PARENT_YET>";
    }

    public void insertBranchTargetLabels(ListMultimap listMultimap) {
        int keyCount = listMultimap.getKeyCount();
        if (keyCount != 0) {
            ArrayList arrayList = new ArrayList(listMultimap.keySet());
            Collections.sort(arrayList);
            Iterator iterator = arrayList.iterator();
            Integer integer = (Integer) iterator.next();
            int bb = this.instructions.size();
            int bc = 0;
            ArrayList arrayList1 = new ArrayList(bb + keyCount);
            int bd = 0;

            for (int i = 0; i < this.instructions.size(); i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if ((i > 0 || !instruction1.isLabel() || !((LabelInstruction) instruction1).isFixed()) && bd == integer) {
                    LabelInstruction labelInstruction;
                    if (instruction1.isLabel()) {
                        labelInstruction = (LabelInstruction) instruction1;
                    } else {
                        labelInstruction = new LabelInstruction(bd, 0);
                        arrayList1.add(labelInstruction);
                        bc++;
                    }

                    Iterator iterator1 = listMultimap.getValues(integer).iterator();

                    while (iterator1.hasNext()) {
                        LabelTargetHolder labelTargetHolder = (LabelTargetHolder) iterator1.next();
                        labelInstruction.addUsageFlag(labelTargetHolder.getUsageFlag());
                        labelTargetHolder.bindLabel(integer, labelInstruction);
                    }

                    if (!iterator.hasNext()) {
                        arrayList1.addAll(this.instructions.subList(i, bb));
                        break;
                    }

                    integer = (Integer) iterator.next();
                }

                arrayList1.add(instruction1);
                bd += instruction1.getLength();
            }

            this.instructions = arrayList1;
        }
    }

    public void applyCodeInsertions(List list1, String string) throws ZkmProcessingException {
        this.applyCodeInsertions(list1, string, false, true);
    }

    public void buildFlowObfuscationCode(
            boolean bl,
            boolean bl1,
            ListMultimap listMultimap,
            OpaquePredicateField opaquePredicateField,
            OpaquePredicateField opaquePredicateField1,
            NestedMultiMap nestedMultiMap,
            List list1,
            List list2,
            ConstantPool constantPool1,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        List list3 = listMultimap.getValues(this);
        int ba = 0;
        LabeledCodeInsertion labeledCodeInsertion = null;
        int bb = this.instructions.size() - 1;
        CodeInsertion codeInsertion = null;
        int bc = this.findLastExitIndex();
        if (list3 != null) {
            int bd = list3.size();

            for (int i = 0; i < bd; i++) {
                CodeInsertion codeInsertion1 = (CodeInsertion) list3.get(i);
                if (codeInsertion1.getPosition() < bb) {
                    bb = codeInsertion1.getPosition();
                    codeInsertion = codeInsertion1;
                }

                if (codeInsertion1 instanceof LabeledCodeInsertion) {
                    ba++;
                    if (labeledCodeInsertion == null && ((LabeledCodeInsertion) codeInsertion1).isExistingBranchTarget()) {
                        labeledCodeInsertion = (LabeledCodeInsertion) codeInsertion1;
                    }
                }
            }
        } else {
            if (listMultimap.isConcurrent()) {
                list3 = new Vector();
            } else {
                list3 = new ArrayList();
            }

            listMultimap.putValues(this, list3);
        }

        if (bl || bl1) {
            bb = Math.min(bc - 1, bb);
            if (bc - 1 < bb) {
                bb = bc - 1;
                codeInsertion = null;
            }
        }

        if (ba > 0 || bl || bl1) {
            MethodFlowAnalyzer methodFlowAnalyzer = this.getCachedFlowAnalyzer(map1, commonSuperTypeResolver1, classMemberLookup1);
            boolean bl3 = false;
            ConstantRefInstruction constantRefInstruction1 = null;
            if (ba > 0 || bl || bl1) {
                if (opaquePredicateField1.isNegatedGetterPresent() && random1.nextBoolean()) {
                    constantRefInstruction1 = opaquePredicateField1.createNegatedGetterCall(constantPool1, list1);
                    bl3 = true;
                } else {
                    constantRefInstruction1 = opaquePredicateField1.createReadInstruction(constantPool1, list1);
                }

                this.recordMemberReference(constantRefInstruction1, nestedMultiMap);
            }

            boolean bl2 = false;
            ConstantRefInstruction constantRefInstruction = null;
            if (bl || bl1) {
                if (opaquePredicateField.isNegatedGetterPresent() && random1.nextBoolean()) {
                    constantRefInstruction = opaquePredicateField.createNegatedGetterCall(constantPool1, list1);
                    bl2 = true;
                } else {
                    constantRefInstruction = opaquePredicateField.createReadInstruction(constantPool1, list1);
                }

                this.recordMemberReference(constantRefInstruction, nestedMultiMap);
            }

            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
            int bg = codeAttributeBody.getMaxLocals() + 1;
            codeAttributeBody.setMaxLocals(bg);
            int bf = bg - 1;
            list2.add(integerCache.valueOf(bf));
            Instruction instruction1 = opaquePredicateField1.createLoadLocal(bf, this.localVariableList);
            Instruction instruction2 = opaquePredicateField1.createStoreLocal(bf, this.localVariableList);
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(constantRefInstruction1);
            MutableInt mutableInt = new MutableInt();
            MutableInt mutableInt1 = mutableInt;
            SyncIndexedSet syncIndexedSet = this.findPrologueSyncPoints(methodFlowAnalyzer, bb, mutableInt1);
            this.addPredicateInsertions(arrayList, instruction2, list3, methodFlowAnalyzer, bb, codeInsertion, mutableInt.getValue(), syncIndexedSet, random1);
            Collections.sort(list3);
            CodeInsertion codeInsertion2 = null;
            ArrayList arrayList1 = new ArrayList(list3);
            int bh = 0;

            for (int i = 0; i < arrayList1.size(); i++) {
                CodeInsertion codeInsertion3 = (CodeInsertion) arrayList1.get(i);
                if (codeInsertion3 instanceof LabeledCodeInsertion) {
                    LabeledCodeInsertion labeledCodeInsertion1 = (LabeledCodeInsertion) codeInsertion3;
                    BranchInstruction branchInstruction;
                    if (labeledCodeInsertion1.isExistingBranchTarget() != bl3) {
                        branchInstruction = opaquePredicateField1.createBranchIfSet(labeledCodeInsertion1.getTargetLabel());
                    } else {
                        branchInstruction = opaquePredicateField1.createBranchIfUnset(labeledCodeInsertion1.getTargetLabel());
                    }

                    if (!HiddenOptionFlags.NO_OPAQUE_INSERTION_SPLIT) {
                        SyncIndexedSet syncIndexedSet1 = this.findEmptyStackPositions(
                                methodFlowAnalyzer, labeledCodeInsertion1.getPosition(), codeInsertion2 != null ? codeInsertion2.getPosition() : -1, (Integer) null
                        );
                        ArrayList arrayList2 = new ArrayList(2);
                        if (!syncIndexedSet1.isEmpty()) {
                            if ((Integer) syncIndexedSet1.getElementAt(syncIndexedSet1.size() - 1) < labeledCodeInsertion1.getPosition()) {
                                CodeInsertion codeInsertion4 = new CodeInsertion(instruction1, (Integer) syncIndexedSet1.getElementAt(syncIndexedSet1.size() - 1), 0, 1);
                                list3.add(bh, codeInsertion4);
                                bh++;
                            } else {
                                arrayList2.add(instruction1);
                            }
                        } else {
                            arrayList2.add(instruction1);
                        }

                        arrayList2.add(branchInstruction);
                        labeledCodeInsertion1.prependInstructions(arrayList2);
                        labeledCodeInsertion1.setExtraStackSize();
                    } else {
                        ArrayList arrayList4 = new ArrayList(2);
                        arrayList4.add(instruction1);
                        arrayList4.add(branchInstruction);
                        labeledCodeInsertion1.prependInstructions(arrayList4);
                        labeledCodeInsertion1.setExtraStackSize();
                    }
                }

                codeInsertion2 = codeInsertion3;
                bh++;
            }

            if (bl1) {
                List list4 = this.buildOpaqueGuardedBlock(
                        bf,
                        constantRefInstruction,
                        instruction1,
                        opaquePredicateField,
                        opaquePredicateField1,
                        bl2,
                        nestedMultiMap,
                        list1,
                        constantPool1,
                        classMemberLookup1,
                        classResolver1,
                        random1
                );
                CodeInsertion codeInsertion5 = new CodeInsertion(list4, bc - 1, 0, 1);
                list3.add(codeInsertion5);
            }

            if (bl) {
                List list5 = this.buildPredicateFieldUpdate(
                        opaquePredicateField, constantRefInstruction, nestedMultiMap, list1, constantPool1, list2, classMemberLookup1, classResolver1, random1
                );
                if (labeledCodeInsertion != null) {
                    labeledCodeInsertion.appendInstructions(list5);
                } else {
                    LabelInstruction labelInstruction = new LabelInstruction(true, 1);
                    int bj = list5.size();
                    ArrayList arrayList3 = new ArrayList(bj + 3);
                    arrayList3.add(instruction1);
                    if (bl3) {
                        arrayList3.add(opaquePredicateField1.createBranchIfUnset(labelInstruction));
                    } else {
                        arrayList3.add(opaquePredicateField1.createBranchIfSet(labelInstruction));
                    }

                    for (int i = 0; i < bj; i++) {
                        arrayList3.add(list5.get(i));
                    }

                    arrayList3.add(labelInstruction);
                    CodeInsertion codeInsertion6 = new CodeInsertion(arrayList3, bc - 1, 0, 1);
                    list3.add(codeInsertion6);
                }
            }
        }
    }

    public boolean isStaticInitializer() {
        return ((CodeAttributeBody) this.getParent()).isStaticInitializer();
    }

    public void printDisassembly(PrintWriter printWriter) throws ZkmProcessingException {
        StringBuilder stringBuilder = new StringBuilder(2);

        for (int i = 0; i < 2; i++) {
            stringBuilder.append("   ");
        }

        int bc = 0;

        for (int i = 0; i < this.instructions.size(); i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);

            try {
                instruction1.printDisassembly(printWriter, stringBuilder);
            } catch (UnknownOpcodeException unknownOpcodeException) {
                throw new ZkmProcessingException(unknownOpcodeException.getMessage());
            }

            bc += instruction1.getLength();
        }
    }

    public void collectExcludedIntConstants(Set set1, IntegerEncryptionExclusions integerEncryptionExclusions) {
        ProgramClass programClass1 = (ProgramClass) this.getOwningClass();
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            int opcode = instruction1.getOpcode();
            if (opcode == 179 || opcode == 181) {
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) ((ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry())
                        .getResolvedMember();
                if (abstractFieldInfo != null
                        && abstractFieldInfo.isProgramMember()
                        && abstractFieldInfo.getParent() == programClass1
                        && integerEncryptionExclusions.isFieldExcluded((FieldInfo) abstractFieldInfo)) {
                    Instruction instruction2 = (Instruction) this.instructions.get(i - 1);
                    int bd = instruction2.getOpcode();
                    if (bd == 18 || bd == 19) {
                        ConstantPoolEntry constantPoolEntry;
                        if (bd == 18) {
                            LdcInstruction ldcInstruction = (LdcInstruction) instruction2;
                            constantPoolEntry = ldcInstruction.getConstantPoolEntry();
                        } else {
                            ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) instruction2;
                            constantPoolEntry = ((ConstantPoolOperand) constantRefInstruction).getConstantPoolEntry();
                        }

                        if (constantPoolEntry instanceof ConstantInteger) {
                            set1.add((ConstantInteger) constantPoolEntry);
                        }
                    }
                }
            }
        }
    }

    public boolean removeMethodCalls(Set set1, ScriptEnvironment scriptEnvironment1) throws ZkmProcessingException {
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        ArrayList arrayList = new ArrayList();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        MutableInt mutableInt = new MutableInt();
        int ba = this.instructions.size();
        SetMultiMap setMultiMap = new SetMultiMap();
        BooleanFlag booleanFlag = new BooleanFlag(false);

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke() && instruction1.getOpcode() != 183) {
                ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                if (abstractMethodInfo != null && set1.contains(abstractMethodInfo)) {
                    String string = resolvedMethodRef.getStackReturnType();
                    Instruction instruction2 = (Instruction) this.instructions.get(i + 1);
                    if (string != null && this.isResultValueUsed(instruction2, setMultiMap, booleanFlag)) {
                        if (scriptEnvironment1.isVerbose()) {
                            linkedHashMap.put(integerCache.valueOf(i), abstractMethodInfo);
                        }
                    } else {
                        List list1 = resolvedMethodRef.getStackParameterTypes();
                        boolean bl3;
                        if (!abstractMethodInfo.isStatic()) {
                            list1.add(0, "java/lang/Object");
                            bl3 = scriptEnvironment1.isVerbose();
                        } else {
                            bl3 = scriptEnvironment1.isVerbose();
                        }

                        if (bl3) {
                            AbstractMethodInfo abstractMethodInfo1 = this.getMethod();
                            printWriter.println(
                                    "\tRemoving call to method '"
                                            + abstractMethodInfo.toOriginalDisplayString()
                                            + (abstractMethodInfo.isRenamed() ? " (" + abstractMethodInfo.toDisplayString() + ')' : "")
                                            + "' in class '"
                                            + abstractMethodInfo.getLocationName()
                                            + "' from within method '"
                                            + abstractMethodInfo1.toOriginalDisplayString()
                                            + (abstractMethodInfo1.isRenamed() ? " (" + abstractMethodInfo1.toDisplayString() + ')' : "")
                                            + "' (A)"
                            );
                        }

                        ArrayList arrayList1 = new ArrayList();
                        int bc = i - 1;
                        int bd = bc;
                        int be = 1 + (string == null ? 0 : 1);
                        boolean bl = false;
                        int bf = list1.size();

                        for (int j = 0; j < bf; j++) {
                            int bh = bf - j - 1;
                            String string1 = (String) list1.get(bh);
                            if (!HiddenOptionFlags.SKIP_STRING_CONCAT_ANALYSIS && !bl) {
                                Instruction instruction3 = (Instruction) this.instructions.get(bd);
                                if (instruction3.pushesWithoutPopping()) {
                                    be++;
                                    bc += -1;
                                    bd += -1;
                                } else if (instruction3.getOpcode() == 180 && ((Instruction) this.instructions.get(bd - 1)).pushesWithoutPopping()) {
                                    be += 2;
                                    bc += -2;
                                    bd += -2;
                                } else {
                                    if (instruction3.isMethodInvoke()) {
                                        MutableInt mutableInt2 = mutableInt;
                                        Set set2 = set1;
                                        if (this.isRemovableCall(bd, (ConstantRefInstruction) instruction3, set2, mutableInt2)) {
                                            int value = mutableInt.getValue();
                                            be += value;
                                            bc -= value;
                                            bd -= value;
                                            continue;
                                        }
                                    }

                                    boolean bl1 = false;
                                    if (!HiddenOptionFlags.SKIP_STRING_BUILDER_ANALYSIS && instruction3.getOpcode() == 182) {
                                        ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) ((ConstantRefInstruction) instruction3).getConstantPoolEntry();
                                        if (resolvedMethodRef1.getMemberName().equals("toString")
                                                && resolvedMethodRef1.getDescriptor().equals("()Ljava/lang/String;")
                                                && (
                                                resolvedMethodRef1.getReferencedClassName().equals("java/lang/StringBuffer")
                                                        || resolvedMethodRef1.getReferencedClassName().equals("java/lang/StringBuilder")
                                        )) {
                                            MutableInt mutableInt1 = new MutableInt();
                                            bl1 = this.traceStringBuilderChain(bd - 1, resolvedMethodRef1.getReferencedClassName(), mutableInt1, set1);
                                            if (bl1) {
                                                int bi = mutableInt1.getValue();
                                                be += bi;
                                                bc -= bi;
                                                bd = bc;
                                            }
                                        }
                                    }

                                    if (!bl1) {
                                        bl = true;
                                        if (ConstantPoolEntry.isWideType(string1)) {
                                            arrayList1.add(SimpleInstruction.forOpcode(88));
                                        } else {
                                            arrayList1.add(SimpleInstruction.forOpcode(87));
                                        }

                                        bd += -1;
                                    }
                                }
                            } else if (ConstantPoolEntry.isWideType(string1)) {
                                arrayList1.add(SimpleInstruction.forOpcode(88));
                            } else {
                                arrayList1.add(SimpleInstruction.forOpcode(87));
                            }
                        }

                        if (be > 0 && arrayList1.isEmpty()) {
                            arrayList1.add(SimpleInstruction.forOpcode(0));
                        }

                        CodeInsertion codeInsertion1 = new CodeInsertion(arrayList1, bc, be, 0);
                        arrayList.add(codeInsertion1);
                    }
                }
            }
        }

        if (scriptEnvironment1.isVerbose()) {
            AbstractMethodInfo abstractMethodInfo2 = this.getMethod();
            Iterator iterator = arrayList.iterator();

            while (iterator.hasNext()) {
                CodeInsertion codeInsertion = (CodeInsertion) iterator.next();
                int bj = codeInsertion.getPosition() + 1;
                int bk = bj + codeInsertion.getReplacedCount();

                for (int i = bj; i < bk; i++) {
                    Integer integer = integerCache.valueOf(i);
                    AbstractMethodInfo abstractMethodInfo4 = (AbstractMethodInfo) linkedHashMap.remove(integer);
                    if (abstractMethodInfo4 != null) {
                        printWriter.println(
                                "\tRemoving call to method '"
                                        + abstractMethodInfo4.toOriginalDisplayString()
                                        + (abstractMethodInfo4.isRenamed() ? " (" + abstractMethodInfo4.toDisplayString() + ')' : "")
                                        + "' in class '"
                                        + abstractMethodInfo4.getLocationName()
                                        + "' from within method '"
                                        + abstractMethodInfo2.toOriginalDisplayString()
                                        + (abstractMethodInfo2.isRenamed() ? " (" + abstractMethodInfo2.toDisplayString() + ')' : "")
                                        + "' (B)"
                        );
                    }
                }
            }

            iterator = linkedHashMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                AbstractMethodInfo abstractMethodInfo3 = (AbstractMethodInfo) entry.getValue();
                printWriter.println(
                        "\tNOT removing call to method '"
                                + abstractMethodInfo3.toOriginalDisplayString()
                                + (abstractMethodInfo3.isRenamed() ? " (" + abstractMethodInfo3.toDisplayString() + ')' : "")
                                + "' in class '"
                                + abstractMethodInfo3.getLocationName()
                                + "' from within method '"
                                + abstractMethodInfo2.toOriginalDisplayString()
                                + (abstractMethodInfo2.isRenamed() ? " (" + abstractMethodInfo2.toDisplayString() + ')' : "")
                                + "' because return value may be used : "
                                + ((Instruction) this.instructions.get((Integer) entry.getKey() + 1)).toAssembly()
                );
            }
        }

        boolean bl2 = arrayList.size() > 0;
        if (bl2) {
            this.applyCodeInsertions(arrayList, "Remove Method Calls");
        }

        return bl2;
    }

    public void applyKeyParameterFlowObfuscation(
            Long long1,
            Long long2,
            ChangedMethodDescriptor changedMethodDescriptor,
            List list1,
            List list2,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Random random1,
            ProcessingStatistics processingStatistics1
    ) throws ZkmException, IOException {
        this.buildKeyParameterPredicates(
                long1, long2, changedMethodDescriptor, list1, list2, map1, classMemberLookup1, commonSuperTypeResolver1, random1, processingStatistics1
        );
        this.applyCodeInsertions(list1, "Flow Obfuscation using Method Parameter List Changing", false);
    }

    public void readInstructions(
            ClassFileInputStream classFileInputStream,
            int ba,
            ListMultimap listMultimap,
            LocalVariableList localVariableList1,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6
    ) throws ZkmProcessingException, IOException {
        int bb = 0;
        AbstractConstantPool abstractConstantPool = this.getConstantPool();

        while (bb < ba) {
            Instruction instruction1;
            try {
                instruction1 = Instruction.readInstruction(
                        classFileInputStream,
                        bb,
                        listMultimap,
                        listMultimap1,
                        listMultimap2,
                        listMultimap3,
                        listMultimap4,
                        listMultimap5,
                        listMultimap6,
                        abstractConstantPool,
                        localVariableList1
                );
            } catch (UnknownOpcodeException unknownOpcodeException) {
                throw new ZkmProcessingException(
                        unknownOpcodeException.getMessage() + " in " + this.getQualifiedMethodName() + " : " + this.getLocationName(), unknownOpcodeException
                );
            }

            if (instruction1.isStringConstantLoad()) {
                this.stringConstantCount++;
            }

            if (instruction1.loadsConstantInteger()) {
                this.intConstantCount++;
            }

            if (instruction1.isLongConstantLoad()) {
                this.longConstantCount++;
            }

            instruction1.setOffset(bb);
            this.instructions.add(instruction1);
            bb += instruction1.getLength();
        }
    }

    public boolean storesToField(FieldInfo fieldInfo) {
        int ba = this.instructions.size();
        int bb = 0;

        while (bb < ba) {
            Instruction instruction1 = (Instruction) this.instructions.get(bb);
            switch (instruction1.getOpcode()) {
                case 179:
                case 181:
                    if (((ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry()).getResolvedMember() == fieldInfo) {
                        return true;
                    }
                default:
                    bb++;
            }
        }

        return false;
    }

    public void collectStringEncryptionCandidates(MultiMapTable multiMapTable, MethodInfo methodInfo1, Set set1) {
        if (this.stringConstantCount != 0) {
            int ba = this.instructions.size();

            for (int i = 0; i < ba; i++) {
                ((Instruction) this.instructions.get(i)).collectStringConstant(multiMapTable, set1, methodInfo1, i);
            }
        }
    }

    public CodeInsertion buildStringConcatReplacement(
            int ba,
            BootstrapMethodInfo bootstrapMethodInfo,
            ResolvedInvokeDynamic resolvedInvokeDynamic,
            Map map1,
            VerifierType[] verifierTypes,
            ConstantPool constantPool1,
            List list1
    ) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList();
        String string = resolvedInvokeDynamic.getDescriptor();
        List list2 = ConstantPoolEntry.getParameterTypes(string);
        int bb = list2.size();
        List list3 = bootstrapMethodInfo.getRecipeParts();
        int bc = list3.size();
        int bd = bb + bc;
        ConcatRecipePart concatRecipePart = (ConcatRecipePart) list3.get(0);
        StringBuilder stringBuilder = new StringBuilder();
        byte be = 1;
        if (bc == 1 && concatRecipePart.getTokenIndex() == bd - 1) {
            stringBuilder.append(string.substring(1, string.indexOf(41)));
            stringBuilder.append("Ljava/lang/String;");
            ResolvedStringConstant resolvedStringConstant = concatRecipePart.getStringConstant();
            if (resolvedStringConstant != null) {
                concatRecipePart.clearStringConstant();
            } else {
                resolvedStringConstant = constantPool1.addStringConstant(concatRecipePart.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        } else if (bc == 1 && concatRecipePart.getTokenIndex() == bd - 2) {
            be = 2;
            String string2 = null;

            for (int i = 0; i < list2.size(); i++) {
                if (i == list2.size() - 1) {
                    stringBuilder.append("Ljava/lang/String;");
                    string2 = (String) list2.get(i);
                }

                stringBuilder.append((String) list2.get(i));
            }

            ResolvedStringConstant resolvedStringConstant2 = concatRecipePart.getStringConstant();
            if (resolvedStringConstant2 != null) {
                concatRecipePart.clearStringConstant();
            } else {
                resolvedStringConstant2 = constantPool1.addStringConstant(concatRecipePart.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant2));
            if (VerifierType.isWideDescriptor(string2)) {
                arrayList.add(SimpleInstruction.forOpcode(91));
                arrayList.add(SimpleInstruction.forOpcode(87));
            } else {
                arrayList.add(SimpleInstruction.forOpcode(95));
            }
        } else if (bc == 2 && concatRecipePart.getTokenIndex() == bd - 3 && ((ConcatRecipePart) list3.get(1)).getTokenIndex() == bd - 1) {
            be = 2;
            String string1 = null;

            for (int i = 0; i < list2.size(); i++) {
                if (i == list2.size() - 1) {
                    stringBuilder.append("Ljava/lang/String;");
                    string1 = (String) list2.get(i);
                }

                stringBuilder.append((String) list2.get(i));
            }

            stringBuilder.append("Ljava/lang/String;");
            ResolvedStringConstant resolvedStringConstant1 = concatRecipePart.getStringConstant();
            if (resolvedStringConstant1 != null) {
                concatRecipePart.clearStringConstant();
            } else {
                resolvedStringConstant1 = constantPool1.addStringConstant(concatRecipePart.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
            if (VerifierType.isWideDescriptor(string1)) {
                arrayList.add(SimpleInstruction.forOpcode(91));
                arrayList.add(SimpleInstruction.forOpcode(87));
            } else {
                arrayList.add(SimpleInstruction.forOpcode(95));
            }

            ConcatRecipePart concatRecipePart1 = (ConcatRecipePart) list3.get(1);
            ResolvedStringConstant resolvedStringConstant5 = concatRecipePart1.getStringConstant();
            if (resolvedStringConstant5 != null) {
                concatRecipePart1.clearStringConstant();
            } else {
                resolvedStringConstant5 = constantPool1.addStringConstant(concatRecipePart1.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant5));
        } else if (bc == 2
                && concatRecipePart.getTokenIndex() == bd - 4
                && ((ConcatRecipePart) list3.get(1)).getTokenIndex() == bd - 2
                && !VerifierType.isWideDescriptor((String) list2.get(list2.size() - 1))
                && !VerifierType.isWideDescriptor((String) list2.get(list2.size() - 2))) {
            be = 2;

            for (int i = 0; i < list2.size(); i++) {
                if (i == list2.size() - 1) {
                    stringBuilder.append("Ljava/lang/String;");
                    String string4 = (String) list2.get(i);
                } else if (i == list2.size() - 2) {
                    stringBuilder.append("Ljava/lang/String;");
                    String string5 = (String) list2.get(i);
                }

                stringBuilder.append((String) list2.get(i));
            }

            ResolvedStringConstant resolvedStringConstant4 = concatRecipePart.getStringConstant();
            if (resolvedStringConstant4 != null) {
                concatRecipePart.clearStringConstant();
            } else {
                resolvedStringConstant4 = constantPool1.addStringConstant(concatRecipePart.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant4));
            arrayList.add(SimpleInstruction.forOpcode(91));
            arrayList.add(SimpleInstruction.forOpcode(87));
            ConcatRecipePart concatRecipePart3 = (ConcatRecipePart) list3.get(1);
            ResolvedStringConstant resolvedStringConstant7 = concatRecipePart3.getStringConstant();
            if (resolvedStringConstant7 != null) {
                concatRecipePart3.clearStringConstant();
            } else {
                resolvedStringConstant7 = constantPool1.addStringConstant(concatRecipePart3.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant7));
            arrayList.add(SimpleInstruction.forOpcode(95));
        } else if (bc == 3
                && concatRecipePart.getTokenIndex() == bd - 5
                && ((ConcatRecipePart) list3.get(1)).getTokenIndex() == bd - 3
                && ((ConcatRecipePart) list3.get(2)).getTokenIndex() == bd - 1
                && !VerifierType.isWideDescriptor((String) list2.get(list2.size() - 1))
                && !VerifierType.isWideDescriptor((String) list2.get(list2.size() - 2))) {
            be = 2;

            for (int i = 0; i < list2.size(); i++) {
                if (i == list2.size() - 1) {
                    stringBuilder.append("Ljava/lang/String;");
                    String string6 = (String) list2.get(i);
                } else if (i == list2.size() - 2) {
                    stringBuilder.append("Ljava/lang/String;");
                    String string3 = (String) list2.get(i);
                }

                stringBuilder.append((String) list2.get(i));
            }

            stringBuilder.append("Ljava/lang/String;");
            ResolvedStringConstant resolvedStringConstant3 = concatRecipePart.getStringConstant();
            if (resolvedStringConstant3 != null) {
                concatRecipePart.clearStringConstant();
            } else {
                resolvedStringConstant3 = constantPool1.addStringConstant(concatRecipePart.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant3));
            arrayList.add(SimpleInstruction.forOpcode(91));
            arrayList.add(SimpleInstruction.forOpcode(87));
            ConcatRecipePart concatRecipePart2 = (ConcatRecipePart) list3.get(1);
            ResolvedStringConstant resolvedStringConstant6 = concatRecipePart2.getStringConstant();
            if (resolvedStringConstant6 != null) {
                concatRecipePart2.clearStringConstant();
            } else {
                resolvedStringConstant6 = constantPool1.addStringConstant(concatRecipePart2.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant6));
            arrayList.add(SimpleInstruction.forOpcode(95));
            ConcatRecipePart concatRecipePart5 = (ConcatRecipePart) list3.get(2);
            ResolvedStringConstant resolvedStringConstant8 = concatRecipePart5.getStringConstant();
            if (resolvedStringConstant8 != null) {
                concatRecipePart5.clearStringConstant();
            } else {
                resolvedStringConstant8 = constantPool1.addStringConstant(concatRecipePart5.getText(), list1);
            }

            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant8));
        } else {
            int maxLocals = this.getMaxLocals();
            int bg = maxLocals;
            int bh = verifierTypes != null ? verifierTypes.length - 1 : -1;
            boolean bl = concatRecipePart.getTokenIndex() > 0;

            for (int i = 0; i < bb - (bl ? 1 : 0); i++) {
                int bs = bh;
                bh += -1;
                VerifierType verifierType = verifierTypes[bs];
                if (verifierType.isIntLike()) {
                    arrayList.add(Instruction.createIntStore(bg++, this.localVariableList, 1));
                } else if (verifierType == VerifierType.FLOAT) {
                    arrayList.add(Instruction.createFloatStore(bg++, this.localVariableList, 1));
                } else if (verifierType == VerifierType.DOUBLE) {
                    arrayList.add(Instruction.createDoubleStore(bg++, this.localVariableList, 1));
                    bg++;
                    be = 2;
                } else if (verifierType == VerifierType.LONG) {
                    arrayList.add(Instruction.createLongStore(bg++, this.localVariableList, 1));
                    bg++;
                    be = 2;
                } else if (verifierType.isArray() || verifierType.isClassType() || verifierType.isNull()) {
                    arrayList.add(Instruction.createObjectStore(bg++, this.localVariableList, 1));
                }
            }

            bh++;
            int br = 0;
            ConcatRecipePart concatRecipePart4 = (ConcatRecipePart) list3.get(0);
            int bj = bg;
            int bk = 0;
            if (bl) {
                bk++;
                stringBuilder.append((String) list2.get(0));
            }

            for (int i = bl ? 1 : 0; i < bd; i++) {
                if (concatRecipePart4 != null && concatRecipePart4.getTokenIndex() == i) {
                    stringBuilder.append("Ljava/lang/String;");
                    ResolvedStringConstant resolvedStringConstant9 = concatRecipePart4.getStringConstant();
                    if (resolvedStringConstant9 != null) {
                        concatRecipePart4.clearStringConstant();
                    } else {
                        resolvedStringConstant9 = constantPool1.addStringConstant(concatRecipePart4.getText(), list1);
                    }

                    arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant9));
                    if (++br < list3.size()) {
                        concatRecipePart4 = (ConcatRecipePart) list3.get(br);
                    } else {
                        concatRecipePart4 = null;
                    }
                } else {
                    stringBuilder.append((String) list2.get(bk++));
                    VerifierType verifierType1 = verifierTypes[bh];
                    bj += -1;
                    if (verifierType1.isIntLike()) {
                        arrayList.add(Instruction.createIntLoad(bj, this.localVariableList, 1));
                    } else if (verifierType1 == VerifierType.FLOAT) {
                        arrayList.add(Instruction.createFloatLoad(bj, this.localVariableList, 1));
                    } else if (verifierType1 == VerifierType.DOUBLE) {
                        bj += -1;
                        arrayList.add(Instruction.createDoubleLoad(bj, this.localVariableList, 1));
                    } else if (verifierType1 == VerifierType.LONG) {
                        bj += -1;
                        arrayList.add(Instruction.createLongLoad(bj, this.localVariableList, 1));
                    } else if (verifierType1.isArray() || verifierType1.isClassType() || verifierType1.isNull()) {
                        arrayList.add(Instruction.createObjectLoad(bj, this.localVariableList, 1));
                    }

                    bh++;
                }
            }

            if (bg != maxLocals) {
                this.setMaxLocals(bg);
            }
        }

        if (!map1.containsKey(resolvedInvokeDynamic)) {
            map1.put(resolvedInvokeDynamic, stringBuilder.toString());
        }

        return new CodeInsertion(arrayList, ba - 1, 0, be);
    }

    public void appendExceptionUnwrap(
            List list1,
            Object object,
            Object object1,
            ReferenceObfuscator referenceObfuscator,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        list1.add(object);
        list1.add(new GotoInstruction(labelInstruction));
        list1.add(object1);
        ResolvedMethodRefConstant resolvedMethodRefConstant = referenceObfuscator.getTargetExceptionRef(constantPool1, list2, classMemberLookup1, classResolver1);
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(SimpleInstruction.forOpcode(191));
        list1.add(labelInstruction);
    }

    public String findUncoveredExceptionType(
            int ba,
            int bb,
            ClassHierarchyQuery classHierarchyQuery,
            List list1,
            List list2,
            List list3,
            List list4,
            boolean bl,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        Iterator iterator = list4.iterator();

        while (iterator.hasNext()) {
            ExceptionTableEntry exceptionTableEntry1 = (ExceptionTableEntry) iterator.next();
            if (exceptionTableEntry1.overlapsPartially(ba, bb) || ba < exceptionTableEntry1.getOffset(0) && bb == exceptionTableEntry1.getOffset(1)) {
                return null;
            }
        }

        iterator = list4.iterator();

        label114:
        while (iterator.hasNext()) {
            ExceptionTableEntry exceptionTableEntry3 = (ExceptionTableEntry) iterator.next();
            ResolvedClassConstant resolvedClassConstant = exceptionTableEntry3.getCatchType();
            if (resolvedClassConstant != null) {
                Iterator iterator1 = list4.iterator();

                while (iterator1.hasNext()) {
                    ExceptionTableEntry exceptionTableEntry2 = (ExceptionTableEntry) iterator1.next();
                    String string = exceptionTableEntry3.getCatchTypeName();
                    String string1 = exceptionTableEntry2.getCatchTypeName();
                    if (exceptionTableEntry2.isWithinRange(ba, bb) && this.areRelatedTypes(string, string1, ignoreMissingReferencesSpec1, classHierarchyQuery)) {
                        continue label114;
                    }
                }

                return resolvedClassConstant.getClassName();
            }
        }

        if (list3 != null) {
            iterator = list3.iterator();

            label97:
            while (iterator.hasNext()) {
                ResolvedClassConstant resolvedClassConstant1 = (ResolvedClassConstant) iterator.next();
                String string3 = resolvedClassConstant1.getClassName();
                Iterator iterator3 = list4.iterator();

                while (iterator3.hasNext()) {
                    ExceptionTableEntry exceptionTableEntry5 = (ExceptionTableEntry) iterator3.next();
                    if (exceptionTableEntry5.isWithinRange(ba, bb)) {
                        String string6 = exceptionTableEntry5.getCatchTypeName();
                        if (this.areRelatedTypes(string3, string6, ignoreMissingReferencesSpec1, classHierarchyQuery)) {
                            continue label97;
                        }
                    }
                }

                return resolvedClassConstant1.getClassName();
            }
        }

        if (list2 != null) {
            iterator = list2.iterator();

            label83:
            while (iterator.hasNext()) {
                ResolvedClassConstant resolvedClassConstant2 = (ResolvedClassConstant) iterator.next();
                String string4 = resolvedClassConstant2.getClassName();
                Iterator iterator4 = list4.iterator();

                while (iterator4.hasNext()) {
                    ExceptionTableEntry exceptionTableEntry6 = (ExceptionTableEntry) iterator4.next();
                    if (exceptionTableEntry6.isWithinRange(ba, bb)) {
                        String string7 = exceptionTableEntry6.getCatchTypeName();
                        if (this.areRelatedTypes(string4, string7, ignoreMissingReferencesSpec1, classHierarchyQuery)) {
                            continue label83;
                        }
                    }
                }

                return resolvedClassConstant2.getClassName();
            }
        }

        if (bl && list1 != null) {
            iterator = list1.iterator();

            label68:
            while (iterator.hasNext()) {
                String string2 = (String) iterator.next();
                Iterator iterator2 = list4.iterator();

                while (iterator2.hasNext()) {
                    ExceptionTableEntry exceptionTableEntry4 = (ExceptionTableEntry) iterator2.next();
                    if (exceptionTableEntry4.isWithinRange(ba, bb)) {
                        String string5 = exceptionTableEntry4.getCatchTypeName();
                        if (this.areRelatedTypes(string2, string5, ignoreMissingReferencesSpec1, classHierarchyQuery)) {
                            continue label68;
                        }
                    }
                }

                return string2;
            }
        }

        return null;
    }

    public void insertAttributeLabels(ListMultimap listMultimap, PrintWriter printWriter) throws ZkmProcessingException {
        int keyCount = listMultimap.getKeyCount();
        if (keyCount != 0) {
            Integer[] integers = new Integer[keyCount];
            ArrayList arrayList = new ArrayList(keyCount + 1);
            LabelInstruction labelInstruction = null;
            int bb = 0;
            Enumeration enumeration = listMultimap.keys();

            while (enumeration.hasMoreElements()) {
                integers[bb++] = (Integer) enumeration.nextElement();
            }

            Arrays.sort(integers);

            for (int i = 0; i < keyCount; i++) {
                Integer integer = integers[i];
                int bc = 0;
                List list1 = listMultimap.getValues(integer);
                if (integer == 0) {
                    Collections.sort(list1, new LocalVariableEntryComparator());
                    if (list1.get(0) instanceof LocalVariableEntry) {
                        list1 = new ArrayList(list1);
                        labelInstruction = new LabelInstruction();
                        arrayList.add(labelInstruction);
                        Iterator iterator = list1.iterator();

                        for (LabelTargetHolder labelTargetHolder = (LabelTargetHolder) iterator.next();
                             labelTargetHolder instanceof LocalVariableEntry;
                             labelTargetHolder = (LabelTargetHolder) iterator.next()
                        ) {
                            iterator.remove();
                            labelTargetHolder.bindLabel(0, labelInstruction);
                            if (!iterator.hasNext()) {
                                break;
                            }
                        }
                    }

                    if (list1.size() == 0) {
                        continue;
                    }
                }

                Iterator iterator2 = list1.iterator();

                while (iterator2.hasNext()) {
                    LabelTargetHolder labelTargetHolder3 = (LabelTargetHolder) iterator2.next();
                    bc |= labelTargetHolder3.getUsageFlag().getBit();
                }

                LabelInstruction labelInstruction1 = new LabelInstruction(integer, bc);
                arrayList.add(labelInstruction1);
                Iterator iterator3 = list1.iterator();

                while (iterator3.hasNext()) {
                    LabelTargetHolder labelTargetHolder1 = (LabelTargetHolder) iterator3.next();
                    labelTargetHolder1.bindLabel(integer, labelInstruction1);
                }
            }

            int bf = arrayList.size();
            int bg = this.instructions.size();
            ArrayList arrayList1 = new ArrayList(bg + bf);
            int bh = 0;
            int bi = 0;
            int bj = 0;
            if (labelInstruction != null) {
                arrayList1.add(labelInstruction);
                bh++;
            }

            int bk;
            if (bf > bh) {
                bk = ((LabelInstruction) arrayList.get(bh)).getOffset();
            } else {
                bk = -1;
            }

            for (int i = 0; i < bg; i++) {
                if (bh < bf && bi > bk) {
                    List list2 = listMultimap.getValues(integers[labelInstruction == null ? bh : bh - 1]);
                    Iterator iterator1 = list2.iterator();

                    while (iterator1.hasNext()) {
                        LabelTargetHolder labelTargetHolder2 = (LabelTargetHolder) iterator1.next();
                        if (!(labelTargetHolder2 instanceof LineNumberEntry)) {
                            Instruction instruction1 = (Instruction) this.instructions.get(i - 1);
                            StringBuilder stringBuilder = new StringBuilder();
                            Integer integer2 = 11169;
                            Integer integer1 = -167;
                            throw new ZkmProcessingException(
                                    stringBuilder.append(labelTargetHolder2.getHolderTypeName(12567, integer1, integer2))
                                            .append(" in method '")
                                            .append(this.getMethod().toDisplayString())
                                            .append("' in class '")
                                            .append(this.getLocationName())
                                            .append("' points into middle of an instruction : ")
                                            .append(instruction1.getMnemonic())
                                            .append(" : ")
                                            .append(bj)
                                            .append(" : ")
                                            .append(bk)
                                            .toString()
                            );
                        }

                        printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid LineNumberTable Attribute" + " (C). Can be ignored.");
                    }

                    boolean bl = false;

                    do {
                        if (!bl) {
                            List list3 = listMultimap.getValues(integerCache.valueOf(bi));
                            if (list3 != null) {
                                for (int j = 0; j < list3.size(); j++) {
                                    if (list3.get(j) instanceof LineNumberEntry) {
                                        bl = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (!bl) {
                            arrayList1.add(arrayList.get(bh++));
                        } else {
                            bh++;
                            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid LineNumberTable Attribute" + " (D). Could not be corrected!");
                        }

                        if (bh < keyCount) {
                            bk = ((LabelInstruction) arrayList.get(bh)).getOffset();
                        }
                    } while (bh < keyCount && bi > bk);
                }

                if (bh < bf && bi == bk) {
                    arrayList1.add(arrayList.get(bh++));
                    if (bh < bf) {
                        bk = ((LabelInstruction) arrayList.get(bh)).getOffset();
                    }
                }

                Instruction instruction2 = (Instruction) this.instructions.get(i);
                arrayList1.add(instruction2);
                bj = bi;
                bi += instruction2.getLength();
            }

            if (bh < bf && bi == bk) {
                arrayList1.add(arrayList.get(bh));
            }

            this.instructions = arrayList1;
        }
    }

    public void applyCodeInsertions(List list1, String string, boolean bl, boolean bl1) throws ZkmProcessingException {
        int ba = this.instructions.size();
        int bb = -1;
        if (bl1) {
            bb = this.findMarkerLabelIndex();
        }

        int bc = list1.size();
        if (bc > 0) {
            Collections.sort(list1);
            ArrayList arrayList = new ArrayList(this.instructions.size() + bc * 3);
            int bd = 0;
            int be = 0;
            Instruction instruction1 = ba > 0 ? (Instruction) this.instructions.get(0) : null;
            int bf = 0;

            for (int i = 0; i < bc; i++) {
                CodeInsertion codeInsertion = (CodeInsertion) list1.get(i);
                int extraStackSize = codeInsertion.getExtraStackSize();
                if (extraStackSize > bd) {
                    bd = extraStackSize;
                }

                boolean bl2 = false;
                int position = codeInsertion.getPosition();
                if (bb > -1 && (position == -1 || position == 0)) {
                    position = bb;
                }

                if (position == -1) {
                    if (bf == 0 && instruction1 != null && instruction1.isLabel() && ((LabelInstruction) instruction1).isFixed()) {
                        LabelInstruction labelInstruction = (LabelInstruction) this.instructions.get(0);
                        arrayList.add(labelInstruction);
                        bf++;
                        bl2 = true;
                    }
                } else {
                    for (int j = be; j <= position; j++) {
                        arrayList.add(this.instructions.get(j));
                    }
                }

                List list2 = codeInsertion.getInstructions();

                for (int j = 0; j < list2.size(); j++) {
                    Instruction instruction2 = (Instruction) list2.get(j);
                    arrayList.add(instruction2);
                    if (instruction2.isStringConstantLoad()) {
                        this.stringConstantCount++;
                    }

                    if (instruction2.loadsConstantInteger()) {
                        this.intConstantCount++;
                    }

                    if (instruction2.isLongConstantLoad()) {
                        this.longConstantCount++;
                    }
                }

                be = position + 1 + codeInsertion.getReplacedCount() + (bl2 ? bf : 0);
            }

            for (int i = be; i < ba; i++) {
                arrayList.add(this.instructions.get(i));
            }

            this.instructions = arrayList;
            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
            int bn = codeAttributeBody.getMaxStack() + bd;
            codeAttributeBody.setMaxStack(bn);
            int codeLength = this.getCodeLength();
            if (!bl) {
                this.recomputeOffsets();
                this.setModified(true);
                if (this.getCodeLength() > 65535) {
                    throw new ZkmProcessingException(
                            string
                                    + " : Bytecode length greater than "
                                    + 65535
                                    + " in "
                                    + this.getQualifiedMethodName()
                                    + " in file '"
                                    + this.getLocationName()
                                    + "' (4). (original="
                                    + codeLength
                                    + ", new="
                                    + this.getCodeLength()
                                    + ") : '"
                                    + string
                                    + "'"
                    );
                }
            }
        }
    }

    public boolean hasConditionalBranches() {
        Iterator iterator = this.instructions.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            if (instruction1.isJump()) {
                switch (instruction1.getOpcode()) {
                    case 167:
                    case 168:
                    case 200:
                    case 201:
                        break;
                    default:
                        return true;
                }
            }
        }

        return false;
    }

    public void collectLongEncryptionCandidates(
            MultiMapTable multiMapTable,
            MethodInfo methodInfo1,
            Set set1,
            LongEncryptionExclusionHandler longEncryptionExclusionHandler,
            List list1,
            ConstantPool constantPool1
    ) {
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        boolean bl = false;
        int ba = this.instructions.size();
        int bb = 0;

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isLabel() && ((LabelInstruction) instruction1).isMarker()) {
                bb = i;
                break;
            }
        }

        for (int i = bb; i < ba; i++) {
            Instruction instruction3 = (Instruction) this.instructions.get(i);
            if (instruction3.isLongConstantLoad() || instruction3.isLongConstant()) {
                boolean bl1 = false;
                if (i < ba - 1) {
                    Instruction instruction2 = (Instruction) this.instructions.get(i + 1);
                    if (instruction2.isFieldStore()) {
                        AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) ((ResolvedFieldRef) ((ConstantRefInstruction) instruction2).getConstantPoolEntry())
                                .getResolvedMember();
                        if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
                            bl1 = true;
                            label62:
                            if (longEncryptionExclusionHandler == null || !longEncryptionExclusionHandler.isFieldExcluded((FieldInfo) abstractFieldInfo)) {
                                int opcode = instruction3.getOpcode();
                                long bg;
                                if (opcode != 9) {
                                    if (opcode != 10) {
                                        instruction3.collectLongConstant(multiMapTable, set1, methodInfo1, i);
                                        break label62;
                                    }

                                    bg = -1L;
                                } else {
                                    bg = -1L;
                                }

                                long be = bg;
                                if (opcode == 9) {
                                    if (!HiddenOptionFlags.ENCRYPT_TRIVIAL_LONGS) {
                                        continue;
                                    }

                                    be = 0L;
                                } else if (opcode == 10) {
                                    if (!HiddenOptionFlags.ENCRYPT_TRIVIAL_LONGS) {
                                        continue;
                                    }

                                    be = 1L;
                                }

                                ConstantLong constantLong = constantPool1.getOrAddLongConstant(be, list1, false, true);
                                ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(20, constantLong);
                                this.instructions.set(i, constantRefInstruction);
                                this.longConstantCount++;
                                multiMapTable.addEntry(programClass1, methodInfo1, constantLong, new RankedValue(i, constantRefInstruction));
                                bl = true;
                            }
                        }
                    }
                }

                if (!bl1 && !instruction3.isLongConstant()) {
                    Integer integer = i;
                    instruction3.collectLongConstant(multiMapTable, set1, methodInfo1, integer);
                }
            }
        }

        if (bl) {
            this.setModified(true);
            this.recomputeOffsets();
        }
    }

    public ExceptionTableEntry[] copyExceptionTable() {
        ExceptionTableEntry[] exceptionTableEntrys = ((CodeAttributeBody) this.getParent()).getExceptionTable();
        ExceptionTableEntry[] exceptionTableEntrys1 = new ExceptionTableEntry[exceptionTableEntrys.length];
        System.arraycopy(exceptionTableEntrys, 0, exceptionTableEntrys1, 0, exceptionTableEntrys.length);
        return exceptionTableEntrys1;
    }

    public void markReachableMethods(Set set1, Set set2) throws ZkmProcessingException {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            switch (instruction1.getOpcode()) {
                case 182:
                case 183:
                case 184:
                case 185:
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) resolvedMethodRef.getResolvedMember();
                    if (abstractMethodInfo1 != null) {
                        ClassFileBase classFileBase1 = abstractMethodInfo1.getOwningClass();
                        if (classFileBase1.isVersionedVariant()) {
                            classFileBase1 = classFileBase1.getBaseVersionClass();
                        }

                        if (set2.contains(classFileBase1)) {
                            abstractMethodInfo1.collectReachableMethods(set1, set2);
                        }
                    }
                    break;
                case 186:
                    ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    AbstractMethodInfo abstractMethodInfo = resolvedInvokeDynamic.getTargetMethod();
                    if (abstractMethodInfo != null) {
                        ClassFileBase classFileBase = abstractMethodInfo.getOwningClass();
                        if (classFileBase.isVersionedVariant()) {
                            classFileBase = classFileBase.getBaseVersionClass();
                        }

                        if (set2.contains(classFileBase)) {
                            abstractMethodInfo.collectReachableMethods(set1, set2);
                        }
                    }
            }
        }
    }

    public void addFakeExceptionHandlers(
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            List list1,
            List list2,
            List list3,
            List list4,
            List list5,
            List list6,
            List list7,
            boolean bl,
            boolean bl1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer;
        try {
            methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
        } catch (StackOverflowError stackOverflowError) {
            throw new CorruptHierarchyException("StackOverflowError trapped in '" + this.getQualifiedMethodName() + "' (5)", stackOverflowError);
        }

        int ba = this.findMarkerLabelIndex();
        int[] bb = this.computeInstructionOffsets();
        List list8 = methodFlowAnalyzer.getNaturalLoops();
        HashMap hashMap = ZkmUtils.createHashMap();

        for (int i = 0; i < list8.size(); i++) {
            NaturalLoop naturalLoop = (NaturalLoop) list8.get(i);
            BasicBlock basicBlock = naturalLoop.getHeader();
            BasicBlock basicBlock1 = naturalLoop.getFirstLatchBlock();
            BasicBlock basicBlock2 = naturalLoop.getFollowBlock();
            BasicBlock basicBlock3 = naturalLoop.getExitBranchTarget();
            hashMap.put(basicBlock, basicBlock);
            hashMap.put(basicBlock1, basicBlock1);
            if (basicBlock3 != null
                    && basicBlock2 != null
                    && basicBlock.getEndIndex() == basicBlock3.getStartIndex() - 1
                    && basicBlock3.getEndIndex() < this.instructions.size() - 1) {
                int startIndex = basicBlock.getStartIndex();
                int endIndex = basicBlock3.getEndIndex();
                MutableInt mutableInt = new MutableInt(endIndex);
                int bf = this.findProtectableRangeStart(startIndex, endIndex, mutableInt, ba, bl1, methodFlowAnalyzer);
                if (bf > -1) {
                    int value = mutableInt.getValue();
                    if (bf <= basicBlock.getEndIndex() && value >= basicBlock3.getStartIndex()) {
                        String string = this.findUncoveredExceptionType(
                                bb[bf], bb[value + 1], classHierarchyQuery, list1, list2, list3, list4, bl, ignoreMissingReferencesSpec1
                        );
                        if (string != null) {
                            boolean bl3 = basicBlock3.lastInstructionFallsThrough(new NonNullList(this.instructions));
                            List list10 = list7;
                            List list9 = list6;
                            Boolean boolean1 = bl3;
                            String string1 = string;
                            Integer integer = value;
                            this.addRethrowingHandler(bf, integer, string1, boolean1, list9, list10, list5);
                        }
                    }
                }
            }
        }

        List list13 = methodFlowAnalyzer.getBlocksCopy();
        Collections.sort(list13);
        Iterator iterator = list13.iterator();

        while (iterator.hasNext()) {
            BasicBlock basicBlock4 = (BasicBlock) iterator.next();
            if (!hashMap.containsKey(basicBlock4)) {
                List list14 = basicBlock4.getSuccessorsCopy();
                if (list14 != null && list14.size() > 1) {
                    Collections.sort(list14);
                    BasicBlock basicBlock5 = (BasicBlock) list14.get(0);
                    if (basicBlock4.getEndIndex() == basicBlock5.getStartIndex() - 1 && basicBlock5.getEndIndex() < this.instructions.size() - 1) {
                        int bh = basicBlock4.getStartIndex();
                        int bi = basicBlock5.getEndIndex();
                        MutableInt mutableInt1 = new MutableInt(bi);
                        int bj = this.findProtectableRangeStart(bh, bi, mutableInt1, ba, bl1, methodFlowAnalyzer);
                        if (bj > -1) {
                            int bk = mutableInt1.getValue();
                            if (bj <= basicBlock4.getEndIndex() && bk >= basicBlock5.getStartIndex()) {
                                String string3 = this.findUncoveredExceptionType(
                                        bb[bj], bb[bk + 1], classHierarchyQuery, list1, list2, list3, list4, bl, ignoreMissingReferencesSpec1
                                );
                                if (string3 != null) {
                                    boolean bl2 = basicBlock5.lastInstructionFallsThrough(new NonNullList(this.instructions));
                                    List list12 = list7;
                                    List list11 = list6;
                                    Boolean boolean2 = bl2;
                                    String string2 = string3;
                                    Integer integer1 = bk;
                                    this.addRethrowingHandler(bj, integer1, string2, boolean2, list11, list12, list5);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (methodFlowAnalyzer != null) {
            methodFlowAnalyzer.release();
        }
    }

    public void insertOpaquePredicateInit(
            OpaquePredicateField opaquePredicateField,
            NestedMultiMap nestedMultiMap,
            List list1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            Random random1
    ) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer;
        try {
            methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classMemberLookup1);
        } catch (StackAnalysisException stackAnalysisException) {
            throw new ZkmProcessingException("Corrupt bytecode in '" + this.getDisplayLocationName() + "' <clinit> : " + stackAnalysisException.getMessage());
        }

        MutableInt mutableInt = new MutableInt();
        SyncIndexedSet syncIndexedSet;
        if (staticInitCalleeAnalyzer1.isInitDependencyLeaf(this.getOwningClass())) {
            MutableInt mutableInt1 = mutableInt;
            syncIndexedSet = this.findPrologueSyncPoints(methodFlowAnalyzer, Integer.MAX_VALUE, mutableInt1);
        } else {
            syncIndexedSet = new SyncIndexedSet(13);
        }

        ConstantPool constantPool1 = (ConstantPool) this.getConstantPool();
        ArrayList arrayList = new ArrayList();
        LabelInstruction labelInstruction = null;
        if (syncIndexedSet.size() <= 1) {
            ConstantRefInstruction constantRefInstruction;
            boolean bl;
            if (opaquePredicateField.isNegatedGetterPresent() && random1.nextBoolean()) {
                bl = true;
                constantRefInstruction = opaquePredicateField.createNegatedGetterCall(constantPool1, list1);
            } else {
                bl = false;
                constantRefInstruction = opaquePredicateField.createReadInstruction(constantPool1, list1);
            }

            this.recordMemberReference(constantRefInstruction, nestedMultiMap);
            arrayList.add(constantRefInstruction);
            labelInstruction = new LabelInstruction(1);
            BranchInstruction branchInstruction;
            if (!bl) {
                branchInstruction = opaquePredicateField.createBranchIfSet(labelInstruction);
            } else {
                branchInstruction = opaquePredicateField.createBranchIfUnset(labelInstruction);
            }

            arrayList.add(branchInstruction);
            if (opaquePredicateField.isReferenceType()) {
                arrayList.addAll(
                        FlowObfuscationManager.buildFieldInitializer(
                                opaquePredicateField, list1, constantPool1, true, classMemberLookup1, commonSuperTypeResolver1.getClassResolver(), random1
                        )
                );
            } else if (opaquePredicateField.isBoolean()) {
                arrayList.add(SimpleInstruction.forOpcode(4));
            } else {
                int ba = random1.nextInt(126) + 1;
                arrayList.add(Instruction.createIntPush(ba));
            }
        } else if (opaquePredicateField.isReferenceType()) {
            if (opaquePredicateField.isNonZeroValue()) {
                arrayList.addAll(
                        FlowObfuscationManager.buildFieldInitializer(
                                opaquePredicateField, list1, constantPool1, true, classMemberLookup1, commonSuperTypeResolver1.getClassResolver(), random1
                        )
                );
            } else {
                arrayList.add(SimpleInstruction.forOpcode(1));
            }
        } else if (opaquePredicateField.isBoolean()) {
            if (opaquePredicateField.isNonZeroValue()) {
                arrayList.add(SimpleInstruction.forOpcode(4));
            } else {
                arrayList.add(SimpleInstruction.forOpcode(3));
            }
        } else if (opaquePredicateField.isNonZeroValue()) {
            int bb = random1.nextInt(126) + 1;
            arrayList.add(Instruction.createIntPush(bb));
        } else {
            arrayList.add(SimpleInstruction.forOpcode(3));
        }

        ConstantRefInstruction constantRefInstruction1 = opaquePredicateField.createWriteInstruction(constantPool1, list1);
        this.recordMemberReference(constantRefInstruction1, nestedMultiMap);
        if (syncIndexedSet.size() <= 1) {
            arrayList.add(constantRefInstruction1);
            arrayList.add(labelInstruction);
            constantRefInstruction1 = null;
        }

        ArrayList arrayList1 = new ArrayList();
        int value = mutableInt.getValue();
        Random random2 = random1;
        SyncIndexedSet syncIndexedSet1 = syncIndexedSet;
        Integer integer = value;
        this.addPredicateInsertions(
                arrayList, constantRefInstruction1, arrayList1, methodFlowAnalyzer, Integer.MAX_VALUE, (CodeInsertion) null, integer, syncIndexedSet1, random2
        );
        this.applyCodeInsertions(arrayList1, "Flow Obfuscation", false, true);
    }

    public void setMaxLocals(int ba) throws ZkmProcessingException {
        ((CodeAttributeBody) this.getParent()).setMaxLocals(ba);
    }

    public int planFlowObfuscationJumps(
            ListMultimap listMultimap,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            ClassHierarchyQuery classHierarchyQuery,
            int ba,
            boolean bl,
            boolean bl1,
            Map map1,
            Random random1
    ) throws ZkmException, IOException {
        String string = "analyzing the control flow of method '"
                + this.getMethod().buildDeclaration()
                + "' in class "
                + this.getLocationName()
                + "' : '"
                + this.getClassName()
                + "' (B)";
        List list1 = listMultimap.createDefaultList();
        int bb = 0;

        MethodFlowAnalyzer methodFlowAnalyzer;
        try {
            methodFlowAnalyzer = this.getCachedFlowAnalyzer(map1, commonSuperTypeResolver1, classHierarchyQuery);
        } catch (StackOverflowError stackOverflowError) {
            throw new CorruptHierarchyException("StackOverflowError trapped in '" + this.getQualifiedMethodName() + "' (1)", stackOverflowError);
        }

        int bc = this.findMarkerLabelIndex();
        this.minReturnDistance = methodFlowAnalyzer.getShortestReturnPathLength();
        StackFrameState[] stackFrameStates = methodFlowAnalyzer.copyFrameStates();
        Set set1 = methodFlowAnalyzer.getMonitorTypes();
        ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(methodFlowAnalyzer.getBlockCount()));
        HashMap hashMap = ZkmUtils.createHashMap();
        List list2;
        if (ba == 1) {
            list2 = new ArrayList();
        } else {
            list2 = methodFlowAnalyzer.getNaturalLoops();
        }

        for (int i = 0; i < list2.size(); i++) {
            NaturalLoop naturalLoop = (NaturalLoop) list2.get(i);
            if (bc <= -1 || !naturalLoop.containsInstruction(bc)) {
                BasicBlock basicBlock = naturalLoop.getHeader();
                BasicBlock basicBlock1 = naturalLoop.getFirstLatchBlock();
                hashMap.put(basicBlock, basicBlock);
                hashMap.put(basicBlock1, basicBlock1);
                BasicBlock basicBlock2 = naturalLoop.getFollowBlock();
                BasicBlock basicBlock3 = naturalLoop.getExitBranchTarget();
                if (basicBlock3 != null && basicBlock2 != null) {
                    MutableInt mutableInt = new MutableInt(-1);
                    MutableInt mutableInt1 = new MutableInt(-1);
                    ObservableHolder observableHolder = new ObservableHolder();
                    this.collectFlowSplitCandidates(
                            mutableInt,
                            mutableInt1,
                            observableHolder,
                            basicBlock3,
                            basicBlock2,
                            basicBlock1,
                            set1,
                            stackFrameStates,
                            commonSuperTypeResolver1,
                            scriptEnvironment1,
                            bl1,
                            random1,
                            string
                    );
                    if (mutableInt1.getValue() != -1) {
                        boolean bl6 = true;
                        byte bi = 2;
                        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
                        CodeInsertion codeInsertion = new CodeInsertion(labelInstruction, mutableInt1.getValue(), 0, 0);
                        list1.add(codeInsertion);
                        LabeledCodeInsertion labeledCodeInsertion = new LabeledCodeInsertion(false, labelInstruction, mutableInt.getValue(), 0);
                        bb++;
                        list1.add(labeledCodeInsertion);
                    }
                }
            }
        }

        List list3 = null;
        if (ba == 4) {
            list3 = methodFlowAnalyzer.getBlocksCopy();
            Collections.sort(list3);
            Iterator iterator = list3.iterator();

            while (iterator.hasNext()) {
                BasicBlock basicBlock4 = (BasicBlock) iterator.next();
                if (!hashMap.containsKey(basicBlock4) && (bc <= -1 || basicBlock4.getStartIndex() > bc)) {
                    List list4 = basicBlock4.getSuccessorsCopy();
                    if (list4 != null && list4.size() > 1) {
                        MutableInt mutableInt2 = new MutableInt(-1);
                        MutableInt mutableInt4 = new MutableInt(-1);
                        ObservableHolder observableHolder1 = new ObservableHolder();
                        Collections.sort(list4);
                        BasicBlock basicBlock7 = (BasicBlock) list4.get(0);
                        BasicBlock basicBlock9 = (BasicBlock) list4.get(list4.size() - 1);
                        boolean bl2 = true;
                        byte bg = 1;
                        bl2 = true;
                        bg = ((byte) ((1 != 0) ? 1 : 0));
                        this.collectBlockFlowSplitCandidates(
                                mutableInt2,
                                mutableInt4,
                                observableHolder1,
                                basicBlock9,
                                basicBlock7,
                                set1,
                                stackFrameStates,
                                commonSuperTypeResolver1,
                                scriptEnvironment1,
                                bl1,
                                random1,
                                string
                        );
                        if (mutableInt4.getValue() == -1) {
                            bl2 = true;
                            bg = ((byte) ((1 != 0) ? 1 : 0));
                            this.collectBlockFlowSplitCandidates(
                                    mutableInt2,
                                    mutableInt4,
                                    observableHolder1,
                                    basicBlock4,
                                    basicBlock7,
                                    set1,
                                    stackFrameStates,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl1,
                                    random1,
                                    string
                            );
                        }

                        if (mutableInt4.getValue() != -1) {
                            bl2 = true;
                            bg = 2;
                            LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
                            CodeInsertion codeInsertion1 = new CodeInsertion(labelInstruction2, mutableInt4.getValue(), 0, 0);
                            list1.add(codeInsertion1);
                            LabeledCodeInsertion labeledCodeInsertion1 = new LabeledCodeInsertion(false, labelInstruction2, mutableInt2.getValue(), 0);
                            bb++;
                            list1.add(labeledCodeInsertion1);
                        }
                    }
                }
            }
        }

        if (ba == 3 || ba == 4) {
            if (list3 == null) {
                list3 = methodFlowAnalyzer.getBlocksCopy();
                Collections.sort(list3);
            }

            Iterator iterator1 = list3.iterator();

            while (iterator1.hasNext()) {
                BasicBlock basicBlock5 = (BasicBlock) iterator1.next();
                if (!hashMap.containsKey(basicBlock5) && (bc <= -1 || basicBlock5.getStartIndex() > bc)) {
                    List list5 = basicBlock5.getSuccessorsCopy();
                    if (list5 != null && list5.size() > 1) {
                        MutableInt mutableInt3 = new MutableInt(-1);
                        MutableInt mutableInt5 = new MutableInt(-1);
                        ObservableHolder observableHolder2 = new ObservableHolder();
                        Collections.sort(list5);
                        BasicBlock basicBlock8 = (BasicBlock) list5.get(0);
                        BasicBlock basicBlock10 = (BasicBlock) list5.get(list5.size() - 1);
                        boolean bl3 = true;
                        byte bh = 1;
                        bl3 = true;
                        bh = ((byte) ((1 != 0) ? 1 : 0));
                        this.collectBlockFlowSplitCandidates(
                                mutableInt3,
                                mutableInt5,
                                observableHolder2,
                                basicBlock5,
                                basicBlock10,
                                set1,
                                stackFrameStates,
                                commonSuperTypeResolver1,
                                scriptEnvironment1,
                                bl1,
                                random1,
                                string
                        );
                        if (mutableInt5.getValue() == -1) {
                            bl3 = true;
                            bh = ((byte) ((1 != 0) ? 1 : 0));
                            this.collectBlockFlowSplitCandidates(
                                    mutableInt3,
                                    mutableInt5,
                                    observableHolder2,
                                    basicBlock5,
                                    basicBlock8,
                                    set1,
                                    stackFrameStates,
                                    commonSuperTypeResolver1,
                                    scriptEnvironment1,
                                    bl1,
                                    random1,
                                    string
                            );
                        }

                        if (mutableInt5.getValue() != -1) {
                            bl3 = true;
                            bh = 2;
                            LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
                            CodeInsertion codeInsertion2 = new CodeInsertion(labelInstruction3, mutableInt5.getValue(), 0, 0);
                            list1.add(codeInsertion2);
                            LabeledCodeInsertion labeledCodeInsertion3 = new LabeledCodeInsertion(false, labelInstruction3, mutableInt3.getValue(), 0);
                            bb++;
                            list1.add(labeledCodeInsertion3);
                        }
                    }
                }
            }
        }

        list3 = methodFlowAnalyzer.getBlocksCopy();
        Collections.sort(list3);
        int be = this.instructions.size();
        Iterator iterator2 = list3.iterator();

        while (iterator2.hasNext()) {
            BasicBlock basicBlock6 = (BasicBlock) iterator2.next();
            if (bc <= -1 || basicBlock6.getStartIndex() > bc) {
                Instruction instruction1 = (Instruction) this.instructions.get(basicBlock6.getEndIndex());
                int bf = basicBlock6.getEndIndex() + 1;
                if (instruction1.getOpcode() == 167 && basicBlock6.getInstructionCount() > 1 && bf < be) {
                    boolean bl4 = true;
                    boolean bl5 = true;
                    StackFrameState stackFrameState = stackFrameStates[basicBlock6.getEndIndex()];
                    StackFrameState stackFrameState1 = stackFrameStates[bf];
                    LabelInstruction labelInstruction1 = ((BranchInstruction) instruction1).getTargetLabel();
                    if (stackFrameState != null
                            && stackFrameState1 != null
                            && labelInstruction1 != this.instructions.get(bf)
                            && (stackFrameState.isCompatibleWith(commonSuperTypeResolver1, stackFrameState1, set1, bl1, string) || !bl)
                            && stackFrameState.getSubroutineEntryLabel() == stackFrameState1.getSubroutineEntryLabel()) {
                        LabeledCodeInsertion labeledCodeInsertion2 = new LabeledCodeInsertion(true, labelInstruction1, basicBlock6.getEndIndex() - 1, 1);
                        bb++;
                        list1.add(labeledCodeInsertion2);
                    }
                }
            }
        }

        if (bb > 0) {
            listMultimap.putValues(this, list1);
        }

        return bb;
    }

    public int getMaxLocals() {
        return ((CodeAttributeBody) this.getParent()).getMaxLocals();
    }

    public HashSet collectLabelsAt(int[] ba) {
        HashSet hashSet = ZkmUtils.createHashSet();

        for (int i = 0; i < ba.length; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(ba[i]);
            if (instruction1 instanceof LabelInstruction) {
                hashSet.add((LabelInstruction) instruction1);
            }
        }

        return hashSet;
    }

    public void collectProgramFieldRef(ResolvedFieldRef resolvedFieldRef, Set set1, Set set2) {
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(resolvedFieldRef.getReferencedClassName());
        if (programClass1 != null) {
            set2.add(this.selectMatchingProgramClass(programClass1));
        }

        AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
        if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
            set1.add((FieldInfo) abstractFieldInfo);
            ProgramClass programClass2 = (ProgramClass) abstractFieldInfo.getOwningClass();
            if (programClass2 != programClass1) {
                set2.add(this.selectMatchingProgramClass(programClass2));
            }
        }
    }

    public static Instruction createNarrowingConversion(int ba) {
        switch (ba) {
            case 66:
                return SimpleInstruction.forOpcode(145);
            case 67:
                return SimpleInstruction.forOpcode(146);
            case 73:
                return null;
            case 83:
                return SimpleInstruction.forOpcode(147);
            default:
                return null;
        }
    }

    private ClassMemberRef createClassMemberRef(
            MemberInfo memberInfo1, ResolvedMemberRef resolvedMemberRef, ReferenceObfuscator referenceObfuscator, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        String string = resolvedMemberRef.getReferencedClassName();
        if (string.charAt(0) == '[') {
            string = "java/lang/Object";
        }

        ClassFileBase classFileBase = this.getOwningClass();
        Integer integer;
        String string1;
        if (classFileBase.hasReleaseVersion()) {
            integer = classFileBase.getReleaseVersion();
            string1 = "in Reference Obfuscation";
        } else {
            integer = null;
            string1 = "in Reference Obfuscation";
        }

        ClassFileBase classFileBase1 = classResolver1.getVersionedClass(string, integer, string1);
        ClassMemberRef classMemberRef;
        if (memberInfo1.isProgramMember() && !HiddenOptionFlags.USE_ACCESSED_CLASS_FOR_REFERENCES) {
            classMemberRef = referenceObfuscator.getOrCreateMemberRef(memberInfo1, memberInfo1.getOwningClass());
        } else {
            classMemberRef = referenceObfuscator.getOrCreateMemberRef(memberInfo1, classFileBase1);
        }

        return classMemberRef;
    }

    public boolean resolveReflectedMethodsInClass(
            ClassFileBase classFileBase, Set set1, Set set2, boolean bl, Set set3, TwoKeyMap twoKeyMap, Set set4, ClassMemberLookup classMemberLookup1, boolean bl1
    ) throws ZkmException, IOException {
        boolean bl2 = bl1;
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            TrackedValue trackedValue = (TrackedValue) iterator.next();
            String string = trackedValue.getNormalizedName();
            if (string != null && string.length() > 0) {
                Iterator iterator1 = set2.iterator();

                while (iterator1.hasNext()) {
                    TrackedValue trackedValue1 = (TrackedValue) iterator1.next();
                    Set set5 = this.buildReflectedSignatures(string, bl, trackedValue1);
                    if (set5 != null) {
                        AbstractMethodInfo abstractMethodInfo = null;
                        Iterator iterator2 = set5.iterator();

                        while (iterator2.hasNext()) {
                            FieldNameTypeSignature fieldNameTypeSignature = (FieldNameTypeSignature) iterator2.next();
                            AbstractMethodInfo abstractMethodInfo1;
                            if (classFileBase.isProgramClass()) {
                                MethodInfo[] methodInfos = classMemberLookup1.findMatchingMethods((ProgramClass) classFileBase, fieldNameTypeSignature);
                                abstractMethodInfo1 = (MethodInfo) AbstractMethodInfo.selectMostSpecificReturnType(methodInfos, classMemberLookup1);
                            } else {
                                AbstractMethodInfo[] abstractMethodInfos = classFileBase.findMethodsByNameAndType(fieldNameTypeSignature);
                                abstractMethodInfo1 = AbstractMethodInfo.selectMostSpecificReturnType(abstractMethodInfos, classMemberLookup1);
                            }

                            if (abstractMethodInfo1 != null) {
                                if (abstractMethodInfo != null) {
                                    abstractMethodInfo = null;
                                    break;
                                }

                                abstractMethodInfo = abstractMethodInfo1;
                            }
                        }

                        if (abstractMethodInfo != null) {
                            twoKeyMap.putValue(abstractMethodInfo, trackedValue, trackedValue);
                            set4.add(abstractMethodInfo);
                            if (trackedValue instanceof StringConstantNameRef) {
                                set3.add(((StringConstantNameRef) trackedValue).getStringConstant());
                            }
                        } else {
                            bl2 = false;
                        }
                    } else {
                        bl2 = false;
                    }
                }
            } else {
                bl2 = false;
            }
        }

        return bl2;
    }

    public boolean resolveReflectedFields(
            Set set1,
            Set set2,
            Set set3,
            Set set4,
            TwoKeyMap twoKeyMap,
            Set set5,
            ObservableHolder observableHolder,
            ClasspathClassLoader classpathClassLoader1,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmException, IOException {
        boolean bl = true;
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            TrackedValue trackedValue = (TrackedValue) iterator.next();
            String string = trackedValue.getNormalizedName();
            if (string != null && string.length() > 0) {
                String string1 = ZkmUtils.dotsToSlashes(string);
                ClassFileBase classFileBase = lookupClass(string1, classpathClassLoader1);
                if (classFileBase != null) {
                    Iterator iterator1 = set2.iterator();

                    while (iterator1.hasNext()) {
                        TrackedValue trackedValue1 = (TrackedValue) iterator1.next();
                        String string2 = trackedValue1.getNormalizedName();
                        if (string2 != null && string2.length() > 0) {
                            AbstractFieldInfo[] abstractFieldInfos;
                            if (set3 != null && set3.size() > 0) {
                                ArrayList arrayList = new ArrayList(set3.size());
                                Iterator iterator2 = set3.iterator();

                                while (iterator2.hasNext()) {
                                    String string3 = ((TrackedValue) iterator2.next()).getStringValue();
                                    AbstractFieldInfo abstractFieldInfo;
                                    if (classFileBase.isProgramClass()) {
                                        abstractFieldInfo = classMemberLookup1.findField(string1, string2, MethodSignature.toTypeDescriptor(string3));
                                    } else {
                                        abstractFieldInfo = classFileBase.findField(string2, MethodSignature.toTypeDescriptor(string3));
                                    }

                                    if (abstractFieldInfo != null) {
                                        arrayList.add(abstractFieldInfo);
                                    }
                                }

                                abstractFieldInfos = ((com.zelix.klassmaster.classfile.AbstractFieldInfo[]) (arrayList.toArray(new AbstractFieldInfo[arrayList.size()])));
                            } else if (classFileBase.isProgramClass()) {
                                abstractFieldInfos = classMemberLookup1.findFieldsByName(string1, string2);
                            } else {
                                abstractFieldInfos = classFileBase.findFieldsByName(string2);
                            }

                            if (abstractFieldInfos != null && abstractFieldInfos.length > 0) {
                                if (abstractFieldInfos.length == 1) {
                                    AbstractFieldInfo abstractFieldInfo1 = abstractFieldInfos[0];
                                    twoKeyMap.putValue(abstractFieldInfo1, trackedValue1, trackedValue1);
                                    set5.add(abstractFieldInfo1);
                                    if (trackedValue1 instanceof StringConstantNameRef) {
                                        set4.add(((StringConstantNameRef) trackedValue1).getStringConstant());
                                    }
                                } else {
                                    observableHolder.setValue("Field inadequately resolved (C) - more than one possible match");
                                    bl = false;
                                }
                            } else {
                                bl = false;
                            }
                        } else {
                            bl = false;
                        }
                    }
                } else if (trackedValue instanceof TracedObjectType) {
                    bl = false;
                }
            } else {
                bl = false;
            }
        }

        return bl;
    }

    public LocalVariableList getLocalVariableList() {
        return this.localVariableList;
    }

    public boolean isInAnyRange(int ba, int[][] bb) {
        for (int[] bc : bb) {
            if (ba >= bc[0] && ba < bc[1]) {
                return true;
            }
        }

        return false;
    }

    public void collectInvokeOpcodes(HashMap hashMap) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            int opcode = instruction1.getOpcode();
            switch (opcode) {
                case 182:
                case 183:
                case 184:
                case 185:
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    hashMap.put(resolvedMethodRef, integerCache.valueOf(opcode));
                    break;
            }
        }
    }

    public void appendIntConstant(int ba, List list1, ConstantPool constantPool1, List list2, int bb) {
        if (!HiddenOptionFlags.ALWAYS_INLINE_INT_CONSTANTS
                && (this.instructions.size() <= 10000 || bb <= 1000 || list2.size() >= 24000 || HiddenOptionFlags.IGNORE_METHOD_SIZE_LIMIT)) {
            Instruction.appendIntConstant(ba, list1, constantPool1, list2);
        } else {
            Instruction instruction1 = Instruction.createIntConstantPush(ba, constantPool1, list2);
            list1.add(instruction1);
        }
    }

    public boolean applyAutoReflection(
            List list1,
            String string,
            List list2,
            boolean bl,
            boolean bl1,
            boolean bl2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            boolean bl3,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(list1.size()));
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            ReflectionCallSite reflectionCallSite = (ReflectionCallSite) iterator.next();
            hashMap.put(reflectionCallSite.getCallInstruction(), reflectionCallSite);
        }

        boolean bl4 = false;
        int bc = this.instructions.size();
        ClassResolver classResolver1 = commonSuperTypeResolver1.getClassResolver();
        ConstantPool constantPool1 = (ConstantPool) this.getOwningClass().getConstantPool();
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < bc; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                ReflectionCallSite reflectionCallSite1 = (ReflectionCallSite) hashMap.get(instruction1);
                if (reflectionCallSite1 != null) {
                    ReflectionApiMethod reflectionApiMethod = reflectionCallSite1.getApiMethod();
                    if (reflectionApiMethod.isHandlingEnabled(bl, bl1, bl2)) {
                        bl4 = true;
                        ReflectionLookupTemplate reflectionLookupTemplate = reflectionApiMethod.getLookupTemplate();
                        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                                string,
                                reflectionLookupTemplate.getLookupMethodName(),
                                reflectionLookupTemplate.getLookupMethodDescriptor(),
                                list2,
                                classMemberLookup1,
                                classResolver1
                        );
                        ResolvedMethodRefConstant[] resolvedMethodRefConstants = null;
                        String[] strings;
                        if ((strings = reflectionLookupTemplate.getHelperOwnerClasses()) != null) {
                            resolvedMethodRefConstants = new ResolvedMethodRefConstant[strings.length];
                            String[] strings1 = reflectionLookupTemplate.getHelperMethodNames();
                            String[] strings2 = reflectionLookupTemplate.getHelperMethodDescriptors();

                            for (int j = 0; j < strings1.length; j++) {
                                resolvedMethodRefConstants[j] = constantPool1.getOrAddMethodRef(
                                        strings[j], strings1[j], strings2[j], list2, classMemberLookup1, classResolver1
                                );
                            }
                        }

                        List list3 = reflectionLookupTemplate.createLookupInstructions(resolvedMethodRefConstant, resolvedMethodRefConstants);
                        arrayList.add(new CodeInsertion(list3, i - 1, 0, reflectionLookupTemplate.getExtraStackSize()));
                        if (bl3) {
                            PrintWriter printWriter = scriptEnvironment1.getLogWriter();
                            printWriter.println(
                                    "\tSet up AutoReflection handling of method '"
                                            + new MethodSignature(reflectionApiMethod.getMethodSignature()).toString()
                                            + "' call in method '"
                                            + reflectionCallSite1.getMethodName()
                                            + "' in class '"
                                            + reflectionCallSite1.getClassName()
                                            + "'"
                            );
                        }
                    }
                }
            }
        }

        if (bl4) {
            this.applyCodeInsertions(arrayList, "Auto Reflection Handling");
        }

        return bl4;
    }

    public boolean hasIntConstants() {
        return this.intConstantCount > 0;
    }

    public int findProtectableRangeStart(int ba, int bb, MutableInt mutableInt, int bc, boolean bl, MethodFlowAnalyzer methodFlowAnalyzer) {
        int bd = -1;
        int be = 0;
        if (bb > bc) {
            for (int i = bb; i >= ba; i += -1) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if (instruction1.isStore() || instruction1.isJsr() || bl && (instruction1.isMonitor() || methodFlowAnalyzer.hasHeldMonitorsAt(i)) || i <= bc) {
                    break;
                }

                bd = i;
                if (!instruction1.isLabel()) {
                    be++;
                }
            }
        }

        if (be <= 2) {
            return -1;
        }

        if (bd > ba) {
        }

        int bg = bb;

        for (Instruction instruction2 = (Instruction) this.instructions.get(bg); instruction2.isLabel(); instruction2 = (Instruction) this.instructions.get(bg)) {
            bg += -1;
        }

        if (bg < bb) {
            mutableInt.setValue(bg);
        }

        if (bd > -1) {
            FrameSignatureKey frameSignatureKey = (FrameSignatureKey) methodFlowAnalyzer.getFrameStateKey(bd);
            if (frameSignatureKey == null || frameSignatureKey.hasUninitializedThisType()) {
                return -1;
            }
        }

        return bd;
    }

    public static boolean usesKeyField(
            AbstractMethodInfo abstractMethodInfo, MethodParamChangeNode methodParamChangeNode, MethodOverrideAnalyzer methodOverrideAnalyzer
    ) {
        return methodParamChangeNode != null
                && methodParamChangeNode.hasKeyField()
                && MethodParameterChanger.isNotOverridable(abstractMethodInfo, methodOverrideAnalyzer);
    }

    public ListMultimap analyzeReflectionCalls(
            Map map1,
            ClasspathClassLoader classpathClassLoader1,
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            TwoKeyMap twoKeyMap,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            ClassMemberLookup classMemberLookup1,
            Map map6,
            TwoKeyMap twoKeyMap1,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap2,
            TwoKeyMap twoKeyMap3,
            Set set1,
            List list1,
            List list2,
            boolean bl
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = ZkmUtils.createHashMap();
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isMethodInvoke()) {
                ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                if (map1.containsKey(resolvedMethodRef)) {
                    arrayList.add(new RankedValue(i, resolvedMethodRef));
                    hashMap.put(integerCache.valueOf(i), null);
                }
            }
        }

        TwoKeyMap twoKeyMap4 = new TwoKeyMap(10, 10);
        if (arrayList.size() > 0) {
            String string1 = "";

            byte be;
            label140:
            {
                try {
                    MethodFlowAnalyzer methodFlowAnalyzer = this.getCachedFlowAnalyzer(map6, commonSuperTypeResolver1, classMemberLookup1);
                    Iterator iterator = arrayList.iterator();

                    while (iterator.hasNext()) {
                        RankedValue rankedValue = (RankedValue) iterator.next();
                        ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) rankedValue.getValue();
                        ReflectionApiMethod reflectionApiMethod = (ReflectionApiMethod) map1.get(resolvedMethodRef1);
                        CallArgumentValues callArgumentValues = methodFlowAnalyzer.traceReflectionCallArguments(
                                rankedValue.getRank(),
                                reflectionApiMethod,
                                nestedMultiMap,
                                nestedMultiMap1,
                                nestedMultiMap2,
                                map1,
                                map2,
                                map3,
                                map4,
                                map5,
                                classMemberLookup1,
                                map6
                        );
                        hashMap.put(integerCache.valueOf(rankedValue.getRank()), callArgumentValues);
                    }
                } catch (StackOverflowError stackOverflowError) {
                    throw new CorruptHierarchyException("StackOverflowError trapped in '" + this.getQualifiedMethodName() + "' (2)", stackOverflowError);
                } catch (ZkmProcessingException zkmProcessingException) {
                    string1 = zkmProcessingException.getMessage();
                    long bd = 56257057901662L;
                    be = 13;
                    break label140;
                }

                long bc = 56257057901662L;
                be = 13;
            }

            Integer integer = Integer.valueOf(be);
            HashSet hashSet = ZkmUtils.createHashSet(integer);
            HashMap hashMap1 = ZkmUtils.createHashMap();
            Iterator iterator1 = arrayList.iterator();

            while (iterator1.hasNext()) {
                RankedValue rankedValue1 = (RankedValue) iterator1.next();
                ResolvedMethodRef resolvedMethodRef3 = (ResolvedMethodRef) rankedValue1.getValue();
                ReflectionApiMethod reflectionApiMethod1 = (ReflectionApiMethod) map1.get(resolvedMethodRef3);
                CallArgumentValues callArgumentValues1 = (CallArgumentValues) hashMap.get(integerCache.valueOf(rankedValue1.getRank()));
                if (callArgumentValues1 != null) {
                    callArgumentValues1.resolvePendingValues(hashMap, integerCache, this);
                    if (!callArgumentValues1.areAllValuesKnown()) {
                        if (reflectionApiMethod1.isReportable()) {
                            ArrayList arrayList1 = new ArrayList();
                            String string = this.describeReflectionCall(reflectionApiMethod1, callArgumentValues1, "UNRESOLVED", arrayList1);
                            twoKeyMap4.putValue(resolvedMethodRef3, rankedValue1, string);
                            if (reflectionApiMethod1.hasLookupTemplate()) {
                                ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(rankedValue1.getRank());
                                hashMap1.put(
                                        constantRefInstruction,
                                        new ReflectionCallSite(
                                                this,
                                                reflectionApiMethod1,
                                                constantRefInstruction,
                                                this.getLocationName(),
                                                this.getQualifiedMethodName(),
                                                arrayList1,
                                                callArgumentValues1.getMemberNameArgument()
                                        )
                                );
                            }
                        }
                    } else {
                        if ((!callArgumentValues1.isValid() || HiddenOptionFlags.RESOLVE_REFLECTION_UNKNOWN_ARGS)
                                && reflectionApiMethod1.isReportable()
                                && reflectionApiMethod1.hasLookupTemplate()) {
                            ArrayList arrayList2 = new ArrayList();
                            this.describeReflectionCall(reflectionApiMethod1, callArgumentValues1, "RESOLVED", arrayList2);
                            ConstantRefInstruction constantRefInstruction3 = (ConstantRefInstruction) this.instructions.get(rankedValue1.getRank());
                            hashMap1.put(
                                    constantRefInstruction3,
                                    new ReflectionCallSite(
                                            this,
                                            reflectionApiMethod1,
                                            constantRefInstruction3,
                                            this.getLocationName(),
                                            this.getQualifiedMethodName(),
                                            arrayList2,
                                            callArgumentValues1.getMemberNameArgument()
                                    )
                            );
                        }

                        if (bl && reflectionApiMethod1.isReportable()) {
                            String string3 = this.describeReflectionCall(reflectionApiMethod1, callArgumentValues1, "RESOLVED");
                            twoKeyMap4.putValue(resolvedMethodRef3, rankedValue1, string3);
                        }
                    }

                    ObservableHolder observableHolder = new ObservableHolder();
                    if (!this.resolveReflectionCallTargets(
                            reflectionApiMethod1,
                            callArgumentValues1,
                            twoKeyMap1,
                            listMultimap,
                            twoKeyMap2,
                            twoKeyMap3,
                            twoKeyMap,
                            classMemberLookup1,
                            classpathClassLoader1,
                            set1,
                            hashSet,
                            observableHolder
                    )
                            && reflectionApiMethod1.isReportable()
                            && (
                            !twoKeyMap4.containsKeys(resolvedMethodRef3, rankedValue1)
                                    || ((String) twoKeyMap4.getValue(resolvedMethodRef3, rankedValue1)).startsWith("RESOLVED")
                    )) {
                        ArrayList arrayList3 = new ArrayList();
                        String string4 = this.describeReflectionCall(reflectionApiMethod1, callArgumentValues1, "UNRESOLVED", arrayList3);
                        if (!observableHolder.isValueNull()) {
                            string4 = string4 + " : " + (String) observableHolder.getValue();
                        }

                        twoKeyMap4.putValue(resolvedMethodRef3, rankedValue1, string4);
                        if (reflectionApiMethod1.hasLookupTemplate()) {
                            ConstantRefInstruction constantRefInstruction1 = (ConstantRefInstruction) this.instructions.get(rankedValue1.getRank());
                            hashMap1.put(
                                    constantRefInstruction1,
                                    new ReflectionCallSite(
                                            this,
                                            reflectionApiMethod1,
                                            constantRefInstruction1,
                                            this.getLocationName(),
                                            this.getQualifiedMethodName(),
                                            arrayList3,
                                            callArgumentValues1.getMemberNameArgument()
                                    )
                            );
                        }
                    }
                } else if (reflectionApiMethod1.isReportable()) {
                    twoKeyMap4.putValue(resolvedMethodRef3, rankedValue1, "UNRESOLVED " + string1);
                    if (reflectionApiMethod1.hasLookupTemplate()) {
                        ConstantRefInstruction constantRefInstruction2 = (ConstantRefInstruction) this.instructions.get(rankedValue1.getRank());
                        hashMap1.put(
                                constantRefInstruction2,
                                new ReflectionCallSite(
                                        this, reflectionApiMethod1, constantRefInstruction2, this.getLocationName(), this.getQualifiedMethodName(), null, null
                                )
                        );
                    }
                }
            }

            iterator1 = hashMap1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry = (Entry) iterator1.next();
                list2.add(entry.getValue());
            }

            if (hashSet.size() > 0) {
                iterator1 = hashSet.iterator();

                while (iterator1.hasNext()) {
                    ClassFileComponent classFileComponent = (ClassFileComponent) iterator1.next();
                    list1.add(classFileComponent);
                    if (classFileComponent instanceof AbstractFieldInfo) {
                        ClassFileBase classFileBase = ((AbstractFieldInfo) classFileComponent).getOwningClass();
                        if (!hashSet.contains(classFileBase)) {
                            list1.add(classFileBase);
                        }
                    } else if (classFileComponent instanceof AbstractMethodInfo) {
                        ClassFileBase classFileBase1 = ((AbstractMethodInfo) classFileComponent).getOwningClass();
                        if (!hashSet.contains(classFileBase1)) {
                            list1.add(classFileBase1);
                        }
                    }
                }
            }
        }

        ListMultimap listMultimap1 = new ListMultimap();
        Enumeration enumeration = twoKeyMap4.keys();

        while (enumeration.hasMoreElements()) {
            ResolvedMethodRef resolvedMethodRef2 = (ResolvedMethodRef) enumeration.nextElement();
            Map map7 = twoKeyMap4.getInnerMap(resolvedMethodRef2);
            Iterator iterator2 = map7.values().iterator();

            while (iterator2.hasNext()) {
                String string2 = (String) iterator2.next();
                listMultimap1.addValue(resolvedMethodRef2, string2);
            }
        }

        return listMultimap1;
    }

    public boolean adjustLdcWidths(Map map1) {
        boolean bl = false;
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = ((Instruction) this.instructions.get(i)).adjustLdcWidth(map1);
            if (instruction1 != null) {
                this.instructions.set(i, instruction1);
                bl = true;
            }
        }

        if (bl) {
            this.setModified(true);
        }

        return bl;
    }

    public void applyLongEncryption(
            List list1,
            boolean bl,
            int[][] ba,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        PriorityEntryComparator priorityEntryComparator = new PriorityEntryComparator(this);
        Collections.sort(list1, priorityEntryComparator);
        int bb = list1.size();
        int bc = -1;
        if (bb != 0) {
            MethodFlowAnalyzer methodFlowAnalyzer = null;
            Iterator iterator = list1.iterator();
            ObjectPair objectPair = (ObjectPair) iterator.next();
            ConstantPoolOperand constantPoolOperand = (ConstantPoolOperand) ((RankedValue) objectPair.getFirst()).getValue();
            int bd = bb;
            this.setModified(true);
            boolean bl1 = false;
            boolean bl2 = false;
            ArrayList arrayList = new ArrayList();
            int be = this.instructions.size();

            for (int i = 0; i < be; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if (instruction1.isLabel()) {
                    LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(1024)) {
                        bl1 = false;
                    }
                }

                if (instruction1 == constantPoolOperand) {
                    bd += -1;
                    ObfuscatedReferenceSlot obfuscatedReferenceSlot = (ObfuscatedReferenceSlot) objectPair.getSecond();
                    switch (LookupStrategySwitchMap.LONG_STORAGE_KIND_SWITCH[obfuscatedReferenceSlot.getStorageKind().ordinal()]) {
                        case 1:
                            ArrayList arrayList4 = new ArrayList();
                            if (obfuscatedReferenceSlot.hasIndex()) {
                                arrayList4.add(Instruction.createObjectLoad(obfuscatedReferenceSlot.getLocalVariableIndex(), this.localVariableList, 12));
                                this.appendIntConstant(obfuscatedReferenceSlot.getIndex(), arrayList4, constantPool1, list2, bb);
                                arrayList4.add(SimpleInstruction.forOpcode(47));
                            } else {
                                arrayList4.add(Instruction.createLongLoad(obfuscatedReferenceSlot.getLocalVariableIndex(), this.localVariableList, 12));
                            }

                            arrayList.add(new CodeInsertion(arrayList4, i - 1, 1, 0));
                            break;
                        case 2:
                            ArrayList arrayList3 = new ArrayList();
                            ResolvedFieldRef resolvedFieldRef = obfuscatedReferenceSlot.getArrayField();
                            if (obfuscatedReferenceSlot.hasIndex()) {
                                if (bl1) {
                                    arrayList3.add(Instruction.createObjectLoad(bc, this.localVariableList, 12));
                                } else {
                                    arrayList3.add(new ConstantRefInstruction(178, resolvedFieldRef));
                                    if (bd > 0 && (ba.length == 0 || !this.isInAnyRange(i, ba))) {
                                        if (!bl2) {
                                            bl2 = true;
                                            bc = intCounter.getValue();
                                            intCounter.incrementAndGet();
                                        }

                                        arrayList3.add(Instruction.createObjectStore(bc, this.localVariableList, 12));
                                        arrayList3.add(Instruction.createObjectLoad(bc, this.localVariableList, 12));
                                        bl1 = true;
                                    }
                                }

                                Instruction.appendIntConstant(obfuscatedReferenceSlot.getIndex(), arrayList3, constantPool1, list2);
                                arrayList3.add(SimpleInstruction.forOpcode(47));
                            } else {
                                arrayList3.add(new ConstantRefInstruction(178, resolvedFieldRef));
                            }

                            arrayList.add(new CodeInsertion(arrayList3, i - 1, 1, 0));
                            break;
                        case 3:
                            ResolvedMethodRef resolvedMethodRef1;
                            ArrayList arrayList6;
                            ArrayList arrayList8;
                            label80:
                            {
                                methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                                resolvedMethodRef1 = obfuscatedReferenceSlot.getLookupMethod();
                                arrayList6 = new ArrayList();
                                arrayList8 = new ArrayList();
                                int index = obfuscatedReferenceSlot.getIndex();
                                long encryptionKey = obfuscatedReferenceSlot.getEncryptionKey();
                                int bx;
                                long by;
                                long bz;
                                if (long1 != null) {
                                    if (localVariableIndex1 != null) {
                                        int bt = index ^ (int) (encryptionKey & 32767L);
                                        this.appendIntConstant(bt, arrayList6, constantPool1, list2, bb);
                                        long bw = encryptionKey ^ long1;
                                        Instruction.appendLongConstant(bw, arrayList8, constantPool1, list2);
                                        arrayList8.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 12));
                                        arrayList8.add(SimpleInstruction.forOpcode(131));
                                        break label80;
                                    }

                                    bx = index;
                                    by = encryptionKey;
                                    bz = 32767L;
                                } else {
                                    bx = index;
                                    by = encryptionKey;
                                    bz = 32767L;
                                }

                                int bu = bx ^ (int) (by & bz);
                                this.appendIntConstant(bu, arrayList6, constantPool1, list2, bb);
                                Instruction.appendLongConstant(encryptionKey, arrayList8, constantPool1, list2);
                            }

                            ConstantRefInstruction constantRefInstruction1 = new ConstantRefInstruction(184, resolvedMethodRef1);
                            this.addSplitCodeInsertions(arrayList6, arrayList8, constantRefInstruction1, arrayList, methodFlowAnalyzer, i);
                            break;
                        case 4:
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ResolvedMethodRef resolvedMethodRef = obfuscatedReferenceSlot.getLookupMethod();
                            ArrayList arrayList5 = new ArrayList();
                            ArrayList arrayList7 = new ArrayList();
                            int bm = obfuscatedReferenceSlot.getIndex();
                            long bo = obfuscatedReferenceSlot.getEncryptionKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int bs = bm ^ (int) (bo & 32767L);
                                this.appendIntConstant(bs, arrayList5, constantPool1, list2, bb);
                                long bv = bo ^ long1;
                                Instruction.appendLongConstant(bv, arrayList7, constantPool1, list2);
                                arrayList7.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 12));
                                arrayList7.add(SimpleInstruction.forOpcode(131));
                            } else {
                                int br = bm ^ (int) bo;
                                Instruction.appendIntConstant(br, arrayList5, constantPool1, list2);
                                Instruction.appendLongConstant(bo, arrayList7, constantPool1, list2);
                            }

                            ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(184, resolvedMethodRef);
                            this.addSplitCodeInsertions(arrayList5, arrayList7, constantRefInstruction, arrayList, methodFlowAnalyzer, i);
                            break;
                        case 5:
                            ResolvedInvokeDynamic resolvedInvokeDynamic = obfuscatedReferenceSlot.getInvokeDynamic();
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ArrayList arrayList1 = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int bg = obfuscatedReferenceSlot.getIndex();
                            long bh = obfuscatedReferenceSlot.getEncryptionKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int bq = bg ^ (int) (bh & 32767L);
                                this.appendIntConstant(bq, arrayList1, constantPool1, list2, bb);
                                long bj = bh ^ long1;
                                Instruction.appendLongConstant(bj, arrayList2, constantPool1, list2);
                                arrayList2.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 12));
                                arrayList2.add(SimpleInstruction.forOpcode(131));
                            } else {
                                int bi = bg ^ (int) bh;
                                this.appendIntConstant(bi, arrayList1, constantPool1, list2, bb);
                                Instruction.appendLongConstant(bh, arrayList2, constantPool1, list2);
                            }

                            InvokeDynamicInstruction invokeDynamicInstruction = new InvokeDynamicInstruction(resolvedInvokeDynamic);
                            this.addSplitCodeInsertions(arrayList1, arrayList2, invokeDynamicInstruction, arrayList, methodFlowAnalyzer, i);
                    }

                    if (!iterator.hasNext()) {
                        break;
                    }

                    objectPair = (ObjectPair) iterator.next();
                    constantPoolOperand = (ConstantPoolOperand) ((RankedValue) objectPair.getFirst()).getValue();
                }
            }

            this.applyCodeInsertions(arrayList, "Long Constant Encryption", bl);
            if (!bl) {
                CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
                int maxStack = codeAttributeBody.getMaxStack();
                codeAttributeBody.setMaxStack(maxStack + 0);
            }
        }
    }

    public Set buildReflectedSignatures(String string, boolean bl, TrackedValue trackedValue) {
        HashSet hashSet = ZkmUtils.createHashSet();
        if (trackedValue instanceof NullReflectionValue) {
            hashSet.add(new FieldNameTypeSignature(string, "()", null));
        } else if (trackedValue instanceof TracedArrayValue) {
            TracedArrayValue tracedArrayValue = (TracedArrayValue) trackedValue;
            String[][] strings = tracedArrayValue.getValueCombinations();
            if (tracedArrayValue.getLength() != 0 && strings != null && strings.length != 0) {
                for (int i = 0; i < strings.length; i++) {
                    String[] strings1 = strings[i];
                    if (bl) {
                        String[] strings2 = new String[strings1.length - 1];
                        System.arraycopy(strings1, 1, strings2, 0, strings2.length);
                        hashSet.add(new FieldNameTypeSignature(string, this.buildParameterDescriptor(strings2), MethodSignature.toTypeDescriptor(strings1[0])));
                    } else {
                        hashSet.add(new FieldNameTypeSignature(string, this.buildParameterDescriptor(strings1), null));
                    }
                }
            } else {
                hashSet.add(new FieldNameTypeSignature(string, "()", null));
            }
        } else if (!(trackedValue instanceof ClassConstantValue) && !(trackedValue instanceof TracedObjectType)) {
            if (trackedValue instanceof StringConstantValue) {
                hashSet.add(new FieldNameTypeSignature(string, trackedValue.getStringValue()));
            } else if (trackedValue instanceof LiteralStringValue) {
                if (bl) {
                    hashSet.add(new FieldNameTypeSignature(string, this.buildParameterDescriptor(new String[0]), trackedValue.getStringValue()));
                } else {
                    String[] strings4 = new String[]{trackedValue.getStringValue()};
                    hashSet.add(new FieldNameTypeSignature(string, this.buildParameterDescriptor(strings4), null));
                }
            }
        } else if (bl) {
            hashSet.add(
                    new FieldNameTypeSignature(string, this.buildParameterDescriptor(new String[0]), MethodSignature.toTypeDescriptor(trackedValue.getStringValue()))
            );
        } else {
            String[] strings3 = new String[]{MethodSignature.toTypeDescriptor(trackedValue.getStringValue())};
            hashSet.add(new FieldNameTypeSignature(string, this.buildParameterDescriptor(strings3), null));
        }

        return hashSet;
    }

    public boolean hasLongConstants() {
        return this.longConstantCount > 0;
    }

    public void collectIntConstants(Set set1) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.loadsConstantInteger()) {
                ConstantPoolEntry constantPoolEntry = ((ConstantPoolOperand) instruction1).getConstantPoolEntry();
                if (constantPoolEntry instanceof ConstantInteger) {
                    set1.add((ConstantInteger) constantPoolEntry);
                }
            }
        }
    }

    public CodeAttributeBody getCodeAttributeBody() {
        return (CodeAttributeBody) this.getParent();
    }

    public ResolvedInvokeDynamic getOrCreateInvokeDynamic(
            String string,
            int ba,
            ResolvedMethodRef resolvedMethodRef,
            BootstrapMethodsAttribute bootstrapMethodsAttribute1,
            Map map1,
            List list1,
            ConstantPool constantPool1
    ) {
        String string1 = (char) ba + string;
        ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) map1.get(string1);
        if (resolvedInvokeDynamic == null) {
            ResolvedNameAndType resolvedNameAndType = constantPool1.createNameAndType(String.valueOf((char) ba), string, list1);
            ResolvedMethodHandleConstant resolvedMethodHandleConstant = constantPool1.getOrAddMethodHandle(
                    MethodHandleRefKind.REF_INVOKE_STATIC, resolvedMethodRef, list1
            );
            ConstantPoolEntry[] constantPoolEntrys = new ConstantPoolEntry[0];
            BootstrapMethodEntry bootstrapMethodEntry = bootstrapMethodsAttribute1.addEntry(resolvedMethodHandleConstant, constantPoolEntrys);
            resolvedInvokeDynamic = new ResolvedInvokeDynamic(constantPool1, resolvedNameAndType, bootstrapMethodEntry);
            list1.add(resolvedInvokeDynamic);
            map1.put(string1, resolvedInvokeDynamic);
        }

        return resolvedInvokeDynamic;
    }

    public String describeReflectionCall(ReflectionApiMethod reflectionApiMethod, CallArgumentValues callArgumentValues, String string, List list1) {
        StringBuilder stringBuilder = new StringBuilder(32);
        stringBuilder.append(string);
        if (reflectionApiMethod.isFunctionalInterfaceLookup()) {
            stringBuilder.append(' ');
            int argumentCount = callArgumentValues.getArgumentCount();

            for (int i = 0; i < argumentCount; i++) {
                stringBuilder.append(this.formatArgumentValues(callArgumentValues, i, false, list1));
            }
        } else {
            int bc = 0;
            List list2 = reflectionApiMethod.getParamDetails();
            Iterator iterator = list2.iterator();

            while (iterator.hasNext()) {
                ReflectionParamDetail reflectionParamDetail = (ReflectionParamDetail) iterator.next();
                stringBuilder.append(
                        " param_"
                                + reflectionParamDetail.getPosition()
                                + "="
                                + this.formatArgumentValues(
                                callArgumentValues, bc++, reflectionParamDetail.isClassParam(), reflectionParamDetail.isTargetClassParam() ? list1 : null
                        )
                );
            }

            if (reflectionApiMethod.isReceiverTargetClass()) {
                stringBuilder.append(" referencedClass=" + this.formatArgumentValues(callArgumentValues, bc, true, list1));
            }
        }

        return stringBuilder.toString();
    }

    public MethodFlowAnalyzer getCachedFlowAnalyzer(Map map1, CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer = (MethodFlowAnalyzer) map1.get(this);
        if (methodFlowAnalyzer != null) {
            return methodFlowAnalyzer;
        }

        methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
        map1.put(this, methodFlowAnalyzer);
        return methodFlowAnalyzer;
    }

    public void collectReferencedProgramMembers(Set set1, Set set2, Set set3, Set set4) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            switch (instruction1.getOpcode()) {
                case 18:
                case 19:
                    ConstantPoolEntry constantPoolEntry = null;
                    if (instruction1 instanceof LdcInstruction) {
                        constantPoolEntry = ((LdcInstruction) instruction1).getConstantPoolEntry();
                    } else if (instruction1 instanceof ConstantRefInstruction) {
                        constantPoolEntry = ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    }

                    if (constantPoolEntry instanceof ResolvedClassConstant) {
                        ResolvedClassConstant resolvedClassConstant1 = (ResolvedClassConstant) constantPoolEntry;
                        ProgramClass programClass2 = resolvedClassConstant1.findProgramClass();
                        if (programClass2 != null) {
                            set1.add(this.selectMatchingProgramClass(programClass2));
                        }
                    }
                    break;
                case 178:
                case 179:
                    ResolvedFieldRef resolvedFieldRef1 = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectProgramFieldRef(resolvedFieldRef1, set3, set2);
                    break;
                case 180:
                case 181:
                    ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectProgramFieldRef(resolvedFieldRef, set3, set1);
                    break;
                case 182:
                case 183:
                case 185:
                    ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectProgramMethodRef(resolvedMethodRef1, set4, set1);
                    break;
                case 184:
                    ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectProgramMethodRef(resolvedMethodRef, set4, set2);
                    break;
                case 186:
                    ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    this.collectProgramInvokeDynamicRefs(resolvedInvokeDynamic, set3, set4, set2, set1);
                    break;
                case 187:
                case 189:
                case 192:
                case 193:
                case 197:
                    ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) ((ConstantRefInstruction) instruction1).getConstantPoolEntry();
                    ProgramClass programClass1 = resolvedClassConstant.findProgramClass();
                    if (programClass1 != null) {
                        set1.add(this.selectMatchingProgramClass(programClass1));
                    }
            }
        }
    }

    public boolean isConstructor() {
        return ((CodeAttributeBody) this.getParent()).isConstructor();
    }

    public AddedParameter[] insertAddedParameters(List list1, ChangedMethodDescriptor changedMethodDescriptor, AbstractMethodInfo abstractMethodInfo, List list2) {
        ConstantPoolEntry.getParameterTypes(changedMethodDescriptor.getDescriptor());
        AddedParameter[] addedParameters1 = changedMethodDescriptor.getAddedParameters();
        list2.addAll(list1);
        String string = null;
        if (abstractMethodInfo.isInstanceNonConstructor()) {
            string = (String) list2.remove(0);
        }

        for (int i = 0; i < addedParameters1.length; i++) {
            AddedParameter addedParameter = addedParameters1[i];
            ZkmAssert.assertTrue(
                    addedParameter.getIndex() <= list2.size(),
                    new String[]{
                            "Inconsistent parameters in method '"
                                    + abstractMethodInfo.toOriginalDisplayString()
                                    + "' in class '"
                                    + abstractMethodInfo.getDisplayLocationName()
                                    + "' : "
                                    + addedParameter.getIndex()
                                    + " : "
                                    + list2.size()
                    }
            );
            list2.add(addedParameter.getIndex(), String.valueOf(addedParameter.getTypeChar()));
        }

        AddedParameter[] addedParameters2;
        if (string != null) {
            String string1 = string;
            list2.add(0, string1);
            addedParameters2 = new AddedParameter[addedParameters1.length];

            for (int i = 0; i < addedParameters1.length; i++) {
                AddedParameter addedParameter1 = addedParameters1[i];
                addedParameters2[i] = new AddedParameter(addedParameter1.getIndex() + 1, addedParameter1.getTypeChar());
            }
        } else {
            addedParameters2 = addedParameters1;
        }

        return addedParameters2;
    }

    public boolean isStatic() {
        return ((CodeAttributeBody) this.getParent()).isMethodStatic();
    }

    public int getInstructionCount() {
        return this.instructions.size();
    }

    public void recordOpcodeStatistics(ProcessingStatistics processingStatistics1) {
        int ba = 0;
        StringBuilder stringBuilder = new StringBuilder(this.instructions.size());
        Iterator iterator = this.instructions.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            if (!instruction1.isLabel()) {
                ba++;
                stringBuilder.append((char) instruction1.getOpcode());
            }
        }

        processingStatistics1.incrementMethodCount();
        processingStatistics1.addMethodBodyHash(stringBuilder.toString().hashCode());
        processingStatistics1.addInstructionCount(ba);
    }

    public boolean isPrivate() {
        return ((CodeAttributeBody) this.getParent()).isMethodPrivate();
    }

    public String describeReflectionCall(ReflectionApiMethod reflectionApiMethod, CallArgumentValues callArgumentValues, String string) {
        return this.describeReflectionCall(reflectionApiMethod, callArgumentValues, string, (List) null);
    }

    public String buildParameterDescriptor(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = strings.length;
        stringBuilder.append("(");

        for (int i = 0; i < ba; i++) {
            String string = strings[i];
            if (string != null) {
                string.length();
                stringBuilder.append(MethodSignature.toTypeDescriptor(string));
            }
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void collectBootstrapMethodEntries(Set set1) {
        Iterator iterator = this.instructions.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            if (instruction1.getOpcode() == 186) {
                ResolvedInvokeDynamic resolvedInvokeDynamic = ((InvokeDynamicInstruction) instruction1).getInvokeDynamicRef();
                set1.add(resolvedInvokeDynamic.getBootstrapMethod());
            }
        }
    }

    public void rewriteCallArguments(
            MethodFlowAnalyzer methodFlowAnalyzer,
            int ba,
            String string,
            ChangedMethodDescriptor changedMethodDescriptor,
            int[] bb,
            ListMultimap listMultimap,
            List list1,
            int bc,
            MutableInt mutableInt
    ) {
        ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) this.instructions.get(ba);
        int bd = ba - 1;
        int be;
        if (ba == 0) {
            be = this.getMethod().getArgumentSlotCount();
        } else {
            be = methodFlowAnalyzer.getStackDepthAt(bd);
        }

        List list2 = ConstantPoolEntry.getParameterTypes(string);
        List list3;
        AddedParameter[] addedParameters1;
        if (constantRefInstruction.getOpcode() == 186) {
            ResolvedInvokeDynamic resolvedInvokeDynamic = ((InvokeDynamicInstruction) constantRefInstruction).getInvokeDynamicRef();
            AbstractMethodInfo abstractMethodInfo = resolvedInvokeDynamic.getLambdaImplMethod();
            list3 = new ArrayList();
            addedParameters1 = this.insertAddedParameters(list2, changedMethodDescriptor, abstractMethodInfo, list3);
            StringBuilder stringBuilder = new StringBuilder();
            Iterator iterator = list3.iterator();

            while (iterator.hasNext()) {
                String string1 = (String) iterator.next();
                stringBuilder.append(string1);
            }

            resolvedInvokeDynamic.getDescriptor();
            resolvedInvokeDynamic.setParameterDescriptor(stringBuilder.toString());
        } else {
            list3 = ConstantPoolEntry.getParameterTypes(changedMethodDescriptor.getDescriptor());
            addedParameters1 = changedMethodDescriptor.getAddedParameters();
        }

        int bp = list2.size();
        int bq = list3.size();
        int br = be - bp;
        if (constantRefInstruction.getOpcode() == 185) {
            ((InvokeInterfaceInstruction) constantRefInstruction).setArgSlotCount(ConstantPoolEntry.countParameterSlots(list3) + 1);
        }

        int bs = 0;
        int bt = 0;
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        ArrayList arrayList = new ArrayList();
        ListMultimap listMultimap1 = new ListMultimap();
        ListMultimap listMultimap2 = new ListMultimap();

        for (int i = 0; i < bq; i++) {
            if (bt < addedParameters1.length && i == addedParameters1[bt].getIndex()) {
                arrayList.clear();
                char typeChar = addedParameters1[bt].getTypeChar();
                if (typeChar == 'J') {
                    arrayList.add(Instruction.createLongLoad(bb[bt], this.localVariableList, 5));
                } else {
                    arrayList.add(Instruction.createIntLoad(bb[bt], this.localVariableList, 5));
                    Instruction instruction1 = createNarrowingConversion(typeChar);
                    if (instruction1 != null) {
                        arrayList.add(instruction1);
                    }
                }

                if (bs >= bp) {
                    listMultimap1.appendValues(integerCache.valueOf(bd), arrayList);
                } else {
                    BasicBlock basicBlock = methodFlowAnalyzer.getBlockContaining(bd);
                    VerifierType[] verifierTypes = methodFlowAnalyzer.copyFrameStates()[bd].getStack();
                    hashSet.clear();
                    hashSet1.clear();
                    if (methodFlowAnalyzer.findStackValueProducers(basicBlock, ba, br, hashSet, hashSet1)) {
                        if (hashSet.size() > 1) {
                            Iterator iterator2 = hashSet.iterator();

                            while (iterator2.hasNext()) {
                                Integer integer = (Integer) iterator2.next();
                                int bv = integer;
                                Instruction instruction4 = (Instruction) this.instructions.get(bv);
                                if (instruction4.pushesWithoutPopping() && !instruction4.isMethodInvoke()) {
                                    listMultimap1.appendValues(integerCache.valueOf(bv - 1), arrayList);
                                } else {
                                    VerifierType[] verifierTypes2 = methodFlowAnalyzer.copyFrameStates()[bv].getStack();
                                    ArrayList arrayList2;
                                    if (verifierTypes2[verifierTypes2.length - 1].isWide()) {
                                        arrayList2 = new ArrayList(arrayList.size() + 2);
                                        arrayList2.addAll(arrayList);
                                        if (typeChar == 'J') {
                                            arrayList2.add(SimpleInstruction.forOpcode(94));
                                            arrayList2.add(SimpleInstruction.forOpcode(88));
                                        } else {
                                            arrayList2.add(SimpleInstruction.forOpcode(91));
                                            arrayList2.add(SimpleInstruction.forOpcode(87));
                                        }
                                    } else {
                                        arrayList2 = new ArrayList(arrayList.size() + 1);
                                        arrayList2.addAll(arrayList);
                                        if (typeChar == 'J') {
                                            arrayList2.add(SimpleInstruction.forOpcode(93));
                                            arrayList2.add(SimpleInstruction.forOpcode(88));
                                        } else {
                                            arrayList2.add(SimpleInstruction.forOpcode(95));
                                        }
                                    }

                                    listMultimap2.appendValues(integer, arrayList2);
                                }
                            }
                        } else {
                            int bu = (Integer) hashSet.iterator().next();
                            Instruction instruction3 = (Instruction) this.instructions.get(bu);
                            if (instruction3.pushesWithoutPopping() && !instruction3.isMethodInvoke()) {
                                listMultimap1.appendValues(integerCache.valueOf(bu - 1), arrayList);
                            } else {
                                VerifierType[] verifierTypes1 = methodFlowAnalyzer.copyFrameStates()[bu].getStack();
                                if (verifierTypes[verifierTypes1.length - 1].isWide()) {
                                    if (typeChar == 'J') {
                                        arrayList.add(SimpleInstruction.forOpcode(94));
                                        arrayList.add(SimpleInstruction.forOpcode(88));
                                    } else {
                                        arrayList.add(SimpleInstruction.forOpcode(91));
                                        arrayList.add(SimpleInstruction.forOpcode(87));
                                    }

                                    listMultimap2.appendValues(integerCache.valueOf(bu), arrayList);
                                } else {
                                    if (typeChar == 'J') {
                                        arrayList.add(SimpleInstruction.forOpcode(93));
                                        arrayList.add(SimpleInstruction.forOpcode(88));
                                    } else {
                                        arrayList.add(SimpleInstruction.forOpcode(95));
                                    }

                                    listMultimap2.appendValues(integerCache.valueOf(bu), arrayList);
                                }
                            }
                        }
                    } else if (bb.length == 1 && br == be - 1 && !verifierTypes[be - 1].isWide()) {
                        if (typeChar == 'J') {
                            arrayList.add(SimpleInstruction.forOpcode(93));
                            arrayList.add(SimpleInstruction.forOpcode(88));
                        } else {
                            arrayList.add(SimpleInstruction.forOpcode(95));
                        }

                        listMultimap1.appendValues(integerCache.valueOf(bd), arrayList);
                    } else if (bb.length == 1 && br == be - 1) {
                        if (typeChar == 'J') {
                            arrayList.add(SimpleInstruction.forOpcode(94));
                            arrayList.add(SimpleInstruction.forOpcode(88));
                        } else {
                            arrayList.add(SimpleInstruction.forOpcode(91));
                            arrayList.add(SimpleInstruction.forOpcode(87));
                        }

                        listMultimap1.appendValues(integerCache.valueOf(bd), arrayList);
                    } else {
                        if (bb.length != 1 || br != be - 2 || verifierTypes[be - 1].isWide() || verifierTypes[be - 2].isWide()) {
                            int bh = bc;
                            this.localVariableList.clearWideFlagsFrom(bh);
                            listMultimap1.clear();
                            listMultimap2.clear();
                            arrayList.clear();
                            ArrayList arrayList1 = new ArrayList();
                            int index = changedMethodDescriptor.getAddedParameters()[0].getIndex();
                            int bj = bp - index;
                            int[] bk = new int[bj];
                            int bl = bp - 1;

                            for (int j = 0; j < bj; j++) {
                                String string2 = (String) list2.get(bl);
                                bl += -1;
                                boolean bl1 = ConstantPoolEntry.isWideType(string2);
                                arrayList1.add(Instruction.createStoreForType(string2, bh, this.localVariableList));
                                bk[j] = bh;
                                if (bh > (Integer) list1.get(list1.size() - 1)) {
                                    list1.add(integerCache.valueOf(bh));
                                    mutableInt.incrementAndGet();
                                }

                                if (bl1) {
                                    if (++bh > (Integer) list1.get(list1.size() - 1)) {
                                        list1.add(integerCache.valueOf(bh));
                                        mutableInt.incrementAndGet();
                                    }
                                }

                                bh++;
                            }

                            int bw = 0;
                            int bx = index;
                            int by = index;
                            int bn = bk.length - 1;

                            while (bx < bq) {
                                String string3 = (String) list3.get(bx);
                                if (bw < addedParameters1.length && bx == addedParameters1[bw].getIndex()) {
                                    char bo = addedParameters1[bw].getTypeChar();
                                    if (bo == 'J') {
                                        arrayList1.add(Instruction.createLongLoad(bb[bw], this.localVariableList, 5));
                                    } else {
                                        arrayList1.add(Instruction.createIntLoad(bb[bw], this.localVariableList, 5));
                                        Instruction instruction2 = createNarrowingConversion(bo);
                                        if (instruction2 != null) {
                                            arrayList1.add(instruction2);
                                        }
                                    }

                                    bw++;
                                } else {
                                    arrayList1.add(Instruction.createLoadForType(string3, bk[bn], this.localVariableList));
                                    bn += -1;
                                    by++;
                                    br++;
                                }

                                bx++;
                            }

                            listMultimap1.appendValues(integerCache.valueOf(bd), arrayList1);
                            break;
                        }

                        if (typeChar == 'J') {
                            arrayList.add(SimpleInstruction.forOpcode(94));
                            arrayList.add(SimpleInstruction.forOpcode(88));
                        } else {
                            arrayList.add(SimpleInstruction.forOpcode(91));
                            arrayList.add(SimpleInstruction.forOpcode(87));
                        }

                        listMultimap1.appendValues(integerCache.valueOf(bd), arrayList);
                    }
                }

                bt++;
            } else {
                bs++;
                br++;
            }
        }

        Iterator iterator1 = listMultimap2.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            listMultimap.prependValues(entry.getKey(), (Collection) entry.getValue());
        }

        iterator1 = listMultimap1.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry1 = (Entry) iterator1.next();
            listMultimap.appendValues(entry1.getKey(), (Collection) entry1.getValue());
        }

        listMultimap2.clear();
        listMultimap1.clear();
    }

    public SyncIndexedSet findInsertionSyncPoints(List list1, MethodFlowAnalyzer methodFlowAnalyzer, int ba) {
        BasicBlock basicBlock = methodFlowAnalyzer.getBlockContaining(ba);
        StackFrameState[] stackFrameStates = methodFlowAnalyzer.copyFrameStates();
        SyncIndexedSet syncIndexedSet = new SyncIndexedSet();
        int bb = this.findMarkerLabelIndex();
        if (ba > 0 && stackFrameStates[ba - 1].getStack().length == 0 && basicBlock.getStartIndex() > bb) {
            int startIndex = basicBlock.getStartIndex();
            int bd = -1;
            if (!list1.isEmpty()) {
                bd = ((CodeInsertion) list1.get(list1.size() - 1)).getPosition();
            }

            int be = Math.max(startIndex, bd + 1);

            for (int i = ba - 1; i >= be; i += -1) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                LabelInstruction labelInstruction = null;
                if (instruction1.isLabel()) {
                    labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(256)) {
                        break;
                    }
                }

                boolean bl = labelInstruction != null && labelInstruction.getOffset() == 0;
                if (stackFrameStates[i].getStack().length == 0
                        && !instruction1.isJump()
                        && instruction1.getOpcode() != 191
                        && (labelInstruction == null || labelInstruction.getOffset() <= 0)) {
                    if (bl) {
                        syncIndexedSet.add(integerCache.valueOf(0));
                    } else {
                        syncIndexedSet.add(integerCache.valueOf(i));
                    }
                }
            }
        }

        return syncIndexedSet;
    }

    public void collectStringConstants(Set set1) {
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isStringConstantLoad()) {
                ConstantPoolEntry constantPoolEntry = ((ConstantPoolOperand) instruction1).getConstantPoolEntry();
                if (constantPoolEntry instanceof ResolvedStringConstant) {
                    set1.add((ResolvedStringConstant) constantPoolEntry);
                }
            }
        }
    }

    public void collectIntEncryptionCandidates(
            MultiMapTable multiMapTable,
            MethodInfo methodInfo1,
            Set set1,
            IntegerEncryptionExclusions integerEncryptionExclusions,
            boolean bl,
            List list1,
            ConstantPool constantPool1
    ) {
        boolean bl1 = false;
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        int ba = this.instructions.size();
        int bb = 0;

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.isLabel() && ((LabelInstruction) instruction1).isMarker()) {
                bb = i;
                break;
            }
        }

        for (int i = bb; i < ba; i++) {
            Instruction instruction3 = (Instruction) this.instructions.get(i);
            if (instruction3.isIntConstantPush() || instruction3.isIntConstantLdc()) {
                boolean bl2 = false;
                if (i < ba - 1) {
                    Instruction instruction2 = (Instruction) this.instructions.get(i + 1);
                    if (instruction2.isFieldStore()) {
                        ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) ((ConstantRefInstruction) instruction2).getConstantPoolEntry();
                        AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
                        if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
                            bl2 = true;
                            if (integerEncryptionExclusions == null || !integerEncryptionExclusions.isFieldExcluded((FieldInfo) abstractFieldInfo)) {
                                if (instruction3.isIntConstantPush()) {
                                    int bd = -1;
                                    if (instruction3.getOpcode() == 16) {
                                        bd = ((BipushInstruction) instruction3).getByteValue();
                                    } else if (instruction3.getOpcode() == 17) {
                                        bd = ((SipushInstruction) instruction3).getValue();
                                    } else if (instruction3 instanceof SimpleInstruction) {
                                        switch (instruction3.getOpcode()) {
                                            case 2:
                                                if (!HiddenOptionFlags.ENCRYPT_TRIVIAL_INTEGERS) {
                                                    continue;
                                                }

                                                bd = -1;
                                                break;
                                            case 3:
                                                if (!HiddenOptionFlags.ENCRYPT_TRIVIAL_INTEGERS) {
                                                    continue;
                                                }

                                                bd = 0;
                                                break;
                                            case 4:
                                                if (!HiddenOptionFlags.ENCRYPT_TRIVIAL_INTEGERS) {
                                                    continue;
                                                }

                                                bd = 1;
                                                break;
                                            case 5:
                                                bd = 2;
                                                break;
                                            case 6:
                                                bd = 3;
                                                break;
                                            case 7:
                                                bd = 4;
                                                break;
                                            case 8:
                                                bd = 5;
                                        }
                                    }

                                    ConstantInteger constantInteger = constantPool1.getOrAddIntegerConstant(bd, list1, false, true);
                                    ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(19, constantInteger);
                                    this.instructions.set(i, constantRefInstruction);
                                    this.intConstantCount++;
                                    multiMapTable.addEntry(programClass1, methodInfo1, constantInteger, new RankedValue(i, constantRefInstruction));
                                    bl1 = true;
                                } else {
                                    Integer integer = i;
                                    instruction3.collectIntConstant(multiMapTable, set1, methodInfo1, integer);
                                }
                            }
                        }
                    }
                }

                if (!bl2 && !(instruction3 instanceof SimpleInstruction)) {
                    if (instruction3.isIntConstantPush()) {
                        if (bl) {
                            int bf = -1;
                            if (instruction3.getOpcode() == 16) {
                                bf = ((BipushInstruction) instruction3).getByteValue();
                            } else if (instruction3.getOpcode() == 17) {
                                bf = ((SipushInstruction) instruction3).getValue();
                            }

                            ConstantInteger constantInteger1 = constantPool1.getOrAddIntegerConstant(bf, list1, false, true);
                            ConstantRefInstruction constantRefInstruction1 = new ConstantRefInstruction(19, constantInteger1);
                            this.instructions.set(i, constantRefInstruction1);
                            this.intConstantCount++;
                            multiMapTable.addEntry(programClass1, methodInfo1, constantInteger1, new RankedValue(i, constantRefInstruction1));
                            bl1 = true;
                        }
                    } else {
                        Integer integer1 = i;
                        instruction3.collectIntConstant(multiMapTable, set1, methodInfo1, integer1);
                    }
                }
            }
        }

        if (bl1) {
            this.setModified(true);
            this.recomputeOffsets();
        }
    }

    public void packParametersIntoArray(
            List list1,
            LocalVariableIndex localVariableIndex1,
            ConstantPool constantPool1,
            List list2,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        MethodInfo methodInfo1 = (MethodInfo) this.getParent().getParent();
        List list3 = methodInfo1.getParameterTypes();
        int ba = methodInfo1.isStatic() ? 0 : 1;
        LocalVariableSlot localVariableSlot1 = this.localVariableList.insertSlot(ba, false, 10);
        this.localVariableList.renumberSlots();
        if (!list3.isEmpty()) {
            int bb = list3.size() + ba;
            int bc = ba + 1;
            int bd = bc;
            ArrayList arrayList = new ArrayList(list3.size());

            for (int i = ba; i < bb; i++) {
                LocalVariableSlot localVariableSlot2 = this.localVariableList.getSlotAt(bd++);
                arrayList.add(localVariableSlot2);
                if (localVariableSlot2.isWideFirstHalf()) {
                    bd++;
                }
            }

            int bh = bd - 1;
            ParameterUsageInfo[] parameterUsageInfos = new ParameterUsageInfo[list3.size()];

            for (int i = 0; i < this.instructions.size(); i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if (instruction1.isLocalVariableAccess()) {
                    LocalVariableInstruction localVariableInstruction = (LocalVariableInstruction) instruction1;
                    LocalVariableIndex localVariableIndex2 = localVariableInstruction.getLocalVariableIndex();
                    int bg = arrayList.indexOf(localVariableIndex2);
                    if (bg > -1) {
                        ParameterUsageInfo parameterUsageInfo = parameterUsageInfos[bg];
                        if (parameterUsageInfo == null) {
                            parameterUsageInfo = new ParameterUsageInfo(this, bg, (String) list3.get(bg));
                            parameterUsageInfos[bg] = parameterUsageInfo;
                        }

                        parameterUsageInfo.addUsage(i, localVariableInstruction.isStore());
                    }
                }
            }

            boolean bl2;
            if (localVariableIndex1 != null) {
                int bi = arrayList.indexOf(localVariableIndex1);
                if (bi > -1) {
                    if (parameterUsageInfos[bi] == null) {
                        parameterUsageInfos[bi] = new ParameterUsageInfo(this, bi, (String) list3.get(bi));
                        bl2 = HiddenOptionFlags.TRACK_ALL_PARAMETER_USAGE;
                    } else {
                        bl2 = HiddenOptionFlags.TRACK_ALL_PARAMETER_USAGE;
                    }
                } else {
                    bl2 = HiddenOptionFlags.TRACK_ALL_PARAMETER_USAGE;
                }
            } else {
                bl2 = HiddenOptionFlags.TRACK_ALL_PARAMETER_USAGE;
            }

            if (!bl2) {
                this.analyzeParameterUsage(parameterUsageInfos);
            }

            LocalVariableInstruction localVariableInstruction2 = new LocalVariableInstruction(localVariableSlot1, LocalVariableAccessKind.OBJECT_LOAD);

            for (int i = 0; i < parameterUsageInfos.length; i++) {
                ParameterUsageInfo parameterUsageInfo2 = parameterUsageInfos[i];
                if (parameterUsageInfo2 != null && !parameterUsageInfo2.isUnpackedAtEntry()) {
                    ArrayList arrayList1 = new ArrayList();
                    arrayList1.add(localVariableInstruction2);
                    String string3 = parameterUsageInfo2.getTypeDescriptor();
                    boolean castRequired = parameterUsageInfo2.isCastRequired();
                    ClassResolver classResolver2 = classResolver1;
                    ClassMemberLookup classMemberLookup2 = classMemberLookup1;
                    List list4 = list2;
                    ConstantPool constantPool2 = constantPool1;
                    Boolean boolean2 = castRequired;
                    Boolean boolean1 = false;
                    String string1 = string3;
                    List list6 = this.buildArrayElementLoad(i, string1, boolean1, boolean2, constantPool2, list4, classMemberLookup2, classResolver2);
                    arrayList1.addAll(list6);
                    list1.add(new CodeInsertion(arrayList1, parameterUsageInfo2.getFirstUsageIndex() - 1, 1, 3));
                }
            }

            int bk = bc;
            int bm = 0;
            ArrayList arrayList2 = new ArrayList(bb);

            for (int i = 0; i < list3.size(); i++) {
                LocalVariableSlot localVariableSlot4 = this.localVariableList.getSlotAt(bk);
                String string = (String) list3.get(i);
                ParameterUsageInfo parameterUsageInfo1 = parameterUsageInfos[i];
                if (parameterUsageInfo1 == null) {
                    if (!methodInfo1.hadLocalVariableTable() && !HiddenOptionFlags.KEEP_UNUSED_PARAMETER_SLOTS) {
                        LocalVariableSlot localVariableSlot3 = this.localVariableList.removeSlot(bk);
                        this.localVariableList.renumberSlots();
                        bh += -1;
                        if (localVariableSlot3.isWideFirstHalf()) {
                            bh += -1;
                        }
                    } else {
                        bk++;
                        if (LocalVariableList.isWideType(string)) {
                            bk++;
                        }
                    }
                } else {
                    if (parameterUsageInfo1.isUnpackedAtEntry()) {
                        boolean bl1 = parameterUsageInfo1.isCastRequired();
                        ClassMemberLookup classMemberLookup3 = classMemberLookup1;
                        List list5 = list2;
                        ConstantPool constantPool3 = constantPool1;
                        Boolean boolean4 = bl1;
                        Boolean boolean3 = true;
                        String string2 = string;
                        List list8 = this.buildArrayElementLoad(bm, string2, boolean3, boolean4, constantPool3, list5, classMemberLookup3, classResolver1);
                        LocalVariableInstruction localVariableInstruction1 = LocalVariableInstruction.createForType(string, localVariableSlot4);
                        list8.add(localVariableInstruction1);
                        arrayList2.add(list8);
                    }

                    bk++;
                    if (LocalVariableList.isWideType(string)) {
                        bk++;
                    }
                }

                bm++;
            }

            if (bl) {
                if (!HiddenOptionFlags.DONT_SHUFFLE_PARAMETERS) {
                    Collections.shuffle(arrayList2, ZkmUtils.createRandom(993));
                    bl2 = HiddenOptionFlags.DONT_SHUFFLE_LOCAL_SLOTS;
                } else {
                    bl2 = HiddenOptionFlags.DONT_SHUFFLE_LOCAL_SLOTS;
                }

                if (!bl2 && bc < bh) {
                    this.localVariableList.shuffleSlots(bc, bh + 1, ZkmUtils.createRandom(994));
                    methodInfo1.setParameterSlotsShuffled();
                }
            }

            if (!arrayList2.isEmpty()) {
                ArrayList arrayList3 = new ArrayList();
                arrayList3.add(localVariableInstruction2);
                Iterator iterator = arrayList2.iterator();

                while (iterator.hasNext()) {
                    List list7 = (List) iterator.next();
                    arrayList3.addAll(list7);
                }

                arrayList3.add(SimpleInstruction.forOpcode(87));
                CodeInsertion codeInsertion = new CodeInsertion(arrayList3, -1, 0, 3);
                codeInsertion.setSequence(-2);
                list1.add(codeInsertion);
            }
        }
    }

    public void applyStringEncryption(
            List list1,
            boolean bl,
            Map map1,
            int[][] ba,
            ListMultimap listMultimap,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        RankedEntryComparator rankedEntryComparator = new RankedEntryComparator(this);
        Collections.sort(list1, rankedEntryComparator);
        int bb = list1.size();
        int bc = -1;
        if (bb != 0) {
            MethodFlowAnalyzer methodFlowAnalyzer = null;
            Iterator iterator = list1.iterator();
            ObjectPair objectPair = (ObjectPair) iterator.next();
            ConstantPoolOperand constantPoolOperand = (ConstantPoolOperand) ((RankedValue) objectPair.getFirst()).getValue();
            int bd = bb;
            this.setModified(true);
            byte be = 0;
            boolean bl1 = false;
            boolean bl2 = false;
            ArrayList arrayList = new ArrayList();
            int bf = this.instructions.size();

            for (int i = 0; i < bf; i++) {
                Instruction instruction1 = (Instruction) this.instructions.get(i);
                if (instruction1.isLabel()) {
                    LabelInstruction labelInstruction = (LabelInstruction) instruction1;
                    if (labelInstruction.hasUsageBits(1) || labelInstruction.hasUsageBits(1024)) {
                        bl1 = false;
                    }
                }

                if (instruction1 == constantPoolOperand) {
                    bd += -1;
                    EncryptedStringLocation encryptedStringLocation = (EncryptedStringLocation) objectPair.getSecond();
                    be = 0;
                    switch (LookupStrategySwitchMap.STRING_TECHNIQUE_SWITCH[encryptedStringLocation.getTechnique().ordinal()]) {
                        case 1:
                            ArrayList arrayList4 = new ArrayList();
                            if (encryptedStringLocation.hasStringIndex()) {
                                be = 1;
                                arrayList4.add(Instruction.createObjectLoad(encryptedStringLocation.getArrayLocalIndex(), this.localVariableList, 1));
                                this.appendIntConstant(encryptedStringLocation.getStringIndex(), arrayList4, constantPool1, list2, bb);
                                arrayList4.add(SimpleInstruction.forOpcode(50));
                            } else {
                                arrayList4.add(Instruction.createObjectLoad(encryptedStringLocation.getArrayLocalIndex(), this.localVariableList, 1));
                            }

                            arrayList.add(new CodeInsertion(arrayList4, i - 1, 1, be));
                            break;
                        case 2:
                            ArrayList arrayList3 = new ArrayList();
                            ResolvedFieldRef resolvedFieldRef = encryptedStringLocation.getArrayFieldRef();
                            if (encryptedStringLocation.hasStringIndex()) {
                                be = 1;
                                if (bl1) {
                                    arrayList3.add(Instruction.createObjectLoad(bc, this.localVariableList, 1));
                                } else {
                                    arrayList3.add(new ConstantRefInstruction(178, resolvedFieldRef));
                                    if (bd > 0 && (ba.length == 0 || !this.isInAnyRange(i, ba))) {
                                        if (!bl2) {
                                            bl2 = true;
                                            bc = intCounter.getValue();
                                            intCounter.incrementAndGet();
                                        }

                                        arrayList3.add(Instruction.createObjectStore(bc, this.localVariableList, 1));
                                        arrayList3.add(Instruction.createObjectLoad(bc, this.localVariableList, 1));
                                        bl1 = true;
                                    }
                                }

                                Instruction.appendIntConstant(encryptedStringLocation.getStringIndex(), arrayList3, constantPool1, list2);
                                arrayList3.add(SimpleInstruction.forOpcode(50));
                            } else {
                                arrayList3.add(new ConstantRefInstruction(178, resolvedFieldRef));
                            }

                            arrayList.add(new CodeInsertion(arrayList3, i - 1, 1, be));
                            break;
                        case 3:
                            ResolvedMethodRefConstant resolvedMethodRefConstant = encryptedStringLocation.getFirstMethodRef();
                            ResolvedMethodRefConstant resolvedMethodRefConstant1 = encryptedStringLocation.getSecondMethodRef();
                            ArrayList arrayList10 = new ArrayList();
                            arrayList10.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
                            arrayList10.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
                            arrayList.add(new CodeInsertion(arrayList10, i - 1, 0, be));
                            break;
                        case 4:
                            LabelInstruction labelInstruction1 = encryptedStringLocation.getJumpLabel();
                            ArrayList arrayList7 = new ArrayList();
                            if (encryptedStringLocation.isJsr()) {
                                arrayList7.add(new JsrInstruction(labelInstruction1));
                                be = 1;
                            } else {
                                MutableInt mutableInt = (MutableInt) map1.get(labelInstruction1);
                                int bq = mutableInt.incrementAndGet();
                                Instruction.appendIntConstant(bq, arrayList7, constantPool1, list2);
                                arrayList7.add(new GotoInstruction(labelInstruction1));
                                LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
                                arrayList7.add(labelInstruction2);
                                listMultimap.addValue(labelInstruction1, new RankedValue(bq, labelInstruction2));
                            }

                            arrayList.add(new CodeInsertion(arrayList7, i - 1, 0, be));
                            break;
                        case 5:
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ResolvedMethodRef resolvedMethodRef1 = encryptedStringLocation.getLookupMethodRef();
                            ArrayList arrayList6 = new ArrayList();
                            ArrayList arrayList9 = new ArrayList();
                            int stringIndex = encryptedStringLocation.getStringIndex();
                            int lookupKey = encryptedStringLocation.getLookupKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int bj = stringIndex ^ long1.intValue();
                                this.appendIntConstant(bj, arrayList6, constantPool1, list2, bb);
                                int bw = lookupKey ^ long1.intValue();
                                this.appendIntConstant(bw, arrayList9, constantPool1, list2, bb);
                                arrayList9.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 1));
                                arrayList9.add(SimpleInstruction.forOpcode(136));
                            } else {
                                this.appendIntConstant(stringIndex, arrayList6, constantPool1, list2, bb);
                                this.appendIntConstant(lookupKey, arrayList9, constantPool1, list2, bb);
                            }

                            ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(184, resolvedMethodRef1);
                            this.addSplitCodeInsertions(arrayList6, arrayList9, constantRefInstruction, arrayList, methodFlowAnalyzer, i);
                            break;
                        case 6:
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ResolvedMethodRef resolvedMethodRef = encryptedStringLocation.getLookupMethodRef();
                            ArrayList arrayList5 = new ArrayList();
                            ArrayList arrayList8 = new ArrayList();
                            int bo = encryptedStringLocation.getStringIndex();
                            long decryptKey = encryptedStringLocation.getDecryptKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int bv = bo ^ (int) (decryptKey & 32767L);
                                this.appendIntConstant(bv, arrayList5, constantPool1, list2, bb);
                                long bx = decryptKey ^ long1;
                                Instruction.appendLongConstant(bx, arrayList8, constantPool1, list2);
                                arrayList8.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 1));
                                arrayList8.add(SimpleInstruction.forOpcode(131));
                            } else {
                                int bu = bo ^ (int) decryptKey;
                                Instruction.appendIntConstant(bu, arrayList5, constantPool1, list2);
                                Instruction.appendLongConstant(decryptKey, arrayList8, constantPool1, list2);
                            }

                            ConstantRefInstruction constantRefInstruction1 = new ConstantRefInstruction(184, resolvedMethodRef);
                            this.addSplitCodeInsertions(arrayList5, arrayList8, constantRefInstruction1, arrayList, methodFlowAnalyzer, i);
                            break;
                        case 7:
                            ResolvedInvokeDynamic resolvedInvokeDynamic = encryptedStringLocation.getIndyEntry();
                            methodFlowAnalyzer = this.ensureFlowAnalyzer(methodFlowAnalyzer, commonSuperTypeResolver1, classHierarchyQuery);
                            ArrayList arrayList1 = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int bh = encryptedStringLocation.getStringIndex();
                            long bi = encryptedStringLocation.getDecryptKey();
                            if (long1 != null && localVariableIndex1 != null) {
                                int bt = bh ^ (int) (bi & 32767L);
                                this.appendIntConstant(bt, arrayList1, constantPool1, list2, bb);
                                long bm = bi ^ long1;
                                Instruction.appendLongConstant(bm, arrayList2, constantPool1, list2);
                                arrayList2.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), this.localVariableList, 1));
                                arrayList2.add(SimpleInstruction.forOpcode(131));
                            } else {
                                int bk = bh ^ (int) bi;
                                this.appendIntConstant(bk, arrayList1, constantPool1, list2, bb);
                                Instruction.appendLongConstant(bi, arrayList2, constantPool1, list2);
                            }

                            InvokeDynamicInstruction invokeDynamicInstruction = new InvokeDynamicInstruction(resolvedInvokeDynamic);
                            this.addSplitCodeInsertions(arrayList1, arrayList2, invokeDynamicInstruction, arrayList, methodFlowAnalyzer, i);
                    }

                    if (!iterator.hasNext()) {
                        break;
                    }

                    objectPair = (ObjectPair) iterator.next();
                    constantPoolOperand = (ConstantPoolOperand) ((RankedValue) objectPair.getFirst()).getValue();
                }
            }

            this.applyCodeInsertions(arrayList, "String Encryption", bl);
            if (!bl) {
                CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
                int maxStack = codeAttributeBody.getMaxStack();
                codeAttributeBody.setMaxStack(maxStack + be);
            }
        }
    }

    public boolean isModified() {
        return this.modified;
    }

    public void planStringConcatReplacements(
            BootstrapMethodIndex bootstrapMethodIndex1,
            ConstantPool constantPool1,
            Map map1,
            ListMultimap listMultimap,
            List list1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer = null;
        StackFrameState[] stackFrameStates = null;
        int ba = this.instructions.size();

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            if (instruction1.getOpcode() == 186) {
                ResolvedInvokeDynamic resolvedInvokeDynamic = ((InvokeDynamicInstruction) instruction1).getInvokeDynamicRef();
                BootstrapMethodEntry bootstrapMethodEntry = resolvedInvokeDynamic.getBootstrapMethod();
                if (bootstrapMethodIndex1.hasInfo(bootstrapMethodEntry)) {
                    if (methodFlowAnalyzer == null) {
                        methodFlowAnalyzer = this.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
                        stackFrameStates = methodFlowAnalyzer.copyFrameStates();
                    }

                    BootstrapMethodInfo bootstrapMethodInfo = bootstrapMethodIndex1.getInfo(bootstrapMethodEntry);
                    VerifierType[] verifierTypes = null;
                    if (i > 0) {
                        verifierTypes = stackFrameStates[i - 1].getStack();
                    }

                    CodeInsertion codeInsertion = this.buildStringConcatReplacement(
                            i, bootstrapMethodInfo, resolvedInvokeDynamic, map1, verifierTypes, constantPool1, list1
                    );
                    listMultimap.addValue(this, codeInsertion);
                }
            }
        }
    }

    public void replaceInstructions(ArrayList arrayList) throws ZkmProcessingException {
        this.instructions = arrayList;
        this.recomputeOffsets();
        if (this.getCodeLength() > 65535) {
            throw new ZkmProcessingException(
                    "Reference Obfuscation"
                            + " : Bytecode length greater than "
                            + 65535
                            + " in "
                            + this.getQualifiedMethodName()
                            + " in file '"
                            + this.getLocationName()
                            + "' (2). (new="
                            + this.getCodeLength()
                            + ") : '"
                            + "Reference Obfuscation"
                            + "'"
            );
        }

        this.setModified(true);
    }

    public int[] computeInstructionOffsets() {
        int ba = this.instructions.size();
        int[] bb = new int[ba];
        int bc = 0;

        for (int i = 0; i < ba; i++) {
            Instruction instruction1 = (Instruction) this.instructions.get(i);
            bb[i] = bc;
            bc += instruction1.getLength();
        }

        return bb;
    }

    public BranchInstruction createKeySignBranch(boolean bl, int ba, LabelInstruction labelInstruction, Random random1) {
        BranchInstruction branchInstruction;
        if (bl) {
            if (ba >= 0) {
                if (ba != 0) {
                    if (random1.nextBoolean()) {
                        branchInstruction = new BranchInstruction(156, labelInstruction);
                    } else {
                        branchInstruction = new BranchInstruction(157, labelInstruction);
                    }
                } else {
                    branchInstruction = new BranchInstruction(156, labelInstruction);
                }
            } else if (random1.nextBoolean()) {
                branchInstruction = new BranchInstruction(158, labelInstruction);
            } else {
                branchInstruction = new BranchInstruction(155, labelInstruction);
            }
        } else if (ba >= 0) {
            if (ba != 0) {
                if (random1.nextBoolean()) {
                    branchInstruction = new BranchInstruction(158, labelInstruction);
                } else {
                    branchInstruction = new BranchInstruction(155, labelInstruction);
                }
            } else {
                branchInstruction = new BranchInstruction(155, labelInstruction);
            }
        } else if (random1.nextBoolean()) {
            branchInstruction = new BranchInstruction(156, labelInstruction);
        } else {
            branchInstruction = new BranchInstruction(157, labelInstruction);
        }

        return branchInstruction;
    }

    public void collectInvokeDynamicRefs(ResolvedInvokeDynamic resolvedInvokeDynamic, Set set1, Set set2, Set set3, Set set4) {
        resolvedInvokeDynamic.collectReferencedClasses(set4, set3, set1, set2);
    }

    public SyncIndexedSet findPrologueSyncPoints(MethodFlowAnalyzer methodFlowAnalyzer, Integer integer, MutableInt mutableInt) {
        int ba = this.findMarkerLabelIndex();
        BasicBlock basicBlock;
        int bb;
        if (ba == -1) {
            basicBlock = methodFlowAnalyzer.getBlock();
            bb = basicBlock.getStartIndex();
        } else {
            basicBlock = methodFlowAnalyzer.getBlockContaining(ba);
            bb = ba;
            ZkmAssert.assertTrue(
                    basicBlock.isHeader(),
                    new String[]{"In '" + this.getOwningClass().getDisplayLocationName() + "' '" + this.getMethod().toOriginalDisplayString() + "'"}
            );
        }

        SyncIndexedSet syncIndexedSet = new SyncIndexedSet();
        StackFrameState[] stackFrameStates = methodFlowAnalyzer.copyFrameStates();
        int endIndex = basicBlock.getEndIndex();
        Instruction instruction1 = (Instruction) this.instructions.get(endIndex);
        int bd = instruction1.canFallThrough() && !instruction1.isJump() ? endIndex + 1 : endIndex;
        bd = Math.min(bd, integer);
        int be = endIndex;

        for (int i = bb; i <= bd; i++) {
            Instruction instruction2 = (Instruction) this.instructions.get(i);
            if (instruction2.isLabel()) {
                LabelInstruction labelInstruction = (LabelInstruction) instruction2;
                if (labelInstruction.hasUsageBits(1) && i < bd) {
                    bd = i;
                    break;
                }

                if (labelInstruction.hasUsageBits(256)) {
                    be = i;
                    bd = i;
                    break;
                }
            }
        }

        mutableInt.setValue(be);
        bd = bd == 0 ? bd : bd - 1;
        boolean bl = bb == 0;
        int bg = -1;

        for (int i = bb; i <= bd; i++) {
            Instruction instruction3 = (Instruction) this.instructions.get(i);
            bl = bl && instruction3.isLabel();
            if (stackFrameStates[i].getStack().length == 0 && !instruction3.isJump() && instruction3.getOpcode() != 191) {
                Integer integer1 = bl ? IntegerCache.MINUS_ONE : integerCache.valueOf(i);
                if (integer1 != IntegerCache.MINUS_ONE) {
                    if (bg != -1) {
                        if (i <= bg + 1) {
                            continue;
                        }

                        syncIndexedSet.add(integer1);
                    } else {
                        syncIndexedSet.add(integer1);
                    }
                } else {
                    syncIndexedSet.add(integer1);
                }

                bg = integer1;
            }
        }

        return syncIndexedSet;
    }

    public MethodFlowAnalyzer ensureFlowAnalyzer(
            MethodFlowAnalyzer methodFlowAnalyzer, CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        MethodFlowAnalyzer methodFlowAnalyzer1 = methodFlowAnalyzer;
        if (methodFlowAnalyzer1 == null) {
            methodFlowAnalyzer1 = this.ensureFlowAnalyzer(methodFlowAnalyzer1, commonSuperTypeResolver1, classHierarchyQuery, false);
        }

        return methodFlowAnalyzer1;
    }

    public void relocateAddedLocals(List list1) {
        int ba = list1.size();
        int argumentSlotCount = ((MethodInfo) this.getParent().getParent()).getArgumentSlotCount();
        LocalVariableList localVariableList1 = this.localVariableList;
        Integer integer = argumentSlotCount;
        localVariableList1.moveTrailingSlots(ba, integer);
    }

    public List buildPredicateFieldUpdate(
            OpaquePredicateField opaquePredicateField,
            ConstantRefInstruction constantRefInstruction,
            NestedMultiMap nestedMultiMap,
            List list1,
            ConstantPool constantPool1,
            List list2,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList(8);
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        ConstantRefInstruction constantRefInstruction1 = opaquePredicateField.createWriteInstruction(constantPool1, list1);
        this.recordMemberReference(constantRefInstruction1, nestedMultiMap);
        if (opaquePredicateField.isBoolean()) {
            arrayList.add(constantRefInstruction);
            arrayList.add(opaquePredicateField.createBranchIfSet(labelInstruction));
            arrayList.add(SimpleInstruction.forOpcode(3));
            arrayList.add(new GotoInstruction(labelInstruction1));
            arrayList.add(labelInstruction);
            arrayList.add(SimpleInstruction.forOpcode(4));
            arrayList.add(labelInstruction1);
            arrayList.add(constantRefInstruction1);
        } else if (opaquePredicateField.isReferenceType()) {
            arrayList.addAll(
                    FlowObfuscationManager.buildFieldInitializer(opaquePredicateField, list1, constantPool1, false, classMemberLookup1, classResolver1, random1)
            );
            arrayList.add(constantRefInstruction1);
        } else {
            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
            int ba = codeAttributeBody.getMaxLocals() + 1;
            codeAttributeBody.setMaxLocals(ba);
            int bb = ba - 1;
            list2.add(integerCache.valueOf(bb));
            Instruction instruction1 = Instruction.createIntStore(bb, this.localVariableList, 2);
            Instruction instruction2 = Instruction.createIntLoad(bb, this.localVariableList, 2);
            arrayList.add(constantRefInstruction);
            arrayList.add(instruction1);
            arrayList.add(Instruction.createIntIncrement(bb, 1, this.localVariableList, 2));
            arrayList.add(instruction2);
            arrayList.add(constantRefInstruction1);
        }

        return arrayList;
    }

    public String getQualifiedMethodName() {
        CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.getParent();
        return codeAttributeBody != null ? codeAttributeBody.getMethodDescription() : "<NO_PARENT_YET>";
    }
}
