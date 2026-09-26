package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.SwingUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TextViewerWindowListener extends WindowAdapter {
    public final TextViewerDialog textViewerDialog;

    public TextViewerWindowListener(TextViewerDialog textViewerDialog1) {
        this.textViewerDialog = textViewerDialog1;
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        SwingUtils.requestFocusOnEdt(this.textViewerDialog.okBtn);
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.textViewerDialog.closeDialog();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
