package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.ScriptStatementInfo;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.IOException;
import java.io.PrintStream;

public abstract class SummarizingStatementNode extends ScriptStatementNode implements ScriptStatementInfo {
    public int lineNumber;

    public SummarizingStatementNode(int ba) {
        super(ba);
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public int getStatementLine() {
        return this.lineNumber;
    }

    @Override
    public SummarizingStatementNode getSummarizingStatement() {
        return this;
    }

    @Override
    public abstract String getStatementName();

    public abstract void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException;

    public final void printMessageSummary(ScriptEnvironment scriptEnvironment1, int ba, int bb, int bc, String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int bd = scriptEnvironment1.getMessageCount() - ba;
        if (bd > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" and ");
            }

            stringBuilder.append(bd + (bd > 1 ? " messages" : " message"));
        }

        int be = scriptEnvironment1.getWarningCount() - bb;
        if (be > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" and ");
            }

            stringBuilder.append(be + (be > 1 ? " warnings" : " warning"));
        }

        int bf = scriptEnvironment1.getErrorCount() - bc;
        if (bf > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" and ");
            }

            stringBuilder.append(bf + (bf > 1 ? " errors" : " error"));
        }

        if (stringBuilder.length() > 0) {
            PrintStream printStream = System.out;
            StringBuilder stringBuilder1 = new StringBuilder();
            int bg = ZkmScriptSimpleNode.getTimestampPrefix().length() + 1;
            Integer integer = 32;
            printStream.println(
                    stringBuilder1.append(ZkmStringUtils.repeatChar(bg, integer))
                            .append(stringBuilder.toString())
                            .append(" detected ")
                            .append(string)
                            .append(" \"")
                            .append(this.getStatementName())
                            .append("\" statement")
                            .toString()
            );
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
