package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayCollection;
import com.zelix.klassmaster.util.EnumerationBackedList;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

public abstract class AbstractConstantPool implements ConstantPoolProvider {
    public ArrayList interfaceMethodRefs;
    public ArrayList fieldRefs;
    public ConstantPoolEntry[] entries;
    public ArrayList nameAndTypes;
    public Map classesByName;
    public ArrayList utf8Constants;
    public ArrayList classConstants;
    public ArrayList methodRefs;
    public ArrayList stringConstants;
    public ArrayList integerConstants;
    public final ConstantNullEntry[] nullEntries = new ConstantNullEntry[2];
    public ArrayList unreferencedUtf8s = new ArrayList();
    public ArrayList longConstants = new ArrayList();
    public ArrayList invokeDynamics = new ArrayList();
    public ArrayList constantDynamics = new ArrayList();
    public ClassFileBase classFile;

    public final ResolvedInterfaceMethodRef getOrCreateInterfaceMethodRef(AbstractMethodInfo abstractMethodInfo, List list1) {
        return (ResolvedInterfaceMethodRef) this.getOrCreateMethodRef(
                abstractMethodInfo.getClassName(), abstractMethodInfo.getJvmName(), abstractMethodInfo.getDescriptor(), list1, abstractMethodInfo
        );
    }

    public final void resolveFieldRefs(ScriptEnvironment scriptEnvironment1, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        int ba = this.fieldRefs.size();

        for (int i = 0; i < ba; i++) {
            ((ResolvedFieldRef) this.fieldRefs.get(i)).syncWithResolvedMember(scriptEnvironment1, classMemberLookup1);
        }
    }

    public final String getClassLocationDescription() {
        return this.classFile.getDisplayLocationName();
    }

    public abstract void renameThisClass(Object object);

    public ResolvedClassConstant getOrCreateClassConstant(String string, List list1) {
        Iterator iterator = this.classConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            if (resolvedClassConstant.hasName(string)) {
                return resolvedClassConstant;
            }
        }

        iterator = list1.iterator();

        while (iterator.hasNext()) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) iterator.next();
            if (constantPoolEntry.getTag() == ConstantPoolTag.CLASS && ((ResolvedClassConstant) constantPoolEntry).hasName(string)) {
                return (ResolvedClassConstant) constantPoolEntry;
            }
        }

        ConstantUtf8 constantUtf8 = new ConstantUtf8(0, this, string);
        ResolvedClassConstant resolvedClassConstant1 = new ResolvedClassConstant(this, constantUtf8);
        list1.add(resolvedClassConstant1);
        list1.add(constantUtf8);
        return resolvedClassConstant1;
    }

    public String getExternalClassName() {
        return this.classFile.getOriginalDottedName();
    }

    public final String getClassLocation() {
        return this.classFile.getLocationName();
    }

    public final ClassFileBase getClassFile() {
        return this.classFile;
    }

    public final ResolvedMethodRefConstant getOrCreateMethodRefConstant(AbstractMethodInfo abstractMethodInfo, List list1) {
        return (ResolvedMethodRefConstant) this.getOrCreateMethodRef(
                abstractMethodInfo.getClassName(), abstractMethodInfo.getJvmName(), abstractMethodInfo.getDescriptor(), list1, abstractMethodInfo
        );
    }

    public ClassConstantBase getThisClassConstant() {
        return this.classFile.getThisClassConstant();
    }

    public ConstantUtf8 createUtf8Constant(String string, List list1) {
        ConstantUtf8 constantUtf8 = new ConstantUtf8(0, this, string);
        list1.add(constantUtf8);
        return constantUtf8;
    }

    public final int getEntryCount() {
        return this.entries.length;
    }

    public String getPackageName() {
        return this.classFile.getOriginalPackagePath();
    }

    public abstract void applyClassRenames(Object object, Object object1) throws ZkmException, IOException;

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
        for (int i = 0; i < this.utf8Constants.size(); i++) {
            ConstantUtf8 constantUtf8 = (ConstantUtf8) this.utf8Constants.get(i);
            if (!listMultimap.containsKey(constantUtf8)) {
                this.unreferencedUtf8s.add(constantUtf8);
            }
        }

        this.mergeDuplicateClassConstants(listMultimap5);
        this.mergeDuplicateMemberRefs(listMultimap6);
        int bk = listMultimap.getValueCount()
                - listMultimap.getKeyCount()
                + (listMultimap1.getValueCount() - listMultimap1.getKeyCount()) * 3
                + (listMultimap2.getValueCount() - listMultimap2.getKeyCount()) * 2
                + listMultimap3.getValueCount()
                - listMultimap3.getKeyCount()
                + (listMultimap4.getValueCount() - listMultimap4.getKeyCount()) * 2
                + (listMultimap7.getValueCount() - listMultimap7.getKeyCount()) * 4;
        if (bk > 0) {
            ArrayList arrayList = new ArrayList(this.entries.length + bk);
            arrayList.addAll(new ArrayCollection(this.entries));
            int bb = this.entries.length;
            EnumerationBackedList enumerationBackedList = new EnumerationBackedList(listMultimap2.keys());
            Collections.sort(enumerationBackedList);
            Iterator iterator = enumerationBackedList.iterator();

            while (iterator.hasNext()) {
                ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) iterator.next();
                List list1 = listMultimap2.getValues(resolvedStringConstant);
                int bc = list1.size();

                for (int i = 1; i < bc; i++) {
                    StringConstantReplaceable stringConstantReplaceable = (StringConstantReplaceable) list1.get(i);
                    ConstantUtf8 constantUtf81 = resolvedStringConstant.getValueUtf8().copyWithIndex(bb++);
                    arrayList.add(constantUtf81);
                    this.utf8Constants.add(constantUtf81);
                    int bv = bb++;
                    ListMultimap listMultimap8 = listMultimap;
                    ResolvedStringConstant resolvedStringConstant2 = resolvedStringConstant;
                    ConstantUtf8 constantUtf84 = constantUtf81;
                    AbstractConstantPool abstractConstantPool1 = this;
                    int be = bv;
                    ResolvedStringConstant resolvedStringConstant1 = new ResolvedStringConstant(
                            be, abstractConstantPool1, constantUtf84, resolvedStringConstant2, listMultimap8, true
                    );
                    arrayList.add(resolvedStringConstant1);
                    this.stringConstants.add(resolvedStringConstant1);
                    stringConstantReplaceable.replaceStringConstant(resolvedStringConstant, resolvedStringConstant1);
                }
            }

            EnumerationBackedList enumerationBackedList1 = new EnumerationBackedList(listMultimap3.keys());
            Collections.sort(enumerationBackedList1);
            Iterator iterator1 = enumerationBackedList1.iterator();

            while (iterator1.hasNext()) {
                ConstantInteger constantInteger1 = (ConstantInteger) iterator1.next();
                List list2 = listMultimap3.getValues(constantInteger1);
                int bl = list2.size();

                for (int i = 1; i < bl; i++) {
                    ConstantIntegerReplacer constantIntegerReplacer = (ConstantIntegerReplacer) list2.get(i);
                    int bw = bb++;
                    ConstantInteger constantInteger = constantInteger1;
                    AbstractConstantPool abstractConstantPool2 = this;
                    int bf = bw;
                    ConstantInteger constantInteger2 = new ConstantInteger(bf, abstractConstantPool2, constantInteger);
                    arrayList.add(constantInteger2);
                    this.integerConstants.add(constantInteger2);
                    constantIntegerReplacer.replaceIntegerConstant(constantInteger1, constantInteger2);
                }
            }

            EnumerationBackedList enumerationBackedList2 = new EnumerationBackedList(listMultimap4.keys());
            Collections.sort(enumerationBackedList2);
            ConstantNullEntry constantNullEntry = this.nullEntries[0];
            Iterator iterator2 = enumerationBackedList2.iterator();

            while (iterator2.hasNext()) {
                ConstantLong constantLong2 = (ConstantLong) iterator2.next();
                List list3 = listMultimap4.getValues(constantLong2);
                int bn = list3.size();

                for (int i = 1; i < bn; i++) {
                    LongConstantReplaceable longConstantReplaceable = (LongConstantReplaceable) list3.get(i);
                    int bx = bb++;
                    ConstantLong constantLong1 = constantLong2;
                    AbstractConstantPool abstractConstantPool3 = this;
                    int bg = bx;
                    ConstantLong constantLong = new ConstantLong(bg, abstractConstantPool3, constantLong1);
                    arrayList.add(constantLong);
                    arrayList.add(constantNullEntry);
                    bb++;
                    this.longConstants.add(constantLong);
                    longConstantReplaceable.replaceLongConstant(constantLong2, constantLong);
                }
            }

            EnumerationBackedList enumerationBackedList3 = new EnumerationBackedList(listMultimap7.keys());
            Collections.sort(enumerationBackedList3);
            Iterator iterator3 = enumerationBackedList3.iterator();

            while (iterator3.hasNext()) {
                ResolvedInvokeDynamic resolvedInvokeDynamic2 = (ResolvedInvokeDynamic) iterator3.next();
                resolvedInvokeDynamic2.getBootstrapMethod();
                List list4 = listMultimap7.getValues(resolvedInvokeDynamic2);
                int bp = list4.size();

                for (int i = 1; i < bp; i++) {
                    InvokeDynamicConstantReplaceable invokeDynamicConstantReplaceable = (InvokeDynamicConstantReplaceable) list4.get(i);
                    ResolvedNameAndType resolvedNameAndType = resolvedInvokeDynamic2.getNameAndType();
                    ConstantUtf8 constantUtf82 = resolvedNameAndType.getNameUtf8().copyWithIndex(bb++);
                    arrayList.add(constantUtf82);
                    this.utf8Constants.add(constantUtf82);
                    ConstantUtf8 constantUtf83 = resolvedNameAndType.getDescriptorUtf8().copyWithIndex(bb++);
                    arrayList.add(constantUtf83);
                    this.utf8Constants.add(constantUtf83);
                    int by = bb++;
                    ListMultimap listMultimap9 = listMultimap;
                    ConstantUtf8 constantUtf85 = constantUtf83;
                    ConstantUtf8 constantUtf86 = constantUtf82;
                    AbstractConstantPool abstractConstantPool4 = this;
                    int bh = by;
                    ResolvedNameAndType resolvedNameAndType1 = new ResolvedNameAndType(bh, abstractConstantPool4, constantUtf86, constantUtf85, listMultimap9);
                    arrayList.add(resolvedNameAndType1);
                    this.nameAndTypes.add(resolvedNameAndType1);
                    by = bb++;
                    ResolvedInvokeDynamic resolvedInvokeDynamic1 = resolvedInvokeDynamic2;
                    ResolvedNameAndType resolvedNameAndType2 = resolvedNameAndType1;
                    int bi = by;
                    ResolvedInvokeDynamic resolvedInvokeDynamic = new ResolvedInvokeDynamic(bi, resolvedNameAndType2, resolvedInvokeDynamic1);
                    arrayList.add(resolvedInvokeDynamic);
                    this.invokeDynamics.add(resolvedInvokeDynamic);
                    invokeDynamicConstantReplaceable.replaceInvokeDynamic(resolvedInvokeDynamic2, resolvedInvokeDynamic);
                }
            }

            EnumerationBackedList enumerationBackedList4 = new EnumerationBackedList(listMultimap1.keys());
            Collections.sort(enumerationBackedList4);
            Iterator iterator4 = enumerationBackedList4.iterator();

            while (iterator4.hasNext()) {
                ResolvedNameAndType resolvedNameAndType3 = (ResolvedNameAndType) iterator4.next();
                List list5 = listMultimap1.getValues(resolvedNameAndType3);
                int bq = list5.size();

                for (int i = 1; i < bq; i++) {
                    NameAndTypeHolder nameAndTypeHolder = (NameAndTypeHolder) list5.get(i);
                    ConstantUtf8 constantUtf810 = resolvedNameAndType3.getNameUtf8().copyWithIndex(bb++);
                    arrayList.add(constantUtf810);
                    this.utf8Constants.add(constantUtf810);
                    ConstantUtf8 constantUtf811 = resolvedNameAndType3.getDescriptorUtf8().copyWithIndex(bb++);
                    arrayList.add(constantUtf811);
                    this.utf8Constants.add(constantUtf811);
                    int bz = bb++;
                    ConstantUtf8 constantUtf87 = constantUtf811;
                    ConstantUtf8 constantUtf88 = constantUtf810;
                    AbstractConstantPool abstractConstantPool5 = this;
                    int bj = bz;
                    ResolvedNameAndType resolvedNameAndType4 = new ResolvedNameAndType(bj, abstractConstantPool5, constantUtf88, constantUtf87, listMultimap);
                    arrayList.add(resolvedNameAndType4);
                    this.nameAndTypes.add(resolvedNameAndType4);
                    nameAndTypeHolder.replaceNameAndType(resolvedNameAndType4);
                }
            }

            EnumerationBackedList enumerationBackedList5 = new EnumerationBackedList(listMultimap.keys());
            Collections.sort(enumerationBackedList5);
            Iterator iterator5 = enumerationBackedList5.iterator();

            while (iterator5.hasNext()) {
                ConstantUtf8 constantUtf89 = (ConstantUtf8) iterator5.next();
                List list6 = listMultimap.getValues(constantUtf89);
                int bt = list6.size();

                for (int i = 1; i < bt; i++) {
                    Utf8ConstantReplaceable utf8ConstantReplaceable = (Utf8ConstantReplaceable) list6.get(i);
                    ConstantUtf8 constantUtf812 = constantUtf89.copyWithIndex(bb++);
                    arrayList.add(constantUtf812);
                    this.utf8Constants.add(constantUtf812);
                    utf8ConstantReplaceable.replaceUtf8Constant(constantUtf89, constantUtf812);
                }
            }

            this.entries = ((com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry[]) (arrayList.toArray(new ConstantPoolEntry[arrayList.size()])));
        }
    }

    public abstract boolean isProgramPool();

    public ResolvedInterfaceMethodRef findInterfaceMethodRef(String string, String string1, String string2) {
        Iterator iterator = this.interfaceMethodRefs.iterator();

        while (iterator.hasNext()) {
            ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = (ResolvedInterfaceMethodRef) iterator.next();
            if (resolvedInterfaceMethodRef.matches(string, string1, string2)) {
                return resolvedInterfaceMethodRef;
            }
        }

        return null;
    }

    public ResolvedMemberRef findPendingMemberRef(String string, String string1, String string2, List list1) {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) iterator.next();
            if (constantPoolEntry instanceof ResolvedMemberRef) {
                ResolvedMemberRef resolvedMemberRef = (ResolvedMemberRef) constantPoolEntry;
                if (resolvedMemberRef.matches(string, string1, string2)) {
                    return resolvedMemberRef;
                }
            }
        }

        return null;
    }

    public String getClassName() {
        return this.classFile.getClassName();
    }

    public final ResolvedMethodRef getOrCreateMethodRef(AbstractMethodInfo abstractMethodInfo, List list1) {
        return this.getOrCreateMethodRef(
                abstractMethodInfo.getClassName(), abstractMethodInfo.getJvmName(), abstractMethodInfo.getDescriptor(), list1, abstractMethodInfo
        );
    }

    public abstract void applyPackageRenames(Object object);

    public ResolvedMethodRef getOrCreateMethodRef(String string, String string1, String string2, List list1, AbstractMethodInfo abstractMethodInfo) {
        boolean bl = abstractMethodInfo.getOwningClass().isInterface();
        ResolvedMethodRef resolvedMethodRef;
        if (bl) {
            resolvedMethodRef = this.findInterfaceMethodRef(string, string1, string2);
        } else {
            resolvedMethodRef = this.findMethodRef(string, string1, string2);
        }

        if (resolvedMethodRef != null) {
            return resolvedMethodRef;
        }

        resolvedMethodRef = (ResolvedMethodRef) this.findPendingMemberRef(string, string1, string2, list1);
        if (resolvedMethodRef != null) {
            return resolvedMethodRef;
        }

        ResolvedClassConstant resolvedClassConstant = this.getOrCreateClassConstant(string, list1);
        ResolvedNameAndType resolvedNameAndType = this.createNameAndType(string1, string2, list1);
        ResolvedMethodRef resolvedMethodRef1;
        if (bl) {
            resolvedMethodRef1 = new ResolvedInterfaceMethodRef(this, resolvedClassConstant, resolvedNameAndType, abstractMethodInfo);
        } else {
            resolvedMethodRef1 = new ResolvedMethodRefConstant(this, resolvedClassConstant, resolvedNameAndType, abstractMethodInfo);
        }

        list1.add(resolvedMethodRef1);
        return resolvedMethodRef1;
    }

    @Override
    public final ConstantPoolEntry getConstantPoolEntry(int ba) {
        return ba > 0 && ba < this.entries.length ? this.entries[ba] : null;
    }

    public final ResolvedFieldRef getOrCreateFieldRef(AbstractFieldInfo abstractFieldInfo, List list1) {
        return this.getOrCreateFieldRef(
                abstractFieldInfo.getClassName(), abstractFieldInfo.getSourceName(), abstractFieldInfo.getDescriptor(), list1, abstractFieldInfo
        );
    }

    public ResolvedNameAndType createNameAndType(String string, String string1, List list1) {
        ConstantUtf8 constantUtf8 = new ConstantUtf8(0, this, string);
        ConstantUtf8 constantUtf81 = new ConstantUtf8(0, this, string1);
        ResolvedNameAndType resolvedNameAndType = new ResolvedNameAndType(0, this, constantUtf8, constantUtf81, null);
        list1.add(resolvedNameAndType);
        list1.add(constantUtf8);
        list1.add(constantUtf81);
        return resolvedNameAndType;
    }

    public synchronized int appendEntries(List list1) {
        int ba = list1.size();
        int bb = 0;

        for (int i = 0; i < ba; i++) {
            bb += ((ConstantPoolEntry) list1.get(i)).getSlotCount();
        }

        int be = this.entries.length;
        ConstantPoolEntry[] constantPoolEntrys = new ConstantPoolEntry[be + bb];
        System.arraycopy(this.entries, 0, constantPoolEntrys, 0, be);

        for (int i = 0; i < ba; i++) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) list1.get(i);
            constantPoolEntry.setIndex(be);
            constantPoolEntrys[be++] = constantPoolEntry;
            if (constantPoolEntry.getSlotCount() == 2) {
                constantPoolEntrys[be] = ConstantNullEntry.getSharedInstance(this);
                be++;
            }
        }

        this.entries = constantPoolEntrys;
        return this.entries.length;
    }

    public final void renumberEntries() {
        int ba = this.entries.length;

        for (int i = 0; i < ba; i++) {
            this.entries[i].setIndex(i);
        }
    }

    public ResolvedMethodRefConstant findMethodRef(String string, String string1, String string2) {
        Iterator iterator = this.methodRefs.iterator();

        while (iterator.hasNext()) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = (ResolvedMethodRefConstant) iterator.next();
            if (resolvedMethodRefConstant.matches(string, string1, string2)) {
                return resolvedMethodRefConstant;
            }
        }

        return null;
    }

    public AbstractConstantPool(ClassFileBase classFileBase) {
        this.classFile = classFileBase;
    }

    public void mergeDuplicateClassConstants(ListMultimap listMultimap) {
        Iterator iterator = this.classConstants.iterator();

        while (iterator.hasNext()) {
            ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) iterator.next();
            if (!listMultimap.containsKey(resolvedClassConstant)) {
                listMultimap.appendValues(resolvedClassConstant, new ArrayList(0));
            }
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        TwoKeyMap twoKeyMap = new TwoKeyMap(listMultimap.getKeyCount());
        Iterator iterator1 = listMultimap.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            String string = ((ResolvedClassConstant) entry.getKey()).getClassName();
            if (twoKeyMap.containsKey(string)) {
                hashSet.add(string);
            }

            twoKeyMap.putValue(string, entry.getKey(), entry.getValue());
        }

        iterator1 = hashSet.iterator();

        while (iterator1.hasNext()) {
            String string1 = (String) iterator1.next();
            Map map1 = twoKeyMap.getInnerMap(string1);
            TreeSet treeSet = new TreeSet(map1.keySet());
            int ba = 0;
            ResolvedClassConstant resolvedClassConstant1 = null;
            Iterator iterator2 = treeSet.iterator();

            while (iterator2.hasNext()) {
                ResolvedClassConstant resolvedClassConstant2 = (ResolvedClassConstant) iterator2.next();
                if (ba++ == 0) {
                    resolvedClassConstant1 = resolvedClassConstant2;
                } else {
                    Iterator iterator3 = ((List) map1.get(resolvedClassConstant2)).iterator();

                    while (iterator3.hasNext()) {
                        ((ClassConstantReplaceable) iterator3.next()).replaceClassConstant(resolvedClassConstant2, resolvedClassConstant1);
                    }
                }
            }
        }

        twoKeyMap.clear();
        hashSet.clear();
    }

    public ResolvedFieldRef findFieldRef(String string, String string1, String string2) {
        for (int i = 0; i < this.fieldRefs.size(); i++) {
            ResolvedFieldRef resolvedFieldRef = (ResolvedFieldRef) this.fieldRefs.get(i);
            if (resolvedFieldRef.matches(string, string1, string2)) {
                return resolvedFieldRef;
            }
        }

        return null;
    }

    public final void resolveMethodRefs(ScriptEnvironment scriptEnvironment1, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        int ba = this.methodRefs.size();

        for (int i = 0; i < ba; i++) {
            ((ResolvedMethodRefConstant) this.methodRefs.get(i)).syncWithResolvedMember(scriptEnvironment1, classMemberLookup1);
        }

        ba = this.interfaceMethodRefs.size();

        for (int i = 0; i < ba; i++) {
            ((ResolvedInterfaceMethodRef) this.interfaceMethodRefs.get(i)).syncWithResolvedMember(scriptEnvironment1, classMemberLookup1);
        }

        ba = this.invokeDynamics.size();

        for (int i = 0; i < ba; i++) {
            ((ResolvedInvokeDynamic) this.invokeDynamics.get(i)).updateNameFromTarget();
        }
    }

    public final ResolvedFieldRef getOrCreateFieldRef(String string, String string1, String string2, List list1, AbstractFieldInfo abstractFieldInfo) {
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
        ResolvedFieldRef resolvedFieldRef1 = new ResolvedFieldRef(this, resolvedClassConstant, resolvedNameAndType, abstractFieldInfo);
        list1.add(resolvedFieldRef1);
        return resolvedFieldRef1;
    }

    public void mergeDuplicateMemberRefs(ListMultimap listMultimap) {
        Iterator iterator = this.methodRefs.iterator();

        while (iterator.hasNext()) {
            ResolvedMemberRef resolvedMemberRef = (ResolvedMemberRef) iterator.next();
            if (!listMultimap.containsKey(resolvedMemberRef)) {
                listMultimap.appendValues(resolvedMemberRef, new ArrayList(0));
            }
        }

        iterator = this.interfaceMethodRefs.iterator();

        while (iterator.hasNext()) {
            ResolvedMemberRef resolvedMemberRef3 = (ResolvedMemberRef) iterator.next();
            if (!listMultimap.containsKey(resolvedMemberRef3)) {
                listMultimap.appendValues(resolvedMemberRef3, new ArrayList(0));
            }
        }

        iterator = this.fieldRefs.iterator();

        while (iterator.hasNext()) {
            ResolvedMemberRef resolvedMemberRef4 = (ResolvedMemberRef) iterator.next();
            if (!listMultimap.containsKey(resolvedMemberRef4)) {
                listMultimap.appendValues(resolvedMemberRef4, new ArrayList(0));
            }
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        TwoKeyMap twoKeyMap = new TwoKeyMap(listMultimap.getKeyCount());
        Iterator iterator1 = listMultimap.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            String string = ((ResolvedMemberRef) entry.getKey()).getMemberRefKey();
            if (twoKeyMap.containsKey(string)) {
                hashSet.add(string);
            }

            twoKeyMap.putValue(string, entry.getKey(), entry.getValue());
        }

        iterator1 = hashSet.iterator();

        while (iterator1.hasNext()) {
            String string1 = (String) iterator1.next();
            Map map1 = twoKeyMap.getInnerMap(string1);
            TreeSet treeSet = new TreeSet(map1.keySet());
            int ba = 0;
            ResolvedMemberRef resolvedMemberRef1 = null;
            Iterator iterator2 = treeSet.iterator();

            while (iterator2.hasNext()) {
                ResolvedMemberRef resolvedMemberRef2 = (ResolvedMemberRef) iterator2.next();
                if (ba++ == 0) {
                    resolvedMemberRef1 = resolvedMemberRef2;
                } else {
                    Iterator iterator3 = ((List) map1.get(resolvedMemberRef2)).iterator();

                    while (iterator3.hasNext()) {
                        ((MemberRefReplaceable) iterator3.next()).replaceMemberRef(resolvedMemberRef2, resolvedMemberRef1);
                    }
                }
            }
        }

        twoKeyMap.clear();
        hashSet.clear();
    }
}
