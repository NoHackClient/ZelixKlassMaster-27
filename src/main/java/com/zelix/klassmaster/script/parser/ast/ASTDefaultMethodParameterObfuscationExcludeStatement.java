package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTDefaultMethodParameterObfuscationExcludeStatement extends ParameterListStatement {
    private static final String STATEMENT_NAME = "default method parameter obfuscation exclude";

    @Override
    public void printStartMessage(Object object) {
    }

    @Override
    public String getStatementName() {
        return STATEMENT_NAME;
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
    }

    public ASTDefaultMethodParameterObfuscationExcludeStatement() {
        super(50);
    }
}
