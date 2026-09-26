package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageViewOkListener implements ActionListener {
    public final TextViewerDialog textViewerDialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.textViewerDialog.closeDialog();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public MessageViewOkListener(TextViewerDialog textViewerDialog1) {
        this.textViewerDialog = textViewerDialog1;
    }
}
