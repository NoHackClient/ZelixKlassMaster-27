package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.dialog.CopyableMessageDialog;
import com.zelix.klassmaster.ui.dialog.EscapeClosableFrame;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class GuiMessageReporter extends MessageReporter {
    public final EscapeClosableFrame ownerFrame;
    public final ScriptEnvironment scriptEnvironment;

    @Override
    public void reportErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string3 = string;
        String string2 = (String) object;
        if (this.scriptEnvironment != null) {
            string3 = string3 + this.scriptEnvironment.takeMessageSuffix();
            PrintWriter printWriter = this.scriptEnvironment.getLogWriter();
            printWriter.println(string2 + " : " + string3);
            printWriter.println(string1);
        }

        CopyableMessageDialog.showMessage(this.ownerFrame, string2, string3, string1);
    }

    @Override
    public void reportFatalErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        String string3 = string;
        if (this.scriptEnvironment != null) {
            string3 = string3 + this.scriptEnvironment.takeMessageSuffix();
            PrintWriter printWriter = this.scriptEnvironment.getLogWriter();
            printWriter.println(string2 + " : " + string3);
            printWriter.println(string1);
        }

        CopyableMessageDialog.showMessage(this.ownerFrame, string2, string3, string1);
        System.err.println();
        System.err.println(string2 + " : " + string3);
        this.ownerFrame.closeFrame();
    }

    @Override
    public void reportInfo(Object object, String string) {
        String string1 = (String) object;
        String string2 = string;
        if (this.scriptEnvironment != null) {
            string2 = string2 + this.scriptEnvironment.takeMessageSuffix();
            this.scriptEnvironment.getLogWriter().println(string1 + " : " + string2);
        }

        if (string2.length() <= 90) {
            MessageBoxDialog.showMessage(this.ownerFrame, string1, string2, true);
        } else {
            CopyableMessageDialog.showMessage(this.ownerFrame, string1, string1, string2);
        }
    }

    @Override
    public void reportFatalError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        String string2 = string;
        if (this.scriptEnvironment != null) {
            string2 = string2 + this.scriptEnvironment.takeMessageSuffix();
            this.scriptEnvironment.getLogWriter().println(string1 + " : " + string2);
        }

        PrintStream printStream;
        if (string2.length() <= 90) {
            MessageBoxDialog.showMessage(this.ownerFrame, string1, string2, true);
            printStream = System.err;
        } else {
            CopyableMessageDialog.showMessage(this.ownerFrame, string1, string1, string2);
            printStream = System.err;
        }

        printStream.println();
        System.err.println(string1 + " : " + string2);
        this.ownerFrame.closeFrame();
    }

    public GuiMessageReporter(EscapeClosableFrame escapeClosableFrame, ScriptEnvironment scriptEnvironment1) {
        this.ownerFrame = escapeClosableFrame;
        this.scriptEnvironment = scriptEnvironment1;
    }

    @Override
    public void reportError(Object object, String string) throws ZkmException, IOException {
        String string1 = string;
        String string2 = (String) object;
        if (this.scriptEnvironment != null) {
            string1 = string1 + this.scriptEnvironment.takeMessageSuffix();
            this.scriptEnvironment.getLogWriter().println(string2 + " : " + string1);
        }

        if (string1.length() <= 90) {
            MessageBoxDialog.showMessage(this.ownerFrame, string2, string1, true);
        } else {
            CopyableMessageDialog.showMessage(this.ownerFrame, string2, string2, string1);
        }
    }

    @Override
    public void reportWarning(Object object, String string) {
        String string2 = string;
        String string1 = (String) object;
        if (this.scriptEnvironment != null) {
            string2 = string2 + this.scriptEnvironment.takeMessageSuffix();
            this.scriptEnvironment.getLogWriter().println(string1 + " : " + string2);
        }

        if (string2.length() <= 90) {
            MessageBoxDialog.showMessage(this.ownerFrame, string1, string2, true);
        } else {
            CopyableMessageDialog.showMessage(this.ownerFrame, string1, string1, string2);
        }
    }

    @Override
    public void reportProblem(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        this.reportWarning(string1, string);
    }
}
