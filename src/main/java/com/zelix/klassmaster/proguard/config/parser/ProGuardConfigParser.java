package com.zelix.klassmaster.proguard.config.parser;

import com.zelix.klassmaster.proguard.ProGuardConfigParserJJCalls;
import com.zelix.klassmaster.proguard.ProGuardConfigParserLookaheadSuccess;
import com.zelix.klassmaster.proguard.ProGuardConfigTreeState;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAdaptClassStringsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAdaptKotlinMetadataOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAdaptResourceFileContentsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAdaptResourceFilenamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAddConfigurationDebuggingOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAllowAccessModificationOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAllowShrinkingClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAndroidOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTApplyMappingOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAssumeNoEscapingParametersOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAssumeNoExternalReturnValuesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAssumeNoExternalSideEffectsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAssumeNoSideEffectsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAssumeValuesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTAtClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTBangClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTBaseDirectoryOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTBomClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTClassObfuscationDictionaryOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontCompressOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontNoteOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontObfuscateOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontOptimizeOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontPreverifyOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontProcessKotlinMetadataOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontShrinkOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontSkipNonPublicLibraryClassMembersOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontSkipNonPublicLibraryClassesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontWarnOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDontusemixedcaseclassnamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTDumpOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTFieldsClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTFlattenPackageHierarchyOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTForceProcessingOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTIfOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTIgnoreWarningsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTInJarsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTIncludeOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTIntegerClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTInterfaceClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepAttributesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepClassMemberNamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepClassMembersOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepClassesWithMemberNamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepClassesWithMembersOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepCodeOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepDirectoriesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepKotlinMetadataOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepNamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepPackageNamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTKeepParameterNamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTLbraceClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTLibraryJarsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTLparenClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTLparenClause2;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTMergeInterfacesAggressivelyOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTMethodsClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTMicroEditionOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTObfuscationDictionaryOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTOptimizationPassesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTOptimizationsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTOutJarsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTOverloadAggressivelyOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPackageObfuscationDictionaryOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPercentClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPercentClause2;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPercentClause3;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPercentClause4;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPrintConfigurationOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPrintMappingOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPrintSeedsOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPrintUsageOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction1;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction2;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction3;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction4;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction5;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction6;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction7;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTProduction8;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPublicClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTPublicClause2;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTQuote122Clause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTRenameSourceFileAttributeOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTRepackageClassesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTSkipNonPublicLibraryClassesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTStarClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTStarClause2;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTStarClause3;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTTargetOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTTildeClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTUseUniqueClassMemberNamesOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTVerboseOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTWhyAreYouKeepingOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ASTZipAlignOption;
import com.zelix.klassmaster.proguard.config.parser.ast.ProGuardConfigASTExtendsClause;
import com.zelix.klassmaster.proguard.config.parser.ast.ProGuardConfigASTImplementsClause;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProGuardConfigParser implements ProGuardConfigTreeConstants, ProGuardConfigConstants {
    public static int[] jj_la1_1;
    public static int[] jj_la1_0;
    public static int[] jj_la1_3;
    public static int[] jj_la1_4;
    public static int[] jj_la1_2;
    private static final String ESCAPED_QUOTE = "\"\"";
    public int[] jj_expentry;
    public ProGuardConfigToken jj_lastpos;
    public ProGuardConfigToken jj_scanpos;
    public int jj_la;
    public int jj_endpos;
    public int lastOptionKind;
    public ProGuardConfigTreeState jjtree = new ProGuardConfigTreeState();
    public final int[] jj_la1 = new int[71];
    public final ProGuardConfigParserJJCalls[] jj_2_rtns = new ProGuardConfigParserJJCalls[6];
    public boolean jj_rescan = false;
    public int jj_gc = 0;
    public final ProGuardConfigParserLookaheadSuccess jj_ls = new ProGuardConfigParserLookaheadSuccess(null);
    public List jj_expentries = new ArrayList();
    public int jj_kind = -1;
    public int[] jj_lasttokens = new int[100];
    public ProGuardConfigSimpleCharStream jj_input_stream;
    public ProGuardConfigTokenManager token_source;
    public ProGuardConfigToken token;
    public ProGuardConfigToken jj_nt;
    public int jj_gen;

    public final void Production1() throws ProGuardConfigParseException {
        ASTProduction1 aSTProduction1 = new ASTProduction1();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction1);
        try {
            if (this.jj_2_6()) {
                switch (this.jj_nt.s) {
                    case 18:
                    case 19:
                    case 24:
                    case 26:
                    case 125:
                    case 126:
                    case 127: {
                        this.PercentClause4();
                        break;
                    }
                    default: {
                        this.jj_la1[50] = this.jj_gen;
                    }
                }
                this.FieldsClause();
            } else {
                switch (this.jj_nt.s) {
                    case 18:
                    case 19:
                    case 24:
                    case 26:
                    case 125:
                    case 126:
                    case 127: {
                        this.PercentClause2();
                        this.StarClause();
                        break;
                    }
                    default: {
                        this.jj_la1[51] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ProGuardConfigParseException();
                    }
                }
            }
            this.jjtree.closeNodeScope(aSTProduction1);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTProduction1);
                }
                throw throwable2;
            }
        }
    }

    public final void PercentClause() throws ProGuardConfigParseException {
        ASTPercentClause aSTPercentClause = new ASTPercentClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPercentClause);

        try {
            switch (this.jj_nt.s) {
                case 18:
                    ProGuardConfigToken proGuardConfigToken7 = this.jj_consume_token(PERCENT);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken7.M);
                    break;
                case 19:
                    ProGuardConfigToken proGuardConfigToken6 = this.jj_consume_token(STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken6.M);
                    break;
                case 24:
                    ProGuardConfigToken proGuardConfigToken5 = this.jj_consume_token(DOUBLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken5.M);
                    break;
                case 26:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(TRIPLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken4.M);
                    break;
                case 27:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(ELLIPSIS);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken3.M);
                    break;
                case 125:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken2.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken1.M);
                    break;
                case 127:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_TYPE);
                    this.jjtree.closeNodeScope(aSTPercentClause);
                    bl = false;
                    aSTPercentClause.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[57] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPercentClause);
            }
        }
    }

    public static void jj_la1_init_0() {
        jj_la1_0 = new int[]{
                1024,
                1644167168,
                1644167168,
                4096,
                0,
                4096,
                0,
                4096,
                4096,
                4096,
                4096,
                4096,
                0,
                0,
                4096,
                0,
                0,
                4096,
                0,
                0,
                0,
                4096,
                65536,
                0,
                4096,
                0,
                4096,
                0,
                4096,
                0,
                4096,
                0,
                17367040,
                0,
                0,
                65536,
                65536,
                1048576,
                2048,
                1048576,
                4096,
                4096,
                16384,
                0,
                0,
                353189888,
                4194304,
                16384,
                268500992,
                84672512,
                84672512,
                84672512,
                218890240,
                84672512,
                16384,
                16384,
                4096,
                218890240,
                84672512,
                84672512,
                84672512,
                4096,
                65536,
                17301504,
                17301504,
                65536,
                -1879048192,
                65536,
                0,
                65536,
                268435456
        };
    }

    public static void jj_la1_init_4() {
        jj_la1_4 = new int[]{
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
                0,
                0,
                0,
                0,
                0,
                0,
                0
        };
    }

    public boolean jj_3R_1() {
        return this.jj_3R_8();
    }

    public boolean jj_3R_2() {
        return this.jj_scan_token(DOUBLE_STAR);
    }

    public final void StarClause() throws ProGuardConfigParseException {
        ASTStarClause aSTStarClause = new ASTStarClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTStarClause);

        try {
            switch (this.jj_nt.s) {
                case 19:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(STAR);
                    this.jjtree.closeNodeScope(aSTStarClause);
                    bl = false;
                    aSTStarClause.setValue(proGuardConfigToken4.M);
                    break;
                case 24:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(DOUBLE_STAR);
                    this.jjtree.closeNodeScope(aSTStarClause);
                    bl = false;
                    aSTStarClause.setValue(proGuardConfigToken3.M);
                    break;
                case 36:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(INIT);
                    this.jjtree.closeNodeScope(aSTStarClause);
                    bl = false;
                    aSTStarClause.setValue(proGuardConfigToken2.M);
                    break;
                case 125:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(aSTStarClause);
                    bl = false;
                    aSTStarClause.setValue(proGuardConfigToken1.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTStarClause);
                    bl = false;
                    aSTStarClause.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[64] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTStarClause);
            }
        }
    }

    public boolean jj_3R_3() {
        return this.jj_3R_32();
    }

    public ProGuardConfigToken jj_consume_token(int jj_kind) throws ProGuardConfigParseException {
        ProGuardConfigToken proGuardConfigToken = this.token;
        if ((this.token = this.jj_nt).Y != null) {
            this.jj_nt = this.jj_nt.Y;
        } else {
            this.jj_nt = this.jj_nt.Y = this.token_source.getNextToken();
        }

        if (this.token.s != jj_kind) {
            this.jj_nt = this.token;
            this.token = proGuardConfigToken;
            this.jj_kind = jj_kind;
            throw this.generateParseException();
        }

        this.jj_gen++;
        if (++this.jj_gc > 100) {
            this.jj_gc = 0;

            for (int i = 0; i < this.jj_2_rtns.length; i++) {
                ProGuardConfigParserJJCalls proGuardConfigParserJJCalls = this.jj_2_rtns[i];

                while (proGuardConfigParserJJCalls != null) {
                    ProGuardConfigParserJJCalls proGuardConfigParserJJCalls1;
                    if (proGuardConfigParserJJCalls.gen < this.jj_gen) {
                        proGuardConfigParserJJCalls.first = null;
                        proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                    } else {
                        proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                    }

                    proGuardConfigParserJJCalls = proGuardConfigParserJJCalls1;
                }
            }
        }

        return this.token;
    }

    public boolean jj_2_1() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_25();
        } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
        } finally {
            this.jj_save(0);
        }

        return true;
    }

    public final void DontNoteOption() throws ProGuardConfigParseException {
        ASTDontNoteOption aSTDontNoteOption = new ASTDontNoteOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontNoteOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            ProGuardConfigTreeState proGuardConfigTreeState;
            proGuardConfigToken = this.jj_consume_token(OPT_DONTNOTE);
            this.lastOptionKind = 57;
            label83:
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Quote122Clause();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[30] = this.jj_gen;
                                proGuardConfigTreeState = this.jjtree;
                                break label83;
                        }
                    }
                default:
                    this.jj_la1[31] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTDontNoteOption);
            bl = false;
            aSTDontNoteOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontNoteOption);
            }
        }
    }

    public boolean jj_scan_token(int ba) {
        if (this.jj_scanpos == this.jj_lastpos) {
            this.jj_la--;
            if (this.jj_scanpos.Y == null) {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.Y = this.token_source.getNextToken();
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.Y;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.Y;
        }

        if (this.jj_rescan) {
            int bb = 0;

            ProGuardConfigToken proGuardConfigToken;
            for (proGuardConfigToken = this.token;
                 proGuardConfigToken != null && proGuardConfigToken != this.jj_scanpos;
                 proGuardConfigToken = proGuardConfigToken.Y
            ) {
                bb++;
            }

            if (proGuardConfigToken != null) {
                this.jj_add_error_token(ba, bb);
            }
        }

        if (this.jj_scanpos.s != ba) {
            return true;
        } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        } else {
            return false;
        }
    }

    public boolean jj_3R_4() {
        return this.jj_scan_token(NAME);
    }

    public final void DontSkipNonPublicLibraryClassMembersOption() throws ProGuardConfigParseException {
        ASTDontSkipNonPublicLibraryClassMembersOption aSTDontSkipNonPublicLibraryClassMembersOption = new ASTDontSkipNonPublicLibraryClassMembersOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontSkipNonPublicLibraryClassMembersOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTSKIPNONPUBLICLIBRARYCLASSMEMBERS);
            this.lastOptionKind = 120;
            this.jjtree.closeNodeScope(aSTDontSkipNonPublicLibraryClassMembersOption);
            bl = false;
            aSTDontSkipNonPublicLibraryClassMembersOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontSkipNonPublicLibraryClassMembersOption);
            }
        }
    }

    public final void KeepClassMembersOption() throws ProGuardConfigParseException {
        ASTKeepClassMembersOption aSTKeepClassMembersOption = new ASTKeepClassMembersOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepClassMembersOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPCLASSMEMBERS);
            this.lastOptionKind = 86;
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.AllowShrinkingClause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[7] = this.jj_gen;
                        this.LbraceClause();
                        this.jjtree.closeNodeScope(aSTKeepClassMembersOption);
                        bl = false;
                        aSTKeepClassMembersOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepClassMembersOption);
            }
        }
    }

    public final void AtClause() throws ProGuardConfigParseException {
        ASTAtClause aSTAtClause = new ASTAtClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAtClause);
        try {
            this.jj_consume_token(14);
            this.StarClause2();
            this.jjtree.closeNodeScope(aSTAtClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTAtClause);
                }
                throw throwable2;
            }
        }
    }

    public final void DontCompressOption() throws ProGuardConfigParseException {
        ASTDontCompressOption aSTDontCompressOption = new ASTDontCompressOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontCompressOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTCOMPRESS);
            this.lastOptionKind = 74;
            this.jjtree.closeNodeScope(aSTDontCompressOption);
            bl = false;
            aSTDontCompressOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontCompressOption);
            }
        }
    }

    public final void LibraryJarsOption() throws ProGuardConfigParseException {
        ASTLibraryJarsOption aSTLibraryJarsOption = new ASTLibraryJarsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLibraryJarsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_LIBRARYJARS);
            this.lastOptionKind = 67;
            this.TildeClause();
            this.jjtree.closeNodeScope(aSTLibraryJarsOption);
            bl = false;
            aSTLibraryJarsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLibraryJarsOption);
            }
        }
    }

    public final void AndroidOption() throws ProGuardConfigParseException {
        ASTAndroidOption aSTAndroidOption = new ASTAndroidOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndroidOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ANDROID);
            this.lastOptionKind = 51;
            this.jjtree.closeNodeScope(aSTAndroidOption);
            bl = false;
            aSTAndroidOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAndroidOption);
            }
        }
    }

    public boolean jj_3R_5() {
        ProGuardConfigToken proGuardConfigToken = this.jj_scanpos;
        if (this.jj_3R_1()) {
            this.jj_scanpos = proGuardConfigToken;
            if (this.jj_3_2()) {
                this.jj_scanpos = proGuardConfigToken;
                if (this.jj_3_3()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean jj_3_1() {
        return this.jj_scan_token(COMMA) ? true : this.jj_3R_29();
    }

    public final void Quote122Clause() throws ProGuardConfigParseException {
        ASTQuote122Clause aSTQuote122Clause = new ASTQuote122Clause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTQuote122Clause);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(QUOTE_122);
            this.jjtree.closeNodeScope(aSTQuote122Clause);
            bl = false;
            String string = proGuardConfigToken.M;
            string = string.substring(1, string.length() - 1);
            string = ZkmStringUtils.replaceAll(string, ESCAPED_QUOTE, "\"");
            aSTQuote122Clause.setValue(string);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTQuote122Clause);
            }
        }
    }

    public final void BangClause() throws ProGuardConfigParseException {
        ASTBangClause aSTBangClause = new ASTBangClause();
        this.jjtree.openNodeScope(aSTBangClause);

        try {
            this.jj_consume_token(BANG);
        } finally {
            this.jjtree.closeNodeScope(aSTBangClause);
        }
    }

    public boolean jj_2_2() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_9();
        } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
        } finally {
            this.jj_save(5);
        }

        return true;
    }

    public final void IncludeOption() throws ProGuardConfigParseException {
        ASTIncludeOption aSTIncludeOption = new ASTIncludeOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIncludeOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_INCLUDE);
            this.lastOptionKind = 46;
            this.Quote122Clause();
            this.jjtree.closeNodeScope(aSTIncludeOption);
            bl = false;
            aSTIncludeOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIncludeOption);
            }
        }
    }

    public final void PackageObfuscationDictionaryOption() throws ProGuardConfigParseException {
        ASTPackageObfuscationDictionaryOption aSTPackageObfuscationDictionaryOption = new ASTPackageObfuscationDictionaryOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPackageObfuscationDictionaryOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_PACKAGEOBFUSCATIONDICTIONARY);
            this.lastOptionKind = 118;
            this.Quote122Clause();
            this.jjtree.closeNodeScope(aSTPackageObfuscationDictionaryOption);
            bl = false;
            aSTPackageObfuscationDictionaryOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPackageObfuscationDictionaryOption);
            }
        }
    }

    public final void DontPreverifyOption() throws ProGuardConfigParseException {
        ASTDontPreverifyOption aSTDontPreverifyOption = new ASTDontPreverifyOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontPreverifyOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTPREVERIFY);
            this.lastOptionKind = 77;
            this.jjtree.closeNodeScope(aSTDontPreverifyOption);
            bl = false;
            aSTDontPreverifyOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontPreverifyOption);
            }
        }
    }

    public final void IntegerClause() throws ProGuardConfigParseException {
        ASTIntegerClause aSTIntegerClause = new ASTIntegerClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIntegerClause);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(INTEGER);
            this.jjtree.closeNodeScope(aSTIntegerClause);
            bl = false;
            aSTIntegerClause.setValue(proGuardConfigToken.M);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIntegerClause);
            }
        }
    }

    public final void TildeClause() throws ProGuardConfigParseException {
        ASTTildeClause aSTTildeClause = new ASTTildeClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTTildeClause);
        try {
            this.Quote122Clause();
            switch (this.jj_nt.s) {
                case 20: {
                    this.LparenClause2();
                    break;
                }
                default: {
                    this.jj_la1[37] = this.jj_gen;
                }
            }
            block13:
            while (true) {
                switch (this.jj_nt.s) {
                    case 11: {
                        break;
                    }
                    default: {
                        this.jj_la1[38] = this.jj_gen;
                        break block13;
                    }
                }
                this.jj_consume_token(11);
                this.Quote122Clause();
                switch (this.jj_nt.s) {
                    case 20: {
                        this.LparenClause2();
                        continue block13;
                    }
                }
                this.jj_la1[39] = this.jj_gen;
            }
            this.jjtree.closeNodeScope(aSTTildeClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTTildeClause);
                }
                throw throwable2;
            }
        }
    }

    public final void KeepNamesOption() throws ProGuardConfigParseException {
        ASTKeepNamesOption aSTKeepNamesOption = new ASTKeepNamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepNamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPNAMES);
            this.lastOptionKind = 61;
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.AllowShrinkingClause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[9] = this.jj_gen;
                        this.LbraceClause();
                        this.jjtree.closeNodeScope(aSTKeepNamesOption);
                        bl = false;
                        aSTKeepNamesOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepNamesOption);
            }
        }
    }

    public boolean jj_2_3() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_33();
        } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
        } finally {
            this.jj_save(3);
        }

        return true;
    }

    public final void PercentClause2() throws ProGuardConfigParseException {
        ASTPercentClause2 aSTPercentClause2 = new ASTPercentClause2();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPercentClause2);

        try {
            switch (this.jj_nt.s) {
                case 18:
                    ProGuardConfigToken proGuardConfigToken6 = this.jj_consume_token(PERCENT);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken6.M);
                    break;
                case 19:
                    ProGuardConfigToken proGuardConfigToken5 = this.jj_consume_token(STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken5.M);
                    break;
                case 24:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(DOUBLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken4.M);
                    break;
                case 26:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(TRIPLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken3.M);
                    break;
                case 125:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken2.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken1.M);
                    break;
                case 127:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_TYPE);
                    this.jjtree.closeNodeScope(aSTPercentClause2);
                    bl = false;
                    aSTPercentClause2.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[58] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPercentClause2);
            }
        }
    }

    public boolean jj_3R_6() {
        return this.jj_scan_token(PERCENT);
    }

    public final void ZipAlignOption() throws ProGuardConfigParseException {
        ASTZipAlignOption aSTZipAlignOption = new ASTZipAlignOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTZipAlignOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ZIPALIGN);
            this.lastOptionKind = 56;
            this.IntegerClause();
            this.jjtree.closeNodeScope(aSTZipAlignOption);
            bl = false;
            aSTZipAlignOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTZipAlignOption);
            }
        }
    }

    public static void jj_la1_init_3() {
        jj_la1_3 = new int[]{
                0,
                33554175,
                33554175,
                0,
                67108864,
                0,
                256,
                0,
                0,
                0,
                0,
                0,
                67108864,
                67108864,
                0,
                67108864,
                67108864,
                0,
                67108864,
                67108864,
                67108864,
                0,
                1610612736,
                67108864,
                0,
                67108864,
                0,
                67108864,
                0,
                67108864,
                0,
                67108864,
                1610612736,
                67108864,
                67108864,
                0,
                1610612736,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                -536870912,
                0,
                0,
                0,
                -536870912,
                -536870912,
                -536870912,
                -536870912,
                -536870912,
                0,
                0,
                0,
                -536870912,
                -536870912,
                -536870912,
                -536870912,
                0,
                0,
                1610612736,
                1610612736,
                0,
                0,
                0,
                0,
                0,
                0
        };
    }

    




    public final void Production2() throws ProGuardConfigParseException {
        ASTProduction2 aSTProduction2 = new ASTProduction2();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction2);
        try {
            this.StarClause2();
            ProGuardConfigToken proGuardConfigToken = this.jj_nt;
            block7:
            while (true) {
                switch (proGuardConfigToken.s) {
                    case 12: {
                        break;
                    }
                    default: {
                        this.jj_la1[61] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(12);
                this.StarClause2();
                proGuardConfigToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTProduction2);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardConfigParseException)) throw (Error) throwable;
                throw (ProGuardConfigParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTProduction2);
                throw throwable2;
            }
        }
    }

    public final void DontShrinkOption() throws ProGuardConfigParseException {
        ASTDontShrinkOption aSTDontShrinkOption = new ASTDontShrinkOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontShrinkOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTSHRINK);
            this.lastOptionKind = 65;
            this.jjtree.closeNodeScope(aSTDontShrinkOption);
            bl = false;
            aSTDontShrinkOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontShrinkOption);
            }
        }
    }

    public boolean jj_3R_7() {
        return this.jj_scan_token(FIELDS);
    }

    public boolean jj_3R_8() {
        return this.jj_scan_token(METHODS);
    }

    public final void AssumeNoExternalSideEffectsOption() throws ProGuardConfigParseException {
        ASTAssumeNoExternalSideEffectsOption aSTAssumeNoExternalSideEffectsOption = new ASTAssumeNoExternalSideEffectsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAssumeNoExternalSideEffectsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ASSUMENOEXTERNALSIDEEFFECTS);
            this.lastOptionKind = 114;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTAssumeNoExternalSideEffectsOption);
            bl = false;
            aSTAssumeNoExternalSideEffectsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAssumeNoExternalSideEffectsOption);
            }
        }
    }

    public final void DontOptimizeOption() throws ProGuardConfigParseException {
        ASTDontOptimizeOption aSTDontOptimizeOption = new ASTDontOptimizeOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontOptimizeOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTOPTIMIZE);
            this.lastOptionKind = 73;
            this.jjtree.closeNodeScope(aSTDontOptimizeOption);
            bl = false;
            aSTDontOptimizeOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontOptimizeOption);
            }
        }
    }

    public boolean jj_2_4() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_15();
        } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
        } finally {
            this.jj_save(1);
        }

        return true;
    }

    public boolean jj_3R_9() {
        return this.jj_3R_13() ? true : this.jj_3R_10();
    }

    public final void AdaptResourceFileContentsOption() throws ProGuardConfigParseException {
        ASTAdaptResourceFileContentsOption aSTAdaptResourceFileContentsOption = new ASTAdaptResourceFileContentsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAdaptResourceFileContentsOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            proGuardConfigToken = this.jj_consume_token(OPT_ADAPTRESOURCEFILECONTENTS);
            this.lastOptionKind = 108;
            label81:
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Quote122Clause();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[28] = this.jj_gen;
                                break label81;
                        }
                    }
                default:
                    this.jj_la1[29] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTAdaptResourceFileContentsOption);
            bl = false;
            aSTAdaptResourceFileContentsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAdaptResourceFileContentsOption);
            }
        }
    }

    public final void RepackageClassesOption() throws ProGuardConfigParseException {
        ASTRepackageClassesOption aSTRepackageClassesOption = new ASTRepackageClassesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRepackageClassesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_REPACKAGECLASSES);
            this.lastOptionKind = 88;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[20] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTRepackageClassesOption);
            bl = false;
            aSTRepackageClassesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTRepackageClassesOption);
            }
        }
    }

    public boolean jj_3R_10() {
        ProGuardConfigToken proGuardConfigToken = this.jj_scanpos;
        if (this.jj_3R_30()) {
            this.jj_scanpos = proGuardConfigToken;
            if (this.jj_3R_2()) {
                this.jj_scanpos = proGuardConfigToken;
                if (this.jj_3R_12()) {
                    this.jj_scanpos = proGuardConfigToken;
                    if (this.jj_3R_4()) {
                        this.jj_scanpos = proGuardConfigToken;
                        if (this.jj_3R_27()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean jj_3R_11() {
        return this.jj_scan_token(DOUBLE_STAR);
    }

    public boolean jj_3R_12() {
        return this.jj_scan_token(INIT);
    }

    public boolean jj_3_2() {
        if (this.jj_3R_13()) {
            return true;
        } else {
            return this.jj_3R_10() ? true : this.jj_scan_token(LPAREN);
        }
    }

    public final void DontProcessKotlinMetadataOption() throws ProGuardConfigParseException {
        ASTDontProcessKotlinMetadataOption aSTDontProcessKotlinMetadataOption = new ASTDontProcessKotlinMetadataOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontProcessKotlinMetadataOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTPROCESSKOTLINMETADATA);
            this.lastOptionKind = 105;
            this.jjtree.closeNodeScope(aSTDontProcessKotlinMetadataOption);
            bl = false;
            aSTDontProcessKotlinMetadataOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontProcessKotlinMetadataOption);
            }
        }
    }

    public final void KeepOption() throws ProGuardConfigParseException {
        ASTKeepOption aSTKeepOption = new ASTKeepOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEP);
            this.lastOptionKind = 30;
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.AllowShrinkingClause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[5] = this.jj_gen;
                        this.LbraceClause();
                        this.jjtree.closeNodeScope(aSTKeepOption);
                        bl = false;
                        aSTKeepOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepOption);
            }
        }
    }

    public final void OutJarsOption() throws ProGuardConfigParseException {
        ASTOutJarsOption aSTOutJarsOption = new ASTOutJarsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTOutJarsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_OUTJARS);
            this.lastOptionKind = 47;
            this.TildeClause();
            this.jjtree.closeNodeScope(aSTOutJarsOption);
            bl = false;
            aSTOutJarsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTOutJarsOption);
            }
        }
    }

    public boolean jj_3R_13() {
        ProGuardConfigToken proGuardConfigToken = this.jj_scanpos;
        if (this.jj_3R_17()) {
            this.jj_scanpos = proGuardConfigToken;
            if (this.jj_3R_23()) {
                this.jj_scanpos = proGuardConfigToken;
                if (this.jj_3R_24()) {
                    this.jj_scanpos = proGuardConfigToken;
                    if (this.jj_3R_28()) {
                        this.jj_scanpos = proGuardConfigToken;
                        if (this.jj_3R_14()) {
                            this.jj_scanpos = proGuardConfigToken;
                            if (this.jj_3R_31()) {
                                this.jj_scanpos = proGuardConfigToken;
                                if (this.jj_3R_22()) {
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

    public final void PrintUsageOption() throws ProGuardConfigParseException {
        ASTPrintUsageOption aSTPrintUsageOption = new ASTPrintUsageOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPrintUsageOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_PRINTUSAGE);
            this.lastOptionKind = 66;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[13] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTPrintUsageOption);
            bl = false;
            aSTPrintUsageOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPrintUsageOption);
            }
        }
    }

    public final ProGuardConfigSimpleNode BomClause() throws ProGuardConfigParseException {
        ASTBomClause aSTBomClause = new ASTBomClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBomClause);

        try {
            switch (this.jj_nt.s) {
                case 10:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[0] = this.jj_gen;
            }

            while (true) {
                switch (this.jj_nt.s) {
                    case 25:
                    case 29:
                    case 30:
                    case 40:
                    case 41:
                    case 45:
                    case 46:
                    case 47:
                    case 51:
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 61:
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 76:
                    case 77:
                    case 78:
                    case 79:
                    case 80:
                    case 81:
                    case 83:
                    case 84:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    case 97:
                    case 98:
                    case 99:
                    case 100:
                    case 101:
                    case 102:
                    case 103:
                    case 105:
                    case 106:
                    case 107:
                    case 108:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                    case 113:
                    case 114:
                    case 115:
                    case 116:
                    case 117:
                    case 118:
                    case 119:
                    case 120:
                        this.Production8();
                        break;
                    case 26:
                    case 27:
                    case 28:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 42:
                    case 43:
                    case 44:
                    case 48:
                    case 49:
                    case 50:
                    case 52:
                    case 53:
                    case 54:
                    case 59:
                    case 60:
                    case 62:
                    case 63:
                    case 68:
                    case 75:
                    case 82:
                    case 85:
                    case 104:
                    default:
                        this.jj_la1[1] = this.jj_gen;
                        this.jj_consume_token(EOF);
                        this.jjtree.closeNodeScope(aSTBomClause);
                        bl = false;
                        return aSTBomClause;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTBomClause);
            }
        }
    }

    public final void ExtendsClause() throws ProGuardConfigParseException {
        ProGuardConfigASTExtendsClause proGuardConfigASTExtendsClause = new ProGuardConfigASTExtendsClause();
        boolean bl = true;
        this.jjtree.openNodeScope(proGuardConfigASTExtendsClause);
        try {
            this.jj_consume_token(39);
            switch (this.jj_nt.s) {
                case 14: {
                    this.AtClause();
                    break;
                }
                default: {
                    this.jj_la1[54] = this.jj_gen;
                }
            }
            this.StarClause2();
            this.jjtree.closeNodeScope(proGuardConfigASTExtendsClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(proGuardConfigASTExtendsClause);
                }
                throw throwable2;
            }
        }
    }

    public final void DontSkipNonPublicLibraryClassesOption() throws ProGuardConfigParseException {
        ASTDontSkipNonPublicLibraryClassesOption aSTDontSkipNonPublicLibraryClassesOption = new ASTDontSkipNonPublicLibraryClassesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontSkipNonPublicLibraryClassesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTSKIPNONPUBLICLIBRARYCLASSES);
            this.lastOptionKind = 119;
            this.jjtree.closeNodeScope(aSTDontSkipNonPublicLibraryClassesOption);
            bl = false;
            aSTDontSkipNonPublicLibraryClassesOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontSkipNonPublicLibraryClassesOption);
            }
        }
    }

    static {
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
        jj_la1_init_3();
        jj_la1_init_4();
    }

    public final void MethodsClause() throws ProGuardConfigParseException {
        ASTMethodsClause aSTMethodsClause = new ASTMethodsClause();
        this.jjtree.openNodeScope(aSTMethodsClause);

        try {
            this.jj_consume_token(METHODS);
        } finally {
            this.jjtree.closeNodeScope(aSTMethodsClause);
        }
    }

    public boolean jj_3R_14() {
        return this.jj_scan_token(NAME);
    }

    public final void AllowAccessModificationOption() throws ProGuardConfigParseException {
        ASTAllowAccessModificationOption aSTAllowAccessModificationOption = new ASTAllowAccessModificationOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAllowAccessModificationOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ALLOWACCESSMODIFICATION);
            this.lastOptionKind = 103;
            this.jjtree.closeNodeScope(aSTAllowAccessModificationOption);
            bl = false;
            aSTAllowAccessModificationOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAllowAccessModificationOption);
            }
        }
    }

    public final void PercentClause3() throws ProGuardConfigParseException {
        ASTPercentClause3 aSTPercentClause3 = new ASTPercentClause3();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPercentClause3);

        try {
            switch (this.jj_nt.s) {
                case 18:
                    ProGuardConfigToken proGuardConfigToken6 = this.jj_consume_token(PERCENT);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken6.M);
                    break;
                case 19:
                    ProGuardConfigToken proGuardConfigToken5 = this.jj_consume_token(STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken5.M);
                    break;
                case 24:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(DOUBLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken4.M);
                    break;
                case 26:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(TRIPLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken3.M);
                    break;
                case 125:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken2.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken1.M);
                    break;
                case 127:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_TYPE);
                    this.jjtree.closeNodeScope(aSTPercentClause3);
                    bl = false;
                    aSTPercentClause3.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[60] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPercentClause3);
            }
        }
    }

    public final void ImplementsClause() throws ProGuardConfigParseException {
        ProGuardConfigASTImplementsClause proGuardConfigASTImplementsClause = new ProGuardConfigASTImplementsClause();
        boolean bl = true;
        this.jjtree.openNodeScope(proGuardConfigASTImplementsClause);
        try {
            this.jj_consume_token(60);
            switch (this.jj_nt.s) {
                case 14: {
                    this.AtClause();
                    break;
                }
                default: {
                    this.jj_la1[55] = this.jj_gen;
                }
            }
            this.StarClause2();
            this.jjtree.closeNodeScope(proGuardConfigASTImplementsClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(proGuardConfigASTImplementsClause);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_15() {
        ProGuardConfigToken proGuardConfigToken = this.jj_scanpos;
        ProGuardConfigToken proGuardConfigToken1;
        if (this.jj_scan_token(BANG)) {
            this.jj_scanpos = proGuardConfigToken;
            proGuardConfigToken1 = this.jj_scanpos;
        } else {
            proGuardConfigToken1 = this.jj_scanpos;
        }

        proGuardConfigToken = proGuardConfigToken1;
        if (this.jj_scan_token(PUBLIC)) {
            this.jj_scanpos = proGuardConfigToken;
            if (this.jj_scan_token(FINAL)) {
                this.jj_scanpos = proGuardConfigToken;
                if (this.jj_scan_token(ABSTRACT)) {
                    this.jj_scanpos = proGuardConfigToken;
                    if (this.jj_scan_token(SYNTHETIC)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public final void AdaptResourceFilenamesOption() throws ProGuardConfigParseException {
        ASTAdaptResourceFilenamesOption aSTAdaptResourceFilenamesOption = new ASTAdaptResourceFilenamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAdaptResourceFilenamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            ProGuardConfigTreeState proGuardConfigTreeState;
            proGuardConfigToken = this.jj_consume_token(OPT_ADAPTRESOURCEFILENAMES);
            this.lastOptionKind = 100;
            label83:
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Quote122Clause();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[26] = this.jj_gen;
                                proGuardConfigTreeState = this.jjtree;
                                break label83;
                        }
                    }
                default:
                    this.jj_la1[27] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTAdaptResourceFilenamesOption);
            bl = false;
            aSTAdaptResourceFilenamesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAdaptResourceFilenamesOption);
            }
        }
    }

    public boolean jj_3R_16() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public final void VerboseOption() throws ProGuardConfigParseException {
        ASTVerboseOption aSTVerboseOption = new ASTVerboseOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTVerboseOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_VERBOSE);
            this.lastOptionKind = 45;
            this.jjtree.closeNodeScope(aSTVerboseOption);
            bl = false;
            aSTVerboseOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTVerboseOption);
            }
        }
    }

    public boolean jj_3R_17() {
        return this.jj_scan_token(PERCENT);
    }

    public final void ApplyMappingOption() throws ProGuardConfigParseException {
        ASTApplyMappingOption aSTApplyMappingOption = new ASTApplyMappingOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTApplyMappingOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_APPLYMAPPING);
            this.lastOptionKind = 69;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[15] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTApplyMappingOption);
            bl = false;
            aSTApplyMappingOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTApplyMappingOption);
            }
        }
    }

    public final void KeepKotlinMetadataOption() throws ProGuardConfigParseException {
        ASTKeepKotlinMetadataOption aSTKeepKotlinMetadataOption = new ASTKeepKotlinMetadataOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepKotlinMetadataOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPKOTLINMETADATA);
            this.lastOptionKind = 91;
            this.jjtree.closeNodeScope(aSTKeepKotlinMetadataOption);
            bl = false;
            aSTKeepKotlinMetadataOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepKotlinMetadataOption);
            }
        }
    }

    public boolean jj_3R_18() {
        return this.jj_scan_token(NAME);
    }

    public final void PrintMappingOption() throws ProGuardConfigParseException {
        ASTPrintMappingOption aSTPrintMappingOption = new ASTPrintMappingOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPrintMappingOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_PRINTMAPPING);
            this.lastOptionKind = 71;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[16] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTPrintMappingOption);
            bl = false;
            aSTPrintMappingOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPrintMappingOption);
            }
        }
    }

    




    public final void Production3() throws ProGuardConfigParseException {
        ASTProduction3 aSTProduction3 = new ASTProduction3();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction3);
        try {
            this.PercentClause();
            ProGuardConfigToken proGuardConfigToken = this.jj_nt;
            block7:
            while (true) {
                switch (proGuardConfigToken.s) {
                    case 12: {
                        break;
                    }
                    default: {
                        this.jj_la1[56] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(12);
                this.PercentClause();
                proGuardConfigToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTProduction3);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardConfigParseException)) throw (Error) throwable;
                throw (ProGuardConfigParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTProduction3);
                throw throwable2;
            }
        }
    }

    public boolean jj_3_3() {
        return this.jj_3R_10() ? true : this.jj_scan_token(LPAREN);
    }

    public void jj_add_error_token(int ba, int jj_endpos) {
        if (jj_endpos < 100) {
            if (jj_endpos == this.jj_endpos + 1) {
                this.jj_lasttokens[this.jj_endpos++] = ba;
            } else if (this.jj_endpos != 0) {
                this.jj_expentry = new int[this.jj_endpos];
                int bc = 0;
                int bg = 0;

                for (int i = this.jj_endpos; bg < i; i = this.jj_endpos) {
                    this.jj_expentry[bc] = this.jj_lasttokens[bc];
                    bg = ++bc;
                }

                Iterator iterator = this.jj_expentries.iterator();

                label45:
                while (iterator.hasNext()) {
                    int[] bd = (int[]) iterator.next();
                    if (bd.length == this.jj_expentry.length) {
                        int be = 0;
                        bg = be;

                        for (int[] jj_expentry = this.jj_expentry; bg < jj_expentry.length; jj_expentry = this.jj_expentry) {
                            if (bd[be] != this.jj_expentry[be]) {
                                continue label45;
                            }

                            bg = ++be;
                        }

                        this.jj_expentries.add(this.jj_expentry);
                        break;
                    }
                }

                if (jj_endpos != 0) {
                    this.jj_lasttokens[(this.jj_endpos = jj_endpos) - 1] = ba;
                }
            }
        }
    }

    public final void AssumeNoSideEffectsOption() throws ProGuardConfigParseException {
        ASTAssumeNoSideEffectsOption aSTAssumeNoSideEffectsOption = new ASTAssumeNoSideEffectsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAssumeNoSideEffectsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ASSUMENOSIDEEFFECTS);
            this.lastOptionKind = 96;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTAssumeNoSideEffectsOption);
            bl = false;
            aSTAssumeNoSideEffectsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAssumeNoSideEffectsOption);
            }
        }
    }

    public final void PrintSeedsOption() throws ProGuardConfigParseException {
        ASTPrintSeedsOption aSTPrintSeedsOption = new ASTPrintSeedsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPrintSeedsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_PRINTSEEDS);
            this.lastOptionKind = 64;
            ProGuardConfigTreeState proGuardConfigTreeState;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    proGuardConfigTreeState = this.jjtree;
                    break;
                default:
                    this.jj_la1[12] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTPrintSeedsOption);
            bl = false;
            aSTPrintSeedsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPrintSeedsOption);
            }
        }
    }

    public final void BaseDirectoryOption() throws ProGuardConfigParseException {
        ASTBaseDirectoryOption aSTBaseDirectoryOption = new ASTBaseDirectoryOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBaseDirectoryOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_BASEDIRECTORY);
            this.lastOptionKind = 78;
            this.Quote122Clause();
            this.jjtree.closeNodeScope(aSTBaseDirectoryOption);
            bl = false;
            aSTBaseDirectoryOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTBaseDirectoryOption);
            }
        }
    }

    public boolean jj_2_5() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_5();
        } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
        } finally {
            this.jj_save(2);
        }

        return true;
    }

    public final void KeepClassesWithMemberNamesOption() throws ProGuardConfigParseException {
        ASTKeepClassesWithMemberNamesOption aSTKeepClassesWithMemberNamesOption = new ASTKeepClassesWithMemberNamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepClassesWithMemberNamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPCLASSESWITHMEMBERNAMES);
            this.lastOptionKind = 112;
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.AllowShrinkingClause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[11] = this.jj_gen;
                        this.LbraceClause();
                        this.jjtree.closeNodeScope(aSTKeepClassesWithMemberNamesOption);
                        bl = false;
                        aSTKeepClassesWithMemberNamesOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepClassesWithMemberNamesOption);
            }
        }
    }

    public final void Production4() throws ProGuardConfigParseException {
        ASTProduction4 aSTProduction4 = new ASTProduction4();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction4);

        try {
            switch (this.jj_nt.s) {
                case 16:
                case 125:
                    ProGuardConfigParser proGuardConfigParser1;
                    byte ba;
                    switch (this.jj_nt.s) {
                        case 16:
                            this.BangClause();
                            proGuardConfigParser1 = this;
                            ba = 125;
                            break;
                        default:
                            this.jj_la1[35] = this.jj_gen;
                            proGuardConfigParser1 = this;
                            ba = 125;
                    }

                    ProGuardConfigToken proGuardConfigToken1 = proGuardConfigParser1.jj_consume_token(ba);
                    this.jjtree.closeNodeScope(aSTProduction4);
                    bl = false;
                    aSTProduction4.setValue(proGuardConfigToken1.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTProduction4);
                    bl = false;
                    aSTProduction4.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[36] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTProduction4);
            }
        }
    }

    public boolean jj_3R_19() {
        return this.jj_scan_token(WILDCARD_TYPE);
    }

    public final void OverloadAggressivelyOption() throws ProGuardConfigParseException {
        ASTOverloadAggressivelyOption aSTOverloadAggressivelyOption = new ASTOverloadAggressivelyOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTOverloadAggressivelyOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_OVERLOADAGGRESSIVELY);
            this.lastOptionKind = 97;
            this.jjtree.closeNodeScope(aSTOverloadAggressivelyOption);
            bl = false;
            aSTOverloadAggressivelyOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTOverloadAggressivelyOption);
            }
        }
    }

    public ProGuardConfigParser(Reader reader1) {
        this.jj_input_stream = new ProGuardConfigSimpleCharStream(reader1);
        this.token_source = new ProGuardConfigTokenManager(this.jj_input_stream);
        this.token = new ProGuardConfigToken();
        this.token.Y = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        int ba = 0;
        int bb = 0;

        for (byte bc = 71; bb < bc; bc = 71) {
            this.jj_la1[ba] = -1;
            bb = ++ba;
        }

        ba = 0;
        bb = 0;

        for (ProGuardConfigParserJJCalls[] proGuardConfigParserJJCalls = this.jj_2_rtns;
             bb < proGuardConfigParserJJCalls.length;
             proGuardConfigParserJJCalls = this.jj_2_rtns
        ) {
            this.jj_2_rtns[ba] = new ProGuardConfigParserJJCalls();
            bb = ++ba;
        }
    }

    public final void PercentClause4() throws ProGuardConfigParseException {
        ASTPercentClause4 aSTPercentClause4 = new ASTPercentClause4();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPercentClause4);

        try {
            switch (this.jj_nt.s) {
                case 18:
                    ProGuardConfigToken proGuardConfigToken6 = this.jj_consume_token(PERCENT);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken6.M);
                    break;
                case 19:
                    ProGuardConfigToken proGuardConfigToken5 = this.jj_consume_token(STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken5.M);
                    break;
                case 24:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(DOUBLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken4.M);
                    break;
                case 26:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(TRIPLE_STAR);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken3.M);
                    break;
                case 125:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken2.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken1.M);
                    break;
                case 127:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_TYPE);
                    this.jjtree.closeNodeScope(aSTPercentClause4);
                    bl = false;
                    aSTPercentClause4.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[59] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPercentClause4);
            }
        }
    }

    public final void AdaptKotlinMetadataOption() throws ProGuardConfigParseException {
        ASTAdaptKotlinMetadataOption aSTAdaptKotlinMetadataOption = new ASTAdaptKotlinMetadataOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAdaptKotlinMetadataOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ADAPTKOTLINMETADATA);
            this.lastOptionKind = 95;
            this.jjtree.closeNodeScope(aSTAdaptKotlinMetadataOption);
            bl = false;
            aSTAdaptKotlinMetadataOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAdaptKotlinMetadataOption);
            }
        }
    }

    public final void KeepCodeOption() throws ProGuardConfigParseException {
        ASTKeepCodeOption aSTKeepCodeOption = new ASTKeepCodeOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepCodeOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPCODE);
            this.lastOptionKind = 55;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTKeepCodeOption);
            bl = false;
            aSTKeepCodeOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepCodeOption);
            }
        }
    }

    public boolean jj_3R_20() {
        if (this.jj_3R_29()) {
            return true;
        }

        ProGuardConfigToken proGuardConfigToken;
        do {
            proGuardConfigToken = this.jj_scanpos;
        } while (!this.jj_3_1());

        this.jj_scanpos = proGuardConfigToken;
        return this.jj_scan_token(TILDE);
    }

    public final void DontObfuscateOption() throws ProGuardConfigParseException {
        ASTDontObfuscateOption aSTDontObfuscateOption = new ASTDontObfuscateOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontObfuscateOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTOBFUSCATE);
            this.lastOptionKind = 79;
            this.jjtree.closeNodeScope(aSTDontObfuscateOption);
            bl = false;
            aSTDontObfuscateOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontObfuscateOption);
            }
        }
    }

    public final void LparenClause() throws ProGuardConfigParseException {
        ASTLparenClause aSTLparenClause = new ASTLparenClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLparenClause);
        try {
            switch (this.jj_nt.s) {
                case 49: {
                    this.MethodsClause();
                    break;
                }
                case 18:
                case 19:
                case 24:
                case 26:
                case 36:
                case 125:
                case 126:
                case 127: {
                    if (this.jj_2_2()) {
                        this.PercentClause3();
                    }
                    this.StarClause();
                    this.jj_consume_token(20);
                    switch (this.jj_nt.s) {
                        case 18:
                        case 19:
                        case 24:
                        case 26:
                        case 27:
                        case 125:
                        case 126:
                        case 127: {
                            this.Production3();
                            break;
                        }
                        default: {
                            this.jj_la1[52] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(21);
                    break;
                }
                default: {
                    this.jj_la1[53] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTLparenClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTLparenClause);
                }
                throw throwable2;
            }
        }
    }

    public final void DontWarnOption() throws ProGuardConfigParseException {
        ASTDontWarnOption aSTDontWarnOption = new ASTDontWarnOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontWarnOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTWARN);
            this.lastOptionKind = 58;
            switch (this.jj_nt.s) {
                case 16:
                case 19:
                case 24:
                case 62:
                case 125:
                case 126:
                    this.Production2();
                    break;
                default:
                    this.jj_la1[32] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTDontWarnOption);
            bl = false;
            aSTDontWarnOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontWarnOption);
            }
        }
    }

    public final void PublicClause() throws ProGuardConfigParseException {
        ASTPublicClause aSTPublicClause = new ASTPublicClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPublicClause);

        try {
            switch (this.jj_nt.s) {
                case 16:
                    this.BangClause();
                    break;
                default:
                    this.jj_la1[67] = this.jj_gen;
            }

            switch (this.jj_nt.s) {
                case 32:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(FINAL);
                    this.jjtree.closeNodeScope(aSTPublicClause);
                    bl = false;
                    aSTPublicClause.setValue(proGuardConfigToken3.M);
                    break;
                case 33:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(PUBLIC);
                    this.jjtree.closeNodeScope(aSTPublicClause);
                    bl = false;
                    aSTPublicClause.setValue(proGuardConfigToken2.M);
                    break;
                case 50:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(ABSTRACT);
                    this.jjtree.closeNodeScope(aSTPublicClause);
                    bl = false;
                    aSTPublicClause.setValue(proGuardConfigToken1.M);
                    break;
                case 54:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(SYNTHETIC);
                    this.jjtree.closeNodeScope(aSTPublicClause);
                    bl = false;
                    aSTPublicClause.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[68] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPublicClause);
            }
        }
    }

    public final void KeepClassesWithMembersOption() throws ProGuardConfigParseException {
        ASTKeepClassesWithMembersOption aSTKeepClassesWithMembersOption = new ASTKeepClassesWithMembersOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepClassesWithMembersOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPCLASSESWITHMEMBERS);
            this.lastOptionKind = 101;
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.AllowShrinkingClause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[8] = this.jj_gen;
                        this.LbraceClause();
                        this.jjtree.closeNodeScope(aSTKeepClassesWithMembersOption);
                        bl = false;
                        aSTKeepClassesWithMembersOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepClassesWithMembersOption);
            }
        }
    }

    public final void ClassObfuscationDictionaryOption() throws ProGuardConfigParseException {
        ASTClassObfuscationDictionaryOption aSTClassObfuscationDictionaryOption = new ASTClassObfuscationDictionaryOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTClassObfuscationDictionaryOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_CLASSOBFUSCATIONDICTIONARY);
            this.lastOptionKind = 113;
            this.Quote122Clause();
            this.jjtree.closeNodeScope(aSTClassObfuscationDictionaryOption);
            bl = false;
            aSTClassObfuscationDictionaryOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTClassObfuscationDictionaryOption);
            }
        }
    }

    public static void jj_la1_init_2() {
        jj_la1_2 = new int[]{
                0,
                -2361361,
                -2361361,
                0,
                0,
                0,
                2361344,
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
                0,
                0,
                0,
                0,
                0,
                0,
                16,
                0,
                0,
                16,
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
                0,
                0,
                0,
                0,
                0,
                16
        };
    }

    public boolean jj_3R_21() {
        return this.jj_scan_token(STAR);
    }

    public boolean jj_2_6() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_26();
        } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
        } finally {
            this.jj_save(4);
        }

        return true;
    }

    public final void KeepDirectoriesOption() throws ProGuardConfigParseException {
        ASTKeepDirectoriesOption aSTKeepDirectoriesOption = new ASTKeepDirectoriesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepDirectoriesOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            ProGuardConfigTreeState proGuardConfigTreeState;
            proGuardConfigToken = this.jj_consume_token(OPT_KEEPDIRECTORIES);
            this.lastOptionKind = 83;
            label83:
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Quote122Clause();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[3] = this.jj_gen;
                                proGuardConfigTreeState = this.jjtree;
                                break label83;
                        }
                    }
                default:
                    this.jj_la1[4] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTKeepDirectoriesOption);
            bl = false;
            aSTKeepDirectoriesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepDirectoriesOption);
            }
        }
    }

    public final void DumpOption() throws ProGuardConfigParseException {
        ASTDumpOption aSTDumpOption = new ASTDumpOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDumpOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DUMP);
            this.lastOptionKind = 29;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[34] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTDumpOption);
            bl = false;
            aSTDumpOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDumpOption);
            }
        }
    }

    public static void jj_la1_init_1() {
        jj_la1_1 = new int[]{
                0,
                663282432,
                663282432,
                0,
                0,
                0,
                Integer.MIN_VALUE,
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
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1073741824,
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
                128,
                268435456,
                5708927,
                0,
                0,
                5573743,
                4096,
                0,
                0,
                0,
                131088,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1073741824,
                16,
                0,
                136314880,
                0,
                4456451,
                0,
                5573743
        };
    }

    public final void InJarsOption() throws ProGuardConfigParseException {
        ASTInJarsOption aSTInJarsOption = new ASTInJarsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTInJarsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_INJARS);
            this.lastOptionKind = 41;
            this.TildeClause();
            this.jjtree.closeNodeScope(aSTInJarsOption);
            bl = false;
            aSTInJarsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTInJarsOption);
            }
        }
    }

    public final void MergeInterfacesAggressivelyOption() throws ProGuardConfigParseException {
        ASTMergeInterfacesAggressivelyOption aSTMergeInterfacesAggressivelyOption = new ASTMergeInterfacesAggressivelyOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMergeInterfacesAggressivelyOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_MERGEINTERFACESAGGRESSIVELY);
            this.lastOptionKind = 116;
            this.jjtree.closeNodeScope(aSTMergeInterfacesAggressivelyOption);
            bl = false;
            aSTMergeInterfacesAggressivelyOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMergeInterfacesAggressivelyOption);
            }
        }
    }

    public boolean jj_3R_22() {
        return this.jj_scan_token(WILDCARD_TYPE);
    }

    public final void PrintConfigurationOption() throws ProGuardConfigParseException {
        ASTPrintConfigurationOption aSTPrintConfigurationOption = new ASTPrintConfigurationOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPrintConfigurationOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_PRINTCONFIGURATION);
            this.lastOptionKind = 92;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[33] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTPrintConfigurationOption);
            bl = false;
            aSTPrintConfigurationOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPrintConfigurationOption);
            }
        }
    }

    public boolean jj_3R_23() {
        return this.jj_scan_token(STAR);
    }

    public final void FlattenPackageHierarchyOption() throws ProGuardConfigParseException {
        ASTFlattenPackageHierarchyOption aSTFlattenPackageHierarchyOption = new ASTFlattenPackageHierarchyOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFlattenPackageHierarchyOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_FLATTENPACKAGEHIERARCHY);
            this.lastOptionKind = 102;
            ProGuardConfigTreeState proGuardConfigTreeState;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    proGuardConfigTreeState = this.jjtree;
                    break;
                default:
                    this.jj_la1[19] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTFlattenPackageHierarchyOption);
            bl = false;
            aSTFlattenPackageHierarchyOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTFlattenPackageHierarchyOption);
            }
        }
    }

    public final void AdaptClassStringsOption() throws ProGuardConfigParseException {
        ASTAdaptClassStringsOption aSTAdaptClassStringsOption = new ASTAdaptClassStringsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAdaptClassStringsOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            ProGuardConfigTreeState proGuardConfigTreeState;
            proGuardConfigToken = this.jj_consume_token(OPT_ADAPTCLASSSTRINGS);
            this.lastOptionKind = 90;
            label83:
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Quote122Clause();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[24] = this.jj_gen;
                                proGuardConfigTreeState = this.jjtree;
                                break label83;
                        }
                    }
                default:
                    this.jj_la1[25] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTAdaptClassStringsOption);
            bl = false;
            aSTAdaptClassStringsOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAdaptClassStringsOption);
            }
        }
    }

    public boolean jj_3R_24() {
        return this.jj_scan_token(DOUBLE_STAR);
    }

    public final void AssumeNoExternalReturnValuesOption() throws ProGuardConfigParseException {
        ASTAssumeNoExternalReturnValuesOption aSTAssumeNoExternalReturnValuesOption = new ASTAssumeNoExternalReturnValuesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAssumeNoExternalReturnValuesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ASSUMENOEXTERNALRETURNVALUES);
            this.lastOptionKind = 117;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTAssumeNoExternalReturnValuesOption);
            bl = false;
            aSTAssumeNoExternalReturnValuesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAssumeNoExternalReturnValuesOption);
            }
        }
    }

    public final void KeepPackageNamesOption() throws ProGuardConfigParseException {
        ASTKeepPackageNamesOption aSTKeepPackageNamesOption = new ASTKeepPackageNamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepPackageNamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            ProGuardConfigTreeState proGuardConfigTreeState;
            proGuardConfigToken = this.jj_consume_token(OPT_KEEPPACKAGENAMES);
            this.lastOptionKind = 87;
            label83:
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Quote122Clause();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[17] = this.jj_gen;
                                proGuardConfigTreeState = this.jjtree;
                                break label83;
                        }
                    }
                default:
                    this.jj_la1[18] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTKeepPackageNamesOption);
            bl = false;
            aSTKeepPackageNamesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepPackageNamesOption);
            }
        }
    }

    public final void StarClause2() throws ProGuardConfigParseException {
        ASTStarClause2 aSTStarClause2 = new ASTStarClause2();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTStarClause2);

        try {
            switch (this.jj_nt.s) {
                case 16:
                    this.BangClause();
                    break;
                default:
                    this.jj_la1[62] = this.jj_gen;
            }

            switch (this.jj_nt.s) {
                case 19:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(STAR);
                    this.jjtree.closeNodeScope(aSTStarClause2);
                    bl = false;
                    aSTStarClause2.setValue(proGuardConfigToken4.M);
                    break;
                case 24:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(DOUBLE_STAR);
                    this.jjtree.closeNodeScope(aSTStarClause2);
                    bl = false;
                    aSTStarClause2.setValue(proGuardConfigToken3.M);
                    break;
                case 62:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(MODULE_INFO);
                    this.jjtree.closeNodeScope(aSTStarClause2);
                    bl = false;
                    aSTStarClause2.setValue(proGuardConfigToken2.M);
                    break;
                case 125:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(aSTStarClause2);
                    bl = false;
                    aSTStarClause2.setValue(proGuardConfigToken1.M);
                    break;
                case 126:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(WILDCARD_NAME);
                    this.jjtree.closeNodeScope(aSTStarClause2);
                    bl = false;
                    aSTStarClause2.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[63] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTStarClause2);
            }
        }
    }

    public void jj_save(int ba) {
        com.zelix.klassmaster.proguard.ProGuardConfigParserJJCalls proGuardConfigParserJJCalls1 = null;
        ProGuardConfigParserJJCalls proGuardConfigParserJJCalls = this.jj_2_rtns[ba];
        int bb = proGuardConfigParserJJCalls.gen;

        int bc;
        while (true) {
            if (bb <= this.jj_gen) {
                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls;
                bc = this.jj_gen;
                break;
            }

            if (proGuardConfigParserJJCalls.next == null) {
                proGuardConfigParserJJCalls = proGuardConfigParserJJCalls.next = new ProGuardConfigParserJJCalls();
                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls;
                bc = this.jj_gen;
                break;
            }

            proGuardConfigParserJJCalls = proGuardConfigParserJJCalls.next;
            bb = proGuardConfigParserJJCalls.gen;
        }

        proGuardConfigParserJJCalls1.gen = bc + Integer.MAX_VALUE - this.jj_la;
        proGuardConfigParserJJCalls.first = this.token;
        proGuardConfigParserJJCalls.arg = Integer.MAX_VALUE;
    }

    public boolean jj_3R_25() {
        return this.jj_3R_20();
    }

    public boolean jj_3R_26() {
        ProGuardConfigToken proGuardConfigToken = this.jj_scanpos;
        if (this.jj_3R_3()) {
            this.jj_scanpos = proGuardConfigToken;
        }

        return this.jj_3R_7();
    }

    public boolean jj_3R_27() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public boolean jj_3R_28() {
        return this.jj_scan_token(TRIPLE_STAR);
    }

    public boolean jj_3R_29() {
        return this.jj_scan_token(QUOTE_122);
    }

    




    public final void Production5() throws ProGuardConfigParseException {
        ASTProduction5 aSTProduction5 = new ASTProduction5();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction5);
        try {
            this.Quote122Clause();
            ProGuardConfigToken proGuardConfigToken = this.jj_nt;
            block7:
            while (true) {
                switch (proGuardConfigToken.s) {
                    case 12: {
                        break;
                    }
                    default: {
                        this.jj_la1[40] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(12);
                this.Quote122Clause();
                proGuardConfigToken = this.jj_nt;
            }
            this.jj_consume_token(11);
            this.jjtree.closeNodeScope(aSTProduction5);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardConfigParseException)) throw (Error) throwable;
                throw (ProGuardConfigParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTProduction5);
                throw throwable2;
            }
        }
    }

    public final void AssumeNoEscapingParametersOption() throws ProGuardConfigParseException {
        ASTAssumeNoEscapingParametersOption aSTAssumeNoEscapingParametersOption = new ASTAssumeNoEscapingParametersOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAssumeNoEscapingParametersOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ASSUMENOESCAPINGPARAMETERS);
            this.lastOptionKind = 110;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTAssumeNoEscapingParametersOption);
            bl = false;
            aSTAssumeNoEscapingParametersOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAssumeNoEscapingParametersOption);
            }
        }
    }

    public boolean jj_3R_30() {
        return this.jj_scan_token(STAR);
    }

    public final void TargetOption() throws ProGuardConfigParseException {
        ASTTargetOption aSTTargetOption = new ASTTargetOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTTargetOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_TARGET);
            this.lastOptionKind = 40;
            this.Quote122Clause();
            this.jjtree.closeNodeScope(aSTTargetOption);
            bl = false;
            aSTTargetOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTTargetOption);
            }
        }
    }

    public final void UseUniqueClassMemberNamesOption() throws ProGuardConfigParseException {
        ASTUseUniqueClassMemberNamesOption aSTUseUniqueClassMemberNamesOption = new ASTUseUniqueClassMemberNamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTUseUniqueClassMemberNamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_USEUNIQUECLASSMEMBERNAMES);
            this.lastOptionKind = 109;
            this.jjtree.closeNodeScope(aSTUseUniqueClassMemberNamesOption);
            bl = false;
            aSTUseUniqueClassMemberNamesOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTUseUniqueClassMemberNamesOption);
            }
        }
    }

    public final void KeepClassMemberNamesOption() throws ProGuardConfigParseException {
        ASTKeepClassMemberNamesOption aSTKeepClassMemberNamesOption = new ASTKeepClassMemberNamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepClassMemberNamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPCLASSMEMBERNAMES);
            this.lastOptionKind = 98;
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.AllowShrinkingClause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[10] = this.jj_gen;
                        this.LbraceClause();
                        this.jjtree.closeNodeScope(aSTKeepClassMemberNamesOption);
                        bl = false;
                        aSTKeepClassMemberNamesOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepClassMemberNamesOption);
            }
        }
    }

    public boolean jj_3R_31() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public void jj_rescan_token() {
        this.jj_rescan = true;
        int ba = 0;
        int bb = 0;

        for (byte bc = 6; bb < bc; bc = 6) {
            try {
                ProGuardConfigParserJJCalls proGuardConfigParserJJCalls = this.jj_2_rtns[ba];
                bb = proGuardConfigParserJJCalls.gen;

                while (true) {
                    ProGuardConfigParserJJCalls proGuardConfigParserJJCalls1;
                    if (bb > this.jj_gen) {
                        this.jj_la = proGuardConfigParserJJCalls.arg;
                        this.jj_lastpos = this.jj_scanpos = proGuardConfigParserJJCalls.first;
                        switch (ba) {
                            case 0:
                                this.jj_3R_25();
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                                break;
                            case 1:
                                this.jj_3R_15();
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                                break;
                            case 2:
                                this.jj_3R_5();
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                                break;
                            case 3:
                                this.jj_3R_33();
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                                break;
                            case 4:
                                this.jj_3R_26();
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                                break;
                            case 5:
                                this.jj_3R_9();
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                                break;
                            default:
                                proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                        }
                    } else {
                        proGuardConfigParserJJCalls1 = proGuardConfigParserJJCalls.next;
                    }

                    proGuardConfigParserJJCalls = proGuardConfigParserJJCalls1;
                    if (proGuardConfigParserJJCalls == null) {
                        break;
                    }

                    bb = proGuardConfigParserJJCalls.gen;
                }
            } catch (ProGuardConfigParserLookaheadSuccess proGuardConfigParserLookaheadSuccess) {
            }

            bb = ++ba;
        }

        this.jj_rescan = false;
    }

    public final void OptimizationsOption() throws ProGuardConfigParseException {
        ASTOptimizationsOption aSTOptimizationsOption = new ASTOptimizationsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTOptimizationsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_OPTIMIZATIONS);
            this.lastOptionKind = 76;
            this.Quote122Clause();
            ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

            while (true) {
                switch (proGuardConfigToken1.s) {
                    case 12:
                        this.jj_consume_token(COMMA);
                        this.Quote122Clause();
                        proGuardConfigToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[14] = this.jj_gen;
                        this.jjtree.closeNodeScope(aSTOptimizationsOption);
                        bl = false;
                        aSTOptimizationsOption.setLineNumber(proGuardConfigToken.c);
                        return;
                }
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTOptimizationsOption);
            }
        }
    }

    public final void RenameSourceFileAttributeOption() throws ProGuardConfigParseException {
        ASTRenameSourceFileAttributeOption aSTRenameSourceFileAttributeOption = new ASTRenameSourceFileAttributeOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRenameSourceFileAttributeOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_RENAMESOURCEFILEATTRIBUTE);
            this.lastOptionKind = 107;
            switch (this.jj_nt.s) {
                case 122:
                    this.Quote122Clause();
                    break;
                default:
                    this.jj_la1[23] = this.jj_gen;
            }

            this.jjtree.closeNodeScope(aSTRenameSourceFileAttributeOption);
            bl = false;
            aSTRenameSourceFileAttributeOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTRenameSourceFileAttributeOption);
            }
        }
    }

    public final void AssumeValuesOption() throws ProGuardConfigParseException {
        ASTAssumeValuesOption aSTAssumeValuesOption = new ASTAssumeValuesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAssumeValuesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ASSUMEVALUES);
            this.lastOptionKind = 72;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTAssumeValuesOption);
            bl = false;
            aSTAssumeValuesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAssumeValuesOption);
            }
        }
    }

    public final void OptimizationPassesOption() throws ProGuardConfigParseException {
        ASTOptimizationPassesOption aSTOptimizationPassesOption = new ASTOptimizationPassesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTOptimizationPassesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_OPTIMIZATIONPASSES);
            this.lastOptionKind = 94;
            this.IntegerClause();
            this.jjtree.closeNodeScope(aSTOptimizationPassesOption);
            bl = false;
            aSTOptimizationPassesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTOptimizationPassesOption);
            }
        }
    }

    public final void KeepParameterNamesOption() throws ProGuardConfigParseException {
        ASTKeepParameterNamesOption aSTKeepParameterNamesOption = new ASTKeepParameterNamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepParameterNamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_KEEPPARAMETERNAMES);
            this.lastOptionKind = 93;
            this.jjtree.closeNodeScope(aSTKeepParameterNamesOption);
            bl = false;
            aSTKeepParameterNamesOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepParameterNamesOption);
            }
        }
    }

    public ProGuardConfigParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] bl = new boolean[130];
        if (this.jj_kind >= 0) {
            bl[this.jj_kind] = true;
            this.jj_kind = -1;
        }

        int ba = 0;
        int bi = ba;

        for (byte bj = 71; bi < bj; bj = 71) {
            if (this.jj_la1[ba] == this.jj_gen) {
                int bb = 0;
                bi = bb;

                for (byte bg = 32; bi < bg; bg = 32) {
                    if ((jj_la1_0[ba] & 1 << bb) != 0) {
                        bl[bb] = true;
                    }

                    if ((jj_la1_1[ba] & 1 << bb) != 0) {
                        bl[32 + bb] = true;
                    }

                    int[] be;
                    if ((jj_la1_2[ba] & 1 << bb) != 0) {
                        bl[64 + bb] = true;
                        be = jj_la1_3;
                    } else {
                        be = jj_la1_3;
                    }

                    int[] bf;
                    if ((be[ba] & 1 << bb) != 0) {
                        bl[96 + bb] = true;
                        bf = jj_la1_4;
                    } else {
                        bf = jj_la1_4;
                    }

                    if ((bf[ba] & 1 << bb) != 0) {
                        bl[128 + bb] = true;
                    }

                    bi = ++bb;
                }
            }

            bi = ++ba;
        }

        ba = 0;
        bi = 0;

        for (short bh = 130; bi < bh; bh = 130) {
            if (bl[ba]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = ba;
                this.jj_expentries.add(this.jj_expentry);
            }

            bi = ++ba;
        }

        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] bc = new int[this.jj_expentries.size()][];
        int bd = 0;
        bi = 0;

        for (List list1 = this.jj_expentries; bi < list1.size(); list1 = this.jj_expentries) {
            bc[bd] = (int[]) this.jj_expentries.get(bd);
            bi = ++bd;
        }

        return new ProGuardConfigParseException(this.token, bc, ProGuardConfigConstants.TOKEN_IMAGE);
    }

    public final void ForceProcessingOption() throws ProGuardConfigParseException {
        ASTForceProcessingOption aSTForceProcessingOption = new ASTForceProcessingOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTForceProcessingOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_FORCEPROCESSING);
            this.lastOptionKind = 84;
            this.jjtree.closeNodeScope(aSTForceProcessingOption);
            bl = false;
            aSTForceProcessingOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTForceProcessingOption);
            }
        }
    }

    public final void PublicClause2() throws ProGuardConfigParseException {
        ASTPublicClause2 aSTPublicClause2 = new ASTPublicClause2();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPublicClause2);

        try {
            switch (this.jj_nt.s) {
                case 16:
                    this.BangClause();
                    break;
                default:
                    this.jj_la1[69] = this.jj_gen;
            }

            switch (this.jj_nt.s) {
                case 28:
                    ProGuardConfigToken proGuardConfigToken13 = this.jj_consume_token(ENUM);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken13.M);
                    break;
                case 29:
                case 30:
                case 31:
                case 36:
                case 39:
                case 40:
                case 41:
                case 44:
                case 45:
                case 46:
                case 47:
                case 49:
                case 51:
                case 53:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                default:
                    this.jj_la1[70] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
                case 32:
                    ProGuardConfigToken proGuardConfigToken12 = this.jj_consume_token(FINAL);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken12.M);
                    break;
                case 33:
                    ProGuardConfigToken proGuardConfigToken11 = this.jj_consume_token(PUBLIC);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken11.M);
                    break;
                case 34:
                    ProGuardConfigToken proGuardConfigToken10 = this.jj_consume_token(STATIC);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken10.M);
                    break;
                case 35:
                    ProGuardConfigToken proGuardConfigToken9 = this.jj_consume_token(NATIVE);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken9.M);
                    break;
                case 37:
                    ProGuardConfigToken proGuardConfigToken8 = this.jj_consume_token(BRIDGE);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken8.M);
                    break;
                case 38:
                    ProGuardConfigToken proGuardConfigToken7 = this.jj_consume_token(PRIVATE);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken7.M);
                    break;
                case 42:
                    ProGuardConfigToken proGuardConfigToken6 = this.jj_consume_token(VOLATILE);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken6.M);
                    break;
                case 43:
                    ProGuardConfigToken proGuardConfigToken5 = this.jj_consume_token(STRICTFP);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken5.M);
                    break;
                case 48:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(PROTECTED);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken4.M);
                    break;
                case 50:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(ABSTRACT);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken3.M);
                    break;
                case 52:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(TRANSIENT);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken2.M);
                    break;
                case 54:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(SYNTHETIC);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken1.M);
                    break;
                case 68:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(SYNCHRONIZED);
                    this.jjtree.closeNodeScope(aSTPublicClause2);
                    bl = false;
                    aSTPublicClause2.setValue(proGuardConfigToken.M);
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPublicClause2);
            }
        }
    }

    public final void KeepAttributesOption() throws ProGuardConfigParseException {
        ASTKeepAttributesOption aSTKeepAttributesOption = new ASTKeepAttributesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepAttributesOption);

        try {
            ProGuardConfigToken proGuardConfigToken;
            ProGuardConfigTreeState proGuardConfigTreeState;
            proGuardConfigToken = this.jj_consume_token(OPT_KEEPATTRIBUTES);
            this.lastOptionKind = 80;
            label83:
            switch (this.jj_nt.s) {
                case 16:
                case 125:
                case 126:
                    this.Production4();
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_nt;

                    while (true) {
                        switch (proGuardConfigToken1.s) {
                            case 12:
                                this.jj_consume_token(COMMA);
                                this.Production4();
                                proGuardConfigToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[21] = this.jj_gen;
                                proGuardConfigTreeState = this.jjtree;
                                break label83;
                        }
                    }
                default:
                    this.jj_la1[22] = this.jj_gen;
                    proGuardConfigTreeState = this.jjtree;
            }

            proGuardConfigTreeState.closeNodeScope(aSTKeepAttributesOption);
            bl = false;
            aSTKeepAttributesOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepAttributesOption);
            }
        }
    }

    public final void Production6() throws ProGuardConfigParseException {
        ASTProduction6 aSTProduction6 = new ASTProduction6();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction6);
        try {
            switch (this.jj_nt.s) {
                case 14: {
                    this.AtClause();
                    break;
                }
                default: {
                    this.jj_la1[47] = this.jj_gen;
                }
            }
            block13:
            while (true) {
                switch (this.jj_nt.s) {
                    case 16:
                    case 28:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 37:
                    case 38:
                    case 42:
                    case 43:
                    case 48:
                    case 50:
                    case 52:
                    case 54:
                    case 68: {
                        break;
                    }
                    default: {
                        this.jj_la1[48] = this.jj_gen;
                        break block13;
                    }
                }
                this.PublicClause2();
            }
            if (this.jj_2_5()) {
                this.LparenClause();
            } else if (this.jj_2_3()) {
                this.StarClause3();
            } else {
                switch (this.jj_nt.s) {
                    case 18:
                    case 19:
                    case 24:
                    case 26:
                    case 44:
                    case 125:
                    case 126:
                    case 127: {
                        this.Production1();
                        break;
                    }
                    default: {
                        this.jj_la1[49] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ProGuardConfigParseException();
                    }
                }
            }
            this.jj_consume_token(13);
            this.jjtree.closeNodeScope(aSTProduction6);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTProduction6);
                }
                throw throwable2;
            }
        }
    }

    public final void AllowShrinkingClause() throws ProGuardConfigParseException {
        ASTAllowShrinkingClause aSTAllowShrinkingClause = new ASTAllowShrinkingClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAllowShrinkingClause);

        try {
            switch (this.jj_nt.s) {
                case 63:
                    ProGuardConfigToken proGuardConfigToken4 = this.jj_consume_token(INCLUDECODE);
                    this.jjtree.closeNodeScope(aSTAllowShrinkingClause);
                    bl = false;
                    aSTAllowShrinkingClause.setValue(proGuardConfigToken4.M);
                    break;
                case 75:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(ALLOWSHRINKING);
                    this.jjtree.closeNodeScope(aSTAllowShrinkingClause);
                    bl = false;
                    aSTAllowShrinkingClause.setValue(proGuardConfigToken3.M);
                    break;
                case 82:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(ALLOWOBFUSCATION);
                    this.jjtree.closeNodeScope(aSTAllowShrinkingClause);
                    bl = false;
                    aSTAllowShrinkingClause.setValue(proGuardConfigToken2.M);
                    break;
                case 85:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(ALLOWOPTIMIZATION);
                    this.jjtree.closeNodeScope(aSTAllowShrinkingClause);
                    bl = false;
                    aSTAllowShrinkingClause.setValue(proGuardConfigToken1.M);
                    break;
                case 104:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(INCLUDEDESCRIPTORCLASSES);
                    this.jjtree.closeNodeScope(aSTAllowShrinkingClause);
                    bl = false;
                    aSTAllowShrinkingClause.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[6] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAllowShrinkingClause);
            }
        }
    }

    




    public final void Production7() throws ProGuardConfigParseException {
        ASTProduction7 aSTProduction7 = new ASTProduction7();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction7);
        try {
            this.Quote122Clause();
            ProGuardConfigToken proGuardConfigToken = this.jj_nt;
            block7:
            while (true) {
                switch (proGuardConfigToken.s) {
                    case 12: {
                        break;
                    }
                    default: {
                        this.jj_la1[41] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(12);
                this.Quote122Clause();
                proGuardConfigToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTProduction7);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardConfigParseException)) throw (Error) throwable;
                throw (ProGuardConfigParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTProduction7);
                throw throwable2;
            }
        }
    }

    public final void AddConfigurationDebuggingOption() throws ProGuardConfigParseException {
        ASTAddConfigurationDebuggingOption aSTAddConfigurationDebuggingOption = new ASTAddConfigurationDebuggingOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAddConfigurationDebuggingOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_ADDCONFIGURATIONDEBUGGING);
            this.lastOptionKind = 106;
            this.jjtree.closeNodeScope(aSTAddConfigurationDebuggingOption);
            bl = false;
            aSTAddConfigurationDebuggingOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAddConfigurationDebuggingOption);
            }
        }
    }

    public final void SkipNonPublicLibraryClassesOption() throws ProGuardConfigParseException {
        ASTSkipNonPublicLibraryClassesOption aSTSkipNonPublicLibraryClassesOption = new ASTSkipNonPublicLibraryClassesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTSkipNonPublicLibraryClassesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_SKIPNONPUBLICLIBRARYCLASSES);
            this.lastOptionKind = 115;
            this.jjtree.closeNodeScope(aSTSkipNonPublicLibraryClassesOption);
            bl = false;
            aSTSkipNonPublicLibraryClassesOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTSkipNonPublicLibraryClassesOption);
            }
        }
    }

    public final void InterfaceClause() throws ProGuardConfigParseException {
        ASTInterfaceClause aSTInterfaceClause = new ASTInterfaceClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTInterfaceClause);

        try {
            switch (this.jj_nt.s) {
                case 16:
                    this.BangClause();
                    break;
                default:
                    this.jj_la1[65] = this.jj_gen;
            }

            switch (this.jj_nt.s) {
                case 28:
                    ProGuardConfigToken proGuardConfigToken3 = this.jj_consume_token(ENUM);
                    this.jjtree.closeNodeScope(aSTInterfaceClause);
                    bl = false;
                    aSTInterfaceClause.setValue(proGuardConfigToken3.M);
                    break;
                case 31:
                    ProGuardConfigToken proGuardConfigToken2 = this.jj_consume_token(31);
                    this.jjtree.closeNodeScope(aSTInterfaceClause);
                    bl = false;
                    aSTInterfaceClause.setValue(proGuardConfigToken2.M);
                    break;
                case 53:
                    ProGuardConfigToken proGuardConfigToken1 = this.jj_consume_token(INTERFACE);
                    this.jjtree.closeNodeScope(aSTInterfaceClause);
                    bl = false;
                    aSTInterfaceClause.setValue(proGuardConfigToken1.M);
                    break;
                case 59:
                    ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(INTERFACE_59);
                    this.jjtree.closeNodeScope(aSTInterfaceClause);
                    bl = false;
                    aSTInterfaceClause.setValue(proGuardConfigToken.M);
                    break;
                default:
                    this.jj_la1[66] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
            }
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTInterfaceClause);
            }
        }
    }

    public final void IfOption() throws ProGuardConfigParseException {
        ASTIfOption aSTIfOption = new ASTIfOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIfOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_IF);
            this.lastOptionKind = 25;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTIfOption);
            bl = false;
            aSTIfOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIfOption);
            }
        }
    }

    public final void MicroEditionOption() throws ProGuardConfigParseException {
        ASTMicroEditionOption aSTMicroEditionOption = new ASTMicroEditionOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMicroEditionOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_MICROEDITION);
            this.lastOptionKind = 70;
            this.jjtree.closeNodeScope(aSTMicroEditionOption);
            bl = false;
            aSTMicroEditionOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMicroEditionOption);
            }
        }
    }

    public final void WhyAreYouKeepingOption() throws ProGuardConfigParseException {
        ASTWhyAreYouKeepingOption aSTWhyAreYouKeepingOption = new ASTWhyAreYouKeepingOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTWhyAreYouKeepingOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_WHYAREYOUKEEPING);
            this.lastOptionKind = 89;
            this.LbraceClause();
            this.jjtree.closeNodeScope(aSTWhyAreYouKeepingOption);
            bl = false;
            aSTWhyAreYouKeepingOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTWhyAreYouKeepingOption);
            }
        }
    }

    public final void Production8() throws ProGuardConfigParseException {
        ASTProduction8 aSTProduction8 = new ASTProduction8();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTProduction8);
        try {
            switch (this.jj_nt.s) {
                case 46: {
                    this.IncludeOption();
                    break;
                }
                case 41: {
                    this.InJarsOption();
                    break;
                }
                case 47: {
                    this.OutJarsOption();
                    break;
                }
                case 67: {
                    this.LibraryJarsOption();
                    break;
                }
                case 78: {
                    this.BaseDirectoryOption();
                    break;
                }
                case 115: {
                    this.SkipNonPublicLibraryClassesOption();
                    break;
                }
                case 119: {
                    this.DontSkipNonPublicLibraryClassesOption();
                    break;
                }
                case 120: {
                    this.DontSkipNonPublicLibraryClassMembersOption();
                    break;
                }
                case 83: {
                    this.KeepDirectoriesOption();
                    break;
                }
                case 40: {
                    this.TargetOption();
                    break;
                }
                case 84: {
                    this.ForceProcessingOption();
                    break;
                }
                case 25: {
                    this.IfOption();
                    break;
                }
                case 30: {
                    this.KeepOption();
                    break;
                }
                case 86: {
                    this.KeepClassMembersOption();
                    break;
                }
                case 101: {
                    this.KeepClassesWithMembersOption();
                    break;
                }
                case 61: {
                    this.KeepNamesOption();
                    break;
                }
                case 98: {
                    this.KeepClassMemberNamesOption();
                    break;
                }
                case 112: {
                    this.KeepClassesWithMemberNamesOption();
                    break;
                }
                case 64: {
                    this.PrintSeedsOption();
                    break;
                }
                case 65: {
                    this.DontShrinkOption();
                    break;
                }
                case 66: {
                    this.PrintUsageOption();
                    break;
                }
                case 89: {
                    this.WhyAreYouKeepingOption();
                    break;
                }
                case 73: {
                    this.DontOptimizeOption();
                    break;
                }
                case 76: {
                    this.OptimizationsOption();
                    break;
                }
                case 94: {
                    this.OptimizationPassesOption();
                    break;
                }
                case 96: {
                    this.AssumeNoSideEffectsOption();
                    break;
                }
                case 103: {
                    this.AllowAccessModificationOption();
                    break;
                }
                case 116: {
                    this.MergeInterfacesAggressivelyOption();
                    break;
                }
                case 79: {
                    this.DontObfuscateOption();
                    break;
                }
                case 69: {
                    this.ApplyMappingOption();
                    break;
                }
                case 71: {
                    this.PrintMappingOption();
                    break;
                }
                case 99: {
                    this.ObfuscationDictionaryOption();
                    break;
                }
                case 113: {
                    this.ClassObfuscationDictionaryOption();
                    break;
                }
                case 118: {
                    this.PackageObfuscationDictionaryOption();
                    break;
                }
                case 97: {
                    this.OverloadAggressivelyOption();
                    break;
                }
                case 109: {
                    this.UseUniqueClassMemberNamesOption();
                    break;
                }
                case 111: {
                    this.DontusemixedcaseclassnamesOption();
                    break;
                }
                case 87: {
                    this.KeepPackageNamesOption();
                    break;
                }
                case 102: {
                    this.FlattenPackageHierarchyOption();
                    break;
                }
                case 88: {
                    this.RepackageClassesOption();
                    break;
                }
                case 80: {
                    this.KeepAttributesOption();
                    break;
                }
                case 93: {
                    this.KeepParameterNamesOption();
                    break;
                }
                case 107: {
                    this.RenameSourceFileAttributeOption();
                    break;
                }
                case 90: {
                    this.AdaptClassStringsOption();
                    break;
                }
                case 100: {
                    this.AdaptResourceFilenamesOption();
                    break;
                }
                case 108: {
                    this.AdaptResourceFileContentsOption();
                    break;
                }
                case 77: {
                    this.DontPreverifyOption();
                    break;
                }
                case 70: {
                    this.MicroEditionOption();
                    break;
                }
                case 51: {
                    this.AndroidOption();
                    break;
                }
                case 45: {
                    this.VerboseOption();
                    break;
                }
                case 57: {
                    this.DontNoteOption();
                    break;
                }
                case 58: {
                    this.DontWarnOption();
                    break;
                }
                case 81: {
                    this.IgnoreWarningsOption();
                    break;
                }
                case 92: {
                    this.PrintConfigurationOption();
                    break;
                }
                case 106: {
                    this.AddConfigurationDebuggingOption();
                    break;
                }
                case 110: {
                    this.AssumeNoEscapingParametersOption();
                    break;
                }
                case 117: {
                    this.AssumeNoExternalReturnValuesOption();
                    break;
                }
                case 114: {
                    this.AssumeNoExternalSideEffectsOption();
                    break;
                }
                case 72: {
                    this.AssumeValuesOption();
                    break;
                }
                case 95: {
                    this.AdaptKotlinMetadataOption();
                    break;
                }
                case 55: {
                    this.KeepCodeOption();
                    break;
                }
                case 74: {
                    this.DontCompressOption();
                    break;
                }
                case 56: {
                    this.ZipAlignOption();
                    break;
                }
                case 91: {
                    this.KeepKotlinMetadataOption();
                    break;
                }
                case 105: {
                    this.DontProcessKotlinMetadataOption();
                    break;
                }
                case 29: {
                    this.DumpOption();
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ProGuardConfigParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTProduction8);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTProduction8);
                }
                throw throwable2;
            }
        }
    }

    public final void FieldsClause() throws ProGuardConfigParseException {
        ASTFieldsClause aSTFieldsClause = new ASTFieldsClause();
        this.jjtree.openNodeScope(aSTFieldsClause);

        try {
            this.jj_consume_token(FIELDS);
        } finally {
            this.jjtree.closeNodeScope(aSTFieldsClause);
        }
    }

    public final void ObfuscationDictionaryOption() throws ProGuardConfigParseException {
        ASTObfuscationDictionaryOption aSTObfuscationDictionaryOption = new ASTObfuscationDictionaryOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscationDictionaryOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_OBFUSCATIONDICTIONARY);
            this.lastOptionKind = 99;
            this.Quote122Clause();
            this.jjtree.closeNodeScope(aSTObfuscationDictionaryOption);
            bl = false;
            aSTObfuscationDictionaryOption.setLineNumber(proGuardConfigToken.c);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ProGuardConfigParseException) {
                throw (ProGuardConfigParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscationDictionaryOption);
            }
        }
    }

    public final void DontusemixedcaseclassnamesOption() throws ProGuardConfigParseException {
        ASTDontusemixedcaseclassnamesOption aSTDontusemixedcaseclassnamesOption = new ASTDontusemixedcaseclassnamesOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDontusemixedcaseclassnamesOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_DONTUSEMIXEDCASECLASSNAMES);
            this.lastOptionKind = 111;
            this.jjtree.closeNodeScope(aSTDontusemixedcaseclassnamesOption);
            bl = false;
            aSTDontusemixedcaseclassnamesOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDontusemixedcaseclassnamesOption);
            }
        }
    }

    public final void StarClause3() throws ProGuardConfigParseException {
        ASTStarClause3 aSTStarClause3 = new ASTStarClause3();
        this.jjtree.openNodeScope(aSTStarClause3);

        try {
            this.jj_consume_token(STAR);
        } finally {
            this.jjtree.closeNodeScope(aSTStarClause3);
        }
    }

    public final void LparenClause2() throws ProGuardConfigParseException {
        ASTLparenClause2 aSTLparenClause2 = new ASTLparenClause2();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLparenClause2);
        try {
            this.jj_consume_token(20);
            if (this.jj_2_1()) {
                this.Production5();
            }
            this.Production7();
            this.jj_consume_token(21);
            this.jjtree.closeNodeScope(aSTLparenClause2);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ProGuardConfigParseException) {
                    throw (ProGuardConfigParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTLparenClause2);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_32() {
        ProGuardConfigToken proGuardConfigToken = this.jj_scanpos;
        if (this.jj_3R_6()) {
            this.jj_scanpos = proGuardConfigToken;
            if (this.jj_3R_21()) {
                this.jj_scanpos = proGuardConfigToken;
                if (this.jj_3R_11()) {
                    this.jj_scanpos = proGuardConfigToken;
                    if (this.jj_3R_34()) {
                        this.jj_scanpos = proGuardConfigToken;
                        if (this.jj_3R_18()) {
                            this.jj_scanpos = proGuardConfigToken;
                            if (this.jj_3R_16()) {
                                this.jj_scanpos = proGuardConfigToken;
                                if (this.jj_3R_19()) {
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

    public boolean jj_3R_33() {
        return this.jj_scan_token(STAR) ? true : this.jj_scan_token(SEMICOLON);
    }

    




    public final void LbraceClause() throws ProGuardConfigParseException {
        ASTLbraceClause aSTLbraceClause = new ASTLbraceClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLbraceClause);
        try {
            int n;
            ProGuardConfigParser proGuardConfigParser;
            block23:
            {
                block22:
                {
                    switch (this.jj_nt.s) {
                        case 14: {
                            this.AtClause();
                            break;
                        }
                        default: {
                            this.jj_la1[42] = this.jj_gen;
                            break block22;
                        }
                    }
                    proGuardConfigParser = this;
                    n = Integer.MAX_VALUE;
                    break block23;
                }
                proGuardConfigParser = this;
                n = Integer.MAX_VALUE;
            }
            while (proGuardConfigParser.jj_2_4()) {
                this.PublicClause();
                proGuardConfigParser = this;
                n = Integer.MAX_VALUE;
            }
            this.InterfaceClause();
            this.Production2();
            switch (this.jj_nt.s) {
                case 39: {
                    this.ExtendsClause();
                    break;
                }
                default: {
                    this.jj_la1[43] = this.jj_gen;
                }
            }
            switch (this.jj_nt.s) {
                case 60: {
                    this.ImplementsClause();
                    break;
                }
                default: {
                    this.jj_la1[44] = this.jj_gen;
                }
            }
            switch (this.jj_nt.s) {
                case 22: {
                    this.jj_consume_token(22);
                    ProGuardConfigToken proGuardConfigToken = this.jj_nt;
                    block20:
                    while (true) {
                        switch (proGuardConfigToken.s) {
                            case 14:
                            case 16:
                            case 18:
                            case 19:
                            case 24:
                            case 26:
                            case 28:
                            case 32:
                            case 33:
                            case 34:
                            case 35:
                            case 36:
                            case 37:
                            case 38:
                            case 42:
                            case 43:
                            case 44:
                            case 48:
                            case 49:
                            case 50:
                            case 52:
                            case 54:
                            case 68:
                            case 125:
                            case 126:
                            case 127: {
                                break;
                            }
                            default: {
                                this.jj_la1[45] = this.jj_gen;
                                break block20;
                            }
                        }
                        this.Production6();
                        proGuardConfigToken = this.jj_nt;
                    }
                    this.jj_consume_token(23);
                    break;
                }
                default: {
                    this.jj_la1[46] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(aSTLbraceClause);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ProGuardConfigParseException)) throw (Error) throwable;
                throw (ProGuardConfigParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTLbraceClause);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_34() {
        return this.jj_scan_token(TRIPLE_STAR);
    }

    public final void IgnoreWarningsOption() throws ProGuardConfigParseException {
        ASTIgnoreWarningsOption aSTIgnoreWarningsOption = new ASTIgnoreWarningsOption();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIgnoreWarningsOption);

        try {
            ProGuardConfigToken proGuardConfigToken = this.jj_consume_token(OPT_IGNOREWARNINGS);
            this.lastOptionKind = 81;
            this.jjtree.closeNodeScope(aSTIgnoreWarningsOption);
            bl = false;
            aSTIgnoreWarningsOption.setLineNumber(proGuardConfigToken.c);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIgnoreWarningsOption);
            }
        }
    }
}
