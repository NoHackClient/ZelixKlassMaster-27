package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.LogMessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ASTClasspathStatement extends SummarizingStatementNode {
    public static char pathSeparator = File.pathSeparatorChar;
    public Integer multiReleaseVersion;
    public List classpathEntries = new ArrayList();

    @Override
    public final void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();
        int messageCount = scriptEnvironment1.getMessageCount();
        int warningCount = scriptEnvironment1.getWarningCount();
        int errorCount = scriptEnvironment1.getErrorCount();

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode = this.jjtGetChild(i);
            if (zkmScriptNode instanceof ASTIntegerLiteral) {
                String string = ((ASTIntegerLiteral) zkmScriptNode).getValue();
                if (i == 0) {
                    try {
                        int bf = Integer.parseInt(string);
                        if (bf < 9) {
                            scriptEnvironment1.logFatalError(
                                    "'"
                                            + this.getStatementName()
                                            + "' statement at line "
                                            + this.getStatementLine()
                                            + " has a multi-release version integer '"
                                            + string
                                            + "' which is not 9 or greater."
                            );
                        } else {
                            this.multiReleaseVersion = bf;
                        }
                    } catch (NumberFormatException numberFormatException) {
                        scriptEnvironment1.logFatalError(
                                "'"
                                        + this.getStatementName()
                                        + "' statement at line "
                                        + this.getStatementLine()
                                        + " has an invalid multi-release version integer : '"
                                        + string
                                        + "'d."
                        );
                    }
                } else {
                    scriptEnvironment1.logFatalError(
                            "'"
                                    + this.getStatementName()
                                    + "' statement at line "
                                    + this.getStatementLine()
                                    + " had a multi-release version integer '"
                                    + string
                                    + "' that was not the first parameter of the statement : "
                                    + i
                                    + "."
                    );
                }
            } else if (zkmScriptNode instanceof ZkmScriptASTStringLiteral) {
                ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = (ZkmScriptASTStringLiteral) zkmScriptNode;
                String string1 = zkmScriptASTStringLiteral.getValue();
                if (this.classpathEntries.contains(string1)) {
                    scriptEnvironment1.logWarning(
                            "\""
                                    + string1
                                    + "\" appears more than once in \""
                                    + this.getStatementName()
                                    + "\" statement at line "
                                    + this.getStatementLine()
                                    + ". First occurrence will be used."
                    );
                } else {
                    this.classpathEntries.add(string1);
                }
            } else {
                scriptEnvironment1.logFatalError(
                        this.getClass().getName() + " had " + zkmScriptNode.getClass().getName() + " as child " + i + " at line " + this.getStatementLine() + "."
                );
            }
        }

        Integer integer1 = errorCount;
        Integer integer = warningCount;
        this.executeStatement(scriptEnvironment1, messageCount, integer, integer1);
    }

    public ASTClasspathStatement() {
        super(53);
    }

    @Override
    public void executeStatement(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        int bc = (Integer) object2;
        int ba = (Integer) object3;
        int bb = (Integer) object1;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
        LogMessageReporter logMessageReporter = new LogMessageReporter(scriptEnvironment1, ZkmScriptSimpleNode.getTimestampPrefix().length());
        ZkmClasspath zkmClasspath = scriptEnvironment1.getClasspathForVersion(this.multiReleaseVersion);
        StringBuilder stringBuilder = new StringBuilder();
        int bd = 0;
        int bf = 0;

        for (List list1 = this.classpathEntries; bf < list1.size(); list1 = this.classpathEntries) {
            String string = (String) this.classpathEntries.get(bd);
            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) != pathSeparator) {
                stringBuilder.append(pathSeparator);
            }

            stringBuilder.append(string);
            bf = ++bd;
        }

        String string4 = stringBuilder.toString();
        String string6 = ZkmScriptSimpleNode.getTimestampPrefix()
                + " Setting classpath"
                + (this.multiReleaseVersion == null ? "" : " for multi-release version " + this.multiReleaseVersion);
        System.out.println(string6 + "...");
        ObservableHolder observableHolder = new ObservableHolder();
        ObservableHolder observableHolder1 = new ObservableHolder();
        boolean bl = ZkmClasspath.validateClasspath(string4, scriptEnvironment1.getDefaultDirectory(), observableHolder, logMessageReporter, observableHolder1);
        String string5 = (String) observableHolder.getValue();
        if (!bl) {
            scriptEnvironment1.logError(
                    "Invalid classpath in \""
                            + this.getStatementName()
                            + "\" statement at line "
                            + this.getStatementLine()
                            + " : "
                            + (String) observableHolder1.getValue()
            );
            PrintStream printStream = System.err;
            StringBuilder stringBuilder1 = new StringBuilder();
            int bg = ZkmScriptSimpleNode.getTimestampPrefix().length() + 1;
            Integer integer = 32;
            printStream.println(stringBuilder1.append(ZkmStringUtils.repeatChar(bg, integer)).append("Error detected while setting classpath.").toString());
        }

        String string1 = zkmClasspath.getClasspath();
        zkmClasspath.setClasspath(string5);
        ObservableHolder observableHolder2 = new ObservableHolder();
        ObservableHolder observableHolder3 = new ObservableHolder();
        if (zkmClasspath.locateRuntimeClasses(observableHolder3, observableHolder2)) {
            if (!observableHolder3.isValueNull()) {
                printWriter.println(
                        ZkmScriptSimpleNode.getTimestampPrefix() + " Using \"" + (String) observableHolder3.getValue() + "\" as path to java.lang.Object (E)"
                );
            } else if (!observableHolder2.isValueNull()) {
                printWriter.println(
                        ZkmScriptSimpleNode.getTimestampPrefix() + " Using \"" + (String) observableHolder2.getValue() + "\" as path to java.lang.Object (F)"
                );
            }
        }

        String string2 = zkmClasspath.getClasspath();
        printWriter.println(string6 + " to \"" + ZkmClasspath.describeClasspath(string2) + "\"");
        observableHolder3.clearValue();
        observableHolder2.clearValue();
        ASTClasspathStatement aSTClasspathStatement1;
        ScriptEnvironment scriptEnvironment2;
        int be;
        int bh;
        int bi;
        String string7;
        if (!zkmClasspath.locateRuntimeClasses(observableHolder3, observableHolder2)) {
            scriptEnvironment1.logFatalError(
                    "Could not find java.lang.Object : '"
                            + string2
                            + "' : "
                            + "java.home"
                            + "='"
                            + SystemEnvironmentConstants.JAVA_HOME
                            + "' '"
                            + SystemEnvironmentConstants.JAVA_VM_VENDOR
                            + "' '"
                            + SystemEnvironmentConstants.JAVA_VM_VERSION
                            + "' '"
                            + SystemEnvironmentConstants.OS_NAME
                            + "' '"
                            + SystemEnvironmentConstants.OS_VERSION
                            + "'"
            );
            aSTClasspathStatement1 = this;
            scriptEnvironment2 = scriptEnvironment1;
            be = bb;
            bh = bc;
            bi = ba;
            string7 = "while executing";
        } else if (!observableHolder2.isValueNull()) {
            String string3 = (String) observableHolder2.getValue();
            printWriter.println("\tAdding '" + string3 + "' to classpath. Must be able to find java.lang.Object.");
            aSTClasspathStatement1 = this;
            scriptEnvironment2 = scriptEnvironment1;
            be = bb;
            bh = bc;
            bi = ba;
            string7 = "while executing";
        } else {
            aSTClasspathStatement1 = this;
            scriptEnvironment2 = scriptEnvironment1;
            be = bb;
            bh = bc;
            bi = ba;
            string7 = "while executing";
        }

        aSTClasspathStatement1.printMessageSummary(scriptEnvironment2, be, bh, bi, string7);
        if (scriptEnvironment1.isVerbose()) {
            printWriter.println(
                    "\tChanging classpath" + (this.multiReleaseVersion == null ? "" : " for multi-release version " + this.multiReleaseVersion) + "..."
            );
            printWriter.println("\tfrom\t\"" + string1 + "\"");
            printWriter.println("\tto\t\"" + zkmClasspath.getClasspathDescription() + "\"");
        }
    }

    @Override
    public String getStatementName() {
        return "classpath";
    }
}
