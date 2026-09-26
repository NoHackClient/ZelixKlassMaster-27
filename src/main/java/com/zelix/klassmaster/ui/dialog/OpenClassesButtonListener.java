package com.zelix.klassmaster.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenClassesButtonListener implements ActionListener, KeyListener {
    public final OpenClassesDialog openClassesDialog;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public OpenClassesButtonListener(OpenClassesDialog openClassesDialog1) {
        this.openClassesDialog = openClassesDialog1;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.openClassesDialog.addBtn) {
            this.openClassesDialog.addSelectedFiles();
            this.openClassesDialog.afterAddSelected();
        } else {
            this.openClassesDialog.addSelectionContents();
            this.openClassesDialog.afterAddContents();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 10) {
            if (keyEvent.getSource() == this.openClassesDialog.addBtn) {
                this.openClassesDialog.addSelectedFiles();
                this.openClassesDialog.afterAddSelected();
            } else {
                this.openClassesDialog.addSelectionContents();
                this.openClassesDialog.afterAddContents();
            }
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
