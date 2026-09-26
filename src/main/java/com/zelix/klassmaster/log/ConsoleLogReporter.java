package com.zelix.klassmaster.log;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;
import java.io.PrintWriter;

public class ConsoleLogReporter extends MessageReporter {
    public final PrintWriter logWriter;
    private final int prefixLength;
    public final ScriptEnvironment scriptEnvironment;

    @Override
    public void reportFatalError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        String string2 = string1 + this.getSeparatorFor(string1) + string + this.scriptEnvironment.takeMessageSuffix();
        System.err.println();
        System.err.println(string2);
        this.logWriter.println(string2);
        this.logWriter.close();
        if (this.isExitOnFatal()) {
            System.exit(1);
        } else {
            throw new ZkmRuntimeException(string2);
        }
    }

    public ConsoleLogReporter(PrintWriter printWriter, int prefixLength, ScriptEnvironment scriptEnvironment1) {
        this.logWriter = printWriter;
        this.prefixLength = prefixLength;
        this.setExitOnFatal(false);
        this.scriptEnvironment = scriptEnvironment1;
    }

    @Override
    public void reportWarning(Object object, String string) {
        String string1 = (String) object;
        String string2 = string1 + this.getSeparatorFor(string1) + string + this.scriptEnvironment.takeMessageSuffix();
        System.out.println();
        System.out.println(string2);
        this.logWriter.println(string2);
    }

    @Override
    public void reportErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        this.reportFatalErrorWithDetail(string2, string, string1);
    }

    @Override
    public void reportInfo(Object object, String string) {
        String string1 = (String) object;
        String string2 = string1 + this.getSeparatorFor(string1) + string + this.scriptEnvironment.takeMessageSuffix();
        System.out.println();
        System.out.println(string2);
        this.logWriter.println(string2);
    }

    @Override
    public void reportError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        this.reportFatalError(string1, string);
    }

    @Override
    public void reportFatalErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        String string3 = string2 + this.getSeparatorFor(string2) + string + this.scriptEnvironment.takeMessageSuffix();
        System.err.println();
        System.err.println(string3);
        System.err.println(string1);
        this.logWriter.println(string3);
        this.logWriter.println(string1);
        this.logWriter.close();
        if (this.isExitOnFatal()) {
            System.exit(1);
        } else {
            throw new ZkmRuntimeException(string3);
        }
    }

    @Override
    public void reportProblem(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        this.reportFatalError(string1, string);
    }
}
