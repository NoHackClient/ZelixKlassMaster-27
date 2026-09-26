package com.zelix.klassmaster.ui.component;

import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalIconFactory;

public class FileIconProvider {
    public JFileChooser fileChooser;

    public FileIconProvider() {
        try {
            this.fileChooser = new JFileChooser();
        } catch (Throwable throwable) {
        }
    }

    public static Icon getNewFolderIcon() {
        try {
            Icon icon1 = UIManager.getIcon("FileChooser.newFolderIcon");
            if (icon1 == null) {
                icon1 = MetalIconFactory.getFileChooserNewFolderIcon();
            }

            return icon1;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static Icon getUpFolderIcon() {
        try {
            Icon icon1 = UIManager.getIcon("FileChooser.upFolderIcon");
            if (icon1 == null) {
                icon1 = MetalIconFactory.getFileChooserUpFolderIcon();
            }

            return icon1;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public Icon getFileIcon(File file1) {
        if (this.fileChooser != null) {
            try {
                return this.fileChooser.getIcon(file1);
            } catch (Throwable throwable) {
            }
        }

        return null;
    }
}
