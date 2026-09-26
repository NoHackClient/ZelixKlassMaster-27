package com.zelix.klassmaster.obfuscator.constants;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantInteger;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.MethodHandleRefKind;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInterfaceMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInvokeDynamic;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodHandleConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.insn.BranchInstruction;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.GotoInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.InvokeDynamicInstruction;
import com.zelix.klassmaster.classfile.insn.InvokeInterfaceInstruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableAllocator;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.NewArrayInstruction;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.classfile.insn.TableSwitchInstruction;
import com.zelix.klassmaster.classfile.insn.TypeInstruction;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.references.ReferenceObfuscator;
import com.zelix.klassmaster.util.ByteConversionUtils;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.RankedValue;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.LongStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class IntegerConstantEncryptor {
    public ResolvedMethodRef desLookupMethod;
    public int indexXorKey;
    public ResolvedMethodRef xorLookupMethod;
    public Cipher cipher;
    private SecretKeyFactory secretKeyFactory;
    public ResolvedFieldRef valueCacheMapField;
    public ResolvedFieldRef valueCacheArrayField;
    public IvParameterSpec ivParameterSpec;
    public ProgramClass currentClass;
    public ResolvedInvokeDynamic lookupIndyEntry;
    public Long classKey;
    public Random random = ZkmUtils.createRandom(5023);
    public Iterator keyIterator;

    public ResolvedInvokeDynamic getLookupIndyEntry() {
        return this.lookupIndyEntry;
    }

    public void buildCallSiteTargetBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ResolvedMethodRef resolvedMethodRef,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Integer", "intValue", "()I", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        arrayList.add(Instruction.createIntStore(4, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "longValue", "()J", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        arrayList.add(Instruction.createLongStore(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 11));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef));
        arrayList.add(Instruction.createIntStore(7, localVariableList1, 11));
        Integer integer = this.currentClass.hasReleaseVersion() ? this.currentClass.getReleaseVersion() : null;
        AbstractFieldInfo abstractFieldInfo = classResolver1.getVersionedClass("java/lang/Integer", integer).findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef("java/lang/Integer", "TYPE", "Ljava/lang/Class;", list1, abstractFieldInfo);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(Instruction.createIntLoad(7, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "constant",
                "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classMemberLookup1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        arrayList.add(Instruction.createObjectStore(8, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(5));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Class", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant2));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        AbstractFieldInfo abstractFieldInfo1 = classResolver1.getVersionedClass("java/lang/Long", integer).findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef("java/lang/Long", "TYPE", "Ljava/lang/Class;", list1, abstractFieldInfo1);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList.add(SimpleInstruction.forOpcode(83));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "dropArguments",
                "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classMemberLookup1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "setTarget", "(Ljava/lang/invoke/MethodHandle;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        arrayList.add(Instruction.createIntLoad(7, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(172));
    }

    public ResolvedMethodRef getLookupMethod() {
        return this.desLookupMethod != null ? this.desLookupMethod : this.xorLookupMethod;
    }

    public void emitDesCipherInit(
            LocalVariableList localVariableList1,
            List list1,
            int ba,
            LocalVariableIndex localVariableIndex1,
            LocalVariableAllocator localVariableAllocator,
            List list2,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        ConstantPool constantPool1 = this.currentClass.getClassConstantPool();
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("DES/CBC/NoPadding", list2, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectStore(ba, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(2));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("DES", list2, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        int andIncrement = localVariableAllocator.getAndIncrement();
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createIntStore(andIncrement, localVariableList1, 11));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new BranchInstruction(162, labelInstruction1));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 11));
        arrayList.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 11));
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(104));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createIntIncrement(andIncrement, 1, localVariableList1, 11));
        arrayList.add(new GotoInstruction(labelInstruction));
        arrayList.add(labelInstruction1);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("javax/crypto/spec/DESKeySpec", list2);
        arrayList.add(new TypeInstruction(resolvedClassConstant));
        arrayList.add(SimpleInstruction.forOpcode(90));
        arrayList.add(SimpleInstruction.forOpcode(95));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/DESKeySpec", "<init>", "([B)V", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant2));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "generateSecret", "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("javax/crypto/spec/IvParameterSpec", list2);
        arrayList.add(new TypeInstruction(resolvedClassConstant1));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/IvParameterSpec", "<init>", "([B)V", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "init", "(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list1.addAll(arrayList);
    }

    public List buildEncryptedValueStrings(
            ObservableHolder[] observableHolders, SetMultiMap setMultiMap, Map map1, Set set1, Long long1, boolean bl, List list1, ConstantPool constantPool1
    ) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList();
        int ba = observableHolders.length;
        int bb = 0;

        while (bb < ba) {
            StringBuilder stringBuilder = new StringBuilder(ba * 4);
            int bc = 0;

            while (bb < ba) {
                ObservableHolder observableHolder = observableHolders[bb];
                EncryptedValueLocation encryptedValueLocation = (EncryptedValueLocation) map1.get(observableHolder);
                Iterator iterator = setMultiMap.getValues(observableHolder).iterator();

                while (iterator.hasNext()) {
                    ConstantInteger constantInteger = (ConstantInteger) iterator.next();
                    set1.add(constantInteger);
                }

                IntEncryptionStorageKind intEncryptionStorageKind = encryptedValueLocation.getStorageKind();
                long bh = this.toRandomizedLong((Integer) observableHolder.getValue());
                short bd = 0;
                if (intEncryptionStorageKind.equals(IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX)) {
                    long be = (Long) this.keyIterator.next();
                    encryptedValueLocation.setDecryptionKey(be);
                    bd = (short) (bb ^ this.indexXorKey);
                    bh ^= be;
                } else {
                    label76:
                    {
                        Iterator iterator1;
                        if (!intEncryptionStorageKind.equals(IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES)) {
                            if (!intEncryptionStorageKind.equals(IntEncryptionStorageKind.INDY_ENTRY_AND_INDEX)) {
                                break label76;
                            }

                            iterator1 = this.keyIterator;
                        } else {
                            iterator1 = this.keyIterator;
                        }

                        long bi = (Long) iterator1.next();
                        encryptedValueLocation.setDecryptionKey(bi);
                        bd = (short) (bb ^ this.indexXorKey);
                        bh = this.desEncrypt(bh, bi);
                    }
                }

                long bj;
                if (long1 != null) {
                    if (bl) {
                        bj = this.desEncrypt(bh, long1);
                    } else {
                        bj = bh ^ long1;
                    }
                } else {
                    bj = bh ^ this.classKey;
                }

                byte[] bf = ByteConversionUtils.longToBytes(bj);

                String string;
                try {
                    string = new String(bf, "ISO-8859-1");
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    throw new ZkmProcessingException(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
                }

                int bg = ZkmUtils.getModifiedUtf8Length(string);
                if (bc > 0 && bc + bg > 32768) {
                    break;
                }

                bc += bg;
                bb++;
                stringBuilder.append(string);
                if (intEncryptionStorageKind.equals(IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX)
                        || intEncryptionStorageKind.equals(IntEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES)
                        || intEncryptionStorageKind.equals(IntEncryptionStorageKind.INDY_ENTRY_AND_INDEX)) {
                    encryptedValueLocation.setIndex(bd);
                }

                if (arrayList.size() == 0 && ba > 3 && bb == ba - 2) {
                    break;
                }
            }

            ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(stringBuilder.toString(), list1);
            arrayList.add(resolvedStringConstant);
        }

        return arrayList;
    }

    public void createDecryptionMembers(
            ProgramClass programClass1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            boolean bl,
            boolean bl1,
            boolean bl2,
            boolean bl3
    ) throws ZkmException, IOException {
        this.resetForClass(programClass1);
        ConstantPool constantPool1 = this.currentClass.getClassConstantPool();
        if (bl3) {
            if (resolvedFieldRef != null && bl1) {
                this.indexXorKey = this.random.nextInt(32766) + 1;
                ProgramClass programClass3 = this.currentClass;
                byte bh;
                boolean bl5;
                InheritedMemberAnalyzer inheritedMemberAnalyzer5;
                ClassMemberLookup classMemberLookup6;
                byte bj;
                if (this.currentClass.isInterface()) {
                    bh = 4;
                    long bi = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 11;
                } else {
                    bh = 1;
                    long bb = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 11;
                }

                Integer integer = Integer.valueOf(bj);
                ClassMemberLookup classMemberLookup2 = classMemberLookup6;
                InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer5;
                Boolean boolean1 = bl5;
                FieldInfo fieldInfo = programClass3.createUniquelyNamedStaticField(
                        "[Ljava/lang/Integer;", bh, boolean1, inheritedMemberAnalyzer1, classMemberLookup2, integer
                );
                this.valueCacheArrayField = constantPool1.getOrAddFieldRef(
                        this.currentClass.getClassName(), fieldInfo.getSourceName(), fieldInfo.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                programClass3 = this.currentClass;
                if (this.currentClass.isInterface()) {
                    bh = 4;
                    long bc = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 11;
                } else {
                    bh = 1;
                    long bd = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 11;
                }

                Integer integer1 = Integer.valueOf(bj);
                ClassMemberLookup classMemberLookup3 = classMemberLookup6;
                InheritedMemberAnalyzer inheritedMemberAnalyzer2 = inheritedMemberAnalyzer5;
                Boolean boolean2 = bl5;
                FieldInfo fieldInfo1 = programClass3.createUniquelyNamedStaticField(
                        "Ljava/util/Map;", bh, boolean2, inheritedMemberAnalyzer2, classMemberLookup3, integer1
                );
                this.valueCacheMapField = constantPool1.getOrAddFieldRef(
                        this.currentClass.getClassName(), fieldInfo1.getSourceName(), fieldInfo1.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[1];
                LocalVariableList localVariableList1 = new LocalVariableList(true, "(IJ)I", 14);
                ArrayList arrayList = new ArrayList();
                this.buildDesLookupMethodBody(
                        localVariableList1, arrayList, bl1, resolvedFieldRef, exceptionHandlerSpecs, list1, constantPool1, classMemberLookup1, classResolver1
                );
                MethodInfo methodInfo1 = this.currentClass
                        .createUniquelyNamedStaticMethod(
                                "(IJ)I",
                                arrayList,
                                6,
                                14,
                                1,
                                localVariableList1,
                                exceptionHandlerSpecs,
                                "Integer Constant Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                11
                        );
                this.desLookupMethod = this.currentClass.addMethodRef(methodInfo1, list1);
                if (bl2) {
                    LocalVariableList localVariableList2 = new LocalVariableList(
                            true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)I", 9
                    );
                    ArrayList arrayList1 = new ArrayList();
                    this.buildCallSiteTargetBody(localVariableList2, arrayList1, this.desLookupMethod, list1, constantPool1, classMemberLookup1, classResolver1);
                    MethodInfo methodInfo2 = this.currentClass
                            .createUniquelyNamedStaticMethod(
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)I",
                                    arrayList1,
                                    7,
                                    9,
                                    1,
                                    localVariableList2,
                                    new ExceptionHandlerSpec[0],
                                    "Integer Constant Encryption",
                                    list1,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1,
                                    11
                            );
                    ResolvedMethodRef resolvedMethodRef = this.currentClass.addMethodRef(methodInfo2, list1);
                    ExceptionHandlerSpec[] exceptionHandlerSpecs1 = new ExceptionHandlerSpec[1];
                    LocalVariableList localVariableList3 = new LocalVariableList(
                            true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", 5
                    );
                    ArrayList arrayList2 = new ArrayList();
                    this.buildBootstrapMethodBody(
                            localVariableList3, arrayList2, resolvedMethodRef, exceptionHandlerSpecs1, list1, constantPool1, classMemberLookup1, classResolver1
                    );
                    MethodInfo methodInfo3 = this.currentClass
                            .createUniquelyNamedStaticMethod(
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                    arrayList2,
                                    7,
                                    5,
                                    1,
                                    localVariableList3,
                                    exceptionHandlerSpecs1,
                                    "Integer Constant Encryption",
                                    list1,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1,
                                    11
                            );
                    ResolvedMethodRef resolvedMethodRef1 = this.currentClass.addMethodRef(methodInfo3, list1);
                    String string = String.valueOf((char) (97 + this.random.nextInt(26)));
                    this.lookupIndyEntry = this.currentClass.createInvokeDynamic(string, "(IJ)I", resolvedMethodRef1, list1, constantPool1);
                }
            }
        } else if (resolvedFieldRef != null) {
            if (bl1) {
                this.indexXorKey = this.random.nextInt(32766) + 1;
                this.classKey = (Long) this.keyIterator.next();
                ProgramClass programClass2 = this.currentClass;
                byte ba;
                boolean bl4;
                InheritedMemberAnalyzer inheritedMemberAnalyzer4;
                ClassMemberLookup classMemberLookup5;
                byte bg;
                if (this.currentClass.isInterface()) {
                    ba = 4;
                    long be = 136463318042509L;
                    bl4 = true;
                    inheritedMemberAnalyzer4 = inheritedMemberAnalyzer;
                    classMemberLookup5 = classMemberLookup1;
                    bg = 11;
                } else {
                    ba = 1;
                    long bf = 136463318042509L;
                    bl4 = true;
                    inheritedMemberAnalyzer4 = inheritedMemberAnalyzer;
                    classMemberLookup5 = classMemberLookup1;
                    bg = 11;
                }

                Integer integer2 = Integer.valueOf(bg);
                ClassMemberLookup classMemberLookup4 = classMemberLookup5;
                InheritedMemberAnalyzer inheritedMemberAnalyzer3 = inheritedMemberAnalyzer4;
                Boolean boolean3 = bl4;
                FieldInfo fieldInfo2 = programClass2.createUniquelyNamedStaticField(
                        "[Ljava/lang/Integer;", ba, boolean3, inheritedMemberAnalyzer3, classMemberLookup4, integer2
                );
                this.valueCacheArrayField = constantPool1.getOrAddFieldRef(
                        this.currentClass.getClassName(), fieldInfo2.getSourceName(), fieldInfo2.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                ExceptionHandlerSpec[] exceptionHandlerSpecs2 = new ExceptionHandlerSpec[0];
                LocalVariableList localVariableList4 = new LocalVariableList(true, "(IJ)I", 4);
                ArrayList arrayList3 = new ArrayList();
                this.buildXorLookupMethodBody(localVariableList4, arrayList3, resolvedFieldRef, list1, constantPool1, classMemberLookup1, classResolver1);
                MethodInfo methodInfo4 = this.currentClass
                        .createUniquelyNamedStaticMethod(
                                "(IJ)I",
                                arrayList3,
                                8,
                                4,
                                1,
                                localVariableList4,
                                exceptionHandlerSpecs2,
                                "Integer Constant Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                11
                        );
                this.xorLookupMethod = this.currentClass.addMethodRef(methodInfo4, list1);
            } else {
                this.classKey = (Long) this.keyIterator.next();
            }
        } else if (bl1) {
            if (!bl) {
                this.classKey = (Long) this.keyIterator.next();
            }
        } else if (!bl) {
            this.classKey = (Long) this.keyIterator.next();
        }
    }

    public void emitDecryptDispatch(
            LocalVariableList localVariableList1,
            LabelInstruction labelInstruction,
            LocalVariableIndex localVariableIndex1,
            LocalVariableAllocator localVariableAllocator,
            List list1,
            List list2,
            ListMultimap listMultimap,
            Integer integer,
            Integer integer1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        arrayList.add(new GotoInstruction(labelInstruction1));
        arrayList.add(labelInstruction);
        List list3 = listMultimap.getValues(labelInstruction);
        if (list3.size() > 1) {
            Collections.sort(list3);
            LabelInstruction labelInstruction2 = (LabelInstruction) ((RankedValue) list3.get(0)).getValue();
            LabelInstruction[] labelInstructions = new LabelInstruction[list3.size() - 1];

            for (int i = 1; i < list3.size(); i++) {
                labelInstructions[i - 1] = (LabelInstruction) ((RankedValue) list3.get(i)).getValue();
            }

            arrayList.add(SimpleInstruction.forOpcode(91));
            arrayList.add(SimpleInstruction.forOpcode(87));
            ClassResolver classResolver2 = classResolver1;
            ClassMemberLookup classMemberLookup2 = classMemberLookup1;
            Integer integer2 = integer1;
            Integer integer3 = integer;
            LocalVariableAllocator localVariableAllocator1 = localVariableAllocator;
            LocalVariableIndex localVariableIndex2 = localVariableIndex1;
            List list4 = list2;
            ArrayList arrayList1 = arrayList;
            LocalVariableList localVariableList2 = localVariableList1;
            this.emitLoadDecryptionKey(
                    localVariableList2, arrayList1, list4, localVariableIndex2, localVariableAllocator1, integer3, integer2, classMemberLookup2, classResolver2
            );
            arrayList.add(SimpleInstruction.forOpcode(93));
            arrayList.add(SimpleInstruction.forOpcode(88));
            arrayList.add(new TableSwitchInstruction(labelInstruction2, labelInstructions.length - 1, labelInstructions));
        } else {
            LabelInstruction labelInstruction3 = (LabelInstruction) ((RankedValue) list3.get(0)).getValue();
            arrayList.add(SimpleInstruction.forOpcode(91));
            arrayList.add(SimpleInstruction.forOpcode(87));
            ClassResolver classResolver3 = classResolver1;
            ClassMemberLookup classMemberLookup3 = classMemberLookup1;
            Integer integer4 = integer1;
            Integer integer5 = integer;
            LocalVariableAllocator localVariableAllocator2 = localVariableAllocator;
            LocalVariableIndex localVariableIndex3 = localVariableIndex1;
            List list5 = list2;
            ArrayList arrayList2 = arrayList;
            LocalVariableList localVariableList3 = localVariableList1;
            this.emitLoadDecryptionKey(
                    localVariableList3, arrayList2, list5, localVariableIndex3, localVariableAllocator2, integer5, integer4, classMemberLookup3, classResolver3
            );
            arrayList.add(SimpleInstruction.forOpcode(93));
            arrayList.add(SimpleInstruction.forOpcode(88));
            arrayList.add(SimpleInstruction.forOpcode(87));
            arrayList.add(new GotoInstruction(labelInstruction3));
        }

        arrayList.add(labelInstruction1);
        list1.addAll(arrayList);
    }

    public void emitLoadDecryptionKey(
            LocalVariableList localVariableList1,
            List list1,
            List list2,
            LocalVariableIndex localVariableIndex1,
            LocalVariableAllocator localVariableAllocator,
            Integer integer,
            Integer integer1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        if (integer != null) {
            this.emitDesDecryptCall(localVariableList1, list1, list2, integer, localVariableAllocator, classMemberLookup1, classResolver1);
        } else if (localVariableIndex1 != null) {
            this.emitLoadKeyAsInt(localVariableList1, list1, localVariableIndex1.getIndex());
        } else {
            this.emitLoadKeyAsInt(localVariableList1, list1, integer1);
        }
    }

    public void resetForClass(ProgramClass programClass1) {
        this.currentClass = programClass1;
        this.indexXorKey = 0;
        this.classKey = null;
        this.lookupIndyEntry = null;
        this.xorLookupMethod = null;
        this.desLookupMethod = null;
        this.valueCacheArrayField = null;
        this.valueCacheMapField = null;
    }

    public void emitCacheMapInit(List list1, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1) throws ZkmException, IOException {
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/util/HashMap", list2);
        list1.add(new TypeInstruction(resolvedClassConstant));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(13));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/util/HashMap", "<init>", "(I)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        list1.add(new ConstantRefInstruction(179, this.valueCacheMapField));
    }

    public void emitEncryptedValueSlot(
            LocalVariableList localVariableList1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            ConstantLong constantLong,
            EncryptedValueLocation encryptedValueLocation,
            LabelInstruction labelInstruction,
            MutableInt mutableInt,
            ListMultimap listMultimap
    ) {
        list1.add(new ConstantRefInstruction(20, constantLong));
        int ba = mutableInt.incrementAndGet();
        list1.add(Instruction.createIntPush(ba));
        list1.add(new GotoInstruction(labelInstruction));
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        list1.add(labelInstruction1);
        listMultimap.addValue(labelInstruction, new RankedValue(ba, labelInstruction1));
        if (encryptedValueLocation.hasArrayField()) {
            list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
        } else if (encryptedValueLocation.hasLocalVariable()) {
            list1.add(Instruction.createLongStore(encryptedValueLocation.getLocalVariableIndex(), localVariableList1, 11));
        }
    }

    public void emitLoadKeyAsInt(LocalVariableList localVariableList1, List list1, int ba) {
        list1.add(Instruction.createLongLoad(ba, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(131));
    }

    public void buildXorLookupMethodBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ResolvedFieldRef resolvedFieldRef,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        arrayList.add(Instruction.createIntLoad(0, localVariableList1, 11));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createLongConstantLoad(32767L, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntConstantPush(this.indexXorKey, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntStore(3, localVariableList1, 11));
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheArrayField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new BranchInstruction(199, labelInstruction));
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheArrayField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
        ArrayList arrayList1;
        byte ba;
        if (this.currentClass.supportsJava5()) {
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
            arrayList.add(SimpleInstruction.forOpcode(47));
            arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
            arrayList.add(SimpleInstruction.forOpcode(131));
            arrayList.add(SimpleInstruction.forOpcode(136));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            arrayList1 = arrayList;
            ba = 83;
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant));
            arrayList.add(SimpleInstruction.forOpcode(89));
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
            arrayList.add(SimpleInstruction.forOpcode(47));
            arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
            arrayList.add(SimpleInstruction.forOpcode(131));
            arrayList.add(SimpleInstruction.forOpcode(136));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "<init>", "(I)V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            arrayList1 = arrayList;
            ba = 83;
        }

        arrayList1.add(SimpleInstruction.forOpcode(ba));
        arrayList.add(labelInstruction);
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheArrayField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Integer", "intValue", "()I", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        arrayList.add(SimpleInstruction.forOpcode(172));
    }

    public void emitEncryptedArrayInit(
            LocalVariableList localVariableList1,
            List list1,
            LocalVariableAllocator localVariableAllocator,
            Set set1,
            List list2,
            ResolvedFieldRef resolvedFieldRef,
            int ba,
            ObservableHolder[] observableHolders,
            SetMultiMap setMultiMap,
            Map map1,
            LabelInstruction labelInstruction,
            MutableInt mutableInt,
            ListMultimap listMultimap,
            Long long1,
            boolean bl,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        int bb = observableHolders.length;
        List list3 = this.buildEncryptedValueStrings(observableHolders, setMultiMap, map1, set1, long1, bl, list2, this.currentClass.getClassConstantPool());
        ConstantPool constantPool1 = this.currentClass.getClassConstantPool();
        int andIncrement = localVariableAllocator.getAndIncrement();
        int bd = localVariableAllocator.getAndIncrement();
        int be = localVariableAllocator.getAndIncrement();
        int bf = localVariableAllocator.getAndIncrement();
        int bg = ba == -1 ? localVariableAllocator.getAndIncrement() : ba;
        int bh = localVariableAllocator.getAndIncrement();
        Instruction.appendIntConstant(bb, list1, constantPool1, list2);
        list1.add(new NewArrayInstruction(11));
        list1.add(Instruction.createObjectStore(bg, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bd, localVariableList1, 11));
        Iterator iterator = list3.iterator();

        while (iterator.hasNext()) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
            LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
            list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createObjectStore(be, localVariableList1, 11));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "length", "()I", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
            list1.add(Instruction.createIntStore(bf, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(3));
            list1.add(Instruction.createIntStore(andIncrement, localVariableList1, 11));
            list1.add(labelInstruction1);
            list1.add(Instruction.createObjectLoad(be, localVariableList1, 11));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 11));
            list1.add(Instruction.createIntIncrement(andIncrement, 8, localVariableList1, 11));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 11));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "substring", "(II)Ljava/lang/String;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
            ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("ISO-8859-1", list2, false);
            list1.add(new ConstantRefInstruction(19, resolvedStringConstant1));
            ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "getBytes", "(Ljava/lang/String;)[B", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
            list1.add(Instruction.createObjectStore(bh, localVariableList1, 11));
            list1.add(Instruction.createObjectLoad(bg, localVariableList1, 11));
            list1.add(Instruction.createIntLoad(bd, localVariableList1, 11));
            list1.add(Instruction.createIntIncrement(bd, 1, localVariableList1, 11));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(3));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(56));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(4));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(48));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(5));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(40));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(6));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(32));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(7));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(24));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(SimpleInstruction.forOpcode(8));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(16));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(Instruction.createIntPush(6));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(8));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 11));
            list1.add(Instruction.createIntPush(7));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(SimpleInstruction.forOpcode(129));
            int bi = mutableInt.incrementAndGet();
            list1.add(Instruction.createIntPush(bi));
            list1.add(new GotoInstruction(labelInstruction));
            LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
            list1.add(labelInstruction2);
            listMultimap.addValue(labelInstruction, new RankedValue(bi, labelInstruction2));
            list1.add(SimpleInstruction.forOpcode(80));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 11));
            list1.add(Instruction.createIntLoad(bf, localVariableList1, 11));
            list1.add(new BranchInstruction(161, labelInstruction1));
        }

        if (resolvedFieldRef != null) {
            list1.add(Instruction.createObjectLoad(bg, localVariableList1, 11));
            list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
            Instruction.appendIntConstant(bb, list1, constantPool1, list2);
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list2);
            list1.add(new ConstantRefInstruction(189, resolvedClassConstant));
            list1.add(new ConstantRefInstruction(179, this.valueCacheArrayField));
        }
    }

    public void buildDesLookupMethodBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            boolean bl,
            ResolvedFieldRef resolvedFieldRef,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 384);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 640);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 1152);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list1);
        exceptionHandlerSpecs[0] = new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction3, labelInstruction4, labelInstruction5);
        arrayList.add(Instruction.createIntLoad(0, localVariableList1, 11));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createLongConstantLoad(32767L, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntConstantPush(this.indexXorKey, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntStore(3, localVariableList1, 11));
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheArrayField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new BranchInstruction(199, labelInstruction2));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(48));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(40));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(6));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(32));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(7));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(24));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(8));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(16));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(7));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 11));
        ArrayList arrayList1;
        byte bd;
        LocalVariableList localVariableList3;
        byte be;
        if (bl) {
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
            arrayList.add(SimpleInstruction.forOpcode(47));
            arrayList1 = arrayList;
            long bc = 62906520448321L;
            bd = 5;
            localVariableList3 = localVariableList1;
            be = 11;
        } else {
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList1 = arrayList;
            long ba = 62906520448321L;
            bd = 5;
            localVariableList3 = localVariableList1;
            be = 11;
        }

        Integer integer1 = Integer.valueOf(be);
        LocalVariableList localVariableList2 = localVariableList3;
        Integer integer = Integer.valueOf(bd);
        arrayList1.add(Instruction.createLongStore(integer, localVariableList2, integer1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(48));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(40));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(6));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(32));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(7));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(24));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(8));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(16));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(7));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createObjectStore(7, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", list1, classMemberLookup1, classResolver1
        );
        byte bb;
        if (this.currentClass.supportsJava5()) {
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            ConstantPool constantPool2;
            String string;
            if (this.currentClass.supportsJava19()) {
                ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                        "java/lang/Thread", "threadId", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
                constantPool2 = constantPool1;
                string = "java/lang/Long";
            } else {
                ResolvedMethodRefConstant resolvedMethodRefConstant13 = constantPool1.getOrAddMethodRef(
                        "java/lang/Thread", "getId", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant13));
                constantPool2 = constantPool1;
                string = "java/lang/Long";
            }

            ResolvedMethodRefConstant resolvedMethodRefConstant14 = constantPool2.getOrAddMethodRef(
                    string, "valueOf", "(J)Ljava/lang/Long;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant14));
            arrayList1 = arrayList;
            bb = 8;
        } else {
            ResolvedMethodRefConstant resolvedMethodRefConstant15 = constantPool1.getOrAddMethodRef(
                    "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", list1, classMemberLookup1, classResolver1
            );
            ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant1));
            arrayList.add(SimpleInstruction.forOpcode(89));
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant15));
            arrayList.add(SimpleInstruction.forOpcode(133));
            ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "<init>", "(J)V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant2));
            arrayList1 = arrayList;
            bb = 8;
        }

        arrayList1.add(Instruction.createObjectStore(bb, localVariableList1, 11));
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheMapField));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 11));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef1));
        ResolvedClassConstant resolvedClassConstant7 = constantPool1.getOrCreateClassConstant("[Ljava/lang/Object;", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant7));
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 11));
        arrayList.add(labelInstruction3);
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(new BranchInstruction(199, labelInstruction));
        arrayList.add(SimpleInstruction.forOpcode(6));
        ResolvedClassConstant resolvedClassConstant8 = constantPool1.getOrCreateClassConstant("java/lang/Object", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant8));
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(3));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("DES/CBC/NoPadding", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(4));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("DES", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(5));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("javax/crypto/spec/IvParameterSpec", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant2));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/IvParameterSpec", "<init>", "([B)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant5));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheMapField));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        arrayList.add(SimpleInstruction.forOpcode(87));
        arrayList.add(labelInstruction);
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("javax/crypto/spec/DESKeySpec", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant3));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/DESKeySpec", "<init>", "([B)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant6));
        arrayList.add(Instruction.createObjectStore(11, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("javax/crypto/SecretKeyFactory", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant4));
        arrayList.add(Instruction.createObjectLoad(11, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "generateSecret", "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        arrayList.add(Instruction.createObjectStore(12, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant5 = constantPool1.getOrCreateClassConstant("javax/crypto/Cipher", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant5));
        arrayList.add(Instruction.createObjectStore(13, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(13, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(2));
        arrayList.add(Instruction.createObjectLoad(12, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant2));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "init", "(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        arrayList.add(Instruction.createObjectLoad(13, localVariableList1, 11));
        arrayList.add(Instruction.createObjectLoad(7, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "doFinal", "([B)[B", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        arrayList.add(Instruction.createObjectStore(10, localVariableList1, 11));
        arrayList.add(labelInstruction4);
        arrayList.add(new GotoInstruction(labelInstruction1));
        arrayList.add(labelInstruction5);
        arrayList.add(Instruction.createObjectStore(11, localVariableList1, 11));
        ResolvedClassConstant resolvedClassConstant6 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant6));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createStringConstantLoad(constantPool1.getClassName(), constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(11, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant10));
        arrayList.add(SimpleInstruction.forOpcode(191));
        arrayList.add(labelInstruction1);
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(7));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntPush(255));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(Instruction.createIntPush(24));
        arrayList.add(SimpleInstruction.forOpcode(120));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(8));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntPush(255));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(Instruction.createIntPush(16));
        arrayList.add(SimpleInstruction.forOpcode(120));
        arrayList.add(SimpleInstruction.forOpcode(128));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntPush(255));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(120));
        arrayList.add(SimpleInstruction.forOpcode(128));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 11));
        arrayList.add(Instruction.createIntPush(7));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntPush(255));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(128));
        arrayList.add(Instruction.createIntStore(11, localVariableList1, 11));
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheArrayField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
        if (this.currentClass.supportsJava5()) {
            arrayList.add(Instruction.createIntLoad(11, localVariableList1, 11));
            ResolvedMethodRefConstant resolvedMethodRefConstant11 = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant11));
            arrayList1 = arrayList;
            bb = 83;
        } else {
            ResolvedClassConstant resolvedClassConstant9 = constantPool1.getOrCreateClassConstant("java/lang/Integer", list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant9));
            arrayList.add(SimpleInstruction.forOpcode(89));
            arrayList.add(Instruction.createIntLoad(11, localVariableList1, 11));
            ResolvedMethodRefConstant resolvedMethodRefConstant12 = constantPool1.getOrAddMethodRef(
                    "java/lang/Integer", "<init>", "(I)V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant12));
            arrayList1 = arrayList;
            bb = 83;
        }

        arrayList1.add(SimpleInstruction.forOpcode(bb));
        arrayList.add(labelInstruction2);
        arrayList.add(new ConstantRefInstruction(178, this.valueCacheArrayField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedMethodRefConstant resolvedMethodRefConstant16 = constantPool1.getOrAddMethodRef(
                "java/lang/Integer", "intValue", "()I", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant16));
        arrayList.add(SimpleInstruction.forOpcode(172));
    }

    public ResolvedFieldRef getValueCacheArrayField() {
        return this.valueCacheArrayField;
    }

    public long getClassKey() {
        return this.classKey;
    }

    public boolean hasClassKey() {
        return this.classKey != null;
    }

    public long desCrypt(long ba, long bb) throws ZkmProcessingException {
        byte[] bc = ByteConversionUtils.longToBytes(bb);

        try {
            DESKeySpec dESKeySpec = new DESKeySpec(bc);
            if (this.secretKeyFactory == null) {
                this.secretKeyFactory = SecretKeyFactory.getInstance("DES");
            }

            if (this.cipher == null) {
                this.cipher = Cipher.getInstance("DES/CBC/NoPadding");
                this.ivParameterSpec = new IvParameterSpec(new byte[8]);
            }

            SecretKey secretKey = this.secretKeyFactory.generateSecret(dESKeySpec);
            this.cipher.init(1, secretKey, this.ivParameterSpec);
            byte[] bd = ByteConversionUtils.longToBytes(ba);
            return ByteConversionUtils.bytesToLong(this.cipher.doFinal(bd));
        } catch (Exception exception) {
            throw new ZkmProcessingException(exception.toString(), exception);
        }
    }

    public static boolean canUseIndyEncryption(ProgramClass programClass1) {
        return HiddenOptionFlags.INTEGER_ENCRYPT_INDY && ReferenceObfuscator.supportsMethodHandles(programClass1);
    }

    public long desEncrypt(long ba, long bb) throws ZkmProcessingException {
        return this.desCrypt(ba, bb);
    }

    public void emitDesDecryptCall(
            LocalVariableList localVariableList1,
            List list1,
            List list2,
            int ba,
            LocalVariableAllocator localVariableAllocator,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        int andIncrement = localVariableAllocator.getAndIncrement();
        localVariableAllocator.incrementAndGet();
        int bc = localVariableAllocator.getAndIncrement();
        ConstantPool constantPool1 = this.currentClass.getClassConstantPool();
        list1.add(Instruction.createLongStore(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(8, list1, constantPool1, list2);
        list1.add(new NewArrayInstruction(8));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(56, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(48, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(5));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(40, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(6));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(32, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(7));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(24, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(8));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(16, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        Instruction.appendIntConstant(6, list1, constantPool1, list2);
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        Instruction.appendIntConstant(8, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        Instruction.appendIntConstant(7, list1, constantPool1, list2);
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(Instruction.createObjectLoad(ba, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(95));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "doFinal", "([B)[B", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(bc, localVariableList1, 11));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(56, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(48, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(5));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(40, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(6));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(32, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(7));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(24, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        list1.add(SimpleInstruction.forOpcode(8));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(16, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        Instruction.appendIntConstant(6, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(8, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 11));
        Instruction.appendIntConstant(7, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        list1.add(SimpleInstruction.forOpcode(129));
    }

    public IntegerConstantEncryptor() {
        LongStream longStream = this.random.longs(1L, Long.MAX_VALUE);
        this.keyIterator = longStream.iterator();
    }

    public ResolvedFieldRef getValueCacheMapField() {
        return this.valueCacheMapField;
    }

    public void buildBootstrapMethodBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ResolvedMethodRef resolvedMethodRef,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list1);
        exceptionHandlerSpecs[0] = new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction2);
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/invoke/MutableCallSite", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant1));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "<init>", "(Ljava/lang/invoke/MethodType;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        arrayList.add(Instruction.createObjectStore(3, localVariableList1, 11));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 11));
        ResolvedMethodHandleConstant resolvedMethodHandleConstant = constantPool1.getOrAddMethodHandle(
                MethodHandleRefKind.REF_INVOKE_STATIC, resolvedMethodRef, list1
        );
        arrayList.add(new ConstantRefInstruction(19, resolvedMethodHandleConstant));
        arrayList.add(Instruction.createClassConstantLoad(constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "parameterCount", "()I", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandle", "asCollector", "(Ljava/lang/Class;I)Ljava/lang/invoke/MethodHandle;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(6));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Object", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant2));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(2));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(83));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "insertArguments",
                "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classMemberLookup1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "explicitCastArguments",
                "(Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classMemberLookup1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "setTarget", "(Ljava/lang/invoke/MethodHandle;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        arrayList.add(labelInstruction1);
        arrayList.add(new GotoInstruction(labelInstruction3));
        arrayList.add(labelInstruction2);
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 11));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant3));
        arrayList.add(SimpleInstruction.forOpcode(89));
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("java/lang/StringBuilder", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant4));
        arrayList.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "<init>", "()V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant6));
        arrayList.add(Instruction.createStringConstantLoad(constantPool1.getClassName(), constantPool1, list1));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(" : ", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 11));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(" : ", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "toString", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 11));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant10));
        arrayList.add(SimpleInstruction.forOpcode(191));
        arrayList.add(labelInstruction3);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 11));
        arrayList.add(SimpleInstruction.forOpcode(176));
    }

    public long toRandomizedLong(int ba) {
        byte[] bb = ByteConversionUtils.intToBytes(ba);
        byte[] bc = new byte[4];
        this.random.nextBytes(bc);
        return (bc[0] & 255L) << 56
                | (bc[1] & 255L) << 48
                | (bc[2] & 255L) << 40
                | (bc[3] & 255L) << 32
                | (bb[0] & 255L) << 24
                | (bb[1] & 255L) << 16
                | (bb[2] & 255L) << 8
                | bb[3] & 255L;
    }

    public void emitStoreDecryptedValue(
            LocalVariableList localVariableList1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            EncryptedValueLocation encryptedValueLocation,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            List list2
    ) {
        IntEncryptionStorageKind intEncryptionStorageKind = encryptedValueLocation.getStorageKind();
        switch (IntEncryptionModeSwitchMap.STORAGE_KIND_SWITCH[intEncryptionStorageKind.ordinal()]) {
            case 1:
            default:
                break;
            case 2:
                if (encryptedValueLocation.hasIndex()) {
                    list1.add(SimpleInstruction.forOpcode(89));
                    list1.add(Instruction.createIntPush(encryptedValueLocation.getIndex()));
                    list1.add(SimpleInstruction.forOpcode(47));
                } else {
                    list1.add(SimpleInstruction.forOpcode(92));
                }

                list1.add(SimpleInstruction.forOpcode(136));
                break;
            case 3:
                label38:
                {
                    long decryptionKey = encryptedValueLocation.getDecryptionKey();
                    ConstantPool constantPool3 = this.currentClass.getClassConstantPool();
                    int index = encryptedValueLocation.getIndex();
                    int bn;
                    long bo;
                    long bp;
                    if (long1 != null) {
                        if (localVariableIndex1 != null) {
                            int bj = index ^ (int) (decryptionKey & 32767L);
                            Instruction.appendIntConstant(bj, list1, constantPool3, list2);
                            list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 11));
                            long bm = long1 ^ decryptionKey;
                            list1.add(Instruction.createLongConstantLoad(bm, constantPool3, list2));
                            list1.add(SimpleInstruction.forOpcode(131));
                            break label38;
                        }

                        bn = index;
                        bo = decryptionKey;
                        bp = 32767L;
                    } else {
                        bn = index;
                        bo = decryptionKey;
                        bp = 32767L;
                    }

                    int bk = bn ^ (int) (bo & bp);
                    Instruction.appendIntConstant(bk, list1, constantPool3, list2);
                    Instruction.appendLongConstant(decryptionKey, list1, constantPool3, list2);
                }

                list1.add(new ConstantRefInstruction(184, this.xorLookupMethod));
                break;
            case 4:
                long be = encryptedValueLocation.getDecryptionKey();
                ConstantPool constantPool2 = this.currentClass.getClassConstantPool();
                int bg = encryptedValueLocation.getIndex();
                if (long1 != null && localVariableIndex1 != null) {
                    int bi = bg ^ (int) (be & 32767L);
                    Instruction.appendIntConstant(bi, list1, constantPool2, list2);
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 11));
                    long bl = long1 ^ be;
                    list1.add(Instruction.createLongConstantLoad(bl, constantPool2, list2));
                    list1.add(SimpleInstruction.forOpcode(131));
                }

                list1.add(new ConstantRefInstruction(184, encryptedValueLocation.getLookupMethod()));
                break;
            case 5:
                long ba = encryptedValueLocation.getDecryptionKey();
                ConstantPool constantPool1 = this.currentClass.getClassConstantPool();
                int bb = encryptedValueLocation.getIndex();
                if (long1 != null && localVariableIndex1 != null) {
                    int bc = bb ^ (int) (ba & 32767L);
                    Instruction.appendIntConstant(bc, list1, constantPool1, list2);
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 11));
                    long bd = long1 ^ ba;
                    list1.add(Instruction.createLongConstantLoad(bd, constantPool1, list2));
                    list1.add(SimpleInstruction.forOpcode(131));
                }

                list1.add(new InvokeDynamicInstruction(encryptedValueLocation.getIndyEntry()));
        }

        list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
    }
}
