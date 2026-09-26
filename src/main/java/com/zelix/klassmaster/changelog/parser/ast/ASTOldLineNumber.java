package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTOldLineNumber extends ChangeLogSimpleNode {
    public ASTOldLineNumber() {
        super(36);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        ((ASTLineNumberChange) changeLogNode).setNewLineNumber(((ChangeLogValueNode) this.jjtGetChild(0)).getValue());
    }
}
