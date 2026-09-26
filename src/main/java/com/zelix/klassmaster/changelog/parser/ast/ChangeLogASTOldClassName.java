package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.ClassNameNodeSetter;

public class ChangeLogASTOldClassName extends ChangeLogNameNode {
    @Override
    public void passNameToParent(Object object) {
        ((ClassNameNodeSetter) object).acceptClassName(super.name);
    }

    public ChangeLogASTOldClassName() {
        super(11);
    }
}
