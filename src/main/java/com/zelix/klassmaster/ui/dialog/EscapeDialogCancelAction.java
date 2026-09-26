package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class EscapeDialogCancelAction extends AbstractAction {
    public final EscapeClosableDialog dialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (this.dialog.isEnabled()) {
            this.dialog.closeDialog();
        }
    }

    public EscapeDialogCancelAction(EscapeClosableDialog escapeClosableDialog) {
        this.dialog = escapeClosableDialog;
    }
}
