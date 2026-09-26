package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class OpenDialogEscapeAction extends AbstractAction {
    public final OpenClassesDialog openClassesDialog;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.openClassesDialog.isEnabled()) {
                this.openClassesDialog.cancelOpen();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public OpenDialogEscapeAction(OpenClassesDialog openClassesDialog1) {
        this.openClassesDialog = openClassesDialog1;
    }
}
