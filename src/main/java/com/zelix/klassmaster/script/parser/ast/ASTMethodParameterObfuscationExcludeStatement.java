package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTMethodParameterObfuscationExcludeStatement extends ASTMethodParameterChangesExcludeStatement {
    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting method parameter obfuscation exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bb = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bc = (Integer) object2;
        int ba = (Integer) object1;
        scriptEnvironment1.addParamObfuscationExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bc, bb, "while executing");
    }

    public ASTMethodParameterObfuscationExcludeStatement() {
        super(40);
    }

    @Override
    public String getStatementName() {
        return "methodParameterObfuscationExclude";
    }
}
