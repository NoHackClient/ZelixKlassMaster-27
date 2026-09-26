package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.changelog.ChangeLogMethodSignature;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.MemberSignatureBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterExclusions;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DisableableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.PairValueMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MethodRenamer {
    public final PairValueMap methodNamePairs = new PairValueMap();
    public MethodOverrideAnalyzer overrideAnalyzer;
    public final ClassHierarchy classHierarchy;
    private final ProgramClass[] programClasses;
    public final ClassFileBase[] allClassFiles;
    public final ClassRepository classRepository;
    public final int keepInnerClassInfoMode;
    public boolean overloadByReturnType;
    public boolean avoidNameOverloading;
    public boolean randomizeNames;
    public final NameExclusionSet exclusionSet;
    public final MethodParameterExclusions parameterExclusions;
    public final FixedClassesExclusionSet fixedClassesExclusions;
    public final ChangeLogMapping changeLogMapping;
    public final boolean changeLogTakesPrecedence;
    public final HashMap classRenameMap;
    public final TwoKeyMap oldToNewMethodMap;
    public final TwoKeyMap newToOldMethodMap;
    public Map keepNameMethods;
    public ScriptEnvironment scriptEnvironment;

    public String getOriginalMemberName(String string, MemberSignatureBase memberSignatureBase) {
        MethodSignature methodSignature1 = null;
        if (memberSignatureBase instanceof MethodSignature) {
            methodSignature1 = (MethodSignature) this.newToOldMethodMap.getValue(string, (MethodSignature) memberSignatureBase);
        } else if (memberSignatureBase instanceof FieldNameTypeSignature) {
            FieldNameTypeSignature fieldNameTypeSignature = (FieldNameTypeSignature) memberSignatureBase;
            Map map1 = this.newToOldMethodMap.getInnerMap(string);
            if (map1 != null) {
                Iterator iterator = map1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    MethodSignature methodSignature2 = (MethodSignature) entry.getKey();
                    if (fieldNameTypeSignature.matchesMethod(methodSignature2)) {
                        methodSignature1 = (MethodSignature) entry.getValue();
                        break;
                    }
                }
            }
        }

        return methodSignature1 != null ? methodSignature1.getName() : memberSignatureBase.getName();
    }

    public void reserveExcludedMethodNames(Set set1) throws ZkmException, IOException {
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            MethodSignature methodSignature1 = methodInfo1.getSignature();
            ClassFileBase classFileBase = (ClassFileBase) methodInfo1.getParent();
            if (classFileBase.isInterface() && !this.overrideAnalyzer.isSignatureReserved(methodSignature1)) {
                String string = classFileBase.getClassName();
                String string1 = methodSignature1.getName();
                if (this.isShortName(string1)) {
                    ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
                    ObservableHolder observableHolder = new ObservableHolder();
                    boolean bl = this.overrideAnalyzer.tryReserveRename(classHierarchyNode, methodSignature1, methodSignature1, observableHolder);
                    if (!bl) {
                        this.overrideAnalyzer.reserveSignature(methodSignature1);
                    }
                } else {
                    this.overrideAnalyzer.reserveSignature(methodSignature1);
                }
            }
        }

        iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo2 = (MethodInfo) iterator.next();
            MethodSignature methodSignature2 = methodInfo2.getSignature();
            ClassFileBase classFileBase1 = (ClassFileBase) methodInfo2.getParent();
            if (!classFileBase1.isInterface() && !this.overrideAnalyzer.isSignatureReserved(methodSignature2)) {
                String string2 = classFileBase1.getClassName();
                String string3 = methodSignature2.getName();
                if (this.isShortName(string3)) {
                    ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(string2);
                    ObservableHolder observableHolder1 = new ObservableHolder();
                    boolean bl1 = this.overrideAnalyzer
                            .tryReserveMethodRename(classHierarchyNode1, methodSignature2, methodSignature2, methodInfo2, observableHolder1);
                    if (!bl1) {
                        this.overrideAnalyzer.reserveSignature(methodSignature2);
                    }
                } else {
                    this.overrideAnalyzer.reserveSignature(methodSignature2);
                }
            }
        }
    }

    public ScriptEnvironment getScriptEnvironment() {
        return this.scriptEnvironment;
    }

    public boolean tryRecordRename(
            ClassHierarchyNode classHierarchyNode,
            MethodSignature methodSignature1,
            MethodSignature methodSignature2,
            AbstractMethodInfo abstractMethodInfo,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        return classHierarchyNode.isInterface()
                ? this.overrideAnalyzer.tryReserveRename(classHierarchyNode, methodSignature2, methodSignature1, observableHolder)
                : this.overrideAnalyzer.tryReserveMethodRename(classHierarchyNode, methodSignature2, methodSignature1, abstractMethodInfo, observableHolder);
    }

    public TwoKeyMap collectUnopenedClassMappings() throws IOException {
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        if (this.changeLogMapping != null) {
            Iterator iterator = this.changeLogMapping.getNewClassNames().iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                if (!ClassHierarchyNode.isProgramClassName(string)) {
                    ArrayList arrayList = this.changeLogMapping.getMethodMappingPairs(string);
                    if (arrayList != null) {
                        Iterator iterator1 = arrayList.iterator();

                        while (iterator1.hasNext()) {
                            ObjectPair objectPair = (ObjectPair) iterator1.next();
                            twoKeyMap.putValue(string, objectPair.getFirst(), objectPair.getSecond());
                        }
                    }
                }
            }
        }

        return twoKeyMap;
    }

    public void applyUnopenedClassMappings(TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        if (this.changeLogMapping != null) {
            Enumeration enumeration = twoKeyMap.keys();

            while (enumeration.hasMoreElements()) {
                String string = (String) enumeration.nextElement();
                Iterator iterator = twoKeyMap.getInnerMap(string).entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    this.oldToNewMethodMap.putValue(string, entry.getKey(), entry.getValue());
                    this.newToOldMethodMap.putValue(string, entry.getValue(), entry.getKey());
                }

                ClasspathClassFile classpathClassFile = (ClasspathClassFile) ClassHierarchyNode.findClassFile(string);
                classpathClassFile.applyMethodNameMapping(this.oldToNewMethodMap, this.methodNamePairs);
            }
        }
    }

    public final void checkChangeLogOverrideMapping(ClassFileBase classFileBase, AbstractMethodInfo abstractMethodInfo) throws IOException {
        String string = classFileBase.getClassName();
        MethodSignature methodSignature1 = abstractMethodInfo.getSignature();
        ChangeLogMethodSignature changeLogMethodSignature = this.changeLogMapping.lookupMethodMapping(string, methodSignature1);
        if (changeLogMethodSignature != null && !abstractMethodInfo.isStatic() && !abstractMethodInfo.isStrictlyPrivate()) {
            ClassFileBase classFileBase1 = this.overrideAnalyzer.getRootMethodClass(abstractMethodInfo);
            if (classFileBase1 != null) {
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(classFileBase1.getClassName());
                if (classHierarchyNode != null && !classHierarchyNode.hasNoClassFile()) {
                    ChangeLogMethodSignature changeLogMethodSignature1 = changeLogMethodSignature;
                    changeLogMethodSignature = this.changeLogMapping
                            .reconcileInheritedMethodMapping(string, classFileBase1.getClassName(), methodSignature1, changeLogMethodSignature, this.classRenameMap);
                    if (!changeLogMethodSignature.equals(changeLogMethodSignature1)) {
                    }
                } else if (!methodSignature1.getName().equals(changeLogMethodSignature.getName())) {
                    String string1 = (String) ZkmUtils.mapOrSelf(string, this.classRenameMap);
                    this.changeLogMapping
                            .logWarning(
                                    "Method '"
                                            + methodSignature1.getNameWithParameters(this.classRenameMap)
                                            + "' in class '"
                                            + ZkmUtils.slashesToDots(string1)
                                            + "' could not be renamed to '"
                                            + changeLogMethodSignature.getName()
                                            + "' because it now overrides a method in '"
                                            + classFileBase1.getOriginalDottedName()
                                            + "' which has not been opened."
                            );
                    this.changeLogMapping.removeMethodMapping(string, methodSignature1);
                }
            }
        }
    }

    public ObjectPair findAssignedNamePair(Set set1, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.setValue(null);
        if (set1 != null) {
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator.next();
                ObjectPair objectPair = this.methodNamePairs.getPair(abstractMethodInfo);
                if (objectPair != null) {
                    observableHolder.setValue(this.exclusionSet.getLinkPrefix(abstractMethodInfo));
                    return objectPair;
                }
            }
        }

        return null;
    }

    public final void applyChangeLogMethodMapping(ClassHierarchyNode classHierarchyNode, ClassFileBase classFileBase, AbstractMethodInfo abstractMethodInfo) throws ZkmException, IOException {
        String string = classFileBase.getClassName();
        MethodSignature methodSignature1 = abstractMethodInfo.getSignature();
        MethodSignature methodSignature2 = this.changeLogMapping.getNewMethodSignature(string, methodSignature1);
        if (methodSignature2 != null) {
            ObservableHolder observableHolder = new ObservableHolder();
            if (this.overrideAnalyzer.isSignatureReserved(methodSignature2)) {
                if (this.overloadByReturnType && !methodSignature2.equals(methodSignature1)
                        || !this.overloadByReturnType && !methodSignature2.getNameTypeSignature().equals(methodSignature1.getNameTypeSignature())) {
                    if (classHierarchyNode.isProgramClass()) {
                        this.changeLogMapping
                                .logWarning(
                                        "Method '"
                                                + abstractMethodInfo.toOriginalDisplayString()
                                                + "' in class '"
                                                + classFileBase.getOriginalDottedName()
                                                + "' could not be renamed to '"
                                                + methodSignature2.getName()
                                                + "' because of a possible name clash. (1)"
                                );
                        this.changeLogMapping.removeMethodMapping(string, methodSignature1);
                    } else {
                        this.changeLogMapping
                                .reportError(
                                        "Method '"
                                                + abstractMethodInfo.toOriginalDisplayString()
                                                + "' in class '"
                                                + classFileBase.getOriginalDottedName()
                                                + "' could not be renamed to '"
                                                + methodSignature2.getName()
                                                + "' because of a possible name clash. (2)"
                                );
                        this.changeLogMapping.removeMethodMapping(string, methodSignature1);
                    }
                }
            } else if ((
                    this.overloadByReturnType && methodSignature2.equals(methodSignature1)
                            || !this.overloadByReturnType && methodSignature2.getNameTypeSignature().equals(methodSignature1.getNameTypeSignature())
            )
                    && !this.isShortName(methodSignature1.getName())) {
                this.overrideAnalyzer.reserveSignature(methodSignature2);
            } else if (!this.tryRecordRename(classHierarchyNode, methodSignature1, methodSignature2, abstractMethodInfo, observableHolder)) {
                String string2 = (String) ZkmUtils.mapOrSelf(string, this.classRenameMap);
                MemberSignatureBase memberSignatureBase = (MemberSignatureBase) observableHolder.getValue();
                String string1 = "";
                if (memberSignatureBase != null) {
                    string1 = " with method '" + memberSignatureBase.getNameWithParameters(this.classRenameMap) + "'";
                }

                if (classHierarchyNode.isProgramClass()) {
                    this.changeLogMapping
                            .logWarning(
                                    "Method '"
                                            + abstractMethodInfo.toOriginalDisplayString()
                                            + "' in class '"
                                            + classFileBase.getOriginalDottedName()
                                            + "' could not be renamed to '"
                                            + methodSignature2.getName()
                                            + "' because of a name clash"
                                            + string1
                                            + ". (3)"
                            );
                    this.changeLogMapping.removeMethodMapping(string, methodSignature1);
                } else {
                    this.changeLogMapping
                            .reportError(
                                    "Method '"
                                            + abstractMethodInfo.toOriginalDisplayString()
                                            + "' in class '"
                                            + classFileBase.getOriginalDottedName()
                                            + "' could not be renamed to '"
                                            + methodSignature2.getName()
                                            + "' because of a possible name clash"
                                            + string1
                                            + ". (4)"
                            );
                    this.changeLogMapping.removeMethodMapping(string, methodSignature1);
                }
            }
        }
    }

    public MethodRenamer(
            NameExclusionSet nameExclusionSet,
            MethodParameterExclusions methodParameterExclusions,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            ChangeLogMapping changeLogMapping1,
            boolean changeLogTakesPrecedence,
            ClassRepository classRepository1,
            ProgramClass[] programClass1,
            ClassFileBase[] classFileBases,
            ClassHierarchy classHierarchy1,
            ClassResolver classResolver1,
            int keepInnerClassInfoMode,
            boolean overloadByReturnType,
            boolean avoidNameOverloading,
            boolean randomizeNames,
            boolean bl4,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ObservableHolder observableHolder,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        this.overrideAnalyzer = new MethodOverrideAnalyzer(
                classRepository1, classHierarchy1, classResolver1, scriptEnvironment1, programClass1.length, overloadByReturnType, bl4, false
        );
        observableHolder.setValue(this.overrideAnalyzer.getRootMethodMap());
        this.classHierarchy = classHierarchy1;
        this.programClasses = programClass1;
        this.allClassFiles = classFileBases;
        this.classRepository = classRepository1;
        this.keepInnerClassInfoMode = keepInnerClassInfoMode;
        this.overloadByReturnType = overloadByReturnType;
        this.avoidNameOverloading = avoidNameOverloading;
        this.randomizeNames = randomizeNames;
        this.exclusionSet = nameExclusionSet;
        this.parameterExclusions = methodParameterExclusions;
        this.fixedClassesExclusions = fixedClassesExclusionSet1;
        this.changeLogMapping = changeLogMapping1;
        this.changeLogTakesPrecedence = changeLogTakesPrecedence;
        this.classRenameMap = hashMap;
        this.oldToNewMethodMap = twoKeyMap;
        this.newToOldMethodMap = twoKeyMap1;
        this.keepNameMethods = map1;
        this.scriptEnvironment = scriptEnvironment1;
    }

    public MethodOverrideAnalyzer getOverrideAnalyzer() {
        return this.overrideAnalyzer;
    }

    public void applyChangeLogInterfaceMappings() throws ZkmException, IOException {
        if (this.changeLogMapping != null) {
            Iterator iterator = this.overrideAnalyzer.getMethodSignatureByGroup().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                this.applyInterfaceSetMapping((ObservableHolder) entry.getKey(), (MethodSignature) entry.getValue());
            }

            List list1 = this.classHierarchy.getTopLoadedNodes();
            int bb = list1.size();

            for (int i = 0; i < bb; i++) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) list1.get(i);
                if (classHierarchyNode.isInterface()) {
                    classHierarchyNode.collectRenamableMethods(this);
                }
            }

            for (int i = 0; i < bb; i++) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
                if (!classHierarchyNode1.isInterface()) {
                    classHierarchyNode1.collectRenamableMethods(this);
                }
            }
        }
    }

    public boolean isShortName(String string) {
        return string.length() <= 2;
    }

    public void applyInterfaceSetMapping(ObservableHolder observableHolder, MethodSignature methodSignature1) throws ZkmException, IOException {
        List list1 = (List) observableHolder.getValue();
        MethodSignature methodSignature2 = null;

        for (int i = 0; i < list1.size(); i++) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) list1.get(i);
            MethodSignature methodSignature3 = this.changeLogMapping.getNewMethodSignature(classHierarchyNode.getClassName(), methodSignature1);
            if (methodSignature3 != null) {
                if (methodSignature2 == null) {
                    methodSignature2 = methodSignature3;
                } else if (!methodSignature3.equals(methodSignature2)) {
                    this.rejectInterfaceMappings(list1, methodSignature1, "clashing mappings in between related interfaces");
                    methodSignature2 = null;
                    break;
                }
            }
        }

        if (methodSignature2 != null) {
            ObservableHolder observableHolder1 = new ObservableHolder();
            boolean bl = this.overrideAnalyzer.tryRenameGroup(observableHolder, methodSignature2, methodSignature1, observableHolder1);
            if (!bl) {
                this.rejectInterfaceMappings(list1, methodSignature1, "clashing mappings in between related interface sets");
            }
        }
    }

    public String getNewMethodName(String string, MethodSignature methodSignature1) {
        MethodSignature methodSignature2 = (MethodSignature) ZkmUtils.twoKeyMapOrSelf(string, methodSignature1, this.oldToNewMethodMap);
        return methodSignature2 != null ? methodSignature2.getName() : null;
    }

    public void validateChangeLogMappings() throws IOException {
        if (this.changeLogMapping != null) {
            List list1 = this.classHierarchy.getTopLoadedNodes();
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) list1.get(i);
                if (classHierarchyNode.isInterface()) {
                    classHierarchyNode.applyMethodRenamer(this);
                }
            }

            for (int i = 0; i < ba; i++) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
                if (!classHierarchyNode1.isInterface()) {
                    classHierarchyNode1.applyMethodRenamer(this);
                }
            }
        }
    }

    public void rejectInterfaceMappings(List list1, MethodSignature methodSignature1, String string) throws IOException {
        for (int i = 0; i < list1.size(); i++) {
            String string1 = ((ClassHierarchyNode) list1.get(i)).getClassName();
            ChangeLogMethodSignature changeLogMethodSignature = this.changeLogMapping.lookupMethodMapping(string1, methodSignature1);
            if (changeLogMethodSignature != null) {
                this.changeLogMapping.removeMethodMapping(string1, methodSignature1);
                this.changeLogMapping
                        .logWarning(
                                "Could not rename method '"
                                        + methodSignature1.getNameWithParameters(this.classRenameMap)
                                        + "' to '"
                                        + changeLogMethodSignature.getName()
                                        + " in interface '"
                                        + ZkmUtils.slashesToDots((String) ZkmUtils.mapOrSelf(string1, this.classRenameMap))
                                        + "' because of '"
                                        + string
                                        + "'."
                        );
            }
        }
    }

    public void renameMethods(MethodNameGeneratorBase methodNameGeneratorBase) throws ZkmException, IOException {
        TwoKeyMap twoKeyMap = null;
        if (this.changeLogMapping != null) {
            twoKeyMap = this.collectUnopenedClassMappings();
            this.validateChangeLogMappings();
        }

        HashSet hashSet = ZkmUtils.createHashSet();
        Enumeration enumeration = this.exclusionSet.getExcludedMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
            AbstractMethodInfo abstractMethodInfo = null;
            if (!methodInfo1.isStrictlyPrivate() && !methodInfo1.isStatic()) {
                abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
            }

            if (abstractMethodInfo != null) {
                if (abstractMethodInfo.isProgramMember()) {
                    this.exclusionSet.excludeMethodFor((MethodInfo) abstractMethodInfo, methodInfo1);
                    this.exclusionSet.unexcludeMethodSilently(methodInfo1);
                    hashSet.add((MethodInfo) abstractMethodInfo);
                }
            } else {
                hashSet.add(methodInfo1);
            }
        }

        if (this.parameterExclusions != null) {
            Enumeration enumeration1 = this.parameterExclusions.getIncludedMethods();

            while (enumeration1.hasMoreElements()) {
                MethodInfo methodInfo4 = (MethodInfo) enumeration1.nextElement();
                AbstractMethodInfo abstractMethodInfo1 = null;
                if (!methodInfo4.isStrictlyPrivate() && !methodInfo4.isStatic()) {
                    abstractMethodInfo1 = this.overrideAnalyzer.findRootMethod(methodInfo4);
                }

                if (abstractMethodInfo1 != null && abstractMethodInfo1.isProgramMember()) {
                    this.parameterExclusions.excludeLinkedMethod((MethodInfo) abstractMethodInfo1, methodInfo4);
                    this.parameterExclusions.restoreMethod(methodInfo4);
                }
            }

            enumeration1 = this.parameterExclusions.getIncludedMethods();

            while (enumeration1.hasMoreElements()) {
                MethodInfo methodInfo5 = (MethodInfo) enumeration1.nextElement();
                ProgramClass programClass2 = methodInfo5.getOwnerProgramClass();
                MethodSignature methodSignature1 = methodInfo5.getSignature();
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass2.getClassName());
                if (programClass2.isInterface()) {
                    ObservableHolder observableHolder = this.overrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
                    if (observableHolder != null) {
                        List list1 = (List) observableHolder.getValue();

                        for (int i = 0; i < list1.size(); i++) {
                            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
                            if (classHierarchyNode1.isProgramClass() && classHierarchyNode1 != classHierarchyNode) {
                                MethodInfo methodInfo2 = this.classRepository.findDeclaredMethod(classHierarchyNode1.getProgramClass(), methodSignature1);
                                if (methodInfo2 != null) {
                                    this.parameterExclusions.excludeLinkedMethod(methodInfo2, methodInfo5);
                                }
                            }
                        }
                    }
                }
            }
        }

        this.exclusionSet.excludeInconsistentLinkedMembers(this.overrideAnalyzer, this.classRenameMap);
        enumeration = this.exclusionSet.getExcludedMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo3 = (MethodInfo) enumeration.nextElement();
            ProgramClass programClass1 = methodInfo3.getOwnerProgramClass();
            MethodSignature methodSignature2 = methodInfo3.getSignature();
            ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.findNode(programClass1.getClassName());
            if (programClass1.isInterface()) {
                ObservableHolder observableHolder1 = this.overrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode2, methodSignature2);
                if (observableHolder1 != null) {
                    List list3 = (List) observableHolder1.getValue();

                    for (int i = 0; i < list3.size(); i++) {
                        ClassHierarchyNode classHierarchyNode5 = (ClassHierarchyNode) list3.get(i);
                        if (classHierarchyNode5.isProgramClass() && classHierarchyNode5 != classHierarchyNode2) {
                            MethodInfo methodInfo6 = this.classRepository.findDeclaredMethod(classHierarchyNode5.getProgramClass(), methodSignature2);
                            if (methodInfo6 != null) {
                                this.exclusionSet.excludeMethodFor(methodInfo6, methodInfo3);
                            }
                        }
                    }
                }
            }
        }

        if (this.changeLogMapping != null) {
            if (!this.changeLogTakesPrecedence) {
                this.exclusionSet.unexcludeRenamedMethods(this.changeLogMapping, this.overrideAnalyzer, this.classRenameMap, this.fixedClassesExclusions);
                if (this.parameterExclusions != null) {
                    FixedClassesExclusionSet fixedClassesExclusionSet1 = this.fixedClassesExclusions;
                    this.parameterExclusions.includeChangeLogObfuscatedMethods(this.changeLogMapping, this.overrideAnalyzer, fixedClassesExclusionSet1);
                }
            } else {
                this.changeLogMapping.applyLooseMethodExclusions(this.exclusionSet, this.classRenameMap, this.classRepository);
                if (this.parameterExclusions != null) {
                    this.changeLogMapping.removeExcludedParameterObfuscatedMethods(this.parameterExclusions, this.overrideAnalyzer);
                }
            }
        }

        this.reserveExcludedMethodNames(hashSet);
        this.applyChangeLogInterfaceMappings();
        if (this.changeLogMapping != null) {
            this.applyUnopenedClassMappings(twoKeyMap);
        }

        HashSet hashSet3 = ZkmUtils.createHashSet();
        List list2 = this.classHierarchy.getTopLoadedNodes();
        int bd = list2.size();
        DisableableMap disableableMap4;
        if (this.avoidNameOverloading) {
            disableableMap4 = new DisableableMap(false);
        } else {
            disableableMap4 = new DisableableMap(true);
        }

        for (int i = 0; i < bd; i++) {
            ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) list2.get(i);
            if (classHierarchyNode3.isInterface()) {
                HashMap hashMap4 = ZkmUtils.createHashMap();
                HashMap hashMap6 = ZkmUtils.createHashMap();
                ListMultimap listMultimap2 = new ListMultimap(13);
                DisableableMap disableableMap5;
                ClassHierarchyNode classHierarchyNode7;
                NameExclusionSet nameExclusionSet3;
                if (this.avoidNameOverloading) {
                    disableableMap5 = new DisableableMap(false);
                    classHierarchyNode7 = classHierarchyNode3;
                    nameExclusionSet3 = this.exclusionSet;
                } else {
                    disableableMap5 = new DisableableMap(true);
                    classHierarchyNode7 = classHierarchyNode3;
                    nameExclusionSet3 = this.exclusionSet;
                }

                Map map1 = this.keepNameMethods;
                HashSet hashSet1 = hashSet3;
                boolean randomizeNames = this.randomizeNames;
                boolean overloadByReturnType = this.overloadByReturnType;
                DisableableMap disableableMap = disableableMap5;
                DisableableMap disableableMap1 = disableableMap4;
                HashMap hashMap = hashMap6;
                int keepInnerClassInfoMode = this.keepInnerClassInfoMode;
                ListMultimap listMultimap = listMultimap2;
                HashMap hashMap1 = hashMap4;
                MethodNameGeneratorBase methodNameGeneratorBase1 = methodNameGeneratorBase;
                MethodOverrideAnalyzer methodOverrideAnalyzer = this.overrideAnalyzer;
                MethodRenamer methodRenamer2 = this;
                ChangeLogMapping changeLogMapping1 = this.changeLogMapping;
                NameExclusionSet nameExclusionSet = nameExclusionSet3;
                classHierarchyNode7.renameMethodsRecursively(
                        nameExclusionSet,
                        changeLogMapping1,
                        methodRenamer2,
                        methodOverrideAnalyzer,
                        methodNameGeneratorBase1,
                        hashMap1,
                        listMultimap,
                        keepInnerClassInfoMode,
                        hashMap,
                        disableableMap1,
                        disableableMap,
                        overloadByReturnType,
                        randomizeNames,
                        hashSet1,
                        map1
                );
            }
        }

        for (int i = 0; i < bd; i++) {
            ClassHierarchyNode classHierarchyNode4 = (ClassHierarchyNode) list2.get(i);
            if (!classHierarchyNode4.isInterface()) {
                HashMap hashMap5 = ZkmUtils.createHashMap();
                HashMap hashMap7 = ZkmUtils.createHashMap();
                ListMultimap listMultimap3 = new ListMultimap(13);
                DisableableMap disableableMap6;
                ClassHierarchyNode classHierarchyNode6;
                NameExclusionSet nameExclusionSet2;
                if (this.avoidNameOverloading) {
                    disableableMap6 = new DisableableMap(false);
                    classHierarchyNode6 = classHierarchyNode4;
                    nameExclusionSet2 = this.exclusionSet;
                } else {
                    disableableMap6 = new DisableableMap(true);
                    classHierarchyNode6 = classHierarchyNode4;
                    nameExclusionSet2 = this.exclusionSet;
                }

                Map map2 = this.keepNameMethods;
                HashSet hashSet2 = hashSet3;
                boolean bl2 = this.randomizeNames;
                boolean bl3 = this.overloadByReturnType;
                DisableableMap disableableMap2 = disableableMap6;
                DisableableMap disableableMap3 = disableableMap4;
                HashMap hashMap2 = hashMap7;
                int bc = this.keepInnerClassInfoMode;
                ListMultimap listMultimap1 = listMultimap3;
                HashMap hashMap3 = hashMap5;
                MethodNameGeneratorBase methodNameGeneratorBase2 = methodNameGeneratorBase;
                MethodOverrideAnalyzer methodOverrideAnalyzer1 = this.overrideAnalyzer;
                MethodRenamer methodRenamer3 = this;
                ChangeLogMapping changeLogMapping2 = this.changeLogMapping;
                NameExclusionSet nameExclusionSet1 = nameExclusionSet2;
                classHierarchyNode6.renameMethodsRecursively(
                        nameExclusionSet1,
                        changeLogMapping2,
                        methodRenamer3,
                        methodOverrideAnalyzer1,
                        methodNameGeneratorBase2,
                        hashMap3,
                        listMultimap1,
                        bc,
                        hashMap2,
                        disableableMap3,
                        disableableMap2,
                        bl3,
                        bl2,
                        hashSet2,
                        map2
                );
            }
        }

        TwoKeyMap twoKeyMap1 = this.oldToNewMethodMap;
        ClassFileBase[] classFileBases = this.allClassFiles;
        this.classRepository.applyMethodRenamesToVersions(classFileBases, twoKeyMap1);
        this.classRepository.resetClassReferences(this.allClassFiles, this.scriptEnvironment, false);
        this.classRepository.indexMethods();
    }

    public MethodSignature recordMethodName(MethodInfo methodInfo1, String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        observableHolder.clearValue();
        String string1 = methodInfo1.getClassName();
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        MethodSignature methodSignature2;
        if (string != null) {
            methodSignature2 = new MethodSignature(string, methodSignature1.getDescriptor());
            this.methodNamePairs.putPair(methodInfo1, methodSignature1.getName(), string);
            if (methodInfo1.isNameChanged()) {
                MethodSignature methodSignature3 = new MethodSignature(string, MethodParameterExclusions.getPackedDescriptor(methodInfo1));
                observableHolder.setValue(methodSignature3);
            }
        } else {
            methodSignature2 = methodSignature1;
            this.methodNamePairs.putPair(methodInfo1, methodInfo1.getSourceName(), methodInfo1.getSourceName());
        }

        MethodSignature methodSignature5 = (MethodSignature) this.oldToNewMethodMap.putValue(string1, methodSignature1, methodSignature2);
        MethodSignature methodSignature4 = (MethodSignature) this.newToOldMethodMap.putValue(string1, methodSignature2, methodSignature1);
        if (methodSignature4 != null) {
            ZkmAssert.assertTrue(
                    false,
                    new String[]{
                            "Duplicate method name in class "
                                    + ZkmUtils.slashesToDots((String) ZkmUtils.mapOrSelf(string1, this.classRenameMap))
                                    + ". Both '"
                                    + methodSignature1.formatDeclaration(this.classRenameMap)
                                    + "' and '"
                                    + methodSignature4.formatDeclaration(this.classRenameMap)
                                    + "' renamed to '"
                                    + methodSignature2.getName()
                                    + "'."
                    }
            );
        }

        return methodSignature2;
    }
}
