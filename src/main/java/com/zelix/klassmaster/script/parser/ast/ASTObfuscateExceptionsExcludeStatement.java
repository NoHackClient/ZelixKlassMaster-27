package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTObfuscateExceptionsExcludeStatement extends ParameterListStatement {
    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int ba = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bc = (Integer) object1;
        int bb = (Integer) object2;
        scriptEnvironment1.addObfuscateExceptionsExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bc, bb, ba, "while executing");
    }

    public ASTObfuscateExceptionsExcludeStatement() {
        super(18);
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting exception obfuscation exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    @Override
    public String getStatementName() {
        return "obfuscateExceptionsExclude";
    }
}
