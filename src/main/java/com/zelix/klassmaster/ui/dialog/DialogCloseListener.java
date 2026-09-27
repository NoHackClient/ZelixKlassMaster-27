package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogCloseListener extends WindowAdapter {
    public final EscapeClosingDialogBase dialog;

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.dialog.closeDialog();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }

    public DialogCloseListener(EscapeClosingDialogBase escapeClosingDialogBase) {
        this.dialog = escapeClosingDialogBase;
    }
}
