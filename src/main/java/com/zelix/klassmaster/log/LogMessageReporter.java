package com.zelix.klassmaster.log;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;

public class LogMessageReporter extends MessageReporter {
    public ScriptEnvironment scriptEnvironment;
    public int prefixLength;

    @Override
    public void reportProblem(Object object, String string) throws ZkmException, IOException {
        this.scriptEnvironment.logWarning(string);
    }

    @Override
    public void reportError(Object object, String string) throws ZkmException, IOException {
        this.scriptEnvironment.logError(string);
    }

    @Override
    public void reportSeriousError(Object object, String string) throws ZkmException, IOException {
        this.scriptEnvironment.logSeriousError_v(string);
    }

    @Override
    public void reportInfo(Object object, String string) {
        this.scriptEnvironment.logMessage(string);
    }

    @Override
    public void reportFatalErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = string + this.getSeparatorFor(string) + string1;
        this.scriptEnvironment.logFatalError(string2);
    }

    @Override
    public void reportFatalError(Object object, String string) throws ZkmException, IOException {
        this.scriptEnvironment.logFatalError(string);
    }

    @Override
    public void reportErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = string + this.getSeparatorFor(string) + string1;
        this.scriptEnvironment.logError(string2);
    }

    @Override
    public void reportWarning(Object object, String string) {
        this.scriptEnvironment.logWarning(string);
    }

    public LogMessageReporter(ScriptEnvironment scriptEnvironment1, int prefixLength) {
        this.scriptEnvironment = scriptEnvironment1;
        this.prefixLength = prefixLength;
        this.setExitOnFatal(!scriptEnvironment1.throwsOnFatalError());
    }
}
