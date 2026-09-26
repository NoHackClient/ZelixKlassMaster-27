package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ObfuscateDialogButtonListener implements ActionListener {
    public final ObfuscateOptionsDialogBase obfuscateOptionsDialog;

    public ObfuscateDialogButtonListener(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        this.obfuscateOptionsDialog = obfuscateOptionsDialogBase;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.obfuscateOptionsDialog.changeLogInBrowseBtn) {
                ObfuscateOptionsDialogBase.accessBrowseInputChangeLog(this.obfuscateOptionsDialog);
            } else if (object == this.obfuscateOptionsDialog.changeLogOutBrowseBtn) {
                ObfuscateOptionsDialogBase.accessBrowseOutputChangeLog(this.obfuscateOptionsDialog);
            } else if (object == this.obfuscateOptionsDialog.previousBtn) {
                this.obfuscateOptionsDialog.goPrevious();
            } else if (object == this.obfuscateOptionsDialog.okBtn) {
                this.obfuscateOptionsDialog.acceptOptions();
            } else if (object == this.obfuscateOptionsDialog.cancelBtn) {
                this.obfuscateOptionsDialog.cancelOptions();
            } else if (object == this.obfuscateOptionsDialog.helpBtn) {
                this.obfuscateOptionsDialog.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
