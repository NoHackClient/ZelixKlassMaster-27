package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTParameterChangeData extends ChangeLogSimpleNode implements ChangeLogNameHolder {
    public String parameterChangeData;

    public ASTParameterChangeData() {
        super(19);
    }

    @Override
    public void setParsedName(Object object) {
        this.parameterChangeData = (String) object;
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bc, int bd, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).interpret(this, 30872, 34067, 41973, abstractChangeLog);
        }

        ((ChangeLogASTMethodNameChange) changeLogNode).setParameterChangeData(this.parameterChangeData);
    }
}
