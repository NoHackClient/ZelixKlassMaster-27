package com.zelix.klassmaster.ui.dialog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HelpViewerKeyListener extends KeyAdapter {
    public final HelpViewerDialog helpViewerDialog;

    public HelpViewerKeyListener(HelpViewerDialog helpViewerDialog1) {
        this.helpViewerDialog = helpViewerDialog1;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == 10) {
                if (keyEvent.getSource() == this.helpViewerDialog.backBtn) {
                    HelpViewerDialog.accessGoBack(this.helpViewerDialog);
                } else if (keyEvent.getSource() == this.helpViewerDialog.closeBtn) {
                    this.helpViewerDialog.closeFrame();
                } else if (keyEvent.getSource() == this.helpViewerDialog.forwardBtn) {
                    HelpViewerDialog.accessGoForward(this.helpViewerDialog);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
