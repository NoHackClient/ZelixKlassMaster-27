package com.zelix.klassmaster.script.parser;

import com.zelix.klassmaster.config.HiddenOptionFlags;

public class ZkmScriptParseException extends Exception implements ZkmScriptConstants {
    public String lineSeparator = HiddenOptionFlags.LINE_SEPARATOR;
    public boolean specialConstructor;
    public ZkmScriptToken currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    public int statementKind;

    public ZkmScriptParseException(ZkmScriptToken zkmScriptToken, int[][] expectedTokenSequences, String[] strings, int statementKind) {
        super("");
        this.specialConstructor = true;
        this.currentToken = zkmScriptToken;
        this.expectedTokenSequences = expectedTokenSequences;
        this.tokenImage = strings;
        this.statementKind = statementKind;
    }

    public String addEscapes(String string) {
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

    public ZkmScriptParseException() {
        this.specialConstructor = false;
    }

    @Override
    public String getMessage() {
        if (!this.specialConstructor) {
            return super.getMessage();
        }

        String string = "";
        int ba = 0;
        boolean bl = this.statementKind == 64
                || this.statementKind == 79
                || this.statementKind == 144
                || this.statementKind == 154
                || this.statementKind == 162
                || this.statementKind == 170
                || this.statementKind == 169
                || this.statementKind == 98
                || this.statementKind == 86
                || this.statementKind == 94
                || this.statementKind == 100
                || this.statementKind == 146
                || this.statementKind == 182
                || this.statementKind == 165
                || this.statementKind == 166
                || this.statementKind == 192
                || this.statementKind == 193
                || this.statementKind == 161
                || this.statementKind == 180
                || this.statementKind == 181;
        int bb = 0;
        int bf = 0;

        for (int[][] expectedTokenSequences = this.expectedTokenSequences; bf < expectedTokenSequences.length; expectedTokenSequences = this.expectedTokenSequences) {
            label134:
            {
                if (bl) {
                    switch (this.expectedTokenSequences[bb][0]) {
                        case 37:
                        case 39:
                        case 41:
                        case 42:
                        case 43:
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                        case 49:
                        case 56:
                        case 60:
                        case 61:
                        case 64:
                        case 69:
                        case 71:
                        case 80:
                        case 81:
                        case 86:
                        case 90:
                            break label134;
                        case 38:
                        case 40:
                        case 44:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 57:
                        case 58:
                        case 59:
                        case 62:
                        case 63:
                        case 65:
                        case 66:
                        case 67:
                        case 68:
                        case 70:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 76:
                        case 77:
                        case 78:
                        case 79:
                        case 82:
                        case 83:
                        case 84:
                        case 85:
                        case 87:
                        case 88:
                        case 89:
                    }
                }

                if (ba < this.expectedTokenSequences[bb].length) {
                    ba = this.expectedTokenSequences[bb].length;
                }

                int bc = 0;
                bf = bc;

                for (int[][] be = this.expectedTokenSequences; bf < be[bb].length; be = this.expectedTokenSequences) {
                    string = string + this.tokenImage[this.expectedTokenSequences[bb][bc]] + " ";
                    bf = ++bc;
                }

                if (this.expectedTokenSequences[bb][this.expectedTokenSequences[bb].length - 1] != 0) {
                    string = string + "...";
                }

                string = string + this.lineSeparator + "    ";
            }

            bf = ++bb;
        }

        String string3 = this.currentToken.image;
        ZkmScriptToken zkmScriptToken = this.currentToken.next;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("While parsing " + this.tokenImage[this.statementKind] + " statement");
        String string1 = this.currentToken.getParameterDescription();
        String string4;
        if (string1.length() > 0) {
            stringBuilder.append(string1);
            string4 = "Zelix KlassMaster encountered \"";
        } else {
            string4 = "Zelix KlassMaster encountered \"";
        }

        String string2 = string4;

        for (int i = 0; i < ba; i++) {
            if (i != 0) {
                string2 = string2 + " ";
            }

            if (zkmScriptToken.kind == 0) {
                string2 = string2 + this.tokenImage[0];
                break;
            }

            string2 = string2 + this.addEscapes(zkmScriptToken.image);
            zkmScriptToken = zkmScriptToken.next;
        }

        string2 = string2
                + "\" "
                + this.lineSeparator
                + (string3 != null && string3.length() > 0 ? "\tafter \"" + string3 + "\"" + this.lineSeparator : "")
                + "\tat line "
                + this.currentToken.next.endLine
                + ", column "
                + this.currentToken.next.endColumn
                + "."
                + this.lineSeparator;
        if (this.expectedTokenSequences.length == 1) {
            string2 = string2 + "Was expecting:" + this.lineSeparator + "    ";
        } else {
            string2 = string2 + "Was expecting one of:" + this.lineSeparator + "    ";
        }

        string2 = string2 + string;
        if (stringBuilder.length() > 0) {
            string2 = string2 + this.lineSeparator + stringBuilder.toString();
        }

        return string2;
    }
}
