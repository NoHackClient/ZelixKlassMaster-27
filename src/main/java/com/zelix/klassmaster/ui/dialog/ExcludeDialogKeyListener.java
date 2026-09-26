package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ExcludeDialogKeyListener extends KeyAdapter {
    public final NameExclusionsDialog dialog;

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.dialog.okBtn) {
                    this.dialog.acceptParameters();
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

    public ExcludeDialogKeyListener(NameExclusionsDialog nameExclusionsDialog) {
        this.dialog = nameExclusionsDialog;
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
