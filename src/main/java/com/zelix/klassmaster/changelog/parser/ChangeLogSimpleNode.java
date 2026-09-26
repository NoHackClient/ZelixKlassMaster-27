package com.zelix.klassmaster.changelog.parser;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public abstract class ChangeLogSimpleNode implements ChangeLogNode {
    private static String interpretErrorPrefix = "Node class needed for or interpret() not defined for ";
    private static String[] opaqueStrings = null;
    public ChangeLogNode[] children;
    public ChangeLogNode parent;
    public int id;

    @Override
    public void jjtOpen() {
    }

    public static String[] getOpaqueStrings() {
        return opaqueStrings;
    }

    @Override
    public void dump() {
        if (this.children != null) {
            for (int i = 0; i < this.children.length; i++) {
                this.children[i].dump();
                this.children[i] = null;
            }

            this.children = null;
        }
    }

    @Override
    public void jjtAddChild(ChangeLogNode changeLogNode, int ba) {
        if (this.children == null) {
            this.children = new ChangeLogNode[ba + 1];
        } else if (ba >= this.children.length) {
            ChangeLogNode[] changeLogNodes = new ChangeLogNode[ba + 1];
            System.arraycopy(this.children, 0, changeLogNodes, 0, this.children.length);
            this.children = changeLogNodes;
        }

        this.children[ba] = changeLogNode;
    }

    public ChangeLogSimpleNode(int id) {
        this.id = id;
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public int jjtGetNumChildren() {
        return this.children == null ? 0 : this.children.length;
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        throw new Error(interpretErrorPrefix + this.getNodeName());
    }

    public String getNodeName() {
        return ChangeLogTreeConstants.NODE_NAMES[this.id];
    }

    @Override
    public ChangeLogNode jjtGetChild(int ba) {
        return this.children[ba];
    }

    @Override
    public void jjtSetParent(ChangeLogNode changeLogNode) {
        this.parent = changeLogNode;
    }
}
