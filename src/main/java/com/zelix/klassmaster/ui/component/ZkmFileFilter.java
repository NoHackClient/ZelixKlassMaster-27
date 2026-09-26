package com.zelix.klassmaster.ui.component;

import java.io.File;
import java.io.FileFilter;

public abstract class ZkmFileFilter implements FileFilter {
    private static boolean initFlag;

    @Override
    public String toString() {
        return this.getDescription();
    }

    public static boolean isInitialized() {
        return true;
    }

    @Override
    public abstract boolean accept(File file1);

    public static boolean getInitFlag() {
        return initFlag;
    }

    public static void setInitFlag() {
        initFlag = true;
    }

    public abstract String getDescription();

    static {
        if (!isInitialized()) {
            setInitFlag();
        }
    }
}
