package com.zelix.klassmaster.proguard.mapping.parser;

public class ProGuardMappingParseException extends Exception {
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    public ProGuardMappingToken currentToken;
    public String lineSeparator = System.getProperty("line.separator", "\n");

    public static String add_escapes(String string) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {
            switch (string.charAt(i)) {
                case '\u0000':
                    break;
                case '\b':
                    stringBuffer.append("\\b");
                    break;
                case '\t':
                    stringBuffer.append("\\t");
                    break;
                case '\n':
                    stringBuffer.append("\\n");
                    break;
                case '\f':
                    stringBuffer.append("\\f");
                    break;
                case '\r':
                    stringBuffer.append("\\r");
                    break;
                case '"':
                    stringBuffer.append("\\\"");
                    break;
                case '\'':
                    stringBuffer.append("\\'");
                    break;
                case '\\':
                    stringBuffer.append("\\\\");
                    break;
                default:
                    char ba;
                    if ((ba = string.charAt(i)) >= ' ' && ba <= '~') {
                        stringBuffer.append(ba);
                    } else {
                        String string1 = "0000" + Integer.toString(ba, 16);
                        stringBuffer.append("\\u" + string1.substring(string1.length() - 4, string1.length()));
                    }
            }
        }

        return stringBuffer.toString();
    }

    public ProGuardMappingParseException() {
    }

    public ProGuardMappingParseException(ProGuardMappingToken proGuardMappingToken, int[][] expectedTokenSequences, String[] strings) {
        super(initialise(proGuardMappingToken, expectedTokenSequences, strings));
        this.currentToken = proGuardMappingToken;
        this.expectedTokenSequences = expectedTokenSequences;
        this.tokenImage = strings;
    }

    private static String initialise(ProGuardMappingToken proGuardMappingToken, int[][] ba, String[] strings) {
        String string = System.getProperty("line.separator", "\n");
        StringBuffer stringBuffer = new StringBuffer();
        int bb = 0;

        for (int i = 0; i < ba.length; i++) {
            if (bb < ba[i].length) {
                bb = ba[i].length;
            }

            for (int j = 0; j < ba[i].length; j++) {
                stringBuffer.append(strings[ba[i][j]]).append(' ');
            }

            if (ba[i][ba[i].length - 1] != 0) {
                stringBuffer.append("...");
            }

            stringBuffer.append(string).append("    ");
        }

        String string1 = "Encountered \"";
        ProGuardMappingToken proGuardMappingToken1 = proGuardMappingToken.U;

        for (int i = 0; i < bb; i++) {
            if (i != 0) {
                string1 = string1 + " ";
            }

            if (proGuardMappingToken1.m == 0) {
                string1 = string1 + strings[0];
                break;
            }

            string1 = string1 + " " + strings[proGuardMappingToken1.m];
            string1 = string1 + " \"";
            string1 = string1 + add_escapes(proGuardMappingToken1.L);
            string1 = string1 + " \"";
            proGuardMappingToken1 = proGuardMappingToken1.U;
        }

        string1 = string1 + "\" at line " + proGuardMappingToken.U.b + ", column " + proGuardMappingToken.U.V;
        string1 = string1 + "." + string;
        if (ba.length == 1) {
            string1 = string1 + "Was expecting:" + string + "    ";
        } else {
            string1 = string1 + "Was expecting one of:" + string + "    ";
        }

        return string1 + stringBuffer.toString();
    }
}
