package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ScriptHelperIntroCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    public void onIntroResult() {
        ScriptHelperWizard.accessShowClasspathStep(this.wizard);
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onIntroResult();
    }

    public ScriptHelperIntroCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }
}
