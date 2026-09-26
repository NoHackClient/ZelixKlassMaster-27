package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ProcessOutputPump;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTExecuteStatement extends SummarizingStatementNode {
    public String command;

    public ASTExecuteStatement() {
        super(81);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetNumChildren();
        ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = (ZkmScriptASTStringLiteral) this.jjtGetChild(0);
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();
        zkmScriptASTStringLiteral.execute(this, scriptEnvironment1);
        this.command = zkmScriptASTStringLiteral.getValue();
        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        String string = ZkmScriptSimpleNode.getTimestampPrefix();
        String string1 = string + " Executing command...";
        printWriter.println(string1 + " [" + this.command + "]");
        System.out.println(string1);
        Runtime runtime1 = Runtime.getRuntime();
        Process process = null;
        String string2 = null;

        try {
            if (SystemEnvironmentConstants.OS_NAME.startsWith("Windows 9")) {
                string2 = "command.com /c ";
            } else if (SystemEnvironmentConstants.OS_NAME.startsWith("Windows NT")
                    || SystemEnvironmentConstants.OS_NAME.startsWith("Windows 200")
                    || SystemEnvironmentConstants.OS_NAME.startsWith("Windows XP")
                    || SystemEnvironmentConstants.OS_NAME.startsWith("OS/2")) {
                string2 = "cmd /c ";
            } else if (SystemEnvironmentConstants.OS_NAME.startsWith("Windows")) {
                string2 = "cmd /c ";
            } else {
                string2 = "";
            }

            process = runtime1.exec(string2 + this.command);
            ProcessOutputPump processOutputPump = new ProcessOutputPump(process.getErrorStream(), printWriter, string.length() + 1, true);
            ProcessOutputPump processOutputPump1 = new ProcessOutputPump(process.getInputStream(), printWriter, string.length() + 1, false);
            processOutputPump.start();
            processOutputPump1.start();
            if (!SystemEnvironmentConstants.JAVA_VM_VENDOR.startsWith("Microsoft")) {
                try {
                    processOutputPump.join();
                    processOutputPump1.join();
                } catch (InterruptedException interruptedException1) {
                }
            }
        } catch (IOException iOException) {
            scriptEnvironment1.logFatalError(this.getStatementName() + " failed: '" + string2 + this.command + "' : '" + iOException.toString() + "'");
        }

        try {
            if (process != null) {
                process.waitFor();
            }
        } catch (InterruptedException interruptedException) {
        }
    }

    @Override
    public String getStatementName() {
        return "execute";
    }
}
