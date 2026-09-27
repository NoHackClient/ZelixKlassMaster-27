package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogWindowCloseListener extends WindowAdapter {
    public final OwnedFrameDialogBase dialog;

    public DialogWindowCloseListener(OwnedFrameDialogBase ownedFrameDialogBase) {
        this.dialog = ownedFrameDialogBase;
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        try {
            this.dialog.closeFrame();
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
