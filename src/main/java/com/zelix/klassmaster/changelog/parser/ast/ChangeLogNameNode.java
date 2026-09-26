package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public abstract class ChangeLogNameNode extends ChangeLogSimpleNode implements ChangeLogNameHolder {
    public String name;

    @Override
    public void setParsedName(Object object) {
        this.name = (String) object;
    }

    public abstract void passNameToParent(Object object);

    public ChangeLogNameNode(int ba) {
        super(ba);
    }

    @Override
    public final void interpret(ChangeLogNode changeLogNode, int bd, int ba, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        String[] strings1 = ChangeLogSimpleNode.getOpaqueStrings();
        int bb = this.jjtGetNumChildren();
        String[] strings = strings1;
        int bc = 0;

        while (true) {
            if (bc < bb) {
                if (ba >= 0) {
                    this.jjtGetChild(bc).interpret(this, 30872, 34067, 41973, abstractChangeLog);
                    bc++;
                    continue;
                }

                this.passNameToParent(strings[1]);
                break;
            }

            this.passNameToParent(changeLogNode);
            break;
        }
    }
}
