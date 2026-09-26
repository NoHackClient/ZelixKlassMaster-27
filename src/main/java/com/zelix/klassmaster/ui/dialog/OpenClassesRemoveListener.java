package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenClassesRemoveListener implements ActionListener, KeyListener {
    public final OpenClassesDialog openClassesDialog;

    public OpenClassesRemoveListener(OpenClassesDialog openClassesDialog1) {
        this.openClassesDialog = openClassesDialog1;
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 10) {
            if (keyEvent.getSource() == this.openClassesDialog.removeBtn) {
                this.openClassesDialog.removeSelectedEntries();
                this.openClassesDialog.afterRemoveSelected();
            } else {
                this.openClassesDialog.clearSelectedList();
                this.openClassesDialog.afterRemoveAll();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.openClassesDialog.removeBtn) {
            this.openClassesDialog.removeSelectedEntries();
            this.openClassesDialog.afterRemoveSelected();
        } else {
            this.openClassesDialog.clearSelectedList();
            this.openClassesDialog.afterRemoveAll();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
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
