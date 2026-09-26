package com.zelix.klassmaster.obfuscator.flow;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.changelog.ChangeLogMemberEntry;
import com.zelix.klassmaster.changelog.LabeledTuple;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.hierarchy.LibraryFieldInfo;
import com.zelix.klassmaster.classfile.hierarchy.LibraryMethod;
import com.zelix.klassmaster.classfile.insn.BranchInstruction;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.classfile.insn.NewArrayInstruction;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.classfile.insn.TypeInstruction;
import com.zelix.klassmaster.config.GroupingsSpec;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.engine.ClassInitOrderHandler;
import com.zelix.klassmaster.engine.ProcessingStatistics;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.license.EncodedClassNameSource;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.rename.IndexedNameGenerator;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.IdentityValueHolder;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NamedSet;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.WeightedObject;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class FlowObfuscationManager extends EncodedClassNameSource {
    public ListMultimap groupingSetsByClass;
    public ListMultimap changeLogClassesByName;
    public Map fieldTypesByGroup;
    public final List groups;
    public List groupFields;
    public ListMultimap packageFieldsByPackage;
    public Map groupFieldByPackageField;
    public final Set generatedMethods;
    public Set primaryCallerClasses;
    public Set secondaryCallerClasses;
    public ProgramClass[] programClasses;
    public ClassFileBase[] classFiles;
    public GroupingsSpec groupingsSpec;
    public final ChangeLogMapping changeLogMapping;
    private final ClassMemberLookup classMemberLookup;
    public final ProcessingStatistics processingStatistics;
    public final StaticInitCalleeAnalyzer staticInitCalleeAnalyzer;
    public boolean deriveGroupingsFromChangeLog;
    public final Map fieldPairsByClass;
    public final boolean useFixedSeed;
    public Random random;
    public final NamedSet[] groupingSets;
    public final Map groupsByClass;
    public Object extensionHook;

    public void addFieldType(Object object, Object object1) {
        ((SyncIndexedSet) this.fieldTypesByGroup.get(object)).add(object1);
    }

    public ListMultimap getPackageFieldsByPackage() {
        ListMultimap listMultimap = new ListMultimap(this.packageFieldsByPackage.getKeyCount());
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        Iterator iterator = this.fieldPairsByClass.values().iterator();

        while (iterator.hasNext()) {
            FlowFieldPair flowFieldPair = (FlowFieldPair) iterator.next();
            String string = flowFieldPair.getPackageName();
            OpaquePredicateField opaquePredicateField = flowFieldPair.getPackageField();
            if ((OpaquePredicateField) twoKeyMap.putValue(string, opaquePredicateField, opaquePredicateField) == null) {
                listMultimap.addValue(string, opaquePredicateField);
            }
        }

        return listMultimap;
    }

    public boolean isUsedInEarlierPass(
            ChangeLogMemberEntry changeLogMemberEntry,
            ClassFileBase classFileBase,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        if (changeLogMemberEntry.hasNoField()) {
            return false;
        } else {
            String string = changeLogMemberEntry.getFieldDescriptor();
            String string1 = changeLogMemberEntry.getFieldTypeName();
            String string2 = changeLogMemberEntry.getClassName();
            String string3 = "' is associated with a Flow Obfuscation "
                    + (changeLogMemberEntry.isTraceBackEntry() ? "TraceBackClass:" : "ForwardClass:")
                    + " but it has been used in an earlier flow obfuscation pass. You have probably used the wrong input change log! Otherwise, you must distribute this application as a whole.";
            Set set1 = inheritedMemberAnalyzer.getVisibleFieldSignatures(classFileBase);
            Set set2 = inheritedMemberAnalyzer.getVisibleMethodNameTypes(classFileBase);
            if (set1 != null && set1.contains(new FieldSignature(changeLogMemberEntry.getFieldName(), string))) {
                observableHolder.setValue("Field '" + string1 + " " + changeLogMemberEntry.getFieldName() + "' in class '" + string2 + string3);
                return true;
            } else if (changeLogMemberEntry.getSetterMethodName() != null
                    && set2 != null
                    && set2.contains(new FieldNameTypeSignature(changeLogMemberEntry.getSetterMethodName(), OpaquePredicateField.setterDescriptorFor(string)))) {
                observableHolder.setValue(
                        "Method '" + changeLogMemberEntry.getSetterMethodName() + OpaquePredicateField.setterDescriptorFor(string) + "' in class '" + string2 + string3
                );
                return true;
            } else if (changeLogMemberEntry.getGetterMethodName() != null
                    && set2 != null
                    && set2.contains(new FieldNameTypeSignature(changeLogMemberEntry.getGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string)))) {
                observableHolder.setValue(
                        "Method '" + changeLogMemberEntry.getGetterMethodName() + OpaquePredicateField.getterDescriptorFor(string) + "' in class '" + string2 + string3
                );
                return true;
            } else if (changeLogMemberEntry.getAltGetterMethodName() != null
                    && set2 != null
                    && set2.contains(new FieldNameTypeSignature(changeLogMemberEntry.getAltGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string)))) {
                observableHolder.setValue(
                        "Method '"
                                + changeLogMemberEntry.getAltGetterMethodName()
                                + OpaquePredicateField.getterDescriptorFor(string)
                                + "' in class '"
                                + string2
                                + string3
                );
                return true;
            } else {
                return false;
            }
        }
    }

    public CountingBag findCandidateClasses(Object object, CountingBag countingBag, ChangeLogMapping changeLogMapping1) {
        if (this.changeLogClassesByName == null) {
            this.changeLogClassesByName = new ListMultimap();
            Map map1 = changeLogMapping1.getMemberClassEntries();
            Iterator iterator = map1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String string = ZkmUtils.dotsToSlashes((String) entry.getKey());
                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string);
                if (programClass1 != null) {
                    ObjectPair objectPair = (ObjectPair) entry.getValue();
                    ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) objectPair.getFirst();
                    ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) objectPair.getSecond();
                    this.changeLogClassesByName.addValue(changeLogMemberEntry.getInternalClassName(), programClass1);
                    this.changeLogClassesByName.addValue(changeLogMemberEntry1.getInternalClassName(), programClass1);
                }
            }
        }

        List list1 = this.changeLogClassesByName.getValues(object);
        if (list1 != null && list1.size() > 0) {
            HashSet hashSet = ZkmUtils.createHashSet(7);
            HashSet hashSet1 = ZkmUtils.createHashSet(7);
            Iterator iterator1 = this.groups.iterator();

            while (iterator1.hasNext()) {
                FlowObfuscationGroup flowObfuscationGroup2 = (FlowObfuscationGroup) iterator1.next();
                boolean bl = true;
                boolean bl1 = false;
                Iterator iterator2 = list1.iterator();

                while (iterator2.hasNext()) {
                    ProgramClass programClass2 = (ProgramClass) iterator2.next();
                    if (!flowObfuscationGroup2.containsClass(programClass2)) {
                        bl = false;
                        break;
                    }

                    if (flowObfuscationGroup2.isCommonClass(programClass2)) {
                        bl1 = true;
                    }
                }

                if (bl) {
                    hashSet1.add(flowObfuscationGroup2);
                    if (bl1) {
                        hashSet.add(flowObfuscationGroup2);
                    }
                }
            }

            FlowObfuscationGroup flowObfuscationGroup1 = null;
            if (hashSet.size() == 1) {
                flowObfuscationGroup1 = (FlowObfuscationGroup) hashSet.iterator().next();
            } else if (hashSet1.size() == 1) {
                flowObfuscationGroup1 = (FlowObfuscationGroup) hashSet1.iterator().next();
            }

            if (flowObfuscationGroup1 != null) {
                CountingBag countingBag2 = ZkmUtils.copyCountingBag(countingBag);
                countingBag2.retainAll(flowObfuscationGroup1.getCommonClasses());
                return countingBag2;
            } else {
                return null;
            }
        } else if (this.changeLogClassesByName.getKeyCount() == 0 && this.groups.size() == 1) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) this.groups.get(0);
            CountingBag countingBag1 = ZkmUtils.copyCountingBag(countingBag);
            countingBag1.retainAll(flowObfuscationGroup.getCommonClasses());
            return countingBag1;
        } else {
            return null;
        }
    }

    public boolean isAdvancedModeEnabled() {
        return this.processingStatistics.meetsFullSizeThreshold();
    }

    public Set getFieldOwnerClasses() {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.fieldPairsByClass.entrySet().iterator();

        while (iterator.hasNext()) {
            FlowFieldPair flowFieldPair = (FlowFieldPair) ((Entry) iterator.next()).getValue();
            hashSet.add(flowFieldPair.getGroupField().getOwnerClass());
            hashSet.add(flowFieldPair.getPackageField().getOwnerClass());
        }

        return hashSet;
    }

    public OpaquePredicateField createLibraryPredicateField(
            ChangeLogMemberEntry changeLogMemberEntry, ClasspathClassFile classpathClassFile, InheritedMemberAnalyzer inheritedMemberAnalyzer
    ) throws ZkmException, IOException {
        FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) this.groupsByClass.get(classpathClassFile);
        String string = changeLogMemberEntry.getFieldDescriptor();
        this.addFieldType(flowObfuscationGroup, string);
        classpathClassFile.getClassName();
        LibraryFieldInfo libraryFieldInfo = (LibraryFieldInfo) classpathClassFile.findField(changeLogMemberEntry.getFieldName(), string);
        if (libraryFieldInfo == null) {
            libraryFieldInfo = classpathClassFile.createField(changeLogMemberEntry.getFieldName(), string, changeLogMemberEntry.hasSetterMethod() ? 1 : 4);
            inheritedMemberAnalyzer.addField(libraryFieldInfo);
        }

        OpaquePredicateField opaquePredicateField;
        if (changeLogMemberEntry.hasSetterMethod()) {
            ArrayList arrayList = new ArrayList();
            MethodSignature methodSignature1 = new MethodSignature(changeLogMemberEntry.getSetterMethodName(), OpaquePredicateField.setterDescriptorFor(string));
            AbstractMethodInfo abstractMethodInfo = classpathClassFile.findMethod(methodSignature1);
            if (abstractMethodInfo == null) {
                abstractMethodInfo = this.createLibrarySetter(
                        classpathClassFile, changeLogMemberEntry.getSetterMethodName(), libraryFieldInfo, arrayList, inheritedMemberAnalyzer
                );
            }

            methodSignature1 = new MethodSignature(changeLogMemberEntry.getGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string));
            AbstractMethodInfo abstractMethodInfo1 = classpathClassFile.findMethod(methodSignature1);
            if (abstractMethodInfo1 == null) {
                abstractMethodInfo1 = this.createLibraryGetter(
                        classpathClassFile, changeLogMemberEntry.getGetterMethodName(), libraryFieldInfo, arrayList, inheritedMemberAnalyzer
                );
            }

            AbstractMethodInfo abstractMethodInfo2 = null;
            if (string.equals("I") || string.equals("Z")) {
                methodSignature1 = new MethodSignature(changeLogMemberEntry.getAltGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string));
                abstractMethodInfo2 = classpathClassFile.findMethod(methodSignature1);
                if (abstractMethodInfo2 == null) {
                    abstractMethodInfo2 = this.createLibraryNegatedGetter(
                            classpathClassFile, changeLogMemberEntry.getAltGetterMethodName(), abstractMethodInfo1, arrayList, inheritedMemberAnalyzer, this.random
                    );
                }
            }

            classpathClassFile.getConstantPool().appendEntries(arrayList);
            opaquePredicateField = new OpaquePredicateField(
                    libraryFieldInfo, abstractMethodInfo, abstractMethodInfo1, abstractMethodInfo2, changeLogMemberEntry.isAlternateVariant()
            );
        } else {
            opaquePredicateField = new OpaquePredicateField(libraryFieldInfo);
        }

        return opaquePredicateField;
    }

    public static List buildFieldInitializer(
            OpaquePredicateField opaquePredicateField,
            List list1,
            ConstantPool constantPool1,
            boolean bl,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        if (opaquePredicateField.isString()) {
            opaquePredicateField.getFieldType();
            String string1 = new IndexedNameGenerator().getNonKeywordName(random1.nextInt(Integer.MAX_VALUE));
            arrayList.add(Instruction.createStringConstantLoad(string1, constantPool1, list1));
        } else if (opaquePredicateField.isObjectType()) {
            String string = opaquePredicateField.getFieldType();
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(string.substring(1, string.length() - 1), list1);
            arrayList.add(new TypeInstruction(resolvedClassConstant));
            arrayList.add(SimpleInstruction.forOpcode(89));
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    resolvedClassConstant.getClassName(), "<init>", "()V", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        } else if (opaquePredicateField.isPrimitiveArray()) {
            arrayList.add(Instruction.createIntConstantPush(random1.nextInt(5) + 1, constantPool1, list1));
            String string2 = opaquePredicateField.getFieldType();
            String string4 = string2.substring(string2.lastIndexOf(91) + 1);
            arrayList.add(new NewArrayInstruction(string4));
        } else if (opaquePredicateField.isObjectArray()) {
            arrayList.add(Instruction.createIntConstantPush(random1.nextInt(5) + 1, constantPool1, list1));
            String string3;
            ConstantPool constantPool2;
            String string5;
            String string6;
            byte ba;
            if (bl) {
                string3 = opaquePredicateField.getFieldDescriptor();
                constantPool2 = constantPool1;
                string5 = string3;
                string6 = string3;
                ba = 91;
            } else {
                string3 = opaquePredicateField.getFieldType();
                constantPool2 = constantPool1;
                string5 = string3;
                string6 = string3;
                ba = 91;
            }

            ResolvedClassConstant resolvedClassConstant1 = constantPool2.getOrCreateClassConstant(
                    string5.substring(string6.lastIndexOf(ba) + 2, string3.length() - 1), list1
            );
            arrayList.add(new ConstantRefInstruction(189, resolvedClassConstant1));
        }

        return arrayList;
    }

    public FlowFieldPair getFieldPair(Object object) {
        return (FlowFieldPair) this.fieldPairsByClass.get(object);
    }

    public OpaquePredicateField createPredicateFieldFromChangeLog(
            ChangeLogMemberEntry changeLogMemberEntry,
            ProgramClass programClass1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            Random random1
    ) throws ZkmException, IOException {
        FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) this.groupsByClass.get(programClass1);
        String string = changeLogMemberEntry.getFieldDescriptor();
        this.addFieldType(flowObfuscationGroup, string);
        FieldInfo fieldInfo = programClass1.createStaticField(
                changeLogMemberEntry.getFieldName(), string, changeLogMemberEntry.hasSetterMethod() ? 1 : 4, inheritedMemberAnalyzer, classMemberLookup1
        );
        OpaquePredicateField opaquePredicateField;
        if (changeLogMemberEntry.hasSetterMethod()) {
            ArrayList arrayList = new ArrayList();
            MethodInfo methodInfo1 = this.createNamedSetter(
                    programClass1, changeLogMemberEntry.getSetterMethodName(), fieldInfo, arrayList, inheritedMemberAnalyzer, classMemberLookup1
            );
            MethodInfo methodInfo2 = this.createNamedGetter(
                    programClass1, changeLogMemberEntry.getGetterMethodName(), fieldInfo, arrayList, inheritedMemberAnalyzer, classMemberLookup1
            );
            MethodInfo methodInfo3 = null;
            String string1 = changeLogMemberEntry.getAltGetterMethodName();
            if ((string.equals("I") || string.equals("Z")) && string1 != null) {
                methodInfo3 = this.createNamedNegatedGetter(programClass1, string1, methodInfo2, arrayList, inheritedMemberAnalyzer, classMemberLookup1, random1);
            }

            programClass1.getClassConstantPool().appendEntries(arrayList);
            opaquePredicateField = new OpaquePredicateField(fieldInfo, methodInfo1, methodInfo2, methodInfo3, changeLogMemberEntry.isAlternateVariant());
        } else {
            opaquePredicateField = new OpaquePredicateField(fieldInfo);
        }

        return opaquePredicateField;
    }

    public Random getRandom() {
        return this.random;
    }

    public ClassFileBase findLibraryClass(Object object, SetMultiMap setMultiMap) {
        Set set1 = setMultiMap.getValues(object);
        if (set1 != null) {
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                if (!classFileBase.isProgramClass()) {
                    return classFileBase;
                }
            }
        }

        return null;
    }

    public OpaquePredicateField resolveFieldForChangeLogEntry(
            ChangeLogMemberEntry changeLogMemberEntry,
            SetMultiMap setMultiMap,
            CountingBag countingBag,
            NameExclusionSet nameExclusionSet,
            Random random1,
            TwoKeyMap twoKeyMap,
            Map map1,
            Map map2,
            TwoKeyMap twoKeyMap1,
            Map map3,
            TwoKeyMap twoKeyMap2,
            Map map4,
            TwoKeyMap twoKeyMap3,
            boolean bl,
            int ba,
            ClassMemberLookup classMemberLookup1,
            Map map5,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer
    ) throws ZkmException, IOException {
        String string = changeLogMemberEntry.getInternalClassName();
        changeLogMemberEntry.getFieldName();
        changeLogMemberEntry.getFieldDescriptor();
        changeLogMemberEntry.getFieldTypeName();
        OpaquePredicateField opaquePredicateField = null;
        String string1 = ZkmUtils.slashesToDots(string);
        this.changeLogMapping.getFlowEntryFieldNames(string);
        if (!this.changeLogMapping.hasClassMapping(string) && !this.changeLogMapping.isRemovedClass(string)) {
            this.changeLogMapping
                    .reportFatalError(
                            "Class '"
                                    + string1
                                    + "' appears as a Flow Obfuscation "
                                    + (bl ? "TraceBackClass:" : "ForwardClass:")
                                    + " but does not appear in the main part of the change log. (C)"
                    );
        } else {
            String string2 = changeLogMemberEntry.getNewClassName();
            if (!changeLogMemberEntry.hasNoField() && !changeLogMemberEntry.hasIncompleteMapping()) {
                this.changeLogMapping
                        .reportFatalError(
                                "Class '"
                                        + string1
                                        + "' appears as a Flow Obfuscation "
                                        + (bl ? "TraceBackClass:" : "ForwardClass:")
                                        + " but its entry in the main part of the change log is corrupt."
                        );
            } else {
                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string);
                if (classFileBase != null) {
                    ObservableHolder observableHolder = new ObservableHolder();
                    if (changeLogMemberEntry.hasCustomReferenceType()) {
                        FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) this.groupsByClass.get(classFileBase);
                        String string3 = changeLogMemberEntry.getArrayElementClassName();
                        ClassFileBase classFileBase1 = ClassHierarchyNode.findClassFile(string3);
                        if (classFileBase1 == null || !flowObfuscationGroup.isCommonClass(classFileBase1)) {
                            observableHolder.setValue(
                                    "Type '"
                                            + ZkmUtils.slashesToDots(string3)
                                            + "' appears in Flow Obfuscation "
                                            + (bl ? "TraceBackClass:" : "ForwardClass:")
                                            + " '"
                                            + string1
                                            + "' but it is no longer available within the corresponding group. You must distribute this application as a whole."
                            );
                        }
                    }

                    if (observableHolder.isValueNull()) {
                        if (this.isUsedInEarlierPass(changeLogMemberEntry, classFileBase, inheritedMemberAnalyzer, observableHolder)) {
                            this.changeLogMapping.logError((String) observableHolder.getValue());
                        } else if (!classFileBase.isInterface()) {
                            if (!this.hasExcludedChangeLogMembers(
                                    changeLogMemberEntry, classFileBase, twoKeyMap1, map4, nameExclusionSet, classMemberLookup1, observableHolder
                            )) {
                                FlowObfuscationManager flowObfuscationManager1;
                                ChangeLogMemberEntry changeLogMemberEntry1;
                                ClassFileBase classFileBase3;
                                ChangeLogMapping changeLogMapping2;
                                if (!classFileBase.isPublic()) {
                                    if (ba != 1) {
                                        this.changeLogMapping
                                                .logError(
                                                        "Class '"
                                                                + string1
                                                                + "' appears as a Flow Obfuscation "
                                                                + (bl ? "TraceBackClass:" : "ForwardClass:")
                                                                + " but it is no longer public. Another class had to be used. You must distribute this application as a whole."
                                                );
                                        return opaquePredicateField;
                                    }

                                    flowObfuscationManager1 = this;
                                    changeLogMemberEntry1 = changeLogMemberEntry;
                                    classFileBase3 = classFileBase;
                                    changeLogMapping2 = this.changeLogMapping;
                                } else {
                                    flowObfuscationManager1 = this;
                                    changeLogMemberEntry1 = changeLogMemberEntry;
                                    classFileBase3 = classFileBase;
                                    changeLogMapping2 = this.changeLogMapping;
                                }

                                InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer;
                                TwoKeyMap twoKeyMap5 = twoKeyMap2;
                                Map map6 = map2;
                                TwoKeyMap twoKeyMap4 = twoKeyMap;
                                Random random2 = random1;
                                ChangeLogMapping changeLogMapping1 = changeLogMapping2;
                                opaquePredicateField = flowObfuscationManager1.getOrCreateChangeLogField(
                                        changeLogMemberEntry1,
                                        classFileBase3,
                                        changeLogMapping1,
                                        random2,
                                        twoKeyMap4,
                                        map6,
                                        twoKeyMap5,
                                        inheritedMemberAnalyzer1,
                                        classMemberLookup1
                                );
                            } else {
                                this.changeLogMapping.logError((String) observableHolder.getValue());
                            }
                        } else {
                            this.changeLogMapping
                                    .logError(
                                            "Class '"
                                                    + string1
                                                    + "' appears as a Flow Obfuscation "
                                                    + (bl ? "TraceBackClass:" : "ForwardClass:")
                                                    + " but it is now an interface. Another class had to be used.  You must distribute this application as a whole."
                                    );
                        }
                    } else {
                        this.changeLogMapping.logError((String) observableHolder.getValue());
                    }
                } else {
                    ClassFileBase classFileBase2 = this.findLibraryClass(string, setMultiMap);
                    if (classFileBase2 == null) {
                        ProgramClass programClass1 = this.findHostClassForChangeLogEntry(
                                changeLogMemberEntry,
                                countingBag,
                                map5,
                                this.changeLogMapping,
                                nameExclusionSet,
                                classMemberLookup1,
                                flowObfuscationExclusions,
                                classInitOrderHandler1
                        );
                        if (programClass1 != null) {
                            map1.put(string, programClass1.getClassName());
                            String string4 = changeLogMemberEntry.getFieldTypeName();
                            opaquePredicateField = this.getOrCreateChangeLogField(
                                    changeLogMemberEntry,
                                    programClass1,
                                    this.changeLogMapping,
                                    random1,
                                    twoKeyMap,
                                    map2,
                                    twoKeyMap2,
                                    inheritedMemberAnalyzer,
                                    classMemberLookup1
                            );
                            map3.put(new Triple(programClass1.getClassName(), opaquePredicateField.getFieldName(), string4), changeLogMemberEntry.getNewFieldName());
                            if (changeLogMemberEntry.hasSetterMethod()) {
                                if (opaquePredicateField.hasSetter()) {
                                    AbstractMethodInfo abstractMethodInfo = opaquePredicateField.getSetter();
                                    twoKeyMap3.putValue(
                                            programClass1.getClassName(),
                                            new Triple(abstractMethodInfo.getSourceName(), string4, "void"),
                                            changeLogMemberEntry.getNewSetterMethodName()
                                    );
                                }

                                if (opaquePredicateField.hasGetter()) {
                                    AbstractMethodInfo abstractMethodInfo1 = opaquePredicateField.getGetter();
                                    twoKeyMap3.putValue(
                                            programClass1.getClassName(),
                                            new Triple(abstractMethodInfo1.getSourceName(), "", string4),
                                            changeLogMemberEntry.getNewGetterMethodName()
                                    );
                                }

                                if (opaquePredicateField.hasNegatedGetter()) {
                                    AbstractMethodInfo abstractMethodInfo2 = opaquePredicateField.getNegatedGetter();
                                    twoKeyMap3.putValue(
                                            programClass1.getClassName(),
                                            new Triple(abstractMethodInfo2.getSourceName(), "", string4),
                                            changeLogMemberEntry.getNewAltGetterMethodName()
                                    );
                                }
                            }
                        } else {
                            this.changeLogMapping
                                    .logError(
                                            "Class '"
                                                    + string1
                                                    + "' appears as a Flow Obfuscation "
                                                    + (bl ? "TraceBackClass:" : "ForwardClass:")
                                                    + " but is no longer available. Another class had to be used.  You must distribute this application as a whole."
                                    );
                        }
                    } else {
                        this.changeLogMapping
                                .reportFatalError(
                                        "Class '"
                                                + string1
                                                + "' appears as a Flow Obfuscation "
                                                + (bl ? "TraceBackClass:" : "ForwardClass:")
                                                + " but could not be found. However, it must be referenced by '"
                                                + classFileBase2.getDottedClassName()
                                                + "' which has not be opened."
                                );
                    }
                }
            }
        }

        return opaquePredicateField;
    }

    public MethodInfo createNamedSetter(
            ProgramClass programClass1,
            String string,
            FieldInfo fieldInfo,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        String string1 = OpaquePredicateField.setterDescriptorFor(fieldInfo.getDescriptor());
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        this.emitSetterBody(arrayList, localVariableList1, programClass1, fieldInfo, list1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createPublicStaticMethod(
                string, string1, arrayList, 1, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer, classMemberLookup1
        );
        this.generatedMethods.add(methodInfo1);
        return methodInfo1;
    }

    public static HashMap buildGroupsFromChangeLog(
            ProgramClass[] programClass1, ClassFileBase[] classFileBases, ChangeLogMapping changeLogMapping1, NamedSet[] namedSets, List list1
    ) throws ZkmProcessingException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(classFileBases.length));

        for (int i = 0; i < namedSets.length; i++) {
            ClassGroupingSet classGroupingSet = new ClassGroupingSet(namedSets[i]);
            FlowObfuscationGroup flowObfuscationGroup = new FlowObfuscationGroup(classGroupingSet);
            list1.add(flowObfuscationGroup);
        }

        indexGroupsByClass(list1, hashMap);

        for (int i = 0; i < programClass1.length; i++) {
            String string1 = programClass1[i].getClassName();
            if (!changeLogMapping1.hasClassMapping(string1)) {
                String string = ZkmUtils.slashesToDots(string1);
                throw new ZkmProcessingException(
                        "'deriveGroupingsFromInputChangeLog' specified but opened class '"
                                + string
                                + "' does not appear in log '"
                                + changeLogMapping1.getChangeLogName()
                                + "'"
                );
            }
        }

        return hashMap;
    }

    public MethodInfo createNegatedGetter(
            ProgramClass programClass1,
            MethodInfo methodInfo1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            Random random1
    ) throws ZkmProcessingException {
        String string = methodInfo1.getDescriptor();
        LocalVariableList localVariableList1 = new LocalVariableList(true, string, 5);
        ArrayList arrayList = new ArrayList();
        this.emitNegatedGetterBody(arrayList, localVariableList1, programClass1, methodInfo1, list1, random1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo2 = programClass1.createUniquelyNamedPublicStaticMethod(
                string, arrayList, 1, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer, classMemberLookup1
        );
        this.generatedMethods.add(methodInfo2);
        return methodInfo2;
    }

    public static HashMap buildGroups(ProgramClass[] programClass1, ClassFileBase[] classFileBases, NamedSet[] namedSets, List list1) throws ZkmException, IOException {
        int ba = namedSets.length;
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();

        for (ProgramClass programClass2 : programClass1) {
            if (!programClass2.isInterface() || programClass2.supportsJava8()) {
                LinkedHashSet linkedHashSet = new LinkedHashSet();

                for (int i = 0; i < ba; i++) {
                    NamedSet namedSet = namedSets[i];
                    if (namedSet.contains(programClass2)) {
                        linkedHashSet.add(new IdentityValueHolder(namedSet));
                    }
                }

                if (linkedHashSet.size() > 1) {
                    HashSet hashSet3 = ZkmUtils.createHashSet();
                    Iterator iterator5 = linkedHashSet.iterator();

                    while (iterator5.hasNext()) {
                        IdentityValueHolder identityValueHolder = (IdentityValueHolder) iterator5.next();
                        hashSet3.add(identityValueHolder.getValue());
                        hashSet.add(identityValueHolder);
                    }

                    hashSet1.add(hashSet3);
                }
            }
        }

        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < ba; i++) {
            NamedSet namedSet2 = namedSets[i];
            if (!hashSet.contains(new IdentityValueHolder(namedSet2))) {
                arrayList.add(new ClassGroupingSet(namedSet2));
            }
        }

        ArrayList arrayList1 = new ArrayList(hashSet1);
        Collections.sort(arrayList1, (hashSet6, hashSet7) -> ((java.util.HashSet) hashSet6).size() - ((java.util.HashSet) hashSet7).size());
        Iterator iterator3 = arrayList1.iterator();

        while (iterator3.hasNext()) {
            short baj = 0;
            long bak = 78229960172139L;
            boolean bl1 = false;
            HashSet hashSet5 = (HashSet) iterator3.next();

            for (int i = arrayList1.size() - 1; i >= 0; i += -1) {
                HashSet hashSet2 = (HashSet) arrayList1.get(i);
                if (hashSet2.size() <= hashSet5.size()) {
                    break;
                }

                if (hashSet2.containsAll(hashSet5)) {
                    bl1 = true;
                    break;
                }
            }

            if (bl1) {
                hashSet1.remove(hashSet5);
                iterator3.remove();
            }
        }

        iterator3 = hashSet1.iterator();

        while (iterator3.hasNext()) {
            HashSet hashSet4 = (HashSet) iterator3.next();
            NamedSet[] namedSets1 = ((com.zelix.klassmaster.util.NamedSet[]) (hashSet4.toArray(new NamedSet[hashSet4.size()])));
            ClassGroupingSet classGroupingSet2 = new ClassGroupingSet(namedSets1[0]);
            int bi = namedSets1.length;

            for (int i = 1; i < bi; i++) {
                classGroupingSet2 = classGroupingSet2.mergeIfOverlapping(new ClassGroupingSet(namedSets1[i]));
            }

            arrayList.add(classGroupingSet2);
        }

        Collections.sort(arrayList);
        int be = arrayList.size();
        Iterator iterator4 = arrayList.iterator();

        while (iterator4.hasNext()) {
            ClassGroupingSet classGroupingSet = (ClassGroupingSet) iterator4.next();
            FlowObfuscationGroup flowObfuscationGroup = new FlowObfuscationGroup(classGroupingSet);
            list1.add(flowObfuscationGroup);
        }

        for (int i = be - 1; i >= 0; i += -1) {
            ClassGroupingSet classGroupingSet1 = (ClassGroupingSet) arrayList.get(i);

            for (int j = i - 1; j >= 0; j += -1) {
                ClassGroupingSet classGroupingSet3 = (ClassGroupingSet) arrayList.get(j);
                NamedSet namedSet3 = classGroupingSet1.getSharedSets(classGroupingSet3);
                if (namedSet3.size() > 0) {
                    NamedSet namedSet1 = ZkmUtils.createNamedSet();
                    Iterator iterator = namedSet3.iterator();

                    while (iterator.hasNext()) {
                        namedSet1.addAll((Collection) iterator.next());
                    }

                    NamedSet namedSet4 = classGroupingSet3.getSetsNotIn(classGroupingSet1);
                    Iterator iterator1 = namedSet1.iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                        boolean bl = false;
                        Iterator iterator2 = namedSet4.iterator();

                        while (iterator2.hasNext()) {
                            if (((NamedSet) iterator2.next()).contains(classFileBase)) {
                                bl = true;
                            }
                        }

                        if (bl) {
                            FlowObfuscationGroup flowObfuscationGroup1 = (FlowObfuscationGroup) list1.get(i);
                            flowObfuscationGroup1.removeMemberClass(classFileBase);
                        } else {
                            FlowObfuscationGroup flowObfuscationGroup2 = (FlowObfuscationGroup) list1.get(j);
                            flowObfuscationGroup2.removeMemberClass(classFileBase);
                        }
                    }
                }
            }
        }

        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(classFileBases.length));
        indexGroupsByClass(list1, hashMap);
        return hashMap;
    }

    public static NamedSet[] computeGroupingSets(
            ProgramClass[] programClass1,
            ClassFileBase[] classFileBases,
            GroupingsSpec groupingsSpec1,
            ChangeLogMapping changeLogMapping1,
            ClassHierarchyQuery classHierarchyQuery,
            Map map1
    ) throws ZkmException, IOException {
        int ba = programClass1.length;
        TwoKeyMap twoKeyMap = new TwoKeyMap();

        for (int i = 0; i < ba; i++) {
            ProgramClass programClass2 = programClass1[i];
            if ((!programClass2.isInterface() || programClass2.supportsJava8()) && !programClass2.isGenerated()) {
                Enumeration enumeration = programClass2.enumerateInputLocations();

                while (enumeration.hasMoreElements()) {
                    InputFileLocation inputFileLocation = (InputFileLocation) enumeration.nextElement();
                    if (inputFileLocation.isPlainFile()) {
                        twoKeyMap.putValue("FILE SYSTEM", programClass2, programClass2);
                    } else {
                        SourceArchive sourceArchive1 = inputFileLocation.getSourceArchive();
                        twoKeyMap.putValue(sourceArchive1.getQualifiedPath(), programClass2, programClass2);
                    }
                }
            }
        }

        if (groupingsSpec1 != null) {
            ReadOnlyMultiMap readOnlyMultiMap = groupingsSpec1.getGroupedClasses();
            ArrayList arrayList = new ArrayList();
            Enumeration enumeration1 = readOnlyMultiMap.keys();

            while (enumeration1.hasMoreElements()) {
                arrayList.add(enumeration1.nextElement());
            }

            for (int i = 0; i < arrayList.size(); i++) {
                String string = (String) arrayList.get(i);
                EnumerableMap enumerableMap = readOnlyMultiMap.getInnerMap(string);
                Map map2 = enumerableMap.copyMap();
                Iterator iterator = map2.keySet().iterator();

                while (iterator.hasNext()) {
                    ProgramClass programClass3 = (ProgramClass) iterator.next();
                    if (programClass3.isInterface() && !programClass3.supportsJava8()) {
                        iterator.remove();
                    }
                }

                if (map2.size() > 0) {
                    twoKeyMap.putInnerMap(string, map2);
                }
            }

            if (groupingsSpec1.isLoggingEnabled()) {
                groupingsSpec1.printGroupingParameters();
                Collections.sort(arrayList);
                PrintWriter printWriter = groupingsSpec1.getLogWriter();
                printWriter.println("\tMatched groupings:");

                for (int i = 0; i < arrayList.size(); i++) {
                    String string1 = (String) arrayList.get(i);
                    printWriter.println("\tGrouping \"" + string1 + "\"");
                    EnumerableMap enumerableMap1 = readOnlyMultiMap.getInnerMap(string1);
                    ArrayList arrayList1 = new ArrayList();
                    Enumeration enumeration3 = enumerableMap1.keys();

                    while (enumeration3.hasMoreElements()) {
                        ProgramClass programClass4 = (ProgramClass) enumeration3.nextElement();
                        arrayList1.add(new LabeledTuple(programClass4.getDottedClassName(), programClass4));
                    }

                    Collections.sort(arrayList1);

                    for (int j = 0; j < arrayList1.size(); j++) {
                        LabeledTuple labeledTuple = (LabeledTuple) arrayList1.get(j);
                        printWriter.println("\t\t" + AbstractExclusionSpec.formatClassWithModifiers((ClassFileBase) labeledTuple.getFirst(), classHierarchyQuery));
                    }
                }
            }
        }

        NamedSet[] namedSets1;
        if (changeLogMapping1 != null && classFileBases.length > programClass1.length) {
            NamedSet[] namedSets = deriveGroupingSetsFromChangeLog(changeLogMapping1, map1);
            namedSets1 = new NamedSet[namedSets.length + twoKeyMap.getKeyCount()];
            System.arraycopy(namedSets, 0, namedSets1, twoKeyMap.getKeyCount(), namedSets.length);
        } else {
            namedSets1 = new NamedSet[twoKeyMap.getKeyCount()];
        }

        int bd = 0;
        Enumeration enumeration2 = twoKeyMap.keys();

        while (enumeration2.hasMoreElements()) {
            String string2 = (String) enumeration2.nextElement();
            Map map3 = twoKeyMap.getInnerMap(string2);
            namedSets1[bd++] = ZkmUtils.createNamedSetFromMap(string2, map3);
        }

        return namedSets1;
    }

    public void emitNegatedGetterBody(
            ArrayList arrayList, LocalVariableList localVariableList1, ClassFileBase classFileBase, AbstractMethodInfo abstractMethodInfo, List list1, Random random1
    ) {
        ResolvedMethodRefConstant resolvedMethodRefConstant = classFileBase.getConstantPool().getOrCreateMethodRefConstant(abstractMethodInfo, list1);
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(Instruction.createIntStore(0, localVariableList1, 2));
        arrayList.add(Instruction.createIntLoad(0, localVariableList1, 2));
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        arrayList.add(new BranchInstruction(154, labelInstruction));
        ArrayList arrayList1;
        short ba;
        if (abstractMethodInfo.getDescriptor().equals("()Z")) {
            arrayList.add(SimpleInstruction.forOpcode(4));
            arrayList1 = arrayList;
            ba = 172;
        } else {
            arrayList.add(Instruction.createIntPush(random1.nextInt(126) + 1));
            arrayList1 = arrayList;
            ba = 172;
        }

        arrayList1.add(SimpleInstruction.forOpcode(ba));
        arrayList.add(labelInstruction);
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(SimpleInstruction.forOpcode(172));
    }

    public OpaquePredicateField getOrCreateChangeLogField(
            ChangeLogMemberEntry changeLogMemberEntry,
            ClassFileBase classFileBase,
            ChangeLogMapping changeLogMapping1,
            Random random1,
            TwoKeyMap twoKeyMap,
            Map map1,
            TwoKeyMap twoKeyMap1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmException, IOException {
        OpaquePredicateField opaquePredicateField = null;
        String string = changeLogMemberEntry.getFieldName();
        String string1 = changeLogMemberEntry.getFieldDescriptor();
        String string2 = changeLogMemberEntry.getFieldTypeName();
        String string3 = classFileBase.getClassName();
        if (!changeLogMemberEntry.hasNoField()) {
            if (classFileBase.isProgramClass()) {
                opaquePredicateField = (OpaquePredicateField) twoKeyMap.getValue(classFileBase, string);
                if (opaquePredicateField == null) {
                    if (this.isUsedInEarlierPass(changeLogMemberEntry, classFileBase, inheritedMemberAnalyzer, new ObservableHolder())) {
                        opaquePredicateField = this.createPredicateField((ProgramClass) classFileBase, inheritedMemberAnalyzer, classMemberLookup1, random1);
                        String string4 = opaquePredicateField.getFieldName();
                        map1.put(new Triple(string3, string, string2), string4);
                        if (changeLogMemberEntry.hasSetterMethod()) {
                            twoKeyMap1.putValue(
                                    classFileBase.getClassName(),
                                    new Triple(changeLogMemberEntry.getSetterMethodName(), changeLogMemberEntry.getFieldTypeName(), "void"),
                                    opaquePredicateField.getSetterName()
                            );
                            twoKeyMap1.putValue(
                                    classFileBase.getClassName(),
                                    new Triple(changeLogMemberEntry.getGetterMethodName(), "", changeLogMemberEntry.getFieldTypeName()),
                                    opaquePredicateField.getGetterName()
                            );
                            if (changeLogMemberEntry.getNewAltGetterMethodName() != null) {
                                twoKeyMap1.putValue(
                                        classFileBase.getClassName(),
                                        new Triple(changeLogMemberEntry.getAltGetterMethodName(), "", changeLogMemberEntry.getFieldTypeName()),
                                        opaquePredicateField.getNegatedGetterName()
                                );
                            }
                        }
                    } else {
                        opaquePredicateField = this.createPredicateFieldFromChangeLog(
                                changeLogMemberEntry, (ProgramClass) classFileBase, inheritedMemberAnalyzer, classMemberLookup1, random1
                        );
                    }

                    OpaquePredicateField opaquePredicateField1 = (OpaquePredicateField) twoKeyMap.putValue(
                            classFileBase, opaquePredicateField.getFieldName(), opaquePredicateField
                    );
                } else if (string1 != null && !opaquePredicateField.getFieldType().equals(string1)) {
                    changeLogMapping1.logError(
                            "The field '"
                                    + string
                                    + "' in class '"
                                    + classFileBase.getDottedClassName()
                                    + "' appears in the change log more than once but with different types.  You must distribute this application as a whole."
                    );
                }
            } else {
                opaquePredicateField = (OpaquePredicateField) twoKeyMap.getValue(classFileBase, string);
                if (opaquePredicateField == null) {
                    opaquePredicateField = this.createLibraryPredicateField(changeLogMemberEntry, (ClasspathClassFile) classFileBase, inheritedMemberAnalyzer);
                }
            }
        } else if (classFileBase.isProgramClass()) {
            opaquePredicateField = this.createPredicateField((ProgramClass) classFileBase, inheritedMemberAnalyzer, classMemberLookup1, random1);
            OpaquePredicateField opaquePredicateField2 = (OpaquePredicateField) twoKeyMap.putValue(
                    classFileBase, opaquePredicateField.getFieldName(), opaquePredicateField
            );
        } else {
            changeLogMapping1.reportFatalError(
                    "Class '"
                            + classFileBase.getDottedClassName()
                            + "' appears in the change log flow obfuscation data but has not been opened for obfuscation yet its flow obfuscation data is incomplete.  For incremental obfuscation, the flow data must be complete."
            );
        }

        return opaquePredicateField;
    }

    public int getGroupFieldCount() {
        return this.groupFields.size();
    }

    public MethodInfo createNamedGetter(
            ProgramClass programClass1,
            String string,
            FieldInfo fieldInfo,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        String string1 = OpaquePredicateField.getterDescriptorFor(fieldInfo.getDescriptor());
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        this.emitGetterBody(arrayList, programClass1, fieldInfo, list1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createPublicStaticMethod(
                string, string1, arrayList, 0, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer, classMemberLookup1
        );
        this.generatedMethods.add(methodInfo1);
        return methodInfo1;
    }

    public Set getAllPairFields() {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.fieldPairsByClass.entrySet().iterator();

        while (iterator.hasNext()) {
            FlowFieldPair flowFieldPair = (FlowFieldPair) ((Entry) iterator.next()).getValue();
            hashSet.add(flowFieldPair.getGroupField());
            hashSet.add(flowFieldPair.getPackageField());
        }

        return hashSet;
    }

    public String pickRandomFieldType(Object object, Random random1) {
        SyncIndexedSet syncIndexedSet = (SyncIndexedSet) this.fieldTypesByGroup.get(object);
        int ba = random1.nextInt(syncIndexedSet.size());
        return (String) syncIndexedSet.getElementAt(ba);
    }

    public boolean hasNonZeroValuedField() {
        Iterator iterator = this.fieldPairsByClass.entrySet().iterator();

        while (iterator.hasNext()) {
            FlowFieldPair flowFieldPair = (FlowFieldPair) ((Entry) iterator.next()).getValue();
            if (flowFieldPair.getGroupField().isNonZeroValue() || flowFieldPair.getPackageField().isNonZeroValue()) {
                return true;
            }
        }

        return false;
    }

    public MethodInfo createNamedNegatedGetter(
            ProgramClass programClass1,
            String string,
            MethodInfo methodInfo1,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            Random random1
    ) throws ZkmProcessingException {
        String string1 = methodInfo1.getDescriptor();
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        this.emitNegatedGetterBody(arrayList, localVariableList1, programClass1, methodInfo1, list1, random1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo2 = programClass1.createPublicStaticMethod(
                string, string1, arrayList, 1, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer, classMemberLookup1
        );
        this.generatedMethods.add(methodInfo2);
        return methodInfo2;
    }

    public LibraryMethod createLibraryNegatedGetter(
            ClasspathClassFile classpathClassFile,
            String string,
            AbstractMethodInfo abstractMethodInfo,
            List list1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Random random1
    ) throws ZkmProcessingException {
        String string1 = abstractMethodInfo.getDescriptor();
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        this.emitNegatedGetterBody(arrayList, localVariableList1, classpathClassFile, abstractMethodInfo, list1, random1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        return classpathClassFile.createCodeMethod(string, string1, arrayList, 1, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer);
    }

    public void emitGetterBody(ArrayList arrayList, ClassFileBase classFileBase, AbstractFieldInfo abstractFieldInfo, List list1) {
        String string = abstractFieldInfo.getDescriptor();
        ResolvedFieldRef resolvedFieldRef = classFileBase.getConstantPool()
                .getOrCreateFieldRef(abstractFieldInfo.getClassName(), abstractFieldInfo.getSourceName(), abstractFieldInfo.getDescriptor(), list1, abstractFieldInfo);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        ArrayList arrayList1;
        short ba;
        if (!string.equals("I")) {
            if (!string.equals("Z")) {
                arrayList.add(SimpleInstruction.forOpcode(176));
                return;
            }

            arrayList1 = arrayList;
            ba = 172;
        } else {
            arrayList1 = arrayList;
            ba = 172;
        }

        arrayList1.add(SimpleInstruction.forOpcode(ba));
    }

    public void emitSetterBody(
            ArrayList arrayList, LocalVariableList localVariableList1, ClassFileBase classFileBase, AbstractFieldInfo abstractFieldInfo, List list1
    ) {
        String string = abstractFieldInfo.getDescriptor();
        ResolvedFieldRef resolvedFieldRef = classFileBase.getConstantPool()
                .getOrCreateFieldRef(abstractFieldInfo.getClassName(), abstractFieldInfo.getSourceName(), abstractFieldInfo.getDescriptor(), list1, abstractFieldInfo);
        if (!string.equals("I") && !string.equals("Z")) {
            arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 2));
        } else {
            arrayList.add(Instruction.createIntLoad(0, localVariableList1, 2));
        }

        arrayList.add(new ConstantRefInstruction(179, resolvedFieldRef));
        arrayList.add(SimpleInstruction.forOpcode(177));
    }

    public boolean isPrimaryCallerClass(Object object) {
        return this.primaryCallerClasses.contains(object);
    }

    public static void indexGroupsByClass(List list1, Map map1) {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) iterator.next();
            flowObfuscationGroup.collectPackageNames();
            Iterator iterator1 = flowObfuscationGroup.getMemberClasses().iterator();

            while (iterator1.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                map1.put(classFileBase, flowObfuscationGroup);
            }
        }
    }

    public int getGroupCount() {
        return this.groups.size();
    }

    public void selectCallerMethods(
            Set set1, Set set2, ListMultimap listMultimap, FlowObfuscationExclusions flowObfuscationExclusions, StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1
    ) {
        Iterator iterator = this.groups.iterator();

        while (iterator.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) iterator.next();
            if (flowObfuscationGroup.getGroupField() != null) {
                Set set3 = flowObfuscationGroup.getMemberClasses();
                ArrayList arrayList = new ArrayList(set3.size());
                Iterator iterator1 = set3.iterator();

                while (iterator1.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                    if (classFileBase.isProgramClass()) {
                        List list1 = listMultimap.getValues((ProgramClass) classFileBase);
                        if (list1 != null) {
                            arrayList.addAll(list1);
                        }
                    }
                }

                Collections.sort(arrayList);
                Collections.reverse(arrayList);
                int ba = flowObfuscationGroup.getPackageNames().size();
                HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
                HashMap hashMap1 = ZkmUtils.createHashMap(hashMap.size());
                Iterator iterator2 = arrayList.iterator();

                while (iterator2.hasNext()) {
                    WeightedObject weightedObject = (WeightedObject) iterator2.next();
                    MethodBytecode methodBytecode1 = (MethodBytecode) weightedObject.value;
                    String string = methodBytecode1.getPackagePath();
                    if (ba <= hashMap.size() && ba <= hashMap1.size() || weightedObject.weight < 10.0F) {
                        break;
                    }

                    if ((flowObfuscationExclusions == null || !flowObfuscationExclusions.isMethodExcluded((MethodInfo) methodBytecode1.getMethod()))
                            && (staticInitCalleeAnalyzer1 == null || !staticInitCalleeAnalyzer1.isCalledByInitializer((MethodInfo) methodBytecode1.getMethod()))
                            && !methodBytecode1.getOwningClass().isMultiRelease()
                            && !methodBytecode1.isStaticInitializer()
                            && !methodBytecode1.isPrivate()) {
                        if (!hashMap.containsKey(string)) {
                            hashMap.put(string, methodBytecode1);
                        } else if (!hashMap1.containsKey(string)) {
                            hashMap1.put(string, methodBytecode1);
                        }
                    }
                }

                iterator2 = hashMap.entrySet().iterator();

                while (iterator2.hasNext()) {
                    Entry entry = (Entry) iterator2.next();
                    MethodBytecode methodBytecode4 = (MethodBytecode) entry.getValue();
                    set1.add(methodBytecode4);
                }

                iterator2 = hashMap1.entrySet().iterator();

                while (iterator2.hasNext()) {
                    Entry entry1 = (Entry) iterator2.next();
                    MethodBytecode methodBytecode5 = (MethodBytecode) entry1.getValue();
                    set2.add(methodBytecode5);
                }
            }
        }

        iterator = set1.iterator();

        while (iterator.hasNext()) {
            MethodBytecode methodBytecode2 = (MethodBytecode) iterator.next();
            this.primaryCallerClasses.add(methodBytecode2.getOwningClass());
        }

        iterator = set2.iterator();

        while (iterator.hasNext()) {
            MethodBytecode methodBytecode3 = (MethodBytecode) iterator.next();
            this.secondaryCallerClasses.add(methodBytecode3.getOwningClass());
        }
    }

    public OpaquePredicateField createPredicateField(
            ProgramClass programClass1, InheritedMemberAnalyzer inheritedMemberAnalyzer, ClassMemberLookup classMemberLookup1, Random random1
    ) throws ZkmException, IOException {
        FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) this.groupsByClass.get(programClass1);
        String string = this.pickRandomFieldType(flowObfuscationGroup, random1);
        OpaquePredicateField opaquePredicateField;
        if (this.isAdvancedModeEnabled()) {
            FieldInfo fieldInfo = programClass1.createUniquelyNamedStaticField(string, 1, false, inheritedMemberAnalyzer, classMemberLookup1, 2);
            ArrayList arrayList = new ArrayList();
            MethodInfo methodInfo1 = this.createSetter(programClass1, fieldInfo, arrayList, inheritedMemberAnalyzer, classMemberLookup1);
            MethodInfo methodInfo2 = this.createGetter(programClass1, fieldInfo, arrayList, inheritedMemberAnalyzer, classMemberLookup1);
            MethodInfo methodInfo3 = null;
            if (string.equals("I") || string.equals("Z")) {
                methodInfo3 = this.createNegatedGetter(programClass1, methodInfo2, arrayList, inheritedMemberAnalyzer, classMemberLookup1, random1);
            }

            programClass1.getClassConstantPool().appendEntries(arrayList);
            opaquePredicateField = new OpaquePredicateField(fieldInfo, methodInfo1, methodInfo2, methodInfo3, random1);
        } else {
            FieldInfo fieldInfo1 = programClass1.createUniquelyNamedStaticField(string, 4, false, inheritedMemberAnalyzer, classMemberLookup1, 2);
            opaquePredicateField = new OpaquePredicateField(fieldInfo1);
        }

        return opaquePredicateField;
    }

    public FlowObfuscationManager(
            ProgramClass[] programClass1,
            ClassFileBase[] classFileBases,
            ScriptEnvironment scriptEnvironment1,
            GroupingsSpec groupingsSpec1,
            ChangeLogMapping changeLogMapping1,
            ClassMemberLookup classMemberLookup1,
            ProcessingStatistics processingStatistics1,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            boolean deriveGroupingsFromChangeLog,
            boolean useFixedSeed
    ) throws ZkmException, IOException {
        label44:
        {
            this.groups = new ArrayList();
            this.groupFields = new ArrayList();
            this.packageFieldsByPackage = new ListMultimap();
            this.groupFieldByPackageField = ZkmUtils.createHashMap();
            this.generatedMethods = ZkmUtils.createHashSet();
            this.primaryCallerClasses = ZkmUtils.createHashSet();
            this.secondaryCallerClasses = ZkmUtils.createHashSet();
            this.programClasses = programClass1;
            this.classFiles = classFileBases;
            this.groupingsSpec = groupingsSpec1;
            this.changeLogMapping = changeLogMapping1;
            this.classMemberLookup = classMemberLookup1;
            this.processingStatistics = processingStatistics1;
            this.staticInitCalleeAnalyzer = staticInitCalleeAnalyzer1;
            this.deriveGroupingsFromChangeLog = deriveGroupingsFromChangeLog;
            this.fieldPairsByClass = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(classFileBases.length));
            this.useFixedSeed = useFixedSeed;
            FlowObfuscationManager flowObfuscationManager1;
            short bb;
            if (!this.useFixedSeed) {
                if (!HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
                    this.random = ZkmUtils.createSeededRandom(this.programClasses.length);
                    break label44;
                }

                flowObfuscationManager1 = this;
                bb = 128;
            } else {
                flowObfuscationManager1 = this;
                bb = 128;
            }

            flowObfuscationManager1.random = ZkmUtils.createRandom(bb);
        }

        this.random.nextBoolean();
        if (!deriveGroupingsFromChangeLog) {
            this.groupingSets = computeGroupingSets(programClass1, classFileBases, groupingsSpec1, changeLogMapping1, classMemberLookup1, (Map) null);
            this.groupsByClass = buildGroups(programClass1, classFileBases, this.groupingSets, this.groups);
        } else {
            if (this.groupingsSpec != null) {
                scriptEnvironment1.logWarning(
                        "'deriveGroupingsFromInputChangeLog' specified but 'groupings' statement present. 'groupings' statement will be ignored."
                );
            }

            if (changeLogMapping1 != null) {
                if (changeLogMapping1.hasTraceBackEntries()) {
                    Object object = null;
                    ChangeLogMapping changeLogMapping2 = changeLogMapping1;
                    this.groupingSets = deriveGroupingSetsFromChangeLog(changeLogMapping2, (Map) object);
                    this.groupsByClass = buildGroupsFromChangeLog(programClass1, this.classFiles, changeLogMapping1, this.groupingSets, this.groups);
                } else {
                    scriptEnvironment1.logSeriousError_v(
                            "'deriveGroupingsFromInputChangeLog' specified but input change log '"
                                    + changeLogMapping1.getChangeLogName()
                                    + " has no flow obfuscation data."
                    );
                    this.groupingSets = null;
                    this.groupsByClass = null;
                }
            } else {
                scriptEnvironment1.logSeriousError_v("'deriveGroupingsFromInputChangeLog' specified but there is no input change log.");
                this.groupingSets = null;
                this.groupsByClass = null;
            }
        }

        for (int i = 0; i < 5; i++) {
            try {
                Class<?> class1 = Class.forName(this.getEncodedClassName(i));
                this.extensionHook = class1.newInstance();
                break;
            } catch (Throwable throwable) {
            }
        }
    }

    public MethodInfo createGetter(
            ProgramClass programClass1, FieldInfo fieldInfo, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer, ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        String string = OpaquePredicateField.getterDescriptorFor(fieldInfo.getDescriptor());
        LocalVariableList localVariableList1 = new LocalVariableList(true, string, 5);
        ArrayList arrayList = new ArrayList();
        this.emitGetterBody(arrayList, programClass1, fieldInfo, list1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedPublicStaticMethod(
                string, arrayList, 0, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer, classMemberLookup1
        );
        this.generatedMethods.add(methodInfo1);
        return methodInfo1;
    }

    public void addArrayFieldType(Object object, ClassFileBase classFileBase) {
        StringBuilder stringBuilder = new StringBuilder(classFileBase.getClassName().length() + 3);
        stringBuilder.append('[');
        stringBuilder.append('L');
        stringBuilder.append(classFileBase.getClassName());
        stringBuilder.append(';');
        ((SyncIndexedSet) this.fieldTypesByGroup.get(object)).add(stringBuilder.toString());
    }

    public boolean hasExcludedChangeLogMembers(
            ChangeLogMemberEntry changeLogMemberEntry,
            ClassFileBase classFileBase,
            TwoKeyMap twoKeyMap,
            Map map1,
            NameExclusionSet nameExclusionSet,
            ClassMemberLookup classMemberLookup1,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        boolean bl = false;
        String string = changeLogMemberEntry.getFieldName();
        String string1 = changeLogMemberEntry.getInternalClassName();
        String string2 = changeLogMemberEntry.getClassName();
        String string3 = changeLogMemberEntry.getFieldTypeName();
        String string4 = changeLogMemberEntry.getFieldDescriptor();
        if (string != null) {
            String string5 = changeLogMemberEntry.getNewFieldName();
            FieldInfo fieldInfo = null;
            if (classFileBase.isProgramClass()) {
                fieldInfo = classMemberLookup1.findField(string1, string5, string4);
            }

            if (fieldInfo != null && fieldInfo.isProgramMember() && nameExclusionSet.isFieldExcluded(fieldInfo)) {
                observableHolder.setValue(
                        "Class '"
                                + string2
                                + "' appears as a Flow Obfuscation "
                                + (changeLogMemberEntry.isTraceBackEntry() ? "TraceBackClass:" : "ForwardClass:")
                                + " but it is now incompatible. You must distribute this application as a whole. : "
                                + fieldInfo.toDisplayString()
                );
                ObjectPair objectPair = new ObjectPair(string, string3);
                twoKeyMap.putValue(string1, objectPair, objectPair);
                bl = true;
            }

            if (changeLogMemberEntry.getSetterMethodName() != null) {
                String string6 = changeLogMemberEntry.getNewSetterMethodName();
                MethodInfo methodInfo1 = null;
                if (classFileBase.isProgramClass()) {
                    methodInfo1 = classMemberLookup1.findDeclaredMethod(
                            (ProgramClass) classFileBase, new MethodSignature(string6, OpaquePredicateField.setterDescriptorFor(string4))
                    );
                }

                if (methodInfo1 != null && methodInfo1.isProgramMember() && nameExclusionSet.isMethodExcluded(methodInfo1)) {
                    observableHolder.setValue(
                            "Class '"
                                    + string2
                                    + "' appears as a Flow Obfuscation "
                                    + (changeLogMemberEntry.isTraceBackEntry() ? "TraceBackClass:" : "ForwardClass:")
                                    + " but it is now incompatible. You must distribute this application as a whole. : "
                                    + methodInfo1.toDisplayString()
                                    + " (A)"
                    );
                    map1.put(string1, new Triple(changeLogMemberEntry.getSetterMethodName(), string3, "void"));
                    bl = true;
                }
            }

            if (changeLogMemberEntry.getGetterMethodName() != null) {
                String string7 = changeLogMemberEntry.getNewGetterMethodName();
                MethodInfo methodInfo2 = null;
                if (classFileBase.isProgramClass()) {
                    methodInfo2 = classMemberLookup1.findDeclaredMethod(
                            (ProgramClass) classFileBase, new MethodSignature(string7, OpaquePredicateField.getterDescriptorFor(string4))
                    );
                }

                if (methodInfo2 != null && methodInfo2.isProgramMember() && nameExclusionSet.isMethodExcluded(methodInfo2)) {
                    observableHolder.setValue(
                            "Class '"
                                    + string2
                                    + "' appears as a Flow Obfuscation "
                                    + (changeLogMemberEntry.isTraceBackEntry() ? "TraceBackClass:" : "ForwardClass:")
                                    + " but it is now incompatible. You must distribute this application as a whole. : "
                                    + methodInfo2.toDisplayString()
                                    + " (B)"
                    );
                    map1.put(string1, new Triple(changeLogMemberEntry.getGetterMethodName(), "", string3));
                    bl = true;
                }
            }

            if (changeLogMemberEntry.getAltGetterMethodName() != null) {
                String string8 = changeLogMemberEntry.getNewAltGetterMethodName();
                MethodInfo methodInfo3 = null;
                if (classFileBase.isProgramClass()) {
                    methodInfo3 = classMemberLookup1.findDeclaredMethod(
                            (ProgramClass) classFileBase, new MethodSignature(string8, OpaquePredicateField.getterDescriptorFor(string4))
                    );
                }

                if (methodInfo3 != null && methodInfo3.isProgramMember() && nameExclusionSet.isMethodExcluded(methodInfo3)) {
                    observableHolder.setValue(
                            "Class '"
                                    + string2
                                    + "' appears as a Flow Obfuscation "
                                    + (changeLogMemberEntry.isTraceBackEntry() ? "TraceBackClass:" : "ForwardClass:")
                                    + " but it is now incompatible. You must distribute this application as a whole. : "
                                    + methodInfo3.toDisplayString()
                                    + " (C)"
                    );
                    map1.put(string1, new Triple(changeLogMemberEntry.getAltGetterMethodName(), "", string3));
                    bl = true;
                }
            }
        }

        return bl;
    }

    public SetMultiMap assignPredicateFields(
            HashSet hashSet,
            NameExclusionSet nameExclusionSet,
            ClassHierarchy classHierarchy1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1
    ) throws ZkmException, IOException {
        if (this.extensionHook != null) {
            try {
                Class<?> class1 = this.extensionHook.getClass();
                Method method1 = class1.getDeclaredMethod(this.getEncodedClassName(0));
                if (method1 != null) {
                    method1.invoke(null);
                }
            } catch (Throwable throwable) {
            }
        }

        SetMultiMap setMultiMap = new SetMultiMap();
        if (this.changeLogMapping != null) {
            ArrayList arrayList = this.changeLogMapping.getOldClassNames();

            for (int i = 0; i < arrayList.size(); i++) {
                hashSet.add(ClassFileBase.getPackagePath(ZkmUtils.dotsToSlashes((String) arrayList.get(i))));
            }
        }

        int be = hashSet.size();
        int bf = this.programClasses.length;
        CountingBag countingBag = new CountingBag(bf);
        CountingBag countingBag1 = new CountingBag(1);

        for (int i = 0; i < bf; i++) {
            this.programClasses[i].countReferencedProgramClasses(countingBag);
        }

        List list1 = classHierarchy1.getTopProgramNodes();
        int bc = list1.size();

        for (int i = 0; i < bc; i++) {
            ((ClassHierarchyNode) list1.get(i)).sumSubtreeCounts(new MutableInt(0), new MutableInt(0), countingBag, countingBag1);
        }

        TwoKeyMap twoKeyMap = new TwoKeyMap();
        this.fieldTypesByGroup = this.createFieldTypeSets();
        if (this.changeLogMapping != null) {
            this.restoreChangeLogFields(
                    hashSet,
                    countingBag,
                    nameExclusionSet,
                    flowObfuscationExclusions,
                    classInitOrderHandler1,
                    this.random,
                    twoKeyMap,
                    setMultiMap,
                    inheritedMemberAnalyzer,
                    classMemberLookup1
            );
        }

        Iterator iterator = this.groups.iterator();

        while (iterator.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) iterator.next();
            Iterator iterator1 = this.groupFields.iterator();

            while (iterator1.hasNext()) {
                OpaquePredicateField opaquePredicateField = (OpaquePredicateField) iterator1.next();
                String string = opaquePredicateField.getOwnerClassName();
                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string);
                if (flowObfuscationGroup.isCommonClass(classFileBase)) {
                    flowObfuscationGroup.setGroupField(opaquePredicateField);
                    break;
                }
            }

            iterator1 = flowObfuscationGroup.getSortedPackageNames().iterator();

            while (iterator1.hasNext()) {
                String string3 = (String) iterator1.next();
                List list3 = this.packageFieldsByPackage.getValues(string3);
                if (list3 != null) {
                    Iterator iterator4 = list3.iterator();

                    while (iterator4.hasNext()) {
                        OpaquePredicateField opaquePredicateField1 = (OpaquePredicateField) iterator4.next();
                        String string1 = opaquePredicateField1.getOwnerClassName();
                        ClassFileBase classFileBase1 = ClassHierarchyNode.findClassFile(string1);
                        if (flowObfuscationGroup.isCommonClass(classFileBase1)) {
                            flowObfuscationGroup.putPackageField(string3, opaquePredicateField1);
                            break;
                        }
                    }
                }
            }

            CountingBag countingBag2 = ZkmUtils.copyCountingBag(countingBag);
            countingBag2.retainAll(flowObfuscationGroup.getCommonClasses());
            List list2 = countingBag2.getSortedByCount();
            Iterator iterator3 = list2.iterator();
            this.assignFieldsFromCandidates(
                    iterator3,
                    flowObfuscationGroup,
                    this.random,
                    twoKeyMap,
                    setMultiMap,
                    inheritedMemberAnalyzer,
                    this.staticInitCalleeAnalyzer,
                    classMemberLookup1,
                    be,
                    flowObfuscationExclusions,
                    classInitOrderHandler1,
                    1
            );
            if (!flowObfuscationGroup.hasAllFields()) {
                this.assignFieldsFromCandidates(
                        iterator3,
                        flowObfuscationGroup,
                        this.random,
                        twoKeyMap,
                        setMultiMap,
                        inheritedMemberAnalyzer,
                        this.staticInitCalleeAnalyzer,
                        classMemberLookup1,
                        be,
                        flowObfuscationExclusions,
                        classInitOrderHandler1,
                        2
                );
            }

            if (be == 1 && !flowObfuscationGroup.hasAllFields()) {
                iterator3 = list2.iterator();
                this.assignFieldsFromCandidates(
                        iterator3,
                        flowObfuscationGroup,
                        this.random,
                        twoKeyMap,
                        setMultiMap,
                        inheritedMemberAnalyzer,
                        this.staticInitCalleeAnalyzer,
                        classMemberLookup1,
                        be,
                        flowObfuscationExclusions,
                        classInitOrderHandler1,
                        3
                );
            }

            OpaquePredicateField opaquePredicateField7 = flowObfuscationGroup.getGroupField();
            if (opaquePredicateField7 != null) {
                if (!flowObfuscationGroup.hasAllFields()) {
                    iterator3 = list2.iterator();
                    this.assignFieldsFromCandidates(
                            iterator3,
                            flowObfuscationGroup,
                            this.random,
                            twoKeyMap,
                            setMultiMap,
                            inheritedMemberAnalyzer,
                            this.staticInitCalleeAnalyzer,
                            classMemberLookup1,
                            be,
                            flowObfuscationExclusions,
                            classInitOrderHandler1,
                            4
                    );
                }

                if (!flowObfuscationGroup.hasAllFields()) {
                    String string4 = opaquePredicateField7.getOwnerClassName();
                    ClassFileBase classFileBase3 = ClassHierarchyNode.findClassFile(string4);
                    if (classFileBase3.isProgramClass()) {
                        Iterator iterator6 = flowObfuscationGroup.getSortedPackageNames().iterator();

                        while (iterator6.hasNext()) {
                            String string2 = (String) iterator6.next();
                            if (flowObfuscationGroup.getPackageField(string2) == null) {
                                OpaquePredicateField opaquePredicateField2 = this.createPredicateField(
                                        (ProgramClass) classFileBase3, inheritedMemberAnalyzer, classMemberLookup1, this.random
                                );
                                this.packageFieldsByPackage.addValue(string2, opaquePredicateField2);
                                twoKeyMap.putValue(classFileBase3, opaquePredicateField2.getFieldName(), opaquePredicateField2);
                                flowObfuscationGroup.putPackageField(string2, opaquePredicateField2);
                            }
                        }
                    }
                }
            }
        }

        iterator = this.groups.iterator();

        while (iterator.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup1 = (FlowObfuscationGroup) iterator.next();
            OpaquePredicateField opaquePredicateField3 = flowObfuscationGroup1.getGroupField();
            Iterator iterator2 = flowObfuscationGroup1.getPackageFields().iterator();

            while (iterator2.hasNext()) {
                OpaquePredicateField opaquePredicateField5 = (OpaquePredicateField) iterator2.next();
                this.groupFieldByPackageField.put(opaquePredicateField5, opaquePredicateField3);
            }
        }

        int bg = 0;
        int bh = 0;

        for (ProgramClass[] programClass2 = this.programClasses; bh < programClass2.length; programClass2 = this.programClasses) {
            ProgramClass programClass1 = this.programClasses[bg];
            FlowObfuscationGroup flowObfuscationGroup2 = (FlowObfuscationGroup) this.groupsByClass.get(programClass1);
            if (flowObfuscationGroup2 != null) {
                OpaquePredicateField opaquePredicateField4 = flowObfuscationGroup2.getGroupField();
                if (opaquePredicateField4 != null && !this.fieldPairsByClass.containsKey(programClass1)) {
                    OpaquePredicateField opaquePredicateField6 = flowObfuscationGroup2.getPackageField(programClass1.getPackagePath());
                    this.fieldPairsByClass.put(programClass1, new FlowFieldPair(opaquePredicateField4, opaquePredicateField6, programClass1.getPackagePath()));
                }

                if (opaquePredicateField4 != null && programClass1.hasVersionedVariants()) {
                    FlowFieldPair flowFieldPair = (FlowFieldPair) this.fieldPairsByClass.get(programClass1);
                    Iterator iterator5 = programClass1.getVersionedVariants().iterator();

                    while (iterator5.hasNext()) {
                        ClassFileBase classFileBase2 = (ClassFileBase) iterator5.next();
                        this.fieldPairsByClass.put(classFileBase2, flowFieldPair);
                    }
                }
            }

            bh = ++bg;
        }

        return setMultiMap;
    }

    public MethodInfo createSetter(
            ProgramClass programClass1, FieldInfo fieldInfo, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer, ClassMemberLookup classMemberLookup1
    ) throws ZkmProcessingException {
        String string = OpaquePredicateField.setterDescriptorFor(fieldInfo.getDescriptor());
        LocalVariableList localVariableList1 = new LocalVariableList(true, string, 5);
        ArrayList arrayList = new ArrayList();
        this.emitSetterBody(arrayList, localVariableList1, programClass1, fieldInfo, list1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        MethodInfo methodInfo1 = programClass1.createUniquelyNamedPublicStaticMethod(
                string, arrayList, 1, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer, classMemberLookup1
        );
        this.generatedMethods.add(methodInfo1);
        return methodInfo1;
    }

    public List getGroupingNamesWithoutField() {
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        Iterator iterator = this.groups.iterator();

        while (iterator.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) iterator.next();
            List list1 = flowObfuscationGroup.getGroupingSetNames();
            hashSet.addAll(list1);
            if (flowObfuscationGroup.getGroupField() != null) {
                hashSet1.addAll(list1);
            }
        }

        hashSet.removeAll(hashSet1);
        ArrayList arrayList = new ArrayList(hashSet);
        Collections.sort(arrayList);
        return arrayList;
    }

    public boolean isGeneratedMethod(Object object) {
        return this.generatedMethods.contains(object);
    }

    public Map getGroupsByClass() {
        return this.groupsByClass;
    }

    public LibraryMethod createLibraryGetter(
            ClasspathClassFile classpathClassFile, String string, AbstractFieldInfo abstractFieldInfo, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer
    ) throws ZkmProcessingException {
        String string1 = OpaquePredicateField.getterDescriptorFor(abstractFieldInfo.getDescriptor());
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        this.emitGetterBody(arrayList, classpathClassFile, abstractFieldInfo, list1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        return classpathClassFile.createCodeMethod(string, string1, arrayList, 0, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer);
    }

    public boolean canHostPredicateField(
            ProgramClass programClass1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1,
            ClassMemberLookup classMemberLookup1,
            boolean bl
    ) throws ZkmException, IOException {
        String string = programClass1.getClassName();
        Set set1 = programClass1.getAnnotationTypes();
        return !programClass1.isInterface()
                && (programClass1.isPublic() || bl)
                && !programClass1.isMultiRelease()
                && (flowObfuscationExclusions == null || !flowObfuscationExclusions.isExcludedFromPredicateFields(programClass1))
                && (classInitOrderHandler1 == null || !classInitOrderHandler1.isLaterInitializedClass(programClass1))
                && !classMemberLookup1.isSubclass(string, "org/eclipse/osgi/util/NLS")
                && !classMemberLookup1.implementsInterface(string, "javax/ejb/EnterpriseBean")
                && (set1 == null || !set1.contains("javax/ejb/Stateful") && !set1.contains("javax/ejb/Stateless") && !set1.contains("javax/ejb/MessageDriven"));
    }

    public static NamedSet[] deriveGroupingSetsFromChangeLog(ChangeLogMapping changeLogMapping1, Map map1) {
        Map map2 = changeLogMapping1.getMemberClassEntries();
        SetMultiMap setMultiMap = new SetMultiMap(map2.size());
        Iterator iterator = map2.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = ZkmUtils.dotsToSlashes((String) entry.getKey());
            ObjectPair objectPair = (ObjectPair) entry.getValue();
            ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) objectPair.getFirst();
            setMultiMap.addValue(changeLogMemberEntry, string);
        }

        ArrayList arrayList1 = new ArrayList(setMultiMap.getKeyCount());
        Iterator iterator2 = setMultiMap.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry1 = (Entry) iterator2.next();
            ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) entry1.getKey();
            Set set1 = (Set) entry1.getValue();
            ArrayList arrayList = new ArrayList(set1.size());
            Iterator iterator1 = set1.iterator();

            while (iterator1.hasNext()) {
                String string1 = (String) iterator1.next();
                String string2 = (String) ZkmUtils.mapOrSelf(string1, map1);
                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string2);
                ZkmAssert.assertNotNullMessage(classFileBase, new String[]{string1, ", ", string2});
                if (!classFileBase.isInterface() || classFileBase.supportsJava8()) {
                    arrayList.add(classFileBase);
                }
            }

            if (arrayList.size() > 0) {
                NamedSet namedSet = ZkmUtils.createNamedSet(changeLogMemberEntry1.getInternalClassName() + "." + changeLogMemberEntry1.getFieldName(), arrayList);
                arrayList1.add(namedSet);
            }
        }

        return ((com.zelix.klassmaster.util.NamedSet[]) (arrayList1.toArray(new NamedSet[arrayList1.size()])));
    }

    public boolean restoreChangeLogFields(
            HashSet hashSet,
            CountingBag countingBag,
            NameExclusionSet nameExclusionSet,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1,
            Random random1,
            TwoKeyMap twoKeyMap,
            SetMultiMap setMultiMap,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            ClassMemberLookup classMemberLookup1
    ) throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        HashMap hashMap1 = ZkmUtils.createHashMap();
        HashMap hashMap2 = ZkmUtils.createHashMap(7);
        HashMap hashMap3 = ZkmUtils.createHashMap(7);
        TwoKeyMap twoKeyMap1 = new TwoKeyMap(7, 7);
        HashMap hashMap4 = ZkmUtils.createHashMap(7);
        TwoKeyMap twoKeyMap2 = new TwoKeyMap();
        HashMap hashMap5 = ZkmUtils.createHashMap();
        TwoKeyMap twoKeyMap3 = new TwoKeyMap();
        boolean bl = true;
        SetMultiMap setMultiMap1 = new SetMultiMap();
        Map map1 = this.changeLogMapping.getMemberClassEntries();
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            String string1 = ZkmUtils.dotsToSlashes(string);
            ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string1);
            if (classFileBase != null && (!classFileBase.isInterface() || classFileBase.supportsJava8())) {
                ObjectPair objectPair = (ObjectPair) entry.getValue();
                ChangeLogMemberEntry changeLogMemberEntry = (ChangeLogMemberEntry) objectPair.getFirst();
                ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) objectPair.getSecond();
                setMultiMap1.addValue(changeLogMemberEntry.getInternalClassName(), classFileBase);
                setMultiMap1.addValue(changeLogMemberEntry1.getInternalClassName(), classFileBase);
            }
        }

        Enumeration enumeration = this.changeLogMapping.getSortedTraceBackEntries();

        while (enumeration.hasMoreElements() && bl) {
            ChangeLogMemberEntry changeLogMemberEntry3 = (ChangeLogMemberEntry) enumeration.nextElement();
            OpaquePredicateField opaquePredicateField3 = this.resolveFieldForChangeLogEntry(
                    changeLogMemberEntry3,
                    setMultiMap1,
                    countingBag,
                    nameExclusionSet,
                    random1,
                    twoKeyMap,
                    hashMap2,
                    hashMap3,
                    twoKeyMap1,
                    hashMap4,
                    twoKeyMap2,
                    hashMap5,
                    twoKeyMap3,
                    true,
                    hashSet.size(),
                    classMemberLookup1,
                    hashMap1,
                    flowObfuscationExclusions,
                    classInitOrderHandler1,
                    inheritedMemberAnalyzer
            );
            if (opaquePredicateField3 != null) {
                ClassFileBase classFileBase2 = opaquePredicateField3.getOwnerClass();
                this.addFieldType((FlowObfuscationGroup) this.groupsByClass.get(classFileBase2), opaquePredicateField3.getFieldType());
                this.groupFields.add(opaquePredicateField3);
                setMultiMap.addValue(classFileBase2, opaquePredicateField3.getFieldSignature());
                hashMap.put(changeLogMemberEntry3, opaquePredicateField3);
            } else {
                bl = false;
            }
        }

        ListMultimap listMultimap = this.changeLogMapping.getSortedForwardClassEntries();
        Enumeration enumeration1 = listMultimap.keys();

        while (enumeration1.hasMoreElements() && bl) {
            String string2 = (String) enumeration1.nextElement();
            String string3 = ZkmUtils.dotsToSlashes(string2);
            if (hashSet.contains(string3)) {
                List list1 = listMultimap.getValues(string2);
                Iterator iterator3 = list1.iterator();

                while (iterator3.hasNext()) {
                    ChangeLogMemberEntry changeLogMemberEntry4 = (ChangeLogMemberEntry) iterator3.next();
                    OpaquePredicateField opaquePredicateField = this.resolveFieldForChangeLogEntry(
                            changeLogMemberEntry4,
                            setMultiMap1,
                            countingBag,
                            nameExclusionSet,
                            random1,
                            twoKeyMap,
                            hashMap2,
                            hashMap3,
                            twoKeyMap1,
                            hashMap4,
                            twoKeyMap2,
                            hashMap5,
                            twoKeyMap3,
                            false,
                            hashSet.size(),
                            classMemberLookup1,
                            hashMap1,
                            flowObfuscationExclusions,
                            classInitOrderHandler1,
                            inheritedMemberAnalyzer
                    );
                    if (opaquePredicateField != null) {
                        this.packageFieldsByPackage.addValue(string3, opaquePredicateField);
                        hashMap.put(changeLogMemberEntry4, opaquePredicateField);
                        ClassFileBase classFileBase1 = ClassHierarchyNode.findClassFile(opaquePredicateField.getOwnerClassName());
                        setMultiMap.addValue(classFileBase1, opaquePredicateField.getFieldSignature());
                        this.addFieldType((FlowObfuscationGroup) this.groupsByClass.get(classFileBase1), opaquePredicateField.getFieldType());
                    } else {
                        bl = false;
                    }
                }
            }
        }

        if (bl) {
            Iterator iterator1 = map1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                String string5 = (String) entry1.getKey();
                String string8 = ZkmUtils.dotsToSlashes(string5);
                ClassFileBase classFileBase3 = ClassHierarchyNode.findClassFile(string8);
                if (classFileBase3 != null) {
                    if (classFileBase3.isInterface() && !classFileBase3.supportsJava8()) {
                        this.changeLogMapping.logMessage("Class '" + string5 + "' has been changed to be an interface. No action required.");
                    } else {
                        ObjectPair objectPair2 = (ObjectPair) entry1.getValue();
                        ChangeLogMemberEntry changeLogMemberEntry2 = (ChangeLogMemberEntry) objectPair2.getFirst();
                        ChangeLogMemberEntry changeLogMemberEntry5 = (ChangeLogMemberEntry) objectPair2.getSecond();
                        OpaquePredicateField opaquePredicateField1 = (OpaquePredicateField) hashMap.get(changeLogMemberEntry2);
                        OpaquePredicateField opaquePredicateField2 = (OpaquePredicateField) hashMap.get(changeLogMemberEntry5);
                        if (opaquePredicateField1 == null || opaquePredicateField2 == null) {
                            bl = false;
                            this.changeLogMapping
                                    .logError("Class '" + string5 + "' affected by a change in class grouping (B). You must distribute this application as a whole.");
                        } else if (this.isFieldClassInAllGroupings(classFileBase3, opaquePredicateField1)
                                && this.isFieldClassInAllGroupings(classFileBase3, opaquePredicateField2)) {
                            this.fieldPairsByClass
                                    .put(classFileBase3, new FlowFieldPair(opaquePredicateField1, opaquePredicateField2, classFileBase3.getPackagePath()));
                        } else {
                            bl = false;
                            this.changeLogMapping
                                    .logError("Class '" + string5 + "' affected by a change in class grouping (A). You must distribute this application as a whole.");
                        }
                    }
                }
            }

            if (!bl) {
                this.fieldPairsByClass.clear();
            }
        }

        Iterator iterator2 = hashMap2.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry2 = (Entry) iterator2.next();
            String string6 = (String) entry2.getKey();
            String string9 = (String) entry2.getValue();
            this.changeLogMapping.renameMappedClass(string6, string9);
        }

        iterator2 = hashMap3.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry3 = (Entry) iterator2.next();
            Triple triple = (Triple) entry3.getKey();
            String string10 = (String) entry3.getValue();
            this.changeLogMapping.renameRawFieldMapping((String) triple.getFirst(), (String) triple.getSecond(), (String) triple.getThird(), string10);
        }

        Enumeration enumeration2 = twoKeyMap1.keys();

        while (enumeration2.hasMoreElements()) {
            String string4 = (String) enumeration2.nextElement();
            Map map2 = twoKeyMap1.getInnerMap(string4);
            Iterator iterator4 = map2.keySet().iterator();

            while (iterator4.hasNext()) {
                ObjectPair objectPair1 = (ObjectPair) iterator4.next();
                String string12 = (String) objectPair1.getFirst();
                String string13 = (String) objectPair1.getSecond();
                this.changeLogMapping.removeRawFieldMapping(string4, string12, string13);
            }
        }

        iterator2 = hashMap4.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry4 = (Entry) iterator2.next();
            Triple triple1 = (Triple) entry4.getKey();
            String string11 = (String) entry4.getValue();
            this.changeLogMapping.addRawFieldMapping((String) triple1.getFirst(), (String) triple1.getSecond(), (String) triple1.getThird(), string11);
        }

        iterator2 = twoKeyMap2.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry5 = (Entry) iterator2.next();
            Map map3 = (Map) entry5.getValue();
            Iterator iterator5 = map3.entrySet().iterator();

            while (iterator5.hasNext()) {
                Entry entry8 = (Entry) iterator5.next();
                Triple triple3 = (Triple) entry8.getKey();
                this.changeLogMapping
                        .renameRawMethodMapping(
                                (String) entry5.getKey(), (String) triple3.getFirst(), (String) triple3.getSecond(), (String) triple3.getThird(), (String) entry8.getValue()
                        );
            }
        }

        iterator2 = hashMap5.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry6 = (Entry) iterator2.next();
            Triple triple2 = (Triple) entry6.getValue();
            this.changeLogMapping
                    .removeRawMethodMapping((String) entry6.getKey(), (String) triple2.getFirst(), (String) triple2.getSecond(), (String) triple2.getThird());
        }

        iterator2 = twoKeyMap3.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry7 = (Entry) iterator2.next();
            String string7 = (String) entry7.getKey();
            Map map4 = (Map) entry7.getValue();
            Iterator iterator6 = map4.entrySet().iterator();

            while (iterator6.hasNext()) {
                Entry entry9 = (Entry) iterator6.next();
                Triple triple4 = (Triple) entry9.getKey();
                this.changeLogMapping
                        .addRawMethodMapping(string7, (String) triple4.getFirst(), (String) triple4.getSecond(), (String) triple4.getThird(), (String) entry9.getValue());
            }
        }

        return bl;
    }

    public List getGroupFieldsInUse() {
        HashSet hashSet = ZkmUtils.createHashSet();
        Iterator iterator = this.fieldPairsByClass.values().iterator();

        while (iterator.hasNext()) {
            FlowFieldPair flowFieldPair = (FlowFieldPair) iterator.next();
            hashSet.add(flowFieldPair.getGroupField());
        }

        return new ArrayList(hashSet);
    }

    public void assignFieldsFromCandidates(
            Iterator iterator,
            FlowObfuscationGroup flowObfuscationGroup,
            Random random1,
            TwoKeyMap twoKeyMap,
            SetMultiMap setMultiMap,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            ClassMemberLookup classMemberLookup1,
            int ba,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1,
            int bb
    ) throws ZkmException, IOException {
        while (iterator.hasNext() && !flowObfuscationGroup.hasAllFields()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            programClass1.getClassName();
            if ((bb != 1 || staticInitCalleeAnalyzer1.isInitDependencyLeaf(programClass1))
                    && !twoKeyMap.containsKey(programClass1)
                    && this.canHostPredicateField(programClass1, flowObfuscationExclusions, classInitOrderHandler1, classMemberLookup1, ba == 1 && bb != 1 && bb != 2)) {
                if (flowObfuscationGroup.getGroupField() == null) {
                    if (this.isAdvancedModeEnabled() && !HiddenOptionFlags.NO_STRING_OPAQUE_PREDICATES) {
                        this.addArrayFieldType(flowObfuscationGroup, programClass1);
                    }

                    OpaquePredicateField opaquePredicateField = this.createPredicateField(programClass1, inheritedMemberAnalyzer, classMemberLookup1, random1);
                    this.groupFields.add(opaquePredicateField);
                    setMultiMap.addValue(programClass1, opaquePredicateField.getFieldSignature());
                    OpaquePredicateField opaquePredicateField2 = (OpaquePredicateField) twoKeyMap.putValue(
                            programClass1, opaquePredicateField.getFieldName(), opaquePredicateField
                    );
                    flowObfuscationGroup.setGroupField(opaquePredicateField);
                } else {
                    boolean bl = false;
                    String string = null;
                    if (bb != 1 && bb != 2 && bb != 3) {
                        Iterator iterator1 = flowObfuscationGroup.getSortedPackageNames().iterator();

                        while (iterator1.hasNext()) {
                            string = (String) iterator1.next();
                            if (flowObfuscationGroup.getPackageField(string) == null) {
                                bl = true;
                                break;
                            }
                        }
                    } else {
                        string = programClass1.getPackagePath();
                        if (flowObfuscationGroup.getPackageField(string) != null) {
                            continue;
                        }

                        bl = true;
                    }

                    if (bl) {
                        OpaquePredicateField opaquePredicateField1 = this.createPredicateField(programClass1, inheritedMemberAnalyzer, classMemberLookup1, random1);
                        this.packageFieldsByPackage.addValue(string, opaquePredicateField1);
                        twoKeyMap.putValue(programClass1, opaquePredicateField1.getFieldName(), opaquePredicateField1);
                        setMultiMap.addValue(programClass1, opaquePredicateField1.getFieldSignature());
                        flowObfuscationGroup.putPackageField(string, opaquePredicateField1);
                    }
                }
            }
        }
    }

    public LibraryMethod createLibrarySetter(
            ClasspathClassFile classpathClassFile, String string, LibraryFieldInfo libraryFieldInfo, List list1, InheritedMemberAnalyzer inheritedMemberAnalyzer
    ) throws ZkmProcessingException {
        String string1 = OpaquePredicateField.setterDescriptorFor(libraryFieldInfo.getDescriptor());
        LocalVariableList localVariableList1 = new LocalVariableList(true, string1, 5);
        ArrayList arrayList = new ArrayList();
        this.emitSetterBody(arrayList, localVariableList1, classpathClassFile, libraryFieldInfo, list1);
        ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
        return classpathClassFile.createCodeMethod(string, string1, arrayList, 1, localVariableList1, exceptionHandlerSpecs, list1, inheritedMemberAnalyzer);
    }

    public boolean isSecondaryCallerClass(Object object) {
        return this.secondaryCallerClasses.contains(object);
    }

    public static ArrayList buildFlowGroups(
            ProgramClass[] programClass1,
            ClassFileBase[] classFileBases,
            Map map1,
            ScriptEnvironment scriptEnvironment1,
            GroupingsSpec groupingsSpec1,
            ChangeLogMapping changeLogMapping1,
            ClassHierarchyQuery classHierarchyQuery,
            boolean bl,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        HashMap hashMap = null;
        ArrayList arrayList = new ArrayList();
        if (!bl) {
            NamedSet[] namedSets = computeGroupingSets(programClass1, classFileBases, groupingsSpec1, changeLogMapping1, classHierarchyQuery, map1);
            hashMap = buildGroups(programClass1, classFileBases, namedSets, arrayList);
        } else {
            if (groupingsSpec1 != null) {
                scriptEnvironment1.logWarning(
                        "'deriveGroupingsFromInputChangeLog' specified but 'groupings' statement present. 'groupings' statement will be ignored."
                );
            }

            if (changeLogMapping1 != null) {
                if (changeLogMapping1.hasTraceBackEntries()) {
                    NamedSet[] namedSets1 = deriveGroupingSetsFromChangeLog(changeLogMapping1, map1);
                    hashMap = buildGroupsFromChangeLog(programClass1, classFileBases, changeLogMapping1, namedSets1, arrayList);
                } else {
                    scriptEnvironment1.logSeriousError_v(
                            "'deriveGroupingsFromInputChangeLog' specified but input change log '"
                                    + changeLogMapping1.getChangeLogName()
                                    + " has no flow obfuscation data."
                    );
                }
            } else {
                scriptEnvironment1.logSeriousError_v("'deriveGroupingsFromInputChangeLog' specified but there is no input change log.");
            }
        }

        observableHolder.setValue(hashMap);
        return arrayList;
    }

    public List getGroups() {
        return this.groups;
    }

    public boolean isFieldClassInAllGroupings(Object object, OpaquePredicateField opaquePredicateField) {
        ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(opaquePredicateField.getOwnerClassName());
        if (this.groupingSetsByClass == null) {
            this.groupingSetsByClass = new ListMultimap(this.classFiles.length);
            int ba = 0;
            int bb = 0;

            for (NamedSet[] namedSets = this.groupingSets; bb < namedSets.length; namedSets = this.groupingSets) {
                Iterator iterator = this.groupingSets[ba].iterator();

                while (iterator.hasNext()) {
                    this.groupingSetsByClass.addValue(iterator.next(), this.groupingSets[ba]);
                }

                bb = ++ba;
            }
        }

        List list1 = this.groupingSetsByClass.getValues(object);
        if (list1 != null) {
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                if (!((NamedSet) iterator1.next()).contains(classFileBase)) {
                    return false;
                }
            }
        }

        return true;
    }

    public Map createFieldTypeSets() {
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.groups.size()));
        SyncIndexedSet syncIndexedSet = new SyncIndexedSet();
        if (HiddenOptionFlags.OBJECT_OPAQUE_PREDICATE_TYPES) {
            syncIndexedSet.add("Ljava/lang/String;");
            syncIndexedSet.add("[I");
            syncIndexedSet.add("[Ljava/lang/String;");
        } else {
            syncIndexedSet.add("I");
            syncIndexedSet.add("Z");
            if (this.isAdvancedModeEnabled() && !HiddenOptionFlags.NO_STRING_OPAQUE_PREDICATES) {
                syncIndexedSet.add("Ljava/lang/String;");
                syncIndexedSet.add("[I");
                syncIndexedSet.add("[Ljava/lang/String;");
            }
        }

        Iterator iterator = this.groups.iterator();

        while (iterator.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) iterator.next();
            hashMap.put(flowObfuscationGroup, new SyncIndexedSet(syncIndexedSet));
        }

        return hashMap;
    }

    public ProgramClass findHostClassForChangeLogEntry(
            ChangeLogMemberEntry changeLogMemberEntry,
            CountingBag countingBag,
            Map map1,
            ChangeLogMapping changeLogMapping1,
            NameExclusionSet nameExclusionSet,
            ClassMemberLookup classMemberLookup1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1
    ) throws ZkmException, IOException {
        if (changeLogMemberEntry.hasNoField()) {
            return null;
        }

        String string = changeLogMemberEntry.getFieldName();
        String string1 = changeLogMemberEntry.getInternalClassName();
        String string2 = changeLogMemberEntry.getNewClassName();
        String string3 = changeLogMemberEntry.getFieldDescriptor();
        if (map1.containsKey(string1)) {
            ProgramClass programClass1 = (ProgramClass) map1.get(string1);
            String string4 = programClass1.getClassName();
            FieldInfo fieldInfo = classMemberLookup1.findField(string4, string, string3);
            MethodInfo methodInfo1 = changeLogMemberEntry.getSetterMethodName() != null
                    ? classMemberLookup1.findDeclaredMethod(
                    programClass1, new MethodSignature(changeLogMemberEntry.getNewSetterMethodName(), OpaquePredicateField.setterDescriptorFor(string3))
            )
                    : null;
            MethodInfo methodInfo2 = changeLogMemberEntry.getGetterMethodName() != null
                    ? classMemberLookup1.findDeclaredMethod(
                    programClass1, new MethodSignature(changeLogMemberEntry.getNewGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string3))
            )
                    : null;
            MethodInfo methodInfo3 = changeLogMemberEntry.getAltGetterMethodName() != null
                    ? classMemberLookup1.findDeclaredMethod(
                    programClass1, new MethodSignature(changeLogMemberEntry.getNewAltGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string3))
            )
                    : null;
            if ((fieldInfo == null || !nameExclusionSet.isFieldExcluded(fieldInfo))
                    && (methodInfo1 == null || !nameExclusionSet.isMethodExcluded(methodInfo1))
                    && (methodInfo2 == null || !nameExclusionSet.isMethodExcluded(methodInfo2))
                    && (methodInfo3 == null || !nameExclusionSet.isMethodExcluded(methodInfo3))) {
                return programClass1;
            }
        }

        if (changeLogMapping1.isNewClassName(string2) && !changeLogMapping1.getOriginalClassName(string2).equals(string1)) {
            return null;
        }

        CountingBag countingBag1 = this.findCandidateClasses(string1, countingBag, changeLogMapping1);
        if (countingBag1 != null) {
            String string5 = ClassFileBase.getPackagePath(string1);
            Iterator iterator = countingBag1.getSortedByCount().iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator.next();
                String string6 = programClass2.getClassName();
                if (programClass2.getPackagePath().equals(string5)
                        && !changeLogMapping1.hasClassMapping(string6)
                        && !nameExclusionSet.isClassNameExcluded(string6)
                        && this.canHostPredicateField(programClass2, flowObfuscationExclusions, classInitOrderHandler1, classMemberLookup1, false)) {
                    FieldInfo fieldInfo1 = classMemberLookup1.findField(string6, string, string3);
                    MethodInfo methodInfo4 = changeLogMemberEntry.getSetterMethodName() != null
                            ? classMemberLookup1.findDeclaredMethod(
                            programClass2, new MethodSignature(changeLogMemberEntry.getNewSetterMethodName(), OpaquePredicateField.setterDescriptorFor(string3))
                    )
                            : null;
                    MethodInfo methodInfo5 = changeLogMemberEntry.getGetterMethodName() != null
                            ? classMemberLookup1.findDeclaredMethod(
                            programClass2, new MethodSignature(changeLogMemberEntry.getNewGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string3))
                    )
                            : null;
                    MethodInfo methodInfo6 = changeLogMemberEntry.getAltGetterMethodName() != null
                            ? classMemberLookup1.findDeclaredMethod(
                            programClass2, new MethodSignature(changeLogMemberEntry.getNewAltGetterMethodName(), OpaquePredicateField.getterDescriptorFor(string3))
                    )
                            : null;
                    if ((fieldInfo1 == null || !nameExclusionSet.isFieldExcluded(fieldInfo1))
                            && (methodInfo4 == null || !nameExclusionSet.isMethodExcluded(methodInfo4))
                            && (methodInfo5 == null || !nameExclusionSet.isMethodExcluded(methodInfo5))
                            && (methodInfo6 == null || !nameExclusionSet.isMethodExcluded(methodInfo6))
                            && !map1.containsValue(programClass2)) {
                        map1.put(string1, programClass2);
                        return programClass2;
                    }
                }
            }
        }

        return null;
    }

    public Set getGeneratedMethods() {
        return ZkmUtils.createHashSetFrom(this.generatedMethods);
    }
}
