package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTMethodParameterChangesExcludeStatement extends ParameterListStatement {
    public ASTMethodParameterChangesExcludeStatement(int ba) {
        super(ba);
    }

    @Override
    public String getStatementName() {
        return "methodParameterChangesExclude";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bb = (Integer) object3;
        int ba = (Integer) object2;
        int bc = (Integer) object1;
        scriptEnvironment1.addParamChangesExcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bc, ba, bb, "while executing");
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting method parameter changes exclusions...";
        printWriter.println(string);
        System.out.println(string);
    }
}
