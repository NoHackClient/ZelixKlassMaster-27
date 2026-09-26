package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTParameterChangeLookupData extends ChangeLogSimpleNode implements ChangeLogNameHolder {
    public String parameterChangeLookupData;

    @Override
    public void setParsedName(Object object) {
        this.parameterChangeLookupData = (String) object;
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int ba, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        ChangeLogSimpleNode.getOpaqueStrings();
        int bb = this.jjtGetNumChildren();
        int bc = 0;

        while (bc < bb) {
            if (ba > 0) {
                this.jjtGetChild(bc).interpret(this, 30872, 34067, 41973, abstractChangeLog);
                bc++;
            }
        }

        ((ChangeLogASTMethodNameChange) changeLogNode).setParameterChangeLookupData(this.parameterChangeLookupData);
    }

    public ASTParameterChangeLookupData() {
        super(20);
    }
}
