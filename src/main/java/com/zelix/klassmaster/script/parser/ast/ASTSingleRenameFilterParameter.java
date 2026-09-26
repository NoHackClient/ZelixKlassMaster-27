package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;

public class ASTSingleRenameFilterParameter extends ParameterListStatement {
    private static final String STATEMENT_NAME = "Single exclude parameter";

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
    }

    @Override
    public String getStatementName() {
        return STATEMENT_NAME;
    }

    public ASTRenameFilterParameter getRenameFilterParameter() {
        return (ASTRenameFilterParameter) this.jjtGetChild(0);
    }

    @Override
    public void printStartMessage(Object object) {
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
    }

    public ASTSingleRenameFilterParameter() {
        super(52);
    }
}
