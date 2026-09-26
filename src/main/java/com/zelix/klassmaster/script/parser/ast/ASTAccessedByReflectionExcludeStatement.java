package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTAccessedByReflectionExcludeStatement extends ParameterListStatement {
    public ASTAccessedByReflectionExcludeStatement() {
        super(34);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int ba = (Integer) object2;
        int bb = (Integer) object1;
        int bc = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addAccessedByReflectionExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bb, ba, bc, "while executing");
    }

    @Override
    public String getStatementName() {
        return "accessedByReflectionExclude";
    }

    @Override
    public void printStartMessage(Object object) {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Excluding from 'accessed by Reflection' set...";
        scriptEnvironment1.getLogWriter().println(string);
        System.out.println(string);
    }
}
