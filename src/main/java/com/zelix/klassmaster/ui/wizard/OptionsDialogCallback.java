package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;

import java.io.IOException;

public class OptionsDialogCallback extends CallbackAdapter {
    public final ExclusionOptionsController controller;

    public OptionsDialogCallback(ExclusionOptionsController exclusionOptionsController) {
        this.controller = exclusionOptionsController;
    }

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onNameExclusionsResult((Integer) object);
    }

    public void onNameExclusionsResult(Integer integer) {
        OptionsWizardStepCallback optionsWizardStepCallback = new OptionsWizardStepCallback(this);
        OptionsWizardStepCallback optionsWizardStepCallback1 = optionsWizardStepCallback;
        this.controller.onNameExclusionsResult(integer, optionsWizardStepCallback1);
    }

    @Override
    public void onDialogCancelled() throws ZkmException, IOException {
        this.controller.restoreMainWindow();
    }
}
