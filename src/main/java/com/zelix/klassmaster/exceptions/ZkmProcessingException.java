package com.zelix.klassmaster.exceptions;

public class ZkmProcessingException extends ZkmException {
    private static String buildTag;

    public static void setBuildTag() {
        buildTag = "LblUN";
    }

    public ZkmProcessingException(String string) {
        super(string);
    }

    public static String getBuildTag() {
        return buildTag;
    }

    public ZkmProcessingException(String string, Throwable throwable) {
        super(string, throwable);
    }

    static {
        if (getBuildTag() != null) {
            setBuildTag();
        }
    }
}
