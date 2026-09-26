package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

public class FileListCellRenderer extends DefaultListCellRenderer {
    public final FilePathList fileList;
    public FileIconProvider iconProvider;

    public FileListCellRenderer(FilePathList filePathList) {
        this.fileList = filePathList;
        this.iconProvider = new FileIconProvider();
    }

    @Override
    public Component getListCellRendererComponent(JList jList, Object object, int ba, boolean bl, boolean bl1) {
        super.getListCellRendererComponent(jList, object, ba, bl, bl1);
        String string = object.toString();
        String string1 = string;
        if (string1.equals(".")) {
            string1 = this.fileList.workingDirPath;
        } else if (string1.equals("..")) {
            string1 = this.fileList.workingDir.getParent();
            if (string1 == null) {
                string1 = this.fileList.workingDirPath;
            }
        }

        File file1 = new File(string1);
        Icon icon1 = this.iconProvider.getFileIcon(file1);
        if (icon1 != null) {
            this.setIcon(icon1);
        }

        if (string.equals(".") || string.equals("..")) {
            this.setText(string);
        } else if (this.fileList.showFullPaths) {
            this.setText(file1.getAbsolutePath());
        } else {
            this.setText(file1.getName());
        }

        return this;
    }
}
