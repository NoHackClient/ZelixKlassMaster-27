package com.zelix.klassmaster.classfile.insn;

import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionArgumentKind;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.HashSet;
import java.util.Map;

public class ValueTraceFrame {
    public final MethodFlowAnalyzer analyzer;
    public final CountingBag visitCounts;
    public BooleanFlag completeFlag;
    public HashSet results;
    public final boolean isArrayArgument;
    public final ReflectionArgumentKind argumentKind;
    public final NestedMultiMap fieldLoadSites;
    public final NestedMultiMap fieldStoreSites;
    public final NestedMultiMap methodCallSites;
    public final CommonSuperTypeResolver superTypeResolver;
    public final ClassMemberLookup memberLookup;
    public final Map pendingReflectionCalls;
    public final HashSet activeMethods;
    public final boolean isStaticMethod;
    public final Map returnedNameSourceCache;
    public final Map fieldValueCache;
    public final Map stackSourceCache;
    public final Map localSourceCache;
    public int nestingLevel;
    public boolean tracingPropertiesLoad;
    public Map flowAnalyzerCache;

    public ValueTraceFrame(
            MethodFlowAnalyzer methodFlowAnalyzer, ValueTraceFrame valueTraceFrame1, ReflectionArgumentKind reflectionArgumentKind, int nestingLevel, boolean tracingPropertiesLoad
    ) {
        this.analyzer = methodFlowAnalyzer;
        this.visitCounts = new CountingBag();
        this.completeFlag = new BooleanFlag(true);
        this.results = valueTraceFrame1.results;
        this.isArrayArgument = valueTraceFrame1.isArrayArgument;
        this.argumentKind = reflectionArgumentKind;
        this.fieldLoadSites = valueTraceFrame1.fieldLoadSites;
        this.fieldStoreSites = valueTraceFrame1.fieldStoreSites;
        this.methodCallSites = valueTraceFrame1.methodCallSites;
        this.superTypeResolver = valueTraceFrame1.superTypeResolver;
        this.memberLookup = valueTraceFrame1.memberLookup;
        this.pendingReflectionCalls = valueTraceFrame1.pendingReflectionCalls;
        this.activeMethods = ZkmUtils.copyHashSet(valueTraceFrame1.activeMethods);
        this.isStaticMethod = valueTraceFrame1.isStaticMethod;
        this.returnedNameSourceCache = valueTraceFrame1.returnedNameSourceCache;
        this.fieldValueCache = valueTraceFrame1.fieldValueCache;
        this.stackSourceCache = valueTraceFrame1.stackSourceCache;
        this.localSourceCache = valueTraceFrame1.localSourceCache;
        this.nestingLevel = nestingLevel;
        this.tracingPropertiesLoad = tracingPropertiesLoad;
        this.flowAnalyzerCache = valueTraceFrame1.flowAnalyzerCache;
        this.completeFlag = valueTraceFrame1.completeFlag;
    }

    public ValueTraceFrame(MethodFlowAnalyzer methodFlowAnalyzer, ValueTraceFrame valueTraceFrame1, boolean isStaticMethod) {
        this.analyzer = methodFlowAnalyzer;
        this.visitCounts = new CountingBag();
        this.completeFlag = new BooleanFlag(true);
        this.results = valueTraceFrame1.results;
        this.isArrayArgument = valueTraceFrame1.isArrayArgument;
        this.argumentKind = valueTraceFrame1.argumentKind;
        this.fieldLoadSites = valueTraceFrame1.fieldLoadSites;
        this.fieldStoreSites = valueTraceFrame1.fieldStoreSites;
        this.methodCallSites = valueTraceFrame1.methodCallSites;
        this.superTypeResolver = valueTraceFrame1.superTypeResolver;
        this.memberLookup = valueTraceFrame1.memberLookup;
        this.pendingReflectionCalls = valueTraceFrame1.pendingReflectionCalls;
        this.activeMethods = ZkmUtils.copyHashSet(valueTraceFrame1.activeMethods);
        this.isStaticMethod = isStaticMethod;
        this.returnedNameSourceCache = valueTraceFrame1.returnedNameSourceCache;
        this.fieldValueCache = valueTraceFrame1.fieldValueCache;
        this.stackSourceCache = valueTraceFrame1.stackSourceCache;
        this.localSourceCache = valueTraceFrame1.localSourceCache;
        this.nestingLevel = valueTraceFrame1.nestingLevel + 1;
        this.flowAnalyzerCache = valueTraceFrame1.flowAnalyzerCache;
        this.completeFlag = valueTraceFrame1.completeFlag;
    }

    public int getNestingLevel() {
        return this.nestingLevel;
    }

    public ValueTraceFrame(
            MethodFlowAnalyzer methodFlowAnalyzer,
            HashSet hashSet,
            boolean bl,
            ReflectionArgumentKind reflectionArgumentKind,
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            Map map1,
            boolean bl1,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            Map map6
    ) {
        this(
                methodFlowAnalyzer,
                hashSet,
                bl,
                reflectionArgumentKind,
                nestedMultiMap,
                nestedMultiMap1,
                nestedMultiMap2,
                commonSuperTypeResolver1,
                classMemberLookup1,
                map1,
                bl1,
                map2,
                map3,
                map4,
                map5,
                0,
                map6
        );
    }

    public void setComplete() {
        this.completeFlag.setValue(false);
    }

    public ValueTraceFrame(
            MethodFlowAnalyzer methodFlowAnalyzer,
            HashSet hashSet,
            boolean isArrayArgument,
            ReflectionArgumentKind reflectionArgumentKind,
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            Map map1,
            boolean isStaticMethod,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            int nestingLevel,
            Map map6
    ) {
        this.analyzer = methodFlowAnalyzer;
        this.visitCounts = new CountingBag();
        this.completeFlag = new BooleanFlag(true);
        this.results = hashSet;
        this.isArrayArgument = isArrayArgument;
        this.argumentKind = reflectionArgumentKind;
        this.fieldLoadSites = nestedMultiMap;
        this.fieldStoreSites = nestedMultiMap1;
        this.methodCallSites = nestedMultiMap2;
        this.superTypeResolver = commonSuperTypeResolver1;
        this.memberLookup = classMemberLookup1;
        this.pendingReflectionCalls = map1;
        this.activeMethods = ZkmUtils.createHashSet();
        this.isStaticMethod = isStaticMethod;
        this.returnedNameSourceCache = map2;
        this.fieldValueCache = map3;
        this.stackSourceCache = map4;
        this.localSourceCache = map5;
        this.nestingLevel = nestingLevel;
        this.flowAnalyzerCache = map6;
    }

    public boolean exitMethod(Object object) {
        return this.activeMethods.remove(object);
    }

    public boolean isComplete() {
        return this.completeFlag.getValue();
    }

    public ValueTraceFrame(MethodFlowAnalyzer methodFlowAnalyzer, ValueTraceFrame valueTraceFrame1, ReflectionArgumentKind reflectionArgumentKind) {
        this(methodFlowAnalyzer, valueTraceFrame1, reflectionArgumentKind, valueTraceFrame1.nestingLevel + 1);
    }

    public ValueTraceFrame(MethodFlowAnalyzer methodFlowAnalyzer, ValueTraceFrame valueTraceFrame1, ReflectionArgumentKind reflectionArgumentKind, int ba) {
        this(methodFlowAnalyzer, valueTraceFrame1, reflectionArgumentKind, ba, false);
    }

    public boolean enterMethod(Object object) {
        return this.activeMethods.add(object);
    }

    public HashSet swapResults(HashSet hashSet) {
        HashSet hashSet1 = this.results;
        this.results = hashSet;
        return hashSet1;
    }
}
