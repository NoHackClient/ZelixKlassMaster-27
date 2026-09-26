package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTStringEncryptionUnexcludeStatement extends ParameterListStatement {
    public ASTStringEncryptionUnexcludeStatement() {
        super(21);
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting string encryption unexclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    @Override
    public String getStatementName() {
        return "encryptStringLiteralsUnexclude";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int ba = (Integer) object2;
        int bb = (Integer) object3;
        int bc = (Integer) object1;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addStringEncryptionUnexcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bc, ba, bb, "while executing");
    }
}
