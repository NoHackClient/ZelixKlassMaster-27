package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTObfuscateFlowExcludeStatement extends ParameterListStatement {
    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting flow obfuscation exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    public ASTObfuscateFlowExcludeStatement() {
        super(16);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bb = (Integer) object2;
        int bc = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = (Integer) object1;
        scriptEnvironment1.addObfuscateFlowExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bb, bc, "while executing");
    }

    @Override
    public String getStatementName() {
        return "obfuscateFlowExclude";
    }
}
