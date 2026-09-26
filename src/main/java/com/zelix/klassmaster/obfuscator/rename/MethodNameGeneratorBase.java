package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MemberSignatureBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterExclusions;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DisableableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.Quadruple;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class MethodNameGeneratorBase {
    public final MethodOverrideAnalyzer overrideAnalyzer;
    public final MethodParameterExclusions parameterExclusions;
    public final MethodRenamer methodRenamer;
    public final NameExclusionSet exclusionSet;
    public final boolean randomizeNames;
    public final ScriptEnvironment scriptEnvironment;
    private final HashMap nameCache;

    public abstract String nextCandidateName(ClassHierarchyNode classHierarchyNode, String string);

    public String generateUniqueName(
            ClassHierarchyNode classHierarchyNode,
            MethodSignature methodSignature1,
            String string,
            Map map1,
            boolean bl,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            boolean bl2
    ) throws ZkmException, IOException {
        Boolean boolean4 = bl2;
        Boolean boolean3 = true;
        Boolean boolean2 = bl1;
        Set set2 = set1;
        DisableableMap disableableMap3 = disableableMap1;
        DisableableMap disableableMap2 = disableableMap;
        Map map8 = map4;
        Map map7 = map3;
        Map map6 = map2;
        Boolean boolean1 = bl;
        Map map5 = map1;
        String string1 = string;
        MethodSignature methodSignature2 = methodSignature1;
        ClassHierarchyNode classHierarchyNode1 = classHierarchyNode;
        return this.generatePrefixedUniqueName(
                (String) null,
                classHierarchyNode1,
                methodSignature2,
                string1,
                map5,
                boolean1,
                map6,
                map7,
                map8,
                disableableMap2,
                disableableMap3,
                set2,
                boolean2,
                boolean3,
                boolean4
        );
    }

    public boolean hasParameterExclusion(MethodInfo methodInfo1) {
        if (this.parameterExclusions == null) {
            return false;
        } else {
            AbstractMethodInfo abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
            if (abstractMethodInfo != null) {
                return abstractMethodInfo.isProgramMember() ? this.parameterExclusions.isMethodExcluded((MethodInfo) abstractMethodInfo) : false;
            } else {
                return this.parameterExclusions.isMethodExcluded(methodInfo1);
            }
        }
    }

    public final String assignNewNameForNode(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            Map map1,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl,
            boolean bl1
    ) throws ZkmException, IOException {
        return this.assignNewName(
                classHierarchyNode, methodInfo1, map1, classHierarchyNode.isInterface(), map2, map3, map4, disableableMap, disableableMap1, set1, bl, bl1
        );
    }

    public final String assignNewName(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            Map map1,
            boolean bl,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            boolean bl2
    ) throws ZkmException, IOException {
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        String string = methodSignature1.getDescriptor();
        AbstractMethodInfo abstractMethodInfo = null;
        if (!methodInfo1.isStrictlyPrivate() && !methodInfo1.isStatic()) {
            abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
        }

        AbstractMethodInfo abstractMethodInfo1;
        NameExclusionSet nameExclusionSet;
        if (abstractMethodInfo != null) {
            abstractMethodInfo1 = abstractMethodInfo;
            nameExclusionSet = this.exclusionSet;
        } else {
            abstractMethodInfo1 = methodInfo1;
            nameExclusionSet = this.exclusionSet;
        }

        String string2 = nameExclusionSet.getLinkPrefix(abstractMethodInfo1);
        String string1;
        if (string2 != null && this.exclusionSet.isInLinkedGroup(abstractMethodInfo1)) {
            Set set2 = this.exclusionSet.getSamePrefixGroupMethods(abstractMethodInfo1);
            Set set3 = this.exclusionSet.getEquivalentMethods(abstractMethodInfo1);
            ObservableHolder observableHolder = new ObservableHolder();
            ObjectPair objectPair = this.methodRenamer.findAssignedNamePair(set2, observableHolder);
            ObservableHolder observableHolder1 = new ObservableHolder();
            ObjectPair objectPair1 = this.methodRenamer.findAssignedNamePair(set3, observableHolder1);
            if (objectPair == null && objectPair1 == null) {
                ArrayList arrayList = new ArrayList(set3.size());

                boolean bl3;
                do {
                    bl3 = true;
                    arrayList.clear();
                    string1 = this.generatePrefixedUniqueName(
                            string2, classHierarchyNode, methodSignature1, string, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, true, bl2
                    );
                    String string3 = string1.substring(string2.length());
                    Iterator iterator = set3.iterator();

                    while (iterator.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo2 = (AbstractMethodInfo) iterator.next();
                        if (abstractMethodInfo2.isProgramMember()) {
                            MethodInfo methodInfo2 = (MethodInfo) abstractMethodInfo2;
                            String string4 = this.exclusionSet.getLinkPrefix(methodInfo2);
                            MethodSignature methodSignature2 = methodInfo2.getSignature();
                            MethodSignature methodSignature3 = new MethodSignature(string4 + string3, methodInfo2.getDescriptor());
                            Boolean boolean4 = bl2;
                            Boolean boolean3 = false;
                            Boolean boolean2 = bl;
                            Boolean boolean1 = bl1;
                            if (!this.isNameAvailable(
                                    classHierarchyNode,
                                    methodSignature3,
                                    methodSignature2,
                                    map1,
                                    map2,
                                    map3,
                                    map4,
                                    disableableMap,
                                    disableableMap1,
                                    set1,
                                    (String) null,
                                    boolean1,
                                    boolean2,
                                    boolean3,
                                    boolean4
                            )) {
                                bl3 = false;
                                break;
                            }

                            arrayList.add(new Quadruple(classHierarchyNode, methodSignature3, methodSignature2, methodInfo2));
                        }
                    }
                } while (!bl3);

                set1.add(MethodSignature.toPropertyName(string1, string2));
                Iterator iterator1 = arrayList.iterator();

                while (iterator1.hasNext()) {
                    Quadruple quadruple = (Quadruple) iterator1.next();
                    ObservableHolder observableHolder2 = new ObservableHolder();
                    boolean bl4;
                    if (((ClassHierarchyNode) quadruple.getFirst()).isInterface()) {
                        bl4 = this.overrideAnalyzer
                                .tryReserveRename(
                                        (ClassHierarchyNode) quadruple.getFirst(),
                                        (MethodSignature) quadruple.getSecond(),
                                        (MethodSignature) quadruple.getThird(),
                                        observableHolder2
                                );
                    } else {
                        bl4 = this.overrideAnalyzer
                                .tryReserveMethodRename(
                                        (ClassHierarchyNode) quadruple.getFirst(),
                                        (MethodSignature) quadruple.getSecond(),
                                        (MethodSignature) quadruple.getThird(),
                                        (AbstractMethodInfo) quadruple.getFourth(),
                                        observableHolder2
                                );
                    }

                    if (!bl4) {
                        ZkmAssert.assertTrue(
                                false,
                                new String[]{
                                        "Unable to record '"
                                                + quadruple.getThird()
                                                + "'->'"
                                                + quadruple.getSecond()
                                                + "' in class '"
                                                + ((ClassHierarchyNode) quadruple.getFirst()).getJavaClassName()
                                                + "' : '"
                                                + ((MethodInfo) quadruple.getFourth()).buildDeclaration()
                                                + "' : '"
                                                + (observableHolder2.isValueNull() ? "null" : ((MemberSignatureBase) observableHolder2.getValue()).formatSignature())
                                                + "' : Related to '"
                                                + methodInfo1.toDisplayString()
                                                + "' in '"
                                                + methodInfo1.getInputPath()
                                                + "'"
                                }
                        );
                    }
                }
            } else if (objectPair != null) {
                string1 = (String) objectPair.getSecond();
            } else {
                string1 = string2 + ((String) objectPair1.getSecond()).substring(((String) observableHolder1.getValue()).length());
            }
        } else {
            string1 = this.generateUniqueName(
                    classHierarchyNode, methodSignature1, string, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl2
            );
        }

        if (bl2 && string1 != null && !string1.equals(abstractMethodInfo1.getSourceName())) {
            abstractMethodInfo1.setNameChanged(true);
            if (abstractMethodInfo1 != methodInfo1) {
                methodInfo1.setNameChanged(true);
            }
        }

        return string1;
    }

    public String generatePrefixedUniqueName(
            String string,
            ClassHierarchyNode classHierarchyNode,
            MethodSignature methodSignature1,
            String string1,
            Map map1,
            boolean bl,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            boolean bl2,
            boolean bl3
    ) throws ZkmException, IOException {
        String string3;
        if (bl1) {
            string3 = methodSignature1.getDescriptor();
        } else {
            string3 = methodSignature1.getParameterDescriptor();
        }

        String string2;
        MethodSignature methodSignature2;
        do {
            string2 = this.nextCandidateName(classHierarchyNode, string3);
            if (HiddenOptionFlags.MARK_RENAMED_LOCALS) {
                string2 = methodSignature1.getName() + '_' + string2;
            }

            if (string != null) {
                string2 = MethodSignature.toAccessorName(string, string2);
            }

            methodSignature2 = new MethodSignature(string2, string1);
        } while (
                !this.isNameAvailable(
                        classHierarchyNode, methodSignature2, methodSignature1, map1, map2, map3, map4, disableableMap, disableableMap1, set1, string, bl1, bl, bl2, bl3
                )
        );

        return string2;
    }

    public final String resolveNewMethodName(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            Map map1,
            boolean bl,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            boolean bl2
    ) throws ZkmException, IOException {
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        ObservableHolder observableHolder = null;
        if (classHierarchyNode.isInterface()) {
            observableHolder = this.overrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
        }

        String string;
        if (observableHolder != null) {
            string = this.overrideAnalyzer.getGroupNewName(observableHolder);
            if (string == null) {
                ClassFileBase classFileBase = this.overrideAnalyzer.getRootMethodClass(methodInfo1);
                if (classFileBase != null) {
                    String string1 = classFileBase.getClassName();
                    ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(string1);
                    if (classHierarchyNode1 == null || classHierarchyNode1.hasNoClassFile()) {
                        string = null;
                    } else if (!classHierarchyNode1.isProgramClass()) {
                        string = this.methodRenamer.getNewMethodName(string1, methodSignature1);
                    } else {
                        string = this.assignNewName(classHierarchyNode, methodInfo1, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl2);
                    }
                } else {
                    string = this.assignNewName(classHierarchyNode, methodInfo1, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl2);
                }

                if (string != null) {
                    this.overrideAnalyzer.setGroupNewName(observableHolder, string);
                }
            } else if (bl2 && !methodInfo1.getSourceName().equals(string)) {
                AbstractMethodInfo abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
                if (abstractMethodInfo != null && abstractMethodInfo.isNameChanged()) {
                    methodInfo1.setNameChanged(true);
                }
            }
        } else {
            ClassFileBase classFileBase1 = this.overrideAnalyzer.getRootMethodClass(methodInfo1);
            if (classFileBase1 != null) {
                ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.findNode(classFileBase1.getClassName());
                if (classHierarchyNode2 != null && !classHierarchyNode2.hasNoClassFile()) {
                    string = this.methodRenamer.getNewMethodName(classFileBase1.getClassName(), methodSignature1);
                    if (bl2 && !methodInfo1.getSourceName().equals(string)) {
                        AbstractMethodInfo abstractMethodInfo1 = this.overrideAnalyzer.findRootMethod(methodInfo1);
                        if (abstractMethodInfo1.isNameChanged()) {
                            methodInfo1.setNameChanged(true);
                        }
                    }
                } else {
                    string = null;
                }
            } else {
                string = this.assignNewName(classHierarchyNode, methodInfo1, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl2);
            }
        }

        return string;
    }

    public final boolean isNameAvailable(
            ClassHierarchyNode classHierarchyNode,
            MethodSignature methodSignature1,
            MethodSignature methodSignature2,
            Map map1,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            String string,
            boolean bl,
            boolean bl1,
            boolean bl2,
            boolean bl3
    ) throws ZkmException, IOException {
        MethodSignature methodSignature3 = null;
        if (bl3) {
            methodSignature3 = new MethodSignature(methodSignature1.getName(), MethodParameterExclusions.getPackedSignatureDescriptor(methodSignature1));
        }

        if (methodSignature1.getName().equals(methodSignature2.getName())) {
            return false;
        }

        if (!disableableMap.isDisabled() && disableableMap.containsKey(methodSignature1.getName())) {
            return false;
        }

        if (!disableableMap1.isDisabled() && disableableMap1.containsKey(methodSignature1.getName())) {
            return false;
        }

        if ((!bl || !map2.containsKey(methodSignature1)) && (bl || !map4.containsKey(methodSignature1.getNameTypeSignature()))) {
            if (!bl3 || (!bl || !map2.containsKey(methodSignature3)) && (bl || !map4.containsKey(methodSignature3.getNameTypeSignature()))) {
                if ((!bl || !map1.containsKey(methodSignature1)) && (bl || !map3.containsKey(methodSignature1.getNameTypeSignature()))) {
                    MethodOverrideAnalyzer methodOverrideAnalyzer;
                    if (bl3) {
                        if (bl && map1.containsKey(methodSignature3)) {
                            return false;
                        }

                        if (!bl) {
                            if (map3.containsKey(methodSignature3.getNameTypeSignature())) {
                                return false;
                            }

                            methodOverrideAnalyzer = this.overrideAnalyzer;
                        } else {
                            methodOverrideAnalyzer = this.overrideAnalyzer;
                        }
                    } else {
                        methodOverrideAnalyzer = this.overrideAnalyzer;
                    }

                    if (methodOverrideAnalyzer.isSignatureReserved(methodSignature1)) {
                        return false;
                    } else if (bl3 && this.overrideAnalyzer.isSignatureReserved(methodSignature3)) {
                        return false;
                    } else if (this.overrideAnalyzer.isReservedForOther(classHierarchyNode, methodSignature1, methodSignature2)) {
                        return false;
                    } else if (bl3 && this.overrideAnalyzer.isReservedForOther(classHierarchyNode, methodSignature3, methodSignature2)) {
                        return false;
                    } else if (bl1
                            && !this.overrideAnalyzer.tryReserveGroupRename(classHierarchyNode, methodSignature1, methodSignature2, bl2, new ObservableHolder())) {
                        return false;
                    } else {
                        return bl3
                                && bl1
                                && !this.overrideAnalyzer.tryReserveGroupRename(classHierarchyNode, methodSignature3, methodSignature2, bl2, new ObservableHolder())
                                ? false
                                : string == null || !set1.contains(MethodSignature.toPropertyName(methodSignature1.getName(), string));
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public abstract String assignMethodName(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            Map map1,
            ListMultimap listMultimap,
            boolean bl,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            boolean bl2
    ) throws ZkmException, IOException;

    public MethodNameGeneratorBase(
            MethodRenamer methodRenamer1, NameExclusionSet nameExclusionSet, MethodParameterExclusions methodParameterExclusions, boolean randomizeNames
    ) {
        this.overrideAnalyzer = methodRenamer1.getOverrideAnalyzer();
        this.parameterExclusions = methodParameterExclusions;
        this.methodRenamer = methodRenamer1;
        this.exclusionSet = nameExclusionSet;
        this.randomizeNames = randomizeNames;
        this.scriptEnvironment = methodRenamer1.getScriptEnvironment();
        this.nameCache = ZkmUtils.createHashMap();
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
