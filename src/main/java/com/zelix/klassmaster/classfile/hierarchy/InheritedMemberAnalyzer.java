package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class InheritedMemberAnalyzer {
    public final Object fieldCreationLock = new Object();
    public final Object methodCreationLock = new Object();
    public final Set classSet;
    public final ClassFileBase[] classes;
    public final ClassResolver classResolver;
    public final Set interfaceFields;
    public final Set interfaceMethods;
    public final SetMultiMap visibleFieldsByClass;
    public final SetMultiMap visibleMethodsByClass;
    public final SetMultiMap relatedClassesByClass;

    public Set getVisibleFieldSignatures(Object object) {
        Set set1 = this.visibleFieldsByClass.getValues(object);
        if (set1 == null) {
            return ZkmUtils.createHashSet(13);
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set1.size()));
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) iterator.next();
            hashSet.add(abstractFieldInfo.getSignature());
        }

        return hashSet;
    }

    public Set getVisibleMethodNameTypes(Object object) {
        Set set1 = this.visibleMethodsByClass.getValues(object);
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set1.size()));
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
            hashSet.add(abstractMethodInfo.getNameTypeSignature());
        }

        return hashSet;
    }

    public InheritedMemberAnalyzer(ClassFileBase[] classFileBases, ClassResolver classResolver1) throws ZkmException, IOException {
        int ba = classFileBases.length;
        this.classSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba));

        for (ClassFileBase classFileBase : classFileBases) {
            this.classSet.add(classFileBase);
            if (classFileBase.hasVersionedVariants()) {
                Iterator iterator = classFileBase.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
                    this.classSet.add(classFileBase1);
                }
            }
        }

        this.classes = ((com.zelix.klassmaster.classfile.ClassFileBase[]) (this.classSet.toArray(new ClassFileBase[this.classSet.size()])));
        ba = this.classes.length;
        this.classResolver = classResolver1;
        this.interfaceFields = ZkmUtils.createHashSet();
        this.interfaceMethods = ZkmUtils.createHashSet();
        SetMultiMap setMultiMap = new SetMultiMap(ba, true);
        SetMultiMap setMultiMap1 = new SetMultiMap(ba, true);
        SetMultiMap setMultiMap2 = new SetMultiMap(ba, true);
        this.analyzeAllClasses(setMultiMap, setMultiMap1, setMultiMap2);
        this.visibleFieldsByClass = new SetMultiMap(ba, true);
        this.visibleMethodsByClass = new SetMultiMap(ba, true);
        this.relatedClassesByClass = new SetMultiMap(ba, true);
        this.buildVisibleMemberMaps(setMultiMap, setMultiMap1, setMultiMap2);
    }

    public Object getFieldCreationLock() {
        return this.fieldCreationLock;
    }

    public void collectHierarchyMembers(
            ClassFileBase classFileBase,
            List list1,
            SetMultiMap setMultiMap,
            List list2,
            SetMultiMap setMultiMap1,
            List list3,
            SetMultiMap setMultiMap2,
            Set set1,
            Set set2,
            String string
    ) throws ZkmException, IOException {
        Set set3 = setMultiMap.getValues(classFileBase);
        if (set3 == null) {
            HashSet hashSet = ZkmUtils.createHashSet();
            setMultiMap.putValueSet(classFileBase, hashSet);
            ArrayList arrayList = new ArrayList(list1);
            arrayList.add(hashSet);
            HashSet hashSet1 = ZkmUtils.createHashSet();
            setMultiMap1.putValueSet(classFileBase, hashSet1);
            ArrayList arrayList1 = new ArrayList(list2);
            arrayList1.add(hashSet1);
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();

            for (int i = 0; i < abstractFieldInfos.length; i++) {
                AbstractFieldInfo abstractFieldInfo = abstractFieldInfos[i];

                for (int j = 0; j < arrayList1.size(); j++) {
                    Set set5 = (Set) arrayList1.get(j);
                    set5.add(abstractFieldInfo);
                }

                if (classFileBase.isInterface()) {
                    set1.add(abstractFieldInfo);
                }
            }

            HashSet hashSet2 = ZkmUtils.createHashSet();
            setMultiMap2.putValueSet(classFileBase, hashSet2);
            ArrayList arrayList2 = new ArrayList(list3);
            arrayList2.add(hashSet2);
            AbstractMethodInfo[] abstractMethodInfos = classFileBase.getDeclaredMethods();

            for (int i = 0; i < abstractMethodInfos.length; i++) {
                AbstractMethodInfo abstractMethodInfo = abstractMethodInfos[i];
                if (classFileBase.isInterface()) {
                    set2.add(abstractMethodInfo);
                }

                for (int j = 0; j < arrayList2.size(); j++) {
                    Set set6 = (Set) arrayList2.get(j);
                    set6.add(abstractMethodInfo);
                }
            }

            if (!classFileBase.getClassName().equals("java/lang/Object")) {
                String string2 = classFileBase.getSuperclassName();
                Integer integer = classFileBase.hasReleaseVersion() ? classFileBase.getReleaseVersion() : null;
                ClassFileBase classFileBase3 = this.classResolver.getVersionedClass(string2, integer, string);
                Iterator iterator5 = arrayList.iterator();

                while (iterator5.hasNext()) {
                    Set set7 = (Set) iterator5.next();
                    set7.add(classFileBase3);
                }

                this.collectHierarchyMembers(classFileBase3, arrayList, setMultiMap, arrayList1, setMultiMap1, arrayList2, setMultiMap2, set1, set2, string);
                String[] strings = classFileBase.getInterfaceNames();

                for (int i = 0; i < strings.length; i++) {
                    String string1 = strings[i];
                    ClassFileBase classFileBase2 = this.classResolver.getVersionedClass(string1, integer, string);
                    Iterator iterator = arrayList.iterator();

                    while (iterator.hasNext()) {
                        ((Set) iterator.next()).add(classFileBase2);
                    }

                    this.collectHierarchyMembers(classFileBase2, arrayList, setMultiMap, arrayList1, setMultiMap1, arrayList2, setMultiMap2, set1, set2, string);
                }
            }
        } else {
            Iterator iterator1 = set3.iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                Iterator iterator3 = list1.iterator();

                while (iterator3.hasNext()) {
                    Set set10 = (Set) iterator3.next();
                    set10.add(classFileBase1);
                }
            }

            Set set8 = setMultiMap1.getValues(classFileBase);
            Iterator iterator2 = set8.iterator();

            while (iterator2.hasNext()) {
                AbstractFieldInfo abstractFieldInfo1 = (AbstractFieldInfo) iterator2.next();

                for (int i = 0; i < list2.size(); i++) {
                    Set set4 = (Set) list2.get(i);
                    set4.add(abstractFieldInfo1);
                }
            }

            Set set9 = setMultiMap2.getValues(classFileBase);
            Iterator iterator4 = set9.iterator();

            while (iterator4.hasNext()) {
                AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator4.next();

                for (int i = 0; i < list3.size(); i++) {
                    Set set11 = (Set) list3.get(i);
                    set11.add(abstractMethodInfo1);
                }
            }
        }
    }

    public void addField(AbstractFieldInfo abstractFieldInfo) {
        ClassFileBase classFileBase = abstractFieldInfo.getOwningClass();
        SetMultiMap setMultiMap;
        if (classFileBase.isInterface()) {
            this.interfaceFields.add(abstractFieldInfo);
            setMultiMap = this.relatedClassesByClass;
        } else {
            setMultiMap = this.relatedClassesByClass;
        }

        Iterator iterator = setMultiMap.getValues(classFileBase).iterator();

        while (iterator.hasNext()) {
            ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
            this.visibleFieldsByClass.addValue(classFileBase1, abstractFieldInfo);
        }
    }

    public void buildVisibleMemberMaps(SetMultiMap setMultiMap, SetMultiMap setMultiMap1, SetMultiMap setMultiMap2) {
        SetMultiMap setMultiMap3 = new SetMultiMap(this.classSet.size());
        Iterator iterator = setMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ClassFileBase classFileBase = (ClassFileBase) entry.getKey();
            Set set1 = (Set) entry.getValue();
            Iterator iterator1 = set1.iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                setMultiMap3.addValue(classFileBase1, classFileBase);
            }
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator3 = this.classSet.iterator();

        while (iterator3.hasNext()) {
            ClassFileBase classFileBase3 = (ClassFileBase) iterator3.next();
            if (!setMultiMap3.containsKey(classFileBase3)) {
                hashSet.add(classFileBase3);
            }
        }

        iterator3 = hashSet.iterator();

        while (iterator3.hasNext()) {
            ClassFileBase classFileBase4 = (ClassFileBase) iterator3.next();
            Set set2 = setMultiMap.getValues(classFileBase4);
            set2.add(classFileBase4);
            Set set3 = setMultiMap1.getValues(classFileBase4);
            this.visibleFieldsByClass.addValues(classFileBase4, set3);
            Set set4 = setMultiMap2.getValues(classFileBase4);
            this.visibleMethodsByClass.addValues(classFileBase4, set4);
            Iterator iterator2 = setMultiMap.getValues(classFileBase4).iterator();

            while (iterator2.hasNext()) {
                ClassFileBase classFileBase2 = (ClassFileBase) iterator2.next();
                this.visibleFieldsByClass.addValues(classFileBase2, set3);
                this.visibleMethodsByClass.addValues(classFileBase2, set4);
                this.relatedClassesByClass.addValues(classFileBase2, set2);
            }
        }
    }

    public Object getMethodCreationLock() {
        return this.methodCreationLock;
    }

    public Set getVisibleMethodSignatures(Object object) {
        Set set1 = this.visibleMethodsByClass.getValues(object);
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set1.size()));
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
            hashSet.add(abstractMethodInfo.getSignature());
        }

        return hashSet;
    }

    public void addMethod(AbstractMethodInfo abstractMethodInfo) {
        ClassFileBase classFileBase = abstractMethodInfo.getOwningClass();
        SetMultiMap setMultiMap;
        if (classFileBase.isInterface()) {
            this.interfaceMethods.add(abstractMethodInfo);
            setMultiMap = this.relatedClassesByClass;
        } else {
            setMultiMap = this.relatedClassesByClass;
        }

        Iterator iterator = setMultiMap.getValues(classFileBase).iterator();

        while (iterator.hasNext()) {
            ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
            this.visibleMethodsByClass.addValue(classFileBase1, abstractMethodInfo);
        }
    }

    public void analyzeAllClasses(SetMultiMap setMultiMap, SetMultiMap setMultiMap1, SetMultiMap setMultiMap2) throws ZkmException, IOException {
        int ba = 0;
        int bb = 0;

        for (ClassFileBase[] classFileBases = this.classes; bb < classFileBases.length; classFileBases = this.classes) {
            ClassFileBase classFileBase = this.classes[ba];
            String string = "analyzing fields and methods in the inheritance hierarchy of class '" + classFileBase.getLocationName() + "'";
            this.collectHierarchyMembers(
                    classFileBase,
                    new ArrayList(),
                    setMultiMap,
                    new ArrayList(),
                    setMultiMap1,
                    new ArrayList(),
                    setMultiMap2,
                    this.interfaceFields,
                    this.interfaceMethods,
                    string
            );
            bb = ++ba;
        }
    }

    public Set getVisibleFieldNames(Object object) {
        Set set1 = this.visibleFieldsByClass.getValues(object);
        if (set1 == null) {
            return ZkmUtils.createHashSet(13);
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set1.size()));
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) iterator.next();
            hashSet.add(abstractFieldInfo.getSourceName());
        }

        return hashSet;
    }
}
