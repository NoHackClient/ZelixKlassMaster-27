package com.zelix.klassmaster.util;

public class JavaRuntimeVersion {
    public static final String JAVA_CLASS_VERSION = System.getProperty("java.class.version");
    public static final int RUNTIME_CLASS_MAJOR_VERSION = Integer.parseInt(JAVA_CLASS_VERSION.split("\\.")[0]);

    public static boolean isRuntimeAtLeastJava8() {
        return RUNTIME_CLASS_MAJOR_VERSION >= 52;
    }

    public static boolean isAtLeastJava7(int ba) {
        return ba >= 51;
    }

    public static boolean isAtLeastJava6(int ba) {
        return ba >= 50;
    }

    public static int getRuntimeClassMajorVersion() {
        return RUNTIME_CLASS_MAJOR_VERSION;
    }

    public static boolean isAtLeastJava9(int ba) {
        return ba >= 53;
    }

    public static boolean isAtLeastJava19(int ba) {
        return ba >= 63;
    }

    public static boolean isAtLeastJava4(int ba) {
        return ba >= 48;
    }

    public static boolean isAtLeastJava8(int ba) {
        return ba >= 52;
    }

    public static boolean isAtLeastJava5(int ba) {
        return ba >= 49;
    }

    public static int toClassMajorVersion(int ba) {
        if (ba < 1) {
            throw new IllegalArgumentException("Invalid Java runtime version '" + ba + "'");
        }

        switch (ba) {
            case 1:
                return 45;
            case 2:
                return 46;
            case 3:
                return 47;
            case 4:
                return 48;
            case 5:
                return 49;
            case 6:
                return 50;
            case 7:
                return 51;
            case 8:
                return 52;
            case 9:
                return 53;
            case 10:
                return 54;
            case 11:
                return 55;
            case 12:
                return 56;
            case 13:
                return 57;
            case 14:
                return 58;
            case 15:
                return 59;
            case 16:
                return 60;
            case 17:
                return 61;
            case 18:
                return 62;
            case 19:
                return 63;
            case 20:
                return 64;
            case 21:
                return 65;
            case 22:
                return 66;
            case 23:
                return 67;
            case 24:
                return 68;
            case 25:
                return 69;
            case 26:
                return 70;
            case 27:
                return 71;
            default:
                return 70;
        }
    }

    public static boolean isRuntimeAtLeastJava19() {
        return RUNTIME_CLASS_MAJOR_VERSION >= 63;
    }

    public static boolean isRuntimeAtLeastJava9() {
        return RUNTIME_CLASS_MAJOR_VERSION >= 53;
    }

    private JavaRuntimeVersion() {
    }
}
