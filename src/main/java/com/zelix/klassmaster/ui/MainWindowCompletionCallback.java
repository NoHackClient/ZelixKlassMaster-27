package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.DialogCallback;

import java.io.IOException;

public class MainWindowCompletionCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;
    public final DialogCallback dialogCallback;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.restoreDefaultCursor();
        if (this.dialogCallback == null) {
            this.mainWindow.setBusy(false);
        }
    }

    public MainWindowCompletionCallback(ZkmMainWindow zkmMainWindow, DialogCallback dialogCallback1) {
        this.mainWindow = zkmMainWindow;
        this.dialogCallback = dialogCallback1;
    }
}
