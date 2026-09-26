package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.DefaultNoOpCallback;

import java.io.IOException;

public class NoOpCallback implements DialogCallback {
    public static NoOpCallback instance = new DefaultNoOpCallback();

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult(Object object, Object object1) {
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
    }

    @Override
    public void onDialogResult(Object object, Object object1, Object object2, Object object3, Object object4, Object object5) throws ZkmException, IOException {
    }

    public static NoOpCallback getInstance() {
        return instance;
    }
}
