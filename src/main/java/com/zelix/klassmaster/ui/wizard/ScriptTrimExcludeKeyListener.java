package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ScriptTrimExcludeKeyListener extends KeyAdapter {
    public final TrimExcludeHelperDialog dialog;

    public ScriptTrimExcludeKeyListener(TrimExcludeHelperDialog trimExcludeHelperDialog) {
        this.dialog = trimExcludeHelperDialog;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.dialog.previousBtn) {
                    this.dialog.goToPreviousStep();
                } else if (keyEvent.getSource() == this.dialog.okBtn) {
                    this.dialog.acceptParameters();
                } else if (keyEvent.getSource() == this.dialog.skipBtn) {
                    this.dialog.skipStep();
                } else if (keyEvent.getSource() == this.dialog.cancelBtn) {
                    this.dialog.cancelDialog();
                } else if (keyEvent.getSource() == this.dialog.helpBtn) {
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
