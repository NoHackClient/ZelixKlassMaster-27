package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.ArrayList;

public class ASTLineNumberChange extends ChangeLogSimpleNode {
    public ArrayList oldLineNumbers;
    public Integer newLineNumber;
    public AbstractChangeLog changeLog;

    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int be, int bf, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        this.changeLog = abstractChangeLog;
        int bb = this.jjtGetNumChildren();
        this.oldLineNumbers = new ArrayList(bb - 1);
        int bc = 0;

        while (bc < bb) {
            this.jjtGetChild(bc).interpret(this, 30872, 34067, 41973, abstractChangeLog);
            if (ba >= 0) {
                bc++;
            }
        }

        if (abstractChangeLog.acceptsLineNumberChanges()) {
            ASTClassChange aSTClassChange = (ASTClassChange) changeLogNode;

            for (int i = 0; i < this.oldLineNumbers.size(); i++) {
                aSTClassChange.addLineNumberChange((Integer) this.oldLineNumbers.get(i), this.newLineNumber);
            }
        }
    }

    public void addOldLineNumber(String string) {
        this.oldLineNumbers.add(this.changeLog.getCachedInteger(Integer.parseInt(string)));
    }

    public void setNewLineNumber(String string) {
        this.newLineNumber = this.changeLog.getCachedInteger(Integer.parseInt(string));
    }

    public ASTLineNumberChange() {
        super(35);
    }
}
