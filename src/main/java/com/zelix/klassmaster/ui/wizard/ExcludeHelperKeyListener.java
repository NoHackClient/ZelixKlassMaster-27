package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ExcludeHelperKeyListener extends KeyAdapter {
    public final ExcludeHelperDialog excludeHelperDialog;

    public ExcludeHelperKeyListener(ExcludeHelperDialog excludeHelperDialog1) {
        this.excludeHelperDialog = excludeHelperDialog1;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.excludeHelperDialog.previousBtn) {
                    this.excludeHelperDialog.goPrevious();
                } else if (keyEvent.getSource() == this.excludeHelperDialog.okBtn) {
                    this.excludeHelperDialog.acceptParameters();
                } else if (keyEvent.getSource() == this.excludeHelperDialog.cancelBtn) {
                    this.excludeHelperDialog.cancelDialog();
                } else if (keyEvent.getSource() == this.excludeHelperDialog.helpBtn) {
                    this.excludeHelperDialog.showHelp();
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
