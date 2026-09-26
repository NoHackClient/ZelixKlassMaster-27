package com.zelix.klassmaster.ui.component;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FileListSelectionListener implements ListSelectionListener {
    public final FileListView fileListView;

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        FileListView.onSelectionChanged(this.fileListView);
    }

    public FileListSelectionListener(FileListView fileListView1) {
        this.fileListView = fileListView1;
    }
}
