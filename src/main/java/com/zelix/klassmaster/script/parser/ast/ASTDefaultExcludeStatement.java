package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTDefaultExcludeStatement extends ParameterListStatement {
    private static final String STATEMENT_NAME = "default exclude";

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
    }

    @Override
    public void printStartMessage(Object object) {
    }

    @Override
    public String getStatementName() {
        return STATEMENT_NAME;
    }

    public ASTDefaultExcludeStatement() {
        super(44);
    }
}
