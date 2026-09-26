package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialogButtonListener implements ActionListener {
    public final MessageBoxDialog messageBoxDialog;

    public MessageDialogButtonListener(MessageBoxDialog messageBoxDialog1) {
        this.messageBoxDialog = messageBoxDialog1;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.messageBoxDialog.okBtn) {
            this.messageBoxDialog.closeDialog();
        } else if (object == this.messageBoxDialog.copyBtn) {
            this.messageBoxDialog.copyMessageToClipboard();
        }
    }
}
