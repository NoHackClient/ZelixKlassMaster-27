package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.OperationStatusCallback;

import java.io.IOException;

public class BuildHelperOpeningCallback extends OperationStatusCallback {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }

    public BuildHelperOpeningCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    public void onOpeningInfoResult(Integer integer) throws ZkmException, IOException {
        BuildHelperWizard.accessOnOpeningInfoResult(this.wizard, integer);
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onOpeningInfoResult((Integer) object);
    }
}
