package com.zelix.klassmaster.ui.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class FileSelectionListener implements PropertyChangeListener {
    public final FileSelector fileSelector;
    private static final String FINAL_SELECTION = "finalSelection";

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(FINAL_SELECTION) && (File) propertyChangeEvent.getNewValue() != null) {
            this.fileSelector.dialogResult = 1;
            FileSelector.getDialog(this.fileSelector).setVisible(false);
            FileSelector.getDialog(this.fileSelector).dispose();
        }
    }

    public FileSelectionListener(FileSelector fileSelector1) {
        this.fileSelector = fileSelector1;
    }
}
