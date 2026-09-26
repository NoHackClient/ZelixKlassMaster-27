package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.AssertionFailedException;

import java.io.PrintWriter;
import java.util.LinkedHashMap;

public class ZkmAssert {
    public static Runtime runtime;
    public static String lineSeparator;

    public static void assertNull(Object object, String string) {
        if (object != null) {
            throw new AssertionFailedException("Assert Failed: " + string);
        }
    }

    public static void assertNotNullMessage(Object object, String[] strings) {
        if (object == null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (String string : strings) {
                stringBuilder.append(string.toString());
            }

            throw new AssertionFailedException("Assert Failed: " + stringBuilder.toString());
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static String formatArray(Object[] objects) {
        return formatArray(objects, true);
    }

    public static void assertTrue(boolean bl, String[] strings) {
        if (!bl) {
            if (strings.length == 1) {
                throw new AssertionFailedException("Assert Failed: " + strings[0]);
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('\'');

            for (int i = 0; i < strings.length; i++) {
                String string = strings[i];
                stringBuilder.append(string);
                if (i < strings.length - 2) {
                    stringBuilder.append("', '");
                } else if (i == strings.length - 2) {
                    stringBuilder.append("' and '");
                } else {
                    stringBuilder.append('\'');
                }
            }

            throw new AssertionFailedException("Assert Failed: " + stringBuilder);
        }
    }

    public static void assertNotNullArgs(Object object, Object[] objects) {
        if (object == null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (Object object1 : objects) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append(object1.toString());
            }

            throw new AssertionFailedException("Assert Failed: " + stringBuilder.toString());
        }
    }

    public static void assertNotNull(Object object, String string) {
        if (object == null) {
            throw new AssertionFailedException("Assert Not Null Failed: " + string);
        }
    }

    public static void noOp() {
    }

    public static void assertTrueWithCode(Integer integer) {
        throw new AssertionFailedException("Assert Failed: " + String.valueOf(integer.intValue()));
    }

    public static String formatArray(Object[] objects, boolean bl) {
        if (objects != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");

            for (int i = 0; i < objects.length; i++) {
                stringBuilder.append("#" + i + ": " + objects[i] + (i < objects.length - 1 ? "," + (bl ? lineSeparator : "") : ""));
            }

            return stringBuilder.toString() + "}";
        } else {
            return "{null array}";
        }
    }

    public static String getSimpleClassName(Object object) {
        if (object == null) {
            return "null";
        }

        String string = object.getClass().getName();
        string = string.replace('/', '.');
        int ba = string.lastIndexOf(".");
        return ba > -1 ? string.substring(ba + 1) : string;
    }

    private static void staticInit() {
        runtime = Runtime.getRuntime();
        System.currentTimeMillis();
        lineSeparator = ZkmUtils.LINE_SEPARATOR;
        new LinkedHashMap();
        runtime.totalMemory();
        runtime.freeMemory();
        new PrintWriter(System.err, true);
        new PrintWriter(System.out, true);
    }

    private ZkmAssert() {
    }
}
