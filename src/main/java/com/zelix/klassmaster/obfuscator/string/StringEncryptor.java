package com.zelix.klassmaster.obfuscator.string;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
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
import com.zelix.klassmaster.classfile.insn.JsrInstruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LdcInstruction;
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
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.RankedValue;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.LongStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class StringEncryptor {
    public static final int MAX_XOR_KEY_VALUE = HiddenOptionFlags.SEVEN_BIT_STRING_KEYS ? 127 : 255;
    public static final int MAX_CHUNK_KEY_VALUE = HiddenOptionFlags.SEVEN_BIT_STRING_KEYS ? 127 : 255;
    public int[] xorKeys;
    public ResolvedMethodRefConstant toCharArrayHelperRef;
    public ResolvedInvokeDynamic stringIndyEntry;
    public ResolvedMethodRefConstant toCharArrayRef;
    public int[] byteShuffleTable;
    public ResolvedMethodRefConstant stringConstructorRef;
    public ResolvedFieldRef stringArrayFieldRef;
    public ResolvedMethodRefConstant internMethodRef;
    private Cipher desCipher;
    public ProgramClass targetClass;
    public ResolvedFieldRef cacheMapFieldRef;
    private SecretKeyFactory desKeyFactory;
    public int indexXorKey;
    public boolean useHelperMethods;
    public ResolvedMethodRef bytesToStringMethodRef;
    public ResolvedMethodRef lookupMethodRef;
    public IvParameterSpec desIv;
    public ResolvedMethodRefConstant decryptHelperRef;
    public Random random = ZkmUtils.createRandom(1028);
    public final boolean useAlternateLoopEntry;
    public final boolean lazyDecrypt;
    public Iterator randomLongKeys;

    public void emitDecryptCall(
            LocalVariableList localVariableList1,
            List list1,
            List list2,
            LocalVariableAllocator localVariableAllocator,
            Integer integer,
            boolean bl,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        if (this.useHelperMethods) {
            list1.add(new ConstantRefInstruction(184, this.toCharArrayHelperRef));
            list1.add(new ConstantRefInstruction(184, this.decryptHelperRef));
        } else if (integer != null) {
            int andIncrement = localVariableAllocator.getAndIncrement();
            ClassMemberLookup classMemberLookup2 = classMemberLookup1;
            ResolvedMethodRefConstant resolvedMethodRefConstant = this.internMethodRef;
            Integer integer1 = andIncrement;
            this.emitDesDecryptCall(localVariableList1, list1, list2, integer, integer1, resolvedMethodRefConstant, classMemberLookup2, classResolver1);
        } else if (bl) {
            this.emitKeyedXorDecryptLoop(
                    localVariableList1,
                    list1,
                    list2,
                    localVariableAllocator.getAndIncrement(),
                    this.toCharArrayRef,
                    this.stringConstructorRef,
                    this.internMethodRef,
                    false,
                    true
            );
        } else {
            this.emitXorDecryptLoop(
                    localVariableList1,
                    list1,
                    list2,
                    localVariableAllocator.getAndIncrement(),
                    this.toCharArrayRef,
                    this.stringConstructorRef,
                    this.internMethodRef,
                    false,
                    true
            );
        }
    }

    public int[] createShuffledByteTable() {
        int[] ba = new int[]{
                0,
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                17,
                18,
                19,
                20,
                21,
                22,
                23,
                24,
                25,
                26,
                27,
                28,
                29,
                30,
                31,
                32,
                33,
                34,
                35,
                36,
                37,
                38,
                39,
                40,
                41,
                42,
                43,
                44,
                45,
                46,
                47,
                48,
                49,
                50,
                51,
                52,
                53,
                54,
                55,
                56,
                57,
                58,
                59,
                60,
                61,
                62,
                63,
                64,
                65,
                66,
                67,
                68,
                69,
                70,
                71,
                72,
                73,
                74,
                75,
                76,
                77,
                78,
                79,
                80,
                81,
                82,
                83,
                84,
                85,
                86,
                87,
                88,
                89,
                90,
                91,
                92,
                93,
                94,
                95,
                96,
                97,
                98,
                99,
                100,
                101,
                102,
                103,
                104,
                105,
                106,
                107,
                108,
                109,
                110,
                111,
                112,
                113,
                114,
                115,
                116,
                117,
                118,
                119,
                120,
                121,
                122,
                123,
                124,
                125,
                126,
                127,
                128,
                129,
                130,
                131,
                132,
                133,
                134,
                135,
                136,
                137,
                138,
                139,
                140,
                141,
                142,
                143,
                144,
                145,
                146,
                147,
                148,
                149,
                150,
                151,
                152,
                153,
                154,
                155,
                156,
                157,
                158,
                159,
                160,
                161,
                162,
                163,
                164,
                165,
                166,
                167,
                168,
                169,
                170,
                171,
                172,
                173,
                174,
                175,
                176,
                177,
                178,
                179,
                180,
                181,
                182,
                183,
                184,
                185,
                186,
                187,
                188,
                189,
                190,
                191,
                192,
                193,
                194,
                195,
                196,
                197,
                198,
                199,
                200,
                201,
                202,
                203,
                204,
                205,
                206,
                207,
                208,
                209,
                210,
                211,
                212,
                213,
                214,
                215,
                216,
                217,
                218,
                219,
                220,
                221,
                222,
                223,
                224,
                225,
                226,
                227,
                228,
                229,
                230,
                231,
                232,
                233,
                234,
                235,
                236,
                237,
                238,
                239,
                240,
                241,
                242,
                243,
                244,
                245,
                246,
                247,
                248,
                249,
                250,
                251,
                252,
                253,
                254,
                255
        };
        ZkmUtils.shuffleInts(ba, this.random);
        return ba;
    }

    public ResolvedInvokeDynamic getStringIndyEntry() {
        return this.stringIndyEntry;
    }

    public void emitKeyedXorDecryptLoop(
            LocalVariableList localVariableList1,
            List list1,
            List list2,
            int ba,
            ResolvedMethodRefConstant resolvedMethodRefConstant,
            ResolvedMethodRefConstant resolvedMethodRefConstant1,
            ResolvedMethodRefConstant resolvedMethodRefConstant2,
            boolean bl,
            boolean bl1
    ) {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction6 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction7 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction8 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction9 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction10 = new LabelInstruction(true, 1);
        LabelInstruction[] labelInstructions = new LabelInstruction[]{
                labelInstruction1, labelInstruction2, labelInstruction3, labelInstruction4, labelInstruction5, labelInstruction6
        };
        if (bl) {
            list1.add(Instruction.createIntLoad(0, localVariableList1, 1));
            list1.add(Instruction.createObjectLoad(1, localVariableList1, 1));
        } else {
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        }

        list1.add(SimpleInstruction.forOpcode(90));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(SimpleInstruction.forOpcode(91));
        list1.add(SimpleInstruction.forOpcode(87));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(ba, localVariableList1, 1));
        if (this.useAlternateLoopEntry) {
            list1.add(SimpleInstruction.forOpcode(93));
            list1.add(SimpleInstruction.forOpcode(88));
            list1.add(SimpleInstruction.forOpcode(91));
            list1.add(SimpleInstruction.forOpcode(4));
            list1.add(new BranchInstruction(163, labelInstruction9));
        } else {
            list1.add(new GotoInstruction(labelInstruction9));
        }

        list1.add(labelInstruction);
        list1.add(SimpleInstruction.forOpcode(92));
        list1.add(SimpleInstruction.forOpcode(95));
        list1.add(Instruction.createIntLoad(ba, localVariableList1, 1));
        list1.add(labelInstruction10);
        list1.add(SimpleInstruction.forOpcode(93));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(SimpleInstruction.forOpcode(95));
        list1.add(Instruction.createIntLoad(ba, localVariableList1, 1));
        list1.add(Instruction.createIntPush(7));
        list1.add(SimpleInstruction.forOpcode(112));
        list1.add(new TableSwitchInstruction(labelInstruction7, 5, labelInstructions));
        int bb = 0;
        int bc = bb;

        for (byte bd = 6; bc < bd; bd = 6) {
            list1.add(labelInstructions[bb]);
            list1.add(Instruction.createIntPush(this.xorKeys[bb]));
            list1.add(new GotoInstruction(labelInstruction8));
            bc = ++bb;
        }

        list1.add(labelInstruction7);
        list1.add(Instruction.createIntPush(this.xorKeys[6]));
        list1.add(labelInstruction8);
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(146));
        list1.add(SimpleInstruction.forOpcode(85));
        list1.add(Instruction.createIntIncrement(ba, 1, localVariableList1, 1));
        if (this.useAlternateLoopEntry) {
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(new BranchInstruction(154, labelInstruction9));
            list1.add(SimpleInstruction.forOpcode(92));
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(new GotoInstruction(labelInstruction10));
        }

        list1.add(labelInstruction9);
        list1.add(SimpleInstruction.forOpcode(93));
        list1.add(SimpleInstruction.forOpcode(88));
        list1.add(SimpleInstruction.forOpcode(91));
        list1.add(Instruction.createIntLoad(ba, localVariableList1, 1));
        list1.add(new BranchInstruction(163, labelInstruction));
        list1.add(SimpleInstruction.forOpcode(87));
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new TypeInstruction(resolvedClassConstant));
        list1.add(SimpleInstruction.forOpcode(90));
        list1.add(SimpleInstruction.forOpcode(95));
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        label41:
        if (resolvedMethodRefConstant2 != null) {
            boolean bl2;
            if (!bl1) {
                if (this.lazyDecrypt) {
                    break label41;
                }

                bl2 = HiddenOptionFlags.STRING_ENCRYPT_INTERN;
            } else {
                bl2 = HiddenOptionFlags.STRING_ENCRYPT_INTERN;
            }

            if (bl2) {
                list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
            }
        }

        if (bl) {
            list1.add(SimpleInstruction.forOpcode(176));
        } else {
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(SimpleInstruction.forOpcode(87));
        }
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
        list1.add(new ConstantRefInstruction(179, this.cacheMapFieldRef));
    }

    public static boolean canUseIndyStrings(ProgramClass programClass1) {
        return HiddenOptionFlags.STRING_ENCRYPT_DES && HiddenOptionFlags.STRING_ENCRYPT_INDY && ReferenceObfuscator.supportsMethodHandles(programClass1);
    }

    public void emitStringArrayInit(
            LocalVariableList localVariableList1,
            List list1,
            LocalVariableAllocator localVariableAllocator,
            Set set1,
            List list2,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            int ba,
            ObservableHolder[] observableHolders,
            SetMultiMap setMultiMap,
            Map map1,
            JsrGotoKind jsrGotoKind,
            LabelInstruction labelInstruction,
            MutableInt mutableInt,
            ListMultimap listMultimap,
            boolean bl,
            Long long1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        int bb = observableHolders.length;
        HashMap hashMap = ZkmUtils.createHashMap();
        Map map2 = this.buildEncryptedChunks(observableHolders, setMultiMap, map1, set1, bl, long1, hashMap, list2, this.targetClass.getClassConstantPool());
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        int andIncrement = localVariableAllocator.getAndIncrement();
        int bd = localVariableAllocator.getAndIncrement();
        int be = localVariableAllocator.getAndIncrement();
        int bf = localVariableAllocator.getAndIncrement();
        int bg = localVariableAllocator.getAndIncrement();
        int bh = ba == -1 ? localVariableAllocator.getAndIncrement() : ba;
        Instruction.appendIntConstant(bb, list1, constantPool1, list2);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new ConstantRefInstruction(189, resolvedClassConstant));
        list1.add(Instruction.createObjectStore(bh, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bf, localVariableList1, 1));
        Iterator iterator = map2.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
            LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) entry.getKey();
            list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createObjectStore(be, localVariableList1, 1));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "length", "()I", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
            list1.add(Instruction.createIntStore(bg, localVariableList1, 1));
            Instruction.appendIntConstant((Integer) entry.getValue(), list1, constantPool1, list2);
            list1.add(Instruction.createIntStore(bd, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(2));
            list1.add(Instruction.createIntStore(andIncrement, localVariableList1, 1));
            list1.add(labelInstruction1);
            if (bl) {
                list1.add(Instruction.createIntPush((Integer) hashMap.get(resolvedStringConstant)));
            }

            list1.add(Instruction.createIntIncrement(andIncrement, 1, localVariableList1, 1));
            list1.add(Instruction.createObjectLoad(be, localVariableList1, 1));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntLoad(bd, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(96));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "substring", "(II)Ljava/lang/String;", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
            if (jsrGotoKind == JsrGotoKind.JSR) {
                list1.add(new JsrInstruction(labelInstruction));
            } else if (jsrGotoKind == JsrGotoKind.GOTO) {
                int bi = mutableInt.incrementAndGet();
                list1.add(Instruction.createIntPush(bi));
                list1.add(new GotoInstruction(labelInstruction));
                LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
                list1.add(labelInstruction3);
                listMultimap.addValue(labelInstruction, new RankedValue(bi, labelInstruction3));
            }

            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(Instruction.createIntLoad(bf, localVariableList1, 1));
            list1.add(Instruction.createIntIncrement(bf, 1, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(SimpleInstruction.forOpcode(83));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 1));
            list1.add(Instruction.createIntLoad(bd, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(96));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntStore(andIncrement, localVariableList1, 1));
            list1.add(Instruction.createIntLoad(bg, localVariableList1, 1));
            list1.add(new BranchInstruction(162, labelInstruction2));
            list1.add(Instruction.createObjectLoad(be, localVariableList1, 1));
            list1.add(Instruction.createIntLoad(andIncrement, localVariableList1, 1));
            ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                    "java/lang/String", "charAt", "(I)C", list2, classMemberLookup1, classResolver1
            );
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
            list1.add(Instruction.createIntStore(bd, localVariableList1, 1));
            list1.add(new GotoInstruction(labelInstruction1));
            list1.add(labelInstruction2);
        }

        if (resolvedFieldRef != null) {
            list1.add(Instruction.createObjectLoad(bh, localVariableList1, 1));
            list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
            if (this.lazyDecrypt) {
                Instruction.appendIntConstant(bb, list1, constantPool1, list2);
                list1.add(new ConstantRefInstruction(189, resolvedClassConstant));
                list1.add(new ConstantRefInstruction(179, resolvedFieldRef1));
            }
        }
    }

    public void buildDesLookupMethodBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            ResolvedFieldRef resolvedFieldRef2,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        arrayList.add(Instruction.createIntLoad(0, localVariableList1, 1));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createLongConstantLoad(32767L, constantPool1, list1));
        arrayList.add(SimpleInstruction.forOpcode(127));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntPush(this.indexXorKey));
        arrayList.add(SimpleInstruction.forOpcode(130));
        arrayList.add(Instruction.createIntStore(5, localVariableList1, 1));
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new BranchInstruction(199, labelInstruction3));
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction6 = new LabelInstruction(true, 128);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list1);
        exceptionHandlerSpecs[0] = new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction4, labelInstruction5, labelInstruction6);
        arrayList.add(labelInstruction4);
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", list1, classMemberLookup1, classResolver1
        );
        if (this.targetClass.supportsJava5()) {
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            if (this.targetClass.supportsJava19()) {
                ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                        "java/lang/Thread", "threadId", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
            } else {
                ResolvedMethodRefConstant resolvedMethodRefConstant13 = constantPool1.getOrAddMethodRef(
                        "java/lang/Thread", "getId", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant13));
            }

            ResolvedMethodRefConstant resolvedMethodRefConstant14 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant14));
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
        }

        arrayList.add(Instruction.createObjectStore(3, localVariableList1, 1));
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef1));
        ResolvedClassConstant resolvedClassConstant7 = constantPool1.getOrCreateClassConstant("[Ljava/lang/Object;", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant7));
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(new BranchInstruction(199, labelInstruction));
        arrayList.add(SimpleInstruction.forOpcode(6));
        ResolvedClassConstant resolvedClassConstant8 = constantPool1.getOrCreateClassConstant("java/lang/Object", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant8));
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("DES/CBC/PKCS5Padding", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(4));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("DES", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant5));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
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
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        arrayList.add(SimpleInstruction.forOpcode(87));
        arrayList.add(labelInstruction5);
        arrayList.add(new GotoInstruction(labelInstruction));
        arrayList.add(labelInstruction6);
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 1));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant2));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createStringConstantLoad(constantPool1.getClassName(), constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant7));
        arrayList.add(SimpleInstruction.forOpcode(191));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new NewArrayInstruction(8));
        arrayList.add(Instruction.createObjectStore(6, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(6, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createIntStore(7, localVariableList1, 1));
        arrayList.add(labelInstruction1);
        arrayList.add(Instruction.createIntLoad(7, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new BranchInstruction(162, labelInstruction2));
        arrayList.add(Instruction.createObjectLoad(6, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(7, localVariableList1, 1));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(7, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(104));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createIntIncrement(7, 1, localVariableList1, 1));
        arrayList.add(new GotoInstruction(labelInstruction1));
        arrayList.add(labelInstruction2);
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("javax/crypto/spec/DESKeySpec", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant3));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectLoad(6, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "javax/crypto/spec/DESKeySpec", "<init>", "([B)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant8));
        arrayList.add(Instruction.createObjectStore(7, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("javax/crypto/SecretKeyFactory", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant4));
        arrayList.add(Instruction.createObjectLoad(7, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "javax/crypto/SecretKeyFactory", "generateSecret", "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        arrayList.add(Instruction.createObjectStore(8, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant5 = constantPool1.getOrCreateClassConstant("javax/crypto/Cipher", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant5));
        arrayList.add(Instruction.createIntPush(2));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "init", "(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef2));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedStringConstant resolvedStringConstant2 = constantPool1.addStringConstant("ISO-8859-1", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant2));
        ResolvedMethodRefConstant resolvedMethodRefConstant11 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "getBytes", "(Ljava/lang/String;)[B", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant11));
        arrayList.add(Instruction.createObjectStore(9, localVariableList1, 1));
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant5));
        arrayList.add(Instruction.createObjectLoad(9, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant12 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "doFinal", "([B)[B", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant12));
        arrayList.add(new ConstantRefInstruction(184, this.bytesToStringMethodRef));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(labelInstruction3);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(50));
        arrayList.add(SimpleInstruction.forOpcode(176));
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
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Integer", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Integer", "intValue", "()I", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        arrayList.add(Instruction.createIntStore(4, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "longValue", "()J", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        arrayList.add(Instruction.createLongStore(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(Instruction.createLongLoad(5, localVariableList1, 1));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef));
        arrayList.add(Instruction.createObjectStore(7, localVariableList1, 1));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/String", list1);
        arrayList.add(new ConstantRefInstruction(19, resolvedClassConstant2));
        arrayList.add(Instruction.createObjectLoad(7, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "constant",
                "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classMemberLookup1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
        arrayList.add(Instruction.createObjectStore(8, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(5));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/Class", list1);
        arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant3));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(3));
        Integer integer = this.targetClass.hasReleaseVersion() ? this.targetClass.getReleaseVersion() : null;
        AbstractFieldInfo abstractFieldInfo = classResolver1.getVersionedClass("java/lang/Integer", integer).findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef("java/lang/Integer", "TYPE", "Ljava/lang/Class;", list1, abstractFieldInfo);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(SimpleInstruction.forOpcode(4));
        AbstractFieldInfo abstractFieldInfo1 = classResolver1.getVersionedClass("java/lang/Long", integer).findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef("java/lang/Long", "TYPE", "Ljava/lang/Class;", list1, abstractFieldInfo1);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList.add(SimpleInstruction.forOpcode(83));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "dropArguments",
                "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classMemberLookup1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "setTarget", "(Ljava/lang/invoke/MethodHandle;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        arrayList.add(Instruction.createObjectLoad(7, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(176));
    }

    public String desEncrypt(String string, long ba) throws ZkmProcessingException {
        byte[] bb = longToBytes(ba);

        try {
            DESKeySpec dESKeySpec = new DESKeySpec(bb);
            if (this.desKeyFactory == null) {
                this.desKeyFactory = SecretKeyFactory.getInstance("DES");
            }

            if (this.desCipher == null) {
                this.desCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                this.desIv = new IvParameterSpec(new byte[8]);
            }

            SecretKey secretKey = this.desKeyFactory.generateSecret(dESKeySpec);
            IvParameterSpec ivParameterSpec1 = this.desIv;
            SecretKey secretKey1 = secretKey;
            this.desCipher.init(1, secretKey1, ivParameterSpec1);
            byte[] bc = ZkmUtils.toModifiedUtf8(string);
            byte[] bd = this.desCipher.doFinal(bc);
            return new String(bd, "ISO-8859-1");
        } catch (Exception exception) {
            throw new ZkmProcessingException(exception.toString(), exception);
        }
    }

    public void resetForClass(ProgramClass programClass1) {
        this.targetClass = programClass1;
        this.xorKeys = null;
        this.indexXorKey = 0;
        this.byteShuffleTable = null;
        this.stringIndyEntry = null;
        this.toCharArrayHelperRef = null;
        this.decryptHelperRef = null;
        this.toCharArrayRef = null;
        this.stringConstructorRef = null;
        this.internMethodRef = null;
        this.bytesToStringMethodRef = null;
        this.lookupMethodRef = null;
        this.stringArrayFieldRef = null;
        this.cacheMapFieldRef = null;
    }

    public ResolvedFieldRef getStringArrayFieldRef() {
        return this.stringArrayFieldRef;
    }

    public void emitEncryptedStringLoad(
            LocalVariableList localVariableList1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedStringConstant resolvedStringConstant,
            EncryptedStringLocation encryptedStringLocation,
            JsrGotoKind jsrGotoKind,
            LabelInstruction labelInstruction,
            MutableInt mutableInt,
            ListMultimap listMultimap,
            boolean bl,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        if (bl) {
            int ba = this.nextRandomKey(this.random, MAX_CHUNK_KEY_VALUE);
            observableHolder.setValue(ba);
            list1.add(Instruction.createIntPush(ba));
        }

        if (resolvedStringConstant.getIndex() > 255) {
            list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        } else {
            list1.add(new LdcInstruction(resolvedStringConstant));
        }

        if (jsrGotoKind == JsrGotoKind.JSR) {
            list1.add(new JsrInstruction(labelInstruction));
        } else if (jsrGotoKind == JsrGotoKind.GOTO) {
            int bb = mutableInt.incrementAndGet();
            list1.add(Instruction.createIntPush(bb));
            list1.add(new GotoInstruction(labelInstruction));
            LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
            list1.add(labelInstruction1);
            listMultimap.addValue(labelInstruction, new RankedValue(bb, labelInstruction1));
        } else {
            list1.add(new ConstantRefInstruction(184, this.toCharArrayHelperRef));
            list1.add(new ConstantRefInstruction(184, this.decryptHelperRef));
        }

        if (encryptedStringLocation.hasArrayField()) {
            list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
        } else if (encryptedStringLocation.hasArrayLocal()) {
            list1.add(Instruction.createObjectStore(encryptedStringLocation.getArrayLocalIndex(), localVariableList1, 1));
        }
    }

    public void buildLookupMethodBody(
            LocalVariableList localVariableList1,
            List list1,
            boolean bl,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            Integer integer,
            int[] ba,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        byte bb;
        if (bl) {
            bb = 2;
        } else {
            bb = 1;
        }

        int bc = bb + 1;
        int bd = bc + 1;
        int be = bd + 1;
        int bf = be + 1;
        int bg = bf + 1;
        int bh = bg + 1;
        int bi = bh + 1;
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction6 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction7 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction8 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction9 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction10 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction11 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction12 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction13 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction14 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction15 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction16 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction17 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction18 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction19 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction20 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction21 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction22 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction23 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction24 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction25 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction26 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction27 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction28 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction29 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction30 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction31 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction32 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction33 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction34 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction35 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction36 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction37 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction38 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction39 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction40 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction41 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction42 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction43 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction44 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction45 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction46 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction47 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction48 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction49 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction50 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction51 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction52 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction53 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction54 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction55 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction56 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction57 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction58 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction59 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction60 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction61 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction62 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction63 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction64 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction65 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction66 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction67 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction68 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction69 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction70 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction71 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction72 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction73 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction74 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction75 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction76 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction77 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction78 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction79 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction80 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction81 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction82 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction83 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction84 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction85 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction86 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction87 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction88 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction89 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction90 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction91 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction92 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction93 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction94 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction95 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction96 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction97 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction98 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction99 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction100 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction101 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction102 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction103 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction104 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction105 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction106 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction107 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction108 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction109 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction110 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction111 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction112 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction113 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction114 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction115 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction116 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction117 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction118 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction119 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction120 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction121 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction122 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction123 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction124 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction125 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction126 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction127 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction128 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction129 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction130 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction131 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction132 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction133 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction134 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction135 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction136 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction137 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction138 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction139 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction140 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction141 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction142 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction143 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction144 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction145 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction146 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction147 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction148 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction149 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction150 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction151 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction152 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction153 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction154 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction155 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction156 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction157 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction158 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction159 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction160 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction161 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction162 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction163 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction164 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction165 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction166 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction167 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction168 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction169 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction170 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction171 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction172 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction173 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction174 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction175 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction176 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction177 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction178 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction179 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction180 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction181 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction182 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction183 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction184 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction185 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction186 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction187 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction188 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction189 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction190 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction191 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction192 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction193 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction194 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction195 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction196 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction197 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction198 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction199 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction200 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction201 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction202 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction203 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction204 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction205 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction206 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction207 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction208 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction209 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction210 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction211 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction212 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction213 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction214 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction215 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction216 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction217 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction218 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction219 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction220 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction221 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction222 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction223 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction224 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction225 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction226 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction227 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction228 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction229 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction230 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction231 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction232 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction233 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction234 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction235 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction236 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction237 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction238 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction239 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction240 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction241 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction242 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction243 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction244 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction245 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction246 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction247 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction248 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction249 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction250 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction251 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction252 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction253 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction254 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction255 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction256 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction257 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction258 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction259 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction260 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction261 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction262 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction263 = new LabelInstruction(true, 1);
        list1.add(Instruction.createIntLoad(0, localVariableList1, 1));
        if (bl) {
            list1.add(Instruction.createIntLoad(bb, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(130));
        }

        list1.add(Instruction.createIntPush(integer));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(Instruction.createIntConstantPush(65535, constantPool1, list2));
        list1.add(SimpleInstruction.forOpcode(126));
        list1.add(Instruction.createIntStore(bc, localVariableList1, 1));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(new BranchInstruction(199, labelInstruction263));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(50));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/String", "toCharArray", "()[C", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 1));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(Instruction.createIntPush(255));
        list1.add(SimpleInstruction.forOpcode(126));
        LabelInstruction[] labelInstructions = new LabelInstruction[]{
                labelInstruction,
                labelInstruction1,
                labelInstruction2,
                labelInstruction3,
                labelInstruction4,
                labelInstruction5,
                labelInstruction6,
                labelInstruction7,
                labelInstruction8,
                labelInstruction9,
                labelInstruction10,
                labelInstruction11,
                labelInstruction12,
                labelInstruction13,
                labelInstruction14,
                labelInstruction15,
                labelInstruction16,
                labelInstruction17,
                labelInstruction18,
                labelInstruction19,
                labelInstruction20,
                labelInstruction21,
                labelInstruction22,
                labelInstruction23,
                labelInstruction24,
                labelInstruction25,
                labelInstruction26,
                labelInstruction27,
                labelInstruction28,
                labelInstruction29,
                labelInstruction30,
                labelInstruction31,
                labelInstruction32,
                labelInstruction33,
                labelInstruction34,
                labelInstruction35,
                labelInstruction36,
                labelInstruction37,
                labelInstruction38,
                labelInstruction39,
                labelInstruction40,
                labelInstruction41,
                labelInstruction42,
                labelInstruction43,
                labelInstruction44,
                labelInstruction45,
                labelInstruction46,
                labelInstruction47,
                labelInstruction48,
                labelInstruction49,
                labelInstruction50,
                labelInstruction51,
                labelInstruction52,
                labelInstruction53,
                labelInstruction54,
                labelInstruction55,
                labelInstruction56,
                labelInstruction57,
                labelInstruction58,
                labelInstruction59,
                labelInstruction60,
                labelInstruction61,
                labelInstruction62,
                labelInstruction63,
                labelInstruction64,
                labelInstruction65,
                labelInstruction66,
                labelInstruction67,
                labelInstruction68,
                labelInstruction69,
                labelInstruction70,
                labelInstruction71,
                labelInstruction72,
                labelInstruction73,
                labelInstruction74,
                labelInstruction75,
                labelInstruction76,
                labelInstruction77,
                labelInstruction78,
                labelInstruction79,
                labelInstruction80,
                labelInstruction81,
                labelInstruction82,
                labelInstruction83,
                labelInstruction84,
                labelInstruction85,
                labelInstruction86,
                labelInstruction87,
                labelInstruction88,
                labelInstruction89,
                labelInstruction90,
                labelInstruction91,
                labelInstruction92,
                labelInstruction93,
                labelInstruction94,
                labelInstruction95,
                labelInstruction96,
                labelInstruction97,
                labelInstruction98,
                labelInstruction99,
                labelInstruction100,
                labelInstruction101,
                labelInstruction102,
                labelInstruction103,
                labelInstruction104,
                labelInstruction105,
                labelInstruction106,
                labelInstruction107,
                labelInstruction108,
                labelInstruction109,
                labelInstruction110,
                labelInstruction111,
                labelInstruction112,
                labelInstruction113,
                labelInstruction114,
                labelInstruction115,
                labelInstruction116,
                labelInstruction117,
                labelInstruction118,
                labelInstruction119,
                labelInstruction120,
                labelInstruction121,
                labelInstruction122,
                labelInstruction123,
                labelInstruction124,
                labelInstruction125,
                labelInstruction126,
                labelInstruction127,
                labelInstruction128,
                labelInstruction129,
                labelInstruction130,
                labelInstruction131,
                labelInstruction132,
                labelInstruction133,
                labelInstruction134,
                labelInstruction135,
                labelInstruction136,
                labelInstruction137,
                labelInstruction138,
                labelInstruction139,
                labelInstruction140,
                labelInstruction141,
                labelInstruction142,
                labelInstruction143,
                labelInstruction144,
                labelInstruction145,
                labelInstruction146,
                labelInstruction147,
                labelInstruction148,
                labelInstruction149,
                labelInstruction150,
                labelInstruction151,
                labelInstruction152,
                labelInstruction153,
                labelInstruction154,
                labelInstruction155,
                labelInstruction156,
                labelInstruction157,
                labelInstruction158,
                labelInstruction159,
                labelInstruction160,
                labelInstruction161,
                labelInstruction162,
                labelInstruction163,
                labelInstruction164,
                labelInstruction165,
                labelInstruction166,
                labelInstruction167,
                labelInstruction168,
                labelInstruction169,
                labelInstruction170,
                labelInstruction171,
                labelInstruction172,
                labelInstruction173,
                labelInstruction174,
                labelInstruction175,
                labelInstruction176,
                labelInstruction177,
                labelInstruction178,
                labelInstruction179,
                labelInstruction180,
                labelInstruction181,
                labelInstruction182,
                labelInstruction183,
                labelInstruction184,
                labelInstruction185,
                labelInstruction186,
                labelInstruction187,
                labelInstruction188,
                labelInstruction189,
                labelInstruction190,
                labelInstruction191,
                labelInstruction192,
                labelInstruction193,
                labelInstruction194,
                labelInstruction195,
                labelInstruction196,
                labelInstruction197,
                labelInstruction198,
                labelInstruction199,
                labelInstruction200,
                labelInstruction201,
                labelInstruction202,
                labelInstruction203,
                labelInstruction204,
                labelInstruction205,
                labelInstruction206,
                labelInstruction207,
                labelInstruction208,
                labelInstruction209,
                labelInstruction210,
                labelInstruction211,
                labelInstruction212,
                labelInstruction213,
                labelInstruction214,
                labelInstruction215,
                labelInstruction216,
                labelInstruction217,
                labelInstruction218,
                labelInstruction219,
                labelInstruction220,
                labelInstruction221,
                labelInstruction222,
                labelInstruction223,
                labelInstruction224,
                labelInstruction225,
                labelInstruction226,
                labelInstruction227,
                labelInstruction228,
                labelInstruction229,
                labelInstruction230,
                labelInstruction231,
                labelInstruction232,
                labelInstruction233,
                labelInstruction234,
                labelInstruction235,
                labelInstruction236,
                labelInstruction237,
                labelInstruction238,
                labelInstruction239,
                labelInstruction240,
                labelInstruction241,
                labelInstruction242,
                labelInstruction243,
                labelInstruction244,
                labelInstruction245,
                labelInstruction246,
                labelInstruction247,
                labelInstruction248,
                labelInstruction249,
                labelInstruction250,
                labelInstruction251,
                labelInstruction252,
                labelInstruction253,
                labelInstruction254
        };
        list1.add(new TableSwitchInstruction(labelInstruction255, labelInstructions.length - 1, labelInstructions));

        for (int i = 0; i < labelInstructions.length; i += 1) {
            list1.add(labelInstructions[i]);
            list1.add(Instruction.createIntPush(ba[i]));
            list1.add(new GotoInstruction(labelInstruction256));
        }

        list1.add(labelInstruction255);
        list1.add(Instruction.createIntPush(ba[255]));
        list1.add(labelInstruction256);
        list1.add(Instruction.createIntStore(be, localVariableList1, 1));
        list1.add(Instruction.createIntLoad(1, localVariableList1, 1));
        List list3;
        short bp;
        if (bl) {
            list1.add(Instruction.createIntLoad(bb, localVariableList1, 1));
            list1.add(SimpleInstruction.forOpcode(130));
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntStore(1, localVariableList1, 1));
            list3 = list1;
            long bo = 24085842137881L;
            bp = 255;
        } else {
            list3 = list1;
            long bm = 24085842137881L;
            bp = 255;
        }

        short bk = bp;
        list3.add(Instruction.createIntPush(bk));
        list1.add(SimpleInstruction.forOpcode(126));
        list1.add(Instruction.createIntLoad(be, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(100));
        list1.add(Instruction.createIntStore(bf, localVariableList1, 1));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 1));
        list1.add(new BranchInstruction(156, labelInstruction257));
        list1.add(Instruction.createIntIncrement(bf, 256, localVariableList1, 1));
        list1.add(labelInstruction257);
        list1.add(Instruction.createIntLoad(1, localVariableList1, 1));
        list1.add(Instruction.createIntConstantPush(65535, constantPool1, list2));
        list1.add(SimpleInstruction.forOpcode(126));
        list1.add(Instruction.createIntPush(8));
        list1.add(SimpleInstruction.forOpcode(124));
        list1.add(Instruction.createIntLoad(be, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(100));
        list1.add(Instruction.createIntStore(bg, localVariableList1, 1));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 1));
        list1.add(new BranchInstruction(156, labelInstruction258));
        list1.add(Instruction.createIntIncrement(bg, 256, localVariableList1, 1));
        list1.add(labelInstruction258);
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bh, localVariableList1, 1));
        list1.add(new GotoInstruction(labelInstruction262));
        list1.add(labelInstruction259);
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(5));
        list1.add(SimpleInstruction.forOpcode(112));
        list1.add(Instruction.createIntStore(bi, localVariableList1, 1));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 1));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(92));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(Instruction.createIntLoad(bi, localVariableList1, 1));
        list1.add(new BranchInstruction(154, labelInstruction260));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(146));
        list1.add(SimpleInstruction.forOpcode(85));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 1));
        list1.add(Instruction.createIntPush(3));
        list1.add(SimpleInstruction.forOpcode(124));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 1));
        list1.add(Instruction.createIntPush(5));
        list1.add(SimpleInstruction.forOpcode(120));
        list1.add(SimpleInstruction.forOpcode(128));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 1));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(Instruction.createIntPush(255));
        list1.add(SimpleInstruction.forOpcode(126));
        list1.add(Instruction.createIntStore(bf, localVariableList1, 1));
        list1.add(new GotoInstruction(labelInstruction261));
        list1.add(labelInstruction260);
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(146));
        list1.add(SimpleInstruction.forOpcode(85));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 1));
        list1.add(Instruction.createIntPush(3));
        list1.add(SimpleInstruction.forOpcode(124));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 1));
        list1.add(Instruction.createIntPush(5));
        list1.add(SimpleInstruction.forOpcode(120));
        list1.add(SimpleInstruction.forOpcode(128));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 1));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(Instruction.createIntPush(255));
        list1.add(SimpleInstruction.forOpcode(126));
        list1.add(Instruction.createIntStore(bg, localVariableList1, 1));
        list1.add(labelInstruction261);
        list1.add(Instruction.createIntIncrement(bh, 1, localVariableList1, 1));
        list1.add(labelInstruction262);
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 1));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(new BranchInstruction(161, labelInstruction259));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 1));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new TypeInstruction(resolvedClassConstant));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "<init>", "([C)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        byte bn;
        if (this.internMethodRef != null) {
            if (HiddenOptionFlags.STRING_ENCRYPT_INTERN) {
                list1.add(new ConstantRefInstruction(182, this.internMethodRef));
                list3 = list1;
                bn = 83;
            } else {
                list3 = list1;
                bn = 83;
            }
        } else {
            list3 = list1;
            bn = 83;
        }

        list3.add(SimpleInstruction.forOpcode(bn));
        list1.add(labelInstruction263);
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public static String xorWithKeys(String string, int[] ba) {
        int bb = ba.length;
        char[] bc = string.toCharArray();
        int bd = bc.length;

        for (int i = 0; i < bd; i++) {
            int bf = i % bb;
            bc[i] = (char) (bc[i] ^ ba[bf]);
        }

        return new String(bc);
    }

    public int[] deriveXorKeys(int ba) {
        int[] bb = new int[this.xorKeys.length];
        int bc = 0;
        int bd = 0;

        for (int[] xorKeys = this.xorKeys; bd < xorKeys.length; xorKeys = this.xorKeys) {
            bb[bc] = this.xorKeys[bc] ^ ba;
            bd = ++bc;
        }

        return bb;
    }

    public static int[] randomKeys(Random random1, int ba, int bb) {
        int[] bc = new int[ba];

        for (int i = 0; i < ba; i++) {
            int be = random1.nextInt() % bb;
            if (be < 0) {
                be += bb;
            }

            bc[i] = ++be;
        }

        return bc;
    }

    public void emitDecryptSubroutine(
            LocalVariableList localVariableList1,
            LabelInstruction labelInstruction,
            LocalVariableAllocator localVariableAllocator,
            List list1,
            List list2,
            JsrGotoKind jsrGotoKind,
            ListMultimap listMultimap,
            Integer integer,
            boolean bl,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        arrayList.add(new GotoInstruction(labelInstruction1));
        arrayList.add(labelInstruction);
        if (jsrGotoKind == JsrGotoKind.JSR) {
            int andIncrement = localVariableAllocator.getAndIncrement();
            arrayList.add(Instruction.createObjectStore(andIncrement, localVariableList1, 1));
            this.emitDecryptCall(localVariableList1, arrayList, list2, localVariableAllocator, integer, bl, classMemberLookup1, classResolver1);
            arrayList.add(Instruction.createRet(andIncrement, localVariableList1));
        } else if (jsrGotoKind == JsrGotoKind.GOTO) {
            List list3 = listMultimap.getValues(labelInstruction);
            if (list3.size() > 1) {
                Collections.sort(list3);
                LabelInstruction labelInstruction2 = (LabelInstruction) ((RankedValue) list3.get(0)).getValue();
                LabelInstruction[] labelInstructions = new LabelInstruction[list3.size() - 1];

                for (int i = 1; i < list3.size(); i++) {
                    labelInstructions[i - 1] = (LabelInstruction) ((RankedValue) list3.get(i)).getValue();
                }

                if (bl) {
                    arrayList.add(SimpleInstruction.forOpcode(91));
                    arrayList.add(SimpleInstruction.forOpcode(87));
                } else {
                    arrayList.add(SimpleInstruction.forOpcode(95));
                }

                this.emitDecryptCall(localVariableList1, arrayList, list2, localVariableAllocator, integer, bl, classMemberLookup1, classResolver1);
                arrayList.add(SimpleInstruction.forOpcode(95));
                arrayList.add(new TableSwitchInstruction(labelInstruction2, labelInstructions.length - 1, labelInstructions));
            } else {
                LabelInstruction labelInstruction3 = (LabelInstruction) ((RankedValue) list3.get(0)).getValue();
                if (bl) {
                    arrayList.add(SimpleInstruction.forOpcode(91));
                    arrayList.add(SimpleInstruction.forOpcode(87));
                } else {
                    arrayList.add(SimpleInstruction.forOpcode(95));
                }

                this.emitDecryptCall(localVariableList1, arrayList, list2, localVariableAllocator, integer, bl, classMemberLookup1, classResolver1);
                arrayList.add(SimpleInstruction.forOpcode(95));
                arrayList.add(SimpleInstruction.forOpcode(87));
                arrayList.add(new GotoInstruction(labelInstruction3));
            }
        }

        arrayList.add(labelInstruction1);
        list1.addAll(arrayList);
    }

    public void buildToCharArrayHelperBody(LocalVariableList localVariableList1, List list1, ResolvedMethodRefConstant resolvedMethodRefConstant, boolean bl) {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(SimpleInstruction.forOpcode(89));
        if (bl) {
            list1.add(labelInstruction1);
        }

        list1.add(SimpleInstruction.forOpcode(190));
        if (bl) {
            list1.add(SimpleInstruction.forOpcode(90));
        }

        list1.add(Instruction.createIntPush(2));
        list1.add(new BranchInstruction(162, labelInstruction));
        if (bl) {
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(new BranchInstruction(153, labelInstruction1));
        } else {
            list1.add(SimpleInstruction.forOpcode(89));
        }

        list1.add(SimpleInstruction.forOpcode(3));
        List list2;
        byte ba;
        if (bl) {
            list1.add(SimpleInstruction.forOpcode(91));
            list2 = list1;
            ba = 92;
        } else {
            list2 = list1;
            ba = 92;
        }

        list2.add(SimpleInstruction.forOpcode(ba));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(Instruction.createIntPush(this.xorKeys[6]));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(146));
        list1.add(SimpleInstruction.forOpcode(85));
        list1.add(labelInstruction);
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public void emitDesDecryptCall(
            LocalVariableList localVariableList1,
            List list1,
            List list2,
            int ba,
            int bb,
            ResolvedMethodRefConstant resolvedMethodRefConstant,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("ISO-8859-1", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "getBytes", "(Ljava/lang/String;)[B", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(Instruction.createObjectLoad(ba, localVariableList1, 1));
        list1.add(SimpleInstruction.forOpcode(95));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "doFinal", "([B)[B", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        list1.add(Instruction.createObjectStore(bb, localVariableList1, 1));
        list1.add(Instruction.createObjectLoad(bb, localVariableList1, 1));
        list1.add(new ConstantRefInstruction(184, this.bytesToStringMethodRef));
        if (resolvedMethodRefConstant != null && HiddenOptionFlags.STRING_ENCRYPT_INTERN) {
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        }
    }

    public Map buildEncryptedChunks(
            ObservableHolder[] observableHolders,
            SetMultiMap setMultiMap,
            Map map1,
            Set set1,
            boolean bl,
            Long long1,
            Map map2,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmProcessingException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int ba = observableHolders.length;
        int bb = 0;

        while (bb < ba) {
            StringBuilder stringBuilder = new StringBuilder();
            int bc = -1;
            int bd = 0;
            Integer integer = null;
            int[] be = null;
            if (bl) {
                integer = this.nextRandomKey(this.random, MAX_CHUNK_KEY_VALUE);
                be = this.deriveXorKeys(integer);
            }

            while (bb < ba) {
                ObservableHolder observableHolder = observableHolders[bb];
                EncryptedStringLocation encryptedStringLocation = (EncryptedStringLocation) map1.get(observableHolder);
                Iterator iterator = setMultiMap.getValues(observableHolder).iterator();

                while (iterator.hasNext()) {
                    ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
                    set1.add(resolvedStringConstant);
                    set1.add(resolvedStringConstant.getValueUtf8());
                }

                StringEncryptionTechnique stringEncryptionTechnique = encryptedStringLocation.getTechnique();
                String string1 = (String) observableHolder.getValue();
                short bf = 0;
                if (stringEncryptionTechnique.equals(StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX)) {
                    int[] bg = randomKeys(this.random, 2, 255);
                    string1 = rollingXorEncrypt(string1, bg, true);
                    int bh = string1.charAt(0) & 255;
                    offsetKeys(bh, bg, this.byteShuffleTable);
                    short bi = (short) (bg[0] | bg[1] << 8);
                    encryptedStringLocation.setLookupKey(bi);
                    bf = (short) (bb ^ this.indexXorKey);
                } else {
                    label81:
                    {
                        Iterator iterator1;
                        if (!stringEncryptionTechnique.equals(StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX_DES)) {
                            if (!stringEncryptionTechnique.equals(StringEncryptionTechnique.INDY_ENTRY_AND_INDEX)) {
                                break label81;
                            }

                            iterator1 = this.randomLongKeys;
                        } else {
                            iterator1 = this.randomLongKeys;
                        }

                        long bk = (Long) iterator1.next();
                        encryptedStringLocation.setDecryptKey(bk);
                        bf = (short) (bb ^ this.indexXorKey);
                        string1 = this.desEncrypt(string1, bk);
                    }
                }

                String string2;
                if (long1 != null) {
                    string2 = this.desEncrypt(string1, long1);
                } else if (bl) {
                    string2 = xorWithKeys(string1, be);
                } else {
                    string2 = xorWithKeys(string1, this.xorKeys);
                }

                int bm = string2.length();
                StringBuilder stringBuilder1 = new StringBuilder(bm + 1);
                if (bc == -1) {
                    bc = bm;
                } else {
                    stringBuilder1.append((char) bm);
                }

                stringBuilder1.append(string2);
                int bj = ZkmUtils.getModifiedUtf8Length(stringBuilder1.toString());
                if (bj > 65535) {
                    String string = (String) observableHolder.getValue();
                    ZkmAssert.assertTrue(
                            false,
                            new String[]{
                                    "String too large in class '"
                                            + this.targetClass.getDisplayLocationName()
                                            + "' : "
                                            + string.length()
                                            + " : "
                                            + bj
                                            + " : "
                                            + ZkmUtils.getModifiedUtf8Length(string)
                                            + " : '"
                                            + ZkmUtils.escapeJavaString(string.substring(0, 50))
                                            + "'"
                            }
                    );
                }

                if (bd > 0 && bd + bj > 32768) {
                    break;
                }

                bd += bj;
                bb++;
                stringBuilder.append(stringBuilder1);
                if (stringEncryptionTechnique.equals(StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX)
                        || stringEncryptionTechnique.equals(StringEncryptionTechnique.LOOKUP_METHOD_AND_INDEX_DES)
                        || stringEncryptionTechnique.equals(StringEncryptionTechnique.INDY_ENTRY_AND_INDEX)) {
                    encryptedStringLocation.setStringIndex(bf);
                }

                if (linkedHashMap.size() == 0 && ba > 3 && bb == ba - 2) {
                    break;
                }
            }

            ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(stringBuilder.toString(), list1);
            linkedHashMap.put(resolvedStringConstant1, bc);
            if (integer != null) {
                map2.put(resolvedStringConstant1, integer);
            }
        }

        return linkedHashMap;
    }

    public void emitStringFieldInit(
            LocalVariableList localVariableList1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            EncryptedStringLocation encryptedStringLocation,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            List list2
    ) {
        StringEncryptionTechnique stringEncryptionTechnique = encryptedStringLocation.getTechnique();
        switch (ArrayLookupKindSwitchMap.TECHNIQUE_SWITCH[stringEncryptionTechnique.ordinal()]) {
            case 1:
            case 2:
                if (encryptedStringLocation.hasStringIndex()) {
                    list1.add(SimpleInstruction.forOpcode(89));
                    list1.add(Instruction.createIntPush(encryptedStringLocation.getStringIndex()));
                    list1.add(SimpleInstruction.forOpcode(50));
                } else {
                    list1.add(SimpleInstruction.forOpcode(89));
                }
                break;
            case 3:
                ResolvedMethodRef resolvedMethodRef = encryptedStringLocation.getLookupMethodRef();
                if (long1 != null && localVariableIndex1 != null) {
                    ConstantPool constantPool3 = this.targetClass.getClassConstantPool();
                    int stringIndex = encryptedStringLocation.getStringIndex();
                    int bc = stringIndex ^ long1.intValue();
                    list1.add(Instruction.createIntConstantPush(bc, constantPool3, list2));
                    int lookupKey = encryptedStringLocation.getLookupKey();
                    int bn = lookupKey ^ long1.intValue();
                    list1.add(Instruction.createIntConstantPush(bn, constantPool3, list2));
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 1));
                    list1.add(SimpleInstruction.forOpcode(136));
                } else {
                    list1.add(Instruction.createIntPush(encryptedStringLocation.getStringIndex()));
                    list1.add(Instruction.createIntPush(encryptedStringLocation.getLookupKey()));
                }

                list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
                break;
            case 4:
                ConstantPool constantPool2 = this.targetClass.getClassConstantPool();
                int bf = encryptedStringLocation.getStringIndex();
                long decryptKey = encryptedStringLocation.getDecryptKey();
                if (long1 != null && localVariableIndex1 != null) {
                    int bk = bf ^ (int) (decryptKey & 32767L);
                    Instruction.appendIntConstant(bk, list1, constantPool2, list2);
                    long bm = decryptKey ^ long1;
                    Instruction.appendLongConstant(bm, list1, constantPool2, list2);
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 1));
                    list1.add(SimpleInstruction.forOpcode(131));
                } else {
                    int bj = bf ^ (int) decryptKey;
                    Instruction.appendIntConstant(bj, list1, constantPool2, list2);
                    Instruction.appendLongConstant(decryptKey, list1, constantPool2, list2);
                }

                ResolvedMethodRef resolvedMethodRef1 = encryptedStringLocation.getLookupMethodRef();
                list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
                break;
            case 5:
                ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
                int ba = encryptedStringLocation.getStringIndex();
                long bb = encryptedStringLocation.getDecryptKey();
                if (long1 != null && localVariableIndex1 != null) {
                    int bi = ba ^ (int) (bb & 32767L);
                    Instruction.appendIntConstant(bi, list1, constantPool1, list2);
                    long be = bb ^ long1;
                    Instruction.appendLongConstant(be, list1, constantPool1, list2);
                    list1.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 1));
                    list1.add(SimpleInstruction.forOpcode(131));
                } else {
                    int bd = ba ^ (int) bb;
                    Instruction.appendIntConstant(bd, list1, constantPool1, list2);
                    Instruction.appendLongConstant(bb, list1, constantPool1, list2);
                }

                list1.add(new InvokeDynamicInstruction(encryptedStringLocation.getIndyEntry()));
        }

        list1.add(new ConstantRefInstruction(179, resolvedFieldRef));
    }

    public ResolvedMethodRef getLookupMethodRef() {
        return this.lookupMethodRef;
    }

    public ResolvedFieldRef getCacheMapFieldRef() {
        return this.cacheMapFieldRef;
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
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "<init>", "(Ljava/lang/invoke/MethodType;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        arrayList.add(Instruction.createObjectStore(3, localVariableList1, 1));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        ResolvedMethodHandleConstant resolvedMethodHandleConstant = constantPool1.getOrAddMethodHandle(
                MethodHandleRefKind.REF_INVOKE_STATIC, resolvedMethodRef, list1
        );
        arrayList.add(new ConstantRefInstruction(19, resolvedMethodHandleConstant));
        arrayList.add(Instruction.createClassConstantLoad(constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 1));
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
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(83));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntPush(2));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 1));
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
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 1));
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
        arrayList.add(Instruction.createObjectStore(4, localVariableList1, 1));
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
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 1));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(" : ", list1, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "toString", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant10));
        arrayList.add(SimpleInstruction.forOpcode(191));
        arrayList.add(labelInstruction3);
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(176));
    }

    public int[] getXorKeys() {
        return this.xorKeys.clone();
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
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("DES/CBC/PKCS5Padding", list2, false);
        arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", list2, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectStore(ba, localVariableList1, 1));
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
        arrayList.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(SimpleInstruction.forOpcode(4));
        arrayList.add(Instruction.createIntStore(andIncrement, localVariableList1, 1));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(new BranchInstruction(162, labelInstruction1));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 1));
        arrayList.add(Instruction.createLongLoad(localVariableIndex1.getIndex(), localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(andIncrement, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(8));
        arrayList.add(SimpleInstruction.forOpcode(104));
        arrayList.add(SimpleInstruction.forOpcode(121));
        arrayList.add(Instruction.createIntPush(56));
        arrayList.add(SimpleInstruction.forOpcode(125));
        arrayList.add(SimpleInstruction.forOpcode(136));
        arrayList.add(SimpleInstruction.forOpcode(145));
        arrayList.add(SimpleInstruction.forOpcode(84));
        arrayList.add(Instruction.createIntIncrement(andIncrement, 1, localVariableList1, 1));
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

    public static byte[] longToBytes(long ba) {
        return new byte[]{
                (byte) (ba >>> 56),
                (byte) (ba << 8 >>> 56),
                (byte) (ba << 16 >>> 56),
                (byte) (ba << 24 >>> 56),
                (byte) (ba << 32 >>> 56),
                (byte) (ba << 40 >>> 56),
                (byte) (ba << 48 >>> 56),
                (byte) (ba << 56 >>> 56)
        };
    }

    public void setUpDecryptionMembers(
            ProgramClass programClass1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            List list1,
            Boolean boolean1,
            boolean bl,
            boolean bl1,
            boolean bl2,
            ResolvedFieldRef resolvedFieldRef,
            boolean bl3,
            boolean bl4,
            boolean bl5
    ) throws ZkmException, IOException {
        this.resetForClass(programClass1);
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        this.internMethodRef = null;
        if (bl) {
            this.internMethodRef = constantPool1.getOrAddMethodRef("java/lang/String", "intern", "()Ljava/lang/String;", list1, classMemberLookup1, classResolver1);
        }

        this.useHelperMethods = boolean1;
        if (bl5) {
            if (this.targetClass.hasStringDecryptMethodRef()) {
                this.bytesToStringMethodRef = this.targetClass.getStringDecryptMethodRef();
            } else {
                LocalVariableList localVariableList1 = new LocalVariableList(true, "([B)Ljava/lang/String;", 7);
                ArrayList arrayList = new ArrayList();
                this.buildBytesToStringBody(localVariableList1, arrayList, list1, constantPool1, classMemberLookup1, classResolver1);
                MethodInfo methodInfo1 = this.targetClass
                        .createUniquelyNamedStaticMethod(
                                "([B)Ljava/lang/String;",
                                arrayList,
                                5,
                                7,
                                1,
                                localVariableList1,
                                new ExceptionHandlerSpec[0],
                                "String Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                1
                        );
                this.bytesToStringMethodRef = this.targetClass.addMethodRef(methodInfo1, list1);
                this.targetClass.setStringDecryptMethodRef(this.bytesToStringMethodRef);
            }

            if (bl1) {
                this.indexXorKey = this.random.nextInt(32766) + 1;
                FieldInfo fieldInfo = this.targetClass
                        .createUniquelyNamedStaticField(
                                "[Ljava/lang/String;", this.targetClass.isInterface() ? 4 : 1, true, inheritedMemberAnalyzer, classMemberLookup1, 1
                        );
                this.stringArrayFieldRef = constantPool1.getOrAddFieldRef(
                        this.targetClass.getClassName(), fieldInfo.getSourceName(), fieldInfo.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                FieldInfo fieldInfo2 = this.targetClass
                        .createUniquelyNamedStaticField("Ljava/util/Map;", this.targetClass.isInterface() ? 4 : 1, true, inheritedMemberAnalyzer, classMemberLookup1, 1);
                this.cacheMapFieldRef = constantPool1.getOrAddFieldRef(
                        this.targetClass.getClassName(), fieldInfo2.getSourceName(), fieldInfo2.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                ExceptionHandlerSpec[] exceptionHandlerSpecs2 = new ExceptionHandlerSpec[1];
                LocalVariableList localVariableList2 = new LocalVariableList(true, "(IJ)Ljava/lang/String;", 10);
                ArrayList arrayList1 = new ArrayList();
                this.buildDesLookupMethodBody(
                        localVariableList2,
                        arrayList1,
                        this.cacheMapFieldRef,
                        this.stringArrayFieldRef,
                        resolvedFieldRef,
                        exceptionHandlerSpecs2,
                        list1,
                        constantPool1,
                        classMemberLookup1,
                        classResolver1
                );
                MethodInfo methodInfo2 = this.targetClass
                        .createUniquelyNamedStaticMethod(
                                "(IJ)Ljava/lang/String;",
                                arrayList1,
                                6,
                                10,
                                1,
                                localVariableList2,
                                exceptionHandlerSpecs2,
                                "String Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                1
                        );
                this.lookupMethodRef = this.targetClass.addMethodRef(methodInfo2, list1);
                if (bl4) {
                    LocalVariableList localVariableList3 = new LocalVariableList(
                            true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", 9
                    );
                    ArrayList arrayList2 = new ArrayList();
                    this.buildCallSiteTargetBody(localVariableList3, arrayList2, this.lookupMethodRef, list1, constantPool1, classMemberLookup1, classResolver1);
                    MethodInfo methodInfo3 = this.targetClass
                            .createUniquelyNamedStaticMethod(
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
                                    arrayList2,
                                    7,
                                    9,
                                    1,
                                    localVariableList3,
                                    new ExceptionHandlerSpec[0],
                                    "String Encryption",
                                    list1,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1,
                                    1
                            );
                    ResolvedMethodRef resolvedMethodRef = this.targetClass.addMethodRef(methodInfo3, list1);
                    ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[1];
                    LocalVariableList localVariableList4 = new LocalVariableList(
                            true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", 5
                    );
                    ArrayList arrayList3 = new ArrayList();
                    ClassMemberLookup classMemberLookup2 = classMemberLookup1;
                    ConstantPool constantPool2 = constantPool1;
                    List list2 = list1;
                    ExceptionHandlerSpec[] exceptionHandlerSpecs1 = exceptionHandlerSpecs;
                    this.buildBootstrapMethodBody(
                            localVariableList4, arrayList3, resolvedMethodRef, exceptionHandlerSpecs1, list2, constantPool2, classMemberLookup2, classResolver1
                    );
                    MethodInfo methodInfo4 = this.targetClass
                            .createUniquelyNamedStaticMethod(
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                    arrayList3,
                                    7,
                                    5,
                                    1,
                                    localVariableList4,
                                    exceptionHandlerSpecs,
                                    "String Encryption",
                                    list1,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1,
                                    1
                            );
                    ResolvedMethodRef resolvedMethodRef1 = this.targetClass.addMethodRef(methodInfo4, list1);
                    String string = String.valueOf((char) (97 + this.random.nextInt(26)));
                    this.stringIndyEntry = this.targetClass.createInvokeDynamic(string, "(IJ)Ljava/lang/String;", resolvedMethodRef1, list1, constantPool1);
                }
            }
        } else {
            this.xorKeys = randomKeys(this.random, 7, MAX_XOR_KEY_VALUE);
            this.toCharArrayRef = constantPool1.getOrAddMethodRef("java/lang/String", "toCharArray", "()[C", list1, classMemberLookup1, classResolver1);
            this.stringConstructorRef = constantPool1.getOrAddMethodRef("java/lang/String", "<init>", "([C)V", list1, classMemberLookup1, classResolver1);
            if (this.useHelperMethods) {
                LocalVariableList localVariableList5 = new LocalVariableList(true, "(Ljava/lang/String;)[C", 1);
                ArrayList arrayList4 = new ArrayList();
                this.buildToCharArrayHelperBody(localVariableList5, arrayList4, this.toCharArrayRef, this.useAlternateLoopEntry);
                MethodInfo methodInfo5 = this.targetClass
                        .createUniquelyNamedStaticMethod(
                                "(Ljava/lang/String;)[C",
                                arrayList4,
                                6,
                                1,
                                1,
                                localVariableList5,
                                new ExceptionHandlerSpec[0],
                                "String Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                1
                        );
                this.toCharArrayHelperRef = this.targetClass.addMethodRefConstant(methodInfo5, list1);
                String string2 = bl2 ? "(I[C)Ljava/lang/String;" : "([C)Ljava/lang/String;";
                LocalVariableList localVariableList7 = new LocalVariableList(true, string2, 2);
                ArrayList arrayList6 = new ArrayList();
                ProgramClass programClass2;
                if (bl2) {
                    this.emitKeyedXorDecryptLoop(
                            localVariableList7, arrayList6, list1, 1, this.toCharArrayRef, this.stringConstructorRef, this.internMethodRef, true, false
                    );
                    programClass2 = this.targetClass;
                } else {
                    this.emitXorDecryptLoop(
                            localVariableList7, arrayList6, list1, 1, this.toCharArrayRef, this.stringConstructorRef, this.internMethodRef, true, false
                    );
                    programClass2 = this.targetClass;
                }

                MethodInfo methodInfo6 = programClass2.createUniquelyNamedStaticMethod(
                        string2,
                        arrayList6,
                        7,
                        2,
                        1,
                        localVariableList7,
                        new ExceptionHandlerSpec[0],
                        "String Encryption",
                        list1,
                        inheritedMemberAnalyzer,
                        classMemberLookup1,
                        1
                );
                this.decryptHelperRef = this.targetClass.addMethodRefConstant(methodInfo6, list1);
            }

            if (bl1) {
                FieldInfo fieldInfo1 = this.targetClass
                        .createUniquelyNamedStaticField(
                                "[Ljava/lang/String;", this.targetClass.isInterface() ? 4 : 1, true, inheritedMemberAnalyzer, classMemberLookup1, 1
                        );
                this.stringArrayFieldRef = constantPool1.getOrAddFieldRef(
                        this.targetClass.getClassName(), fieldInfo1.getSourceName(), fieldInfo1.getDescriptor(), list1, classMemberLookup1, classResolver1, true
                );
                String string1 = bl3 ? "(III)Ljava/lang/String;" : "(II)Ljava/lang/String;";
                int bb = bl3 ? 10 : 9;
                LocalVariableList localVariableList6 = new LocalVariableList(true, string1, bb);
                ArrayList arrayList5 = new ArrayList();
                int[] bc = randomKeys(this.random, 2, 255);
                this.indexXorKey = (short) (bc[1] << 8 | bc[0]);
                this.byteShuffleTable = this.createShuffledByteTable();
                ResolvedFieldRef resolvedFieldRef1 = this.stringArrayFieldRef;
                ClassMemberLookup classMemberLookup3 = classMemberLookup1;
                ConstantPool constantPool3 = constantPool1;
                List list3 = list1;
                int[] byteShuffleTable = this.byteShuffleTable;
                Integer integer = this.indexXorKey;
                this.buildLookupMethodBody(
                        localVariableList6, arrayList5, bl3, resolvedFieldRef1, resolvedFieldRef, integer, byteShuffleTable, list3, constantPool3, classMemberLookup3, classResolver1
                );
                MethodInfo methodInfo7 = this.targetClass
                        .createUniquelyNamedStaticMethod(
                                string1,
                                arrayList5,
                                5,
                                bb,
                                1,
                                localVariableList6,
                                new ExceptionHandlerSpec[0],
                                "String Encryption",
                                list1,
                                inheritedMemberAnalyzer,
                                classMemberLookup1,
                                1
                        );
                this.lookupMethodRef = this.targetClass.addMethodRef(methodInfo7, list1);
            }
        }
    }

    public static void offsetKeys(int ba, int[] bb, int[] bc) {
        int bd = bc[ba];

        for (int i = 0; i < bb.length; i++) {
            bb[i] = (bb[i] + bd) % 256;
        }
    }

    public void buildBytesToStringBody(
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 1);
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createIntStore(1, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(190));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntStore(2, localVariableList1, 1));
        arrayList.add(new NewArrayInstruction(5));
        arrayList.add(Instruction.createObjectStore(3, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createIntStore(4, localVariableList1, 1));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(2, localVariableList1, 1));
        arrayList.add(new BranchInstruction(162, labelInstruction4));
        arrayList.add(Instruction.createIntPush(255));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createIntStore(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(192));
        arrayList.add(new BranchInstruction(162, labelInstruction1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createIntIncrement(1, 1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(SimpleInstruction.forOpcode(85));
        arrayList.add(new GotoInstruction(labelInstruction3));
        arrayList.add(labelInstruction1);
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(224));
        arrayList.add(new BranchInstruction(162, labelInstruction2));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(31));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(SimpleInstruction.forOpcode(120));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntStore(6, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntStore(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(6, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(63));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(SimpleInstruction.forOpcode(128));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntStore(6, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createIntIncrement(1, 1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(6, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(85));
        arrayList.add(new GotoInstruction(labelInstruction3));
        arrayList.add(labelInstruction2);
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(2, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(5));
        arrayList.add(SimpleInstruction.forOpcode(100));
        arrayList.add(new BranchInstruction(162, labelInstruction3));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(15));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntPush(12));
        arrayList.add(SimpleInstruction.forOpcode(120));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntStore(6, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntStore(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(6, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(63));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntPush(6));
        arrayList.add(SimpleInstruction.forOpcode(120));
        arrayList.add(SimpleInstruction.forOpcode(128));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntStore(6, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(4, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(51));
        arrayList.add(Instruction.createIntStore(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(6, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 1));
        arrayList.add(Instruction.createIntPush(63));
        arrayList.add(SimpleInstruction.forOpcode(126));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(SimpleInstruction.forOpcode(128));
        arrayList.add(SimpleInstruction.forOpcode(146));
        arrayList.add(Instruction.createIntStore(6, localVariableList1, 1));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(1, localVariableList1, 1));
        arrayList.add(Instruction.createIntIncrement(1, 1, localVariableList1, 1));
        arrayList.add(Instruction.createIntLoad(6, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(85));
        arrayList.add(labelInstruction3);
        arrayList.add(Instruction.createIntIncrement(4, 1, localVariableList1, 1));
        arrayList.add(new GotoInstruction(labelInstruction));
        arrayList.add(labelInstruction4);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 1));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createIntLoad(1, localVariableList1, 1));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/String", "<init>", "([CII)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(176));
    }

    public static String rollingXorEncrypt(String string, int[] ba, boolean bl) {
        int[] bb = new int[ba.length];
        System.arraycopy(ba, 0, bb, 0, ba.length);
        int bc = bb.length;
        char[] bd = string.toCharArray();
        int be = bd.length;

        for (int i = 0; i < be; i++) {
            int bg = i % bc;
            char bh = bd[i];
            bd[i] = (char) (bd[i] ^ bb[bg]);
            int bi = bb[bg] >>> 3 | bb[bg] << 5;
            short bj;
            if (bl) {
                bi ^= bh;
                bj = 255;
            } else {
                bi ^= bd[i];
                bj = 255;
            }

            bb[bg] = bi & bj;
        }

        return new String(bd);
    }

    public StringEncryptor(boolean useAlternateLoopEntry, boolean lazyDecrypt) {
        this.useAlternateLoopEntry = useAlternateLoopEntry;
        this.lazyDecrypt = lazyDecrypt;
        LongStream longStream = this.random.longs(1L, Long.MAX_VALUE);
        this.randomLongKeys = longStream.iterator();
    }

    public void emitXorDecryptLoop(
            LocalVariableList localVariableList1,
            List list1,
            List list2,
            int ba,
            ResolvedMethodRefConstant resolvedMethodRefConstant,
            ResolvedMethodRefConstant resolvedMethodRefConstant1,
            ResolvedMethodRefConstant resolvedMethodRefConstant2,
            boolean bl,
            boolean bl1
    ) {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction6 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction7 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction8 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction9 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction10 = new LabelInstruction(true, 1);
        LabelInstruction[] labelInstructions = new LabelInstruction[]{
                labelInstruction1, labelInstruction2, labelInstruction3, labelInstruction4, labelInstruction5, labelInstruction6
        };
        if (bl) {
            list1.add(Instruction.createObjectLoad(0, localVariableList1, 1));
        } else {
            list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        }

        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(SimpleInstruction.forOpcode(95));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(ba, localVariableList1, 1));
        if (this.useAlternateLoopEntry) {
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(SimpleInstruction.forOpcode(4));
            list1.add(new BranchInstruction(163, labelInstruction9));
        } else {
            list1.add(new GotoInstruction(labelInstruction9));
        }

        list1.add(labelInstruction);
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntLoad(ba, localVariableList1, 1));
        list1.add(labelInstruction10);
        list1.add(SimpleInstruction.forOpcode(92));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(Instruction.createIntLoad(ba, localVariableList1, 1));
        list1.add(Instruction.createIntPush(7));
        list1.add(SimpleInstruction.forOpcode(112));
        list1.add(new TableSwitchInstruction(labelInstruction7, 5, labelInstructions));
        int bb = 0;
        int bc = 0;

        for (byte bd = 6; bc < bd; bd = 6) {
            list1.add(labelInstructions[bb]);
            list1.add(Instruction.createIntPush(this.xorKeys[bb]));
            list1.add(new GotoInstruction(labelInstruction8));
            bc = ++bb;
        }

        list1.add(labelInstruction7);
        list1.add(Instruction.createIntPush(this.xorKeys[6]));
        list1.add(labelInstruction8);
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(146));
        list1.add(SimpleInstruction.forOpcode(85));
        list1.add(Instruction.createIntIncrement(ba, 1, localVariableList1, 1));
        if (this.useAlternateLoopEntry) {
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(SimpleInstruction.forOpcode(90));
            list1.add(new BranchInstruction(154, labelInstruction9));
            list1.add(SimpleInstruction.forOpcode(92));
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(new GotoInstruction(labelInstruction10));
        }

        list1.add(labelInstruction9);
        list1.add(SimpleInstruction.forOpcode(95));
        list1.add(SimpleInstruction.forOpcode(90));
        list1.add(Instruction.createIntLoad(ba, localVariableList1, 1));
        list1.add(new BranchInstruction(163, labelInstruction));
        ConstantPool constantPool1 = this.targetClass.getClassConstantPool();
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new TypeInstruction(resolvedClassConstant));
        list1.add(SimpleInstruction.forOpcode(90));
        list1.add(SimpleInstruction.forOpcode(95));
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        label41:
        if (resolvedMethodRefConstant2 != null) {
            boolean bl2;
            if (!bl1) {
                if (this.lazyDecrypt) {
                    break label41;
                }

                bl2 = HiddenOptionFlags.STRING_ENCRYPT_INTERN;
            } else {
                bl2 = HiddenOptionFlags.STRING_ENCRYPT_INTERN;
            }

            if (bl2) {
                list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
            }
        }

        if (bl) {
            list1.add(SimpleInstruction.forOpcode(176));
        } else {
            list1.add(SimpleInstruction.forOpcode(95));
            list1.add(SimpleInstruction.forOpcode(87));
        }
    }

    public int nextRandomKey(Random random1, int ba) {
        int bb = random1.nextInt() % ba;
        if (bb < 0) {
            bb += ba;
        }

        return bb + 1;
    }
}
