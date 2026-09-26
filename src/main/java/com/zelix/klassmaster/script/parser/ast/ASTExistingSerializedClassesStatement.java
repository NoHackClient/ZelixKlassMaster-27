package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTExistingSerializedClassesStatement extends ParameterListStatement {
    @Override
    public String getStatementName() {
        return "existingSerializedClasses";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bb = (Integer) object3;
        int bc = (Integer) object1;
        int ba = (Integer) object2;
        scriptEnvironment1.addExistingSerializedClassesStatement(this);
        this.printMessageSummary(scriptEnvironment1, bc, ba, bb, "while executing");
    }

    @Override
    public void printStartMessage(Object object) {
        PrintWriter printWriter = ((ScriptEnvironment) object).getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting existing serialized classes...";
        printWriter.println(string);
        System.out.println(string);
    }

    public ASTExistingSerializedClassesStatement() {
        super(26);
    }
}
