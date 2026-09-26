package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperTrimOptionsCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    public BuildHelperTrimOptionsCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onTrimOptionsResult((TrimOptions) object, (Integer) object1);
    }

    public void onTrimOptionsResult(TrimOptions trimOptions1, Integer integer) throws ZkmException, IOException {
        BuildHelperWizard.accessOnTrimOptionsResult(this.wizard, trimOptions1, integer);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }
}
