package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodsAttribute;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionApiMethod;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class ConstantPool extends AbstractConstantPool {
    public static final char[] ZKM_CHARS = new char[]{'Z', 'K', 'M'};
    public static final Map UNBOX_METHOD_NAMES = ZkmUtils.createHashMap(17);
    public static final Map UNBOX_METHOD_DESCRIPTORS = ZkmUtils.createHashMap(17);
    public static final String FILLER_UTF8_VALUE;
    public ArrayList floatConstants = new ArrayList();
    public ArrayList doubleConstants = new ArrayList();
    public ArrayList methodHandleConstants = new ArrayList();
    public ArrayList methodTypeConstants = new ArrayList();
    public List moduleConstants = new ArrayList();
    public List packageConstants = new ArrayList();
    public ArrayList rawEntries = new ArrayList();
    public int baseEntryCount;

    public ConstantInteger getOrAddIntegerConstant(int ba, List list1) {
        return this.getOrAddIntegerConstant(ba, list1, true, false);
    }

    static {
        StringBuilder stringBuilder = new StringBuilder();

        for (char ba : ZKM_CHARS) {
            stringBuilder.append(ba);
        }

        if (!HiddenOptionFlags.NO_VERSION_STAMP) {
            stringBuilder.append("27.0.0");
            stringBuilder.append('E');
        } else {
            for (char bb : ZKM_CHARS) {
                stringBuilder.append(bb);
            }
        }

        FILLER_UTF8_VALUE = stringBuilder.toString();
        UNBOX_METHOD_NAMES.put("B", "byteValue");
        UNBOX_METHOD_NAMES.put("C", "charValue");
        UNBOX_METHOD_NAMES.put("D", "doubleValue");
        UNBOX_METHOD_NAMES.put("F", "floatValue");
        UNBOX_METHOD_NAMES.put("I", "intValue");
        UNBOX_METHOD_NAMES.put("J", "longValue");
        UNBOX_METHOD_NAMES.put("S", "shortValue");
        UNBOX_METHOD_NAMES.put("Z", "booleanValue");
        UNBOX_METHOD_DESCRIPTORS.put("B", "()B");
        UNBOX_METHOD_DESCRIPTORS.put("C", "()C");
        UNBOX_METHOD_DESCRIPTORS.put("D", "()D");
        UNBOX_METHOD_DESCRIPTORS.put("F", "()F");
        UNBOX_METHOD_DESCRIPTORS.put("I", "()I");
        UNBOX_METHOD_DESCRIPTORS.put("J", "()J");
        UNBOX_METHOD_DESCRIPTORS.put("S", "()S");
        UNBOX_METHOD_DESCRIPTORS.put("Z", "()Z");
    }

    public ResolvedFieldRef getOrAddFieldRef(
            String string, String string1, String string2, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, Boolean boolean1
    ) throws ZkmException, IOException {
        ResolvedFieldRef resolvedFieldRef = this.findFieldRef(string, string1, string2);
        if (resolvedFieldRef != null) {
            return resolvedFieldRef;
        }

        resolvedFieldRef = (ResolvedFieldRef) this.findPendingMemberRef(string, string1, string2, list1);
        if (resolvedFieldRef != null) {
            return resolvedFieldRef;
        }

        ResolvedClassConstant resolvedClassConstant = this.getOrCreateClassConstant(string, list1);
        ResolvedNameAndType resolvedNameAndType = this.createNameAndType(string1, string2, list1);
        ResolvedFieldRef resolvedFieldRef1 = new ResolvedFieldRef(this, resolvedClassConstant, resolvedNameAndType, classMemberLookup1, classResolver1, boolean1);
        list1.add(resolvedFieldRef1);
        return resolvedFieldRef1;
    }

    public synchronized void removeEntries(Set set1) {
        Iterator iterator = this.stringConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
            if (set1.contains(resolvedStringConstant)) {
                iterator.remove();
            }
        }

        iterator = this.utf8Constants.iterator();

        while (iterator.hasNext()) {
            ConstantUtf8 constantUtf8 = (ConstantUtf8) iterator.next();
            if (set1.contains(constantUtf8)) {
                iterator.remove();
            }
        }

        iterator = super.integerConstants.iterator();

        while (iterator.hasNext()) {
            ConstantInteger constantInteger = (ConstantInteger) iterator.next();
            if (set1.contains(constantInteger)) {
                iterator.remove();
            }
        }

        iterator = super.longConstants.iterator();

        while (iterator.hasNext()) {
            ConstantLong constantLong = (ConstantLong) iterator.next();
            if (set1.contains(constantLong)) {
                iterator.remove();
            }
        }

        int bb = this.entries.length;
        ArrayList arrayList = new ArrayList(bb);

        for (int i = 0; i < bb; i++) {
            ConstantPoolEntry constantPoolEntry = this.entries[i];
            if (!(constantPoolEntry instanceof ConstantNullEntry) && set1.contains(constantPoolEntry)) {
                if (constantPoolEntry.getSlotCount() == 2) {
                    i++;
                }
            } else {
                arrayList.add(constantPoolEntry);
            }
        }

        this.entries = ((com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry[]) (arrayList.toArray(new ConstantPoolEntry[arrayList.size()])));
        this.renumberEntries();
        this.baseEntryCount = this.entries.length;
    }

    public void resolveMemberRefTargets(
            Set set1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        int ba = this.fieldRefs.size();

        for (int i = 0; i < ba; i++) {
            ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) this.fieldRefs.get(i);
            resolvedFieldRef.resolveMember(classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1);
        }

        int bc = this.methodRefs.size();

        for (int i = 0; i < bc; i++) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = (ResolvedMethodRefConstant) this.methodRefs.get(i);
            resolvedMethodRefConstant.resolveMember(classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1);
        }

        int be = super.interfaceMethodRefs.size();

        for (int i = 0; i < be; i++) {
            ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = (ResolvedInterfaceMethodRef) super.interfaceMethodRefs.get(i);
            resolvedInterfaceMethodRef.resolveMember(classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1);
        }

        int bg = super.invokeDynamics.size();

        for (int i = 0; i < bg; i++) {
            ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) super.invokeDynamics.get(i);
            resolvedInvokeDynamic.resolveTargetMethod(set1, classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1);
        }

        int bi = super.constantDynamics.size();

        for (int i = 0; i < bi; i++) {
            ((ResolvedConstantDynamic) super.constantDynamics.get(i)).resolveTypeClass(classResolver1);
        }
    }

    public RemappedConstantPool createRemappedPool(UsedConstantsCollector usedConstantsCollector, Set set1) {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(Math.max(7, (int) ((this.getEntryCount() - this.baseEntryCount) * 0.7))));
        ConstantPoolEntry[] constantPoolEntrys = this.entries.clone();
        int ba = Math.max(15, (int) (this.baseEntryCount * 0.7));
        ListMultimap listMultimap = new ListMultimap(ba, 1);
        ArrayList arrayList = new ArrayList(Math.max(5, (int) (this.baseEntryCount * 0.7)));

        for (int i = 0; i < this.utf8Constants.size(); i++) {
            ConstantUtf8 constantUtf8 = (ConstantUtf8) this.utf8Constants.get(i);
            if (usedConstantsCollector.isUtf8Used(constantUtf8)) {
                String string = constantUtf8.getConstantKey();
                listMultimap.addValue(string, constantUtf8);
            } else {
                constantPoolEntrys[constantUtf8.getIndex()] = null;
            }
        }

        this.mergeDuplicateEntries(listMultimap, arrayList, constantPoolEntrys, hashMap);
        ListMultimap listMultimap1 = new ListMultimap(Math.max(10, (this.getEntryCount() - this.baseEntryCount) / 4), 1);

        for (int i = 0; i < this.nameAndTypes.size(); i++) {
            ResolvedNameAndType resolvedNameAndType = (ResolvedNameAndType) this.nameAndTypes.get(i);
            if (usedConstantsCollector.isNameAndTypeUsed(resolvedNameAndType)) {
                String string1 = resolvedNameAndType.getConstantKey();
                listMultimap1.addValue(string1, resolvedNameAndType);
            } else {
                constantPoolEntrys[resolvedNameAndType.getIndex()] = null;
            }
        }

        this.mergeDuplicateEntries(listMultimap1, arrayList, constantPoolEntrys, hashMap);
        ListMultimap listMultimap2 = new ListMultimap(Math.max(10, (this.getEntryCount() - this.baseEntryCount) / 4), 1);

        for (int i = 0; i < this.stringConstants.size(); i++) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) this.stringConstants.get(i);
            if (usedConstantsCollector.isStringUsed(resolvedStringConstant)) {
                String string2 = resolvedStringConstant.getConstantKey();
                listMultimap2.addValue(string2, resolvedStringConstant);
            } else {
                constantPoolEntrys[resolvedStringConstant.getIndex()] = null;
            }
        }

        this.mergeDuplicateEntries(listMultimap2, arrayList, constantPoolEntrys, hashMap);
        ListMultimap listMultimap3 = new ListMultimap(Math.max(10, (this.getEntryCount() - this.baseEntryCount) / 4), 1);
        int bf = 0;
        int bj = 0;

        for (ArrayList arrayList6 = super.invokeDynamics; bj < arrayList6.size(); arrayList6 = super.invokeDynamics) {
            ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) super.invokeDynamics.get(bf);
            if (usedConstantsCollector.isInvokeDynamicUsed(resolvedInvokeDynamic)) {
                String string3 = resolvedInvokeDynamic.getConstantKey();
                listMultimap3.addValue(string3, resolvedInvokeDynamic);
            } else {
                constantPoolEntrys[resolvedInvokeDynamic.getIndex()] = null;
            }

            bj = ++bf;
        }

        this.mergeDuplicateEntries(listMultimap3, arrayList, constantPoolEntrys, hashMap);
        ListMultimap listMultimap4 = new ListMultimap(Math.max(10, (this.getEntryCount() - this.baseEntryCount) / 4), 1);
        int bg = 0;
        bj = 0;

        for (ArrayList arrayList4 = super.integerConstants; bj < arrayList4.size(); arrayList4 = super.integerConstants) {
            ConstantInteger constantInteger = (ConstantInteger) super.integerConstants.get(bg);
            if (usedConstantsCollector.isIntegerUsed(constantInteger)) {
                listMultimap4.addValue(constantInteger.getConstantKey(), constantInteger);
            } else {
                constantPoolEntrys[constantInteger.getIndex()] = null;
            }

            bj = ++bg;
        }

        this.mergeDuplicateEntries(listMultimap4, arrayList, constantPoolEntrys, hashMap);
        ListMultimap listMultimap5 = new ListMultimap(Math.max(10, (this.getEntryCount() - this.baseEntryCount) / 4), 1);
        int bh = 0;
        bj = 0;

        for (ArrayList arrayList5 = super.longConstants; bj < arrayList5.size(); arrayList5 = super.longConstants) {
            ConstantLong constantLong = (ConstantLong) super.longConstants.get(bh);
            if (usedConstantsCollector.isLongUsed(constantLong)) {
                listMultimap5.addValue(constantLong.getConstantKey(), constantLong);
            } else {
                int index = constantLong.getIndex();
                constantPoolEntrys[index] = null;
                constantPoolEntrys[index + 1] = null;
            }

            bj = ++bh;
        }

        this.mergeDuplicateEntries(listMultimap5, arrayList, constantPoolEntrys, hashMap);
        this.clearUnusedEntries(usedConstantsCollector, constantPoolEntrys);
        bh = constantPoolEntrys.length;
        ArrayList arrayList1 = new ArrayList(bh);
        boolean bl = HiddenOptionFlags.DONT_ADD_WATERMARK;
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(bh));
        arrayList1.add(constantPoolEntrys[0]);
        hashSet.add(constantPoolEntrys[0]);
        SetMultiMap setMultiMap = new SetMultiMap(hashMap.size());
        Iterator iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            setMultiMap.addValue(entry.getValue(), entry.getKey());
        }

        Random random1 = null;
        List list1 = usedConstantsCollector.getLdcConstants();
        if (!HiddenOptionFlags.DONT_SHUFFLE_CONSTANT_POOL) {
            random1 = ZkmUtils.createRandom(1428);
            ZkmUtils.shuffleList(list1, random1);
        } else {
            Collections.sort(list1);
        }

        Iterator iterator1 = list1.iterator();

        while (iterator1.hasNext()) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) iterator1.next();
            ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) ZkmUtils.mapOrSelf(constantPoolEntry, hashMap);
            if (hashSet.add(constantPoolEntry1)) {
                arrayList1.add(constantPoolEntry1);
                if (arrayList1.size() > 255) {
                    set1.addAll(usedConstantsCollector.getLdcReferencingMethods(constantPoolEntry1));
                    Set set2 = setMultiMap.getValues(constantPoolEntry1);
                    if (set2 != null) {
                        Iterator iterator2 = set2.iterator();

                        while (iterator2.hasNext()) {
                            ConstantPoolEntry constantPoolEntry2 = (ConstantPoolEntry) iterator2.next();
                            set1.addAll(usedConstantsCollector.getLdcReferencingMethods(constantPoolEntry2));
                        }
                    }
                }
            }
        }

        List list2 = usedConstantsCollector.getLdcWideConstants();
        if (!HiddenOptionFlags.DONT_SHUFFLE_CONSTANT_POOL) {
            ZkmUtils.shuffleList(list2, random1);
        } else {
            Collections.sort(list2);
        }

        Iterator iterator3 = list2.iterator();

        while (iterator3.hasNext()) {
            ConstantPoolEntry constantPoolEntry4 = (ConstantPoolEntry) iterator3.next();
            ConstantPoolEntry constantPoolEntry5 = (ConstantPoolEntry) ZkmUtils.mapOrSelf(constantPoolEntry4, hashMap);
            if (hashSet.add(constantPoolEntry5)) {
                arrayList1.add(constantPoolEntry5);
                if (arrayList1.size() <= 255) {
                    set1.addAll(usedConstantsCollector.getLdcReferencingMethods(constantPoolEntry5));
                    Set set3 = setMultiMap.getValues(constantPoolEntry5);
                    if (set3 != null) {
                        Iterator iterator6 = set3.iterator();

                        while (iterator6.hasNext()) {
                            ConstantPoolEntry constantPoolEntry3 = (ConstantPoolEntry) iterator6.next();
                            set1.addAll(usedConstantsCollector.getLdcReferencingMethods(constantPoolEntry3));
                        }
                    }
                }
            }
        }

        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();

        for (int i = 0; i < bh; i++) {
            ConstantPoolEntry constantPoolEntry6 = constantPoolEntrys[i];
            if (constantPoolEntry6 != null) {
                if (!hashSet.contains(constantPoolEntry6)) {
                    if (!HiddenOptionFlags.DONT_SHUFFLE_CONSTANT_POOL) {
                        if (constantPoolEntry6.getSlotCount() == 2) {
                            arrayList3.add(constantPoolEntry6);
                            i++;
                        } else {
                            arrayList2.add(constantPoolEntry6);
                        }
                    } else {
                        arrayList1.add(constantPoolEntry6);
                        hashSet.add(constantPoolEntry6);
                        if (constantPoolEntry6.getSlotCount() == 2) {
                            ConstantNullEntry constantNullEntry1 = (ConstantNullEntry) constantPoolEntrys[++i];
                            arrayList1.add(constantNullEntry1);
                            hashSet.add(constantNullEntry1);
                        }
                    }
                }
            } else if (!bl) {
                arrayList1.add(new ConstantUtf8(i, this, FILLER_UTF8_VALUE));
                bl = true;
            }
        }

        if (arrayList2.size() > 0) {
            ZkmUtils.shuffleList(arrayList2, random1);
            Iterator iterator4 = arrayList2.iterator();

            while (iterator4.hasNext()) {
                ConstantPoolEntry constantPoolEntry7 = (ConstantPoolEntry) iterator4.next();
                arrayList1.add(constantPoolEntry7);
                hashSet.add(constantPoolEntry7);
            }
        }

        if (arrayList3.size() > 0) {
            ZkmUtils.shuffleList(arrayList3, random1);
            ConstantNullEntry constantNullEntry = this.nullEntries[0];
            Iterator iterator5 = arrayList3.iterator();

            while (iterator5.hasNext()) {
                ConstantPoolEntry constantPoolEntry8 = (ConstantPoolEntry) iterator5.next();
                arrayList1.add(constantPoolEntry8);
                hashSet.add(constantPoolEntry8);
                arrayList1.add(constantNullEntry);
                hashSet.add(constantNullEntry);
            }
        }

        constantPoolEntrys = ((com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry[]) (arrayList1.toArray(new ConstantPoolEntry[arrayList1.size()])));
        return new RemappedConstantPool(this, constantPoolEntrys, hashMap);
    }

    public String translateQualifiedName(String string, EnumerableMap enumerableMap, String string1) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, string1);
        int ba = 0;

        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            if (!isIdentifierPath(string2)) {
                return null;
            }

            if (ba++ == 0 && string2.length() < 2) {
                return null;
            }
        }

        MutableInt mutableInt = new MutableInt();
        String string3 = ResourcePathTranslator.renamePackagePath(string.replace(string1, "/"), enumerableMap, mutableInt).replace("/", string1);
        return !string.equals(string3) && mutableInt.getValue() < 2 ? null : string3;
    }

    public void resolveConstants(
            ArrayList arrayList, ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3
    ) throws ClassFileFormatException {
        int ba = 0;

        boolean bl;
        do {
            bl = false;
            ListIterator listIterator = arrayList.listIterator();

            while (listIterator.hasNext()) {
                ResolvableConstant resolvableConstant = (ResolvableConstant) listIterator.next();
                ConstantPoolEntry constantPoolEntry = resolvableConstant.resolve(listMultimap, listMultimap1, listMultimap2, listMultimap3);
                if (constantPoolEntry == null) {
                    bl = true;
                } else {
                    listIterator.remove();
                    this.entries[resolvableConstant.getIndex()] = constantPoolEntry;
                    if (constantPoolEntry instanceof RawConstantEntry) {
                        this.rawEntries.add((RawConstantEntry) constantPoolEntry);
                    } else {
                        this.registerEntry(constantPoolEntry);
                    }
                }
            }

            if (++ba > 3) {
                throw new ClassFileFormatException(this.getClassLocationDescription() + " : " + " Unknown problem validating constant pool (B).");
            }
        } while (bl);
    }

    public ConstantInteger getOrAddIntegerConstant_s_0(int ba, List list1) {
        return this.getOrAddIntegerConstant(ba, list1);
    }

    public static boolean isIdentifierPath(String string) {
        if (string != null && string.length() != 0) {
            if (!Character.isJavaIdentifierStart(string.charAt(0))) {
                return false;
            }

            int ba = string.length();

            for (int i = 1; i < ba; i++) {
                char bc = string.charAt(i);
                if (bc != '/' && !Character.isJavaIdentifierPart(bc)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void renameThisClass(Object object) {
        String string = (String) object;
        String string1 = this.getClassName();
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(super.classesByName.size()));
        Iterator iterator = this.classConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            if (resolvedClassConstant.getClassName().equals(string1)) {
                resolvedClassConstant.setClassName(string);
            }

            hashMap.put(resolvedClassConstant.getClassName(), resolvedClassConstant);
        }

        super.classesByName = hashMap;
    }

    public void renumberBootstrapMethods() {
        if (super.invokeDynamics.size() > 0) {
            ((ResolvedInvokeDynamic) super.invokeDynamics.get(0)).renumberBootstrapMethods();
        }
    }

    public void loadLibraryMethodOwner(AbstractMethodInfo abstractMethodInfo, Set set1, HashMap hashMap, MessageReporter messageReporter1) throws ZkmException, IOException {
        if (abstractMethodInfo != null && abstractMethodInfo.isLibraryStub()) {
            ClasspathClassFile classpathClassFile = (ClasspathClassFile) abstractMethodInfo.getOwningClass();
            if (set1.add(classpathClassFile)) {
                classpathClassFile.updateClassNames(hashMap, messageReporter1);
            }
        }
    }

    public int replaceStringConstantValue(Object object, String string) {
        int ba = 0;
        Iterator iterator = this.stringConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
            if (resolvedStringConstant.getEditableValue().equals(object)) {
                resolvedStringConstant.getValueUtf8().setValue(string);
                ba++;
            }
        }

        return ba;
    }

    public void mergeDuplicateEntries(ListMultimap listMultimap, List list1, ConstantPoolEntry[] constantPoolEntrys, Map map1) {
        Enumeration enumeration = listMultimap.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            List list2 = listMultimap.getValues(string);
            int ba = list2.size();
            ArrayList arrayList = null;
            if (ba > 1) {
                arrayList = new ArrayList(ba - 1);
            }

            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) list2.get(0);
            list1.add(constantPoolEntry);

            for (int i = 1; i < ba; i++) {
                ConstantPoolEntry constantPoolEntry1 = (ConstantPoolEntry) list2.get(i);
                int index = constantPoolEntry1.getIndex();
                constantPoolEntrys[index] = null;
                map1.put(constantPoolEntry1, constantPoolEntry);
                if (constantPoolEntry1.isLongOrDouble()) {
                    constantPoolEntrys[index + 1] = null;
                }

                arrayList.add(constantPoolEntry1);
            }
        }
    }

    @Override
    public boolean isProgramPool() {
        return true;
    }

    @Override
    public synchronized int appendEntries(List list1) {
        int ba = super.appendEntries(list1);
        int bb = list1.size();

        for (int i = 0; i < bb; i++) {
            this.registerEntry((ConstantPoolEntry) list1.get(i));
        }

        return ba;
    }

    public void collectReferencedProgramMembers(HashSet hashSet, HashSet hashSet1, HashSet hashSet2) {
        int ba = this.classConstants.size();

        for (int i = 0; i < ba; i++) {
            String string = ((ResolvedClassConstant) this.classConstants.get(i)).getClassName();
            String string1 = ClassFileBase.descriptorToClassName(string);
            if (string1 != null) {
                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
                if (programClass1 != null) {
                    hashSet.add(programClass1);
                }
            }
        }

        int be = this.fieldRefs.size();

        for (int i = 0; i < be; i++) {
            ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) this.fieldRefs.get(i);
            AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) resolvedFieldRef.getResolvedMember();
            if (abstractFieldInfo != null && abstractFieldInfo.isProgramMember()) {
                hashSet1.add((FieldInfo) abstractFieldInfo);
            }

            ProgramClass programClass2 = resolvedFieldRef.getFieldTypeProgramClass();
            if (programClass2 != null) {
                hashSet.add(programClass2);
            }
        }

        int bg = this.methodRefs.size();

        for (int i = 0; i < bg; i++) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = (ResolvedMethodRefConstant) this.methodRefs.get(i);
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRefConstant.getResolvedMember();
            if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember()) {
                hashSet2.add((MethodInfo) abstractMethodInfo);
            }

            List list1 = resolvedMethodRefConstant.getDescriptorProgramClasses();

            for (int j = 0; j < list1.size(); j++) {
                hashSet.add(list1.get(j));
            }
        }

        int bi = super.interfaceMethodRefs.size();

        for (int i = 0; i < bi; i++) {
            ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = (ResolvedInterfaceMethodRef) super.interfaceMethodRefs.get(i);
            AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) resolvedInterfaceMethodRef.getResolvedMember();
            if (abstractMethodInfo1 != null && abstractMethodInfo1.isProgramMember()) {
                hashSet2.add((MethodInfo) abstractMethodInfo1);
            }

            List list2 = resolvedInterfaceMethodRef.getDescriptorProgramClasses();

            for (int j = 0; j < list2.size(); j++) {
                hashSet.add(list2.get(j));
            }
        }
    }

    public void remapResourceNameStrings(HashMap hashMap, HashSet hashSet, Set set1, HashMap hashMap1) {
        EnumerableMap enumerableMap = new EnumerableMap(hashMap1);
        int ba = this.stringConstants.size();

        for (int i = 0; i < ba; i++) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) this.stringConstants.get(i);
            if (!hashSet.contains(resolvedStringConstant)) {
                ConstantUtf8 constantUtf8 = resolvedStringConstant.getValueUtf8();
                String string = constantUtf8.getValue();
                int bc = string.length();
                BooleanFlag booleanFlag = new BooleanFlag();
                String string1 = ConstantPoolEntry.remapClassNameString(string, hashMap, booleanFlag);
                if (!string1.equals(string)) {
                    constantUtf8.setValue(string1);
                } else if (!booleanFlag.getValue()) {
                    String string2;
                    if (string.startsWith("/") && bc > 0) {
                        string2 = string.substring(1);
                    } else {
                        string2 = string;
                    }

                    if (set1.contains(string2)) {
                        String string7 = ResourcePathTranslator.renameResourceParentPath(string, enumerableMap);
                        if (!string7.equals(string)) {
                            constantUtf8.setValue(string7);
                        }
                    } else {
                        int bd = string.indexOf("/");
                        int be = string.lastIndexOf("/");
                        int bf = string.indexOf(".");
                        if (bd > 0 && bf > 0 && be < bf && bf < bc - 1) {
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList1 = new ArrayList();
                            String string11 = this.getPackageName() + (string.charAt(string.length() - 1) == '/' ? "" : '/') + string;
                            Iterator iterator = set1.iterator();

                            while (iterator.hasNext()) {
                                String string6 = (String) iterator.next();
                                if (string6.endsWith(string11)) {
                                    arrayList.add(string6);
                                }

                                if (string6.endsWith(string)) {
                                    arrayList1.add(string6);
                                }
                            }

                            String string12;
                            if (arrayList.size() == 1) {
                                string12 = (String) arrayList.get(0);
                            } else {
                                if (arrayList1.size() != 1) {
                                    continue;
                                }

                                string12 = (String) arrayList1.get(0);
                            }

                            if (string12 != null) {
                                String string13 = ResourcePathTranslator.translateRelativeReference(string12, string, enumerableMap);
                                if (!string13.equals(string)) {
                                    constantUtf8.setValue(string13);
                                }
                            }
                        } else if (bf == -1) {
                            if (bd > 0 && be < bc - 1 && ZkmStringUtils.countChar(string, '/') >= 2) {
                                if (enumerableMap.containsKey(string)) {
                                    String string3 = (String) enumerableMap.get(string);
                                    if (string3 != null && !string.equals(string3)) {
                                        constantUtf8.setValue(string3);
                                    }
                                } else if (!HiddenOptionFlags.DONT_RENAME_SLASHED_STRINGS) {
                                    String string8 = this.translateQualifiedName(string, enumerableMap, "/");
                                    if (string8 != null && !string.equals(string8)) {
                                        constantUtf8.setValue(string8);
                                    }
                                }
                            }
                        } else if (bd == -1 && bf > 0 && string.lastIndexOf(".") < bc - 1 && ZkmStringUtils.countChar(string, '.') >= 2) {
                            String string9 = string.replace('.', '/');
                            if (enumerableMap.containsKey(string9)) {
                                String string4 = (String) enumerableMap.get(string9);
                                if (string4 != null && !string9.equals(string4) && !HiddenOptionFlags.DONT_RENAME_DOTTED_STRINGS) {
                                    String string5 = string4.replace('/', '.');
                                    constantUtf8.setValue(string5);
                                }
                            } else if (HiddenOptionFlags.RENAME_PARTIAL_DOTTED_STRINGS) {
                                String string10 = this.translateQualifiedName(string, enumerableMap, ".");
                                if (string10 != null && !string.equals(string10)) {
                                    constantUtf8.setValue(string10);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ResolvedMethodHandleConstant getOrAddMethodHandle(MethodHandleRefKind methodHandleRefKind, ResolvedMemberRef resolvedMemberRef, List list1) {
        Iterator iterator = this.methodHandleConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedMethodHandleConstant resolvedMethodHandleConstant = (ResolvedMethodHandleConstant) iterator.next();
            if (resolvedMethodHandleConstant.getRefKind() == methodHandleRefKind && resolvedMethodHandleConstant.matches(methodHandleRefKind, resolvedMemberRef)) {
                return resolvedMethodHandleConstant;
            }
        }

        iterator = list1.iterator();

        while (iterator.hasNext()) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) iterator.next();
            if (constantPoolEntry instanceof ResolvedMethodHandleConstant) {
                ResolvedMethodHandleConstant resolvedMethodHandleConstant1 = (ResolvedMethodHandleConstant) constantPoolEntry;
                if (resolvedMethodHandleConstant1.getRefKind() == methodHandleRefKind
                        && resolvedMethodHandleConstant1.matches(methodHandleRefKind, resolvedMemberRef)) {
                    return resolvedMethodHandleConstant1;
                }
            }
        }

        ResolvedMethodHandleConstant resolvedMethodHandleConstant2 = new ResolvedMethodHandleConstant(this, methodHandleRefKind, resolvedMemberRef);
        list1.add(resolvedMethodHandleConstant2);
        return resolvedMethodHandleConstant2;
    }

    private void registerEntry(ConstantPoolEntry constantPoolEntry) {
        ConstantPoolTag constantPoolTag = constantPoolEntry.getTag();
        switch (ConstantPoolTagSwitchMap.TAG_SWITCH_TABLE[constantPoolTag.ordinal()]) {
            case 1:
                this.classConstants.add((ResolvedClassConstant) constantPoolEntry);
                ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) super.classesByName
                        .put(((ResolvedClassConstant) constantPoolEntry).getClassName(), (ResolvedClassConstant) constantPoolEntry);
                break;
            case 2:
                this.nameAndTypes.add((ResolvedNameAndType) constantPoolEntry);
                break;
            case 3:
                this.methodRefs.add((ResolvedMethodRefConstant) constantPoolEntry);
                break;
            case 4:
                super.interfaceMethodRefs.add((ResolvedInterfaceMethodRef) constantPoolEntry);
                break;
            case 5:
                this.fieldRefs.add((ResolvedFieldRef) constantPoolEntry);
                break;
            case 6:
                this.utf8Constants.add((ConstantUtf8) constantPoolEntry);
                break;
            case 7:
                this.stringConstants.add((ResolvedStringConstant) constantPoolEntry);
                break;
            case 8:
                super.integerConstants.add((ConstantInteger) constantPoolEntry);
                break;
            case 9:
                super.longConstants.add((ConstantLong) constantPoolEntry);
                break;
            case 10:
                this.floatConstants.add((ConstantFloat) constantPoolEntry);
                break;
            case 11:
                this.doubleConstants.add((ConstantDouble) constantPoolEntry);
                break;
            case 12:
                this.methodHandleConstants.add((ResolvedMethodHandleConstant) constantPoolEntry);
                break;
            case 13:
                this.methodTypeConstants.add((ResolvedMethodType) constantPoolEntry);
                break;
            case 14:
                super.constantDynamics.add((ResolvedConstantDynamic) constantPoolEntry);
                break;
            case 15:
                super.invokeDynamics.add((ResolvedInvokeDynamic) constantPoolEntry);
                break;
            case 16:
                this.moduleConstants.add((ResolvedConstantModule) constantPoolEntry);
                break;
            case 17:
                this.packageConstants.add((ResolvedPackageConstant) constantPoolEntry);
        }
    }

    public void countReferencedProgramClasses(CountingBag countingBag) {
        String string = this.classFile.getClassName();
        int ba = this.classConstants.size();

        for (int i = 0; i < ba; i++) {
            String string1 = ((ResolvedClassConstant) this.classConstants.get(i)).getClassName();
            if (string1.startsWith("[")) {
                string1 = ClassFileBase.descriptorToClassName(string1);
            }

            ProgramClass programClass1;
            if (string1 != null && !string1.equals(string) && (programClass1 = ClassHierarchyNode.findProgramClass(string1)) != null) {
                countingBag.add(programClass1);
            }
        }
    }

    public ResolvedInterfaceMethodRef getOrAddInterfaceMethodRef(
            String string, String string1, String string2, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = this.findInterfaceMethodRef(string, string1, string2);
        if (resolvedInterfaceMethodRef != null) {
            return resolvedInterfaceMethodRef;
        }

        resolvedInterfaceMethodRef = (ResolvedInterfaceMethodRef) this.findPendingMemberRef(string, string1, string2, list1);
        if (resolvedInterfaceMethodRef != null) {
            return resolvedInterfaceMethodRef;
        }

        ResolvedClassConstant resolvedClassConstant = this.getOrCreateClassConstant(string, list1);
        ResolvedNameAndType resolvedNameAndType = this.createNameAndType(string1, string2, list1);
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = new ResolvedInterfaceMethodRef(
                this, resolvedClassConstant, resolvedNameAndType, classMemberLookup1, classResolver1
        );
        list1.add(resolvedInterfaceMethodRef1);
        return resolvedInterfaceMethodRef1;
    }

    public ConstantPool(
            ClassFileInputStream classFileInputStream,
            ProgramClass programClass1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3
    ) throws ClassFileFormatException, IOException {
        super(programClass1);
        int ba = classFileInputStream.readUnsignedShort();
        this.baseEntryCount = ba;
        this.entries = new ConstantPoolEntry[ba];
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        this.utf8Constants = new ArrayList(Math.max(5, (int) (ba * 1.5)));
        this.nameAndTypes = new ArrayList(Math.max(5, (int) (ba * 0.2)));
        this.classConstants = new ArrayList(Math.max(5, (int) (ba * 0.09)));
        super.classesByName = ZkmUtils.createHashMap(Math.max(5, (int) (ba * 0.09)));
        this.stringConstants = new ArrayList(Math.max(5, (int) (ba * 0.05)));
        super.integerConstants = new ArrayList(Math.max(5, (int) (ba * 0.01)));
        super.longConstants = new ArrayList(Math.max(5, (int) (ba * 0.01)));
        this.methodRefs = new ArrayList(Math.max(5, (int) (ba * 0.15)));
        super.interfaceMethodRefs = new ArrayList(Math.max(5, (int) (ba * 0.15)));
        this.fieldRefs = new ArrayList(Math.max(5, (int) (ba * 0.15)));
        this.entries[0] = ConstantNullEntry.getSharedInstance(this);
        int bb = 1;

        while (bb < ba) {
            ConstantPoolEntry constantPoolEntry = ConstantPoolEntry.readEntry(bb, classFileInputStream, this);
            this.entries[bb++] = constantPoolEntry;
            if (constantPoolEntry.getSlotCount() == 2) {
                this.entries[bb++] = ConstantNullEntry.getSharedInstance(this);
            }

            ConstantPoolTag constantPoolTag = constantPoolEntry.getTag();
            switch (ConstantPoolTagSwitchMap.TAG_SWITCH_TABLE[constantPoolTag.ordinal()]) {
                case 1:
                    arrayList.add(constantPoolEntry);
                    break;
                case 2:
                    arrayList1.add(constantPoolEntry);
                    break;
                case 3:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 4:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 5:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 6:
                    this.utf8Constants.add((ConstantUtf8) constantPoolEntry);
                    break;
                case 7:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 8:
                    super.integerConstants.add((ConstantInteger) constantPoolEntry);
                    break;
                case 9:
                    super.longConstants.add((ConstantLong) constantPoolEntry);
                    break;
                case 10:
                    this.floatConstants.add((ConstantFloat) constantPoolEntry);
                    break;
                case 11:
                    this.doubleConstants.add((ConstantDouble) constantPoolEntry);
                    break;
                case 12:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 13:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 14:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 15:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 16:
                    arrayList2.add(constantPoolEntry);
                    break;
                case 17:
                    arrayList2.add(constantPoolEntry);
            }
        }

        this.resolveConstants(arrayList, listMultimap, listMultimap1, listMultimap2, listMultimap3);
        this.resolveConstants(arrayList1, listMultimap, listMultimap1, listMultimap2, listMultimap3);
        this.resolveConstants(arrayList2, listMultimap, listMultimap1, listMultimap2, listMultimap3);
    }

    public ResolvedMethodRefConstant getOrAddMethodRef(
            String string, String string1, String string2, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ResolvedMethodRefConstant resolvedMethodRefConstant = this.findMethodRef(string, string1, string2);
        if (resolvedMethodRefConstant != null) {
            return resolvedMethodRefConstant;
        }

        resolvedMethodRefConstant = (ResolvedMethodRefConstant) this.findPendingMemberRef(string, string1, string2, list1);
        if (resolvedMethodRefConstant != null) {
            return resolvedMethodRefConstant;
        }

        ResolvedClassConstant resolvedClassConstant = this.getOrCreateClassConstant(string, list1);
        ResolvedNameAndType resolvedNameAndType = this.createNameAndType(string1, string2, list1);
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = new ResolvedMethodRefConstant(
                this, resolvedClassConstant, resolvedNameAndType, classMemberLookup1, classResolver1
        );
        list1.add(resolvedMethodRefConstant1);
        return resolvedMethodRefConstant1;
    }

    @Override
    public void applyPackageRenames(Object object) {
        HashMap hashMap = (HashMap) object;
        Iterator iterator = this.packageConstants.iterator();

        while (iterator.hasNext()) {
            ((ResolvedPackageConstant) iterator.next()).remapName(hashMap);
        }
    }

    @Override
    public void unshareConstants(
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            ListMultimap listMultimap7
    ) {
        super.unshareConstants(listMultimap, listMultimap1, listMultimap2, listMultimap3, listMultimap4, listMultimap5, listMultimap6, listMultimap7);
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.classConstants.size()));
        Iterator iterator = this.classConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            hashMap.put(resolvedClassConstant.getClassName(), resolvedClassConstant);
        }

        super.classesByName = hashMap;
    }

    public void remapDescriptorStrings(HashMap hashMap) {
        int ba = 0;
        int bb = 0;

        for (ArrayList arrayList = super.unreferencedUtf8s; bb < arrayList.size(); arrayList = super.unreferencedUtf8s) {
            ConstantUtf8 constantUtf8 = (ConstantUtf8) super.unreferencedUtf8s.get(ba);
            String string = constantUtf8.getValue();
            String string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap);
            if (!string1.equals(string)) {
                constantUtf8.setValue(string1);
            }

            bb = ++ba;
        }

        Iterator iterator = this.stringConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
            ConstantUtf8 constantUtf81 = resolvedStringConstant.getValueUtf8();
            String string3 = constantUtf81.getValue();
            String string2 = ConstantPoolEntry.remapDescriptorClassNames(string3, hashMap);
            if (!string2.equals(string3)) {
                constantUtf81.setValue(string2);
            }
        }
    }

    public int replaceIntegerValue(int ba) {
        int bb = 0;
        Iterator iterator = super.integerConstants.iterator();

        while (iterator.hasNext()) {
            ConstantInteger constantInteger = (ConstantInteger) iterator.next();
            if (constantInteger.getValue() == Integer.MAX_VALUE) {
                constantInteger.setValue(ba);
                bb++;
            }
        }

        return bb;
    }

    public ResolvedStringConstant addStringConstant(String string, List list1) {
        return this.addStringConstant(string, list1, true);
    }

    public NumericConstantEntry[] getLiteralConstants() {
        NumericConstantEntry[] numericConstantEntrys = new NumericConstantEntry[super.integerConstants.size()
                + super.longConstants.size()
                + this.floatConstants.size()
                + this.doubleConstants.size()
                + this.stringConstants.size()];
        int ba = 0;

        for (int i = 0; i < this.stringConstants.size(); i++) {
            numericConstantEntrys[ba++] = (NumericConstantEntry) this.stringConstants.get(i);
        }

        for (int i = 0; i < super.integerConstants.size(); i++) {
            numericConstantEntrys[ba++] = (NumericConstantEntry) super.integerConstants.get(i);
        }

        int bd = 0;
        int be = bd;

        for (ArrayList arrayList = super.longConstants; be < arrayList.size(); arrayList = super.longConstants) {
            numericConstantEntrys[ba++] = (NumericConstantEntry) super.longConstants.get(bd);
            be = ++bd;
        }

        bd = 0;
        be = 0;

        for (ArrayList arrayList1 = this.floatConstants; be < arrayList1.size(); arrayList1 = this.floatConstants) {
            numericConstantEntrys[ba++] = (NumericConstantEntry) this.floatConstants.get(bd);
            be = ++bd;
        }

        bd = 0;
        be = 0;

        for (ArrayList arrayList2 = this.doubleConstants; be < arrayList2.size(); arrayList2 = this.doubleConstants) {
            numericConstantEntrys[ba++] = (NumericConstantEntry) this.doubleConstants.get(bd);
            be = ++bd;
        }

        return numericConstantEntrys;
    }

    public ResolvedMethodRefConstant getOrAddUnboxMethodRef(String string, List list1, ClassMemberLookup classMemberLookup1, ClassResolver classResolver1) throws ZkmException, IOException {
        String string1 = string;
        switch (string1.charAt(0)) {
            case 'B':
            case 'C':
            case 'S':
                string1 = "I";
            default:
                String string2 = ConstantPoolEntry.getWrapperClassName(string1);
                String string3 = (String) UNBOX_METHOD_NAMES.get(string1);
                String string4 = (String) UNBOX_METHOD_DESCRIPTORS.get(string1);
                return this.getOrAddMethodRef(string2, string3, string4, list1, classMemberLookup1, classResolver1);
        }
    }

    public List findSubclassConstants(ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.classConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            if (!resolvedClassConstant.isArrayClass()) {
                String string = resolvedClassConstant.getClassName();

                try {
                    if (string.equals("java/lang/RuntimeException") || classHierarchyQuery.isSubclass(string, "java/lang/RuntimeException")) {
                        arrayList.add(resolvedClassConstant);
                    }
                } catch (ClassFileLoadException classFileLoadException) {
                }
            }
        }

        return arrayList;
    }

    public void clearUnusedEntries(UsedConstantsCollector usedConstantsCollector, ConstantPoolEntry[] constantPoolEntrys) {
        ConstantPoolTag.getFlowKey();

        for (int i = 0; i < constantPoolEntrys.length; i++) {
            if (constantPoolEntrys[i] != null && !(constantPoolEntrys[i] instanceof ConstantNullEntry)) {
                ConstantPoolEntry constantPoolEntry = constantPoolEntrys[i];
                if (!usedConstantsCollector.isUsed(constantPoolEntry)) {
                    constantPoolEntrys[i] = null;
                    if (constantPoolEntry.isLongOrDouble()) {
                        ConstantPoolEntry constantPoolEntry1 = constantPoolEntrys[++i];
                        constantPoolEntrys[i] = null;
                    }
                }
            }
        }
    }

    public ResolvedStringConstant addStringConstant(String string, List list1, boolean bl) {
        ConstantUtf8 constantUtf8 = new ConstantUtf8(0, this, string);
        ResolvedStringConstant resolvedStringConstant = new ResolvedStringConstant(this, constantUtf8, bl);
        list1.add(constantUtf8);
        list1.add(resolvedStringConstant);
        return resolvedStringConstant;
    }

    public ConstantLong getOrAddLongConstant(long ba, List list1) {
        return this.getOrAddLongConstant(ba, list1, true, false);
    }

    public ConstantInteger getOrAddIntegerConstant(int ba, List list1, boolean bl, boolean bl1) {
        if (bl) {
            int bb = 0;
            int bc = 0;

            for (ArrayList arrayList = super.integerConstants; bc < arrayList.size(); arrayList = super.integerConstants) {
                ConstantInteger constantInteger = (ConstantInteger) super.integerConstants.get(bb);
                if (!constantInteger.isInProgramPool() && constantInteger.hasValue(ba)) {
                    return constantInteger;
                }

                bc = ++bb;
            }

            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) iterator.next();
                if (constantPoolEntry instanceof ConstantInteger) {
                    ConstantInteger constantInteger1 = (ConstantInteger) constantPoolEntry;
                    if (!constantInteger1.isInProgramPool() && constantInteger1.hasValue(ba)) {
                        return constantInteger1;
                    }
                }
            }
        }

        ConstantInteger constantInteger2;
        if (bl1) {
            constantInteger2 = new ConstantInteger(this, ba, true);
        } else {
            constantInteger2 = new ConstantInteger(this, ba, false);
        }

        list1.add(constantInteger2);
        return constantInteger2;
    }

    public ConstantLong getOrAddLongConstant(long ba, List list1, boolean bl, boolean bl1) {
        if (bl) {
            int bb = 0;
            int bc = 0;

            for (ArrayList arrayList = super.longConstants; bc < arrayList.size(); arrayList = super.longConstants) {
                ConstantLong constantLong = (ConstantLong) super.longConstants.get(bb);
                if (!constantLong.isInProgramPool() && constantLong.hasValue(ba)) {
                    return constantLong;
                }

                bc = ++bb;
            }

            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) iterator.next();
                if (constantPoolEntry instanceof ConstantLong) {
                    ConstantLong constantLong1 = (ConstantLong) constantPoolEntry;
                    if (!constantLong1.isInProgramPool() && constantLong1.hasValue(ba)) {
                        return constantLong1;
                    }
                }
            }
        }

        ConstantLong constantLong2;
        if (bl1) {
            constantLong2 = new ConstantLong(this, ba, true);
        } else {
            constantLong2 = new ConstantLong(this, ba, false);
        }

        list1.add(constantLong2);
        return constantLong2;
    }

    public void findReflectionApiCalls(Set set1, Map map1, ClassMemberLookup classMemberLookup1, MessageReporter messageReporter1) throws ZkmException, IOException {
        for (int i = 0; i < this.methodRefs.size(); i++) {
            ResolvedMethodRef resolvedMethodRef = (ResolvedMethodRef) this.methodRefs.get(i);

            try {
                ReflectionApiMethod reflectionApiMethod;
                if ((reflectionApiMethod = resolvedMethodRef.findReflectionApiMethod(classMemberLookup1)) != null) {
                    map1.put(resolvedMethodRef, reflectionApiMethod);
                    set1.add((ProgramClass) this.classFile);
                }
            } catch (ZkmClassNotFoundException zkmClassNotFoundException1) {
                messageReporter1.reportWarning(
                        "CLASSPATH WARNING:",
                        "Reference to method '"
                                + resolvedMethodRef.getDisplayString()
                                + "' couldn't be checked because couldn't find '"
                                + zkmClassNotFoundException1.getClassName()
                                + "'. Ensure appropriate classpath is set BEFORE classes are loaded. (1)"
                );
            } catch (ClassFileLoadException classFileLoadException1) {
                messageReporter1.reportWarning(
                        "CLASSPATH WARNING:",
                        "Reference to method '" + resolvedMethodRef.getDisplayString() + "' couldn't be checked. (1) : " + classFileLoadException1.getMessage()
                );
            }
        }

        int bb = 0;
        int bc = 0;

        for (ArrayList arrayList = super.interfaceMethodRefs; bc < arrayList.size(); arrayList = super.interfaceMethodRefs) {
            ResolvedMethodRef resolvedMethodRef1 = (ResolvedMethodRef) super.interfaceMethodRefs.get(bb);

            try {
                ReflectionApiMethod reflectionApiMethod1;
                if ((reflectionApiMethod1 = resolvedMethodRef1.findReflectionApiMethod(classMemberLookup1)) != null) {
                    map1.put(resolvedMethodRef1, reflectionApiMethod1);
                    set1.add((ProgramClass) this.classFile);
                }
            } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
                messageReporter1.reportWarning(
                        "CLASSPATH WARNING:",
                        "Reference to method '"
                                + resolvedMethodRef1.getDisplayString()
                                + "' couldn't be checked because couldn't find '"
                                + zkmClassNotFoundException.getClassName()
                                + "'. Ensure appropriate classpath is set BEFORE classes are loaded. (2)"
                );
            } catch (ClassFileLoadException classFileLoadException) {
                messageReporter1.reportWarning(
                        "CLASSPATH WARNING:",
                        "Reference to method '" + resolvedMethodRef1.getDisplayString() + "' couldn't be checked. (2) : " + classFileLoadException.getMessage()
                );
            }

            bc = ++bb;
        }
    }

    @Override
    public void applyClassRenames(Object object, Object object1) throws ZkmException, IOException {
        MessageReporter messageReporter1 = (MessageReporter) object1;
        HashMap hashMap = (HashMap) object;
        HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(super.classesByName.size()));
        Iterator iterator = this.classConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            resolvedClassConstant.remapName(hashMap);
            hashMap1.put(resolvedClassConstant.getClassName(), resolvedClassConstant);
        }

        super.classesByName = hashMap1;
        int ba = this.nameAndTypes.size();

        for (int i = 0; i < ba; i++) {
            if (this.nameAndTypes.get(i) != null) {
                ((ResolvedNameAndType) this.nameAndTypes.get(i)).remapDescriptor(hashMap);
            }
        }

        int bb = this.methodTypeConstants.size();

        for (int i = 0; i < bb; i++) {
            if (this.methodTypeConstants.get(i) != null) {
                ((ResolvedMethodType) this.methodTypeConstants.get(i)).remapDescriptor(hashMap);
            }
        }

        HashSet hashSet = ZkmUtils.createHashSet(13);
        Iterator iterator1 = this.methodRefs.iterator();

        while (iterator1.hasNext()) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = (ResolvedMethodRefConstant) iterator1.next();
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) resolvedMethodRefConstant.getResolvedMember();
            this.loadLibraryMethodOwner(abstractMethodInfo, hashSet, hashMap, messageReporter1);
        }

        iterator1 = super.interfaceMethodRefs.iterator();

        while (iterator1.hasNext()) {
            ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = (ResolvedInterfaceMethodRef) iterator1.next();
            AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) resolvedInterfaceMethodRef.getResolvedMember();
            this.loadLibraryMethodOwner(abstractMethodInfo1, hashSet, hashMap, messageReporter1);
        }

        iterator1 = super.invokeDynamics.iterator();

        while (iterator1.hasNext()) {
            ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) iterator1.next();
            AbstractMethodInfo abstractMethodInfo2 = resolvedInvokeDynamic.getTargetMethod();
            this.loadLibraryMethodOwner(abstractMethodInfo2, hashSet, hashMap, messageReporter1);
        }
    }

    public int computeDedupedEntryCount() {
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        HashSet hashSet2 = ZkmUtils.createHashSet();
        Iterator iterator = this.utf8Constants.iterator();

        while (iterator.hasNext()) {
            ConstantUtf8 constantUtf8 = (ConstantUtf8) iterator.next();
            hashSet.add(constantUtf8.getConstantKey());
        }

        iterator = this.nameAndTypes.iterator();

        while (iterator.hasNext()) {
            ResolvedNameAndType resolvedNameAndType = (ResolvedNameAndType) iterator.next();
            hashSet1.add(resolvedNameAndType.getConstantKey());
        }

        iterator = this.stringConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
            hashSet2.add(resolvedStringConstant.getConstantKey());
        }

        int ba = this.getEntryCount()
                - this.utf8Constants.size()
                + hashSet.size()
                - this.nameAndTypes.size()
                + hashSet1.size()
                - this.stringConstants.size()
                + hashSet2.size();
        if (ba > 65535) {
        }

        return ba;
    }

    public void clearOriginalStringFlags() {
        Iterator iterator = this.stringConstants.iterator();

        while (iterator.hasNext()) {
            ((ResolvedStringConstant) iterator.next()).setFromClassFile();
        }
    }

    public void removeUnusedEntries(UsedConstantsCollector usedConstantsCollector) {
        int ba = this.classConstants.size();
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        ArrayList arrayList = new ArrayList(ba);
        Set set1 = usedConstantsCollector.getUsedClasses();

        for (int i = 0; i < ba; i++) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) this.classConstants.get(i);
            if (set1.contains(resolvedClassConstant)) {
                arrayList.add(resolvedClassConstant);
                hashMap.put(resolvedClassConstant.getClassName(), resolvedClassConstant);
            }
        }

        super.classesByName = hashMap;
        this.classConstants = arrayList;
        ba = this.utf8Constants.size();
        Set set2 = usedConstantsCollector.getUsedUtf8s();
        ArrayList arrayList1 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ConstantUtf8 constantUtf8 = (ConstantUtf8) this.utf8Constants.get(i);
            if (set2.contains(constantUtf8)) {
                arrayList1.add(constantUtf8);
            }
        }

        this.utf8Constants = arrayList1;
        ba = this.nameAndTypes.size();
        Set set3 = usedConstantsCollector.getUsedNameAndTypes();
        ArrayList arrayList2 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedNameAndType resolvedNameAndType = (ResolvedNameAndType) this.nameAndTypes.get(i);
            if (set3.contains(resolvedNameAndType)) {
                arrayList2.add(resolvedNameAndType);
            }
        }

        this.nameAndTypes = arrayList2;
        ba = this.fieldRefs.size();
        Set set4 = usedConstantsCollector.getUsedMemberRefs();
        ArrayList arrayList3 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) this.fieldRefs.get(i);
            if (set4.contains(resolvedFieldRef)) {
                arrayList3.add(resolvedFieldRef);
            }
        }

        this.fieldRefs = arrayList3;
        ba = this.methodRefs.size();
        ArrayList arrayList4 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = (ResolvedMethodRefConstant) this.methodRefs.get(i);
            if (set4.contains(resolvedMethodRefConstant)) {
                arrayList4.add(resolvedMethodRefConstant);
            }
        }

        this.methodRefs = arrayList4;
        ba = super.interfaceMethodRefs.size();
        ArrayList arrayList5 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = (ResolvedInterfaceMethodRef) super.interfaceMethodRefs.get(i);
            if (set4.contains(resolvedInterfaceMethodRef)) {
                arrayList5.add(resolvedInterfaceMethodRef);
            }
        }

        super.interfaceMethodRefs = arrayList5;
        ba = this.stringConstants.size();
        Set set5 = usedConstantsCollector.getUsedLiterals();
        ArrayList arrayList6 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) this.stringConstants.get(i);
            if (set5.contains(resolvedStringConstant)) {
                arrayList6.add(resolvedStringConstant);
            }
        }

        this.stringConstants = arrayList6;
        ba = super.integerConstants.size();
        ArrayList arrayList7 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ConstantInteger constantInteger = (ConstantInteger) super.integerConstants.get(i);
            if (set5.contains(constantInteger)) {
                arrayList7.add(constantInteger);
            }
        }

        super.integerConstants = arrayList7;
        ba = super.longConstants.size();
        ArrayList arrayList8 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ConstantLong constantLong = (ConstantLong) super.longConstants.get(i);
            if (set5.contains(constantLong)) {
                arrayList8.add(constantLong);
            }
        }

        super.longConstants = arrayList8;
        ba = this.floatConstants.size();
        ArrayList arrayList9 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ConstantFloat constantFloat = (ConstantFloat) this.floatConstants.get(i);
            if (set5.contains(constantFloat)) {
                arrayList9.add(constantFloat);
            }
        }

        this.floatConstants = arrayList9;
        ba = this.doubleConstants.size();
        ArrayList arrayList10 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ConstantDouble constantDouble = (ConstantDouble) this.doubleConstants.get(i);
            if (set5.contains(constantDouble)) {
                arrayList10.add(constantDouble);
            }
        }

        this.doubleConstants = arrayList10;
        ba = super.constantDynamics.size();
        Set set6 = usedConstantsCollector.getUsedConstantDynamics();
        ArrayList arrayList11 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedConstantDynamic resolvedConstantDynamic = (ResolvedConstantDynamic) super.constantDynamics.get(i);
            if (set6.contains(resolvedConstantDynamic)) {
                arrayList11.add(resolvedConstantDynamic);
            }
        }

        super.constantDynamics = arrayList11;
        ba = super.invokeDynamics.size();
        Set set7 = usedConstantsCollector.getUsedInvokeDynamics();
        ArrayList arrayList12 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) super.invokeDynamics.get(i);
            if (set7.contains(resolvedInvokeDynamic)) {
                arrayList12.add(resolvedInvokeDynamic);
            }
        }

        super.invokeDynamics = arrayList12;
        ba = this.methodTypeConstants.size();
        Set set8 = usedConstantsCollector.getUsedMethodTypes();
        ArrayList arrayList13 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedMethodType resolvedMethodType = (ResolvedMethodType) this.methodTypeConstants.get(i);
            if (set8.contains(resolvedMethodType)) {
                arrayList13.add(resolvedMethodType);
            }
        }

        this.methodTypeConstants = arrayList13;
        ba = this.methodHandleConstants.size();
        Set set9 = usedConstantsCollector.getUsedMethodHandles();
        ArrayList arrayList14 = new ArrayList(ba);

        for (int i = 0; i < ba; i++) {
            ResolvedMethodHandleConstant resolvedMethodHandleConstant = (ResolvedMethodHandleConstant) this.methodHandleConstants.get(i);
            if (set9.contains(resolvedMethodHandleConstant)) {
                arrayList14.add(resolvedMethodHandleConstant);
            }
        }

        this.methodHandleConstants = arrayList14;
        int br = this.entries.length;
        ArrayList arrayList15 = new ArrayList(ba);

        for (int i = 0; i < br; i++) {
            ConstantPoolEntry constantPoolEntry = this.entries[i];
            if (!(constantPoolEntry instanceof ConstantNullEntry) && !usedConstantsCollector.isUsed(constantPoolEntry)) {
                if (constantPoolEntry.getSlotCount() == 2) {
                    i++;
                }
            } else {
                arrayList15.add(constantPoolEntry);
            }
        }

        this.entries = ((com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry[]) (arrayList15.toArray(new ConstantPoolEntry[arrayList15.size()])));
        this.renumberEntries();
        this.baseEntryCount = this.entries.length;
    }

    public void linkBootstrapMethods(BootstrapMethodsAttribute bootstrapMethodsAttribute1) {
        Iterator iterator = super.invokeDynamics.iterator();

        while (iterator.hasNext()) {
            ResolvedInvokeDynamic resolvedInvokeDynamic = (ResolvedInvokeDynamic) iterator.next();
            resolvedInvokeDynamic.setBootstrapMethod(bootstrapMethodsAttribute1.getEntry(resolvedInvokeDynamic.getBootstrapMethodIndex()));
        }

        iterator = super.constantDynamics.iterator();

        while (iterator.hasNext()) {
            ResolvedConstantDynamic resolvedConstantDynamic = (ResolvedConstantDynamic) iterator.next();
            resolvedConstantDynamic.setBootstrapMethod(bootstrapMethodsAttribute1.getEntry(resolvedConstantDynamic.getBootstrapMethodIndex()));
        }
    }
}
