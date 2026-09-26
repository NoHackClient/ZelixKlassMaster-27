package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTTrimUnexcludeStatement extends ParameterListStatement {
    @Override
    public void printStartMessage(Object object) {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting trim unexclusions...";
        scriptEnvironment1.getLogWriter().println(string);
        System.out.println(string);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bb = (Integer) object1;
        int bc = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = (Integer) object2;
        scriptEnvironment1.addTrimUnexcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bb, ba, bc, "while executing");
    }

    public ASTTrimUnexcludeStatement() {
        super(30);
    }

    @Override
    public String getStatementName() {
        return "trimUnexclude";
    }
}
