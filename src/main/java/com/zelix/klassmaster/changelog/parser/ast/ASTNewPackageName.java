package com.zelix.klassmaster.changelog.parser.ast;

public class ASTNewPackageName extends ChangeLogNameNode {
    @Override
    public void passNameToParent(Object object) {
        ((ASTPackageChange) object).setNewPackageName(super.name);
    }

    public ASTNewPackageName() {
        super(10);
    }
}
