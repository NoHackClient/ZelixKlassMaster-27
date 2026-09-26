package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTNewMethodName extends ChangeLogSimpleNode implements ChangeLogNameHolder {
    public String newMethodName;

    public ChangeLogASTNewMethodName() {
        super(18);
    }

    @Override
    public void setParsedName(Object object) {
        this.newMethodName = (String) object;
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        long bh = (long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48;
        long bd = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 125719108998908L;
        long bi = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int be = (int) ((bh ^ 0L) >>> 32);
        int bf = (int) (bi << 32 >>> 48);
        int bg = (int) (bi << 48 >>> 48);
        this.jjtGetChild(0).interpret(this, be, bf, bg, abstractChangeLog);
        ChangeLogASTMethodNameChange changeLogASTMethodNameChange = (ChangeLogASTMethodNameChange) changeLogNode;
        String string = this.newMethodName;
        changeLogASTMethodNameChange.setNewMethodName(string);
    }
}
