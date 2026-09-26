package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogNameHolder;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTSourceName extends ChangeLogSimpleNode implements ChangeLogNameHolder {
    public String sourceName;

    @Override
    public final void interpret(ChangeLogNode changeLogNode, int ba, int bb, int bc, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        long bg = ((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L;
        int bd = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) >>> 32);
        int be = (int) ((((long) ba << 32 | (long) bb << 48 >>> 32 | (long) bc << 48 >>> 48) ^ 0L) << 32 >>> 48);
        int bf = (int) (bg << 48 >>> 48);
        ChangeLogSimpleNode changeLogSimpleNode = (ChangeLogSimpleNode) this.jjtGetChild(0);
        changeLogSimpleNode.interpret(this, bd, be, bf, abstractChangeLog);
        if (bc > 0) {
            if (changeLogSimpleNode instanceof ChangeLogValueNode) {
                this.sourceName = ((ChangeLogValueNode) changeLogSimpleNode).getValue();
            }

            this.applyToClassChange((ASTClassChange) changeLogNode);
        }
    }

    public ASTSourceName() {
        super(13);
    }

    @Override
    public void setParsedName(Object object) {
        this.sourceName = (String) object;
    }

    public void applyToClassChange(ASTClassChange aSTClassChange) {
        aSTClassChange.setSourceName(this.sourceName);
    }
}
