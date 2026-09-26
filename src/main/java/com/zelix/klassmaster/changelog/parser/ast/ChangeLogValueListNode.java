package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public abstract class ChangeLogValueListNode extends ChangeLogSimpleNode {
    @Override
    public final void interpret(ChangeLogNode changeLogNode, int bc, int bd, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        FlowObfuscationDataNode flowObfuscationDataNode = (FlowObfuscationDataNode) changeLogNode;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            String string = ((ChangeLogValueNode) this.jjtGetChild(i)).getValue();
            flowObfuscationDataNode.addValue(string);
        }
    }

    public ChangeLogValueListNode(int ba) {
        super(ba);
    }
}
