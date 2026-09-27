package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EvaluationDialogWindowListener extends WindowAdapter {
    public final EvaluationLicenseDialog dialog;

    public EvaluationDialogWindowListener(EvaluationLicenseDialog evaluationLicenseDialog) {
        this.dialog = evaluationLicenseDialog;
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.dialog.closeDialog();
            this.dialog.dialogCallback.onDialogCancelled();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
