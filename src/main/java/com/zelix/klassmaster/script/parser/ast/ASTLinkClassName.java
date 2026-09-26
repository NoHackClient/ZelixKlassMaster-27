package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTLinkClassName extends ScriptValueNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ASTRenameFilterParameter) object).setLinkClassName(this.getValue());
    }

    public ASTLinkClassName() {
        super(199);
    }
}
