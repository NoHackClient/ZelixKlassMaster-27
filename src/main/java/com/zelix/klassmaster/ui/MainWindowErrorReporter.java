package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.dialog.EscapeClosableFrame;

import java.io.IOException;

public class MainWindowErrorReporter extends GuiMessageReporter {
    public final ZkmMainWindow mainWindow;

    @Override
    public void reportErrorWithDetail(Object object, String string, String string1) throws ZkmException, IOException {
        String string2 = (String) object;
        super.reportErrorWithDetail(string2, string, string1);
        String string3 = string2 + this.getSeparatorFor(string2) + string;
        ZkmMainWindow.getLogWriter(this.mainWindow).println(string3);
        ZkmMainWindow.getLogWriter(this.mainWindow).println(string1);
        ZkmMainWindow.getLogWriter(this.mainWindow).flush();
        this.mainWindow.closeFrame();
    }

    public MainWindowErrorReporter(ZkmMainWindow zkmMainWindow, EscapeClosableFrame escapeClosableFrame, ScriptEnvironment scriptEnvironment1) {
        super(escapeClosableFrame, scriptEnvironment1);
        this.mainWindow = zkmMainWindow;
    }

    @Override
    public void reportError(Object object, String string) throws ZkmException, IOException {
        String string1 = (String) object;
        super.reportError(string1, string);
        String string2 = string1 + this.getSeparatorFor(string1) + string;
        ZkmMainWindow.getLogWriter(this.mainWindow).println(string2);
        ZkmMainWindow.getLogWriter(this.mainWindow).flush();
        this.mainWindow.closeFrame();
    }
}
