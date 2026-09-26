package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WarningsDialogWindowListener extends WindowAdapter {
    public final ClassOpenWarningsDialog warningsDialog;

    @Override
    public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.warningsDialog.closeDialog();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public WarningsDialogWindowListener(ClassOpenWarningsDialog classOpenWarningsDialog) {
        this.warningsDialog = classOpenWarningsDialog;
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        SwingUtils.requestFocusOnEdt(this.warningsDialog.okBtn);
    }
}
