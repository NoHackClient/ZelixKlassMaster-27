package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTLinkMethodName extends ScriptValueNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ASTLinkMethodSignature) object).setLinkMethodName(this.getValue());
    }

    public ASTLinkMethodName() {
        super(208);
    }
}
