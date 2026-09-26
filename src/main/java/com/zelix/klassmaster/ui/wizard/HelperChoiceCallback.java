package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.CallbackAdapter;
import com.zelix.klassmaster.ui.ZkmMainWindow;

import java.io.IOException;

public class HelperChoiceCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.setBusy(false);
        this.mainWindow.restoreDefaultCursor();
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.startChosenHelper((Integer) object);
    }

    public void startChosenHelper(int ba) {
        this.mainWindow.restoreDefaultCursor();
        if (ba == 1) {
            ScriptEnvironment scriptEnvironment1 = this.mainWindow.createScriptEnvironment();
            new BuildHelperWizard(this.mainWindow, scriptEnvironment1, ZkmMainWindow.getUserPreferences(this.mainWindow));
        } else if (ba == 2) {
            new OpenDialogHelper(this.mainWindow, ZkmMainWindow.getUserPreferences(this.mainWindow));
        }
    }

    public HelperChoiceCallback(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }
}
