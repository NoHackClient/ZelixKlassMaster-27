package com.zelix.klassmaster.changelog.parser.ast;

public class ASTFlowObfuscationClassName extends ChangeLogNameNode {
    public ASTFlowObfuscationClassName() {
        super(44);
    }

    @Override
    public void passNameToParent(Object object) {
        ((FlowObfuscationDataNode) object).setClassName(super.name);
    }
}
