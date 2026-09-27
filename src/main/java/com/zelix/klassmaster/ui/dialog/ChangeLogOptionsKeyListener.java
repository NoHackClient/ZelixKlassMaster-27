package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChangeLogOptionsKeyListener extends KeyAdapter {
    public final ObfuscateOptionsDialogBase dialog;

    public ChangeLogOptionsKeyListener(ObfuscateOptionsDialogBase obfuscateOptionsDialogBase) {
        this.dialog = obfuscateOptionsDialogBase;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                Object object = keyEvent.getSource();
                if (object == this.dialog.changeLogInBrowseBtn) {
                    ObfuscateOptionsDialogBase.accessBrowseInputChangeLog(this.dialog);
                } else if (object == this.dialog.changeLogOutBrowseBtn) {
                    ObfuscateOptionsDialogBase.accessBrowseOutputChangeLog(this.dialog);
                } else if (object == this.dialog.previousBtn) {
                    this.dialog.goPrevious();
                } else if (object == this.dialog.okBtn) {
                    this.dialog.acceptOptions();
                } else if (object == this.dialog.cancelBtn) {
                    this.dialog.cancelOptions();
                } else if (object == this.dialog.helpBtn) {
                    this.dialog.showHelp();
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
