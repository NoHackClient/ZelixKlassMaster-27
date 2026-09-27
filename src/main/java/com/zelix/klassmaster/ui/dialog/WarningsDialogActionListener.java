package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WarningsDialogActionListener implements ActionListener {
    public final ClassOpenWarningsDialog warningsDialog;

    public WarningsDialogActionListener(ClassOpenWarningsDialog classOpenWarningsDialog) {
        this.warningsDialog = classOpenWarningsDialog;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.warningsDialog.closeDialog();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
