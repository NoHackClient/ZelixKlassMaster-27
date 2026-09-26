package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class TrimWizardMainClassCallback extends CallbackAdapter {
    public final TrimWizardController controller;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.controller.restoreMainWindow();
    }

    public TrimWizardMainClassCallback(TrimWizardController trimWizardController) {
        this.controller = trimWizardController;
    }

    public void handleExclusionsResult(Integer integer) {
        TrimWizardStepCallback trimWizardStepCallback = new TrimWizardStepCallback(this);
        TrimWizardStepCallback trimWizardStepCallback1 = trimWizardStepCallback;
        this.controller.onTrimExclusionsClosed(integer, trimWizardStepCallback1);
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.handleExclusionsResult((Integer) object);
    }
}
