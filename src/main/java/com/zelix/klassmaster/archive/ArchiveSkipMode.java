package com.zelix.klassmaster.archive;

public enum ArchiveSkipMode {
    TOP_LEVEL(""),
    SKIP("-"),
    UNSKIP("+");

    public static final ArchiveSkipMode[] VALUES = new ArchiveSkipMode[]{TOP_LEVEL, SKIP, UNSKIP};
    public final String prefix;

    public String getPrefix() {
        return this.prefix;
    }

    ArchiveSkipMode(String string1) {
        this.prefix = string1;
    }
}
