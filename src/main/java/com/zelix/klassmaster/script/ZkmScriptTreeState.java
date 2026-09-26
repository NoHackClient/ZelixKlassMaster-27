package com.zelix.klassmaster.script;

import com.zelix.klassmaster.script.parser.ZkmScriptNode;

import java.util.ArrayList;
import java.util.List;

public class ZkmScriptTreeState {
    public boolean nodeCreated;
    public List nodes = new ArrayList();
    public List marks = new ArrayList();
    public int stackSize = 0;
    public int currentMark = 0;

    public void closeNodeScope(ZkmScriptNode zkmScriptNode) {
        int ba = this.nodeArity();
        this.currentMark = (Integer) this.marks.remove(this.marks.size() - 1);

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                zkmScriptNode.jjtOpen();
                this.pushNode(zkmScriptNode);
                this.nodeCreated = true;
                return;
            }

            ZkmScriptNode zkmScriptNode1 = this.popNode();
            zkmScriptNode1.jjtSetParent(zkmScriptNode);
            zkmScriptNode.jjtAddChild(zkmScriptNode1, ba);
        }
    }

    public int nodeArity() {
        return this.stackSize - this.currentMark;
    }

    public void pushNode(Object object) {
        this.nodes.add(object);
        this.stackSize++;
    }

    public ZkmScriptNode popNode() {
        List list1;
        if (--this.stackSize < this.currentMark) {
            this.currentMark = (Integer) this.marks.remove(this.marks.size() - 1);
            list1 = this.nodes;
        } else {
            list1 = this.nodes;
        }

        return (ZkmScriptNode) list1.remove(this.nodes.size() - 1);
    }

    public void clearNodeScope() {
        for (int i = this.stackSize; i > this.currentMark; i = this.stackSize) {
            this.popNode();
        }

        this.currentMark = (Integer) this.marks.remove(this.marks.size() - 1);
    }

    public void openNodeScope(ZkmScriptNode zkmScriptNode) {
        this.marks.add(this.currentMark);
        this.currentMark = this.stackSize;
        zkmScriptNode.jjtClose();
    }
}
