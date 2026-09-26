package com.zelix.klassmaster.proguard;

import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;

import java.util.ArrayList;
import java.util.List;

public class ProGuardMappingTreeState {
    public boolean nodeCreated;
    public List nodes = new ArrayList();
    public List marks = new ArrayList();
    public int sp = 0;
    public int mk = 0;

    public void reset() {
        this.nodes.clear();
        this.marks.clear();
        this.sp = 0;
        this.mk = 0;
    }

    public ProGuardMappingNode popNode() {
        List list1;
        if (--this.sp < this.mk) {
            this.mk = (Integer) this.marks.remove(this.marks.size() - 1);
            list1 = this.nodes;
        } else {
            list1 = this.nodes;
        }

        return (ProGuardMappingNode) list1.remove(this.nodes.size() - 1);
    }

    public int nodeArity() {
        return this.sp - this.mk;
    }

    public void openNodeScope(ProGuardMappingNode proGuardMappingNode) {
        this.marks.add(this.mk);
        this.mk = this.sp;
        proGuardMappingNode.jjtOpen();
    }

    public void pushNode(Object object) {
        this.nodes.add(object);
        this.sp++;
    }

    public void clearNodeScope() {
        for (int i = this.sp; i > this.mk; i = this.sp) {
            this.popNode();
        }

        this.mk = (Integer) this.marks.remove(this.marks.size() - 1);
    }

    public void closeNodeScope(ProGuardMappingNode proGuardMappingNode) {
        int ba = this.nodeArity();
        this.mk = (Integer) this.marks.remove(this.marks.size() - 1);

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                proGuardMappingNode.jjtClose();
                this.pushNode(proGuardMappingNode);
                this.nodeCreated = true;
                return;
            }

            ProGuardMappingNode proGuardMappingNode1 = this.popNode();
            proGuardMappingNode1.jjtSetParent(proGuardMappingNode);
            proGuardMappingNode.jjtAddChild(proGuardMappingNode1, ba);
        }
    }
}
