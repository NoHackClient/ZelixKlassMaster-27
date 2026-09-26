package com.zelix.klassmaster.changelog.parser;

import com.zelix.klassmaster.config.HiddenOptionFlags;

public class ChangeLogParseException extends Exception implements ChangeLogConstants {
    public ChangeLogToken currentToken;
    public String[] tokenImage;
    public int[][] expectedTokenSequences;
    public String eol = HiddenOptionFlags.LINE_SEPARATOR;
    public boolean specialConstructor;

    public ChangeLogParseException() {
        this.specialConstructor = false;
    }

    public ChangeLogParseException(ChangeLogToken changeLogToken, int[][] expectedTokenSequences, String[] strings) {
        super("");
        this.specialConstructor = true;
        this.currentToken = changeLogToken;
        this.expectedTokenSequences = expectedTokenSequences;
        this.tokenImage = strings;
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
                    stringBuffer.append("\"");
                    break;
                case '\'':
                    stringBuffer.append("'");
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

    @Override
    public String getMessage() {
        if (!this.specialConstructor) {
            return super.getMessage();
        }

        String string = "";
        int ba = 0;
        int bb = 0;
        int bf = 0;

        for (int[][] expectedTokenSequences = this.expectedTokenSequences; bf < expectedTokenSequences.length; expectedTokenSequences = this.expectedTokenSequences) {
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

            string = string + this.eol + "    ";
            bf = ++bb;
        }

        String string2 = this.currentToken.k;
        ChangeLogToken changeLogToken = this.currentToken.h;
        String string1 = "Zelix KlassMaster encountered \"";

        for (int i = 0; i < ba; i++) {
            if (i != 0) {
                string1 = string1 + " ";
            }

            if (changeLogToken.H == 0) {
                string1 = string1 + this.tokenImage[0];
                break;
            }

            string1 = string1 + this.addEscapes(changeLogToken.k);
            changeLogToken = changeLogToken.h;
        }

        string1 = string1
                + "\" "
                + this.eol
                + (string2 != null && string2.length() > 0 ? "\tafter \"" + string2 + "\"" + this.eol : "")
                + "\tat line "
                + this.currentToken.h.n
                + ", column "
                + this.currentToken.h.D
                + "."
                + this.eol;
        if (this.expectedTokenSequences.length == 1) {
            string1 = string1 + "Was expecting:" + this.eol + "    ";
        } else {
            string1 = string1 + "Was expecting one of:" + this.eol + "    ";
        }

        return string1 + string;
    }
}
