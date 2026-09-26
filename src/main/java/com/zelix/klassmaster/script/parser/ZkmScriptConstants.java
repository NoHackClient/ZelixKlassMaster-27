package com.zelix.klassmaster.script.parser;

public interface ZkmScriptConstants {
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
    int DIGIT = 13;
    int BOM = 14;
    int ASSIGN = 15;
    int MINUS = 16;
    int LBRACKET = 17;
    int RBRACKET = 18;
    int SEMICOLON = 19;
    int COMMA = 20;
    int DOT = 21;
    int SLASH = 22;
    int STAR = 23;
    int PLUS = 24;
    int BANG = 25;
    int CARET = 26;
    int AT = 27;
    int LPAREN = 28;
    int RPAREN = 29;
    int LBRACE = 30;
    int RBRACE = 31;
    int HOOK = 32;
    int GT = 33;
    int OR = 34;
    int AND_AND = 35;
    int GC = 36;
    int AND = 38;
    int OPEN = 39;
    int ENUM = 40;
    int KEEP = 42;
    int AS_IS = 44;
    int TRIM = 45;
    int PRINT = 46;
    int LIGHT = 47;
    int FALSE = 49;
    int FINAL = 50;
    int ASCII = 51;
    int BRIDGE = 52;
    int LINK = 53;
    int INIT = 54;
    int SPARSE = 55;
    int SEARCH = 56;
    int PUBLIC = 57;
    int STATIC = 58;
    int NATIVE = 59;
    int DELETE = 60;
    int NORMAL = 61;
    int THROWS = 62;
    int RANDOM = 63;
    int EXCLUDE = 64;
    int SAVE_ALL = 65;
    int EXTENDS = 66;
    int PACKAGE = 67;
    int PRIVATE = 68;
    int EXECUTE = 69;
    int MODERATE = 70;
    int SCRAMBLE = 71;
    int ABSTRACT = 72;
    int CLINIT = 73;
    int ENHANCED = 74;
    int VOLATILE = 75;
    int NON_ASCII = 76;
    int SYNTHETIC = 77;
    int PREVERIFY = 78;
    int UNEXCLUDE = 79;
    int CLASSPATH = 80;
    int OBFUSCATE = 81;
    int PROTECTED = 82;
    int INTERFACE = 83;
    int TRANSIENT = 84;
    int RANDOMIZE = 85;
    int GROUPINGS = 86;
    int ANNOTATION = 87;
    int CONTAINING = 88;
    int SAVE_ALL_OLD = 89;
    int IMPLEMENTS = 91;
    int CONTAINED_IN = 92;
    int KEEP_VISIBLE = 93;
    int TRIM_EXCLUDE = 94;
    int LINE_NUMBERS = 95;
    int IF_IN_ARCHIVE = 96;
    int PACKAGE_INFO = 97;
    int FIXED_CLASSES = 98;
    int SYNCHRONIZED = 99;
    int TRIM_UNEXCLUDE = 100;
    int OBFUSCATE_FLOW = 101;
    int FLOW_OBFUSCATE = 102;
    int CHANGE_LOG_FILE = 103;
    int LOCAL_VARIABLES = 104;
    int HIDE_FIELD_NAMES = 105;
    int NEW_NAMES_PREFIX = 106;
    int RESET_GROUPINGS = 107;
    int IN_SPECIAL_CLASS = 108;
    int EXTRA_AGGRESSIVE = 109;
    int CHANGE_LOG_FILE_IN = 110;
    int RESET_EXCLUSIONS = 111;
    int UNIQUE_CLASS_NAMES = 112;
    int METHOD_PARAMETERS = 113;
    int KEEP_GENERICS_INFO = 114;
    int CHANGE_LOG_FILE_OUT = 115;
    int LEGAL_IDENTIFIERS = 116;
    int ALL_CLASSES_OPENED = 117;
    int NEW_CLASS_NAME_FILE = 118;
    int NEW_FIELD_NAME_FILE = 119;
    int LAST_MODIFIED_TIME = 120;
    int MAKE_CLASSES_PUBLIC = 121;
    int UNIQUE_METHOD_NAMES = 122;
    int NEW_METHOD_NAME_FILE = 123;
    int KEEP_BALANCED_LOCKS = 124;
    int SIGNATURE_CLASSES = 125;
    int RESET_FIXED_CLASSES = 126;
    int REMOVE_METHOD_CALLS = 127;
    int NEW_NAME_CHARACTERS = 128;
    int DELETE_XMLCOMMENTS = 129;
    int OPEN_NESTED_ARCHIVES = 130;
    int NEW_PACKAGE_NAME_FILE = 131;
    int ARCHIVE_COMPRESSION = 132;
    int KEEP_INNER_CLASS_INFO = 133;
    int AUTO_REFLECTION_HASH = 134;
    int OBFUSCATE_PARAMETERS = 135;
    int MIXED_CASE_CLASS_NAMES = 136;
    int IF_NAME_NOT_OBFUSCATED = 137;
    int RESET_TRIM_EXCLUSIONS = 138;
    int OBFUSCATE_REFERENCES = 139;
    int KEEP_IF_NOT_OBFUSCATED = 140;
    int EXPECTED_FINAL_SHA256 = 141;
    int IN_REFERENCING_CLASSES = 142;
    int LOOSE_CHANGE_LOG_FILE_IN = 143;
    int OBFUSCATE_FLOW_EXCLUDE = 144;
    int EXCEPTION_OBFUSCATION = 145;
    int ACCESSED_BY_REFLECTION = 146;
    int ASSUME_RUNTIME_VERSION = 147;
    int ENCRYPT_LONG_CONSTANTS = 148;
    int HIDE_STATIC_METHOD_NAMES = 149;
    int ENCRYPT_STRING_LITERALS = 150;
    int AUTO_REFLECTION_PACKAGE = 151;
    int LONG_ENCRYPTION_EXCLUDE = 152;
    int EXPECTED_INITIAL_SHA256 = 153;
    int OBFUSCATE_FLOW_UNEXCLUDE = 154;
    int METHOD_PARAMETER_CHANGES = 155;
    int AUTO_REFLECTION_HANDLING = 156;
    int RESET_REMOVE_METHOD_CALLS = 158;
    int LONG_ENCRYPTION_UNEXCLUDE = 159;
    int ENCRYPT_INTEGER_CONSTANTS = 160;
    int IGNORE_MISSING_REFERENCES = 161;
    int STRING_ENCRYPTION_EXCLUDE = 162;
    int DELETE_UNKNOWN_ATTRIBUTES = 163;
    int INTEGER_ENCRYPTION_EXCLUDE = 164;
    int REMOVE_METHOD_CALLS_INCLUDE = 165;
    int REMOVE_METHOD_CALLS_EXCLUDE = 166;
    int CLASS_INITIALIZATION_ORDER = 167;
    int AGGRESSIVE_METHOD_RENAMING = 168;
    int EXISTING_SERIALIZED_CLASSES = 169;
    int STRING_ENCRYPTION_UNEXCLUDE = 170;
    int RESET_ACCESSED_BY_REFLECTION = 171;
    int DELETE_EXCEPTION_ATTRIBUTES = 172;
    int OBFUSCATE_EXCEPTIONS_EXCLUDE = 173;
    int INTEGER_ENCRYPTION_UNEXCLUDE = 174;
    int OBFUSCATE_REFERENCES_PACKAGE = 175;
    int KEEP_VISIBLE_IF_NOT_OBFUSCATED = 176;
    int DELETE_ANNOTATION_ATTRIBUTES = 177;
    int DELETE_SOURCE_FILE_ATTRIBUTES = 178;
    int DELETE_DEPRECATED_ATTRIBUTES = 179;
    int OBFUSCATE_REFERENCES_INCLUDE = 180;
    int OBFUSCATE_REFERENCES_EXCLUDE = 181;
    int ACCESSED_BY_REFLECTION_EXCLUDE = 182;
    int ALLOW_METHOD_PARAMETER_CHANGES = 183;
    int KEEP_VISIBLE_METHOD_PARAMETERS = 184;
    int COLLAPSE_PACKAGES_WITH_DEFAULT = 185;
    int OBFUSCATE_EXCEPTIONS_UNEXCLUDE = 186;
    int RESET_IGNORE_MISSING_REFERENCES = 187;
    int OBFUSCATE_REFERENCE_STRUCTURES = 188;
    int RESET_OBFUSCATE_FLOW_EXCLUSIONS = 189;
    int RESET_LONG_ENCRYPTION_EXCLUSIONS = 190;
    int RESET_CLASS_INITIALIZATION_ORDER = 191;
    int METHOD_PARAMETER_CHANGES_INCLUDE = 192;
    int METHOD_PARAMETER_CHANGES_EXCLUDE = 193;
    int METHOD_PARAMETER_CHANGES_PACKAGE = 194;
    int RESET_EXISTING_SERIALIZED_CLASSES = 195;
    int DELETE_DEBUG_EXTENSION_ATTRIBUTES = 196;
    int RESET_STRING_ENCRYPTION_EXCLUSIONS = 197;
    int RESET_INTEGER_ENCRYPTION_EXCLUSIONS = 198;
    int METHOD_PARAMETER_OBFUSCATION_INCLUDE = 199;
    int METHOD_PARAMETER_OBFUSCATION_EXCLUDE = 200;
    int DERIVE_GROUPINGS_FROM_INPUT_CHANGE_LOG = 201;
    int DERIVE_SUBCLASS_NAMES_FROM_SUPERCLASS = 202;
    int RESET_OBFUSCATE_REFERENCE_EXCLUSIONS = 203;
    int RESET_OBFUSCATE_EXCEPTIONS_EXCLUSIONS = 204;
    int KEEP_METHOD_PARAMETERS_IF_NOT_OBFUSCATED = 205;
    int RESET_METHOD_PARAMETER_CHANGES_EXCLUSIONS = 206;
    int RESET_METHOD_PARAMETER_OBFUSCATION_EXCLUSIONS = 207;
    int KEEP_VISIBLE_METHOD_PARAMETERS_IF_NOT_OBFUSCATED = 208;
    int QUOTE = 209;
    int QUOTE_210 = 210;
    int TOKEN_211 = 211;
    int INTEGER_LITERAL = 212;
    int NAME = 213;
    int WILDCARD_NAME = 214;
    int MODULE_NAME = 215;
    int LINKED_CLASS_NAME_WITH_SUFFIX = 216;
    int LINKED_METHOD_NAME = 217;
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
            "<DIGIT>",
            "\"\\ufeff\"",
            "\"=\"",
            "\"-\"",
            "\"[\"",
            "\"]\"",
            "\";\"",
            "\",\"",
            "\".\"",
            "\"/\"",
            "\"*\"",
            "\"+\"",
            "\"!\"",
            "\"^\"",
            "\"@\"",
            "\"(\"",
            "\")\"",
            "\"{\"",
            "\"}\"",
            "\"?\"",
            "\">\"",
            "\"||\"",
            "\"&&\"",
            "\"gc\"",
            "\"all\"",
            "\"and\"",
            "\"open\"",
            "\"enum\"",
            "\"true\"",
            "\"keep\"",
            "\"none\"",
            "\"asIs\"",
            "\"trim\"",
            "\"print\"",
            "\"light\"",
            "\"heavy\"",
            "\"false\"",
            "\"final\"",
            "\"ASCII\"",
            "\"bridge\"",
            "\"<link>\"",
            "\"<init>\"",
            "\"sparse\"",
            "\"search\"",
            "\"public\"",
            "\"static\"",
            "\"native\"",
            "\"delete\"",
            "\"normal\"",
            "\"throws\"",
            "\"random\"",
            "\"exclude\"",
            "\"saveAll\"",
            "\"extends\"",
            "\"package\"",
            "\"private\"",
            "\"execute\"",
            "\"moderate\"",
            "\"scramble\"",
            "\"abstract\"",
            "\"<clinit>\"",
            "\"enhanced\"",
            "\"volatile\"",
            "\"non-ASCII\"",
            "\"synthetic\"",
            "\"preverify\"",
            "\"unexclude\"",
            "\"classpath\"",
            "\"obfuscate\"",
            "\"protected\"",
            "\"interface\"",
            "\"transient\"",
            "\"randomize\"",
            "\"groupings\"",
            "\"annotation\"",
            "\"containing\"",
            "\"saveAllOld\"",
            "\"aggressive\"",
            "\"implements\"",
            "\"containedIn\"",
            "\"keepVisible\"",
            "\"trimExclude\"",
            "\"lineNumbers\"",
            "\"ifInArchive\"",
            "\"package-info\"",
            "\"fixedClasses\"",
            "\"synchronized\"",
            "\"trimUnexclude\"",
            "\"obfuscateFlow\"",
            "\"flowObfuscate\"",
            "\"changeLogFile\"",
            "\"localVariables\"",
            "\"hideFieldNames\"",
            "\"newNamesPrefix\"",
            "\"resetGroupings\"",
            "\"inSpecialClass\"",
            "\"extraAggressive\"",
            "\"changeLogFileIn\"",
            "\"resetExclusions\"",
            "\"uniqueClassNames\"",
            "\"methodParameters\"",
            "\"keepGenericsInfo\"",
            "\"changeLogFileOut\"",
            "\"legalIdentifiers\"",
            "\"allClassesOpened\"",
            "\"newClassNameFile\"",
            "\"newFieldNameFile\"",
            "\"lastModifiedTime\"",
            "\"makeClassesPublic\"",
            "\"uniqueMethodNames\"",
            "\"newMethodNameFile\"",
            "\"keepBalancedLocks\"",
            "\"+signatureClasses\"",
            "\"resetFixedClasses\"",
            "\"removeMethodCalls\"",
            "\"newNameCharacters\"",
            "\"deleteXMLComments\"",
            "\"openNestedArchives\"",
            "\"newPackageNameFile\"",
            "\"archiveCompression\"",
            "\"keepInnerClassInfo\"",
            "\"autoReflectionHash\"",
            "\"obfuscateParameters\"",
            "\"mixedCaseClassNames\"",
            "\"ifNameNotObfuscated\"",
            "\"resetTrimExclusions\"",
            "\"obfuscateReferences\"",
            "\"keepIfNotObfuscated\"",
            "\"expectedFinal_SHA256\"",
            "\"inReferencingClasses\"",
            "\"looseChangeLogFileIn\"",
            "\"obfuscateFlowExclude\"",
            "\"exceptionObfuscation\"",
            "\"accessedByReflection\"",
            "\"assumeRuntimeVersion\"",
            "\"encryptLongConstants\"",
            "\"hideStaticMethodNames\"",
            "\"encryptStringLiterals\"",
            "\"autoReflectionPackage\"",
            "\"longEncryptionExclude\"",
            "\"expectedInitial_SHA256\"",
            "\"obfuscateFlowUnexclude\"",
            "\"methodParameterChanges\"",
            "\"autoReflectionHandling\"",
            "\"deleteEmptyDirectories\"",
            "\"resetRemoveMethodCalls\"",
            "\"longEncryptionUnexclude\"",
            "\"encryptIntegerConstants\"",
            "\"ignoreMissingReferences\"",
            "\"stringEncryptionExclude\"",
            "\"deleteUnknownAttributes\"",
            "\"integerEncryptionExclude\"",
            "\"removeMethodCallsInclude\"",
            "\"removeMethodCallsExclude\"",
            "\"classInitializationOrder\"",
            "\"aggressiveMethodRenaming\"",
            "\"existingSerializedClasses\"",
            "\"stringEncryptionUnexclude\"",
            "\"resetAccessedByReflection\"",
            "\"deleteExceptionAttributes\"",
            "\"obfuscateExceptionsExclude\"",
            "\"integerEncryptionUnexclude\"",
            "\"obfuscateReferencesPackage\"",
            "\"keepVisibleIfNotObfuscated\"",
            "\"deleteAnnotationAttributes\"",
            "\"deleteSourceFileAttributes\"",
            "\"deleteDeprecatedAttributes\"",
            "\"obfuscateReferencesInclude\"",
            "\"obfuscateReferencesExclude\"",
            "\"accessedByReflectionExclude\"",
            "\"allowMethodParameterChanges\"",
            "\"keepVisibleMethodParameters\"",
            "\"collapsePackagesWithDefault\"",
            "\"obfuscateExceptionsUnexclude\"",
            "\"resetIgnoreMissingReferences\"",
            "\"obfuscateReferenceStructures\"",
            "\"resetObfuscateFlowExclusions\"",
            "\"resetLongEncryptionExclusions\"",
            "\"resetClassInitializationOrder\"",
            "\"methodParameterChangesInclude\"",
            "\"methodParameterChangesExclude\"",
            "\"methodParameterChangesPackage\"",
            "\"resetExistingSerializedClasses\"",
            "\"deleteDebugExtensionAttributes\"",
            "\"resetStringEncryptionExclusions\"",
            "\"resetIntegerEncryptionExclusions\"",
            "\"methodParameterObfuscationInclude\"",
            "\"methodParameterObfuscationExclude\"",
            "\"deriveGroupingsFromInputChangeLog\"",
            "\"deriveSubclassNamesFromSuperclass\"",
            "\"resetObfuscateReferenceExclusions\"",
            "\"resetObfuscateExceptionsExclusions\"",
            "\"keepMethodParametersIfNotObfuscated\"",
            "\"resetMethodParameterChangesExclusions\"",
            "\"resetMethodParameterObfuscationExclusions\"",
            "\"keepVisibleMethodParametersIfNotObfuscated\"",
            "\"\\\"\"",
            "\"\\\"\"",
            "<token of kind 211>",
            "<INTEGER_LITERAL>",
            "<NAME>",
            "<WILDCARD_NAME>",
            "<MODULE_NAME>",
            "<LINKED_CLASS_NAME_WITH_SUFFIX>",
            "<LINKED_METHOD_NAME>"
    };
}
