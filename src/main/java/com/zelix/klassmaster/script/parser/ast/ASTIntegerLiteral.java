package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTIntegerLiteral extends ScriptValueNode {
    public ASTIntegerLiteral() {
        super(219);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
    }
}
