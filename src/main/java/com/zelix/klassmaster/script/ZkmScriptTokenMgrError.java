



package com.zelix.klassmaster.script;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.script.parser.ZkmScriptConstants;

public class ZkmScriptTokenMgrError extends Error implements ZkmScriptConstants {
    public static final String LINE_SEPARATOR;
    public int errorCode;

    public ZkmScriptTokenMgrError(final boolean b, final int n, final int i, final int j, final String s, final char k) {
        this(buildLexicalErrorMessage(b, n, i, j, s, k));
    }

    public ZkmScriptTokenMgrError(final String message) {
        super(message);
        this.errorCode = 0;
    }

    static {
        LINE_SEPARATOR = HiddenOptionFlags.LINE_SEPARATOR;
    }

    public static String buildLexicalErrorMessage(final boolean b, final int n, final Integer n2, final Integer n3, final String s, final int n4) {
        String str = null;
        String str2 = null;
        switch (n) {
            case 0: {
                break;
            }
            case 1:
            case 2:
            case 3: {
                str = "comment";
                break;
            }
            case 4: {
                str = "string literal";
                String s2;
                if (n4 != 10) {
                    if (n4 != 13) {
                        break;
                    }
                    s2 = "String not terminated at the end of the line.";
                } else {
                    s2 = "String not terminated at the end of the line.";
                }
                str2 = s2;
                break;
            }
            default: {
                str = "unknown lexical state";
                break;
            }
        }
        return "Zelix KlassMaster encountered " + (b ? "unexpected <EOF> " : ("\"" + addEscapes(String.valueOf((char) n4)) + "\"" + " (ascii " + n4 + "),")) + ZkmScriptTokenMgrError.LINE_SEPARATOR + "\tat line " + (int) n2 + ", column " + (int) n3 + (b ? "" : (ZkmScriptTokenMgrError.LINE_SEPARATOR + "\tafter : \"" + addEscapes(s) + "\"")) + ZkmScriptTokenMgrError.LINE_SEPARATOR + "LEXICAL ERROR " + ((str == null) ? "" : ("while inside a " + str + ".")) + ((str2 == null) ? "" : (" " + str2));
    }

    public static final String addEscapes(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '\0': {
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                case '\"': {
                    sb.append("\\\"");
                    break;
                }
                case '\'': {
                    sb.append("\\'");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                default: {
                    final char char1;
                    if ((char1 = s.charAt(i)) < ' ' || char1 > '~') {
                        final String string = "0000" + Integer.toString(char1, 16);
                        sb.append("\\u" + string.substring(string.length() - 4, string.length()));
                        break;
                    }
                    sb.append(char1);
                    break;
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String getMessage() {
        switch (this.errorCode) {
            case 1:
            case 2:
            case 3: {
                return "Zelix KlassMaster internal parser error (" + this.errorCode + ")";
            }
            default: {
                return super.getMessage();
            }
        }
    }
}
