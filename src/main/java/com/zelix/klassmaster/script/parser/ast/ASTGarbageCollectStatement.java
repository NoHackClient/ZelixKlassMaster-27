package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTGarbageCollectStatement extends SummarizingStatementNode {
    public int sleepMillis;

    @Override
    public String getStatementName() {
        return "gc";
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Garbage collecting...";
        printWriter.println(string);
        System.out.println(string);
        Runtime runtime1 = Runtime.getRuntime();
        int ba = (int) (runtime1.totalMemory() - runtime1.freeMemory()) / 1024;
        runtime1.gc();
        if (scriptEnvironment1.isVerbose()) {
            printWriter.println("\tSleeping for " + this.sleepMillis + " milliseconds");
        }

        long bd;
        label21:
        {
            try {
                Thread.sleep(this.sleepMillis);
            } catch (InterruptedException interruptedException) {
                bd = runtime1.totalMemory();
                break label21;
            }

            bd = runtime1.totalMemory();
        }

        int bb = (int) (bd - runtime1.freeMemory()) / 1024;
        long bc = runtime1.maxMemory();
        if (bc > -1L) {
            printWriter.println("\tMaximum memory is " + bc / 1024L + "K. " + bb + "K in use. Changed by " + (bb - ba) + "K.");
        } else {
            printWriter.println("\t" + bb + "K in use. Changed by " + (bb - ba) + "K.");
        }
    }

    public ASTGarbageCollectStatement() {
        super(80);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();
        if (ba == 1) {
            ASTIntegerLiteral aSTIntegerLiteral = (ASTIntegerLiteral) this.jjtGetChild(0);
            aSTIntegerLiteral.execute(this, scriptEnvironment1);
            String string = aSTIntegerLiteral.getValue();

            try {
                this.sleepMillis = Integer.parseInt(string);
            } catch (NumberFormatException numberFormatException) {
                this.sleepMillis = 500;
                if (this.sleepMillis < 0) {
                    this.sleepMillis = 500;
                }
            }
        } else {
            this.sleepMillis = 500;
        }

        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }
}
