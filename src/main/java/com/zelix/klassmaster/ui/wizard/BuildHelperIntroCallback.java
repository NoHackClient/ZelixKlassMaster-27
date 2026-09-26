package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperIntroCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    public BuildHelperIntroCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    public void onIntroResult() {
        BuildHelperWizard.accessShowClasspathStep(this.wizard);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onIntroResult();
    }
}
