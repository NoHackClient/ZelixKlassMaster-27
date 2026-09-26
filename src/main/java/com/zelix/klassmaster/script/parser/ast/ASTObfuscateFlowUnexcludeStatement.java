package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTObfuscateFlowUnexcludeStatement extends ParameterListStatement {
    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = (Integer) object2;
        int bc = (Integer) object1;
        int bb = (Integer) object3;
        scriptEnvironment1.addObfuscateFlowUnexcludeStatement(this);
        this.printMessageSummary(scriptEnvironment1, bc, ba, bb, "while executing");
    }

    public ASTObfuscateFlowUnexcludeStatement() {
        super(17);
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting flow obfuscation unexclusions...";
        printWriter.println(string);
        System.out.println(string);
    }

    @Override
    public String getStatementName() {
        return "obfuscateFlowUnexclude";
    }
}
