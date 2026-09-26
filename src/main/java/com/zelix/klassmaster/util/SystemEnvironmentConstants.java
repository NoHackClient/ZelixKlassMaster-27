package com.zelix.klassmaster.util;

import java.io.File;

public class SystemEnvironmentConstants {
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final char FILE_SEPARATOR_CHAR = FILE_SEPARATOR.charAt(0);
    public static final String PATH_SEPARATOR_String = System.getProperty("path.separator");
    public static final char PATH_SEPARATOR_CHAR = PATH_SEPARATOR_String.charAt(0);
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public static final String JAVA_VM_VENDOR;
    public static final String JAVA_VM_VERSION;
    public static final String JAVA_HOME;
    public static final String BOOT_CLASS_PATH;
    public static final String OS_NAME;
    public static final String OS_VERSION;


    static {
        new File(USER_DIR);
        JAVA_VM_VENDOR = System.getProperty("java.vm.vendor", "Sun");
        JAVA_VM_VERSION = System.getProperty("java.vm.version");
        JAVA_HOME = System.getProperty("java.home", USER_DIR);
        BOOT_CLASS_PATH = System.getProperty("sun.boot.class.path");
        OS_NAME = System.getProperty("os.name");
        OS_VERSION = System.getProperty("os.version");
    }


    protected SystemEnvironmentConstants() {
    }
}
