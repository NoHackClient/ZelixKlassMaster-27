package com.zelix.klassmaster.proguard.mapping.parser;

import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;

public abstract class ProGuardMappingSimpleNode implements ProGuardMappingNode {
    public ProGuardMappingNode parent;
    public ProGuardMappingNode[] children;
    private static int[] flowState;
    public int id;

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).translate(this, proGuardMappingTranslator);
        }
    }

    @Override
    public ProGuardMappingNode jjtGetChild(int ba) {
        return this.children[ba];
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtSetParent(ProGuardMappingNode proGuardMappingNode) {
        this.parent = proGuardMappingNode;
    }

    @Override
    public void jjtAddChild(ProGuardMappingNode proGuardMappingNode, int ba) {
        ProGuardMappingNode[] proGuardMappingNodes1;
        if (this.children == null) {
            this.children = new ProGuardMappingNode[ba + 1];
            proGuardMappingNodes1 = this.children;
        } else if (ba >= this.children.length) {
            ProGuardMappingNode[] proGuardMappingNodes = new ProGuardMappingNode[ba + 1];
            System.arraycopy(this.children, 0, proGuardMappingNodes, 0, this.children.length);
            this.children = proGuardMappingNodes;
            proGuardMappingNodes1 = this.children;
        } else {
            proGuardMappingNodes1 = this.children;
        }

        proGuardMappingNodes1[ba] = proGuardMappingNode;
    }

    @Override
    public String toString() {
        return ProGuardMappingTreeConstants.jjtNodeName[this.id];
    }

    public static void setFlowState(int[] ba) {
        flowState = ba;
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public int jjtGetNumChildren() {
        return this.children == null ? 0 : this.children.length;
    }

    public static int[] getFlowState() {
        return flowState;
    }

    public ProGuardMappingSimpleNode(int id) {
        this.id = id;
    }

    static {
        if (getFlowState() != null) {
            setFlowState(new int[3]);
        }
    }
}
