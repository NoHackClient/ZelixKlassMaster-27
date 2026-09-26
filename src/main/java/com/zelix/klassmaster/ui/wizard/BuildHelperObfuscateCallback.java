package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperObfuscateCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onObfuscateOptionsResult((ObfuscateOptions) object, (Integer) object1);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public BuildHelperObfuscateCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    public void onObfuscateOptionsResult(ObfuscateOptions obfuscateOptions1, Integer integer) throws ZkmException, IOException {
        BuildHelperWizard.accessOnObfuscateOptionsResult(this.wizard, obfuscateOptions1, integer);
    }
}
