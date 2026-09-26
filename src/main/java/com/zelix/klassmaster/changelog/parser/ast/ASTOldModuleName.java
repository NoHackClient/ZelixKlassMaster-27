package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.script.NameValueHolder;

public class ASTOldModuleName extends ChangeLogNameNode {
    @Override
    public void passNameToParent(Object object) {
        ((NameValueHolder) object).setNameValue(super.name);
    }

    public ASTOldModuleName() {
        super(8);
    }
}
