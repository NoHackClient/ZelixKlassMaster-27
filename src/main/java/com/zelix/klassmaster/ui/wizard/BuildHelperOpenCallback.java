package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.ui.dialog.CopyableMessageDialog;

import java.io.IOException;

public class BuildHelperOpenCallback extends OperationStatusCallback {
    public final BuildHelperWizard wizard;

    public BuildHelperOpenCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        if (this.isSuccessful()) {
            BuildHelperWizard.accessShowOpeningInfo(this.wizard);
        } else {
            String string = this.getStatusMessage() != null ? " : " + this.getStatusMessage() + "." : "";
            new CopyableMessageDialog(
                    this.wizard.mainWindow,
                    "Serious Error",
                    "Class not found.",
                    "Error while opening classes."
                            + string
                            + " Cannot proceed to next step. Please adjust your "
                            + "Zelix KlassMaster"
                            + " classpath or class file selections."
            );
            BuildHelperWizard.accessShowOpenClassesDialog(this.wizard, this.wizard.inputRoots);
        }
    }
}
