package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class BuildHelperScriptCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;
    private static final String HELPER_TITLE = "Build Helper";

    public void showEquivalentScript() {
        BuildHelperWizard.accessShowScriptDialog(this.wizard, this.wizard.buildEquivalentScript(HELPER_TITLE, this.wizard.inputRoots));
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.showEquivalentScript();
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public BuildHelperScriptCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
