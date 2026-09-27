package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExcludeDialogActionListener implements ActionListener {
    public final NameExclusionsDialog dialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.dialog.okBtn) {
                this.dialog.acceptParameters();
            } else if (object == this.dialog.cancelBtn) {
                this.dialog.cancelDialog();
            } else if (object == this.dialog.helpBtn) {
                this.dialog.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public ExcludeDialogActionListener(NameExclusionsDialog nameExclusionsDialog) {
        this.dialog = nameExclusionsDialog;
    }
}
