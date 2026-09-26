package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTFieldNameChange extends ChangeLogModifierNode {
    public String fieldType;
    public String oldFieldName;
    public String newFieldName;

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int be, int ba, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        if (abstractChangeLog.acceptsFieldChanges()) {
            int bb = this.jjtGetNumChildren();
            int bc = 0;

            while (bc < bb) {
                ChangeLogNode changeLogNode1 = this.jjtGetChild(bc);
                if (ba > 0) {
                    changeLogNode1.interpret(this, 30872, 34067, 41973, abstractChangeLog);
                    bc++;
                }
            }

            if (this.newFieldName == null) {
                this.newFieldName = this.oldFieldName;
            }

            ((ASTClassChange) changeLogNode).addFieldChange(this.newFieldName, this.oldFieldName, this.fieldType, this.getModifiers());
        }
    }

    public void setFieldType(String string) {
        this.fieldType = string;
    }

    public void setOldFieldName(String string) {
        this.oldFieldName = string;
    }

    public ChangeLogASTFieldNameChange() {
        super(14);
    }

    public void setNewFieldName(String string) {
        this.newFieldName = string;
    }
}
