package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperStepCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    public void onReferenceInclusionsResult(Integer integer) throws ZkmException, IOException {
        this.wizard.onReferenceInclusionsResult(integer);
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onReferenceInclusionsResult((Integer) object);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public BuildHelperStepCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
