package com.zelix.klassmaster.proguard.mapping.parser;

public class ProGuardMappingTokenMgrError extends Error {
    public int errorCode;

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public ProGuardMappingTokenMgrError(String string, int errorCode) {
        super(string);
        this.errorCode = errorCode;
    }

    public static String LexicalError(boolean bl, int ba, int bb, String string, int bc) {
        return "Lexical error at line "
                + ba
                + ", column "
                + bb
                + ".  Encountered: "
                + (bl ? "<EOF> " : "\"" + addEscapes(String.valueOf((char) bc)) + "\"" + " (" + bc + "), ")
                + "after : \""
                + addEscapes(string)
                + "\"";
    }

    public ProGuardMappingTokenMgrError(boolean bl, int ba, int bb, String string, char bc) {
        this(LexicalError(bl, ba, bb, string, bc), 0);
    }

    public static final String addEscapes(String string) {
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
}
