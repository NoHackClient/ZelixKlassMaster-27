package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.CallbackAdapter;
import com.zelix.klassmaster.util.DialogCallback;

import java.io.IOException;

public class ScriptHelperMainClassesCallback extends CallbackAdapter {
    public final ScriptHelperWizard wizard;

    @Override
    public void onDialogResult(Object object) throws ZkmException, IOException {
        this.onMainClassesFound((DialogCallback) object);
    }

    public void onMainClassesFound(DialogCallback dialogCallback1) throws ZkmException, IOException {
        ScriptHelperWizard.accessShowTrimExcludeDialog(this.wizard, dialogCallback1);
    }

    public ScriptHelperMainClassesCallback(ScriptHelperWizard scriptHelperWizard) {
        this.wizard = scriptHelperWizard;
    }
}
