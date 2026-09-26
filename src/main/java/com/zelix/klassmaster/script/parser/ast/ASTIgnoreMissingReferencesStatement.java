package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTIgnoreMissingReferencesStatement extends ParameterListStatement {
    @Override
    public String getStatementName() {
        return "ignoreMissingReferences";
    }

    public ASTIgnoreMissingReferencesStatement() {
        super(28);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int ba = (Integer) object1;
        int bb = (Integer) object3;
        int bc = (Integer) object2;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addIgnoreMissingReferencesStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bc, bb, "while executing");
    }

    @Override
    public void printStartMessage(Object object) {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting references to be ignored...";
        scriptEnvironment1.getLogWriter().println(string);
        System.out.println(string);
    }
}
