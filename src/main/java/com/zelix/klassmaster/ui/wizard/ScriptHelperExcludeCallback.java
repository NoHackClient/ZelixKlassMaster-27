package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptHelperExcludeCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onExcludeResult((Integer) object);
    }

    public void onExcludeResult(Integer integer) {
        ScriptHelperWizard.accessOnExcludeResult(this.wizard, integer);
    }

    public ScriptHelperExcludeCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }
}
