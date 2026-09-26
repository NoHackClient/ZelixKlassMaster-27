package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObjectTriple;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.LongStream;

public class MethodKeyInjector {
    public MethodParamChangeNode currentNode;
    public NodePairKeys currentPairKeys;
    public SourceArchive currentArchive;
    public LongKeyNode currentKeyNode;
    public Map methodKeys = ZkmUtils.createHashMap(13);
    public Map keyLocals = ZkmUtils.createHashMap(13);
    public final MethodParameterChanger parameterChanger;
    public final ClassMemberLookup memberLookup;
    public final ClassResolver classResolver;
    public final Random random;
    public final Iterator randomKeyIterator;
    public final ObjectTriple defaultHelperTriple;
    public final Map archiveHelperTriples;

    public Long createMethodKey(Object object) {
        long ba = MethodParameterChanger.nextRandomKey();
        this.methodKeys.put(object, ba);
        return ba;
    }

    public void putKeyLocal(Object object, Object object1) {
        LocalVariableIndex localVariableIndex1 = (LocalVariableIndex) this.keyLocals.put(object, object1);
    }

    public LocalVariableIndex getKeyLocal(Object object) {
        return (LocalVariableIndex) this.keyLocals.get(object);
    }

    public boolean hasMethodKey(Object object) {
        return this.methodKeys.containsKey(object);
    }

    public void initializeForMethod(MethodInfo methodInfo1, int ba) {
        ObjectTriple objectTriple;
        if (this.currentArchive != null) {
            objectTriple = (ObjectTriple) this.archiveHelperTriples.get(this.currentArchive);
        } else {
            objectTriple = this.defaultHelperTriple;
        }

        this.currentNode = new MethodParamChangeNode(methodInfo1, true, false);
        this.currentPairKeys = this.parameterChanger.getNodePairKeys(ba);
        this.currentNode
                .setKeys(
                        this.currentPairKeys.getFirstNode(),
                        this.currentPairKeys.getSecondNode(),
                        this.currentPairKeys.getFirstNodeKey(),
                        this.currentPairKeys.getFirstRandomKey()
                );
        this.currentNode.setRandomKey((Long) this.randomKeyIterator.next());
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        this.parameterChanger.putHelperTriple(programClass1, objectTriple);
        this.parameterChanger.putInitNode(methodInfo1, this.currentNode);
        this.parameterChanger.addStringEncryptionClass(programClass1);
        LongKeyNode longKeyNode = this.currentNode.getFirstKeyNode();
        this.currentKeyNode = new LongKeyNode(longKeyNode, this.parameterChanger.getFinalBitLayout());
    }

    public MethodParameterChanger getParameterChanger() {
        return this.parameterChanger;
    }

    public List buildKeyDecodeInstructions(LocalVariableList localVariableList1, MutableInt mutableInt, List list1, ConstantPool constantPool1) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) constantPool1.getClassFile();
        int[] finalBitLayout = this.parameterChanger.getFinalBitLayout();
        Long long1 = MethodParameterChanger.rerandomizeHighByte(this.currentNode.getSecondaryKey(), finalBitLayout);
        Long long2 = MethodParameterChanger.rerandomizeHighByte(this.currentPairKeys.getSecondRandomKey(), finalBitLayout);
        Long long3 = (Long) this.randomKeyIterator.next();
        this.currentKeyNode.pushKey(long3);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt1 = new MutableInt();
        List list2 = this.parameterChanger
                .buildKeyLookupCode(programClass1, long1, long2, long3, mutableInt1, list1, constantPool1, this.memberLookup, this.classResolver);
        int ba = 0 + mutableInt1.getValue();
        arrayList.addAll(list2);
        arrayList.add(Instruction.createLongLoad(0, localVariableList1, 4));
        arrayList.add(SimpleInstruction.forOpcode(131));
        arrayList.add(Instruction.createLongStore(0, localVariableList1, 4));
        arrayList.add(new LabelInstruction(8192));
        ba += 3;
        mutableInt.setValue(ba);
        return arrayList;
    }

    public Map getKeyLocals() {
        return this.keyLocals;
    }

    public MethodKeyInjector(
            MethodParameterChanger methodParameterChanger,
            ObjectTriple objectTriple,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            Random random1
    ) {
        this.parameterChanger = methodParameterChanger;
        this.memberLookup = classMemberLookup1;
        this.classResolver = classResolver1;
        this.random = random1;
        LongStream longStream = this.random.longs(1L, 281474976710656L);
        this.randomKeyIterator = longStream.iterator();
        this.defaultHelperTriple = objectTriple;
        this.archiveHelperTriples = map1;
    }

    public MethodParamChangeNode getCurrentNode() {
        return this.currentNode;
    }

    public List buildKeyInitInstructions(MethodInfo methodInfo1, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer, int ba) throws ZkmException, IOException {
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        this.currentNode.buildInitInstructions(programClass1, this.memberLookup, this.classResolver, list1, inheritedMemberAnalyzer, this.parameterChanger);
        ArrayList arrayList = new ArrayList(this.currentNode.getInitInstructions());
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        Long long1 = this.createMethodKey(methodInfo1);
        ResolvedFieldRef resolvedFieldRef = this.currentNode.getKeyFieldRef();
        long effectiveKey = this.currentNode.getEffectiveKey();
        long bc = long1 ^ effectiveKey;
        int localVariableCount = methodInfo1.getLocalVariableCount();
        methodInfo1.setMaxLocals(localVariableCount + 2);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(Instruction.createLongConstantLoad(bc, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(131));
        LocalVariableList localVariableList1 = methodInfo1.getLocalVariableList();
        arrayList.add(Instruction.createLongStore(localVariableCount, localVariableList1, ba));
        this.putKeyLocal(methodInfo1, localVariableList1.getSlotAt(localVariableCount));
        arrayList.add(new LabelInstruction(8192));
        return arrayList;
    }

    public Long getMethodKey(Object object) {
        return (Long) this.methodKeys.get(object);
    }

    public void setCurrentArchive(SourceArchive sourceArchive1) {
        this.currentArchive = sourceArchive1;
    }

    public long getKeyValueAt(int ba) {
        return this.currentKeyNode.getMiddleBitsAt(ba);
    }

    public Map getMethodKeys() {
        return Collections.unmodifiableMap(this.methodKeys);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
