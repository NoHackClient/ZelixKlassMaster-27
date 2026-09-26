package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SkippableStepButtonListener implements ActionListener {
    public final TrimOptionsWizardDialog dialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Object object = actionEvent.getSource();
            if (object == this.dialog.previousBtn) {
                this.dialog.onPrevious();
            } else if (object == this.dialog.okBtn) {
                this.dialog.onOk();
            } else if (object == this.dialog.skipBtn) {
                this.dialog.skipStep();
            } else if (object == this.dialog.cancelBtn) {
                this.dialog.onCancel();
            } else if (object == this.dialog.helpBtn) {
                this.dialog.showHelp();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public SkippableStepButtonListener(TrimOptionsWizardDialog trimOptionsWizardDialog) {
        this.dialog = trimOptionsWizardDialog;
    }
}
