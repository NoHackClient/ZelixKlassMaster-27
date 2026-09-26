package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTLongEncryptionExcludeStatement extends ParameterListStatement {
    @Override
    public String getStatementName() {
        return "longEncryptionExclude";
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting long constant encryption exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    public ASTLongEncryptionExcludeStatement() {
        super(24);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bc = (Integer) object3;
        int ba = (Integer) object2;
        int bb = (Integer) object1;
        scriptEnvironment1.addLongEncryptionExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bb, ba, bc, "while executing");
    }
}
