package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class DialogEscapeAction extends AbstractAction {
    public final OwnedFrameDialogBase dialog;

    public DialogEscapeAction(OwnedFrameDialogBase ownedFrameDialogBase) {
        this.dialog = ownedFrameDialogBase;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            if (this.dialog.isEnabled()) {
                this.dialog.cancelDialog();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
