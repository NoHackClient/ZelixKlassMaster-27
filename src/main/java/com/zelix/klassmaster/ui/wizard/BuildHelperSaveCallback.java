package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperSaveCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        BuildHelperWizard.accessShowScriptPrompt(this.wizard);
    }

    public BuildHelperSaveCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
