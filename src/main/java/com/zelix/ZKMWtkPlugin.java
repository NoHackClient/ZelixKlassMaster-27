package com.zelix;

import com.sun.kvem.environment.Obfuscator;
import com.zelix.klassmaster.engine.WtkPluginConstants;
import com.zelix.klassmaster.engine.ZkmApiBase;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.File;

public class ZKMWtkPlugin extends WtkPluginConstants implements Obfuscator {
    public File jadFile;

    public void run(File file1, String string3, String string4, String string, String string1, String string2, String string5) {
        try {
            ZkmApiBase.run(this.jadFile, string, file1, string2, string1);
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public void createScriptFile(File file1, File file2) {
        this.jadFile = file1;
        if (this.jadFile == null) {
            throw new IllegalArgumentException("ZKM: Null JAD file.");
        }

        if (!this.jadFile.exists()) {
            throw new IllegalArgumentException("ZKM: JAD file '" + this.jadFile.getAbsolutePath() + "' does not exist.");
        }
    }
}
