package com.zelix.klassmaster.changelog.parser.ast;

public class ASTFlowObfuscationPackageName extends ChangeLogNameNode {
    @Override
    public void passNameToParent(Object object) {
        ((ASTPackageFlowObfuscationData) object).setPackageName(super.name);
    }

    public ASTFlowObfuscationPackageName() {
        super(45);
    }
}
