package com.zelix.klassmaster.log;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public abstract class MessageReporter {
    public boolean exitOnFatal = true;
    private static final String SEPARATOR = " : ";

    public abstract void reportErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException;

    public abstract void reportFatalError(Object object, String string) throws ZkmException, IOException;

    public abstract void reportInfo(Object object, String string);

    public void setExitOnFatal(boolean exitOnFatal) {
        this.exitOnFatal = exitOnFatal;
    }

    public abstract void reportWarning(Object object, String string);

    public abstract void reportError(Object object, String string) throws ZkmException, IOException;

    public abstract void reportFatalErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException;

    public boolean isExitOnFatal() {
        return this.exitOnFatal;
    }

    public abstract void reportProblem(Object object, String string) throws ZkmException, IOException;

    public void reportSeriousError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        this.reportError(string1, string);
    }

    public final String getSeparatorFor(String string) {
        return string != null && string.trim().endsWith(":") ? " " : SEPARATOR;
    }
}
