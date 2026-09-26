package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptTrimExcludeCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onTrimExcludeResult((Integer) object);
    }

    public void onTrimExcludeResult(Integer integer) {
        ScriptHelperWizard.accessOnTrimExcludeResult(this.wizard, integer);
    }

    public ScriptTrimExcludeCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }
}
