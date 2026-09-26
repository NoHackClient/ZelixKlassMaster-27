package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CopyableMessageButtonListener implements ActionListener {
    public final CopyableMessageDialog dialog;

    public CopyableMessageButtonListener(CopyableMessageDialog copyableMessageDialog1) {
        this.dialog = copyableMessageDialog1;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.dialog.okBtn) {
            this.dialog.closeDialog();
        } else if (object == this.dialog.copyBtn) {
            this.dialog.copyMessageToClipboard();
        }
    }
}
