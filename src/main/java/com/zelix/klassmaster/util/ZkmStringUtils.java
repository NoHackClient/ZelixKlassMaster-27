package com.zelix.klassmaster.util;

import java.io.StringWriter;
import java.util.StringTokenizer;

public class ZkmStringUtils {
    public static String replaceAll(String string, String string1, String string2) {
        if (string == null) {
            throw new IllegalArgumentException("Null String");
        }

        int ba = string.length();
        StringWriter stringWriter = new StringWriter(ba);
        char[] bb = string.toCharArray();
        int bc = string1.length();
        int bd = 0;

        int be;
        while (bd < ba && (be = string.indexOf(string1, bd)) > -1) {
            stringWriter.write(bb, bd, be - bd);
            stringWriter.write(string2);
            bd = be + bc;
        }

        stringWriter.write(bb, bd, ba - bd);
        return stringWriter.toString();
    }

    public static boolean matchesWildcard(String string, String string1) {
        if (!string1.equals("*") && !string.equals(string1)) {
            if (string.indexOf("*") == -1 && string1.indexOf("*") == -1) {
                return string.equals(string1);
            }

            StringTokenizer stringTokenizer = new StringTokenizer(string1, "*", true);
            String[] strings = new String[stringTokenizer.countTokens()];
            int ba = 0;

            while (stringTokenizer.hasMoreTokens()) {
                strings[ba++] = stringTokenizer.nextToken();
            }

            return matchWildcardTokens(string, 0, strings, 0);
        } else {
            return true;
        }
    }

    public static String repeatChar(int ba, Integer integer) {
        StringBuilder stringBuilder = new StringBuilder(ba);

        for (int i = 0; i < ba; i++) {
            stringBuilder.append((char) integer.intValue());
        }

        return stringBuilder.toString();
    }

    private static boolean matchWildcardTokens(String string, int ba, String[] strings, int bb) {
        if (ba >= string.length()) {
            if (bb >= strings.length) {
                return true;
            }

            while (bb < strings.length) {
                if (!strings[bb].equals("*")) {
                    return false;
                }

                bb++;
            }

            return true;
        } else {
            if (bb >= strings.length) {
                return false;
            }

            String string1 = strings[bb];
            if (!string1.equals("*")) {
                boolean bl = string.regionMatches(ba, string1, 0, string1.length());
                if (!bl) {
                    return false;
                }

                ba += string1.length();
                return matchWildcardTokens(string, ba, strings, bb + 1);
            } else {
                while (string1.equals("*")) {
                    if (++bb >= strings.length) {
                        break;
                    }

                    string1 = strings[bb];
                }

                if (string1.equals("*")) {
                    return true;
                }

                while (ba < string.length()) {
                    int bc = string.indexOf(string1, ba);
                    if (bc == -1) {
                        return false;
                    }

                    ba = bc;
                    if (matchWildcardTokens(string, ba + string1.length(), strings, bb + 1)) {
                        return true;
                    }

                    ba++;
                }

                return false;
            }
        }
    }

    public static int countOccurrences(String string, String string1) {
        if (string != null && string.length() > 0 && string1 != null && string1.length() > 0) {
            int ba = string1.length();
            int bb = -1;
            int bc = 0 - ba;

            do {
                bc += ba;
                bc = string.indexOf(string1, bc);
                bb++;
            } while (bc != -1);

            return bb;
        } else {
            return 0;
        }
    }

    public static int skipWhitespace(String string, int ba) {
        int bb = string.length();
        if (bb == 0) {
            return -1;
        }

        int bc = ba;
        char bd = string.charAt(bc);
        boolean bl = Character.isWhitespace(bd);

        while (bl) {
            if (++bc == bb) {
                return -1;
            }

            bd = string.charAt(bc);
            bl = Character.isWhitespace(bd);
        }

        return bc;
    }

    public static String escapeJavaString(String string) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char ba = string.charAt(i);
            switch (ba) {
                case '\u0000':
                    break;
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '"':
                    stringBuilder.append("\\\"");
                    break;
                case '\'':
                    stringBuilder.append("\\'");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                default:
                    if (ba >= ' ' && ba <= '~') {
                        stringBuilder.append(ba);
                    } else {
                        stringBuilder.append("\\u" + pad(Integer.toHexString(ba), 82, 4, 48));
                    }
            }
        }

        return stringBuilder.toString();
    }

    public static int indexOfAny(String string, int ba, char[] bb) {
        int bc = string.length();

        for (int i = ba; i < bc; i++) {
            char be = string.charAt(i);

            for (int j = 0; j < bb.length; j++) {
                if (be == bb[j]) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static String pad(String string, int ba, int bb, int bc) {
        int bd = string.length();
        StringBuilder stringBuilder;
        switch (ba) {
            case 76:
                stringBuilder = new StringBuilder(string);
                int bg = bb - bd;

                for (int i = 0; i < bg; i++) {
                    stringBuilder.append((char) bc);
                }
                break;
            case 82:
                stringBuilder = new StringBuilder();
                int be = bb - bd;

                for (int i = 0; i < be; i++) {
                    stringBuilder.append((char) bc);
                }

                stringBuilder.append(string);
                break;
            default:
                throw new IllegalArgumentException("Invalid justification");
        }

        return stringBuilder.toString().substring(0, bb);
    }

    public static int countChar(String string, char ba) {
        if (string != null && string.length() > 0) {
            char[] bb = string.toCharArray();
            int bc = 0;
            char[] bd = bb;
            int be = bd.length;

            for (int i = 0; i < be; i++) {
                if (bd[i] == ba) {
                    bc++;
                }
            }

            return bc;
        } else {
            return 0;
        }
    }

    private ZkmStringUtils() {
    }
}
