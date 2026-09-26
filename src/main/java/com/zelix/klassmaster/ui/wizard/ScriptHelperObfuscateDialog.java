package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.util.DialogCallback;

public class ScriptHelperObfuscateDialog extends BuildHelperObfuscateOptionsDialog {
    public ScriptHelperObfuscateDialog(
            String string,
            ZkmMainWindow zkmMainWindow,
            ObfuscateOptions obfuscateOptions1,
            ExclusionWizardSettings exclusionWizardSettings,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) {
        super(
                "ZKM Script Helper - \"obfuscate\" Statement", string, zkmMainWindow, obfuscateOptions1, exclusionWizardSettings, scriptEnvironment1, dialogCallback1
        );
    }

    @Override
    public boolean openOutputChangeLog(Object object) {
        return true;
    }

    @Override
    public boolean loadInputChangeLog(Object object, Object object1) throws ZkmException {
        return true;
    }
}
