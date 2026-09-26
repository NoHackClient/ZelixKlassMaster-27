package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;

public class ASTNTStatement extends ScriptStatementNode {
    public SummarizingStatementNode statement;

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.statement = (SummarizingStatementNode) this.jjtGetChild(0);
        this.statement.execute(this, scriptEnvironment1);
    }

    public ASTNTStatement() {
        super(1);
    }
}
