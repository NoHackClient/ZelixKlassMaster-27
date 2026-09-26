package com.zelix.klassmaster.log;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ConsoleMessageReporter extends MessageReporter {
    @Override
    public void reportWarning(Object object, String string) {
        String string1 = (String) object;
        System.out.println();
        System.out.println(string1 + this.getSeparatorFor(string1) + string);
    }

    @Override
    public void reportInfo(Object object, String string) {
        String string1 = (String) object;
        System.out.println();
        System.out.println(string1 + this.getSeparatorFor(string1) + string);
    }

    @Override
    public void reportFatalError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        System.err.println();
        System.err.println(string1 + this.getSeparatorFor(string1) + string);
        System.exit(1);
    }

    @Override
    public void reportProblem(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        this.reportWarning(string1, string);
    }

    @Override
    public void reportError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        this.reportFatalError(string1, string);
    }

    @Override
    public void reportErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        this.reportFatalErrorWithDetail(string2, string, string1);
    }

    @Override
    public void reportFatalErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        System.err.println();
        System.err.println(string2 + this.getSeparatorFor(string2) + string);
        System.err.println(string1);
        System.exit(1);
    }
}
