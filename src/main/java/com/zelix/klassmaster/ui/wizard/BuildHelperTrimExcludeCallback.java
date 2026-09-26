package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperTrimExcludeCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public BuildHelperTrimExcludeCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onTrimExclusionsResult((Integer) object);
    }

    public void onTrimExclusionsResult(Integer integer) {
        BuildHelperWizard.accessOnTrimExclusionsResult(this.wizard, integer);
    }
}
