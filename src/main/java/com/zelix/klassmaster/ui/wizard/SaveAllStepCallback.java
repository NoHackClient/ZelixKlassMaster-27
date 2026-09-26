package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.File;
import java.io.IOException;

public class SaveAllStepCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    public void onSaveAllResult(File file1, Integer integer) throws ZkmException, IOException {
        ScriptHelperWizard.accessOnSaveAllResult(this.wizard, file1, integer);
    }

    public SaveAllStepCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }

    @Override
    public void onDialogResult_v(Object object, Object object1) throws ZkmException, IOException {
        this.onSaveAllResult((File) object, (Integer) object1);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizard.finishWizard();
    }
}
