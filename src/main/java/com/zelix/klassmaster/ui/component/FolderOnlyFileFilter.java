package com.zelix.klassmaster.ui.component;

import java.io.File;

public class FolderOnlyFileFilter extends ZkmFileFilter {
    public static String description = "Only Folders";

    @Override
    public boolean accept(File file1) {
        return file1.isDirectory();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
