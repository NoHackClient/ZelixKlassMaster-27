package com.zelix.klassmaster.archive;

import java.util.zip.ZipException;

public class AcceptAllFileFilter implements ArchivePathFilter {
    @Override
    public boolean acceptsPath(Object object) {
        return true;
    }

    @Override
    public String toFilterExpression() throws ZipException {
        return "";
    }
}
