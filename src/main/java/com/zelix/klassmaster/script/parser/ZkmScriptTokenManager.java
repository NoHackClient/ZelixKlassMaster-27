package com.zelix.klassmaster.script.parser;

import com.zelix.klassmaster.script.ZkmScriptTokenMgrError;

import java.io.IOException;
import java.io.PrintStream;

public class ZkmScriptTokenManager implements ZkmScriptConstants {
    public static long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    public static long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    public static int[] jjnextStates = new int[]{16, 17, 14, 18, 24};
    public static String[] jjstrLiteralImages = new String[]{
            "",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "\ufeff",
            "=",
            "-",
            "[",
            "]",
            ";",
            ",",
            ".",
            "/",
            "*",
            "+",
            "!",
            "^",
            "@",
            "(",
            ")",
            "{",
            "}",
            "?",
            ">",
            "||",
            "&&",
            "gc",
            "all",
            "and",
            "open",
            "enum",
            "true",
            "keep",
            "none",
            "asIs",
            "trim",
            "print",
            "light",
            "heavy",
            "false",
            "final",
            "ASCII",
            "bridge",
            "<link>",
            "<init>",
            "sparse",
            "search",
            "public",
            "static",
            "native",
            "delete",
            "normal",
            "throws",
            "random",
            "exclude",
            "saveAll",
            "extends",
            "package",
            "private",
            "execute",
            "moderate",
            "scramble",
            "abstract",
            "<clinit>",
            "enhanced",
            "volatile",
            "non-ASCII",
            "synthetic",
            "preverify",
            "unexclude",
            "classpath",
            "obfuscate",
            "protected",
            "interface",
            "transient",
            "randomize",
            "groupings",
            "annotation",
            "containing",
            "saveAllOld",
            "aggressive",
            "implements",
            "containedIn",
            "keepVisible",
            "trimExclude",
            "lineNumbers",
            "ifInArchive",
            "package-info",
            "fixedClasses",
            "synchronized",
            "trimUnexclude",
            "obfuscateFlow",
            "flowObfuscate",
            "changeLogFile",
            "localVariables",
            "hideFieldNames",
            "newNamesPrefix",
            "resetGroupings",
            "inSpecialClass",
            "extraAggressive",
            "changeLogFileIn",
            "resetExclusions",
            "uniqueClassNames",
            "methodParameters",
            "keepGenericsInfo",
            "changeLogFileOut",
            "legalIdentifiers",
            "allClassesOpened",
            "newClassNameFile",
            "newFieldNameFile",
            "lastModifiedTime",
            "makeClassesPublic",
            "uniqueMethodNames",
            "newMethodNameFile",
            "keepBalancedLocks",
            "+signatureClasses",
            "resetFixedClasses",
            "removeMethodCalls",
            "newNameCharacters",
            "deleteXMLComments",
            "openNestedArchives",
            "newPackageNameFile",
            "archiveCompression",
            "keepInnerClassInfo",
            "autoReflectionHash",
            "obfuscateParameters",
            "mixedCaseClassNames",
            "ifNameNotObfuscated",
            "resetTrimExclusions",
            "obfuscateReferences",
            "keepIfNotObfuscated",
            "expectedFinal_SHA256",
            "inReferencingClasses",
            "looseChangeLogFileIn",
            "obfuscateFlowExclude",
            "exceptionObfuscation",
            "accessedByReflection",
            "assumeRuntimeVersion",
            "encryptLongConstants",
            "hideStaticMethodNames",
            "encryptStringLiterals",
            "autoReflectionPackage",
            "longEncryptionExclude",
            "expectedInitial_SHA256",
            "obfuscateFlowUnexclude",
            "methodParameterChanges",
            "autoReflectionHandling",
            "deleteEmptyDirectories",
            "resetRemoveMethodCalls",
            "longEncryptionUnexclude",
            "encryptIntegerConstants",
            "ignoreMissingReferences",
            "stringEncryptionExclude",
            "deleteUnknownAttributes",
            "integerEncryptionExclude",
            "removeMethodCallsInclude",
            "removeMethodCallsExclude",
            "classInitializationOrder",
            "aggressiveMethodRenaming",
            "existingSerializedClasses",
            "stringEncryptionUnexclude",
            "resetAccessedByReflection",
            "deleteExceptionAttributes",
            "obfuscateExceptionsExclude",
            "integerEncryptionUnexclude",
            "obfuscateReferencesPackage",
            "keepVisibleIfNotObfuscated",
            "deleteAnnotationAttributes",
            "deleteSourceFileAttributes",
            "deleteDeprecatedAttributes",
            "obfuscateReferencesInclude",
            "obfuscateReferencesExclude",
            "accessedByReflectionExclude",
            "allowMethodParameterChanges",
            "keepVisibleMethodParameters",
            "collapsePackagesWithDefault",
            "obfuscateExceptionsUnexclude",
            "resetIgnoreMissingReferences",
            "obfuscateReferenceStructures",
            "resetObfuscateFlowExclusions",
            "resetLongEncryptionExclusions",
            "resetClassInitializationOrder",
            "methodParameterChangesInclude",
            "methodParameterChangesExclude",
            "methodParameterChangesPackage",
            "resetExistingSerializedClasses",
            "deleteDebugExtensionAttributes",
            "resetStringEncryptionExclusions",
            "resetIntegerEncryptionExclusions",
            "methodParameterObfuscationInclude",
            "methodParameterObfuscationExclude",
            "deriveGroupingsFromInputChangeLog",
            "deriveSubclassNamesFromSuperclass",
            "resetObfuscateReferenceExclusions",
            "resetObfuscateExceptionsExclusions",
            "keepMethodParametersIfNotObfuscated",
            "resetMethodParameterChangesExclusions",
            "resetMethodParameterObfuscationExclusions",
            "keepVisibleMethodParametersIfNotObfuscated",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    };
    public static int[] jjnewLexState = new int[]{
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            1,
            2,
            3,
            0,
            0,
            0,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            4,
            0,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
    };
    public static long[] jjtoToken = new long[]{-16383L, -1L, -1L, 66453503L};
    public static long[] jjtoSkip = new long[]{3646L, 0L, 0L, 0L};
    public static long[] jjtoSpecial = new long[]{3584L, 0L, 0L, 0L};
    public int jjnewStateCnt;
    public int jjmatchedPos;
    public int jjround;
    public char curChar;
    public int jjimageLen;
    public int lengthOfMatch;
    public int jjmatchedKind;
    public PrintStream debugStream = System.out;
    public final int[] jjrounds = new int[25];
    public final int[] jjstateSet = new int[50];
    public final StringBuilder jjimage = new StringBuilder();
    public StringBuilder image = this.jjimage;
    public int curLexState = 0;
    public int defaultLexState = 0;
    public ZkmScriptSimpleCharStream input_stream;

    public int jjMoveStringLiteralDfa37_0(long ba) {
        long bb = 98304L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(35, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(36, 0L, 0L, 0L, bb);
            return 37;
        }

        switch (this.curChar) {
            case 'c':
                return this.jjMoveStringLiteralDfa38_0(bb, 65536L);
            case 'i':
                return this.jjMoveStringLiteralDfa38_0(bb, 32768L);
            default:
                return this.jjStartNfa_0(36, 0L, 0L, 0L, bb);
        }
    }

    public ZkmScriptTokenManager(ZkmScriptSimpleCharStream zkmScriptSimpleCharStream) {
        this.input_stream = zkmScriptSimpleCharStream;
    }

    public void jjCheckNAddTwoStates(int ba, int bb) {
        this.jjCheckNAdd(ba);
        this.jjCheckNAdd(bb);
    }

    public int jjMoveStringLiteralDfa16_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bi = bf;
        long bh = bd;
        long bg = bb;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(14, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(15, 0L, bg, bh, bi);
            return 16;
        }

        switch (this.curChar) {
            case 'A':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 3940649673957376L, bi, 0L);
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'P':
            case 'Q':
            case 'T':
            case 'V':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'g':
            case 'j':
            case 'k':
            case 'q':
            case 'u':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(15, 0L, bg, bh, bi);
            case 'E':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 17179869184L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 2097152L, bi, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 281474976710656L, bi, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 1099511627776L, bi, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 33554432L, bi, 0L);
            case 'U':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 4398046511104L, bi, 0L);
            case 'W':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 144115188075855872L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 1048576L, bi, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 0L, bi, 384L);
            case 'c':
                if ((bg & 144115188075855872L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 121, 26);
                }

                return this.jjMoveStringLiteralDfa17_0(bg, bh, 1166573041002481664L, bi, 4096L);
            case 'd':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 72057595111669760L, bi, 65536L);
            case 'e':
                if ((bg & 576460752303423488L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 123, 26);
                }

                return this.jjMoveStringLiteralDfa17_0(bg, bh, 36039802876854404L, bi, 49152L);
            case 'f':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 32L, bi, 2048L);
            case 'h':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 134217728L, bi, 7L);
            case 'i':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 4611686568183201792L, bi, 8L);
            case 'l':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, -9223372036854677496L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 256L, bi, 1024L);
            case 'n':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 576531194330480640L, bi, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 2594108569737495568L, bi, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 0L, bi, 32L);
            case 'r':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 34359738368L, bi, 512L);
            case 's':
                if ((bg & 288230376151711744L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 122, 26);
                } else if ((bg & 1152921504606846976L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 124, 26);
                } else if ((bg & 2305843009213693952L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 125, 26);
                } else if ((bg & 4611686018427387904L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 126, 26);
                } else {
                    if ((bg & Long.MIN_VALUE) != 0L) {
                        this.jjmatchedKind = 127;
                        this.jjmatchedPos = 16;
                    } else {
                        if ((bh & 1L) != 0L) {
                            return this.jjStartNfaWithStates_0(16, 128, 26);
                        }

                        if ((bh & 2L) != 0L) {
                            return this.jjStartNfaWithStates_0(16, 129, 26);
                        }
                    }

                    return this.jjMoveStringLiteralDfa17_0(bg, bh, 412317401152L, bi, 16L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 18031991232795136L, bi, 8192L);
            case 'x':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 67108864L, bi, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa17_0(bg, bh, 0L, bi, 64L);
        }
    }

    public int jjMoveStringLiteralDfa38_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(36, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(37, 0L, 0L, 0L, bc);
            return 38;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa39_0(bc, 65536L);
            case 'o':
                return this.jjMoveStringLiteralDfa39_0(bc, 32768L);
            default:
                return this.jjStartNfa_0(37, 0L, 0L, 0L, bc);
        }
    }

    public int jjStopAtPos(int jjmatchedPos, int jjmatchedKind) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        return jjmatchedPos + 1;
    }

    public int jjMoveStringLiteralDfa40_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(38, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(39, 0L, 0L, 0L, bc);
            return 40;
        }

        switch (this.curChar) {
            case 'e':
                return this.jjMoveStringLiteralDfa41_0(bc);
            case 's':
                if ((bc & 32768L) != 0L) {
                    return this.jjStartNfaWithStates_0(40, 207, 26);
                }
            default:
                return this.jjStartNfa_0(39, 0L, 0L, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa3_0(long ba, long bb, long bc, long bd, long be, long bf, long bg, long bh) {
        long bj = bd;
        long bk = bf;
        long bi = bb;
        long bl = bh;
        if (((bi = bi & ba) | (bj = bj & bc) | (bk = bk & be) | (bl = bl & bg)) == 0L) {
            return this.jjStartNfa_0(1, ba, bc, be, bg);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(2, bi, bj, bk, bl);
            return 3;
        }

        switch (this.curChar) {
            case '-':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 4096L, bk, 0L, bl, 0L);
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'D':
            case 'E':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'O':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'f':
            case 'j':
            default:
                return this.jjStartNfa_0(2, bi, bj, bk, bl);
            case 'C':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 27021597764222976L, bk, 0L, bl, 0L);
            case 'F':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 36028797018963968L, bk, 0L, bl, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa4_0(bi, 2251799813685248L, bj, 0L, bk, 0L, bl, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 576460752303423488L, bk, 0L, bl, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 4398046511104L, bk, 1L, bl, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 0L, bk, 8L, bl, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa4_0(bi, 1125899906842624L, bj, 4504699139001472L, bk, 512L, bl, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 34359738400L, bk, 0L, bl, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa4_0(bi, -9218868437227405312L, bj, 2097152L, bk, 0L, bl, 0L);
            case 'e':
                if ((bi & 2199023255552L) != 0L) {
                    return this.jjStartNfaWithStates_0(3, 41, 26);
                } else {
                    if ((bi & 8796093022208L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 43, 26);
                    }

                    return this.jjMoveStringLiteralDfa4_0(bi, 1152921504606846976L, bj, 4755952958469308486L, bk, -1707330346977696510L, bl, 55416L);
                }
            case 'g':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 2305843009213693952L, bk, 2164260864L, bl, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa4_0(bi, 140737488355328L, bj, 562949953421312L, bk, 134217744L, bl, 391L);
            case 'i':
                return this.jjMoveStringLiteralDfa4_0(bi, 594475150812905472L, bj, 512L, bk, 4415226380288L, bl, 1536L);
            case 'k':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 8589934600L, bk, 0L, bl, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa4_0(bi, 144115188075855872L, bj, 134217729L, bk, 144115188075855872L, bl, 0L);
            case 'm':
                if ((bi & 1099511627776L) != 0L) {
                    return this.jjStartNfaWithStates_0(3, 40, 26);
                }

                ZkmScriptTokenManager zkmScriptTokenManager2;
                long bn;
                long bp;
                if ((bi & 35184372088832L) != 0L) {
                    this.jjmatchedKind = 45;
                    this.jjmatchedPos = 3;
                    zkmScriptTokenManager2 = this;
                    bn = bi;
                    bp = 2305843009213693952L;
                } else {
                    zkmScriptTokenManager2 = this;
                    bn = bi;
                    bp = 2305843009213693952L;
                }

                return zkmScriptTokenManager2.jjMoveStringLiteralDfa4_0(bn, bp, bj, 69793218560L, bk, 0L, bl, 0L);
            case 'n':
                ZkmScriptTokenManager zkmScriptTokenManager1;
                long bm;
                long bo;
                if ((bi & 549755813888L) != 0L) {
                    this.jjmatchedKind = 39;
                    this.jjmatchedPos = 3;
                    zkmScriptTokenManager1 = this;
                    bm = bi;
                    bo = 9077567998918656L;
                } else {
                    zkmScriptTokenManager1 = this;
                    bm = bi;
                    bo = 9077567998918656L;
                }

                return zkmScriptTokenManager1.jjMoveStringLiteralDfa4_0(bm, bo, bj, 2322722609692672L, bk, 4L, bl, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa4_0(bi, 4611686018427387904L, bj, -9223372036846387200L, bk, 36029218202583104L, bl, 0L);
            case 'p':
                ZkmScriptTokenManager zkmScriptTokenManager3;
                long bq;
                long br;
                long bs;
                long bt;
                if ((bi & 4398046511104L) != 0L) {
                    this.jjmatchedKind = 42;
                    this.jjmatchedPos = 3;
                    zkmScriptTokenManager3 = this;
                    bq = bi;
                    br = 0L;
                    bs = bj;
                    bt = 1154064997236604928L;
                } else {
                    zkmScriptTokenManager3 = this;
                    bq = bi;
                    br = 0L;
                    bs = bj;
                    bt = 1154064997236604928L;
                }

                return zkmScriptTokenManager3.jjMoveStringLiteralDfa4_0(bq, br, bs, bt, bk, 72339069014642720L, bl, 73728L);
            case 'q':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 288511851128422400L, bk, 0L, bl, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa4_0(bi, 108086391056891904L, bj, 35184439197696L, bk, 1103811837952L, bl, 0L);
            case 's':
                if ((bi & 17592186044416L) != 0L) {
                    return this.jjStartNfaWithStates_0(3, 44, 26);
                }

                return this.jjMoveStringLiteralDfa4_0(bi, 562949953421312L, bj, 65536L, bk, 2748779102208L, bl, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa4_0(bi, 288230376151711744L, bj, 72057594323411200L, bk, 0L, bl, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 137443278848L, bk, 1454838601568815232L, bl, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa4_0(bi, 281474976710656L, bj, 16400L, bk, 0L, bl, 0L);
            case 'w':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 274877906944L, bk, 0L, bl, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa4_0(bi, 0L, bj, 32768L, bk, 0L, bl, 0L);
        }
    }

    public int jjMoveStringLiteralDfa8_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bi = bf;
        long bg = bb;
        long bh = bd;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(6, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(7, 0L, bg, bh, bi);
            return 8;
        }

        switch (this.curChar) {
            case 'B':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 18014398509744128L, bi, 0L);
            case 'C':
            case 'D':
            case 'E':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'M':
            case 'O':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'q':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(7, 0L, bg, bh, bi);
            case 'F':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 8192L, bi, 0L);
            case 'I':
                if ((bg & 4096L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 76, 7);
                }

                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 33554432L, bi, 0L);
            case 'L':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 2L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa9_0(bg, 54043195528445952L, bh, 0L, bi, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa9_0(bg, 4398046511104L, bh, 144115188075855872L, bi, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 2199023255552L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa9_0(bg, 281474976710656L, bh, 0L, bi, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa9_0(bg, 536870912L, bh, 72339069014638592L, bi, 66576L);
            case 'c':
                if ((bg & 8192L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 77, 26);
                }

                return this.jjMoveStringLiteralDfa9_0(bg, 68719476736L, bh, 22007412424704L, bi, 0L);
            case 'd':
                if ((bg & 262144L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 82, 26);
                }

                return this.jjMoveStringLiteralDfa9_0(bg, 576462951595114496L, bh, 0L, bi, 0L);
            case 'e':
                if ((bg & 32768L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 79, 26);
                } else {
                    if ((bg & 131072L) != 0L) {
                        this.jjmatchedKind = 81;
                        this.jjmatchedPos = 8;
                    } else {
                        if ((bg & 524288L) != 0L) {
                            return this.jjStartNfaWithStates_0(8, 83, 26);
                        }

                        if ((bg & 2097152L) != 0L) {
                            return this.jjStartNfaWithStates_0(8, 85, 26);
                        }
                    }

                    return this.jjMoveStringLiteralDfa9_0(bg, 4620693357268566016L, bh, 1454847397938137540L, bi, 64L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa9_0(bg, 72057594037927936L, bh, 0L, bi, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa9_0(bg, 2322718313676800L, bh, 4611686018427387912L, bi, 0L);
            case 'h':
                if ((bg & 65536L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 80, 26);
                }

                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 36028797018963969L, bi, 49152L);
            case 'i':
                return this.jjMoveStringLiteralDfa9_0(bg, 1146756268032L, bh, 2097152L, bi, 32L);
            case 'k':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 34359738368L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa9_0(bg, 158329707954176L, bh, 0L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 1024L, bi, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa9_0(bg, 1157425104250994688L, bh, 633391712747520L, bi, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa9_0(bg, 8388608L, bh, 576460753378213904L, bi, 8704L);
            case 'p':
                return this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 2251800350556160L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa9_0(bg, 2307567043446046720L, bh, 134217760L, bi, 391L);
            case 's':
                if ((bg & 4194304L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 86, 26);
                }

                return this.jjMoveStringLiteralDfa9_0(bg, 144115480133632000L, bh, -9223372028264841216L, bi, 8L);
            case 't':
                if ((bg & 1048576L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 84, 26);
                }

                return this.jjMoveStringLiteralDfa9_0(bg, -8935141660568846336L, bh, 962076873216L, bi, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa9_0(bg, 8797166764032L, bh, 2306968909120536576L, bi, 6144L);
            case 'v':
                return this.jjMoveStringLiteralDfa9_0(bg, 67108864L, bh, 1099511627776L, bi, 0L);
            case 'y':
                return (bg & 16384L) != 0L ? this.jjStartNfaWithStates_0(8, 78, 26) : this.jjMoveStringLiteralDfa9_0(bg, 0L, bh, 2164260864L, bi, 0L);
        }
    }

    public int jjStartNfaWithStates_0(int jjmatchedPos, int jjmatchedKind, int bc) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            return jjmatchedPos + 1;
        }

        return this.jjMoveNfa_0(bc, jjmatchedPos + 1);
    }

    public int jjMoveStringLiteralDfa19_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(17, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(18, 0L, 0L, be, bf);
            return 19;
        }

        switch (this.curChar) {
            case '2':
                return this.jjMoveStringLiteralDfa20_0(be, 33554432L, bf, 0L);
            case '6':
                if ((be & 8192L) != 0L) {
                    return this.jjStartNfaWithStates_0(19, 141, 26);
                }
            case '3':
            case '4':
            case '5':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'f':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(18, 0L, 0L, be, bf);
            case 'E':
                return this.jjMoveStringLiteralDfa20_0(be, 4620728402054217728L, bf, 0L);
            case 'F':
                return this.jjMoveStringLiteralDfa20_0(be, 0L, bf, 1024L);
            case 'I':
                return this.jjMoveStringLiteralDfa20_0(be, 4503599627370496L, bf, 512L);
            case 'O':
                return this.jjMoveStringLiteralDfa20_0(be, 549755813888L, bf, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa20_0(be, 140737488355328L, bf, 0L);
            case 'U':
                return this.jjMoveStringLiteralDfa20_0(be, 288230376151711744L, bf, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa20_0(be, -9223370933048180736L, bf, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa20_0(be, 481036337152L, bf, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa20_0(be, 16777216L, bf, 0L);
            case 'e':
                if ((be & 65536L) != 0L) {
                    return this.jjStartNfaWithStates_0(19, 144, 26);
                }

                return this.jjMoveStringLiteralDfa20_0(be, 576539917142720512L, bf, 2048L);
            case 'g':
                return this.jjMoveStringLiteralDfa20_0(be, 142606336L, bf, 7L);
            case 'h':
                return this.jjMoveStringLiteralDfa20_0(be, 144115188075855872L, bf, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa20_0(be, 17592991350784L, bf, 72L);
            case 'l':
                return this.jjMoveStringLiteralDfa20_0(be, 2219428544512L, bf, 0L);
            case 'n':
                if ((be & 32768L) != 0L) {
                    return this.jjStartNfaWithStates_0(19, 143, 26);
                } else if ((be & 131072L) != 0L) {
                    return this.jjStartNfaWithStates_0(19, 145, 26);
                } else {
                    if ((be & 262144L) != 0L) {
                        this.jjmatchedKind = 146;
                        this.jjmatchedPos = 19;
                    } else if ((be & 524288L) != 0L) {
                        return this.jjStartNfaWithStates_0(19, 147, 26);
                    }

                    return this.jjMoveStringLiteralDfa20_0(be, 18014407099416576L, bf, 16L);
                }
            case 'o':
                return this.jjMoveStringLiteralDfa20_0(be, 0L, bf, 32L);
            case 'r':
                return this.jjMoveStringLiteralDfa20_0(be, 112027040730841088L, bf, 114688L);
            case 's':
                if ((be & 16384L) != 0L) {
                    return this.jjStartNfaWithStates_0(19, 142, 26);
                } else {
                    if ((be & 1048576L) != 0L) {
                        return this.jjStartNfaWithStates_0(19, 148, 26);
                    }

                    return this.jjMoveStringLiteralDfa20_0(be, 0L, bf, 8576L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa20_0(be, 1152921504606846976L, bf, 4096L);
            case 'u':
                return this.jjMoveStringLiteralDfa20_0(be, 281509403557888L, bf, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa20_0(be, 2305847407260205056L, bf, 0L);
        }
    }

    public void jjCheckNAddStates() {
        int ba = 0;
        ZkmScriptTokenManager zkmScriptTokenManager1 = this;
        int[] bb = jjnextStates;

        while (true) {
            zkmScriptTokenManager1.jjCheckNAdd(bb[ba]);
            if (ba++ == 4) {
                return;
            }

            zkmScriptTokenManager1 = this;
            bb = jjnextStates;
        }
    }

    public int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1();
    }

    public ZkmScriptToken getNextToken() {
        ZkmScriptToken zkmScriptToken = null;
        int ba = 0;

        label134:
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            } catch (IOException iOException) {
                this.jjmatchedKind = 0;
                ZkmScriptToken zkmScriptToken1 = this.jjFillToken();
                zkmScriptToken1.specialToken = zkmScriptToken;
                return zkmScriptToken1;
            }

            this.image = this.jjimage;
            this.image.setLength(0);
            this.jjimageLen = 0;
            int curLexState = this.curLexState;

            while (true) {
                switch (curLexState) {
                    case 0:
                        try {
                            this.input_stream.backup(0);

                            for (char curChar = this.curChar; curChar <= ' ' && (4294981120L & 1L << this.curChar) != 0L; curChar = this.curChar) {
                                this.curChar = this.input_stream.BeginToken();
                            }
                        } catch (IOException iOException3) {
                            continue label134;
                        }

                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 12) {
                            this.jjmatchedKind = 12;
                        }
                        break;
                    case 2:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_2();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 12) {
                            this.jjmatchedKind = 12;
                        }
                        break;
                    case 3:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_3();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 12) {
                            this.jjmatchedKind = 12;
                        }
                        break;
                    case 4:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_4();
                }

                if (this.jjmatchedKind == Integer.MAX_VALUE) {
                    break label134;
                }

                if (this.jjmatchedPos + 1 < ba) {
                    this.input_stream.backup(ba - this.jjmatchedPos - 1);
                }

                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                    ZkmScriptToken zkmScriptToken3 = this.jjFillToken();
                    zkmScriptToken3.specialToken = zkmScriptToken;
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }

                    return zkmScriptToken3;
                }

                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                    int[] be;
                    if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                        ZkmScriptToken zkmScriptToken2 = this.jjFillToken();
                        if (zkmScriptToken == null) {
                            zkmScriptToken = zkmScriptToken2;
                        } else {
                            zkmScriptToken2.specialToken = zkmScriptToken;
                            zkmScriptToken = zkmScriptToken.next = zkmScriptToken2;
                        }

                        this.SkipLexicalActions();
                        be = jjnewLexState;
                    } else {
                        this.SkipLexicalActions();
                        be = jjnewLexState;
                    }

                    if (be[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    break;
                }

                this.MoreLexicalActions();
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }

                ba = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;

                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException iOException2) {
                    break label134;
                }

                curLexState = this.curLexState;
            }
        }

        int column = this.input_stream.getColumn();
        int endColumn = this.input_stream.getEndColumn();
        String string = null;
        boolean bl = false;

        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        } catch (IOException iOException1) {
            bl = true;
            string = ba <= 1 ? "" : this.input_stream.GetImage();
            if (this.curChar != '\n' && this.curChar != '\r') {
                endColumn++;
            } else {
                column++;
                endColumn = 0;
            }
        }

        if (!bl) {
            this.input_stream.backup(1);
            string = ba <= 1 ? "" : this.input_stream.GetImage();
        }

        throw new ZkmScriptTokenMgrError(bl, this.curLexState, column, endColumn, string, this.curChar);
    }

    public int jjMoveStringLiteralDfa11_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bh = bd;
        long bi = bf;
        long bg = bb;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(9, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(10, 0L, bg, bh, bi);
            return 11;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 1048576L, bi, 0L);
            case 'D':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 536870912L, bi, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 0L, bi, 48L);
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'O':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'h':
            case 'j':
            case 'q':
            case 'u':
            case 'v':
            case 'x':
            default:
                return this.jjStartNfa_0(10, 0L, bg, bh, bi);
            case 'I':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 281474976710656L, bi, 0L);
            case 'L':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 32768L, bi, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 648518347415093248L, bi, 65536L);
            case 'N':
                return this.jjMoveStringLiteralDfa12_0(bg, 281474976710656L, bh, 0L, bi, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa12_0(bg, 144115188075855872L, bh, 36028797018963968L, bi, 49152L);
            case 'a':
                return this.jjMoveStringLiteralDfa12_0(bg, 17592186044416L, bh, 2306405959167123753L, bi, 15360L);
            case 'c':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 4902203378764874752L, bi, 0L);
            case 'd':
                if ((bg & 34359738368L) != 0L) {
                    return this.jjStartNfaWithStates_0(11, 99, 26);
                }

                return this.jjMoveStringLiteralDfa12_0(bg, -7710162493338812416L, bh, 412316860416L, bi, 0L);
            case 'e':
                return this.jjMoveStringLiteralDfa12_0(bg, 54606145481867264L, bh, 19150194157551616L, bi, 391L);
            case 'f':
                return this.jjMoveStringLiteralDfa12_0(bg, 4507997673881600L, bh, 1166573040977451520L, bi, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 4294967296L, bi, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa12_0(bg, 140737488355328L, bh, 2201464340544L, bi, 512L);
            case 'k':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 144115188075855872L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa12_0(bg, 6919852845466386432L, bh, 549755813888L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa12_0(bg, 576462951326679040L, bh, 524290L, bi, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa12_0(bg, 8796093022208L, bh, -9223372028260630528L, bi, 8L);
            case 'o':
                if ((bg & 8589934592L) != 0L) {
                    return this.jjStartNfaWithStates_0(11, 97, 7);
                }

                return this.jjMoveStringLiteralDfa12_0(bg, 137438953472L, bh, 67174400L, bi, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa12_0(bg, 9007199254740992L, bh, 4415226380288L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 148L, bi, 64L);
            case 's':
                if ((bg & 17179869184L) != 0L) {
                    return this.jjStartNfaWithStates_0(11, 98, 26);
                }

                return this.jjMoveStringLiteralDfa12_0(bg, 1161084278931456L, bh, 0L, bi, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa12_0(bg, 274877906944L, bh, 17592219598848L, bi, 0L);
            case 'w':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 34359738368L, bi, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa12_0(bg, 0L, bh, 70437463654400L, bi, 0L);
        }
    }

    public int jjMoveStringLiteralDfa36_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(34, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(35, 0L, 0L, 0L, bc);
            return 36;
        }

        switch (this.curChar) {
            case 's':
                if ((bc & 16384L) != 0L) {
                    return this.jjStartNfaWithStates_0(36, 206, 26);
                }

                return this.jjMoveStringLiteralDfa37_0(bc);
            default:
                return this.jjStartNfa_0(35, 0L, 0L, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa1_2() {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            return 1;
        }

        switch (this.curChar) {
            case '/':
                return this.jjStopAtPos(1, 10);
            default:
                return 2;
        }
    }

    public int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '*':
                return this.jjMoveStringLiteralDfa1_3();
            default:
                return 1;
        }
    }

    public int jjMoveStringLiteralDfa18_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(16, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(17, 0L, 0L, be, bf);
            return 18;
        }

        switch (this.curChar) {
            case '5':
                return this.jjMoveStringLiteralDfa19_0(be, 8192L, bf, 0L);
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'B':
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'q':
            case 'v':
            case 'w':
            case 'y':
            default:
                return this.jjStartNfa_0(17, 0L, 0L, be, bf);
            case 'A':
                return this.jjMoveStringLiteralDfa19_0(be, 33554432L, bf, 0L);
            case 'C':
                return this.jjMoveStringLiteralDfa19_0(be, 2199023255552L, bf, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa19_0(be, 2305843009213693952L, bf, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa19_0(be, 32768L, bf, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa19_0(be, 576460752303423488L, bf, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa19_0(be, 1152921504606846976L, bf, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa19_0(be, 72057595124252672L, bf, 65536L);
            case 'b':
                return this.jjMoveStringLiteralDfa19_0(be, 34359738368L, bf, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa19_0(be, 19327352832L, bf, 0L);
            case 'd':
                if ((be & 512L) != 0L) {
                    return this.jjStartNfaWithStates_0(18, 137, 26);
                } else {
                    if ((be & 4096L) != 0L) {
                        return this.jjStartNfaWithStates_0(18, 140, 26);
                    }

                    return this.jjMoveStringLiteralDfa19_0(be, 65536L, bf, 0L);
                }
            case 'e':
                return this.jjMoveStringLiteralDfa19_0(be, 36033203655426048L, bf, 49152L);
            case 'f':
                return this.jjMoveStringLiteralDfa19_0(be, 281474976710656L, bf, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa19_0(be, 0L, bf, 32L);
            case 'l':
                return this.jjMoveStringLiteralDfa19_0(be, 8796428566528L, bf, 8L);
            case 'm':
                return this.jjMoveStringLiteralDfa19_0(be, 2097152L, bf, 512L);
            case 'n':
                return this.jjMoveStringLiteralDfa19_0(be, 4611758174012178432L, bf, 7L);
            case 'o':
                return this.jjMoveStringLiteralDfa19_0(be, 18014398510399488L, bf, 16L);
            case 'p':
                return this.jjMoveStringLiteralDfa19_0(be, 0L, bf, 4096L);
            case 'r':
                return this.jjMoveStringLiteralDfa19_0(be, 17592722915328L, bf, 10240L);
            case 's':
                if ((be & 128L) != 0L) {
                    return this.jjStartNfaWithStates_0(18, 135, 26);
                } else if ((be & 256L) != 0L) {
                    return this.jjStartNfaWithStates_0(18, 136, 26);
                } else {
                    if ((be & 1024L) != 0L) {
                        return this.jjStartNfaWithStates_0(18, 138, 26);
                    }

                    ZkmScriptTokenManager zkmScriptTokenManager1;
                    long bg;
                    long bh;
                    if ((be & 2048L) != 0L) {
                        this.jjmatchedKind = 139;
                        this.jjmatchedPos = 18;
                        zkmScriptTokenManager1 = this;
                        bg = be;
                        bh = 301917096894267392L;
                    } else {
                        zkmScriptTokenManager1 = this;
                        bg = be;
                        bh = 301917096894267392L;
                    }

                    return zkmScriptTokenManager1.jjMoveStringLiteralDfa19_0(bg, bh, bf, 1024L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa19_0(be, 148055842045820928L, bf, 64L);
            case 'u':
                return this.jjMoveStringLiteralDfa19_0(be, 16777216L, bf, 384L);
            case 'x':
                return this.jjMoveStringLiteralDfa19_0(be, 343597383680L, bf, 0L);
            case 'z':
                return this.jjMoveStringLiteralDfa19_0(be, Long.MIN_VALUE, bf, 0L);
        }
    }

    public void SkipLexicalActions() {
        switch (this.jjmatchedKind) {
        }
    }

    public int jjMoveStringLiteralDfa7_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bh = bd;
        long bi = bf;
        long bg = bb;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(5, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(6, 0L, bg, bh, bi);
            return 7;
        }

        switch (this.curChar) {
            case '-':
                return this.jjMoveStringLiteralDfa8_0(bg, 8589934592L, bh, 0L, bi, 0L);
            case '>':
                if ((bg & 512L) != 0L) {
                    return this.jjStopAtPos(7, 73);
                }
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'N':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'k':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            case 'y':
            default:
                return this.jjStartNfa_0(6, 0L, bg, bh, bi);
            case 'C':
                return this.jjMoveStringLiteralDfa8_0(bg, 0L, bh, 17L, bi, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa8_0(bg, 0L, bh, 70437463654400L, bi, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa8_0(bg, 4096L, bh, 4294967296L, bi, 0L);
            case 'L':
                return this.jjMoveStringLiteralDfa8_0(bg, 0L, bh, 1048576L, bi, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa8_0(bg, 0L, bh, 2L, bi, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa8_0(bg, 33554432L, bh, 0L, bi, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa8_0(bg, 0L, bh, 4194304L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa8_0(bg, 1153502063926181888L, bh, -9223372036720525304L, bi, 391L);
            case 'b':
                return this.jjMoveStringLiteralDfa8_0(bg, 2147483648L, bh, 0L, bi, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa8_0(bg, 140737488879616L, bh, 8796093022208L, bi, 0L);
            case 'd':
                if ((bg & 1024L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 74, 26);
                }

                return this.jjMoveStringLiteralDfa8_0(bg, 36028797018996736L, bh, 18014398543306752L, bi, 0L);
            case 'e':
                if ((bg & 64L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 70, 26);
                } else if ((bg & 128L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 71, 26);
                } else {
                    if ((bg & 2048L) != 0L) {
                        return this.jjStartNfaWithStates_0(7, 75, 26);
                    }

                    return this.jjMoveStringLiteralDfa8_0(bg, -8929512160900153344L, bh, 146367400206417952L, bi, 16L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa8_0(bg, 16384L, bh, 2305843009213693952L, bi, 6144L);
            case 'g':
                return this.jjMoveStringLiteralDfa8_0(bg, 35184376283136L, bh, 2199023255552L, bi, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa8_0(bg, 4294967296L, bh, 0L, bi, 8192L);
            case 'i':
                return this.jjMoveStringLiteralDfa8_0(bg, 72057594667081728L, bh, 72340726872015872L, bi, 65544L);
            case 'l':
                return this.jjMoveStringLiteralDfa8_0(bg, 283675073708032L, bh, 276824128L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa8_0(bg, 0L, bh, 1610612736L, bi, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa8_0(bg, 34495004672L, bh, 5188714170270351360L, bi, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa8_0(bg, 578792266710122496L, bh, 1125899906978304L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa8_0(bg, 1099511627776L, bh, 2164260864L, bi, 544L);
            case 's':
                return this.jjMoveStringLiteralDfa8_0(bg, 171141183886589952L, bh, 256L, bi, 0L);
            case 't':
                if ((bg & 256L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 72, 26);
                }

                return this.jjMoveStringLiteralDfa8_0(bg, 137439150080L, bh, 1490867398589352068L, bi, 49216L);
            case 'u':
                return this.jjMoveStringLiteralDfa8_0(bg, 2305843284091600896L, bh, 524288L, bi, 1024L);
            case 'x':
                return this.jjMoveStringLiteralDfa8_0(bg, 4611686087146864640L, bh, 17592186044416L, bi, 0L);
            case 'z':
                return this.jjMoveStringLiteralDfa8_0(bg, 2097152L, bh, 0L, bi, 0L);
        }
    }

    public int jjMoveStringLiteralDfa2_0(long ba, long bb, long bc, long bd, long be, long bf, long bg, long bh) {
        long bl = bh;
        long bi = bb;
        long bj = bd;
        long bk = bf;
        if (((bi = bi & ba) | (bj = bj & bc) | (bk = bk & be) | (bl = bl & bg)) == 0L) {
            return this.jjStartNfa_0(0, ba, bc, be, bg);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(1, bi, bj, bk, bl);
            return 2;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa3_0(bi, 2251799813685248L, bj, 0L, bk, 0L, bl, 0L);
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'O':
            case 'P':
            case 'Q':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'q':
            default:
                return this.jjStartNfa_0(1, bi, bj, bk, bl);
            case 'I':
                return this.jjMoveStringLiteralDfa3_0(bi, 17592186044416L, bj, 4294967296L, bk, 0L, bl, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 0L, bk, 512L, bl, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 0L, bk, 16384L, bl, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 17592186044416L, bk, 0L, bl, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa3_0(bi, 396598242185314304L, bj, 2322718314790912L, bk, 549755813888L, bl, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa3_0(bi, 144115188075855872L, bj, 0L, bk, 0L, bl, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 1108101562377L, bk, 18014402810085392L, bl, 0L);
            case 'd':
                if ((bi & 274877906944L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 38, 26);
                }

                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 2199023255616L, bk, 2097152L, bl, 0L);
            case 'e':
                return this.jjMoveStringLiteralDfa3_0(bi, 4947802324992L, bj, 1154047405050609696L, bk, 72339069014642724L, bl, 73728L);
            case 'f':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 137439084544L, bk, 1454838601568290944L, bl, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa3_0(bi, 140737488355328L, bj, 4503599694479360L, bk, 1099511627776L, bl, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 1024L, bk, 0L, bl, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa3_0(bi, 13616351998377984L, bj, 2594354930135334928L, bk, 2199023255552L, bl, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 144115188075855872L, bk, 0L, bl, 0L);
            case 'l':
                ZkmScriptTokenManager zkmScriptTokenManager1;
                long bm;
                long bn;
                if ((bi & 137438953472L) != 0L) {
                    this.jjmatchedKind = 37;
                    this.jjmatchedPos = 2;
                    zkmScriptTokenManager1 = this;
                    bm = bi;
                    bn = 1153484454560268288L;
                } else {
                    zkmScriptTokenManager1 = this;
                    bm = bi;
                    bn = 1153484454560268288L;
                }

                return zkmScriptTokenManager1.jjMoveStringLiteralDfa3_0(bm, bn, bj, 9007199254743552L, bk, 184102261851422722L, bl, 16L);
            case 'm':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, Long.MIN_VALUE, bk, 412316860416L, bl, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa3_0(bi, -9204222942345428992L, bj, 36802932736L, bk, 10754195456L, bl, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 274882363392L, bk, 32768L, bl, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 134217728L, bk, 33562624L, bl, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa3_0(bi, 6917529027641081856L, bj, 128L, bk, 4415226380288L, bl, 1536L);
            case 's':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 4683893146046693632L, bk, -1729373459742981120L, bl, 55400L);
            case 't':
                return this.jjMoveStringLiteralDfa3_0(bi, 576460752303423488L, bj, 598134326034436L, bk, 70437874696256L, bl, 391L);
            case 'u':
                return this.jjMoveStringLiteralDfa3_0(bi, 3298534883328L, bj, 0L, bk, 0L, bl, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 33554434L, bk, 0L, bl, 0L);
            case 'w':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 630508345878380544L, bk, 9L, bl, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa3_0(bi, 0L, bj, 17179869184L, bk, 256L, bl, 0L);
        }
    }

    public int jjMoveNfa_1() {
        int ba = 0;
        int bb = 0;
        this.jjnewStateCnt = 3;
        int bc = 1;
        this.jjstateSet[0] = 0;
        int bd = Integer.MAX_VALUE;

        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }

            if (this.curChar < '@') {
                long be = 1L << this.curChar;
                int[] jjstateSet = this.jjstateSet;

                while (true) {
                    bc += -1;
                    switch (jjstateSet[bc]) {
                        case 0:
                            char bh;
                            if ((13312L & be) != 0L) {
                                if (bd > 9) {
                                    bd = 9;
                                    bh = this.curChar;
                                } else {
                                    bh = this.curChar;
                                }
                            } else {
                                bh = this.curChar;
                            }

                            if (bh == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if (this.curChar == '\n' && bd > 9) {
                                bd = 9;
                            }
                            break;
                        case 2:
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                    }

                    if (bc == bb) {
                        break;
                    }

                    jjstateSet = this.jjstateSet;
                }
            } else if (this.curChar < 128) {
                int[] bf = this.jjstateSet;

                while (true) {
                    bc += -1;
                    switch (bf[bc]) {
                    }

                    if (bc == bb) {
                        break;
                    }

                    bf = this.jjstateSet;
                }
            } else {
                int[] bi = this.jjstateSet;

                while (true) {
                    bc += -1;
                    switch (bi[bc]) {
                    }

                    if (bc == bb) {
                        break;
                    }

                    bi = this.jjstateSet;
                }
            }

            if (bd != Integer.MAX_VALUE) {
                this.jjmatchedKind = bd;
                this.jjmatchedPos = ba;
                bd = Integer.MAX_VALUE;
            }

            ba++;
            if ((bc = this.jjnewStateCnt) == (bb = 3 - (this.jjnewStateCnt = bb))) {
                return ba;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException iOException) {
                return ba;
            }
        }
    }

    public int jjMoveStringLiteralDfa14_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bi = bf;
        long bg = bb;
        long bh = bd;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(12, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(13, 0L, bg, bh, bi);
            return 14;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 4294967296L, bi, 0L);
            case 'D':
            case 'G':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'O':
            case 'Q':
            case 'T':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'd':
            case 'g':
            case 'j':
            case 'k':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(13, 0L, bg, bh, bi);
            case 'E':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 16777216L, bi, 4096L);
            case 'F':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 2305843009213726728L, bi, 0L);
            case 'H':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 268435520L, bi, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 32L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 256L, bi, 1024L);
            case 'P':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 8388608L, bi, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 0L, bi, 2048L);
            case 'S':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 8192L, bi, 0L);
            case 'U':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 2147483648L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 36029346774777856L, bi, 49152L);
            case 'c':
                return this.jjMoveStringLiteralDfa15_0(bg, 1152921504606846976L, bh, 135680L, bi, 64L);
            case 'e':
                if ((bg & 35184372088832L) != 0L) {
                    return this.jjStartNfaWithStates_0(14, 109, 26);
                }

                return this.jjMoveStringLiteralDfa15_0(bg, 9288674231451648L, bh, 1330954436503931009L, bi, 24L);
            case 'f':
                return this.jjMoveStringLiteralDfa15_0(bg, 1125899906842624L, bh, 0L, bi, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 72057595111669760L, bi, 65536L);
            case 'i':
                return this.jjMoveStringLiteralDfa15_0(bg, 576460752303423488L, bh, -9223299400363671548L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa15_0(bg, -9025213653250473984L, bh, 1126312257273856L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa15_0(bg, 360287970189639680L, bh, 0L, bi, 8192L);
            case 'n':
                if ((bg & 70368744177664L) != 0L) {
                    return this.jjStartNfaWithStates_0(14, 110, 26);
                }

                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 17592253153282L, bi, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 849939670237184L, bi, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 4611686018427387904L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa15_0(bg, 5066549580791808L, bh, 134217728L, bi, 423L);
            case 's':
                if ((bg & 140737488355328L) != 0L) {
                    return this.jjStartNfaWithStates_0(14, 111, 26);
                }

                return this.jjMoveStringLiteralDfa15_0(bg, 6917529027641081856L, bh, 576460752304473104L, bi, 512L);
            case 't':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 288265594883538944L, bi, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa15_0(bg, 2251799813685248L, bh, 0L, bi, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 65536L, bi, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa15_0(bg, 0L, bh, 8796093022208L, bi, 0L);
        }
    }

    public int jjMoveStringLiteralDfa30_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(28, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(29, 0L, 0L, 0L, bc);
            return 30;
        }

        switch (this.curChar) {
            case 'L':
                return this.jjMoveStringLiteralDfa31_0(bc, 512L);
            case 'a':
                return this.jjMoveStringLiteralDfa31_0(bc, 1024L);
            case 'c':
                return this.jjMoveStringLiteralDfa31_0(bc, 8192L);
            case 'i':
                return this.jjMoveStringLiteralDfa31_0(bc, 4096L);
            case 'l':
                return this.jjMoveStringLiteralDfa31_0(bc, 16384L);
            case 'n':
                return this.jjMoveStringLiteralDfa31_0(bc, 32832L);
            case 'o':
                return this.jjMoveStringLiteralDfa31_0(bc, 67584L);
            case 's':
                if ((bc & 32L) != 0L) {
                    return this.jjStartNfaWithStates_0(30, 197, 26);
                }
            default:
                return this.jjStartNfa_0(29, 0L, 0L, 0L, bc);
            case 'u':
                return this.jjMoveStringLiteralDfa31_0(bc, 384L);
        }
    }

    public int jjMoveStringLiteralDfa1_0(long ba, long bb, long bc, long bd) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(0, ba, bb, bc, bd);
            return 1;
        }

        switch (this.curChar) {
            case '&':
                if ((ba & 34359738368L) != 0L) {
                    return this.jjStopAtPos(1, 35);
                }
            case '\'':
            case '(':
            case ')':
            case '+':
            case ',':
            case '-':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'd':
            case 'j':
            case 'k':
            case 'q':
            case 'v':
            case 'w':
            case 'z':
            case '{':
            default:
                break;
            case '*':
                if ((ba & 256L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 8, 0);
                }
                break;
            case '/':
                if ((ba & 64L) != 0L) {
                    return this.jjStopAtPos(1, 6);
                }
                break;
            case 'S':
                return this.jjMoveStringLiteralDfa2_0(ba, 2251799813685248L, bb, 0L, bc, 0L, bd, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa2_0(ba, -8646348334597931008L, bb, 216172790739369994L, bc, 0L, bd, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 137439084800L, bc, 1454838601568290944L, bd, 0L);
            case 'c':
                if ((ba & 68719476736L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 36, 26);
                }

                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 640L, bc, 18014398509744128L, bd, 0L);
            case 'e':
                return this.jjMoveStringLiteralDfa2_0(ba, 1225264971667996672L, bb, -2821914184336277504L, bc, -1653075701521181653L, bd, 131071L);
            case 'f':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 4294967296L, bc, 512L, bd, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 67108864L, bc, 1108101562368L, bd, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa2_0(ba, 4611686018427387904L, bb, 2322718313676800L, bc, 0L, bd, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa2_0(ba, 19281035904679936L, bb, 2218350608384L, bc, 2097408L, bd, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa2_0(ba, 9007336693694464L, bb, 9007474132713472L, bc, 36029346774777856L, bd, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 134217728L, bc, 0L, bd, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa2_0(ba, 1374389534720L, bb, 288529443323413504L, bc, 70441763880960L, bd, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa2_0(ba, 2305851805306716160L, bb, 1099796846656L, bc, 144115190240149504L, bd, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa2_0(ba, 36029346774777856L, bb, 0L, bc, 4L, bd, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa2_0(ba, 4611351766892544L, bb, 69798739984L, bc, 16L, bd, 0L);
            case 's':
                return this.jjMoveStringLiteralDfa2_0(ba, 17592186044416L, bb, 2305843009213693952L, bc, 524288L, bd, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa2_0(ba, 288230376151711744L, bb, 0L, bc, 4415226380288L, bd, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa2_0(ba, 144115188075855872L, bb, 0L, bc, 276824128L, bd, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 35184372088869L, bc, 2199056949248L, bd, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 34359746560L, bc, 0L, bd, 0L);
            case '|':
                if ((ba & 17179869184L) != 0L) {
                    return this.jjStopAtPos(1, 34);
                }
        }

        return this.jjStartNfa_0(0, ba, bb, bc, bd);
    }

    public int jjMoveStringLiteralDfa5_0(long ba, long bb, long bc, long bd, long be, long bf, long bg, long bh) {
        long bk = bf;
        long bj = bd;
        long bl = bh;
        long bi = bb;
        if (((bi = bi & ba) | (bj = bj & bc) | (bk = bk & be) | (bl = bl & bg)) == 0L) {
            return this.jjStartNfa_0(3, ba, bc, be, bg);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(4, bi, bj, bk, bl);
            return 5;
        }

        switch (this.curChar) {
            case '>':
                if ((bi & 9007199254740992L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 53, 9);
                }

                if ((bi & 18014398509481984L) != 0L) {
                    return this.jjStopAtPos(5, 54);
                }
            case '?':
            case '@':
            case 'B':
            case 'D':
            case 'H':
            case 'J':
            case 'K':
            case 'N':
            case 'P':
            case 'Q':
            case 'U':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'k':
            case 'q':
            case 'w':
            default:
                break;
            case 'A':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 35184372088832L, bk, 8796093022208L, bl, 0L);
            case 'C':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 17179869184L, bk, -9223372036854742784L, bl, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 140737488355328L, bk, 0L, bl, 8L);
            case 'F':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 4611686018427387904L, bk, 0L, bl, 0L);
            case 'G':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 8796093022208L, bk, 0L, bl, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 4503599627370496L, bk, 576461302059237376L, bl, 64L);
            case 'L':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 0L, bk, 4611686018427387904L, bl, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 0L, bk, 36028797018963968L, bl, 49152L);
            case 'O':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 0L, bk, 2305843009213693952L, bl, 6144L);
            case 'R':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 0L, bk, 1073741824L, bl, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 4096L, bk, 0L, bl, 32L);
            case 'T':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 0L, bk, 1024L, bl, 0L);
            case 'V':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 1099511627776L, bk, 0L, bl, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 3485786111593152832L, bk, 0L, bl, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 274877907072L, bk, 0L, bl, 0L);
            case 'c':
                if ((bi & 144115188075855872L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 57, 26);
                }

                if ((bi & 288230376151711744L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 58, 26);
                }

                return this.jjMoveStringLiteralDfa6_0(bi, bj, 17729625392128L, bk, 1454838601568290952L, bl, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 562949953421317L, bk, 134217728L, bl, 391L);
            case 'e':
                if ((bi & 4503599627370496L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 52, 26);
                }

                if ((bi & 36028797018963968L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 55, 26);
                }

                if ((bi & 576460752303423488L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 59, 26);
                }

                ZkmScriptTokenManager zkmScriptTokenManager1;
                long ca;
                long ce;
                long cf;
                if ((bi & 1152921504606846976L) != 0L) {
                    this.jjmatchedKind = 60;
                    this.jjmatchedPos = 5;
                    zkmScriptTokenManager1 = this;
                    ca = bi;
                    long cc = 0L;
                    ce = bj;
                    cf = -8895382770486861824L;
                } else {
                    zkmScriptTokenManager1 = this;
                    ca = bi;
                    long cd = 0L;
                    ce = bj;
                    cf = -8895382770486861824L;
                }

                long bm = 9744L;
                long bn = bl;
                long bo = 4029135404417606L;
                long bp = bk;
                long bq = cf;
                long br = ce;
                long bs = ca;
                return zkmScriptTokenManager1.jjMoveStringLiteralDfa6_0(bs, br, bq, bp, bo, bn, bm);
            case 'f':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 524288L, bk, 4096L, bl, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 8589934600L, bk, 4415226380288L, bl, 0L);
            case 'h':
                if ((bi & 72057594037927936L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 56, 26);
                }
                break;
            case 'i':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 2199850584576L, bk, 72341268037894144L, bl, 65536L);
            case 'l':
                if ((bi & 2305843009213693952L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 61, 26);
                }

                return this.jjMoveStringLiteralDfa6_0(bi, bj, 144115188109443074L, bk, 0L, bl, 0L);
            case 'm':
                ZkmScriptTokenManager zkmScriptTokenManager2;
                long cg;
                long ci;
                long cj;
                if ((bi & Long.MIN_VALUE) != 0L) {
                    this.jjmatchedKind = 63;
                    this.jjmatchedPos = 5;
                    zkmScriptTokenManager2 = this;
                    cg = bi;
                    long ch = 0L;
                    ci = bj;
                    cj = 4398182825984L;
                } else {
                    zkmScriptTokenManager2 = this;
                    cg = bi;
                    long cb = 0L;
                    ci = bj;
                    cj = 4398182825984L;
                }

                long bt = 0L;
                long bu = bl;
                long bv = 1L;
                long bw = bk;
                long bx = cj;
                long by = ci;
                long bz = cg;
                return zkmScriptTokenManager2.jjMoveStringLiteralDfa6_0(bz, by, bx, bw, bv, bu, bt);
            case 'n':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 68719476736L, bk, 2164260896L, bl, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 72057594037927936L, bk, 0L, bl, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 65536L, bk, 144115192376066048L, bl, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 38654722048L, bk, 0L, bl, 0L);
            case 's':
                if ((bi & 4611686018427387904L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 62, 26);
                }

                return this.jjMoveStringLiteralDfa6_0(bi, bj, 67108864L, bk, 18015498021371904L, bl, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 576460752303423536L, bk, 35790848L, bl, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 2147483648L, bk, 0L, bl, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 0L, bk, 16L, bl, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa6_0(bi, bj, 1073741824L, bk, 0L, bl, 0L);
        }

        return this.jjStartNfa_0(4, bi, bj, bk, bl);
    }

    public int jjMoveStringLiteralDfa9_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bi = bf;
        long bg = bb;
        long bh = bd;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(7, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(8, 0L, bg, bh, bi);
            return 9;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa10_0(bg, 17592186044416L, bh, 290L, bi, 0L);
            case 'D':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'Q':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'f':
            case 'j':
            case 'k':
            case 'q':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(8, 0L, bg, bh, bi);
            case 'E':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 4899951578951189504L, bi, 0L);
            case 'F':
                return this.jjMoveStringLiteralDfa10_0(bg, 2322855752630272L, bh, 67174400L, bi, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa10_0(bg, 268435456L, bh, 0L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa10_0(bg, 576462951326679040L, bh, 0L, bi, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 135680L, bi, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 128L, bi, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 1166573040977315840L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa10_0(bg, 54607244993495040L, bh, 144115188210073601L, bi, 391L);
            case 'c':
                return this.jjMoveStringLiteralDfa10_0(bg, 1152921779484753920L, bh, 70437742592064L, bi, 1024L);
            case 'd':
                if ((bg & 33554432L) != 0L) {
                    return this.jjStartNfaWithStates_0(9, 89, 26);
                }

                return this.jjMoveStringLiteralDfa10_0(bg, 4611686019501129728L, bh, 4L, bi, 8192L);
            case 'e':
                ZkmScriptTokenManager zkmScriptTokenManager1;
                long bj;
                long bk;
                if ((bg & 67108864L) != 0L) {
                    this.jjmatchedKind = 90;
                    this.jjmatchedPos = 9;
                    zkmScriptTokenManager1 = this;
                    bj = bg;
                    bk = 2449993381661638656L;
                } else {
                    zkmScriptTokenManager1 = this;
                    bj = bg;
                    bk = 2449993381661638656L;
                }

                return zkmScriptTokenManager1.jjMoveStringLiteralDfa10_0(bj, bk, bh, 20890720927752L, bi, 0L);
            case 'g':
                if ((bg & 16777216L) != 0L) {
                    return this.jjStartNfaWithStates_0(9, 88, 26);
                }

                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 32768L, bi, 64L);
            case 'h':
                return this.jjMoveStringLiteralDfa10_0(bg, -8935141660703064064L, bh, 412316860416L, bi, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa10_0(bg, 73183493944770560L, bh, 549755822080L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa10_0(bg, 69256347648L, bh, 72339069014638592L, bi, 65536L);
            case 'm':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 16L, bi, 0L);
            case 'n':
                if ((bg & 8388608L) != 0L) {
                    return this.jjStartNfaWithStates_0(9, 87, 26);
                }

                return this.jjMoveStringLiteralDfa10_0(bg, 8589934592L, bh, 34394341376L, bi, 32L);
            case 'o':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 36591746972385280L, bi, 49152L);
            case 'p':
                return this.jjMoveStringLiteralDfa10_0(bg, 8796093022208L, bh, 2164260864L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa10_0(bg, 4400193994752L, bh, 579842867254525952L, bi, 0L);
            case 's':
                if ((bg & 134217728L) != 0L) {
                    return this.jjStartNfaWithStates_0(9, 91, 26);
                }

                return this.jjMoveStringLiteralDfa10_0(bg, 9288691411320832L, bh, -6917520222958125056L, bi, 6144L);
            case 't':
                return this.jjMoveStringLiteralDfa10_0(bg, 4503599627370496L, bh, 4832362496L, bi, 8L);
            case 'u':
                return this.jjMoveStringLiteralDfa10_0(bg, 140737488355328L, bh, 0L, bi, 528L);
            case 'v':
                return this.jjMoveStringLiteralDfa10_0(bg, 4294967296L, bh, 1073741824L, bi, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa10_0(bg, 0L, bh, 18014398509744128L, bi, 0L);
            case 'z':
                return this.jjMoveStringLiteralDfa10_0(bg, 34359738368L, bh, 0L, bi, 0L);
        }
    }

    public static final boolean jjCanMove_1(int ba, int bb, long bc) {
        switch (ba) {
            case 0:
                return (jjbitVec2[bb] & bc) != 0L;
            default:
                return false;
        }
    }

    public int jjMoveStringLiteralDfa20_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(18, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(19, 0L, 0L, be, bf);
            return 20;
        }

        switch (this.curChar) {
            case '5':
                return this.jjMoveStringLiteralDfa21_0(be, 33554432L, bf, 0L);
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'B':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            case 'y':
            default:
                return this.jjStartNfa_0(19, 0L, 0L, be, bf);
            case 'A':
                return this.jjMoveStringLiteralDfa21_0(be, 0L, bf, 16L);
            case 'C':
                return this.jjMoveStringLiteralDfa21_0(be, 36028797018963968L, bf, 16384L);
            case 'D':
                return this.jjMoveStringLiteralDfa21_0(be, 144115188075855872L, bf, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa21_0(be, 18014398509481984L, bf, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa21_0(be, 0L, bf, 8192L);
            case 'O':
                return this.jjMoveStringLiteralDfa21_0(be, 0L, bf, 32768L);
            case 'a':
                return this.jjMoveStringLiteralDfa21_0(be, 72200530549538816L, bf, 65536L);
            case 'b':
                return this.jjMoveStringLiteralDfa21_0(be, 17592186044416L, bf, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa21_0(be, 2305856211943161856L, bf, 384L);
            case 'd':
                return this.jjMoveStringLiteralDfa21_0(be, 67108864L, bf, 0L);
            case 'e':
                if ((be & 8388608L) != 0L) {
                    return this.jjStartNfaWithStates_0(20, 151, 26);
                } else {
                    if ((be & 16777216L) != 0L) {
                        return this.jjStartNfaWithStates_0(20, 152, 26);
                    }

                    return this.jjMoveStringLiteralDfa21_0(be, 671088640L, bf, 7L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa21_0(be, 576460752303423488L, bf, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa21_0(be, 3940649673949184L, bf, 4096L);
            case 'l':
                return this.jjMoveStringLiteralDfa21_0(be, 482110078976L, bf, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa21_0(be, 1099511627776L, bf, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa21_0(be, 292733980342484992L, bf, 2592L);
            case 'o':
                return this.jjMoveStringLiteralDfa21_0(be, 0L, bf, 64L);
            case 'r':
                return this.jjMoveStringLiteralDfa21_0(be, 1152922054362660864L, bf, 1024L);
            case 's':
                if ((be & 2097152L) != 0L) {
                    return this.jjStartNfaWithStates_0(20, 149, 26);
                } else {
                    if ((be & 4194304L) != 0L) {
                        return this.jjStartNfaWithStates_0(20, 150, 26);
                    }

                    return this.jjMoveStringLiteralDfa21_0(be, 281474976710656L, bf, 0L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa21_0(be, -9223372002495037440L, bf, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa21_0(be, 19327352832L, bf, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa21_0(be, 4620798770798395392L, bf, 0L);
            case 'z':
                return this.jjMoveStringLiteralDfa21_0(be, 0L, bf, 8L);
        }
    }

    public int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '*':
                return this.jjMoveStringLiteralDfa1_2();
            default:
                return 1;
        }
    }

    public int jjMoveStringLiteralDfa28_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(26, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(27, 0L, 0L, be, bf);
            return 28;
        }

        switch (this.curChar) {
            case 'c':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 1408L);
            case 'e':
                if ((bf & 1L) != 0L) {
                    return this.jjStartNfaWithStates_0(28, 192, 26);
                } else if ((bf & 2L) != 0L) {
                    return this.jjStartNfaWithStates_0(28, 193, 26);
                } else {
                    if ((bf & 4L) != 0L) {
                        return this.jjStartNfaWithStates_0(28, 194, 26);
                    }

                    return this.jjMoveStringLiteralDfa29_0(be, bf, 24L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 65536L);
            case 'g':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 512L);
            case 'i':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 32832L);
            case 'o':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 32L);
            case 'r':
                if ((be & Long.MIN_VALUE) != 0L) {
                    return this.jjStartNfaWithStates_0(28, 191, 26);
                }
            case 'd':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'p':
            case 'q':
            case 't':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(27, 0L, 0L, be, bf);
            case 's':
                if ((be & 4611686018427387904L) != 0L) {
                    return this.jjStartNfaWithStates_0(28, 190, 26);
                }

                return this.jjMoveStringLiteralDfa29_0(be, bf, 2048L);
            case 'u':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 12288L);
            case 'x':
                return this.jjMoveStringLiteralDfa29_0(be, bf, 16384L);
        }
    }

    public int jjMoveStringLiteralDfa12_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bi = bf;
        long bh = bd;
        long bg = bb;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(10, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(11, 0L, bg, bh, bi);
            return 12;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa13_0(bg, Long.MIN_VALUE, bh, 412316860416L, bi, 0L);
            case 'D':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'M':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'h':
            case 'j':
            case 'k':
            case 'q':
            case 'v':
            default:
                return this.jjStartNfa_0(11, 0L, bg, bh, bi);
            case 'E':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 0L, bi, 64L);
            case 'F':
                return this.jjMoveStringLiteralDfa13_0(bg, 54043195528445952L, bh, 1125899906842624L, bi, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa13_0(bg, 1125899906842624L, bh, 0L, bi, 0L);
            case 'L':
                return this.jjMoveStringLiteralDfa13_0(bg, 1152921504606846976L, bh, 0L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa13_0(bg, 288230376151711744L, bh, 0L, bi, 0L);
            case 'T':
                return this.jjMoveStringLiteralDfa13_0(bg, 72057594037927936L, bh, 0L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa13_0(bg, 6917810502617792512L, bh, 182397983931760768L, bi, 49152L);
            case 'c':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 5L, bi, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 8796093022208L, bi, 0L);
            case 'e':
                if ((bg & 68719476736L) != 0L) {
                    return this.jjStartNfaWithStates_0(12, 100, 26);
                } else {
                    if ((bg & 274877906944L) != 0L) {
                        return this.jjStartNfaWithStates_0(12, 102, 26);
                    }

                    ZkmScriptTokenManager zkmScriptTokenManager1;
                    long bj;
                    long bk;
                    if ((bg & 549755813888L) != 0L) {
                        this.jjmatchedKind = 103;
                        this.jjmatchedPos = 12;
                        zkmScriptTokenManager1 = this;
                        bj = bg;
                        bk = 587793418650910720L;
                    } else {
                        zkmScriptTokenManager1 = this;
                        bj = bg;
                        bk = 587793418650910720L;
                    }

                    return zkmScriptTokenManager1.jjMoveStringLiteralDfa13_0(bj, bk, bh, 1526896200908277776L, bi, 65536L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 18295873486454784L, bi, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa13_0(bg, 8796093022208L, bh, 8594145280L, bi, 8L);
            case 'i':
                return this.jjMoveStringLiteralDfa13_0(bg, 4543182045970432L, bh, -8646893142039068672L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 9216L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 10L, bi, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 34359738368L, bi, 544L);
            case 'o':
                return this.jjMoveStringLiteralDfa13_0(bg, 140737488355328L, bh, 2442166336L, bi, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 70437463654400L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 4611686018427387904L, bi, 8192L);
            case 's':
                return this.jjMoveStringLiteralDfa13_0(bg, 17592186044416L, bh, 288L, bi, 1024L);
            case 't':
                return this.jjMoveStringLiteralDfa13_0(bg, 562949953421312L, bh, 2306411474041438208L, bi, 6535L);
            case 'u':
                return this.jjMoveStringLiteralDfa13_0(bg, 144115188075855872L, bh, 135680L, bi, 0L);
            case 'w':
                ZkmScriptTokenManager zkmScriptTokenManager2;
                long bl;
                long bm;
                long bn;
                long bo;
                if ((bg & 137438953472L) != 0L) {
                    this.jjmatchedKind = 101;
                    this.jjmatchedPos = 12;
                    zkmScriptTokenManager2 = this;
                    bl = bg;
                    bm = 0L;
                    bn = bh;
                    bo = 67174400L;
                } else {
                    zkmScriptTokenManager2 = this;
                    bl = bg;
                    bm = 0L;
                    bn = bh;
                    bo = 67174400L;
                }

                return zkmScriptTokenManager2.jjMoveStringLiteralDfa13_0(bl, bm, bn, bo, bi, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa13_0(bg, 0L, bh, 0L, bi, 16L);
        }
    }

    public int jjMoveStringLiteralDfa25_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(23, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(24, 0L, 0L, be, bf);
            return 25;
        }

        switch (this.curChar) {
            case 'O':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 8192L);
            case 'a':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 8L);
            case 'b':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 16L);
            case 'c':
                return this.jjMoveStringLiteralDfa26_0(be, 576460752303423488L, bf, 34816L);
            case 'd':
                if ((be & 281474976710656L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 176, 26);
                }

                return this.jjMoveStringLiteralDfa26_0(be, 18014398509481984L, bf, 0L);
            case 'e':
                if ((be & 35184372088832L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 173, 26);
                } else if ((be & 70368744177664L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 174, 26);
                } else if ((be & 140737488355328L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 175, 26);
                } else if ((be & 4503599627370496L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 180, 26);
                } else {
                    if ((be & 9007199254740992L) != 0L) {
                        return this.jjStartNfaWithStates_0(25, 181, 26);
                    }

                    return this.jjMoveStringLiteralDfa26_0(be, 36028797018963968L, bf, 16384L);
                }
            case 'h':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 512L);
            case 'i':
                return this.jjMoveStringLiteralDfa26_0(be, 4611686018427387904L, bf, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 4L);
            case 'l':
                return this.jjMoveStringLiteralDfa26_0(be, 144115188075855872L, bf, 67L);
            case 'n':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 384L);
            case 'o':
                return this.jjMoveStringLiteralDfa26_0(be, 2305843009213693952L, bf, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 1024L);
            case 'r':
                return this.jjMoveStringLiteralDfa26_0(be, -7998392938210000896L, bf, 65536L);
            case 's':
                if ((be & 562949953421312L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 177, 26);
                } else if ((be & 1125899906842624L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 178, 26);
                } else if ((be & 2251799813685248L) != 0L) {
                    return this.jjStartNfaWithStates_0(25, 179, 26);
                }
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'f':
            case 'g':
            case 'j':
            case 'm':
            case 'q':
            case 't':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(24, 0L, 0L, be, bf);
            case 'u':
                return this.jjMoveStringLiteralDfa26_0(be, 288230376151711744L, bf, 32L);
            case 'x':
                return this.jjMoveStringLiteralDfa26_0(be, 0L, bf, 4096L);
        }
    }

    public int jjMoveStringLiteralDfa10_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bi = bf;
        long bh = bd;
        long bg = bb;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(8, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(9, 0L, bg, bh, bi);
            return 10;
        }

        switch (this.curChar) {
            case 'A':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 4L, bi, 0L);
            case 'B':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'Q':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'h':
            case 'j':
            case 'k':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(9, 0L, bg, bh, bi);
            case 'C':
                return this.jjMoveStringLiteralDfa11_0(bg, 6917529027641081856L, bh, 0L, bi, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, Long.MIN_VALUE, bi, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 1099513724928L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 8L, bi, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa11_0(bg, 9007199254740992L, bh, 0L, bi, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 0L, bi, 8192L);
            case 'R':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 18014398509744128L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa11_0(bg, 576463226204585984L, bh, 549755814016L, bi, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa11_0(bg, 1099511627776L, bh, 135680L, bi, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa11_0(bg, 1125899906842624L, bh, 2451084097196392448L, bi, 6144L);
            case 'd':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 36028797018963968L, bi, 49152L);
            case 'e':
                if ((bg & 536870912L) != 0L) {
                    this.jjmatchedKind = 93;
                    this.jjmatchedPos = 10;
                } else {
                    if ((bg & 1073741824L) != 0L) {
                        return this.jjStartNfaWithStates_0(10, 94, 26);
                    }

                    if ((bg & 4294967296L) != 0L) {
                        return this.jjStartNfaWithStates_0(10, 96, 26);
                    }
                }

                return this.jjMoveStringLiteralDfa11_0(bg, 1224983548230893568L, bh, 1817624667477805056L, bi, 65600L);
            case 'f':
                return this.jjMoveStringLiteralDfa11_0(bg, 8589934592L, bh, 0L, bi, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 1048576L, bi, 48L);
            case 'i':
                return this.jjMoveStringLiteralDfa11_0(bg, 6835114034069504L, bh, 8628224000L, bi, 8L);
            case 'l':
                return this.jjMoveStringLiteralDfa11_0(bg, 17729624997888L, bh, 67174688L, bi, 1024L);
            case 'm':
                return this.jjMoveStringLiteralDfa11_0(bg, 54606145481867264L, bh, 134217728L, bi, 391L);
            case 'n':
                if ((bg & 268435456L) != 0L) {
                    return this.jjStartNfaWithStates_0(10, 92, 26);
                }

                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 4611686018427396096L, bi, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa11_0(bg, -8935141660703064064L, bh, 446676598786L, bi, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 17592186044432L, bi, 512L);
            case 'r':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 72636486909953L, bi, 0L);
            case 's':
                if ((bg & 2147483648L) != 0L) {
                    return this.jjStartNfaWithStates_0(10, 95, 26);
                }

                return this.jjMoveStringLiteralDfa11_0(bg, 144572584913010688L, bh, 8796093022208L, bi, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 562952394506304L, bi, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa11_0(bg, 68719476736L, bh, 0L, bi, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 288265560523801600L, bi, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa11_0(bg, 0L, bh, 4415763251200L, bi, 0L);
        }
    }

    public int jjMoveStringLiteralDfa17_0(long ba, long bb, long bc, long bd, long be) {
        long bf = bc;
        long bg = be;
        if ((0L & ba | (bf = bf & bb) | (bg = bg & bd)) == 0L) {
            return this.jjStartNfa_0(15, 0L, ba, bb, bd);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(16, 0L, 0L, bf, bg);
            return 17;
        }

        switch (this.curChar) {
            case '2':
                return this.jjMoveStringLiteralDfa18_0(bf, 8192L, bg, 0L);
            case 'C':
                return this.jjMoveStringLiteralDfa18_0(bf, 1073741824L, bg, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa18_0(bf, 343597383680L, bg, 0L);
            case 'H':
                return this.jjMoveStringLiteralDfa18_0(bf, 33554432L, bg, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa18_0(bf, 137438953472L, bg, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa18_0(bf, 72057594037927936L, bg, 65536L);
            case 'U':
                return this.jjMoveStringLiteralDfa18_0(bf, 70368744177664L, bg, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa18_0(bf, 136314880L, bg, 15L);
            case 'b':
                return this.jjMoveStringLiteralDfa18_0(bf, 281474976710656L, bg, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa18_0(bf, 67108864L, bg, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa18_0(bf, 2199291691008L, bg, 0L);
            case 'e':
                if ((bf & 8L) != 0L) {
                    return this.jjStartNfaWithStates_0(17, 131, 26);
                }

                return this.jjMoveStringLiteralDfa18_0(bf, 1166574140488981248L, bg, 15360L);
            case 'f':
                return this.jjMoveStringLiteralDfa18_0(bf, 8796093022208L, bg, 384L);
            case 'g':
                return this.jjMoveStringLiteralDfa18_0(bf, 576460752303423488L, bg, 0L);
            case 'h':
                if ((bf & 64L) != 0L) {
                    return this.jjStartNfaWithStates_0(17, 134, 26);
                }
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'D':
            case 'F':
            case 'G':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'm':
            case 'q':
            case 'v':
            default:
                return this.jjStartNfa_0(16, 0L, 0L, bf, bg);
            case 'i':
                return this.jjMoveStringLiteralDfa18_0(bf, -9061242415908782080L, bg, 16L);
            case 'k':
                return this.jjMoveStringLiteralDfa18_0(bf, 8388608L, bg, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa18_0(bf, 16777216L, bg, 0L);
            case 'n':
                if ((bf & 16L) != 0L) {
                    return this.jjStartNfaWithStates_0(17, 132, 26);
                }

                return this.jjMoveStringLiteralDfa18_0(bf, 288269958571361280L, bg, 0L);
            case 'o':
                if ((bf & 32L) != 0L) {
                    return this.jjStartNfaWithStates_0(17, 133, 26);
                }

                return this.jjMoveStringLiteralDfa18_0(bf, 4611686568720072704L, bg, 512L);
            case 'p':
                return this.jjMoveStringLiteralDfa18_0(bf, 0L, bg, 64L);
            case 'r':
                return this.jjMoveStringLiteralDfa18_0(bf, 8594129024L, bg, 0L);
            case 's':
                if ((bf & 4L) != 0L) {
                    return this.jjStartNfaWithStates_0(17, 130, 26);
                }

                return this.jjMoveStringLiteralDfa18_0(bf, 4294983680L, bg, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa18_0(bf, 39987038878957568L, bg, 49184L);
            case 'u':
                return this.jjMoveStringLiteralDfa18_0(bf, 65536L, bg, 0L);
            case 'w':
                return this.jjMoveStringLiteralDfa18_0(bf, 2305843009213693952L, bg, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa18_0(bf, 19327352832L, bg, 0L);
        }
    }

    public int jjMoveStringLiteralDfa26_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(24, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(25, 0L, 0L, be, bf);
            return 26;
        }

        switch (this.curChar) {
            case 'E':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 256L);
            case 'I':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 128L);
            case 'a':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 33284L);
            case 'b':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 8192L);
            case 'c':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 4096L);
            case 'd':
                return this.jjMoveStringLiteralDfa27_0(be, -8935141660703064064L, bf, 0L);
            case 'e':
                if ((be & 18014398509481984L) != 0L) {
                    return this.jjStartNfaWithStates_0(26, 182, 26);
                }

                return this.jjMoveStringLiteralDfa27_0(be, 1729382256910270464L, bf, 1024L);
            case 'l':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 2048L);
            case 'n':
                return this.jjMoveStringLiteralDfa27_0(be, 2305843009213693952L, bf, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa27_0(be, 4611686018427387904L, bf, 0L);
            case 's':
                if ((be & 36028797018963968L) != 0L) {
                    return this.jjStartNfaWithStates_0(26, 183, 26);
                }

                ZkmScriptTokenManager zkmScriptTokenManager1;
                long bg;
                long bh;
                long bi;
                long bj;
                if ((be & 72057594037927936L) != 0L) {
                    this.jjmatchedKind = 184;
                    this.jjmatchedPos = 26;
                    zkmScriptTokenManager1 = this;
                    bg = be;
                    bh = 0L;
                    bi = bf;
                    bj = 81960L;
                } else {
                    zkmScriptTokenManager1 = this;
                    bg = be;
                    bh = 0L;
                    bi = bf;
                    bj = 81960L;
                }

                return zkmScriptTokenManager1.jjMoveStringLiteralDfa27_0(bg, bh, bi, bj);
            case 't':
                if ((be & 144115188075855872L) != 0L) {
                    return this.jjStartNfaWithStates_0(26, 185, 26);
                }
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'r':
            default:
                return this.jjStartNfa_0(25, 0L, 0L, be, bf);
            case 'u':
                return this.jjMoveStringLiteralDfa27_0(be, 0L, bf, 83L);
        }
    }

    public int jjMoveStringLiteralDfa32_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(30, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(31, 0L, 0L, 0L, bc);
            return 32;
        }

        switch (this.curChar) {
            case 'O':
                return this.jjMoveStringLiteralDfa33_0(bc, 65536L);
            case 'e':
                if ((bc & 128L) != 0L) {
                    return this.jjStartNfaWithStates_0(32, 199, 26);
                }

                if ((bc & 256L) != 0L) {
                    return this.jjStartNfaWithStates_0(32, 200, 26);
                }
                break;
            case 'g':
                if ((bc & 512L) != 0L) {
                    return this.jjStartNfaWithStates_0(32, 201, 26);
                }
                break;
            case 'n':
                return this.jjMoveStringLiteralDfa33_0(bc, 4096L);
            case 's':
                if ((bc & 1024L) != 0L) {
                    return this.jjStartNfaWithStates_0(32, 202, 26);
                }

                if ((bc & 2048L) != 0L) {
                    return this.jjStartNfaWithStates_0(32, 203, 26);
                }

                return this.jjMoveStringLiteralDfa33_0(bc, 16384L);
            case 't':
                return this.jjMoveStringLiteralDfa33_0(bc, 8192L);
            case 'x':
                return this.jjMoveStringLiteralDfa33_0(bc, 32768L);
        }

        return this.jjStartNfa_0(31, 0L, 0L, 0L, bc);
    }

    public void jjCheckNAdd(int ba) {
        if (this.jjrounds[ba] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = ba;
            this.jjrounds[ba] = this.jjround;
        }
    }

    public int jjMoveStringLiteralDfa1_3() {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            return 1;
        }

        switch (this.curChar) {
            case '/':
                return this.jjStopAtPos(1, 11);
            default:
                return 2;
        }
    }

    public int jjMoveStringLiteralDfa34_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(32, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(33, 0L, 0L, 0L, bc);
            return 34;
        }

        switch (this.curChar) {
            case 'd':
                if ((bc & 8192L) != 0L) {
                    return this.jjStartNfaWithStates_0(34, 205, 26);
                }
            default:
                return this.jjStartNfa_0(33, 0L, 0L, 0L, bc);
            case 'f':
                return this.jjMoveStringLiteralDfa35_0(bc, 65536L);
            case 'l':
                return this.jjMoveStringLiteralDfa35_0(bc, 32768L);
            case 'o':
                return this.jjMoveStringLiteralDfa35_0(bc, 16384L);
        }
    }

    public int jjMoveStringLiteralDfa33_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(31, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(32, 0L, 0L, 0L, bc);
            return 33;
        }

        switch (this.curChar) {
            case 'b':
                return this.jjMoveStringLiteralDfa34_0(bc, 65536L);
            case 'c':
                return this.jjMoveStringLiteralDfa34_0(bc, 32768L);
            case 'e':
                return this.jjMoveStringLiteralDfa34_0(bc, 8192L);
            case 'i':
                return this.jjMoveStringLiteralDfa34_0(bc, 16384L);
            case 's':
                if ((bc & 4096L) != 0L) {
                    return this.jjStartNfaWithStates_0(33, 204, 26);
                }
            default:
                return this.jjStartNfa_0(32, 0L, 0L, 0L, bc);
        }
    }

    public int jjMoveNfa_0(int ba, int bb) {
        int bc = bb;
        int bd = 0;
        this.jjnewStateCnt = 25;
        int be = 1;
        this.jjstateSet[0] = ba;
        int bf = Integer.MAX_VALUE;

        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }

            if (this.curChar < '@') {
                long bm = 1L << this.curChar;
                int[] jjstateSet = this.jjstateSet;

                while (true) {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 0:
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if ((-140737488355329L & bm) != 0L && bf > 7) {
                                bf = 7;
                            }
                            break;
                        case 2:
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 0;
                            }
                            break;
                        case 3:
                            if ((287997348674519551L & bm) != 0L) {
                                ZkmScriptTokenManager zkmScriptTokenManager8;
                                byte co;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager8 = this;
                                    long cg = 2768045378409L;
                                    co = 7;
                                } else {
                                    zkmScriptTokenManager8 = this;
                                    long ch = 2768045378409L;
                                    co = 7;
                                }

                                Integer integer = Integer.valueOf(co);
                                zkmScriptTokenManager8.jjCheckNAdd(integer);
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                            } else if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            }

                            if ((287962164302430719L & bm) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }

                            if ((287957766255919615L & bm) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAddStates();
                            }

                            if ((287948901175001088L & bm) != 0L) {
                                if (bf > 212) {
                                    bf = 212;
                                }

                                this.jjCheckNAdd(4);
                            }
                            break;
                        case 4:
                            if ((287948901175001088L & bm) != 0L) {
                                if (bf > 212) {
                                    bf = 212;
                                }

                                this.jjCheckNAdd(4);
                            }
                            break;
                        case 5:
                            if ((287962164302430719L & bm) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }
                            break;
                        case 6:
                            if ((287997348674519551L & bm) != 0L) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 7:
                            if ((576227724826231295L & bm) != 0L) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 8:
                            if (this.curChar == '>') {
                                this.jjCheckNAdd(9);
                            }
                            break;
                        case 9:
                            if ((287957766255919615L & bm) != 0L) {
                                if (bf > 216) {
                                    bf = 216;
                                }

                                this.jjCheckNAdd(9);
                            }
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        default:
                            break;
                        case 14:
                            if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                            }
                            break;
                        case 15:
                            if ((287957766255919615L & bm) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 16:
                            if ((287957766255919615L & bm) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAdd(16);
                            }
                            break;
                        case 17:
                            if ((287957766255919615L & bm) != 0L) {
                                this.jjCheckNAddTwoStates(17, 14);
                            }
                            break;
                        case 18:
                            if ((287957766255919615L & bm) != 0L) {
                                this.jjCheckNAddTwoStates(18, 24);
                            }
                            break;
                        case 19:
                            if (this.curChar == '>' && bf > 217) {
                                bf = 217;
                            }
                            break;
                        case 24:
                            if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 23;
                            }
                            break;
                        case 25:
                            long bs;
                            if ((576227724826231295L & bm) != 0L) {
                                ZkmScriptTokenManager zkmScriptTokenManager7;
                                byte cn;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager7 = this;
                                    long ce = 2768045378409L;
                                    cn = 7;
                                } else {
                                    zkmScriptTokenManager7 = this;
                                    long cf = 2768045378409L;
                                    cn = 7;
                                }

                                Integer integer1 = Integer.valueOf(cn);
                                zkmScriptTokenManager7.jjCheckNAdd(integer1);
                                bs = 287962164302430719L;
                            } else {
                                bs = 287962164302430719L;
                            }

                            if ((bs & bm) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }
                            break;
                        case 26:
                            if ((576227724826231295L & bm) != 0L) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 23;
                            }

                            if ((287962164302430719L & bm) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                            }

                            long br;
                            if ((287957766255919615L & bm) != 0L) {
                                this.jjCheckNAddTwoStates(18, 24);
                                br = 287957766255919615L;
                            } else {
                                br = 287957766255919615L;
                            }

                            if ((br & bm) != 0L) {
                                this.jjCheckNAddTwoStates(17, 14);
                            }

                            if ((287957766255919615L & bm) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAdd(16);
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    jjstateSet = this.jjstateSet;
                }
            } else if (this.curChar < 128) {
                long bl = 1L << (this.curChar & '?');
                int[] bn = this.jjstateSet;

                while (true) {
                    be += -1;
                    switch (bn[be]) {
                        case 1:
                            if (bf > 7) {
                                bf = 7;
                            }
                        case 2:
                        case 4:
                        case 8:
                        case 14:
                        case 19:
                        case 24:
                        default:
                            break;
                        case 3:
                            long bp;
                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAddStates();
                                bp = -8646911290859585538L;
                            } else {
                                bp = -8646911290859585538L;
                            }

                            if ((bp & bl) != 0L) {
                                ZkmScriptTokenManager zkmScriptTokenManager6;
                                byte cm;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager6 = this;
                                    long cc = 2768045378409L;
                                    cm = 7;
                                } else {
                                    zkmScriptTokenManager6 = this;
                                    long cd = 2768045378409L;
                                    cm = 7;
                                }

                                Integer integer2 = Integer.valueOf(cm);
                                zkmScriptTokenManager6.jjCheckNAdd(integer2);
                            }

                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }
                            break;
                        case 5:
                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }
                            break;
                        case 6:
                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 7:
                            if ((-8646911290859585537L & bl) != 0L) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 9:
                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 216) {
                                    bf = 216;
                                }

                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            }
                            break;
                        case 10:
                            if (this.curChar == 'k') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                            }
                            break;
                        case 11:
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                            }
                            break;
                        case 12:
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                            }
                            break;
                        case 13:
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 12;
                            }
                            break;
                        case 15:
                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 16:
                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAdd(16);
                            }
                            break;
                        case 17:
                            if ((-8646911290859585538L & bl) != 0L) {
                                this.jjCheckNAddTwoStates(17, 14);
                            }
                            break;
                        case 18:
                            if ((-8646911290859585538L & bl) != 0L) {
                                this.jjCheckNAddTwoStates(18, 24);
                            }
                            break;
                        case 20:
                            if (this.curChar == 'k') {
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                            }
                            break;
                        case 21:
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                            }
                            break;
                        case 22:
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 21;
                            }
                            break;
                        case 23:
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                            }
                            break;
                        case 25:
                            if ((-8646911290859585537L & bl) != 0L) {
                                ZkmScriptTokenManager zkmScriptTokenManager5;
                                byte cl;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager5 = this;
                                    long ca = 2768045378409L;
                                    cl = 7;
                                } else {
                                    zkmScriptTokenManager5 = this;
                                    long cb = 2768045378409L;
                                    cl = 7;
                                }

                                Integer integer3 = Integer.valueOf(cl);
                                zkmScriptTokenManager5.jjCheckNAdd(integer3);
                            }

                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }
                            break;
                        case 26:
                            if ((-8646911290859585537L & bl) != 0L) {
                                ZkmScriptTokenManager zkmScriptTokenManager4;
                                byte ck;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager4 = this;
                                    long by = 2768045378409L;
                                    ck = 7;
                                } else {
                                    zkmScriptTokenManager4 = this;
                                    long bz = 2768045378409L;
                                    ck = 7;
                                }

                                Integer integer4 = Integer.valueOf(ck);
                                zkmScriptTokenManager4.jjCheckNAdd(integer4);
                            }

                            if ((-8646911290859585538L & bl) != 0L) {
                                this.jjCheckNAddTwoStates(18, 24);
                            }

                            long bo;
                            if ((-8646911290859585538L & bl) != 0L) {
                                this.jjCheckNAddTwoStates(17, 14);
                                bo = -8646911290859585538L;
                            } else {
                                bo = -8646911290859585538L;
                            }

                            if ((bo & bl) != 0L) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }

                            if ((-8646911290859585538L & bl) != 0L) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAdd(16);
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    bn = this.jjstateSet;
                }
            } else {
                int bg = this.curChar >> '\b';
                int bh = bg >> 6;
                long bi = 1L << (bg & 63);
                int bj = (this.curChar & 255) >> 6;
                long bk = 1L << (this.curChar & '?');
                int[] cp = this.jjstateSet;

                while (true) {
                    be += -1;
                    switch (cp[be]) {
                        case 1:
                            if (jjCanMove_1(bg, bj, bk) && bf > 7) {
                                bf = 7;
                            }
                        case 2:
                        case 4:
                        case 8:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        default:
                            break;
                        case 3:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                ZkmScriptTokenManager zkmScriptTokenManager3;
                                byte cj;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager3 = this;
                                    long bw = 2768045378409L;
                                    cj = 7;
                                } else {
                                    zkmScriptTokenManager3 = this;
                                    long bx = 2768045378409L;
                                    cj = 7;
                                }

                                Integer integer5 = Integer.valueOf(cj);
                                zkmScriptTokenManager3.jjCheckNAdd(integer5);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 5:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }
                            break;
                        case 6:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 7:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 9:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 216) {
                                    bf = 216;
                                }

                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            }
                            break;
                        case 15:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 16:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 213) {
                                    bf = 213;
                                }

                                this.jjCheckNAdd(16);
                            }
                            break;
                        case 17:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                this.jjCheckNAddTwoStates(17, 14);
                            }
                            break;
                        case 18:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                this.jjCheckNAddTwoStates(18, 24);
                            }
                            break;
                        case 25:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 215) {
                                    bf = 215;
                                }

                                this.jjCheckNAdd(7);
                            }
                            break;
                        case 26:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                ZkmScriptTokenManager zkmScriptTokenManager1;
                                byte cr;
                                if (bf > 213) {
                                    bf = 213;
                                    zkmScriptTokenManager1 = this;
                                    long cq = 2768045378409L;
                                    cr = 16;
                                } else {
                                    zkmScriptTokenManager1 = this;
                                    long bt = 2768045378409L;
                                    cr = 16;
                                }

                                Integer integer6 = Integer.valueOf(cr);
                                zkmScriptTokenManager1.jjCheckNAdd(integer6);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                if (bf > 214) {
                                    bf = 214;
                                }

                                this.jjCheckNAdd(5);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                ZkmScriptTokenManager zkmScriptTokenManager2;
                                byte ci;
                                if (bf > 215) {
                                    bf = 215;
                                    zkmScriptTokenManager2 = this;
                                    long bu = 2768045378409L;
                                    ci = 7;
                                } else {
                                    zkmScriptTokenManager2 = this;
                                    long bv = 2768045378409L;
                                    ci = 7;
                                }

                                Integer integer7 = Integer.valueOf(ci);
                                zkmScriptTokenManager2.jjCheckNAdd(integer7);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                this.jjCheckNAddTwoStates(17, 14);
                            }

                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                this.jjCheckNAddTwoStates(18, 24);
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    cp = this.jjstateSet;
                }
            }

            if (bf != Integer.MAX_VALUE) {
                this.jjmatchedKind = bf;
                this.jjmatchedPos = bc;
                bf = Integer.MAX_VALUE;
            }

            bc++;
            if ((be = this.jjnewStateCnt) == (bd = 25 - (this.jjnewStateCnt = bd))) {
                return bc;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException iOException) {
                return bc;
            }
        }
    }

    public int jjMoveStringLiteralDfa4_0(long ba, long bb, long bc, long bd, long be, long bf, long bg, long bh) {
        long bk = bf;
        long bi = bb;
        long bj = bd;
        long bl = bh;
        if (((bi = bi & ba) | (bj = bj & bc) | (bk = bk & be) | (bl = bl & bg)) == 0L) {
            return this.jjStartNfa_0(2, ba, bc, be, bg);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(3, bi, bj, bk, bl);
            return 4;
        }

        switch (this.curChar) {
            case 'A':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 4328525826L, bk, 0L, bl, 0L);
            case 'B':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 1152921504606846976L, bk, 0L, bl, 0L);
            case 'C':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 144115188075855872L, bk, 0L, bl, 0L);
            case 'D':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'P':
            case 'Q':
            case 'T':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'j':
            case 'q':
            case 'x':
            default:
                return this.jjStartNfa_0(3, bi, bj, bk, bl);
            case 'E':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 1073741824L, bk, 2164260864L, bl, 0L);
            case 'F':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 2199023255552L, bk, 0L, bl, 0L);
            case 'G':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 1125899906842624L, bk, 0L, bl, 0L);
            case 'I':
                if ((bi & 2251799813685248L) != 0L) {
                    return this.jjStartNfaWithStates_0(4, 51, 26);
                }

                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 0L, bk, 4128L, bl, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 72057594037927936L, bk, 0L, bl, 8192L);
            case 'N':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 2147483648L, bk, 4L, bl, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 274877906944L, bk, 0L, bl, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 0L, bk, 276824128L, bl, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 0L, bk, 2097152L, bl, 0L);
            case 'U':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 68719476736L, bk, 0L, bl, 0L);
            case 'V':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 536870912L, bk, 72339069014638592L, bl, 65536L);
            case 'a':
                return this.jjMoveStringLiteralDfa5_0(bi, 2305843009213693952L, bj, 39591293747224L, bk, 144115188075855881L, bl, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa5_0(bi, 72057594037927936L, bj, 32768L, bk, 33562624L, bl, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 17179869184L, bk, 256L, bl, 0L);
            case 'e':
                if ((bi & 562949953421312L) != 0L) {
                    return this.jjStartNfaWithStates_0(4, 49, 26);
                }

                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 576478344691073024L, bk, 1099511660544L, bl, 0L);
            case 'f':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 0L, bk, 16384L, bl, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa5_0(bi, 4503599627370496L, bj, 2322718313676800L, bk, 70437463654400L, bl, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 34359746560L, bk, 0L, bl, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa5_0(bi, 432345564227567616L, bj, 36028797018963968L, bk, 16L, bl, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa5_0(bi, 9007199254740992L, bj, 0L, bk, 0L, bl, 0L);
            case 'l':
                if ((bi & 1125899906842624L) != 0L) {
                    return this.jjStartNfaWithStates_0(4, 50, 26);
                }

                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 31526296903221248L, bk, 0L, bl, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 128L, bk, 524800L, bl, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 2305843009213695492L, bk, 4415226380288L, bl, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa5_0(bi, Long.MIN_VALUE, bj, 562949955518464L, bk, 134217728L, bl, 391L);
            case 'p':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 4194304L, bk, 131072L, bl, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 524608L, bk, 8589934592L, bl, 0L);
            case 's':
                return this.jjMoveStringLiteralDfa5_0(bi, 36028797018963968L, bj, 137440198656L, bk, 1472853549833848960L, bl, 0L);
            case 't':
                if ((bi & 70368744177664L) != 0L) {
                    return this.jjStartNfaWithStates_0(4, 46, 26);
                } else {
                    if ((bi & 140737488355328L) != 0L) {
                        return this.jjStartNfaWithStates_0(4, 47, 26);
                    }

                    return this.jjMoveStringLiteralDfa5_0(bi, 1170935903116328960L, bj, 4611835552017156096L, bk, -1725412983963646974L, bl, 55416L);
                }
            case 'u':
                return this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 288511851128422433L, bk, 0L, bl, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa5_0(bi, 576460752303423488L, bj, Long.MIN_VALUE, bk, 412316860416L, bl, 1536L);
            case 'w':
                return this.jjMoveStringLiteralDfa5_0(bi, 4611686018427387904L, bj, 0L, bk, 36028797018963968L, bl, 0L);
            case 'y':
                return (bi & 281474976710656L) != 0L
                        ? this.jjStartNfaWithStates_0(4, 48, 26)
                        : this.jjMoveStringLiteralDfa5_0(bi, 0L, bj, 0L, bk, 4300210176L, bl, 0L);
        }
    }

    public int jjMoveStringLiteralDfa15_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bh = bd;
        long bg = bb;
        long bi = bf;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(13, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(14, 0L, bg, bh, bi);
            return 15;
        }

        switch (this.curChar) {
            case 'A':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 17592186044416L, bi, 0L);
            case 'B':
            case 'D':
            case 'E':
            case 'G':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'P':
            case 'Q':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '`':
            case 'b':
            case 'g':
            case 'h':
            case 'j':
            case 'p':
            case 'q':
            case 'u':
            case 'w':
            default:
                return this.jjStartNfa_0(14, 0L, bg, bh, bi);
            case 'C':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 134217728L, bi, 7L);
            case 'F':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 0L, bi, 512L);
            case 'H':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 8192L, bi, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 0L, bi, 384L);
            case 'R':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 8796093022208L, bi, 0L);
            case '_':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 33554432L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, -9223372036577799360L, bi, 1024L);
            case 'c':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 18014399046680576L, bi, 0L);
            case 'd':
                if ((bg & 9007199254740992L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 117, 26);
                }

                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 2252899327410176L, bi, 0L);
            case 'e':
                if ((bg & 18014398509481984L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 118, 26);
                } else if ((bg & 36028797018963968L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 119, 26);
                } else {
                    if ((bg & 72057594037927936L) != 0L) {
                        return this.jjStartNfaWithStates_0(15, 120, 26);
                    }

                    return this.jjMoveStringLiteralDfa16_0(bg, 7205759403792793600L, bh, 1125899973951488L, bi, 10240L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 8589934592L, bi, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa16_0(bg, 144115188075855872L, bh, 864726312827257880L, bi, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa16_0(bg, 1152921504606846976L, bh, 0L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa16_0(bg, -8646911284551352320L, bh, 2305843421530554368L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 36028797018963968L, bi, 49152L);
            case 'n':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 1167140408304601120L, bi, 16L);
            case 'o':
                if ((bg & 1125899906842624L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 114, 26);
                }

                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 72128036870291456L, bi, 65536L);
            case 'r':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 524289L, bi, 72L);
            case 's':
                if ((bg & 281474976710656L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 112, 26);
                } else if ((bg & 562949953421312L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 113, 26);
                } else {
                    if ((bg & 4503599627370496L) != 0L) {
                        return this.jjStartNfaWithStates_0(15, 116, 26);
                    }

                    return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 144115188075855872L, bi, 0L);
                }
            case 't':
                if ((bg & 2251799813685248L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 115, 26);
                }

                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 4611968077524893826L, bi, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 4L, bi, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 16777216L, bi, 4096L);
            case 'y':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 0L, bi, 32L);
            case 'z':
                return this.jjMoveStringLiteralDfa16_0(bg, 0L, bh, 2199023255552L, bi, 0L);
        }
    }

    public int jjMoveStringLiteralDfa13_0(long ba, long bb, long bc, long bd, long be, long bf) {
        long bg = bb;
        long bh = bd;
        long bi = bf;
        if (((bg = bg & ba) | (bh = bh & bc) | (bi = bi & be)) == 0L) {
            return this.jjStartNfa_0(11, 0L, ba, bc, be);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(12, 0L, bg, bh, bi);
            return 13;
        }

        switch (this.curChar) {
            case 'A':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 34359738368L, bi, 0L);
            case 'B':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 8796093022208L, bi, 0L);
            case 'C':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 16384L, bi, 0L);
            case 'E':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 65536L, bi, 0L);
            case 'F':
                return this.jjMoveStringLiteralDfa14_0(bg, 576460752303423488L, bh, 0L, bi, 0L);
            case 'I':
                return this.jjMoveStringLiteralDfa14_0(bg, 70368744177664L, bh, 0L, bi, 0L);
            case 'L':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 4194304L, bi, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 281474976710656L, bi, 0L);
            case 'O':
                return this.jjMoveStringLiteralDfa14_0(bg, 2251799813685248L, bh, 0L, bi, 0L);
            case 'R':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 8589934592L, bi, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 0L, bi, 8L);
            case 'U':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 67108864L, bi, 0L);
            case 'V':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 524288L, bi, 0L);
            case '_':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 8192L, bi, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa14_0(bg, -8935141660703064064L, bh, 412350414848L, bi, 8192L);
            case 'b':
                return this.jjMoveStringLiteralDfa14_0(bg, 144115188075855872L, bh, 0L, bi, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 0L, bi, 32L);
            case 'e':
                return this.jjMoveStringLiteralDfa14_0(bg, 5066549580791808L, bh, 2305843009347911690L, bi, 6535L);
            case 'g':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 144115188075888640L, bi, 512L);
            case 'h':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 1099513724932L, bi, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa14_0(bg, 126100789566373888L, bh, 1693265086644224L, bi, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 18016597532999680L, bi, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa14_0(bg, 281474976710656L, bh, 128L, bi, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa14_0(bg, 10273836649938944L, bh, 2442133568L, bi, 64L);
            case 'o':
                return this.jjMoveStringLiteralDfa14_0(bg, 1152921504606846976L, bh, 17592186044416L, bi, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 288265560523800576L, bi, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 1202601842828118016L, bi, 49152L);
            case 's':
                if ((bg & 1099511627776L) != 0L) {
                    return this.jjStartNfaWithStates_0(13, 104, 26);
                } else if ((bg & 2199023255552L) != 0L) {
                    return this.jjStartNfaWithStates_0(13, 105, 26);
                } else if ((bg & 8796093022208L) != 0L) {
                    return this.jjStartNfaWithStates_0(13, 107, 26);
                } else {
                    if ((bg & 17592186044416L) != 0L) {
                        return this.jjStartNfaWithStates_0(13, 108, 26);
                    }

                    return this.jjMoveStringLiteralDfa14_0(bg, 6917529027641081856L, bh, 576460752303559472L, bi, 1024L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, -9148992204465766399L, bi, 65552L);
            case 'u':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 1024L, bi, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa14_0(bg, 35184372088832L, bh, 0L, bi, 0L);
            case 'x':
                if ((bg & 4398046511104L) != 0L) {
                    return this.jjStartNfaWithStates_0(13, 106, 26);
                }
            case 'D':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'M':
            case 'P':
            case 'Q':
            case 'T':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '`':
            case 'd':
            case 'f':
            case 'j':
            case 'k':
            case 'q':
            case 'w':
            default:
                return this.jjStartNfa_0(12, 0L, bg, bh, bi);
            case 'y':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 4611686018427387904L, bi, 0L);
            case 'z':
                return this.jjMoveStringLiteralDfa14_0(bg, 0L, bh, 549755813888L, bi, 0L);
        }
    }

    public int jjMoveStringLiteralDfa31_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(29, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(30, 0L, 0L, 0L, bc);
            return 31;
        }

        switch (this.curChar) {
            case 'E':
                return this.jjMoveStringLiteralDfa32_0(bc, 32768L);
            case 'a':
                return this.jjMoveStringLiteralDfa32_0(bc, 8192L);
            case 'd':
                return this.jjMoveStringLiteralDfa32_0(bc, 384L);
            case 'n':
                return this.jjMoveStringLiteralDfa32_0(bc, 2048L);
            case 'o':
                return this.jjMoveStringLiteralDfa32_0(bc, 4608L);
            case 's':
                if ((bc & 64L) != 0L) {
                    return this.jjStartNfaWithStates_0(31, 198, 26);
                }

                return this.jjMoveStringLiteralDfa32_0(bc, 1024L);
            case 't':
                return this.jjMoveStringLiteralDfa32_0(bc, 65536L);
            case 'u':
                return this.jjMoveStringLiteralDfa32_0(bc, 16384L);
            default:
                return this.jjStartNfa_0(30, 0L, 0L, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa23_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(21, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(22, 0L, 0L, be, bf);
            return 23;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa24_0(be, 0L, bf, 8L);
            case 'E':
                return this.jjMoveStringLiteralDfa24_0(be, 0L, bf, 2048L);
            case 'S':
                return this.jjMoveStringLiteralDfa24_0(be, 0L, bf, 1024L);
            case 'a':
                return this.jjMoveStringLiteralDfa24_0(be, 144255925564211200L, bf, 4L);
            case 'c':
                return this.jjMoveStringLiteralDfa24_0(be, 288230376151711744L, bf, 32L);
            case 'd':
                return this.jjMoveStringLiteralDfa24_0(be, 4398046511104L, bf, 0L);
            case 'e':
                if ((be & 68719476736L) != 0L) {
                    return this.jjStartNfaWithStates_0(23, 164, 26);
                } else if ((be & 137438953472L) != 0L) {
                    return this.jjStartNfaWithStates_0(23, 165, 26);
                } else {
                    if ((be & 274877906944L) != 0L) {
                        return this.jjStartNfaWithStates_0(23, 166, 26);
                    }

                    return this.jjMoveStringLiteralDfa24_0(be, 576480543512723456L, bf, 0L);
                }
            case 'g':
                if ((be & 1099511627776L) != 0L) {
                    return this.jjStartNfaWithStates_0(23, 168, 26);
                }
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(22, 0L, 0L, be, bf);
            case 'i':
                return this.jjMoveStringLiteralDfa24_0(be, 0L, bf, 384L);
            case 'l':
                return this.jjMoveStringLiteralDfa24_0(be, 18014398509481984L, bf, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa24_0(be, -9187343239835811840L, bf, 16385L);
            case 'o':
                return this.jjMoveStringLiteralDfa24_0(be, 8796093022208L, bf, 8192L);
            case 'r':
                if ((be & 549755813888L) != 0L) {
                    return this.jjStartNfaWithStates_0(23, 167, 26);
                }

                return this.jjMoveStringLiteralDfa24_0(be, 0L, bf, 16L);
            case 's':
                return this.jjMoveStringLiteralDfa24_0(be, 2305843009213693952L, bf, 4096L);
            case 't':
                return this.jjMoveStringLiteralDfa24_0(be, 1229201223295434752L, bf, 66048L);
            case 'u':
                return this.jjMoveStringLiteralDfa24_0(be, 4625302370425765888L, bf, 32768L);
            case 'x':
                return this.jjMoveStringLiteralDfa24_0(be, 0L, bf, 66L);
        }
    }

    public int jjMoveStringLiteralDfa35_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(33, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(34, 0L, 0L, 0L, bc);
            return 35;
        }

        switch (this.curChar) {
            case 'n':
                return this.jjMoveStringLiteralDfa36_0(bc, 16384L);
            case 'u':
                return this.jjMoveStringLiteralDfa36_0(bc, 98304L);
            default:
                return this.jjStartNfa_0(34, 0L, 0L, 0L, bc);
        }
    }

    public final int jjStartNfa_0(int ba, long bb, long bc, long bd, long be) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(ba, bb, bc, bd, be), ba + 1);
    }

    public int jjStartNfaWithStates_4() {
        this.jjmatchedKind = 210;
        this.jjmatchedPos = 0;

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            return 0 + 1;
        }

        return this.jjMoveNfa_4(2, 0 + 1);
    }

    public void ReInitRounds() {
        this.jjround = -2147483647;
        int ba = 25;

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                return;
            }

            this.jjrounds[ba] = Integer.MIN_VALUE;
        }
    }

    public int jjMoveStringLiteralDfa29_0(long ba, long bb, long bc) {
        long bd = bc;
        if ((0L & ba | (bd = bd & bb)) == 0L) {
            return this.jjStartNfa_0(27, 0L, 0L, ba, bb);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(28, 0L, 0L, 0L, bd);
            return 29;
        }

        switch (this.curChar) {
            case 'N':
                return this.jjMoveStringLiteralDfa30_0(bd, 65536L);
            case 'c':
                return this.jjMoveStringLiteralDfa30_0(bd, 16384L);
            case 'e':
                return this.jjMoveStringLiteralDfa30_0(bd, 512L);
            case 'i':
                return this.jjMoveStringLiteralDfa30_0(bd, 2048L);
            case 'l':
                return this.jjMoveStringLiteralDfa30_0(bd, 1408L);
            case 'n':
                return this.jjMoveStringLiteralDfa30_0(bd, 32L);
            case 'o':
                return this.jjMoveStringLiteralDfa30_0(bd, 32832L);
            case 's':
                if ((bd & 8L) != 0L) {
                    return this.jjStartNfaWithStates_0(29, 195, 26);
                } else {
                    if ((bd & 16L) != 0L) {
                        return this.jjStartNfaWithStates_0(29, 196, 26);
                    }

                    return this.jjMoveStringLiteralDfa30_0(bd, 12288L);
                }
            default:
                return this.jjStartNfa_0(28, 0L, 0L, 0L, bd);
        }
    }

    public final int jjStopStringLiteralDfa_0(int ba, long bb, long bc, long bd, long be) {
        switch (ba) {
            case 0:
                if ((bb & 4194624L) != 0L) {
                    return 2;
                } else if ((bb & 8388608L) != 0L) {
                    return 25;
                } else {
                    byte cv;
                    if ((bb & 27021597764222976L) == 0L) {
                        if ((bc & 512L) == 0L) {
                            ZkmScriptTokenManager zkmScriptTokenManager15;
                            if ((bb & -27021666483699712L) == 0L) {
                                if ((bc & -2305843009213694465L) == 0L) {
                                    if ((bd & -1L) == 0L) {
                                        if ((be & 131071L) == 0L) {
                                            if ((bb & 65536L) != 0L) {
                                                return 7;
                                            }

                                            if ((bb & 16793600L) == 0L) {
                                                if ((bc & 2305843009213693952L) == 0L) {
                                                    return -1;
                                                }

                                                cv = 26;
                                            } else {
                                                cv = 26;
                                            }

                                            return cv;
                                        }

                                        zkmScriptTokenManager15 = this;
                                        short ek = 213;
                                    } else {
                                        zkmScriptTokenManager15 = this;
                                        short el = 213;
                                    }
                                } else {
                                    zkmScriptTokenManager15 = this;
                                    short em = 213;
                                }
                            } else {
                                zkmScriptTokenManager15 = this;
                                short en = 213;
                            }

                            zkmScriptTokenManager15.jjmatchedKind = 213;
                            return 26;
                        }

                        cv = 13;
                    } else {
                        cv = 13;
                    }

                    return cv;
                }
            case 1:
                if ((bb & 256L) != 0L) {
                    return 0;
                } else if ((bb & 9007199254740992L) != 0L) {
                    return 12;
                } else {
                    ZkmScriptTokenManager zkmScriptTokenManager14;
                    if ((bb & -27021735203176448L) == 0L) {
                        if ((bc & -513L) == 0L) {
                            if ((bd & -1L) == 0L) {
                                if ((be & 131071L) == 0L) {
                                    if ((bb & 68719476736L) != 0L) {
                                        return 26;
                                    }

                                    return -1;
                                }

                                zkmScriptTokenManager14 = this;
                                short eg = 213;
                            } else {
                                zkmScriptTokenManager14 = this;
                                short eh = 213;
                            }
                        } else {
                            zkmScriptTokenManager14 = this;
                            short ei = 213;
                        }
                    } else {
                        zkmScriptTokenManager14 = this;
                        short ej = 213;
                    }

                    zkmScriptTokenManager14.jjmatchedKind = 213;
                    this.jjmatchedPos = 1;
                    return 26;
                }
            case 2:
                if ((bb & 9007199254740992L) != 0L) {
                    return 11;
                } else {
                    int cs;
                    if ((bb & -27022147520036864L) == 0L) {
                        if ((bc & -9007199254741505L) == 0L) {
                            if ((bd & -36028797018963969L) == 0L) {
                                if ((be & 131071L) == 0L) {
                                    byte ct;
                                    if ((bb & 412316860416L) == 0L) {
                                        if ((bc & 9007199254740992L) == 0L) {
                                            if ((bd & 36028797018963968L) == 0L) {
                                                return -1;
                                            }

                                            ct = 26;
                                        } else {
                                            ct = 26;
                                        }
                                    } else {
                                        ct = 26;
                                    }

                                    return ct;
                                }

                                cs = this.jjmatchedPos;
                            } else {
                                cs = this.jjmatchedPos;
                            }
                        } else {
                            cs = this.jjmatchedPos;
                        }
                    } else {
                        cs = this.jjmatchedPos;
                    }

                    byte cu;
                    if (cs != 2) {
                        this.jjmatchedKind = 213;
                        this.jjmatchedPos = 2;
                        cu = 26;
                    } else {
                        cu = 26;
                    }

                    return cu;
                }
            case 3:
                if ((bb & 9007199254740992L) != 0L) {
                    return 10;
                } else if ((bc & 4096L) != 0L) {
                    byte cr;
                    if (this.jjmatchedPos != 3) {
                        this.jjmatchedKind = 215;
                        this.jjmatchedPos = 3;
                        cr = 7;
                    } else {
                        cr = 7;
                    }

                    return cr;
                } else {
                    int co;
                    if ((bb & -27091966508400640L) == 0L) {
                        if ((bc & -1154047474843783681L) == 0L) {
                            if ((bd & -72339069014642725L) == 0L) {
                                if ((be & 57343L) == 0L) {
                                    byte cp;
                                    if ((bb & 69818988363776L) == 0L) {
                                        if ((bc & 1154047474843779072L) == 0L) {
                                            if ((bd & 72339069014642724L) == 0L) {
                                                if ((be & 73728L) == 0L) {
                                                    return -1;
                                                }

                                                cp = 26;
                                            } else {
                                                cp = 26;
                                            }
                                        } else {
                                            cp = 26;
                                        }
                                    } else {
                                        cp = 26;
                                    }

                                    return cp;
                                }

                                co = this.jjmatchedPos;
                            } else {
                                co = this.jjmatchedPos;
                            }
                        } else {
                            co = this.jjmatchedPos;
                        }
                    } else {
                        co = this.jjmatchedPos;
                    }

                    byte cq;
                    if (co != 3) {
                        this.jjmatchedKind = 213;
                        this.jjmatchedPos = 3;
                        cq = 26;
                    } else {
                        cq = 26;
                    }

                    return cq;
                }
            case 4:
                ZkmScriptTokenManager zkmScriptTokenManager13;
                if ((bb & -31525197391593472L) == 0L) {
                    if ((bc & -4609L) == 0L) {
                        if ((bd & -1L) == 0L) {
                            if ((be & 131071L) == 0L) {
                                if ((bb & 9007199254740992L) != 0L) {
                                    return 8;
                                }

                                if ((bc & 4096L) != 0L) {
                                    this.jjmatchedKind = 215;
                                    this.jjmatchedPos = 4;
                                    return 7;
                                }

                                if ((bb & 4433230883192832L) != 0L) {
                                    return 26;
                                }

                                return -1;
                            }

                            zkmScriptTokenManager13 = this;
                            short ec = 213;
                        } else {
                            zkmScriptTokenManager13 = this;
                            short ed = 213;
                        }
                    } else {
                        zkmScriptTokenManager13 = this;
                        short ee = 213;
                    }
                } else {
                    zkmScriptTokenManager13 = this;
                    short ef = 213;
                }

                zkmScriptTokenManager13.jjmatchedKind = 213;
                this.jjmatchedPos = 4;
                return 26;
            case 5:
                int ck;
                if ((bc & -2101761L) == 0L) {
                    if ((bd & -3958276756602883L) == 0L) {
                        if ((be & 131055L) == 0L) {
                            if ((bc & 4096L) != 0L) {
                                byte cm;
                                if (this.jjmatchedPos != 5) {
                                    this.jjmatchedKind = 215;
                                    this.jjmatchedPos = 5;
                                    cm = 7;
                                } else {
                                    cm = 7;
                                }

                                return cm;
                            }

                            if ((bb & 9007199254740992L) != 0L) {
                                return 9;
                            }

                            byte cl;
                            if ((bb & -31525197391593472L) == 0L) {
                                if ((bc & 2097152L) == 0L) {
                                    if ((bd & 3958276756602882L) == 0L) {
                                        if ((be & 16L) == 0L) {
                                            return -1;
                                        }

                                        cl = 26;
                                    } else {
                                        cl = 26;
                                    }
                                } else {
                                    cl = 26;
                                }
                            } else {
                                cl = 26;
                            }

                            return cl;
                        }

                        ck = this.jjmatchedPos;
                    } else {
                        ck = this.jjmatchedPos;
                    }
                } else {
                    ck = this.jjmatchedPos;
                }

                byte cn;
                if (ck != 5) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 5;
                    cn = 26;
                } else {
                    cn = 26;
                }

                return cn;
            case 6:
                int ch;
                if ((bc & -8623493696L) == 0L) {
                    if ((bd & -1L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 4096L) != 0L) {
                                byte ci;
                                if (this.jjmatchedPos != 6) {
                                    this.jjmatchedKind = 215;
                                    this.jjmatchedPos = 6;
                                    ci = 7;
                                } else {
                                    ci = 7;
                                }

                                return ci;
                            }

                            if ((bc & 8623489087L) != 0L) {
                                return 26;
                            }

                            return -1;
                        }

                        ch = this.jjmatchedPos;
                    } else {
                        ch = this.jjmatchedPos;
                    }
                } else {
                    ch = this.jjmatchedPos;
                }

                byte cj;
                if (ch != 6) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 6;
                    cj = 26;
                } else {
                    cj = 26;
                }

                return cj;
            case 7:
                ZkmScriptTokenManager zkmScriptTokenManager12;
                if ((bc & -8589942784L) == 0L) {
                    if ((bd & -1L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 8589938688L) != 0L) {
                                this.jjmatchedKind = 215;
                                this.jjmatchedPos = 7;
                                return 7;
                            }

                            if ((bc & 3520L) != 0L) {
                                return 26;
                            }

                            return -1;
                        }

                        zkmScriptTokenManager12 = this;
                        short dz = 213;
                    } else {
                        zkmScriptTokenManager12 = this;
                        short ea = 213;
                    }
                } else {
                    zkmScriptTokenManager12 = this;
                    short eb = 213;
                }

                zkmScriptTokenManager12.jjmatchedKind = 213;
                this.jjmatchedPos = 7;
                return 26;
            case 8:
                int cd;
                if ((bc & -146037276672L) == 0L) {
                    if ((bd & -1454838601568290945L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 8589934592L) != 0L) {
                                byte cf;
                                if (this.jjmatchedPos != 8) {
                                    this.jjmatchedKind = 215;
                                    this.jjmatchedPos = 8;
                                    cf = 7;
                                } else {
                                    cf = 7;
                                }

                                return cf;
                            }

                            if ((bc & 4096L) != 0L) {
                                return 7;
                            }

                            byte ce;
                            if ((bc & 137447333888L) == 0L) {
                                if ((bd & 1454838601568290944L) == 0L) {
                                    return -1;
                                }

                                ce = 26;
                            } else {
                                ce = 26;
                            }

                            return ce;
                        }

                        cd = this.jjmatchedPos;
                    } else {
                        cd = this.jjmatchedPos;
                    }
                } else {
                    cd = this.jjmatchedPos;
                }

                byte cg;
                if (cd != 8) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 8;
                    cg = 26;
                } else {
                    cg = 26;
                }

                return cg;
            case 9:
                int bz;
                if ((bc & -8858370048L) == 0L) {
                    if ((bd & -1099511627777L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 8589934592L) != 0L) {
                                byte cb;
                                if (this.jjmatchedPos != 9) {
                                    this.jjmatchedKind = 215;
                                    this.jjmatchedPos = 9;
                                    cb = 7;
                                } else {
                                    cb = 7;
                                }

                                return cb;
                            }

                            byte ca;
                            if ((bc & 260046848L) == 0L) {
                                if ((bd & 1099511627776L) == 0L) {
                                    return -1;
                                }

                                ca = 26;
                            } else {
                                ca = 26;
                            }

                            return ca;
                        }

                        bz = this.jjmatchedPos;
                    } else {
                        bz = this.jjmatchedPos;
                    }
                } else {
                    bz = this.jjmatchedPos;
                }

                byte cc;
                if (bz != 9) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 9;
                    cc = 26;
                } else {
                    cc = 26;
                }

                return cc;
            case 10:
                int bv;
                if ((bc & -17179869184L) == 0L) {
                    if ((bd & -72339069014638593L) == 0L) {
                        if ((be & 65535L) == 0L) {
                            if ((bc & 8589934592L) != 0L) {
                                byte bx;
                                if (this.jjmatchedPos != 10) {
                                    this.jjmatchedKind = 215;
                                    this.jjmatchedPos = 10;
                                    bx = 7;
                                } else {
                                    bx = 7;
                                }

                                return bx;
                            }

                            byte bw;
                            if ((bc & 8321499136L) == 0L) {
                                if ((bd & 72339069014638592L) == 0L) {
                                    if ((be & 65536L) == 0L) {
                                        return -1;
                                    }

                                    bw = 26;
                                } else {
                                    bw = 26;
                                }
                            } else {
                                bw = 26;
                            }

                            return bw;
                        }

                        bv = this.jjmatchedPos;
                    } else {
                        bv = this.jjmatchedPos;
                    }
                } else {
                    bv = this.jjmatchedPos;
                }

                byte by;
                if (bv != 10) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 10;
                    by = 26;
                } else {
                    by = 26;
                }

                return by;
            case 11:
                ZkmScriptTokenManager zkmScriptTokenManager11;
                if ((bc & -68719476736L) == 0L) {
                    if ((bd & -1L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 8589934592L) != 0L) {
                                return 7;
                            }

                            if ((bc & 51539607552L) != 0L) {
                                return 26;
                            }

                            return -1;
                        }

                        zkmScriptTokenManager11 = this;
                        short dw = 213;
                    } else {
                        zkmScriptTokenManager11 = this;
                        short dx = 213;
                    }
                } else {
                    zkmScriptTokenManager11 = this;
                    short dy = 213;
                }

                zkmScriptTokenManager11.jjmatchedKind = 213;
                this.jjmatchedPos = 11;
                return 26;
            case 12:
                int bs;
                if ((bc & -2323268069490688L) == 0L) {
                    if ((bd & -67174401L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            byte bt;
                            if ((bc & 2323199350013952L) == 0L) {
                                if ((bd & 67174400L) == 0L) {
                                    return -1;
                                }

                                bt = 26;
                            } else {
                                bt = 26;
                            }

                            return bt;
                        }

                        bs = this.jjmatchedPos;
                    } else {
                        bs = this.jjmatchedPos;
                    }
                } else {
                    bs = this.jjmatchedPos;
                }

                byte bu;
                if (bs != 12) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 12;
                    bu = 26;
                } else {
                    bu = 26;
                }

                return bu;
            case 13:
                ZkmScriptTokenManager zkmScriptTokenManager10;
                if ((bc & -35184372088832L) == 0L) {
                    if ((bd & -1L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 34084860461056L) != 0L) {
                                return 26;
                            }

                            return -1;
                        }

                        zkmScriptTokenManager10 = this;
                        short ds = 213;
                    } else {
                        zkmScriptTokenManager10 = this;
                        short dt = 213;
                    }
                } else {
                    zkmScriptTokenManager10 = this;
                    short du = 213;
                }

                zkmScriptTokenManager10.jjmatchedKind = 213;
                this.jjmatchedPos = 13;
                return 26;
            case 14:
                ZkmScriptTokenManager zkmScriptTokenManager9;
                if ((bc & -281474976710656L) == 0L) {
                    if ((bd & -1L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 246290604621824L) != 0L) {
                                return 26;
                            }

                            return -1;
                        }

                        zkmScriptTokenManager9 = this;
                        short dn = 213;
                    } else {
                        zkmScriptTokenManager9 = this;
                        short dp = 213;
                    }
                } else {
                    zkmScriptTokenManager9 = this;
                    short dq = 213;
                }

                zkmScriptTokenManager9.jjmatchedKind = 213;
                this.jjmatchedPos = 14;
                return 26;
            case 15:
                ZkmScriptTokenManager zkmScriptTokenManager8;
                if ((bc & -144115188075855872L) == 0L) {
                    if ((bd & -1L) == 0L) {
                        if ((be & 131071L) == 0L) {
                            if ((bc & 143833713099145216L) != 0L) {
                                return 26;
                            }

                            return -1;
                        }

                        zkmScriptTokenManager8 = this;
                        short dk = 213;
                    } else {
                        zkmScriptTokenManager8 = this;
                        short dl = 213;
                    }
                } else {
                    zkmScriptTokenManager8 = this;
                    short dm = 213;
                }

                zkmScriptTokenManager8.jjmatchedKind = 213;
                this.jjmatchedPos = 15;
                return 26;
            case 16:
                int bp;
                if ((bd & -412316860420L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        byte bq;
                        if ((bc & -144115188075855872L) == 0L) {
                            if ((bd & 412316860419L) == 0L) {
                                return -1;
                            }

                            bq = 26;
                        } else {
                            bq = 26;
                        }

                        return bq;
                    }

                    bp = this.jjmatchedPos;
                } else {
                    bp = this.jjmatchedPos;
                }

                byte br;
                if (bp != 16) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 16;
                    br = 26;
                } else {
                    br = 26;
                }

                return br;
            case 17:
                ZkmScriptTokenManager zkmScriptTokenManager7;
                if ((bd & -128L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 124L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager7 = this;
                    short dh = 213;
                } else {
                    zkmScriptTokenManager7 = this;
                    short dj = 213;
                }

                zkmScriptTokenManager7.jjmatchedKind = 213;
                this.jjmatchedPos = 17;
                return 26;
            case 18:
                int bn;
                if ((bd & -13651536370475008L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 13651536370474880L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    bn = this.jjmatchedPos;
                } else {
                    bn = this.jjmatchedPos;
                }

                byte bo;
                if (bn != 18) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 18;
                    bo = 26;
                } else {
                    bo = 26;
                }

                return bo;
            case 19:
                int bl;
                if ((bd & -18014398511579136L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 18014398511570944L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    bl = this.jjmatchedPos;
                } else {
                    bl = this.jjmatchedPos;
                }

                byte bm;
                if (bl != 19) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 19;
                    bm = 26;
                } else {
                    bm = 26;
                }

                return bm;
            case 20:
                ZkmScriptTokenManager zkmScriptTokenManager6;
                if ((bd & -33554432L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 31457280L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager6 = this;
                    short df = 213;
                } else {
                    zkmScriptTokenManager6 = this;
                    short dg = 213;
                }

                zkmScriptTokenManager6.jjmatchedKind = 213;
                this.jjmatchedPos = 20;
                return 26;
            case 21:
                int bi;
                if ((bd & -2147483648L) == 0L) {
                    if ((be & 131064L) == 0L) {
                        byte bj;
                        if ((bd & 2113929216L) == 0L) {
                            if ((be & 7L) == 0L) {
                                return -1;
                            }

                            bj = 26;
                        } else {
                            bj = 26;
                        }

                        return bj;
                    }

                    bi = this.jjmatchedPos;
                } else {
                    bi = this.jjmatchedPos;
                }

                byte bk;
                if (bi != 21) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 21;
                    bk = 26;
                } else {
                    bk = 26;
                }

                return bk;
            case 22:
                ZkmScriptTokenManager zkmScriptTokenManager5;
                if ((bd & -68719476736L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 66571993088L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager5 = this;
                    short dd = 213;
                } else {
                    zkmScriptTokenManager5 = this;
                    short de = 213;
                }

                zkmScriptTokenManager5.jjmatchedKind = 213;
                this.jjmatchedPos = 22;
                return 26;
            case 23:
                ZkmScriptTokenManager zkmScriptTokenManager4;
                if ((bd & -2199023255552L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 2130303778816L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager4 = this;
                    short db = 213;
                } else {
                    zkmScriptTokenManager4 = this;
                    short dc = 213;
                }

                zkmScriptTokenManager4.jjmatchedKind = 213;
                this.jjmatchedPos = 23;
                return 26;
            case 24:
                ZkmScriptTokenManager zkmScriptTokenManager3;
                if ((bd & -35184372088832L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 32985348833280L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager3 = this;
                    short cz = 213;
                } else {
                    zkmScriptTokenManager3 = this;
                    short da = 213;
                }

                zkmScriptTokenManager3.jjmatchedKind = 213;
                this.jjmatchedPos = 24;
                return 26;
            case 25:
                ZkmScriptTokenManager zkmScriptTokenManager2;
                if ((bd & -18014398509481984L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 17979214137393152L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager2 = this;
                    short cx = 213;
                } else {
                    zkmScriptTokenManager2 = this;
                    short cy = 213;
                }

                zkmScriptTokenManager2.jjmatchedKind = 213;
                this.jjmatchedPos = 25;
                return 26;
            case 26:
                int bf;
                if ((bd & -288230376151711744L) == 0L) {
                    if ((be & 65535L) == 0L) {
                        byte bg;
                        if ((bd & 270215977642229760L) == 0L) {
                            if ((be & 65536L) == 0L) {
                                return -1;
                            }

                            bg = 26;
                        } else {
                            bg = 26;
                        }

                        return bg;
                    }

                    bf = this.jjmatchedPos;
                } else {
                    bf = this.jjmatchedPos;
                }

                byte bh;
                if (bf != 26) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 26;
                    bh = 26;
                } else {
                    bh = 26;
                }

                return bh;
            case 27:
                ZkmScriptTokenManager zkmScriptTokenManager1;
                if ((bd & -4611686018427387904L) == 0L) {
                    if ((be & 131071L) == 0L) {
                        if ((bd & 4323455642275676160L) != 0L) {
                            return 26;
                        }

                        return -1;
                    }

                    zkmScriptTokenManager1 = this;
                    short ep = 213;
                } else {
                    zkmScriptTokenManager1 = this;
                    short cw = 213;
                }

                zkmScriptTokenManager1.jjmatchedKind = 213;
                this.jjmatchedPos = 27;
                return 26;
            case 28:
                if ((be & 131064L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 28;
                    return 26;
                } else {
                    byte eo;
                    if ((bd & -4611686018427387904L) == 0L) {
                        if ((be & 7L) == 0L) {
                            return -1;
                        }

                        eo = 26;
                    } else {
                        eo = 26;
                    }

                    return eo;
                }
            case 29:
                if ((be & 131040L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 29;
                    return 26;
                } else {
                    if ((be & 24L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 30:
                if ((be & 131008L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 30;
                    return 26;
                } else {
                    if ((be & 32L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 31:
                if ((be & 130944L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 31;
                    return 26;
                } else {
                    if ((be & 64L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 32:
                if ((be & 126976L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 32;
                    return 26;
                } else {
                    if ((be & 3968L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 33:
                if ((be & 122880L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 33;
                    return 26;
                } else {
                    if ((be & 4096L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 34:
                if ((be & 114688L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 34;
                    return 26;
                } else {
                    if ((be & 8192L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 35:
                if ((be & 114688L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 35;
                    return 26;
                }

                return -1;
            case 36:
                if ((be & 98304L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 36;
                    return 26;
                } else {
                    if ((be & 16384L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            case 37:
                if ((be & 98304L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 37;
                    return 26;
                }

                return -1;
            case 38:
                if ((be & 98304L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 38;
                    return 26;
                }

                return -1;
            case 39:
                if ((be & 98304L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 39;
                    return 26;
                }

                return -1;
            case 40:
                if ((be & 65536L) != 0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 40;
                    return 26;
                } else {
                    if ((be & 32768L) != 0L) {
                        return 26;
                    }

                    return -1;
                }
            default:
                return -1;
        }
    }

    public int jjMoveStringLiteralDfa21_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(19, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(20, 0L, 0L, be, bf);
            return 21;
        }

        switch (this.curChar) {
            case '6':
                if ((be & 33554432L) != 0L) {
                    return this.jjStartNfaWithStates_0(21, 153, 26);
                }
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'k':
            case 'q':
            case 'r':
            case 'v':
            case 'w':
            default:
                break;
            case 'E':
                return this.jjMoveStringLiteralDfa22_0(be, 0L, bf, 32L);
            case 'a':
                return this.jjMoveStringLiteralDfa22_0(be, 0L, bf, 384L);
            case 'b':
                return this.jjMoveStringLiteralDfa22_0(be, 3940649673949184L, bf, 32768L);
            case 'c':
                return this.jjMoveStringLiteralDfa22_0(be, 4625724582890831872L, bf, 2048L);
            case 'd':
                return this.jjMoveStringLiteralDfa22_0(be, 569083166720L, bf, 0L);
            case 'e':
                if ((be & 67108864L) != 0L) {
                    return this.jjStartNfaWithStates_0(21, 154, 26);
                }

                return this.jjMoveStringLiteralDfa22_0(be, 1008806359480664064L, bf, 8L);
            case 'f':
                return this.jjMoveStringLiteralDfa22_0(be, 0L, bf, 8192L);
            case 'g':
                if ((be & 268435456L) != 0L) {
                    return this.jjStartNfaWithStates_0(21, 156, 26);
                }
                break;
            case 'h':
                return this.jjMoveStringLiteralDfa22_0(be, 36028797018963968L, bf, 16384L);
            case 'i':
                return this.jjMoveStringLiteralDfa22_0(be, -9223370937343148032L, bf, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa22_0(be, 2305847407260205056L, bf, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa22_0(be, 72057594037927936L, bf, 65536L);
            case 'n':
                return this.jjMoveStringLiteralDfa22_0(be, 0L, bf, 64L);
            case 'o':
                return this.jjMoveStringLiteralDfa22_0(be, 0L, bf, 5120L);
            case 'p':
                return this.jjMoveStringLiteralDfa22_0(be, 0L, bf, 512L);
            case 's':
                if ((be & 134217728L) != 0L) {
                    this.jjmatchedKind = 155;
                    this.jjmatchedPos = 21;
                } else {
                    if ((be & 536870912L) != 0L) {
                        return this.jjStartNfaWithStates_0(21, 157, 26);
                    }

                    if ((be & 1073741824L) != 0L) {
                        return this.jjStartNfaWithStates_0(21, 158, 26);
                    }
                }

                return this.jjMoveStringLiteralDfa22_0(be, 2199023255552L, bf, 7L);
            case 't':
                return this.jjMoveStringLiteralDfa22_0(be, 8800387989504L, bf, 16L);
            case 'u':
                return this.jjMoveStringLiteralDfa22_0(be, 1152939577829228544L, bf, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa22_0(be, 18014398509481984L, bf, 0L);
        }

        return this.jjStartNfa_0(20, 0L, 0L, be, bf);
    }

    public int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '!':
                return this.jjStopAtPos(0, 25);
            case '"':
                return this.jjStopAtPos(0, 209);
            case '&':
                return this.jjMoveStringLiteralDfa1_0(34359738368L, 0L, 0L, 0L);
            case '(':
                return this.jjStopAtPos(0, 28);
            case ')':
                return this.jjStopAtPos(0, 29);
            case '*':
                return this.jjStartNfaWithStates_0(0, 23, 25);
            case '+':
                this.jjmatchedKind = 24;
                return this.jjMoveStringLiteralDfa1_0(0L, 2305843009213693952L, 0L, 0L);
            case ',':
                return this.jjStopAtPos(0, 20);
            case '-':
                return this.jjStartNfaWithStates_0(0, 16, 7);
            case '.':
                return this.jjStopAtPos(0, 21);
            case '/':
                this.jjmatchedKind = 22;
                return this.jjMoveStringLiteralDfa1_0(320L, 0L, 0L, 0L);
            case ';':
                return this.jjStopAtPos(0, 19);
            case '<':
                return this.jjMoveStringLiteralDfa1_0(27021597764222976L, 512L, 0L, 0L);
            case '=':
                return this.jjStopAtPos(0, 15);
            case '>':
                return this.jjStopAtPos(0, 33);
            case '?':
                return this.jjStopAtPos(0, 32);
            case '@':
                return this.jjStopAtPos(0, 27);
            case 'A':
                return this.jjMoveStringLiteralDfa1_0(2251799813685248L, 0L, 0L, 0L);
            case '[':
                return this.jjStopAtPos(0, 17);
            case ']':
                return this.jjStopAtPos(0, 18);
            case '^':
                return this.jjStopAtPos(0, 26);
            case 'a':
                return this.jjMoveStringLiteralDfa1_0(18004502904832L, 9007199330238720L, 54044295317684304L, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa1_0(4503599627370496L, 0L, 0L, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa1_0(0L, 2322718598955008L, 144115737831669760L, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa1_0(1152921504606846976L, 0L, 3958276756602882L, 1552L);
            case 'e':
                return this.jjMoveStringLiteralDfa1_0(1099511627776L, 35184372089893L, 2203357159424L, 0L);
            case 'f':
                return this.jjMoveStringLiteralDfa1_0(1688849860263936L, 292057776128L, 0L, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa1_0(68719476736L, 4194304L, 0L, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa1_0(281474976710656L, 2199023255552L, 2097152L, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa1_0(0L, 17596615753728L, 70446053605888L, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa1_0(4398046511104L, 1154047405050560512L, 72339069014642720L, 73728L);
            case 'l':
                return this.jjMoveStringLiteralDfa1_0(140737488355328L, 76562295324409856L, 2164293632L, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa1_0(0L, 144678138029277248L, 134217984L, 391L);
            case 'n':
                return this.jjMoveStringLiteralDfa1_0(2882312557610139648L, 630508345878384640L, 9L, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa1_0(549755813888L, 137439084544L, 1454838601568290948L, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa1_0(144185556820033536L, 8590213144L, 0L, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa1_0(Long.MIN_VALUE, -4611536484843913216L, -1729373047426644992L, 55400L);
            case 's':
                return this.jjMoveStringLiteralDfa1_0(396316767208603648L, 34393301122L, 4415226380288L, 0L);
            case 't':
                return this.jjMoveStringLiteralDfa1_0(4611723401822732288L, 69794267136L, 0L, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa1_0(0L, 288511851128455168L, 0L, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa1_0(0L, 2048L, 0L, 0L);
            case '{':
                return this.jjStopAtPos(0, 30);
            case '|':
                return this.jjMoveStringLiteralDfa1_0(17179869184L, 0L, 0L, 0L);
            case '}':
                return this.jjStopAtPos(0, 31);
            case '\ufeff':
                return this.jjStartNfaWithStates_0(0, 14, 26);
            default:
                return this.jjMoveNfa_0(3, 0);
        }
    }

    public int jjMoveStringLiteralDfa41_0(long ba) {
        long bb = 65536L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(39, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(40, 0L, 0L, 0L, bb);
            return 41;
        }

        switch (this.curChar) {
            case 'd':
                if ((bb & 65536L) != 0L) {
                    return this.jjStartNfaWithStates_0(41, 208, 26);
                }
            default:
                return this.jjStartNfa_0(40, 0L, 0L, 0L, bb);
        }
    }

    public int jjMoveStringLiteralDfa27_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(25, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(26, 0L, 0L, be, bf);
            return 27;
        }

        switch (this.curChar) {
            case 'E':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 16384L);
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'a':
            case 'b':
            case 'c':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(26, 0L, 0L, be, bf);
            case 'I':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 65536L);
            case 'd':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 3L);
            case 'e':
                if ((be & 288230376151711744L) != 0L) {
                    return this.jjStartNfaWithStates_0(27, 186, 26);
                }

                return this.jjMoveStringLiteralDfa28_0(be, Long.MIN_VALUE, bf, 0L);
            case 'f':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 8192L);
            case 'g':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 4L);
            case 'i':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 32L);
            case 'l':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 4096L);
            case 'n':
                return this.jjMoveStringLiteralDfa28_0(be, 4611686018427387904L, bf, 640L);
            case 'r':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 1024L);
            case 's':
                if ((be & 576460752303423488L) != 0L) {
                    return this.jjStartNfaWithStates_0(27, 187, 26);
                } else if ((be & 1152921504606846976L) != 0L) {
                    return this.jjStartNfaWithStates_0(27, 188, 26);
                } else {
                    if ((be & 2305843009213693952L) != 0L) {
                        return this.jjStartNfaWithStates_0(27, 189, 26);
                    }

                    return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 72L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 32784L);
            case 'u':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 2048L);
            case 'x':
                return this.jjMoveStringLiteralDfa28_0(be, 0L, bf, 256L);
        }
    }

    public int jjMoveNfa_4(int ba, int bb) {
        int bc = bb;
        int bd = 0;
        this.jjnewStateCnt = 3;
        int be = 1;
        this.jjstateSet[0] = ba;
        int bf = Integer.MAX_VALUE;

        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }

            if (this.curChar < '@') {
                long bl = 1L << this.curChar;
                int[] jjstateSet = this.jjstateSet;

                while (true) {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 0:
                            if ((-17179882497L & bl) != 0L) {
                                if (bf > 211) {
                                    bf = 211;
                                }
                            } else if (this.curChar == '"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            }
                            break;
                        case 1:
                            if (this.curChar == '"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            }
                            break;
                        case 2:
                            if (this.curChar == '"' && bf > 211) {
                                bf = 211;
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    jjstateSet = this.jjstateSet;
                }
            } else if (this.curChar < 128) {
                int[] bm = this.jjstateSet;

                while (true) {
                    be += -1;
                    switch (bm[be]) {
                        case 0:
                            bf = 211;
                    }

                    if (be == bd) {
                        break;
                    }

                    bm = this.jjstateSet;
                }
            } else {
                int bg = this.curChar >> '\b';
                int bh = bg >> 6;
                long bi = 1L << (bg & 63);
                int bj = (this.curChar & 255) >> 6;
                long bk = 1L << (this.curChar & '?');
                int[] bo = this.jjstateSet;

                while (true) {
                    be += -1;
                    switch (bo[be]) {
                        case 0:
                            if (jjCanMove_0(bg, bh, bj, bi, bk) && bf > 211) {
                                bf = 211;
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    bo = this.jjstateSet;
                }
            }

            if (bf != Integer.MAX_VALUE) {
                this.jjmatchedKind = bf;
                this.jjmatchedPos = bc;
                bf = Integer.MAX_VALUE;
            }

            bc++;
            if ((be = this.jjnewStateCnt) == (bd = 3 - (this.jjnewStateCnt = bd))) {
                return bc;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException iOException) {
                return bc;
            }
        }
    }

    public int jjMoveStringLiteralDfa6_0(long ba, long bb, long bc, long bd, long be, long bf, long bg) {
        long bi = be;
        long bj = bg;
        long bh = bc;
        if ((0L & ba | (bh = bh & bb) | (bi = bi & bd) | (bj = bj & bf)) == 0L) {
            return this.jjStartNfa_0(4, ba, bb, bd, bf);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(5, 0L, bh, bi, bj);
            return 6;
        }

        switch (this.curChar) {
            case 'A':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 562949953421312L, bj, 0L);
            case 'B':
            case 'F':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'O':
            case 'Q':
            case 'T':
            case 'V':
            case 'W':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'j':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(5, 0L, bh, bi, bj);
            case 'C':
                return this.jjMoveStringLiteralDfa7_0(bh, 281474976714752L, bi, 0L, bj, 0L);
            case 'D':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 2251799813685248L, bj, 16L);
            case 'E':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 22007949295616L, bj, 0L);
            case 'G':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 0L, bj, 512L);
            case 'L':
                return this.jjMoveStringLiteralDfa7_0(bh, 2322718313676800L, bi, 0L, bj, 0L);
            case 'M':
                return this.jjMoveStringLiteralDfa7_0(bh, -8935141660703064064L, bi, 420906795008L, bj, 0L);
            case 'N':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 4608L, bj, 0L);
            case 'P':
                return this.jjMoveStringLiteralDfa7_0(bh, 562949953421312L, bi, 134217728L, bj, 391L);
            case 'R':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 524288L, bj, 0L);
            case 'S':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 1125899906842624L, bj, 1024L);
            case 'U':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 34359738368L, bj, 0L);
            case 'X':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 2L, bj, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa7_0(bh, 144116425027158016L, bi, 1454838601570388352L, bj, 0L);
            case 'b':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 2305843009213693952L, bj, 6144L);
            case 'c':
                return this.jjMoveStringLiteralDfa7_0(bh, 5368709376L, bi, 8798257283072L, bj, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa7_0(bh, 76561193665298432L, bi, 0L, bj, 0L);
            case 'e':
                if ((bh & 1L) != 0L) {
                    return this.jjStartNfaWithStates_0(6, 64, 26);
                } else {
                    if ((bh & 8L) != 0L) {
                        this.jjmatchedKind = 67;
                        this.jjmatchedPos = 6;
                    } else {
                        if ((bh & 16L) != 0L) {
                            return this.jjStartNfaWithStates_0(6, 68, 26);
                        }

                        if ((bh & 32L) != 0L) {
                            return this.jjStartNfaWithStates_0(6, 69, 26);
                        }
                    }

                    return this.jjMoveStringLiteralDfa7_0(bh, 6674514445312L, bi, 54043196636012561L, bj, 49152L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa7_0(bh, 274877906944L, bi, 276824128L, bj, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa7_0(bh, 35184372088832L, bi, 576460752303423488L, bj, 0L);
            case 'h':
                return this.jjMoveStringLiteralDfa7_0(bh, 576460752303423488L, bi, 32768L, bj, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa7_0(bh, 4611703610615545856L, bi, 131072L, bj, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa7_0(bh, 0L, bi, 8L, bj, 0L);
            case 'l':
                ZkmScriptTokenManager zkmScriptTokenManager1;
                long bk;
                long bl;
                if ((bh & 2L) != 0L) {
                    this.jjmatchedKind = 65;
                    this.jjmatchedPos = 6;
                    zkmScriptTokenManager1 = this;
                    bk = bh;
                    bl = 1188950318839236736L;
                } else {
                    zkmScriptTokenManager1 = this;
                    bk = bh;
                    bl = 1188950318839236736L;
                }

                return zkmScriptTokenManager1.jjMoveStringLiteralDfa7_0(bk, bl, bi, Long.MIN_VALUE, bj, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa7_0(bh, 2147483648L, bi, 0L, bj, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa7_0(bh, 1125900196249600L, bi, 2748779069472L, bj, 64L);
            case 'o':
                return this.jjMoveStringLiteralDfa7_0(bh, 34359738368L, bi, 4611686018427387904L, bj, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa7_0(bh, 8796093022208L, bi, 70437463671808L, bj, 0L);
            case 's':
                if ((bh & 4L) != 0L) {
                    return this.jjStartNfaWithStates_0(6, 66, 26);
                }

                return this.jjMoveStringLiteralDfa7_0(bh, 27021598368202752L, bi, 216455356602122244L, bj, 65536L);
            case 't':
                return this.jjMoveStringLiteralDfa7_0(bh, 2305843009222353472L, bi, 4300210176L, bj, 8224L);
            case 'u':
                return this.jjMoveStringLiteralDfa7_0(bh, 32768L, bi, 0L, bj, 0L);
            case 'x':
                return this.jjMoveStringLiteralDfa7_0(bh, 140737488355328L, bi, 0L, bj, 8L);
        }
    }

    public void MoreLexicalActions() {
        this.jjimageLen = this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1);
        switch (this.jjmatchedKind) {
            case 7:
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.input_stream.backup(1);
        }
    }

    public int jjMoveStringLiteralDfa0_4() {
        switch (this.curChar) {
            case '"':
                return this.jjStartNfaWithStates_4();
            default:
                return this.jjMoveNfa_4(0, 0);
        }
    }

    public int jjMoveStringLiteralDfa39_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(37, 0L, 0L, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(38, 0L, 0L, 0L, bc);
            return 39;
        }

        switch (this.curChar) {
            case 'n':
                return this.jjMoveStringLiteralDfa40_0(bc, 32768L);
            case 't':
                return this.jjMoveStringLiteralDfa40_0(bc, 65536L);
            default:
                return this.jjStartNfa_0(38, 0L, 0L, 0L, bc);
        }
    }

    public static final boolean jjCanMove_0(int ba, int bb, int bc, long bd, long be) {
        switch (ba) {
            case 0:
                return (jjbitVec2[bc] & be) != 0L;
            default:
                return (jjbitVec0[bb] & bd) != 0L;
        }
    }

    public ZkmScriptToken jjFillToken() {
        String string1 = jjstrLiteralImages[this.jjmatchedKind];
        String string = string1 == null ? this.input_stream.GetImage() : string1;
        int line = this.input_stream.getLine();
        int endLine = this.input_stream.getEndLine();
        int column = this.input_stream.getColumn();
        int endColumn = this.input_stream.getEndColumn();
        ZkmScriptToken zkmScriptToken = ZkmScriptToken.newToken(this.jjmatchedKind);
        zkmScriptToken.kind = this.jjmatchedKind;
        zkmScriptToken.image = string;
        zkmScriptToken.endLine = line;
        zkmScriptToken.beginColumn = column;
        zkmScriptToken.endColumn = endLine;
        zkmScriptToken.lastColumn = endColumn;
        return zkmScriptToken;
    }

    public int jjMoveStringLiteralDfa24_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(22, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(23, 0L, 0L, be, bf);
            return 24;
        }

        switch (this.curChar) {
            case 'C':
                return this.jjMoveStringLiteralDfa25_0(be, 0L, bf, 512L);
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'a':
            case 'b':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'r':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(23, 0L, 0L, be, bf);
            case 'E':
                return this.jjMoveStringLiteralDfa25_0(be, 0L, bf, 4096L);
            case 'O':
                return this.jjMoveStringLiteralDfa25_0(be, Long.MIN_VALUE, bf, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa25_0(be, 0L, bf, 71L);
            case 'd':
                return this.jjMoveStringLiteralDfa25_0(be, 13616351998377984L, bf, 0L);
            case 'e':
                if ((be & 4398046511104L) != 0L) {
                    return this.jjStartNfaWithStates_0(24, 170, 26);
                }

                return this.jjMoveStringLiteralDfa25_0(be, 76279718688587776L, bf, 65536L);
            case 'g':
                return this.jjMoveStringLiteralDfa25_0(be, 36169534507319296L, bf, 16384L);
            case 'i':
                return this.jjMoveStringLiteralDfa25_0(be, 2305843009213693952L, bf, 16L);
            case 'l':
                return this.jjMoveStringLiteralDfa25_0(be, 288230376151711744L, bf, 40L);
            case 'n':
                if ((be & 8796093022208L) != 0L) {
                    return this.jjStartNfaWithStates_0(24, 171, 26);
                }

                return this.jjMoveStringLiteralDfa25_0(be, 576460752303423488L, bf, 0L);
            case 'o':
                return this.jjMoveStringLiteralDfa25_0(be, 0L, bf, 384L);
            case 's':
                if ((be & 2199023255552L) != 0L) {
                    return this.jjStartNfaWithStates_0(24, 169, 26);
                } else {
                    if ((be & 17592186044416L) != 0L) {
                        return this.jjStartNfaWithStates_0(24, 172, 26);
                    }

                    return this.jjMoveStringLiteralDfa25_0(be, 4611686018427387904L, bf, 32768L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa25_0(be, 0L, bf, 8192L);
            case 'u':
                return this.jjMoveStringLiteralDfa25_0(be, 1315051091192184832L, bf, 1024L);
            case 'x':
                return this.jjMoveStringLiteralDfa25_0(be, 0L, bf, 2048L);
        }
    }

    public int jjMoveStringLiteralDfa22_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(20, 0L, 0L, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(21, 0L, 0L, be, bf);
            return 22;
        }

        switch (this.curChar) {
            case 'E':
                return this.jjMoveStringLiteralDfa23_0(be, 0L, bf, 66L);
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'O':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'g':
            case 'h':
            case 'j':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(21, 0L, 0L, be, bf);
            case 'I':
                return this.jjMoveStringLiteralDfa23_0(be, 0L, bf, 1L);
            case 'N':
                return this.jjMoveStringLiteralDfa23_0(be, 0L, bf, 8192L);
            case 'P':
                return this.jjMoveStringLiteralDfa23_0(be, 0L, bf, 4L);
            case 'a':
                return this.jjMoveStringLiteralDfa23_0(be, 36310271995674624L, bf, 16384L);
            case 'c':
                return this.jjMoveStringLiteralDfa23_0(be, 1170935903116328960L, bf, 0L);
            case 'd':
                return this.jjMoveStringLiteralDfa23_0(be, 481036337152L, bf, 8L);
            case 'e':
                if ((be & 2147483648L) != 0L) {
                    return this.jjStartNfaWithStates_0(22, 159, 26);
                } else {
                    if ((be & 17179869184L) != 0L) {
                        return this.jjStartNfaWithStates_0(22, 162, 26);
                    }

                    return this.jjMoveStringLiteralDfa23_0(be, 72058143793741824L, bf, 67584L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa23_0(be, 144115188075855872L, bf, 32768L);
            case 'i':
                return this.jjMoveStringLiteralDfa23_0(be, 8796093022208L, bf, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa23_0(be, 140737488355328L, bf, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa23_0(be, 4625302370425765888L, bf, 0L);
            case 'm':
                return this.jjMoveStringLiteralDfa23_0(be, 0L, bf, 1024L);
            case 'n':
                return this.jjMoveStringLiteralDfa23_0(be, 1099511627776L, bf, 4096L);
            case 'o':
                return this.jjMoveStringLiteralDfa23_0(be, Long.MIN_VALUE, bf, 0L);
            case 'r':
                return this.jjMoveStringLiteralDfa23_0(be, 576460752303423488L, bf, 0L);
            case 's':
                if ((be & 4294967296L) != 0L) {
                    return this.jjStartNfaWithStates_0(22, 160, 26);
                } else if ((be & 8589934592L) != 0L) {
                    return this.jjStartNfaWithStates_0(22, 161, 26);
                } else {
                    if ((be & 34359738368L) != 0L) {
                        return this.jjStartNfaWithStates_0(22, 163, 26);
                    }

                    return this.jjMoveStringLiteralDfa23_0(be, 2199023255552L, bf, 0L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa23_0(be, 17592186044416L, bf, 400L);
            case 'u':
                return this.jjMoveStringLiteralDfa23_0(be, 2309788056934154240L, bf, 512L);
            case 'x':
                return this.jjMoveStringLiteralDfa23_0(be, 288230376151711744L, bf, 32L);
        }
    }
}
