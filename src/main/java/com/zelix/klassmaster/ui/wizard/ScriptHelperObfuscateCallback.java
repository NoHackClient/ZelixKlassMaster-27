package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptHelperObfuscateCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onObfuscateOptionsResult((ObfuscateOptions) object, (Integer) object1);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public ScriptHelperObfuscateCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    public void onObfuscateOptionsResult(ObfuscateOptions obfuscateOptions1, Integer integer) throws ZkmException, IOException {
        ScriptHelperWizard.accessOnObfuscateOptionsResult(this.wizard, obfuscateOptions1, integer);
    }
}
