package com.zelix.klassmaster.proguard;

public class ProGuardWildcardConverter {
    public static String convertClassPattern(String string) {
        if (string.equals("***")) {
            return "*.*";
        }

        String string1 = string.replace('?', '*');
        string1 = string1.replace('%', '*');
        string1 = collapseWildcards(string1);
        return string1.equals("*") ? "*.*" : string1;
    }

    public static String convertPackagePattern(String string) {
        if (string.equals("***")) {
            return "*.";
        }

        String string1 = string.replace('?', '*');
        string1 = collapseWildcards(string1);
        if (string1.equals("*")) {
            return "*.";
        }

        if (string1.charAt(string1.length() - 1) != '.') {
            string1 = string1 + '.';
        }

        return string1;
    }

    public static String convertFieldPattern(String string) {
        return collapseWildcards(string.replace('?', '*'));
    }

    public static String quoteIfNeeded(String string) {
        String string1 = string;
        string1 = string1.trim();
        if (string1.indexOf(32) <= -1 && string1.length() != 0) {
            return string1;
        }

        StringBuilder stringBuilder = new StringBuilder(string1.length() + 2);
        stringBuilder.append('"');
        stringBuilder.append(string1);
        stringBuilder.append('"');
        return stringBuilder.toString();
    }

    public static String convertMethodPattern(String string) {
        return !string.equals("<init>") && !string.equals("***") && string.indexOf(46) <= -1 ? collapseWildcards(string.replace('?', '*')) : "<init>";
    }

    public static String collapseWildcards(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = false;
        char[] ba = string.toCharArray();

        for (int i = 0; i < ba.length; i++) {
            char bc = ba[i];
            if (bc == '*') {
                if (!bl) {
                    stringBuilder.append(bc);
                    bl = true;
                }
            } else if (bc == '.' && i > 0 && i == ba.length - 3 && ba[i + 1] == '*' && ba[i + 2] == '*') {
                stringBuilder.append('*');
                stringBuilder.append(bc);
            } else {
                stringBuilder.append(bc);
                bl = false;
            }
        }

        return stringBuilder.toString();
    }

    public static String convertParameterPattern(String string) {
        if (string.equals("...")) {
            return "*";
        } else if (string.equals("***")) {
            return "?";
        } else if (string.indexOf("%") > -1) {
            return "?";
        } else {
            return string.indexOf(60) > -1 ? "?" : collapseWildcards(string.replace('?', '*'));
        }
    }

    private ProGuardWildcardConverter() {
    }
}
