package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.log.LogMessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.NoOpCallback;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTRemoveMethodCallsStatement extends SummarizingStatementNode {
    public ASTRemoveMethodCallsStatement() {
        super(54);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bb = (Integer) object2;
        int ba = (Integer) object1;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int bc = (Integer) object3;
        ClassRepository classRepository1 = scriptEnvironment1.getClassRepository();
        if (!classRepository1.isOpened()) {
            scriptEnvironment1.logFatalError(
                    "Attempt to use \"" + this.getStatementName() + "\" statement at line " + this.getStatementLine() + " before opening classes"
            );
        } else if (classRepository1.hasNoClassesOpened()) {
            scriptEnvironment1.logFatalError(
                    "Attempt to use \"" + this.getStatementName() + "\" statement at line " + this.getStatementLine() + " with no classes opened"
            );
        } else if (!classRepository1.isOpenedWithoutErrors()) {
            scriptEnvironment1.logFatalError(
                    "Attempt to use \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + " with unusable classes : "
                            + classRepository1.getOpenErrorMessage()
            );
        } else if (!classRepository1.hasProgramClasses()) {
            scriptEnvironment1.logMessage(
                    "Only module-info.class classes opened. Statement \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + " will have no effect."
            );
            return;
        }

        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix() + " Removing method calls...";
        printWriter.println(string);
        System.out.println(string);
        new LogMessageReporter(scriptEnvironment1, ZkmScriptSimpleNode.getTimestampPrefix().length());
        NoOpCallback.getInstance();

        ASTRemoveMethodCallsStatement aSTRemoveMethodCallsStatement1;
        ScriptEnvironment scriptEnvironment2;
        int bd;
        int be;
        int bf;
        String string1;
        label28:
        {
            try {
                classRepository1.removeMethodCalls(
                        scriptEnvironment1.getRemoveMethodCallsIncludeStatements(), scriptEnvironment1.getRemoveMethodCallsExcludeStatements(), scriptEnvironment1
                );
            } catch (ZkmProcessingException zkmProcessingException) {
                scriptEnvironment1.logFatalError(zkmProcessingException.getMessage());
                aSTRemoveMethodCallsStatement1 = this;
                scriptEnvironment2 = scriptEnvironment1;
                bd = ba;
                be = bb;
                bf = bc;
                string1 = "while executing";
                break label28;
            }

            aSTRemoveMethodCallsStatement1 = this;
            scriptEnvironment2 = scriptEnvironment1;
            bd = ba;
            be = bb;
            bf = bc;
            string1 = "while executing";
        }

        aSTRemoveMethodCallsStatement1.printMessageSummary(scriptEnvironment2, bd, be, bf, string1);
    }

    @Override
    public String getStatementName() {
        return "removeMethodCalls";
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();
        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }
}
