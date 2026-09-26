package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class EvaluationDialogEscapeAction extends AbstractAction {
    public final EvaluationLicenseDialog dialog;

    public EvaluationDialogEscapeAction(EvaluationLicenseDialog evaluationLicenseDialog) {
        this.dialog = evaluationLicenseDialog;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.dialog.closeDialog();
            this.dialog.dialogCallback.onDialogCancelled();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
