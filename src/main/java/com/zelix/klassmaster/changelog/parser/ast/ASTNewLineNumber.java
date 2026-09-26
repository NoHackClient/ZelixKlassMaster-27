package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTNewLineNumber extends ChangeLogSimpleNode {
    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 29099041019815L) >>> 48);
        long be = (((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 29099041019815L) << 16 >>> 16;
        ASTLineNumberChange aSTLineNumberChange = (ASTLineNumberChange) changeLogNode;
        String string1 = ((ChangeLogValueNode) this.jjtGetChild(0)).getValue();
        short bf = (short) bd;
        String string = string1;
        aSTLineNumberChange.addOldLineNumber(string);
    }

    public ASTNewLineNumber() {
        super(37);
    }
}
