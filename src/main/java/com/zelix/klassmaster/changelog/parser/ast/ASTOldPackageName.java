package com.zelix.klassmaster.changelog.parser.ast;

public class ASTOldPackageName extends ChangeLogNameNode {
    @Override
    public void passNameToParent(Object object) {
        ((ASTPackageChange) object).setOldPackageName(super.name);
    }

    public ASTOldPackageName() {
        super(9);
    }
}
