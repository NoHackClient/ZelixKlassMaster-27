package com.zelix.klassmaster.proguard.mapping.parser;

import java.io.IOException;

public class ProGuardMappingTokenManager implements ProGuardMappingConstants {
    public static int jjround;
    public static int defaultLexState;
    public static long[] jjbitVec2;
    public static String[] jjstrLiteralImages;
    public static long[] jjbitVec0;
    public static int[] jjstateSet;
    public static char curChar;
    public static long[] jjtoSkip;
    public static int jjmatchedPos;
    public static int jjimageLen;
    public static long[] jjtoToken;
    public static int[] jjnewLexState;
    public static int[] jjrounds;
    public static ProGuardMappingSimpleCharStream input_stream;
    public static long[] jjtoSpecial;
    public static int curLexState;
    public static StringBuilder jjimage;
    public static StringBuilder image;
    public static int jjnewStateCnt;
    public static int jjmatchedKind;

    public static int moveStringLiteralDfaLevel6(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(4, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(5, bc);
            return 6;
        }

        switch (curChar) {
            case 'e':
                return jjMoveStringLiteralDfa8_0(bc, 33554432L);
            case 't':
                return jjMoveStringLiteralDfa8_0(bc, 16777216L);
            default:
                return jjStartNfa_0(5, bc);
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

    public static int jjMoveStringLiteralDfa0_1() {
        return jjAddStates();
    }

    public static final int jjStopStringLiteralDfa_0(int ba, long bb) {
        switch (ba) {
            case 0:
                if ((bb & 524368L) != 0L) {
                    return 2;
                } else if ((bb & 33554432L) != 0L) {
                    jjmatchedKind = 30;
                    return 5;
                } else {
                    if ((bb & 2048L) != 0L) {
                        return 5;
                    }

                    return -1;
                }
            case 1:
                if ((bb & 64L) != 0L) {
                    return 0;
                } else {
                    if ((bb & 33554432L) != 0L) {
                        jjmatchedKind = 30;
                        jjmatchedPos = 1;
                        return 5;
                    }

                    return -1;
                }
            case 2:
                if ((bb & 33554432L) != 0L) {
                    jjmatchedKind = 30;
                    jjmatchedPos = 2;
                    return 5;
                }

                return -1;
            case 3:
                if ((bb & 33554432L) != 0L) {
                    jjmatchedKind = 30;
                    jjmatchedPos = 3;
                    return 5;
                }

                return -1;
            case 4:
                if ((bb & 33554432L) != 0L) {
                    jjmatchedKind = 30;
                    jjmatchedPos = 4;
                    return 5;
                }

                return -1;
            case 5:
                if ((bb & 33554432L) != 0L) {
                    jjmatchedKind = 30;
                    jjmatchedPos = 5;
                    return 5;
                }

                return -1;
            case 6:
                if ((bb & 33554432L) != 0L) {
                    jjmatchedKind = 30;
                    jjmatchedPos = 6;
                    return 5;
                }

                return -1;
            case 7:
                if ((bb & 33554432L) != 0L) {
                    if (jjmatchedPos < 6) {
                        jjmatchedKind = 30;
                        jjmatchedPos = 6;
                    }

                    return -1;
                }

                return -1;
            case 8:
                if ((bb & 33554432L) != 0L) {
                    if (jjmatchedPos < 6) {
                        jjmatchedKind = 30;
                        jjmatchedPos = 6;
                    }

                    return -1;
                }

                return -1;
            case 9:
                if ((bb & 33554432L) != 0L) {
                    if (jjmatchedPos < 6) {
                        jjmatchedKind = 30;
                        jjmatchedPos = 6;
                    }

                    return -1;
                }

                return -1;
            case 10:
                if ((bb & 33554432L) != 0L) {
                    if (jjmatchedPos < 6) {
                        jjmatchedKind = 30;
                        jjmatchedPos = 6;
                    }

                    return -1;
                }

                return -1;
            default:
                return -1;
        }
    }

    public static int jjStartNfaWithStates_0(int ba, int bb, int bc) {
        jjmatchedKind = bb;
        jjmatchedPos = ba;

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return ba + 1;
        }

        return jjMoveNfa_0(bc, ba + 1);
    }

    public static int jjMoveStringLiteralDfa1_2() {
        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return 1;
        }

        switch (curChar) {
            case '/':
                return jjStopAtPos(1, 8);
            default:
                return 2;
        }
    }

    public static int jjMoveStringLiteralDfa8_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(5, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(6, bc);
            return 7;
        }

        byte bf;
        switch (curChar) {
            case '-':
                return jjMoveStringLiteralDfa7_0(bc);
            case '>':
                if ((bc & 16777216L) != 0L) {
                    return jjStopAtPos(7, 24);
                }

                long bd = 26046378827727L;
                bf = 6;
                break;
            default:
                long be = 26046378827727L;
                bf = 6;
        }

        Long long1 = bc;
        Integer integer = Integer.valueOf(bf);
        return jjStartNfa_0(integer, long1);
    }

    public static int moveStringLiteralDfaLevel4(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(2, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(3, bc);
            return 4;
        }

        switch (curChar) {
            case 'a':
                return jjMoveStringLiteralDfa3_0(bc, 33554432L);
            case 'n':
                return jjMoveStringLiteralDfa3_0(bc, 16777216L);
            case 't':
                return jjMoveStringLiteralDfa3_0(bc, 8388608L);
            default:
                return jjStartNfa_0(3, bc);
        }
    }

    public static int jjMoveStringLiteralDfa4_0(long ba) {
        long bb = 33554432L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(8, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(9, bb);
            return 10;
        }

        switch (curChar) {
            case 'f':
                return jjMoveStringLiteralDfa6_0(bb);
            default:
                return jjStartNfa_0(9, bb);
        }
    }

    public static int jjMoveStringLiteralDfa7_0(long ba) {
        long bb = 33554432L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(6, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(7, bb);
            return 8;
        }

        switch (curChar) {
            case 'i':
                return jjMoveStringLiteralDfa5_0(bb);
            default:
                return jjStartNfa_0(7, bb);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static void jjCheckNAdd(int ba) {
        if (jjrounds[ba] != jjround) {
            jjstateSet[jjnewStateCnt++] = ba;
            jjrounds[ba] = jjround;
        }
    }

    public static int jjMoveStringLiteralDfa5_0(long ba) {
        long bb = 33554432L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(7, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(8, bb);
            return 9;
        }

        switch (curChar) {
            case 'n':
                return jjMoveStringLiteralDfa4_0(bb);
            default:
                return jjStartNfa_0(8, bb);
        }
    }

    public static int jjMoveStringLiteralDfa1_3() {
        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return 1;
        }

        switch (curChar) {
            case '/':
                return jjStopAtPos(1, 9);
            default:
                return 2;
        }
    }

    public static int jjAddStates() {
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
                int[] bg = jjstateSet;

                while (true) {
                    bc += -1;
                    switch (bg[bc]) {
                        case 0:
                            char bh;
                            if ((13312L & be) != 0L) {
                                if (bd > 7) {
                                    bd = 7;
                                    bh = curChar;
                                } else {
                                    bh = curChar;
                                }
                            } else {
                                bh = curChar;
                            }

                            if (bh == '\r') {
                                jjstateSet[jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if (curChar == '\n' && bd > 7) {
                                bd = 7;
                            }
                            break;
                        case 2:
                            if (curChar == '\r') {
                                jjstateSet[jjnewStateCnt++] = 1;
                            }
                    }

                    if (bc == bb) {
                        break;
                    }

                    bg = jjstateSet;
                }
            } else if (curChar < 128) {
                int[] bf = jjstateSet;

                while (true) {
                    bc += -1;
                    switch (bf[bc]) {
                    }

                    if (bc == bb) {
                        break;
                    }

                    bf = jjstateSet;
                }
            } else {
                int[] bj = jjstateSet;

                while (true) {
                    bc += -1;
                    switch (bj[bc]) {
                    }

                    if (bc == bb) {
                        break;
                    }

                    bj = jjstateSet;
                }
            }

            if (bd != Integer.MAX_VALUE) {
                jjmatchedKind = bd;
                jjmatchedPos = ba;
                bd = Integer.MAX_VALUE;
            }

            ba++;
            int bi = bc = jjnewStateCnt;
            jjnewStateCnt = bb;
            if (bi == (bb = 3 - bb)) {
                return ba;
            }

            try {
                curChar = ProGuardMappingSimpleCharStream.readChar();
            } catch (IOException iOException) {
                return ba;
            }
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

    public static void ReInitRounds() {
        jjround = -2147483647;
        int ba = 6;

        while (true) {
            int bb = ba;
            ba += -1;
            if (bb <= 0) {
                return;
            }

            jjrounds[ba] = Integer.MIN_VALUE;
        }
    }

    public static void MoreLexicalActions() {
        jjimageLen = jjimageLen + jjmatchedPos + 1;
        switch (jjmatchedKind) {
            case 5:
                image.append(ProGuardMappingSimpleCharStream.GetSuffix(jjimageLen));
                jjimageLen = 0;
                ProGuardMappingSimpleCharStream.backup(1);
        }
    }

    public static int jjStartNfaWithStates_4() {
        jjmatchedKind = 27;
        jjmatchedPos = 0;

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            return 0 + 1;
        }

        return jjMoveNfa_4(2, 0 + 1);
    }

    public static int moveStringLiteralDfaLevel2(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(0, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(1, bc);
            return 2;
        }

        switch (curChar) {
            case 'c':
                return jjMoveStringLiteralDfa2_0(bc, 33554432L);
            case 'l':
                return jjMoveStringLiteralDfa2_0(bc, 16777216L);
            case 'n':
                return jjMoveStringLiteralDfa2_0(bc, 8388608L);
            default:
                return jjStartNfa_0(1, bc);
        }
    }

    public ProGuardMappingTokenManager(ProGuardMappingSimpleCharStream proGuardMappingSimpleCharStream) {
        if (input_stream != null) {
            throw new ProGuardMappingTokenMgrError(
                    "ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", 1
            );
        }

        input_stream = proGuardMappingSimpleCharStream;
    }

    public static int jjMoveStringLiteralDfa0_3() {
        switch (curChar) {
            case '*':
                return jjMoveStringLiteralDfa1_3();
            default:
                return 1;
        }
    }

    public static void ReInit(ProGuardMappingSimpleCharStream proGuardMappingSimpleCharStream) {
        jjnewStateCnt = 0;
        jjmatchedPos = 0;
        curLexState = defaultLexState;
        input_stream = proGuardMappingSimpleCharStream;
        ReInitRounds();
    }

    public static int jjStopAtPos(int ba, int bb) {
        jjmatchedKind = bb;
        jjmatchedPos = ba;
        return ba + 1;
    }

    public static int jjMoveStringLiteralDfa2_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(1, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(2, bc);
            return 3;
        }

        switch (curChar) {
            case 'i':
                return moveStringLiteralDfaLevel4(bc, 25165824L);
            case 'k':
                return moveStringLiteralDfaLevel4(bc, 33554432L);
            default:
                return jjStartNfa_0(2, bc);
        }
    }

    public static int jjMoveStringLiteralDfa0_4() {
        switch (curChar) {
            case '"':
                return jjStartNfaWithStates_4();
            default:
                return jjMoveNfa_4(0, 0);
        }
    }

    public static int jjMoveStringLiteralDfa3_0(long ba, long bb) {
        long bc = bb;
        if ((bc = bc & ba) == 0L) {
            return jjStartNfa_0(3, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(4, bc);
            return 5;
        }

        switch (curChar) {
            case '>':
                if ((bc & 8388608L) != 0L) {
                    return jjStopAtPos(5, 23);
                }
            default:
                return jjStartNfa_0(4, bc);
            case 'g':
                return moveStringLiteralDfaLevel6(bc, 33554432L);
            case 'i':
                return moveStringLiteralDfaLevel6(bc, 16777216L);
        }
    }

    public static final int jjStartNfa_0(int ba, long bb) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(ba, bb), ba + 1);
    }

    public static int jjMoveNfa_0(int ba, int bb) {
        int bc = bb;
        int bi = 0;
        jjnewStateCnt = 6;
        int bj = 1;
        jjstateSet[0] = ba;
        int bk = Integer.MAX_VALUE;

        while (true) {
            if (++jjround == Integer.MAX_VALUE) {
                ReInitRounds();
            }

            if (curChar < '@') {
                long bu = 1L << curChar;
                int[] bw = jjstateSet;

                while (true) {
                    bj += -1;
                    switch (bw[bj]) {
                        case 0:
                            if (curChar == '*') {
                                jjstateSet[jjnewStateCnt++] = 1;
                            }
                            break;
                        case 1:
                            if ((-140737488355329L & bu) != 0L && bk > 5) {
                                bk = 5;
                            }
                            break;
                        case 2:
                            if (curChar == '*') {
                                jjstateSet[jjnewStateCnt++] = 0;
                            }
                            break;
                        case 3:
                            if ((287957766255919615L & bu) != 0L) {
                                if (bk > 30) {
                                    bk = 30;
                                }

                                jjCheckNAdd(5);
                            } else if (curChar == '/') {
                                jjstateSet[jjnewStateCnt++] = 2;
                            }

                            if ((287948901175001088L & bu) != 0L) {
                                if (bk > 29) {
                                    bk = 29;
                                }

                                jjCheckNAdd(4);
                            }
                            break;
                        case 4:
                            if ((287948901175001088L & bu) != 0L) {
                                if (bk > 29) {
                                    bk = 29;
                                }

                                jjCheckNAdd(4);
                            }
                            break;
                        case 5:
                            if ((287957766255919615L & bu) != 0L) {
                                if (bk > 30) {
                                    bk = 30;
                                }

                                jjCheckNAdd(5);
                            }
                    }

                    if (bj == bi) {
                        break;
                    }

                    bw = jjstateSet;
                }
            } else if (curChar < 128) {
                long bt = 1L << (curChar & '?');
                int[] bv = jjstateSet;

                while (true) {
                    bj += -1;
                    switch (bv[bj]) {
                        case 1:
                            if (bk > 5) {
                                bk = 5;
                            }
                        case 2:
                        case 4:
                        default:
                            break;
                        case 3:
                        case 5:
                            if ((-8646911290859585538L & bt) != 0L) {
                                if (bk > 30) {
                                    bk = 30;
                                }

                                jjCheckNAdd(5);
                            }
                    }

                    if (bj == bi) {
                        break;
                    }

                    bv = jjstateSet;
                }
            } else {
                int bl = curChar >> '\b';
                int bm = bl >> 6;
                long bn = 1L << (bl & 63);
                int bo = (curChar & 255) >> 6;
                long bp = 1L << (curChar & '?');
                int[] by = jjstateSet;

                while (true) {
                    bj += -1;
                    switch (by[bj]) {
                        case 1:
                            long bq = bp;
                            int br = bo;
                            int bs = bl;
                            if (jjCanMove_1(bs, br, bq) && bk > 5) {
                                bk = 5;
                            }
                        case 2:
                        case 4:
                        default:
                            break;
                        case 3:
                        case 5:
                            long bd = bp;
                            long be = bn;
                            int bf = bo;
                            int bg = bm;
                            int bh = bl;
                            if (jjCanMove_0(bh, bg, bf, be, bd)) {
                                if (bk > 30) {
                                    bk = 30;
                                }

                                jjCheckNAdd(5);
                            }
                    }

                    if (bj == bi) {
                        break;
                    }

                    by = jjstateSet;
                }
            }

            if (bk != Integer.MAX_VALUE) {
                jjmatchedKind = bk;
                jjmatchedPos = bc;
                bk = Integer.MAX_VALUE;
            }

            bc++;
            int bx = bj = jjnewStateCnt;
            jjnewStateCnt = bi;
            if (bx == (bi = 6 - bi)) {
                return bc;
            }

            try {
                curChar = ProGuardMappingSimpleCharStream.readChar();
            } catch (IOException iOException) {
                return bc;
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

    public static int jjMoveStringLiteralDfa6_0(long ba) {
        long bb = 33554432L;
        if ((bb = bb & ba) == 0L) {
            return jjStartNfa_0(9, ba);
        }

        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(10, bb);
            return 11;
        }

        byte be;
        switch (curChar) {
            case 'o':
                if ((bb & 33554432L) != 0L) {
                    return jjStopAtPos(11, 25);
                }

                long bc = 26046378827727L;
                be = 10;
                break;
            default:
                long bd = 26046378827727L;
                be = 10;
        }

        Long long1 = bb;
        Integer integer = Integer.valueOf(be);
        return jjStartNfa_0(integer, long1);
    }

    public static ProGuardMappingToken jjFillToken() {
        String string1 = jjstrLiteralImages[jjmatchedKind];
        String string = string1 == null ? ProGuardMappingSimpleCharStream.GetImage() : string1;
        int column = ProGuardMappingSimpleCharStream.getColumn();
        int line = ProGuardMappingSimpleCharStream.getLine();
        int endColumn = ProGuardMappingSimpleCharStream.getEndColumn();
        int endLine = ProGuardMappingSimpleCharStream.getEndLine();
        ProGuardMappingToken proGuardMappingToken = ProGuardMappingToken.newToken(jjmatchedKind, string);
        proGuardMappingToken.b = column;
        proGuardMappingToken.M = endColumn;
        proGuardMappingToken.V = line;
        proGuardMappingToken.C = endLine;
        return proGuardMappingToken;
    }

    public static ProGuardMappingToken getNextToken() {
        ProGuardMappingToken proGuardMappingToken = null;
        int ba = 0;

        label136:
        while (true) {
            try {
                curChar = ProGuardMappingSimpleCharStream.BeginToken();
            } catch (IOException iOException) {
                jjmatchedKind = 0;
                ProGuardMappingToken proGuardMappingToken1 = jjFillToken();
                proGuardMappingToken1.r = proGuardMappingToken;
                return proGuardMappingToken1;
            }

            image = jjimage;
            image.setLength(0);
            jjimageLen = 0;
            int bi = curLexState;

            while (true) {
                switch (bi) {
                    case 0:
                        try {
                            ProGuardMappingSimpleCharStream.backup(0);

                            for (char bg = curChar; bg <= ' ' && (4294971904L & 1L << curChar) != 0L; bg = curChar) {
                                curChar = ProGuardMappingSimpleCharStream.BeginToken();
                            }
                        } catch (IOException iOException3) {
                            continue label136;
                        }

                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_1();
                        if (jjmatchedPos == 0 && jjmatchedKind > 10) {
                            jjmatchedKind = 10;
                        }
                        break;
                    case 2:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_2();
                        if (jjmatchedPos == 0 && jjmatchedKind > 10) {
                            jjmatchedKind = 10;
                        }
                        break;
                    case 3:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_3();
                        if (jjmatchedPos == 0 && jjmatchedKind > 10) {
                            jjmatchedKind = 10;
                        }
                        break;
                    case 4:
                        jjmatchedKind = Integer.MAX_VALUE;
                        jjmatchedPos = 0;
                        ba = jjMoveStringLiteralDfa0_4();
                }

                if (jjmatchedKind == Integer.MAX_VALUE) {
                    break label136;
                }

                long[] bh;
                if (jjmatchedPos + 1 < ba) {
                    ProGuardMappingSimpleCharStream.backup(ba - jjmatchedPos - 1);
                    bh = jjtoToken;
                } else {
                    bh = jjtoToken;
                }

                if ((bh[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 63)) != 0L) {
                    ProGuardMappingToken proGuardMappingToken3 = jjFillToken();
                    proGuardMappingToken3.r = proGuardMappingToken;
                    if (jjnewLexState[jjmatchedKind] != -1) {
                        curLexState = jjnewLexState[jjmatchedKind];
                    }

                    return proGuardMappingToken3;
                }

                if ((jjtoSkip[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 63)) != 0L) {
                    if ((jjtoSpecial[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 63)) != 0L) {
                        ProGuardMappingToken proGuardMappingToken2 = jjFillToken();
                        if (proGuardMappingToken == null) {
                            proGuardMappingToken = proGuardMappingToken2;
                        } else {
                            proGuardMappingToken2.r = proGuardMappingToken;
                            proGuardMappingToken = proGuardMappingToken.U = proGuardMappingToken2;
                        }

                        SkipLexicalActions();
                    } else {
                        SkipLexicalActions();
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
                    curChar = ProGuardMappingSimpleCharStream.readChar();
                } catch (IOException iOException2) {
                    break label136;
                }

                bi = curLexState;
            }
        }

        int endColumn = ProGuardMappingSimpleCharStream.getEndColumn();
        int endLine = ProGuardMappingSimpleCharStream.getEndLine();
        String string = null;
        boolean bl = false;

        try {
            ProGuardMappingSimpleCharStream.readChar();
            ProGuardMappingSimpleCharStream.backup(1);
        } catch (IOException iOException1) {
            bl = true;
            string = ba <= 1 ? "" : ProGuardMappingSimpleCharStream.GetImage();
            if (curChar != '\n' && curChar != '\r') {
                endLine++;
            } else {
                endColumn++;
                endLine = 0;
            }
        }

        if (!bl) {
            ProGuardMappingSimpleCharStream.backup(1);
            string = ba <= 1 ? "" : ProGuardMappingSimpleCharStream.GetImage();
        }

        char bd = curChar;
        String string1 = string;
        int be = endLine;
        int bf = endColumn;
        boolean bl1 = bl;
        throw new ProGuardMappingTokenMgrError(bl1, bf, be, string1, bd);
    }

    public static int jjMoveStringLiteralDfa1_0(long ba) {
        try {
            curChar = ProGuardMappingSimpleCharStream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(0, ba);
            return 1;
        }

        switch (curChar) {
            case '*':
                if ((ba & 64L) != 0L) {
                    return jjStartNfaWithStates_0(1, 6, 0);
                }
                break;
            case '/':
                if ((ba & 16L) != 0L) {
                    return jjStopAtPos(1, 4);
                }
                break;
            case '>':
                if ((ba & 4194304L) != 0L) {
                    return jjStopAtPos(1, 22);
                }
                break;
            case 'a':
                return moveStringLiteralDfaLevel2(ba, 33554432L);
            case 'c':
                return moveStringLiteralDfaLevel2(ba, 16777216L);
            case 'i':
                return moveStringLiteralDfaLevel2(ba, 8388608L);
        }

        return jjStartNfa_0(0, ba);
    }

    public static int jjMoveStringLiteralDfa0_0() {
        switch (curChar) {
            case '\n':
                return jjStopAtPos(0, 12);
            case '\r':
                return jjStopAtPos(0, 13);
            case '"':
                return jjStopAtPos(0, 26);
            case '(':
                return jjStopAtPos(0, 14);
            case ')':
                return jjStopAtPos(0, 15);
            case ',':
                return jjStopAtPos(0, 18);
            case '-':
                return jjMoveStringLiteralDfa1_0(4194304L);
            case '.':
                return jjStopAtPos(0, 20);
            case '/':
                jjmatchedKind = 19;
                return jjMoveStringLiteralDfa1_0(80L);
            case ':':
                return jjStopAtPos(0, 21);
            case '<':
                return jjMoveStringLiteralDfa1_0(25165824L);
            case '[':
                return jjStopAtPos(0, 16);
            case ']':
                return jjStopAtPos(0, 17);
            case 'p':
                return jjMoveStringLiteralDfa1_0(33554432L);
            case '\ufeff':
                return jjStartNfaWithStates_0(0, 11, 5);
            default:
                return jjMoveNfa_0(3, 0);
        }
    }

    public static int jjMoveNfa_4(int ba, int bb) {
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
                int[] bn = jjstateSet;

                while (true) {
                    be += -1;
                    switch (bn[be]) {
                        case 0:
                            if ((-17179882497L & bl) != 0L) {
                                if (bf > 28) {
                                    bf = 28;
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
                            if (curChar == '"' && bf > 28) {
                                bf = 28;
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    bn = jjstateSet;
                }
            } else if (curChar < 128) {
                int[] bm = jjstateSet;

                while (true) {
                    be += -1;
                    switch (bm[be]) {
                        case 0:
                            bf = 28;
                    }

                    if (be == bd) {
                        break;
                    }

                    bm = jjstateSet;
                }
            } else {
                int bg = curChar >> '\b';
                int bh = bg >> 6;
                long bi = 1L << (bg & 63);
                int bj = (curChar & 255) >> 6;
                long bk = 1L << (curChar & '?');
                int[] bp = jjstateSet;

                while (true) {
                    be += -1;
                    switch (bp[be]) {
                        case 0:
                            if (jjCanMove_0(bg, bh, bj, bi, bk) && bf > 28) {
                                bf = 28;
                            }
                    }

                    if (be == bd) {
                        break;
                    }

                    bp = jjstateSet;
                }
            }

            if (bf != Integer.MAX_VALUE) {
                jjmatchedKind = bf;
                jjmatchedPos = bc;
                bf = Integer.MAX_VALUE;
            }

            bc++;
            int bo = be = jjnewStateCnt;
            jjnewStateCnt = bd;
            if (bo == (bd = 3 - bd)) {
                return bc;
            }

            try {
                curChar = ProGuardMappingSimpleCharStream.readChar();
            } catch (IOException iOException) {
                return bc;
            }
        }
    }

    public static void SkipLexicalActions() {
        switch (jjmatchedKind) {
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
                "\ufeff",
                "\n",
                "\r",
                "(",
                ")",
                "[",
                "]",
                ",",
                "/",
                ".",
                ":",
                "->",
                "<init>",
                "<clinit>",
                "package-info",
                null,
                null,
                null,
                null,
                null
        };
        jjnewLexState = new int[]{-1, -1, -1, -1, 1, 2, 3, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 0, -1, -1, -1};
        jjtoToken = new long[]{1811937281L};
        jjtoSkip = new long[]{910L};
        jjtoSpecial = new long[]{896L};
        jjrounds = new int[6];
        jjstateSet = new int[12];
        jjimage = new StringBuilder();
        image = jjimage;
        curLexState = 0;
        defaultLexState = 0;
    }
}
