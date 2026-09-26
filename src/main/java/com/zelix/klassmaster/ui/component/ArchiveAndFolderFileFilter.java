package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.archive.ZkmFileUtils;

import java.io.File;

public class ArchiveAndFolderFileFilter extends ZkmFileFilter {
    public static String description = "Folders and Java archive files";

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File file1) {
        return file1.isDirectory() ? true : ZkmFileUtils.isKnownArchiveName(file1.getName());
    }
}
