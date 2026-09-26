package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class ReferenceInclusionsCallback extends CallbackAdapter {
    public final ObfuscateWizardFlow wizardFlow;

    public void onReferenceInclusionsResult(Integer integer) throws ZkmException, IOException {
        ObfuscateWizardFlow.accessOnReferenceInclusionsResult(this.wizardFlow, integer);
    }

    public ReferenceInclusionsCallback(ObfuscateWizardFlow obfuscateWizardFlow) {
        this.wizardFlow = obfuscateWizardFlow;
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.wizardFlow.restoreMainWindow();
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onReferenceInclusionsResult((Integer) object);
    }
}
