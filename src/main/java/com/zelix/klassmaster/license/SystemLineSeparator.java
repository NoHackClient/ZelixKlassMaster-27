package com.zelix.klassmaster.license;

public abstract class SystemLineSeparator {
    static {
        System.getProperty("line.separator", "\n");
    }
}
