package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class OptionsWizardStepCallback extends CallbackAdapter {
    public final OptionsDialogCallback optionsDialogCallback;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.optionsDialogCallback.controller.restoreMainWindow();
    }

    @Override
    public void onDialogResult_v(Object object1, Object object) throws ZkmException, IOException {
        Integer integer = (Integer) object;
        this.onOptionsResult(integer);
    }

    public void onOptionsResult(Integer integer) throws ZkmException, IOException {
        this.optionsDialogCallback.controller.onOptionsResult(integer);
    }

    public OptionsWizardStepCallback(OptionsDialogCallback optionsDialogCallback1) {
        this.optionsDialogCallback = optionsDialogCallback1;
    }
}
