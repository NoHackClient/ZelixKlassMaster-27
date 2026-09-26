package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.OperationStatusCallback;

import java.io.IOException;

public class BuildHelperContinueCallback extends OperationStatusCallback {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        if (this.wizard.saveDirectory == null) {
            this.wizard.showDefaultSaveDialog();
        } else {
            this.wizard.showSaveDialog(this.wizard.saveDirectory);
        }
    }

    public BuildHelperContinueCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
