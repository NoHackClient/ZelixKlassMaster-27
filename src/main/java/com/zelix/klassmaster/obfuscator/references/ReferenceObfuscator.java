package com.zelix.klassmaster.obfuscator.references;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ClassMemberRef;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.NamedTypeRef;
import com.zelix.klassmaster.classfile.PrimitiveTypeDescriptor;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.MethodHandleRefKind;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodHandleConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.hierarchy.ArrayTypeReference;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.insn.BipushInstruction;
import com.zelix.klassmaster.classfile.insn.BranchInstruction;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.GotoInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.classfile.insn.NewArrayInstruction;
import com.zelix.klassmaster.classfile.insn.PrimitiveBoxingGenerator;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.classfile.insn.TableSwitchInstruction;
import com.zelix.klassmaster.classfile.insn.TypeInstruction;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.obfuscator.parameters.MethodKeyInjector;
import com.zelix.klassmaster.obfuscator.string.StringEncryptor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMapView;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.VisitableNode;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class ReferenceObfuscator implements SyntheticClassFactory {
    private static final int MAX_CHAR_VALUE = (int) (Math.pow(2.0, 7.0) - 1.0);
    public static final int MAX_METHOD_CODE_SIZE = 65535 - (HiddenOptionFlags.REFERENCE_OBFUSCATION_METHOD_KEYS ? 47500 : 32);
    public static final byte[] LEGACY_LOOKUP_CLASS_BYTES = ZkmUtils.parseByteString(
            "5m72565a00030019000a0700080700090a000200040c0006000501000314152e0100061o2x322x381q0100041v332s2t0100051x3134383d01000g2y2p3a2p1b302p322v1b272q2y2t2r38000100010002000000000001000100060005000100070000000h0001000100000005165300034x000000000000"
    );
    public static final byte[] LOOKUP_CLASS_BYTES = ZkmUtils.parseByteString(
            "5m72565a0000001d000a0a000300070700080700090100061o2x322x381q01000314152e0100041v332s2t0c000400050100051x3134383d01000g2y2p3a2p1b302p322v1b272q2y2t2r38000x00020003000000000001000100040005000100060000000h0001000100000005165300014x000000000000"
    );
    public static IntegerCache integerCache = IntegerCache.getInstance();
    private final TwoKeyMap memberRefCache = new TwoKeyMap();
    public final Random random = ZkmUtils.createRandom(65536);
    public final boolean randomizeNames;
    public final BooleanFlag twoLongKeysFlag;
    public final List nameDictionary;

    public String encryptMemberString(String string, int ba, ObservableHolder observableHolder, int[] bb) throws ZkmException, IOException {
        long bc = ba;
        bc <<= 46;
        int[] bd = StringEncryptor.randomKeys(this.random, 6, MAX_CHAR_VALUE);
        long be = Math.abs(this.random.nextInt() % 16);
        int bf = (int) ((ba & 3) << 4 | be);
        bc |= be << 42;
        String string1 = StringEncryptor.xorWithKeys(string, bd);
        shiftCharOffsets(bf, bd, bb);

        for (int i = 0; i < bd.length; i++) {
            long bh = bd[i];
            int bi = 7 * (6 - i - 1);
            long bj = bh << bi;
            bc |= bj;
        }

        observableHolder.setValue(bc);
        return string1;
    }

    public void buildCallSiteInvokerCode(
            ArrayList arrayList,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            List list1,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassRepository classRepository1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(this.usesTwoLongKeys() ? 11 : 9);
        mutableInt.setValue(6);
        int ba = this.usesTwoLongKeys() ? 9 : 8;
        LocalVariableList localVariableList2 = localVariableList1;
        arrayList.add(Instruction.createObjectLoad(4, localVariableList2, 4));
        arrayList.add(SimpleInstruction.forOpcode(190));
        arrayList.add(Instruction.createIntPush(this.usesTwoLongKeys() ? 2 : 1));
        arrayList.add(SimpleInstruction.forOpcode(100));
        arrayList.add(Instruction.createIntStore(5, localVariableList1, 4));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        arrayList.add(Instruction.createIntLoad(5, localVariableList1, 4));
        arrayList.add(SimpleInstruction.forOpcode(50));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
        arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "longValue", "()J", list1, classRepository1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        arrayList.add(Instruction.createLongStore(6, localVariableList1, 4));
        if (this.usesTwoLongKeys()) {
            arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 4));
            arrayList.add(Instruction.createIntIncrement(5, 1, localVariableList1, 4));
            arrayList.add(Instruction.createIntLoad(5, localVariableList1, 4));
            arrayList.add(SimpleInstruction.forOpcode(50));
            arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/Long", "longValue", "()J", list1, classRepository1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
            arrayList.add(Instruction.createLongStore(ba, localVariableList1, 4));
        }

        localVariableList2 = localVariableList1;
        arrayList.add(Instruction.createObjectLoad(0, localVariableList2, 4));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        arrayList.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        arrayList.add(Instruction.createLongLoad(6, localVariableList1, 4));
        if (this.usesTwoLongKeys()) {
            arrayList.add(Instruction.createLongLoad(ba, localVariableList1, 4));
        }

        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRef));
        arrayList.add(Instruction.createObjectStore(8, localVariableList1, 4));
        arrayList.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 4));
        localVariableList2 = localVariableList1;
        arrayList.add(Instruction.createObjectLoad(3, localVariableList2, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "explicitCastArguments",
                "(Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                list1,
                classRepository1,
                classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant5));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "setTarget", "(Ljava/lang/invoke/MethodHandle;)V", list1, classRepository1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        arrayList.add(Instruction.createObjectLoad(8, localVariableList1, 4));
        arrayList.add(Instruction.createClassConstantLoad(constantPool1, list1));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        arrayList.add(SimpleInstruction.forOpcode(190));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandle", "asSpreader", "(Ljava/lang/Class;I)Ljava/lang/invoke/MethodHandle;", list1, classRepository1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        arrayList.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandle", "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", list1, classRepository1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        arrayList.add(SimpleInstruction.forOpcode(176));
    }

    public ResolvedMethodRef createMethodHandleResolverMethod(
            boolean bl,
            ProgramClass programClass1,
            int[] ba,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedMethodRef resolvedMethodRef1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[1];
        String string2 = this.usesTwoLongKeys()
                ? "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;JJ)Ljava/lang/invoke/MethodHandle;"
                : "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;J)Ljava/lang/invoke/MethodHandle;";
        int bb = this.usesTwoLongKeys() ? 15 : 13;
        String string = string2;
        int bc = bb;
        String string1 = string;
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, bc);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildMethodHandleResolverCode(
                arrayList,
                localVariableList1,
                exceptionHandlerSpecs,
                ba,
                resolvedMethodRef,
                resolvedMethodRef1,
                list1,
                mutableInt1,
                mutableInt,
                constantPool1,
                classRepository1,
                classResolver1
        );
        return this.addGeneratedMethod(
                programClass1,
                this.usesTwoLongKeys()
                        ? "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;JJ)Ljava/lang/invoke/MethodHandle;"
                        : "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;J)Ljava/lang/invoke/MethodHandle;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                bl,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public static void shiftCharOffsets(int ba, int[] bb, int[] bc) {
        int bd = bc[ba];

        for (int i = 0; i < bb.length; i++) {
            bb[i] = (bb[i] + bd) % (MAX_CHAR_VALUE + 1);
        }
    }

    public void buildFieldLookupCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedMethodRef resolvedMethodRef1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(3, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(4, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction3));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(5, localVariableList1, 4));
        list1.add(labelInstruction1);
        list1.add(Instruction.createIntLoad(5, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(new BranchInstruction(162, labelInstruction3));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(5, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        list1.add(Instruction.createObjectStore(3, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction2));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction2);
        list1.add(Instruction.createIntIncrement(5, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction1));
        list1.add(labelInstruction3);
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public void buildClassResolverCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            List list2,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(this.twoLongKeysFlag.getValue() ? 8 : 6);
        mutableInt.setValue(4);
        int ba = this.twoLongKeysFlag.getValue() ? 2 : 0;
        int bb = ba + 2;
        int bc = bb + 1;
        int bd = bc + 1;
        int be = bd + 1;
        LabelInstruction labelInstruction = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 128);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list2);
        exceptionHandlerSpecs[0] = new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction4);
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(Instruction.createObjectStore(bc, localVariableList1, 4));
        list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(Instruction.createLongLoad(ba, localVariableList1, 4));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        list1.add(Instruction.createIntStore(bb, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(labelInstruction);
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new ConstantRefInstruction(193, resolvedClassConstant1));
        list1.add(new BranchInstruction(153, labelInstruction2));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(bc, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        if (HiddenOptionFlags.ALT_REFERENCE_LABEL_PLACEMENT) {
            list1.add(labelInstruction1);
        }

        list1.add(new GotoInstruction(labelInstruction3));
        list1.add(labelInstruction2);
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Class", list2);
        list1.add(new ConstantRefInstruction(192, resolvedClassConstant2));
        list1.add(Instruction.createObjectStore(bc, localVariableList1, 4));
        if (!HiddenOptionFlags.ALT_REFERENCE_LABEL_PLACEMENT) {
            list1.add(labelInstruction1);
        }

        list1.add(new GotoInstruction(labelInstruction3));
        list1.add(labelInstruction4);
        list1.add(Instruction.createObjectStore(be, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list2);
        list1.add(new TypeInstruction(resolvedClassConstant3));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/Exception", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant2));
        list1.add(SimpleInstruction.forOpcode(191));
        list1.add(labelInstruction3);
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public void buildFieldResolverCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            ResolvedMethodRef resolvedMethodRef1,
            ResolvedMethodRef resolvedMethodRef2,
            ResolvedMethodRef resolvedMethodRef3,
            List list2,
            ConstantLong constantLong,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        int ba = this.twoLongKeysFlag.getValue() ? 2 : 0;
        int bb = ba + 2;
        int bc = bb + 1;
        int bd = bc + 1;
        int be = bd + 1;
        int bf = be + 1;
        int bg = bf + 1;
        int bh = bg + 1;
        int bi = bh + 1;
        int bj = bi + 1;
        int bk = bj + 1;
        int bl = bk + 1;
        int bm = bl + 1;
        int bn = bm;
        mutableInt1.setValue(this.twoLongKeysFlag.getValue() ? 16 : 14);
        mutableInt.setValue(3);
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction6 = new LabelInstruction(true, 1);
        list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(Instruction.createLongLoad(ba, localVariableList1, 4));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        list1.add(Instruction.createIntStore(bb, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(bc, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new ConstantRefInstruction(193, resolvedClassConstant));
        list1.add(new BranchInstruction(153, labelInstruction6));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(new BipushInstruction(8));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/String", "indexOf", "(I)I", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createIntStore(be, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "substring", "(II)Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "parseLong", "(Ljava/lang/String;I)J", list2, classMemberLookup1, classResolver1
        );
        list1.add(new BipushInstruction(36));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bf, localVariableList1, 4));
        list1.add(Instruction.createIntIncrement(be, 1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(new BipushInstruction(8));
        list1.add(Instruction.createIntLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "indexOf", "(II)I", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(Instruction.createIntStore(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(be, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(Instruction.createObjectStore(bh, localVariableList1, 4));
        list1.add(Instruction.createIntIncrement(bg, 1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "substring", "(I)Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        list1.add(new BipushInstruction(36));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        list1.add(Instruction.createObjectStore(bj, localVariableList1, 4));
        list1.add(labelInstruction);
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef2));
        list1.add(Instruction.createObjectStore(bk, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction1));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction1);
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list1.add(Instruction.createObjectStore(bl, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bl, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bm, localVariableList1, 4));
        list1.add(labelInstruction2);
        list1.add(Instruction.createIntLoad(bm, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bl, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(new BranchInstruction(162, labelInstruction4));
        list1.add(Instruction.createObjectLoad(bl, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bm, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef3));
        list1.add(Instruction.createObjectStore(bk, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction3));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bb, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction3);
        list1.add(Instruction.createIntIncrement(bm, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction2));
        list1.add(labelInstruction4);
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("java.lang.Object", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "equals", "(Ljava/lang/Object;)Z", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        list1.add(new BranchInstruction(154, labelInstruction5));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(Instruction.createObjectStore(bj, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        list1.add(new BranchInstruction(199, labelInstruction));
        list1.add(new ConstantRefInstruction(20, constantLong));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bj, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction));
        list1.add(labelInstruction5);
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/StringBuffer", list2);
        list1.add(new TypeInstruction(resolvedClassConstant1));
        list1.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "<init>", "()V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant9));
        list1.add(Instruction.createObjectStore(bn, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bn, localVariableList1, 4));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("NoSuchFieldException in ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        list1.add(new BipushInstruction(32));
        ResolvedMethodRefConstant resolvedMethodRefConstant11 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "append", "(C)Ljava/lang/StringBuffer;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant11));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        list1.add(new BipushInstruction(32));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant11));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        list1.add(SimpleInstruction.forOpcode(87));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list2);
        list1.add(new TypeInstruction(resolvedClassConstant2));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(bn, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant12 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant12));
        ResolvedMethodRefConstant resolvedMethodRefConstant13 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant13));
        list1.add(SimpleInstruction.forOpcode(191));
        list1.add(labelInstruction6);
        list1.add(Instruction.createObjectLoad(bc, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/reflect/Field", list2);
        list1.add(new ConstantRefInstruction(192, resolvedClassConstant3));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public void buildDeclaredFieldFinderCode(
            List list1,
            LocalVariableList localVariableList1,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(7);
        mutableInt.setValue(2);
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getDeclaredFields", "()[Ljava/lang/reflect/Field;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(3, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(Instruction.createIntStore(4, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(5, localVariableList1, 4));
        list1.add(labelInstruction);
        list1.add(Instruction.createIntLoad(5, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(4, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction2));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(5, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(6, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(6, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "equals", "(Ljava/lang/Object;)Z", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        list1.add(new BranchInstruction(153, labelInstruction1));
        list1.add(Instruction.createObjectLoad(6, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(new BranchInstruction(166, labelInstruction1));
        list1.add(Instruction.createObjectLoad(6, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction1);
        list1.add(Instruction.createIntIncrement(5, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction));
        list1.add(labelInstruction2);
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public ResolvedMethodRef createFieldResolverMethod(
            boolean bl,
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            ResolvedMethodRef resolvedMethodRef1,
            ResolvedMethodRef resolvedMethodRef2,
            ResolvedMethodRef resolvedMethodRef3,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantLong constantLong,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        String string2;
        BooleanFlag booleanFlag;
        if (this.twoLongKeysFlag.getValue()) {
            string2 = "(JJ)Ljava/lang/reflect/Field;";
            booleanFlag = this.twoLongKeysFlag;
        } else {
            string2 = "(J)Ljava/lang/reflect/Field;";
            booleanFlag = this.twoLongKeysFlag;
        }

        int ba = booleanFlag.getValue() ? 16 : 14;
        String string = string2;
        int bb = ba;
        String string1 = string;
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, bb);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildFieldResolverCode(
                arrayList,
                localVariableList1,
                resolvedMethodRef,
                resolvedFieldRef,
                resolvedFieldRef1,
                resolvedMethodRef1,
                resolvedMethodRef2,
                resolvedMethodRef3,
                list1,
                constantLong,
                mutableInt1,
                mutableInt,
                constantPool1,
                classRepository1,
                classResolver1
        );
        return this.addGeneratedMethod(
                programClass1,
                this.twoLongKeysFlag.getValue() ? "(JJ)Ljava/lang/reflect/Field;" : "(J)Ljava/lang/reflect/Field;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                bl,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public ClassMemberRef getOrCreateMemberRef(MemberInfo memberInfo1, ClassFileBase classFileBase) {
        ClassMemberRef classMemberRef = (ClassMemberRef) this.memberRefCache.getValue(memberInfo1, classFileBase);
        if (classMemberRef == null) {
            classMemberRef = new ClassMemberRef(memberInfo1, classFileBase);
        }

        return classMemberRef;
    }

    public int[] pickRandomLetters() {
        int[] ba = new int[]{
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
                36,
                162,
                163,
                164,
                165,
                170,
                181,
                186,
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
                248,
                249,
                250,
                251,
                252,
                253,
                254,
                255
        };
        int[] bb = new int[]{104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115};
        int[] bc;
        if (this.usesTwoLongKeys() && HiddenOptionFlags.XOR_INDY_OPCODE_KEY) {
            bc = bb;
        } else {
            bc = ba;
        }

        byte be;
        if (!HiddenOptionFlags.DONT_SHUFFLE_REFERENCE_OPCODES) {
            ZkmUtils.shuffleInts(bc, this.random);
            be = 7;
        } else {
            be = 7;
        }

        int[] bd = new int[be];
        System.arraycopy(bc, 0, bd, 0, bd.length);
        return bd;
    }

    public List buildReturnUnboxCode(
            ResolvedMethodRef resolvedMethodRef, ConstantPool constantPool1, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList(2);
        String string = ConstantPoolEntry.getReturnDescriptor(resolvedMethodRef.getDescriptor());
        if (string.equals("V")) {
            arrayList.add(SimpleInstruction.forOpcode(87));
            return arrayList;
        }

        if (!string.startsWith("[") && (!string.startsWith("L") || !string.endsWith(";"))) {
            if (string.equals("Z")) {
                ResolvedClassConstant resolvedClassConstant8 = constantPool1.getOrCreateClassConstant("java/lang/Boolean", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant8));
                ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                        "java/lang/Boolean", "booleanValue", "()Z", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
                return arrayList;
            } else if (string.equals("B")) {
                ResolvedClassConstant resolvedClassConstant7 = constantPool1.getOrCreateClassConstant("java/lang/Byte", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant7));
                ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                        "java/lang/Byte", "byteValue", "()B", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
                return arrayList;
            } else if (string.equals("S")) {
                ResolvedClassConstant resolvedClassConstant6 = constantPool1.getOrCreateClassConstant("java/lang/Short", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant6));
                ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                        "java/lang/Short", "shortValue", "()S", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
                return arrayList;
            } else if (string.equals("C")) {
                ResolvedClassConstant resolvedClassConstant5 = constantPool1.getOrCreateClassConstant("java/lang/Character", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant5));
                ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                        "java/lang/Character", "charValue", "()C", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
                return arrayList;
            } else if (string.equals("I")) {
                ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("java/lang/Integer", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant4));
                ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                        "java/lang/Integer", "intValue", "()I", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
                return arrayList;
            } else if (string.equals("J")) {
                ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/Long", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant3));
                ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                        "java/lang/Long", "longValue", "()J", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
                return arrayList;
            } else if (string.equals("F")) {
                ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Float", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant2));
                ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                        "java/lang/Float", "floatValue", "()F", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
                return arrayList;
            } else if (string.equals("D")) {
                ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Double", list1);
                arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant1));
                ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                        "java/lang/Double", "doubleValue", "()D", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
                return arrayList;
            } else {
                return null;
            }
        } else {
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(
                    ConstantPoolEntry.stripClassDescriptor(resolvedMethodRef.getStackReturnType()), list1
            );
            arrayList.add(new ConstantRefInstruction(192, resolvedClassConstant));
            return arrayList;
        }
    }

    public void buildReferenceTableChunks(
            String string,
            List list1,
            Map map1,
            Map map2,
            int[] ba,
            Map map3,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            MethodKeyInjector methodKeyInjector,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            List list2,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        int bb = map1.size() + map2.size();
        int bc = 0;
        int bd = 0;
        InstructionListBuffer instructionListBuffer = new InstructionListBuffer(bb, string);
        ArrayList arrayList = instructionListBuffer.getInstructions();
        list1.add(instructionListBuffer);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        bd += 3;
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(map1.size()));
        ObservableHolder observableHolder = new ObservableHolder();
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            NamedTypeRef namedTypeRef = (NamedTypeRef) entry.getKey();
            String string1 = namedTypeRef.getClassName().replace('/', '.');
            Integer integer = (Integer) entry.getValue();
            hashMap.put(string1, integer);
            ArrayList arrayList1;
            byte bf;
            if (methodKeyInjector != null) {
                if (arrayList.size() == 1) {
                    MutableInt mutableInt = new MutableInt();
                    LocalVariableList localVariableList2 = instructionListBuffer.getLocalVariables();
                    list1.size();
                    List list3 = methodKeyInjector.buildKeyDecodeInstructions(localVariableList2, mutableInt, list2, constantPool1);
                    arrayList.addAll(0, list3);
                    bd += mutableInt.getValue();
                    arrayList1 = arrayList;
                    bf = 89;
                } else {
                    arrayList1 = arrayList;
                    bf = 89;
                }
            } else {
                arrayList1 = arrayList;
                bf = 89;
            }

            arrayList1.add(SimpleInstruction.forOpcode(bf));
            bd = ++bd + Instruction.appendIntConstant(integer, arrayList, constantPool1, list2);
            if (namedTypeRef.isPrimitive()) {
                map3.put(integer, this.createIndexedKey(integer));
                PrimitiveTypeDescriptor primitiveTypeDescriptor = (PrimitiveTypeDescriptor) namedTypeRef;
                ResolvedFieldRef resolvedFieldRef2 = constantPool1.getOrAddFieldRef(
                        primitiveTypeDescriptor.getWrapperClassName(),
                        PrimitiveTypeDescriptor.getTypeFieldName(),
                        PrimitiveTypeDescriptor.getTypeFieldDescriptor(),
                        list2,
                        classMemberLookup1,
                        classResolver1,
                        true
                );
                arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef2));
                arrayList.add(SimpleInstruction.forOpcode(83));
                arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef1));
                bd += 7;
                bd += Instruction.appendIntConstant(integer, arrayList, constantPool1, list2);
                ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(primitiveTypeDescriptor.getWrapperClassName(), list2);
                arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant));
                arrayList.add(SimpleInstruction.forOpcode(83));
                bd += 4;
            } else {
                String string3 = this.encryptMemberString(string1, integer, observableHolder, ba);
                map3.put(integer, observableHolder.getValue());
                ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(string3, list2);
                arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant1));
                bd += 3;
                arrayList.add(SimpleInstruction.forOpcode(83));
                bd++;
            }

            bc++;
            if (bd > MAX_METHOD_CODE_SIZE) {
                arrayList.add(SimpleInstruction.forOpcode(177));
                if (bc < bb) {
                    instructionListBuffer = new InstructionListBuffer(bb, string);
                    arrayList = instructionListBuffer.getInstructions();
                    list1.add(instructionListBuffer);
                    arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
                    bd = 3;
                } else {
                    arrayList = null;
                    bd = 0;
                }
            }
        }

        iterator = map2.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry1 = (Entry) iterator.next();
            ClassMemberRef classMemberRef = (ClassMemberRef) entry1.getKey();
            int be = (Integer) entry1.getValue();
            String string2 = this.buildMemberDescriptor(classMemberRef, hashMap, map3);
            String string4 = this.encryptMemberString(string2, be, observableHolder, ba);
            map3.put(be, observableHolder.getValue());
            ResolvedStringConstant resolvedStringConstant2 = constantPool1.addStringConstant(string4, list2);
            if (methodKeyInjector != null && arrayList.size() == 1) {
                MutableInt mutableInt1 = new MutableInt();
                LocalVariableList localVariableList1 = instructionListBuffer.getLocalVariables();
                list1.size();
                List list4 = methodKeyInjector.buildKeyDecodeInstructions(localVariableList1, mutableInt1, list2, constantPool1);
                arrayList.addAll(0, list4);
                bd += mutableInt1.getValue();
            }

            arrayList.add(SimpleInstruction.forOpcode(89));
            bd += Instruction.appendIntConstant(be, arrayList, constantPool1, list2);
            arrayList.add(new ConstantRefInstruction(19, resolvedStringConstant2));
            arrayList.add(SimpleInstruction.forOpcode(83));
            bd += 5;
            bc++;
            if (bd > MAX_METHOD_CODE_SIZE) {
                arrayList.add(SimpleInstruction.forOpcode(177));
                if (bc < bb) {
                    instructionListBuffer = new InstructionListBuffer(bb, string);
                    arrayList = instructionListBuffer.getInstructions();
                    list1.add(instructionListBuffer);
                    arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
                    bd = 3;
                } else {
                    arrayList = null;
                    bd = 0;
                }
            }
        }

        if (arrayList != null) {
            arrayList.add(SimpleInstruction.forOpcode(177));
        }
    }

    public ResolvedMethodRef addHelperMethod(
            ProgramClass programClass1,
            String string,
            ArrayList arrayList,
            int ba,
            int bb,
            LocalVariableList localVariableList1,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            boolean bl,
            List list1,
            ObservableHolder observableHolder,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmException, IOException {
        byte bc;
        if (bl) {
            bc = 4;
        } else {
            bc = 1;
        }

        MethodInfo methodInfo1 = programClass1.createUniquelyNamedStaticMethod(
                string,
                arrayList,
                ba,
                bb,
                bc,
                localVariableList1,
                exceptionHandlerSpecs,
                "Reference Obfuscation",
                list1,
                inheritedMemberAnalyzer,
                classMemberLookup1,
                4
        );
        if (observableHolder != null) {
            observableHolder.setValue(methodInfo1.getBytecode());
        }

        return programClass1.addMethodRef(methodInfo1, list1);
    }

    public ResolvedMethodRefConstant getMethodInvokeRef(
            ConstantPool constantPool1, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        return constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
    }

    public NamedTypeRef resolveTypeRef(String string, Integer integer, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        ClassResolver classResolver2 = classResolver1;
        String string1 = string;
        Integer integer1 = integer;

        try {
            if (ConstantPoolEntry.isPrimitiveDescriptor(string1)) {
                return PrimitiveTypeDescriptor.forDescriptor(string1);
            }
        } catch (ZkmRuntimeException zkmRuntimeException2) {
            throw zkmRuntimeException2;
        }

        String string3 = string1;

        try {
            if (string3.startsWith("[")) {
                return new ArrayTypeReference(string1, integer1, classResolver2, ignoreMissingReferencesSpec1);
            }
        } catch (ZkmRuntimeException zkmRuntimeException1) {
            throw zkmRuntimeException1;
        }

        try {
            string3 = string1.substring(1, string1.length() - 1);
        } catch (ZkmRuntimeException zkmRuntimeException) {
            throw zkmRuntimeException;
        }

        String string2 = string3;
        return classResolver2.getVersionedClass(string2, integer1, "Looking for type '" + string1 + "' during " + "Reference Obfuscation");
    }

    public void buildDeclaredMethodFinderCode(
            List list1,
            LocalVariableList localVariableList1,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(11);
        mutableInt.setValue(3);
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 1);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(5, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(5, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(Instruction.createIntStore(6, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(7, localVariableList1, 4));
        list1.add(labelInstruction);
        list1.add(Instruction.createIntLoad(7, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(6, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction5));
        list1.add(Instruction.createObjectLoad(5, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(7, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(8, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(8, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "equals", "(Ljava/lang/Object;)Z", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        list1.add(new BranchInstruction(153, labelInstruction4));
        list1.add(Instruction.createObjectLoad(8, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(new BranchInstruction(166, labelInstruction4));
        list1.add(Instruction.createObjectLoad(8, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        list1.add(Instruction.createObjectStore(9, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(9, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(Instruction.createIntLoad(3, localVariableList1, 4));
        list1.add(new BranchInstruction(160, labelInstruction4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(10, localVariableList1, 4));
        list1.add(labelInstruction1);
        list1.add(Instruction.createIntLoad(10, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(3, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction3));
        list1.add(Instruction.createObjectLoad(9, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(10, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(10, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(new BranchInstruction(165, labelInstruction2));
        list1.add(new GotoInstruction(labelInstruction4));
        list1.add(labelInstruction2);
        list1.add(Instruction.createIntIncrement(10, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction1));
        list1.add(labelInstruction3);
        list1.add(Instruction.createObjectLoad(8, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction4);
        list1.add(Instruction.createIntIncrement(7, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction));
        list1.add(labelInstruction5);
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public ReferenceObfuscator(BooleanFlag booleanFlag, List list1, boolean randomizeNames) {
        this.randomizeNames = randomizeNames;
        this.twoLongKeysFlag = booleanFlag;
        this.nameDictionary = list1;
    }

    public ResolvedMethodRef createIndexDecoderMethod(
            ProgramClass programClass1,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            int[] ba,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        String string = this.twoLongKeysFlag.getValue() ? "(JJ)I" : "(J)I";
        String string1 = string;
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildIndexDecoderCode(
                arrayList,
                localVariableList1,
                resolvedFieldRef,
                resolvedFieldRef1,
                ba,
                list1,
                mutableInt1,
                mutableInt,
                constantPool1,
                classRepository1,
                classResolver1
        );
        return this.addGeneratedMethod(
                programClass1,
                this.twoLongKeysFlag.getValue() ? "(JJ)I" : "(J)I",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                false,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public void createArgumentPackerMethods(
            boolean bl,
            ProgramClass programClass1,
            TwoKeyMap twoKeyMap,
            Map map1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Set set1,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        SetMultiMap setMultiMap = new SetMultiMap();
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ClassMemberRef classMemberRef = (ClassMemberRef) entry.getKey();
            if (set1.contains(classMemberRef) || needsReflectionInvoker(programClass1, programClass1, classMemberRef, classRepository1)) {
                StringBuilder stringBuilder = new StringBuilder();
                List list2 = ConstantPoolEntry.getParameterTypes(classMemberRef.getDescriptor());
                Iterator iterator1 = list2.iterator();

                while (iterator1.hasNext()) {
                    String string = (String) iterator1.next();
                    if (string.length() == 1) {
                        stringBuilder.append(string);
                    } else {
                        stringBuilder.append("Ljava/lang/Object;");
                    }
                }

                setMultiMap.addValue(stringBuilder.toString(), classMemberRef);
            }
        }

        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        Iterator iterator3 = setMultiMap.entrySet().iterator();

        while (iterator3.hasNext()) {
            Entry entry1 = (Entry) iterator3.next();
            String string1 = (String) entry1.getKey();
            String string2 = "(" + string1 + ")[Ljava/lang/Object;";
            LocalVariableList localVariableList1 = new LocalVariableList(true, string2, 5);
            ArrayList arrayList = new ArrayList();
            MutableInt mutableInt = new MutableInt();
            int ba = this.buildArgumentPackerCode(
                    string2, arrayList, localVariableList1, mutableInt, list1, constantPool1, classRepository1, classResolver1, programClass1.supportsJava5()
            );
            ResolvedMethodRef resolvedMethodRef = this.addGeneratedMethod(
                    programClass1,
                    string2,
                    arrayList,
                    mutableInt.getValue(),
                    ba,
                    localVariableList1,
                    exceptionHandlerSpecs,
                    bl,
                    list1,
                    inheritedMemberAnalyzer,
                    classRepository1
            );
            Iterator iterator2 = ((Set) entry1.getValue()).iterator();

            while (iterator2.hasNext()) {
                ClassMemberRef classMemberRef1 = (ClassMemberRef) iterator2.next();
                twoKeyMap.putValue(programClass1, classMemberRef1, resolvedMethodRef);
            }
        }
    }

    public static boolean supportsMethodHandles(ClassFileBase classFileBase) {
        return classFileBase.supportsJava8() || !HiddenOptionFlags.NO_JAVA7_INDY && classFileBase.supportsJava7() && !classFileBase.isInterface();
    }

    public Long createIndexedKey(int ba) {
        long bb = ba;
        bb <<= 46;
        long bc = this.random.nextLong() & 70368744177663L;
        bb |= bc;
        return bb;
    }

    public static boolean bothSupportInvokedynamic(ProgramClass programClass1, ProgramClass programClass2) {
        return HiddenOptionFlags.OBFUSCATE_REFERENCES_INDY
                && supportsInvokedynamic(programClass1)
                && (programClass2 == null || supportsInvokedynamic(programClass2));
    }

    public void buildMethodLookupCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedMethodRef resolvedMethodRef1,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(3, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(5, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(5, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction));
        list1.add(Instruction.createObjectLoad(5, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(6, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(6, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction3));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(7, localVariableList1, 4));
        list1.add(labelInstruction1);
        list1.add(Instruction.createIntLoad(7, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(6, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(new BranchInstruction(162, labelInstruction3));
        list1.add(Instruction.createObjectLoad(6, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(7, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(3, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        list1.add(Instruction.createObjectStore(5, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(5, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction2));
        list1.add(Instruction.createObjectLoad(5, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction2);
        list1.add(Instruction.createIntIncrement(7, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction1));
        list1.add(labelInstruction3);
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public int[] shuffleKeyBitPositions() {
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
                63
        };
        ZkmUtils.shuffleInts(ba, this.random);
        return ba;
    }

    public ResolvedMethodRef createDeclaredMethodFinderMethod(
            ProgramClass programClass1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        LocalVariableList localVariableList1 = new LocalVariableList(
                true, "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", 11
        );
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        this.buildDeclaredMethodFinderCode(arrayList, localVariableList1, list1, mutableInt1, mutableInt, constantPool1, classRepository1, classResolver1);
        return this.addGeneratedMethod(
                programClass1,
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                false,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public ResolvedMethodRef createClassResolverMethod(
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[1];
        String string = this.twoLongKeysFlag.getValue() ? "(JJ)Ljava/lang/Class;" : "(J)Ljava/lang/Class;";
        String string1 = string;
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildClassResolverCode(
                arrayList,
                localVariableList1,
                resolvedMethodRef,
                resolvedFieldRef,
                resolvedFieldRef1,
                list1,
                exceptionHandlerSpecs,
                mutableInt1,
                mutableInt,
                constantPool1,
                classRepository1,
                classResolver1
        );
        return this.addGeneratedMethod(
                programClass1,
                this.twoLongKeysFlag.getValue() ? "(JJ)Ljava/lang/Class;" : "(J)Ljava/lang/Class;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                false,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public ResolvedMethodRef createFieldLookupMethod(
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MutableInt mutableInt = new MutableInt(6);
        MutableInt mutableInt1 = new MutableInt(3);
        ObservableHolder observableHolder = new ObservableHolder();
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", 5);
        ResolvedMethodRef resolvedMethodRef1 = this.addHelperMethod(
                programClass1,
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;",
                new ArrayList(),
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                false,
                list1,
                observableHolder,
                inheritedMemberAnalyzer,
                classRepository1
        );
        ArrayList arrayList = new ArrayList();
        this.buildFieldLookupCode(arrayList, localVariableList1, resolvedMethodRef1, resolvedMethodRef, list1, constantPool1, classRepository1, classResolver1);
        ((MethodBytecode) observableHolder.getValue()).replaceInstructions(arrayList);
        return resolvedMethodRef1;
    }

    public String buildMemberDescriptor(ClassMemberRef classMemberRef, Map map1, Map map2) {
        StringBuilder stringBuilder = new StringBuilder();
        String string = classMemberRef.getOwnerClass().getClassName();
        Integer integer = (Integer) map1.get(string.replace('/', '.'));
        if (integer != null) {
            stringBuilder.append(String.valueOf(Long.toString((Long) map2.get(integer), 36)));
        } else {
            stringBuilder.append(string);
        }

        stringBuilder.append('\b');
        stringBuilder.append(classMemberRef.getName());
        if (!classMemberRef.isField()) {
            stringBuilder.append('\b');
            String string1 = classMemberRef.getDescriptor();
            Iterator iterator = ConstantPoolEntry.getParameterClassForNames(string1).iterator();

            while (iterator.hasNext()) {
                String string2 = (String) iterator.next();
                this.appendTypeName(string2, stringBuilder, map1, map2, true);
            }

            this.appendTypeName(MethodSignature.getReturnPart(string1), stringBuilder, map1, map2, true);
        } else {
            stringBuilder.append('\b');
            String string3 = classMemberRef.getDescriptor();
            this.appendTypeName(string3, stringBuilder, map1, map2, false);
        }

        return stringBuilder.toString();
    }

    public ResolvedMethodRef createMethodResolverMethod(
            boolean bl,
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            ResolvedMethodRef resolvedMethodRef1,
            ResolvedMethodRef resolvedMethodRef2,
            ResolvedMethodRef resolvedMethodRef3,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantLong constantLong,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        String string2;
        BooleanFlag booleanFlag;
        if (this.twoLongKeysFlag.getValue()) {
            string2 = "(JJ)Ljava/lang/reflect/Method;";
            booleanFlag = this.twoLongKeysFlag;
        } else {
            string2 = "(J)Ljava/lang/reflect/Method;";
            booleanFlag = this.twoLongKeysFlag;
        }

        int ba = booleanFlag.getValue() ? 20 : 18;
        String string = string2;
        int bb = ba;
        String string1 = string;
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, bb);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        this.buildMethodResolverCode(
                arrayList,
                localVariableList1,
                resolvedMethodRef,
                resolvedFieldRef,
                resolvedFieldRef1,
                resolvedMethodRef1,
                resolvedMethodRef2,
                resolvedMethodRef3,
                list1,
                constantLong,
                mutableInt1,
                mutableInt,
                constantPool1,
                classRepository1,
                classResolver1
        );
        return this.addGeneratedMethod(
                programClass1,
                this.twoLongKeysFlag.getValue() ? "(JJ)Ljava/lang/reflect/Method;" : "(J)Ljava/lang/reflect/Method;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                bl,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public int buildArgumentPackerCode(
            String string,
            List list1,
            LocalVariableList localVariableList1,
            MutableInt mutableInt,
            List list2,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        int ba = 0;
        mutableInt.setValue(4);
        boolean bl1 = false;
        boolean bl2 = false;
        boolean bl3 = false;
        List list3 = ConstantPoolEntry.getParameterTypes(string);
        list1.add(Instruction.createIntPush(list3.size()));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Object", list2);
        list1.add(new ConstantRefInstruction(189, resolvedClassConstant));

        for (int i = 0; i < list3.size(); i++) {
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntPush(i));
            String string1 = (String) list3.get(i);
            if (string1.length() > 1) {
                list1.add(Instruction.createObjectLoad(ba, localVariableList1, 4));
            } else {
                bl2 = bl2 || !bl;
                if (string1.equals("I")) {
                    PrimitiveBoxingGenerator.boxIntLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1, 4);
                } else if (string1.equals("B")) {
                    PrimitiveBoxingGenerator.boxByteLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1);
                } else if (string1.equals("Z")) {
                    PrimitiveBoxingGenerator.boxBooleanLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1, 4);
                } else if (string1.equals("S")) {
                    PrimitiveBoxingGenerator.boxShortLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1);
                } else if (string1.equals("C")) {
                    PrimitiveBoxingGenerator.boxCharLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1);
                } else if (string1.equals("J")) {
                    bl1 = true;
                    bl3 = !bl;
                    PrimitiveBoxingGenerator.boxLongLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1, 4);
                    ba++;
                } else if (string1.equals("F")) {
                    PrimitiveBoxingGenerator.boxFloatLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1, 4);
                } else if (string1.equals("D")) {
                    bl1 = true;
                    bl3 = !bl;
                    PrimitiveBoxingGenerator.boxDoubleLocal(ba, list1, bl, localVariableList1, list2, constantPool1, classMemberLookup1, classResolver1, 4);
                    ba++;
                }
            }

            list1.add(SimpleInstruction.forOpcode(83));
            ba++;
        }

        list1.add(SimpleInstruction.forOpcode(176));
        if (bl1) {
            mutableInt.incrementAndGet();
        }

        if (bl2) {
            mutableInt.addAndGet(2);
        }

        if (bl3) {
            mutableInt.incrementAndGet();
        }

        return ba;
    }

    public boolean usesTwoLongKeys() {
        return this.twoLongKeysFlag.getValue();
    }

    public ResolvedMethodRef createMethodLookupMethod(
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        MutableInt mutableInt = new MutableInt(8);
        MutableInt mutableInt1 = new MutableInt(5);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        LocalVariableList localVariableList1 = new LocalVariableList(
                true, "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;", 10
        );
        ObservableHolder observableHolder = new ObservableHolder();
        ResolvedMethodRef resolvedMethodRef1 = this.addHelperMethod(
                programClass1,
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;I[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                new ArrayList(),
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                false,
                list1,
                observableHolder,
                inheritedMemberAnalyzer,
                classRepository1
        );
        ArrayList arrayList = new ArrayList();
        this.buildMethodLookupCode(arrayList, localVariableList1, resolvedMethodRef1, resolvedMethodRef, list1, constantPool1, classRepository1, classResolver1);
        ((MethodBytecode) observableHolder.getValue()).replaceInstructions(arrayList);
        return resolvedMethodRef1;
    }

    public Set assignLookupClassNames(
            Set set1,
            ObservableHolder observableHolder,
            Map map1,
            Map map2,
            ChangeLogMapping changeLogMapping1,
            String string,
            SetMultiMap setMultiMap,
            SetMultiMap setMultiMap1,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            EnumerableMap enumerableMap2,
            String string1,
            boolean bl,
            ClasspathClassLoader classpathClassLoader1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        Set set2 = set1;
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        HashSet hashSet2 = ZkmUtils.createHashSet();
        set2 = LookupClassFactory.partitionClassesByModule(set2, hashSet1, hashSet2, hashSet);
        int ba = hashSet2.size();
        HashSet hashSet3 = ZkmUtils.createHashSet();
        Object object = null;
        HashSet hashSet4 = ZkmUtils.createHashSet();
        Set set3;
        Object object1;
        ObservableHolder observableHolder2;
        Map map5;
        Map map6;
        String string11;
        if (changeLogMapping1 != null) {
            String string2 = changeLogMapping1.getReferenceObfuscationClass();
            if (string2 != null) {
                String string3 = LookupClassFactory.validateChangeLogLookupName(
                        "ObfuscateReferencesClass:", string2, "obfuscateReferencesPackage", string, "Reference Obfuscation", hashSet, changeLogMapping1, enumerableMap2
                );
                if (string3 != null) {
                    observableHolder.setValue(string3);
                    hashSet3.add(string3);
                    hashSet4.add(ZkmUtils.dotsToSlashes(string3));
                    Iterator iterator = hashSet1.iterator();

                    while (iterator.hasNext()) {
                        ProgramClass programClass1 = (ProgramClass) iterator.next();
                        map2.put(programClass1, string3);
                    }
                }
            }

            Iterator iterator2 = readOnlyMultiMapView.keySet().iterator();

            while (iterator2.hasNext()) {
                SourceArchive sourceArchive1 = (SourceArchive) iterator2.next();
                String string9 = sourceArchive1.getModuleName();
                String string4 = changeLogMapping1.getModuleReferenceObfuscationClass(string9);
                if (string4 != null) {
                    String string5 = LookupClassFactory.validateChangeLogLookupName(
                            "ObfuscateReferencesClass:",
                            string4,
                            "obfuscateReferencesPackage",
                            string,
                            "Reference Obfuscation",
                            setMultiMap1.getValues(sourceArchive1),
                            changeLogMapping1,
                            enumerableMap2
                    );
                    if (string5 != null) {
                        map1.put(sourceArchive1, string5);
                        hashSet3.add(string5);
                        hashSet4.add(ZkmUtils.dotsToSlashes(string5));
                        List list1 = readOnlyMultiMapView.getValues(sourceArchive1);
                        if (list1 != null) {
                            Iterator iterator1 = list1.iterator();

                            while (iterator1.hasNext()) {
                                ProgramClass programClass2 = (ProgramClass) iterator1.next();
                                map2.put(programClass2, string5);
                            }
                        }
                    }
                }
            }

            if (ba != 0 && observableHolder.isValueNull()) {
                set3 = set2;
                object1 = object;
                observableHolder2 = observableHolder;
                map5 = map1;
                map6 = map2;
                string11 = "obfuscateReferencesPackage";
            } else {
                if (readOnlyMultiMapView.getKeyCount() == map1.size()) {
                    LookupClassFactory.assignClassesToLookupNames(observableHolder, map1, hashSet2, map2, readOnlyMultiMapView);
                    return hashSet3;
                }

                set3 = set2;
                object1 = object;
                observableHolder2 = observableHolder;
                map5 = map1;
                map6 = map2;
                string11 = "obfuscateReferencesPackage";
            }
        } else {
            set3 = set2;
            object1 = object;
            observableHolder2 = observableHolder;
            map5 = map1;
            map6 = map2;
            string11 = "obfuscateReferencesPackage";
        }

        ClasspathClassLoader classpathClassLoader2 = classpathClassLoader1;
        List list2 = this.nameDictionary;
        Boolean boolean2 = this.randomizeNames;
        Boolean boolean1 = bl;
        String string8 = string1;
        HashSet hashSet7 = hashSet4;
        EnumerableMap enumerableMap5 = enumerableMap2;
        EnumerableMap enumerableMap4 = enumerableMap1;
        EnumerableMap enumerableMap3 = enumerableMap;
        SetMultiMap setMultiMap3 = setMultiMap1;
        SetMultiMap setMultiMap2 = setMultiMap;
        ReadOnlyMultiMapView readOnlyMultiMapView1 = readOnlyMultiMapView;
        HashSet hashSet6 = hashSet2;
        HashSet hashSet5 = hashSet1;
        String string7 = string;
        String string6 = string11;
        Map map4 = map6;
        Map map3 = map5;
        ObservableHolder observableHolder1 = observableHolder2;
        return LookupClassFactory.assignLookupClassNames(
                set3,
                (String) object1,
                observableHolder1,
                map3,
                map4,
                string6,
                string7,
                hashSet5,
                hashSet6,
                readOnlyMultiMapView1,
                setMultiMap2,
                setMultiMap3,
                enumerableMap3,
                enumerableMap4,
                enumerableMap5,
                hashSet7,
                string8,
                boolean1,
                boolean2,
                list2,
                classpathClassLoader2,
                scriptEnvironment1
        );
    }

    public void buildMethodHandleResolverCode(
            List list1,
            LocalVariableList localVariableList1,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            int[] ba,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedMethodRef resolvedMethodRef1,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(this.usesTwoLongKeys() ? 15 : 13);
        mutableInt.setValue(6);
        LabelInstruction labelInstruction = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 128);
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
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list2);
        exceptionHandlerSpecs[0] = new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction2);
        int bb = this.usesTwoLongKeys() ? 6 : 4;
        int bc = this.usesTwoLongKeys() ? bb + 2 : 6;
        int bd = bc + 1;
        int be = bd + 1;
        int bf = be + 1;
        int bg = bf + 1;
        int bh = bg + 1;
        int bi = bh;
        int bj = bi + 1;
        int bk = bj;
        int bl = bg;
        int bm = bh;
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/String", "charAt", "(I)C", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        if (this.usesTwoLongKeys() && HiddenOptionFlags.XOR_INDY_OPCODE_KEY) {
            list1.add(Instruction.createLongLoad(bb, localVariableList1, 4));
            list1.add(SimpleInstruction.forOpcode(136));
            list1.add(Instruction.createIntPush(7));
            list1.add(SimpleInstruction.forOpcode(126));
            list1.add(SimpleInstruction.forOpcode(130));
        }

        list1.add(Instruction.createIntStore(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(Instruction.createObjectStore(be, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(Instruction.createObjectStore(bf, localVariableList1, 4));
        list1.add(labelInstruction);
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[0]));
        list1.add(new BranchInstruction(159, labelInstruction4));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[1]));
        list1.add(new BranchInstruction(159, labelInstruction4));
        list1.add(labelInstruction3);
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[2]));
        list1.add(new BranchInstruction(159, labelInstruction4));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[3]));
        list1.add(new BranchInstruction(160, labelInstruction8));
        list1.add(labelInstruction4);
        list1.add(Instruction.createLongLoad(4, localVariableList1, 4));
        if (this.usesTwoLongKeys()) {
            list1.add(Instruction.createLongLoad(bb, localVariableList1, 4));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        list1.add(Instruction.createObjectStore(be, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(Instruction.createObjectStore(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        list1.add(Instruction.createObjectStore(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(Instruction.createObjectStore(bj, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[0]));
        list1.add(new BranchInstruction(160, labelInstruction5));
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findGetter",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction11));
        list1.add(labelInstruction5);
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[1]));
        list1.add(new BranchInstruction(160, labelInstruction6));
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findSetter",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction11));
        list1.add(labelInstruction6);
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[2]));
        list1.add(new BranchInstruction(160, labelInstruction7));
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findStaticGetter",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction11));
        list1.add(labelInstruction7);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findStaticSetter",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction11));
        list1.add(labelInstruction8);
        list1.add(Instruction.createLongLoad(4, localVariableList1, 4));
        if (this.usesTwoLongKeys()) {
            list1.add(Instruction.createLongLoad(bb, localVariableList1, 4));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bf, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getDeclaringClass", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(Instruction.createObjectStore(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        list1.add(Instruction.createObjectStore(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant11 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant11));
        ResolvedMethodRefConstant resolvedMethodRefConstant12 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType",
                "methodType",
                "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant12));
        list1.add(Instruction.createObjectStore(bk, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[4]));
        list1.add(new BranchInstruction(160, labelInstruction9));
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant13 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findVirtual",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant13));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction11));
        list1.add(labelInstruction9);
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createIntPush(ba[5]));
        list1.add(new BranchInstruction(160, labelInstruction10));
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant14 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findStatic",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant14));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction11));
        list1.add(labelInstruction10);
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bk, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant15 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles$Lookup",
                "findSpecial",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant15));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(labelInstruction11);
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant16 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "parameterCount", "()I", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant16));
        int bn = this.usesTwoLongKeys() ? 2 : 1;
        list1.add(Instruction.createIntPush(bn));
        list1.add(SimpleInstruction.forOpcode(100));
        list1.add(Instruction.createIntPush(bn));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Class", list2);
        list1.add(new ConstantRefInstruction(189, resolvedClassConstant1));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(0));
        AbstractFieldInfo abstractFieldInfo = classResolver1.getClassFile("java/lang/Long").findField("TYPE", "Ljava/lang/Class;");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef("java/lang/Long", "TYPE", "Ljava/lang/Class;", list2, abstractFieldInfo);
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(SimpleInstruction.forOpcode(83));
        ConstantPool constantPool2;
        String string;
        if (this.usesTwoLongKeys()) {
            list1.add(SimpleInstruction.forOpcode(89));
            list1.add(Instruction.createIntPush(1));
            list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
            list1.add(SimpleInstruction.forOpcode(83));
            constantPool2 = constantPool1;
            string = "java/lang/invoke/MethodHandles";
        } else {
            constantPool2 = constantPool1;
            string = "java/lang/invoke/MethodHandles";
        }

        ResolvedMethodRefConstant resolvedMethodRefConstant17 = constantPool2.getOrAddMethodRef(
                string,
                "dropArguments",
                "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant17));
        list1.add(labelInstruction1);
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction2);
        list1.add(Instruction.createObjectStore(bl, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/StringBuilder", list2);
        list1.add(new TypeInstruction(resolvedClassConstant2));
        list1.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant18 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "<init>", "()V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant18));
        list1.add(Instruction.createObjectStore(bm, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bm, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bl, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant19 = constantPool1.getOrAddMethodRef(
                "java/lang/Object", "getClass", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant19));
        ResolvedMethodRefConstant resolvedMethodRefConstant20 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant20));
        ResolvedMethodRefConstant resolvedMethodRefConstant21 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant21));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(" : ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant21));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction12));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant22 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Field", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant22));
        list1.add(new GotoInstruction(labelInstruction14));
        list1.add(labelInstruction12);
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction13));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant23 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant23));
        list1.add(new GotoInstruction(labelInstruction14));
        list1.add(labelInstruction13);
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(" null ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        list1.add(labelInstruction14);
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant21));
        ResolvedStringConstant resolvedStringConstant2 = constantPool1.addStringConstant(" : ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant2));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant21));
        list1.add(Instruction.createObjectLoad(bl, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant24 = constantPool1.getOrAddMethodRef(
                "java/lang/Exception", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant24));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant21));
        list1.add(SimpleInstruction.forOpcode(87));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list2);
        list1.add(new TypeInstruction(resolvedClassConstant3));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(bm, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant25 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant25));
        ResolvedMethodRefConstant resolvedMethodRefConstant26 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant26));
        list1.add(SimpleInstruction.forOpcode(191));
    }

    public void buildBootstrapCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(5);
        mutableInt.setValue(7);
        LabelInstruction labelInstruction = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 128);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list2);
        exceptionHandlerSpecs[0] = new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction2);
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/invoke/MutableCallSite", list2);
        list1.add(new TypeInstruction(resolvedClassConstant1));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "<init>", "(Ljava/lang/invoke/MethodType;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(3, localVariableList1, 4));
        list1.add(labelInstruction);
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        ResolvedMethodHandleConstant resolvedMethodHandleConstant = constantPool1.getOrAddMethodHandle(
                MethodHandleRefKind.REF_INVOKE_STATIC, resolvedMethodRef, list2
        );
        list1.add(new ConstantRefInstruction(19, resolvedMethodHandleConstant));
        list1.add(Instruction.createClassConstantLoad(constantPool1, list2));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "parameterCount", "()I", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandle", "asCollector", "(Ljava/lang/Class;I)Ljava/lang/invoke/MethodHandle;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(SimpleInstruction.forOpcode(7));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Object", list2);
        list1.add(new ConstantRefInstruction(189, resolvedClassConstant2));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(0));
        list1.add(Instruction.createObjectLoad(0, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(1));
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(2));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntPush(3));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "insertArguments",
                "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodHandles",
                "explicitCastArguments",
                "(Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                list2,
                classMemberLookup1,
                classResolver1
        );
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MutableCallSite", "setTarget", "(Ljava/lang/invoke/MethodHandle;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list1.add(labelInstruction1);
        list1.add(new GotoInstruction(labelInstruction3));
        list1.add(labelInstruction2);
        list1.add(Instruction.createObjectStore(4, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list2);
        list1.add(new TypeInstruction(resolvedClassConstant3));
        list1.add(SimpleInstruction.forOpcode(89));
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("java/lang/StringBuilder", list2);
        list1.add(new TypeInstruction(resolvedClassConstant4));
        list1.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "<init>", "()V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant6));
        list1.add(Instruction.createStringConstantLoad(constantPool1.getClassName(), constantPool1, list2));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(" : ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        list1.add(Instruction.createObjectLoad(1, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant(" : ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        list1.add(Instruction.createObjectLoad(2, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/invoke/MethodType", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        list1.add(Instruction.createObjectLoad(4, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant10));
        list1.add(SimpleInstruction.forOpcode(191));
        list1.add(labelInstruction3);
        list1.add(Instruction.createObjectLoad(3, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public ResolvedMethodRefConstant getTargetExceptionRef(
            ConstantPool constantPool1, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        return constantPool1.getOrAddMethodRef(
                "java/lang/reflect/InvocationTargetException", "getTargetException", "()Ljava/lang/Throwable;", list1, classMemberLookup1, classResolver1
        );
    }

    @Override
    public ProgramClass createSyntheticClass(String string, int ba, int bb, int bc, Object object) throws ZkmException {
        VisitableNode[] visitableNodes1 = ReferenceObfuscationExclusions.getVisitableNodes();

        try {
            ClassFileInputStream classFileInputStream1;
            label24:
            {
                byte[] bd;
                if (ba < 46) {
                    bd = LEGACY_LOOKUP_CLASS_BYTES;
                    if (bb < 0) {
                        VisitableNode[] visitableNodes2 = visitableNodes1;
                        classFileInputStream1 = ClassFileInputStream.fromBytes((byte[]) (Object) (visitableNodes2[0]), (Boolean) (Object) (visitableNodes2[2]));
                        break label24;
                    }
                } else {
                    bd = LOOKUP_CLASS_BYTES;
                }

                Boolean boolean1 = false;
                byte[] be = bd;
                classFileInputStream1 = ClassFileInputStream.fromBytes(be, boolean1);
            }

            ClassFileInputStream classFileInputStream = classFileInputStream1;
            InputFileLocation inputFileLocation = InputFileLocation.createNamedPlaceholder("ReferenceObfuscation." + string);
            ObservableHolder observableHolder = new ObservableHolder();
            ListenerRegistry listenerRegistry1 = new ListenerRegistry();
            PrintWriter printWriter = new PrintWriter(new StringWriter());
            new PrintWriter(new StringWriter());
            ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap();
            ProgramClass programClass1 = new ProgramClass(
                    classFileInputStream, inputFileLocation, observableHolder, listenerRegistry1, printWriter, threeKeyMultiMap, 4
            );
            programClass1.renameClass(string, ZkmUtils.createHashMap(13));
            programClass1.setMajorVersion(ba);
            programClass1.setMinorVersion(bc);
            return programClass1;
        } catch (IOException iOException) {
            throw new ReferenceObfuscationException("ObfuscateReferences (A) : " + iOException.getMessage(), iOException);
        } catch (ZkmProcessingException zkmProcessingException) {
            throw new ReferenceObfuscationException("ObfuscateReferences (B) : " + zkmProcessingException.getMessage(), zkmProcessingException);
        }
    }

    public ResolvedMethodRefConstant getFieldSetterRef(
            ResolvedFieldRef resolvedFieldRef, ConstantPool constantPool1, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        String string = resolvedFieldRef.getDescriptor();
        ConstantPool constantPool2;
        String string1;
        if (!string.startsWith("[")) {
            if (!string.startsWith("L") || !string.endsWith(";")) {
                if (string.equals("Z")) {
                    return constantPool1.getOrAddMethodRef(
                            "java/lang/reflect/Field", "setBoolean", "(Ljava/lang/Object;Z)V", list1, classMemberLookup1, classResolver1
                    );
                }

                if (string.equals("B")) {
                    return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "setByte", "(Ljava/lang/Object;B)V", list1, classMemberLookup1, classResolver1);
                }

                if (string.equals("S")) {
                    return constantPool1.getOrAddMethodRef(
                            "java/lang/reflect/Field", "setShort", "(Ljava/lang/Object;S)V", list1, classMemberLookup1, classResolver1
                    );
                }

                if (string.equals("C")) {
                    return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "setChar", "(Ljava/lang/Object;C)V", list1, classMemberLookup1, classResolver1);
                }

                if (string.equals("I")) {
                    return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "setInt", "(Ljava/lang/Object;I)V", list1, classMemberLookup1, classResolver1);
                }

                if (string.equals("J")) {
                    return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "setLong", "(Ljava/lang/Object;J)V", list1, classMemberLookup1, classResolver1);
                }

                if (string.equals("F")) {
                    return constantPool1.getOrAddMethodRef(
                            "java/lang/reflect/Field", "setFloat", "(Ljava/lang/Object;F)V", list1, classMemberLookup1, classResolver1
                    );
                }

                if (string.equals("D")) {
                    return constantPool1.getOrAddMethodRef(
                            "java/lang/reflect/Field", "setDouble", "(Ljava/lang/Object;D)V", list1, classMemberLookup1, classResolver1
                    );
                }

                return constantPool1.getOrAddMethodRef(
                        "java/lang/reflect/Field", "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", list1, classMemberLookup1, classResolver1
                );
            }

            constantPool2 = constantPool1;
            string1 = "java/lang/reflect/Field";
        } else {
            constantPool2 = constantPool1;
            string1 = "java/lang/reflect/Field";
        }

        return constantPool2.getOrAddMethodRef(string1, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", list1, classMemberLookup1, classResolver1);
    }

    public static boolean needsReflectionInvoker(
            ProgramClass programClass1, ProgramClass programClass2, ClassMemberRef classMemberRef, ClassHierarchyQuery classHierarchyQuery
    ) {
        if (classMemberRef.isField()) {
            return false;
        } else if (!HiddenOptionFlags.OBFUSCATE_REFERENCES_INDY) {
            return true;
        } else {
            return !canUseInvokedynamic(classMemberRef, classHierarchyQuery) ? true : !bothSupportInvokedynamic(programClass1, programClass2);
        }
    }

    public Map assignTypeIndices(ListMultimap listMultimap, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        int ba = 0;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        String string5;
        ClassResolver classResolver2;
        if (listMultimap != null) {
            HashSet hashSet = ZkmUtils.createHashSet();
            Iterator iterator = listMultimap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MethodBytecode methodBytecode1 = (MethodBytecode) entry.getKey();
                Iterator iterator1 = ((List) entry.getValue()).iterator();

                while (iterator1.hasNext()) {
                    ClassMemberRef classMemberRef = (ClassMemberRef) iterator1.next();
                    if (hashSet.add(classMemberRef)) {
                        ClassFileBase classFileBase = classMemberRef.getOwnerClass();
                        if (!linkedHashMap.containsKey(classFileBase)) {
                            linkedHashMap.put(classFileBase, integerCache.valueOf(ba++));
                        }

                        ClassFileBase classFileBase1 = methodBytecode1.getOwningClass();
                        Integer integer = classFileBase1.hasReleaseVersion() ? classFileBase1.getReleaseVersion() : null;
                        if (!classMemberRef.isField()) {
                            String string3 = classMemberRef.getDescriptor();
                            List list1 = ConstantPoolEntry.getParameterClassForNames(string3);
                            Iterator iterator2 = list1.iterator();

                            while (iterator2.hasNext()) {
                                String string1 = (String) iterator2.next();
                                NamedTypeRef namedTypeRef1 = this.resolveTypeRef(string1, integer, classResolver1, ignoreMissingReferencesSpec1);
                                if (!linkedHashMap.containsKey(namedTypeRef1)) {
                                    linkedHashMap.put(namedTypeRef1, ba++);
                                }
                            }

                            String string4 = MethodSignature.getReturnPart(string3);
                            NamedTypeRef namedTypeRef2 = this.resolveTypeRef(string4, integer, classResolver1, ignoreMissingReferencesSpec1);
                            if (!linkedHashMap.containsKey(namedTypeRef2)) {
                                linkedHashMap.put(namedTypeRef2, ba++);
                            }
                        } else {
                            String string = classMemberRef.getDescriptor();
                            NamedTypeRef namedTypeRef = this.resolveTypeRef(string, integer, classResolver1, ignoreMissingReferencesSpec1);
                            if (!linkedHashMap.containsKey(namedTypeRef)) {
                                linkedHashMap.put(namedTypeRef, ba++);
                            }
                        }
                    }
                }
            }

            classResolver2 = classResolver1;
            short bc = 668;
            string5 = "java/lang/Object";
        } else {
            classResolver2 = classResolver1;
            short bb = 668;
            string5 = "java/lang/Object";
        }

        String string2 = string5;
        ClassFileBase classFileBase2 = classResolver2.getClassFile(string2);
        if (!linkedHashMap.containsKey(classFileBase2)) {
            linkedHashMap.put(classFileBase2, ba);
        }

        return linkedHashMap;
    }

    public void buildMethodResolverCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedMethodRef resolvedMethodRef,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            ResolvedMethodRef resolvedMethodRef1,
            ResolvedMethodRef resolvedMethodRef2,
            ResolvedMethodRef resolvedMethodRef3,
            List list2,
            ConstantLong constantLong,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(this.twoLongKeysFlag.getValue() ? 20 : 18);
        mutableInt.setValue(5);
        int bb = this.twoLongKeysFlag.getValue() ? 2 : 0;
        int bc = bb + 2;
        int bd = bc + 1;
        int be = bd + 1;
        int bf = be + 1;
        int bg = bf + 1;
        int bh = bg + 1;
        int bi = bh + 1;
        int bj = bi + 1;
        int bk = bj + 1;
        int bl = bk + 1;
        int bm = bl + 1;
        int bn = bm + 1;
        int bo = bn + 1;
        int bp = bo + 1;
        int bq = bo;
        int br = bp;
        int bs = bp;
        int bt = bp + 1;
        int bu = bt + 1;
        int bv = bt;
        int bw = bu;
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
        list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(Instruction.createLongLoad(bb, localVariableList1, 4));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        list1.add(Instruction.createIntStore(bc, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        LocalVariableList localVariableList2 = localVariableList1;
        int ba = bd;
        list1.add(Instruction.createObjectLoad(ba, localVariableList2, 4));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new ConstantRefInstruction(193, resolvedClassConstant));
        list1.add(new BranchInstruction(153, labelInstruction14));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(be, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(new BipushInstruction(8));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/String", "indexOf", "(I)I", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createIntStore(bf, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "substring", "(II)Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(new BipushInstruction(36));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Long", "parseLong", "(Ljava/lang/String;I)J", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bg, localVariableList1, 4));
        list1.add(Instruction.createIntIncrement(bf, 1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(new BipushInstruction(8));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "indexOf", "(II)I", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(Instruction.createIntStore(bh, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bf, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(Instruction.createObjectStore(bi, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(2));
        list1.add(Instruction.createIntStore(bj, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 4));
        list1.add(Instruction.createIntStore(bk, localVariableList1, 4));
        list1.add(labelInstruction);
        list1.add(Instruction.createIntIncrement(bj, 1, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(new BipushInstruction(8));
        list1.add(Instruction.createIntIncrement(bk, 1, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntStore(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(2));
        list1.add(new BranchInstruction(163, labelInstruction));
        list1.add(Instruction.createIntLoad(bj, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(SimpleInstruction.forOpcode(100));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createIntStore(bl, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Class", list2);
        list1.add(new ConstantRefInstruction(189, resolvedClassConstant1));
        list1.add(Instruction.createObjectStore(bm, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(1));
        list1.add(Instruction.createObjectStore(bn, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(SimpleInstruction.forOpcode(96));
        list1.add(Instruction.createIntStore(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bo, localVariableList1, 4));
        list1.add(labelInstruction1);
        list1.add(Instruction.createIntLoad(bo, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bj, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction3));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(new BipushInstruction(8));
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        list1.add(Instruction.createIntStore(bp, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(be, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bp, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
        list1.add(new BipushInstruction(36));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bn, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bo, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction2));
        list1.add(Instruction.createObjectLoad(bm, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bo, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bn, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(labelInstruction2);
        list1.add(Instruction.createIntLoad(bp, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(4));
        list1.add(SimpleInstruction.forOpcode(96));
        list1.add(Instruction.createIntStore(bk, localVariableList1, 4));
        list1.add(Instruction.createIntIncrement(bo, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction1));
        list1.add(labelInstruction3);
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectStore(bq, localVariableList1, 4));
        list1.add(labelInstruction4);
        list1.add(Instruction.createObjectLoad(bq, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bn, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bm, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef2));
        list1.add(Instruction.createObjectStore(br, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(br, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction5));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(br, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(Instruction.createObjectLoad(br, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction5);
        localVariableList2 = localVariableList1;
        ba = bq;
        list1.add(Instruction.createObjectLoad(ba, localVariableList2, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getName", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant("java.lang.Object", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "equals", "(Ljava/lang/Object;)Z", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list1.add(new BranchInstruction(154, labelInstruction6));
        list1.add(Instruction.createObjectLoad(bq, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectStore(bq, localVariableList1, 4));
        list1.add(new BranchInstruction(199, labelInstruction4));
        list1.add(new ConstantRefInstruction(20, constantLong));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bq, localVariableList1, 4));
        list1.add(labelInstruction6);
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createObjectStore(bq, localVariableList1, 4));
        list1.add(labelInstruction7);
        list1.add(Instruction.createObjectLoad(bq, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectStore(bs, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction10));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bt, localVariableList1, 4));
        list1.add(labelInstruction8);
        list1.add(Instruction.createIntLoad(bt, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bs, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(new BranchInstruction(162, labelInstruction10));
        list1.add(Instruction.createObjectLoad(bs, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bt, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bn, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bm, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(184, resolvedMethodRef3));
        list1.add(Instruction.createObjectStore(bu, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bu, localVariableList1, 4));
        list1.add(new BranchInstruction(198, labelInstruction9));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bu, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(Instruction.createObjectLoad(bu, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(176));
        list1.add(labelInstruction9);
        list1.add(Instruction.createIntIncrement(bt, 1, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction8));
        list1.add(labelInstruction10);
        list1.add(Instruction.createObjectLoad(bq, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        ResolvedStringConstant resolvedStringConstant1 = constantPool1.addStringConstant("java.lang.Object", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant1));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list1.add(new BranchInstruction(154, labelInstruction11));
        list1.add(Instruction.createObjectLoad(bq, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectStore(bq, localVariableList1, 4));
        list1.add(new BranchInstruction(199, labelInstruction7));
        list1.add(new ConstantRefInstruction(20, constantLong));
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(SimpleInstruction.forOpcode(9));
        }

        list1.add(new ConstantRefInstruction(184, resolvedMethodRef1));
        list1.add(Instruction.createObjectStore(bq, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction7));
        list1.add(labelInstruction11);
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("java/lang/StringBuffer", list2);
        list1.add(new TypeInstruction(resolvedClassConstant4));
        list1.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant12 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "<init>", "()V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant12));
        list1.add(Instruction.createObjectStore(bv, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bv, localVariableList1, 4));
        ResolvedStringConstant resolvedStringConstant2 = constantPool1.addStringConstant("NoSuchMethodException in ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant2));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(Instruction.createObjectLoad(bg, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(new BipushInstruction(32));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "append", "(C)Ljava/lang/StringBuffer;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        list1.add(Instruction.createObjectLoad(bn, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(new BipushInstruction(32));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        list1.add(Instruction.createObjectLoad(bi, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(new BipushInstruction(40));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        list1.add(SimpleInstruction.forOpcode(87));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bw, localVariableList1, 4));
        list1.add(labelInstruction12);
        list1.add(Instruction.createIntLoad(bw, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction13));
        list1.add(Instruction.createObjectLoad(bv, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bm, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bw, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(SimpleInstruction.forOpcode(87));
        list1.add(Instruction.createIntIncrement(bw, 1, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bw, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(new BranchInstruction(162, labelInstruction12));
        list1.add(Instruction.createObjectLoad(bv, localVariableList1, 4));
        ResolvedStringConstant resolvedStringConstant3 = constantPool1.addStringConstant(", ", list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant3));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant8));
        list1.add(SimpleInstruction.forOpcode(87));
        list1.add(new GotoInstruction(labelInstruction12));
        list1.add(labelInstruction13);
        list1.add(Instruction.createObjectLoad(bv, localVariableList1, 4));
        list1.add(new BipushInstruction(41));
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant9));
        list1.add(SimpleInstruction.forOpcode(87));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/RuntimeException", list2);
        list1.add(new TypeInstruction(resolvedClassConstant2));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(bv, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant10 = constantPool1.getOrAddMethodRef(
                "java/lang/StringBuffer", "toString", "()Ljava/lang/String;", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant10));
        ResolvedMethodRefConstant resolvedMethodRefConstant11 = constantPool1.getOrAddMethodRef(
                "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant11));
        list1.add(SimpleInstruction.forOpcode(191));
        list1.add(labelInstruction14);
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/lang/reflect/Method", list2);
        list1.add(new ConstantRefInstruction(192, resolvedClassConstant3));
        list1.add(SimpleInstruction.forOpcode(176));
    }

    public ResolvedMethodRef createDeclaredFieldFinderMethod(
            ProgramClass programClass1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        LocalVariableList localVariableList1 = new LocalVariableList(true, "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;", 7);
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildDeclaredFieldFinderCode(arrayList, localVariableList1, list1, mutableInt1, mutableInt, constantPool1, classRepository1, classResolver1);
        return this.addGeneratedMethod(
                programClass1,
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/reflect/Field;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                false,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public ResolvedMethodRef createCallSiteInvokerMethod(
            boolean bl,
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        int ba = this.usesTwoLongKeys() ? 11 : 9;
        int bb = ba;
        LocalVariableList localVariableList1 = new LocalVariableList(
                true,
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/Object;",
                bb
        );
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildCallSiteInvokerCode(
                arrayList, localVariableList1, resolvedMethodRef, list1, mutableInt1, mutableInt, constantPool1, classRepository1, classResolver1
        );
        int value = mutableInt1.getValue();
        int bd = mutableInt.getValue();
        ClassRepository classRepository2 = classRepository1;
        InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer;
        return this.addHelperMethod(
                programClass1,
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/invoke/MutableCallSite;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/Object;",
                arrayList,
                value,
                bd,
                localVariableList1,
                exceptionHandlerSpecs,
                bl,
                list1,
                (ObservableHolder) null,
                inheritedMemberAnalyzer1,
                classRepository2
        );
    }

    public ResolvedMethodRefConstant getFieldGetterRef(
            ResolvedFieldRef resolvedFieldRef,
            BooleanFlag booleanFlag,
            ConstantPool constantPool1,
            List list1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        String string = resolvedFieldRef.getDescriptor();
        if (!string.startsWith("[") && (!string.startsWith("L") || !string.endsWith(";"))) {
            if (string.equals("Z")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getBoolean", "(Ljava/lang/Object;)Z", list1, classMemberLookup1, classResolver1);
            } else if (string.equals("B")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getByte", "(Ljava/lang/Object;)B", list1, classMemberLookup1, classResolver1);
            } else if (string.equals("S")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getShort", "(Ljava/lang/Object;)S", list1, classMemberLookup1, classResolver1);
            } else if (string.equals("C")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getChar", "(Ljava/lang/Object;)C", list1, classMemberLookup1, classResolver1);
            } else if (string.equals("I")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getInt", "(Ljava/lang/Object;)I", list1, classMemberLookup1, classResolver1);
            } else if (string.equals("J")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getLong", "(Ljava/lang/Object;)J", list1, classMemberLookup1, classResolver1);
            } else if (string.equals("F")) {
                return constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getFloat", "(Ljava/lang/Object;)F", list1, classMemberLookup1, classResolver1);
            } else {
                return string.equals("D")
                        ? constantPool1.getOrAddMethodRef("java/lang/reflect/Field", "getDouble", "(Ljava/lang/Object;)D", list1, classMemberLookup1, classResolver1)
                        : constantPool1.getOrAddMethodRef(
                        "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
                );
            }
        } else {
            booleanFlag.setValue(true);
            return constantPool1.getOrAddMethodRef(
                    "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
            );
        }
    }

    public static boolean supportsInvokedynamic(ClassFileBase classFileBase) {
        return HiddenOptionFlags.OBFUSCATE_REFERENCES_INDY && supportsMethodHandles(classFileBase);
    }

    public ResolvedMethodRef createBootstrapMethod(
            boolean bl,
            ProgramClass programClass1,
            ResolvedMethodRef resolvedMethodRef,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[1];
        LocalVariableList localVariableList1 = new LocalVariableList(
                true, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", 5
        );
        ArrayList arrayList = new ArrayList();
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt1 = new MutableInt(0);
        this.buildBootstrapCode(
                arrayList,
                localVariableList1,
                resolvedMethodRef,
                exceptionHandlerSpecs,
                list1,
                mutableInt1,
                mutableInt,
                constantPool1,
                classRepository1,
                classResolver1
        );
        return this.addGeneratedMethod(
                programClass1,
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                arrayList,
                mutableInt1.getValue(),
                mutableInt.getValue(),
                localVariableList1,
                exceptionHandlerSpecs,
                bl,
                list1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public void appendTypeName(String string, StringBuilder stringBuilder, Map map1, Map map2, boolean bl) {
        String string1 = ConstantPoolEntry.stripClassDescriptor(string).replace('/', '.');
        Integer integer = (Integer) map1.get(string1);
        if (integer != null) {
            Long long1 = (Long) map2.get(integer);
            stringBuilder.append(String.valueOf(Long.toString(long1, 36)));
        } else {
            stringBuilder.append(string1);
        }

        if (bl) {
            stringBuilder.append('\b');
        }
    }

    public Map assignMemberRefIndices(
            boolean bl,
            BooleanFlag booleanFlag,
            BooleanFlag booleanFlag1,
            ListMultimap listMultimap,
            int ba,
            Set set1,
            BooleanFlag booleanFlag2,
            BooleanFlag booleanFlag3,
            ClassHierarchyQuery classHierarchyQuery
    ) {
        int bb = ba;
        HashSet hashSet = ZkmUtils.createHashSet();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (listMultimap != null) {
            Iterator iterator = listMultimap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                hashSet.addAll((Collection) entry.getValue());
                if (!supportsInvokedynamic(((MethodBytecode) entry.getKey()).getOwningClass())) {
                    Iterator iterator1 = ((List) entry.getValue()).iterator();

                    while (iterator1.hasNext()) {
                        ClassMemberRef classMemberRef = (ClassMemberRef) iterator1.next();
                        if (!classMemberRef.isField()) {
                            set1.add(classMemberRef);
                        }
                    }
                }
            }

            iterator = hashSet.iterator();

            while (iterator.hasNext()) {
                ClassMemberRef classMemberRef1 = (ClassMemberRef) iterator.next();
                if (!linkedHashMap.containsKey(classMemberRef1)) {
                    linkedHashMap.put(classMemberRef1, integerCache.valueOf(bb++));
                    if (classMemberRef1.isField()) {
                        booleanFlag2.setValue(true);
                    } else {
                        booleanFlag3.setValue(true);
                    }
                }

                if (bl && !set1.contains(classMemberRef1) && canUseInvokedynamic(classMemberRef1, classHierarchyQuery)) {
                    booleanFlag.setValue(true);
                } else {
                    booleanFlag1.setValue(true);
                }
            }
        }

        return linkedHashMap;
    }

    public static boolean canUseInvokedynamic(ClassMemberRef classMemberRef, ClassHierarchyQuery classHierarchyQuery) {
        boolean bl;
        if (classMemberRef.isProgramMember()) {
            bl = true;
        } else if (HiddenOptionFlags.OBFUSCATE_JDK_MEMBER_REFERENCES) {
            String string = classMemberRef.getPackagePath();
            MemberInfo memberInfo1 = classMemberRef.getMember();
            ClassFileBase classFileBase = classMemberRef.getOwnerClass();
            if (!string.equals("java/lang/invoke")
                    && (!memberInfo1.isMethod() || !memberInfo1.isNative() || !memberInfo1.getClassName().equals("java/lang/Object"))
                    && (
                    !classFileBase.isInterface()
                            || !memberInfo1.isMethod()
                            || !memberInfo1.isAbstract()
                            || !classHierarchyQuery.isObjectMethodSignature(((AbstractMethodInfo) memberInfo1).getSignature())
            )) {
                bl = true;
            } else {
                bl = false;
            }
        } else {
            bl = false;
        }

        return bl;
    }

    public void buildIndexDecoderCode(
            List list1,
            LocalVariableList localVariableList1,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            int[] ba,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt1.setValue(this.twoLongKeysFlag.getValue() ? 11 : 9);
        mutableInt.setValue(6);
        int bb = this.twoLongKeysFlag.getValue() ? 2 : 0;
        int bc = bb + 2;
        int bd = bc + 1;
        int be = bd + 1;
        int bf = be + 1;
        int bg = bf + 1;
        int bh = bg + 1;
        int bi = bh + 1;
        int bj = bg;
        int bk = bh;
        int bl = bi;
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
        list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
        List list3;
        byte bq;
        if (this.twoLongKeysFlag.getValue()) {
            list1.add(Instruction.createLongLoad(bb, localVariableList1, 4));
            list1.add(Instruction.createIntPush(48));
            list1.add(SimpleInstruction.forOpcode(121));
            list1.add(Instruction.createLongLoad(bb, localVariableList1, 4));
            list1.add(SimpleInstruction.forOpcode(129));
            list1.add(SimpleInstruction.forOpcode(131));
            list1.add(Instruction.createLongStore(0, localVariableList1, 4));
            list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
            list3 = list1;
            long bp = 24085842137881L;
            bq = 46;
        } else {
            list3 = list1;
            long bo = 24085842137881L;
            bq = 46;
        }

        byte bn = bq;
        list3.add(Instruction.createIntPush(bn));
        list1.add(SimpleInstruction.forOpcode(125));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(Instruction.createIntStore(bc, localVariableList1, 4));
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(new BranchInstruction(198, labelInstruction));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(172));
        list1.add(labelInstruction);
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(50));
        list1.add(Instruction.createObjectStore(bd, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new ConstantRefInstruction(193, resolvedClassConstant));
        list1.add(new BranchInstruction(154, labelInstruction2));
        list1.add(labelInstruction1);
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(172));
        list1.add(labelInstruction2);
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(be, localVariableList1, 4));
        list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
        list1.add(Instruction.createIntPush(42));
        list1.add(SimpleInstruction.forOpcode(125));
        ConstantLong constantLong = constantPool1.getOrAddLongConstant(63L, list2);
        list1.add(new ConstantRefInstruction(20, constantLong));
        list1.add(SimpleInstruction.forOpcode(127));
        list1.add(SimpleInstruction.forOpcode(136));
        LabelInstruction[] labelInstructions = new LabelInstruction[]{
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
                labelInstruction65
        };
        list1.add(new TableSwitchInstruction(labelInstruction66, labelInstructions.length - 1, labelInstructions));

        for (int i = 0; i < labelInstructions.length; i += 1) {
            list1.add(labelInstructions[i]);
            list1.add(Instruction.createIntPush(ba[i]));
            list1.add(new GotoInstruction(labelInstruction67));
        }

        list1.add(labelInstruction66);
        list1.add(Instruction.createIntPush(ba[63]));
        list1.add(labelInstruction67);
        list1.add(Instruction.createIntStore(be, localVariableList1, 4));
        list1.add(Instruction.createIntPush(6));
        list1.add(new NewArrayInstruction(10));
        list1.add(Instruction.createObjectStore(bf, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bg, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction70));
        list1.add(labelInstruction68);
        list1.add(Instruction.createIntPush(7));
        list1.add(SimpleInstruction.forOpcode(8));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(100));
        list1.add(SimpleInstruction.forOpcode(104));
        list1.add(Instruction.createIntStore(bh, localVariableList1, 4));
        list1.add(Instruction.createLongLoad(0, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bh, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(125));
        ConstantLong constantLong1 = constantPool1.getOrAddLongConstant(127L, list2);
        list1.add(new ConstantRefInstruction(20, constantLong1));
        list1.add(SimpleInstruction.forOpcode(127));
        list1.add(SimpleInstruction.forOpcode(136));
        list1.add(Instruction.createIntStore(bi, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bi, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(be, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(100));
        list1.add(Instruction.createIntStore(bi, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bi, localVariableList1, 4));
        list1.add(new BranchInstruction(156, labelInstruction69));
        list1.add(Instruction.createIntIncrement(bi, 128, localVariableList1, 4));
        list1.add(labelInstruction69);
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bi, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(79));
        list1.add(Instruction.createIntIncrement(bg, 1, localVariableList1, 4));
        list1.add(labelInstruction70);
        list1.add(Instruction.createIntLoad(bg, localVariableList1, 4));
        list1.add(Instruction.createIntPush(6));
        list1.add(new BranchInstruction(161, labelInstruction68));
        list1.add(labelInstruction71);
        list1.add(Instruction.createObjectLoad(bd, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new ConstantRefInstruction(192, resolvedClassConstant1));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/String", "toCharArray", "()[C", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        list1.add(Instruction.createObjectStore(bj, localVariableList1, 4));
        list1.add(labelInstruction75);
        list1.add(SimpleInstruction.forOpcode(3));
        list1.add(Instruction.createIntStore(bk, localVariableList1, 4));
        list1.add(new GotoInstruction(labelInstruction73));
        list1.add(labelInstruction72);
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bf, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(SimpleInstruction.forOpcode(112));
        list1.add(SimpleInstruction.forOpcode(46));
        list1.add(Instruction.createIntStore(bl, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(new BranchInstruction(153, labelInstruction74));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(52));
        list1.add(Instruction.createIntLoad(bl, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(130));
        list1.add(SimpleInstruction.forOpcode(146));
        list1.add(SimpleInstruction.forOpcode(85));
        list1.add(Instruction.createIntIncrement(bk, 1, localVariableList1, 4));
        list1.add(labelInstruction73);
        list1.add(Instruction.createIntLoad(bk, localVariableList1, 4));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(190));
        list1.add(new BranchInstruction(161, labelInstruction72));
        list1.add(labelInstruction74);
        list1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/String", list2);
        list1.add(new TypeInstruction(resolvedClassConstant2));
        list1.add(SimpleInstruction.forOpcode(89));
        list1.add(Instruction.createObjectLoad(bj, localVariableList1, 4));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "<init>", "([C)V", list2, classMemberLookup1, classResolver1
        );
        list1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        list1.add(SimpleInstruction.forOpcode(83));
        list1.add(Instruction.createIntLoad(bc, localVariableList1, 4));
        list1.add(SimpleInstruction.forOpcode(172));
    }

    public void buildReferenceTableInit(
            ProgramClass programClass1,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedFieldRef resolvedFieldRef1,
            MethodInfo methodInfo1,
            Map map1,
            Map map2,
            int[] ba,
            Map map3,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            MethodKeyInjector methodKeyInjector,
            ClassRepository classRepository1,
            ClassResolver classResolver1,
            List list1,
            ConstantPool constantPool1
    ) throws ZkmException, IOException {
        int bb = map1.size() + map2.size();
        int bc = 65535 - constantPool1.getEntryCount() - list1.size() - 100;
        if (!HiddenOptionFlags.REFERENCE_OBFUSCATION_METHOD_KEYS && bb > bc) {
            throw new ZkmProcessingException(
                    "Reference Obfuscation : ConstantPool length too large in class '"
                            + programClass1.getLocationName()
                            + "' : "
                            + bb
                            + " > "
                            + bc
                            + " : "
                            + constantPool1.getEntryCount()
                            + " : Consider limiting the number of references being obscured or using 'inReferencingClasses' setting. (A)"
            );
        }

        if (methodKeyInjector != null) {
            methodKeyInjector.initializeForMethod(methodInfo1, 1);
        }

        String string = methodKeyInjector == null ? "()V" : "(J)V";
        ArrayList arrayList = new ArrayList();
        this.buildReferenceTableChunks(
                string,
                arrayList,
                map1,
                map2,
                ba,
                map3,
                resolvedFieldRef,
                resolvedFieldRef1,
                methodKeyInjector,
                classRepository1,
                classResolver1,
                list1,
                constantPool1
        );
        ArrayList arrayList1 = new ArrayList();
        if (methodKeyInjector != null) {
            arrayList1.addAll(methodKeyInjector.buildKeyInitInstructions(methodInfo1, list1, inheritedMemberAnalyzer, 4));
        }

        Instruction instruction1 = Instruction.createIntConstantPush(bb, constantPool1, list1);
        arrayList1.add(instruction1);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Object", list1);
        arrayList1.add(new ConstantRefInstruction(189, resolvedClassConstant));
        arrayList1.add(new ConstantRefInstruction(179, resolvedFieldRef));
        Instruction instruction2 = Instruction.createIntConstantPush(bb, constantPool1, list1);
        arrayList1.add(instruction2);
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/String", list1);
        arrayList1.add(new ConstantRefInstruction(189, resolvedClassConstant1));
        arrayList1.add(new ConstantRefInstruction(179, resolvedFieldRef1));
        int bd = methodKeyInjector == null ? 0 : 2;
        LocalVariableList localVariableList1 = methodInfo1.getLocalVariableList();

        for (int i = 0; i < arrayList.size(); i++) {
            InstructionListBuffer instructionListBuffer = (InstructionListBuffer) arrayList.get(i);
            MethodInfo methodInfo2 = programClass1.createUniquelyNamedStaticMethod(
                    string,
                    instructionListBuffer.getInstructions(),
                    4,
                    bd,
                    1,
                    instructionListBuffer.getLocalVariables(),
                    new ExceptionHandlerSpec[0],
                    "Reference Obfuscation",
                    list1,
                    inheritedMemberAnalyzer,
                    classRepository1,
                    4
            );
            if (methodKeyInjector != null) {
                Long long1 = methodKeyInjector.createMethodKey(methodInfo2);
                methodKeyInjector.putKeyLocal(methodInfo2, instructionListBuffer.getLocalVariables().getSlotAt(0));
                long bf = methodKeyInjector.getMethodKey(methodInfo1);
                arrayList1.add(Instruction.createLongLoad(methodKeyInjector.getKeyLocal(methodInfo1).getIndex(), localVariableList1, 4));
                long bg = methodKeyInjector.getKeyValueAt(i);
                long bh = long1 ^ bf ^ bg;
                arrayList1.add(Instruction.createLongConstantLoad(bh, constantPool1, list1));
                arrayList1.add(SimpleInstruction.forOpcode(131));
            }

            ResolvedMethodRef resolvedMethodRef = programClass1.addMethodRef(methodInfo2, list1);
            arrayList1.add(new ConstantRefInstruction(184, resolvedMethodRef));
        }

        methodInfo1.insertInstructionsAtStart(arrayList1, 0, "Reference Obfuscation");
    }

    public ResolvedMethodRef addGeneratedMethod(
            ProgramClass programClass1,
            String string,
            ArrayList arrayList,
            int ba,
            int bb,
            LocalVariableList localVariableList1,
            ExceptionHandlerSpec[] exceptionHandlerSpecs,
            boolean bl,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassRepository classRepository1
    ) throws ZkmException, IOException {
        ClassRepository classRepository2 = classRepository1;
        InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer;
        return this.addHelperMethod(
                programClass1,
                string,
                arrayList,
                ba,
                bb,
                localVariableList1,
                exceptionHandlerSpecs,
                bl,
                list1,
                (ObservableHolder) null,
                inheritedMemberAnalyzer1,
                classRepository2
        );
    }

    public boolean isWideField(ResolvedFieldRef resolvedFieldRef) {
        String string = resolvedFieldRef.getDescriptor();
        if (string.length() == 1) {
            switch (string.charAt(0)) {
                case 'B':
                case 'C':
                case 'F':
                case 'I':
                case 'S':
                case 'Z':
                    return false;
                case 'D':
                case 'J':
                    return true;
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
                    return true;
            }
        } else {
            return false;
        }
    }
}
