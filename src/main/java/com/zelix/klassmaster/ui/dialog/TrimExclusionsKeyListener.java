package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TrimExclusionsKeyListener extends KeyAdapter {
    public final TrimExclusionsDialog trimExclusionsDialog;

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.trimExclusionsDialog.okBtn) {
                    this.trimExclusionsDialog.acceptParameters();
                } else if (keyEvent.getSource() == this.trimExclusionsDialog.testBtn) {
                    this.trimExclusionsDialog.runTestTrim();
                } else if (keyEvent.getSource() == this.trimExclusionsDialog.cancelBtn) {
                    this.trimExclusionsDialog.cancelDialog();
                } else if (keyEvent.getSource() == this.trimExclusionsDialog.helpBtn) {
                    this.trimExclusionsDialog.showHelp();
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public TrimExclusionsKeyListener(TrimExclusionsDialog trimExclusionsDialog1) {
        this.trimExclusionsDialog = trimExclusionsDialog1;
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
