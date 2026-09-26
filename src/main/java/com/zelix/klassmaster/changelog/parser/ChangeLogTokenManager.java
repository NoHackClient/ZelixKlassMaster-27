package com.zelix.klassmaster.changelog.parser;

import java.io.IOException;

public class ChangeLogTokenManager implements ChangeLogConstants {
    private static int[] jjrounds;
    public static int curLexState;
    private static StringBuilder image;
    private static int jjimageLen;
    public static int jjmatchedPos;
    public static int jjmatchedKind;
    public static long[] jjtoToken;
    public static int[] jjnewLexState;
    public static String[] jjstrLiteralImages;
    private static StringBuilder jjimage;
    public static long[] jjbitVec2;
    public static int jjround;
    public static long[] jjtoSpecial;
    private static int[] jjstateSet;
    public static int jjnewStateCnt;
    public static ChangeLogSimpleCharStream input_stream;
    public static char curChar;
    public static long[] jjbitVec0;
    public static long[] jjtoSkip;
    public static int defaultLexState;

    public static int moveStringLiteralDfa3(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(1, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(2, be, bf);
            return 3;
        }

        switch (curChar) {
            case 'a':
                return moveStringLiteralDfa4(be, 1407376494166016L, bf, 0L);
            case 'b':
                return moveStringLiteralDfa4(be, 1152921504606846976L, bf, 0L);
            case 'c':
                return moveStringLiteralDfa4(be, 2305847407260205056L, bf, 4L);
            case 'd':
                return moveStringLiteralDfa4(be, 137438953472L, bf, 0L);
            case 'e':
                if ((be & 134217728L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(3, 27, integer);
                } else {
                    if ((be & 268435456L) != 0L) {
                        Integer integer1 = 7;
                        return jjStartNfaWithStates_0(3, 28, integer1);
                    }

                    return moveStringLiteralDfa4(be, 9007199254740992L, bf, 3L);
                }
            case 'f':
            case 'g':
            case 'j':
            case 'p':
            case 'q':
            default:
                return jjStartNfa_0(2, be, bf);
            case 'h':
                return moveStringLiteralDfa4(be, 72057598332895232L, bf, 128L);
            case 'i':
                return moveStringLiteralDfa4(be, 36301063585792L, bf, 0L);
            case 'k':
                return moveStringLiteralDfa4(be, 576531121047601152L, bf, 0L);
            case 'l':
                return moveStringLiteralDfa4(be, 36037867989893120L, bf, 0L);
            case 'm':
                if ((be & 67108864L) != 0L) {
                    Integer integer2 = 7;
                    return jjStartNfaWithStates_0(3, 26, integer2);
                }

                return moveStringLiteralDfa4(be, 68719476736L, bf, 0L);
            case 'n':
                return moveStringLiteralDfa4(be, 18014398509481984L, bf, 24L);
            case 'o':
                return moveStringLiteralDfa4(be, 288230376151711744L, bf, 32L);
            case 'r':
                return moveStringLiteralDfa4(be, 144132780261900288L, bf, 0L);
            case 's':
                return moveStringLiteralDfa4(be, 36507222016L, bf, 0L);
            case 't':
                return moveStringLiteralDfa4(be, 7318899150290944L, bf, 0L);
            case 'u':
                return moveStringLiteralDfa4(be, -9223369837831520256L, bf, 64L);
            case 'v':
                return moveStringLiteralDfa4(be, 140746078289920L, bf, 0L);
            case 'w':
                return moveStringLiteralDfa4(be, 4611686018427387904L, bf, 0L);
        }
    }

    public static int jjMoveStringLiteralDfa4_0(long ba) {
        long bb = 128L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(23, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(24, 0L, bb);
            return 25;
        }

        switch (curChar) {
            case 's':
                return jjMoveStringLiteralDfa5_0(bb);
            default:
                return jjStartNfa_0(24, 0L, bb);
        }
    }

    public static int moveStringLiteralDfa15(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(13, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(14, 0L, bc);
            return 15;
        }

        switch (curChar) {
            case 'C':
                return moveStringLiteralDfa16(bc, 128L);
            case 'l':
                return moveStringLiteralDfa16(bc, 32L);
            case 'n':
                return moveStringLiteralDfa16(bc, 88L);
            default:
                return jjStartNfa_0(14, 0L, bc);
        }
    }

    public static int moveNfaStringState(int ba, int bb) {
        int bc = bb;
        int bd = 0;
        jjnewStateCnt = 3;
        int be = 1;
        jjstateSet[0] = ba;
        int bf = Integer.MAX_VALUE;

        while (true) {
            if (++jjround == Integer.MAX_VALUE) {
                ReInitRounds();
            }

            if (curChar < '@') {
                long bl = 1L << curChar;

                do {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 0:
                            if ((-17179882497L & bl) != 0L) {
                                if (bf > 74) {
                                    bf = 74;
                                }
                            } else if (curChar == '"') {
                                jjstateSet[jjnewStateCnt++] = 2;
                            }
                            break;
                        case 1:
                            if (curChar == '"') {
                                jjstateSet[jjnewStateCnt++] = 2;
                            }
                            break;
                        case 2:
                            if (curChar == '"' && bf > 74) {
                                bf = 74;
                            }
                    }
                } while (be != bd);
            } else if (curChar < 128) {
                do {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 0:
                            bf = 74;
                    }
                } while (be != bd);
            } else {
                int bg = curChar >> '\b';
                int bh = bg >> 6;
                long bi = 1L << (bg & 63);
                int bj = (curChar & 255) >> 6;
                long bk = 1L << (curChar & '?');

                do {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 0:
                            if (jjCanMove_0(bg, bh, bj, bi, bk) && bf > 74) {
                                bf = 74;
                            }
                    }
                } while (be != bd);
            }

            if (bf != Integer.MAX_VALUE) {
                jjmatchedKind = bf;
                jjmatchedPos = bc;
                bf = Integer.MAX_VALUE;
            }

            bc++;
            int bm = be = jjnewStateCnt;
            jjnewStateCnt = bd;
            if (bm == (bd = 3 - bd)) {
                return bc;
            }

            try {
                curChar = ChangeLogSimpleCharStream.readChar();
            } catch (IOException iOException) {
                return bc;
            }
        }
    }

    public static int jjMoveStringLiteralDfa5_0(long ba) {
        long bb = 128L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(24, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(25, 0L, bb);
            return 26;
        }

        switch (curChar) {
            case 'e':
                return jjMoveStringLiteralDfa7_0(bb);
            default:
                return jjStartNfa_0(25, 0L, bb);
        }
    }

    public static int moveStringLiteralDfa20(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(18, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(19, 0L, bc);
            return 20;
        }

        switch (curChar) {
            case 'e':
                return moveStringLiteralDfa21(bc, 128L);
            case 'l':
                return moveStringLiteralDfa21(bc, 64L);
            default:
                return jjStartNfa_0(19, 0L, bc);
        }
    }

    private static int jjMoveStringLiteralDfa0_0() {
        switch (curChar) {
            case '"':
                return jjStopAtPos(0, 72);
            case '(':
                return jjStopAtPos(0, 14);
            case ')':
                return jjStopAtPos(0, 17);
            case '*':
                return jjStopAtPos(0, 23);
            case ',':
                return jjStopAtPos(0, 20);
            case '.':
                return jjStopAtPos(0, 22);
            case '/':
                jjmatchedKind = 21;
                return moveStringLiteralDfa1(320L, 0L);
            case ':':
                jjmatchedKind = 15;
                return moveStringLiteralDfa1(65536L, 0L);
            case '<':
                return moveStringLiteralDfa1(35201551958016L, 0L);
            case '=':
                return moveStringLiteralDfa1(16777216L, 0L);
            case 'A':
                return moveStringLiteralDfa1(0L, 32L);
            case 'C':
                return moveStringLiteralDfa1(34359738368L, 0L);
            case 'D':
                return moveStringLiteralDfa1(536870912L, 0L);
            case 'F':
                return moveStringLiteralDfa1(4647714815446351872L, 0L);
            case 'L':
                return moveStringLiteralDfa1(0L, 2L);
            case 'M':
                return moveStringLiteralDfa1(-7998390739186745344L, 128L);
            case 'N':
                return moveStringLiteralDfa1(0L, 1L);
            case 'O':
                return moveStringLiteralDfa1(0L, 64L);
            case 'P':
                return moveStringLiteralDfa1(70368744177664L, 0L);
            case 'S':
                return moveStringLiteralDfa1(17592186044416L, 24L);
            case 'T':
                return moveStringLiteralDfa1(0L, 4L);
            case '[':
                return jjStopAtPos(0, 18);
            case ']':
                return jjStopAtPos(0, 19);
            case 'a':
                return moveStringLiteralDfa1(432908514214543360L, 0L);
            case 'b':
                return moveStringLiteralDfa1(137438953472L, 0L);
            case 'e':
                return moveStringLiteralDfa1(294669183352832L, 0L);
            case 'f':
                return moveStringLiteralDfa1(3221225472L, 0L);
            case 'h':
                return moveStringLiteralDfa1(8589934592L, 0L);
            case 'i':
                return moveStringLiteralDfa1(9007199254740992L, 0L);
            case 'l':
                return moveStringLiteralDfa1(4294967296L, 0L);
            case 'n':
                return moveStringLiteralDfa1(1168365322240L, 0L);
            case 'p':
                return moveStringLiteralDfa1(578853564483371008L, 0L);
            case 's':
                return moveStringLiteralDfa1(2310347158596878336L, 0L);
            case 't':
                return moveStringLiteralDfa1(18014398777917440L, 0L);
            case 'v':
                return moveStringLiteralDfa1(1125899906842624L, 0L);
            case '\ufeff':
                Integer integer = 6;
                return jjStartNfaWithStates_0(0, 13, integer);
            default:
                return moveNfaDefaultState(3, 0);
        }
    }

    private static int jjStopAtPos(int ba, int bb) {
        jjmatchedKind = bb;
        jjmatchedPos = ba;
        return ba + 1;
    }

    public static int moveStringLiteralDfa7(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(5, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(6, be, bf);
            return 7;
        }

        byte bn;
        switch (curChar) {
            case '-':
                return moveStringLiteralDfa8(be, 576460752303423488L, bf, 0L);
            case ':':
                if ((be & 70368744177664L) != 0L) {
                    return jjStopAtPos(7, 46);
                }

                long bl = 76733928428305L;
                bn = 6;
                break;
            case '>':
                if ((be & 35184372088832L) != 0L) {
                    return jjStopAtPos(7, 45);
                }

                long bk = 76733928428305L;
                bn = 6;
                break;
            case 'C':
                return moveStringLiteralDfa8(be, 4611686018427387904L, bf, 1L);
            case 'O':
                return moveStringLiteralDfa8(be, 72057594037927936L, bf, 0L);
            case 'a':
                return moveStringLiteralDfa8(be, 0L, bf, 128L);
            case 'b':
                return moveStringLiteralDfa8(be, 0L, bf, 2L);
            case 'c':
                return moveStringLiteralDfa8(be, 9007199254740992L, bf, 4L);
            case 'd':
                if ((be & 281474976710656L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(7, 48, integer);
                }

                long bj = 76733928428305L;
                bn = 6;
                break;
            case 'e':
                if ((be & 1125899906842624L) != 0L) {
                    Integer integer1 = 7;
                    return jjStartNfaWithStates_0(7, 50, integer1);
                }

                return moveStringLiteralDfa8(be, 2251799813685248L, bf, 0L);
            case 'f':
                return moveStringLiteralDfa8(be, 36028797018963968L, bf, 0L);
            case 'i':
                return moveStringLiteralDfa8(be, 436849163854938112L, bf, 0L);
            case 'l':
                return moveStringLiteralDfa8(be, 1152921504606846976L, bf, 32L);
            case 'n':
                return moveStringLiteralDfa8(be, 2323857407723175936L, bf, 0L);
            case 'r':
                return moveStringLiteralDfa8(be, 0L, bf, 24L);
            case 't':
                if ((be & 562949953421312L) != 0L) {
                    Integer integer2 = 7;
                    return jjStartNfaWithStates_0(7, 49, integer2);
                }

                return moveStringLiteralDfa8(be, Long.MIN_VALUE, bf, 64L);
            default:
                long bm = 76733928428305L;
                bn = 6;
        }

        long bg = bf;
        long bh = be;
        byte bi = bn;
        return jjStartNfa_0(bi, bh, bg);
    }

    public ChangeLogTokenManager(ChangeLogSimpleCharStream changeLogSimpleCharStream) {
        if (input_stream != null) {
            throw new ChangeLogTokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", 1);
        }

        input_stream = changeLogSimpleCharStream;
    }

    private static int moveStringLiteralDfa2(long ba, long bb, long bc, long bd) {
        if (((bb = bb & ba) | (bd = bd & bc)) == 0L) {
            return jjStartNfa_0(0, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(1, bb, bd);
            return 2;
        }

        switch (curChar) {
            case 'a':
                return moveStringLiteralDfa3(bb, 18014991214968832L, bd, 4L);
            case 'b':
                return moveStringLiteralDfa3(bb, 274877906944L, bd, 0L);
            case 'c':
                return moveStringLiteralDfa3(bb, 576539917140623360L, bd, 0L);
            case 'd':
                if ((bb & 33554432L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(2, 25, integer);
                }

                return moveStringLiteralDfa3(bb, 2199023255552L, bd, 0L);
            case 'e':
                return moveStringLiteralDfa3(bb, 36033195065475072L, bd, 0L);
            case 'f':
                return moveStringLiteralDfa3(bb, 0L, bd, 64L);
            case 'g':
                return moveStringLiteralDfa3(bb, 144115192370823168L, bd, 24L);
            case 'h':
                return moveStringLiteralDfa3(bb, 281474976710656L, bd, 0L);
            case 'i':
                return moveStringLiteralDfa3(bb, 140874927308800L, bd, 0L);
            case 'j':
            case 'k':
            case 'p':
            case 'q':
            default:
                return jjStartNfa_0(1, bb, bd);
            case 'l':
                return moveStringLiteralDfa3(bb, 1161086426415104L, bd, 0L);
            case 'm':
                return moveStringLiteralDfa3(bb, 1152921504606846976L, bd, 1L);
            case 'n':
                return moveStringLiteralDfa3(bb, -6624795033474170880L, bd, 2L);
            case 'o':
                return moveStringLiteralDfa3(bb, 2251799813685248L, bd, 0L);
            case 'r':
                return moveStringLiteralDfa3(bb, 4611686087146864640L, bd, 0L);
            case 's':
                return moveStringLiteralDfa3(bb, 562949953421312L, bd, 0L);
            case 't':
                return moveStringLiteralDfa3(bb, 81065893341167616L, bd, 160L);
            case 'u':
                return moveStringLiteralDfa3(bb, 17592521588736L, bd, 0L);
        }
    }

    public static void MoreLexicalActions() {
        jjimageLen = jjimageLen + jjmatchedPos + 1;
        switch (jjmatchedKind) {
            case 7:
                image.append(ChangeLogSimpleCharStream.GetSuffix(jjimageLen));
                jjimageLen = 0;
                ChangeLogSimpleCharStream.backup(1);
        }
    }

    public static void ReInitRounds() {
        jjround = -2147483647;
        int ba = 7;

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                return;
            }

            jjrounds[ba] = Integer.MIN_VALUE;
        }
    }

    public static int jjMoveStringLiteralDfa2_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(16, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(17, 0L, bc);
            return 18;
        }

        switch (curChar) {
            case 'd':
                if ((bc & 8L) != 0L) {
                    jjmatchedKind = 67;
                    jjmatchedPos = 18;
                }

                return jjMoveStringLiteralDfa3_0(bc, 16L);
            case 'n':
                return jjMoveStringLiteralDfa3_0(bc, 128L);
            case 's':
                return jjMoveStringLiteralDfa3_0(bc, 96L);
            default:
                return jjStartNfa_0(17, 0L, bc);
        }
    }

    public static int jjMoveStringLiteralDfa0_2() {
        switch (curChar) {
            case '*':
                return jjMoveStringLiteralDfa1_2();
            default:
                return 1;
        }
    }

    public static int jjMoveStringLiteralDfa0_4() {
        switch (curChar) {
            case '"':
                return jjStartNfaWithStates_4();
            default:
                return moveNfaStringState(0, 0);
        }
    }

    public static int moveStringLiteralDfa21(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(19, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(20, 0L, bc);
            return 21;
        }

        switch (curChar) {
            case 'C':
                return moveStringLiteralDfa22(bc, 128L);
            case 'a':
                return moveStringLiteralDfa22(bc, 64L);
            default:
                return jjStartNfa_0(20, 0L, bc);
        }
    }

    private static final int jjStopStringLiteralDfa_0(int ba, long bb, long bc) {
        switch (ba) {
            case 0:
                if ((bb & 2097472L) != 0L) {
                    return 2;
                } else if ((bb & 8192L) != 0L) {
                    return 6;
                } else {
                    byte bo;
                    if ((bb & -35201585512448L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bo = 76;
                    } else {
                        bo = 76;
                    }

                    jjmatchedKind = bo;
                    return 7;
                }
            case 1:
                if ((bb & 256L) != 0L) {
                    return 0;
                } else {
                    byte bn;
                    if ((bb & -35201585512448L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bn = 76;
                    } else {
                        bn = 76;
                    }

                    jjmatchedKind = bn;
                    jjmatchedPos = 1;
                    return 7;
                }
            case 2:
                if ((bb & 33554432L) != 0L) {
                    return 7;
                } else {
                    byte bm;
                    if ((bb & -35201619066880L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bm = 76;
                    } else {
                        bm = 76;
                    }

                    jjmatchedKind = bm;
                    jjmatchedPos = 2;
                    return 7;
                }
            case 3:
                if ((bb & 469762048L) != 0L) {
                    return 7;
                } else {
                    byte bl;
                    if ((bb & -35202088828928L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bl = 76;
                    } else {
                        bl = 76;
                    }

                    jjmatchedKind = bl;
                    jjmatchedPos = 3;
                    return 7;
                }
            case 4:
                if ((bb & 16106127360L) != 0L) {
                    return 7;
                } else {
                    byte bk;
                    if ((bb & -35218731827200L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bk = 76;
                    } else {
                        bk = 76;
                    }

                    jjmatchedKind = bk;
                    jjmatchedPos = 4;
                    return 7;
                }
            case 5:
                if ((bb & 2130303778816L) != 0L) {
                    return 7;
                } else {
                    byte bj;
                    if ((bb & -37383395344384L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bj = 76;
                    } else {
                        bj = 76;
                    }

                    jjmatchedKind = bj;
                    jjmatchedPos = 5;
                    return 7;
                }
            case 6:
                if ((bb & 153931627888640L) != 0L) {
                    return 7;
                } else {
                    byte bi;
                    if ((bb & -211106232532992L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bi = 76;
                    } else {
                        bi = 76;
                    }

                    jjmatchedKind = bi;
                    jjmatchedPos = 6;
                    return 7;
                }
            case 7:
                if ((bb & 1970324836974592L) != 0L) {
                    return 7;
                } else if ((bb & 576460752303423488L) != 0L) {
                    jjmatchedKind = 77;
                    jjmatchedPos = 7;
                    return 6;
                } else {
                    byte bh;
                    if ((bb & -578712552117108736L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            return -1;
                        }

                        bh = 76;
                    } else {
                        bh = 76;
                    }

                    jjmatchedKind = bh;
                    jjmatchedPos = 7;
                    return 7;
                }
            case 8:
                if ((bb & 33776997205278720L) != 0L) {
                    return 7;
                } else {
                    byte bg;
                    if ((bb & -648518346341351424L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            if ((bb & 576460752303423488L) != 0L) {
                                jjmatchedKind = 77;
                                jjmatchedPos = 8;
                                return 6;
                            }

                            return -1;
                        }

                        bg = 76;
                    } else {
                        bg = 76;
                    }

                    jjmatchedKind = bg;
                    jjmatchedPos = 8;
                    return 7;
                }
            case 9:
                if ((bb & 432345564227567616L) != 0L) {
                    return 7;
                } else {
                    byte bf;
                    if ((bb & -1152921504606846976L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            if ((bb & 576460752303423488L) != 0L) {
                                jjmatchedKind = 77;
                                jjmatchedPos = 9;
                                return 6;
                            }

                            return -1;
                        }

                        bf = 76;
                    } else {
                        bf = 76;
                    }

                    jjmatchedKind = bf;
                    jjmatchedPos = 9;
                    return 7;
                }
            case 10:
                byte be;
                if ((bb & -1152921504606846976L) == 0L) {
                    if ((bc & 255L) == 0L) {
                        if ((bb & 576460752303423488L) != 0L) {
                            jjmatchedKind = 77;
                            jjmatchedPos = 10;
                            return 6;
                        }

                        return -1;
                    }

                    be = 76;
                } else {
                    be = 76;
                }

                jjmatchedKind = be;
                jjmatchedPos = 10;
                return 7;
            case 11:
                if ((bb & 2305843009213693952L) != 0L) {
                    return 7;
                } else {
                    byte bd;
                    if ((bb & -4611686018427387904L) == 0L) {
                        if ((bc & 255L) == 0L) {
                            if ((bb & 576460752303423488L) != 0L) {
                                return 6;
                            }

                            return -1;
                        }

                        bd = 76;
                    } else {
                        bd = 76;
                    }

                    jjmatchedKind = bd;
                    jjmatchedPos = 11;
                    return 7;
                }
            case 12:
                if ((bc & 255L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 12;
                    return 7;
                }

                return -1;
            case 13:
                if ((bc & 1L) != 0L) {
                    return 7;
                } else {
                    if ((bc & 252L) != 0L) {
                        jjmatchedKind = 76;
                        jjmatchedPos = 13;
                        return 7;
                    }

                    return -1;
                }
            case 14:
                if ((bc & 248L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 14;
                    return 7;
                }

                return -1;
            case 15:
                if ((bc & 248L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 15;
                    return 7;
                }

                return -1;
            case 16:
                if ((bc & 248L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 16;
                    return 7;
                }

                return -1;
            case 17:
                if ((bc & 248L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 17;
                    return 7;
                }

                return -1;
            case 18:
                if ((bc & 24L) != 0L) {
                    return 7;
                } else {
                    if ((bc & 224L) != 0L) {
                        byte bp;
                        if (jjmatchedPos != 18) {
                            jjmatchedKind = 76;
                            jjmatchedPos = 18;
                            bp = 7;
                        } else {
                            bp = 7;
                        }

                        return bp;
                    }

                    return -1;
                }
            case 19:
                if ((bc & 192L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 19;
                    return 7;
                }

                return -1;
            case 20:
                if ((bc & 192L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 20;
                    return 7;
                }

                return -1;
            case 21:
                if ((bc & 192L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 21;
                    return 7;
                }

                return -1;
            case 22:
                if ((bc & 192L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 22;
                    return 7;
                }

                return -1;
            case 23:
                if ((bc & 192L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 23;
                    return 7;
                }

                return -1;
            case 24:
                if ((bc & 128L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 24;
                    return 7;
                }

                return -1;
            case 25:
                if ((bc & 128L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 25;
                    return 7;
                }

                return -1;
            case 26:
                if ((bc & 128L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 26;
                    return 7;
                }

                return -1;
            case 27:
                if ((bc & 128L) != 0L) {
                    jjmatchedKind = 76;
                    jjmatchedPos = 27;
                    return 7;
                }

                return -1;
            default:
                return -1;
        }
    }

    private static void jjCheckNAdd(int ba) {
        if (jjrounds[ba] != jjround) {
            jjstateSet[jjnewStateCnt++] = ba;
            jjrounds[ba] = jjround;
        }
    }

    public static int moveStringLiteralDfa13(long ba, long bb, long bc) {
        long bd = bc;
        if ((0L & ba | (bd = bd & bb)) == 0L) {
            return jjStartNfa_0(11, ba, bb);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(12, 0L, bd);
            return 13;
        }

        byte bj;
        switch (curChar) {
            case ':':
                if ((bd & 2L) != 0L) {
                    return jjStopAtPos(13, 65);
                }

                long bh = 76733928428305L;
                bj = 12;
                break;
            case 'd':
                if ((bd & 1L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(13, 64, integer);
                }

                long bg = 76733928428305L;
                bj = 12;
                break;
            case 'e':
                return moveStringLiteralDfa14(bd, 128L);
            case 'h':
                return moveStringLiteralDfa14(bd, 24L);
            case 'n':
                return moveStringLiteralDfa14(bd, 32L);
            case 'r':
                return moveStringLiteralDfa14(bd, 64L);
            case 's':
                return moveStringLiteralDfa14(bd, 4L);
            default:
                long bi = 76733928428305L;
                bj = 12;
        }

        long be = bd;
        byte bf = bj;
        return jjStartNfa_0(bf, 0L, be);
    }

    public static final boolean jjCanMove_0(int ba, int bb, int bc, long bd, long be) {
        switch (ba) {
            case 0:
                return (jjbitVec2[bc] & be) != 0L;
            default:
                return (jjbitVec0[bb] & bd) != 0L;
        }
    }

    private static int moveStringLiteralDfa1(long ba, long bb) {
        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(0, ba, bb);
            return 1;
        }

        switch (curChar) {
            case '*':
                if ((ba & 256L) != 0L) {
                    Integer integer = 0;
                    return jjStartNfaWithStates_0(1, 8, integer);
                }
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
            case ';':
            case '<':
            case '=':
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
            case 'd':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 's':
            case 'v':
            case 'w':
            default:
                break;
            case '/':
                if ((ba & 64L) != 0L) {
                    return jjStopAtPos(1, 6);
                }
                break;
            case ':':
                if ((ba & 65536L) != 0L) {
                    return jjStopAtPos(1, 16);
                }
                break;
            case '>':
                if ((ba & 16777216L) != 0L) {
                    return jjStopAtPos(1, 24);
                }
                break;
            case 'a':
                return moveStringLiteralDfa2(ba, -8646839813611192320L, bb, 1L);
            case 'b':
                return moveStringLiteralDfa2(ba, 562949953421312L, bb, 64L);
            case 'c':
                return moveStringLiteralDfa2(ba, 35184372088832L, bb, 0L);
            case 'e':
                return moveStringLiteralDfa2(ba, 1224979107234709504L, bb, 128L);
            case 'g':
                return moveStringLiteralDfa2(ba, 144115188075855872L, bb, 0L);
            case 'i':
                return moveStringLiteralDfa2(ba, 36028819567542272L, bb, 26L);
            case 'l':
                return moveStringLiteralDfa2(ba, 34359738368L, bb, 0L);
            case 'n':
                return moveStringLiteralDfa2(ba, 297519050483826688L, bb, 0L);
            case 'o':
                return moveStringLiteralDfa2(ba, 4612831778397224960L, bb, 0L);
            case 'r':
                return moveStringLiteralDfa2(ba, 20407073518911488L, bb, 4L);
            case 't':
                return moveStringLiteralDfa2(ba, 549755813888L, bb, 0L);
            case 'u':
                return moveStringLiteralDfa2(ba, 274877906944L, bb, 32L);
            case 'x':
                return moveStringLiteralDfa2(ba, 13194139533312L, bb, 0L);
            case 'y':
                return moveStringLiteralDfa2(ba, 2310346608841064448L, bb, 0L);
        }

        return jjStartNfa_0(0, ba, bb);
    }

    public static int moveStringLiteralDfa17(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(15, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(16, 0L, bc);
            return 17;
        }

        switch (curChar) {
            case 'a':
                return jjMoveStringLiteralDfa2_0(bc, 128L);
            case 'e':
                return jjMoveStringLiteralDfa2_0(bc, 88L);
            case 's':
                return jjMoveStringLiteralDfa2_0(bc, 32L);
            default:
                return jjStartNfa_0(16, 0L, bc);
        }
    }

    public static int moveStringLiteralDfa5(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(3, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(4, be, bf);
            return 5;
        }

        switch (curChar) {
            case ':':
                if ((be & 34359738368L) != 0L) {
                    return jjStopAtPos(5, 35);
                }
            case ';':
            case '<':
            case '=':
            case '?':
            case '@':
            case 'A':
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
            case 'j':
            case 'k':
            case 'm':
            case 'n':
            case 'p':
            case 'q':
            default:
                break;
            case '>':
                if ((be & 17179869184L) != 0L) {
                    return jjStopAtPos(5, 34);
                }
                break;
            case 'B':
                return moveStringLiteralDfa6(be, 0L, bf, 4L);
            case 'a':
                return moveStringLiteralDfa6(be, -8934578710749642752L, bf, 0L);
            case 'c':
                if ((be & 274877906944L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(5, 38, integer);
                }

                if ((be & 549755813888L) != 0L) {
                    Integer integer1 = 7;
                    return jjStartNfaWithStates_0(5, 39, integer1);
                }

                return moveStringLiteralDfa6(be, 2533274790395904L, bf, 64L);
            case 'd':
                return moveStringLiteralDfa6(be, 72066390130950144L, bf, 128L);
            case 'e':
                if ((be & 137438953472L) != 0L) {
                    Integer integer2 = 7;
                    return jjStartNfaWithStates_0(5, 37, integer2);
                }

                if ((be & 1099511627776L) != 0L) {
                    Integer integer3 = 7;
                    return jjStartNfaWithStates_0(5, 40, integer3);
                }

                return moveStringLiteralDfa6(be, 4523390836670464L, bf, 32L);
            case 'f':
                return moveStringLiteralDfa6(be, 9007199254740992L, bf, 0L);
            case 'g':
                return moveStringLiteralDfa6(be, 576531121047601152L, bf, 0L);
            case 'i':
                return moveStringLiteralDfa6(be, 19175482788413440L, bf, 0L);
            case 'l':
                if ((be & 68719476736L) != 0L) {
                    Integer integer4 = 7;
                    return jjStartNfaWithStates_0(5, 36, integer4);
                }
                break;
            case 'o':
                return moveStringLiteralDfa6(be, 0L, bf, 1L);
            case 'r':
                return moveStringLiteralDfa6(be, 8070450532247928832L, bf, 0L);
            case 's':
                return moveStringLiteralDfa6(be, 180143985094819840L, bf, 0L);
            case 't':
                return moveStringLiteralDfa6(be, 145135534866432L, bf, 24L);
            case 'u':
                return moveStringLiteralDfa6(be, 0L, bf, 2L);
        }

        return jjStartNfa_0(4, be, bf);
    }

    public static int moveStringLiteralDfa14(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(12, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(13, 0L, bc);
            return 14;
        }

        byte bh;
        switch (curChar) {
            case ':':
                if ((bc & 4L) != 0L) {
                    return jjStopAtPos(14, 66);
                }

                long bf = 76733928428305L;
                bh = 13;
                break;
            case 'C':
                return moveStringLiteralDfa15(bc, 32L);
            case 'a':
                return moveStringLiteralDfa15(bc, 24L);
            case 'e':
                return moveStringLiteralDfa15(bc, 64L);
            case 'r':
                return moveStringLiteralDfa15(bc, 128L);
            default:
                long bg = 76733928428305L;
                bh = 13;
        }

        long bd = bc;
        byte be = bh;
        return jjStartNfa_0(be, 0L, bd);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static int jjMoveNfa_0() {
        int ba = 0;
        int bb = 0;
        jjnewStateCnt = 3;
        int bc = 1;
        jjstateSet[0] = 0;
        int bd = Integer.MAX_VALUE;

        while (true) {
            if (++jjround == Integer.MAX_VALUE) {
                ReInitRounds();
            }

            if (curChar < '@') {
                long be = 1L << curChar;

                do {
                    bc += -1;
                    switch (jjstateSet[bc]) {
                        case 0:
                            if ((13312L & be) != 0L && bd > 9) {
                                bd = 9;
                            }

                            if (curChar == '\r') {
                                jjstateSet[jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if (curChar == '\n' && bd > 9) {
                                bd = 9;
                            }
                            break;
                        case 2:
                            if (curChar == '\r') {
                                jjstateSet[jjnewStateCnt++] = 1;
                            }
                    }
                } while (bc != bb);
            } else if (curChar < 128) {
                do {
                    bc += -1;
                    switch (jjstateSet[bc]) {
                    }
                } while (bc != bb);
            } else {
                do {
                    bc += -1;
                    switch (jjstateSet[bc]) {
                    }
                } while (bc != bb);
            }

            if (bd != Integer.MAX_VALUE) {
                jjmatchedKind = bd;
                jjmatchedPos = ba;
                bd = Integer.MAX_VALUE;
            }

            ba++;
            int bf = bc = jjnewStateCnt;
            jjnewStateCnt = bb;
            if (bf == (bb = 3 - bb)) {
                return ba;
            }

            try {
                curChar = ChangeLogSimpleCharStream.readChar();
            } catch (IOException iOException) {
                return ba;
            }
        }
    }

    public static int moveStringLiteralDfa11(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(9, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(10, be, bf);
            return 11;
        }

        byte bm;
        switch (curChar) {
            case ':':
                if ((be & 1152921504606846976L) != 0L) {
                    return jjStopAtPos(11, 60);
                }

                long bk = 76733928428305L;
                bm = 10;
                break;
            case 'O':
                return moveStringLiteralDfa12(be, 0L, bf, 2L);
            case 'a':
                return moveStringLiteralDfa12(be, 0L, bf, 4L);
            case 'd':
                if ((be & 2305843009213693952L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(11, 61, integer);
                }

                return moveStringLiteralDfa12(be, Long.MIN_VALUE, bf, 0L);
            case 'e':
                return moveStringLiteralDfa12(be, 0L, bf, 128L);
            case 'f':
                return moveStringLiteralDfa12(be, 0L, bf, 64L);
            case 'g':
                return moveStringLiteralDfa12(be, 0L, bf, 1L);
            case 'i':
                return moveStringLiteralDfa12(be, 0L, bf, 32L);
            case 'o':
                if ((be & 576460752303423488L) != 0L) {
                    Integer integer1 = 6;
                    return jjStartNfaWithStates_0(11, 59, integer1);
                }

                long bj = 76733928428305L;
                bm = 10;
                break;
            case 's':
                return moveStringLiteralDfa12(be, 4611686018427387904L, bf, 0L);
            case 't':
                return moveStringLiteralDfa12(be, 0L, bf, 24L);
            default:
                long bl = 76733928428305L;
                bm = 10;
        }

        long bg = bf;
        long bh = be;
        byte bi = bm;
        return jjStartNfa_0(bi, bh, bg);
    }

    public static ChangeLogToken getNextToken() {
        ChangeLogToken changeLogToken = null;
        int ba = 0;

        label128:
        while (true) {
            try {
                curChar = ChangeLogSimpleCharStream.BeginToken();
            } catch (IOException iOException) {
                jjmatchedKind = 0;
                ChangeLogToken changeLogToken1 = jjFillToken();
                changeLogToken1.R = changeLogToken;
                return changeLogToken1;
            }

            image = jjimage;
            image.setLength(0);
            jjimageLen = 0;

            while (true) {
                switch (curLexState) {
                    case 0:
                        try {
                            ChangeLogSimpleCharStream.backup(0);

                            while (curChar <= ' ' && (4294981120L & 1L << curChar) != 0L) {
                                curChar = ChangeLogSimpleCharStream.BeginToken();
                            }
                        } catch (IOException iOException3) {
                            continue label128;
                        }

                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_1();
                        if (jjmatchedPos == 0 && jjmatchedKind > 12) {
                            jjmatchedKind = 12;
                        }
                        break;
                    case 2:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_2();
                        if (jjmatchedPos == 0 && jjmatchedKind > 12) {
                            jjmatchedKind = 12;
                        }
                        break;
                    case 3:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_3();
                        if (jjmatchedPos == 0 && jjmatchedKind > 12) {
                            jjmatchedKind = 12;
                        }
                        break;
                    case 4:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_4();
                }

                if (jjmatchedKind == Integer.MAX_VALUE) {
                    break label128;
                }

                if (jjmatchedPos + 1 < ba) {
                    ChangeLogSimpleCharStream.backup(ba - jjmatchedPos - 1);
                }

                if ((jjtoToken[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 63)) != 0L) {
                    ChangeLogToken changeLogToken3 = jjFillToken();
                    changeLogToken3.R = changeLogToken;
                    if (jjnewLexState[jjmatchedKind] != -1) {
                        curLexState = jjnewLexState[jjmatchedKind];
                    }

                    return changeLogToken3;
                }

                if ((jjtoSkip[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 63)) != 0L) {
                    if ((jjtoSpecial[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 63)) != 0L) {
                        ChangeLogToken changeLogToken2 = jjFillToken();
                        if (changeLogToken == null) {
                            changeLogToken = changeLogToken2;
                        } else {
                            changeLogToken2.R = changeLogToken;
                            changeLogToken = changeLogToken.h = changeLogToken2;
                        }

                        skipLexicalActions();
                    } else {
                        skipLexicalActions();
                    }

                    if (jjnewLexState[jjmatchedKind] != -1) {
                        curLexState = jjnewLexState[jjmatchedKind];
                    }
                    break;
                }

                MoreLexicalActions();
                if (jjnewLexState[jjmatchedKind] != -1) {
                    curLexState = jjnewLexState[jjmatchedKind];
                }

                ba = 0;
                jjmatchedKind = Integer.MAX_VALUE;

                try {
                    curChar = ChangeLogSimpleCharStream.readChar();
                } catch (IOException iOException2) {
                    break label128;
                }
            }
        }

        int endColumn = ChangeLogSimpleCharStream.getEndColumn();
        int column = ChangeLogSimpleCharStream.getColumn();
        String string = null;
        boolean bl = false;

        try {
            ChangeLogSimpleCharStream.readChar();
            ChangeLogSimpleCharStream.backup(1);
        } catch (IOException iOException1) {
            bl = true;
            string = ba <= 1 ? "" : ChangeLogSimpleCharStream.GetImage();
            if (curChar != '\n' && curChar != '\r') {
                column++;
            } else {
                endColumn++;
                column = 0;
            }
        }

        if (!bl) {
            ChangeLogSimpleCharStream.backup(1);
            string = ba <= 1 ? "" : ChangeLogSimpleCharStream.GetImage();
        }

        char bd = curChar;
        String string1 = string;
        int be = column;
        int bf = endColumn;
        boolean bl1 = bl;
        throw new ChangeLogTokenMgrError(bl1, bf, be, string1, bd);
    }

    public static int moveStringLiteralDfa8(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(6, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(7, be, bf);
            return 8;
        }

        byte bo;
        switch (curChar) {
            case ':':
                if ((be & 36028797018963968L) != 0L) {
                    return jjStopAtPos(8, 55);
                }

                long bm = 76733928428305L;
                bo = 7;
                break;
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
            case 'b':
            case 'g':
            case 'j':
            case 'm':
            case 'n':
            case 'p':
            case 'q':
            case 's':
            default:
                long bl = 76733928428305L;
                bo = 7;
                break;
            case 'a':
                return moveStringLiteralDfa9(be, 1152921504606846976L, bf, 0L);
            case 'c':
                if ((be & 4503599627370496L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(8, 52, integer);
                }

                long bk = 76733928428305L;
                bo = 7;
                break;
            case 'd':
                if ((be & 2251799813685248L) != 0L) {
                    Integer integer1 = 7;
                    return jjStartNfaWithStates_0(8, 51, integer1);
                }

                long bj = 76733928428305L;
                bo = 7;
                break;
            case 'e':
                if ((be & 9007199254740992L) != 0L) {
                    Integer integer2 = 7;
                    return jjStartNfaWithStates_0(8, 53, integer2);
                }

                return moveStringLiteralDfa9(be, 0L, bf, 122L);
            case 'f':
                return moveStringLiteralDfa9(be, 72057594037927936L, bf, 0L);
            case 'h':
                return moveStringLiteralDfa9(be, 0L, bf, 1L);
            case 'i':
                return moveStringLiteralDfa9(be, 2882303761517117440L, bf, 0L);
            case 'k':
                return moveStringLiteralDfa9(be, 0L, bf, 4L);
            case 'l':
                return moveStringLiteralDfa9(be, 4611686018427387904L, bf, 0L);
            case 'o':
                return moveStringLiteralDfa9(be, 288230376151711744L, bf, 0L);
            case 'r':
                return moveStringLiteralDfa9(be, 0L, bf, 128L);
            case 't':
                if ((be & 18014398509481984L) != 0L) {
                    Integer integer3 = 7;
                    return jjStartNfaWithStates_0(8, 54, integer3);
                }

                long bn = 76733928428305L;
                bo = 7;
                break;
            case 'u':
                return moveStringLiteralDfa9(be, Long.MIN_VALUE, bf, 0L);
            case 'v':
                return moveStringLiteralDfa9(be, 144115188075855872L, bf, 0L);
        }

        long bg = bf;
        long bh = be;
        byte bi = bo;
        return jjStartNfa_0(bi, bh, bg);
    }

    public static int moveStringLiteralDfa16(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(14, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(15, 0L, bc);
            return 16;
        }

        switch (curChar) {
            case 'a':
                return moveStringLiteralDfa17(bc, 32L);
            case 'b':
            case 'd':
            case 'e':
            case 'f':
            default:
                return jjStartNfa_0(15, 0L, bc);
            case 'c':
                return moveStringLiteralDfa17(bc, 64L);
            case 'g':
                return moveStringLiteralDfa17(bc, 24L);
            case 'h':
                return moveStringLiteralDfa17(bc, 128L);
        }
    }

    public static int jjMoveStringLiteralDfa0_3() {
        switch (curChar) {
            case '*':
                return jjMoveStringLiteralDfa1_3();
            default:
                return 1;
        }
    }

    public static int jjMoveStringLiteralDfa6_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(22, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(23, 0L, bc);
            return 24;
        }

        byte bh;
        switch (curChar) {
            case ':':
                if ((bc & 64L) != 0L) {
                    return jjStopAtPos(24, 70);
                }

                long bf = 76733928428305L;
                bh = 23;
                break;
            case 's':
                return jjMoveStringLiteralDfa4_0(bc);
            default:
                long bg = 76733928428305L;
                bh = 23;
        }

        long bd = bc;
        byte be = bh;
        return jjStartNfa_0(be, 0L, bd);
    }

    public static int moveStringLiteralDfa9(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(7, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(8, be, bf);
            return 9;
        }

        byte bm;
        switch (curChar) {
            case ':':
                if ((be & 72057594037927936L) != 0L) {
                    return jjStopAtPos(9, 56);
                }

                long bk = 76733928428305L;
                bm = 8;
                break;
            case 'C':
                return moveStringLiteralDfa10(be, 0L, bf, 4L);
            case 'N':
                return moveStringLiteralDfa10(be, 0L, bf, 24L);
            case 'R':
                return moveStringLiteralDfa10(be, 0L, bf, 64L);
            case 'a':
                return moveStringLiteralDfa10(be, 4611686018427387904L, bf, 129L);
            case 'c':
                return moveStringLiteralDfa10(be, 0L, bf, 32L);
            case 'e':
                if ((be & 144115188075855872L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(9, 57, integer);
                }

                long bj = 76733928428305L;
                bm = 8;
                break;
            case 'n':
                if ((be & 288230376151711744L) != 0L) {
                    Integer integer1 = 7;
                    return jjStartNfaWithStates_0(9, 58, integer1);
                }

                return moveStringLiteralDfa10(be, 576460752303423488L, bf, 0L);
            case 'r':
                return moveStringLiteralDfa10(be, Long.MIN_VALUE, bf, 2L);
            case 's':
                return moveStringLiteralDfa10(be, 1152921504606846976L, bf, 0L);
            case 'z':
                return moveStringLiteralDfa10(be, 2305843009213693952L, bf, 0L);
            default:
                long bl = 76733928428305L;
                bm = 8;
        }

        long bg = bf;
        long bh = be;
        byte bi = bm;
        return jjStartNfa_0(bi, bh, bg);
    }

    public static int moveStringLiteralDfa23(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(21, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(22, 0L, bc);
            return 23;
        }

        switch (curChar) {
            case 'a':
                return jjMoveStringLiteralDfa6_0(bc, 128L);
            case 's':
                return jjMoveStringLiteralDfa6_0(bc, 64L);
            default:
                return jjStartNfa_0(22, 0L, bc);
        }
    }

    public static int moveStringLiteralDfa12(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(10, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(11, be, bf);
            return 12;
        }

        byte bl;
        switch (curChar) {
            case ':':
                if ((be & 4611686018427387904L) != 0L) {
                    return jjStopAtPos(12, 62);
                }

                if ((be & Long.MIN_VALUE) != 0L) {
                    return jjStopAtPos(12, 63);
                }

                long bj = 76733928428305L;
                bl = 11;
                break;
            case 'C':
                return moveStringLiteralDfa13(be, bf, 24L);
            case 'e':
                return moveStringLiteralDfa13(be, bf, 65L);
            case 'f':
                return moveStringLiteralDfa13(be, bf, 2L);
            case 'o':
                return moveStringLiteralDfa13(be, bf, 32L);
            case 's':
                return moveStringLiteralDfa13(be, bf, 4L);
            case 't':
                return moveStringLiteralDfa13(be, bf, 128L);
            default:
                long bk = 76733928428305L;
                bl = 11;
        }

        long bg = bf;
        long bh = be;
        byte bi = bl;
        return jjStartNfa_0(bi, bh, bg);
    }

    public static int jjMoveStringLiteralDfa7_0(long ba) {
        long bb = 128L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(25, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(26, 0L, bb);
            return 27;
        }

        switch (curChar) {
            case 's':
                return jjMoveStringLiteralDfa8_0(bb);
            default:
                return jjStartNfa_0(26, 0L, bb);
        }
    }

    public static int jjMoveStringLiteralDfa3_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(17, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(18, 0L, bc);
            return 19;
        }

        byte bh;
        switch (curChar) {
            case ':':
                if ((bc & 16L) != 0L) {
                    return jjStopAtPos(19, 68);
                }

                if ((bc & 32L) != 0L) {
                    return jjStopAtPos(19, 69);
                }

                long bf = 76733928428305L;
                bh = 18;
                break;
            case 'C':
                return moveStringLiteralDfa20(bc, 64L);
            case 'g':
                return moveStringLiteralDfa20(bc, 128L);
            default:
                long bg = 76733928428305L;
                bh = 18;
        }

        long bd = bc;
        byte be = bh;
        return jjStartNfa_0(be, 0L, bd);
    }

    public static int jjStartNfaWithStates_4() {
        jjmatchedKind = 73;
        jjmatchedPos = 0;

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return 0 + 1;
        }

        return moveNfaStringState(2, 0 + 1);
    }

    public static int jjMoveStringLiteralDfa1_3() {
        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return 1;
        }

        switch (curChar) {
            case '/':
                return jjStopAtPos(1, 11);
            default:
                return 2;
        }
    }

    public static ChangeLogToken jjFillToken() {
        String string1 = jjstrLiteralImages[jjmatchedKind];
        String string = string1 == null ? ChangeLogSimpleCharStream.GetImage() : string1;
        int endLine = ChangeLogSimpleCharStream.getEndLine();
        int line = ChangeLogSimpleCharStream.getLine();
        int endColumn = ChangeLogSimpleCharStream.getEndColumn();
        int column = ChangeLogSimpleCharStream.getColumn();
        ChangeLogToken changeLogToken = ChangeLogToken.newToken_sg9(jjmatchedKind);
        changeLogToken.H = jjmatchedKind;
        changeLogToken.k = string;
        changeLogToken.n = endLine;
        changeLogToken.q = endColumn;
        changeLogToken.D = line;
        changeLogToken.B = column;
        return changeLogToken;
    }

    public static int jjMoveStringLiteralDfa1_2() {
        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return 1;
        }

        switch (curChar) {
            case '/':
                return jjStopAtPos(1, 10);
            default:
                return 2;
        }
    }

    public static int moveStringLiteralDfa6(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(4, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(5, be, bf);
            return 6;
        }

        switch (curChar) {
            case ':':
                if ((be & 2199023255552L) != 0L) {
                    return jjStopAtPos(6, 41);
                } else if ((be & 17592186044416L) != 0L) {
                    return jjStopAtPos(6, 44);
                }
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
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
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
            case 'i':
            case 'j':
            case 'k':
            case 'n':
            case 'p':
            case 'q':
            case 'r':
            default:
                return jjStartNfa_0(5, be, bf);
            case 'C':
                return moveStringLiteralDfa7(be, 1152921504606846976L, bf, 0L);
            case 'O':
                return moveStringLiteralDfa7(be, 36028797018963968L, bf, 0L);
            case 'P':
                return moveStringLiteralDfa7(be, 0L, bf, 128L);
            case 'a':
                return moveStringLiteralDfa7(be, 9007199254740992L, bf, 68L);
            case 'c':
                return moveStringLiteralDfa7(be, -9222809086901354496L, bf, 0L);
            case 'd':
                return moveStringLiteralDfa7(be, 4611686018427387904L, bf, 0L);
            case 'e':
                if ((be & 4398046511104L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(6, 42, integer);
                } else if ((be & 8796093022208L) != 0L) {
                    Integer integer1 = 7;
                    return jjStartNfaWithStates_0(6, 43, integer1);
                } else {
                    if ((be & 140737488355328L) != 0L) {
                        Integer integer2 = 7;
                        return jjStartNfaWithStates_0(6, 47, integer2);
                    }

                    return moveStringLiteralDfa7(be, 594826994533793792L, bf, 0L);
                }
            case 'f':
                return moveStringLiteralDfa7(be, 0L, bf, 32L);
            case 'l':
                return moveStringLiteralDfa7(be, 1125899906842624L, bf, 0L);
            case 'm':
                return moveStringLiteralDfa7(be, 0L, bf, 2L);
            case 'o':
                return moveStringLiteralDfa7(be, 2305843009213693952L, bf, 0L);
            case 's':
                return moveStringLiteralDfa7(be, 216172782113783808L, bf, 0L);
            case 't':
                return moveStringLiteralDfa7(be, 295020959964856320L, bf, 1L);
            case 'u':
                return moveStringLiteralDfa7(be, 0L, bf, 24L);
        }
    }

    public static void skipLexicalActions() {
        switch (jjmatchedKind) {
        }
    }

    public static int jjMoveStringLiteralDfa8_0(long ba) {
        long bb = 128L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(26, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(27, 0L, bb);
            return 28;
        }

        byte bg;
        switch (curChar) {
            case ':':
                if ((bb & 128L) != 0L) {
                    return jjStopAtPos(28, 71);
                }

                long be = 76733928428305L;
                bg = 27;
                break;
            default:
                long bf = 76733928428305L;
                bg = 27;
        }

        long bc = bb;
        byte bd = bg;
        return jjStartNfa_0(bd, 0L, bc);
    }

    public static void ReInit(ChangeLogSimpleCharStream changeLogSimpleCharStream) {
        jjnewStateCnt = 0;
        jjmatchedPos = 0;
        curLexState = defaultLexState;
        input_stream = changeLogSimpleCharStream;
        ReInitRounds();
    }

    public static int jjStartNfaWithStates_0(int ba, int bb, Integer integer) {
        jjmatchedKind = bb;
        jjmatchedPos = ba;

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return ba + 1;
        }

        return moveNfaDefaultState(integer, ba + 1);
    }

    private static int moveNfaDefaultState(int ba, int bb) {
        int bd = 0;
        jjnewStateCnt = 7;
        int be = 1;
        jjstateSet[0] = ba;
        int bf = Integer.MAX_VALUE;

        while (true) {
            if (++jjround == Integer.MAX_VALUE) {
                ReInitRounds();
            }

            if (curChar < '@') {
                long cc = 1L << curChar;

                do {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 0:
                            if (curChar == '*') {
                                jjstateSet[jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if ((-140737488355329L & cc) != 0L && bf > 7) {
                                bf = 7;
                            }
                            break;
                        case 2:
                            if (curChar == '*') {
                                jjstateSet[jjnewStateCnt++] = 0;
                            }
                            break;
                        case 3:
                            if ((287992950628008447L & cc) != 0L) {
                                byte cl;
                                if (bf > 77) {
                                    bf = 77;
                                    cl = 6;
                                } else {
                                    cl = 6;
                                }

                                byte by = cl;
                                jjCheckNAdd(by);
                            } else if (curChar == '/') {
                                jjstateSet[jjnewStateCnt++] = 2;
                            }

                            long cm;
                            if ((287948901175001088L & cc) != 0L) {
                                if (bf > 76) {
                                    bf = 76;
                                }

                                byte bz = 5;
                                jjCheckNAdd(bz);
                                cm = 287948901175001088L;
                            } else {
                                cm = 287948901175001088L;
                            }

                            if ((cm & cc) != 0L) {
                                if (bf > 75) {
                                    bf = 75;
                                }

                                byte ca = 4;
                                jjCheckNAdd(ca);
                            }
                            break;
                        case 4:
                            if ((287948901175001088L & cc) != 0L) {
                                if (bf > 75) {
                                    bf = 75;
                                }

                                byte bx = 4;
                                jjCheckNAdd(bx);
                            }
                            break;
                        case 5:
                            if ((287948901175001088L & cc) != 0L) {
                                if (bf > 76) {
                                    bf = 76;
                                }

                                byte bw = 5;
                                jjCheckNAdd(bw);
                            }
                            break;
                        case 6:
                            if ((287992950628008447L & cc) != 0L) {
                                byte ck;
                                if (bf > 77) {
                                    bf = 77;
                                    ck = 6;
                                } else {
                                    ck = 6;
                                }

                                byte bv = ck;
                                jjCheckNAdd(bv);
                            }
                            break;
                        case 7:
                            if ((287992950628008447L & cc) != 0L) {
                                byte cj;
                                if (bf > 77) {
                                    bf = 77;
                                    cj = 6;
                                } else {
                                    cj = 6;
                                }

                                byte bt = cj;
                                jjCheckNAdd(bt);
                            }

                            if ((287948901175001088L & cc) != 0L) {
                                if (bf > 76) {
                                    bf = 76;
                                }

                                byte bu = 5;
                                jjCheckNAdd(bu);
                            }
                    }
                } while (be != bd);
            } else if (curChar < 128) {
                long cb = 1L << (curChar & '?');

                do {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 1:
                            if (bf > 7) {
                                bf = 7;
                            }
                        case 2:
                        case 4:
                        default:
                            break;
                        case 3:
                            long ci;
                            if ((-8646911290859585538L & cb) != 0L) {
                                byte ch;
                                if (bf > 77) {
                                    bf = 77;
                                    ch = 6;
                                } else {
                                    ch = 6;
                                }

                                byte br = ch;
                                jjCheckNAdd(br);
                                ci = 576460743847706622L;
                            } else {
                                ci = 576460743847706622L;
                            }

                            if ((ci & cb) != 0L) {
                                if (bf > 76) {
                                    bf = 76;
                                }

                                byte bs = 5;
                                jjCheckNAdd(bs);
                            }
                            break;
                        case 5:
                            if ((576460743847706622L & cb) != 0L) {
                                if (bf > 76) {
                                    bf = 76;
                                }

                                byte bq = 5;
                                jjCheckNAdd(bq);
                            }
                            break;
                        case 6:
                            if ((-8646911290859585538L & cb) != 0L) {
                                byte cg;
                                if (bf > 77) {
                                    bf = 77;
                                    cg = 6;
                                } else {
                                    cg = 6;
                                }

                                byte bp = cg;
                                jjCheckNAdd(bp);
                            }
                            break;
                        case 7:
                            long cf;
                            if ((-8646911290859585538L & cb) != 0L) {
                                byte ce;
                                if (bf > 77) {
                                    bf = 77;
                                    ce = 6;
                                } else {
                                    ce = 6;
                                }

                                byte bn = ce;
                                jjCheckNAdd(bn);
                                cf = 576460743847706622L;
                            } else {
                                cf = 576460743847706622L;
                            }

                            if ((cf & cb) != 0L) {
                                if (bf > 76) {
                                    bf = 76;
                                }

                                byte bo = 5;
                                jjCheckNAdd(bo);
                            }
                    }
                } while (be != bd);
            } else {
                int bg = curChar >> '\b';
                int bh = bg >> 6;
                long bi = 1L << (bg & 63);
                int bj = (curChar & 255) >> 6;
                long bk = 1L << (curChar & '?');

                do {
                    be += -1;
                    switch (jjstateSet[be]) {
                        case 1:
                            long bm = bk;
                            if (jjCanMove_1(bg, bj, bm) && bf > 7) {
                                bf = 7;
                            }
                        case 2:
                        case 4:
                        case 5:
                        default:
                            break;
                        case 3:
                        case 6:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                byte cd;
                                if (bf > 77) {
                                    bf = 77;
                                    cd = 6;
                                } else {
                                    cd = 6;
                                }

                                byte bl = cd;
                                jjCheckNAdd(bl);
                            }
                            break;
                        case 7:
                            if (jjCanMove_0(bg, bh, bj, bi, bk)) {
                                byte co;
                                if (bf > 77) {
                                    bf = 77;
                                    co = 6;
                                } else {
                                    co = 6;
                                }

                                byte bc = co;
                                jjCheckNAdd(bc);
                            }
                    }
                } while (be != bd);
            }

            if (bf != Integer.MAX_VALUE) {
                jjmatchedKind = bf;
                jjmatchedPos = bb;
                bf = Integer.MAX_VALUE;
            }

            bb++;
            int cn = be = jjnewStateCnt;
            jjnewStateCnt = bd;
            if (cn == (bd = 7 - bd)) {
                return bb;
            }

            try {
                curChar = ChangeLogSimpleCharStream.readChar();
            } catch (IOException iOException) {
                return bb;
            }
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

    public static int jjMoveStringLiteralDfa0_1() {
        return jjMoveNfa_0();
    }

    public static int moveStringLiteralDfa4(long ba, long bb, long bc, long bd) {
        long be = bb;
        long bf = bd;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(2, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(3, be, bf);
            return 4;
        }

        switch (curChar) {
            case ':':
                if ((be & 536870912L) != 0L) {
                    return jjStopAtPos(4, 29);
                }
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
            case 'O':
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
            case '_':
            case '`':
            case 'b':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'w':
            case 'x':
            default:
                break;
            case 'N':
                return moveStringLiteralDfa5(be, 0L, bf, 3L);
            case 'R':
                return moveStringLiteralDfa5(be, 0L, bf, 32L);
            case 'a':
                return moveStringLiteralDfa5(be, 5188357945682821120L, bf, 24L);
            case 'c':
                return moveStringLiteralDfa5(be, 17592186044416L, bf, 0L);
            case 'd':
                return moveStringLiteralDfa5(be, 36028797018963968L, bf, 0L);
            case 'e':
                if ((be & 2147483648L) != 0L) {
                    Integer integer = 7;
                    return jjStartNfaWithStates_0(4, 31, integer);
                }

                long bk = bf;
                long bl = 1299288492496388096L;
                long bn = be;
                return moveStringLiteralDfa5(bn, bl, bk, 4L);
            case 'f':
                return moveStringLiteralDfa5(be, Long.MIN_VALUE, bf, 0L);
            case 'g':
                return moveStringLiteralDfa5(be, 137438953472L, bf, 0L);
            case 'h':
                return moveStringLiteralDfa5(be, 2310346608841064448L, bf, 0L);
            case 'i':
                return moveStringLiteralDfa5(be, 824633720832L, bf, 0L);
            case 'l':
                if ((be & 1073741824L) != 0L) {
                    Integer integer1 = 7;
                    return jjStartNfaWithStates_0(4, 30, integer1);
                }

                long bj = bf;
                long bh = 2199023255552L;
                long bm = be;
                return moveStringLiteralDfa5(bm, bh, bj, 0L);
            case 'n':
                return moveStringLiteralDfa5(be, 316659348799488L, bf, 0L);
            case 'o':
                return moveStringLiteralDfa5(be, 72057594037927936L, bf, 128L);
            case 'r':
                return moveStringLiteralDfa5(be, 9570149208162304L, bf, 0L);
            case 's':
                return moveStringLiteralDfa5(be, 18014432869220352L, bf, 64L);
            case 't':
                if ((be & 4294967296L) != 0L) {
                    Integer integer2 = 7;
                    return jjStartNfaWithStates_0(4, 32, integer2);
                }

                long bg = bf;
                long bi = be;
                return moveStringLiteralDfa5(bi, 289356293238423552L, bg, 0L);
            case 'u':
                return moveStringLiteralDfa5(be, 13194139533312L, bf, 0L);
            case 'v':
                return moveStringLiteralDfa5(be, 1099511627776L, bf, 0L);
            case 'y':
                if ((be & 8589934592L) != 0L) {
                    Integer integer3 = 7;
                    return jjStartNfaWithStates_0(4, 33, integer3);
                }
        }

        return jjStartNfa_0(3, be, bf);
    }

    public static int moveStringLiteralDfa22(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(20, 0L, ba);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(21, 0L, bc);
            return 22;
        }

        switch (curChar) {
            case 'l':
                return moveStringLiteralDfa23(bc, 128L);
            case 's':
                return moveStringLiteralDfa23(bc, 64L);
            default:
                return jjStartNfa_0(21, 0L, bc);
        }
    }

    private static final int jjStartNfa_0(int ba, long bb, long bc) {
        return moveNfaDefaultState(jjStopStringLiteralDfa_0(ba, bb, bc), ba + 1);
    }

    public static int moveStringLiteralDfa10(long ba, long bb, long bc, long bd) {
        long bf = bd;
        long be = bb;
        if (((be = be & ba) | (bf = bf & bc)) == 0L) {
            return jjStartNfa_0(8, ba, bc);
        }

        try {
            curChar = ChangeLogSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(9, be, bf);
            return 10;
        }

        switch (curChar) {
            case 'e':
                return moveStringLiteralDfa11(be, -6917529027641081856L, bf, 64L);
            case 'f':
                return moveStringLiteralDfa11(be, 576460752303423488L, bf, 0L);
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'p':
            case 'q':
            case 'r':
            default:
                return jjStartNfa_0(9, be, bf);
            case 'l':
                return moveStringLiteralDfa11(be, 0L, bf, 4L);
            case 'm':
                return moveStringLiteralDfa11(be, 0L, bf, 128L);
            case 'n':
                return moveStringLiteralDfa11(be, 0L, bf, 1L);
            case 'o':
                return moveStringLiteralDfa11(be, 0L, bf, 24L);
            case 's':
                return moveStringLiteralDfa11(be, 5764607523034234880L, bf, 2L);
            case 't':
                return moveStringLiteralDfa11(be, 0L, bf, 32L);
        }
    }

    private static void staticInit() {
        jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
        jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
        jjstrLiteralImages = new String[]{
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
                "\ufeff",
                "(",
                ":",
                "::",
                ")",
                "[",
                "]",
                ",",
                "/",
                ".",
                "*",
                "=>",
                "and",
                "enum",
                "none",
                "true",
                "Data:",
                "final",
                "false",
                "light",
                "heavy",
                "<init>",
                "Class:",
                "normal",
                "bridge",
                "public",
                "static",
                "native",
                "Module:",
                "execute",
                "exclude",
                "Source:",
                "<clinit>",
                "Package:",
                "private",
                "enhanced",
                "abstract",
                "volatile",
                "protected",
                "synthetic",
                "interface",
                "transient",
                "FieldsOf:",
                "MethodsOf:",
                "aggressive",
                "annotation",
                "package-info",
                "MemberClass:",
                "synchronized",
                "ForwardClass:",
                "Manufactured:",
                "NameNotChanged",
                "LineNumbersOf:",
                "TraceBackClass:",
                "SignatureNotChanged",
                "SignatureNotChanged:",
                "AutoReflectionClass:",
                "ObfuscateReferencesClass:",
                "MethodParameterChangeClasses:",
                null,
                null,
                null,
                null,
                null,
                null
        };
        jjnewLexState = new int[]{
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
                4,
                0,
                -1,
                -1,
                -1,
                -1
        };
        jjtoToken = new long[]{-8191L, 15103L};
        jjtoSkip = new long[]{3646L, 0L};
        jjtoSpecial = new long[]{3584L, 0L};
        jjrounds = new int[7];
        jjstateSet = new int[14];
        jjimage = new StringBuilder();
        image = jjimage;
        curLexState = 0;
        defaultLexState = 0;
    }
}
