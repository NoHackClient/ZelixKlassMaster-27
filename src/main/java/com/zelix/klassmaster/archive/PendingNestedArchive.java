package com.zelix.klassmaster.archive;

import java.io.File;

public class PendingNestedArchive {
    public SourceArchive parentArchive;
    public String entryName;
    public File extractedFile;

    public PendingNestedArchive(SourceArchive sourceArchive1, String string, File file1) {
        this.parentArchive = sourceArchive1;
        this.entryName = string;
        this.extractedFile = file1;
    }
}
