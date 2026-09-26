package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeLogASTMemberModifier extends ChangeLogValueNode {
    @Override
    public void interpret(ChangeLogNode changeLogNode, int ba, int bc, int bb, AbstractChangeLog abstractChangeLog) throws ZkmException, IOException {
        ChangeLogModifierNode changeLogModifierNode = (ChangeLogModifierNode) this.parent;
        if (this.value.equals("public")) {
            changeLogModifierNode.addPublicModifier();
        } else {
            ChangeLogASTMemberModifier changeLogASTMemberModifier1 = this;
            if (bb >= 0) {
                if (this.value.equals("protected")) {
                    changeLogModifierNode.addProtectedModifier();
                    return;
                }

                changeLogASTMemberModifier1 = this;
            }

            String string = changeLogASTMemberModifier1.value;
            String string1 = "private";
            if (ba >= 0) {
                if (changeLogASTMemberModifier1.value.equals("private")) {
                    changeLogModifierNode.addPrivateModifier();
                    return;
                }

                string = this.value;
                string1 = "static";
            }

            if (string.equals(string1)) {
                changeLogModifierNode.addStaticModifier();
            }
        }
    }

    public ChangeLogASTMemberModifier() {
        super(25);
    }
}
