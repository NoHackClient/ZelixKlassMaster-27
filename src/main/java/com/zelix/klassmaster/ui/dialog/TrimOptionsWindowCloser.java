package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TrimOptionsWindowCloser extends WindowAdapter {
    public final DeleteAttributesDialog deleteAttributesDialog;

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.deleteAttributesDialog.closeFrame();
            this.deleteAttributesDialog.dialogCallback.onDialogCancelled();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public TrimOptionsWindowCloser(DeleteAttributesDialog deleteAttributesDialog1) {
        this.deleteAttributesDialog = deleteAttributesDialog1;
    }
}
