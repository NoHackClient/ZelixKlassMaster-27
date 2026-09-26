package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.util.HashMap;
import java.util.Map;

public abstract class FieldNameAssigner {
    public ClassHierarchyNode currentClassNode;
    public int keepInnerClassInfoMode;
    public boolean randomizeNames;

    public FieldNameAssigner(int keepInnerClassInfoMode, boolean randomizeNames) {
        this.keepInnerClassInfoMode = keepInnerClassInfoMode;
        this.randomizeNames = randomizeNames;
    }

    public abstract String assignFieldName(
            ClassHierarchyNode classHierarchyNode,
            FieldInfo fieldInfo,
            String string,
            String string1,
            Map map1,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            Map map2,
            boolean bl,
            Map map3,
            HashMap hashMap,
            SetMultiMap setMultiMap,
            TwoKeyMap twoKeyMap
    );
}
