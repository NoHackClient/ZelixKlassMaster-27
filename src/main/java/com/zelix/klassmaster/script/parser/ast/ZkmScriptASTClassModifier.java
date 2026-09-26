package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ZkmScriptASTClassModifier extends ScriptValueNode {
    public ZkmScriptASTClassModifier() {
        super(140);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ASTRenameFilterParameter) object).addClassModifier(this.value);
    }
}
