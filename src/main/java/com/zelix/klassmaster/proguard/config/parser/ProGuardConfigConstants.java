package com.zelix.klassmaster.proguard.config.parser;

public interface ProGuardConfigConstants {
    int EOF = 0;
    int SPACE = 1;
    int TAB = 2;
    int LF = 3;
    int CR = 4;
    int FF = 5;
    int HASH = 6;
    int SINGLE_LINE_COMMENT = 7;
    int TOKEN_8 = 8;
    int DIGIT = 9;
    int BOM = 10;
    int TILDE = 11;
    int COMMA = 12;
    int SEMICOLON = 13;
    int AT = 14;
    int DOT = 15;
    int BANG = 16;
    int HOOK = 17;
    int PERCENT = 18;
    int STAR = 19;
    int LPAREN = 20;
    int RPAREN = 21;
    int LBRACE = 22;
    int RBRACE = 23;
    int DOUBLE_STAR = 24;
    int OPT_IF = 25;
    int TRIPLE_STAR = 26;
    int ELLIPSIS = 27;
    int ENUM = 28;
    int OPT_DUMP = 29;
    int OPT_KEEP = 30;
    int FINAL = 32;
    int PUBLIC = 33;
    int STATIC = 34;
    int NATIVE = 35;
    int INIT = 36;
    int BRIDGE = 37;
    int PRIVATE = 38;
    int EXTENDS = 39;
    int OPT_TARGET = 40;
    int OPT_INJARS = 41;
    int VOLATILE = 42;
    int STRICTFP = 43;
    int FIELDS = 44;
    int OPT_VERBOSE = 45;
    int OPT_INCLUDE = 46;
    int OPT_OUTJARS = 47;
    int PROTECTED = 48;
    int METHODS = 49;
    int ABSTRACT = 50;
    int OPT_ANDROID = 51;
    int TRANSIENT = 52;
    int INTERFACE = 53;
    int SYNTHETIC = 54;
    int OPT_KEEPCODE = 55;
    int OPT_ZIPALIGN = 56;
    int OPT_DONTNOTE = 57;
    int OPT_DONTWARN = 58;
    int INTERFACE_59 = 59;
    int IMPLEMENTS = 60;
    int OPT_KEEPNAMES = 61;
    int MODULE_INFO = 62;
    int INCLUDECODE = 63;
    int OPT_PRINTSEEDS = 64;
    int OPT_DONTSHRINK = 65;
    int OPT_PRINTUSAGE = 66;
    int OPT_LIBRARYJARS = 67;
    int SYNCHRONIZED = 68;
    int OPT_APPLYMAPPING = 69;
    int OPT_MICROEDITION = 70;
    int OPT_PRINTMAPPING = 71;
    int OPT_ASSUMEVALUES = 72;
    int OPT_DONTOPTIMIZE = 73;
    int OPT_DONTCOMPRESS = 74;
    int ALLOWSHRINKING = 75;
    int OPT_OPTIMIZATIONS = 76;
    int OPT_DONTPREVERIFY = 77;
    int OPT_BASEDIRECTORY = 78;
    int OPT_DONTOBFUSCATE = 79;
    int OPT_KEEPATTRIBUTES = 80;
    int OPT_IGNOREWARNINGS = 81;
    int ALLOWOBFUSCATION = 82;
    int OPT_KEEPDIRECTORIES = 83;
    int OPT_FORCEPROCESSING = 84;
    int ALLOWOPTIMIZATION = 85;
    int OPT_KEEPCLASSMEMBERS = 86;
    int OPT_KEEPPACKAGENAMES = 87;
    int OPT_REPACKAGECLASSES = 88;
    int OPT_WHYAREYOUKEEPING = 89;
    int OPT_ADAPTCLASSSTRINGS = 90;
    int OPT_KEEPKOTLINMETADATA = 91;
    int OPT_PRINTCONFIGURATION = 92;
    int OPT_KEEPPARAMETERNAMES = 93;
    int OPT_OPTIMIZATIONPASSES = 94;
    int OPT_ADAPTKOTLINMETADATA = 95;
    int OPT_ASSUMENOSIDEEFFECTS = 96;
    int OPT_OVERLOADAGGRESSIVELY = 97;
    int OPT_KEEPCLASSMEMBERNAMES = 98;
    int OPT_OBFUSCATIONDICTIONARY = 99;
    int OPT_ADAPTRESOURCEFILENAMES = 100;
    int OPT_KEEPCLASSESWITHMEMBERS = 101;
    int OPT_FLATTENPACKAGEHIERARCHY = 102;
    int OPT_ALLOWACCESSMODIFICATION = 103;
    int INCLUDEDESCRIPTORCLASSES = 104;
    int OPT_DONTPROCESSKOTLINMETADATA = 105;
    int OPT_ADDCONFIGURATIONDEBUGGING = 106;
    int OPT_RENAMESOURCEFILEATTRIBUTE = 107;
    int OPT_ADAPTRESOURCEFILECONTENTS = 108;
    int OPT_USEUNIQUECLASSMEMBERNAMES = 109;
    int OPT_ASSUMENOESCAPINGPARAMETERS = 110;
    int OPT_DONTUSEMIXEDCASECLASSNAMES = 111;
    int OPT_KEEPCLASSESWITHMEMBERNAMES = 112;
    int OPT_CLASSOBFUSCATIONDICTIONARY = 113;
    int OPT_ASSUMENOEXTERNALSIDEEFFECTS = 114;
    int OPT_SKIPNONPUBLICLIBRARYCLASSES = 115;
    int OPT_MERGEINTERFACESAGGRESSIVELY = 116;
    int OPT_ASSUMENOEXTERNALRETURNVALUES = 117;
    int OPT_PACKAGEOBFUSCATIONDICTIONARY = 118;
    int OPT_DONTSKIPNONPUBLICLIBRARYCLASSES = 119;
    int OPT_DONTSKIPNONPUBLICLIBRARYCLASSMEMBERS = 120;
    int QUOTE = 121;
    int QUOTE_122 = 122;
    int TOKEN_123 = 123;
    int INTEGER = 124;
    int NAME = 125;
    int WILDCARD_NAME = 126;
    int WILDCARD_TYPE = 127;
    int LETTER = 128;
    int PART_LETTER = 129;
    String[] TOKEN_IMAGE = new String[]{
            "<EOF>",
            "\" \"",
            "\"\\t\"",
            "\"\\n\"",
            "\"\\r\"",
            "\"\\f\"",
            "\"#\"",
            "<SINGLE_LINE_COMMENT>",
            "<token of kind 8>",
            "<DIGIT>",
            "\"\\ufeff\"",
            "\"~\"",
            "\",\"",
            "\";\"",
            "\"@\"",
            "\".\"",
            "\"!\"",
            "\"?\"",
            "\"%\"",
            "\"*\"",
            "\"(\"",
            "\")\"",
            "\"{\"",
            "\"}\"",
            "\"**\"",
            "\"-if\"",
            "\"***\"",
            "\"...\"",
            "\"enum\"",
            "\"-dump\"",
            "\"-keep\"",
            "\"class\"",
            "\"final\"",
            "\"public\"",
            "\"static\"",
            "\"native\"",
            "\"<init>\"",
            "\"bridge\"",
            "\"private\"",
            "\"extends\"",
            "\"-target\"",
            "\"-injars\"",
            "\"volatile\"",
            "\"strictfp\"",
            "\"<fields>\"",
            "\"-verbose\"",
            "\"-include\"",
            "\"-outjars\"",
            "\"protected\"",
            "\"<methods>\"",
            "\"abstract\"",
            "\"-android\"",
            "\"transient\"",
            "\"interface\"",
            "\"synthetic\"",
            "\"-keepcode\"",
            "\"-zipalign\"",
            "\"-dontnote\"",
            "\"-dontwarn\"",
            "\"@interface\"",
            "\"implements\"",
            "\"-keepnames\"",
            "\"module-info\"",
            "\"includecode\"",
            "\"-printseeds\"",
            "\"-dontshrink\"",
            "\"-printusage\"",
            "\"-libraryjars\"",
            "\"synchronized\"",
            "\"-applymapping\"",
            "\"-microedition\"",
            "\"-printmapping\"",
            "\"-assumevalues\"",
            "\"-dontoptimize\"",
            "\"-dontcompress\"",
            "\"allowshrinking\"",
            "\"-optimizations\"",
            "\"-dontpreverify\"",
            "\"-basedirectory\"",
            "\"-dontobfuscate\"",
            "\"-keepattributes\"",
            "\"-ignorewarnings\"",
            "\"allowobfuscation\"",
            "\"-keepdirectories\"",
            "\"-forceprocessing\"",
            "\"allowoptimization\"",
            "\"-keepclassmembers\"",
            "\"-keeppackagenames\"",
            "\"-repackageclasses\"",
            "\"-whyareyoukeeping\"",
            "\"-adaptclassstrings\"",
            "\"-keepkotlinmetadata\"",
            "\"-printconfiguration\"",
            "\"-keepparameternames\"",
            "\"-optimizationpasses\"",
            "\"-adaptkotlinmetadata\"",
            "\"-assumenosideeffects\"",
            "\"-overloadaggressively\"",
            "\"-keepclassmembernames\"",
            "\"-obfuscationdictionary\"",
            "\"-adaptresourcefilenames\"",
            "\"-keepclasseswithmembers\"",
            "\"-flattenpackagehierarchy\"",
            "\"-allowaccessmodification\"",
            "\"includedescriptorclasses\"",
            "\"-dontprocesskotlinmetadata\"",
            "\"-addconfigurationdebugging\"",
            "\"-renamesourcefileattribute\"",
            "\"-adaptresourcefilecontents\"",
            "\"-useuniqueclassmembernames\"",
            "\"-assumenoescapingparameters\"",
            "\"-dontusemixedcaseclassnames\"",
            "\"-keepclasseswithmembernames\"",
            "\"-classobfuscationdictionary\"",
            "\"-assumenoexternalsideeffects\"",
            "\"-skipnonpubliclibraryclasses\"",
            "\"-mergeinterfacesaggressively\"",
            "\"-assumenoexternalreturnvalues\"",
            "\"-packageobfuscationdictionary\"",
            "\"-dontskipnonpubliclibraryclasses\"",
            "\"-dontskipnonpubliclibraryclassmembers\"",
            "\"\\\"\"",
            "\"\\\"\"",
            "<token of kind 123>",
            "<INTEGER>",
            "<NAME>",
            "<WILDCARD_NAME>",
            "<WILDCARD_TYPE>",
            "<LETTER>",
            "<PART_LETTER>"
    };
}
