package com.zelix.klassmaster.ui.component;

import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;

public class UpFolderButton extends JButton implements PropertyChangeListener, FileChooserComponent {
    public static String fallbackText = "Up";

    @Override
    public Insets getMargin() {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
    }

    public UpFolderButton() {
        Icon icon1 = FileIconProvider.getUpFolderIcon();
        UpFolderButton upFolderButton2;
        String string;
        if (icon1 != null) {
            this.setIcon(icon1);
            upFolderButton2 = this;
            string = "Up one level";
        } else {
            this.setText(fallbackText);
            upFolderButton2 = this;
            string = "Up one level";
        }

        upFolderButton2.setToolTipText(string);
    }

    @Override
    public float getAlignmentY() {
        return 0.5F;
    }
}
