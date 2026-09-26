package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    public void onScriptDialogResult(Integer integer) {
        BuildHelperWizard.accessOnScriptDialogResult(this.wizard, integer);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onScriptDialogResult((Integer) object);
    }

    public BuildHelperCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
