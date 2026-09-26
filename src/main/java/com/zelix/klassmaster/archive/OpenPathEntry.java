package com.zelix.klassmaster.archive;

public class OpenPathEntry {
    public final String path;
    public final ArchivePathFilter filter;
    public final String expectedInitialSha256;
    public final String expectedFinalSha256;

    public String getExpectedInitialSha256() {
        return this.expectedInitialSha256;
    }

    public ArchivePathFilter getFilter() {
        return this.filter;
    }

    public String getPath() {
        return this.path;
    }

    public OpenPathEntry(String string, ArchivePathFilter archivePathFilter, String string1, String string2) {
        this.path = string;
        this.filter = archivePathFilter;
        this.expectedInitialSha256 = string1 != null ? string1.toLowerCase() : null;
        this.expectedFinalSha256 = string2 != null ? string2.toLowerCase() : null;
    }

    public String getExpectedFinalSha256() {
        return this.expectedFinalSha256;
    }
}
