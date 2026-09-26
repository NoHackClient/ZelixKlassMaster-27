package com.zelix.klassmaster.ui.component;

import java.io.File;
import java.io.FilenameFilter;

public class DirectoryFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File file1, String string) {
        return new File(file1, string).isDirectory();
    }
}
