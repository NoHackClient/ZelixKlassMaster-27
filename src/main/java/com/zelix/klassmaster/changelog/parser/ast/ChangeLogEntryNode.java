package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public abstract class ChangeLogEntryNode extends ChangeLogSimpleNode {
    public AbstractChangeLog changeLog;

    public ChangeLogEntryNode(int ba) {
        super(ba);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int ba, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        this.changeLog = abstractChangeLog;
        int bb = this.jjtGetNumChildren();
        this.beginEntry(abstractChangeLog, bb);
        int bc = 0;

        while (bc < bb) {
            this.jjtGetChild(bc).interpret(this, 30872, 34067, 41973, abstractChangeLog);
            if (ba > 0) {
                bc++;
            }
        }

        this.applyToChangeLog(abstractChangeLog);
    }

    public abstract void applyToChangeLog(Object object) throws ZkmException, IOException;

    public abstract void beginEntry(Object object, Object object1);
}
