package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ChangeLogOptionListener implements ItemListener {
    public final ObfuscateOptionsDialogBase dialog;

    public ChangeLogOptionListener(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        this.dialog = obfuscateOptionsDialogBase;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        Object object = itemEvent.getSource();
        if (object == this.dialog.changeLogInChk) {
            this.dialog.logFileInTxt.setEnabled(this.dialog.changeLogInChk.isSelected());
            this.dialog.changeLogInBrowseBtn.setEnabled(this.dialog.changeLogInChk.isSelected());
            if (!this.dialog.changeLogInChk.isSelected()) {
                this.dialog.logFileInTxt.select(0, 0);
            }
        } else if (object == this.dialog.changeLogOutChk) {
            this.dialog.logFileOutTxt.setEnabled(this.dialog.changeLogOutChk.isSelected());
            this.dialog.changeLogOutBrowseBtn.setEnabled(this.dialog.changeLogOutChk.isSelected());
            if (!this.dialog.changeLogOutChk.isSelected()) {
                this.dialog.logFileOutTxt.select(0, 0);
            }
        }
    }
}
