package com.zelix.klassmaster.changelog.parser;

import com.zelix.klassmaster.changelog.ChangeLogParserJJCalls;
import com.zelix.klassmaster.changelog.ChangeLogParserLookaheadSuccess;
import com.zelix.klassmaster.changelog.JJTChangeLogParserState;
import com.zelix.klassmaster.changelog.parser.ast.ASTAlphanumeric;
import com.zelix.klassmaster.changelog.parser.ast.ASTAutoReflectionClassName;
import com.zelix.klassmaster.changelog.parser.ast.ASTClassChange;
import com.zelix.klassmaster.changelog.parser.ast.ASTDisregardedNameList;
import com.zelix.klassmaster.changelog.parser.ast.ASTFieldData;
import com.zelix.klassmaster.changelog.parser.ast.ASTFieldType;
import com.zelix.klassmaster.changelog.parser.ast.ASTFlowObfuscationClassName;
import com.zelix.klassmaster.changelog.parser.ast.ASTFlowObfuscationPackageName;
import com.zelix.klassmaster.changelog.parser.ast.ASTInteger;
import com.zelix.klassmaster.changelog.parser.ast.ASTLineNumberChange;
import com.zelix.klassmaster.changelog.parser.ast.ASTMainFlowObfuscationData;
import com.zelix.klassmaster.changelog.parser.ast.ASTManufactured;
import com.zelix.klassmaster.changelog.parser.ast.ASTMemberData;
import com.zelix.klassmaster.changelog.parser.ast.ASTMemberFlowObfuscationData;
import com.zelix.klassmaster.changelog.parser.ast.ASTMethodParameterClassNames;
import com.zelix.klassmaster.changelog.parser.ast.ASTMethodReturnType;
import com.zelix.klassmaster.changelog.parser.ast.ASTModuleChange;
import com.zelix.klassmaster.changelog.parser.ast.ASTNewLineNumber;
import com.zelix.klassmaster.changelog.parser.ast.ASTNewMethodSignature;
import com.zelix.klassmaster.changelog.parser.ast.ASTNewPackageName;
import com.zelix.klassmaster.changelog.parser.ast.ASTObfuscateReferencesClassName;
import com.zelix.klassmaster.changelog.parser.ast.ASTOldLineNumber;
import com.zelix.klassmaster.changelog.parser.ast.ASTOldModuleName;
import com.zelix.klassmaster.changelog.parser.ast.ASTOldPackageName;
import com.zelix.klassmaster.changelog.parser.ast.ASTPackageChange;
import com.zelix.klassmaster.changelog.parser.ast.ASTPackageFlowObfuscationData;
import com.zelix.klassmaster.changelog.parser.ast.ASTPackageNameNotChanged;
import com.zelix.klassmaster.changelog.parser.ast.ASTParameterChangeData;
import com.zelix.klassmaster.changelog.parser.ast.ASTParameterChangeLookupData;
import com.zelix.klassmaster.changelog.parser.ast.ASTParameterObfuscated;
import com.zelix.klassmaster.changelog.parser.ast.ASTSourceName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTArrayLevel;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTClassModifier;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTFieldNameChange;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTInput;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTMemberModifier;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTMethodArguments;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTMethodNameChange;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTMethodSignature;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTNameList;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTNewClassName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTNewFieldName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTNewMethodName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTOldClassName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTOldFieldName;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTQualifiedType;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTStringLiteral;
import com.zelix.klassmaster.changelog.parser.ast.ChangeLogASTType;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChangeLogParser implements ChangeLogTreeConstants, ChangeLogConstants {
    public static int jj_endpos;
    public static int[] jj_la1_2;
    private static ChangeLogToken jj_lastpos;
    public static int[] jj_la1_0;
    public static ChangeLogToken jj_nt;
    public static ChangeLogToken token;
    public static ChangeLogParser instance;
    public static int[] jj_la1_1;
    private static int jj_gen;
    public static int[] jj_expentry;
    private static ChangeLogToken jj_scanpos;
    private static int jj_la;
    public static ChangeLogSimpleCharStream jj_input_stream;
    public static JJTChangeLogParserState jjtree = new JJTChangeLogParserState();
    public static boolean jj_initialized_once = false;
    private static final int[] jj_la1 = new int[60];
    private static final ChangeLogParserJJCalls[] jj_2_rtns;
    private static boolean jj_rescan;
    private static int jj_gc;
    public static final ChangeLogParserLookaheadSuccess jj_ls;
    public static List jj_expentries;
    public static int jj_kind;
    public static int[] jj_lasttokens;

    public static boolean jj_3R_1() {
        return jj_scan_token(VOLATILE);
    }

    public static boolean jj_3R_2() {
        return jj_scan_token(BRIDGE);
    }

    public static boolean jj_2_1() {
        jj_la = 3;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3R_26();
        } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
        } finally {
            jj_save(0, 3);
        }

        return true;
    }

    public static boolean jj_3R_3() {
        return jj_scan_token(ANNOTATION);
    }

    public static boolean jj_3R_4() {
        return jj_scan_token(PRIVATE);
    }

    public static final void MethodArguments() throws ChangeLogParseException {
        ChangeLogASTMethodArguments changeLogASTMethodArguments = new ChangeLogASTMethodArguments();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTMethodArguments);
        try {
            ChangeLogParser.QualifiedType();
            block7:
            while (true) {
                switch (ChangeLogParser.jj_nt.H) {
                    case 20: {
                        break;
                    }
                    default: {
                        ChangeLogParser.jj_la1[46] = jj_gen;
                        break block7;
                    }
                }
                ChangeLogParser.jj_consume_token(20);
                ChangeLogParser.QualifiedType();
            }
            jjtree.closeNodeScope(changeLogASTMethodArguments);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTMethodArguments);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_5() {
        return jj_scan_token(FALSE);
    }

    public static boolean jj_3_1() {
        return jj_3R_43() ? true : jj_scan_token(LPAREN);
    }

    public static boolean jj_3R_6() {
        return jj_scan_token(FINAL);
    }

    public static boolean jj_3R_7() {
        return jj_3R_57();
    }

    public static final void OldFieldName() throws ChangeLogParseException {
        ChangeLogASTOldFieldName changeLogASTOldFieldName = new ChangeLogASTOldFieldName();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTOldFieldName);
        try {
            ChangeLogParser.Name();
            jjtree.closeNodeScope(changeLogASTOldFieldName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTOldFieldName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_8() {
        return jj_scan_token(28);
    }

    public static final void MainFlowObfuscationData() throws ChangeLogParseException {
        ASTMainFlowObfuscationData aSTMainFlowObfuscationData = new ASTMainFlowObfuscationData();
        boolean bl = true;
        jjtree.openNodeScope(aSTMainFlowObfuscationData);
        try {
            ChangeLogParser.jj_consume_token(66);
            ChangeLogParser.FlowObfuscationClassName();
            switch (ChangeLogParser.jj_nt.H) {
                case 29: {
                    ChangeLogParser.jj_consume_token(29);
                    ChangeLogParser.FieldData();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[54] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTMainFlowObfuscationData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTMainFlowObfuscationData);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_9() {
        return jj_scan_token(BRIDGE);
    }

    public static boolean jj_3R_10() {
        return jj_scan_token(BRIDGE);
    }

    public static boolean jj_3R_11() {
        return jj_scan_token(33);
    }

    public static boolean jj_3R_12() {
        return jj_scan_token(ANNOTATION);
    }

    public static void jj_save(int ba, int bb) {
        ChangeLogParserJJCalls changeLogParserJJCalls;
        for (changeLogParserJJCalls = jj_2_rtns[ba]; changeLogParserJJCalls.gen > jj_gen; changeLogParserJJCalls = changeLogParserJJCalls.next) {
            if (changeLogParserJJCalls.next == null) {
                changeLogParserJJCalls = changeLogParserJJCalls.next = new ChangeLogParserJJCalls();
                break;
            }
        }

        changeLogParserJJCalls.gen = jj_gen + bb - jj_la;
        changeLogParserJJCalls.first = token;
        changeLogParserJJCalls.arg = bb;
    }

    public static boolean jj_3R_13() {
        return jj_scan_token(AND);
    }

    public static final void PackageFlowObfuscationData() throws ChangeLogParseException {
        ASTPackageFlowObfuscationData aSTPackageFlowObfuscationData = new ASTPackageFlowObfuscationData();
        boolean bl = true;
        jjtree.openNodeScope(aSTPackageFlowObfuscationData);
        try {
            ChangeLogParser.jj_consume_token(62);
            ChangeLogParser.FlowObfuscationClassName();
            switch (ChangeLogParser.jj_nt.H) {
                case 46: {
                    ChangeLogParser.jj_consume_token(46);
                    ChangeLogParser.FlowObfuscationPackageName();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[55] = jj_gen;
                }
            }
            switch (ChangeLogParser.jj_nt.H) {
                case 29: {
                    ChangeLogParser.jj_consume_token(29);
                    ChangeLogParser.FieldData();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[56] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTPackageFlowObfuscationData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTPackageFlowObfuscationData);
                }
                throw throwable2;
            }
        }
    }

    public static final void MethodSignature() throws ChangeLogParseException {
        ChangeLogASTMethodSignature changeLogASTMethodSignature = new ChangeLogASTMethodSignature();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTMethodSignature);
        try {
            int n;
            block10:
            {
                ChangeLogParser.Name();
                ChangeLogParser.jj_consume_token(14);
                switch (ChangeLogParser.jj_nt.H) {
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 31:
                    case 32:
                    case 33:
                    case 36:
                    case 37:
                    case 42:
                    case 43:
                    case 48:
                    case 52:
                    case 57:
                    case 58:
                    case 64:
                    case 67:
                    case 75:
                    case 76:
                    case 77: {
                        ChangeLogParser.MethodArguments();
                        break;
                    }
                    default: {
                        ChangeLogParser.jj_la1[43] = jj_gen;
                        n = 17;
                        break block10;
                    }
                }
                n = 17;
            }
            ChangeLogParser.jj_consume_token(n);
            jjtree.closeNodeScope(changeLogASTMethodSignature);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTMethodSignature);
                }
                throw throwable2;
            }
        }
    }

    public static final void ParameterChangeLookupData() throws ChangeLogParseException {
        ASTParameterChangeLookupData aSTParameterChangeLookupData = new ASTParameterChangeLookupData();
        boolean bl = true;
        jjtree.openNodeScope(aSTParameterChangeLookupData);
        try {
            ChangeLogParser.jj_consume_token(16);
            ChangeLogParser.Name();
            jjtree.closeNodeScope(aSTParameterChangeLookupData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTParameterChangeLookupData);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_14() {
        return jj_scan_token(NAME_NOT_CHANGED);
    }

    public static boolean jj_3R_15() {
        return jj_scan_token(LIGHT);
    }

    public static boolean jj_3R_16() {
        return jj_scan_token(EXCLUDE);
    }

    public static boolean jj_3R_17() {
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

    public static final void MethodNameChange() throws ChangeLogParseException {
        ChangeLogASTMethodNameChange changeLogASTMethodNameChange = new ChangeLogASTMethodNameChange();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTMethodNameChange);
        try {
            while (ChangeLogParser.jj_2_6()) {
                ChangeLogParser.MemberModifier();
            }
            if (ChangeLogParser.jj_2_2()) {
                ChangeLogParser.MethodReturnType();
            }
            ChangeLogParser.MethodSignature();
            block2:
            switch (ChangeLogParser.jj_nt.H) {
                case 24: {
                    ChangeLogParser.jj_consume_token(24);
                    if (ChangeLogParser.jj_2_3()) {
                        ChangeLogParser.NewMethodSignature();
                        switch (ChangeLogParser.jj_nt.H) {
                            case 24: {
                                ChangeLogParser.jj_consume_token(24);
                                ChangeLogParser.NewMethodSignature();
                                break;
                            }
                            default: {
                                ChangeLogParser.jj_la1[30] = jj_gen;
                            }
                        }
                        switch (ChangeLogParser.jj_nt.H) {
                            case 15: {
                                ChangeLogParser.ParameterChangeData();
                                break;
                            }
                            default: {
                                ChangeLogParser.jj_la1[31] = jj_gen;
                            }
                        }
                        switch (ChangeLogParser.jj_nt.H) {
                            case 16: {
                                ChangeLogParser.ParameterChangeLookupData();
                                break block2;
                            }
                        }
                        ChangeLogParser.jj_la1[32] = jj_gen;
                        break;
                    }
                    switch (ChangeLogParser.jj_nt.H) {
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                        case 30:
                        case 31:
                        case 32:
                        case 33:
                        case 34:
                        case 36:
                        case 37:
                        case 38:
                        case 40:
                        case 42:
                        case 43:
                        case 45:
                        case 47:
                        case 48:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 57:
                        case 58:
                        case 59:
                        case 64:
                        case 67:
                        case 75:
                        case 76:
                        case 77: {
                            ChangeLogParser.NewMethodName();
                            break block2;
                        }
                    }
                    ChangeLogParser.jj_la1[33] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
                case 64: {
                    ChangeLogParser.jj_consume_token(64);
                    break;
                }
                case 67: {
                    ChangeLogParser.jj_consume_token(67);
                    switch (ChangeLogParser.jj_nt.H) {
                        case 16: {
                            ChangeLogParser.ParameterChangeLookupData();
                            break block2;
                        }
                    }
                    ChangeLogParser.jj_la1[34] = jj_gen;
                    break;
                }
                case 68: {
                    ChangeLogParser.jj_consume_token(68);
                    switch (ChangeLogParser.jj_nt.H) {
                        case 16: {
                            ChangeLogParser.ParameterChangeLookupData();
                            break block2;
                        }
                    }
                    ChangeLogParser.jj_la1[35] = jj_gen;
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[36] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
            }
            switch (ChangeLogParser.jj_nt.H) {
                case 63: {
                    ChangeLogParser.Manufactured();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[37] = jj_gen;
                }
            }
            jjtree.closeNodeScope(changeLogASTMethodNameChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTMethodNameChange);
                }
                throw throwable2;
            }
        }
    }


    static {
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
        jj_2_rtns = new ChangeLogParserJJCalls[6];
        jj_rescan = false;
        jj_gc = 0;
        jj_ls = new ChangeLogParserLookaheadSuccess(null);
        jj_expentries = new ArrayList();
        jj_kind = -1;
        jj_lasttokens = new int[100];
    }


    public static boolean jj_3R_18() {
        return jj_scan_token(SYNTHETIC);
    }

    public static boolean jj_3R_19() {
        return jj_scan_token(NORMAL);
    }

    public static final void FieldData() throws ChangeLogParseException {
        ASTFieldData aSTFieldData = new ASTFieldData();
        boolean bl = true;
        jjtree.openNodeScope(aSTFieldData);
        try {
            switch (ChangeLogParser.jj_nt.H) {
                case 75: {
                    ChangeLogParser.Integer();
                    break;
                }
                case 76: {
                    ChangeLogParser.Alphanumeric();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[57] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
            }
            block11:
            while (true) {
                switch (ChangeLogParser.jj_nt.H) {
                    case 76: {
                        break;
                    }
                    default: {
                        ChangeLogParser.jj_la1[58] = jj_gen;
                        break block11;
                    }
                }
                ChangeLogParser.Alphanumeric();
            }
            jjtree.closeNodeScope(aSTFieldData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTFieldData);
                }
                throw throwable2;
            }
        }
    }

    public static final void MemberData() throws ChangeLogParseException {
        ASTMemberData aSTMemberData = new ASTMemberData();
        boolean bl = true;
        jjtree.openNodeScope(aSTMemberData);
        try {
            ChangeLogParser.Integer();
            switch (ChangeLogParser.jj_nt.H) {
                case 76: {
                    ChangeLogParser.Alphanumeric();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[59] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTMemberData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTMemberData);
                }
                throw throwable2;
            }
        }
    }

    public static final void ModuleChange() throws ChangeLogParseException {
        ASTModuleChange aSTModuleChange = new ASTModuleChange();
        boolean bl = true;
        jjtree.openNodeScope(aSTModuleChange);
        try {
            ChangeLogParser.jj_consume_token(41);
            ChangeLogParser.OldModuleName();
            ChangeLogParser.jj_consume_token(64);
            jjtree.closeNodeScope(aSTModuleChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTModuleChange);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_20() {
        return jj_scan_token(AND);
    }

    




    public static final void LineNumberChange() throws ChangeLogParseException {
        ASTLineNumberChange aSTLineNumberChange = new ASTLineNumberChange();
        boolean bl = true;
        jjtree.openNodeScope(aSTLineNumberChange);
        try {
            ChangeLogParser.OldLineNumber();
            ChangeLogParser.jj_consume_token(24);
            ChangeLogParser.NewLineNumber();
            block2:
            switch (ChangeLogParser.jj_nt.H) {
                case 20:
                case 25: {
                    switch (ChangeLogParser.jj_nt.H) {
                        case 25: {
                            ChangeLogParser.jj_consume_token(25);
                            ChangeLogParser.NewLineNumber();
                            break block2;
                        }
                        case 20: {
                            int n = 20;
                            block14:
                            while (true) {
                                ChangeLogParser.jj_consume_token(n);
                                ChangeLogParser.NewLineNumber();
                                switch (ChangeLogParser.jj_nt.H) {
                                    case 20: {
                                        n = 20;
                                        continue block14;
                                    }
                                }
                                break;
                            }
                            ChangeLogParser.jj_la1[51] = jj_gen;
                            ChangeLogParser.jj_consume_token(25);
                            ChangeLogParser.NewLineNumber();
                            break block2;
                        }
                    }
                    ChangeLogParser.jj_la1[52] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
                default: {
                    ChangeLogParser.jj_la1[53] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTLineNumberChange);
            return;
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ChangeLogParseException)) throw (Error) throwable;
                throw (ChangeLogParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                jjtree.closeNodeScope(aSTLineNumberChange);
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_21() {
        return jj_scan_token(ENUM);
    }

    public static final void NameList() throws ChangeLogParseException {
        ChangeLogASTNameList changeLogASTNameList = new ChangeLogASTNameList();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTNameList);
        try {
            ChangeLogParser.Name();
            block11:
            while (true) {
                block15:
                {
                    switch (ChangeLogParser.jj_nt.H) {
                        case 21:
                        case 22: {
                            break;
                        }
                        default: {
                            ChangeLogParser.jj_la1[38] = jj_gen;
                            break block11;
                        }
                    }
                    switch (ChangeLogParser.jj_nt.H) {
                        case 22: {
                            ChangeLogParser.jj_consume_token(22);
                            break block15;
                        }
                        case 21: {
                            ChangeLogParser.jj_consume_token(21);
                            break;
                        }
                        default: {
                            ChangeLogParser.jj_la1[39] = jj_gen;
                            ChangeLogParser.jj_consume_token(-1);
                            throw new ChangeLogParseException();
                        }
                    }
                    ChangeLogParser.Name();
                    continue;
                }
                ChangeLogParser.Name();
            }
            jjtree.closeNodeScope(changeLogASTNameList);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTNameList);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_22() {
        return jj_scan_token(PROTECTED);
    }

    public static final void ParameterObfuscated() throws ChangeLogParseException {
        ASTParameterObfuscated aSTParameterObfuscated = new ASTParameterObfuscated();
        jjtree.openNodeScope(aSTParameterObfuscated);

        try {
            jj_consume_token(STAR);
        } finally {
            jjtree.closeNodeScope(aSTParameterObfuscated);
        }
    }

    public static final void NewMethodName() throws ChangeLogParseException {
        ChangeLogASTNewMethodName changeLogASTNewMethodName = new ChangeLogASTNewMethodName();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTNewMethodName);
        try {
            ChangeLogParser.Name();
            jjtree.closeNodeScope(changeLogASTNewMethodName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTNewMethodName);
                }
                throw throwable2;
            }
        }
    }

    public static final void FlowObfuscationPackageName() throws ChangeLogParseException {
        ASTFlowObfuscationPackageName aSTFlowObfuscationPackageName = new ASTFlowObfuscationPackageName();
        boolean bl = true;
        jjtree.openNodeScope(aSTFlowObfuscationPackageName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(aSTFlowObfuscationPackageName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTFlowObfuscationPackageName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_23() {
        return jj_scan_token(INTEGER_LITERAL);
    }

    public static void jj_la1_init_0() {
        jj_la1_0 = new int[]{
                8192,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                -570425344,
                16777216,
                16777216,
                0,
                -570425344,
                0,
                -570425344,
                0,
                0,
                0,
                -570425344,
                16777216,
                0,
                16777216,
                32768,
                65536,
                -570425344,
                65536,
                65536,
                16777216,
                0,
                6291456,
                6291456,
                4194304,
                1140850688,
                1140850688,
                -1644167168,
                -1644167168,
                8388608,
                1048576,
                4194304,
                262144,
                -570425344,
                -1644167168,
                1048576,
                34603008,
                34603008,
                536870912,
                0,
                536870912,
                0,
                0,
                0
        };
    }

    public static boolean jj_3R_24() {
        return jj_scan_token(SIGNATURE_NOT_CHANGED);
    }

    public static final void OldLineNumber() throws ChangeLogParseException {
        ASTOldLineNumber aSTOldLineNumber = new ASTOldLineNumber();
        boolean bl = true;
        jjtree.openNodeScope(aSTOldLineNumber);
        try {
            ChangeLogParser.Integer();
            jjtree.closeNodeScope(aSTOldLineNumber);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTOldLineNumber);
                }
                throw throwable2;
            }
        }
    }

    public static ChangeLogParseException generateParseException() {
        jj_expentries.clear();
        boolean[] bl = new boolean[78];
        if (jj_kind >= 0) {
            bl[jj_kind] = true;
            jj_kind = -1;
        }

        int ba = 0;
        int bh = ba;

        for (byte bi = 60; bh < bi; bi = 60) {
            if (jj_la1[ba] == jj_gen) {
                int bb = 0;
                bh = bb;

                for (byte bf = 32; bh < bf; bf = 32) {
                    if ((jj_la1_0[ba] & 1 << bb) != 0) {
                        bl[bb] = true;
                    }

                    int[] be;
                    if ((jj_la1_1[ba] & 1 << bb) != 0) {
                        bl[32 + bb] = true;
                        be = jj_la1_2;
                    } else {
                        be = jj_la1_2;
                    }

                    if ((be[ba] & 1 << bb) != 0) {
                        bl[64 + bb] = true;
                    }

                    bh = ++bb;
                }
            }

            bh = ++ba;
        }

        ba = 0;
        bh = ba;

        for (byte bg = 78; bh < bg; bg = 78) {
            if (bl[ba]) {
                jj_expentry = new int[1];
                jj_expentry[0] = ba;
                jj_expentries.add(jj_expentry);
            }

            bh = ++ba;
        }

        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int[][] bc = new int[jj_expentries.size()][];
        int bd = 0;
        bh = 0;

        for (List list1 = jj_expentries; bh < list1.size(); list1 = jj_expentries) {
            bc[bd] = (int[]) jj_expentries.get(bd);
            bh = ++bd;
        }

        return new ChangeLogParseException(token, bc, ChangeLogConstants.TOKEN_IMAGE);
    }

    public static boolean jj_3R_25() {
        return jj_scan_token(FINAL);
    }

    public static boolean jj_3R_26() {
        return jj_3R_73();
    }

    public static boolean jj_3R_27() {
        return jj_scan_token(ANNOTATION);
    }

    public static boolean jj_3R_28() {
        return jj_scan_token(NAME_NOT_CHANGED);
    }

    public static final void StringLiteral() throws ChangeLogParseException {
        ChangeLogASTStringLiteral changeLogASTStringLiteral = new ChangeLogASTStringLiteral();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTStringLiteral);

        try {
            ChangeLogToken changeLogToken = jj_consume_token(QUOTE_73);
            jjtree.closeNodeScope(changeLogASTStringLiteral);
            bl = false;
            String string = changeLogToken.k;
            string = string.substring(1, string.length() - 1);
            string = ZkmStringUtils.replaceAll(string, "\"\"", "\"");
            changeLogASTStringLiteral.setValue(string);
        } finally {
            if (bl) {
                jjtree.closeNodeScope(changeLogASTStringLiteral);
            }
        }
    }

    public static boolean jj_3R_29() {
        return jj_scan_token(PACKAGE_INFO);
    }

    public static boolean jj_2_2() {
        jj_la = Integer.MAX_VALUE;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3_2();
        } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
        } finally {
            jj_save(4, Integer.MAX_VALUE);
        }

        return true;
    }

    public static boolean jj_3R_30() {
        return jj_scan_token(PUBLIC);
    }

    public static boolean jj_3R_31() {
        return jj_scan_token(SYNTHETIC);
    }

    public static boolean jj_3R_32() {
        return jj_scan_token(ALPHANUMERIC_LITERAL);
    }

    public static boolean jj_3R_33() {
        return jj_scan_token(PRIVATE);
    }

    public static boolean jj_2_3() {
        jj_la = Integer.MAX_VALUE;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3_1();
        } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
        } finally {
            jj_save(5, Integer.MAX_VALUE);
        }

        return true;
    }

    public static final void FlowObfuscationClassName() throws ChangeLogParseException {
        ASTFlowObfuscationClassName aSTFlowObfuscationClassName = new ASTFlowObfuscationClassName();
        boolean bl = true;
        jjtree.openNodeScope(aSTFlowObfuscationClassName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(aSTFlowObfuscationClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTFlowObfuscationClassName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_34() {
        return jj_scan_token(FINAL);
    }

    private static ChangeLogToken jj_consume_token(int ba) throws ChangeLogParseException {
        ChangeLogToken changeLogToken = token;
        if ((token = jj_nt).h != null) {
            jj_nt = jj_nt.h;
        } else {
            jj_nt = jj_nt.h = ChangeLogTokenManager.getNextToken();
        }

        if (token.H != ba) {
            jj_nt = token;
            token = changeLogToken;
            jj_kind = ba;
            throw generateParseException();
        }

        jj_gen++;
        if (++jj_gc > 100) {
            jj_gc = 0;

            for (int i = 0; i < jj_2_rtns.length; i++) {
                ChangeLogParserJJCalls changeLogParserJJCalls = jj_2_rtns[i];

                while (changeLogParserJJCalls != null) {
                    ChangeLogParserJJCalls changeLogParserJJCalls1;
                    if (changeLogParserJJCalls.gen < jj_gen) {
                        changeLogParserJJCalls.first = null;
                        changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                    } else {
                        changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                    }

                    changeLogParserJJCalls = changeLogParserJJCalls1;
                }
            }
        }

        return token;
    }

    public static boolean jj_3R_35() {
        if (jj_3R_60()) {
            return true;
        }

        if (jj_3R_84()) {
            return true;
        }

        ChangeLogToken changeLogToken = jj_scanpos;
        if (jj_scan_token(ARROW_EQ)) {
            jj_scanpos = changeLogToken;
            if (jj_scan_token(NAME_NOT_CHANGED)) {
                return true;
            }
        }

        return false;
    }

    public static boolean jj_3R_36() {
        return jj_scan_token(PROTECTED);
    }

    public static boolean jj_3R_37() {
        return jj_scan_token(LBRACKET) ? true : jj_scan_token(RBRACKET);
    }

    public static boolean jj_3R_38() {
        return jj_scan_token(INTEGER_LITERAL);
    }

    public static boolean jj_3R_39() {
        return jj_scan_token(ALPHANUMERIC_LITERAL);
    }

    public static boolean jj_3R_40() {
        return jj_scan_token(EXECUTE);
    }

    public static boolean jj_3R_41() {
        return jj_scan_token(27);
    }

    public static boolean jj_3R_42() {
        ChangeLogToken changeLogToken = jj_scanpos;
        if (jj_3R_45()) {
            jj_scanpos = changeLogToken;
            if (jj_3R_28()) {
                jj_scanpos = changeLogToken;
                if (jj_3R_24()) {
                    jj_scanpos = changeLogToken;
                    if (jj_3R_27()) {
                        jj_scanpos = changeLogToken;
                        if (jj_3R_9()) {
                            jj_scanpos = changeLogToken;
                            if (jj_3R_46()) {
                                jj_scanpos = changeLogToken;
                                if (jj_3R_31()) {
                                    jj_scanpos = changeLogToken;
                                    if (jj_3R_53()) {
                                        jj_scanpos = changeLogToken;
                                        if (jj_3R_15()) {
                                            jj_scanpos = changeLogToken;
                                            if (jj_3R_11()) {
                                                jj_scanpos = changeLogToken;
                                                if (jj_3R_80()) {
                                                    jj_scanpos = changeLogToken;
                                                    if (jj_3R_52()) {
                                                        jj_scanpos = changeLogToken;
                                                        if (jj_3R_41()) {
                                                            jj_scanpos = changeLogToken;
                                                            if (jj_3R_63()) {
                                                                jj_scanpos = changeLogToken;
                                                                if (jj_3R_64()) {
                                                                    jj_scanpos = changeLogToken;
                                                                    if (jj_3R_70()) {
                                                                        jj_scanpos = changeLogToken;
                                                                        if (jj_3R_8()) {
                                                                            jj_scanpos = changeLogToken;
                                                                            if (jj_3R_13()) {
                                                                                jj_scanpos = changeLogToken;
                                                                                if (jj_3R_38()) {
                                                                                    jj_scanpos = changeLogToken;
                                                                                    if (jj_3R_32()) {
                                                                                        return true;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean jj_scan_token(int ba) {
        if (jj_scanpos == jj_lastpos) {
            jj_la--;
            if (jj_scanpos.h == null) {
                jj_lastpos = jj_scanpos = jj_scanpos.h = ChangeLogTokenManager.getNextToken();
            } else {
                jj_lastpos = jj_scanpos = jj_scanpos.h;
            }
        } else {
            jj_scanpos = jj_scanpos.h;
        }

        if (jj_rescan) {
            int bb = 0;

            ChangeLogToken changeLogToken;
            for (changeLogToken = token; changeLogToken != null && changeLogToken != jj_scanpos; changeLogToken = changeLogToken.h) {
                bb++;
            }

            if (changeLogToken != null) {
                jj_add_error_token(ba, bb);
            }
        }

        if (jj_scanpos.H != ba) {
            return true;
        } else if (jj_la == 0 && jj_scanpos == jj_lastpos) {
            throw jj_ls;
        } else {
            return false;
        }
    }

    public static boolean jj_3R_43() {
        ChangeLogToken changeLogToken = jj_scanpos;
        if (jj_3R_17()) {
            jj_scanpos = changeLogToken;
            if (jj_3R_39()) {
                jj_scanpos = changeLogToken;
                if (jj_3R_29()) {
                    jj_scanpos = changeLogToken;
                    if (jj_3R_23()) {
                        jj_scanpos = changeLogToken;
                        if (jj_3R_20()) {
                            jj_scanpos = changeLogToken;
                            if (jj_3R_14()) {
                                jj_scanpos = changeLogToken;
                                if (jj_3R_66()) {
                                    jj_scanpos = changeLogToken;
                                    if (jj_3R_12()) {
                                        jj_scanpos = changeLogToken;
                                        if (jj_3R_2()) {
                                            jj_scanpos = changeLogToken;
                                            if (jj_3R_21()) {
                                                jj_scanpos = changeLogToken;
                                                if (jj_3R_81()) {
                                                    jj_scanpos = changeLogToken;
                                                    if (jj_3R_1()) {
                                                        jj_scanpos = changeLogToken;
                                                        if (jj_3R_6()) {
                                                            jj_scanpos = changeLogToken;
                                                            if (jj_3R_78()) {
                                                                jj_scanpos = changeLogToken;
                                                                if (jj_3R_69()) {
                                                                    jj_scanpos = changeLogToken;
                                                                    if (jj_3R_72()) {
                                                                        jj_scanpos = changeLogToken;
                                                                        if (jj_3R_48()) {
                                                                            jj_scanpos = changeLogToken;
                                                                            if (jj_3R_19()) {
                                                                                jj_scanpos = changeLogToken;
                                                                                if (jj_3R_71()) {
                                                                                    jj_scanpos = changeLogToken;
                                                                                    if (jj_3R_83()) {
                                                                                        jj_scanpos = changeLogToken;
                                                                                        if (jj_3R_40()) {
                                                                                            jj_scanpos = changeLogToken;
                                                                                            if (jj_3R_16()) {
                                                                                                jj_scanpos = changeLogToken;
                                                                                                if (jj_3R_5()) {
                                                                                                    jj_scanpos = changeLogToken;
                                                                                                    if (jj_3R_51()) {
                                                                                                        jj_scanpos = changeLogToken;
                                                                                                        if (jj_3R_59()) {
                                                                                                            jj_scanpos = changeLogToken;
                                                                                                            if (jj_3R_76()) {
                                                                                                                jj_scanpos = changeLogToken;
                                                                                                                if (jj_3R_22()) {
                                                                                                                    jj_scanpos = changeLogToken;
                                                                                                                    if (jj_3R_4()) {
                                                                                                                        jj_scanpos = changeLogToken;
                                                                                                                        if (jj_3R_77()) {
                                                                                                                            jj_scanpos = changeLogToken;
                                                                                                                            if (jj_3R_65()) {
                                                                                                                                return true;
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean jj_3R_44() {
        return jj_3R_67();
    }

    public static boolean jj_3R_45() {
        return jj_scan_token(NAME);
    }

    public static final void Name() throws ChangeLogParseException {
        ChangeLogASTName changeLogASTName = new ChangeLogASTName();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTName);

        try {
            switch (jj_nt.H) {
                case 25:
                    ChangeLogToken changeLogToken29 = jj_consume_token(AND);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken29.k);
                    break;
                case 26:
                    ChangeLogToken changeLogToken28 = jj_consume_token(ENUM);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken28.k);
                    break;
                case 27:
                    ChangeLogToken changeLogToken27 = jj_consume_token(27);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken27.k);
                    break;
                case 28:
                    ChangeLogToken changeLogToken26 = jj_consume_token(28);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken26.k);
                    break;
                case 29:
                case 35:
                case 39:
                case 41:
                case 44:
                case 46:
                case 49:
                case 54:
                case 55:
                case 56:
                case 60:
                case 61:
                case 62:
                case 63:
                case 65:
                case 66:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                default:
                    jj_la1[49] = jj_gen;
                    jj_consume_token(-1);
                    throw new ChangeLogParseException();
                case 30:
                    ChangeLogToken changeLogToken25 = jj_consume_token(FINAL);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken25.k);
                    break;
                case 31:
                    ChangeLogToken changeLogToken24 = jj_consume_token(FALSE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken24.k);
                    break;
                case 32:
                    ChangeLogToken changeLogToken23 = jj_consume_token(LIGHT);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken23.k);
                    break;
                case 33:
                    ChangeLogToken changeLogToken22 = jj_consume_token(33);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken22.k);
                    break;
                case 34:
                    ChangeLogToken changeLogToken21 = jj_consume_token(INIT);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken21.k);
                    break;
                case 36:
                    ChangeLogToken changeLogToken20 = jj_consume_token(NORMAL);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken20.k);
                    break;
                case 37:
                    ChangeLogToken changeLogToken19 = jj_consume_token(BRIDGE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken19.k);
                    break;
                case 38:
                    ChangeLogToken changeLogToken18 = jj_consume_token(PUBLIC);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken18.k);
                    break;
                case 40:
                    ChangeLogToken changeLogToken17 = jj_consume_token(NATIVE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken17.k);
                    break;
                case 42:
                    ChangeLogToken changeLogToken16 = jj_consume_token(EXECUTE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken16.k);
                    break;
                case 43:
                    ChangeLogToken changeLogToken15 = jj_consume_token(EXCLUDE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken15.k);
                    break;
                case 45:
                    ChangeLogToken changeLogToken14 = jj_consume_token(CLINIT);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken14.k);
                    break;
                case 47:
                    ChangeLogToken changeLogToken13 = jj_consume_token(PRIVATE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken13.k);
                    break;
                case 48:
                    ChangeLogToken changeLogToken12 = jj_consume_token(ENHANCED);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken12.k);
                    break;
                case 50:
                    ChangeLogToken changeLogToken11 = jj_consume_token(VOLATILE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken11.k);
                    break;
                case 51:
                    ChangeLogToken changeLogToken10 = jj_consume_token(PROTECTED);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken10.k);
                    break;
                case 52:
                    ChangeLogToken changeLogToken9 = jj_consume_token(SYNTHETIC);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken9.k);
                    break;
                case 53:
                    ChangeLogToken changeLogToken8 = jj_consume_token(INTERFACE);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken8.k);
                    break;
                case 57:
                    ChangeLogToken changeLogToken7 = jj_consume_token(57);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken7.k);
                    break;
                case 58:
                    ChangeLogToken changeLogToken6 = jj_consume_token(ANNOTATION);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken6.k);
                    break;
                case 59:
                    ChangeLogToken changeLogToken5 = jj_consume_token(PACKAGE_INFO);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken5.k);
                    break;
                case 64:
                    ChangeLogToken changeLogToken4 = jj_consume_token(NAME_NOT_CHANGED);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken4.k);
                    break;
                case 67:
                    ChangeLogToken changeLogToken3 = jj_consume_token(SIGNATURE_NOT_CHANGED);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken3.k);
                    break;
                case 75:
                    ChangeLogToken changeLogToken2 = jj_consume_token(INTEGER_LITERAL);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken2.k);
                    break;
                case 76:
                    ChangeLogToken changeLogToken1 = jj_consume_token(ALPHANUMERIC_LITERAL);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken1.k);
                    break;
                case 77:
                    ChangeLogToken changeLogToken = jj_consume_token(NAME);
                    jjtree.closeNodeScope(changeLogASTName);
                    bl = false;
                    changeLogASTName.setValue(changeLogToken.k);
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(changeLogASTName);
            }
        }
    }

    public static boolean jj_3R_46() {
        return jj_scan_token(ENUM);
    }

    public static final void DisregardedNameList() throws ChangeLogParseException {
        ASTDisregardedNameList aSTDisregardedNameList = new ASTDisregardedNameList();
        boolean bl = true;
        jjtree.openNodeScope(aSTDisregardedNameList);
        try {
            ChangeLogParser.Name();
            block7:
            while (true) {
                switch (ChangeLogParser.jj_nt.H) {
                    case 22: {
                        break;
                    }
                    default: {
                        ChangeLogParser.jj_la1[40] = jj_gen;
                        break block7;
                    }
                }
                ChangeLogParser.jj_consume_token(22);
                ChangeLogParser.Name();
            }
            jjtree.closeNodeScope(aSTDisregardedNameList);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTDisregardedNameList);
                }
                throw throwable2;
            }
        }
    }

    public static final void NewPackageName() throws ChangeLogParseException {
        ASTNewPackageName aSTNewPackageName = new ASTNewPackageName();
        boolean bl = true;
        jjtree.openNodeScope(aSTNewPackageName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(aSTNewPackageName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTNewPackageName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_47() {
        return jj_scan_token(SYNCHRONIZED);
    }

    public static final void Alphanumeric() throws ChangeLogParseException {
        ASTAlphanumeric aSTAlphanumeric = new ASTAlphanumeric();
        boolean bl = true;
        jjtree.openNodeScope(aSTAlphanumeric);

        try {
            ChangeLogToken changeLogToken = jj_consume_token(ALPHANUMERIC_LITERAL);
            jjtree.closeNodeScope(aSTAlphanumeric);
            bl = false;
            aSTAlphanumeric.setValue(changeLogToken.k);
        } finally {
            if (bl) {
                jjtree.closeNodeScope(aSTAlphanumeric);
            }
        }
    }

    public static boolean jj_3R_48() {
        return jj_scan_token(33);
    }

    public static final void Type() throws ChangeLogParseException {
        ChangeLogASTType changeLogASTType = new ChangeLogASTType();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTType);

        try {
            switch (jj_nt.H) {
                case 25:
                    ChangeLogToken changeLogToken19 = jj_consume_token(AND);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken19.k);
                    break;
                case 26:
                    ChangeLogToken changeLogToken18 = jj_consume_token(ENUM);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken18.k);
                    break;
                case 27:
                    ChangeLogToken changeLogToken17 = jj_consume_token(27);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken17.k);
                    break;
                case 28:
                    ChangeLogToken changeLogToken16 = jj_consume_token(28);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken16.k);
                    break;
                case 29:
                case 30:
                case 34:
                case 35:
                case 38:
                case 39:
                case 40:
                case 41:
                case 44:
                case 45:
                case 46:
                case 47:
                case 49:
                case 50:
                case 51:
                case 53:
                case 54:
                case 55:
                case 56:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 65:
                case 66:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                default:
                    jj_la1[50] = jj_gen;
                    jj_consume_token(-1);
                    throw new ChangeLogParseException();
                case 31:
                    ChangeLogToken changeLogToken15 = jj_consume_token(FALSE);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken15.k);
                    break;
                case 32:
                    ChangeLogToken changeLogToken14 = jj_consume_token(LIGHT);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken14.k);
                    break;
                case 33:
                    ChangeLogToken changeLogToken13 = jj_consume_token(33);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken13.k);
                    break;
                case 36:
                    ChangeLogToken changeLogToken12 = jj_consume_token(NORMAL);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken12.k);
                    break;
                case 37:
                    ChangeLogToken changeLogToken11 = jj_consume_token(BRIDGE);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken11.k);
                    break;
                case 42:
                    ChangeLogToken changeLogToken10 = jj_consume_token(EXECUTE);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken10.k);
                    break;
                case 43:
                    ChangeLogToken changeLogToken9 = jj_consume_token(EXCLUDE);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken9.k);
                    break;
                case 48:
                    ChangeLogToken changeLogToken8 = jj_consume_token(ENHANCED);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken8.k);
                    break;
                case 52:
                    ChangeLogToken changeLogToken7 = jj_consume_token(SYNTHETIC);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken7.k);
                    break;
                case 57:
                    ChangeLogToken changeLogToken6 = jj_consume_token(57);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken6.k);
                    break;
                case 58:
                    ChangeLogToken changeLogToken5 = jj_consume_token(ANNOTATION);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken5.k);
                    break;
                case 64:
                    ChangeLogToken changeLogToken4 = jj_consume_token(NAME_NOT_CHANGED);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken4.k);
                    break;
                case 67:
                    ChangeLogToken changeLogToken3 = jj_consume_token(SIGNATURE_NOT_CHANGED);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken3.k);
                    break;
                case 75:
                    ChangeLogToken changeLogToken2 = jj_consume_token(INTEGER_LITERAL);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken2.k);
                    break;
                case 76:
                    ChangeLogToken changeLogToken1 = jj_consume_token(ALPHANUMERIC_LITERAL);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken1.k);
                    break;
                case 77:
                    ChangeLogToken changeLogToken = jj_consume_token(NAME);
                    jjtree.closeNodeScope(changeLogASTType);
                    bl = false;
                    changeLogASTType.setValue(changeLogToken.k);
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(changeLogASTType);
            }
        }
    }

    public static boolean jj_3R_49() {
        return jj_scan_token(STATIC);
    }

    public static final void OldPackageName() throws ChangeLogParseException {
        ASTOldPackageName aSTOldPackageName = new ASTOldPackageName();
        boolean bl = true;
        jjtree.openNodeScope(aSTOldPackageName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(aSTOldPackageName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTOldPackageName);
                }
                throw throwable2;
            }
        }
    }

    public static final void MemberModifier() throws ChangeLogParseException {
        ChangeLogASTMemberModifier changeLogASTMemberModifier = new ChangeLogASTMemberModifier();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTMemberModifier);

        try {
            switch (jj_nt.H) {
                case 26:
                    ChangeLogToken changeLogToken12 = jj_consume_token(ENUM);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken12.k);
                    break;
                case 27:
                case 28:
                case 29:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 48:
                case 53:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                default:
                    jj_la1[42] = jj_gen;
                    jj_consume_token(-1);
                    throw new ChangeLogParseException();
                case 30:
                    ChangeLogToken changeLogToken11 = jj_consume_token(FINAL);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken11.k);
                    break;
                case 37:
                    ChangeLogToken changeLogToken10 = jj_consume_token(BRIDGE);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken10.k);
                    break;
                case 38:
                    ChangeLogToken changeLogToken9 = jj_consume_token(PUBLIC);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken9.k);
                    break;
                case 39:
                    ChangeLogToken changeLogToken8 = jj_consume_token(STATIC);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken8.k);
                    break;
                case 40:
                    ChangeLogToken changeLogToken7 = jj_consume_token(NATIVE);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken7.k);
                    break;
                case 47:
                    ChangeLogToken changeLogToken6 = jj_consume_token(PRIVATE);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken6.k);
                    break;
                case 49:
                    ChangeLogToken changeLogToken5 = jj_consume_token(ABSTRACT);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken5.k);
                    break;
                case 50:
                    ChangeLogToken changeLogToken4 = jj_consume_token(VOLATILE);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken4.k);
                    break;
                case 51:
                    ChangeLogToken changeLogToken3 = jj_consume_token(PROTECTED);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken3.k);
                    break;
                case 52:
                    ChangeLogToken changeLogToken2 = jj_consume_token(SYNTHETIC);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken2.k);
                    break;
                case 54:
                    ChangeLogToken changeLogToken1 = jj_consume_token(TRANSIENT);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken1.k);
                    break;
                case 61:
                    ChangeLogToken changeLogToken = jj_consume_token(SYNCHRONIZED);
                    jjtree.closeNodeScope(changeLogASTMemberModifier);
                    bl = false;
                    changeLogASTMemberModifier.setValue(changeLogToken.k);
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(changeLogASTMemberModifier);
            }
        }
    }

    public static boolean jj_3R_50() {
        return jj_3R_37();
    }

    public static boolean jj_3R_51() {
        return jj_scan_token(28);
    }

    public static boolean jj_2_4() {
        jj_la = Integer.MAX_VALUE;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3R_35();
        } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
        } finally {
            jj_save(2, Integer.MAX_VALUE);
        }

        return true;
    }

    public static boolean jj_3R_52() {
        return jj_scan_token(57);
    }

    public static boolean jj_3R_53() {
        return jj_scan_token(ENHANCED);
    }

    public ChangeLogParser(Reader reader1) {
        if (jj_initialized_once) {
            System.out.println("ERROR: Second call to constructor of static parser. ");
            throw new Error();
        }

        instance = this;
        jj_initialized_once = true;
        jj_input_stream = new ChangeLogSimpleCharStream(reader1);
        new ChangeLogTokenManager(jj_input_stream);
        token = new ChangeLogToken();
        token.h = jj_nt = ChangeLogTokenManager.getNextToken();
        jj_gen = 0;
        int ba = 0;
        int bc = 0;

        for (byte bd = 60; bc < bd; bd = 60) {
            jj_la1[ba] = -1;
            bc = ++ba;
        }

        for (int i = 0; i < jj_2_rtns.length; i++) {
            jj_2_rtns[i] = new ChangeLogParserJJCalls();
        }
    }

    public static boolean jj_3R_54() {
        return jj_scan_token(TRANSIENT);
    }

    public static boolean jj_3R_55() {
        return jj_scan_token(INTERFACE);
    }

    public static final ChangeLogSimpleNode Input() throws ChangeLogParseException {
        ChangeLogASTInput changeLogASTInput = new ChangeLogASTInput();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTInput);

        try {
            switch (jj_nt.H) {
                case 13:
                    jj_consume_token(BOM);
                    break;
                default:
                    jj_la1[0] = jj_gen;
            }

            while (true) {
                switch (jj_nt.H) {
                    case 41:
                        ModuleChange();
                        break;
                    default:
                        jj_la1[1] = jj_gen;

                        while (true) {
                            switch (jj_nt.H) {
                                case 46:
                                    PackageChange();
                                    break;
                                default:
                                    jj_la1[2] = jj_gen;

                                    while (true) {
                                        switch (jj_nt.H) {
                                            case 35:
                                                ClassChange();
                                                break;
                                            default:
                                                jj_la1[3] = jj_gen;

                                                while (true) {
                                                    switch (jj_nt.H) {
                                                        case 69:
                                                            AutoReflectionClassName();
                                                            break;
                                                        default:
                                                            jj_la1[4] = jj_gen;

                                                            while (true) {
                                                                switch (jj_nt.H) {
                                                                    case 70:
                                                                        ObfuscateReferencesClassName();
                                                                        break;
                                                                    default:
                                                                        jj_la1[5] = jj_gen;

                                                                        while (true) {
                                                                            switch (jj_nt.H) {
                                                                                case 71:
                                                                                    MethodParameterClassNames();
                                                                                    break;
                                                                                default:
                                                                                    jj_la1[6] = jj_gen;
                                                                                    label214:
                                                                                    switch (jj_nt.H) {
                                                                                        case 62:
                                                                                        case 66:
                                                                                            switch (jj_nt.H) {
                                                                                                case 62:
                                                                                                    while (true) {
                                                                                                        PackageFlowObfuscationData();
                                                                                                        switch (jj_nt.H) {
                                                                                                            case 62:
                                                                                                                break;
                                                                                                            default:
                                                                                                                jj_la1[10] = jj_gen;

                                                                                                                while (true) {
                                                                                                                    switch (jj_nt.H) {
                                                                                                                        case 60:
                                                                                                                            MemberFlowObfuscationData();
                                                                                                                            break;
                                                                                                                        default:
                                                                                                                            jj_la1[11] = jj_gen;
                                                                                                                            break label214;
                                                                                                                    }
                                                                                                                }
                                                                                                        }
                                                                                                    }
                                                                                                case 66:
                                                                                                    while (true) {
                                                                                                        MainFlowObfuscationData();
                                                                                                        switch (jj_nt.H) {
                                                                                                            case 66:
                                                                                                                break;
                                                                                                            default:
                                                                                                                jj_la1[7] = jj_gen;

                                                                                                                while (true) {
                                                                                                                    switch (jj_nt.H) {
                                                                                                                        case 62:
                                                                                                                            PackageFlowObfuscationData();
                                                                                                                            break;
                                                                                                                        default:
                                                                                                                            jj_la1[8] = jj_gen;

                                                                                                                            while (true) {
                                                                                                                                switch (jj_nt.H) {
                                                                                                                                    case 60:
                                                                                                                                        MemberFlowObfuscationData();
                                                                                                                                        break;
                                                                                                                                    default:
                                                                                                                                        jj_la1[9] = jj_gen;
                                                                                                                                        break label214;
                                                                                                                                }
                                                                                                                            }
                                                                                                                    }
                                                                                                                }
                                                                                                        }
                                                                                                    }
                                                                                                default:
                                                                                                    jj_la1[12] = jj_gen;
                                                                                                    jj_consume_token(-1);
                                                                                                    throw new ChangeLogParseException();
                                                                                            }
                                                                                        default:
                                                                                            jj_la1[13] = jj_gen;
                                                                                    }

                                                                                    jj_consume_token(EOF);
                                                                                    jjtree.closeNodeScope(changeLogASTInput);
                                                                                    bl = false;
                                                                                    return changeLogASTInput;
                                                                            }
                                                                        }
                                                                }
                                                            }
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
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
            } else if (throwable instanceof ChangeLogParseException) {
                throw (ChangeLogParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(changeLogASTInput);
            }
        }
    }

    public static final void AutoReflectionClassName() throws ChangeLogParseException {
        ASTAutoReflectionClassName aSTAutoReflectionClassName = new ASTAutoReflectionClassName();
        boolean bl = true;
        jjtree.openNodeScope(aSTAutoReflectionClassName);
        try {
            ChangeLogParser.jj_consume_token(69);
            ChangeLogParser.OldClassName();
            switch (ChangeLogParser.jj_nt.H) {
                case 41: {
                    ChangeLogParser.jj_consume_token(41);
                    ChangeLogParser.OldModuleName();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[14] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTAutoReflectionClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTAutoReflectionClassName);
                }
                throw throwable2;
            }
        }
    }

    public static final void NewLineNumber() throws ChangeLogParseException {
        ASTNewLineNumber aSTNewLineNumber = new ASTNewLineNumber();
        boolean bl = true;
        jjtree.openNodeScope(aSTNewLineNumber);
        try {
            ChangeLogParser.Integer();
            jjtree.closeNodeScope(aSTNewLineNumber);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTNewLineNumber);
                }
                throw throwable2;
            }
        }
    }

    public static void jj_la1_init_2() {
        jj_la1_2 = new int[]{
                0,
                0,
                0,
                0,
                32,
                64,
                128,
                4,
                0,
                0,
                0,
                0,
                4,
                4,
                0,
                0,
                0,
                14345,
                1,
                1,
                0,
                14345,
                0,
                14345,
                0,
                2048,
                2,
                14857,
                1,
                0,
                0,
                0,
                0,
                14345,
                0,
                0,
                25,
                0,
                0,
                0,
                0,
                0,
                0,
                14345,
                14345,
                0,
                0,
                0,
                0,
                14345,
                14345,
                0,
                0,
                0,
                0,
                0,
                0,
                6144,
                4096,
                4096
        };
    }

    public static boolean jj_2_5() {
        jj_la = 4;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3R_68();
        } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
        } finally {
            jj_save(1, 4);
        }

        return true;
    }

    public static void jj_la1_init_1() {
        jj_la1_1 = new int[]{
                0,
                512,
                16384,
                8,
                0,
                0,
                0,
                0,
                1073741824,
                268435456,
                1073741824,
                268435456,
                1073741824,
                1073741824,
                512,
                512,
                512,
                238923127,
                0,
                0,
                4096,
                780119543,
                8388608,
                780119543,
                16777216,
                0,
                0,
                238923127,
                0,
                Integer.MIN_VALUE,
                0,
                0,
                0,
                238923127,
                0,
                0,
                0,
                Integer.MIN_VALUE,
                0,
                0,
                0,
                70385728,
                543064544,
                101780531,
                101780531,
                0,
                0,
                0,
                0,
                238923127,
                101780531,
                0,
                0,
                0,
                0,
                16384,
                0,
                0,
                0,
                0
        };
    }

    public static final void QualifiedType() throws ChangeLogParseException {
        ChangeLogASTQualifiedType changeLogASTQualifiedType = new ChangeLogASTQualifiedType();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTQualifiedType);
        try {
            ChangeLogParser.Type();
            block10:
            while (true) {
                switch (ChangeLogParser.jj_nt.H) {
                    case 22: {
                        break;
                    }
                    default: {
                        ChangeLogParser.jj_la1[47] = jj_gen;
                        break block10;
                    }
                }
                ChangeLogParser.jj_consume_token(22);
                ChangeLogParser.Type();
            }
            block11:
            while (true) {
                switch (ChangeLogParser.jj_nt.H) {
                    case 18: {
                        break;
                    }
                    default: {
                        ChangeLogParser.jj_la1[48] = jj_gen;
                        break block11;
                    }
                }
                ChangeLogParser.ArrayLevel();
            }
            jjtree.closeNodeScope(changeLogASTQualifiedType);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTQualifiedType);
                }
                throw throwable2;
            }
        }
    }

    public static void jj_rescan_token() {
        jj_rescan = true;
        int ba = 0;
        int bb = 0;

        for (byte bc = 6; bb < bc; bc = 6) {
            try {
                ChangeLogParserJJCalls changeLogParserJJCalls = jj_2_rtns[ba];

                do {
                    ChangeLogParserJJCalls changeLogParserJJCalls1;
                    if (changeLogParserJJCalls.gen > jj_gen) {
                        jj_la = changeLogParserJJCalls.arg;
                        jj_lastpos = jj_scanpos = changeLogParserJJCalls.first;
                        switch (ba) {
                            case 0:
                                jj_3R_26();
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                                break;
                            case 1:
                                jj_3R_68();
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                                break;
                            case 2:
                                jj_3R_35();
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                                break;
                            case 3:
                                jj_3R_44();
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                                break;
                            case 4:
                                jj_3_2();
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                                break;
                            case 5:
                                jj_3_1();
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                                break;
                            default:
                                changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                        }
                    } else {
                        changeLogParserJJCalls1 = changeLogParserJJCalls.next;
                    }

                    changeLogParserJJCalls = changeLogParserJJCalls1;
                } while (changeLogParserJJCalls != null);
            } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
            }

            bb = ++ba;
        }

        jj_rescan = false;
    }

    public static boolean jj_3R_56() {
        return jj_scan_token(PUBLIC);
    }

    public static final void ParameterChangeData() throws ChangeLogParseException {
        ASTParameterChangeData aSTParameterChangeData = new ASTParameterChangeData();
        boolean bl = true;
        jjtree.openNodeScope(aSTParameterChangeData);
        try {
            ChangeLogParser.jj_consume_token(15);
            ChangeLogParser.Name();
            jjtree.closeNodeScope(aSTParameterChangeData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTParameterChangeData);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_57() {
        if (jj_3R_42()) {
            return true;
        }

        ChangeLogToken changeLogToken;
        do {
            changeLogToken = jj_scanpos;
        } while (!jj_3_3());

        jj_scanpos = changeLogToken;

        do {
            changeLogToken = jj_scanpos;
        } while (!jj_3R_50());

        jj_scanpos = changeLogToken;
        return false;
    }

    public static boolean jj_3R_58() {
        return jj_scan_token(VOLATILE);
    }

    public static final void NewFieldName() throws ChangeLogParseException {
        ChangeLogASTNewFieldName changeLogASTNewFieldName = new ChangeLogASTNewFieldName();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTNewFieldName);
        try {
            ChangeLogParser.Name();
            jjtree.closeNodeScope(changeLogASTNewFieldName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTNewFieldName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_59() {
        return jj_scan_token(NATIVE);
    }

    public static final void OldClassName() throws ChangeLogParseException {
        ChangeLogASTOldClassName changeLogASTOldClassName = new ChangeLogASTOldClassName();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTOldClassName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(changeLogASTOldClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTOldClassName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_60() {
        return jj_3R_57();
    }

    public static final void Integer() throws ChangeLogParseException {
        ASTInteger aSTInteger = new ASTInteger();
        boolean bl = true;
        jjtree.openNodeScope(aSTInteger);

        try {
            ChangeLogToken changeLogToken = jj_consume_token(INTEGER_LITERAL);
            jjtree.closeNodeScope(aSTInteger);
            bl = false;
            aSTInteger.setValue(changeLogToken.k);
        } finally {
            if (bl) {
                jjtree.closeNodeScope(aSTInteger);
            }
        }
    }

    public static boolean jj_3R_61() {
        return jj_scan_token(ENUM);
    }

    public static boolean jj_3R_62() {
        return jj_scan_token(ABSTRACT);
    }

    public static boolean jj_3_2() {
        if (jj_3R_7()) {
            return true;
        } else {
            return jj_3R_43() ? true : jj_scan_token(LPAREN);
        }
    }

    public static final void NewClassName() throws ChangeLogParseException {
        ChangeLogASTNewClassName changeLogASTNewClassName = new ChangeLogASTNewClassName();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTNewClassName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(changeLogASTNewClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTNewClassName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3_3() {
        return jj_scan_token(DOT) ? true : jj_3R_42();
    }

    public static final void ClassChange() throws ChangeLogParseException {
        ASTClassChange aSTClassChange = new ASTClassChange();
        boolean bl = true;
        jjtree.openNodeScope(aSTClassChange);
        try {
            ChangeLogParser.jj_consume_token(35);
            while (ChangeLogParser.jj_2_1()) {
                ChangeLogParser.ClassModifier();
            }
            ChangeLogParser.OldClassName();
            switch (ChangeLogParser.jj_nt.H) {
                case 24: {
                    ChangeLogParser.jj_consume_token(24);
                    ChangeLogParser.NewClassName();
                    break;
                }
                case 64: {
                    ChangeLogParser.jj_consume_token(64);
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[19] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
            }
            switch (ChangeLogParser.jj_nt.H) {
                case 44: {
                    ChangeLogParser.jj_consume_token(44);
                    ChangeLogParser.SourceName();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[20] = jj_gen;
                }
            }
            block9:
            switch (ChangeLogParser.jj_nt.H) {
                case 55: {
                    ChangeLogParser.jj_consume_token(55);
                    ChangeLogParser.DisregardedNameList();
                    while (true) {
                        switch (ChangeLogParser.jj_nt.H) {
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                            case 30:
                            case 31:
                            case 32:
                            case 33:
                            case 34:
                            case 36:
                            case 37:
                            case 38:
                            case 39:
                            case 40:
                            case 42:
                            case 43:
                            case 45:
                            case 47:
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 57:
                            case 58:
                            case 59:
                            case 61:
                            case 64:
                            case 67:
                            case 75:
                            case 76:
                            case 77: {
                                break;
                            }
                            default: {
                                ChangeLogParser.jj_la1[21] = jj_gen;
                                break block9;
                            }
                        }
                        ChangeLogParser.FieldNameChange();
                    }
                }
                default: {
                    ChangeLogParser.jj_la1[22] = jj_gen;
                }
            }
            block15:
            switch (ChangeLogParser.jj_nt.H) {
                case 56: {
                    ChangeLogParser.jj_consume_token(56);
                    ChangeLogParser.DisregardedNameList();
                    while (true) {
                        switch (ChangeLogParser.jj_nt.H) {
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                            case 30:
                            case 31:
                            case 32:
                            case 33:
                            case 34:
                            case 36:
                            case 37:
                            case 38:
                            case 39:
                            case 40:
                            case 42:
                            case 43:
                            case 45:
                            case 47:
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 57:
                            case 58:
                            case 59:
                            case 61:
                            case 64:
                            case 67:
                            case 75:
                            case 76:
                            case 77: {
                                break;
                            }
                            default: {
                                ChangeLogParser.jj_la1[23] = jj_gen;
                                break block15;
                            }
                        }
                        ChangeLogParser.MethodNameChange();
                    }
                }
                default: {
                    ChangeLogParser.jj_la1[24] = jj_gen;
                }
            }
            block21:
            switch (ChangeLogParser.jj_nt.H) {
                case 65: {
                    ChangeLogParser.jj_consume_token(65);
                    ChangeLogParser.DisregardedNameList();
                    while (true) {
                        switch (ChangeLogParser.jj_nt.H) {
                            case 75: {
                                break;
                            }
                            default: {
                                ChangeLogParser.jj_la1[25] = jj_gen;
                                break block21;
                            }
                        }
                        ChangeLogParser.LineNumberChange();
                    }
                }
                default: {
                    ChangeLogParser.jj_la1[26] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTClassChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTClassChange);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_63() {
        return jj_scan_token(EXECUTE);
    }

    public static final void MethodReturnType() throws ChangeLogParseException {
        ASTMethodReturnType aSTMethodReturnType = new ASTMethodReturnType();
        boolean bl = true;
        jjtree.openNodeScope(aSTMethodReturnType);
        try {
            ChangeLogParser.QualifiedType();
            jjtree.closeNodeScope(aSTMethodReturnType);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTMethodReturnType);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_64() {
        return jj_scan_token(EXCLUDE);
    }

    public static final void MethodParameterClassNames() throws ChangeLogParseException {
        ASTMethodParameterClassNames aSTMethodParameterClassNames = new ASTMethodParameterClassNames();
        boolean bl = true;
        jjtree.openNodeScope(aSTMethodParameterClassNames);
        try {
            ChangeLogParser.jj_consume_token(71);
            ChangeLogParser.OldClassName();
            ChangeLogParser.OldClassName();
            ChangeLogParser.OldClassName();
            switch (ChangeLogParser.jj_nt.H) {
                case 41: {
                    ChangeLogParser.jj_consume_token(41);
                    ChangeLogParser.OldModuleName();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[16] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTMethodParameterClassNames);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTMethodParameterClassNames);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_65() {
        return jj_scan_token(CLINIT);
    }

    public static boolean jj_3R_66() {
        return jj_scan_token(SIGNATURE_NOT_CHANGED);
    }

    public static final void NewMethodSignature() throws ChangeLogParseException {
        ASTNewMethodSignature aSTNewMethodSignature = new ASTNewMethodSignature();
        boolean bl = true;
        jjtree.openNodeScope(aSTNewMethodSignature);
        try {
            ChangeLogParser.Name();
            ChangeLogParser.jj_consume_token(14);
            switch (ChangeLogParser.jj_nt.H) {
                case 25:
                case 26:
                case 27:
                case 28:
                case 31:
                case 32:
                case 33:
                case 36:
                case 37:
                case 42:
                case 43:
                case 48:
                case 52:
                case 57:
                case 58:
                case 64:
                case 67:
                case 75:
                case 76:
                case 77: {
                    ChangeLogParser.MethodArguments();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[44] = jj_gen;
                }
            }
            ChangeLogParser.jj_consume_token(17);
            switch (ChangeLogParser.jj_nt.H) {
                case 23: {
                    ChangeLogParser.ParameterObfuscated();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[45] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTNewMethodSignature);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTNewMethodSignature);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_67() {
        ChangeLogToken changeLogToken = jj_scanpos;
        if (jj_3R_30()) {
            jj_scanpos = changeLogToken;
            if (jj_3R_36()) {
                jj_scanpos = changeLogToken;
                if (jj_3R_33()) {
                    jj_scanpos = changeLogToken;
                    if (jj_3R_25()) {
                        jj_scanpos = changeLogToken;
                        if (jj_3R_62()) {
                            jj_scanpos = changeLogToken;
                            if (jj_3R_49()) {
                                jj_scanpos = changeLogToken;
                                if (jj_3R_58()) {
                                    jj_scanpos = changeLogToken;
                                    if (jj_3R_54()) {
                                        jj_scanpos = changeLogToken;
                                        if (jj_3R_47()) {
                                            jj_scanpos = changeLogToken;
                                            if (jj_3R_79()) {
                                                jj_scanpos = changeLogToken;
                                                if (jj_3R_82()) {
                                                    jj_scanpos = changeLogToken;
                                                    if (jj_3R_75()) {
                                                        jj_scanpos = changeLogToken;
                                                        if (jj_3R_10()) {
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static final void PackageChange() throws ChangeLogParseException {
        ASTPackageChange aSTPackageChange = new ASTPackageChange();
        boolean bl = true;
        jjtree.openNodeScope(aSTPackageChange);
        try {
            ChangeLogParser.jj_consume_token(46);
            ChangeLogParser.OldPackageName();
            block2:
            switch (ChangeLogParser.jj_nt.H) {
                case 24: {
                    ChangeLogParser.jj_consume_token(24);
                    switch (ChangeLogParser.jj_nt.H) {
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                        case 30:
                        case 31:
                        case 32:
                        case 33:
                        case 34:
                        case 36:
                        case 37:
                        case 38:
                        case 40:
                        case 42:
                        case 43:
                        case 45:
                        case 47:
                        case 48:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 57:
                        case 58:
                        case 59:
                        case 64:
                        case 67:
                        case 75:
                        case 76:
                        case 77: {
                            ChangeLogParser.NewPackageName();
                            break block2;
                        }
                    }
                    ChangeLogParser.jj_la1[17] = jj_gen;
                    break;
                }
                case 64: {
                    ChangeLogParser.PackageNameNotChanged();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[18] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
            }
            jjtree.closeNodeScope(aSTPackageChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTPackageChange);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_68() {
        return jj_3R_67();
    }

    public static boolean jj_3R_69() {
        return jj_scan_token(ENHANCED);
    }

    public static boolean jj_3R_70() {
        return jj_scan_token(FALSE);
    }

    public static boolean jj_3R_71() {
        return jj_scan_token(57);
    }

    public static boolean jj_3R_72() {
        return jj_scan_token(LIGHT);
    }

    public static final void MemberFlowObfuscationData() throws ChangeLogParseException {
        ASTMemberFlowObfuscationData aSTMemberFlowObfuscationData = new ASTMemberFlowObfuscationData();
        boolean bl = true;
        jjtree.openNodeScope(aSTMemberFlowObfuscationData);
        try {
            ChangeLogParser.jj_consume_token(60);
            ChangeLogParser.FlowObfuscationClassName();
            ChangeLogParser.jj_consume_token(29);
            ChangeLogParser.MemberData();
            jjtree.closeNodeScope(aSTMemberFlowObfuscationData);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTMemberFlowObfuscationData);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_73() {
        ChangeLogToken changeLogToken = jj_scanpos;
        if (jj_3R_56()) {
            jj_scanpos = changeLogToken;
            if (jj_3R_34()) {
                jj_scanpos = changeLogToken;
                if (jj_3R_55()) {
                    jj_scanpos = changeLogToken;
                    if (jj_3R_74()) {
                        jj_scanpos = changeLogToken;
                        if (jj_3R_18()) {
                            jj_scanpos = changeLogToken;
                            if (jj_3R_61()) {
                                jj_scanpos = changeLogToken;
                                if (jj_3R_3()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean jj_3R_74() {
        return jj_scan_token(ABSTRACT);
    }

    public static final void OldModuleName() throws ChangeLogParseException {
        ASTOldModuleName aSTOldModuleName = new ASTOldModuleName();
        boolean bl = true;
        jjtree.openNodeScope(aSTOldModuleName);
        try {
            ChangeLogParser.NameList();
            jjtree.closeNodeScope(aSTOldModuleName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTOldModuleName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_75() {
        return jj_scan_token(ENUM);
    }

    public static final void ClassModifier() throws ChangeLogParseException {
        ChangeLogASTClassModifier changeLogASTClassModifier = new ChangeLogASTClassModifier();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTClassModifier);

        try {
            switch (jj_nt.H) {
                case 26:
                    ChangeLogToken changeLogToken6 = jj_consume_token(ENUM);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken6.k);
                    break;
                case 30:
                    ChangeLogToken changeLogToken5 = jj_consume_token(FINAL);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken5.k);
                    break;
                case 38:
                    ChangeLogToken changeLogToken4 = jj_consume_token(PUBLIC);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken4.k);
                    break;
                case 49:
                    ChangeLogToken changeLogToken3 = jj_consume_token(ABSTRACT);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken3.k);
                    break;
                case 52:
                    ChangeLogToken changeLogToken2 = jj_consume_token(SYNTHETIC);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken2.k);
                    break;
                case 53:
                    ChangeLogToken changeLogToken1 = jj_consume_token(INTERFACE);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken1.k);
                    break;
                case 58:
                    ChangeLogToken changeLogToken = jj_consume_token(ANNOTATION);
                    jjtree.closeNodeScope(changeLogASTClassModifier);
                    bl = false;
                    changeLogASTClassModifier.setValue(changeLogToken.k);
                    break;
                default:
                    jj_la1[41] = jj_gen;
                    jj_consume_token(-1);
                    throw new ChangeLogParseException();
            }
        } finally {
            if (bl) {
                jjtree.closeNodeScope(changeLogASTClassModifier);
            }
        }
    }

    public static boolean jj_3R_76() {
        return jj_scan_token(PUBLIC);
    }

    public static boolean jj_3R_77() {
        return jj_scan_token(INIT);
    }

    public static boolean jj_3R_78() {
        return jj_scan_token(INTERFACE);
    }

    public static boolean jj_2_6() {
        jj_la = 4;
        jj_lastpos = jj_scanpos = token;

        try {
            return !jj_3R_44();
        } catch (ChangeLogParserLookaheadSuccess changeLogParserLookaheadSuccess) {
        } finally {
            jj_save(3, 4);
        }

        return true;
    }

    public static final void ArrayLevel() throws ChangeLogParseException {
        ChangeLogASTArrayLevel changeLogASTArrayLevel = new ChangeLogASTArrayLevel();
        jjtree.openNodeScope(changeLogASTArrayLevel);

        try {
            jj_consume_token(LBRACKET);
            jj_consume_token(RBRACKET);
        } finally {
            jjtree.closeNodeScope(changeLogASTArrayLevel);
        }
    }

    public static final void FieldType() throws ChangeLogParseException {
        ASTFieldType aSTFieldType = new ASTFieldType();
        boolean bl = true;
        jjtree.openNodeScope(aSTFieldType);
        try {
            ChangeLogParser.QualifiedType();
            jjtree.closeNodeScope(aSTFieldType);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTFieldType);
                }
                throw throwable2;
            }
        }
    }

    public static final void ObfuscateReferencesClassName() throws ChangeLogParseException {
        ASTObfuscateReferencesClassName aSTObfuscateReferencesClassName = new ASTObfuscateReferencesClassName();
        boolean bl = true;
        jjtree.openNodeScope(aSTObfuscateReferencesClassName);
        try {
            ChangeLogParser.jj_consume_token(70);
            ChangeLogParser.OldClassName();
            switch (ChangeLogParser.jj_nt.H) {
                case 41: {
                    ChangeLogParser.jj_consume_token(41);
                    ChangeLogParser.OldModuleName();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[15] = jj_gen;
                }
            }
            jjtree.closeNodeScope(aSTObfuscateReferencesClassName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTObfuscateReferencesClassName);
                }
                throw throwable2;
            }
        }
    }

    public static boolean jj_3R_79() {
        return jj_scan_token(NATIVE);
    }

    public static boolean jj_3R_80() {
        return jj_scan_token(NORMAL);
    }

    public static boolean jj_3R_81() {
        return jj_scan_token(SYNTHETIC);
    }

    public static boolean jj_3R_82() {
        return jj_scan_token(SYNTHETIC);
    }

    public static final void PackageNameNotChanged() throws ChangeLogParseException {
        ASTPackageNameNotChanged aSTPackageNameNotChanged = new ASTPackageNameNotChanged();
        jjtree.openNodeScope(aSTPackageNameNotChanged);

        try {
            jj_consume_token(NAME_NOT_CHANGED);
        } finally {
            jjtree.closeNodeScope(aSTPackageNameNotChanged);
        }
    }

    public static final void Manufactured() throws ChangeLogParseException {
        ASTManufactured aSTManufactured = new ASTManufactured();
        jjtree.openNodeScope(aSTManufactured);

        try {
            jj_consume_token(MANUFACTURED);
        } finally {
            jjtree.closeNodeScope(aSTManufactured);
        }
    }

    public static final void SourceName() throws ChangeLogParseException {
        ASTSourceName aSTSourceName = new ASTSourceName();
        boolean bl = true;
        jjtree.openNodeScope(aSTSourceName);
        try {
            switch (ChangeLogParser.jj_nt.H) {
                case 73: {
                    ChangeLogParser.StringLiteral();
                    break;
                }
                case 25:
                case 26:
                case 27:
                case 28:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 36:
                case 37:
                case 38:
                case 40:
                case 42:
                case 43:
                case 45:
                case 47:
                case 48:
                case 50:
                case 51:
                case 52:
                case 53:
                case 57:
                case 58:
                case 59:
                case 64:
                case 67:
                case 75:
                case 76:
                case 77: {
                    ChangeLogParser.NameList();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[27] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
            }
            jjtree.closeNodeScope(aSTSourceName);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(aSTSourceName);
                }
                throw throwable2;
            }
        }
    }

    public static final void FieldNameChange() throws ChangeLogParseException {
        ChangeLogASTFieldNameChange changeLogASTFieldNameChange = new ChangeLogASTFieldNameChange();
        boolean bl = true;
        jjtree.openNodeScope(changeLogASTFieldNameChange);
        try {
            while (ChangeLogParser.jj_2_5()) {
                ChangeLogParser.MemberModifier();
            }
            if (ChangeLogParser.jj_2_4()) {
                ChangeLogParser.FieldType();
            }
            ChangeLogParser.OldFieldName();
            switch (ChangeLogParser.jj_nt.H) {
                case 24: {
                    ChangeLogParser.jj_consume_token(24);
                    ChangeLogParser.NewFieldName();
                    break;
                }
                case 64: {
                    ChangeLogParser.jj_consume_token(64);
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[28] = jj_gen;
                    ChangeLogParser.jj_consume_token(-1);
                    throw new ChangeLogParseException();
                }
            }
            switch (ChangeLogParser.jj_nt.H) {
                case 63: {
                    ChangeLogParser.Manufactured();
                    break;
                }
                default: {
                    ChangeLogParser.jj_la1[29] = jj_gen;
                }
            }
            jjtree.closeNodeScope(changeLogASTFieldNameChange);
        } catch (Throwable throwable) {
            try {
                jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ChangeLogParseException) {
                    throw (ChangeLogParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    jjtree.closeNodeScope(changeLogASTFieldNameChange);
                }
                throw throwable2;
            }
        }
    }

    public static void ReInit(Reader reader1) {
        jj_input_stream.ReInit_v(reader1);
        ChangeLogTokenManager.ReInit(jj_input_stream);
        token = new ChangeLogToken();
        token.h = jj_nt = ChangeLogTokenManager.getNextToken();
        jjtree.reset();
        jj_gen = 0;
        int ba = 0;
        int bc = 0;

        for (byte bd = 60; bc < bd; bd = 60) {
            jj_la1[ba] = -1;
            bc = ++ba;
        }

        for (int i = 0; i < jj_2_rtns.length; i++) {
            jj_2_rtns[i] = new ChangeLogParserJJCalls();
        }
    }

    public static boolean jj_3R_83() {
        return jj_scan_token(27);
    }

    public static boolean jj_3R_84() {
        return jj_3R_43();
    }
}
