package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTResetGroupingsStatement extends SummarizingStatementNode {
    @Override
    public String getStatementName() {
        return "resetGroupings";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Resetting groupings...";
        printWriter.println(string);
        System.out.println(string);
        scriptEnvironment1.resetGroupings();
    }

    public ASTResetGroupingsStatement() {
        super(75);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int warningCount = scriptEnvironment1.getWarningCount();
        Integer integer1 = scriptEnvironment1.getErrorCount();
        Integer integer = scriptEnvironment1.getWarningCount();
        this.executeStatement(scriptEnvironment1, warningCount, integer, integer1);
    }
}
