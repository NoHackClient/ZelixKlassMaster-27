package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.SwingUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MessageDialogWindowListener extends WindowAdapter {
    public final MessageBoxDialog messageBoxDialog;

    public MessageDialogWindowListener(MessageBoxDialog messageBoxDialog1) {
        this.messageBoxDialog = messageBoxDialog1;
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        SwingUtils.requestFocusOnEdt(this.messageBoxDialog.okBtn);
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        this.messageBoxDialog.closeDialog();
    }
}
