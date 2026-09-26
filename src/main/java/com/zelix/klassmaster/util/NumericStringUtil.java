package com.zelix.klassmaster.util;

public class NumericStringUtil {
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    private NumericStringUtil() {
    }
}
