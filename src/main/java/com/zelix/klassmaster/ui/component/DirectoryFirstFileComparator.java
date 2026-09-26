package com.zelix.klassmaster.ui.component;

import java.io.File;
import java.util.Comparator;

public class DirectoryFirstFileComparator implements Comparator {
    public static final DirectoryFirstFileComparator INSTANCE = new DirectoryFirstFileComparator();

    public int compareFiles(File file1, File file2) {
        String string = file1.getName().toLowerCase();
        String string1 = file2.getName().toLowerCase();
        if (file1.isDirectory()) {
            return !file2.isDirectory() ? -1 : string.compareTo(string1);
        } else {
            return file2.isDirectory() ? 1 : string.compareTo(string1);
        }
    }

    private DirectoryFirstFileComparator() {
    }

    @Override
    public int compare(Object object, Object object1) {
        return this.compareFiles((File) object, (File) object1);
    }

    public static DirectoryFirstFileComparator getInstance() {
        return INSTANCE;
    }
}
