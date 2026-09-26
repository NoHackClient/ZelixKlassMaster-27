package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.classfile.insn.CodeInsertion;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.IntCounter;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.classfile.insn.MethodFlowAnalyzer;
import com.zelix.klassmaster.classfile.insn.StackMapTableBuilder;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.engine.ProcessingStatistics;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.MethodAnalysisException;
import com.zelix.klassmaster.exceptions.StackAnalysisException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.constants.IntegerEncryptionExclusions;
import com.zelix.klassmaster.obfuscator.constants.LongEncryptionExclusionHandler;
import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;
import com.zelix.klassmaster.obfuscator.parameters.MethodParamChangeNode;
import com.zelix.klassmaster.obfuscator.references.ReferenceObfuscator;
import com.zelix.klassmaster.obfuscator.string.StringEncryptionExclusionSpec;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayCollection;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.PairValueMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class CodeAttributeBody extends Attribute {
    private int maxStack;
    private int maxLocals;
    private MethodBytecode bytecode;
    private int exceptionTableLength;
    private ExceptionTableEntry[] exceptionTable;
    private int attributeCount;
    private Attribute[] attributes;

    public void applyParameterObfuscation(
            List list1,
            Map map1,
            ConstantPool constantPool1,
            List list2,
            LocalVariableIndex localVariableIndex1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        this.bytecode.applyParameterPacking(list1, map1, constantPool1, list2, localVariableIndex1, classMemberLookup1, classResolver1, bl);
    }

    @Override
    public void collectUsedConstants(char bc, int ba, UsedConstantsCollector usedConstantsCollector, char bd) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
        int bb = 0;

        while (true) {
            if (bb >= this.exceptionTable.length) {
                bb = 0;
                break;
            }

            this.exceptionTable[bb].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            if (ba <= 0) {
                break;
            }

            bb++;
        }

        while (bb < this.attributes.length) {
            this.attributes[bb].collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
            bb++;
        }

        if (this.bytecode != null) {
            this.bytecode.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector, '鞍');
        }
    }

    public int getMaxStack() {
        return this.maxStack;
    }

    public int indexOfStackMapTable() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof StackMapTableAttribute) {
                return i;
            }
        }

        return -1;
    }

    public ListMultimap analyzeReflectionCalls(
            Map map1,
            ClasspathClassLoader classpathClassLoader1,
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            ClassMemberLookup classMemberLookup1,
            Map map6,
            TwoKeyMap twoKeyMap,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap1,
            TwoKeyMap twoKeyMap2,
            TwoKeyMap twoKeyMap3,
            Set set1,
            List list1,
            List list2,
            boolean bl
    ) throws ZkmException, IOException {
        return this.bytecode
                .analyzeReflectionCalls(
                        map1,
                        classpathClassLoader1,
                        nestedMultiMap,
                        nestedMultiMap1,
                        nestedMultiMap2,
                        twoKeyMap3,
                        commonSuperTypeResolver1,
                        map2,
                        map3,
                        map4,
                        map5,
                        classMemberLookup1,
                        map6,
                        twoKeyMap,
                        listMultimap,
                        twoKeyMap1,
                        twoKeyMap2,
                        set1,
                        list1,
                        list2,
                        bl
                );
    }

    public void rebuildStackMapTable(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, ConstantPool constantPool1, List list1
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            MethodFlowAnalyzer methodFlowAnalyzer = this.bytecode.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery, true);
            if (!methodFlowAnalyzer.hasFrameLabels()) {
                this.bytecode.setModified(false);
                return;
            }

            if (!this.bytecode.isModified() && !HiddenOptionFlags.REBUILD_ALL_STACK_MAPS) {
                return;
            }

            StackMapTableAttribute stackMapTableAttribute = this.getStackMapTableAttribute();
            ConstantUtf8 constantUtf8;
            Set set1;
            if (stackMapTableAttribute != null) {
                UsedConstantsCollector usedConstantsCollector = new UsedConstantsCollector(31);
                UsedConstantsCollector usedConstantsCollector1 = usedConstantsCollector;
                stackMapTableAttribute.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector1, '鞍');
                set1 = ZkmUtils.createHashSetFrom(usedConstantsCollector.getUsedClasses());
                constantUtf8 = stackMapTableAttribute.getNameConstant();
            } else {
                constantUtf8 = constantPool1.createUtf8Constant("StackMapTable", list1);
                set1 = Collections.emptySet();
            }

            StackMapTableAttribute stackMapTableAttribute1 = new StackMapTableAttribute(this, constantUtf8);
            StackMapTableBuilder stackMapTableBuilder = new StackMapTableBuilder(this.bytecode, methodFlowAnalyzer);
            StackMapTableFrame[] stackMapTableFrames;
            if (HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("1") || this.maxLocals == 0 || !methodFlowAnalyzer.isLocalLivenessConsistent()) {
                stackMapTableFrames = stackMapTableBuilder.buildFrames(stackMapTableAttribute1, constantPool1, set1, list1, false, false);
            } else if (HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("2")) {
                stackMapTableFrames = stackMapTableBuilder.buildFrames(stackMapTableAttribute1, constantPool1, set1, list1, true, false);
            } else if (HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("5") && this.maxLocals > 1 && this.maxLocals <= 20) {
                stackMapTableFrames = stackMapTableBuilder.buildSmallestFrames(this.maxLocals, stackMapTableAttribute1, constantPool1, set1, list1);
            } else {
                ArrayList arrayList = new ArrayList(list1);

                StackMapTableFrame[] stackMapTableFrames1;
                try {
                    stackMapTableFrames1 = stackMapTableBuilder.buildFrames(
                            stackMapTableAttribute1, constantPool1, ZkmUtils.createHashSetFrom(set1), arrayList, false, false
                    );
                } catch (AssertionFailedException assertionFailedException) {
                    throw assertionFailedException;
                }

                ArrayList arrayList1 = new ArrayList(list1);
                StackMapTableFrame[] stackMapTableFrames2 = stackMapTableBuilder.buildFrames(
                        stackMapTableAttribute1, constantPool1, ZkmUtils.createHashSetFrom(set1), arrayList1, true, false
                );
                int ba = sumFrameSizes(stackMapTableFrames1);
                int bb = sumFrameSizes(stackMapTableFrames2);
                StackMapTableFrame stackMapTableFrame = stackMapTableFrames1[stackMapTableFrames1.length - 1];
                StackMapFrameKind stackMapFrameKind = stackMapTableFrame.getFrameKind();
                Instruction instruction1 = methodFlowAnalyzer.getBlockContaining(stackMapTableFrame.getLabel().getInstructionIndex())
                        .findLastRealInstruction(this.bytecode.getInstructions());
                if (!HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("3")
                        && stackMapFrameKind != StackMapFrameKind.SAME
                        && stackMapFrameKind != StackMapFrameKind.SAME_EXTENDED
                        && stackMapFrameKind != StackMapFrameKind.SAME_LOCALS_1_STACK_ITEM
                        && stackMapFrameKind != StackMapFrameKind.SAME_LOCALS_1_STACK_ITEM_EXTENDED
                        && stackMapFrameKind != StackMapFrameKind.CHOP
                        && instruction1.isReturn()) {
                    ArrayList arrayList2 = new ArrayList(list1);
                    StackMapTableFrame[] stackMapTableFrames3 = stackMapTableBuilder.buildFrames(
                            stackMapTableAttribute1, constantPool1, ZkmUtils.createHashSetFrom(set1), arrayList2, false, true
                    );
                    int bc = sumFrameSizes(stackMapTableFrames3);
                    if (ba < bb && ba < bc) {
                        ZkmUtils.mergeUniqueInto(arrayList, list1);
                        stackMapTableFrames = stackMapTableFrames1;
                    } else if (bb < ba && bb < bc) {
                        ZkmUtils.mergeUniqueInto(arrayList1, list1);
                        stackMapTableFrames = stackMapTableFrames2;
                    } else {
                        ZkmUtils.mergeUniqueInto(arrayList2, list1);
                        stackMapTableFrames = stackMapTableFrames3;
                    }
                } else if (bb < ba) {
                    ZkmUtils.mergeUniqueInto(arrayList1, list1);
                    stackMapTableFrames = stackMapTableFrames2;
                } else {
                    ZkmUtils.mergeUniqueInto(arrayList, list1);
                    stackMapTableFrames = stackMapTableFrames1;
                }
            }

            stackMapTableAttribute1.setFrames(stackMapTableFrames);
            if (stackMapTableAttribute != null) {
                int bd = this.indexOfStackMapTable();
                this.attributes[bd] = stackMapTableAttribute1;
            } else if (stackMapTableAttribute1.getFrameCount() > 0) {
                Attribute[] attributes1 = new Attribute[this.attributeCount + 1];
                System.arraycopy(this.attributes, 0, attributes1, 0, this.attributeCount);
                attributes1[this.attributeCount] = stackMapTableAttribute1;
                this.attributes = attributes1;
                this.attributeCount++;
            }

            this.bytecode.setModified(false);
            methodFlowAnalyzer.release();
        }
    }

    public final void resolveAttributeReferences(
            ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            }
        }
    }

    public int obfuscateFlow(
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
        return this.bytecode != null
                ? this.bytecode.planFlowObfuscationJumps(listMultimap, commonSuperTypeResolver1, scriptEnvironment1, classHierarchyQuery, ba, bl, bl1, map1, random1)
                : 0;
    }

    public void addOpaquePredicateInitialization(
            OpaquePredicateField opaquePredicateField,
            NestedMultiMap nestedMultiMap,
            List list1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            Random random1
    ) throws ZkmException, IOException {
        this.bytecode
                .insertOpaquePredicateInit(
                        opaquePredicateField, nestedMultiMap, list1, commonSuperTypeResolver1, classMemberLookup1, staticInitCalleeAnalyzer1, random1
                );
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        super.write(dataOutputStream);
        dataOutputStream.writeShort(this.maxStack);
        dataOutputStream.writeShort(this.maxLocals);
        this.bytecode.writeCode(dataOutputStream);
        dataOutputStream.writeShort(this.exceptionTableLength);

        for (int i = 0; i < this.exceptionTableLength; i++) {
            this.exceptionTable[i].writeTo(dataOutputStream);
        }

        dataOutputStream.writeShort(this.attributeCount);

        for (int i = 0; i < this.attributeCount; i++) {
            this.attributes[i].write(dataOutputStream);
        }
    }

    public void removeLocalVariableTables() {
        this.removeAttributesOfType(LocalVariableTableBase.class);
    }

    public boolean hasConditionalBranches() {
        return this.bytecode.hasConditionalBranches();
    }

    public void setMaxLocals(int maxLocals) throws ZkmProcessingException {
        if (maxLocals <= 65535) {
            this.maxLocals = maxLocals;
        } else {
            throw new ZkmProcessingException("Local variable count greater than 65535 in " + this.getMethodDescription() + ". (" + maxLocals + ")");
        }
    }

    public void applyLongEncryption(
            List list1,
            boolean bl,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            int ba = 0;
            if (this.exceptionTableLength > 0) {
                for (ExceptionTableEntry exceptionTableEntry1 : this.exceptionTable) {
                    if (exceptionTableEntry1.isNewlyCreated()) {
                        ba++;
                    }
                }
            }

            int[][] bb;
            if (ba > 0) {
                this.bytecode.recomputeOffsets();
                int bc = 0;
                bb = new int[ba][2];

                for (int i = 0; i < this.exceptionTableLength; i++) {
                    ExceptionTableEntry exceptionTableEntry2 = this.exceptionTable[i];
                    if (exceptionTableEntry2.isNewlyCreated()) {
                        bb[bc][0] = exceptionTableEntry2.getStartIndex();
                        bb[bc][1] = exceptionTableEntry2.getEndIndex();
                        bc++;
                    }
                }
            } else {
                bb = new int[0][0];
            }

            this.bytecode
                    .applyLongEncryption(list1, bl, bb, long1, localVariableIndex1, intCounter, constantPool1, list2, commonSuperTypeResolver1, classHierarchyQuery);
            if (!bl && this.maxLocals < intCounter.getValue()) {
                this.setMaxLocals(intCounter.getValue());
            }
        }
    }

    public void setCodeModified() {
        if (this.bytecode != null) {
            this.bytecode.setModified(true);
        }
    }

    public StackMapAttribute getStackMapAttribute() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof StackMapAttribute) {
                return (StackMapAttribute) this.attributes[i];
            }
        }

        return null;
    }

    public boolean removeDeadInstructions(MethodFlowAnalyzer methodFlowAnalyzer, boolean bl, ScriptEnvironment scriptEnvironment1) {
        int[] ba = methodFlowAnalyzer.findDeadCodeIndices();
        if (ba.length <= 0) {
            return false;
        }

        SetMultiMap setMultiMap = new SetMultiMap();
        LineNumberTableAttribute lineNumberTableAttribute = null;
        LocalVariableTableAttribute localVariableTableAttribute = null;
        LocalVariableTypeTableAttribute localVariableTypeTableAttribute = null;
        StackMapAttribute stackMapAttribute = null;
        StackMapTableAttribute stackMapTableAttribute = null;
        this.bytecode.collectLabelReferences(setMultiMap);

        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof LineNumberTableAttribute) {
                lineNumberTableAttribute = (LineNumberTableAttribute) this.attributes[i];
                lineNumberTableAttribute.registerLabelTargets(setMultiMap);
            } else if (this.attributes[i] instanceof LocalVariableTableAttribute) {
                localVariableTableAttribute = (LocalVariableTableAttribute) this.attributes[i];
                localVariableTableAttribute.registerLabelTargets(setMultiMap);
            } else if (this.attributes[i] instanceof LocalVariableTypeTableAttribute) {
                localVariableTypeTableAttribute = (LocalVariableTypeTableAttribute) this.attributes[i];
                localVariableTableAttribute.registerLabelTargets(setMultiMap);
            } else if (this.attributes[i] instanceof StackMapTableAttribute) {
                stackMapTableAttribute = (StackMapTableAttribute) this.attributes[i];
                stackMapTableAttribute.collectLabelReferences(setMultiMap);
            } else if (this.attributes[i] instanceof StackMapAttribute) {
                stackMapAttribute = (StackMapAttribute) this.attributes[i];
                stackMapAttribute.collectLabelReferences(setMultiMap);
            }
        }

        for (int i = 0; i < this.exceptionTableLength; i++) {
            this.exceptionTable[i].registerLabelTargets(setMultiMap);
        }

        boolean bl1 = ((MethodInfo) this.getParent()).collectTypeAnnotationTargets(setMultiMap);
        HashSet hashSet = this.bytecode.collectLabelsAt(ba);
        if (lineNumberTableAttribute != null) {
            lineNumberTableAttribute.removeEntriesAtLabels(hashSet, setMultiMap);
        }

        if (localVariableTableAttribute != null) {
            localVariableTableAttribute.removeEntriesAtLabels(hashSet, setMultiMap);
        }

        if (localVariableTypeTableAttribute != null) {
            localVariableTypeTableAttribute.removeEntriesAtLabels(hashSet, setMultiMap);
        }

        if (stackMapAttribute != null) {
            stackMapAttribute.removeFramesAtDeadLabels(hashSet, setMultiMap);
        }

        if (stackMapTableAttribute != null) {
            stackMapTableAttribute.removeFramesAtDeadLabels(hashSet, setMultiMap);
        }

        ArrayList arrayList = new ArrayList(this.exceptionTable.length);

        for (int i = 0; i < this.exceptionTableLength; i++) {
            if (!this.exceptionTable[i].unlinkIfRangeRemoved(hashSet, ba, setMultiMap)) {
                arrayList.add(this.exceptionTable[i]);
            }
        }

        if (this.exceptionTableLength > arrayList.size()) {
            ExceptionTableEntry[] exceptionTableEntrys = new ExceptionTableEntry[arrayList.size()];
            this.exceptionTable = ((com.zelix.klassmaster.classfile.attribute.ExceptionTableEntry[]) (arrayList.toArray(exceptionTableEntrys)));
            this.exceptionTableLength = this.exceptionTable.length;
        }

        if (bl1) {
            ((MethodInfo) this.getParent()).pruneTypeAnnotationTargets(hashSet, setMultiMap);
        }

        this.bytecode.removeDeadInstructions(ba, bl, scriptEnvironment1);
        methodFlowAnalyzer.release();
        return true;
    }

    public void collectEncryptableStrings(MultiMapTable multiMapTable, MethodInfo methodInfo1, Set set1) {
        this.bytecode.collectStringEncryptionCandidates(multiMapTable, methodInfo1, set1);
    }

    public String getMethodName() {
        return ((AbstractMethodInfo) this.getParent()).getSourceName();
    }

    public String getMethodDeclaration() {
        return ((AbstractMethodInfo) this.getParent()).toOriginalDisplayString();
    }

    public CodeAttributeBody(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            ListMultimap listMultimap7,
            PrintWriter printWriter,
            ThreeKeyMultiMap threeKeyMultiMap,
            ListMultimap listMultimap8
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        this.maxStack = classFileInputStream.readUnsignedShort();
        this.maxLocals = classFileInputStream.readUnsignedShort();
        this.bytecode = new MethodBytecode(
                this, classFileInputStream, listMultimap8, listMultimap2, listMultimap3, listMultimap4, listMultimap5, listMultimap6, listMultimap7
        );
        this.exceptionTableLength = classFileInputStream.readUnsignedShort();
        this.exceptionTable = new ExceptionTableEntry[this.exceptionTableLength];

        for (int i = 0; i < this.exceptionTableLength; i++) {
            this.exceptionTable[i] = new ExceptionTableEntry(this, classFileInputStream, listMultimap8, listMultimap5);
        }

        this.attributeCount = classFileInputStream.readUnsignedShort();
        this.attributes = new Attribute[this.attributeCount];

        for (int i = 0; i < this.attributeCount; i++) {
            this.attributes[i] = Attribute.readAttribute(
                    this,
                    classFileInputStream,
                    this.bytecode.getLocalVariableList(),
                    listMultimap,
                    listMultimap1,
                    listMultimap2,
                    listMultimap3,
                    listMultimap4,
                    listMultimap5,
                    listMultimap6,
                    listMultimap7,
                    printWriter,
                    listMultimap8,
                    threeKeyMultiMap
            );
            if (this.attributes[i] instanceof AbstractStackMapAttribute) {
                ((ProgramClass) this.getOwningClass()).setHasStackMaps(true);
            } else if (this.attributes[i] instanceof LineNumberTableAttribute) {
                ((ProgramClass) this.getOwningClass()).setHasLineNumberTables(true);
            }
        }
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    public int indexOfStackMap() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof StackMapAttribute) {
                return i;
            }
        }

        return -1;
    }

    public void collectReachableMethods(Set set1, Set set2) throws ZkmProcessingException {
        this.bytecode.markReachableMethods(set1, set2);
    }

    public void applyIntegerEncryption(
            List list1,
            boolean bl,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            int ba = 0;
            if (this.exceptionTableLength > 0) {
                for (ExceptionTableEntry exceptionTableEntry1 : this.exceptionTable) {
                    if (exceptionTableEntry1.isNewlyCreated()) {
                        ba++;
                    }
                }
            }

            int[][] bb;
            if (ba > 0) {
                this.bytecode.recomputeOffsets();
                int bc = 0;
                bb = new int[ba][2];

                for (int i = 0; i < this.exceptionTableLength; i++) {
                    ExceptionTableEntry exceptionTableEntry2 = this.exceptionTable[i];
                    if (exceptionTableEntry2.isNewlyCreated()) {
                        bb[bc][0] = exceptionTableEntry2.getStartIndex();
                        bb[bc][1] = exceptionTableEntry2.getEndIndex();
                        bc++;
                    }
                }
            } else {
                bb = new int[0][0];
            }

            this.bytecode
                    .applyIntegerEncryption(list1, bl, bb, long1, localVariableIndex1, intCounter, constantPool1, list2, commonSuperTypeResolver1, classHierarchyQuery);
            if (!bl && this.maxLocals < intCounter.getValue()) {
                this.setMaxLocals(intCounter.getValue());
            }
        }
    }

    public void printExceptionTable(PrintWriter printWriter) {
        printWriter.println("");
        StringBuffer stringBuffer = new StringBuffer(2);

        for (int i = 0; i < 2; i++) {
            stringBuffer.append("   ");
        }

        for (int i = 0; i < this.exceptionTable.length; i++) {
            this.exceptionTable[i].printEntry(printWriter, stringBuffer);
        }
    }

    public boolean isMethodPrivate() {
        return ((AbstractMethodInfo) this.getParent()).isStrictlyPrivate();
    }

    public boolean hasLongConstants() {
        return this.bytecode.hasLongConstants();
    }

    public void applyCodeInsertion(CodeInsertion codeInsertion, int ba, int bb) throws ZkmProcessingException {
        if (this.bytecode != null) {
            if (this.getMaxStack() < ba) {
                this.setMaxStack(ba);
            }

            if (this.getMaxLocals() < bb) {
                this.setMaxLocals(bb);
            }

            this.bytecode.applyCodeInsertion(codeInsertion, "Method Parameter List Changing");
        }
    }

    public ConstantPoolEntry getFirstLoadedConstant() {
        return this.bytecode != null ? this.bytecode.getLeadingConstant() : null;
    }

    public boolean hasIntegerConstants() {
        return this.bytecode.hasIntConstants();
    }

    public void collectExcludedFieldStrings(Set set1, StringEncryptionExclusionSpec stringEncryptionExclusionSpec) {
        this.bytecode.collectExcludedStringConstants(set1, stringEncryptionExclusionSpec);
    }

    public boolean isStaticInitializer() {
        return ((AbstractMethodInfo) this.getParent()).isStaticInitializer();
    }

    public boolean hasLocalVariableTable() {
        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof LocalVariableTableBase) {
                return true;
            }
        }

        return false;
    }

    public void retainParameterLocalVariables() {
        List list1 = this.getNormalizedParameterTypes();
        if (list1 != null && list1.size() > 0) {
            int ba = this.isMethodStatic() ? 0 : 1;
            HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(list1.size()));

            for (int i = 0; i < list1.size(); i++) {
                String string = (String) list1.get(i);
                hashSet.add(integerCache.valueOf(ba));
                ba++;
                if (string.equals("D") || string.equals("J")) {
                    ba++;
                }
            }

            int bc = this.attributes.length;

            for (int i = 0; i < bc; i++) {
                if (this.attributes[i] instanceof LocalVariableTableBase) {
                    ((LocalVariableTableBase) this.attributes[i]).retainEntriesForLocals(hashSet);
                }
            }
        } else {
            this.removeLocalVariableTables();
        }
    }

    public void collectUsedBootstrapMethods(Set set1) {
        this.bytecode.collectBootstrapMethodEntries(set1);
    }

    public void applyOpaquePredicateFlow(
            Set set1,
            Set set2,
            OpaquePredicateField opaquePredicateField,
            OpaquePredicateField opaquePredicateField1,
            NestedMultiMap nestedMultiMap,
            ArrayList arrayList,
            ConstantPool constantPool1,
            ListMultimap listMultimap,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            this.bytecode
                    .applyFlowObfuscation(
                            set1,
                            set2,
                            opaquePredicateField,
                            opaquePredicateField1,
                            nestedMultiMap,
                            arrayList,
                            constantPool1,
                            listMultimap,
                            map1,
                            classMemberLookup1,
                            commonSuperTypeResolver1,
                            classResolver1,
                            random1
                    );
        }
    }

    public void collectObfuscatableReferences(
            TwoKeySetMultiMap twoKeySetMultiMap,
            ReferenceObfuscator referenceObfuscator,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        this.bytecode.collectReferenceObfuscationSites(twoKeySetMultiMap, referenceObfuscator, commonSuperTypeResolver1, classHierarchyQuery);
    }

    public int[] insertInstructions(List list1, int ba, int bb, int bc, String string, List list2) throws ZkmProcessingException {
        if (this.bytecode != null) {
            if (this.getMaxStack() < bb) {
                this.setMaxStack(bb);
            }

            int maxLocals = this.getMaxLocals();
            this.setMaxLocals(maxLocals + ba);
            return this.bytecode.insertAtMethodStart(list1, bc, string, list2);
        } else {
            return null;
        }
    }

    public void recordStatistics(ProcessingStatistics processingStatistics1) {
        this.bytecode.recordOpcodeStatistics(processingStatistics1);
    }

    public boolean hasStringConstants() {
        return this.bytecode.hasStringConstants();
    }

    public void collectCallGraph(SetMultiMap setMultiMap, Set set1) {
        this.bytecode.recordCallGraphEdges(setMultiMap, set1);
    }

    public List getNormalizedParameterTypes() {
        return ((AbstractMethodInfo) this.getParent()).getWidenedParameterTypes();
    }

    public final void collectAttributeReferences(Set set1) {
        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).collectReferencedClasses(set1);
            }
        }
    }

    public void collectExcludedFieldIntegers(Set set1, IntegerEncryptionExclusions integerEncryptionExclusions) {
        this.bytecode.collectExcludedIntConstants(set1, integerEncryptionExclusions);
    }

    public static int sumFrameSizes(StackMapFrame[] stackMapFrames) {
        int ba = 0;

        for (StackMapFrame stackMapFrame : stackMapFrames) {
            ba += stackMapFrame.getFrameSize();
        }

        return ba;
    }

    public boolean hasLineNumber(int ba) {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof LineNumberTableAttribute) {
                return ((LineNumberTableAttribute) this.attributes[i]).hasLineNumber(ba);
            }
        }

        return false;
    }

    public void collectSerialPersistentFields(List list1, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        if (this.bytecode != null) {
            this.bytecode.collectSerialPersistentFields(list1, classMemberLookup1);
        }
    }

    public int getCodeSize() {
        return this.bytecode.getSerializedLength();
    }

    public void removeAttributesOfType(Class class1) {
        int ba = this.attributes.length;
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            if (!class1.isInstance(this.attributes[i])) {
                arrayList.add(this.attributes[i]);
            }
        }

        int bc = arrayList.size();
        if (bc < ba) {
            this.attributes = new Attribute[bc];
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(this.attributes)));
        }

        this.attributeCount = this.attributes.length;
        this.length = this.getLength();
    }

    public void setExceptionHandlers(ExceptionHandlerSpec[] exceptionHandlerSpecs) {
        this.exceptionTableLength = exceptionHandlerSpecs.length;
        this.exceptionTable = new ExceptionTableEntry[this.exceptionTableLength];

        for (int i = 0; i < this.exceptionTableLength; i++) {
            ExceptionHandlerSpec exceptionHandlerSpec = exceptionHandlerSpecs[i];
            this.exceptionTable[i] = new ExceptionTableEntry(
                    this,
                    exceptionHandlerSpec.getCatchType(),
                    exceptionHandlerSpec.getStartLabel(),
                    exceptionHandlerSpec.getEndLabel(),
                    exceptionHandlerSpec.getHandlerLabel()
            );
        }
    }

    public final void updateAttributesAfterMethodRename() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyMethodRenames();
            }
        }
    }

    public void obfuscateLocalVariableNames() {
        int ba = this.attributes.length;

        for (int i = 0; i < ba; i++) {
            if (this.attributes[i] instanceof LocalVariableTableBase) {
                ((LocalVariableTableBase) this.attributes[i]).obfuscateNames();
            }
        }
    }

    public boolean regenerateStackMapIfNeeded(
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            ConstantPool constantPool1,
            List list1,
            Map map1,
            boolean bl,
            boolean bl1
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            if (!this.bytecode.adjustLdcWidths(map1) && !this.bytecode.isModified()) {
                return false;
            }

            this.bytecode.recomputeOffsets();
            if (bl) {
                if (bl1) {
                    this.rebuildStackMap(commonSuperTypeResolver1, classHierarchyQuery, constantPool1, list1);
                } else {
                    this.rebuildStackMapTable(commonSuperTypeResolver1, classHierarchyQuery, constantPool1, list1);
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean analyzeExceptionObfuscation(
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            List list1,
            List list2,
            List list3,
            List list4,
            PairValueMap pairValueMap,
            boolean bl,
            boolean bl1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        boolean bl2 = false;
        if (this.bytecode != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList1 = new ArrayList();
            this.bytecode
                    .addFakeExceptionHandlers(
                            commonSuperTypeResolver1,
                            classHierarchyQuery,
                            list1,
                            list2,
                            list3,
                            new ArrayList(new ArrayCollection(this.exceptionTable)),
                            list4,
                            arrayList,
                            arrayList1,
                            bl,
                            bl1,
                            ignoreMissingReferencesSpec1
                    );
            if (arrayList1.size() > 0 || arrayList.size() > 0) {
                pairValueMap.putPair(this.bytecode, arrayList1, arrayList);
                bl2 = true;
            }
        }

        return bl2;
    }

    public void setMaxStack(int maxStack) throws ZkmProcessingException {
        if (maxStack <= 65535) {
            this.maxStack = maxStack;
        } else {
            throw new ZkmProcessingException("Stack size greater than 65535 in " + this.getMethodDescription() + ". (" + maxStack + ")");
        }
    }

    public final void updateAttributesAfterFieldRename() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyFieldRenames();
            }
        }
    }

    public void removeLocalVariableTypeTable() {
        int ba = this.attributes.length;
        Vector vector = new Vector();

        for (int i = 0; i < ba; i++) {
            if (!(this.attributes[i] instanceof LocalVariableTypeTableAttribute)) {
                if (this.attributes[i] instanceof TypeAnnotationsAttribute) {
                    TypeAnnotationsAttribute typeAnnotationsAttribute = (TypeAnnotationsAttribute) this.attributes[i];
                    typeAnnotationsAttribute.removeSignatureDependentAnnotations();
                    if (!typeAnnotationsAttribute.isEmpty()) {
                        vector.add(this.attributes[i]);
                    }
                } else {
                    vector.add(this.attributes[i]);
                }
            }
        }

        int bc = vector.size();
        if (bc < ba) {
            this.attributes = new Attribute[bc];
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (vector.toArray(this.attributes)));
        }

        this.attributeCount = this.attributes.length;
        this.length = this.getLength();
    }

    public AbstractMethodInfo getOwnerMethod() {
        return (AbstractMethodInfo) this.getParent();
    }

    public boolean isMethodStatic() {
        return ((AbstractMethodInfo) this.getParent()).isStatic();
    }

    public boolean removeDeadCode(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, boolean bl, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl1 = false;
        if (this.bytecode != null) {
            MethodFlowAnalyzer methodFlowAnalyzer = this.bytecode.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
            bl1 = this.removeDeadInstructions(methodFlowAnalyzer, bl, scriptEnvironment1);
            if (!methodFlowAnalyzer.isClosed()) {
                methodFlowAnalyzer.release();
            }
        }

        return bl1;
    }

    public CodeAttributeBody(ConstantUtf8 constantUtf8, int maxStack, int maxLocals, MethodBytecode methodBytecode1, ExceptionHandlerSpec[] exceptionHandlerSpecs) {
        super(null, constantUtf8, methodBytecode1.getSerializedLength() + exceptionHandlerSpecs.length * 8);
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.bytecode = methodBytecode1;
        this.bytecode.setParent(this);
        this.exceptionTableLength = exceptionHandlerSpecs.length;
        this.exceptionTable = new ExceptionTableEntry[this.exceptionTableLength];

        for (int i = 0; i < this.exceptionTableLength; i++) {
            ExceptionHandlerSpec exceptionHandlerSpec = exceptionHandlerSpecs[i];
            this.exceptionTable[i] = new ExceptionTableEntry(
                    this,
                    exceptionHandlerSpec.getCatchType(),
                    exceptionHandlerSpec.getStartLabel(),
                    exceptionHandlerSpec.getEndLabel(),
                    exceptionHandlerSpec.getHandlerLabel()
            );
        }

        this.attributeCount = 0;
        this.attributes = new Attribute[this.attributeCount];
    }

    public void updateLocalVariableTypes(
            ConstantPool constantPool1, ArrayList arrayList, CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            ListMultimap listMultimap = new ListMultimap();
            MethodFlowAnalyzer methodFlowAnalyzer = null;
            int ba = this.attributes.length;

            for (int i = 0; i < ba; i++) {
                if (this.attributes[i] instanceof LocalVariableTableAttribute) {
                    LocalVariableTableAttribute localVariableTableAttribute = (LocalVariableTableAttribute) this.attributes[i];
                    if (methodFlowAnalyzer == null) {
                        methodFlowAnalyzer = this.bytecode.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
                    }

                    localVariableTableAttribute.addMissingEntries(
                            this.maxLocals, methodFlowAnalyzer, constantPool1, arrayList, this.bytecode.getLocalVariableList(), listMultimap
                    );
                }
            }

            if (methodFlowAnalyzer != null) {
                methodFlowAnalyzer.release();
            }

            if (listMultimap.getKeyCount() > 0) {
                this.bytecode.insertBranchTargetLabels(listMultimap);
            }
        }
    }

    public CodeAttributeBody(RawCodeAttribute rawCodeAttribute) throws ZkmProcessingException, IOException {
        super(rawCodeAttribute.getParent(), rawCodeAttribute.getNameConstant(), rawCodeAttribute.getLength());
        this.maxStack = rawCodeAttribute.getMaxStack();
        this.maxLocals = rawCodeAttribute.getMaxLocals();
        ListMultimap listMultimap = new ListMultimap();
        ListMultimap listMultimap1 = new ListMultimap();
        ListMultimap listMultimap2 = new ListMultimap();
        ListMultimap listMultimap3 = new ListMultimap();
        ListMultimap listMultimap4 = new ListMultimap();
        ListMultimap listMultimap5 = new ListMultimap();
        ListMultimap listMultimap6 = new ListMultimap();
        ListMultimap listMultimap7 = new ListMultimap();
        ListMultimap listMultimap8 = new ListMultimap();
        ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap();
        PrintWriter printWriter = new PrintWriter(new StringWriter());

        try {
            this.bytecode = new MethodBytecode(
                    this, rawCodeAttribute.getCode(), listMultimap, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5, listMultimap6
            );
        } catch (IOException iOException) {
        }

        this.exceptionTableLength = rawCodeAttribute.getExceptionTableLength();
        this.exceptionTable = new ExceptionTableEntry[this.exceptionTableLength];
        ClassFileInputStream classFileInputStream = ClassFileInputStream.fromBytes(rawCodeAttribute.getExceptionTableBytes(), false);

        for (int i = 0; i < this.exceptionTableLength; i++) {
            this.exceptionTable[i] = new ExceptionTableEntry(this, classFileInputStream, listMultimap, listMultimap4);
        }

        this.attributeCount = rawCodeAttribute.getAttributeCount();
        this.attributes = new Attribute[this.attributeCount];
        ClassFileInputStream classFileInputStream1 = ClassFileInputStream.fromBytes(rawCodeAttribute.getAttributeBytes(), false);

        for (int i = 0; i < this.attributeCount; i++) {
            this.attributes[i] = Attribute.readAttribute(
                    this,
                    classFileInputStream1,
                    this.bytecode.getLocalVariableList(),
                    listMultimap7,
                    listMultimap8,
                    listMultimap1,
                    listMultimap2,
                    listMultimap3,
                    listMultimap4,
                    listMultimap5,
                    listMultimap6,
                    printWriter,
                    listMultimap,
                    threeKeyMultiMap
            );
        }

        this.resolveOffsetLabels(listMultimap, printWriter);
        this.bytecode.recomputeOffsets();
    }

    public void collectLongConstants(Set set1) {
        this.bytecode.collectLongConstants(set1);
    }

    public boolean applyAutoReflectionHandling(
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
        return this.bytecode != null
                ? this.bytecode.applyAutoReflection(list1, string, list2, bl, bl1, bl2, commonSuperTypeResolver1, classMemberLookup1, bl3, scriptEnvironment1)
                : false;
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
        if (this.bytecode != null) {
            this.bytecode
                    .applyParameterChanges(
                            methodParamChangeNode,
                            map1,
                            map2,
                            map3,
                            bl,
                            map4,
                            commonSuperTypeResolver1,
                            classHierarchyQuery,
                            list1,
                            constantPool1,
                            methodOverrideAnalyzer,
                            set1
                    );
        }
    }

    public void printInstructions(PrintWriter printWriter) throws ZkmProcessingException {
        if (this.bytecode != null) {
            this.bytecode.printDisassembly(printWriter);
        }
    }

    public String getMethodDescription() {
        return this.getDisplayLocationName() + " " + ((AbstractMethodInfo) this.getParent()).getOriginalNameWithParameters();
    }

    public void trimAttributes(TrimProcessor trimProcessor1, TrimOptions trimOptions1, ScriptEnvironment scriptEnvironment1, PrintWriter printWriter) throws IOException {
        PrintWriter printWriter1 = scriptEnvironment1.getLogWriter();
        int ba = this.attributes.length;
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            if (this.attributes[i] instanceof UnknownAttribute && trimOptions1.c) {
                UnknownAttribute unknownAttribute = (UnknownAttribute) this.attributes[i];
                if (scriptEnvironment1.isVerbose()) {
                    printWriter1.println(
                            "\tDeleting unknown attribute '"
                                    + unknownAttribute.getAttributeName()
                                    + "' in method '"
                                    + this.getMethodName()
                                    + "' in class "
                                    + ZkmUtils.slashesToDots(this.getClassName())
                    );
                }

                printWriter.println(
                        "Deleting unknown attribute '"
                                + unknownAttribute.getAttributeName()
                                + "' in method '"
                                + this.getMethodName()
                                + "' in class "
                                + ZkmUtils.slashesToDots(this.getClassName())
                );
            } else if (this.attributes[i] instanceof ReferencingAttribute && trimOptions1.d) {
                ReferencingAttribute referencingAttribute = (ReferencingAttribute) this.attributes[i];
                if (trimProcessor1.hasAnnotationRetainedClasses() && !referencingAttribute.trimAnnotations(trimProcessor1, scriptEnvironment1, printWriter)) {
                    arrayList.add(this.attributes[i]);
                }
            } else {
                arrayList.add(this.attributes[i]);
            }
        }

        int bc = arrayList.size();
        if (bc < ba) {
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[bc])));
        }

        this.attributeCount = this.attributes.length;
        this.length = this.getLength();
    }

    public boolean isConstructor() {
        return ((AbstractMethodInfo) this.getParent()).isConstructor();
    }

    public void analyzeFlow(CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        if (this.bytecode != null && this.bytecode.isModified()) {
            try {
                this.bytecode.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery);
            } catch (MethodAnalysisException methodAnalysisException) {
                scriptEnvironment1.logMessage(methodAnalysisException.toString() + " (A)");
            } catch (StackAnalysisException stackAnalysisException) {
                stackAnalysisException.printStackTrace();
                throw new ZkmProcessingException(stackAnalysisException.getMessage(), stackAnalysisException);
            }
        }
    }

    @Override
    public int getLength() {
        int ba = 4 + this.bytecode.getSerializedLength() + 2 + this.exceptionTableLength * 8 + 2;

        for (int i = 0; i < this.attributeCount; i++) {
            ba += this.attributes[i].getTotalSize();
        }

        this.length = ba;
        return ba;
    }

    public StackMapTableAttribute getStackMapTableAttribute() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof StackMapTableAttribute) {
                return (StackMapTableAttribute) this.attributes[i];
            }
        }

        return null;
    }

    public ExceptionTableEntry[] getExceptionTable() {
        return this.exceptionTable;
    }

    public int getShortestPathLength() {
        return this.bytecode.getMinReturnDistance();
    }

    public void resolveOffsetLabels(ListMultimap listMultimap, PrintWriter printWriter) throws ZkmProcessingException {
        this.bytecode.insertAttributeLabels(listMultimap, printWriter);
    }

    public void rebuildStackMap(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, ConstantPool constantPool1, List list1
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            MethodFlowAnalyzer methodFlowAnalyzer = this.bytecode.createFlowAnalyzer(commonSuperTypeResolver1, classHierarchyQuery, true);
            if (!methodFlowAnalyzer.hasFrameLabels()) {
                this.bytecode.setModified(false);
                return;
            }

            if (!this.bytecode.isModified() && !HiddenOptionFlags.REBUILD_ALL_STACK_MAPS) {
                return;
            }

            StackMapAttribute stackMapAttribute = this.getStackMapAttribute();
            Set set1;
            ConstantUtf8 constantUtf8;
            if (stackMapAttribute != null) {
                constantUtf8 = stackMapAttribute.getNameConstant();
                UsedConstantsCollector usedConstantsCollector = new UsedConstantsCollector(31);
                UsedConstantsCollector usedConstantsCollector1 = usedConstantsCollector;
                stackMapAttribute.collectUsedConstants('\u0000', 1103830477, usedConstantsCollector1, '鞍');
                set1 = ZkmUtils.createHashSetFrom(usedConstantsCollector.getUsedClasses());
            } else {
                constantUtf8 = constantPool1.createUtf8Constant("StackMap", list1);
                set1 = Collections.EMPTY_SET;
            }

            StackMapAttribute stackMapAttribute1 = new StackMapAttribute(this, constantUtf8);
            StackMapEntry[] stackMapEntrys;
            if (HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("1")) {
                stackMapEntrys = methodFlowAnalyzer.buildStackMapEntries(stackMapAttribute1, constantPool1, set1, list1, false);
            } else if (HiddenOptionFlags.STACK_MAP_ALGORITHM.equals("2")) {
                stackMapEntrys = methodFlowAnalyzer.buildStackMapEntries(stackMapAttribute1, constantPool1, set1, list1, true);
            } else {
                StackMapEntry[] stackMapEntrys1 = methodFlowAnalyzer.buildStackMapEntries(stackMapAttribute1, constantPool1, set1, list1, false);
                StackMapEntry[] stackMapEntrys2 = methodFlowAnalyzer.buildStackMapEntries(stackMapAttribute1, constantPool1, set1, list1, true);
                if (sumFrameSizes(stackMapEntrys2) < sumFrameSizes(stackMapEntrys1)) {
                    stackMapEntrys = stackMapEntrys2;
                } else {
                    stackMapEntrys = stackMapEntrys1;
                }
            }

            stackMapAttribute1.setFrames(stackMapEntrys);
            if (stackMapAttribute != null) {
                int ba = this.indexOfStackMap();
                this.attributes[ba] = stackMapAttribute1;
            } else if (stackMapAttribute1.getFrameCount() > 0) {
                Attribute[] attributes1 = new Attribute[this.attributeCount + 1];
                System.arraycopy(this.attributes, 0, attributes1, 0, this.attributeCount);
                attributes1[this.attributeCount] = stackMapAttribute1;
                this.attributes = attributes1;
                this.attributeCount++;
            }

            this.bytecode.setModified(false);
            methodFlowAnalyzer.release();
        }
    }

    public Map getLocalVariableNameMap() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof LocalVariableTableAttribute) {
                return ((LocalVariableTableAttribute) this.attributes[i]).getLocalNamesByIndex();
            }
        }

        return null;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        Map map1 = (Map) object;
        ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
        Map map2 = map1;
        ScriptEnvironment scriptEnvironment4 = scriptEnvironment2;
        Map map4 = map2;
        DataOutputStream dataOutputStream1 = dataOutputStream;
        super.writeRemapped(dataOutputStream1, map4, scriptEnvironment4);
        dataOutputStream.writeShort(this.maxStack);
        dataOutputStream.writeShort(this.maxLocals);
        this.bytecode.writeCodeRemapped(dataOutputStream, map1);
        dataOutputStream.writeShort(this.exceptionTableLength);

        for (int i = 0; i < this.exceptionTableLength; i++) {
            this.exceptionTable[i].writeRemapped(dataOutputStream, map1);
        }

        dataOutputStream.writeShort(this.attributeCount);

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof UnknownAttribute && this.attributes[i].getLength() > 0) {
                UnknownAttribute unknownAttribute = (UnknownAttribute) this.attributes[i];
                if (unknownAttribute.isCommonAcrossClasses()) {
                    scriptEnvironment1.logWarning(
                            "Method contain unknown attribute '"
                                    + unknownAttribute.getAttributeName()
                                    + "'. The integrity of this attribute may be affected by obfuscation."
                    );
                } else {
                    scriptEnvironment1.logWarning(
                            "Method '"
                                    + this.getMethodName()
                                    + "' in class '"
                                    + this.getDottedClassName()
                                    + "' contains unknown attribute '"
                                    + unknownAttribute.getAttributeName()
                                    + "'. The integrity of this attribute may be affected by obfuscation. Consider using the Trim function to delete it."
                    );
                }
            }

            Attribute attribute = this.attributes[i];
            ScriptEnvironment scriptEnvironment3 = scriptEnvironment1;
            Map map3 = map1;
            attribute.writeRemapped(dataOutputStream, map3, scriptEnvironment3);
        }
    }

    public void collectEncryptableIntegers(
            MultiMapTable multiMapTable,
            MethodInfo methodInfo1,
            Set set1,
            IntegerEncryptionExclusions integerEncryptionExclusions,
            boolean bl,
            List list1,
            ConstantPool constantPool1
    ) {
        this.bytecode.collectIntEncryptionCandidates(multiMapTable, methodInfo1, set1, integerEncryptionExclusions, bl, list1, constantPool1);
    }

    public void expandStringConcatInvokes(
            BootstrapMethodIndex bootstrapMethodIndex1,
            ConstantPool constantPool1,
            Map map1,
            ListMultimap listMultimap,
            List list1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        this.bytecode
                .planStringConcatReplacements(bootstrapMethodIndex1, constantPool1, map1, listMultimap, list1, commonSuperTypeResolver1, classHierarchyQuery);
    }

    public int indexOfInstruction(Instruction instruction1) {
        return this.bytecode != null ? this.bytecode.indexOfInstruction(instruction1) : -1;
    }

    public void collectReferencedClasses(Set set1, Set set2, Set set3, Set set4) {
        this.bytecode.collectReferencedMembers(set1, set2, set3, set4);

        for (int i = 0; i < this.exceptionTable.length; i++) {
            ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(this.exceptionTable[i].getCatchTypeName());
            if (classFileBase != null) {
                if (classFileBase.isMultiRelease()) {
                    set1.addAll(classFileBase.getAllVersions());
                } else {
                    set1.add(classFileBase);
                }
            }
        }
    }

    public void collectEncryptableLongs(
            MultiMapTable multiMapTable,
            MethodInfo methodInfo1,
            Set set1,
            LongEncryptionExclusionHandler longEncryptionExclusionHandler,
            List list1,
            ConstantPool constantPool1
    ) {
        this.bytecode.collectLongEncryptionCandidates(multiMapTable, methodInfo1, set1, longEncryptionExclusionHandler, list1, constantPool1);
    }

    public void collectExcludedFieldLongs(Set set1, LongEncryptionExclusionHandler longEncryptionExclusionHandler) {
        this.bytecode.collectExcludedLongConstants(set1, longEncryptionExclusionHandler);
    }

    public void removeStackMapAttributes() {
        this.removeAttributesOfType(AbstractStackMapAttribute.class);
    }

    public void collectMethodInvokeOpcodes(HashMap hashMap) {
        if (this.bytecode != null) {
            this.bytecode.collectInvokeOpcodes(hashMap);
        }
    }

    public void removeLineNumberTable() {
        this.removeAttributesOfType(LineNumberTableAttribute.class);
    }

    public void collectIntegerConstants(Set set1) {
        this.bytecode.collectIntConstants(set1);
    }

    public void indexMemberReferences(NestedMultiMap nestedMultiMap, NestedMultiMap nestedMultiMap1, NestedMultiMap nestedMultiMap2, IntegerCache integerCache1) {
        this.bytecode.indexFieldAndMethodUsages(nestedMultiMap, nestedMultiMap1, nestedMultiMap2, integerCache1);
    }

    public MethodBytecode getBytecode() {
        return this.bytecode;
    }

    public void collectStringConstants(Set set1) {
        this.bytecode.collectStringConstants(set1);
    }

    @Override
    public final void remapClassNames(Object object, Object object1, Object object2, Object object3) throws ZkmProcessingException {
        int ba = (Integer) object1;
        HashMap hashMap = (HashMap) object2;
        HashMap hashMap1 = (HashMap) object3;
        int bb = (Integer) object;

        for (int i = 0; i < this.attributeCount; i++) {
            Attribute attribute = this.attributes[i];
            HashMap hashMap3 = hashMap1;
            HashMap hashMap2 = hashMap;
            Integer integer = ba;
            attribute.remapClassNames(bb, integer, hashMap2, hashMap3);
        }
    }

    public void collectLineNumberEntries(ArrayList arrayList) {
        LineNumberTableAttribute lineNumberTableAttribute = this.getLineNumberTableAttribute();
        if (lineNumberTableAttribute != null) {
            lineNumberTableAttribute.addEntriesTo(arrayList);
        }
    }

    public void insertMethodKeyInitialization(
            MethodParamChangeNode methodParamChangeNode, Long long1, ObservableHolder observableHolder, List list1, ConstantPool constantPool1, String string, int ba
    ) throws ZkmException, IOException {
        MethodBytecode methodBytecode1 = this.bytecode;
        MethodInfo methodInfo1 = (MethodInfo) this.getOwnerMethod();
        Integer integer = ba;
        String string1 = string;
        ConstantPool constantPool2 = constantPool1;
        List list2 = list1;
        ObservableHolder observableHolder1 = observableHolder;
        methodBytecode1.insertMethodKeyLocal(methodParamChangeNode, long1, observableHolder1, list2, constantPool2, string1, integer);
    }

    public void collectReferencedProgramClasses(Set set1, Set set2, Set set3, Set set4) {
        this.bytecode.collectReferencedProgramMembers(set1, set2, set3, set4);
        ClassFileBase classFileBase = this.getOwningClass();

        for (int i = 0; i < this.exceptionTable.length; i++) {
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(this.exceptionTable[i].getCatchTypeName());
            if (programClass1 != null) {
                if (classFileBase.isVersionedVariant() && programClass1.hasVersionedVariants()) {
                    programClass1 = (ProgramClass) programClass1.selectVersionForRelease(classFileBase.getReleaseVersion());
                }

                set1.add(programClass1);
            }
        }
    }

    public boolean storesToField(FieldInfo fieldInfo) {
        return this.bytecode != null ? this.bytecode.storesToField(fieldInfo) : false;
    }

    public int[] getLineNumbers() {
        LineNumberTableAttribute lineNumberTableAttribute = this.getLineNumberTableAttribute();
        return lineNumberTableAttribute != null ? lineNumberTableAttribute.getSortedLineNumbers() : new int[0];
    }

    public void applyStringEncryption(
            List list1,
            boolean bl,
            Map map1,
            ListMultimap listMultimap,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.bytecode != null) {
            int ba = 0;
            if (this.exceptionTableLength > 0) {
                for (ExceptionTableEntry exceptionTableEntry1 : this.exceptionTable) {
                    if (exceptionTableEntry1.isNewlyCreated()) {
                        ba++;
                    }
                }
            }

            int[][] bb;
            if (ba > 0) {
                this.bytecode.recomputeOffsets();
                int bc = 0;
                bb = new int[ba][2];

                for (int i = 0; i < this.exceptionTableLength; i++) {
                    ExceptionTableEntry exceptionTableEntry2 = this.exceptionTable[i];
                    if (exceptionTableEntry2.isNewlyCreated()) {
                        bb[bc][0] = exceptionTableEntry2.getStartIndex();
                        bb[bc][1] = exceptionTableEntry2.getEndIndex();
                        bc++;
                    }
                }
            } else {
                bb = new int[0][0];
            }

            this.bytecode
                    .applyStringEncryption(
                            list1, bl, map1, bb, listMultimap, long1, localVariableIndex1, intCounter, constantPool1, list2, commonSuperTypeResolver1, classHierarchyQuery
                    );
            if (!bl && this.maxLocals < intCounter.getValue()) {
                this.setMaxLocals(intCounter.getValue());
            }
        }
    }

    public void collectMethodCallSites(ListMultimap listMultimap) {
        this.bytecode.indexCalledMethods(listMultimap);
    }

    public LineNumberTableAttribute getLineNumberTableAttribute() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof LineNumberTableAttribute) {
                return (LineNumberTableAttribute) this.attributes[i];
            }
        }

        return null;
    }

    public void insertExceptionTableEntries(boolean bl, List list1, ScriptEnvironment scriptEnvironment1) {
        if (list1.size() > 0) {
            List[] lists = new List[this.exceptionTable.length + 1];
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ExceptionTableEntry exceptionTableEntry1 = (ExceptionTableEntry) iterator.next();
                int ba = this.exceptionTable.length;
                boolean bl1 = false;

                for (int i = this.exceptionTable.length - 1; i >= 0; i += -1) {
                    ExceptionTableEntry exceptionTableEntry2 = this.exceptionTable[i];
                    if (exceptionTableEntry1.isOutsideRange(exceptionTableEntry2.getOffset(0), exceptionTableEntry2.getOffset(1))) {
                        if (!bl1 && exceptionTableEntry1.getOffset(0) < exceptionTableEntry2.getOffset(0)) {
                            ba = i;
                        } else {
                            bl1 = true;
                        }
                    } else {
                        if (!exceptionTableEntry1.isWithinRange(exceptionTableEntry2.getOffset(0), exceptionTableEntry2.getOffset(1))
                                || exceptionTableEntry1.getOffset(0) == exceptionTableEntry2.getOffset(0)
                                && exceptionTableEntry1.getOffset(1) == exceptionTableEntry2.getOffset(1)) {
                            break;
                        }

                        ba = i;
                    }
                }

                if (lists[ba] == null) {
                    lists[ba] = new ArrayList();
                }

                lists[ba].add(exceptionTableEntry1);
            }

            LinkedList linkedList = new LinkedList();

            for (ExceptionTableEntry exceptionTableEntry3 : this.exceptionTable) {
                linkedList.add(exceptionTableEntry3);
            }

            for (int i = lists.length - 1; i >= 0; i += -1) {
                List list2 = lists[i];
                if (list2 != null) {
                    Collections.sort(list2, ExceptionTableEntry.getComparator());
                    Iterator iterator1 = list2.iterator();

                    while (iterator1.hasNext()) {
                        ExceptionTableEntry exceptionTableEntry4 = (ExceptionTableEntry) iterator1.next();
                        if (i == lists.length - 1) {
                            linkedList.add(exceptionTableEntry4);
                        } else {
                            linkedList.add(i, exceptionTableEntry4);
                        }
                    }
                }
            }

            this.exceptionTable = ((com.zelix.klassmaster.classfile.attribute.ExceptionTableEntry[]) (linkedList.toArray(new ExceptionTableEntry[linkedList.size()])));
            this.exceptionTableLength = this.exceptionTable.length;
            new StringBuilder();
            if (bl && scriptEnvironment1.isVerbose()) {
                PrintWriter printWriter = scriptEnvironment1.getLogWriter();
                printWriter.println(
                        "\tException obfuscated method '"
                                + ((MethodInfo) this.getParent()).toOriginalDisplayString()
                                + "' in class '"
                                + this.getOriginalDottedName()
                                + "'"
                                + ""
                );
            }
        }
    }
}
