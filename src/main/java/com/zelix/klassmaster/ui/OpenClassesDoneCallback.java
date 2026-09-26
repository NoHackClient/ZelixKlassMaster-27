package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class OpenClassesDoneCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;
    public final OperationStatusCallback statusCallback;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.restoreDefaultCursor();
        ZkmMainWindow.getStatusHolder(this.mainWindow).setValue(" ");
        if (this.statusCallback == null) {
            this.mainWindow.setBusy(false);
        }
    }

    public OpenClassesDoneCallback(ZkmMainWindow zkmMainWindow, OperationStatusCallback operationStatusCallback) {
        this.mainWindow = zkmMainWindow;
        this.statusCallback = operationStatusCallback;
    }
}
