package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ObfuscateOptionsCallback extends CallbackAdapter {
    public final ObfuscateWizardFlow wizardFlow;

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizardFlow.restoreMainWindow();
    }

    public void onObfuscateOptionsResult(Integer integer) throws ZkmException, IOException {
        this.wizardFlow.onOptionsResult(integer);
    }

    public ObfuscateOptionsCallback(ObfuscateWizardFlow obfuscateWizardFlow) {
        this.wizardFlow = obfuscateWizardFlow;
    }

    @Override
    public void onDialogResult_v(Object object1, Object object) throws ZkmException, IOException {
        Integer integer = (Integer) object;
        this.onObfuscateOptionsResult(integer);
    }
}
