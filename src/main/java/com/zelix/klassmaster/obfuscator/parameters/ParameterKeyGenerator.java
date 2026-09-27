package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.IdentityHashSet;
import com.zelix.klassmaster.util.IdentityMapWrapper;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.MutableLong;
import com.zelix.klassmaster.util.MutablePair;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.PairMultiMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.PrimitiveIterator.OfLong;
import java.util.stream.LongStream;

public class ParameterKeyGenerator {
    public static int mergeSortDepthLimit;
    public static Iterator indexRandoms;
    public int keyCount;
    public Iterator randomLongs;
    public Set targetNodes;
    public int indexBitOffset;
    public Set usedMiddleValues;
    public Set sourceOnlyLayouts;
    public List allKeyNodes;
    public Set pendingLayoutNodes;
    public int rootPairCount;
    public int initialNodeCount;
    public int minLinkCount;
    public List initialNodes;
    public Set targetOnlyLayouts;
    public Set sourceNodes;
    public final List bitLayouts = new ArrayList();
    public final List sharedKeyPairs = new ArrayList();
    public boolean initialized = false;
    public Random random;
    public final Iterator middleValueIterator;
    public final ListMultimap nodesByLayout;
    public final Set layouts;
    public final RootKeyNode rootNode;

    public void generateInitialNodes(List list1, int ba, boolean bl) {
        boolean bl2 = false;
        this.initialNodes = new ArrayList(this.initialNodeCount);
        this.allKeyNodes = new ArrayList(this.keyCount);
        int[] bb = MethodParameterChanger.createBitPermutation(this.random);
        this.addLayout(bb);
        list1.add(this.getCurrentLayout().clone());
        int bc = 64 * (ba < 3010 ? 1 : 4);
        boolean bl1 = false;
        int bd = -1;
        if (HiddenOptionFlags.LARGE_PARAMETER_KEYS && bl) {
            int be = this.initialNodeCount * 17;
            int bf = Math.min(be, 65035);
            bd = Math.max(200 + this.random.nextInt(100), bf / 34 + this.random.nextInt(100));
        }

        int bn = 0;
        int bq = bn;

        for (int i = this.initialNodeCount; bq < i; i = this.initialNodeCount) {
            int bh = this.random.nextInt(256);
            MutablePair mutablePair = null;
            int bg;
            long bo;
            if (this.initialNodes.size() > 1 && this.random.nextInt(bc) == 0) {
                int bi = this.random.nextInt(this.initialNodes.size());
                LongKeyNode longKeyNode = (LongKeyNode) this.initialNodes.get(bi);
                mutablePair = new MutablePair(longKeyNode);
                bo = longKeyNode.getMixedKey();
                bg = longKeyNode.hashCode();
            } else {
                bo = this.nextMiddleValue();
                bg = this.random.nextInt(256);
            }

            LongKeyNode longKeyNode1 = new LongKeyNode(bh, bo, bg, this.getCurrentLayout(), this.nodesByLayout, this.layouts);
            list1.add(longKeyNode1.getCurrentKey());
            this.initialNodes.add(longKeyNode1);
            if (mutablePair != null) {
                mutablePair.setValue(longKeyNode1);
                this.sharedKeyPairs.add(mutablePair);
            }

            label117:
            {
                List list2 = this.nodesByLayout.getValues(bb);
                int bj = list2 != null ? this.nodesByLayout.getValues(bb).size() : 0;
                if (bj < 64 || this.random.nextInt(128) != 0) {
                    if (bj <= 153) {
                        bl2 = HiddenOptionFlags.LARGE_PARAMETER_KEYS;
                        break label117;
                    }

                    if (this.random.nextInt(32) != 0) {
                        bl2 = HiddenOptionFlags.LARGE_PARAMETER_KEYS;
                        break label117;
                    }
                }

                int[] bk = bb.clone();
                bb = bk;
                this.addLayout(bb);
                list1.add(this.getCurrentLayout().clone());
                bl2 = HiddenOptionFlags.LARGE_PARAMETER_KEYS;
            }

            if (bl2 && bl && !bl1 && bn >= bd) {
                bl1 = true;
                long[] bp = new long[100];
                int bm = 0;
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    if (iterator.next() instanceof Long) {
                        LongKeyNode longKeyNode2 = (LongKeyNode) this.initialNodes.get(bm);
                        bp[bm] = ((LongKeyNode) this.initialNodes.get(bm)).extractBits();
                        bm++;
                    }

                    if (bm > 99) {
                        break;
                    }
                }

                list1.add(bp);
            }

            bq = ++bn;
        }

        this.allKeyNodes.addAll(this.initialNodes);
        bb = MethodParameterChanger.createBitPermutation(this.random);
        this.addLayout(bb);
        list1.add(this.getCurrentLayout().clone());
        sortNodes(this.allKeyNodes);
        bn = 0;
        Iterator iterator1 = this.allKeyNodes.iterator();

        while (iterator1.hasNext()) {
            ((LongKeyNode) iterator1.next()).setIndex(bn++);
        }
    }

    public long encodeIndexKey(int ba) {
        int[] currentLayout = this.getCurrentLayout();
        int bc = 64 - this.indexBitOffset;
        long bd = (Long) indexRandoms.next();
        return MethodParameterChanger.composeIndexKey(ba, bc, bd, currentLayout);
    }

    public int[] getCurrentLayout() {
        return (int[]) this.bitLayouts.get(this.bitLayouts.size() - 1);
    }

    public long nextMiddleValue() {
        return (Long) this.middleValueIterator.next();
    }

    public int getLayoutGroupSize() {
        return 128;
    }

    public void classifyNodes(PairMultiMap pairMultiMap) {
        Map map1 = this.rootNode.getKeyChainMap();
        this.sourceNodes = new IdentityHashSet(ZkmUtils.getPrimeCapacity(map1.size()));
        this.targetNodes = new IdentityHashSet(ZkmUtils.getPrimeCapacity(map1.size()));
        ListMultimap listMultimap = new ListMultimap(true, map1.size());
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            EncryptionKeyChain encryptionKeyChain = (EncryptionKeyChain) entry.getKey();
            EncryptionKeyChain encryptionKeyChain1 = (EncryptionKeyChain) entry.getValue();
            this.sourceNodes.add((LongKeyNode) encryptionKeyChain);
            if (encryptionKeyChain1.isKeyed()) {
                this.targetNodes.add((LongKeyNode) encryptionKeyChain1);
            }

            listMultimap.addValue(encryptionKeyChain1, (LongKeyNode) encryptionKeyChain);
        }

        IdentityHashSet identityHashSet = new IdentityHashSet(this.sourceNodes.size());
        Iterator iterator3 = this.sourceNodes.iterator();

        while (iterator3.hasNext()) {
            EncryptionKeyChain encryptionKeyChain2 = (EncryptionKeyChain) iterator3.next();
            if (!this.targetNodes.contains(encryptionKeyChain2)) {
                identityHashSet.add(encryptionKeyChain2);
            }
        }

        IdentityHashSet identityHashSet1 = new IdentityHashSet(this.targetNodes.size());
        Iterator iterator4 = this.targetNodes.iterator();

        while (iterator4.hasNext()) {
            EncryptionKeyChain encryptionKeyChain3 = (EncryptionKeyChain) iterator4.next();
            if (!this.sourceNodes.contains(encryptionKeyChain3)) {
                identityHashSet1.add(encryptionKeyChain3);
            }
        }

        IdentityHashSet identityHashSet2 = new IdentityHashSet(this.sharedKeyPairs.size() * 2);
        Iterator iterator5 = this.sharedKeyPairs.iterator();

        while (iterator5.hasNext()) {
            MutablePair mutablePair = (MutablePair) iterator5.next();
            identityHashSet2.add(mutablePair.getKey());
            identityHashSet2.add(mutablePair.getValue());
        }

        IdentityHashSet identityHashSet3 = new IdentityHashSet();
        IdentityHashSet identityHashSet4 = new IdentityHashSet();
        Iterator iterator1 = pairMultiMap.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry1 = (Entry) iterator1.next();
            String string = (String) entry1.getKey();
            List list1 = (List) entry1.getValue();
            int ba = list1.size();
            if (ba > 1) {
                identityHashSet4.clear();
                LongKeyNode longKeyNode = null;

                for (int i = 0; i < ba; i++) {
                    ObjectPair objectPair = (ObjectPair) list1.get(i);
                    LongKeyNode longKeyNode1 = (LongKeyNode) objectPair.getFirst();
                    if (i == 0) {
                        longKeyNode = longKeyNode1;
                    }

                    if (i > 0 && longKeyNode1 != longKeyNode) {
                        identityHashSet3.add(longKeyNode1);
                    }

                    if (identityHashSet2.contains(longKeyNode1)) {
                        identityHashSet4.add(longKeyNode1);
                    }
                }

                if (identityHashSet4.size() > 1) {
                }
            }
        }

        int bc = 0;
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator2 = this.layouts.iterator();

        while (iterator2.hasNext()) {
            int[] bd = (int[]) iterator2.next();
            List list2 = this.nodesByLayout.getValues(bd);
            stringBuilder.append("#" + bc++ + " (" + System.identityHashCode(bd) + ")\t" + list2.size());
            stringBuilder.append(ZkmAssert.lineSeparator);
        }

        this.sourceOnlyLayouts = new IdentityHashSet(this.nodesByLayout.getKeyCount());
        this.targetOnlyLayouts = new IdentityHashSet(this.nodesByLayout.getKeyCount());
        iterator2 = this.layouts.iterator();

        while (iterator2.hasNext()) {
            int[] be = (int[]) iterator2.next();
            List list3 = this.nodesByLayout.getValues(be);
            boolean bl = false;
            boolean bl1 = false;
            Iterator iterator6 = list3.iterator();

            while (iterator6.hasNext()) {
                LongKeyNode longKeyNode2 = (LongKeyNode) iterator6.next();
                Set set1;
                if (this.sourceNodes.contains(longKeyNode2)) {
                    bl = true;
                    set1 = this.targetNodes;
                } else {
                    set1 = this.targetNodes;
                }

                if (set1.contains(longKeyNode2)) {
                    bl1 = true;
                }

                if (bl && bl1) {
                    break;
                }
            }

            if (bl && !bl1) {
                this.sourceOnlyLayouts.add(be);
            } else if (!bl && bl1) {
                this.targetOnlyLayouts.add(be);
            }
        }
    }

    public int getIndexBitOffset() {
        return this.indexBitOffset;
    }

    public boolean isLayoutValid(int[] ba, List list1, Set set1) {
        boolean bl = true;
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(list1.size()));
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            LongKeyNode longKeyNode = (LongKeyNode) iterator.next();
            long mixedKey = longKeyNode.getMixedKey();
            long currentKey = longKeyNode.getCurrentKey();
            long bd = MethodParameterChanger.extractPermutedBits(currentKey, 8, 55, ba);
            long be = MethodParameterChanger.extractLowPermutedBits(currentKey, 56, ba);
            boolean bl1 = this.targetNodes.contains(longKeyNode);
            boolean bl2 = this.sourceNodes.contains(longKeyNode);
            if (bl2) {
                bl = bl && hashSet.add(be);
            }

            if (!bl) {
                return false;
            }

            if (bd == mixedKey) {
                return false;
            }

            if (bl1 && bd == 0L) {
                return false;
            }

            if (bl1 && bd > 140737488355327L) {
                return false;
            }

            if (bl2 && set1.contains(be)) {
                return false;
            }
        }

        return true;
    }

    public ParameterKeyGenerator(
            List list1,
            List list2,
            List list3,
            List list4,
            ObservableHolder observableHolder,
            List list5,
            List list6,
            List list7,
            List list8,
            ObservableHolder observableHolder1,
            Iterator iterator,
            Random random1,
            boolean bl
    ) throws ZkmException, IOException {
        this.random = random1;
        this.middleValueIterator = iterator;
        MethodParamChangeComparator methodParamChangeComparator = new MethodParamChangeComparator(this);
        Collections.sort(list1, methodParamChangeComparator);
        Collections.sort(list2, methodParamChangeComparator);
        list1.size();
        Integer integer = list2.size();
        this.initSizes(integer);
        this.nodesByLayout = new ListMultimap(true, this.rootPairCount / 128);
        this.layouts = new LinkedHashSet(ZkmUtils.getPrimeCapacity(this.rootPairCount / 128));
        this.generateInitialNodes(list3, list2.size(), bl);
        this.rootNode = RootKeyNode.create(this.random, this.rootPairCount, list4, this);
        PairMultiMap pairMultiMap = this.rootNode.getKeyPairMap();
        this.classifyNodes(pairMultiMap);
        this.relayoutGroups(list5);
        IdentityHashSet identityHashSet = new IdentityHashSet(this.sourceNodes.size());
        IdentityHashSet identityHashSet1 = new IdentityHashSet(this.targetNodes.size());
        IdentityMapWrapper identityMapWrapper = new IdentityMapWrapper(ZkmUtils.getPrimeCapacity(list2.size()));
        int[] ba = MethodParameterChanger.createBitPermutation(this.random);
        this.addLayout(ba);
        observableHolder.setValue(this.getCurrentLayout().clone());
        this.initialized = true;
        this.assignNodePairs(identityHashSet, identityHashSet1, list2, identityMapWrapper);
        this.buildKeyChains(identityHashSet, identityHashSet1, list1, list2, identityMapWrapper, list6, list7, list8);
        int[] currentLayout = this.getCurrentLayout();
        int[] bc = new int[currentLayout.length];
        System.arraycopy(currentLayout, 0, bc, 0, currentLayout.length);
        observableHolder1.setValue(bc);
    }

    public int getMergeSortDepthLimit() {
        return mergeSortDepthLimit;
    }

    public void linkFalseDependents(MethodParamChangeNode methodParamChangeNode, List list1) {
        List list2 = methodParamChangeNode.getFalseDependents();
        int ba = list2.size();
        if (ba != 0) {
            if (ba > 1) {
                list2 = new ArrayList(list2);
                ZkmUtils.shuffleList(list2, this.random);
            }

            LongKeyNode longKeyNode = methodParamChangeNode.getSecondKeyNode();
            ArrayList arrayList = new ArrayList(ba + 1);
            arrayList.add(new ObjectPair(methodParamChangeNode, longKeyNode));

            for (int i = 0; i < ba; i++) {
                MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) list2.get(i);
                LongKeyNode longKeyNode1 = methodParamChangeNode1.getFirstKeyNode();
                longKeyNode.appendToChain(longKeyNode1);
                long bc = this.encodeIndexKey(longKeyNode.getIndex());
                long bd = this.encodeIndexKey(longKeyNode1.getIndex());
                list1.add(new MutablePair(bc, bd));
                arrayList.add(new ObjectPair(methodParamChangeNode1, longKeyNode1));
                longKeyNode = longKeyNode1;
            }
        }
    }

    public void relayoutGroups(List list1) {
        sortNodes(this.allKeyNodes);
        this.pendingLayoutNodes = new IdentityHashSet(this.allKeyNodes.size());
        int ba = 0;
        Iterator iterator = this.allKeyNodes.iterator();

        while (iterator.hasNext()) {
            LongKeyNode longKeyNode = (LongKeyNode) iterator.next();
            longKeyNode.setIndex(ba++);
            this.pendingLayoutNodes.add(longKeyNode);
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.sourceNodes.size()));
        Iterator iterator1 = this.sourceNodes.iterator();

        while (iterator1.hasNext()) {
            LongKeyNode longKeyNode1 = (LongKeyNode) iterator1.next();
            hashSet.add(longKeyNode1.getLow56Bits());
        }

        ArrayList arrayList1 = new ArrayList(this.layouts);
        ArrayList arrayList2 = new ArrayList(arrayList1);
        int bb = arrayList1.size();
        ArrayList arrayList = new ArrayList(bb * 3);
        int bc = 0;

        label59:
        while (true) {
            int bd;
            if (bc <= bb * 2) {
                bd = this.random.nextInt(bb);
            } else {
                Random random2 = this.random;

                while (true) {
                    bd = random2.nextInt(bb);
                    if (arrayList2.contains(arrayList1.get(bd))) {
                        if (!this.random.nextBoolean()) {
                            break;
                        }

                        random2 = this.random;
                    } else {
                        random2 = this.random;
                    }
                }
            }

            int[] be = (int[]) arrayList1.get(bd);
            List list2 = this.nodesByLayout.getValues(be);
            LongKeyNode longKeyNode2 = null;
            Random random1 = this.random;

            while (true) {
                int bf = random1.nextInt(list2.size());
                LongKeyNode longKeyNode3 = (LongKeyNode) list2.get(bf);
                if (longKeyNode3.getIndex() < this.keyCount) {
                    longKeyNode2 = longKeyNode3;
                }

                if (longKeyNode2 != null) {
                    arrayList.add(new ObjectPair(be, longKeyNode2));
                    arrayList2.remove(be);
                    bc++;
                    if (arrayList2.isEmpty()) {
                        break label59;
                    }
                    break;
                }

                random1 = this.random;
            }
        }

        int bg = arrayList.size();

        for (int i = 0; i < bg; i++) {
            ObjectPair objectPair = (ObjectPair) arrayList.get(i);
            this.relayoutGroup((int[]) objectPair.getFirst(), (LongKeyNode) objectPair.getSecond(), hashSet, list1);
        }
    }

    public void addLayout(Object object) {
        this.bitLayouts.add(object);
    }

    public void relayoutGroup(int[] ba, LongKeyNode longKeyNode, Set set1, List list1) {
        List list2 = this.nodesByLayout.getValues(ba);
        int[] bb = null;
        int bc = -1;
        if (this.targetOnlyLayouts.contains(ba)) {
            while (++bc <= 8192) {
                bb = MethodParameterChanger.createBitPermutation(this.random);
                if (this.isLayoutValid(bb, list2, set1)) {
                    break;
                }
            }
        } else {
            while (++bc <= 8192) {
                bb = ba.clone();
                MethodParameterChanger.reshufflePermutation(bb, this.random);
                if (this.isLayoutValid(bb, list2, set1)) {
                    break;
                }
            }
        }

        if (bc <= 8192) {
            int index = longKeyNode.getIndex();
            long be = this.encodeIndexKey(index);
            list1.add(be);
            list1.add(bb.clone());
            HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(list2.size()));
            Iterator iterator = list2.iterator();

            while (iterator.hasNext()) {
                LongKeyNode longKeyNode1 = (LongKeyNode) iterator.next();
                if (this.sourceNodes.contains(longKeyNode1)) {
                    hashSet.add(longKeyNode1.getLow56Bits());
                }
            }

            longKeyNode.copyBitLayout(bb);
            HashSet hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(list2.size()));
            Iterator iterator1 = list2.iterator();

            while (iterator1.hasNext()) {
                LongKeyNode longKeyNode2 = (LongKeyNode) iterator1.next();
                this.pendingLayoutNodes.remove(longKeyNode2);
                if (this.sourceNodes.contains(longKeyNode2)) {
                    hashSet1.add(longKeyNode2.getLow56Bits());
                }
            }

            iterator1 = hashSet.iterator();

            while (iterator1.hasNext()) {
                Long long1 = (Long) iterator1.next();
                set1.remove(long1);
            }

            iterator1 = hashSet1.iterator();

            while (iterator1.hasNext()) {
                Long long2 = (Long) iterator1.next();
                set1.add(long2);
            }
        }
    }

    public long generateUniqueKey() {
        int[] currentLayout = this.getCurrentLayout();
        int bb = this.random.nextInt(256);
        int bc = this.random.nextInt(256);

        long bd;
        do {
            bd = this.nextMiddleValue();
        } while (this.usedMiddleValues.contains(bd));

        this.usedMiddleValues.add(bd);
        return MethodParameterChanger.composeKey(bc, bd, bb, currentLayout);
    }

    public static void sortNodes(List list1) {
        mergeSort(0, list1.size() - 1, list1, new ArrayList(list1), 0);
    }

    public static int computeKeyCount(int ba, MutableInt mutableInt) {
        int bb = 0;
        if (HiddenOptionFlags.PARAMETER_KEY_SIZE_OVERRIDE != null) {
            try {
                bb = Integer.parseInt(HiddenOptionFlags.PARAMETER_KEY_SIZE_OVERRIDE);
            } catch (NumberFormatException numberFormatException) {
            }
        }

        int bg;
        short bh;
        if (ba <= 2010) {
            int bc = ba * 6;
            mutableInt.setValue(2);
            bg = bc;
            bh = 2048;
        } else if (ba >= 4510) {
            int be = ba * 3;
            mutableInt.setValue(5);
            bg = be;
            bh = 2048;
        } else {
            float bd = (ba - 2010) / 2500.0F;
            int bf = (int) (ba * (6.0F - 3.0F * bd));
            mutableInt.setValue(2 + Math.round(3.0F * bd));
            bg = bf;
            bh = 2048;
        }

        return Math.max(bg, Math.max(bh, bb));
    }

    public void initSizes(int ba) {
        MutableInt mutableInt = new MutableInt();
        this.keyCount = computeKeyCount(ba, mutableInt);
        int value = mutableInt.getValue();
        this.initialNodeCount = this.keyCount / value;
        this.rootPairCount = this.keyCount / 2;
        int bc = 8;
        int bf = 8;

        for (byte bg = 25; bf < bg; bg = 25) {
            if (2 << bc > this.keyCount) {
                this.indexBitOffset = 63 - bc;
                break;
            }

            bf = ++bc;
        }

        mergeSortDepthLimit = computeMergeSortDepth(this.keyCount);
        long be = (1L << this.indexBitOffset) - 1L;
        long bd = be + 1L;
        indexRandoms = this.random.longs(0L, bd).iterator();
        this.randomLongs = this.random.longs(0L, Long.MAX_VALUE).iterator();
        this.minLinkCount = this.initialNodeCount / 5;
    }

    public void assignNodePairs(Set set1, Set set2, List list1, IdentityMapWrapper identityMapWrapper) {
        Map map1 = this.rootNode.getKeyChainMap();
        int ba = list1.size();
        ArrayList arrayList = new ArrayList(ba);
        this.usedMiddleValues = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.sourceNodes.size()));
        ArrayList arrayList1 = new ArrayList(this.sourceNodes.size());
        Iterator iterator = this.sourceNodes.iterator();

        while (iterator.hasNext()) {
            LongKeyNode longKeyNode = (LongKeyNode) iterator.next();
            arrayList1.add(longKeyNode);
        }

        KeyNodeComparator keyNodeComparator = new KeyNodeComparator(this);
        Collections.sort(arrayList1, keyNodeComparator);

        do {
            ZkmAssert.assertTrue(
                    !arrayList1.isEmpty(),
                    new String[]{"Insufficient entries : " + this.sourceNodes.size() + " : " + ba + " : " + arrayList.size() + " : " + this.pendingLayoutNodes.size()}
            );
            int bd = this.random.nextInt(arrayList1.size());
            LongKeyNode longKeyNode1 = (LongKeyNode) arrayList1.remove(bd);
            if (!this.pendingLayoutNodes.contains(longKeyNode1) && !this.targetNodes.contains(longKeyNode1)) {
                EncryptionKeyChain encryptionKeyChain = (EncryptionKeyChain) map1.get(longKeyNode1);
                if (encryptionKeyChain.isKeyed()) {
                    LongKeyNode longKeyNode2 = (LongKeyNode) encryptionKeyChain;
                    if (!this.pendingLayoutNodes.contains(longKeyNode2) && !set2.contains(longKeyNode2) && !this.sourceNodes.contains(longKeyNode2)) {
                        this.usedMiddleValues.add(longKeyNode1.getMixedKey());
                        set1.add(longKeyNode1);
                        set2.add(longKeyNode2);
                        arrayList.add(new MutablePair(longKeyNode1, longKeyNode2));
                    }
                }
            }
        } while (arrayList.size() < ba);

        int be = 0;
        HashSet hashSet = ZkmUtils.createHashSet();
        HashMap hashMap = ZkmUtils.createHashMap();
        Iterator iterator1 = arrayList.iterator();

        while (iterator1.hasNext()) {
            MutablePair mutablePair = (MutablePair) iterator1.next();
            MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) list1.get(be++);
            ProgramClass programClass1 = methodParamChangeNode.getInitMethod().getOwnerProgramClass();
            if (programClass1.hasVersionedVariants()) {
                hashSet.add(methodParamChangeNode);
            } else if (programClass1.isVersionedVariant()) {
                hashMap.put(programClass1, methodParamChangeNode);
                continue;
            }

            LongKeyNode longKeyNode3 = (LongKeyNode) mutablePair.getKey();
            LongKeyNode longKeyNode4 = (LongKeyNode) mutablePair.getValue();
            long bb = this.deriveKey(longKeyNode3);
            long bc = this.generateUniqueKey();
            methodParamChangeNode.setKeys(longKeyNode3, longKeyNode4, bb, bc);
            identityMapWrapper.put(longKeyNode4, methodParamChangeNode);
        }

        iterator1 = hashSet.iterator();

        while (iterator1.hasNext()) {
            MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) iterator1.next();
            ProgramClass programClass2 = methodParamChangeNode1.getInitMethod().getOwnerProgramClass();
            Iterator iterator2 = programClass2.getVersionedVariants().iterator();

            while (iterator2.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator2.next();
                MethodParamChangeNode methodParamChangeNode2 = (MethodParamChangeNode) hashMap.get(classFileBase);
                methodParamChangeNode2.setKeys(
                        methodParamChangeNode1.getFirstKeyNode(),
                        methodParamChangeNode1.getSecondKeyNode(),
                        methodParamChangeNode1.getPrimaryKey(),
                        methodParamChangeNode1.getSecondaryKey()
                );
            }
        }
    }

    public long deriveKey(LongKeyNode longKeyNode) {
        int[] currentLayout = this.getCurrentLayout();
        long mixedKey = longKeyNode.getMixedKey();
        int bc = longKeyNode.hashCode();
        return MethodParameterChanger.composeKey(this.random.nextInt(256), mixedKey, bc, currentLayout);
    }

    public EncryptionKeyChain getOrCreateNode(long ba, BooleanFlag booleanFlag) {
        int[] currentLayout = this.getCurrentLayout();
        int bc = (int) MethodParameterChanger.extractPermutedBits(ba, this.indexBitOffset, 63, currentLayout);
        if (bc < this.initialNodes.size()) {
            return (EncryptionKeyChain) this.allKeyNodes.get(bc);
        }

        if (this.allKeyNodes.size() % 128 == 0) {
            int[] bd = currentLayout.clone();
            currentLayout = bd;
            this.addLayout(currentLayout);
        }

        LongKeyNode longKeyNode = new LongKeyNode(ba, this.getCurrentLayout(), this.nodesByLayout, this.layouts);
        longKeyNode.setIndex(this.allKeyNodes.size());
        this.allKeyNodes.add(longKeyNode);
        if (this.allKeyNodes.size() >= this.keyCount - 1) {
            booleanFlag.setValue(true);
        }

        return longKeyNode;
    }

    public static void merge(int ba, int bb, int bc, List list1, List list2) {
        int bd = ba;
        int be = bd;
        int bf = bb + 1;

        for (int i = bd; i <= bc; i++) {
            list2.set(i, list1.get(i));
        }

        while (be <= bb && bf <= bc) {
            LongKeyNode longKeyNode;
            if (((LongKeyNode) list2.get(be)).isOrderedBefore((EncryptionKeyChain) list2.get(bf))) {
                longKeyNode = (LongKeyNode) list2.get(be++);
            } else {
                longKeyNode = (LongKeyNode) list2.get(bf++);
            }

            list1.set(bd, longKeyNode);
            bd++;
        }

        while (be <= bb) {
            list1.set(bd, list2.get(be));
            bd++;
            be++;
        }
    }

    public static void mergeSort(int ba, int bb, List list1, List list2, int bc) {
        int bd = bc;
        if (ba < bb) {
            int be = ba + (bb - ba) / 2;
            if (++bd < mergeSortDepthLimit) {
                mergeSort(ba, be, list1, list2, bd);
                mergeSort(be + 1, bb, list1, list2, bd);
            }

            merge(ba, be, bb, list1, list2);
        }
    }

    public static int computeMergeSortDepth(int ba) {
        byte bc;
        if (ba > 14336) {
            if (!HiddenOptionFlags.NO_PARAMETER_KEY_SIZE_LIMIT) {
                if (ba <= 16384) {
                    return 12;
                }

                if (ba <= 20480) {
                    return 10;
                }

                if (ba <= 24576) {
                    return 8;
                }

                if (ba <= 28672) {
                    return 6;
                }

                if (ba <= 32768) {
                    return 4;
                }

                byte bb;
                if (ba <= 35840) {
                    bb = 2;
                } else {
                    bb = 1;
                }

                return bb;
            }

            bc = 17;
        } else {
            bc = 17;
        }

        return bc;
    }

    public void linkDependents(MethodParamChangeNode methodParamChangeNode, List list1, List list2, Set set1, Set set2) {
        List list3 = methodParamChangeNode.getDependents();
        int ba = list3.size();
        if (ba != 0) {
            if (ba > 1) {
                list3 = new ArrayList(list3);
                ZkmUtils.shuffleList(list3, this.random);
            }

            LongKeyNode longKeyNode = methodParamChangeNode.getSecondKeyNode();
            ArrayList arrayList = new ArrayList(ba + 1);
            arrayList.add(new ObjectPair(methodParamChangeNode, longKeyNode));

            for (int i = 0; i < ba; i++) {
                MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) list3.get(i);
                LongKeyNode longKeyNode1 = methodParamChangeNode1.getFirstKeyNode();
                longKeyNode.appendToChain(longKeyNode1);
                long bc = this.encodeIndexKey(longKeyNode.getIndex());
                long bd = this.encodeIndexKey(longKeyNode1.getIndex());
                list1.add(new MutablePair(bc, bd));
                arrayList.add(new ObjectPair(methodParamChangeNode1, longKeyNode1));
                longKeyNode = longKeyNode1;
            }

            ObjectPair objectPair1 = (ObjectPair) arrayList.get(0);
            long randomKey = ((MethodParamChangeNode) objectPair1.getFirst()).getRandomKey();
            LongKeyNode longKeyNode4 = (LongKeyNode) objectPair1.getSecond();
            MutableLong mutableLong = new MutableLong();
            long currentKey = longKeyNode4.getCurrentKey();
            int[] bitLayout = longKeyNode4.getBitLayout();
            Random random1 = this.random;
            long bn = computeXorMask(randomKey, currentKey, bitLayout, mutableLong, (Set) null, random1);
            longKeyNode4.setXorMask(bn);
            set2.add(longKeyNode4);
            long be = this.encodeIndexKey(longKeyNode4.getIndex());
            list2.add(new MutablePair(be, bn));
            int bf = arrayList.size();

            for (int i = 1; i < bf; i++) {
                ObjectPair objectPair = (ObjectPair) arrayList.get(i);
                MethodParamChangeNode methodParamChangeNode2 = (MethodParamChangeNode) objectPair.getFirst();
                LongKeyNode longKeyNode2 = (LongKeyNode) objectPair.getSecond();
                long bh = computeXorMask(randomKey, longKeyNode2.getCurrentKey(), longKeyNode2.getBitLayout(), mutableLong, set1, this.random);
                longKeyNode2.setXorMask(bh);
                set2.add(longKeyNode2);
                long bi = this.encodeIndexKey(longKeyNode2.getIndex());
                longKeyNode2.addKey(mutableLong.getValue());
                list2.add(new MutablePair(bi, bh));
                long bj = this.deriveKey(longKeyNode2);
                methodParamChangeNode2.replacePrimaryKey(bj);
                if (!methodParamChangeNode2.hasKeyField()) {
                    LongKeyNode longKeyNode3 = methodParamChangeNode2.getSecondKeyNode();
                    long bk = methodParamChangeNode2.getRandomKey();
                    longKeyNode3.setXorMask(bk);
                    set2.add(longKeyNode3);
                    long bl = this.encodeIndexKey(longKeyNode3.getIndex());
                    list2.add(new MutablePair(bl, bk));
                }
            }

            Iterator iterator = list3.iterator();

            while (iterator.hasNext()) {
                MethodParamChangeNode methodParamChangeNode3 = (MethodParamChangeNode) iterator.next();
                this.linkDependents(methodParamChangeNode3, list1, list2, set1, set2);
            }
        }
    }

    public void buildKeyChains(Set set1, Set set2, List list1, List list2, IdentityMapWrapper identityMapWrapper, List list3, List list4, List list5) {
        sortNodes(this.allKeyNodes);
        int ba = 0;
        Iterator iterator = this.allKeyNodes.iterator();

        while (iterator.hasNext()) {
            LongKeyNode longKeyNode = (LongKeyNode) iterator.next();
            longKeyNode.setIndex(ba++);
        }

        IdentityHashSet identityHashSet = new IdentityHashSet(set1.size() + set2.size());
        identityHashSet.addAll(set1);
        identityHashSet.addAll(set2);
        LongStream longStream = this.random.longs(1L, 281474976710655L);
        OfLong ofLong = longStream.iterator();
        Iterator iterator1 = list2.iterator();

        while (iterator1.hasNext()) {
            MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) iterator1.next();
            long bb = ofLong.next();
            methodParamChangeNode.setRandomKey(bb);
        }

        Map map1 = this.rootNode.getKeyChainMap();
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(this.sourceNodes.size()));
        Iterator iterator2 = this.sourceNodes.iterator();

        while (iterator2.hasNext()) {
            LongKeyNode longKeyNode1 = (LongKeyNode) iterator2.next();
            hashSet.add(longKeyNode1.getLow56Bits());
        }

        IdentityHashSet identityHashSet1 = new IdentityHashSet(this.allKeyNodes.size());
        Iterator iterator3 = list1.iterator();

        while (iterator3.hasNext()) {
            MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) iterator3.next();
            this.linkDependents(methodParamChangeNode1, list3, list4, hashSet, identityHashSet1);
        }

        iterator3 = list2.iterator();

        while (iterator3.hasNext()) {
            MethodParamChangeNode methodParamChangeNode2 = (MethodParamChangeNode) iterator3.next();
            if (methodParamChangeNode2.hasFalseDependents()) {
                this.linkFalseDependents(methodParamChangeNode2, list3);
            }
        }

        MutableLong mutableLong = new MutableLong();
        Iterator iterator4 = this.allKeyNodes.iterator();

        while (iterator4.hasNext()) {
            LongKeyNode longKeyNode2 = (LongKeyNode) iterator4.next();
            if (!identityHashSet1.contains(longKeyNode2)) {
                long bc;
                if (identityMapWrapper.containsKey(longKeyNode2)) {
                    bc = ((MethodParamChangeNode) identityMapWrapper.get(longKeyNode2)).getRandomKey();
                } else {
                    bc = (Long) this.randomLongs.next();
                }

                long currentKey = longKeyNode2.getCurrentKey();
                int[] bitLayout = longKeyNode2.getBitLayout();
                Random random1 = this.random;
                long be = computeXorMask(bc, currentKey, bitLayout, mutableLong, (Set) null, random1);
                longKeyNode2.setXorMask(be);
                long bf = this.encodeIndexKey(longKeyNode2.getIndex());
                list4.add(new MutablePair(bf, be));
                identityHashSet1.add(longKeyNode2);
            }
        }

        this.collectExtraPairs(set1, set2, identityHashSet, list5);
        int bk = Math.min(this.keyCount, this.allKeyNodes.size() - 1);
        List list6;
        Random random4;
        if (!HiddenOptionFlags.SIMPLE_PARAMETER_KEYS) {
            byte bl;
            int br;
            if (list3.size() < 1000) {
                bl = 4;
                br = this.minLinkCount;
            } else if (list3.size() < 2000) {
                bl = 2;
                br = this.minLinkCount;
            } else {
                bl = 1;
                br = this.minLinkCount;
            }

            int bm = Math.max(br, Math.min(map1.size() / 2, list3.size() * bl));
            int bd = 0;

            label97:
            while (true) {
                int bn = -1;
                LongKeyNode longKeyNode3 = null;
                int bo = 0;
                Random random2 = this.random;

                while (true) {
                    int bg = random2.nextInt(bk + 1);
                    LongKeyNode longKeyNode4 = (LongKeyNode) this.allKeyNodes.get(bg);
                    if (!identityHashSet.contains(longKeyNode4) && !longKeyNode4.hasSuccessorIn(identityHashSet)) {
                        bn = bg;
                        longKeyNode3 = longKeyNode4;
                    }

                    if (bo++ > bk * 10) {
                        list6 = list3;
                        random4 = this.random;
                        break label97;
                    }

                    if (bn != -1) {
                        bg = -1;
                        longKeyNode4 = null;
                        int bh = 0;
                        Random random3 = this.random;

                        while (true) {
                            int bi = random3.nextInt(bk + 1);
                            LongKeyNode longKeyNode5 = (LongKeyNode) this.allKeyNodes.get(bi);
                            if (longKeyNode5 != longKeyNode3 && !longKeyNode5.hasPrevious() && !longKeyNode5.hasSuccessor(longKeyNode3)) {
                                bg = bi;
                                longKeyNode4 = longKeyNode5;
                            }

                            if (bh++ > bk * 10) {
                                list6 = list3;
                                random4 = this.random;
                                break label97;
                            }

                            if (bg != -1) {
                                longKeyNode3.appendToChain(longKeyNode4);
                                long bp = this.encodeIndexKey(bn);
                                long bj = this.encodeIndexKey(bg);
                                list3.add(new MutablePair(bp, bj));
                                if (++bd >= bm) {
                                    list6 = list3;
                                    random4 = this.random;
                                    break label97;
                                }
                                continue label97;
                            }

                            random3 = this.random;
                        }
                    }

                    random2 = this.random;
                }
            }
        } else {
            list6 = list3;
            random4 = this.random;
        }

        ZkmUtils.shuffleList(list6, random4);
        ZkmUtils.shuffleList(list4, this.random);
    }

    public void collectExtraPairs(Set set1, Set set2, Set set3, List list1) {
        Map map1 = this.rootNode.getKeyChainMap();
        int ba = 0;
        Iterator iterator = map1.keySet().iterator();

        while (iterator.hasNext()) {
            LongKeyNode longKeyNode = (LongKeyNode & EncryptionKeyChain) iterator.next();
            if (!set1.contains(longKeyNode) && !set3.contains(longKeyNode) && !this.targetNodes.contains(longKeyNode)) {
                EncryptionKeyChain encryptionKeyChain = (EncryptionKeyChain) map1.get(longKeyNode);
                if (encryptionKeyChain.isKeyed()
                        && !set2.contains(encryptionKeyChain)
                        && !set3.contains(encryptionKeyChain)
                        && !this.sourceNodes.contains(encryptionKeyChain)) {
                    LongKeyNode longKeyNode1 = (LongKeyNode) encryptionKeyChain;
                    long bb = this.deriveKey(longKeyNode);
                    long bc = this.generateUniqueKey();
                    long bd = this.generateUniqueKey();
                    list1.add(new NodePairKeys(longKeyNode, longKeyNode1, bb, bc, bd));
                    set1.add(longKeyNode);
                    set2.add(longKeyNode1);
                    set3.add(longKeyNode);
                    set3.add(longKeyNode1);
                    ba++;
                }
            }

            if (ba > 1) {
                break;
            }
        }
    }

    public static long computeXorMask(long ba, long bb, int[] bc, MutableLong mutableLong, Set set1, Random random1) {
        long bd = MethodParameterChanger.permuteAllBits(bb, bc) & 72057594037927935L;
        long be = MethodParameterChanger.permuteBits(ba, bc);
        long bf = be & 72057594037927935L;
        long bg = be >>> 8 & 281474976710655L;
        long bh = be & 255L;
        OfLong ofLong = random1.longs(0L, 72057594037927936L).iterator();

        long bi;
        long bj;
        long bk;
        do {
            bk = ofLong.next();
            bj = bk << 8 | bh;
            long bl = bj & 72057594037927935L;
            bi = bd ^ bf ^ bl;
        } while ((bk & 281474976710655L ^ bg) == 0L || set1 != null && set1.contains(bi));

        bk = MethodParameterChanger.permuteBits(bj, bc);
        long bm = bb ^ ba ^ bk;
        mutableLong.setValue(bm);
        if (set1 != null) {
            set1.remove(bd);
            set1.add(bi);
        }

        return bk;
    }
}
