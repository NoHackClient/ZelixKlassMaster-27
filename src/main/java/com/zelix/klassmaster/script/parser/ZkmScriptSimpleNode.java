package com.zelix.klassmaster.script.parser;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ast.SummarizingStatementNode;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;

public abstract class ZkmScriptSimpleNode implements ZkmScriptNode {
    private static int flowControlKey;
    public ZkmScriptNode parent;
    public ZkmScriptNode[] children;
    public int nodeTypeId;
    private static final String NO_INTERPRET_MESSAGE;

    public ZkmScriptSimpleNode(int nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
    }

    @Override
    public void jjtAddChild(ZkmScriptNode zkmScriptNode, int ba) {
        if (this.children == null) {
            this.children = new ZkmScriptNode[ba + 1];
        } else if (ba >= this.children.length) {
            ZkmScriptNode[] zkmScriptNodes = new ZkmScriptNode[ba + 1];
            System.arraycopy(this.children, 0, zkmScriptNodes, 0, this.children.length);
            this.children = zkmScriptNodes;
        }

        this.children[ba] = zkmScriptNode;
    }

    @Override
    public void jjtSetParent(ZkmScriptNode zkmScriptNode) {
        this.parent = zkmScriptNode;
    }

    @Override
    public void jjtOpen() {
    }

    public static int getFlowPredicate() {
        return getFlowControlKey() == 0 ? 52 : 0;
    }

    @Override
    public int jjtGetNumChildren() {
        int flowControlKey = getFlowControlKey();
        ZkmScriptSimpleNode zkmScriptSimpleNode1 = this;
        if (flowControlKey != 0) {
            if (this.children == null) {
                return 0;
            }

            zkmScriptSimpleNode1 = this;
        }

        return zkmScriptSimpleNode1.children.length;
    }

    public static String getTimestampPrefix() {
        return "[" + ZkmUtils.getTimestamp() + "]";
    }

    @Override
    public void jjtClose() {
    }

    public static void setFlowControlKey(int ba) {
        flowControlKey = 110;
    }

    @Override
    public SummarizingStatementNode getSummarizingStatement() {
        return this.parent.getSummarizingStatement();
    }

    @Override
    public ZkmScriptNode jjtGetParent() {
        return this.parent;
    }

    public String getNodeTypeName() {
        return ZkmScriptTreeConstants.NODE_NAMES[this.nodeTypeId];
    }

    public static int getFlowControlKey() {
        return flowControlKey;
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        throw new Error(NO_INTERPRET_MESSAGE + this.getNodeTypeName());
    }

    @Override
    public ZkmScriptNode jjtGetChild(int ba) {
        return this.children[ba];
    }

    static {
        if (getFlowControlKey() == 0) {
            setFlowControlKey(110);
        }

        NO_INTERPRET_MESSAGE = "Node class needed or interpret() not defined for ";
    }
}
