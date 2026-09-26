package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.OperationStatusCallback;

import java.io.IOException;

public class BuildHelperTrimCallback extends OperationStatusCallback {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        if (this.isSuccessful()) {
            BuildHelperWizard.accessShowNameExclusionsDialog(this.wizard);
        } else {
            BuildHelperWizard.accessShowTrimOptionsDialog(this.wizard, this.wizard.trimOptions);
        }
    }

    public BuildHelperTrimCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }
}
