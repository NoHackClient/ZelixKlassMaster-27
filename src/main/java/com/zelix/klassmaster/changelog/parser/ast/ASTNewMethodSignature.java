package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTNewMethodSignature extends ChangeLogMethodSignatureNode {
    public boolean parametersObfuscated;

    public void setParametersObfuscated() {
        this.parametersObfuscated = true;
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int be, int ba, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int bb = this.jjtGetNumChildren();

        for (int i = 0; i < bb; i++) {
            ChangeLogNode changeLogNode1 = this;
            if (ba >= 0) {
                changeLogNode1 = this.jjtGetChild(i);
            }

            changeLogNode1.interpret(this, 30872, 34067, 41973, abstractChangeLog);
        }

        ((ChangeLogASTMethodNameChange) changeLogNode).setNewSignature(super.methodName, super.argumentTypes, this.parametersObfuscated);
    }

    public ASTNewMethodSignature() {
        super(27);
    }
}
