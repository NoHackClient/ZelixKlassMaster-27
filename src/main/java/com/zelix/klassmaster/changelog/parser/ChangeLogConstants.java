package com.zelix.klassmaster.changelog.parser;

public interface ChangeLogConstants {
    int EOF = 0;
    int SPACE = 1;
    int TAB = 2;
    int LF = 3;
    int CR = 4;
    int FF = 5;
    int SLASH_SLASH = 6;
    int TOKEN_7 = 7;
    int COMMENT_START = 8;
    int SINGLE_LINE_COMMENT = 9;
    int COMMENT_END = 10;
    int COMMENT_END_11 = 11;
    int TOKEN_12 = 12;
    int BOM = 13;
    int LPAREN = 14;
    int COLON = 15;
    int DOUBLE_COLON = 16;
    int RPAREN = 17;
    int LBRACKET = 18;
    int RBRACKET = 19;
    int COMMA = 20;
    int SLASH = 21;
    int DOT = 22;
    int STAR = 23;
    int ARROW_EQ = 24;
    int AND = 25;
    int ENUM = 26;
    int DATA = 29;
    int FINAL = 30;
    int FALSE = 31;
    int LIGHT = 32;
    int INIT = 34;
    int NORMAL = 36;
    int BRIDGE = 37;
    int PUBLIC = 38;
    int STATIC = 39;
    int NATIVE = 40;
    int MODULE = 41;
    int EXECUTE = 42;
    int EXCLUDE = 43;
    int SOURCE = 44;
    int CLINIT = 45;
    int PACKAGE = 46;
    int PRIVATE = 47;
    int ENHANCED = 48;
    int ABSTRACT = 49;
    int VOLATILE = 50;
    int PROTECTED = 51;
    int SYNTHETIC = 52;
    int INTERFACE = 53;
    int TRANSIENT = 54;
    int FIELDS_OF = 55;
    int METHODS_OF = 56;
    int ANNOTATION = 58;
    int PACKAGE_INFO = 59;
    int MEMBER_CLASS = 60;
    int SYNCHRONIZED = 61;
    int FORWARD_CLASS = 62;
    int MANUFACTURED = 63;
    int NAME_NOT_CHANGED = 64;
    int LINE_NUMBERS_OF = 65;
    int TRACE_BACK_CLASS = 66;
    int SIGNATURE_NOT_CHANGED = 67;
    int SIGNATURE_NOT_CHANGED_68 = 68;
    int AUTO_REFLECTION_CLASS = 69;
    int OBFUSCATE_REFERENCES_CLASS = 70;
    int METHOD_PARAMETER_CHANGE_CLASSES = 71;
    int QUOTE = 72;
    int QUOTE_73 = 73;
    int TOKEN_74 = 74;
    int INTEGER_LITERAL = 75;
    int ALPHANUMERIC_LITERAL = 76;
    int NAME = 77;
    String[] TOKEN_IMAGE = new String[]{
            "<EOF>",
            "\" \"",
            "\"\\t\"",
            "\"\\n\"",
            "\"\\r\"",
            "\"\\f\"",
            "\"//\"",
            "<token of kind 7>",
            "\"/*\"",
            "<SINGLE_LINE_COMMENT>",
            "\"*/\"",
            "\"*/\"",
            "<token of kind 12>",
            "\"\\ufeff\"",
            "\"(\"",
            "\":\"",
            "\"::\"",
            "\")\"",
            "\"[\"",
            "\"]\"",
            "\",\"",
            "\"/\"",
            "\".\"",
            "\"*\"",
            "\"=>\"",
            "\"and\"",
            "\"enum\"",
            "\"none\"",
            "\"true\"",
            "\"Data:\"",
            "\"final\"",
            "\"false\"",
            "\"light\"",
            "\"heavy\"",
            "\"<init>\"",
            "\"Class:\"",
            "\"normal\"",
            "\"bridge\"",
            "\"public\"",
            "\"static\"",
            "\"native\"",
            "\"Module:\"",
            "\"execute\"",
            "\"exclude\"",
            "\"Source:\"",
            "\"<clinit>\"",
            "\"Package:\"",
            "\"private\"",
            "\"enhanced\"",
            "\"abstract\"",
            "\"volatile\"",
            "\"protected\"",
            "\"synthetic\"",
            "\"interface\"",
            "\"transient\"",
            "\"FieldsOf:\"",
            "\"MethodsOf:\"",
            "\"aggressive\"",
            "\"annotation\"",
            "\"package-info\"",
            "\"MemberClass:\"",
            "\"synchronized\"",
            "\"ForwardClass:\"",
            "\"Manufactured:\"",
            "\"NameNotChanged\"",
            "\"LineNumbersOf:\"",
            "\"TraceBackClass:\"",
            "\"SignatureNotChanged\"",
            "\"SignatureNotChanged:\"",
            "\"AutoReflectionClass:\"",
            "\"ObfuscateReferencesClass:\"",
            "\"MethodParameterChangeClasses:\"",
            "\"\\\"\"",
            "\"\\\"\"",
            "<token of kind 74>",
            "<INTEGER_LITERAL>",
            "<ALPHANUMERIC_LITERAL>",
            "<NAME>"
    };
}
