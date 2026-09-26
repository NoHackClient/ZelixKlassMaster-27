package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.DialogCallback;

import java.io.IOException;

public class OperationStatusCallback implements DialogCallback {
    public String statusMessage;
    public boolean successful = true;

    public boolean isSuccessful() {
        return this.successful;
    }

    public void setStatus(String string) {
        this.successful = false;
        this.statusMessage = string;
    }

    @Override
    public void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult(Object object, Object object1) {
    }

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    @Override
    public void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) throws ZkmException, IOException {
    }
}
