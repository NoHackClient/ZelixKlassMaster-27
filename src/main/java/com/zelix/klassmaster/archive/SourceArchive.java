package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipException;

public class SourceArchive implements Comparable {
    public String moduleName;
    public ArchiveManifest manifest;
    public SourceArchive parentArchive;
    public Vector nestedArchives = new Vector();
    public String filePath;
    public String name;
    public long fileSize;
    private String qualifiedPath;
    public String expectedInitialSha256;
    public String expectedFinalSha256;

    public boolean isTopLevel() {
        return this.parentArchive == null;
    }

    public SourceArchive getRootArchive() {
        if (this.parentArchive != null && this.parentArchive.getParentArchive() != null) {
            return this.parentArchive.getRootArchive();
        } else {
            return this.parentArchive != null ? this.parentArchive : null;
        }
    }

    public void setModuleName(String string) {
        this.moduleName = string;
    }

    public String getDisplayPath() {
        int ba = this.qualifiedPath.indexOf("!");
        return ba == -1 ? this.getFileName() : new File(this.qualifiedPath.substring(0, ba)).getName() + this.qualifiedPath.substring(ba);
    }

    public SourceArchive(String string, long ba, String string1) {
        this(string, ba, string1, null, null);
    }

    public String getOriginalPath() {
        return this.isTopLevel() ? this.qualifiedPath : this.name;
    }

    public String getFileName() {
        return new File(this.name).getName();
    }

    public Enumeration getNestedArchives() {
        return this.nestedArchives.elements();
    }

    public String getQualifiedPath() {
        return this.qualifiedPath;
    }

    public void addNestedArchive(Object object) {
        this.nestedArchives.addElement(object);
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public SourceArchive(String string, long fileSize, String string1, String string2, String string3) {
        this.filePath = string;
        this.name = string1;
        this.fileSize = fileSize;
        this.qualifiedPath = string1;
        this.expectedInitialSha256 = string2;
        this.expectedFinalSha256 = string3;
    }

    public SourceArchive getParentArchive() {
        return this.parentArchive;
    }

    public String getNestedEntryPath(String string) {
        return this.qualifiedPath + "!" + string;
    }

    public boolean hasModuleName() {
        return this.moduleName != null;
    }

    public void setManifest(ArchiveManifest archiveManifest) {
        this.manifest = archiveManifest;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasMicroEditionConfiguration() {
        return this.manifest != null ? this.manifest.hasMicroEditionConfiguration() : false;
    }

    public boolean isCldc10() throws ZkmException {
        return this.manifest != null ? this.manifest.isCldc10() : false;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByQualifiedPath((SourceArchive) object);
    }

    public String getExpectedFinalSha256() {
        return this.expectedFinalSha256;
    }

    public ArchiveManifest getManifest() {
        return this.manifest;
    }

    public SourceArchive findNestedArchive(Object object) {
        if (this.nestedArchives != null) {
            Iterator iterator = this.nestedArchives.iterator();

            while (iterator.hasNext()) {
                SourceArchive sourceArchive2 = (SourceArchive) iterator.next();
                if (sourceArchive2.getName().equals(object)) {
                    return sourceArchive2;
                }
            }
        }

        return null;
    }

    public int compareByQualifiedPath(SourceArchive sourceArchive2) {
        return this.getQualifiedPath().compareToIgnoreCase(sourceArchive2.getQualifiedPath());
    }

    public String getFilePath() {
        return this.filePath == null ? this.name : this.filePath;
    }

    public SourceArchive(String string, long fileSize, String string1, SourceArchive sourceArchive2) throws ZipException {
        this.filePath = string;
        this.name = string1;
        this.parentArchive = sourceArchive2;
        if (sourceArchive2 != null) {
            this.qualifiedPath = sourceArchive2.getQualifiedPath() + "!" + string1;
            sourceArchive2.addNestedArchive(this);
        } else {
            this.qualifiedPath = string1;
        }

        this.fileSize = fileSize;
    }
}
