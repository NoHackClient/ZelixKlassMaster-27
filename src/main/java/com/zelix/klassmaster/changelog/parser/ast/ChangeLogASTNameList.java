package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.Vector;

public class ChangeLogASTNameList extends ChangeLogSimpleNode implements ChangeLogNameHolder {
    public Vector nameParts = new Vector();

    public String buildDottedName() {
        StringBuffer stringBuffer = new StringBuffer();
        int ba = this.nameParts.size();

        for (int i = 0; i < ba; i++) {
            stringBuffer.append((String) this.nameParts.elementAt(i));
            if (i < ba - 1) {
                stringBuffer.append(".");
            }
        }

        return stringBuffer.toString();
    }

    public ChangeLogASTNameList() {
        super(22);
    }

    @Override
    public void setParsedName(Object object) {
        this.nameParts.addElement(object);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int ba, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int bb = this.jjtGetNumChildren();

        for (int i = 0; i < bb; i++) {
            ChangeLogNode changeLogNode1 = this;
            if (ba > 0) {
                changeLogNode1 = this.jjtGetChild(i);
            }

            changeLogNode1.interpret(this, 30872, 34067, 41973, abstractChangeLog);
        }

        ChangeLogNameHolder changeLogNameHolder = (ChangeLogNameHolder) changeLogNode;
        changeLogNameHolder.setParsedName(this.buildDottedName());
    }
}
