package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrimExcludeHelperButtonListener implements ActionListener {
    public final TrimExcludeHelperDialog dialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.dialog.previousBtn) {
                this.dialog.goToPreviousStep();
            } else if (object == this.dialog.okBtn) {
                this.dialog.acceptParameters();
            } else if (object == this.dialog.skipBtn) {
                this.dialog.skipStep();
            } else if (object == this.dialog.cancelBtn) {
                this.dialog.cancelDialog();
            } else if (object == this.dialog.helpBtn) {
                this.dialog.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public TrimExcludeHelperButtonListener(TrimExcludeHelperDialog trimExcludeHelperDialog) {
        this.dialog = trimExcludeHelperDialog;
    }
}
