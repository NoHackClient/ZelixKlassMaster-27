package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTLongEncryptionUnexcludeStatement extends ParameterListStatement {
    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting long constant encryption unexclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    public ASTLongEncryptionUnexcludeStatement() {
        super(25);
    }

    @Override
    public String getStatementName() {
        return "longEncryptionUnexclude";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bc = (Integer) object3;
        int ba = (Integer) object1;
        int bb = (Integer) object2;
        scriptEnvironment1.addLongEncryptionUnexcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, ba, bb, bc, "while executing");
    }
}
