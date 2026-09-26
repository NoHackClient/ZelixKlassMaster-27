package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.NoOpCallback;

import java.awt.Frame;
import java.io.IOException;

public class StartupDialogCallback extends NoOpCallback {
    public final KlassMaster application;

    public StartupDialogCallback(KlassMaster klassMaster) {
        this.application = klassMaster;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        System.exit(0);
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.startMainWindow((Frame) object);
    }

    public void startMainWindow(Frame frame1) throws ZkmException, IOException {
        this.application.showMainWindow((ZkmMainWindow) frame1);
    }
}
