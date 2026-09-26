package com.zelix.klassmaster.archive;

import java.util.zip.ZipException;

public interface ArchivePathFilter {
    AcceptAllFileFilter ACCEPT_ALL = new AcceptAllFileFilter();

    String toFilterExpression() throws ZipException;

    boolean acceptsPath(Object object);
}
