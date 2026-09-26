package com.zelix.klassmaster.archive;

import java.io.File;
import java.io.FilenameFilter;

public class ClassFileNameFilter implements FilenameFilter {
    private static final String CLASS_EXTENSION = ".class";

    @Override
    public boolean accept(File file1, String string) {
        return string.endsWith(CLASS_EXTENSION);
    }
}
