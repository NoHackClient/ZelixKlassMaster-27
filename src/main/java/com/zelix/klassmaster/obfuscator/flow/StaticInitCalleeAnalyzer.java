package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodNameDescComparator;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.hierarchy.LibraryMethod;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayCollection;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class StaticInitCalleeAnalyzer {
    public Set staticInitializers = ZkmUtils.createHashSet();
    public final ClassRepository classRepository;
    public final ListMultimap initializersByCallee;
    public final ListMultimap calleesByInitializer;
    public final ListMultimap initDependenciesByClass;

    public Set getDependentSubclasses(MethodInfo methodInfo1) throws ZkmException, IOException {
        String string = methodInfo1.getClassName();
        HashSet hashSet = ZkmUtils.createHashSet();
        List list1 = this.initDependenciesByClass.getValues(methodInfo1.getOwnerProgramClass());
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                if (classFileBase.isProgramClass() && this.classRepository.isSubclass(classFileBase.getClassName(), string)) {
                    hashSet.add((ProgramClass) classFileBase);
                }
            }
        }

        return hashSet;
    }

    public void collectInitializerDependencies(
            AbstractMethodInfo abstractMethodInfo, List list1, List list2, Set set1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1
    ) throws ZkmProcessingException {
        HashSet hashSet = ZkmUtils.createHashSet(13);
        HashSet hashSet1 = ZkmUtils.createHashSet();
        HashSet hashSet2 = ZkmUtils.createHashSet(13);
        HashSet hashSet3 = ZkmUtils.createHashSet(13);
        HashSet hashSet4 = ZkmUtils.createHashSet(13);
        abstractMethodInfo.collectReachableMethods(hashSet, set1);
        if (list2 != null) {
            Iterator iterator = list2.iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                methodInfo1.collectReachableMethods(hashSet, set1);
            }
        }

        hashSet.remove(abstractMethodInfo);
        abstractMethodInfo.collectCodeTypeReferences(hashSet1, hashSet2, hashSet3, hashSet4);
        if (list1 != null) {
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                ProgramClass programClass1 = (ProgramClass) iterator1.next();
                hashSet2.add(programClass1);
            }
        }

        Iterator iterator2 = hashSet.iterator();

        while (iterator2.hasNext()) {
            AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator2.next();
            setMultiMap.addValue(abstractMethodInfo1, abstractMethodInfo);
            abstractMethodInfo1.collectCodeTypeReferences(hashSet1, hashSet2, hashSet3, hashSet4);
        }

        if (!hashSet3.isEmpty()) {
            iterator2 = hashSet3.iterator();

            while (iterator2.hasNext()) {
                AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) iterator2.next();
                ClassFileBase classFileBase = ConstantPoolEntry.lookupClassByDescriptor(abstractFieldInfo.getDescriptor());
                if (classFileBase != null) {
                    if (classFileBase.isMultiRelease()) {
                        hashSet2.addAll(classFileBase.getAllVersions());
                    } else {
                        hashSet2.add(classFileBase);
                    }
                }
            }
        }

        if (!hashSet4.isEmpty()) {
            iterator2 = hashSet4.iterator();

            while (iterator2.hasNext()) {
                AbstractMethodInfo abstractMethodInfo2 = (AbstractMethodInfo) iterator2.next();
                List list3 = ConstantPoolEntry.getReferencedClasses(abstractMethodInfo2.getDescriptor());
                if (list3 != null) {
                    hashSet2.addAll(list3);
                }
            }
        }

        if (!hashSet2.isEmpty()) {
            setMultiMap1.addValues(abstractMethodInfo.getOwningClass(), hashSet2);
        }

        if (!hashSet1.isEmpty()) {
            setMultiMap1.addValues(abstractMethodInfo.getOwningClass(), hashSet1);
        }
    }

    public boolean isInitDependencyLeaf(Object object) {
        List list1 = this.initDependenciesByClass.getValues(object);
        boolean bl = true;
        if (list1 != null && !list1.isEmpty()) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                List list2 = this.initDependenciesByClass.getValues(classFileBase);
                if (list2 != null && !list2.isEmpty()) {
                    bl = false;
                    break;
                }
            }
        }

        return bl;
    }

    public boolean isInitializerOrCallee(MethodInfo methodInfo1) {
        return this.staticInitializers.contains(methodInfo1) ? true : this.isCalledByInitializer(methodInfo1);
    }

    public void propagateInitDependencies(ClassHierarchyNode classHierarchyNode, Set set1, SetMultiMap setMultiMap, SetMultiMap setMultiMap1) {
        ClassFileBase classFileBase = classHierarchyNode.getClassFile();
        Set set2 = setMultiMap.getValues(classFileBase);
        if (set2 != null) {
            set1.addAll(set2);
        }

        if (classFileBase.hasVersionedVariants()) {
            Iterator iterator = classFileBase.getVersionedVariants().iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
                Set set3 = setMultiMap.getValues(classFileBase1);
                if (set3 != null) {
                    set1.addAll(set3);
                }
            }
        }

        setMultiMap1.addValues(classFileBase, set1);
        if (classFileBase.hasVersionedVariants()) {
            Iterator iterator1 = classFileBase.getVersionedVariants().iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase2 = (ClassFileBase) iterator1.next();
                setMultiMap1.addValues(classFileBase2, set1);
            }
        }

        Enumeration enumeration = classHierarchyNode.enumerateSubclasses();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                ClassFileBase classFileBase3 = classHierarchyNode1.getClassFile();
                if (classFileBase3 != null) {
                    this.propagateInitDependencies(classHierarchyNode1, ZkmUtils.createHashSetFrom(set1), setMultiMap, setMultiMap1);
                }
            }
        }
    }

    public boolean isCalledBySubclassInitializer(MethodInfo methodInfo1) throws ZkmException, IOException {
        String string = methodInfo1.getClassName();
        List list1 = this.initializersByCallee.getValues(methodInfo1);
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string1 = ((AbstractMethodInfo) iterator.next()).getClassName();
                if (this.classRepository.isSubclass(string, string1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCalledByInitializer(MethodInfo methodInfo1) {
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        if (programClass1.isMultiRelease()) {
            MethodSignature methodSignature1 = methodInfo1.getSignature();
            Iterator iterator = programClass1.getAllVersions().iterator();

            while (iterator.hasNext()) {
                AbstractMethodInfo abstractMethodInfo = ((ClassFileBase) iterator.next()).findMethod(methodSignature1);
                if (this.initializersByCallee.containsKey(abstractMethodInfo)) {
                    return true;
                }
            }

            return false;
        } else {
            return this.initializersByCallee.containsKey(methodInfo1);
        }
    }

    public void logExcludedCallees(PrintWriter printWriter, FlowObfuscationExclusions flowObfuscationExclusions) {
        List list1 = ZkmUtils.enumerationToList(this.initializersByCallee.keys());
        MethodNameDescComparator methodNameDescComparator = new MethodNameDescComparator(this);
        Collections.sort(list1, methodNameDescComparator);

        for (int i = 0; i < list1.size(); i++) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) list1.get(i);
            if (abstractMethodInfo.isProgramMember()) {
                MethodInfo methodInfo1 = (MethodInfo) abstractMethodInfo;
                if (flowObfuscationExclusions == null
                        || !flowObfuscationExclusions.isClassExcluded(methodInfo1.getOwnerProgramClass()) && !flowObfuscationExclusions.isMethodExcluded(methodInfo1)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    List list2 = this.initializersByCallee.getValues(methodInfo1);
                    Iterator iterator = list2.iterator();

                    while (iterator.hasNext()) {
                        ClassFileBase classFileBase = ((AbstractMethodInfo) iterator.next()).getOwningClass();
                        stringBuilder.append("\"" + classFileBase.getDottedClassName() + "\"");
                        if (classFileBase.hasReleaseVersion()) {
                            stringBuilder.append(" v" + classFileBase.getReleaseVersion());
                        }

                        if (iterator.hasNext()) {
                            stringBuilder.append(", ");
                        }
                    }

                    printWriter.println(
                            "\tExcluding from flow obfuscation method \""
                                    + methodInfo1.buildDeclaration(false).trim()
                                    + "\" in class \""
                                    + methodInfo1.getDottedClassName()
                                    + "\" "
                                    + (list2.size() == 1 ? "because it is called by the class initializer in " : "because it is called by class initializers in ")
                                    + stringBuilder
                    );
                }
            }
        }
    }

    public StaticInitCalleeAnalyzer(
            ClassFileBase[] classFileBases,
            ClassRepository classRepository1,
            ClassHierarchy classHierarchy1,
            ScriptEnvironment scriptEnvironment1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassResolver classResolver1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1,
            boolean bl
    ) throws ZkmException, IOException {
        this.classRepository = classRepository1;

        for (ClassFileBase classFileBase : classFileBases) {
            if (classFileBase.isProgramClass()) {
                ProgramClass programClass1 = (ProgramClass) classFileBase;
                MethodInfo methodInfo1 = classRepository1.findDeclaredMethod(programClass1, MethodSignature.STATIC_INITIALIZER);
                if (methodInfo1 != null) {
                    this.staticInitializers.add(methodInfo1);
                }
            } else {
                ClasspathClassFile classpathClassFile = (ClasspathClassFile) classFileBase;
                classpathClassFile.resolveConstantPool(classRepository1, classResolver1, ignoreMissingReferencesSpec1);
                LibraryMethod libraryMethod = (LibraryMethod) classpathClassFile.findMethod(MethodSignature.STATIC_INITIALIZER);
                if (libraryMethod != null) {
                    this.staticInitializers.add(libraryMethod);
                }
            }

            if (classFileBase.hasVersionedVariants()) {
                Iterator iterator2 = classFileBase.getVersionedVariants().iterator();

                while (iterator2.hasNext()) {
                    ClassFileBase classFileBase2 = (ClassFileBase) iterator2.next();
                    AbstractMethodInfo abstractMethodInfo = classFileBase2.findMethod(MethodSignature.STATIC_INITIALIZER);
                    if (abstractMethodInfo != null) {
                        this.staticInitializers.add(abstractMethodInfo);
                    }
                }
            }
        }

        SetMultiMap setMultiMap = new SetMultiMap(this.staticInitializers.size());
        HashSet hashSet = ZkmUtils.createHashSetFrom(new ArrayCollection(classFileBases));
        SetMultiMap setMultiMap1 = new SetMultiMap(hashSet.size());
        Iterator iterator1 = this.staticInitializers.iterator();

        while (iterator1.hasNext()) {
            AbstractMethodInfo abstractMethodInfo2 = (AbstractMethodInfo) iterator1.next();
            List list3 = null;
            List list4 = null;
            if (abstractMethodInfo2.isProgramMember()) {
                list3 = classRepository1.getReflectedMethods((MethodInfo) abstractMethodInfo2);
                list4 = classRepository1.getReflectedClasses((MethodInfo) abstractMethodInfo2);
            }

            this.collectInitializerDependencies(abstractMethodInfo2, list4, list3, hashSet, setMultiMap1, setMultiMap);
        }

        this.initializersByCallee = setMultiMap1.toListMultimap();
        setMultiMap1.clear();
        this.calleesByInitializer = new ListMultimap(this.staticInitializers.size());
        iterator1 = this.initializersByCallee.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            Iterator iterator3 = ((List) entry.getValue()).iterator();

            while (iterator3.hasNext()) {
                AbstractMethodInfo abstractMethodInfo3 = (AbstractMethodInfo) iterator3.next();
                AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) entry.getKey();
                this.calleesByInitializer.addValue(abstractMethodInfo3, abstractMethodInfo1);
            }
        }

        SetMultiMap setMultiMap2 = new SetMultiMap(this.staticInitializers.size());
        List list2 = classHierarchy1.getTopLoadedNodes();
        Iterator iterator4 = list2.iterator();

        while (iterator4.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator4.next();
            this.propagateInitDependencies(classHierarchyNode, ZkmUtils.createHashSet(), setMultiMap, setMultiMap2);
        }

        this.initDependenciesByClass = setMultiMap2.toListMultimap();
        setMultiMap.clear();
        setMultiMap2.clear();
        iterator4 = this.initDependenciesByClass.entrySet().iterator();

        while (iterator4.hasNext()) {
            Entry entry1 = (Entry) iterator4.next();
            ClassFileBase classFileBase3 = (ClassFileBase) entry1.getKey();
            List list1 = (List) entry1.getValue();
            if (classFileBase3.isMultiRelease()) {
                Iterator iterator = classFileBase3.getAllVersions().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
                    list1.remove(classFileBase1);
                }
            } else {
                list1.remove(classFileBase3);
            }
        }

        if (bl && this.staticInitializers.size() > 0 && scriptEnvironment1.isVerbose()) {
            PrintWriter printWriter = scriptEnvironment1.getLogWriter();
            if (printWriter != null) {
                this.logExcludedCallees(printWriter, flowObfuscationExclusions);
            }
        }
    }
}
