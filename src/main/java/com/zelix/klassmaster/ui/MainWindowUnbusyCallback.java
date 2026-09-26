package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class MainWindowUnbusyCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;

    public MainWindowUnbusyCallback(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    public void clearBusyState() {
        this.mainWindow.setBusy(false);
    }

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.clearBusyState();
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.setBusy(false);
    }
}
