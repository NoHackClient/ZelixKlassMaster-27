package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTNegatedClassModifier extends ScriptValueNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ASTRenameFilterParameter) object).addClassModifier("!" + this.value);
    }

    public ASTNegatedClassModifier() {
        super(141);
    }
}
