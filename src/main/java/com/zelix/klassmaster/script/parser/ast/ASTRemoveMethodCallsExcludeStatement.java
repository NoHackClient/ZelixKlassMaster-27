package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTRemoveMethodCallsExcludeStatement extends ContainedInStatementBase {
    @Override
    public String getStatementName() {
        return "removeMethodCallsExclude";
    }

    public ASTRemoveMethodCallsExcludeStatement() {
        super(36);
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting remove method calls exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bc = (Integer) object2;
        int bb = (Integer) object3;
        int ba = (Integer) object1;
        scriptEnvironment1.addRemoveMethodCallsExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bc, bb, "while executing");
    }
}
