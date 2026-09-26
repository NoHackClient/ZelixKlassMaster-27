package com.zelix.klassmaster.proguard;

import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.util.ArrayList;
import java.util.List;

public class ProGuardConfigTreeState {
    public boolean nodeCreated;
    public List nodes = new ArrayList();
    public List marks = new ArrayList();
    public int sp = 0;
    public int mk = 0;

    public void closeNodeScope(ProGuardConfigNode proGuardConfigNode) {
        int ba = this.nodeArity();
        this.mk = (Integer) this.marks.remove(this.marks.size() - 1);

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                proGuardConfigNode.jjtOpen();
                this.pushNode(proGuardConfigNode);
                this.nodeCreated = true;
                return;
            }

            ProGuardConfigNode proGuardConfigNode1 = this.popNode();
            proGuardConfigNode1.jjtSetParent(proGuardConfigNode);
            proGuardConfigNode.jjtAddChild(proGuardConfigNode1, ba);
        }
    }

    public void clearNodeScope() {
        for (int i = this.sp; i > this.mk; i = this.sp) {
            this.popNode();
        }

        this.mk = (Integer) this.marks.remove(this.marks.size() - 1);
    }

    public ProGuardConfigNode popNode() {
        List list1;
        if (--this.sp < this.mk) {
            this.mk = (Integer) this.marks.remove(this.marks.size() - 1);
            list1 = this.nodes;
        } else {
            list1 = this.nodes;
        }

        return (ProGuardConfigNode) list1.remove(this.nodes.size() - 1);
    }

    public int nodeArity() {
        return this.sp - this.mk;
    }

    public void openNodeScope(ProGuardConfigNode proGuardConfigNode) {
        this.marks.add(this.mk);
        this.mk = this.sp;
        proGuardConfigNode.jjtClose();
    }

    public void pushNode(Object object) {
        this.nodes.add(object);
        this.sp++;
    }
}
