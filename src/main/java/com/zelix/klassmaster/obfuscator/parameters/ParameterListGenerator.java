package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.MemberSignatureBase;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ParameterListGenerator {
    public final MethodOverrideAnalyzer overrideAnalyzer;
    public final MethodParameterChangeSet changeSet;
    public final boolean randomizeLayouts;
    public final Random random;

    public ChangedMethodDescriptor generateForStaticMethod(
            ClassHierarchyNode classHierarchyNode, MethodInfo methodInfo1, SetMultiMap setMultiMap, Map map1, Map map2, boolean bl, boolean bl1
    ) throws ZkmException, IOException {
        return this.generateDescriptor(classHierarchyNode, methodInfo1, setMultiMap, classHierarchyNode.isInterface(), map1, map2, bl, bl1);
    }

    public final ChangedMethodDescriptor generateDescriptor(
            ClassHierarchyNode classHierarchyNode, MethodInfo methodInfo1, SetMultiMap setMultiMap, boolean bl, Map map1, Map map2, boolean bl1, boolean bl2
    ) throws ZkmException, IOException {
        boolean bl3 = false;
        boolean bl4 = false;
        if (this.randomizeLayouts) {
            int ba = 1;
            if (HiddenOptionFlags.EXTRA_PARAMETER_ODDS != null) {
                try {
                    ba = Integer.parseInt(HiddenOptionFlags.EXTRA_PARAMETER_ODDS);
                } catch (Exception exception) {
                }
            }

            if (bl1 || HiddenOptionFlags.ALWAYS_ADD_EXTRA_PARAMETERS || this.random.nextInt(ba) == 0) {
                bl3 = true;
            }

            int bb;
            Random random1;
            bb = 4;
            label62:
            if (HiddenOptionFlags.EXTRA_PARAMETER_COUNT != null) {
                try {
                    bb = Integer.parseInt(HiddenOptionFlags.EXTRA_PARAMETER_COUNT);
                } catch (Exception exception1) {
                    random1 = this.random;
                    break label62;
                }

                random1 = this.random;
            } else {
                label77:
                {
                    byte bd;
                    if (!bl1) {
                        if (!HiddenOptionFlags.ALWAYS_ADD_EXTRA_PARAMETERS) {
                            random1 = this.random;
                            break label77;
                        }

                        bd = 25;
                    } else {
                        bd = 25;
                    }

                    bb = bd;
                    random1 = this.random;
                }
            }

            if (random1.nextInt(bb) == 0) {
                bl4 = true;
            }
        }

        char[][] bc;
        if (bl4) {
            bc = new char[ExtraParamLayoutGenerator.PARAM_TYPE_LAYOUTS.length][];
            System.arraycopy(ExtraParamLayoutGenerator.PARAM_TYPE_LAYOUTS, 0, bc, 0, ExtraParamLayoutGenerator.PARAM_TYPE_LAYOUTS.length);
            ZkmUtils.shuffleArray(bc, ZkmUtils.createRandom(131));
        } else {
            bc = ExtraParamLayoutGenerator.PARAM_TYPE_LAYOUTS;
        }

        MethodSignature methodSignature1 = methodInfo1.getSignature();
        boolean varargs = methodInfo1.isVarargs();
        if (varargs) {
            List list1 = MethodSignature.splitParameterDescriptors(methodSignature1.getDescriptor());
            if (list1.size() == 0) {
                return null;
            }
        }

        ChangedMethodDescriptor changedMethodDescriptor;
        if (!bl3 && !HiddenOptionFlags.SKIP_PARAMETER_LIST_REUSE) {
            changedMethodDescriptor = this.findSequentialDescriptor(classHierarchyNode, methodInfo1, methodSignature1, varargs, setMultiMap, bl, map1, map2, bl2, bc);
        } else {
            changedMethodDescriptor = this.findShuffledDescriptor(classHierarchyNode, methodInfo1, methodSignature1, varargs, setMultiMap, bl, map1, map2, bl2, bc);
        }

        return changedMethodDescriptor;
    }

    public ChangedMethodDescriptor findShuffledDescriptor(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            MethodSignature methodSignature1,
            boolean bl,
            SetMultiMap setMultiMap,
            boolean bl1,
            Map map1,
            Map map2,
            boolean bl2,
            char[][] ba
    ) throws ZkmException, IOException {
        String string = methodSignature1.getName();
        HashSet hashSet = ZkmUtils.createHashSet();

        for (int i = 0; i < ba.length; i++) {
            char[] bc = ba[i];
            List list1 = listCandidateDescriptors(methodInfo1.hasLambdaArgCount(), methodSignature1, bl, bc);
            if (list1.size() > 1) {
                ZkmUtils.shuffleList(list1, this.random);
            }

            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ChangedMethodDescriptor changedMethodDescriptor = (ChangedMethodDescriptor) iterator.next();
                String string1 = changedMethodDescriptor.getDescriptor();
                if (hashSet.add(string1)) {
                    MethodSignature methodSignature2 = new MethodSignature(string, string1);
                    if (this.isDescriptorUsable(
                            classHierarchyNode, methodInfo1, changedMethodDescriptor, methodSignature2, methodSignature1, setMultiMap, map1, map2, bl2, bl1
                    )) {
                        return changedMethodDescriptor;
                    }
                }
            }
        }

        return null;
    }

    public final boolean isDescriptorUsable(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            ChangedMethodDescriptor changedMethodDescriptor,
            MethodSignature methodSignature1,
            MethodSignature methodSignature2,
            SetMultiMap setMultiMap,
            Map map1,
            Map map2,
            boolean bl,
            boolean bl1
    ) throws ZkmException, IOException {
        FieldNameTypeSignature fieldNameTypeSignature = methodSignature1.getNameTypeSignature();
        MemberSignatureBase memberSignatureBase = bl ? methodSignature1 : fieldNameTypeSignature;
        if (methodSignature1.getDescriptor().equals(methodSignature2.getDescriptor())) {
            return false;
        }

        if ((!bl || !map1.containsKey(methodSignature1)) && (bl || !map2.containsKey(methodSignature1.getNameTypeSignature()))) {
            if (!methodSignature2.isConstructor() && setMultiMap.containsKey(memberSignatureBase)) {
                return false;
            } else if (this.overrideAnalyzer.isSignatureReserved(methodSignature1)) {
                return false;
            } else if (this.overrideAnalyzer.isReservedForOther(classHierarchyNode, methodSignature1, methodSignature2)) {
                return false;
            } else {
                return bl1 && !this.overrideAnalyzer.tryReserveGroupRename(classHierarchyNode, methodSignature1, methodSignature2, true, new ObservableHolder())
                        ? false
                        : this.isAddedParameterPlacementValid(methodInfo1, changedMethodDescriptor);
            }
        } else {
            return false;
        }
    }

    public ChangedMethodDescriptor generateForPrivateMethod(
            ClassHierarchyNode classHierarchyNode, MethodInfo methodInfo1, SetMultiMap setMultiMap, Map map1, Map map2, boolean bl, boolean bl1
    ) throws ZkmException, IOException {
        return this.generateDescriptor(classHierarchyNode, methodInfo1, setMultiMap, classHierarchyNode.isInterface(), map1, map2, bl, bl1);
    }

    public static List listCandidateDescriptors(boolean bl, MethodSignature methodSignature1, boolean bl1, char[] ba) {
        ArrayList arrayList = new ArrayList();
        String string = methodSignature1.getName();
        String string1 = methodSignature1.getDescriptor();
        String string2 = MethodSignature.getReturnPart(string1);
        List list1 = MethodSignature.splitParameterDescriptors(string1);
        ChangedMethodDescriptor changedMethodDescriptor = null;
        ExtraParamLayoutGenerator extraParamLayoutGenerator = new ExtraParamLayoutGenerator();
        HashSet hashSet = ZkmUtils.createHashSet();

        while (true) {
            changedMethodDescriptor = extraParamLayoutGenerator.nextLayout(bl, list1, string2, ba, changedMethodDescriptor, bl1);
            if (changedMethodDescriptor == null) {
                return arrayList;
            }

            String string3 = changedMethodDescriptor.getDescriptor();
            if (hashSet.add(string3)) {
                new MethodSignature(string, string3);
                arrayList.add(changedMethodDescriptor);
            }
        }
    }

    public ParameterListGenerator(MethodParameterChangeSet methodParameterChangeSet, boolean randomizeLayouts, Random random1) {
        this.overrideAnalyzer = methodParameterChangeSet.getOverrideAnalyzer();
        this.changeSet = methodParameterChangeSet;
        this.randomizeLayouts = randomizeLayouts;
        this.random = random1;
    }

    public ChangedMethodDescriptor generateForVirtualMethod(
            ClassHierarchyNode classHierarchyNode, MethodInfo methodInfo1, SetMultiMap setMultiMap, boolean bl, Map map1, Map map2, boolean bl1, boolean bl2
    ) throws ZkmException, IOException {
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        ObservableHolder observableHolder = null;
        if (classHierarchyNode.isInterface()) {
            observableHolder = this.overrideAnalyzer.getInterfaceMethodGroup(classHierarchyNode, methodSignature1);
        }

        ChangedMethodDescriptor changedMethodDescriptor;
        if (observableHolder != null) {
            changedMethodDescriptor = this.overrideAnalyzer.getGroupChangedDescriptor(observableHolder);
            if (changedMethodDescriptor == null) {
                AbstractMethodInfo abstractMethodInfo = this.overrideAnalyzer.findRootMethod(methodInfo1);
                if (abstractMethodInfo != null) {
                    String string = abstractMethodInfo.getClassName();
                    ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(string);
                    if (classHierarchyNode1 == null || classHierarchyNode1.hasNoClassFile()) {
                        changedMethodDescriptor = null;
                    } else if (!classHierarchyNode1.isProgramClass()) {
                        changedMethodDescriptor = this.changeSet.getChangedDescriptor(abstractMethodInfo);
                    } else {
                        changedMethodDescriptor = this.generateDescriptor(classHierarchyNode, methodInfo1, setMultiMap, bl, map1, map2, bl1, bl2);
                    }
                } else {
                    changedMethodDescriptor = this.generateDescriptor(classHierarchyNode, methodInfo1, setMultiMap, bl, map1, map2, bl1, bl2);
                }

                if (changedMethodDescriptor != null) {
                    this.overrideAnalyzer.setGroupChangedDescriptor(observableHolder, changedMethodDescriptor);
                }
            }
        } else {
            AbstractMethodInfo abstractMethodInfo1 = this.overrideAnalyzer.findRootMethod(methodInfo1);
            if (abstractMethodInfo1 != null) {
                ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.findNode(abstractMethodInfo1.getClassName());
                if (classHierarchyNode2 != null && !classHierarchyNode2.hasNoClassFile()) {
                    changedMethodDescriptor = this.changeSet.getChangedDescriptor(abstractMethodInfo1);
                } else {
                    changedMethodDescriptor = null;
                }
            } else {
                changedMethodDescriptor = this.generateDescriptor(classHierarchyNode, methodInfo1, setMultiMap, bl, map1, map2, bl1, bl2);
            }
        }

        return changedMethodDescriptor;
    }

    public ChangedMethodDescriptor findSequentialDescriptor(
            ClassHierarchyNode classHierarchyNode,
            MethodInfo methodInfo1,
            MethodSignature methodSignature1,
            boolean bl,
            SetMultiMap setMultiMap,
            boolean bl1,
            Map map1,
            Map map2,
            boolean bl2,
            char[][] ba
    ) throws ZkmException, IOException {
        String string = methodSignature1.getName();
        String string1 = methodSignature1.getDescriptor();
        String string2 = MethodSignature.getReturnPart(string1);
        List list1 = MethodSignature.splitParameterDescriptors(string1);
        ChangedMethodDescriptor changedMethodDescriptor = null;
        MutableInt mutableInt = new MutableInt();
        ExtraParamLayoutGenerator extraParamLayoutGenerator = new ExtraParamLayoutGenerator();
        HashSet hashSet = ZkmUtils.createHashSet();

        while (true) {
            changedMethodDescriptor = extraParamLayoutGenerator.nextLayoutWithTypes(
                    methodInfo1.hasLambdaArgCount(), list1, string2, mutableInt, changedMethodDescriptor, bl, ba
            );
            if (changedMethodDescriptor == null) {
                return null;
            }

            String string3 = changedMethodDescriptor.getDescriptor();
            if (hashSet.add(string3)) {
                MethodSignature methodSignature2 = new MethodSignature(string, string3);
                if (this.isDescriptorUsable(
                        classHierarchyNode, methodInfo1, changedMethodDescriptor, methodSignature2, methodSignature1, setMultiMap, map1, map2, bl2, bl1
                )) {
                    return changedMethodDescriptor;
                }
            }
        }
    }

    public boolean isAddedParameterPlacementValid(MethodInfo methodInfo1, ChangedMethodDescriptor changedMethodDescriptor) {
        if (!methodInfo1.hasLambdaArgCount()) {
            return true;
        }

        int lambdaArgCount = methodInfo1.getLambdaArgCount();
        if (lambdaArgCount > 0) {
            AddedParameter[] addedParameters1 = changedMethodDescriptor.getAddedParameters();
            AddedParameter addedParameter = addedParameters1[addedParameters1.length - 1];
            List list1 = MethodSignature.splitParameterDescriptors(methodInfo1.getDescriptor());
            if (addedParameter.getIndex() >= list1.size() + addedParameters1.length - lambdaArgCount) {
                return false;
            }
        }

        return true;
    }
}
