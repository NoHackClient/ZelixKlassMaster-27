package com.zelix.klassmaster.ui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FileSelectorFieldKeyListener extends KeyAdapter implements ActionListener {
    public final FileSelector fileSelector;

    public FileSelectorFieldKeyListener(FileSelector fileSelector1) {
        this.fileSelector = fileSelector1;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 10) {
            if (keyEvent.getSource() == FileSelector.getApproveButton(this.fileSelector)) {
                this.fileSelector.approveSelection();
            } else if (keyEvent.getSource() == FileSelector.getCancelButton(this.fileSelector)) {
                this.fileSelector.cancelDialog();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == FileSelector.getApproveButton(this.fileSelector)) {
            this.fileSelector.approveSelection();
        } else if (actionEvent.getSource() == FileSelector.getCancelButton(this.fileSelector)) {
            this.fileSelector.cancelDialog();
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
