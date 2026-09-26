package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ZkmScriptASTType extends ScriptValueNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ZkmScriptASTQualifiedType) super.parent).addNamePart(this.getValue());
    }

    public ZkmScriptASTType() {
        super(218);
    }
}
