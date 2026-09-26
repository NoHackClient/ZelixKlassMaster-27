package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptHelperRefIncludeCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onReferenceInclusionsResult((Integer) object);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public ScriptHelperRefIncludeCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    public void onReferenceInclusionsResult(Integer integer) throws ZkmException, IOException {
        this.wizard.onReferenceInclusionsResult(integer);
    }
}
