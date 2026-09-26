package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTAccessedByReflectionStatement extends ParameterListStatement {
    public ASTAccessedByReflectionStatement() {
        super(33);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bc = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bb = (Integer) object2;
        int ba = (Integer) object1;
        scriptEnvironment1.addAccessedByReflectionStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bb, bc, "while executing");
    }

    @Override
    public String getStatementName() {
        return "accessedByReflection";
    }

    @Override
    public void printStartMessage(Object object) {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Creating or adding to 'accessed by Reflection' set...";
        scriptEnvironment1.getLogWriter().println(string);
        System.out.println(string);
    }
}
