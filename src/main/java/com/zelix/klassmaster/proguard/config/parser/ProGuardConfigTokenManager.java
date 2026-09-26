package com.zelix.klassmaster.proguard.config.parser;

import java.io.IOException;
import java.io.PrintStream;

public class ProGuardConfigTokenManager implements ProGuardConfigConstants {
    public static long[] jjbitVec0 = new long[]{-4503599625273342L, -8193L, -17525614051329L, 1297036692691091455L};
    public static long[] jjbitVec1 = new long[]{0L, 0L, 297242231151001600L, -36028797027352577L};
    public static long[] jjbitVec2 = new long[]{4503586742468607L, -65536L, -432556670460100609L, 70501888360451L};
    public static long[] jjbitVec3 = new long[]{0L, 288230376151711744L, -17179879616L, 4503599577006079L};
    public static long[] jjbitVec4 = new long[]{-1L, -1L, -4093L, 234187180623206815L};
    public static long[] jjbitVec5 = new long[]{-562949953421312L, -8547991553L, 255L, 1979120929931264L};
    public static long[] jjbitVec6 = new long[]{576460743713488896L, -562949953419265L, -1L, 2017613045381988351L};
    public static long[] jjbitVec7 = new long[]{35184371892224L, 0L, 274877906943L, 0L};
    public static long[] jjbitVec8 = new long[]{2594073385365405664L, 17163157504L, 271902628478820320L, 4222140488351744L};
    public static long[] jjbitVec9 = new long[]{247132830528276448L, 7881300924956672L, 2589004636761075680L, 4295032832L};
    public static long[] jjbitVec10 = new long[]{2579997437506199520L, 15837691904L, 270153412153034720L, 0L};
    public static long[] jjbitVec11 = new long[]{283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L};
    public static long[] jjbitVec12 = new long[]{288228177128316896L, 12884901888L, 3457638613854978016L, 127L};
    public static long[] jjbitVec13 = new long[]{-9219431387180826626L, 127L, 2309762420256548246L, 805306463L};
    public static long[] jjbitVec14 = new long[]{1L, 8796093021951L, 3840L, 0L};
    public static long[] jjbitVec15 = new long[]{7679401525247L, 4128768L, -4294967296L, 36028797018898495L};
    public static long[] jjbitVec16 = new long[]{-1L, -2080374785L, -1065151889409L, 288230376151711743L};
    public static long[] jjbitVec17 = new long[]{-129L, -3263218305L, 9168625153884503423L, -140737496776899L};
    public static long[] jjbitVec18 = new long[]{-2160230401L, 134217599L, -4294967296L, 9007199254740991L};
    public static long[] jjbitVec19 = new long[]{-2L, -1L, -1L, -1L};
    public static long[] jjbitVec20 = new long[]{-1L, 35923243902697471L, -4160749570L, 8796093022207L};
    public static long[] jjbitVec21 = new long[]{0L, 0L, 4503599627370495L, 134217728L};
    public static long[] jjbitVec22 = new long[]{-4294967296L, 72057594037927935L, 2199023255551L, 0L};
    public static long[] jjbitVec23 = new long[]{-1L, -1L, -4026531841L, 288230376151711743L};
    public static long[] jjbitVec24 = new long[]{-3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L};
    public static long[] jjbitVec25 = new long[]{Long.MIN_VALUE, -9223372036854775807L, 281470681743360L, 0L};
    public static long[] jjbitVec26 = new long[]{287031153606524036L, -4294967296L, 15L, 0L};
    public static long[] jjbitVec27 = new long[]{521858996278132960L, -2L, -6977224705L, Long.MAX_VALUE};
    public static long[] jjbitVec28 = new long[]{-527765581332512L, -1L, 72057589742993407L, 0L};
    public static long[] jjbitVec29 = new long[]{-1L, -1L, 18014398509481983L, 0L};
    public static long[] jjbitVec30 = new long[]{-1L, -1L, 274877906943L, 0L};
    public static long[] jjbitVec31 = new long[]{-1L, -1L, 8191L, 0L};
    public static long[] jjbitVec32 = new long[]{-1L, -1L, 68719476735L, 0L};
    public static long[] jjbitVec33 = new long[]{70368744177663L, 0L, 0L, 0L};
    public static long[] jjbitVec34 = new long[]{6881498030004502655L, -37L, 1125899906842623L, -524288L};
    public static long[] jjbitVec35 = new long[]{4611686018427387903L, -65536L, -196609L, 1152640029630136575L};
    public static long[] jjbitVec36 = new long[]{6755399441055744L, -11538275021824000L, -1L, 2305843009213693951L};
    public static long[] jjbitVec37 = new long[]{-8646911293141286896L, -137304735746L, Long.MAX_VALUE, 425688104188L};
    public static long[] jjbitVec38 = new long[]{0L, 0L, 297242235445968895L, -36028797027352577L};
    public static long[] jjbitVec39 = new long[]{-1L, 288230406216515583L, -17179879616L, 4503599577006079L};
    public static long[] jjbitVec40 = new long[]{-1L, -1L, -3973L, 234187180623206815L};
    public static long[] jjbitVec41 = new long[]{-562949953421312L, -8547991553L, -4899916411759099649L, 1979120929931286L};
    public static long[] jjbitVec42 = new long[]{576460743713488896L, -277081220972545L, -1L, 2305629702346244095L};
    public static long[] jjbitVec43 = new long[]{-246290604654592L, 2047L, 562949953421311L, 0L};
    public static long[] jjbitVec44 = new long[]{-864691128455135250L, 281268803551231L, -3186861885341720594L, 4503392135166367L};
    public static long[] jjbitVec45 = new long[]{-3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393851839L};
    public static long[] jjbitVec46 = new long[]{-878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L};
    public static long[] jjbitVec47 = new long[]{-4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L};
    public static long[] jjbitVec48 = new long[]{-4323457841299070996L, 281212992110031L, 3457638613854978028L, 3377704004977791L};
    public static long[] jjbitVec49 = new long[]{-8646911284551352322L, 67076095L, 4323434403644581270L, 872365919L};
    public static long[] jjbitVec50 = new long[]{-4422530440275951615L, -554153860399361L, 2305843009196855263L, 64L};
    public static long[] jjbitVec51 = new long[]{272457864671395839L, 67044351L, -4294967296L, 36028797018898495L};
    public static long[] jjbitVec52 = new long[]{-2160230401L, 1123701017804671L, -4294967296L, 9007199254740991L};
    public static long[] jjbitVec53 = new long[]{0L, 0L, -1L, 4393886810111L};
    public static long[] jjbitVec54 = new long[]{-4227893248L, 72057594037927935L, 4398046511103L, 0L};
    public static long[] jjbitVec55 = new long[]{-9223235697412870144L, -9223094959924576255L, 281470681743360L, 9126739968L};
    public static long[] jjbitVec56 = new long[]{522136073208332512L, -2L, -6876561409L, Long.MAX_VALUE};
    public static long[] jjbitVec57 = new long[]{6881498031078244479L, -37L, 1125899906842623L, -524288L};
    public static long[] jjbitVec58 = new long[]{6755463865565184L, -11538275021824000L, -1L, -6917529027641081857L};
    public static long[] jjbitVec59 = new long[]{-8646911293074243568L, -137304735746L, Long.MAX_VALUE, 1008806742219095292L};
    public static long[] jjbitVec60 = new long[]{0L, 0L, -1L, -1L};
    public static int[] jjnextStates = new int[]{6, 3, 11, 8};
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
            "\ufeff",
            "~",
            ",",
            ";",
            "@",
            ".",
            "!",
            "?",
            "%",
            "*",
            "(",
            ")",
            "{",
            "}",
            "**",
            "-if",
            "***",
            "...",
            "enum",
            "-dump",
            "-keep",
            "class",
            "final",
            "public",
            "static",
            "native",
            "<init>",
            "bridge",
            "private",
            "extends",
            "-target",
            "-injars",
            "volatile",
            "strictfp",
            "<fields>",
            "-verbose",
            "-include",
            "-outjars",
            "protected",
            "<methods>",
            "abstract",
            "-android",
            "transient",
            "interface",
            "synthetic",
            "-keepcode",
            "-zipalign",
            "-dontnote",
            "-dontwarn",
            "@interface",
            "implements",
            "-keepnames",
            "module-info",
            "includecode",
            "-printseeds",
            "-dontshrink",
            "-printusage",
            "-libraryjars",
            "synchronized",
            "-applymapping",
            "-microedition",
            "-printmapping",
            "-assumevalues",
            "-dontoptimize",
            "-dontcompress",
            "allowshrinking",
            "-optimizations",
            "-dontpreverify",
            "-basedirectory",
            "-dontobfuscate",
            "-keepattributes",
            "-ignorewarnings",
            "allowobfuscation",
            "-keepdirectories",
            "-forceprocessing",
            "allowoptimization",
            "-keepclassmembers",
            "-keeppackagenames",
            "-repackageclasses",
            "-whyareyoukeeping",
            "-adaptclassstrings",
            "-keepkotlinmetadata",
            "-printconfiguration",
            "-keepparameternames",
            "-optimizationpasses",
            "-adaptkotlinmetadata",
            "-assumenosideeffects",
            "-overloadaggressively",
            "-keepclassmembernames",
            "-obfuscationdictionary",
            "-adaptresourcefilenames",
            "-keepclasseswithmembers",
            "-flattenpackagehierarchy",
            "-allowaccessmodification",
            "includedescriptorclasses",
            "-dontprocesskotlinmetadata",
            "-addconfigurationdebugging",
            "-renamesourcefileattribute",
            "-adaptresourcefilecontents",
            "-useuniqueclassmembernames",
            "-assumenoescapingparameters",
            "-dontusemixedcaseclassnames",
            "-keepclasseswithmembernames",
            "-classobfuscationdictionary",
            "-assumenoexternalsideeffects",
            "-skipnonpubliclibraryclasses",
            "-mergeinterfacesaggressively",
            "-assumenoexternalreturnvalues",
            "-packageobfuscationdictionary",
            "-dontskipnonpubliclibraryclasses",
            "-dontskipnonpubliclibraryclassmembers",
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
            2,
            0,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
    };
    public static long[] jjtoToken = new long[]{-1023L, -720575940379279361L, 0L};
    public static long[] jjtoSkip = new long[]{190L, 0L, 0L};
    public static long[] jjtoSpecial = new long[]{128L, 0L, 0L};
    public char curChar;
    public int jjmatchedPos;
    public int jjround;
    public int jjimageLen;
    public int jjnewStateCnt;
    public int jjmatchedKind;
    public PrintStream debugStream = System.out;
    public final int[] jjrounds = new int[12];
    public final int[] jjstateSet = new int[24];
    public final StringBuilder jjimage = new StringBuilder();
    public StringBuilder image = this.jjimage;
    public int curLexState = 0;
    public int defaultLexState = 0;
    public ProGuardConfigSimpleCharStream input_stream;

    private int jjMoveNfa_1() {
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
                                if (bd > 7) {
                                    bd = 7;
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
                            if (this.curChar == '\n' && bd > 7) {
                                bd = 7;
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

    public void ReInitRounds() {
        this.jjround = -2147483647;
        int ba = 12;

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                return;
            }

            this.jjrounds[ba] = Integer.MIN_VALUE;
        }
    }

    public int jjMoveStringLiteralDfa8_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(6, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(7, be, bf);
            return 8;
        }

        switch (this.curChar) {
            case '>':
                if ((be & 562949953421312L) != 0L) {
                    return this.jjStopAtPos(8, 49);
                }
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
            case 'q':
            default:
                return this.jjStartNfa_0(7, be, bf);
            case 'a':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 1677857028L);
            case 'c':
                if ((be & 18014398509481984L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 54, 13);
                }

                return this.jjMoveStringLiteralDfa9_0(be, 576460752303423488L, bf, 2748779069440L);
            case 'd':
                if ((be & 281474976710656L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 48, 13);
                }

                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 8589934592L);
            case 'e':
                if ((be & 9007199254740992L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 53, 13);
                } else if ((be & 36028797018963968L) != 0L) {
                    return this.jjStopAtPos(8, 55);
                } else {
                    if ((be & 144115188075855872L) != 0L) {
                        return this.jjStopAtPos(8, 57);
                    }

                    return this.jjMoveStringLiteralDfa9_0(be, 2305843009213693952L, bf, 1099512168449L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 562949953421312L);
            case 'g':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 16777216L);
            case 'i':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 4398048610898L);
            case 'j':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 8L);
            case 'k':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 8388608L);
            case 'l':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 134217728L);
            case 'm':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 140737488355328L);
            case 'n':
                if ((be & 72057594037927936L) != 0L) {
                    return this.jjStopAtPos(8, 56);
                } else {
                    if ((be & 288230376151711744L) != 0L) {
                        return this.jjStopAtPos(8, 58);
                    }

                    return this.jjMoveStringLiteralDfa9_0(be, 4611686018427387904L, bf, 268435456L);
                }
            case 'o':
                return this.jjMoveStringLiteralDfa9_0(be, Long.MIN_VALUE, bf, 28226666837835776L);
            case 'p':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 110338465748485280L);
            case 'r':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 65536L);
            case 's':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 299290505248768L);
            case 't':
                if ((be & 4503599627370496L) != 0L) {
                    return this.jjStartNfaWithStates_0(8, 52, 13);
                }

                return this.jjMoveStringLiteralDfa9_0(be, 1152921504606846976L, bf, 4503636134592512L);
            case 'u':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 35184372383744L);
            case 'v':
                return this.jjMoveStringLiteralDfa9_0(be, 0L, bf, 8192L);
        }
    }

    public int jjMoveStringLiteralDfa36_0(long ba) {
        long bb = 72057594037927936L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(34, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(35, 0L, bb);
            return 36;
        }

        switch (this.curChar) {
            case 's':
                if ((bb & 72057594037927936L) != 0L) {
                    return this.jjStopAtPos(36, 120);
                }
            default:
                return this.jjStartNfa_0(35, 0L, bb);
        }
    }

    public int jjMoveStringLiteralDfa6_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(4, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(5, be, bf);
            return 6;
        }

        switch (this.curChar) {
            case '-':
                return this.jjMoveStringLiteralDfa7_0(be, 4611686018427387904L, bf, 0L);
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
            case 'q':
            default:
                return this.jjStartNfa_0(5, be, bf);
            case 'a':
                return this.jjMoveStringLiteralDfa7_0(be, 2603080584620146688L, bf, 550301073408L);
            case 'b':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 294912L);
            case 'c':
                return this.jjMoveStringLiteralDfa7_0(be, 1125899906842624L, bf, 34695282688L);
            case 'd':
                return this.jjMoveStringLiteralDfa7_0(be, 633318697598976L, bf, 0L);
            case 'e':
                if ((be & 274877906944L) != 0L) {
                    return this.jjStartNfaWithStates_0(6, 38, 13);
                }

                return this.jjMoveStringLiteralDfa7_0(be, -8065946932620558336L, bf, 10213642716971328L);
            case 'f':
                return this.jjMoveStringLiteralDfa7_0(be, 576469548396445696L, bf, 0L);
            case 'g':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 18014398509481984L);
            case 'h':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 2050L);
            case 'i':
                return this.jjMoveStringLiteralDfa7_0(be, 74309393851613184L, bf, 4538785073745920L);
            case 'k':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 108086393221152768L);
            case 'l':
                return this.jjMoveStringLiteralDfa7_0(be, 4398046511104L, bf, 281629599727616L);
            case 'm':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 160L);
            case 'n':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 4398046511104L);
            case 'o':
                return this.jjMoveStringLiteralDfa7_0(be, 180143985094819840L, bf, 2814758491259920L);
            case 'p':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 3146240L);
            case 'r':
                return this.jjMoveStringLiteralDfa7_0(be, 140737488355328L, bf, 19859928784904L);
            case 's':
                if ((be & 549755813888L) != 0L) {
                    return this.jjStartNfaWithStates_0(6, 39, 13);
                } else {
                    if ((be & 2199023255552L) != 0L) {
                        return this.jjStopAtPos(6, 41);
                    }

                    return this.jjMoveStringLiteralDfa7_0(be, 52776558133248L, bf, 140737488355329L);
                }
            case 't':
                if ((be & 1099511627776L) != 0L) {
                    return this.jjStopAtPos(6, 40);
                }

                return this.jjMoveStringLiteralDfa7_0(be, 18295873486192640L, bf, 65536L);
            case 'u':
                return this.jjMoveStringLiteralDfa7_0(be, 0L, bf, 4L);
        }
    }

    public int jjMoveStringLiteralDfa3_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(1, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(2, be, bf);
            return 3;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa4_0(be, 4402341478400L, bf, 580887951441920L);
            case 'b':
                return this.jjMoveStringLiteralDfa4_0(be, 0L, bf, 8L);
            case 'c':
                return this.jjMoveStringLiteralDfa4_0(be, 70368744177664L, bf, 18014398509482064L);
            case 'd':
                return this.jjMoveStringLiteralDfa4_0(be, 2251937252638720L, bf, 4398046511104L);
            case 'e':
                return this.jjMoveStringLiteralDfa4_0(be, 2350897148502999040L, bf, 316823241818112L);
            case 'f':
                return this.jjMoveStringLiteralDfa4_0(be, 0L, bf, 34359738368L);
            case 'g':
            case 'h':
            case 'k':
            case 'q':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(2, be, bf);
            case 'i':
                return this.jjMoveStringLiteralDfa4_0(be, 8899172237312L, bf, 2251800082120837L);
            case 'j':
                return this.jjMoveStringLiteralDfa4_0(be, 2199023255552L, bf, 0L);
            case 'l':
                return this.jjMoveStringLiteralDfa4_0(be, -8070450523657994240L, bf, 1649267441664L);
            case 'm':
                if ((be & 268435456L) != 0L) {
                    return this.jjStartNfaWithStates_0(3, 28, 13);
                }

                return this.jjMoveStringLiteralDfa4_0(be, 536870912L, bf, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa4_0(be, 436849163854938112L, bf, 108238123661698562L);
            case 'o':
                return this.jjMoveStringLiteralDfa4_0(be, 0L, bf, 2361344L);
            case 'p':
                return this.jjMoveStringLiteralDfa4_0(be, 72057594037927936L, bf, 16777248L);
            case 'r':
                return this.jjMoveStringLiteralDfa4_0(be, 36283883716608L, bf, 4503599628419072L);
            case 's':
                return this.jjMoveStringLiteralDfa4_0(be, 2147483648L, bf, 10203472200745216L);
            case 't':
                return this.jjMoveStringLiteralDfa4_0(be, 596586230318104576L, bf, 1073745920L);
            case 'u':
                return this.jjMoveStringLiteralDfa4_0(be, 4611686018427387904L, bf, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa4_0(be, 274877906944L, bf, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa4_0(be, 0L, bf, 33554432L);
        }
    }

    public static final boolean jjCanMove_0(int ba, int bb, int bc, long bd, long be) {
        switch (ba) {
            case 0:
                return (jjbitVec1[bc] & be) != 0L;
            case 2:
                return (jjbitVec2[bc] & be) != 0L;
            case 3:
                return (jjbitVec3[bc] & be) != 0L;
            case 4:
                return (jjbitVec4[bc] & be) != 0L;
            case 5:
                return (jjbitVec5[bc] & be) != 0L;
            case 6:
                return (jjbitVec6[bc] & be) != 0L;
            case 7:
                return (jjbitVec7[bc] & be) != 0L;
            case 9:
                return (jjbitVec8[bc] & be) != 0L;
            case 10:
                return (jjbitVec9[bc] & be) != 0L;
            case 11:
                return (jjbitVec10[bc] & be) != 0L;
            case 12:
                return (jjbitVec11[bc] & be) != 0L;
            case 13:
                return (jjbitVec12[bc] & be) != 0L;
            case 14:
                return (jjbitVec13[bc] & be) != 0L;
            case 15:
                return (jjbitVec14[bc] & be) != 0L;
            case 16:
                return (jjbitVec15[bc] & be) != 0L;
            case 17:
                return (jjbitVec16[bc] & be) != 0L;
            case 18:
                return (jjbitVec17[bc] & be) != 0L;
            case 19:
                return (jjbitVec18[bc] & be) != 0L;
            case 20:
                return (jjbitVec19[bc] & be) != 0L;
            case 22:
                return (jjbitVec20[bc] & be) != 0L;
            case 23:
                return (jjbitVec21[bc] & be) != 0L;
            case 24:
                return (jjbitVec22[bc] & be) != 0L;
            case 30:
                return (jjbitVec23[bc] & be) != 0L;
            case 31:
                return (jjbitVec24[bc] & be) != 0L;
            case 32:
                return (jjbitVec25[bc] & be) != 0L;
            case 33:
                return (jjbitVec26[bc] & be) != 0L;
            case 48:
                return (jjbitVec27[bc] & be) != 0L;
            case 49:
                return (jjbitVec28[bc] & be) != 0L;
            case 77:
                return (jjbitVec29[bc] & be) != 0L;
            case 159:
                return (jjbitVec30[bc] & be) != 0L;
            case 164:
                return (jjbitVec31[bc] & be) != 0L;
            case 215:
                return (jjbitVec32[bc] & be) != 0L;
            case 250:
                return (jjbitVec33[bc] & be) != 0L;
            case 251:
                return (jjbitVec34[bc] & be) != 0L;
            case 253:
                return (jjbitVec35[bc] & be) != 0L;
            case 254:
                return (jjbitVec36[bc] & be) != 0L;
            case 255:
                return (jjbitVec37[bc] & be) != 0L;
            default:
                return (jjbitVec0[bb] & bd) != 0L;
        }
    }

    public ProGuardConfigTokenManager(ProGuardConfigSimpleCharStream proGuardConfigSimpleCharStream) {
        this.input_stream = proGuardConfigSimpleCharStream;
    }

    public void jjCheckNAddStates() {
        int ba = 0;
        ProGuardConfigTokenManager proGuardConfigTokenManager1 = this;
        int[] bb = jjnextStates;

        while (true) {
            proGuardConfigTokenManager1.jjCheckNAdd(bb[ba]);
            if (ba++ == 3) {
                return;
            }

            proGuardConfigTokenManager1 = this;
            bb = jjnextStates;
        }
    }

    public int jjMoveStringLiteralDfa18_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(16, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(17, 0L, bc);
            return 18;
        }

        switch (this.curChar) {
            case 'a':
                if ((bc & 134217728L) != 0L) {
                    return this.jjStopAtPos(18, 91);
                }

                return this.jjMoveStringLiteralDfa19_0(bc, 2322168557862912L);
            case 'b':
                return this.jjMoveStringLiteralDfa19_0(bc, 35184372088832L);
            case 'c':
                return this.jjMoveStringLiteralDfa19_0(bc, 18141941858304L);
            case 'e':
                return this.jjMoveStringLiteralDfa19_0(bc, 9011605891186688L);
            case 'g':
                return this.jjMoveStringLiteralDfa19_0(bc, 4503599627370496L);
            case 'i':
                return this.jjMoveStringLiteralDfa19_0(bc, 1688849860263936L);
            case 'l':
                return this.jjMoveStringLiteralDfa19_0(bc, 108228228056875008L);
            case 'm':
                return this.jjMoveStringLiteralDfa19_0(bc, 283828618788864L);
            case 'n':
                if ((bc & 268435456L) != 0L) {
                    return this.jjStopAtPos(18, 92);
                }

                return this.jjMoveStringLiteralDfa19_0(bc, 18014501588697088L);
            case 'r':
                return this.jjMoveStringLiteralDfa19_0(bc, 274877906944L);
            case 's':
                if ((bc & 536870912L) != 0L) {
                    return this.jjStopAtPos(18, 93);
                } else if ((bc & 1073741824L) != 0L) {
                    return this.jjStopAtPos(18, 94);
                }
            case 'd':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'o':
            case 'p':
            case 'q':
            default:
                return this.jjStartNfa_0(17, 0L, bc);
            case 't':
                return this.jjMoveStringLiteralDfa19_0(bc, 8802535473152L);
        }
    }

    public int jjMoveStringLiteralDfa15_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(13, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(14, 0L, bc);
            return 15;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa16_0(bc, 10133101845938176L);
            case 'd':
                return this.jjMoveStringLiteralDfa16_0(bc, 134217728L);
            case 'e':
                return this.jjMoveStringLiteralDfa16_0(bc, 25165824L);
            case 'f':
                return this.jjMoveStringLiteralDfa16_0(bc, 4294967296L);
            case 'g':
                if ((bc & 1048576L) != 0L) {
                    return this.jjStopAtPos(15, 84);
                }
            case 'b':
            case 'c':
            case 'j':
            case 'k':
            case 'p':
            case 'q':
            default:
                return this.jjStartNfa_0(14, 0L, bc);
            case 'h':
                return this.jjMoveStringLiteralDfa16_0(bc, 281887293571072L);
            case 'i':
                return this.jjMoveStringLiteralDfa16_0(bc, 2270010475020288L);
            case 'l':
                return this.jjMoveStringLiteralDfa16_0(bc, 108097386173169664L);
            case 'm':
                return this.jjMoveStringLiteralDfa16_0(bc, 35184372088832L);
            case 'n':
                if ((bc & 262144L) != 0L) {
                    return this.jjStartNfaWithStates_0(15, 82, 13);
                }

                return this.jjMoveStringLiteralDfa16_0(bc, 70368844840960L);
            case 'o':
                return this.jjMoveStringLiteralDfa16_0(bc, 568447513657344L);
            case 'r':
                return this.jjMoveStringLiteralDfa16_0(bc, 17184063488L);
            case 's':
                if ((bc & 524288L) != 0L) {
                    return this.jjStopAtPos(15, 83);
                }

                return this.jjMoveStringLiteralDfa16_0(bc, 4644346779402240L);
            case 't':
                return this.jjMoveStringLiteralDfa16_0(bc, 18014433137655808L);
        }
    }

    public int jjMoveStringLiteralDfa20_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(18, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(19, 0L, bc);
            return 20;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa21_0(bc, 70368744177664L);
            case 'b':
                return this.jjMoveStringLiteralDfa21_0(bc, 108086391056891904L);
            case 'c':
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'o':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(19, 0L, bc);
            case 'e':
                return this.jjMoveStringLiteralDfa21_0(bc, 5911111949877248L);
            case 'i':
                return this.jjMoveStringLiteralDfa21_0(bc, 18014398509481984L);
            case 'm':
                return this.jjMoveStringLiteralDfa21_0(bc, 68719476736L);
            case 'n':
                return this.jjMoveStringLiteralDfa21_0(bc, 17592186044416L);
            case 'r':
                return this.jjMoveStringLiteralDfa21_0(bc, 44289702756352L);
            case 's':
                if ((bc & 17179869184L) != 0L) {
                    return this.jjStopAtPos(20, 98);
                }

                return this.jjMoveStringLiteralDfa21_0(bc, 141836999983104L);
            case 't':
                return this.jjMoveStringLiteralDfa21_0(bc, 565698732490752L);
            case 'u':
                return this.jjMoveStringLiteralDfa21_0(bc, 9011597301252096L);
            case 'y':
                return (bc & 8589934592L) != 0L ? this.jjStopAtPos(20, 97) : this.jjMoveStringLiteralDfa21_0(bc, 2251799813685248L);
        }
    }

    public int jjMoveStringLiteralDfa5_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(3, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(4, be, bf);
            return 5;
        }

        switch (this.curChar) {
            case '>':
                if ((be & 68719476736L) != 0L) {
                    return this.jjStopAtPos(5, 36);
                }
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
            case 'q':
            case 'v':
            case 'x':
            default:
                return this.jjStartNfa_0(4, be, bf);
            case 'a':
                return this.jjMoveStringLiteralDfa6_0(be, 1266637395197952L, bf, 18014398509547528L);
            case 'c':
                if ((be & 8589934592L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 33, 13);
                } else {
                    if ((be & 17179869184L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 34, 13);
                    }

                    return this.jjMoveStringLiteralDfa6_0(be, 36310271995674624L, bf, 281629616505856L);
                }
            case 'd':
                return this.jjMoveStringLiteralDfa6_0(be, -9223353894912917504L, bf, 1099512168448L);
            case 'e':
                if ((be & 34359738368L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 35, 13);
                } else {
                    if ((be & 137438953472L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 37, 13);
                    }

                    return this.jjMoveStringLiteralDfa6_0(be, 4629701516448497664L, bf, 4503599628419072L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa6_0(be, 9007199254740992L, bf, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa6_0(be, 4507997673881600L, bf, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa6_0(be, 0L, bf, 134217728L);
            case 'l':
                return this.jjMoveStringLiteralDfa6_0(be, 72057594037927936L, bf, 8589934592L);
            case 'm':
                return this.jjMoveStringLiteralDfa6_0(be, 1152921504606846976L, bf, 10212269367496960L);
            case 'n':
                return this.jjMoveStringLiteralDfa6_0(be, 2449958197289549824L, bf, 2286984185774080L);
            case 'o':
                return this.jjMoveStringLiteralDfa6_0(be, 2849934139195392L, bf, 4398048903744L);
            case 'p':
                return this.jjMoveStringLiteralDfa6_0(be, 0L, bf, 2199568523264L);
            case 'r':
                return this.jjMoveStringLiteralDfa6_0(be, 576462951326679040L, bf, 33685520L);
            case 's':
                return this.jjMoveStringLiteralDfa6_0(be, 0L, bf, 108649375370053634L);
            case 't':
                return this.jjMoveStringLiteralDfa6_0(be, 9070970929152L, bf, 17938266456197L);
            case 'u':
                return this.jjMoveStringLiteralDfa6_0(be, 70368744177664L, bf, 140737488355328L);
            case 'w':
                return this.jjMoveStringLiteralDfa6_0(be, 288230376151711744L, bf, 549755813888L);
            case 'y':
                return this.jjMoveStringLiteralDfa6_0(be, 0L, bf, 32L);
        }
    }

    public final int jjStartNfa_0(int ba, long bb, long bc) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(ba, bb, bc), ba + 1);
    }

    public ProGuardConfigToken getNextToken() {
        ProGuardConfigToken proGuardConfigToken = null;
        int ba = 0;

        label124:
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            } catch (IOException iOException) {
                this.jjmatchedKind = 0;
                ProGuardConfigToken proGuardConfigToken1 = this.jjFillToken();
                proGuardConfigToken1.w = proGuardConfigToken;
                return proGuardConfigToken1;
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
                            continue label124;
                        }

                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 8) {
                            this.jjmatchedKind = 8;
                        }
                        break;
                    case 2:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        ba = this.jjMoveStringLiteralDfa0_2();
                }

                if (this.jjmatchedKind == Integer.MAX_VALUE) {
                    break label124;
                }

                if (this.jjmatchedPos + 1 < ba) {
                    this.input_stream.backup(ba - this.jjmatchedPos - 1);
                }

                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                    ProGuardConfigToken proGuardConfigToken3 = this.jjFillToken();
                    proGuardConfigToken3.w = proGuardConfigToken;
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }

                    return proGuardConfigToken3;
                }

                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                    int[] bh;
                    if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                        ProGuardConfigToken proGuardConfigToken2 = this.jjFillToken();
                        if (proGuardConfigToken == null) {
                            proGuardConfigToken = proGuardConfigToken2;
                        } else {
                            proGuardConfigToken2.w = proGuardConfigToken;
                            proGuardConfigToken = proGuardConfigToken.Y = proGuardConfigToken2;
                        }

                        this.TokenLexicalActions();
                        bh = jjnewLexState;
                    } else {
                        this.TokenLexicalActions();
                        bh = jjnewLexState;
                    }

                    if (bh[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    break;
                }

                this.jjimageLen = this.jjimageLen + this.jjmatchedPos + 1;
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }

                ba = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;

                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException iOException2) {
                    break label124;
                }

                curLexState = this.curLexState;
            }
        }

        int line = this.input_stream.getLine();
        int column = this.input_stream.getColumn();
        String string = null;
        boolean bl = false;

        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        } catch (IOException iOException1) {
            bl = true;
            string = ba <= 1 ? "" : this.input_stream.GetImage();
            if (this.curChar != '\n' && this.curChar != '\r') {
                column++;
            } else {
                line++;
                column = 0;
            }
        }

        if (!bl) {
            this.input_stream.backup(1);
            string = ba <= 1 ? "" : this.input_stream.GetImage();
        }

        char bd = this.curChar;
        String string1 = string;
        int be = column;
        int bf = line;
        boolean bl1 = bl;
        throw new ProGuardConfigTokenMgrError(bl1, bf, be, string1, bd);
    }

    public int jjMoveStringLiteralDfa27_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(25, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(26, 0L, bc);
            return 27;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa28_0(bc, 108086391056891904L);
            case 'e':
                return this.jjMoveStringLiteralDfa28_0(bc, 9007199254740992L);
            case 'r':
                return this.jjMoveStringLiteralDfa28_0(bc, 18014398509481984L);
            case 's':
                if ((bc & 1125899906842624L) != 0L) {
                    return this.jjStopAtPos(27, 114);
                }

                if ((bc & 2251799813685248L) != 0L) {
                    return this.jjStopAtPos(27, 115);
                }
                break;
            case 'y':
                if ((bc & 4503599627370496L) != 0L) {
                    return this.jjStopAtPos(27, 116);
                }
        }

        return this.jjStartNfa_0(26, 0L, bc);
    }

    public int jjMoveStringLiteralDfa11_0(long ba, long bb, long bc) {
        long bd = bc;
        if ((0L & ba | (bd = bd & bb)) == 0L) {
            return this.jjStartNfa_0(9, ba, bb);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(10, 0L, bd);
            return 11;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa2_0(bd, 294912L);
            case 'b':
            case 'h':
            case 'j':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            default:
                return this.jjStartNfa_0(10, 0L, bd);
            case 'c':
                return this.jjMoveStringLiteralDfa2_0(bd, 642114790621184L);
            case 'd':
                if ((bd & 16L) != 0L) {
                    return this.jjStartNfaWithStates_0(11, 68, 13);
                }

                return this.jjMoveStringLiteralDfa2_0(bd, 4294967296L);
            case 'e':
                return this.jjMoveStringLiteralDfa2_0(bd, 140754714362112L);
            case 'f':
                return this.jjMoveStringLiteralDfa2_0(bd, 4503599627370496L);
            case 'g':
                return this.jjMoveStringLiteralDfa2_0(bd, 8858370048L);
            case 'i':
                return this.jjMoveStringLiteralDfa2_0(bd, 141312L);
            case 'k':
                return this.jjMoveStringLiteralDfa2_0(bd, 274877906944L);
            case 'l':
                return this.jjMoveStringLiteralDfa2_0(bd, 2286984202551296L);
            case 'm':
                return this.jjMoveStringLiteralDfa2_0(bd, 134217728L);
            case 'n':
                return this.jjMoveStringLiteralDfa2_0(bd, 108086427564114080L);
            case 'o':
                return this.jjMoveStringLiteralDfa2_0(bd, 1074286656L);
            case 'r':
                return this.jjMoveStringLiteralDfa2_0(bd, 23158463660032L);
            case 's':
                if ((bd & 8L) != 0L) {
                    return this.jjStopAtPos(11, 67);
                }

                return this.jjMoveStringLiteralDfa2_0(bd, 284361262892032L);
            case 't':
                return this.jjMoveStringLiteralDfa2_0(bd, 10133099698454528L);
            case 'u':
                return this.jjMoveStringLiteralDfa2_0(bd, 18014398509547520L);
            case 'z':
                return this.jjMoveStringLiteralDfa2_0(bd, 2097664L);
        }
    }

    public int jjMoveStringLiteralDfa24_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(22, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(23, 0L, bc);
            return 24;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa25_0(bc, 9570149208162304L);
            case 'b':
            case 'c':
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'p':
            case 'q':
            case 'r':
            case 'u':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(23, 0L, bc);
            case 'e':
                return this.jjMoveStringLiteralDfa25_0(bc, 1231453023109120L);
            case 'm':
                return this.jjMoveStringLiteralDfa25_0(bc, 422212465065984L);
            case 'n':
                return this.jjMoveStringLiteralDfa25_0(bc, 4398046511104L);
            case 'o':
                return this.jjMoveStringLiteralDfa25_0(bc, 18014398509481984L);
            case 's':
                return this.jjMoveStringLiteralDfa25_0(bc, 2251799813685248L);
            case 't':
                return this.jjMoveStringLiteralDfa25_0(bc, 28587302322176L);
            case 'v':
                return this.jjMoveStringLiteralDfa25_0(bc, 4503599627370496L);
            case 'y':
                return this.jjMoveStringLiteralDfa25_0(bc, 108086391056891904L);
        }
    }

    public int jjMoveNfa_0(int ba, int bb) {
        int bc = bb;
        int bh = 0;
        this.jjnewStateCnt = 12;
        int bi = 1;
        this.jjstateSet[0] = ba;
        int bj = Integer.MAX_VALUE;

        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }

            if (this.curChar < '@') {
                long ck = 1L << this.curChar;
                int[] co = this.jjstateSet;

                while (true) {
                    bi += -1;
                    switch (co[bi]) {
                        case 0:
                            if ((287948901175001088L & ck) != 0L) {
                                if (bj > 124) {
                                    bj = 124;
                                }

                                this.jjCheckNAdd(0);
                            }
                            break;
                        case 1:
                            if ((-8935348299901189633L & ck) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddStates();
                            } else if (this.curChar == '%') {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAdd(8);
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }

                            if ((287948901175001088L & ck) != 0L) {
                                if (bj > 124) {
                                    bj = 124;
                                }

                                this.jjCheckNAdd(0);
                            } else if (this.curChar == '$') {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                            break;
                        case 2:
                            if ((287948970162897407L & ck) != 0L) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                            break;
                        case 3:
                            if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }
                            break;
                        case 4:
                            if ((287667426198290432L & ck) != 0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 5;
                            }
                            break;
                        case 5:
                            if (this.curChar == '>') {
                                bj = 126;
                                this.jjCheckNAddTwoStates(6, 3);
                            }
                            break;
                        case 6:
                            if ((-8935348299901189633L & ck) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddTwoStates(6, 3);
                            }
                            break;
                        case 7:
                            if (this.curChar == '%') {
                                bj = 127;
                                this.jjCheckNAdd(8);
                            }
                        case 8:
                        case 9:
                        default:
                            break;
                        case 10:
                            if ((-8935348299901189633L & ck) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 11:
                            if ((-8935348299901189633L & ck) != 0L) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager7;
                                byte dj;
                                if (bj > 127) {
                                    bj = 127;
                                    proGuardConfigTokenManager7 = this;
                                    long db = 15152030326468L;
                                    dj = 11;
                                } else {
                                    proGuardConfigTokenManager7 = this;
                                    long dc = 15152030326468L;
                                    dj = 11;
                                }

                                Integer integer3 = 8;
                                Integer integer2 = Integer.valueOf(dj);
                                proGuardConfigTokenManager7.jjCheckNAddTwoStates(integer2, integer3);
                            }
                            break;
                        case 12:
                            if ((-8935348299901189633L & ck) != 0L) {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAddTwoStates(11, 8);
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }

                            if ((-8935348299901189633L & ck) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddTwoStates(6, 3);
                            }
                            break;
                        case 13:
                            if ((-8935348299901189633L & ck) != 0L) {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAddTwoStates(11, 8);
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }

                            long cp;
                            if ((-8935348299901189633L & ck) != 0L) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager6;
                                byte dh;
                                if (bj > 126) {
                                    bj = 126;
                                    proGuardConfigTokenManager6 = this;
                                    long cz = 15152030326468L;
                                    dh = 6;
                                } else {
                                    proGuardConfigTokenManager6 = this;
                                    long da = 15152030326468L;
                                    dh = 6;
                                }

                                Integer integer1 = 3;
                                Integer integer = Integer.valueOf(dh);
                                proGuardConfigTokenManager6.jjCheckNAddTwoStates(integer, integer1);
                                cp = 287948970162897407L;
                            } else {
                                cp = 287948970162897407L;
                            }

                            if ((cp & ck) != 0L) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                    }

                    if (bi == bh) {
                        break;
                    }

                    co = this.jjstateSet;
                }
            } else if (this.curChar < 128) {
                long cj = 1L << (this.curChar & '?');
                int[] cl = this.jjstateSet;

                while (true) {
                    bi += -1;
                    switch (cl[bi]) {
                        case 1:
                            long cn;
                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddStates();
                                cn = 576460745995190270L;
                            } else {
                                cn = 576460745995190270L;
                            }

                            if ((cn & cj) != 0L) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                            break;
                        case 2:
                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        default:
                            break;
                        case 6:
                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddTwoStates(6, 3);
                            }
                            break;
                        case 8:
                            if (this.curChar == '[') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            }
                            break;
                        case 9:
                            if (this.curChar == ']') {
                                bj = 127;
                                this.jjCheckNAdd(8);
                            }
                            break;
                        case 10:
                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 11:
                            if ((-8646911290859585538L & cj) != 0L) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager5;
                                byte dg;
                                if (bj > 127) {
                                    bj = 127;
                                    proGuardConfigTokenManager5 = this;
                                    long cx = 15152030326468L;
                                    dg = 11;
                                } else {
                                    proGuardConfigTokenManager5 = this;
                                    long cy = 15152030326468L;
                                    dg = 11;
                                }

                                Integer integer7 = 8;
                                Integer integer6 = Integer.valueOf(dg);
                                proGuardConfigTokenManager5.jjCheckNAddTwoStates(integer6, integer7);
                            }
                            break;
                        case 12:
                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAddTwoStates(11, 8);
                            } else if (this.curChar == '[') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            }

                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddTwoStates(6, 3);
                            }
                            break;
                        case 13:
                            if ((-8646911290859585538L & cj) != 0L) {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAddTwoStates(11, 8);
                            } else if (this.curChar == '[') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            }

                            long cm;
                            if ((-8646911290859585538L & cj) != 0L) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager4;
                                byte df;
                                if (bj > 126) {
                                    bj = 126;
                                    proGuardConfigTokenManager4 = this;
                                    long cv = 15152030326468L;
                                    df = 6;
                                } else {
                                    proGuardConfigTokenManager4 = this;
                                    long cw = 15152030326468L;
                                    df = 6;
                                }

                                Integer integer5 = 3;
                                Integer integer4 = Integer.valueOf(df);
                                proGuardConfigTokenManager4.jjCheckNAddTwoStates(integer4, integer5);
                                cm = -8646911290859585538L;
                            } else {
                                cm = -8646911290859585538L;
                            }

                            if ((cm & cj) != 0L) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                    }

                    if (bi == bh) {
                        break;
                    }

                    cl = this.jjstateSet;
                }
            } else {
                int bk = this.curChar >> '\b';
                int bl = bk >> 6;
                long bm = 1L << (bk & 63);
                int bn = (this.curChar & 255) >> 6;
                long bo = 1L << (this.curChar & '?');
                int[] dk = this.jjstateSet;

                while (true) {
                    bi += -1;
                    switch (dk[bi]) {
                        case 1:
                            long bt = bo;
                            long by = bm;
                            int cd = bn;
                            int ci = bl;
                            if (jjCanMove_0(bk, ci, cd, by, bt)) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }

                            bt = bo;
                            by = bm;
                            cd = bn;
                            ci = bl;
                            if (jjCanMove_1(bk, ci, cd, by, bt)) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 2:
                            long bs = bo;
                            long bx = bm;
                            int cc = bn;
                            int ch = bl;
                            if (jjCanMove_1(bk, ch, cc, bx, bs)) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                        default:
                            break;
                        case 6:
                            if (jjCanMove_1(bk, bl, bn, bm, bo)) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddTwoStates(6, 3);
                            }
                            break;
                        case 10:
                            long br = bo;
                            long bw = bm;
                            int cb = bn;
                            int cg = bl;
                            if (jjCanMove_1(bk, cg, cb, bw, br)) {
                                if (bj > 126) {
                                    bj = 126;
                                }

                                this.jjCheckNAddStates();
                            }
                            break;
                        case 11:
                            long bq = bo;
                            long bv = bm;
                            int ca = bn;
                            int cf = bl;
                            if (jjCanMove_1(bk, cf, ca, bv, bq)) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager3;
                                byte de;
                                if (bj > 127) {
                                    bj = 127;
                                    proGuardConfigTokenManager3 = this;
                                    long ct = 15152030326468L;
                                    de = 11;
                                } else {
                                    proGuardConfigTokenManager3 = this;
                                    long cu = 15152030326468L;
                                    de = 11;
                                }

                                Integer integer13 = 8;
                                Integer integer12 = Integer.valueOf(de);
                                proGuardConfigTokenManager3.jjCheckNAddTwoStates(integer12, integer13);
                            }
                            break;
                        case 12:
                            long bp = bo;
                            long bu = bm;
                            int bz = bn;
                            int ce = bl;
                            if (jjCanMove_1(bk, ce, bz, bu, bp)) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager2;
                                byte dd;
                                if (bj > 126) {
                                    bj = 126;
                                    proGuardConfigTokenManager2 = this;
                                    long cr = 15152030326468L;
                                    dd = 6;
                                } else {
                                    proGuardConfigTokenManager2 = this;
                                    long cs = 15152030326468L;
                                    dd = 6;
                                }

                                Integer integer11 = 3;
                                Integer integer10 = Integer.valueOf(dd);
                                proGuardConfigTokenManager2.jjCheckNAddTwoStates(integer10, integer11);
                            }

                            bp = bo;
                            bu = bm;
                            bz = bn;
                            ce = bl;
                            if (jjCanMove_1(bk, ce, bz, bu, bp)) {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAddTwoStates(11, 8);
                            }
                            break;
                        case 13:
                            long bd = bo;
                            long be = bm;
                            int bf = bn;
                            int bg = bl;
                            if (jjCanMove_1(bk, bg, bf, be, bd)) {
                                if (bj > 125) {
                                    bj = 125;
                                }

                                this.jjCheckNAdd(2);
                            }

                            bd = bo;
                            be = bm;
                            bf = bn;
                            bg = bl;
                            if (jjCanMove_1(bk, bg, bf, be, bd)) {
                                ProGuardConfigTokenManager proGuardConfigTokenManager1;
                                byte dm;
                                if (bj > 126) {
                                    bj = 126;
                                    proGuardConfigTokenManager1 = this;
                                    long dl = 15152030326468L;
                                    dm = 6;
                                } else {
                                    proGuardConfigTokenManager1 = this;
                                    long cq = 15152030326468L;
                                    dm = 6;
                                }

                                Integer integer9 = 3;
                                Integer integer8 = Integer.valueOf(dm);
                                proGuardConfigTokenManager1.jjCheckNAddTwoStates(integer8, integer9);
                            }

                            bd = bo;
                            be = bm;
                            bf = bn;
                            bg = bl;
                            if (jjCanMove_1(bk, bg, bf, be, bd)) {
                                if (bj > 127) {
                                    bj = 127;
                                }

                                this.jjCheckNAddTwoStates(11, 8);
                            }
                    }

                    if (bi == bh) {
                        break;
                    }

                    dk = this.jjstateSet;
                }
            }

            if (bj != Integer.MAX_VALUE) {
                this.jjmatchedKind = bj;
                this.jjmatchedPos = bc;
                bj = Integer.MAX_VALUE;
            }

            bc++;
            if ((bi = this.jjnewStateCnt) == (bh = 12 - (this.jjnewStateCnt = bh))) {
                return bc;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException iOException) {
                return bc;
            }
        }
    }

    public int jjMoveStringLiteralDfa9_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(7, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(8, be, bf);
            return 9;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 283476230152L);
            case 'b':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 18014398509481984L);
            case 'c':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 1589248L);
            case 'd':
                return this.jjMoveStringLiteralDfa10_0(be, Long.MIN_VALUE, bf, 1L);
            case 'e':
                if ((be & 576460752303423488L) != 0L) {
                    return this.jjStopAtPos(9, 59);
                }

                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 14745000701075456L);
            case 'f':
                return this.jjMoveStringLiteralDfa10_0(be, 4611686018427387904L, bf, 268435456L);
            case 'g':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 4398046511108L);
            case 'h':
            case 'j':
            case 'k':
            case 'q':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            default:
                return this.jjStartNfa_0(8, be, bf);
            case 'i':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 140771982376960L);
            case 'l':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 2147483904L);
            case 'm':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 538968576L);
            case 'n':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 108086391056893954L);
            case 'o':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 17660905521152L);
            case 'p':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 160L);
            case 'r':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 132096L);
            case 's':
                if ((be & 1152921504606846976L) != 0L) {
                    return this.jjStartNfaWithStates_0(9, 60, 13);
                } else {
                    if ((be & 2305843009213693952L) != 0L) {
                        return this.jjStopAtPos(9, 61);
                    }

                    return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 282733473726464L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 1073745984L);
            case 'u':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 2823545893683200L);
            case 'z':
                return this.jjMoveStringLiteralDfa10_0(be, 0L, bf, 16L);
        }
    }

    public int jjMoveStringLiteralDfa34_0(long ba) {
        long bb = 72057594037927936L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(32, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(33, 0L, bb);
            return 34;
        }

        switch (this.curChar) {
            case 'e':
                return this.jjMoveStringLiteralDfa35_0(bb);
            default:
                return this.jjStartNfa_0(33, 0L, bb);
        }
    }

    public static final boolean jjCanMove_1(int ba, int bb, int bc, long bd, long be) {
        switch (ba) {
            case 0:
                return (jjbitVec38[bc] & be) != 0L;
            case 2:
                return (jjbitVec2[bc] & be) != 0L;
            case 3:
                return (jjbitVec39[bc] & be) != 0L;
            case 4:
                return (jjbitVec40[bc] & be) != 0L;
            case 5:
                return (jjbitVec41[bc] & be) != 0L;
            case 6:
                return (jjbitVec42[bc] & be) != 0L;
            case 7:
                return (jjbitVec43[bc] & be) != 0L;
            case 9:
                return (jjbitVec44[bc] & be) != 0L;
            case 10:
                return (jjbitVec45[bc] & be) != 0L;
            case 11:
                return (jjbitVec46[bc] & be) != 0L;
            case 12:
                return (jjbitVec47[bc] & be) != 0L;
            case 13:
                return (jjbitVec48[bc] & be) != 0L;
            case 14:
                return (jjbitVec49[bc] & be) != 0L;
            case 15:
                return (jjbitVec50[bc] & be) != 0L;
            case 16:
                return (jjbitVec51[bc] & be) != 0L;
            case 17:
                return (jjbitVec16[bc] & be) != 0L;
            case 18:
                return (jjbitVec17[bc] & be) != 0L;
            case 19:
                return (jjbitVec52[bc] & be) != 0L;
            case 20:
                return (jjbitVec19[bc] & be) != 0L;
            case 22:
                return (jjbitVec20[bc] & be) != 0L;
            case 23:
                return (jjbitVec53[bc] & be) != 0L;
            case 24:
                return (jjbitVec54[bc] & be) != 0L;
            case 30:
                return (jjbitVec23[bc] & be) != 0L;
            case 31:
                return (jjbitVec24[bc] & be) != 0L;
            case 32:
                return (jjbitVec55[bc] & be) != 0L;
            case 33:
                return (jjbitVec26[bc] & be) != 0L;
            case 48:
                return (jjbitVec56[bc] & be) != 0L;
            case 49:
                return (jjbitVec28[bc] & be) != 0L;
            case 77:
                return (jjbitVec29[bc] & be) != 0L;
            case 159:
                return (jjbitVec30[bc] & be) != 0L;
            case 164:
                return (jjbitVec31[bc] & be) != 0L;
            case 215:
                return (jjbitVec32[bc] & be) != 0L;
            case 250:
                return (jjbitVec33[bc] & be) != 0L;
            case 251:
                return (jjbitVec57[bc] & be) != 0L;
            case 253:
                return (jjbitVec35[bc] & be) != 0L;
            case 254:
                return (jjbitVec58[bc] & be) != 0L;
            case 255:
                return (jjbitVec59[bc] & be) != 0L;
            default:
                return (jjbitVec0[bb] & bd) != 0L;
        }
    }

    public int jjMoveStringLiteralDfa30_0(long ba) {
        long bb = 108086391056891904L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(28, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(29, 0L, bb);
            return 30;
        }

        switch (this.curChar) {
            case 'e':
                return this.jjMoveStringLiteralDfa31_0(bb, 36028797018963968L);
            case 'm':
                return this.jjMoveStringLiteralDfa31_0(bb, 72057594037927936L);
            default:
                return this.jjStartNfa_0(29, 0L, bb);
        }
    }

    public int jjMoveStringLiteralDfa17_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(15, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(16, 0L, bc);
            return 17;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa18_0(bc, 8815420375040L);
            case 'b':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'q':
            case 'u':
            default:
                return this.jjStartNfa_0(16, 0L, bc);
            case 'c':
                return this.jjMoveStringLiteralDfa18_0(bc, 108228232351842304L);
            case 'd':
                return this.jjMoveStringLiteralDfa18_0(bc, 567347999932416L);
            case 'e':
                return this.jjMoveStringLiteralDfa18_0(bc, 299549809704960L);
            case 'g':
                return this.jjMoveStringLiteralDfa18_0(bc, 4503599627370496L);
            case 'i':
                return this.jjMoveStringLiteralDfa18_0(bc, 549755813888L);
            case 'm':
                return this.jjMoveStringLiteralDfa18_0(bc, 35184372088832L);
            case 'n':
                return this.jjMoveStringLiteralDfa18_0(bc, 2199023255552L);
            case 'o':
                return this.jjMoveStringLiteralDfa18_0(bc, 18014433137655808L);
            case 'p':
                return this.jjMoveStringLiteralDfa18_0(bc, 70368744177664L);
            case 'r':
                return this.jjMoveStringLiteralDfa18_0(bc, 11258999068426240L);
            case 's':
                if ((bc & 67108864L) != 0L) {
                    return this.jjStopAtPos(17, 90);
                }

                return this.jjMoveStringLiteralDfa18_0(bc, 1125899906842624L);
            case 't':
                return this.jjMoveStringLiteralDfa18_0(bc, 134217728L);
            case 'v':
                return this.jjMoveStringLiteralDfa18_0(bc, 8589934592L);
        }
    }

    public int jjMoveStringLiteralDfa19_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(17, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(18, 0L, bc);
            return 19;
        }

        switch (this.curChar) {
            case 'a':
                if ((bc & 2147483648L) != 0L) {
                    return this.jjStopAtPos(19, 95);
                }

                return this.jjMoveStringLiteralDfa20_0(bc, 142764712919040L);
            case 'b':
                return this.jjMoveStringLiteralDfa20_0(bc, 286010462175232L);
            case 'c':
                return this.jjMoveStringLiteralDfa20_0(bc, 562949953421312L);
            case 'd':
                return this.jjMoveStringLiteralDfa20_0(bc, 19140298416324608L);
            case 'e':
                return this.jjMoveStringLiteralDfa20_0(bc, 37400575213568L);
            case 'i':
                return this.jjMoveStringLiteralDfa20_0(bc, 108086391056891904L);
            case 'l':
                return this.jjMoveStringLiteralDfa20_0(bc, 8589934592L);
            case 'o':
                return this.jjMoveStringLiteralDfa20_0(bc, 17592186044416L);
            case 'r':
                return this.jjMoveStringLiteralDfa20_0(bc, 6825768185233408L);
            case 's':
                if ((bc & 4294967296L) != 0L) {
                    return this.jjStopAtPos(19, 96);
                }
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'n':
            case 'p':
            case 'q':
            default:
                return this.jjStartNfa_0(18, 0L, bc);
            case 't':
                return this.jjMoveStringLiteralDfa20_0(bc, 9015995347763200L);
        }
    }

    public int jjStartNfaWithStates_2() {
        this.jjmatchedKind = 122;
        this.jjmatchedPos = 0;

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            return 0 + 1;
        }

        return this.jjMoveNfa_2(2, 0 + 1);
    }

    public static final boolean jjCanMove_2(int ba, int bb, int bc, long bd, long be) {
        switch (ba) {
            case 0:
                return (jjbitVec60[bc] & be) != 0L;
            default:
                return (jjbitVec19[bb] & bd) != 0L;
        }
    }

    public int jjMoveStringLiteralDfa2_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(10, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(11, 0L, bc);
            return 12;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa3_0(bc, 5176775640350720L);
            case 'c':
                return this.jjMoveStringLiteralDfa3_0(bc, 17660905521152L);
            case 'd':
                return this.jjMoveStringLiteralDfa3_0(bc, 140771848093696L);
            case 'e':
                if ((bc & 512L) != 0L) {
                    return this.jjStopAtPos(12, 73);
                }

                return this.jjMoveStringLiteralDfa3_0(bc, 10141900254216192L);
            case 'f':
                return this.jjMoveStringLiteralDfa3_0(bc, 8192L);
            case 'g':
                if ((bc & 32L) != 0L) {
                    return this.jjStopAtPos(12, 69);
                } else if ((bc & 128L) != 0L) {
                    return this.jjStopAtPos(12, 71);
                }
            case 'b':
            case 'h':
            case 'j':
            case 'l':
            case 'o':
            case 'q':
            case 'v':
            default:
                return this.jjStartNfa_0(11, 0L, bc);
            case 'i':
                return this.jjMoveStringLiteralDfa3_0(bc, 2252899325313024L);
            case 'k':
                return this.jjMoveStringLiteralDfa3_0(bc, 2199023255552L);
            case 'm':
                return this.jjMoveStringLiteralDfa3_0(bc, 569087361024L);
            case 'n':
                if ((bc & 64L) != 0L) {
                    return this.jjStopAtPos(12, 70);
                }

                return this.jjMoveStringLiteralDfa3_0(bc, 1082267648L);
            case 'p':
                return this.jjMoveStringLiteralDfa3_0(bc, 108086391056891904L);
            case 'r':
                return this.jjMoveStringLiteralDfa3_0(bc, 8590475264L);
            case 's':
                if ((bc & 256L) != 0L) {
                    return this.jjStopAtPos(12, 72);
                } else {
                    if ((bc & 1024L) != 0L) {
                        return this.jjStopAtPos(12, 74);
                    }

                    return this.jjMoveStringLiteralDfa3_0(bc, 18014398510530560L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa3_0(bc, 67469312L);
            case 'u':
                return this.jjMoveStringLiteralDfa3_0(bc, 268435456L);
            case 'w':
                return this.jjMoveStringLiteralDfa3_0(bc, 281612415664128L);
        }
    }

    public int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '"':
                return this.jjStartNfaWithStates_2();
            default:
                return this.jjMoveNfa_2(0, 0);
        }
    }

    public int jjMoveStringLiteralDfa29_0(long ba) {
        long bb = 108086391056891904L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(27, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(28, 0L, bb);
            return 29;
        }

        switch (this.curChar) {
            case 's':
                return this.jjMoveStringLiteralDfa30_0(bb);
            default:
                return this.jjStartNfa_0(28, 0L, bb);
        }
    }

    public int jjMoveStringLiteralDfa4_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(2, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(3, be, bf);
            return 4;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa5_0(be, 72060067939090432L, bf, 8796143353856L);
            case 'b':
                return this.jjMoveStringLiteralDfa5_0(be, 35184372088832L, bf, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa5_0(be, 8796093022208L, bf, 4398047559680L);
            case 'd':
            case 'f':
            case 'm':
            case 'q':
            default:
                return this.jjStartNfa_0(3, be, bf);
            case 'e':
                return this.jjMoveStringLiteralDfa5_0(be, 1729663731886981120L, bf, 16384L);
            case 'g':
                return this.jjMoveStringLiteralDfa5_0(be, 1236950581248L, bf, 4503599627370496L);
            case 'h':
                return this.jjMoveStringLiteralDfa5_0(be, 18577348462903296L, bf, 16L);
            case 'i':
                return this.jjMoveStringLiteralDfa5_0(be, 25769803776L, bf, 1073745920L);
            case 'j':
                return this.jjMoveStringLiteralDfa5_0(be, 140737488355328L, bf, 0L);
            case 'k':
                return this.jjMoveStringLiteralDfa5_0(be, 0L, bf, 18014398509481984L);
            case 'l':
                if ((be & 4294967296L) != 0L) {
                    return this.jjStartNfaWithStates_0(4, 32, 13);
                }

                return this.jjMoveStringLiteralDfa5_0(be, 4611773979357609984L, bf, 32L);
            case 'n':
                return this.jjMoveStringLiteralDfa5_0(be, 549755813888L, bf, 268435589L);
            case 'o':
                return this.jjMoveStringLiteralDfa5_0(be, 0L, bf, 549755944960L);
            case 'p':
                if ((be & 536870912L) != 0L) {
                    return this.jjStopAtPos(4, 29);
                }

                ProGuardConfigTokenManager proGuardConfigTokenManager1;
                long bg;
                long bh;
                if ((be & 1073741824L) != 0L) {
                    this.jjmatchedKind = 30;
                    this.jjmatchedPos = 4;
                    proGuardConfigTokenManager1 = this;
                    bg = be;
                    bh = 2341871806232657920L;
                } else {
                    proGuardConfigTokenManager1 = this;
                    bg = be;
                    bh = 2341871806232657920L;
                }

                return proGuardConfigTokenManager1.jjMoveStringLiteralDfa5_0(bg, bh, bf, 2551093213593600L);
            case 'r':
                return this.jjMoveStringLiteralDfa5_0(be, 12384898975268864L, bf, 8589934664L);
            case 's':
                if ((be & 2147483648L) != 0L) {
                    return this.jjStartNfaWithStates_0(4, 31, 13);
                }

                return this.jjMoveStringLiteralDfa5_0(be, 4503599627370496L, bf, 562949953421312L);
            case 't':
                return this.jjMoveStringLiteralDfa5_0(be, 432350030993555456L, bf, 108229602446452226L);
            case 'u':
                return this.jjMoveStringLiteralDfa5_0(be, Long.MIN_VALUE, bf, 10239790444183808L);
            case 'v':
                return this.jjMoveStringLiteralDfa5_0(be, 34359738368L, bf, 0L);
            case 'w':
                return this.jjMoveStringLiteralDfa5_0(be, 0L, bf, 2361344L);
        }
    }

    public int jjMoveStringLiteralDfa31_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(29, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(30, 0L, bc);
            return 31;
        }

        switch (this.curChar) {
            case 'e':
                return this.jjMoveStringLiteralDfa32_0(bc);
            case 's':
                if ((bc & 36028797018963968L) != 0L) {
                    return this.jjStopAtPos(31, 119);
                }
            default:
                return this.jjStartNfa_0(30, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa32_0(long ba) {
        long bb = 72057594037927936L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(30, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(31, 0L, bb);
            return 32;
        }

        switch (this.curChar) {
            case 'm':
                return this.jjMoveStringLiteralDfa33_0(bb);
            default:
                return this.jjStartNfa_0(31, 0L, bb);
        }
    }

    public int jjMoveStringLiteralDfa21_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(19, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(20, 0L, bc);
            return 21;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa22_0(bc, 2199023255552L);
            case 'c':
                return this.jjMoveStringLiteralDfa22_0(bc, 20266473201074176L);
            case 'e':
                return this.jjMoveStringLiteralDfa22_0(bc, 1125968626319360L);
            case 'g':
                return this.jjMoveStringLiteralDfa22_0(bc, 4398046511104L);
            case 'i':
                return this.jjMoveStringLiteralDfa22_0(bc, 572295802257408L);
            case 'm':
                return this.jjMoveStringLiteralDfa22_0(bc, 70368744177664L);
            case 'n':
                return this.jjMoveStringLiteralDfa22_0(bc, 35184372088832L);
            case 'r':
                return this.jjMoveStringLiteralDfa22_0(bc, 117375202727297024L);
            case 's':
                return this.jjMoveStringLiteralDfa22_0(bc, 4645436627353600L);
            case 't':
                return this.jjMoveStringLiteralDfa22_0(bc, 17592186044416L);
            case 'y':
                if ((bc & 34359738368L) != 0L) {
                    return this.jjStopAtPos(21, 99);
                }
            case 'b':
            case 'd':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'o':
            case 'p':
            case 'q':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(20, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa28_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(26, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(27, 0L, bc);
            return 28;
        }

        switch (this.curChar) {
            case 's':
                if ((bc & 9007199254740992L) != 0L) {
                    return this.jjStopAtPos(28, 117);
                }

                return this.jjMoveStringLiteralDfa29_0(bc);
            case 'y':
                if ((bc & 18014398509481984L) != 0L) {
                    return this.jjStopAtPos(28, 118);
                }
            default:
                return this.jjStartNfa_0(27, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa3_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(11, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(12, 0L, bc);
            return 13;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa14_0(bc, 8388608L);
            case 'b':
                return this.jjMoveStringLiteralDfa14_0(bc, 17184063488L);
            case 'c':
                return this.jjMoveStringLiteralDfa14_0(bc, 24910535438893056L);
            case 'e':
                if ((bc & 32768L) != 0L) {
                    return this.jjStopAtPos(13, 79);
                }

                return this.jjMoveStringLiteralDfa14_0(bc, 17675937972224L);
            case 'f':
                return this.jjMoveStringLiteralDfa14_0(bc, 8796093022208L);
            case 'g':
                if ((bc & 2048L) != 0L) {
                    return this.jjStartNfaWithStates_0(13, 75, 13);
                }

                return this.jjMoveStringLiteralDfa14_0(bc, 274878038016L);
            case 'i':
                return this.jjMoveStringLiteralDfa14_0(bc, 281646777237504L);
            case 'o':
                return this.jjMoveStringLiteralDfa14_0(bc, 2748779069440L);
            case 'p':
                return this.jjMoveStringLiteralDfa14_0(bc, 71469363101696L);
            case 'r':
                return this.jjMoveStringLiteralDfa14_0(bc, 10133100033998848L);
            case 's':
                if ((bc & 4096L) != 0L) {
                    return this.jjStopAtPos(13, 76);
                }

                return this.jjMoveStringLiteralDfa14_0(bc, 35184388866048L);
            case 't':
                return this.jjMoveStringLiteralDfa14_0(bc, 567348136247296L);
            case 'u':
                return this.jjMoveStringLiteralDfa14_0(bc, 108086391056891904L);
            case 'y':
                if ((bc & 8192L) != 0L) {
                    return this.jjStopAtPos(13, 77);
                } else if ((bc & 16384L) != 0L) {
                    return this.jjStopAtPos(13, 78);
                }
            case 'd':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'q':
            case 'v':
            case 'w':
            case 'x':
            default:
                return this.jjStartNfa_0(12, 0L, bc);
        }
    }

    public int jjMoveStringLiteralDfa33_0(long ba) {
        long bb = 72057594037927936L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(31, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(32, 0L, bb);
            return 33;
        }

        switch (this.curChar) {
            case 'b':
                return this.jjMoveStringLiteralDfa34_0(bb);
            default:
                return this.jjStartNfa_0(32, 0L, bb);
        }
    }

    public int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '!':
                return this.jjStopAtPos(0, 16);
            case '"':
                return this.jjStopAtPos(0, 121);
            case '#':
                return this.jjStopAtPos(0, 6);
            case '%':
                return this.jjStartNfaWithStates_0(0, 18, 8);
            case '(':
                return this.jjStopAtPos(0, 20);
            case ')':
                return this.jjStopAtPos(0, 21);
            case '*':
                this.jjmatchedKind = 19;
                return this.jjMoveStringLiteralDfa1_0(83886080L, 0L);
            case ',':
                return this.jjStopAtPos(0, 12);
            case '-':
                return this.jjMoveStringLiteralDfa1_0(2848776355095511040L, 144114088561866735L);
            case '.':
                this.jjmatchedKind = 15;
                return this.jjMoveStringLiteralDfa1_0(134217728L, 0L);
            case ';':
                return this.jjStopAtPos(0, 13);
            case '<':
                return this.jjMoveStringLiteralDfa1_0(580610858942464L, 0L);
            case '?':
                return this.jjStartNfaWithStates_0(0, 17, 12);
            case '@':
                this.jjmatchedKind = 14;
                return this.jjMoveStringLiteralDfa1_0(576460752303423488L, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa1_0(1125899906842624L, 2361344L);
            case 'b':
                return this.jjMoveStringLiteralDfa1_0(137438953472L, 0L);
            case 'c':
                return this.jjMoveStringLiteralDfa1_0(2147483648L, 0L);
            case 'e':
                return this.jjMoveStringLiteralDfa1_0(550024249344L, 0L);
            case 'f':
                return this.jjMoveStringLiteralDfa1_0(4294967296L, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa1_0(-8061443332993187840L, 1099511627776L);
            case 'm':
                return this.jjMoveStringLiteralDfa1_0(4611686018427387904L, 0L);
            case 'n':
                return this.jjMoveStringLiteralDfa1_0(34359738368L, 0L);
            case 'p':
                return this.jjMoveStringLiteralDfa1_0(281758444552192L, 0L);
            case 's':
                return this.jjMoveStringLiteralDfa1_0(18023211782373376L, 16L);
            case 't':
                return this.jjMoveStringLiteralDfa1_0(4503599627370496L, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa1_0(4398046511104L, 0L);
            case '{':
                return this.jjStopAtPos(0, 22);
            case '}':
                return this.jjStopAtPos(0, 23);
            case '~':
                return this.jjStopAtPos(0, 11);
            case '\ufeff':
                return this.jjStartNfaWithStates_0(0, 10, 12);
            default:
                return this.jjMoveNfa_0(1, 0);
        }
    }

    public final int jjStopStringLiteralDfa_0(int ba, long bb, long bc) {
        switch (ba) {
            case 0:
                if ((bb & 218792960L) != 0L) {
                    return 12;
                } else {
                    ProGuardConfigTokenManager proGuardConfigTokenManager7;
                    if ((bb & -3425817718492758016L) == 0L) {
                        if ((bc & 1099513989136L) == 0L) {
                            if ((bb & 580610858942464L) != 0L) {
                                return 4;
                            }

                            if ((bb & 262144L) != 0L) {
                                return 8;
                            }

                            return -1;
                        }

                        proGuardConfigTokenManager7 = this;
                        byte bv = 125;
                    } else {
                        proGuardConfigTokenManager7 = this;
                        byte bw = 125;
                    }

                    proGuardConfigTokenManager7.jjmatchedKind = 125;
                    return 13;
                }
            case 1:
                if ((bb & 83886080L) != 0L) {
                    return 12;
                } else if ((bb & 134217728L) != 0L) {
                    byte bh;
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 126;
                        this.jjmatchedPos = 1;
                        bh = 12;
                    } else {
                        bh = 12;
                    }

                    return bh;
                } else {
                    int bf;
                    if ((bb & -3425817718492758016L) == 0L) {
                        if ((bc & 1099513989136L) == 0L) {
                            return -1;
                        }

                        bf = this.jjmatchedPos;
                    } else {
                        bf = this.jjmatchedPos;
                    }

                    byte bg;
                    if (bf != 1) {
                        this.jjmatchedKind = 125;
                        this.jjmatchedPos = 1;
                        bg = 13;
                    } else {
                        bg = 13;
                    }

                    return bg;
                }
            case 2:
                if ((bb & 201326592L) != 0L) {
                    return 12;
                } else {
                    ProGuardConfigTokenManager proGuardConfigTokenManager6;
                    if ((bb & -3425817718492758016L) == 0L) {
                        if ((bc & 1099513989136L) == 0L) {
                            return -1;
                        }

                        proGuardConfigTokenManager6 = this;
                        byte bt = 125;
                    } else {
                        proGuardConfigTokenManager6 = this;
                        byte bu = 125;
                    }

                    proGuardConfigTokenManager6.jjmatchedKind = 125;
                    this.jjmatchedPos = 2;
                    return 13;
                }
            case 3:
                ProGuardConfigTokenManager proGuardConfigTokenManager5;
                if ((bb & -3425817718761193472L) == 0L) {
                    if ((bc & 1099513989136L) == 0L) {
                        if ((bb & 268435456L) != 0L) {
                            return 13;
                        }

                        return -1;
                    }

                    proGuardConfigTokenManager5 = this;
                    byte br = 125;
                } else {
                    proGuardConfigTokenManager5 = this;
                    byte bs = 125;
                }

                proGuardConfigTokenManager5.jjmatchedKind = 125;
                this.jjmatchedPos = 3;
                return 13;
            case 4:
                int bd;
                if ((bb & -3425817725203644416L) == 0L) {
                    if ((bc & 1099513989136L) == 0L) {
                        if ((bb & 6442450944L) != 0L) {
                            return 13;
                        }

                        return -1;
                    }

                    bd = this.jjmatchedPos;
                } else {
                    bd = this.jjmatchedPos;
                }

                byte be;
                if (bd != 4) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 4;
                    be = 13;
                } else {
                    be = 13;
                }

                return be;
            case 5:
                ProGuardConfigTokenManager proGuardConfigTokenManager4;
                if ((bb & -3425817922772140032L) == 0L) {
                    if ((bc & 1099513989136L) == 0L) {
                        if ((bb & 197568495616L) != 0L) {
                            return 13;
                        }

                        return -1;
                    }

                    proGuardConfigTokenManager4 = this;
                    byte bp = 125;
                } else {
                    proGuardConfigTokenManager4 = this;
                    byte bq = 125;
                }

                proGuardConfigTokenManager4.jjmatchedKind = 125;
                this.jjmatchedPos = 5;
                return 13;
            case 6:
                if ((bb & 4611686018427387904L) != 0L) {
                    if (this.jjmatchedPos < 5) {
                        this.jjmatchedKind = 125;
                        this.jjmatchedPos = 5;
                    }

                    return -1;
                } else {
                    ProGuardConfigTokenManager proGuardConfigTokenManager3;
                    if ((bb & -8037504765833248768L) == 0L) {
                        if ((bc & 1099513989136L) == 0L) {
                            if ((bb & 824633720832L) != 0L) {
                                return 13;
                            }

                            return -1;
                        }

                        proGuardConfigTokenManager3 = this;
                        byte bn = 125;
                    } else {
                        proGuardConfigTokenManager3 = this;
                        byte bo = 125;
                    }

                    proGuardConfigTokenManager3.jjmatchedKind = 125;
                    this.jjmatchedPos = 6;
                    return 13;
                }
            case 7:
                ProGuardConfigTokenManager proGuardConfigTokenManager2;
                if ((bb & -8038643859879624704L) == 0L) {
                    if ((bc & 1099513989136L) == 0L) {
                        if ((bb & 4611686018427387904L) != 0L) {
                            if (this.jjmatchedPos < 5) {
                                this.jjmatchedKind = 125;
                                this.jjmatchedPos = 5;
                            }

                            return -1;
                        }

                        if ((bb & 1139094046375936L) != 0L) {
                            return 13;
                        }

                        return -1;
                    }

                    proGuardConfigTokenManager2 = this;
                    byte bl = 125;
                } else {
                    proGuardConfigTokenManager2 = this;
                    byte bm = 125;
                }

                proGuardConfigTokenManager2.jjmatchedKind = 125;
                this.jjmatchedPos = 7;
                return 13;
            case 8:
                ProGuardConfigTokenManager proGuardConfigTokenManager1;
                if ((bb & -8070450532247928832L) == 0L) {
                    if ((bc & 1099513989136L) == 0L) {
                        if ((bb & 4611686018427387904L) != 0L) {
                            if (this.jjmatchedPos < 5) {
                                this.jjmatchedKind = 125;
                                this.jjmatchedPos = 5;
                            }

                            return -1;
                        }

                        if ((bb & 31806672368304128L) != 0L) {
                            return 13;
                        }

                        return -1;
                    }

                    proGuardConfigTokenManager1 = this;
                    byte bj = 125;
                } else {
                    proGuardConfigTokenManager1 = this;
                    byte bk = 125;
                }

                proGuardConfigTokenManager1.jjmatchedKind = 125;
                this.jjmatchedPos = 8;
                return 13;
            case 9:
                if ((bb & 4611686018427387904L) != 0L) {
                    if (this.jjmatchedPos < 5) {
                        this.jjmatchedKind = 125;
                        this.jjmatchedPos = 5;
                    }

                    return -1;
                } else {
                    ProGuardConfigTokenManager proGuardConfigTokenManager8;
                    if ((bb & Long.MIN_VALUE) == 0L) {
                        if ((bc & 1099513989136L) == 0L) {
                            if ((bb & 1152921504606846976L) != 0L) {
                                return 13;
                            }

                            return -1;
                        }

                        proGuardConfigTokenManager8 = this;
                        byte bx = 125;
                    } else {
                        proGuardConfigTokenManager8 = this;
                        byte bi = 125;
                    }

                    proGuardConfigTokenManager8.jjmatchedKind = 125;
                    this.jjmatchedPos = 9;
                    return 13;
                }
            case 10:
                if ((bb & 4611686018427387904L) != 0L) {
                    if (this.jjmatchedPos < 5) {
                        this.jjmatchedKind = 125;
                        this.jjmatchedPos = 5;
                    }

                    return -1;
                } else if ((bc & 1099513989136L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 10;
                    return 13;
                } else {
                    if ((bb & Long.MIN_VALUE) != 0L) {
                        return 13;
                    }

                    return -1;
                }
            case 11:
                if ((bc & 1099513989120L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 11;
                    return 13;
                } else {
                    if ((bc & 16L) != 0L) {
                        return 13;
                    }

                    return -1;
                }
            case 12:
                if ((bc & 1099513989120L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 12;
                    return 13;
                }

                return -1;
            case 13:
                if ((bc & 1099513987072L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 13;
                    return 13;
                } else {
                    if ((bc & 2048L) != 0L) {
                        return 13;
                    }

                    return -1;
                }
            case 14:
                if ((bc & 1099513987072L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 14;
                    return 13;
                }

                return -1;
            case 15:
                if ((bc & 1099513724928L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 15;
                    return 13;
                } else {
                    if ((bc & 262144L) != 0L) {
                        return 13;
                    }

                    return -1;
                }
            case 16:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 16;
                    return 13;
                } else {
                    if ((bc & 2097152L) != 0L) {
                        return 13;
                    }

                    return -1;
                }
            case 17:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 17;
                    return 13;
                }

                return -1;
            case 18:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 18;
                    return 13;
                }

                return -1;
            case 19:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 19;
                    return 13;
                }

                return -1;
            case 20:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 20;
                    return 13;
                }

                return -1;
            case 21:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 21;
                    return 13;
                }

                return -1;
            case 22:
                if ((bc & 1099511627776L) != 0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 22;
                    return 13;
                }

                return -1;
            case 23:
                if ((bc & 1099511627776L) != 0L) {
                    return 13;
                }

                return -1;
            default:
                return -1;
        }
    }

    public int jjMoveNfa_2(int ba, int bb) {
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
                                if (bf > 123) {
                                    bf = 123;
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
                            if (this.curChar == '"' && bf > 123) {
                                bf = 123;
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
                            bf = 123;
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
                            if (jjCanMove_2(bg, bh, bj, bi, bk) && bf > 123) {
                                bf = 123;
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

    public int jjStopAtPos(int jjmatchedPos, int jjmatchedKind) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        return jjmatchedPos + 1;
    }

    public int jjMoveStringLiteralDfa10_0(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(8, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(9, be, bf);
            return 10;
        }

        switch (this.curChar) {
            case 'b':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 2251799813750784L);
            case 'c':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 36558778695680L);
            case 'd':
            case 'h':
            case 'j':
            case 'l':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            default:
                return this.jjStartNfa_0(9, be, bf);
            case 'e':
                if ((be & Long.MIN_VALUE) != 0L) {
                    return this.jjStartNfaWithStates_0(10, 63, 13);
                } else {
                    if ((bf & 4L) != 0L) {
                        return this.jjStopAtPos(10, 66);
                    }

                    return this.jjMoveStringLiteralDfa11_0(be, bf, 281612953584656L);
                }
            case 'f':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 18014398509481984L);
            case 'g':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 8598323200L);
            case 'i':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 7786730208L);
            case 'k':
                if ((bf & 2L) != 0L) {
                    return this.jjStopAtPos(10, 65);
                }

                return this.jjMoveStringLiteralDfa11_0(be, bf, 33556480L);
            case 'm':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 17184063488L);
            case 'n':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 134348800L);
            case 'o':
                if ((be & 4611686018427387904L) != 0L) {
                    return this.jjStopAtPos(10, 62);
                }

                return this.jjMoveStringLiteralDfa11_0(be, bf, 108086425416630272L);
            case 'r':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 4512395720400904L);
            case 's':
                if ((bf & 1L) != 0L) {
                    return this.jjStopAtPos(10, 64);
                }

                return this.jjMoveStringLiteralDfa11_0(be, bf, 636067543777280L);
            case 't':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 540672L);
            case 'u':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 22058952032512L);
            case 'x':
                return this.jjMoveStringLiteralDfa11_0(be, bf, 10273836649938944L);
        }
    }

    public int jjMoveStringLiteralDfa7_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(5, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(6, be, bf);
            return 7;
        }

        switch (this.curChar) {
            case '>':
                if ((be & 17592186044416L) != 0L) {
                    return this.jjStopAtPos(7, 44);
                }
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
            case 'u':
            case 'x':
            default:
                break;
            case 'a':
                return this.jjMoveStringLiteralDfa8_0(be, 576460752303423488L, bf, 281672566177952L);
            case 'b':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 562949953421312L);
            case 'c':
                return this.jjMoveStringLiteralDfa8_0(be, -9214364837600034816L, bf, 549764202496L);
            case 'd':
                if ((be & 2251799813685248L) != 0L) {
                    return this.jjStopAtPos(7, 51);
                }

                return this.jjMoveStringLiteralDfa8_0(be, 36028797018963968L, bf, 1099511627840L);
            case 'e':
                if ((be & 4398046511104L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 42, 13);
                }

                if ((be & 35184372088832L) != 0L) {
                    return this.jjStopAtPos(7, 45);
                }

                if ((be & 70368744177664L) != 0L) {
                    return this.jjStopAtPos(7, 46);
                }

                return this.jjMoveStringLiteralDfa8_0(be, 281474976710656L, bf, 18172796903366657L);
            case 'f':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 4398046806016L);
            case 'g':
                return this.jjMoveStringLiteralDfa8_0(be, 72057594037927936L, bf, 0L);
            case 'i':
                return this.jjMoveStringLiteralDfa8_0(be, 4629700416936869888L, bf, 108086391056891904L);
            case 'l':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 67108864L);
            case 'm':
                return this.jjMoveStringLiteralDfa8_0(be, 2305843009213693952L, bf, 1024L);
            case 'n':
                return this.jjMoveStringLiteralDfa8_0(be, 1157425104234217472L, bf, 16959146519691280L);
            case 'o':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 2201439174656L);
            case 'p':
                if ((be & 8796093022208L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 43, 13);
                }
                break;
            case 'q':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 35184372088832L);
            case 'r':
                return this.jjMoveStringLiteralDfa8_0(be, 288230376151711744L, bf, 538462210L);
            case 's':
                if ((be & 140737488355328L) != 0L) {
                    return this.jjStopAtPos(7, 47);
                }

                return this.jjMoveStringLiteralDfa8_0(be, 562949953421312L, bf, 8796093022212L);
            case 't':
                if ((be & 1125899906842624L) != 0L) {
                    return this.jjStartNfaWithStates_0(7, 50, 13);
                }

                return this.jjMoveStringLiteralDfa8_0(be, 144115188075855872L, bf, 136380928L);
            case 'v':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 256L);
            case 'w':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 131072L);
            case 'y':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 33554440L);
            case 'z':
                return this.jjMoveStringLiteralDfa8_0(be, 0L, bf, 1073745920L);
        }

        return this.jjStartNfa_0(6, be, bf);
    }

    public void TokenLexicalActions() {
        switch (this.jjmatchedKind) {
        }
    }

    public int jjMoveStringLiteralDfa14_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(12, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(13, 0L, bc);
            return 14;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa15_0(bc, 18155137474232320L);
            case 'b':
                return this.jjMoveStringLiteralDfa15_0(bc, 108086391056891904L);
            case 'c':
                return this.jjMoveStringLiteralDfa15_0(bc, 34359738368L);
            case 'd':
                return this.jjMoveStringLiteralDfa15_0(bc, 549755813888L);
            case 'e':
                return this.jjMoveStringLiteralDfa15_0(bc, 4503891689865216L);
            case 'f':
                return this.jjMoveStringLiteralDfa15_0(bc, 17665200488448L);
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'p':
            case 'q':
            case 'r':
            default:
                return this.jjStartNfa_0(13, 0L, bc);
            case 'i':
                return this.jjMoveStringLiteralDfa15_0(bc, 646512939892736L);
            case 'l':
                return this.jjMoveStringLiteralDfa15_0(bc, 2251799813685248L);
            case 'm':
                return this.jjMoveStringLiteralDfa15_0(bc, 8388608L);
            case 'n':
                return this.jjMoveStringLiteralDfa15_0(bc, 10133099699503104L);
            case 'o':
                return this.jjMoveStringLiteralDfa15_0(bc, 262144L);
            case 's':
                if ((bc & 65536L) != 0L) {
                    return this.jjStopAtPos(14, 80);
                } else {
                    if ((bc & 131072L) != 0L) {
                        return this.jjStopAtPos(14, 81);
                    }

                    return this.jjMoveStringLiteralDfa15_0(bc, 35192978800640L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa15_0(bc, 284913098031104L);
        }
    }

    public void jjCheckNAdd(int ba) {
        if (this.jjrounds[ba] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = ba;
            this.jjrounds[ba] = this.jjround;
        }
    }

    public ProGuardConfigToken jjFillToken() {
        String string1 = jjstrLiteralImages[this.jjmatchedKind];
        String string = string1 == null ? this.input_stream.GetImage() : string1;
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        int line = this.input_stream.getLine();
        int column = this.input_stream.getColumn();
        ProGuardConfigToken proGuardConfigToken = ProGuardConfigToken.newToken(this.jjmatchedKind);
        proGuardConfigToken.s = this.jjmatchedKind;
        proGuardConfigToken.M = string;
        proGuardConfigToken.c = endLine;
        proGuardConfigToken.R = line;
        proGuardConfigToken.F = endColumn;
        proGuardConfigToken.d = column;
        return proGuardConfigToken;
    }

    public int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1();
    }

    public int jjMoveStringLiteralDfa23_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(21, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(22, 0L, bc);
            return 23;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa24_0(bc, 2676211302006784L);
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'o':
            case 'p':
            case 'q':
            case 'w':
            case 'x':
            default:
                break;
            case 'f':
                return this.jjMoveStringLiteralDfa24_0(bc, 1125899906842624L);
            case 'i':
                return this.jjMoveStringLiteralDfa24_0(bc, 22522396183363584L);
            case 'm':
                return this.jjMoveStringLiteralDfa24_0(bc, 35184372088832L);
            case 'n':
                if ((bc & 549755813888L) != 0L) {
                    return this.jjStopAtPos(23, 103);
                }

                return this.jjMoveStringLiteralDfa24_0(bc, 580542139465728L);
            case 'r':
                return this.jjMoveStringLiteralDfa24_0(bc, 108086391056891904L);
            case 's':
                if ((bc & 1099511627776L) != 0L) {
                    return this.jjStartNfaWithStates_0(23, 104, 13);
                }
                break;
            case 't':
                return this.jjMoveStringLiteralDfa24_0(bc, 70368744177664L);
            case 'u':
                return this.jjMoveStringLiteralDfa24_0(bc, 8796093022208L);
            case 'v':
                return this.jjMoveStringLiteralDfa24_0(bc, 9007199254740992L);
            case 'y':
                if ((bc & 274877906944L) != 0L) {
                    return this.jjStopAtPos(23, 102);
                }
        }

        return this.jjStartNfa_0(22, 0L, bc);
    }

    public int jjMoveStringLiteralDfa25_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(23, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(24, 0L, bc);
            return 25;
        }

        switch (this.curChar) {
            case 'a':
                if ((bc & 2199023255552L) != 0L) {
                    return this.jjStopAtPos(25, 105);
                }
            case 'b':
            case 'd':
            case 'f':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            default:
                break;
            case 'c':
                return this.jjMoveStringLiteralDfa26_0(bc, 109212290963734528L);
            case 'e':
                if ((bc & 8796093022208L) != 0L) {
                    return this.jjStopAtPos(25, 107);
                }

                return this.jjMoveStringLiteralDfa26_0(bc, 4925812092436480L);
            case 'g':
                if ((bc & 4398046511104L) != 0L) {
                    return this.jjStopAtPos(25, 106);
                }
                break;
            case 'l':
                return this.jjMoveStringLiteralDfa26_0(bc, 9007199254740992L);
            case 'n':
                return this.jjMoveStringLiteralDfa26_0(bc, 18014398509481984L);
            case 'r':
                return this.jjMoveStringLiteralDfa26_0(bc, 633318697598976L);
            case 's':
                if ((bc & 17592186044416L) != 0L) {
                    return this.jjStopAtPos(25, 108);
                }

                if ((bc & 35184372088832L) != 0L) {
                    return this.jjStopAtPos(25, 109);
                }

                return this.jjMoveStringLiteralDfa26_0(bc, 2251799813685248L);
        }

        return this.jjStartNfa_0(24, 0L, bc);
    }

    public int jjMoveStringLiteralDfa22_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(20, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(21, 0L, bc);
            return 22;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa23_0(bc, 108121575428980736L);
            case 'b':
                return this.jjMoveStringLiteralDfa23_0(bc, 8796093022208L);
            case 'c':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'r':
            default:
                return this.jjStartNfa_0(21, 0L, bc);
            case 'd':
                return this.jjMoveStringLiteralDfa23_0(bc, 2199023255552L);
            case 'e':
                return this.jjMoveStringLiteralDfa23_0(bc, 89060441849856L);
            case 'f':
                return this.jjMoveStringLiteralDfa23_0(bc, 1125899906842624L);
            case 'g':
                return this.jjMoveStringLiteralDfa23_0(bc, 4398046511104L);
            case 'h':
                return this.jjMoveStringLiteralDfa23_0(bc, 274877906944L);
            case 'l':
                return this.jjMoveStringLiteralDfa23_0(bc, 2251799813685248L);
            case 'n':
                return this.jjMoveStringLiteralDfa23_0(bc, 9429411719806976L);
            case 'o':
                return this.jjMoveStringLiteralDfa23_0(bc, 563499709235200L);
            case 's':
                if ((bc & 68719476736L) != 0L) {
                    return this.jjStopAtPos(22, 100);
                } else {
                    if ((bc & 137438953472L) != 0L) {
                        return this.jjStopAtPos(22, 101);
                    }

                    return this.jjMoveStringLiteralDfa23_0(bc, 4503599627370496L);
                }
            case 't':
                return this.jjMoveStringLiteralDfa23_0(bc, 18014398509481984L);
        }
    }

    public int jjMoveStringLiteralDfa1_0(long ba, long bb) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(0, ba, bb);
            return 1;
        }

        switch (this.curChar) {
            case '*':
                ProGuardConfigTokenManager proGuardConfigTokenManager1;
                long bc;
                long bd;
                if ((ba & 16777216L) != 0L) {
                    this.jjmatchedKind = 24;
                    this.jjmatchedPos = 1;
                    proGuardConfigTokenManager1 = this;
                    bc = ba;
                    bd = 67108864L;
                } else {
                    proGuardConfigTokenManager1 = this;
                    bc = ba;
                    bd = 67108864L;
                }

                return proGuardConfigTokenManager1.jjMoveStringLiteralDfa2_0(bc, bd, bb, 0L);
            case '+':
            case ',':
            case '-':
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
            case 'e':
            case 'g':
            case 'h':
            case 'j':
            case 'q':
            default:
                return this.jjStartNfa_0(0, ba, bb);
            case '.':
                return this.jjMoveStringLiteralDfa2_0(ba, 134217728L, bb, 0L);
            case 'a':
                return this.jjMoveStringLiteralDfa2_0(ba, 2251834173423616L, bb, 10226083123167520L);
            case 'b':
                return this.jjMoveStringLiteralDfa2_0(ba, 1125899906842624L, bb, 16384L);
            case 'c':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 562949953421312L);
            case 'd':
                return this.jjMoveStringLiteralDfa2_0(ba, 432345564764438528L, bb, 108229327568545282L);
            case 'f':
                return this.jjMoveStringLiteralDfa2_0(ba, 17592186044416L, bb, 274878955520L);
            case 'i':
                return this.jjMoveStringLiteralDfa2_0(ba, 576533393118855168L, bb, 131072L);
            case 'k':
                return this.jjMoveStringLiteralDfa2_0(ba, 2341871807306399744L, bb, 281630279794688L);
            case 'l':
                return this.jjMoveStringLiteralDfa2_0(ba, 2147483648L, bb, 2361352L);
            case 'm':
                return this.jjMoveStringLiteralDfa2_0(ba, 1153484454560268288L, bb, 4503599627370560L);
            case 'n':
                return this.jjMoveStringLiteralDfa2_0(ba, -9214364837331599360L, bb, 1099511627776L);
            case 'o':
                return this.jjMoveStringLiteralDfa2_0(ba, 4611831153962254336L, bb, 44023418880L);
            case 'p':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 18014398777917573L);
            case 'r':
                return this.jjMoveStringLiteralDfa2_0(ba, 4785486920941568L, bb, 8796109799424L);
            case 's':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 2251799813685248L);
            case 't':
                return this.jjMoveStringLiteralDfa2_0(ba, 9912784519168L, bb, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa2_0(ba, 8589934592L, bb, 35184372088832L);
            case 'v':
                return this.jjMoveStringLiteralDfa2_0(ba, 35184372088832L, bb, 0L);
            case 'w':
                return this.jjMoveStringLiteralDfa2_0(ba, 0L, bb, 33554432L);
            case 'x':
                return this.jjMoveStringLiteralDfa2_0(ba, 549755813888L, bb, 0L);
            case 'y':
                return this.jjMoveStringLiteralDfa2_0(ba, 18014398509481984L, bb, 16L);
            case 'z':
                return this.jjMoveStringLiteralDfa2_0(ba, 72057594037927936L, bb, 0L);
        }
    }

    public int jjMoveStringLiteralDfa2_0(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return this.jjStartNfa_0(0, ba, bc);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(1, be, bf);
            return 2;
        }

        switch (this.curChar) {
            case '*':
                if ((be & 67108864L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 26, 12);
                }
            case '+':
            case ',':
            case '-':
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
            case 'm':
            case 'q':
            default:
                break;
            case '.':
                if ((be & 134217728L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 27, 12);
                }
                break;
            case 'a':
                return this.jjMoveStringLiteralDfa3_0(be, 4504718466351104L, bf, 18014398509498368L);
            case 'b':
                return this.jjMoveStringLiteralDfa3_0(be, 8589934592L, bf, 34359738368L);
            case 'c':
                return this.jjMoveStringLiteralDfa3_0(be, Long.MIN_VALUE, bf, 1099511627776L);
            case 'd':
                return this.jjMoveStringLiteralDfa3_0(be, 4611686018427387904L, bf, 22061166624768L);
            case 'e':
                return this.jjMoveStringLiteralDfa3_0(be, 2342469941631909888L, bf, 4794026016964608L);
            case 'f':
                if ((be & 33554432L) != 0L) {
                    return this.jjStopAtPos(2, 25);
                }
                break;
            case 'g':
                return this.jjMoveStringLiteralDfa3_0(be, 0L, bf, 131072L);
            case 'h':
                return this.jjMoveStringLiteralDfa3_0(be, 0L, bf, 33554432L);
            case 'i':
                return this.jjMoveStringLiteralDfa3_0(be, 72075598540832768L, bf, 72L);
            case 'k':
                return this.jjMoveStringLiteralDfa3_0(be, 0L, bf, 2251799813685248L);
            case 'l':
                return this.jjMoveStringLiteralDfa3_0(be, 4398046511104L, bf, 563774589503488L);
            case 'n':
                return this.jjMoveStringLiteralDfa3_0(be, 596799591408467968L, bf, 16L);
            case 'o':
                return this.jjMoveStringLiteralDfa3_0(be, 432627039204278272L, bf, 108229327569593858L);
            case 'p':
                return this.jjMoveStringLiteralDfa3_0(be, 1152921504606846976L, bf, 1073745952L);
            case 'r':
                return this.jjMoveStringLiteralDfa3_0(be, 8796093022208L, bf, 268435589L);
            case 's':
                return this.jjMoveStringLiteralDfa3_0(be, 1125899906842624L, bf, 10238656572817664L);
            case 't':
                return this.jjMoveStringLiteralDfa3_0(be, 9007783370293248L, bf, 0L);
            case 'u':
                return this.jjMoveStringLiteralDfa3_0(be, 140738293661696L, bf, 0L);
            case 'v':
                return this.jjMoveStringLiteralDfa3_0(be, 0L, bf, 8589934592L);
        }

        return this.jjStartNfa_0(1, be, bf);
    }

    public void jjCheckNAddTwoStates(int ba, int bb) {
        this.jjCheckNAdd(ba);
        this.jjCheckNAdd(bb);
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

    public int jjMoveStringLiteralDfa26_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(24, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(25, 0L, bc);
            return 26;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa27_0(bc, 18014398509481984L);
            case 'b':
            case 'c':
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 'v':
            case 'w':
            case 'x':
            default:
                break;
            case 'e':
                return this.jjMoveStringLiteralDfa27_0(bc, 2251799813685248L);
            case 'l':
                return this.jjMoveStringLiteralDfa27_0(bc, 112589990684262400L);
            case 's':
                if ((bc & 70368744177664L) != 0L) {
                    return this.jjStopAtPos(26, 110);
                }

                if ((bc & 140737488355328L) != 0L) {
                    return this.jjStopAtPos(26, 111);
                }

                if ((bc & 281474976710656L) != 0L) {
                    return this.jjStopAtPos(26, 112);
                }
                break;
            case 't':
                return this.jjMoveStringLiteralDfa27_0(bc, 1125899906842624L);
            case 'u':
                return this.jjMoveStringLiteralDfa27_0(bc, 9007199254740992L);
            case 'y':
                if ((bc & 562949953421312L) != 0L) {
                    return this.jjStopAtPos(26, 113);
                }
        }

        return this.jjStartNfa_0(25, 0L, bc);
    }

    public int jjMoveStringLiteralDfa16_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return this.jjStartNfa_0(14, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(15, 0L, bc);
            return 16;
        }

        switch (this.curChar) {
            case 'a':
                return this.jjMoveStringLiteralDfa17_0(bc, 4503599761588224L);
            case 'b':
                return this.jjMoveStringLiteralDfa17_0(bc, 2251799813685248L);
            case 'c':
            case 'h':
            case 'j':
            case 'k':
            case 'o':
            case 'p':
            case 'q':
            default:
                return this.jjStartNfa_0(15, 0L, bc);
            case 'd':
                return this.jjMoveStringLiteralDfa17_0(bc, 2147483648L);
            case 'e':
                return this.jjMoveStringLiteralDfa17_0(bc, 184722248433664L);
            case 'f':
                return this.jjMoveStringLiteralDfa17_0(bc, 549755813888L);
            case 'g':
                if ((bc & 33554432L) != 0L) {
                    return this.jjStopAtPos(16, 89);
                }

                return this.jjMoveStringLiteralDfa17_0(bc, 70368811286528L);
            case 'i':
                return this.jjMoveStringLiteralDfa17_0(bc, 126103306685644800L);
            case 'l':
                return this.jjMoveStringLiteralDfa17_0(bc, 10150760067104768L);
            case 'm':
                return this.jjMoveStringLiteralDfa17_0(bc, 281612952535040L);
            case 'n':
                if ((bc & 2097152L) != 0L) {
                    return this.jjStartNfaWithStates_0(16, 85, 13);
                }

                return this.jjMoveStringLiteralDfa17_0(bc, 567365179801600L);
            case 'r':
                return this.jjMoveStringLiteralDfa17_0(bc, 1099511627776L);
            case 's':
                if ((bc & 4194304L) != 0L) {
                    return this.jjStopAtPos(16, 86);
                } else if ((bc & 8388608L) != 0L) {
                    return this.jjStopAtPos(16, 87);
                } else {
                    return (bc & 16777216L) != 0L ? this.jjStopAtPos(16, 88) : this.jjMoveStringLiteralDfa17_0(bc, 1073741824L);
                }
        }
    }

    public int jjMoveStringLiteralDfa35_0(long ba) {
        long bb = 72057594037927936L;
        if ((bb = bb & ba) == 0L) {
            return this.jjStartNfa_0(33, 0L, ba);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            this.jjStopStringLiteralDfa_0(34, 0L, bb);
            return 35;
        }

        switch (this.curChar) {
            case 'r':
                return this.jjMoveStringLiteralDfa36_0(bb);
            default:
                return this.jjStartNfa_0(34, 0L, bb);
        }
    }
}
