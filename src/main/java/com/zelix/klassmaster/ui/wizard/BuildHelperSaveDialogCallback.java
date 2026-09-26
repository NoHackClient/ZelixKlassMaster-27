package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.File;
import java.io.IOException;

public class BuildHelperSaveDialogCallback extends CallbackAdapter {
    public final BuildHelperWizard wizard;

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onSaveDirectoryResult((File) object, (Integer) object1);
    }

    public BuildHelperSaveDialogCallback(BuildHelperWizard buildHelperWizard) {
        this.wizard = buildHelperWizard;
    }

    public void onSaveDirectoryResult(File file1, Integer integer) {
        BuildHelperWizard.accessOnSaveDirectoryResult(this.wizard, file1, integer);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }
}
