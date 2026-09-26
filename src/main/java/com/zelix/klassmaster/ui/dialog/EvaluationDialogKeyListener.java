package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EvaluationDialogKeyListener extends KeyAdapter {
    public final EvaluationLicenseDialog dialog;

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                this.dialog.closeDialog();
                if (keyEvent.getSource() == this.dialog.okBtn) {
                    this.dialog.dialogCallback.onDialogResult(this.dialog.ownerFrame);
                } else {
                    this.dialog.dialogCallback.onDialogCancelled();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public EvaluationDialogKeyListener(EvaluationLicenseDialog evaluationLicenseDialog) {
        this.dialog = evaluationLicenseDialog;
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
