package com.zelix.klassmaster.archive;

import java.io.File;
import java.io.FilenameFilter;

public class PrefixSuffixFilenameFilter implements FilenameFilter {
    public String prefix = "ZKM";
    public String suffix = ".tmp";

    @Override
    public boolean accept(File file1, String string) {
        return new File(file1, string).isDirectory() ? false : string.startsWith(this.prefix) && string.endsWith(this.suffix);
    }
}
