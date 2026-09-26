package com.zelix.klassmaster.classfile.hierarchy;

import java.util.ArrayList;

public abstract class HierarchyNodeVisitor {
    public abstract void visitNode(ClassHierarchyNode classHierarchyNode, int ba, ArrayList arrayList);
}
