package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTType extends ChangeLogValueNode {
    public ChangeLogASTType() {
        super(34);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        ((ChangeLogASTQualifiedType) changeLogNode).addNamePart(this.getValue());
    }
}
