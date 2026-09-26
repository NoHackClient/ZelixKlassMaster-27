package com.zelix.klassmaster.proguard.mapping.parser;

import com.zelix.klassmaster.proguard.ProGuardMappingParserJJCalls;
import com.zelix.klassmaster.proguard.ProGuardMappingParserLookaheadSuccess;
import com.zelix.klassmaster.proguard.ProGuardMappingTreeState;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ASTClassBlock;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ASTClassNameChange;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTArrayLevel;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTFieldNameChange;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTInput;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTMethodArguments;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTMethodName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTMethodNameChange;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTMethodSignature;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTNameList;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTNewClassName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTNewFieldName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTNewMethodName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTOldClassName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTOldFieldName;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTQualifiedType;
import com.zelix.klassmaster.proguard.mapping.parser.ast.ProGuardMappingASTType;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProGuardMappingParser implements ProGuardMappingTreeConstants, ProGuardMappingConstants {
    public static ProGuardMappingToken jj_lastpos;
    public static ProGuardMappingSimpleCharStream jj_input_stream;
    public static ProGuardMappingParser parserInstance;
    public static int[] jj_expentry;
    public static ProGuardMappingToken jj_scanpos;
    public static int jj_la;
    public static int jj_endpos;
    public static ProGuardMappingToken token;
    public static int jj_gen;
    public static int[] jj_la1_0;
    public static ProGuardMappingToken nextToken;
    private static final String SECOND_CONSTRUCTOR_ERROR = "ERROR: Second call to constructor of static parser. ";
    public static ProGuardMappingTreeState jjtree = new ProGuardMappingTreeState();
    public static boolean jj_initialized_once = false;
    public static final int[] jj_la1 = new int[15];
    public static final ProGuardMappingParserJJCalls[] jj_2_rtns;
    public static boolean jj_rescan;
    public static int jj_gc;
    public static final ProGuardMappingParserLookaheadSuccess jj_ls;
    public static List jj_expentries;
    public static int jj_kind;
    public static int[] jj_lasttokens;

    public static final void ArrayLevel() throws ProGuardMappingParseException {
        ProGuardMappingASTArrayLevel proGuardMappingASTArrayLevel = new ProGuardMappingASTArrayLevel();
        jjtree.openNodeScope(proGuardMappingASTArrayLevel);

        try {
            jj_consume_token(LBRACKET);
            jj_consume_token(RBRACKET);
        } finally {
            jjtree.closeNodeScope(proGuardMappingASTArrayLevel);
        }
    }

    public static boolean jj_3R_1() {
        if (jj_3R_6()) {
            return true;
        }

        if (jj_3R_13()) {
            return true;
        }

        if (jj_scan_token(ARROW)) {
            return true;
        }

        if (jj_3R_13()) {
            return true;
        }

        ProGuardMappingToken proGuardMappingToken = jj_scanpos;
        if (jj_scan_token(LF)) {
            jj_scanpos = proGuardMappingToken;
            if (jj_scan_token(CR)) {
                return true;
            }
        }

        return false;
    }

    public static boolean jj_3R_2() {
        ProGuardMappingToken proGuardMappingToken = jj_scanpos;
        if (jj_3R_8()) {
            jj_scanpos = proGuardMappingToken;
            if (jj_3R_12()) {
                jj_scanpos = proGuardMappingToken;
                if (jj_3R_3()) {
                    jj_scanpos = proGuardMappingToken;
                    if (jj_3R_14()) {
                        jj_scanpos = proGuardMappingToken;
                        if (jj_3R_9()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean jj_3R_3() {
        return jj_scan_token(INIT);
    }

    public static boolean jj_3R_4() {
        return jj_scan_token(INTEGER_LITERAL);
    }

    public static boolean jj_2_1() {
        jj_la = Integer.MAX_VALUE;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3_1();
        } catch (ProGuardMappingParserLookaheadSuccess proGuardMappingParserLookaheadSuccess) {
        } finally {
            jj_save(1, Integer.MAX_VALUE);
        }

        return true;
    }

    public static ProGuardMappingParseException generateParseException() {
        jj_expentries.clear();
        boolean[] bl = new boolean[31];
        if (jj_kind >= 0) {
            bl[jj_kind] = true;
            jj_kind = -1;
        }

        int ba = 0;
        int bg = ba;

        for (byte bh = 15; bg < bh; bh = 15) {
            if (jj_la1[ba] == jj_gen) {
                int bb = 0;
                bg = bb;

                for (byte bf = 32; bg < bf; bf = 32) {
                    if ((jj_la1_0[ba] & 1 << bb) != 0) {
                        bl[bb] = true;
                    }

                    bg = ++bb;
                }
            }

            bg = ++ba;
        }

        for (int i = 0; i < 31; i++) {
            if (bl[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.add(jj_expentry);
            }
        }

        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int[][] bd = new int[jj_expentries.size()][];
        int be = 0;
        bg = 0;

        for (List list1 = jj_expentries; bg < list1.size(); list1 = jj_expentries) {
            bd[be] = (int[]) jj_expentries.get(be);
            bg = ++be;
        }

        return new ProGuardMappingParseException(token, bd, ProGuardMappingConstants.TOKEN_IMAGE);
    }

    public static final void ClassNameChange() throws ProGuardMappingParseException {
        ASTClassNameChange aSTClassNameChange = new ASTClassNameChange();
        boolean bl = true;
        jjtree.openNodeScope(aSTClassNameChange);
        try {
            ProGuardMappingParser.OldClassName();
            ProGuardMappingParser.jj_consume_token(22);
            ProGuardMappingParser.NewClassName();
            ProGuardMappingParser.jj_consume_token(21);
            jjtree.closeNodeScope(aSTClassNameChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTClassNameChange);
                }
                throw throwable2;
            }
        }
    }

    public static void jj_rescan_token() {
        jj_rescan = true;

        for (int i = 0; i < 2; i++) {
            try {
                ProGuardMappingParserJJCalls proGuardMappingParserJJCalls = jj_2_rtns[i];
                int bb = proGuardMappingParserJJCalls.gen;

                while (true) {
                    ProGuardMappingParserJJCalls proGuardMappingParserJJCalls1;
                    if (bb > jj_gen) {
                        jj_la = proGuardMappingParserJJCalls.arg;
                        jj_lastpos = jj_scanpos = proGuardMappingParserJJCalls.first;
                        switch (i) {
                            case 0:
                                jj_3R_1();
                                proGuardMappingParserJJCalls1 = proGuardMappingParserJJCalls.next;
                                break;
                            case 1:
                                jj_3_1();
                                proGuardMappingParserJJCalls1 = proGuardMappingParserJJCalls.next;
                                break;
                            default:
                                proGuardMappingParserJJCalls1 = proGuardMappingParserJJCalls.next;
                        }
                    } else {
                        proGuardMappingParserJJCalls1 = proGuardMappingParserJJCalls.next;
                    }

                    proGuardMappingParserJJCalls = proGuardMappingParserJJCalls1;
                    if (proGuardMappingParserJJCalls == null) {
                        break;
                    }

                    bb = proGuardMappingParserJJCalls.gen;
                }
            } catch (ProGuardMappingParserLookaheadSuccess proGuardMappingParserLookaheadSuccess) {
            }
        }

        jj_rescan = false;
    }

    public static boolean jj_3_1() {
        if (jj_3R_6()) {
            return true;
        } else {
            return jj_3R_2() ? true : jj_scan_token(LPAREN);
        }
    }

    public static boolean jj_3R_5() {
        return jj_scan_token(NAME);
    }

    public static boolean jj_3R_6() {
        if (jj_3R_5()) {
            return true;
        }

        ProGuardMappingToken proGuardMappingToken1 = jj_scanpos;

        while (true) {
            ProGuardMappingToken proGuardMappingToken = proGuardMappingToken1;
            if (jj_3_2()) {
                jj_scanpos = proGuardMappingToken;
                proGuardMappingToken1 = jj_scanpos;

                while (true) {
                    proGuardMappingToken = proGuardMappingToken1;
                    if (jj_3R_10()) {
                        jj_scanpos = proGuardMappingToken;
                        return false;
                    }

                    proGuardMappingToken1 = jj_scanpos;
                }
            }

            proGuardMappingToken1 = jj_scanpos;
        }
    }

    public static boolean jj_3R_7() {
        return jj_scan_token(LBRACKET) ? true : jj_scan_token(RBRACKET);
    }

    public static boolean jj_3R_8() {
        return jj_scan_token(NAME);
    }

    public static void jj_add_error_token(int ba, int bb) {
        if (bb < 100) {
            if (bb == jj_endpos + 1) {
                jj_lasttokens[jj_endpos++] = ba;
            } else if (jj_endpos != 0) {
                jj_expentry = new int[jj_endpos];
                int bc = 0;
                int bh = bc;

                for (int i = jj_endpos; bh < i; i = jj_endpos) {
                    jj_expentry[bc] = jj_lasttokens[bc];
                    bh = ++bc;
                }

                Iterator iterator = jj_expentries.iterator();

                label45:
                while (iterator.hasNext()) {
                    int[] bd = (int[]) iterator.next();
                    if (bd.length == jj_expentry.length) {
                        int be = 0;
                        bh = be;

                        for (int[] bg = jj_expentry; bh < bg.length; bg = jj_expentry) {
                            if (bd[be] != jj_expentry[be]) {
                                continue label45;
                            }

                            bh = ++be;
                        }

                        jj_expentries.add(jj_expentry);
                        break;
                    }
                }

                if (bb != 0) {
                    int[] bf = jj_lasttokens;
                    jj_endpos = bb;
                    bf[bb - 1] = ba;
                }
            }
        }
    }

    public static final void Name() throws ProGuardMappingParseException {
        ProGuardMappingASTName proGuardMappingASTName = new ProGuardMappingASTName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTName);

        try {
            switch (nextToken.m) {
                case 25:
                    ProGuardMappingToken proGuardMappingToken2 = jj_consume_token(PACKAGE_INFO);
                    jjtree.closeNodeScope(proGuardMappingASTName);
                    bl = false;
                    proGuardMappingASTName.setName(proGuardMappingToken2.L);
                    break;
                case 29:
                    ProGuardMappingToken proGuardMappingToken1 = jj_consume_token(INTEGER_LITERAL);
                    jjtree.closeNodeScope(proGuardMappingASTName);
                    bl = false;
                    proGuardMappingASTName.setName(proGuardMappingToken1.L);
                    break;
                case 30:
                    ProGuardMappingToken proGuardMappingToken = jj_consume_token(NAME);
                    jjtree.closeNodeScope(proGuardMappingASTName);
                    bl = false;
                    proGuardMappingASTName.setName(proGuardMappingToken.L);
                    break;
                default:
                    jj_la1[14] = jj_gen;
                    jj_consume_token(-1);
                    throw new ProGuardMappingParseException();
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(proGuardMappingASTName);
            }
        }
    }

    




    public static final void QualifiedType() throws ProGuardMappingParseException {
        ProGuardMappingASTQualifiedType proGuardMappingASTQualifiedType = new ProGuardMappingASTQualifiedType();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTQualifiedType);
        try {
            ProGuardMappingParser.Type();
            ProGuardMappingToken proGuardMappingToken = nextToken;
            block10:
            while (true) {
                switch (proGuardMappingToken.m) {
                    case 20: {
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[11] = jj_gen;
                        break block10;
                    }
                }
                ProGuardMappingParser.jj_consume_token(20);
                ProGuardMappingParser.Type();
                proGuardMappingToken = nextToken;
            }
            ProGuardMappingToken proGuardMappingToken2 = nextToken;
            block11:
            while (true) {
                switch (proGuardMappingToken2.m) {
                    case 16: {
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[12] = jj_gen;
                        break block11;
                    }
                }
                ProGuardMappingParser.ArrayLevel();
                proGuardMappingToken2 = nextToken;
            }
            jjtree.closeNodeScope(proGuardMappingASTQualifiedType);
            return;
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardMappingParseException)) throw (Error) throwable;
                throw (ProGuardMappingParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                jjtree.closeNodeScope(proGuardMappingASTQualifiedType);
                throw throwable2;
            }
        }
    }

    




    public static final void ClassBlock() throws ProGuardMappingParseException {
        ASTClassBlock aSTClassBlock = new ASTClassBlock();
        boolean bl = true;
        jjtree.openNodeScope(aSTClassBlock);
        try {
            Integer n;
            ProGuardMappingParser.ClassNameChange();
            ProGuardMappingToken proGuardMappingToken = nextToken;
            block25:
            while (true) {
                switch (proGuardMappingToken.m) {
                    case 12: {
                        ProGuardMappingParser.jj_consume_token(12);
                        break;
                    }
                    case 13: {
                        ProGuardMappingParser.jj_consume_token(13);
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[2] = jj_gen;
                        ProGuardMappingParser.jj_consume_token(-1);
                        throw new ProGuardMappingParseException();
                    }
                }
                switch (ProGuardMappingParser.nextToken.m) {
                    case 12:
                    case 13: {
                        proGuardMappingToken = nextToken;
                        continue block25;
                    }
                }
                break;
            }
            ProGuardMappingParser.jj_la1[3] = jj_gen;
            long l = 102863502202123L;
            int n2 = Integer.MAX_VALUE;
            while (ProGuardMappingParser.jj_2_2(n = Integer.valueOf(n2))) {
                ProGuardMappingParser.FieldNameChange();
                ProGuardMappingToken proGuardMappingToken2 = nextToken;
                block27:
                while (true) {
                    switch (proGuardMappingToken2.m) {
                        case 12: {
                            ProGuardMappingParser.jj_consume_token(12);
                            break;
                        }
                        case 13: {
                            ProGuardMappingParser.jj_consume_token(13);
                            break;
                        }
                        default: {
                            ProGuardMappingParser.jj_la1[4] = jj_gen;
                            ProGuardMappingParser.jj_consume_token(-1);
                            throw new ProGuardMappingParseException();
                        }
                    }
                    switch (ProGuardMappingParser.nextToken.m) {
                        case 12:
                        case 13: {
                            proGuardMappingToken2 = nextToken;
                            continue block27;
                        }
                    }
                    break;
                }
                ProGuardMappingParser.jj_la1[5] = jj_gen;
                l = 102863502202123L;
                n2 = Integer.MAX_VALUE;
            }
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardMappingParseException)) throw (Error) throwable;
                throw (ProGuardMappingParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                jjtree.closeNodeScope(aSTClassBlock);
                throw throwable2;
            }
        }
        while (true) {
            if (!ProGuardMappingParser.jj_2_1()) {
                jjtree.closeNodeScope(aSTClassBlock);
                return;
            }
            ProGuardMappingParser.MethodNameChange();
            ProGuardMappingToken proGuardMappingToken = nextToken;
            block29:
            while (true) {
                switch (proGuardMappingToken.m) {
                    case 12: {
                        ProGuardMappingParser.jj_consume_token(12);
                        break;
                    }
                    case 13: {
                        ProGuardMappingParser.jj_consume_token(13);
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[6] = jj_gen;
                        ProGuardMappingParser.jj_consume_token(-1);
                        throw new ProGuardMappingParseException();
                    }
                }
                switch (ProGuardMappingParser.nextToken.m) {
                    case 12:
                    case 13: {
                        proGuardMappingToken = nextToken;
                        continue block29;
                    }
                }
                break;
            }
            ProGuardMappingParser.jj_la1[7] = jj_gen;
        }
    }

    public static boolean jj_scan_token(int ba) {
        if (jj_scanpos == jj_lastpos) {
            jj_la--;
            if (jj_scanpos.U == null) {
                jj_lastpos = jj_scanpos = jj_scanpos.U = ProGuardMappingTokenManager.getNextToken();
            } else {
                jj_lastpos = jj_scanpos = jj_scanpos.U;
            }
        } else {
            jj_scanpos = jj_scanpos.U;
        }

        ProGuardMappingToken proGuardMappingToken1;
        if (jj_rescan) {
            int bb = 0;

            ProGuardMappingToken proGuardMappingToken;
            for (proGuardMappingToken = token; proGuardMappingToken != null && proGuardMappingToken != jj_scanpos; proGuardMappingToken = proGuardMappingToken.U) {
                bb++;
            }

            if (proGuardMappingToken != null) {
                jj_add_error_token(ba, bb);
                proGuardMappingToken1 = jj_scanpos;
            } else {
                proGuardMappingToken1 = jj_scanpos;
            }
        } else {
            proGuardMappingToken1 = jj_scanpos;
        }

        if (proGuardMappingToken1.m != ba) {
            return true;
        } else if (jj_la == 0 && jj_scanpos == jj_lastpos) {
            throw jj_ls;
        } else {
            return false;
        }
    }

    public static boolean jj_3R_9() {
        return jj_scan_token(PACKAGE_INFO);
    }

    public static final void MethodNameChange() throws ProGuardMappingParseException {
        ProGuardMappingASTMethodNameChange proGuardMappingASTMethodNameChange = new ProGuardMappingASTMethodNameChange();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTMethodNameChange);
        try {
            ProGuardMappingParser.QualifiedType();
            ProGuardMappingParser.MethodSignature();
            ProGuardMappingParser.jj_consume_token(22);
            ProGuardMappingParser.NewMethodName();
            jjtree.closeNodeScope(proGuardMappingASTMethodNameChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTMethodNameChange);
                }
                throw throwable2;
            }
        }
    }

    public static final void Type() throws ProGuardMappingParseException {
        ProGuardMappingASTType proGuardMappingASTType = new ProGuardMappingASTType();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTType);

        try {
            ProGuardMappingToken proGuardMappingToken = jj_consume_token(NAME);
            jjtree.closeNodeScope(proGuardMappingASTType);
            bl = false;
            proGuardMappingASTType.setName(proGuardMappingToken.L);
        } finally {
            if (bl) {
                jjtree.closeNodeScope(proGuardMappingASTType);
            }
        }
    }

    public static boolean jj_3R_10() {
        return jj_3R_7();
    }

    public static boolean jj_3R_11() {
        return jj_scan_token(PACKAGE_INFO);
    }

    public static final void FieldNameChange() throws ProGuardMappingParseException {
        ProGuardMappingASTFieldNameChange proGuardMappingASTFieldNameChange = new ProGuardMappingASTFieldNameChange();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTFieldNameChange);
        try {
            ProGuardMappingParser.QualifiedType();
            ProGuardMappingParser.OldFieldName();
            ProGuardMappingParser.jj_consume_token(22);
            ProGuardMappingParser.NewFieldName();
            jjtree.closeNodeScope(proGuardMappingASTFieldNameChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTFieldNameChange);
                }
                throw throwable2;
            }
        }
    }

    public static final void NewClassName() throws ProGuardMappingParseException {
        ProGuardMappingASTNewClassName proGuardMappingASTNewClassName = new ProGuardMappingASTNewClassName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTNewClassName);
        try {
            ProGuardMappingParser.NameList();
            jjtree.closeNodeScope(proGuardMappingASTNewClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTNewClassName);
                }
                throw throwable2;
            }
        }
    }

    public static void jj_la1_init_0() {
        jj_la1_0 = new int[]{2048, 1644167168, 12288, 12288, 12288, 12288, 12288, 12288, 1048576, 1073741824, 262144, 1048576, 65536, 1669332992, 1644167168};
    }


    static {
        jj_la1_init_0();
        jj_2_rtns = new ProGuardMappingParserJJCalls[2];
        jj_rescan = false;
        jj_gc = 0;
        jj_ls = new ProGuardMappingParserLookaheadSuccess(null);
        jj_expentries = new ArrayList();
        jj_kind = -1;
        jj_lasttokens = new int[100];
    }


    public static boolean jj_3R_12() {
        return jj_scan_token(INTEGER_LITERAL);
    }

    public static final void MethodSignature() throws ProGuardMappingParseException {
        ProGuardMappingASTMethodSignature proGuardMappingASTMethodSignature = new ProGuardMappingASTMethodSignature();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTMethodSignature);
        try {
            int n;
            block10:
            {
                ProGuardMappingParser.MethodName();
                ProGuardMappingParser.jj_consume_token(14);
                switch (ProGuardMappingParser.nextToken.m) {
                    case 30: {
                        ProGuardMappingParser.MethodArguments();
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[9] = jj_gen;
                        n = 15;
                        break block10;
                    }
                }
                n = 15;
            }
            ProGuardMappingParser.jj_consume_token(n);
            jjtree.closeNodeScope(proGuardMappingASTMethodSignature);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTMethodSignature);
                }
                throw throwable2;
            }
        }
    }

    public static final void NewMethodName() throws ProGuardMappingParseException {
        ProGuardMappingASTNewMethodName proGuardMappingASTNewMethodName = new ProGuardMappingASTNewMethodName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTNewMethodName);
        try {
            ProGuardMappingParser.MethodName();
            jjtree.closeNodeScope(proGuardMappingASTNewMethodName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTNewMethodName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_2_2(int ba) {
        jj_la = ba;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3R_1();
        } catch (ProGuardMappingParserLookaheadSuccess proGuardMappingParserLookaheadSuccess) {
        } finally {
            jj_save(0, ba);
        }

        return true;
    }

    public static boolean jj_3R_13() {
        ProGuardMappingToken proGuardMappingToken = jj_scanpos;
        if (jj_3R_15()) {
            jj_scanpos = proGuardMappingToken;
            if (jj_3R_4()) {
                jj_scanpos = proGuardMappingToken;
                if (jj_3R_11()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static final ProGuardMappingSimpleNode Input() throws ProGuardMappingParseException {
        ProGuardMappingASTInput proGuardMappingASTInput = new ProGuardMappingASTInput();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTInput);

        try {
            ProGuardMappingToken proGuardMappingToken;
            switch (nextToken.m) {
                case 11:
                    jj_consume_token(BOM);
                    proGuardMappingToken = nextToken;
                    break;
                default:
                    jj_la1[0] = jj_gen;
                    proGuardMappingToken = nextToken;
            }

            while (true) {
                switch (proGuardMappingToken.m) {
                    case 25:
                    case 29:
                    case 30:
                        ClassBlock();
                        proGuardMappingToken = nextToken;
                        break;
                    default:
                        jj_la1[1] = jj_gen;
                        jj_consume_token(EOF);
                        jjtree.closeNodeScope(proGuardMappingASTInput);
                        bl = false;
                        return proGuardMappingASTInput;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                jjtree.clearNodeScope();
                bl = false;
            } else {
                jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardMappingParseException) {
                throw (ProGuardMappingParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(proGuardMappingASTInput);
            }
        }
    }

    public static final void NewFieldName() throws ProGuardMappingParseException {
        ProGuardMappingASTNewFieldName proGuardMappingASTNewFieldName = new ProGuardMappingASTNewFieldName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTNewFieldName);
        try {
            ProGuardMappingParser.Name();
            jjtree.closeNodeScope(proGuardMappingASTNewFieldName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTNewFieldName);
                }
                throw throwable2;
            }
        }
    }

    public static final void OldFieldName() throws ProGuardMappingParseException {
        ProGuardMappingASTOldFieldName proGuardMappingASTOldFieldName = new ProGuardMappingASTOldFieldName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTOldFieldName);
        try {
            ProGuardMappingParser.Name();
            jjtree.closeNodeScope(proGuardMappingASTOldFieldName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTOldFieldName);
                }
                throw throwable2;
            }
        }
    }

    




    public static final void MethodArguments() throws ProGuardMappingParseException {
        ProGuardMappingASTMethodArguments proGuardMappingASTMethodArguments = new ProGuardMappingASTMethodArguments();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTMethodArguments);
        try {
            ProGuardMappingParser.QualifiedType();
            ProGuardMappingToken proGuardMappingToken = nextToken;
            block7:
            while (true) {
                switch (proGuardMappingToken.m) {
                    case 18: {
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[10] = jj_gen;
                        break block7;
                    }
                }
                ProGuardMappingParser.jj_consume_token(18);
                ProGuardMappingParser.QualifiedType();
                proGuardMappingToken = nextToken;
            }
            jjtree.closeNodeScope(proGuardMappingASTMethodArguments);
            return;
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardMappingParseException)) throw (Error) throwable;
                throw (ProGuardMappingParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                jjtree.closeNodeScope(proGuardMappingASTMethodArguments);
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_14() {
        return jj_scan_token(CLINIT);
    }

    public static ProGuardMappingToken jj_consume_token(int ba) throws ProGuardMappingParseException {
        ProGuardMappingToken proGuardMappingToken = token;
        if ((token = nextToken).U != null) {
            nextToken = nextToken.U;
        } else {
            nextToken = nextToken.U = ProGuardMappingTokenManager.getNextToken();
        }

        if (token.m != ba) {
            nextToken = token;
            token = proGuardMappingToken;
            jj_kind = ba;
            throw generateParseException();
        }

        jj_gen++;
        if (++jj_gc > 100) {
            jj_gc = 0;

            for (int i = 0; i < jj_2_rtns.length; i++) {
                ProGuardMappingParserJJCalls proGuardMappingParserJJCalls = jj_2_rtns[i];

                while (proGuardMappingParserJJCalls != null) {
                    ProGuardMappingParserJJCalls proGuardMappingParserJJCalls1;
                    if (proGuardMappingParserJJCalls.gen < jj_gen) {
                        proGuardMappingParserJJCalls.first = null;
                        proGuardMappingParserJJCalls1 = proGuardMappingParserJJCalls.next;
                    } else {
                        proGuardMappingParserJJCalls1 = proGuardMappingParserJJCalls.next;
                    }

                    proGuardMappingParserJJCalls = proGuardMappingParserJJCalls1;
                }
            }
        }

        return token;
    }

    public static void jj_save(int ba, int bb) {
        ProGuardMappingParserJJCalls proGuardMappingParserJJCalls = jj_2_rtns[ba];

        for (int i = proGuardMappingParserJJCalls.gen; i > jj_gen; i = proGuardMappingParserJJCalls.gen) {
            if (proGuardMappingParserJJCalls.next == null) {
                proGuardMappingParserJJCalls = proGuardMappingParserJJCalls.next = new ProGuardMappingParserJJCalls();
                break;
            }

            proGuardMappingParserJJCalls = proGuardMappingParserJJCalls.next;
        }

        proGuardMappingParserJJCalls.gen = jj_gen + bb - jj_la;
        proGuardMappingParserJJCalls.first = token;
        proGuardMappingParserJJCalls.arg = bb;
    }

    




    public static final void NameList() throws ProGuardMappingParseException {
        ProGuardMappingASTNameList proGuardMappingASTNameList = new ProGuardMappingASTNameList();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTNameList);
        try {
            ProGuardMappingParser.Name();
            ProGuardMappingToken proGuardMappingToken = nextToken;
            block7:
            while (true) {
                switch (proGuardMappingToken.m) {
                    case 20: {
                        break;
                    }
                    default: {
                        ProGuardMappingParser.jj_la1[8] = jj_gen;
                        break block7;
                    }
                }
                ProGuardMappingParser.jj_consume_token(20);
                ProGuardMappingParser.Name();
                proGuardMappingToken = nextToken;
            }
            jjtree.closeNodeScope(proGuardMappingASTNameList);
            return;
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardMappingParseException)) throw (Error) throwable;
                throw (ProGuardMappingParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                jjtree.closeNodeScope(proGuardMappingASTNameList);
                throw throwable2;
            }
        }
    }

    public static final void MethodName() throws ProGuardMappingParseException {
        ProGuardMappingASTMethodName proGuardMappingASTMethodName = new ProGuardMappingASTMethodName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTMethodName);

        try {
            switch (nextToken.m) {
                case 23:
                    ProGuardMappingToken proGuardMappingToken4 = jj_consume_token(INIT);
                    jjtree.closeNodeScope(proGuardMappingASTMethodName);
                    bl = false;
                    proGuardMappingASTMethodName.setName(proGuardMappingToken4.L);
                    break;
                case 24:
                    ProGuardMappingToken proGuardMappingToken3 = jj_consume_token(CLINIT);
                    jjtree.closeNodeScope(proGuardMappingASTMethodName);
                    bl = false;
                    proGuardMappingASTMethodName.setName(proGuardMappingToken3.L);
                    break;
                case 25:
                    ProGuardMappingToken proGuardMappingToken2 = jj_consume_token(PACKAGE_INFO);
                    jjtree.closeNodeScope(proGuardMappingASTMethodName);
                    bl = false;
                    proGuardMappingASTMethodName.setName(proGuardMappingToken2.L);
                    break;
                case 26:
                case 27:
                case 28:
                default:
                    jj_la1[13] = jj_gen;
                    jj_consume_token(-1);
                    throw new ProGuardMappingParseException();
                case 29:
                    ProGuardMappingToken proGuardMappingToken1 = jj_consume_token(INTEGER_LITERAL);
                    jjtree.closeNodeScope(proGuardMappingASTMethodName);
                    bl = false;
                    proGuardMappingASTMethodName.setName(proGuardMappingToken1.L);
                    break;
                case 30:
                    ProGuardMappingToken proGuardMappingToken = jj_consume_token(NAME);
                    jjtree.closeNodeScope(proGuardMappingASTMethodName);
                    bl = false;
                    proGuardMappingASTMethodName.setName(proGuardMappingToken.L);
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(proGuardMappingASTMethodName);
            }
        }
    }

    public static final void OldClassName() throws ProGuardMappingParseException {
        ProGuardMappingASTOldClassName proGuardMappingASTOldClassName = new ProGuardMappingASTOldClassName();
        boolean bl = true;
        jjtree.openNodeScope(proGuardMappingASTOldClassName);
        try {
            ProGuardMappingParser.NameList();
            jjtree.closeNodeScope(proGuardMappingASTOldClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardMappingParseException) {
                    throw (ProGuardMappingParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(proGuardMappingASTOldClassName);
                }
                throw throwable2;
            }
        }
    }

    public static void ReInit(Reader reader1) {
        jj_input_stream.ReInit(reader1);
        ProGuardMappingTokenManager.ReInit(jj_input_stream);
        token = new ProGuardMappingToken();
        token.U = nextToken = ProGuardMappingTokenManager.getNextToken();
        jjtree.reset();
        jj_gen = 0;
        int ba = 0;
        int bc = 0;

        for (byte bd = 15; bc < bd; bd = 15) {
            jj_la1[ba] = -1;
            bc = ++ba;
        }

        for (int i = 0; i < jj_2_rtns.length; i++) {
            jj_2_rtns[i] = new ProGuardMappingParserJJCalls();
        }
    }

    public static boolean jj_3R_15() {
        return jj_scan_token(NAME);
    }

    public static boolean jj_3_2() {
        return jj_scan_token(DOT) ? true : jj_3R_5();
    }

    public ProGuardMappingParser(Reader reader1) {
        if (jj_initialized_once) {
            System.out.println(SECOND_CONSTRUCTOR_ERROR);
            throw new Error();
        }

        jj_initialized_once = true;
        jj_input_stream = new ProGuardMappingSimpleCharStream(reader1);
        new ProGuardMappingTokenManager(jj_input_stream);
        token = new ProGuardMappingToken();
        token.U = nextToken = ProGuardMappingTokenManager.getNextToken();
        jj_gen = 0;
        int ba = 0;
        int bc = 0;

        for (byte bd = 15; bc < bd; bd = 15) {
            jj_la1[ba] = -1;
            bc = ++ba;
        }

        for (int i = 0; i < jj_2_rtns.length; i++) {
            jj_2_rtns[i] = new ProGuardMappingParserJJCalls();
        }
    }
}
