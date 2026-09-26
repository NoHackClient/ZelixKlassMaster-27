package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTInput extends ChangeLogSimpleNode {
    public ChangeLogASTInput() {
        super(0);
    }

    @Override
    public void interpret(ChangeLogNode changeLogNode, int bd, int ba, int be, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        int bb = this.jjtGetNumChildren();
        int bc = 0;

        while (true) {
            if (bc < bb) {
                this.jjtGetChild(bc).interpret(this, 30872, 34067, 41973, abstractChangeLog);
            } else if (ba >= 0) {
                return;
            }

            bc++;
        }
    }
}
