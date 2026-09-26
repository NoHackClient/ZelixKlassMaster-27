package com.zelix.klassmaster.ui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileSelectorButtonListener implements ActionListener {
    public final FileSelector fileSelector;

    public FileSelectorButtonListener(FileSelector fileSelector1) {
        this.fileSelector = fileSelector1;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == FileSelector.getUpFolderButton(this.fileSelector)) {
            this.fileSelector.goToParentDirectory();
        } else if (actionEvent.getSource() == FileSelector.getNewFolderButton(this.fileSelector)) {
            this.fileSelector.promptCreateFolder();
        }
    }
}
