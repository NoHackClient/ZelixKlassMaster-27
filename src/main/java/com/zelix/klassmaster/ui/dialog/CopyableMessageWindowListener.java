package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.ui.component.SwingUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CopyableMessageWindowListener extends WindowAdapter {
    public final CopyableMessageDialog dialog;

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        this.dialog.closeDialog();
    }

    public CopyableMessageWindowListener(CopyableMessageDialog copyableMessageDialog1) {
        this.dialog = copyableMessageDialog1;
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        SwingUtils.requestFocusOnEdt(this.dialog.okBtn);
    }
}
