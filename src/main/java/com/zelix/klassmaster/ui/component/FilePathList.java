package com.zelix.klassmaster.ui.component;

import java.io.File;
import javax.swing.ListModel;

public class FilePathList extends PlatformAwareJList {
    public boolean showFullPaths = true;
    public String workingDirPath = System.getProperty("user.dir");
    public File workingDir = new File(this.workingDirPath);

    public FilePathList(ListModel listModel1) {
        super(listModel1);
        this.installRenderer();
    }

    public FilePathList() {
        this.installRenderer();
    }

    public void setShowFullPaths() {
        this.showFullPaths = false;
    }

    public void installRenderer() {
        this.setCellRenderer(new FileListCellRenderer(this));
    }
}
