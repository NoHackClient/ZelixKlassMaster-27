package com.zelix.klassmaster.changelog.parser.ast;

public class ChangeLogASTNewClassName extends ChangeLogNameNode {
    @Override
    public void passNameToParent(Object object) {
        ((ASTClassChange) object).setNewClassName(super.name);
    }

    public ChangeLogASTNewClassName() {
        super(12);
    }
}
