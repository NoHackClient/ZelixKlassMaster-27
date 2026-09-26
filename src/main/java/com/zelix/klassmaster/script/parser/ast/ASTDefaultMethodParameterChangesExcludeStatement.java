package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTDefaultMethodParameterChangesExcludeStatement extends ParameterListStatement {
    private static final String STATEMENT_NAME = "default method parameter changes exclude";

    @Override
    public void printStartMessage(Object object) {
    }

    public ASTDefaultMethodParameterChangesExcludeStatement() {
        super(48);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
    }

    @Override
    public String getStatementName() {
        return STATEMENT_NAME;
    }
}
