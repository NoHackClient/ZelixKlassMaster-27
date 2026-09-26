package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class ArchiveZipFile extends ZipFile {
    public ArchiveZipFile(File file1) throws IOException {
        super(file1);
    }

    public ArchiveZipFile(String string) throws IOException {
        super(string);
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
