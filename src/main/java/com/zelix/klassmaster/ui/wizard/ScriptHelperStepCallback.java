package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptHelperStepCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public ScriptHelperStepCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onScriptDialogResult((Integer) object);
    }

    public void onScriptDialogResult(Integer integer) {
        ScriptHelperWizard.accessOnScriptDialogResult(this.wizard, integer);
    }
}
