package com.zelix.klassmaster.archive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ZipOutputTarget {
    public File tempFile;
    public ZipOutputStream zipOutputStream;

    public String getTempFilePath() {
        return this.tempFile != null ? this.tempFile.getAbsolutePath() : null;
    }

    public boolean isNotOpened() {
        return this.zipOutputStream == null;
    }

    public void close() throws IOException {
        if (this.zipOutputStream != null) {
            this.zipOutputStream.close();
        }
    }

    public ZipOutputStream getOrCreateZipOutputStream() throws IOException {
        if (this.zipOutputStream == null) {
            this.tempFile = TempFileManager.createTempFile();
            this.zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.tempFile)));
        }

        return this.zipOutputStream;
    }
}
