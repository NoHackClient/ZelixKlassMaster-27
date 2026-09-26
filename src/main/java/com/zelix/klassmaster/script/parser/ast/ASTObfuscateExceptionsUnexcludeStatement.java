package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTObfuscateExceptionsUnexcludeStatement extends ParameterListStatement {
    public ASTObfuscateExceptionsUnexcludeStatement() {
        super(19);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bb = (Integer) object2;
        int ba = (Integer) object3;
        int bc = (Integer) object1;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addObfuscateExceptionsUnexcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bc, bb, ba, "while executing");
    }

    @Override
    public String getStatementName() {
        return "obfuscateExceptionsUnexclude";
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting exception obfuscation unexclusions...";
        printWriter.println(string);
        System.out.println(string);
    }
}
