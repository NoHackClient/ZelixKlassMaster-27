package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTDefaultTrimExcludeStatement extends ParameterListStatement {
    private static final String STATEMENT_NAME = "default trim exclude";

    public ASTDefaultTrimExcludeStatement() {
        super(46);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
    }

    @Override
    public String getStatementName() {
        return STATEMENT_NAME;
    }

    @Override
    public void printStartMessage(Object object) {
    }
}
