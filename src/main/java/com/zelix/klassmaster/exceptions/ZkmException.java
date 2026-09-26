package com.zelix.klassmaster.exceptions;

public class ZkmException extends Exception {
    private static String buildTag;

    public ZkmException(String string) {
        super(string);
    }

    public static String getBuildTag_String() {
        return buildTag;
    }

    public ZkmException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public static void setBuildTag_v() {
        buildTag = "f18Lpc";
    }

    static {
        if (getBuildTag_String() == null) {
            setBuildTag_v();
        }
    }
}
