package com.zelix.klassmaster.ui.dialog;

import com.zelix.GuiResources;
import com.zelix.UserPreferences;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.util.DialogCallback;

import javax.swing.JFrame;

public class StackTraceClasspathDialog extends ClasspathDialog {
    private static final String HELP_PAGE_ID = "038";

    @Override
    public void saveClasspathPreference(String string) {
        super.userPreferences.setBytecodeClasspath(string);
        super.userPreferences.savePreferences();
    }

    @Override
    public void showHelp() {
        GuiResources.showHelpTopic(HELP_PAGE_ID);
    }

    public StackTraceClasspathDialog(
            JFrame jFrame, ZkmClasspath zkmClasspath, String string, String string1, UserPreferences userPreferences1, DialogCallback dialogCallback1
    ) {
        super(jFrame, "Obfuscated Bytecode Classpath", zkmClasspath, string, "OK", string1, userPreferences1, dialogCallback1);
    }
}
