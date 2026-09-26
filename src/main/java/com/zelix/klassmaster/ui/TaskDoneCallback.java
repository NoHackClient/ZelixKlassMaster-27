package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class TaskDoneCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;
    public final OperationStatusCallback statusCallback;

    public TaskDoneCallback(ZkmMainWindow zkmMainWindow, OperationStatusCallback operationStatusCallback) {
        this.mainWindow = zkmMainWindow;
        this.statusCallback = operationStatusCallback;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.restoreDefaultCursor();
        ZkmMainWindow.getStatusHolder(this.mainWindow).setValue(" ");
        if (this.statusCallback == null) {
            this.mainWindow.setBusy(false);
        }
    }
}
