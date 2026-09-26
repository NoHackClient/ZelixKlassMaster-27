package com.zelix.klassmaster.obfuscator.constants;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
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

public class LongConstantEncryptor {
    public ProgramClass targetClass;
    public ResolvedFieldRef cipherCacheField;
    private SecretKeyFactory secretKeyFactory;
    public Cipher desCipher;
    public ResolvedMethodRef simpleLookupMethod;
    public int indexXorKey;
    public Long classXorKey;
    public ResolvedFieldRef longCacheField;
    public ResolvedInvokeDynamic lookupInvokeDynamic;
    public IvParameterSpec zeroIv;
    public ResolvedMethodRef desLookupMethod;
    public Random random = ZkmUtils.createRandom(5023);
    public Iterator randomLongs;

    public void resetForClass(ProgramClass programClass1) {
        this.targetClass = programClass1;
        this.indexXorKey = 0;
        this.classXorKey = null;
        this.lookupInvokeDynamic = null;
        this.simpleLookupMethod = null;
        this.desLookupMethod = null;
        this.longCacheField = null;
        this.cipherCacheField = null;
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
        arrayList.add(Instruction.createIntLoad(0, localVariableList1, 12));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createLongConstantLoad(32767L, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntConstantPush(this.indexXorKey, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntStore(3, localVariableList1, 12));
        arrayList.add(new ConstantRefInstruction(178, this.longCacheField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new BranchInstruction(199, labelInstruction2));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(48));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(40));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(6));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(32));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(7));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(24));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(8));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(16));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(7));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 12));
        ArrayList arrayList1;
        byte bd;
        LocalVariableList localVariableList3;
        byte be;
        if (bl) {
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
            arrayList.add(SimpleInstruction.forOpcode(47));
            arrayList1 = arrayList;
            long bc = 62906520448321L;
            bd = 5;
            localVariableList3 = localVariableList1;
            be = 12;
        } else {
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList1 = arrayList;
            long ba = 62906520448321L;
            bd = 5;
            localVariableList3 = localVariableList1;
            be = 12;
        }

        Integer integer1 = Integer.valueOf(be);
        LocalVariableList localVariableList2 = localVariableList3;
        Integer integer = Integer.valueOf(bd);
        arrayList1.add(Instruction.createLongStore(integer, localVariableList2, integer1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(48));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(40));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(6));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(32));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(7));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(24));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(8));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(16));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(7));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createObjectStore(7, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", list1, classMemberLookup1, classResolver1
        );
        byte bb;
        if (this.targetClass.supportsJava5()) {
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            if (this.targetClass.supportsJava19()) {
                ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                        "java/lang/Thread", "threadId", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
            } else {
                ResolvedMethodRefConstant resolvedMethodRefConstant14 = constantPool1.getOrAddMethodRef(
                        "java/lang/Thread", "getId", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant14));
            }

            ResolvedMethodRefConstant resolvedMethodRefConstant15 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant15));
            arrayList1 = arrayList;
            bb = 8;
        } else {
            ResolvedClassConstant resolvedClassConstant6 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant6));
            arrayList.add(SimpleInstruction.forOpcode(89));
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                    "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
            arrayList.add(SimpleInstruction.forOpcode(133));
            ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "<init>", "(J)V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant3));
            arrayList1 = arrayList;
            bb = 8;
        }

        arrayList1.add(Instruction.createObjectStore(bb, localVariableList1, 12));
        arrayList.add(new ConstantRefInstruction(178, this.cipherCacheField));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 12));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef1));
        ResolvedClassConstant resolvedClassConstant7 = constantPool1.getOrCreateClassConstant("[Ljava/lang/Object;", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant7));
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 12));
        arrayList.add(labelInstruction3);
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(new BranchInstruction(199, labelInstruction));
        arrayList.add(SimpleInstruction.forOpcode(6));
        ResolvedClassConstant resolvedClassConstant8 = constantPool1.getOrCreateClassConstant("java/lang/Object", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant8));
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(3));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("DES/CBC/NoPadding", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(4));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("DES", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant5));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(5));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("javax/crypto/spec/IvParameterSpec", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant1));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/IvParameterSpec", "<init>", "([B)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant6));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(new ConstantRefInstruction(178, this.cipherCacheField));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        arrayList.add(SimpleInstruction.forOpcode(87));
        arrayList.add(labelInstruction);
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("javax/crypto/spec/DESKeySpec", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant2));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/DESKeySpec", "<init>", "([B)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant7));
        arrayList.add(Instruction.createObjectStore(11, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("javax/crypto/SecretKeyFactory", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant3));
        arrayList.add(Instruction.createObjectLoad(11, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "generateSecret", "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        arrayList.add(Instruction.createObjectStore(12, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("javax/crypto/Cipher", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant4));
        arrayList.add(Instruction.createObjectStore(13, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(13, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(2));
        arrayList.add(Instruction.createObjectLoad(12, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "init", "(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        arrayList.add(Instruction.createObjectLoad(13, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(7, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "doFinal", "([B)[B", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        arrayList.add(Instruction.createObjectStore(10, localVariableList1, 12));
        arrayList.add(labelInstruction4);
        arrayList.add(new GotoInstruction(labelInstruction1));
        arrayList.add(labelInstruction5);
        arrayList.add(Instruction.createObjectStore(11, localVariableList1, 12));
        ResolvedClassConstant resolvedClassConstant5 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant5));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createStringConstantLoad(constantPool1.getClassName(), constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(11, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant11 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant11));
        arrayList.add(SimpleInstruction.forOpcode(191));
        arrayList.add(labelInstruction1);
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(48));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(40));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(6));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(32));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(7));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(24));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(8));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(16));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createObjectLoad(10, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(7));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, arrayList, constantPool1, list1);
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(SimpleInstruction.forOpcode(129));
        arrayList.add(Instruction.createLongStore(11, localVariableList1, 12));
        arrayList.add(new ConstantRefInstruction(178, this.longCacheField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
        if (this.targetClass.supportsJava5()) {
            arrayList.add(Instruction.createLongLoad(11, localVariableList1, 12));
            ResolvedMethodRefConstant resolvedMethodRefConstant12 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant12));
            arrayList1 = arrayList;
            bb = 83;
        } else {
            ResolvedClassConstant resolvedClassConstant9 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant9));
            arrayList.add(SimpleInstruction.forOpcode(89));
            arrayList.add(Instruction.createLongLoad(11, localVariableList1, 12));
            ResolvedMethodRefConstant resolvedMethodRefConstant13 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "<init>", "(J)V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant13));
            arrayList1 = arrayList;
            bb = 83;
        }

        arrayList1.add(SimpleInstruction.forOpcode(bb));
        arrayList.add(labelInstruction2);
        arrayList.add(new ConstantRefInstruction(178, this.longCacheField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedMethodRefConstant resolvedMethodRefConstant16 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "longValue", "()J", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant16));
        arrayList.add(SimpleInstruction.forOpcode(173));
    }

    public void emitSwitchDispatch(
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
            this.emitKeyDerivation(
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
            this.emitKeyDerivation(
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

    public static boolean isLongEncryptionEnabled(ProgramClass programClass1) {
        return HiddenOptionFlags.LONG_ENCRYPT_INDY && ReferenceObfuscator.supportsMethodHandles(programClass1);
    }

    public void emitDecryptAndStore(
            LocalVariableList localVariableList1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            ObfuscatedReferenceSlot obfuscatedReferenceSlot,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            List list2
    ) {
        LongEncryptionStorageKind longEncryptionStorageKind = obfuscatedReferenceSlot.getStorageKind();
        switch (LongEncryptionKindSwitchMap.STORAGE_KIND_SWITCH[longEncryptionStorageKind.ordinal()]) {
            case 1:
            default:
                break;
            case 2:
                if (obfuscatedReferenceSlot.hasIndex()) {
                    list1.add(SimpleInstruction.forOpcode(89));
                    list1.add(Instruction.createIntPush(obfuscatedReferenceSlot.getIndex()));
                    list1.add(SimpleInstruction.forOpcode(47));
                } else {
                    list1.add(SimpleInstruction.forOpcode(92));
                }
                break;
            case 3:
                label36:
                {
                    long encryptionKey = obfuscatedReferenceSlot.getEncryptionKey();
                    ConstantPool constantPool3 = this.targetClass.getClassConstantPool();
                    int index = obfuscatedReferenceSlot.getIndex();
                    int bn;
                    long bo;
                    long bp;
                    if (long1 != null) {
                        if (localVariableIndex1 != null) {
                            int bj = index ^ (int) (encryptionKey & 32767L);
                            Instruction.appendIntConstant(bj, list1, constantPool3, list2);
                            list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 12));
                            long bm = long1 ^ encryptionKey;
                            list1.add(Instruction.createLongConstantLoad(bm, constantPool3, list2));
                            list1.add(SimpleInstruction.forOpcode(131));
                            break label36;
                        }

                        bn = index;
                        bo = encryptionKey;
                        bp = 32767L;
                    } else {
                        bn = index;
                        bo = encryptionKey;
                        bp = 32767L;
                    }

                    int bk = bn ^ (int) (bo & bp);
                    Instruction.appendIntConstant(bk, list1, constantPool3, list2);
                    Instruction.appendLongConstant(encryptionKey, list1, constantPool3, list2);
                }

                list1.add(new ConstantRefInstruction(184, this.simpleLookupMethod));
                break;
            case 4:
                long be = obfuscatedReferenceSlot.getEncryptionKey();
                ConstantPool constantPool2 = this.targetClass.getClassConstantPool();
                int bg = obfuscatedReferenceSlot.getIndex();
                if (long1 != null && localVariableIndex1 != null) {
                    int bi = bg ^ (int) (be & 32767L);
                    Instruction.appendIntConstant(bi, list1, constantPool2, list2);
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 12));
                    long bl = long1 ^ be;
                    list1.add(Instruction.createLongConstantLoad(bl, constantPool2, list2));
                    list1.add(SimpleInstruction.forOpcode(131));
                }

                list1.add(new ConstantRefInstruction(184, obfuscatedReferenceSlot.getLookupMethod()));
                break;
            case 5:
                long ba = obfuscatedReferenceSlot.getEncryptionKey();
                ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
                int bb = obfuscatedReferenceSlot.getIndex();
                if (long1 != null && localVariableIndex1 != null) {
                    int bc = bb ^ (int) (ba & 32767L);
                    Instruction.appendIntConstant(bc, list1, constantPool1, list2);
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 12));
                    long bd = long1 ^ ba;
                    list1.add(Instruction.createLongConstantLoad(bd, constantPool1, list2));
                    list1.add(SimpleInstruction.forOpcode(131));
                }

                list1.add(new InvokeDynamicInstruction(obfuscatedReferenceSlot.getInvokeDynamic()));
        }

        list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
    }

    public void emitKeyBytes(
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
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        list1.add(Instruction.createLongStore(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(8, list1, constantPool1, list2);
        list1.add(new NewArrayInstruction(8));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(56, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(48, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(5));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(40, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(6));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(32, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(7));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(24, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(8));
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(16, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        Instruction.appendIntConstant(6, list1, constantPool1, list2);
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        Instruction.appendIntConstant(8, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(SimpleInstruction.forOpcode(89));
        Instruction.appendIntConstant(7, list1, constantPool1, list2);
        list1.add(Instruction.createLongLoad(andIncrement, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(SimpleInstruction.forOpcode(145));
        list1.add(SimpleInstruction.forOpcode(84));
        list1.add(Instruction.createObjectLoad(ba, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(95));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "doFinal", "([B)[B", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(bc, localVariableList1, 12));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(56, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(48, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(5));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(40, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(6));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(32, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(7));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(24, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(8));
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(16, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        Instruction.appendIntConstant(6, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        Instruction.appendIntConstant(8, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(121));
        list1.add(SimpleInstruction.forOpcode(129));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 12));
        Instruction.appendIntConstant(7, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(51));
        list1.add(SimpleInstruction.forOpcode(133));
        Instruction.appendLongConstant(255L, list1, constantPool1, list2);
        list1.add(SimpleInstruction.forOpcode(127));
        list1.add(SimpleInstruction.forOpcode(129));
    }

    public ResolvedInvokeDynamic getLookupInvokeDynamic() {
        return this.lookupInvokeDynamic;
    }

    public ResolvedFieldRef getLongCacheField() {
        return this.longCacheField;
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
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("DES/CBC/NoPadding", list2, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectStore(ba, localVariableList1, 12));
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
        arrayList.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createIntStore(andIncrement, localVariableList1, 12));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new BranchInstruction(162, labelInstruction1));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 12));
        arrayList.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 12));
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 12));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(104));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createIntIncrement(andIncrement, 1, localVariableList1, 12));
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

    public void emitEncryptedConstantLoad(
            LocalVariableList localVariableList1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            ConstantLong constantLong,
            ObfuscatedReferenceSlot obfuscatedReferenceSlot,
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
        if (obfuscatedReferenceSlot.hasArrayField()) {
            list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
        } else if (obfuscatedReferenceSlot.hasLocalVariable()) {
            list1.add(Instruction.createLongStore(obfuscatedReferenceSlot.getLocalVariableIndex(), localVariableList1, 12));
        }
    }

    public void emitEncryptedStringTable(
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
        List list3 = this.buildEncryptedStrings(observableHolders, setMultiMap, map1, set1, long1, bl, list2, this.targetClass.getClassConstantPool());
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        int andIncrement = localVariableAllocator.getAndIncrement();
        int bd = localVariableAllocator.getAndIncrement();
        int be = localVariableAllocator.getAndIncrement();
        int bf = localVariableAllocator.getAndIncrement();
        int bg = ba == -1 ? localVariableAllocator.getAndIncrement() : ba;
        int bh = localVariableAllocator.getAndIncrement();
        Instruction.appendIntConstant(bb, list1, constantPool1, list2);
        list1.add(new NewArrayInstruction(11));
        list1.add(Instruction.createObjectStore(bg, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bd, localVariableList1, 12));
        Iterator iterator = list3.iterator();

        while (iterator.hasNext()) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
            LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
            list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createObjectStore(be, localVariableList1, 12));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "length", "()I", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
            list1.add(Instruction.createIntStore(bf, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(3));
            list1.add(Instruction.createIntStore(andIncrement, localVariableList1, 12));
            list1.add(labelInstruction1);
            list1.add(Instruction.createObjectLoad(be, localVariableList1, 12));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 12));
            list1.add(Instruction.createIntIncrement(andIncrement, 8, localVariableList1, 12));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 12));
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
            list1.add(Instruction.createObjectStore(bh, localVariableList1, 12));
            list1.add(Instruction.createObjectLoad(bg, localVariableList1, 12));
            list1.add(Instruction.createIntLoad(bd, localVariableList1, 12));
            list1.add(Instruction.createIntIncrement(bd, 1, localVariableList1, 12));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(3));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(56));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(4));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(48));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(5));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(40));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(6));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(32));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(7));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(24));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(SimpleInstruction.forOpcode(8));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(16));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
            list1.add(Instruction.createIntPush(6));
            list1.add(SimpleInstruction.forOpcode(51));
            list1.add(SimpleInstruction.forOpcode(133));
            Instruction.appendLongConstant(255L, list1, constantPool1, list2);
            list1.add(SimpleInstruction.forOpcode(127));
            list1.add(Instruction.createIntPush(8));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 12));
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
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 12));
            list1.add(Instruction.createIntLoad(bf, localVariableList1, 12));
            list1.add(new BranchInstruction(161, labelInstruction1));
        }

        if (resolvedFieldRef != null) {
            list1.add(Instruction.createObjectLoad(bg, localVariableList1, 12));
            list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
            Instruction.appendIntConstant(bb, list1, constantPool1, list2);
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Long", list2);
            list1.add(new ConstantRefInstruction(189, resolvedClassConstant));
            list1.add(new ConstantRefInstruction(179, this.longCacheField));
        }
    }

    public long desEncrypt(long ba, long bb) throws ZkmProcessingException {
        return this.desTransform(ba, bb);
    }

    public void emitLoadKeyLocal(LocalVariableList localVariableList1, List list1, int ba) {
        list1.add(Instruction.createLongLoad(ba, localVariableList1, 12));
        list1.add(SimpleInstruction.forOpcode(131));
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
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "<init>", "(Ljava/lang/invoke/MethodType;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        arrayList.add(Instruction.createObjectStore(3, localVariableList1, 12));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 12));
        ResolvedMethodHandleConstant resolvedMethodHandleConstant = constantPool1.getOrAddMethodHandle(
                MethodHandleRefKind.REF_INVOKE_STATIC, resolvedMethodRef, list1
        );
        arrayList.add(new ConstantRefInstruction(19, resolvedMethodHandleConstant));
        arrayList.add(Instruction.createClassConstantLoad(constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 12));
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
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(2));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 12));
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
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 12));
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
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 12));
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
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 12));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(" : ", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "toString", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant10));
        arrayList.add(SimpleInstruction.forOpcode(191));
        arrayList.add(labelInstruction3);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(176));
    }

    public List buildEncryptedStrings(
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
                ObfuscatedReferenceSlot obfuscatedReferenceSlot = (ObfuscatedReferenceSlot) map1.get(observableHolder);
                Iterator iterator = setMultiMap.getValues(observableHolder).iterator();

                while (iterator.hasNext()) {
                    ConstantLong constantLong = (ConstantLong) iterator.next();
                    set1.add(constantLong);
                }

                LongEncryptionStorageKind longEncryptionStorageKind = obfuscatedReferenceSlot.getStorageKind();
                long bh = (Long) observableHolder.getValue();
                short bd = 0;
                if (longEncryptionStorageKind.equals(LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX)) {
                    long be = (Long) this.randomLongs.next();
                    obfuscatedReferenceSlot.setEncryptionKey(be);
                    bd = (short) (bb ^ this.indexXorKey);
                    bh ^= be;
                } else {
                    label76:
                    {
                        Iterator iterator1;
                        if (!longEncryptionStorageKind.equals(LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES)) {
                            if (!longEncryptionStorageKind.equals(LongEncryptionStorageKind.INDY_ENTRY_AND_INDEX)) {
                                break label76;
                            }

                            iterator1 = this.randomLongs;
                        } else {
                            iterator1 = this.randomLongs;
                        }

                        long bi = (Long) iterator1.next();
                        obfuscatedReferenceSlot.setEncryptionKey(bi);
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
                    bj = bh ^ this.classXorKey;
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
                if (longEncryptionStorageKind.equals(LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX)
                        || longEncryptionStorageKind.equals(LongEncryptionStorageKind.LOOKUP_METHOD_AND_INDEX_DES)
                        || longEncryptionStorageKind.equals(LongEncryptionStorageKind.INDY_ENTRY_AND_INDEX)) {
                    obfuscatedReferenceSlot.setIndex(bd);
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

    public LongConstantEncryptor() {
        LongStream longStream = this.random.longs(1L, Long.MAX_VALUE);
        this.randomLongs = longStream.iterator();
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
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Integer", "intValue", "()I", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        arrayList.add(Instruction.createIntStore(4, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "longValue", "()J", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        arrayList.add(Instruction.createLongStore(5, localVariableList1, 12));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 12));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 12));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef));
        arrayList.add(Instruction.createLongStore(7, localVariableList1, 12));
        Integer integer = this.targetClass.hasReleaseVersion() ? this.targetClass.getReleaseVersion() : null;
        AbstractFieldInfo abstractFieldInfo = classResolver1.getVersionedClass("java/lang/Long", integer).findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef("java/lang/Long", "TYPE", "Ljava/lang/Class;", list1, abstractFieldInfo);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(Instruction.createLongLoad(7, localVariableList1, 12));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list1, classMemberLookup1, classResolver1
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
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 12));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(5));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Class", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant2));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        AbstractFieldInfo abstractFieldInfo1 = classResolver1.getVersionedClass("java/lang/Integer", integer).findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef("java/lang/Integer", "TYPE", "Ljava/lang/Class;", list1, abstractFieldInfo1);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
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
        arrayList.add(Instruction.createLongLoad(7, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(173));
    }

    public ResolvedMethodRef getLookupMethod() {
        return this.desLookupMethod != null ? this.desLookupMethod : this.simpleLookupMethod;
    }

    public ResolvedFieldRef getCipherCacheField() {
        return this.cipherCacheField;
    }

    public boolean hasClassXorKey() {
        return this.classXorKey != null;
    }

    public void emitKeyDerivation(
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
            this.emitKeyBytes(localVariableList1, list1, list2, integer, localVariableAllocator, classMemberLookup1, classResolver1);
        } else if (localVariableIndex1 != null) {
            this.emitLoadKeyLocal(localVariableList1, list1, localVariableIndex1.getIndex());
        } else {
            this.emitLoadKeyLocal(localVariableList1, list1, integer1);
        }
    }

    public long getClassXorKey() {
        return this.classXorKey;
    }

    public void initializeForClass(
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
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        if (bl3) {
            if (resolvedFieldRef != null && bl1) {
                this.indexXorKey = this.random.nextInt(32766) + 1;
                ProgramClass programClass3 = this.targetClass;
                byte bh;
                boolean bl5;
                InheritedMemberAnalyzer inheritedMemberAnalyzer5;
                ClassMemberLookup classMemberLookup6;
                byte bj;
                if (this.targetClass.isInterface()) {
                    bh = 4;
                    long bi = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 12;
                } else {
                    bh = 1;
                    long bb = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 12;
                }

                Integer integer = Integer.valueOf(bj);
                ClassMemberLookup classMemberLookup2 = classMemberLookup6;
                InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer5;
                Boolean boolean1 = bl5;
                FieldInfo fieldInfo = programClass3.createUniquelyNamedStaticField(
                        "[Ljava/lang/Long;", bh, boolean1, inheritedMemberAnalyzer1, classMemberLookup2, integer
                );
                this.longCacheField = constantPool1.getOrAddFieldRef(
                        this.targetClass.getClassName(), fieldInfo.getSourceName(), fieldInfo.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                programClass3 = this.targetClass;
                if (this.targetClass.isInterface()) {
                    bh = 4;
                    long bc = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 12;
                } else {
                    bh = 1;
                    long bd = 136463318042509L;
                    bl5 = true;
                    inheritedMemberAnalyzer5 = inheritedMemberAnalyzer;
                    classMemberLookup6 = classMemberLookup1;
                    bj = 12;
                }

                Integer integer1 = Integer.valueOf(bj);
                ClassMemberLookup classMemberLookup3 = classMemberLookup6;
                InheritedMemberAnalyzer inheritedMemberAnalyzer2 = inheritedMemberAnalyzer5;
                Boolean boolean2 = bl5;
                FieldInfo fieldInfo1 = programClass3.createUniquelyNamedStaticField(
                        "Ljava/util/Map;", bh, boolean2, inheritedMemberAnalyzer2, classMemberLookup3, integer1
                );
                this.cipherCacheField = constantPool1.getOrAddFieldRef(
                        this.targetClass.getClassName(), fieldInfo1.getSourceName(), fieldInfo1.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[1];
                LocalVariableList localVariableList1 = new LocalVariableList(true, "(IJ)J", 14);
                ArrayList arrayList = new ArrayList();
                this.buildDesLookupMethodBody(
                        localVariableList1, arrayList, bl1, resolvedFieldRef, exceptionHandlerSpecs, list1, constantPool1, classMemberLookup1, classResolver1
                );
                MethodInfo methodInfo1 = this.targetClass
                        .createUniquelyNamedStaticMethod(
                                "(IJ)J",
                                arrayList,
                                6,
                                14,
                                1,
                                localVariableList1,
                                exceptionHandlerSpecs,
                                "Long Constant Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                12
                        );
                this.desLookupMethod = this.targetClass.addMethodRef(methodInfo1, list1);
                if (bl2) {
                    LocalVariableList localVariableList2 = new LocalVariableList(
                            true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)J", 10
                    );
                    ArrayList arrayList1 = new ArrayList();
                    this.buildCallSiteTargetBody(localVariableList2, arrayList1, this.desLookupMethod, list1, constantPool1, classMemberLookup1, classResolver1);
                    MethodInfo methodInfo2 = this.targetClass
                            .createUniquelyNamedStaticMethod(
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)J",
                                    arrayList1,
                                    7,
                                    10,
                                    1,
                                    localVariableList2,
                                    new ExceptionHandlerSpec[0],
                                    "Long Constant Encryption",
                                    list1,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1,
                                    12
                            );
                    ResolvedMethodRef resolvedMethodRef = this.targetClass.addMethodRef(methodInfo2, list1);
                    ExceptionHandlerSpec[] exceptionHandlerSpecs1 = new ExceptionHandlerSpec[1];
                    LocalVariableList localVariableList3 = new LocalVariableList(
                            true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", 5
                    );
                    ArrayList arrayList2 = new ArrayList();
                    this.buildBootstrapMethodBody(
                            localVariableList3, arrayList2, resolvedMethodRef, exceptionHandlerSpecs1, list1, constantPool1, classMemberLookup1, classResolver1
                    );
                    MethodInfo methodInfo3 = this.targetClass
                            .createUniquelyNamedStaticMethod(
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                    arrayList2,
                                    7,
                                    5,
                                    1,
                                    localVariableList3,
                                    exceptionHandlerSpecs1,
                                    "Long Constant Encryption",
                                    list1,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1,
                                    12
                            );
                    ResolvedMethodRef resolvedMethodRef1 = this.targetClass.addMethodRef(methodInfo3, list1);
                    String string = String.valueOf((char) (97 + this.random.nextInt(26)));
                    this.lookupInvokeDynamic = this.targetClass.createInvokeDynamic(string, "(IJ)J", resolvedMethodRef1, list1, constantPool1);
                }
            }
        } else if (resolvedFieldRef != null) {
            if (bl1) {
                this.indexXorKey = this.random.nextInt(32766) + 1;
                this.classXorKey = (Long) this.randomLongs.next();
                ProgramClass programClass2 = this.targetClass;
                byte ba;
                boolean bl4;
                InheritedMemberAnalyzer inheritedMemberAnalyzer4;
                ClassMemberLookup classMemberLookup5;
                byte bg;
                if (this.targetClass.isInterface()) {
                    ba = 4;
                    long be = 136463318042509L;
                    bl4 = true;
                    inheritedMemberAnalyzer4 = inheritedMemberAnalyzer;
                    classMemberLookup5 = classMemberLookup1;
                    bg = 12;
                } else {
                    ba = 1;
                    long bf = 136463318042509L;
                    bl4 = true;
                    inheritedMemberAnalyzer4 = inheritedMemberAnalyzer;
                    classMemberLookup5 = classMemberLookup1;
                    bg = 12;
                }

                Integer integer2 = Integer.valueOf(bg);
                ClassMemberLookup classMemberLookup4 = classMemberLookup5;
                InheritedMemberAnalyzer inheritedMemberAnalyzer3 = inheritedMemberAnalyzer4;
                Boolean boolean3 = bl4;
                FieldInfo fieldInfo2 = programClass2.createUniquelyNamedStaticField(
                        "[Ljava/lang/Long;", ba, boolean3, inheritedMemberAnalyzer3, classMemberLookup4, integer2
                );
                this.longCacheField = constantPool1.getOrAddFieldRef(
                        this.targetClass.getClassName(), fieldInfo2.getSourceName(), fieldInfo2.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                ExceptionHandlerSpec[] exceptionHandlerSpecs2 = new ExceptionHandlerSpec[0];
                LocalVariableList localVariableList4 = new LocalVariableList(true, "(IJ)J", 4);
                ArrayList arrayList3 = new ArrayList();
                this.buildSimpleLookupMethodBody(localVariableList4, arrayList3, resolvedFieldRef, list1, constantPool1, classMemberLookup1, classResolver1);
                MethodInfo methodInfo4 = this.targetClass
                        .createUniquelyNamedStaticMethod(
                                "(IJ)J",
                                arrayList3,
                                8,
                                4,
                                1,
                                localVariableList4,
                                exceptionHandlerSpecs2,
                                "Long Constant Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                12
                        );
                this.simpleLookupMethod = this.targetClass.addMethodRef(methodInfo4, list1);
            } else {
                this.classXorKey = (Long) this.randomLongs.next();
            }
        } else if (bl1) {
            if (!bl) {
                this.classXorKey = (Long) this.randomLongs.next();
            }
        } else if (!bl) {
            this.classXorKey = (Long) this.randomLongs.next();
        }
    }

    public void buildSimpleLookupMethodBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ResolvedFieldRef resolvedFieldRef,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        arrayList.add(Instruction.createIntLoad(0, localVariableList1, 12));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntConstantPush(this.indexXorKey, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntConstantPush(32767, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(Instruction.createIntStore(3, localVariableList1, 12));
        arrayList.add(new ConstantRefInstruction(178, this.longCacheField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new BranchInstruction(199, labelInstruction));
        arrayList.add(new ConstantRefInstruction(178, this.longCacheField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
        ArrayList arrayList1;
        byte ba;
        if (this.targetClass.supportsJava5()) {
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
            arrayList.add(SimpleInstruction.forOpcode(47));
            arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
            arrayList.add(SimpleInstruction.forOpcode(131));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            arrayList1 = arrayList;
            ba = 83;
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant));
            arrayList.add(SimpleInstruction.forOpcode(89));
            arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
            arrayList.add(SimpleInstruction.forOpcode(47));
            arrayList.add(Instruction.createLongLoad(1, localVariableList1, 12));
            arrayList.add(SimpleInstruction.forOpcode(131));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "<init>", "(J)V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
            arrayList1 = arrayList;
            ba = 83;
        }

        arrayList1.add(SimpleInstruction.forOpcode(ba));
        arrayList.add(labelInstruction);
        arrayList.add(new ConstantRefInstruction(178, this.longCacheField));
        arrayList.add(Instruction.createIntLoad(3, localVariableList1, 12));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "longValue", "()J", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        arrayList.add(SimpleInstruction.forOpcode(173));
    }

    public long desTransform(long ba, long bb) throws ZkmProcessingException {
        byte[] bc = ByteConversionUtils.longToBytes(bb);

        try {
            DESKeySpec dESKeySpec = new DESKeySpec(bc);
            if (this.secretKeyFactory == null) {
                this.secretKeyFactory = SecretKeyFactory.getInstance("DES");
            }

            if (this.desCipher == null) {
                this.desCipher = Cipher.getInstance("DES/CBC/NoPadding");
                this.zeroIv = new IvParameterSpec(new byte[8]);
            }

            SecretKey secretKey = this.secretKeyFactory.generateSecret(dESKeySpec);
            this.desCipher.init(1, secretKey, this.zeroIv);
            byte[] bd = ByteConversionUtils.longToBytes(ba);
            return ByteConversionUtils.bytesToLong(this.desCipher.doFinal(bd));
        } catch (Exception exception) {
            throw new ZkmProcessingException(exception.toString(), exception);
        }
    }

    public void emitCipherCacheInit(List list1, List list2, ConstantPool constantPool1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1) throws ZkmException, IOException {
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/util/HashMap", list2);
        list1.add(new TypeInstruction(resolvedClassConstant));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(13));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/util/HashMap", "<init>", "(I)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        list1.add(new ConstantRefInstruction(179, this.cipherCacheField));
    }
}
