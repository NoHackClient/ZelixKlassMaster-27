package com.zelix.klassmaster.ui.component;

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

public class DirectoryComboBoxRenderer extends DefaultListCellRenderer {
    public final DirectoryComboBox comboBox;
    public IndentedIcon indentedIcon;
    public FileIconProvider iconProvider;

    @Override
    public Component getListCellRendererComponent(JList jList, Object object, int ba, boolean bl, boolean bl1) {
        super.getListCellRendererComponent(jList, object, ba, bl, bl1);
        File file1 = (File) object;
        if (file1 == null) {
            this.setText("");
            return this;
        }

        int bb = 0;
        FileIconProvider fileIconProvider;
        if (ba != -1) {
            for (File file2 = file1.getParentFile(); file2 != null; file2 = file2.getParentFile()) {
                bb++;
            }

            fileIconProvider = this.iconProvider;
        } else {
            fileIconProvider = this.iconProvider;
        }

        Icon icon1 = fileIconProvider.getFileIcon(file1);
        this.indentedIcon.icon = icon1;
        this.indentedIcon.indentLevel = bb;
        this.setIcon(this.indentedIcon);
        return this;
    }

    public DirectoryComboBoxRenderer(DirectoryComboBox directoryComboBox1) {
        this.comboBox = directoryComboBox1;
        this.indentedIcon = new IndentedIcon();
        this.iconProvider = new FileIconProvider();
    }
}
