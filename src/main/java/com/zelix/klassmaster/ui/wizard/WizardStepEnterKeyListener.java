package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class WizardStepEnterKeyListener extends KeyAdapter {
    public final TrimOptionsWizardDialog dialog;

    public WizardStepEnterKeyListener(TrimOptionsWizardDialog trimOptionsWizardDialog) {
        this.dialog = trimOptionsWizardDialog;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Object object = keyEvent.getSource();
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
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
