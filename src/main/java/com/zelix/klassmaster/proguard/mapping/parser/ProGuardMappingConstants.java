package com.zelix.klassmaster.proguard.mapping.parser;

public interface ProGuardMappingConstants {
    int EOF = 0;
    int SPACE = 1;
    int TAB = 2;
    int FF = 3;
    int SLASH_SLASH = 4;
    int TOKEN_5 = 5;
    int COMMENT_START = 6;
    int SINGLE_LINE_COMMENT = 7;
    int COMMENT_END = 8;
    int COMMENT_END_9 = 9;
    int TOKEN_10 = 10;
    int BOM = 11;
    int LF = 12;
    int CR = 13;
    int LPAREN = 14;
    int RPAREN = 15;
    int LBRACKET = 16;
    int RBRACKET = 17;
    int COMMA = 18;
    int SLASH = 19;
    int DOT = 20;
    int COLON = 21;
    int ARROW = 22;
    int INIT = 23;
    int CLINIT = 24;
    int PACKAGE_INFO = 25;
    int QUOTE = 26;
    int QUOTE_27 = 27;
    int TOKEN_28 = 28;
    int INTEGER_LITERAL = 29;
    int NAME = 30;
    String[] TOKEN_IMAGE = new String[]{
            "<EOF>",
            "\" \"",
            "\"\\t\"",
            "\"\\f\"",
            "\"//\"",
            "<token of kind 5>",
            "\"/*\"",
            "<SINGLE_LINE_COMMENT>",
            "\"*/\"",
            "\"*/\"",
            "<token of kind 10>",
            "\"\\ufeff\"",
            "\"\\n\"",
            "\"\\r\"",
            "\"(\"",
            "\")\"",
            "\"[\"",
            "\"]\"",
            "\",\"",
            "\"/\"",
            "\".\"",
            "\":\"",
            "\"->\"",
            "\"<init>\"",
            "\"<clinit>\"",
            "\"package-info\"",
            "\"\\\"\"",
            "\"\\\"\"",
            "<token of kind 28>",
            "<INTEGER_LITERAL>",
            "<NAME>"
    };
}
