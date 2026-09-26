package com.zelix.klassmaster.ui.dialog;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MessageDialogEnterKeyListener extends KeyAdapter {
    public final MessageBoxDialog messageBoxDialog;

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        Object object = keyEvent.getSource();
        if (keyEvent.getKeyCode() == 10) {
            if (object == this.messageBoxDialog.okBtn) {
                this.messageBoxDialog.closeDialog();
            } else if (object == this.messageBoxDialog.copyBtn) {
                this.messageBoxDialog.copyMessageToClipboard();
            }
        }
    }

    public MessageDialogEnterKeyListener(MessageBoxDialog messageBoxDialog1) {
        this.messageBoxDialog = messageBoxDialog1;
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
