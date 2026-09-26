package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTMethodParameterObfuscationIncludeStatement extends ASTMethodParameterChangesIncludeStatement {
    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int ba = (Integer) object1;
        int bb = (Integer) object3;
        int bc = (Integer) object2;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addParamObfuscationIncludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bc, bb, "while executing");
    }

    @Override
    public String getStatementName() {
        return "methodParameterObfuscationInclude";
    }

    public ASTMethodParameterObfuscationIncludeStatement() {
        super(39);
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting method parameter obfuscation inclusions...";
        printWriter.println(string);
        System.out.println(string);
    }
}
