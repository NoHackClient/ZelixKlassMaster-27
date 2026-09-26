package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.changelog.parser.ChangeLogNode;

import java.util.ArrayList;
import java.util.List;

public class JJTChangeLogParserState {
    private boolean nodeCreated;
    private List nodes = new ArrayList();
    private List marks = new ArrayList();
    private int sp = 0;
    private int mk = 0;

    public void closeNodeScope(ChangeLogNode changeLogNode) {
        int ba = this.nodeArity();
        this.mk = (Integer) this.marks.remove(this.marks.size() - 1);

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                changeLogNode.jjtOpen();
                this.pushNode(changeLogNode);
                this.nodeCreated = true;
                return;
            }

            ChangeLogNode changeLogNode1 = this.popNode();
            changeLogNode1.jjtSetParent(changeLogNode);
            changeLogNode.jjtAddChild(changeLogNode1, ba);
        }
    }

    public void pushNode(ChangeLogNode changeLogNode) {
        this.nodes.add(changeLogNode);
        this.sp++;
    }

    public ChangeLogNode popNode() {
        if (--this.sp < this.mk) {
            this.mk = (Integer) this.marks.remove(this.marks.size() - 1);
        }

        return (ChangeLogNode) this.nodes.remove(this.nodes.size() - 1);
    }

    public int nodeArity() {
        return this.sp - this.mk;
    }

    public void clearNodeScope() {
        while (this.sp > this.mk) {
            this.popNode();
        }

        this.mk = (Integer) this.marks.remove(this.marks.size() - 1);
    }

    public void openNodeScope(ChangeLogNode changeLogNode) {
        this.marks.add(this.mk);
        this.mk = this.sp;
        changeLogNode.jjtClose();
    }

    public void reset() {
        this.nodes.clear();
        this.marks.clear();
        this.sp = 0;
        this.mk = 0;
    }
}
