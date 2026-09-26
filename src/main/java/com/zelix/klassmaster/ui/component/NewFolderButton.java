package com.zelix.klassmaster.ui.component;

import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JButton;

public class NewFolderButton extends JButton {
    public static String fallbackText = "New";

    @Override
    public float getAlignmentY() {
        return 0.5F;
    }

    public NewFolderButton() {
        Icon icon1 = FileIconProvider.getNewFolderIcon();
        NewFolderButton newFolderButton2;
        String string;
        if (icon1 != null) {
            this.setIcon(icon1);
            newFolderButton2 = this;
            string = "New Folder";
        } else {
            this.setText(fallbackText);
            newFolderButton2 = this;
            string = "New Folder";
        }

        newFolderButton2.setToolTipText(string);
    }

    @Override
    public Insets getMargin() {
        return new Insets(1, 1, 1, 1);
    }
}
