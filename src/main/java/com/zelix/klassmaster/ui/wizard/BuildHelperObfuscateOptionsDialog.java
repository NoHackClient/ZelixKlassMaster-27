package com.zelix.klassmaster.ui.wizard;

import com.zelix.GuiResources;
import com.zelix.klassmaster.config.ExclusionWizardSettings;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.ui.ZkmMainWindow;
import com.zelix.klassmaster.ui.dialog.ObfuscateOptionsDialog;
import com.zelix.klassmaster.util.DialogCallback;

import javax.swing.JButton;

public class BuildHelperObfuscateOptionsDialog extends ObfuscateOptionsDialog {
    public BuildHelperObfuscateOptionsDialog(
            String string,
            String string1,
            ZkmMainWindow zkmMainWindow,
            ObfuscateOptions obfuscateOptions1,
            ExclusionWizardSettings exclusionWizardSettings,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) {
        super(string, string1, zkmMainWindow, obfuscateOptions1, exclusionWizardSettings, scriptEnvironment1, dialogCallback1);
    }

    @Override
    public void createButtons(String string) {
        super.previousBtn = new JButton("Previous");
        super.previousBtn.setToolTipText(GuiResources.getTooltipText("PREVIOUS_SCREEN"));
        super.okBtn = new JButton("Next");
        super.okBtn.setToolTipText(string);
        super.cancelBtn = new JButton("Cancel");
        super.cancelBtn.setToolTipText(GuiResources.getTooltipText("CANCEL_DIALOG"));
        super.helpBtn = new JButton("Help");
        super.helpBtn.setToolTipText(GuiResources.getTooltipText("SCREEN_HELP"));
    }
}
