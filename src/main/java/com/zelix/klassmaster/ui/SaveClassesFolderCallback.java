package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.DialogCallback;

import java.io.File;
import java.io.IOException;

public class SaveClassesFolderCallback extends CallbackAdapter {
    public final ZkmMainWindow mainWindow;

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.saveToFolder((File) object);
    }

    public SaveClassesFolderCallback(ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
    }

    public void ignoreResult() {
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.mainWindow.setBusy(false);
    }

    public void saveToFolder(File file1) {
        if (file1 != null) {
            this.mainWindow.saveClasses(file1, (DialogCallback) null);
        }
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.ignoreResult();
    }
}
