package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.archive.ZkmFileUtils;

import java.io.File;

public class JadXmlFileFilter extends ZkmFileFilter {
    public static String description = "Folders, archives, 'jad' & 'xml' files";

    @Override
    public boolean accept(File file1) {
        String string = file1.getName().toLowerCase();
        if (file1.isDirectory()) {
            return true;
        } else if (ZkmFileUtils.isKnownArchiveName(file1.getName())) {
            return true;
        } else {
            return string.endsWith(".jad") ? true : ZkmFileUtils.isXmlFileName(string);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }
}
