package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class NameExclusionsStepCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public void onNameExclusionsResult(Integer integer) {
        BuildHelperWizard.accessOnNameExclusionsResult(this.wizard, integer);
    }

    public NameExclusionsStepCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onNameExclusionsResult((Integer) object);
    }
}
