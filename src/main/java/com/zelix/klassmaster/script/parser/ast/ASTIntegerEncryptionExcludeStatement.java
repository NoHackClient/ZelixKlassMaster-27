package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTIntegerEncryptionExcludeStatement extends ParameterListStatement {
    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting integer constant encryption exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    @Override
    public String getStatementName() {
        return "integerEncryptionExclude";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int ba = (Integer) object2;
        int bb = (Integer) object1;
        int bc = (Integer) object3;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        scriptEnvironment1.addIntegerEncryptionExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bb, ba, bc, "while executing");
    }

    public ASTIntegerEncryptionExcludeStatement() {
        super(22);
    }
}
