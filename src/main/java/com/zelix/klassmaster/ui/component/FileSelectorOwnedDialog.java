package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.ui.dialog.EscapeClosingDialogBase;

import javax.swing.JFrame;

public class FileSelectorOwnedDialog extends EscapeClosingDialogBase {
    public final FileSelector fileSelector;

    public FileSelectorOwnedDialog(FileSelector fileSelector1, JFrame jFrame, String string) {
        super(jFrame, string, true);
        this.fileSelector = fileSelector1;
    }
}
