package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.attribute.AppendFrame;
import com.zelix.klassmaster.classfile.attribute.StackMapFrameKind;
import com.zelix.klassmaster.classfile.attribute.StackMapTableAttribute;
import com.zelix.klassmaster.classfile.attribute.StackMapTableFrame;
import com.zelix.klassmaster.classfile.attribute.VerificationTypeInfo;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NonNullList;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StackMapTableBuilder {
    public static boolean[][][] maskCombinationCache = new boolean[21][][];
    private final NonNullList instructions;
    public final MethodBytecode methodBytecode;
    public final MethodFlowAnalyzer flowAnalyzer;
    private final StackFrameState[] frameStates;
    public final String methodDescription;

    public int findLastNonTopIndex(VerifierType[] verifierTypes) {
        int ba = -1;

        for (int i = verifierTypes.length - 1; i >= 0; i += -1) {
            if (!verifierTypes[i].isTop()) {
                ba = i;
                break;
            }
        }

        return ba;
    }

    public static boolean[][] pruneFixedLocalCombinations(int ba, boolean[] bl, boolean[][] bl1) {
        boolean[][] bl2 = bl1;

        for (int i = 0; i < bl.length; i++) {
            if (bl[i]) {
                boolean[][] bl3 = new boolean[bl2.length / 2][ba];
                int bc = (int) Math.pow(2.0, i + 1);
                int bd = bl1.length / bc;
                boolean bl4 = true;
                int be = 0;
                int bf = 1;

                for (int j = 0; j < bl2.length; j++) {
                    if (bl4) {
                        System.arraycopy(bl2[j], 0, bl3[be], 0, ba);
                        be++;
                    }

                    if (++bf > bd) {
                        bl4 = !bl4;
                        bf = 1;
                    }
                }

                bl2 = bl3;
            }
        }

        return bl2;
    }

    public StackMapTableBuilder(MethodBytecode methodBytecode1, MethodFlowAnalyzer methodFlowAnalyzer) throws ZkmException, IOException {
        this.instructions = methodBytecode1.getInstructions();
        this.methodBytecode = methodBytecode1;
        this.flowAnalyzer = methodFlowAnalyzer;
        if (HiddenOptionFlags.STACK_MAP_USE_ALL_LOCALS) {
            this.frameStates = methodFlowAnalyzer.computeStackMapFrameStates();
        } else {
            this.frameStates = methodFlowAnalyzer.copyFrameStates();
        }

        this.methodDescription = methodBytecode1.getQualifiedMethodName();
    }

    public StackMapTableFrame[] buildFrames(
            StackMapTableAttribute stackMapTableAttribute, ConstantPool constantPool1, Set set1, List list1, boolean bl, boolean bl1
    ) throws ZkmException, IOException {
        boolean bl3 = bl;
        boolean bl2 = bl1;
        Integer[] integers = this.flowAnalyzer.getFrameLabelIndices();
        int ba = integers.length;
        HashMap hashMap = ZkmUtils.createHashMap();
        HashMap hashMap1 = ZkmUtils.createHashMap();
        StackMapTableFrame[] stackMapTableFrames = new StackMapTableFrame[ba];
        MutableInt mutableInt = new MutableInt(-1);
        ObservableHolder observableHolder = new ObservableHolder();
        ObservableHolder observableHolder1 = new ObservableHolder();
        if (this.frameStates.length > 0) {
            observableHolder.setValue(
                    VerificationTypeInfo.convertVerifierTypes(
                            stackMapTableAttribute, this.frameStates[0].getLocals(), constantPool1, set1, list1, true, hashMap, hashMap1
                    )
            );
            observableHolder1.setValue(new VerificationTypeInfo[0]);
        }

        if (bl3 || bl2) {
            for (int i = 0; i < ba; i++) {
                int bc = integers[i];
                StackFrameState stackFrameState = this.frameStates[bc];
                if (stackFrameState.hasWideLocal()) {
                    bl3 = false;
                    bl2 = false;
                    break;
                }
            }
        }

        StackFrameState stackFrameState2 = this.frameStates[0];

        for (int i = 0; i < ba; i++) {
            int be = integers[i];
            LabelInstruction labelInstruction = (LabelInstruction) this.instructions.get(be);
            StackFrameState stackFrameState1 = this.frameStates[be];
            if (stackFrameState1 == null) {
                ZkmAssert.assertNotNullMessage(
                        stackFrameState1,
                        new String[]{
                                "Dead instruction at ",
                                String.valueOf(be),
                                " : '",
                                this.methodDescription,
                                "' : ",
                                Integer.toHexString(labelInstruction.getUsageBits()),
                                " : ",
                                String.valueOf(labelInstruction.getOffset())
                        }
                );
            }

            if (bl2 && i >= ba - 1) {
                stackMapTableFrames[i] = this.buildCompactFrame(
                        stackMapTableAttribute,
                        labelInstruction,
                        stackFrameState2,
                        stackFrameState1,
                        constantPool1,
                        set1,
                        list1,
                        mutableInt,
                        observableHolder1,
                        observableHolder,
                        hashMap,
                        hashMap1
                );
            } else {
                VerifierType[] verifierTypes;
                if (bl3) {
                    verifierTypes = stackFrameState1.getLocalsMaskedByLiveness();
                } else {
                    verifierTypes = stackFrameState1.getLocals();
                }

                stackMapTableFrames[i] = StackMapTableFrame.createFrame(
                        stackMapTableAttribute,
                        labelInstruction,
                        stackFrameState1.getStack(),
                        verifierTypes,
                        constantPool1,
                        set1,
                        list1,
                        mutableInt,
                        observableHolder1,
                        observableHolder,
                        hashMap,
                        hashMap1
                );
            }

            stackFrameState2 = stackFrameState1;
        }

        return stackMapTableFrames;
    }

    public static boolean[][] getLocalMaskCombinations(int ba) {
        if (maskCombinationCache[ba] == null) {
            int bb = (int) Math.pow(2.0, ba);
            boolean[][] bl = new boolean[bb][ba];

            for (int i = 0; i < ba; i++) {
                int bd = (int) Math.pow(2.0, i + 1);
                int be = bb / bd;
                boolean bl1 = true;
                int bf = 0;

                for (int j = 0; j < bb; j++) {
                    if (++bf > be) {
                        bf = 1;
                        bl1 = !bl1;
                    }

                    bl[j][i] = bl1;
                }
            }

            maskCombinationCache[ba] = bl;
        }

        return maskCombinationCache[ba];
    }

    public StackMapTableFrame buildCompactFrame(
            StackMapTableAttribute stackMapTableAttribute,
            LabelInstruction labelInstruction,
            StackFrameState stackFrameState,
            StackFrameState stackFrameState1,
            ConstantPool constantPool1,
            Set set1,
            List list1,
            MutableInt mutableInt,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            Map map1,
            Map map2
    ) throws ZkmException, IOException {
        VerifierType[] verifierTypes = stackFrameState1.getLocals();
        VerifierType[] verifierTypes1 = stackFrameState1.getLocalsMaskedByLiveness();
        VerifierType[] verifierTypes2 = stackFrameState1.getStack();
        ArrayList arrayList1 = new ArrayList(list1);
        ArrayList arrayList2 = new ArrayList(list1);
        HashSet hashSet = ZkmUtils.createHashSetFrom(set1);
        MutableInt mutableInt2 = new MutableInt(mutableInt.getValue());
        ObservableHolder observableHolder4 = new ObservableHolder(observableHolder.getValue());
        ObservableHolder observableHolder5 = new ObservableHolder(observableHolder1.getValue());
        HashMap hashMap2 = ZkmUtils.copyToHashMap(map1);
        HashMap hashMap = ZkmUtils.copyToHashMap(map2);
        HashMap hashMap1 = hashMap2;
        ObservableHolder observableHolder2 = observableHolder5;
        ObservableHolder observableHolder3 = observableHolder4;
        MutableInt mutableInt1 = mutableInt2;
        ArrayList arrayList = arrayList1;
        StackMapTableFrame stackMapTableFrame = StackMapTableFrame.createFrame(
                stackMapTableAttribute,
                labelInstruction,
                verifierTypes2,
                verifierTypes,
                constantPool1,
                hashSet,
                arrayList,
                mutableInt1,
                observableHolder3,
                observableHolder2,
                hashMap1,
                hashMap
        );
        hashSet = ZkmUtils.createHashSetFrom(set1);
        mutableInt2 = new MutableInt(mutableInt.getValue());
        observableHolder4 = new ObservableHolder(observableHolder.getValue());
        observableHolder5 = new ObservableHolder(observableHolder1.getValue());
        hashMap2 = ZkmUtils.copyToHashMap(map1);
        hashMap = ZkmUtils.copyToHashMap(map2);
        hashMap1 = hashMap2;
        observableHolder2 = observableHolder5;
        observableHolder3 = observableHolder4;
        mutableInt1 = mutableInt2;
        arrayList = arrayList2;
        StackMapTableFrame.createFrame(
                stackMapTableAttribute,
                labelInstruction,
                verifierTypes2,
                verifierTypes1,
                constantPool1,
                hashSet,
                arrayList,
                mutableInt1,
                observableHolder3,
                observableHolder2,
                hashMap1,
                hashMap
        );
        boolean bl = false;
        VerifierType[] verifierTypes3 = new VerifierType[verifierTypes.length];
        System.arraycopy(verifierTypes, 0, verifierTypes3, 0, verifierTypes.length);
        StackMapFrameKind stackMapFrameKind = stackMapTableFrame.getFrameKind();
        if (stackMapFrameKind == StackMapFrameKind.APPEND) {
            int ba = 0;

            for (VerificationTypeInfo verificationTypeInfo : (VerificationTypeInfo[]) observableHolder1.getValue()) {
                ba += verificationTypeInfo.isCategory2() ? 2 : 1;
            }

            int bd = 0;

            for (VerificationTypeInfo verificationTypeInfo1 : ((AppendFrame) stackMapTableFrame).getAppendedLocals()) {
                bd += verificationTypeInfo1.isCategory2() ? 2 : 1;
            }

            for (int i = ba + bd - 1; i >= ba && !stackFrameState1.isLocalLive(i); i += -1) {
                verifierTypes3[i] = verifierTypes1[i];
                bl = true;
            }
        } else if (stackMapFrameKind == StackMapFrameKind.FULL) {
            if (verifierTypes2.length < 2) {
                VerificationTypeInfo[] verificationTypeInfos = (VerificationTypeInfo[]) observableHolder1.getValue();
                VerifierType[] verifierTypes4 = stackFrameState.getLocals();
                int bf = this.findLastNonTopIndex(verifierTypes4);
                int bg = this.findLastNonTopIndex(verifierTypes);
                int bh = -1;
                int bi = 0;

                while (bi <= bf && verifierTypes4[bi] == verifierTypes[bi]) {
                    bh = bi++;
                }

                if (bf < bg && bh == bf) {
                    bi = bg;

                    for (int i = bg; i >= bh + 1 && verifierTypes1[i].isTop(); i += -1) {
                        verifierTypes3[i] = verifierTypes1[i];
                        bi += -1;
                    }

                    if (bi != bf && (verifierTypes2.length != 0 || bi - bf > 3)) {
                        System.arraycopy(verifierTypes, 0, verifierTypes3, 0, verifierTypes.length);
                    } else {
                        bl = true;
                    }
                }
            }

            if (!bl) {
                for (int i = 0; i < verifierTypes.length; i++) {
                    VerifierType verifierType = verifierTypes[i];
                    VerifierType verifierType1 = verifierTypes1[i];
                    if (!verifierType.isUninitializedThis() && !verifierType.isTop() && verifierType1.isTop()) {
                        verifierTypes3[i] = verifierType1;
                        bl = true;
                    }
                }
            }
        }

        return bl
                ? StackMapTableFrame.createFrame(
                stackMapTableAttribute,
                labelInstruction,
                verifierTypes2,
                verifierTypes3,
                constantPool1,
                set1,
                list1,
                mutableInt,
                observableHolder,
                observableHolder1,
                map1,
                map2
        )
                : StackMapTableFrame.createFrame(
                stackMapTableAttribute,
                labelInstruction,
                verifierTypes2,
                verifierTypes,
                constantPool1,
                set1,
                list1,
                mutableInt,
                observableHolder,
                observableHolder1,
                map1,
                map2
        );
    }

    public boolean[][] getReducedMaskCombinations(int ba, Integer[] integers, boolean[][] bl) {
        boolean[] bl1 = this.findLivenessInvariantLocals(ba, integers);
        return pruneFixedLocalCombinations(ba, bl1, bl);
    }

    public StackMapTableFrame[] buildFramesWithMask(
            int ba,
            Integer[] integers,
            boolean[] bl,
            StackMapTableAttribute stackMapTableAttribute,
            ConstantPool constantPool1,
            Set set1,
            List list1,
            Map map1,
            Map map2
    ) throws ZkmException, IOException {
        MutableInt mutableInt = new MutableInt(-1);
        ObservableHolder observableHolder = new ObservableHolder();
        ObservableHolder observableHolder1 = new ObservableHolder();
        observableHolder.setValue(
                VerificationTypeInfo.convertVerifierTypes(stackMapTableAttribute, this.frameStates[0].getLocals(), constantPool1, set1, list1, true, map1, map2)
        );
        observableHolder1.setValue(new VerificationTypeInfo[0]);
        StackMapTableFrame[] stackMapTableFrames = new StackMapTableFrame[integers.length];

        for (int i = 0; i < integers.length; i++) {
            int bc = integers[i];
            LabelInstruction labelInstruction = (LabelInstruction) this.instructions.get(bc);
            StackFrameState stackFrameState = this.frameStates[bc];
            VerifierType[] verifierTypes = stackFrameState.getLocals();
            VerifierType[] verifierTypes1 = stackFrameState.getLocalsMaskedByLiveness();
            VerifierType[] verifierTypes2 = new VerifierType[ba];

            for (int j = 0; j < ba; j++) {
                if (bl[j]) {
                    verifierTypes2[j] = verifierTypes[j];
                } else {
                    verifierTypes2[j] = verifierTypes1[j];
                }

                if (j > 0) {
                    if (verifierTypes2[j].isWideSecondSlot() && !verifierTypes2[j - 1].isWide()) {
                        verifierTypes2[j] = VerifierType.TOP;
                    } else if (verifierTypes2[j - 1].isWide() && !verifierTypes2[j].isWideSecondSlot()) {
                        verifierTypes2[j] = VerifierType.WIDE_SECOND_SLOT;
                    }
                }
            }

            stackMapTableFrames[i] = StackMapTableFrame.createFrame(
                    stackMapTableAttribute,
                    labelInstruction,
                    stackFrameState.getStack(),
                    verifierTypes2,
                    constantPool1,
                    set1,
                    list1,
                    mutableInt,
                    observableHolder1,
                    observableHolder,
                    map1,
                    map2
            );
        }

        return stackMapTableFrames;
    }

    public StackMapTableFrame[] buildSmallestFrames(int ba, StackMapTableAttribute stackMapTableAttribute, ConstantPool constantPool1, Set set1, List list1) throws ZkmException, IOException {
        ZkmAssert.assertTrue(ba <= 20, new String[]{"Variable count : " + ba + ">" + 20, this.methodBytecode.getDisplayLocationName()});
        Integer[] integers = this.flowAnalyzer.getFrameLabelIndices();
        StackMapTableFrame[] stackMapTableFrames = new StackMapTableFrame[integers.length];
        if (integers.length > 0 && this.frameStates.length > 0) {
            boolean[][] bl = getLocalMaskCombinations(ba);
            bl = this.getReducedMaskCombinations(ba, integers, bl);
            if (bl.length > 128) {
                boolean[][] bl1 = new boolean[2][bl[0].length];
                System.arraycopy(bl[0], 0, bl1[0], 0, bl[0].length);
                System.arraycopy(bl[bl.length - 1], 0, bl1[1], 0, bl[1].length);
                bl = bl1;
            }

            int bb = Integer.MAX_VALUE;
            int bc = -1;
            ArrayList arrayList = new ArrayList(list1);
            HashSet hashSet = ZkmUtils.createHashSetFrom(set1);

            for (int i = 0; i < bl.length; i++) {
                HashMap hashMap = ZkmUtils.createHashMap();
                HashMap hashMap1 = ZkmUtils.createHashMap();
                StackMapTableFrame[] stackMapTableFrames2 = new StackMapTableFrame[integers.length];
                StackMapTableFrame[] stackMapTableFrames1 = this.buildFramesWithMask(
                        ba, integers, bl[i], stackMapTableAttribute, constantPool1, hashSet, arrayList, hashMap, hashMap1
                );
                int be = 0;

                for (StackMapTableFrame stackMapTableFrame : stackMapTableFrames1) {
                    be += stackMapTableFrame.getFrameSize();
                }

                if (be < bb) {
                    bb = be;
                    bc = i;
                }
            }

            HashMap hashMap2 = ZkmUtils.createHashMap();
            HashMap hashMap3 = ZkmUtils.createHashMap();
            stackMapTableFrames = this.buildFramesWithMask(ba, integers, bl[bc], stackMapTableAttribute, constantPool1, set1, list1, hashMap2, hashMap3);
        }

        return stackMapTableFrames;
    }

    public boolean[] findLivenessInvariantLocals(int ba, Integer[] integers) {
        boolean[] bl = new boolean[ba];

        for (int i = 0; i < bl.length; i++) {
            bl[i] = true;
        }

        StringBuilder[] stringBuilders1 = new StringBuilder[ba];
        StringBuilder[] stringBuilders = new StringBuilder[ba];

        for (int i = 0; i < integers.length; i++) {
            if (i == 0) {
                for (int j = 0; j < ba; j++) {
                    stringBuilders1[j] = new StringBuilder();
                    stringBuilders[j] = new StringBuilder();
                }
            }

            int bf = integers[i];
            StackFrameState stackFrameState = this.frameStates[bf];
            VerifierType[] verifierTypes = stackFrameState.getLocals();
            VerifierType[] verifierTypes1 = stackFrameState.getLocalsMaskedByLiveness();

            for (int j = 0; j < ba; j++) {
                stringBuilders1[j].append(verifierTypes[j] + ";");
                stringBuilders[j].append(verifierTypes1[j] + ";");
                if (bl[j] && !verifierTypes[j].equals(verifierTypes1[j])) {
                    bl[j] = false;
                }
            }
        }

        return bl;
    }
}
