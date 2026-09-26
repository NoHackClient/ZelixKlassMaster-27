package com.zelix.klassmaster.ui.dialog;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OpenClassesListListener implements MouseListener, ListSelectionListener, KeyListener {
    public final OpenClassesDialog openClassesDialog;

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            this.openClassesDialog.removeSelectedEntries();
            this.openClassesDialog.setRemoveEnabled(false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (this.openClassesDialog.getSelectedCount() == 0) {
            this.openClassesDialog.setRemoveEnabled(false);
        } else {
            this.openClassesDialog.setRemoveEnabled(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        OpenClassesDialog openClassesDialog1;
        if (keyEvent.getKeyCode() != 10) {
            if (keyEvent.getKeyCode() != 127) {
                return;
            }

            openClassesDialog1 = this.openClassesDialog;
        } else {
            openClassesDialog1 = this.openClassesDialog;
        }

        openClassesDialog1.removeSelectedEntries();
        this.openClassesDialog.setRemoveEnabled(false);
    }

    public OpenClassesListListener(OpenClassesDialog openClassesDialog1) {
        this.openClassesDialog = openClassesDialog1;
    }
}
