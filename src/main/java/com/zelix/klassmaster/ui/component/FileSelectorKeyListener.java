package com.zelix.klassmaster.ui.component;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FileSelectorKeyListener extends KeyAdapter {
    public final FileSelector fileSelector;

    public FileSelectorKeyListener(FileSelector fileSelector1) {
        this.fileSelector = fileSelector1;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 10) {
            if (keyEvent.getSource() == FileSelector.getUpFolderButton(this.fileSelector)) {
                this.fileSelector.goToParentDirectory();
            } else if (keyEvent.getSource() == FileSelector.getNewFolderButton(this.fileSelector)) {
                this.fileSelector.promptCreateFolder();
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
