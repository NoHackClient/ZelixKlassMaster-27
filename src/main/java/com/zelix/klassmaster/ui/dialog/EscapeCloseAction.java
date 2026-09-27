package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class EscapeCloseAction extends AbstractAction {
    public final EscapeClosingDialogBase dialog;

    public EscapeCloseAction(EscapeClosingDialogBase escapeClosingDialogBase) {
        this.dialog = escapeClosingDialogBase;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.dialog.isEnabled()) {
                this.dialog.closeDialog();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
