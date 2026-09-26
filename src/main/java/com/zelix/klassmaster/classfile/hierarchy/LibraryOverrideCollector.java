package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ClassLoadFailureException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.EmptyEnumeration;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LibraryOverrideCollector {
    public HashSet libraryOverridingMethods = ZkmUtils.createHashSet();
    public TwoKeyMap overriddenMethodsByMethod = new TwoKeyMap();
    public TwoKeyMap overridingMethodsByMethod = new TwoKeyMap();
    public ClassRepository classRepository;
    public ClasspathClassLoader classpathLoader;

    public Set getOverriddenMethods(Object object) {
        Map map1 = this.overriddenMethodsByMethod.getInnerMap(object);
        return map1 != null ? ZkmUtils.createHashSetFrom(map1.keySet()) : ZkmUtils.createHashSet();
    }

    public MethodInfo[] getLibraryOverridingMethods() {
        MethodInfo[] methodInfos = new MethodInfo[this.libraryOverridingMethods.size()];
        return ((com.zelix.klassmaster.classfile.MethodInfo[]) (this.libraryOverridingMethods.toArray(methodInfos)));
    }

    public void collectLibrarySupertypeMethods(ClassFileBase classFileBase, HashSet hashSet, HashSet hashSet1, ListMultimap listMultimap) throws ZkmException, IOException {
        if (!classFileBase.getClassName().equals("java/lang/Object")) {
            String string = classFileBase.getSuperclassName();
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
            if ((classHierarchyNode == null || !classHierarchyNode.isProgramClass()) && !hashSet1.contains(string)) {
                hashSet1.add(string);
                String string1 = "looking for superclass of '" + classFileBase.getDottedClassName() + "' : '" + classFileBase.getLocationName() + "'";
                ClasspathClassFile classpathClassFile = this.loadLibraryClass(string, string1);
                this.addLibraryMethodSignatures(classpathClassFile, hashSet, listMultimap);
                this.collectLibrarySupertypeMethods(classpathClassFile, hashSet, hashSet1, listMultimap);
            }

            String[] strings = classFileBase.getInterfaceNames();

            for (int i = 0; i < strings.length; i++) {
                String string2 = strings[i];
                ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(string2);
                if ((classHierarchyNode1 == null || !classHierarchyNode1.isProgramClass()) && !hashSet1.contains(string2)) {
                    hashSet1.add(string2);
                    String string3 = "looking for interfaces implemented by '"
                            + classFileBase.getDottedClassName()
                            + "' : '"
                            + classFileBase.getLocationName()
                            + "'";
                    ClasspathClassFile classpathClassFile1 = this.loadLibraryClass(string2, string3);
                    HashSet hashSet2;
                    if (classFileBase.isProgramClass()) {
                        hashSet2 = ZkmUtils.createHashSet();
                    } else {
                        hashSet2 = hashSet;
                    }

                    this.addLibraryMethodSignatures(classpathClassFile1, hashSet2, listMultimap);
                    this.collectLibrarySupertypeMethods(classpathClassFile1, hashSet2, hashSet1, listMultimap);
                    if (classFileBase.isProgramClass()) {
                        hashSet.addAll(hashSet2);
                        this.addInheritedLibraryImplementations((ProgramClass) classFileBase, hashSet2.iterator());
                    }
                }
            }
        }
    }

    public Enumeration enumerateOverridingMethods(Object object) {
        Map map1 = this.overridingMethodsByMethod.getInnerMap(object);
        return map1 != null ? Collections.enumeration(map1.keySet()) : new EmptyEnumeration();
    }

    public void addInheritedLibraryImplementations(ProgramClass programClass1, Iterator iterator) {
        while (iterator.hasNext()) {
            MethodSignature methodSignature1 = (MethodSignature) iterator.next();
            if (!this.classRepository.declaresMethod(methodSignature1, programClass1)) {
                for (ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass1.getClassName()).getSuperclassNode();
                     classHierarchyNode.isProgramClass();
                     classHierarchyNode = classHierarchyNode.getSuperclassNode()
                ) {
                    ProgramClass programClass2 = classHierarchyNode.getProgramClass();
                    if (this.classRepository.declaresMethod(methodSignature1, programClass2)) {
                        MethodInfo methodInfo1 = this.classRepository.findDeclaredMethod(programClass2, methodSignature1);
                        if (!methodInfo1.isAbstract()) {
                            this.libraryOverridingMethods.add(methodInfo1);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void collectOverrides(
            ClassHierarchyNode classHierarchyNode, HashSet hashSet, TwoKeyMap twoKeyMap, HashSet hashSet1, ListMultimap listMultimap, boolean bl
    ) throws ZkmException, IOException {
        ProgramClass programClass1 = classHierarchyNode.getProgramClass();
        this.collectLibrarySupertypeMethods(programClass1, hashSet, hashSet1, listMultimap);
        if (bl) {
            Enumeration enumeration = twoKeyMap.keys();

            while (enumeration.hasMoreElements()) {
                MethodSignature methodSignature1 = (MethodSignature) enumeration.nextElement();
                if (!this.classRepository.declaresMethod(methodSignature1, programClass1)) {
                    for (ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                         classHierarchyNode1.isProgramClass();
                         classHierarchyNode1 = classHierarchyNode1.getSuperclassNode()
                    ) {
                        ProgramClass programClass2 = classHierarchyNode1.getProgramClass();
                        if (this.classRepository.declaresMethod(methodSignature1, programClass2)) {
                            MethodInfo methodInfo1 = this.classRepository.findDeclaredMethod(programClass2, methodSignature1);
                            if (!methodInfo1.isAbstract()) {
                                Map map1 = twoKeyMap.getInnerMap(methodSignature1);
                                Iterator iterator = map1.keySet().iterator();

                                while (iterator.hasNext()) {
                                    MethodInfo methodInfo2 = (MethodInfo) iterator.next();
                                    this.overriddenMethodsByMethod.putValue(methodInfo1, methodInfo2, methodInfo2);
                                }
                            }
                            break;
                        }
                    }
                }
            }

            this.addInheritedLibraryImplementations(programClass1, hashSet.iterator());
        }

        MethodInfo[] methodInfos = programClass1.getMethodInfos();

        for (int i = 0; i < methodInfos.length; i++) {
            MethodInfo methodInfo3 = methodInfos[i];
            if (!methodInfo3.isStatic() && !methodInfo3.isStrictlyPrivate() && !methodInfo3.isConstructor()) {
                MethodSignature methodSignature2 = methodInfo3.getSignature();
                if (hashSet.contains(methodSignature2)) {
                    this.libraryOverridingMethods.add(methodInfo3);
                }

                if (twoKeyMap.containsKey(methodSignature2)) {
                    Map map2 = twoKeyMap.getInnerMap(methodSignature2);
                    Iterator iterator1 = map2.keySet().iterator();

                    while (iterator1.hasNext()) {
                        MethodInfo methodInfo4 = (MethodInfo) iterator1.next();
                        this.overriddenMethodsByMethod.putValue(methodInfo3, methodInfo4, methodInfo4);
                    }
                }

                twoKeyMap.putValue(methodSignature2, methodInfo3, methodInfo3);
            }
        }

        Enumeration enumeration1 = classHierarchyNode.enumerateSubclasses();
        if (enumeration1 != null) {
            while (enumeration1.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) enumeration1.nextElement();
                this.collectOverrides(
                        classHierarchyNode2, ZkmUtils.copyHashSet(hashSet), ZkmUtils.copyTwoKeyMap(twoKeyMap), ZkmUtils.copyHashSet(hashSet1), listMultimap, false
                );
            }
        }

        Enumeration enumeration2 = classHierarchyNode.enumerateImplementors();
        if (enumeration2 != null) {
            while (enumeration2.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) enumeration2.nextElement();
                String[] strings = new String[]{"(1) " + classHierarchyNode.getClassName() + " " + classHierarchyNode3.getClassName()};
                ZkmAssert.assertTrue(classHierarchyNode.isProgramClass(), strings);
                strings = new String[]{"(2) " + classHierarchyNode.getClassName() + " " + classHierarchyNode3.getClassName()};
                ZkmAssert.assertTrue(classHierarchyNode3.isProgramClass(), strings);
                boolean bl1 = classHierarchyNode.isInterface() && !classHierarchyNode3.isInterface();
                this.collectOverrides(
                        classHierarchyNode3, ZkmUtils.copyHashSet(hashSet), ZkmUtils.copyTwoKeyMap(twoKeyMap), ZkmUtils.copyHashSet(hashSet1), listMultimap, bl1
                );
            }
        }
    }

    public void addLibraryMethodSignatures(ClasspathClassFile classpathClassFile, HashSet hashSet, ListMultimap listMultimap) {
        List list1 = listMultimap.getValues(classpathClassFile);
        if (list1 == null) {
            list1 = listMultimap.createDefaultList();
            LibraryMethod[] libraryMethods = classpathClassFile.getMethods();

            for (int i = 0; i < libraryMethods.length; i++) {
                LibraryMethod libraryMethod = libraryMethods[i];
                if (!libraryMethod.isStatic() && !libraryMethod.isStrictlyPrivate() && !libraryMethod.isConstructor()) {
                    list1.add(libraryMethod.getSignature());
                }
            }

            listMultimap.putValues(classpathClassFile, list1);
        }

        hashSet.addAll(list1);
    }

    public ClasspathClassFile loadLibraryClass(String string, String string1) throws ZkmException, IOException {
        try {
            return this.classpathLoader.findClassFile(string, true, string1);
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            throw new ClassLoadFailureException(
                    "Class '" + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName()) + "' not found while " + string1 + ". (2)"
            );
        } catch (ClassFileLoadException classFileLoadException) {
            throw new ClassLoadFailureException(classFileLoadException.getMessage());
        }
    }

    public LibraryOverrideCollector(ClassRepository classRepository1, ClassHierarchy classHierarchy1, ClasspathClassLoader classpathClassLoader1) throws ZkmException, IOException {
        this.classRepository = classRepository1;
        this.classpathLoader = classpathClassLoader1;
        ListMultimap listMultimap = new ListMultimap();
        List list1 = classHierarchy1.getTopProgramNodes();

        for (int i = 0; i < list1.size(); i++) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) list1.get(i);
            HashSet hashSet = ZkmUtils.createHashSet();
            HashSet hashSet1 = ZkmUtils.createHashSet();
            TwoKeyMap twoKeyMap = new TwoKeyMap();
            this.collectOverrides(classHierarchyNode, hashSet1, twoKeyMap, hashSet, listMultimap, false);
        }

        Enumeration enumeration = this.overriddenMethodsByMethod.keys();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
            Map map1 = this.overriddenMethodsByMethod.getInnerMap(methodInfo1);
            Iterator iterator = map1.keySet().iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo2 = (MethodInfo) iterator.next();
                this.overridingMethodsByMethod.putValue(methodInfo2, methodInfo1, methodInfo1);
            }
        }
    }
}
