package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MemberSignatureBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.engine.ObfuscationEngine;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationManager;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.PairMultiMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SetValuedMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.UniqueWorkQueue;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class MethodParameterChangeSet extends OpaquePredicateBase {
    public Map initNodeMap;
    public final Map originalSignatures = ZkmUtils.createHashMap();
    public final Map changedDescriptors = ZkmUtils.createHashMap();
    public final MethodOverrideAnalyzer overrideAnalyzer;
    public final ClassHierarchy classHierarchy;
    public final CommonSuperTypeResolver superTypeResolver;
    public final ProgramClass[] programClasses;
    public final ClassRepository classRepository;
    public final int changeLevel;
    public final boolean compareFullSignatures;
    public final boolean shuffleMethods;
    public final MethodParameterExclusions exclusions;
    public final FixedClassesExclusionSet fixedClassesExclusions;
    public final ChangeLogMapping changeLogMapping;
    public final boolean useChangeLogExclusions;
    public final Map keyedMethodMap;
    public final Map superTypeMap;
    public final Set excludedMethods;
    public final HashMap classNameMap;
    public final TwoKeyMap originalToCurrentSignatures;
    public final TwoKeyMap currentToOriginalSignatures;
    public final ObservableHolder defaultHelperHolder;
    public final Map archiveHelperClasses;
    public final String helperClassPrefix;
    public final SetValuedMap helperClassUsers;
    public Map descriptorOverrides;
    public final BooleanFlag changesEnabled;
    public final boolean includeInterfaces;
    public final ScriptEnvironment scriptEnvironment;
    public Object helperInstance;

    public void rejectInterfaceMappings(List list1, MethodSignature methodSignature1, String string) {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            ClassFileBase classFileBase = ((ClassHierarchyNode) iterator.next()).getClassFile();
            if (classFileBase != null) {
                AbstractMethodInfo abstractMethodInfo = classFileBase.findMethod(methodSignature1);
                if (abstractMethodInfo != null && this.changeLogMapping.hasAddedParametersEntry(abstractMethodInfo)) {
                    AddedParameter[] addedParameters1 = this.changeLogMapping.getAddedParameters(abstractMethodInfo);
                    this.changeLogMapping.removeAddedParameters(abstractMethodInfo);
                    if (addedParameters1.length > 0) {
                        MethodSignature methodSignature2 = ChangeLogMapping.createChangedMethodSignature(abstractMethodInfo, addedParameters1);
                        this.changeLogMapping
                                .logWarning(
                                        "Could not change parameter list in method '"
                                                + abstractMethodInfo.getOriginalNameWithParameters()
                                                + "' to '"
                                                + methodSignature2.toDeclarationString()
                                                + " in interface '"
                                                + abstractMethodInfo.getOriginalDottedName()
                                                + "' because of '"
                                                + string
                                                + "'."
                                );
                    }
                }
            }
        }
    }

    public Set resolveDeclaringMethods(Collection collection1) {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            MethodSignature methodSignature1 = methodInfo1.getSignature();
            ClassFileBase classFileBase = null;
            if (!methodInfo1.isStrictlyPrivate() && !methodInfo1.isStatic()) {
                classFileBase = this.overrideAnalyzer.getRootMethodClass(methodInfo1);
            }

            if (classFileBase != null) {
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(classFileBase.getClassName());
                if (classHierarchyNode != null && classHierarchyNode.isProgramClass()) {
                    ProgramClass programClass1 = classHierarchyNode.getProgramClass();
                    MethodInfo methodInfo2 = this.classRepository.findDeclaredMethod(programClass1, methodSignature1);
                    hashSet.add(methodInfo2);
                }
            } else {
                hashSet.add(methodInfo1);
            }
        }

        return hashSet;
    }

    public final void validateMappedSignature(ClassHierarchyNode classHierarchyNode, ClassFileBase classFileBase, AbstractMethodInfo abstractMethodInfo) throws ZkmException, IOException {
        String string = classFileBase.getClassName();
        MethodSignature methodSignature1 = abstractMethodInfo.getSignature();
        if (this.changeLogMapping.hasAddedParameters(abstractMethodInfo)) {
            AddedParameter[] addedParameters1 = this.changeLogMapping.getAddedParameters(abstractMethodInfo);
            MethodSignature methodSignature2 = ChangeLogMapping.createChangedMethodSignature(abstractMethodInfo, addedParameters1);
            ObservableHolder observableHolder = new ObservableHolder();
            if (this.overrideAnalyzer.isSignatureReserved(methodSignature2)) {
                if (this.compareFullSignatures && !methodSignature2.equals(methodSignature1)
                        || !this.compareFullSignatures && !methodSignature2.getNameTypeSignature().equals(methodSignature1.getNameTypeSignature())) {
                    if (classHierarchyNode.isProgramClass()) {
                        this.changeLogMapping
                                .logWarning(
                                        "Method '"
                                                + abstractMethodInfo.toOriginalDisplayString()
                                                + "' in class '"
                                                + classFileBase.getOriginalDottedName()
                                                + "' could not have its parameter list changed to '"
                                                + methodSignature2.toDeclarationString()
                                                + "' because of a possible clash. (1)"
                                );
                    } else {
                        this.changeLogMapping
                                .reportError(
                                        "Method '"
                                                + abstractMethodInfo.toOriginalDisplayString()
                                                + "' in class '"
                                                + classFileBase.getOriginalDottedName()
                                                + "' could not have its parameter list changed to '"
                                                + methodSignature2.toDeclarationString()
                                                + "' because of a possible clash. (2)"
                                );
                    }

                    MethodSignature methodSignature3 = methodSignature1;
                    ChangeLogMapping changeLogMapping2;
                    if (abstractMethodInfo.isRenamed()) {
                        methodSignature3 = new MethodSignature(abstractMethodInfo.getOriginalMemberName(), abstractMethodInfo.getOriginalDescriptor());
                        changeLogMapping2 = this.changeLogMapping;
                    } else {
                        changeLogMapping2 = this.changeLogMapping;
                    }

                    changeLogMapping2.removeMethodMapping(string, methodSignature3);
                }
            } else if (!this.canChangeWithoutClash(classHierarchyNode, methodSignature1, methodSignature2, abstractMethodInfo, observableHolder)) {
                MemberSignatureBase memberSignatureBase = (MemberSignatureBase) observableHolder.getValue();
                String string1 = "";
                if (memberSignatureBase != null) {
                    string1 = " with method '" + memberSignatureBase.getNameWithParameters(this.classNameMap) + "'";
                }

                if (classHierarchyNode.isProgramClass()) {
                    this.changeLogMapping
                            .logWarning(
                                    "Method '"
                                            + abstractMethodInfo.toOriginalDisplayString()
                                            + "' in class '"
                                            + classFileBase.getOriginalDottedName()
                                            + "' could not have its parameter list changed to '"
                                            + methodSignature2.toDeclarationString()
                                            + "' because of a name clash"
                                            + string1
                                            + ". (3)"
                            );
                } else {
                    this.changeLogMapping
                            .reportError(
                                    "Method '"
                                            + abstractMethodInfo.toOriginalDisplayString()
                                            + "' in class '"
                                            + classFileBase.getOriginalDottedName()
                                            + "' could not have its parameter list changed to '"
                                            + methodSignature2.toDeclarationString()
                                            + "' because of a possible name clash"
                                            + string1
                                            + ". (4)"
                            );
                }

                MethodSignature methodSignature4 = methodSignature1;
                ChangeLogMapping changeLogMapping1;
                if (abstractMethodInfo.isRenamed()) {
                    methodSignature4 = new MethodSignature(abstractMethodInfo.getOriginalMemberName(), abstractMethodInfo.getOriginalDescriptor());
                    changeLogMapping1 = this.changeLogMapping;
                } else {
                    changeLogMapping1 = this.changeLogMapping;
                }

                changeLogMapping1.removeMethodMapping(string, methodSignature4);
            }
        }
    }

    public Map changeParameterLists(
            Set set1,
            Set set2,
            Set set3,
            Set set4,
            ListMultimap listMultimap,
            Set set5,
            ParameterListGenerator parameterListGenerator,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            FlowObfuscationManager flowObfuscationManager,
            MethodParameterChanger methodParameterChanger,
            ObservableHolder observableHolder,
            boolean bl
    ) throws ZkmException, IOException {
        this.loadOriginalSignatures();
        List list1 = null;
        MethodParameterExclusions methodParameterExclusions;
        if (this.changeLogMapping != null) {
            if (this.changeLogMapping.hasParameterChangeData()) {
                list1 = this.changeLogMapping.getLibraryMethodAddedParameters();
            }

            this.applyChangeLogToHierarchy();
            methodParameterExclusions = this.exclusions;
        } else {
            methodParameterExclusions = this.exclusions;
        }

        Enumeration enumeration = methodParameterExclusions.getIncludedMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo1 = (MethodInfo) enumeration.nextElement();
            AbstractMethodInfo abstractMethodInfo = null;
            if (!methodInfo1.isStrictlyPrivate() && !methodInfo1.isStatic()) {
                abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
            }

            if (abstractMethodInfo != null && abstractMethodInfo.isProgramMember()) {
                this.exclusions.excludeLinkedMethod((MethodInfo) abstractMethodInfo, methodInfo1);
                this.exclusions.restoreMethod(methodInfo1);
            }
        }

        enumeration = this.exclusions.getIncludedMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo4 = (MethodInfo) enumeration.nextElement();
            ProgramClass programClass3 = methodInfo4.getOwnerProgramClass();
            MethodSignature methodSignature1 = methodInfo4.getSignature();
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass3.getClassName());
            if (programClass3.isInterface()) {
                ObservableHolder observableHolder1 = this.overrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
                if (observableHolder1 != null) {
                    List list2 = (List) observableHolder1.getValue();

                    for (int i = 0; i < list2.size(); i += 1) {
                        ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list2.get(i);
                        if (classHierarchyNode1.isProgramClass() && classHierarchyNode1 != classHierarchyNode) {
                            MethodInfo methodInfo2 = this.classRepository.findDeclaredMethod(classHierarchyNode1.getProgramClass(), methodSignature1);
                            if (methodInfo2 != null) {
                                this.exclusions.excludeLinkedMethod(methodInfo2, methodInfo4);
                            }
                        }
                    }
                }
            }
        }

        if (this.changeLogMapping != null) {
            if (!this.useChangeLogExclusions) {
                this.exclusions.includeChangeLogChangedMethods(this.changeLogMapping, this.overrideAnalyzer, this.fixedClassesExclusions);
            } else {
                this.changeLogMapping.applyLooseParameterExclusions(this.exclusions, set1, this.classNameMap);
            }
        }

        MethodOverrideAnalyzer methodOverrideAnalyzer = this.overrideAnalyzer;
        this.addChangeLogMethods(this.changeLogMapping, set1, set5, methodOverrideAnalyzer);
        SetMultiMap setMultiMap4 = this.collectChangeableMethods(set1, listMultimap, this.exclusions, this.changeLogMapping, this.overrideAnalyzer);
        if (setMultiMap4.isEmpty() && HiddenOptionFlags.ABORT_ON_PARAMETER_CONFLICT) {
            this.changesEnabled.setValue(false);
            return null;
        }

        ListMultimap listMultimap1 = new ListMultimap();
        ListMultimap listMultimap2 = this.collectKeyedMethods(listMultimap1, listMultimap, set1, setMultiMap4);
        HashSet hashSet = ZkmUtils.createHashSet();
        Enumeration enumeration1 = this.exclusions.getIncludedMethods();

        while (enumeration1.hasMoreElements()) {
            MethodInfo methodInfo5 = (MethodInfo) enumeration1.nextElement();
            hashSet.add(methodInfo5);
        }

        if (!set5.isEmpty()) {
            this.reserveSignatures(set5);
        }

        this.reserveSignatures(hashSet);
        this.reserveUnchangedSignatures(setMultiMap4, hashSet);
        this.resolveInterfaceMappings();
        if (list1 != null) {
            this.applyChangeLogDescriptors(list1);
        }

        List list4 = this.classHierarchy.getTopProgramNodes();
        int bb = list4.size();

        for (int i = 0; i < bb; i += 1) {
            ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) list4.get(i);
            if (classHierarchyNode2.isInterface()) {
                SetMultiMap setMultiMap5 = new SetMultiMap();
                this.collectInheritedSignatures(classHierarchyNode2.getClassFile(), setMultiMap5);
                Map map3 = this.descriptorOverrides;
                boolean shuffleMethods = this.shuffleMethods;
                boolean compareFullSignatures = this.compareFullSignatures;
                SetMultiMap setMultiMap = setMultiMap5;
                MethodOverrideAnalyzer methodOverrideAnalyzer2 = this.overrideAnalyzer;
                SetMultiMap setMultiMap1 = setMultiMap4;
                ParameterListGenerator parameterListGenerator1 = parameterListGenerator;
                MethodParameterChangeSet methodParameterChangeSet1 = this;
                ChangeLogMapping changeLogMapping1 = this.changeLogMapping;
                classHierarchyNode2.applyParameterChanges(
                        changeLogMapping1, methodParameterChangeSet1, parameterListGenerator1, setMultiMap1, methodOverrideAnalyzer2, setMultiMap, compareFullSignatures, shuffleMethods, map3
                );
            }
        }

        for (int i = 0; i < bb; i += 1) {
            ClassHierarchyNode classHierarchyNode3 = (ClassHierarchyNode) list4.get(i);
            if (!classHierarchyNode3.isInterface()) {
                SetMultiMap setMultiMap6 = new SetMultiMap();
                this.collectInheritedSignatures(classHierarchyNode3.getClassFile(), setMultiMap6);
                Map map4 = this.descriptorOverrides;
                boolean bl3 = this.shuffleMethods;
                boolean bl4 = this.compareFullSignatures;
                SetMultiMap setMultiMap2 = setMultiMap6;
                MethodOverrideAnalyzer methodOverrideAnalyzer3 = this.overrideAnalyzer;
                SetMultiMap setMultiMap3 = setMultiMap4;
                ParameterListGenerator parameterListGenerator2 = parameterListGenerator;
                MethodParameterChangeSet methodParameterChangeSet2 = this;
                ChangeLogMapping changeLogMapping2 = this.changeLogMapping;
                classHierarchyNode3.applyParameterChanges(
                        changeLogMapping2, methodParameterChangeSet2, parameterListGenerator2, setMultiMap3, methodOverrideAnalyzer3, setMultiMap2, bl4, bl3, map4
                );
            }
        }

        if (this.scriptEnvironment.isVerbose()) {
            TreeSet treeSet = new TreeSet();
            ListMultimap listMultimap3 = new ListMultimap();
            Iterator iterator3 = this.changedDescriptors.keySet().iterator();

            while (iterator3.hasNext()) {
                MethodInfo methodInfo6 = (MethodInfo) iterator3.next();
                ProgramClass programClass1 = methodInfo6.getOwnerProgramClass();
                treeSet.add(programClass1);
                listMultimap3.addValue(programClass1, methodInfo6);
            }

            MethodNameComparator methodNameComparator = new MethodNameComparator(this);
            int be = 0;
            int bg = 0;
            PrintWriter printWriter = this.scriptEnvironment.getLogWriter();
            Iterator iterator = treeSet.iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator.next();
                be += 1;
                printWriter.println("\tChanged parameter lists in class '" + AbstractExclusionSpec.formatClass(programClass2, this.classRepository, false) + "'");
                List list3 = listMultimap3.getValues(programClass2);
                Collections.sort(list3, methodNameComparator);
                Iterator iterator1 = list3.iterator();

                while (iterator1.hasNext()) {
                    MethodInfo methodInfo3 = (MethodInfo) iterator1.next();
                    bg += 1;
                    String string = MethodSignature.formatParameterTypes(
                            ((ChangedMethodDescriptor) this.changedDescriptors.get(methodInfo3)).getDescriptor(), this.classNameMap
                    );
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(
                            "\t\tmethod '"
                                    + methodInfo3.getModifierString()
                                    + methodInfo3.toOriginalDisplayString()
                                    + "' => '"
                                    + methodInfo3.getOriginalMemberName()
                                    + string
                                    + "'"
                    );
                    if (methodInfo3.isRenamed()) {
                        stringBuilder.append(" (");
                        stringBuilder.append(methodInfo3.toDisplayString());
                        stringBuilder.append(')');
                    }

                    printWriter.println(stringBuilder.toString());
                }
            }

            if (this.scriptEnvironment.isVerbose()) {
                this.scriptEnvironment
                        .getLogWriter()
                        .println(
                                "\tMethod Parameter List Changing : Changed parameter lists in "
                                        + (bl ? "" : " ")
                                        + bg
                                        + " method"
                                        + (bg == 1 ? "" : 's')
                                        + " in "
                                        + (flowObfuscationManager != null ? "" : " ")
                                        + be
                                        + " class"
                                        + (be == 1 ? "" : "es")
                                        + "."
                        );
            }
        }

        this.initNodeMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(listMultimap2.getKeyCount()));
        label209:
        if (methodParameterChanger.isKeyChainingEnabled()) {
            boolean bl5;
            if (listMultimap2.getKeyCount() + listMultimap1.getKeyCount() <= 31) {
                if (!HiddenOptionFlags.CHECK_SMALL_CHANGE_SETS) {
                    break label209;
                }

                bl5 = HiddenOptionFlags.SKIP_PARAMETER_MAP_REBUILD;
            } else {
                bl5 = HiddenOptionFlags.SKIP_PARAMETER_MAP_REBUILD;
            }

            if (!bl5 && HiddenOptionFlags.REBUILD_PARAMETER_MAPS) {
                EnumerableMap enumerableMap = this.classRepository.getCumulativeNameMap();
                EnumerableMap enumerableMap1 = this.classRepository.getOriginalToCurrentNameMap();
                EnumerableMap enumerableMap2 = this.classRepository.getCurrentToOriginalNameMap();
                methodParameterChanger.generateKeyInfrastructure(
                        listMultimap2,
                        listMultimap1,
                        set2,
                        set3,
                        set4,
                        this.defaultHelperHolder,
                        this.archiveHelperClasses,
                        this.helperClassUsers,
                        this.helperClassPrefix,
                        enumerableMap,
                        enumerableMap1,
                        enumerableMap2,
                        this.initNodeMap,
                        observableHolder
                );
            }
        }

        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.initNodeMap.size()));
        Iterator iterator2 = this.initNodeMap.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry = (Entry) iterator2.next();
            MethodInfo methodInfo7 = (MethodInfo) entry.getKey();
            if (methodInfo7.isStaticInitializer()) {
                hashMap.put(methodInfo7.getOwnerProgramClass(), entry.getValue());
            }
        }

        ClassResolver classResolver2 = this.classRepository.getClassResolver();
        ProgramClass[] programClass4 = this.programClasses;
        int bf = programClass4.length;

        for (int i = 0; i < bf; i += 1) {
            ProgramClass programClass5 = programClass4[i];
            ArrayList arrayList1 = new ArrayList();
            programClass5.applyStaticInitParameterChange(
                    this.initNodeMap, inheritedMemberAnalyzer, this.classRepository, classResolver2, arrayList1, methodParameterChanger
            );
            if (!arrayList1.isEmpty()) {
                programClass5.addPoolConstants(arrayList1);
            }
        }

        programClass4 = this.programClasses;
        bf = programClass4.length;

        for (int i = 0; i < bf; i += 1) {
            ProgramClass programClass6 = programClass4[i];
            if ((this.fixedClassesExclusions == null || !this.fixedClassesExclusions.isMatchedClass(programClass6)) && !programClass6.isGenerated()) {
                ArrayList arrayList2 = new ArrayList();
                Map map5 = this.changedDescriptors;
                Map map6 = this.initNodeMap;
                listMultimap2.getValues(programClass6);
                Set set7 = this.excludedMethods;
                MethodOverrideAnalyzer methodOverrideAnalyzer1 = this.overrideAnalyzer;
                MethodParameterChanger methodParameterChanger1 = methodParameterChanger;
                ArrayList arrayList = arrayList2;
                ClassResolver classResolver1 = classResolver2;
                ClassRepository classRepository1 = this.classRepository;
                CommonSuperTypeResolver commonSuperTypeResolver1 = this.superTypeResolver;
                Map map2 = this.superTypeMap;
                InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer;
                Set set6 = set1;
                Map map1 = this.keyedMethodMap;
                programClass6.applyParameterChangesToMethods(
                        map5,
                        map6,
                        hashMap,
                        map1,
                        set6,
                        inheritedMemberAnalyzer1,
                        map2,
                        commonSuperTypeResolver1,
                        classRepository1,
                        classResolver1,
                        arrayList,
                        methodParameterChanger1,
                        methodOverrideAnalyzer1,
                        set7
                );
                if (!arrayList2.isEmpty()) {
                    programClass6.addPoolConstants(arrayList2);
                }
            }
        }

        this.classRepository.resetClassReferences(this.programClasses, this.scriptEnvironment, true);
        PairMultiMap pairMultiMap = new PairMultiMap();
        Iterator iterator4 = this.changedDescriptors.entrySet().iterator();

        while (iterator4.hasNext()) {
            Entry entry1 = (Entry) iterator4.next();
            MethodInfo methodInfo8 = (MethodInfo) entry1.getKey();
            ChangedMethodDescriptor changedMethodDescriptor = (ChangedMethodDescriptor) entry1.getValue();
            methodInfo8.updateParameterAnnotations(changedMethodDescriptor);
            pairMultiMap.addPair(methodInfo8.getOwnerProgramClass(), methodInfo8, changedMethodDescriptor);
        }

        Iterator iterator5 = pairMultiMap.entrySet().iterator();

        while (iterator5.hasNext()) {
            Entry entry2 = (Entry) iterator5.next();
            ProgramClass programClass7 = (ProgramClass) entry2.getKey();
            ConstantPool constantPool1 = programClass7.getClassConstantPool();
            ArrayList arrayList3 = new ArrayList();
            Iterator iterator6 = ((List) entry2.getValue()).iterator();

            while (iterator6.hasNext()) {
                ObjectPair objectPair = (ObjectPair) iterator6.next();
                ((MethodInfo) objectPair.getFirst()).updateMethodParametersAttribute((ChangedMethodDescriptor) objectPair.getSecond(), constantPool1, arrayList3);
            }

            if (!arrayList3.isEmpty()) {
                programClass7.addPoolConstants(arrayList3);
            }
        }

        this.classRepository.indexMethods();
        this.recordSignatureMappings();
        return this.changedDescriptors;
    }

    public void applyChangeLogToHierarchy() {
        if (this.changeLogMapping != null) {
            List list1 = this.classHierarchy.getTopLoadedNodes();
            int ba = list1.size();

            for (int i = 0; i < ba; i++) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) list1.get(i);
                if (classHierarchyNode.isInterface()) {
                    classHierarchyNode.registerParameterChangeMethods(this);
                }
            }

            for (int i = 0; i < ba; i++) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
                if (!classHierarchyNode1.isInterface()) {
                    classHierarchyNode1.registerParameterChangeMethods(this);
                }
            }
        }
    }

    public void resolveInterfaceMappings() throws ZkmException, IOException {
        if (this.changeLogMapping != null) {
            Iterator iterator = this.overrideAnalyzer.getMethodSignatureByGroup().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                this.validateInterfaceSetMapping((ObservableHolder) entry.getKey(), (MethodSignature) entry.getValue());
            }

            List list1 = this.classHierarchy.getTopLoadedNodes();
            int bb = list1.size();

            for (int i = 0; i < bb; i++) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) list1.get(i);
                if (classHierarchyNode.isInterface()) {
                    classHierarchyNode.analyzeParameterChanges(this);
                }
            }

            for (int i = 0; i < bb; i++) {
                ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) list1.get(i);
                if (!classHierarchyNode1.isInterface()) {
                    classHierarchyNode1.analyzeParameterChanges(this);
                }
            }
        }
    }

    public void addChangeLogMethods(ChangeLogMapping changeLogMapping1, Set set1, Set set2, MethodOverrideAnalyzer methodOverrideAnalyzer) {
        if (changeLogMapping1 != null && changeLogMapping1.hasParameterChangeData()) {
            HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(set2.size()));
            Iterator iterator = set2.iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                MethodSignature methodSignature1 = methodInfo1.getSignature();
                ClassFileBase classFileBase = methodOverrideAnalyzer.findTopOverriddenOwner(methodInfo1.getOwnerProgramClass(), methodSignature1);
                hashMap.put(methodSignature1, classFileBase != null ? classFileBase : methodInfo1.getOwnerProgramClass());
            }

            List list1 = changeLogMapping1.getMethodsWithAddedParameters();
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator1.next();
                MethodInfo methodInfo2 = (MethodInfo) abstractMethodInfo1;
                AbstractMethodInfo abstractMethodInfo = methodOverrideAnalyzer.getRootMethod(methodInfo2);
                if (!hashMap.isEmpty()) {
                    AddedParameter[] addedParameters1 = changeLogMapping1.getAddedParameters(abstractMethodInfo);
                    ChangedMethodDescriptor changedMethodDescriptor = new ChangedMethodDescriptor(methodInfo2.getDescriptor(), addedParameters1);
                    MethodSignature methodSignature2 = new MethodSignature(methodInfo2.getSourceName(), changedMethodDescriptor.getDescriptor());
                    if (hashMap.get(methodSignature2) == abstractMethodInfo.getOwningClass()) {
                        changeLogMapping1.removeAddedParameters(abstractMethodInfo);
                        changeLogMapping1.logWarning(
                                "Method '"
                                        + methodInfo2.toOriginalDisplayString()
                                        + "' in class '"
                                        + methodInfo2.getOriginalDottedName()
                                        + "' could not be changed to '"
                                        + methodSignature2
                                        + "' because of existing synthetic method. You may need to distribute this application as a whole. (A)"
                        );
                        continue;
                    }
                }

                if (!set1.contains(methodInfo2)) {
                    set1.add(methodInfo2);
                }
            }
        }
    }

    public boolean canChangeWithoutClash(
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

    public void recordSignatureMappings() {
        this.originalToCurrentSignatures.clear();
        this.currentToOriginalSignatures.clear();

        for (ProgramClass programClass1 : this.programClasses) {
            if (!programClass1.isVersionedVariant()) {
                String string = programClass1.getClassName();

                for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
                    if (!methodInfo1.isStaticInitializer()) {
                        MethodSignature methodSignature1 = (MethodSignature) this.originalSignatures.get(methodInfo1);
                        MethodSignature methodSignature2 = methodInfo1.getSignature();
                        this.originalToCurrentSignatures.putValue(string, methodSignature1, methodSignature2);
                        this.currentToOriginalSignatures.putValue(string, methodSignature2, methodSignature1);
                    }
                }
            }
        }
    }

    public SetMultiMap collectChangeableMethods(
            Set set1,
            ListMultimap listMultimap,
            MethodParameterExclusions methodParameterExclusions,
            ChangeLogMapping changeLogMapping1,
            MethodOverrideAnalyzer methodOverrideAnalyzer
    ) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set1.size()));
        ObservableHolder observableHolder = new ObservableHolder();
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            if (this.isChangeable(methodInfo1, methodParameterExclusions, changeLogMapping1, observableHolder)) {
                MethodInfo methodInfo2 = (MethodInfo) observableHolder.getValue();
                if (hashSet.add(methodInfo2)) {
                    Set set2 = methodOverrideAnalyzer.getOverrideGroup(methodInfo2);
                    if (set2 != null) {
                        Iterator iterator1 = set2.iterator();

                        while (iterator1.hasNext()) {
                            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator1.next();
                            hashSet.add((MethodInfo) abstractMethodInfo);
                        }
                    }
                }
            }
        }

        UniqueWorkQueue uniqueWorkQueue = new UniqueWorkQueue(Math.max((int) (hashSet.size() * 1.5), 5));
        uniqueWorkQueue.enqueueAll(hashSet);

        while (!uniqueWorkQueue.isEmpty()) {
            MethodInfo methodInfo4 = (MethodInfo) uniqueWorkQueue.dequeue();
            List list1 = listMultimap.getValues(methodInfo4);
            if (list1 != null) {
                Iterator iterator5 = list1.iterator();

                while (iterator5.hasNext()) {
                    MethodInfo methodInfo6 = (MethodInfo) iterator5.next();
                    if (!hashSet.contains(methodInfo6) && this.isChangeable(methodInfo6, methodParameterExclusions, changeLogMapping1, observableHolder)) {
                        MethodInfo methodInfo7 = (MethodInfo) observableHolder.getValue();
                        if (hashSet.add(methodInfo7)) {
                            uniqueWorkQueue.enqueue(methodInfo7);
                            Set set3 = methodOverrideAnalyzer.getOverrideGroup(methodInfo7);
                            if (set3 != null) {
                                Iterator iterator2 = set3.iterator();

                                while (iterator2.hasNext()) {
                                    AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator2.next();
                                    if (hashSet.add((MethodInfo) abstractMethodInfo1)) {
                                        uniqueWorkQueue.enqueue((MethodInfo) abstractMethodInfo1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SetMultiMap setMultiMap = new SetMultiMap(ZkmUtils.getPrimeCapacity(this.programClasses.length));
        Iterator iterator4 = hashSet.iterator();

        while (iterator4.hasNext()) {
            MethodInfo methodInfo5 = (MethodInfo) iterator4.next();
            if (methodInfo5.isLambdaImplementation()) {
                AbstractMethodInfo abstractMethodInfo2 = methodOverrideAnalyzer.findRootMethod(methodInfo5);
                if (abstractMethodInfo2 != null) {
                    if (abstractMethodInfo2 != methodInfo5 && abstractMethodInfo2.isProgramMember()) {
                        abstractMethodInfo2.setLambdaArgCount(methodInfo5.getLambdaArgCount());
                    }

                    ClassFileBase classFileBase = abstractMethodInfo2.getOwningClass();
                    ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(classFileBase.getClassName());
                    if (classHierarchyNode != null) {
                        MethodSignature methodSignature1 = abstractMethodInfo2.getSignature();
                        ObservableHolder observableHolder1 = methodOverrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
                        if (observableHolder1 != null) {
                            Iterator iterator3 = ((List) observableHolder1.getValue()).iterator();

                            while (iterator3.hasNext()) {
                                MethodInfo methodInfo3 = ((ClassHierarchyNode) iterator3.next()).getProgramClass().findMethodBySignature(methodSignature1);
                                if (methodInfo3 != null) {
                                    methodInfo3.setLambdaArgCount(methodInfo5.getLambdaArgCount());
                                }
                            }
                        }
                    }
                }
            }

            setMultiMap.addValue(methodInfo5.getOwnerProgramClass(), methodInfo5);
        }

        return setMultiMap;
    }

    public final void checkOverriddenMapping(AbstractMethodInfo abstractMethodInfo) {
        if (this.changeLogMapping.hasAddedParametersEntry(abstractMethodInfo)) {
            AddedParameter[] addedParameters1 = this.changeLogMapping.getAddedParameters(abstractMethodInfo);
            if (!abstractMethodInfo.isStatic() && !abstractMethodInfo.isStrictlyPrivate()) {
                ClassFileBase classFileBase = this.overrideAnalyzer.getRootMethodClass(abstractMethodInfo);
                if (classFileBase != null) {
                    AbstractMethodInfo abstractMethodInfo1 = classFileBase.findMethod(abstractMethodInfo.getSignature());
                    ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(classFileBase.getClassName());
                    if (classHierarchyNode != null && !classHierarchyNode.hasNoClassFile()) {
                        AddedParameter[] addedParameters2 = this.changeLogMapping.reconcileAddedParameters(abstractMethodInfo, abstractMethodInfo1, addedParameters1);
                        if (addedParameters1 != null && addedParameters2 != null
                                ? !ZkmUtils.arraysEqual(addedParameters1, addedParameters2)
                                : addedParameters1 != addedParameters2) {
                        }
                    } else if (addedParameters1.length > 0) {
                        MethodSignature methodSignature1 = abstractMethodInfo1.getSignature();
                        MethodSignature methodSignature2 = ChangeLogMapping.createChangedMethodSignature(abstractMethodInfo, addedParameters1);
                        if (!methodSignature1.equals(methodSignature2)) {
                            this.changeLogMapping
                                    .logWarning(
                                            "Method '"
                                                    + abstractMethodInfo.getOriginalNameWithParameters()
                                                    + "' in class '"
                                                    + abstractMethodInfo.getOriginalDottedName()
                                                    + "' could not have its parameter list changed to '"
                                                    + methodSignature2.toDeclarationString()
                                                    + "' because it now overrides a method in '"
                                                    + classFileBase.getOriginalDottedName()
                                                    + "' which has not been opened."
                                    );
                            this.changeLogMapping.removeAddedParameters(abstractMethodInfo);
                        }
                    }
                }
            }
        }
    }

    public ListMultimap collectKeyedMethods(ListMultimap listMultimap, ListMultimap listMultimap1, Set set1, SetMultiMap setMultiMap) {
        ListMultimap listMultimap2 = new ListMultimap();
        SetMultiMap setMultiMap1 = new SetMultiMap();
        Iterator iterator = listMultimap1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            MethodInfo methodInfo1 = (MethodInfo) entry.getKey();
            Iterator iterator1 = ((List) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                MethodInfo methodInfo2 = (MethodInfo) iterator1.next();
                setMultiMap1.addValue(methodInfo2, methodInfo1);
            }
        }

        iterator = this.keyedMethodMap.keySet().iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo4 = (MethodInfo) iterator.next();
            ProgramClass programClass1 = methodInfo4.getOwnerProgramClass();
            if (!setMultiMap.containsValue(programClass1, methodInfo4)) {
                if (set1.contains(methodInfo4)) {
                    listMultimap2.addValue(programClass1, methodInfo4);
                } else {
                    Set set2 = setMultiMap1.getValues(methodInfo4);
                    if (set2 != null) {
                        Iterator iterator2 = set2.iterator();

                        while (iterator2.hasNext()) {
                            MethodInfo methodInfo3 = (MethodInfo) iterator2.next();
                            if (setMultiMap.containsValue(methodInfo3.getOwnerProgramClass(), methodInfo3)) {
                                listMultimap2.addValue(programClass1, methodInfo4);
                                break;
                            }
                        }
                    }
                }
            } else if (MethodParameterChanger.isNotOverridable(methodInfo4, this.overrideAnalyzer) && !HiddenOptionFlags.SKIP_UNRELATED_PARAMETER_CHANGES) {
                listMultimap.addValue(programClass1, methodInfo4);
            }
        }

        return listMultimap2;
    }

    public MethodSignature recordChangedDescriptor(MethodInfo methodInfo1, ChangedMethodDescriptor changedMethodDescriptor) {
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        MethodSignature methodSignature2;
        if (changedMethodDescriptor != null) {
            this.changedDescriptors.put(methodInfo1, changedMethodDescriptor);
            String string = changedMethodDescriptor.getDescriptor();
            methodSignature2 = new MethodSignature(methodSignature1.getName(), string);
        } else {
            methodSignature2 = methodSignature1;
        }

        return methodSignature2;
    }

    public AbstractMethodInfo getRootMethod(MethodInfo methodInfo1) {
        if (!methodInfo1.isStrictlyPrivate() && !methodInfo1.isStatic() && !methodInfo1.isConstructor()) {
            AbstractMethodInfo abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
            if (abstractMethodInfo == null) {
                abstractMethodInfo = methodInfo1;
            }

            return abstractMethodInfo;
        } else {
            return methodInfo1;
        }
    }

    public void validateInterfaceSetMapping(ObservableHolder observableHolder, MethodSignature methodSignature1) throws ZkmException, IOException {
        List list1 = (List) observableHolder.getValue();
        AbstractMethodInfo abstractMethodInfo = null;
        AbstractMethodInfo abstractMethodInfo1 = null;
        AddedParameter[] addedParameters1 = null;
        MethodSignature methodSignature2 = null;
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            ClassFileBase classFileBase = classHierarchyNode.getClassFile();
            if (classFileBase != null) {
                AbstractMethodInfo abstractMethodInfo2 = classFileBase.findMethod(methodSignature1);
                if (abstractMethodInfo2 != null && this.changeLogMapping.hasAddedParametersEntry(abstractMethodInfo2)) {
                    addedParameters1 = this.changeLogMapping.getAddedParameters(abstractMethodInfo2);
                    if (addedParameters1.length > 0) {
                        methodSignature2 = ChangeLogMapping.createChangedMethodSignature(abstractMethodInfo2, addedParameters1);
                        abstractMethodInfo = abstractMethodInfo2;
                    } else {
                        abstractMethodInfo1 = abstractMethodInfo2;
                    }
                    break;
                }
            }
        }

        iterator = list1.iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator.next();
            ClassFileBase classFileBase1 = classHierarchyNode1.getClassFile();
            if (classFileBase1 != null) {
                AbstractMethodInfo abstractMethodInfo3 = classFileBase1.findMethod(methodSignature1);
                if (abstractMethodInfo3 != null && this.changeLogMapping.hasAddedParametersEntry(abstractMethodInfo3)) {
                    AddedParameter[] addedParameters2 = this.changeLogMapping.getAddedParameters(abstractMethodInfo3);
                    if (addedParameters2.length > 0) {
                        if (abstractMethodInfo1 != null) {
                            this.rejectInterfaceMappings(
                                    list1,
                                    methodSignature1,
                                    "clashing mappings in between related interfaces (1) : "
                                            + abstractMethodInfo1.getOriginalNameWithParameters()
                                            + " : "
                                            + abstractMethodInfo3.getOriginalNameWithParameters()
                            );
                            methodSignature2 = null;
                            break;
                        }

                        if (!ZkmUtils.arraysEqual(addedParameters1, addedParameters2)) {
                            this.rejectInterfaceMappings(
                                    list1,
                                    methodSignature1,
                                    "clashing mappings in between related interfaces (2) : "
                                            + abstractMethodInfo.getOriginalNameWithParameters()
                                            + " : "
                                            + abstractMethodInfo3.getOriginalNameWithParameters()
                            );
                            methodSignature2 = null;
                            break;
                        }
                    }
                }
            }
        }

        if (methodSignature2 != null) {
            ObservableHolder observableHolder1 = new ObservableHolder();
            ChangedMethodDescriptor changedMethodDescriptor = new ChangedMethodDescriptor(abstractMethodInfo.getDescriptor(), addedParameters1);
            boolean bl = this.overrideAnalyzer.tryRenameGroup(observableHolder, methodSignature2, methodSignature1, observableHolder1, changedMethodDescriptor);
            if (!bl) {
                this.rejectInterfaceMappings(list1, methodSignature1, "clashing mappings in between related interface sets");
            }
        }
    }

    public void collectInheritedSignatures(ClassFileBase classFileBase, SetMultiMap setMultiMap) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        ObfuscationEngine.collectInheritedMethods(
                classFileBase, setMultiMap, this.compareFullSignatures, hashSet, this.classRepository.getClassResolver(), "Method Parameter List Changing"
        );
    }

    public boolean isChangeable(
            MethodInfo methodInfo1, MethodParameterExclusions methodParameterExclusions, ChangeLogMapping changeLogMapping1, ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        AbstractMethodInfo abstractMethodInfo = this.getRootMethod(methodInfo1);
        if (!abstractMethodInfo.isProgramMember()) {
            return false;
        }

        observableHolder.setValue((MethodInfo) abstractMethodInfo);
        return methodParameterExclusions.isMethodExcluded((MethodInfo) abstractMethodInfo)
                && (changeLogMapping1 == null || changeLogMapping1.isLoose() || !changeLogMapping1.isParameterListUnchanged(abstractMethodInfo));
    }

    public MethodParameterChangeSet(
            MethodParameterExclusions methodParameterExclusions,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            ChangeLogMapping changeLogMapping1,
            boolean useChangeLogExclusions,
            ClassRepository classRepository1,
            ProgramClass[] programClass1,
            ClassHierarchy classHierarchy1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            int changeLevel,
            boolean compareFullSignatures,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            boolean shuffleMethods,
            Map map1,
            Map map2,
            Set set1,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            ObservableHolder observableHolder,
            Map map3,
            String string,
            SetValuedMap setValuedMap,
            Map map4,
            BooleanFlag booleanFlag,
            boolean includeInterfaces,
            ScriptEnvironment scriptEnvironment1
    ) {
        this.overrideAnalyzer = methodOverrideAnalyzer;
        this.classHierarchy = classHierarchy1;
        this.superTypeResolver = commonSuperTypeResolver1;
        ArrayList arrayList = new ArrayList(programClass1.length + 10);

        for (ProgramClass programClass2 : programClass1) {
            arrayList.add(programClass2);
            if (programClass2.hasVersionedVariants()) {
                Iterator iterator = programClass2.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                    arrayList.add((ProgramClass) classFileBase);
                }
            }
        }

        this.programClasses = ((com.zelix.klassmaster.classfile.ProgramClass[]) (arrayList.toArray(new ProgramClass[arrayList.size()])));
        this.classRepository = classRepository1;
        this.changeLevel = changeLevel;
        this.compareFullSignatures = compareFullSignatures;
        this.shuffleMethods = shuffleMethods;
        this.exclusions = methodParameterExclusions;
        this.fixedClassesExclusions = fixedClassesExclusionSet1;
        this.changeLogMapping = changeLogMapping1;
        this.useChangeLogExclusions = useChangeLogExclusions;
        this.keyedMethodMap = map1;
        this.superTypeMap = map2;
        this.excludedMethods = set1;
        this.classNameMap = hashMap;
        this.originalToCurrentSignatures = twoKeyMap;
        this.currentToOriginalSignatures = twoKeyMap1;
        this.defaultHelperHolder = observableHolder;
        this.archiveHelperClasses = map3;
        this.helperClassPrefix = string;
        this.helperClassUsers = setValuedMap;
        this.descriptorOverrides = map4;
        this.changesEnabled = booleanFlag;
        this.includeInterfaces = includeInterfaces;
        this.scriptEnvironment = scriptEnvironment1;

        for (int i = 0; i < OBFUSCATED_STRINGS.length - 1; i++) {
            try {
                String string1 = OBFUSCATED_STRINGS[i];
                Class<?> class1 = Class.forName(string1);
                this.helperInstance = class1.newInstance();
                break;
            } catch (Throwable throwable) {
            }
        }
    }

    public ChangedMethodDescriptor getChangedDescriptor(Object object) {
        return (ChangedMethodDescriptor) this.changedDescriptors.get(object);
    }

    public void reserveUnchangedSignatures(SetMultiMap setMultiMap, Set set1) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = setMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Iterator iterator1 = ((Set) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator1.next();
                hashSet.add(methodInfo1);
            }
        }

        HashSet hashSet1 = ZkmUtils.createHashSet();
        Enumeration enumeration = this.exclusions.getCandidateMethods();

        while (enumeration.hasMoreElements()) {
            MethodInfo methodInfo2 = (MethodInfo) enumeration.nextElement();
            if (!hashSet.contains(methodInfo2)) {
                hashSet1.add(methodInfo2);
            }
        }

        Set set2 = this.resolveDeclaringMethods(hashSet1);
        set2.removeAll(set1);
        this.reserveSignatures(set2);
    }

    public void applyChangeLogDescriptors(List list1) throws ZkmException, IOException {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            ObjectPair objectPair = (ObjectPair) iterator.next();
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) objectPair.getFirst();
            MethodSignature methodSignature1 = ChangeLogMapping.createChangedMethodSignature(abstractMethodInfo, (AddedParameter[]) objectPair.getSecond());
            abstractMethodInfo.setDescriptor(methodSignature1.getDescriptor());
            abstractMethodInfo.setHasChangedParameters();
        }
    }

    public Map getInitNodeMap() {
        return this.initNodeMap;
    }

    public MethodOverrideAnalyzer getOverrideAnalyzer() {
        return this.overrideAnalyzer;
    }

    public void reserveSignatures(Set set1) throws ZkmException, IOException {
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            MethodSignature methodSignature1 = methodInfo1.getSignature();
            ClassFileBase classFileBase = (ClassFileBase) methodInfo1.getParent();
            if (classFileBase.isInterface() && !methodInfo1.isStaticInitializer() && !this.overrideAnalyzer.isSignatureReserved(methodSignature1)) {
                String string = classFileBase.getClassName();
                ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
                ObservableHolder observableHolder = new ObservableHolder();
                boolean bl = this.overrideAnalyzer.tryReserveRename(classHierarchyNode, methodSignature1, methodSignature1, observableHolder);
                if (!bl) {
                    this.overrideAnalyzer.reserveSignature(methodSignature1);
                }
            }
        }

        iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo2 = (MethodInfo) iterator.next();
            MethodSignature methodSignature2 = methodInfo2.getSignature();
            ClassFileBase classFileBase1 = (ClassFileBase) methodInfo2.getParent();
            if (!classFileBase1.isInterface() && !methodInfo2.isStaticInitializer() && !this.overrideAnalyzer.isSignatureReserved(methodSignature2)) {
                String string1 = classFileBase1.getClassName();
                ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(string1);
                ObservableHolder observableHolder1 = new ObservableHolder();
                boolean bl1;
                if (methodInfo2.isConstructor()) {
                    bl1 = this.overrideAnalyzer.tryReservePrivate(classHierarchyNode1, methodSignature2, observableHolder1);
                } else {
                    bl1 = this.overrideAnalyzer.tryReserveMethodRename(classHierarchyNode1, methodSignature2, methodSignature2, methodInfo2, observableHolder1);
                }

                if (!bl1) {
                    this.overrideAnalyzer.reserveSignature(methodSignature2);
                }
            }
        }
    }

    public void loadOriginalSignatures() {
        for (ProgramClass programClass1 : this.programClasses) {
            if (!programClass1.isVersionedVariant()) {
                String string = programClass1.getClassName();

                for (MethodInfo methodInfo1 : programClass1.getMethodInfos()) {
                    MethodSignature methodSignature1 = methodInfo1.getSignature();
                    MethodSignature methodSignature2 = (MethodSignature) ZkmUtils.twoKeyMapOrSelf(string, methodSignature1, this.currentToOriginalSignatures);
                    this.originalSignatures.put(methodInfo1, methodSignature2);
                }
            }
        }
    }
}
