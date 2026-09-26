package com.zelix.klassmaster.classfile.hierarchy;

import java.util.ArrayList;

public class ClassListCollector extends HierarchyNodeVisitor {
    @Override
    public void visitNode(ClassHierarchyNode classHierarchyNode, int ba, ArrayList arrayList) {
        classHierarchyNode.setDepth(ba);
        arrayList.add(classHierarchyNode);
    }
}
