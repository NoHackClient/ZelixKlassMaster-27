package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ObfuscateOptionsEscapeAction extends AbstractAction {
    public final ObfuscateOptionsDialogBase obfuscateOptionsDialog;

    public ObfuscateOptionsEscapeAction(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        this.obfuscateOptionsDialog = obfuscateOptionsDialogBase;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.obfuscateOptionsDialog.isEnabled()) {
                this.obfuscateOptionsDialog.cancelOptions();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
