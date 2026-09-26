package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class TrimWizardStepCallback extends CallbackAdapter {
    public final TrimWizardMainClassCallback parentCallback;

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.handleTrimOptionsResult((TrimOptions) object, (Integer) object1);
    }

    public void handleTrimOptionsResult(TrimOptions trimOptions1, Integer integer) throws ZkmException, IOException {
        this.parentCallback.controller.onTrimOptionsClosed(trimOptions1, integer);
    }

    public TrimWizardStepCallback(TrimWizardMainClassCallback trimWizardMainClassCallback) {
        this.parentCallback = trimWizardMainClassCallback;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.parentCallback.controller.restoreMainWindow();
    }
}
