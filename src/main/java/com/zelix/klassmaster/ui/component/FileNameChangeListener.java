package com.zelix.klassmaster.ui.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FileNameChangeListener implements PropertyChangeListener {
    public final FileSelector fileSelector;
    private static final String FILE_NAME_CHANGED = "fileNameChanged";

    public FileNameChangeListener(FileSelector fileSelector1) {
        this.fileSelector = fileSelector1;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(FILE_NAME_CHANGED)) {
            String string = (String) propertyChangeEvent.getNewValue();
            FileSelector fileSelector1;
            if (string != null) {
                if (string.length() != 0) {
                    FileSelector.getApproveButton(this.fileSelector).setEnabled(true);
                    return;
                }

                fileSelector1 = this.fileSelector;
            } else {
                fileSelector1 = this.fileSelector;
            }

            FileSelector.getApproveButton(fileSelector1).setEnabled(false);
        }
    }
}
