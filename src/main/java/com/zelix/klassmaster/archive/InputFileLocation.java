package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.util.MutableInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InputFileLocation {
    public Path path;
    public ZipEntry zipEntry;
    public String zipFileName;
    public ZipFile zipFile;
    public String expectedFinalSha256;
    public SourceArchive sourceArchive;
    public ZkmClasspath classpath;
    public File file;
    public int locationType;
    public String name;

    public String getExpectedFinalSha256() {
        return this.expectedFinalSha256;
    }

    public SourceArchive getSourceArchive() {
        return this.sourceArchive;
    }

    private InputFileLocation(StringBuilder stringBuilder) {
        this.file = null;
        this.locationType = 4;
        this.name = stringBuilder.toString();
    }

    public InputFileLocation(String string) {
        this.file = new File(string);
        this.locationType = 1;
        this.name = this.file.getAbsolutePath();
    }

    public InputFileLocation(ZipFile zipFile1, ZipEntry zipEntry1, SourceArchive sourceArchive1) {
        this.zipFile = zipFile1;
        this.zipFileName = zipFile1.getName();
        this.zipEntry = zipEntry1;
        this.locationType = 2;
        this.name = zipEntry1.getName();
        this.sourceArchive = sourceArchive1;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof InputFileLocation ? this.getLocationKey().equals(((InputFileLocation) object).getLocationKey()) : false;
    }

    public InputFileLocation(String string, File file1) {
        this.file = file1;
        this.locationType = 1;
        this.name = file1.getAbsolutePath();
        if (string != null) {
            this.expectedFinalSha256 = string.toLowerCase();
        }
    }

    public boolean isPlainFile() {
        return this.locationType == 1;
    }

    public InputStream openInputStream(MutableInt mutableInt) throws IOException {
        switch (this.locationType) {
            case 1:
                FileInputStream fileInputStream = new FileInputStream(this.name);
                if (mutableInt != null) {
                    mutableInt.setValue(fileInputStream.available());
                }

                return fileInputStream;
            case 2:
                if (this.zipFile == null) {
                    this.zipFile = new ArchiveZipFile(this.zipFileName);
                }

                if (this.zipEntry == null) {
                    this.zipEntry = this.zipFile.getEntry(this.name);
                }

                InputStream inputStream1 = this.zipFile.getInputStream(this.zipEntry);
                if (mutableInt != null) {
                    mutableInt.setValue((int) this.zipEntry.getSize());
                }

                return inputStream1;
            default:
                throw new RuntimeException("Bad type=" + this.locationType + " in " + this.getClass().getName());
        }
    }

    public String getName() {
        return this.name;
    }

    public InputFileLocation(File file1) {
        this(null, file1);
    }

    public InputFileLocation(ZipFile zipFile1, ZipEntry zipEntry1) {
        this(zipFile1, zipEntry1, null);
    }

    public String getQualifiedName() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = this.getArchivePath();
        if (string != null) {
            stringBuilder.append(string);
            stringBuilder.append("!");
        }

        stringBuilder.append(this.getName());
        return stringBuilder.toString();
    }

    public File getFile() {
        switch (this.locationType) {
            case 1:
                if (this.file == null) {
                    this.file = new File(this.name);
                }

                return this.file;
            case 2:
                return null;
            default:
                throw new RuntimeException("Bad type=" + this.locationType + " in " + this.getClass().getName());
        }
    }

    public String getArchivePath() {
        return this.sourceArchive != null ? this.sourceArchive.getQualifiedPath() : this.zipFileName;
    }

    public boolean isArchiveEntry() {
        return this.locationType == 2;
    }

    public String getLocationKey() {
        if (this.locationType == 1) {
            return this.name;
        } else {
            return this.locationType == 3 ? this.path.toString() : this.getArchivePath() + '!' + this.name;
        }
    }

    @Override
    public int hashCode() {
        return this.getLocationKey().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public static InputFileLocation createNamedPlaceholder(String string) {
        return new InputFileLocation(new StringBuilder(string));
    }

    public InputFileLocation(Path path1, ZkmClasspath zkmClasspath) {
        this.path = path1.toAbsolutePath();
        this.classpath = zkmClasspath;
        this.locationType = 3;
        this.name = path1.toString();
    }

    public void releaseResources() {
        this.file = null;
        this.zipEntry = null;
        if (this.zipFile != null) {
            try {
                this.zipFile.close();
            } catch (IOException iOException) {
                this.zipFile = null;
                return;
            }

            this.zipFile = null;
        }
    }
}
