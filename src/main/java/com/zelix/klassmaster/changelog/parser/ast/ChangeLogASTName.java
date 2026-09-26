package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTName extends ChangeLogValueNode {
    public ChangeLogASTName() {
        super(33);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        long bd = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 42379030247544L;
        ChangeLogNameHolder changeLogNameHolder = (ChangeLogNameHolder) changeLogNode;
        String string = this.getValue();
        changeLogNameHolder.setParsedName(string);
    }
}
