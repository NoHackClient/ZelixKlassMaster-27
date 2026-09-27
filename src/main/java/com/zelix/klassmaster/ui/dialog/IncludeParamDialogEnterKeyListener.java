package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IncludeParamDialogEnterKeyListener extends KeyAdapter {
    public final InclusionParametersDialog inclusionParametersDialog;

    public IncludeParamDialogEnterKeyListener(InclusionParametersDialog inclusionParametersDialog1) {
        this.inclusionParametersDialog = inclusionParametersDialog1;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == InclusionParametersDialog.accessPreviousButton(this.inclusionParametersDialog)) {
                    this.inclusionParametersDialog.goPrevious();
                } else if (keyEvent.getSource() == this.inclusionParametersDialog.okBtn) {
                    this.inclusionParametersDialog.acceptParameters();
                } else if (keyEvent.getSource() == this.inclusionParametersDialog.cancelBtn) {
                    this.inclusionParametersDialog.cancelDialog();
                } else if (keyEvent.getSource() == this.inclusionParametersDialog.helpBtn) {
                    this.inclusionParametersDialog.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
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
