package com.zelix.klassmaster.ui.component;

import java.io.File;

public class TextFileFilter extends ZkmFileFilter {
    public static String description = "Text files (*.txt)";

    @Override
    public boolean accept(File file1) {
        return file1.isDirectory() || file1.getName().endsWith(".txt");
    }

    @Override
    public String getDescription() {
        return description;
    }
}
