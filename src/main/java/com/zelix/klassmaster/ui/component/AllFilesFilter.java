package com.zelix.klassmaster.ui.component;

import java.io.File;

public class AllFilesFilter extends ZkmFileFilter {
    public static String description = "All files (\"*.*\")";

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File file1) {
        return true;
    }
}
