package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.LinkedSetMultimap;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTClassInitializationOrderStatement extends SummarizingStatementNode {
    public LinkedSetMultimap initializationOrders = new LinkedSetMultimap();

    public ASTClassInitializationOrderStatement() {
        super(83);
    }

    @Override
    public String getStatementName() {
        return "classInitializationOrder";
    }

    public void addInitializationOrder(Object object, Object object1) {
        this.initializationOrders.addValue(object, object1);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Setting class initialization order...";
        printWriter.println(string);
        System.out.println(string);
        int bd = this.jjtGetNumChildren();

        for (int i = 0; i < bd; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bc = (Integer) object3;
        int ba = (Integer) object1;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bb = (Integer) object2;
        scriptEnvironment1.addClassInitializationOrder(this.initializationOrders);
        this.printMessageSummary(scriptEnvironment1, ba, bb, bc, "while executing");
    }
}
