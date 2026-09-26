package com.zelix.klassmaster.script.parser;

import com.zelix.klassmaster.script.ZkmScriptParserJJCalls;
import com.zelix.klassmaster.script.ZkmScriptParserLookaheadSuccess;
import com.zelix.klassmaster.script.ZkmScriptTreeState;
import com.zelix.klassmaster.script.parser.ast.ASTAccessedByReflectionExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTAccessedByReflectionStatement;
import com.zelix.klassmaster.script.parser.ast.ASTAggressiveOverloadParameter;
import com.zelix.klassmaster.script.parser.ast.ASTAllowMethodParameterChangesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTAndAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAndClassSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAndFieldSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAndFileFilterComponent;
import com.zelix.klassmaster.script.parser.ast.ASTAndMemberSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAndMethodSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAndModuleSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAndPackageSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTAnnotation;
import com.zelix.klassmaster.script.parser.ast.ASTArchiveCompressionParameter;
import com.zelix.klassmaster.script.parser.ast.ASTArchiveCompressionType;
import com.zelix.klassmaster.script.parser.ast.ASTAssumeRuntimeVersionParameter;
import com.zelix.klassmaster.script.parser.ast.ASTAutoReflectionHashParameter;
import com.zelix.klassmaster.script.parser.ast.ASTAutoReflectionPackageParameter;
import com.zelix.klassmaster.script.parser.ast.ASTAutoReflectionParameter;
import com.zelix.klassmaster.script.parser.ast.ASTAutoReflectionType;
import com.zelix.klassmaster.script.parser.ast.ASTBasicMethodSignature;
import com.zelix.klassmaster.script.parser.ast.ASTBoolean;
import com.zelix.klassmaster.script.parser.ast.ASTBooleanOrIfInArchive;
import com.zelix.klassmaster.script.parser.ast.ASTBooleanOrIfNameNotObfucated;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedClassSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedFieldSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedMemberSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedMethodSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedModuleSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTBracketedPackageSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTChangeLogInParameter;
import com.zelix.klassmaster.script.parser.ast.ASTChangeLogOutParameter;
import com.zelix.klassmaster.script.parser.ast.ASTCharacterType;
import com.zelix.klassmaster.script.parser.ast.ASTClassComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTClassInitializationOrder;
import com.zelix.klassmaster.script.parser.ast.ASTClassInitializationOrderStatement;
import com.zelix.klassmaster.script.parser.ast.ASTClassName;
import com.zelix.klassmaster.script.parser.ast.ASTClasspathStatement;
import com.zelix.klassmaster.script.parser.ast.ASTCollapsePackages;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTComplexClassSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTComplexFieldSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTComplexMemberSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTComplexMethodSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTComplexModuleSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTComplexPackageSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTContainedInClause;
import com.zelix.klassmaster.script.parser.ast.ASTContainingClause;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultMethodParameterChangesExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultMethodParameterChangesExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultMethodParameterObfuscationExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultMethodParameterObfuscationExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultTrimExcludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTDefaultTrimExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteAnnotationsParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteDebugExtensionAttributesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteDeprecatedAttributesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteEmptyDirectoriesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteExceptionAttributesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteSourceFileAttributesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteUnknownAttributesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeleteXMLCommentsParameter;
import com.zelix.klassmaster.script.parser.ast.ASTDeriveGroupingsFromChangeLogParameter;
import com.zelix.klassmaster.script.parser.ast.ASTEncryptIntegerConstantsParameter;
import com.zelix.klassmaster.script.parser.ast.ASTEncryptLongConstantsParameter;
import com.zelix.klassmaster.script.parser.ast.ASTEncryptParameter;
import com.zelix.klassmaster.script.parser.ast.ASTEncryptionType;
import com.zelix.klassmaster.script.parser.ast.ASTEnhancedIncrementalParameter;
import com.zelix.klassmaster.script.parser.ast.ASTExceptionObfuscationParameter;
import com.zelix.klassmaster.script.parser.ast.ASTExceptionObfuscationType;
import com.zelix.klassmaster.script.parser.ast.ASTExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTExecuteStatement;
import com.zelix.klassmaster.script.parser.ast.ASTExistingSerializedClassesStatement;
import com.zelix.klassmaster.script.parser.ast.ASTExpectedFinalHash;
import com.zelix.klassmaster.script.parser.ast.ASTExpectedInitialHash;
import com.zelix.klassmaster.script.parser.ast.ASTFieldName;
import com.zelix.klassmaster.script.parser.ast.ASTFieldSignature;
import com.zelix.klassmaster.script.parser.ast.ASTFileFilter;
import com.zelix.klassmaster.script.parser.ast.ASTFileFilterComponent;
import com.zelix.klassmaster.script.parser.ast.ASTFixedClassesStatement;
import com.zelix.klassmaster.script.parser.ast.ASTFlowObfuscationType;
import com.zelix.klassmaster.script.parser.ast.ASTGarbageCollectStatement;
import com.zelix.klassmaster.script.parser.ast.ASTGrouping;
import com.zelix.klassmaster.script.parser.ast.ASTGroupingsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTHideFieldNamesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTHideStaticMethodNamesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTIgnoreMissingReferencesStatement;
import com.zelix.klassmaster.script.parser.ast.ASTIntegerEncryptionExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTIntegerEncryptionType;
import com.zelix.klassmaster.script.parser.ast.ASTIntegerEncryptionUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTIntegerLiteral;
import com.zelix.klassmaster.script.parser.ast.ASTJarQualifier;
import com.zelix.klassmaster.script.parser.ast.ASTKeepBalancedLocksParameter;
import com.zelix.klassmaster.script.parser.ast.ASTKeepGenericsParameter;
import com.zelix.klassmaster.script.parser.ast.ASTKeepInnerClassesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTLastModifiedTime;
import com.zelix.klassmaster.script.parser.ast.ASTLegalIdsParameter;
import com.zelix.klassmaster.script.parser.ast.ASTLineNumberParameterType;
import com.zelix.klassmaster.script.parser.ast.ASTLineNumbersParameter;
import com.zelix.klassmaster.script.parser.ast.ASTLinkClassName;
import com.zelix.klassmaster.script.parser.ast.ASTLinkLiteralPackageComponent;
import com.zelix.klassmaster.script.parser.ast.ASTLinkMethodName;
import com.zelix.klassmaster.script.parser.ast.ASTLinkMethodSignature;
import com.zelix.klassmaster.script.parser.ast.ASTLinkPackageName;
import com.zelix.klassmaster.script.parser.ast.ASTLiteralPackageComponent;
import com.zelix.klassmaster.script.parser.ast.ASTLoadParameter;
import com.zelix.klassmaster.script.parser.ast.ASTLoadStatement;
import com.zelix.klassmaster.script.parser.ast.ASTLocalVariableParameterType;
import com.zelix.klassmaster.script.parser.ast.ASTLocalVariablesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTLongEncryptionExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTLongEncryptionType;
import com.zelix.klassmaster.script.parser.ast.ASTLongEncryptionUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTMakeClassesPublicParameter;
import com.zelix.klassmaster.script.parser.ast.ASTMemberComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTMemberModifierHelper;
import com.zelix.klassmaster.script.parser.ast.ASTMemberSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTMemberSpecifierComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTMemberSpecifierModifier;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameter;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterChangePackageParameter;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterChangesExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterChangesIncludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterChangesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterChangesType;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterObfuscationExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParameterObfuscationIncludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParametersParameter;
import com.zelix.klassmaster.script.parser.ast.ASTMethodParametersParameterType;
import com.zelix.klassmaster.script.parser.ast.ASTMixedCaseClassNamesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTModuleName;
import com.zelix.klassmaster.script.parser.ast.ASTModuleNameComponent;
import com.zelix.klassmaster.script.parser.ast.ASTNTStatement;
import com.zelix.klassmaster.script.parser.ast.ASTNegatedClassModifier;
import com.zelix.klassmaster.script.parser.ast.ASTNegatedFileFilterComponent;
import com.zelix.klassmaster.script.parser.ast.ASTNegatedMemberModifier;
import com.zelix.klassmaster.script.parser.ast.ASTNegatedMemberSpecifierModifier;
import com.zelix.klassmaster.script.parser.ast.ASTNewClassNameFileParameter;
import com.zelix.klassmaster.script.parser.ast.ASTNewFieldNameFileParameter;
import com.zelix.klassmaster.script.parser.ast.ASTNewMethodNameFileParameter;
import com.zelix.klassmaster.script.parser.ast.ASTNewNameCharactersParameter;
import com.zelix.klassmaster.script.parser.ast.ASTNewNamesPrefixParameter;
import com.zelix.klassmaster.script.parser.ast.ASTNewPackageNameFileParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateExceptionsExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateExceptionsUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateFlowExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateFlowParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateFlowUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateParametersParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateParametersType;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferenceStructuresParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferenceStructuresType;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesIncludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesPackageParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateReferencesType;
import com.zelix.klassmaster.script.parser.ast.ASTObfuscateStatement;
import com.zelix.klassmaster.script.parser.ast.ASTOpenNestedArchivesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTOrFileFilterComponent;
import com.zelix.klassmaster.script.parser.ast.ASTPackageName;
import com.zelix.klassmaster.script.parser.ast.ASTParameterComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTParameterPlaceHolder;
import com.zelix.klassmaster.script.parser.ast.ASTPlusSignatureClasses;
import com.zelix.klassmaster.script.parser.ast.ASTPreverifyParameter;
import com.zelix.klassmaster.script.parser.ast.ASTPrintStatement;
import com.zelix.klassmaster.script.parser.ast.ASTQualifiedClassName;
import com.zelix.klassmaster.script.parser.ast.ASTRandomizeParameter;
import com.zelix.klassmaster.script.parser.ast.ASTReferencingAnnotation;
import com.zelix.klassmaster.script.parser.ast.ASTReferencingAnnotationComponentName;
import com.zelix.klassmaster.script.parser.ast.ASTRemoveMethodCallsExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTRemoveMethodCallsIncludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTRemoveMethodCallsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ASTResetAccessedByReflectionStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetClassInitializationOrderStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetExistingSerializedClassesStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetFixedClassesStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetGroupingsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetIgnoreMissingReferencesStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetIntegerEncryptionExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetLongEncryptionExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetMethodParameterChangesExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetMethodParameterObfuscationExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetObfuscateExceptionsExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetObfuscateFlowExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetObfuscateReferenceExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetRemoveMethodCallsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetStringEncryptionExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTResetTrimExclusionsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTSaveAllParameter;
import com.zelix.klassmaster.script.parser.ast.ASTSaveAllStatement;
import com.zelix.klassmaster.script.parser.ast.ASTSingleObfuscateReferencesIncludeInput;
import com.zelix.klassmaster.script.parser.ast.ASTSingleRenameFilterParameter;
import com.zelix.klassmaster.script.parser.ast.ASTSkipArchivePath;
import com.zelix.klassmaster.script.parser.ast.ASTStandAloneAnnotation;
import com.zelix.klassmaster.script.parser.ast.ASTStringEncryptionExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTStringEncryptionUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTThrowsClause;
import com.zelix.klassmaster.script.parser.ast.ASTTrimExcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTTrimParameter;
import com.zelix.klassmaster.script.parser.ast.ASTTrimStatement;
import com.zelix.klassmaster.script.parser.ast.ASTTrimUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTUnSkipArchivePath;
import com.zelix.klassmaster.script.parser.ast.ASTUnexcludeStatement;
import com.zelix.klassmaster.script.parser.ast.ASTUniqueClassNamesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTUniqueMethodNamesParameter;
import com.zelix.klassmaster.script.parser.ast.ASTWildcardType;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTArrayLevel;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTClassModifier;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTExtendsClause;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTImplementsClause;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTInput;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTMemberModifier;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTMethodArguments;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTMethodName;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTMethodSignature;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTQualifiedType;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTStringLiteral;
import com.zelix.klassmaster.script.parser.ast.ZkmScriptASTType;
import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZkmScriptParser implements ZkmScriptTreeConstants, ZkmScriptConstants {
    public static int[] jj_la1_3;
    public static int[] jj_la1_1;
    public static int[] jj_la1_4;
    public static int[] jj_la1_2;
    public static int[] jj_la1_5;
    public static int[] jj_la1_0;
    public static int[] jj_la1_6;
    private static final String ESCAPED_QUOTE = "\"\"";
    private ZkmScriptToken jj_scanpos;
    public int currentStatementKind;
    public int[] jj_expentry;
    public int jj_la;
    public ZkmScriptToken jj_lastpos;
    public int jj_endpos;
    public ZkmScriptTreeState jjtree = new ZkmScriptTreeState();
    public final int[] jj_la1 = new int[187];
    public final ZkmScriptParserJJCalls[] jj_2_rtns = new ZkmScriptParserJJCalls[28];
    public boolean jj_rescan = false;
    public int jj_gc = 0;
    public final ZkmScriptParserLookaheadSuccess jj_ls = new ZkmScriptParserLookaheadSuccess(null);
    public List jj_expentries = new ArrayList();
    public int jj_kind = -1;
    public int[] jj_lasttokens = new int[100];
    public ZkmScriptSimpleCharStream jj_input_stream;
    public ZkmScriptTokenManager token_source;
    public ZkmScriptToken token;
    public ZkmScriptToken jj_nt;
    public int jj_gen;

    




    public final void AndPackageSpecifier() throws ZkmScriptParseException {
        ASTAndPackageSpecifier aSTAndPackageSpecifier = new ASTAndPackageSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndPackageSpecifier);
        try {
            this.ComplexPackageSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[148] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexPackageSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndPackageSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndPackageSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3_1() {
        return this.jj_scan_token(OR) ? true : this.jj_3R_93();
    }

    public static void jj_la1_init_4() {
        jj_la1_4 = new int[]{
                0,
                -989527040,
                -989527040,
                4,
                0,
                33562624,
                0,
                0,
                33562624,
                0,
                33562624,
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
                419072489,
                536870930,
                0,
                536870930,
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
                419072489,
                0,
                0,
                32768,
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
                16384,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                4096,
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
                512,
                0
        };
    }

    public boolean jj_3R_1() {
        return this.jj_3R_75();
    }

    public boolean jj_3R_2() {
        return this.jj_scan_token(RANDOMIZE);
    }

    public static void jj_la1_init_1() {
        jj_la1_1 = new int[]{
                0,
                24720,
                24720,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                12,
                12,
                0,
                0,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                64,
                -1289884240,
                64,
                64,
                0,
                64,
                0,
                0,
                64,
                0,
                64,
                64,
                0,
                64,
                0,
                0,
                64,
                0,
                64,
                64,
                64,
                64,
                0,
                64,
                0,
                0,
                64,
                0,
                0,
                64,
                0,
                0,
                64,
                0,
                0,
                64,
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
                6176,
                0,
                64,
                0,
                0,
                236192000,
                -1323700832,
                -1084363360,
                0,
                -1323700816,
                0,
                0,
                0,
                16777216,
                33816832,
                0,
                0,
                236192000,
                -1323700832,
                -1082266208,
                -1289884240,
                -1289884240,
                0,
                0,
                0,
                0,
                0,
                0,
                524288,
                33816832,
                33816832,
                236192000,
                -1084363360,
                -1289884240,
                -1084363360,
                0,
                4,
                8,
                236192000,
                -1323700832,
                0,
                0,
                0,
                0,
                0,
                100352,
                536872960,
                -1610610688,
                536872960,
                0,
                536905728,
                536872960,
                537004544,
                536872960,
                536872960,
                268436480,
                268436480,
                268436480,
                0,
                0,
                4,
                8,
                -1323700816,
                0,
                0,
                4,
                8,
                -1323700816,
                0,
                0,
                0,
                4,
                8,
                -1323700816,
                -1323700816,
                0,
                -1323700832,
                -1323700832,
                0,
                0,
                0,
                4,
                8,
                -1323700832,
                -1323700832,
                -1323700832,
                0,
                4,
                8,
                -1323700832,
                -1323700831,
                1073741824,
                -1323700831,
                1073741824,
                0,
                -1323700831,
                2097152,
                -1319506528,
                0,
                4,
                8,
                -1319506528,
                0,
                0,
                -1323700831,
                0,
                0,
                -1323700832,
                131584,
                131584,
                131584
        };
    }

    public final void WildcardType() throws ZkmScriptParseException {
        ASTWildcardType aSTWildcardType = new ASTWildcardType();
        this.jjtree.openNodeScope(aSTWildcardType);

        try {
            this.jj_consume_token(STAR);
        } finally {
            this.jjtree.closeNodeScope(aSTWildcardType);
        }
    }

    public boolean jj_3R_3() {
        return this.jj_scan_token(NAME);
    }

    public boolean jj_2_1() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_57();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(4, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_4() {
        return this.jj_scan_token(KEEP);
    }

    public boolean jj_3R_5() {
        return this.jj_scan_token(SCRAMBLE) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_6() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(BANG)) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_159();
    }

    public final void DeleteDebugExtensionAttributesParameter() throws ZkmScriptParseException {
        ASTDeleteDebugExtensionAttributesParameter aSTDeleteDebugExtensionAttributesParameter = new ASTDeleteDebugExtensionAttributesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteDebugExtensionAttributesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_DEBUG_EXTENSION_ATTRIBUTES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteDebugExtensionAttributesParameter);
            bl = false;
            aSTDeleteDebugExtensionAttributesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteDebugExtensionAttributesParameter);
            }
        }
    }

    public final ZkmScriptSimpleNode ObfuscateExceptionsUnexcludeStatement() throws ZkmScriptParseException {
        ASTObfuscateExceptionsUnexcludeStatement aSTObfuscateExceptionsUnexcludeStatement = new ASTObfuscateExceptionsUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateExceptionsUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_EXCEPTIONS_UNEXCLUDE);
            this.currentStatementKind = 186;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[21] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateExceptionsUnexcludeStatement);
                        bl = false;
                        aSTObfuscateExceptionsUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateExceptionsUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateExceptionsUnexcludeStatement);
            }
        }
    }

    public final void ObfuscateReferenceStructuresParameter() throws ZkmScriptParseException {
        ASTObfuscateReferenceStructuresParameter aSTObfuscateReferenceStructuresParameter = new ASTObfuscateReferenceStructuresParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferenceStructuresParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_REFERENCE_STRUCTURES);
            this.jj_consume_token(ASSIGN);
            this.ObfuscateReferenceStructuresType();
            this.jjtree.closeNodeScope(aSTObfuscateReferenceStructuresParameter);
            bl = false;
            aSTObfuscateReferenceStructuresParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferenceStructuresParameter);
            }
        }
    }

    public boolean jj_3R_7() {
        return this.jj_3R_161();
    }

    public boolean jj_3_2() {
        return this.jj_3R_45() ? true : this.jj_scan_token(LPAREN);
    }

    public final void ExpectedInitialHash() throws ZkmScriptParseException {
        ASTExpectedInitialHash aSTExpectedInitialHash = new ASTExpectedInitialHash();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExpectedInitialHash);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXPECTED_INITIAL_SHA256);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTExpectedInitialHash);
            bl = false;
            aSTExpectedInitialHash.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExpectedInitialHash);
            }
        }
    }

    public final void ComplexModuleSpecifier() throws ZkmScriptParseException {
        ASTComplexModuleSpecifier aSTComplexModuleSpecifier = new ASTComplexModuleSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexModuleSpecifier);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 28: {
                    this.BracketedModuleSpecifier();
                    break;
                }
                case 23:
                case 36:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213:
                case 214:
                case 215: {
                    this.ModuleName();
                    break;
                }
                default: {
                    this.jj_la1[138] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            switch (this.jj_nt.kind) {
                case 26: {
                    this.jj_consume_token(26);
                    aSTComplexModuleSpecifier.setExcludesModuleName();
                    break;
                }
                default: {
                    this.jj_la1[139] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(aSTComplexModuleSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexModuleSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public final ZkmScriptSimpleNode RemoveMethodCallsIncludeStatement() throws ZkmScriptParseException {
        ASTRemoveMethodCallsIncludeStatement aSTRemoveMethodCallsIncludeStatement = new ASTRemoveMethodCallsIncludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRemoveMethodCallsIncludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(REMOVE_METHOD_CALLS_INCLUDE);
            this.currentStatementKind = 165;
            switch (this.jj_nt.kind) {
                case CONTAINED_IN:
                    this.ContainedInClause();
                    break;
                default:
                    this.jj_la1[42] = this.jj_gen;
            }

            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        switch (this.jj_nt.kind) {
                            case CONTAINED_IN:
                                this.ContainedInClause();
                                break;
                            default:
                                this.jj_la1[44] = this.jj_gen;
                        }

                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[43] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTRemoveMethodCallsIncludeStatement);
                        bl = false;
                        aSTRemoveMethodCallsIncludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTRemoveMethodCallsIncludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTRemoveMethodCallsIncludeStatement);
            }
        }
    }

    public boolean jj_3R_8() {
        return this.jj_scan_token(KEEP);
    }

    public final void ArrayLevel() throws ZkmScriptParseException {
        ZkmScriptASTArrayLevel zkmScriptASTArrayLevel = new ZkmScriptASTArrayLevel();
        this.jjtree.openNodeScope(zkmScriptASTArrayLevel);

        try {
            this.jj_consume_token(LBRACKET);
            this.jj_consume_token(RBRACKET);
        } finally {
            this.jjtree.closeNodeScope(zkmScriptASTArrayLevel);
        }
    }

    




    public final void AndClassSpecifier() throws ZkmScriptParseException {
        ASTAndClassSpecifier aSTAndClassSpecifier = new ASTAndClassSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndClassSpecifier);
        try {
            this.ComplexClassSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[158] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexClassSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndClassSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndClassSpecifier);
                throw throwable2;
            }
        }
    }

    




    public final void ThrowsClause() throws ZkmScriptParseException {
        ASTThrowsClause aSTThrowsClause = new ASTThrowsClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTThrowsClause);
        try {
            this.jj_consume_token(62);
            this.QualifiedClassName();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 20: {
                        break;
                    }
                    default: {
                        this.jj_la1[120] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(20);
                this.QualifiedClassName();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTThrowsClause);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTThrowsClause);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_9() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_76()) {
            this.jj_scanpos = zkmScriptToken;
        }

        if (this.jj_scan_token(LPAREN)) {
            return true;
        }

        if (this.jj_3R_176()) {
            return true;
        }

        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_14());

        this.jj_scanpos = zkmScriptToken;
        return this.jj_scan_token(RPAREN);
    }

    




    public final void BracketedMemberSpecifier() throws ZkmScriptParseException {
        ASTBracketedMemberSpecifier aSTBracketedMemberSpecifier = new ASTBracketedMemberSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedMemberSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedMemberSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[111] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndMemberSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[112] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndMemberSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedMemberSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedMemberSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_10() {
        return this.jj_scan_token(WILDCARD_NAME) ? true : this.jj_scan_token(DOT);
    }

    public final void ComplexMethodSpecifier() throws ZkmScriptParseException {
        ASTComplexMethodSpecifier aSTComplexMethodSpecifier = new ASTComplexMethodSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexMethodSpecifier);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 28: {
                    this.BracketedMethodSpecifier();
                    break;
                }
                case 23:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 54:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 73:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213:
                case 214: {
                    this.MethodName();
                    break;
                }
                default: {
                    this.jj_la1[173] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTComplexMethodSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexMethodSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_11() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(BANG)) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_138();
    }

    public final void LinkClassName() throws ZkmScriptParseException {
        ASTLinkClassName aSTLinkClassName = new ASTLinkClassName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLinkClassName);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LINKED_CLASS_NAME_WITH_SUFFIX);
            aSTLinkClassName.setValue(zkmScriptToken.image);
            this.jjtree.closeNodeScope(aSTLinkClassName);
            bl = false;
            zkmScriptToken.setParameterKind(6);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLinkClassName);
            }
        }
    }

    public boolean jj_3R_12() {
        return this.jj_3R_222();
    }

    public final void PlusSignatureClasses() throws ZkmScriptParseException {
        ASTPlusSignatureClasses aSTPlusSignatureClasses = new ASTPlusSignatureClasses();
        this.jjtree.openNodeScope(aSTPlusSignatureClasses);

        try {
            this.jj_consume_token(SIGNATURE_CLASSES);
        } finally {
            this.jjtree.closeNodeScope(aSTPlusSignatureClasses);
        }
    }

    public boolean jj_3R_13() {
        return this.jj_scan_token(NORMAL) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_14() {
        return this.jj_3R_9();
    }

    public boolean jj_3R_15() {
        return this.jj_3R_125();
    }

    public boolean jj_3R_16() {
        return this.jj_scan_token(PRINT);
    }

    public final ZkmScriptSimpleNode AccessedByReflectionStatement() throws ZkmScriptParseException {
        ASTAccessedByReflectionStatement aSTAccessedByReflectionStatement = new ASTAccessedByReflectionStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAccessedByReflectionStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ACCESSED_BY_REFLECTION);
            this.currentStatementKind = 146;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[40] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTAccessedByReflectionStatement);
                        bl = false;
                        aSTAccessedByReflectionStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTAccessedByReflectionStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAccessedByReflectionStatement);
            }
        }
    }

    public final void UnSkipArchivePath() throws ZkmScriptParseException {
        ASTUnSkipArchivePath aSTUnSkipArchivePath = new ASTUnSkipArchivePath();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTUnSkipArchivePath);
        try {
            this.jj_consume_token(24);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTUnSkipArchivePath);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTUnSkipArchivePath);
                }
                throw throwable2;
            }
        }
    }

    public final void SaveAllStatement() throws ZkmScriptParseException {
        ASTSaveAllStatement aSTSaveAllStatement = new ASTSaveAllStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTSaveAllStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(SAVE_ALL);
            this.currentStatementKind = 65;
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case LAST_MODIFIED_TIME:
                    case DELETE_XMLCOMMENTS:
                    case ARCHIVE_COMPRESSION:
                    case 157:
                        this.SaveAllParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[73] = this.jj_gen;

                        while (true) {
                            this.StringLiteral();
                            switch (this.jj_nt.kind) {
                                case QUOTE_210:
                                    break;
                                default:
                                    this.jj_la1[74] = this.jj_gen;
                                    this.jj_consume_token(SEMICOLON);
                                    this.jjtree.closeNodeScope(aSTSaveAllStatement);
                                    bl = false;
                                    aSTSaveAllStatement.setLineNumber(zkmScriptToken.endLine);
                                    return;
                            }
                        }
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTSaveAllStatement);
            }
        }
    }

    public boolean jj_3R_17() {
        return this.jj_scan_token(PACKAGE_INFO);
    }

    public final void StandAloneAnnotation() throws ZkmScriptParseException {
        ASTStandAloneAnnotation aSTStandAloneAnnotation = new ASTStandAloneAnnotation();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTStandAloneAnnotation);
        try {
            this.jj_consume_token(27);
            this.QualifiedClassName();
            this.jjtree.closeNodeScope(aSTStandAloneAnnotation);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTStandAloneAnnotation);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_2_2() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_267();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(13, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3_3() {
        return this.jj_scan_token(OR) ? true : this.jj_3R_123();
    }

    public boolean jj_3R_18() {
        if (this.jj_3R_198()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_17());

        this.jj_scanpos = zkmScriptToken;
        return false;
    }

    public final void TrimParameter() throws ZkmScriptParseException {
        ASTTrimParameter aSTTrimParameter = new ASTTrimParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTTrimParameter);
        try {
            switch (this.jj_nt.kind) {
                case 178: {
                    this.DeleteSourceFileAttributesParameter();
                    break;
                }
                case 179: {
                    this.DeleteDeprecatedAttributesParameter();
                    break;
                }
                case 177: {
                    this.DeleteAnnotationsParameter();
                    break;
                }
                case 163: {
                    this.DeleteUnknownAttributesParameter();
                    break;
                }
                case 172: {
                    this.DeleteExceptionAttributesParameter();
                    break;
                }
                case 196: {
                    this.DeleteDebugExtensionAttributesParameter();
                    break;
                }
                default: {
                    this.jj_la1[98] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTTrimParameter);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTTrimParameter);
                }
                throw throwable2;
            }
        }
    }

    public final void EncryptionType() throws ZkmScriptParseException {
        ASTEncryptionType aSTEncryptionType = new ASTEncryptionType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTEncryptionType);

        try {
            switch (this.jj_nt.kind) {
                case 41:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(41);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken6.image);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken5.image);
                    break;
                case FALSE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(FALSE);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken4.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken3.image);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(ENHANCED);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken2.image);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(90);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken1.image);
                    break;
                case FLOW_OBFUSCATE:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(FLOW_OBFUSCATE);
                    this.jjtree.closeNodeScope(aSTEncryptionType);
                    bl = false;
                    aSTEncryptionType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[128] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTEncryptionType);
            }
        }
    }

    public boolean jj_2_3() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_215();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(7, Integer.MAX_VALUE);
        }

        return true;
    }

    public final void DeleteEmptyDirectoriesParameter() throws ZkmScriptParseException {
        ASTDeleteEmptyDirectoriesParameter aSTDeleteEmptyDirectoriesParameter = new ASTDeleteEmptyDirectoriesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteEmptyDirectoriesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(157);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteEmptyDirectoriesParameter);
            bl = false;
            aSTDeleteEmptyDirectoriesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteEmptyDirectoriesParameter);
            }
        }
    }

    public final void GarbageCollectStatement() throws ZkmScriptParseException {
        ASTGarbageCollectStatement aSTGarbageCollectStatement = new ASTGarbageCollectStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTGarbageCollectStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(GC);
            this.currentStatementKind = 36;
            ZkmScriptParser zkmScriptParser1;
            byte ba;
            switch (this.jj_nt.kind) {
                case INTEGER_LITERAL:
                    this.IntegerLiteral();
                    zkmScriptParser1 = this;
                    ba = 19;
                    break;
                default:
                    this.jj_la1[77] = this.jj_gen;
                    zkmScriptParser1 = this;
                    ba = 19;
            }

            zkmScriptParser1.jj_consume_token(ba);
            this.jjtree.closeNodeScope(aSTGarbageCollectStatement);
            bl = false;
            aSTGarbageCollectStatement.setLineNumber(zkmScriptToken.endLine);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTGarbageCollectStatement);
            }
        }
    }

    public boolean jj_3R_19() {
        return this.jj_scan_token(PRINT);
    }

    public boolean jj_3R_20() {
        return this.jj_scan_token(ANNOTATION);
    }

    public boolean jj_3R_21() {
        return this.jj_scan_token(PRINT);
    }

    public final void RandomizeParameter() throws ZkmScriptParseException {
        ASTRandomizeParameter aSTRandomizeParameter = new ASTRandomizeParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRandomizeParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RANDOMIZE);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTRandomizeParameter);
            bl = false;
            aSTRandomizeParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTRandomizeParameter);
            }
        }
    }

    public ZkmScriptParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] bl = new boolean[218];
        if (this.jj_kind >= 0) {
            bl[this.jj_kind] = true;
            this.jj_kind = -1;
        }

        int ba = 0;
        int bi = ba;

        for (short bj = 187; bi < bj; bj = 187) {
            if (this.jj_la1[ba] == this.jj_gen) {
                int bb = 0;
                bi = bb;

                for (byte bg = 32; bi < bg; bg = 32) {
                    int[] be;
                    if ((jj_la1_0[ba] & 1 << bb) != 0) {
                        bl[bb] = true;
                        be = jj_la1_1;
                    } else {
                        be = jj_la1_1;
                    }

                    if ((be[ba] & 1 << bb) != 0) {
                        bl[32 + bb] = true;
                    }

                    if ((jj_la1_2[ba] & 1 << bb) != 0) {
                        bl[64 + bb] = true;
                    }

                    if ((jj_la1_3[ba] & 1 << bb) != 0) {
                        bl[96 + bb] = true;
                    }

                    if ((jj_la1_4[ba] & 1 << bb) != 0) {
                        bl[128 + bb] = true;
                    }

                    int[] bf;
                    if ((jj_la1_5[ba] & 1 << bb) != 0) {
                        bl[160 + bb] = true;
                        bf = jj_la1_6;
                    } else {
                        bf = jj_la1_6;
                    }

                    if ((bf[ba] & 1 << bb) != 0) {
                        bl[192 + bb] = true;
                    }

                    bi = ++bb;
                }
            }

            bi = ++ba;
        }

        ba = 0;
        bi = 0;

        for (short bh = 218; bi < bh; bh = 218) {
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

        return new ZkmScriptParseException(this.token, bc, ZkmScriptConstants.TOKEN_IMAGE, this.currentStatementKind);
    }

    public static void jj_la1_init_5() {
        jj_la1_5 = new int[]{
                0,
                -328175882,
                -328175882,
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
                1048576,
                0,
                0,
                0,
                921608,
                310411521,
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
                921608,
                310411521,
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
                16777216,
                65536,
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

    public boolean jj_2_4() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_19();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(3, Integer.MAX_VALUE);
        }

        return true;
    }

    public final ZkmScriptSimpleNode IgnoreMissingReferencesStatement() throws ZkmScriptParseException {
        ASTIgnoreMissingReferencesStatement aSTIgnoreMissingReferencesStatement = new ASTIgnoreMissingReferencesStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIgnoreMissingReferencesStatement);

        try {
            ZkmScriptToken zkmScriptToken;
            ZkmScriptParser zkmScriptParser1;
            byte ba;
            zkmScriptToken = this.jj_consume_token(IGNORE_MISSING_REFERENCES);
            this.currentStatementKind = 161;
            label82:
            switch (this.jj_nt.kind) {
                case STAR:
                case BANG:
                case AT:
                case LPAREN:
                case GC:
                case 37:
                case OPEN:
                case ENUM:
                case KEEP:
                case 43:
                case TRIM:
                case PRINT:
                case LIGHT:
                case 48:
                case FINAL:
                case ASCII:
                case BRIDGE:
                case SEARCH:
                case PUBLIC:
                case DELETE:
                case NORMAL:
                case RANDOM:
                case EXCLUDE:
                case PACKAGE:
                case EXECUTE:
                case SCRAMBLE:
                case ABSTRACT:
                case ENHANCED:
                case SYNTHETIC:
                case CLASSPATH:
                case OBFUSCATE:
                case INTERFACE:
                case RANDOMIZE:
                case GROUPINGS:
                case ANNOTATION:
                case 90:
                case PACKAGE_INFO:
                case QUOTE_210:
                case NAME:
                case WILDCARD_NAME:
                case MODULE_NAME:
                case LINKED_CLASS_NAME_WITH_SUFFIX:
                    this.RenameFilterParameter();
                    ZkmScriptToken zkmScriptToken1 = this.jj_nt;

                    while (true) {
                        switch (zkmScriptToken1.kind) {
                            case AND:
                                this.jj_consume_token(AND);
                                this.RenameFilterParameter();
                                zkmScriptToken1 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[30] = this.jj_gen;
                                zkmScriptParser1 = this;
                                ba = 19;
                                break label82;
                        }
                    }
                case PLUS:
                case CARET:
                case RPAREN:
                case LBRACE:
                case RBRACE:
                case HOOK:
                case GT:
                case OR:
                case AND_AND:
                case AND:
                case 41:
                case AS_IS:
                case FALSE:
                case LINK:
                case INIT:
                case SPARSE:
                case STATIC:
                case NATIVE:
                case THROWS:
                case SAVE_ALL:
                case EXTENDS:
                case PRIVATE:
                case MODERATE:
                case CLINIT:
                case VOLATILE:
                case NON_ASCII:
                case PREVERIFY:
                case UNEXCLUDE:
                case PROTECTED:
                case TRANSIENT:
                case CONTAINING:
                case SAVE_ALL_OLD:
                case IMPLEMENTS:
                case CONTAINED_IN:
                case KEEP_VISIBLE:
                case TRIM_EXCLUDE:
                case LINE_NUMBERS:
                case IF_IN_ARCHIVE:
                case FIXED_CLASSES:
                case SYNCHRONIZED:
                case TRIM_UNEXCLUDE:
                case OBFUSCATE_FLOW:
                case FLOW_OBFUSCATE:
                case CHANGE_LOG_FILE:
                case LOCAL_VARIABLES:
                case HIDE_FIELD_NAMES:
                case NEW_NAMES_PREFIX:
                case RESET_GROUPINGS:
                case IN_SPECIAL_CLASS:
                case EXTRA_AGGRESSIVE:
                case CHANGE_LOG_FILE_IN:
                case RESET_EXCLUSIONS:
                case UNIQUE_CLASS_NAMES:
                case METHOD_PARAMETERS:
                case KEEP_GENERICS_INFO:
                case CHANGE_LOG_FILE_OUT:
                case LEGAL_IDENTIFIERS:
                case ALL_CLASSES_OPENED:
                case NEW_CLASS_NAME_FILE:
                case NEW_FIELD_NAME_FILE:
                case LAST_MODIFIED_TIME:
                case MAKE_CLASSES_PUBLIC:
                case UNIQUE_METHOD_NAMES:
                case NEW_METHOD_NAME_FILE:
                case KEEP_BALANCED_LOCKS:
                case SIGNATURE_CLASSES:
                case RESET_FIXED_CLASSES:
                case REMOVE_METHOD_CALLS:
                case NEW_NAME_CHARACTERS:
                case DELETE_XMLCOMMENTS:
                case OPEN_NESTED_ARCHIVES:
                case NEW_PACKAGE_NAME_FILE:
                case ARCHIVE_COMPRESSION:
                case KEEP_INNER_CLASS_INFO:
                case AUTO_REFLECTION_HASH:
                case OBFUSCATE_PARAMETERS:
                case MIXED_CASE_CLASS_NAMES:
                case IF_NAME_NOT_OBFUSCATED:
                case RESET_TRIM_EXCLUSIONS:
                case OBFUSCATE_REFERENCES:
                case KEEP_IF_NOT_OBFUSCATED:
                case EXPECTED_FINAL_SHA256:
                case IN_REFERENCING_CLASSES:
                case LOOSE_CHANGE_LOG_FILE_IN:
                case OBFUSCATE_FLOW_EXCLUDE:
                case EXCEPTION_OBFUSCATION:
                case ACCESSED_BY_REFLECTION:
                case ASSUME_RUNTIME_VERSION:
                case ENCRYPT_LONG_CONSTANTS:
                case HIDE_STATIC_METHOD_NAMES:
                case ENCRYPT_STRING_LITERALS:
                case AUTO_REFLECTION_PACKAGE:
                case LONG_ENCRYPTION_EXCLUDE:
                case EXPECTED_INITIAL_SHA256:
                case OBFUSCATE_FLOW_UNEXCLUDE:
                case METHOD_PARAMETER_CHANGES:
                case AUTO_REFLECTION_HANDLING:
                case 157:
                case RESET_REMOVE_METHOD_CALLS:
                case LONG_ENCRYPTION_UNEXCLUDE:
                case ENCRYPT_INTEGER_CONSTANTS:
                case IGNORE_MISSING_REFERENCES:
                case STRING_ENCRYPTION_EXCLUDE:
                case DELETE_UNKNOWN_ATTRIBUTES:
                case INTEGER_ENCRYPTION_EXCLUDE:
                case REMOVE_METHOD_CALLS_INCLUDE:
                case REMOVE_METHOD_CALLS_EXCLUDE:
                case CLASS_INITIALIZATION_ORDER:
                case AGGRESSIVE_METHOD_RENAMING:
                case EXISTING_SERIALIZED_CLASSES:
                case STRING_ENCRYPTION_UNEXCLUDE:
                case RESET_ACCESSED_BY_REFLECTION:
                case DELETE_EXCEPTION_ATTRIBUTES:
                case OBFUSCATE_EXCEPTIONS_EXCLUDE:
                case INTEGER_ENCRYPTION_UNEXCLUDE:
                case OBFUSCATE_REFERENCES_PACKAGE:
                case KEEP_VISIBLE_IF_NOT_OBFUSCATED:
                case DELETE_ANNOTATION_ATTRIBUTES:
                case DELETE_SOURCE_FILE_ATTRIBUTES:
                case DELETE_DEPRECATED_ATTRIBUTES:
                case OBFUSCATE_REFERENCES_INCLUDE:
                case OBFUSCATE_REFERENCES_EXCLUDE:
                case ACCESSED_BY_REFLECTION_EXCLUDE:
                case ALLOW_METHOD_PARAMETER_CHANGES:
                case KEEP_VISIBLE_METHOD_PARAMETERS:
                case COLLAPSE_PACKAGES_WITH_DEFAULT:
                case OBFUSCATE_EXCEPTIONS_UNEXCLUDE:
                case RESET_IGNORE_MISSING_REFERENCES:
                case OBFUSCATE_REFERENCE_STRUCTURES:
                case RESET_OBFUSCATE_FLOW_EXCLUSIONS:
                case RESET_LONG_ENCRYPTION_EXCLUSIONS:
                case RESET_CLASS_INITIALIZATION_ORDER:
                case METHOD_PARAMETER_CHANGES_INCLUDE:
                case METHOD_PARAMETER_CHANGES_EXCLUDE:
                case METHOD_PARAMETER_CHANGES_PACKAGE:
                case RESET_EXISTING_SERIALIZED_CLASSES:
                case DELETE_DEBUG_EXTENSION_ATTRIBUTES:
                case RESET_STRING_ENCRYPTION_EXCLUSIONS:
                case RESET_INTEGER_ENCRYPTION_EXCLUSIONS:
                case METHOD_PARAMETER_OBFUSCATION_INCLUDE:
                case METHOD_PARAMETER_OBFUSCATION_EXCLUDE:
                case DERIVE_GROUPINGS_FROM_INPUT_CHANGE_LOG:
                case DERIVE_SUBCLASS_NAMES_FROM_SUPERCLASS:
                case RESET_OBFUSCATE_REFERENCE_EXCLUSIONS:
                case RESET_OBFUSCATE_EXCEPTIONS_EXCLUSIONS:
                case KEEP_METHOD_PARAMETERS_IF_NOT_OBFUSCATED:
                case RESET_METHOD_PARAMETER_CHANGES_EXCLUSIONS:
                case RESET_METHOD_PARAMETER_OBFUSCATION_EXCLUSIONS:
                case KEEP_VISIBLE_METHOD_PARAMETERS_IF_NOT_OBFUSCATED:
                case QUOTE:
                case TOKEN_211:
                case INTEGER_LITERAL:
                default:
                    this.jj_la1[31] = this.jj_gen;
                    zkmScriptParser1 = this;
                    ba = 19;
            }

            zkmScriptParser1.jj_consume_token(ba);
            this.jjtree.closeNodeScope(aSTIgnoreMissingReferencesStatement);
            bl = false;
            aSTIgnoreMissingReferencesStatement.setLineNumber(zkmScriptToken.endLine);
            return aSTIgnoreMissingReferencesStatement;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIgnoreMissingReferencesStatement);
            }
        }
    }

    public boolean jj_2_5() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_104();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(24, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_22() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public final ZkmScriptSimpleNode IntegerEncryptionExcludeStatement() throws ZkmScriptParseException {
        ASTIntegerEncryptionExcludeStatement aSTIntegerEncryptionExcludeStatement = new ASTIntegerEncryptionExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIntegerEncryptionExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(INTEGER_ENCRYPTION_EXCLUDE);
            this.currentStatementKind = 164;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[24] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTIntegerEncryptionExcludeStatement);
                        bl = false;
                        aSTIntegerEncryptionExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTIntegerEncryptionExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIntegerEncryptionExcludeStatement);
            }
        }
    }

    public boolean jj_3R_23() {
        return this.jj_scan_token(CLINIT);
    }

    public boolean jj_3R_24() {
        return this.jj_scan_token(GROUPINGS) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_25() {
        return this.jj_scan_token(ANNOTATION);
    }

    public final ZkmScriptSimpleNode DefaultTrimExcludeStatement() throws ZkmScriptParseException {
        ASTDefaultTrimExcludeStatement aSTDefaultTrimExcludeStatement = new ASTDefaultTrimExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultTrimExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(TRIM_EXCLUDE);
            this.currentStatementKind = 94;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[59] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTDefaultTrimExcludeStatement);
                        bl = false;
                        aSTDefaultTrimExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTDefaultTrimExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultTrimExcludeStatement);
            }
        }
    }

    public boolean jj_3R_26() {
        return this.jj_3R_249();
    }

    public boolean jj_3R_27() {
        return this.jj_3R_137();
    }

    public final void UniqueClassNamesParameter() throws ZkmScriptParseException {
        ASTUniqueClassNamesParameter aSTUniqueClassNamesParameter = new ASTUniqueClassNamesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTUniqueClassNamesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(UNIQUE_CLASS_NAMES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTUniqueClassNamesParameter);
            bl = false;
            aSTUniqueClassNamesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTUniqueClassNamesParameter);
            }
        }
    }

    public final void LoadParameter() throws ZkmScriptParseException {
        ASTLoadParameter aSTLoadParameter = new ASTLoadParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLoadParameter);
        try {
            switch (this.jj_nt.kind) {
                case 153: {
                    this.ExpectedInitialHash();
                    break;
                }
                case 141: {
                    this.ExpectedFinalHash();
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTLoadParameter);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTLoadParameter);
                }
                throw throwable2;
            }
        }
    }

    public final ZkmScriptSimpleNode ObfuscateReferencesExcludeStatement() throws ZkmScriptParseException {
        ASTObfuscateReferencesExcludeStatement aSTObfuscateReferencesExcludeStatement = new ASTObfuscateReferencesExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferencesExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_REFERENCES_EXCLUDE);
            this.currentStatementKind = 181;
            switch (this.jj_nt.kind) {
                case CONTAINED_IN:
                    this.ContainedInClause();
                    break;
                default:
                    this.jj_la1[37] = this.jj_gen;
            }

            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        switch (this.jj_nt.kind) {
                            case CONTAINED_IN:
                                this.ContainedInClause();
                                break;
                            default:
                                this.jj_la1[39] = this.jj_gen;
                        }

                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[38] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateReferencesExcludeStatement);
                        bl = false;
                        aSTObfuscateReferencesExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateReferencesExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferencesExcludeStatement);
            }
        }
    }

    public ZkmScriptToken jj_consume_token(int jj_kind) throws ZkmScriptParseException {
        ZkmScriptToken zkmScriptToken = this.token;
        if ((this.token = this.jj_nt).next != null) {
            this.jj_nt = this.jj_nt.next;
        } else {
            this.jj_nt = this.jj_nt.next = this.token_source.getNextToken();
        }

        if (this.token.kind != jj_kind) {
            this.jj_nt = this.token;
            this.token = zkmScriptToken;
            this.jj_kind = jj_kind;
            throw this.generateParseException();
        }

        this.jj_gen++;
        if (++this.jj_gc > 100) {
            this.jj_gc = 0;

            for (int i = 0; i < this.jj_2_rtns.length; i++) {
                ZkmScriptParserJJCalls zkmScriptParserJJCalls = this.jj_2_rtns[i];

                while (zkmScriptParserJJCalls != null) {
                    ZkmScriptParserJJCalls zkmScriptParserJJCalls1;
                    if (zkmScriptParserJJCalls.gen < this.jj_gen) {
                        zkmScriptParserJJCalls.first = null;
                        zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                    } else {
                        zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                    }

                    zkmScriptParserJJCalls = zkmScriptParserJJCalls1;
                }
            }
        }

        return this.token;
    }

    public boolean jj_2_6() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_131();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(1, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_28() {
        return this.jj_scan_token(ENUM);
    }

    public boolean jj_3R_29() {
        return this.jj_3R_128();
    }

    public final void KeepInnerClassesParameter() throws ZkmScriptParseException {
        ASTKeepInnerClassesParameter aSTKeepInnerClassesParameter = new ASTKeepInnerClassesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepInnerClassesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(KEEP_INNER_CLASS_INFO);
            this.jj_consume_token(ASSIGN);
            this.BooleanOrIfNameNotObfucated();
            this.jjtree.closeNodeScope(aSTKeepInnerClassesParameter);
            bl = false;
            aSTKeepInnerClassesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepInnerClassesParameter);
            }
        }
    }

    public final void MemberModifierHelper() throws ZkmScriptParseException {
        ASTMemberModifierHelper aSTMemberModifierHelper = new ASTMemberModifierHelper();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMemberModifierHelper);

        try {
            switch (this.jj_nt.kind) {
                case ENUM:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(ENUM);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken13.image);
                    break;
                case 41:
                case KEEP:
                case 43:
                case AS_IS:
                case TRIM:
                case PRINT:
                case LIGHT:
                case 48:
                case FALSE:
                case ASCII:
                case LINK:
                case INIT:
                case SPARSE:
                case SEARCH:
                case DELETE:
                case NORMAL:
                case THROWS:
                case RANDOM:
                case EXCLUDE:
                case SAVE_ALL:
                case EXTENDS:
                case EXECUTE:
                case MODERATE:
                case SCRAMBLE:
                case CLINIT:
                case ENHANCED:
                case NON_ASCII:
                case PREVERIFY:
                case UNEXCLUDE:
                case CLASSPATH:
                case OBFUSCATE:
                case INTERFACE:
                case RANDOMIZE:
                case GROUPINGS:
                case ANNOTATION:
                case CONTAINING:
                case SAVE_ALL_OLD:
                case 90:
                case IMPLEMENTS:
                case CONTAINED_IN:
                case KEEP_VISIBLE:
                case TRIM_EXCLUDE:
                case LINE_NUMBERS:
                case IF_IN_ARCHIVE:
                case PACKAGE_INFO:
                case FIXED_CLASSES:
                default:
                    this.jj_la1[107] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                case FINAL:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(FINAL);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken12.image);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(BRIDGE);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken11.image);
                    break;
                case PUBLIC:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(PUBLIC);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken10.image);
                    break;
                case STATIC:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(STATIC);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken9.image);
                    break;
                case NATIVE:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(NATIVE);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken8.image);
                    break;
                case PACKAGE:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(PACKAGE);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken7.image);
                    break;
                case PRIVATE:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(PRIVATE);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken6.image);
                    break;
                case ABSTRACT:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(ABSTRACT);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken5.image);
                    break;
                case VOLATILE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(VOLATILE);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken4.image);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(SYNTHETIC);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken3.image);
                    break;
                case PROTECTED:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(PROTECTED);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken2.image);
                    break;
                case TRANSIENT:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(TRANSIENT);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken1.image);
                    break;
                case SYNCHRONIZED:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(SYNCHRONIZED);
                    this.jjtree.closeNodeScope(aSTMemberModifierHelper);
                    bl = false;
                    aSTMemberModifierHelper.setValue(zkmScriptToken.image);
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMemberModifierHelper);
            }
        }
    }

    public boolean jj_3R_30() {
        return this.jj_scan_token(BANG);
    }

    




    public final void BracketedMethodSpecifier() throws ZkmScriptParseException {
        ASTBracketedMethodSpecifier aSTBracketedMethodSpecifier = new ASTBracketedMethodSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedMethodSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedMethodSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[174] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndMethodSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[175] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndMethodSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedMethodSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedMethodSpecifier);
                throw throwable2;
            }
        }
    }

    public final void NegatedClassModifier() throws ZkmScriptParseException {
        ASTNegatedClassModifier aSTNegatedClassModifier = new ASTNegatedClassModifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNegatedClassModifier);

        try {
            this.jj_consume_token(BANG);
            switch (this.jj_nt.kind) {
                case ENUM:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(ENUM);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken7.image);
                    break;
                case FINAL:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(FINAL);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken6.image);
                    break;
                case PUBLIC:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(PUBLIC);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken5.image);
                    break;
                case PACKAGE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(PACKAGE);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken4.image);
                    break;
                case ABSTRACT:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(ABSTRACT);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken3.image);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(SYNTHETIC);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken2.image);
                    break;
                case INTERFACE:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(INTERFACE);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken1.image);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(ANNOTATION);
                    this.jjtree.closeNodeScope(aSTNegatedClassModifier);
                    bl = false;
                    aSTNegatedClassModifier.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[106] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNegatedClassModifier);
            }
        }
    }

    public final void ClasspathStatement() throws ZkmScriptParseException {
        ASTClasspathStatement aSTClasspathStatement = new ASTClasspathStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTClasspathStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(CLASSPATH);
            this.currentStatementKind = 80;
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case QUOTE_210:
                        this.StringLiteral();
                        break;
                    case INTEGER_LITERAL:
                        this.IntegerLiteral();
                        break;
                    default:
                        this.jj_la1[69] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ZkmScriptParseException();
                }

                switch (this.jj_nt.kind) {
                    case QUOTE_210:
                    case INTEGER_LITERAL:
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[70] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTClasspathStatement);
                        bl = false;
                        aSTClasspathStatement.setLineNumber(zkmScriptToken.endLine);
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTClasspathStatement);
            }
        }
    }

    public final void BooleanOrIfInArchive() throws ZkmScriptParseException {
        ASTBooleanOrIfInArchive aSTBooleanOrIfInArchive = new ASTBooleanOrIfInArchive();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBooleanOrIfInArchive);

        try {
            switch (this.jj_nt.kind) {
                case 41:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(41);
                    this.jjtree.closeNodeScope(aSTBooleanOrIfInArchive);
                    bl = false;
                    aSTBooleanOrIfInArchive.setValue(zkmScriptToken2.image);
                    break;
                case FALSE:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(FALSE);
                    this.jjtree.closeNodeScope(aSTBooleanOrIfInArchive);
                    bl = false;
                    aSTBooleanOrIfInArchive.setValue(zkmScriptToken1.image);
                    break;
                case IF_IN_ARCHIVE:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(IF_IN_ARCHIVE);
                    this.jjtree.closeNodeScope(aSTBooleanOrIfInArchive);
                    bl = false;
                    aSTBooleanOrIfInArchive.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[186] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTBooleanOrIfInArchive);
            }
        }
    }

    public boolean jj_3R_31() {
        return this.jj_scan_token(PRINT);
    }

    




    public final void AndMemberSpecifier() throws ZkmScriptParseException {
        ASTAndMemberSpecifier aSTAndMemberSpecifier = new ASTAndMemberSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndMemberSpecifier);
        try {
            this.ComplexMemberSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[113] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexMemberSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndMemberSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndMemberSpecifier);
                throw throwable2;
            }
        }
    }

    public final void ClassInitializationOrder() throws ZkmScriptParseException {
        ASTClassInitializationOrder aSTClassInitializationOrder = new ASTClassInitializationOrder();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTClassInitializationOrder);
        try {
            this.QualifiedClassName();
            this.jj_consume_token(33);
            this.QualifiedClassName();
            this.jjtree.closeNodeScope(aSTClassInitializationOrder);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTClassInitializationOrder);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_32() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_237()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_7()) {
                return true;
            }
        }

        return false;
    }

    public final void MemberSpecifierModifier() throws ZkmScriptParseException {
        ASTMemberSpecifierModifier aSTMemberSpecifierModifier = new ASTMemberSpecifierModifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMemberSpecifierModifier);
        try {
            this.MemberModifierHelper();
            this.jjtree.closeNodeScope(aSTMemberSpecifierModifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTMemberSpecifierModifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_33() {
        return this.jj_scan_token(PRIVATE);
    }

    public boolean jj_3R_34() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public boolean jj_3R_35() {
        return this.jj_scan_token(RANDOM) ? true : this.jj_scan_token(DOT);
    }

    public final void ObfuscateParameter() throws ZkmScriptParseException {
        ASTObfuscateParameter aSTObfuscateParameter = new ASTObfuscateParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateParameter);
        try {
            switch (this.jj_nt.kind) {
                case 110:
                case 143: {
                    this.ChangeLogInParameter();
                    break;
                }
                case 103:
                case 115: {
                    this.ChangeLogOutParameter();
                    break;
                }
                case 150: {
                    this.EncryptParameter();
                    break;
                }
                case 160: {
                    this.EncryptIntegerConstantsParameter();
                    break;
                }
                case 148: {
                    this.EncryptLongConstantsParameter();
                    break;
                }
                case 101: {
                    this.ObfuscateFlowParameter();
                    break;
                }
                case 135: {
                    this.ObfuscateParametersParameter();
                    break;
                }
                case 168: {
                    this.AggressiveOverloadParameter();
                    break;
                }
                case 133: {
                    this.KeepInnerClassesParameter();
                    break;
                }
                case 114: {
                    this.KeepGenericsParameter();
                    break;
                }
                case 95: {
                    this.LineNumbersParameter();
                    break;
                }
                case 104: {
                    this.LocalVariablesParameter();
                    break;
                }
                case 113: {
                    this.MethodParametersParameter();
                    break;
                }
                case 185: {
                    this.CollapsePackages();
                    break;
                }
                case 116: {
                    this.LegalIdsParameter();
                    break;
                }
                case 128: {
                    this.NewNameCharactersParameter();
                    break;
                }
                case 106: {
                    this.NewNamesPrefixParameter();
                    break;
                }
                case 85: {
                    this.RandomizeParameter();
                    break;
                }
                case 117: {
                    this.EnhancedIncrementalParameter();
                    break;
                }
                case 201: {
                    this.DeriveGroupingsFromChangeLogParameter();
                    break;
                }
                case 105: {
                    this.HideFieldNamesParameter();
                    break;
                }
                case 149: {
                    this.HideStaticMethodNamesParameter();
                    break;
                }
                case 145: {
                    this.ExceptionObfuscationParameter();
                    break;
                }
                case 151: {
                    this.AutoReflectionPackageParameter();
                    break;
                }
                case 134: {
                    this.AutoReflectionHashParameter();
                    break;
                }
                case 156: {
                    this.AutoReflectionParameter();
                    break;
                }
                case 78: {
                    this.PreverifyParameter();
                    break;
                }
                case 121: {
                    this.MakeClassesPublicParameter();
                    break;
                }
                case 124: {
                    this.KeepBalancedLocksParameter();
                    break;
                }
                case 136: {
                    this.MixedCaseClassNamesParameter();
                    break;
                }
                case 112: {
                    this.UniqueClassNamesParameter();
                    break;
                }
                case 122: {
                    this.UniqueMethodNamesParameter();
                    break;
                }
                case 139: {
                    this.ObfuscateReferencesParameter();
                    break;
                }
                case 188: {
                    this.ObfuscateReferenceStructuresParameter();
                    break;
                }
                case 175: {
                    this.ObfuscateReferencesPackageParameter();
                    break;
                }
                case 147: {
                    this.AssumeRuntimeVersionParameter();
                    break;
                }
                case 183: {
                    this.AllowMethodParameterChangesParameter();
                    break;
                }
                case 155: {
                    this.MethodParameterChangesParameter();
                    break;
                }
                case 194: {
                    this.MethodParameterChangePackageParameter();
                    break;
                }
                case 131: {
                    this.NewPackageNameFileParameter();
                    break;
                }
                case 118: {
                    this.NewClassNameFileParameter();
                    break;
                }
                case 119: {
                    this.NewFieldNameFileParameter();
                    break;
                }
                case 123: {
                    this.NewMethodNameFileParameter();
                    break;
                }
                default: {
                    this.jj_la1[99] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTObfuscateParameter);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTObfuscateParameter);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_36() {
        return this.jj_scan_token(ABSTRACT);
    }

    public boolean jj_3R_37() {
        return this.jj_scan_token(NAME);
    }

    public final void ExpectedFinalHash() throws ZkmScriptParseException {
        ASTExpectedFinalHash aSTExpectedFinalHash = new ASTExpectedFinalHash();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExpectedFinalHash);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXPECTED_FINAL_SHA256);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTExpectedFinalHash);
            bl = false;
            aSTExpectedFinalHash.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExpectedFinalHash);
            }
        }
    }

    public final void LineNumberParameterType() throws ZkmScriptParseException {
        ASTLineNumberParameterType aSTLineNumberParameterType = new ASTLineNumberParameterType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLineNumberParameterType);

        try {
            switch (this.jj_nt.kind) {
                case KEEP:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(KEEP);
                    this.jjtree.closeNodeScope(aSTLineNumberParameterType);
                    bl = false;
                    aSTLineNumberParameterType.setValue(zkmScriptToken2.image);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(DELETE);
                    this.jjtree.closeNodeScope(aSTLineNumberParameterType);
                    bl = false;
                    aSTLineNumberParameterType.setValue(zkmScriptToken1.image);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(SCRAMBLE);
                    this.jjtree.closeNodeScope(aSTLineNumberParameterType);
                    bl = false;
                    aSTLineNumberParameterType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[131] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLineNumberParameterType);
            }
        }
    }

    public boolean jj_3R_38() {
        return this.jj_scan_token(STAR);
    }

    public boolean jj_3R_39() {
        return this.jj_3R_143();
    }

    public final ZkmScriptSimpleNode ObfuscateFlowExcludeStatement() throws ZkmScriptParseException {
        ASTObfuscateFlowExcludeStatement aSTObfuscateFlowExcludeStatement = new ASTObfuscateFlowExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateFlowExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_FLOW_EXCLUDE);
            this.currentStatementKind = 144;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[18] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateFlowExcludeStatement);
                        bl = false;
                        aSTObfuscateFlowExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateFlowExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateFlowExcludeStatement);
            }
        }
    }

    public boolean jj_3R_40() {
        return this.jj_scan_token(RANDOM);
    }

    public boolean jj_3R_41() {
        return this.jj_scan_token(RANDOMIZE);
    }

    public final void AndFileFilterComponent() throws ZkmScriptParseException {
        ASTAndFileFilterComponent aSTAndFileFilterComponent = new ASTAndFileFilterComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndFileFilterComponent);
        try {
            this.jj_consume_token(35);
            switch (this.jj_nt.kind) {
                case 210: {
                    this.FileFilterComponent();
                    break;
                }
                case 25: {
                    this.NegatedFileFilterComponent();
                    break;
                }
                default: {
                    this.jj_la1[14] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTAndFileFilterComponent);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTAndFileFilterComponent);
                }
                throw throwable2;
            }
        }
    }

    public final void IntegerEncryptionType() throws ZkmScriptParseException {
        ASTIntegerEncryptionType aSTIntegerEncryptionType = new ASTIntegerEncryptionType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIntegerEncryptionType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTIntegerEncryptionType);
                    bl = false;
                    aSTIntegerEncryptionType.setValue(zkmScriptToken2.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTIntegerEncryptionType);
                    bl = false;
                    aSTIntegerEncryptionType.setValue(zkmScriptToken1.image);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(90);
                    this.jjtree.closeNodeScope(aSTIntegerEncryptionType);
                    bl = false;
                    aSTIntegerEncryptionType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[129] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIntegerEncryptionType);
            }
        }
    }

    public boolean jj_3R_42() {
        return this.jj_scan_token(RANDOM);
    }

    public boolean jj_3R_43() {
        return this.jj_scan_token(ANNOTATION);
    }

    public boolean jj_3R_44() {
        return this.jj_scan_token(NAME);
    }

    public boolean jj_3R_45() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_110()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_86()) {
                return true;
            }
        }

        return false;
    }

    public boolean jj_3R_46() {
        return this.jj_scan_token(LIGHT);
    }

    public boolean jj_3R_47() {
        return this.jj_scan_token(LIGHT);
    }

    public static void jj_la1_init_3() {
        jj_la1_3 = new int[]{
                0,
                -1073706988,
                -1073706988,
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
                2,
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
                520046496,
                16777216,
                0,
                16777216,
                0,
                0,
                0,
                0,
                0,
                8,
                0,
                8,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                8,
                0,
                8,
                2,
                2,
                0,
                520046496,
                0,
                0,
                16384,
                524416,
                0,
                0,
                0,
                8,
                8,
                2,
                8,
                0,
                0,
                0,
                8,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                64,
                0,
                4096,
                8192,
                0,
                64,
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
                2,
                0,
                0,
                0,
                0,
                0,
                2,
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
                536870912,
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
                1
        };
    }

    public boolean jj_3R_48() {
        return this.jj_scan_token(RANDOM);
    }

    public final ZkmScriptSimpleNode ObfuscateReferencesIncludeStatement() throws ZkmScriptParseException {
        ASTObfuscateReferencesIncludeStatement aSTObfuscateReferencesIncludeStatement = new ASTObfuscateReferencesIncludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferencesIncludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_REFERENCES_INCLUDE);
            this.currentStatementKind = 180;
            switch (this.jj_nt.kind) {
                case CONTAINED_IN:
                    this.ContainedInClause();
                    break;
                default:
                    this.jj_la1[34] = this.jj_gen;
            }

            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        switch (this.jj_nt.kind) {
                            case CONTAINED_IN:
                                this.ContainedInClause();
                                break;
                            default:
                                this.jj_la1[36] = this.jj_gen;
                        }

                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[35] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateReferencesIncludeStatement);
                        bl = false;
                        aSTObfuscateReferencesIncludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateReferencesIncludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferencesIncludeStatement);
            }
        }
    }

    




    public final void AndFieldSpecifier() throws ZkmScriptParseException {
        ASTAndFieldSpecifier aSTAndFieldSpecifier = new ASTAndFieldSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndFieldSpecifier);
        try {
            this.ComplexFieldSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[164] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexFieldSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndFieldSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndFieldSpecifier);
                throw throwable2;
            }
        }
    }

    public final void Type() throws ZkmScriptParseException {
        ZkmScriptASTType zkmScriptASTType = new ZkmScriptASTType();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTType);

        try {
            switch (this.jj_nt.kind) {
                case 37:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(37);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken26.image);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(OPEN);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken25.image);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(ENUM);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken24.image);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(KEEP);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken23.image);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken22.image);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(TRIM);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken21.image);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(PRINT);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken20.image);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(LIGHT);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken19.image);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(48);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken18.image);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(ASCII);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken17.image);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(BRIDGE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken16.image);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(SEARCH);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken15.image);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(DELETE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken14.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken13.image);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(RANDOM);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken12.image);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(EXCLUDE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken11.image);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(EXECUTE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken10.image);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(SCRAMBLE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken9.image);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(ENHANCED);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken8.image);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(SYNTHETIC);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken7.image);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(CLASSPATH);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken6.image);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(OBFUSCATE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken5.image);
                    break;
                case RANDOMIZE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(RANDOMIZE);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken4.image);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(GROUPINGS);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken3.image);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(ANNOTATION);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken2.image);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(90);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken1.image);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(NAME);
                    this.jjtree.closeNodeScope(zkmScriptASTType);
                    bl = false;
                    zkmScriptASTType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[183] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(zkmScriptASTType);
            }
        }
    }

    public boolean jj_2_7() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_18();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(11, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_2_8() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_154();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(16, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_49() {
        return this.jj_scan_token(STAR);
    }

    public final ZkmScriptSimpleNode DefaultMethodParameterChangesExcludeInput() throws ZkmScriptParseException {
        ASTDefaultMethodParameterChangesExcludeInput aSTDefaultMethodParameterChangesExcludeInput = new ASTDefaultMethodParameterChangesExcludeInput();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultMethodParameterChangesExcludeInput);

        try {
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[60] = this.jj_gen;
            }

            switch (this.jj_nt.kind) {
                case METHOD_PARAMETER_CHANGES_EXCLUDE:
                    this.DefaultMethodParameterChangesExcludeStatement();
                    break;
                default:
                    this.jj_la1[61] = this.jj_gen;
            }

            this.jj_consume_token(EOF);
            this.jjtree.closeNodeScope(aSTDefaultMethodParameterChangesExcludeInput);
            bl = false;
            return aSTDefaultMethodParameterChangesExcludeInput;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultMethodParameterChangesExcludeInput);
            }
        }
    }

    public boolean jj_3R_50() {
        return this.jj_scan_token(EXECUTE);
    }

    public boolean jj_3R_51() {
        return this.jj_scan_token(DOT);
    }

    public final void ObfuscateReferencesType() throws ZkmScriptParseException {
        ASTObfuscateReferencesType aSTObfuscateReferencesType = new ASTObfuscateReferencesType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferencesType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTObfuscateReferencesType);
                    bl = false;
                    aSTObfuscateReferencesType.setValue(zkmScriptToken1.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTObfuscateReferencesType);
                    bl = false;
                    aSTObfuscateReferencesType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[124] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferencesType);
            }
        }
    }

    public boolean jj_3R_52() {
        return this.jj_scan_token(43);
    }

    public final void DeleteUnknownAttributesParameter() throws ZkmScriptParseException {
        ASTDeleteUnknownAttributesParameter aSTDeleteUnknownAttributesParameter = new ASTDeleteUnknownAttributesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteUnknownAttributesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_UNKNOWN_ATTRIBUTES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteUnknownAttributesParameter);
            bl = false;
            aSTDeleteUnknownAttributesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteUnknownAttributesParameter);
            }
        }
    }

    public boolean jj_3R_53() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_155()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_265()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_172()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_84()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_69()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_41()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_200()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_16()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_60()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_98()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_201()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_90()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_193()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_62()) {
                                                                this.jj_scanpos = zkmScriptToken;
                                                                if (this.jj_3R_85()) {
                                                                    this.jj_scanpos = zkmScriptToken;
                                                                    if (this.jj_3R_199()) {
                                                                        this.jj_scanpos = zkmScriptToken;
                                                                        if (this.jj_3R_47()) {
                                                                            this.jj_scanpos = zkmScriptToken;
                                                                            if (this.jj_3R_183()) {
                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                if (this.jj_3R_148()) {
                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                    if (this.jj_3R_269()) {
                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                        if (this.jj_3R_94()) {
                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                            if (this.jj_3R_242()) {
                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                if (this.jj_3R_227()) {
                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                    if (this.jj_3R_238()) {
                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                        if (this.jj_3R_55()) {
                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                            if (this.jj_3R_166()) {
                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                if (this.jj_3R_20()) {
                                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                                    if (this.jj_3R_195()) {
                                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                                        if (this.jj_3R_111()) {
                                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                                            if (this.jj_3R_184()) {
                                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                                if (this.jj_3R_275()) {
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
        }

        return false;
    }

    public final void BasicMethodSignature() throws ZkmScriptParseException {
        ASTBasicMethodSignature aSTBasicMethodSignature = new ASTBasicMethodSignature();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBasicMethodSignature);
        try {
            this.ComplexMethodSpecifier();
            this.jj_consume_token(28);
            switch (this.jj_nt.kind) {
                case 23:
                case 25:
                case 27:
                case 28:
                case 32:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213: {
                    this.MethodArguments();
                    break;
                }
                default: {
                    this.jj_la1[166] = this.jj_gen;
                }
            }
            this.jj_consume_token(29);
            switch (this.jj_nt.kind) {
                case 62: {
                    this.ThrowsClause();
                    break;
                }
                default: {
                    this.jj_la1[167] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(aSTBasicMethodSignature);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTBasicMethodSignature);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_54() {
        return this.jj_scan_token(PACKAGE) ? true : this.jj_scan_token(DOT);
    }

    public final void HideFieldNamesParameter() throws ZkmScriptParseException {
        ASTHideFieldNamesParameter aSTHideFieldNamesParameter = new ASTHideFieldNamesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTHideFieldNamesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(HIDE_FIELD_NAMES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTHideFieldNamesParameter);
            bl = false;
            aSTHideFieldNamesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTHideFieldNamesParameter);
            }
        }
    }

    public boolean jj_3R_55() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public void jj_save(int ba, int bb) {
        com.zelix.klassmaster.script.ZkmScriptParserJJCalls zkmScriptParserJJCalls1 = null;
        ZkmScriptParserJJCalls zkmScriptParserJJCalls = this.jj_2_rtns[ba];
        int bc = zkmScriptParserJJCalls.gen;

        int bd;
        while (true) {
            if (bc <= this.jj_gen) {
                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls;
                bd = this.jj_gen;
                break;
            }

            if (zkmScriptParserJJCalls.next == null) {
                zkmScriptParserJJCalls = zkmScriptParserJJCalls.next = new ZkmScriptParserJJCalls();
                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls;
                bd = this.jj_gen;
                break;
            }

            zkmScriptParserJJCalls = zkmScriptParserJJCalls.next;
            bc = zkmScriptParserJJCalls.gen;
        }

        zkmScriptParserJJCalls1.gen = bd + bb - this.jj_la;
        zkmScriptParserJJCalls.first = this.token;
        zkmScriptParserJJCalls.arg = bb;
    }

    public boolean jj_3R_56() {
        return this.jj_scan_token(DELETE);
    }

    public final ZkmScriptSimpleNode LongEncryptionExcludeStatement() throws ZkmScriptParseException {
        ASTLongEncryptionExcludeStatement aSTLongEncryptionExcludeStatement = new ASTLongEncryptionExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLongEncryptionExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LONG_ENCRYPTION_EXCLUDE);
            this.currentStatementKind = 152;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[26] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTLongEncryptionExcludeStatement);
                        bl = false;
                        aSTLongEncryptionExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTLongEncryptionExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLongEncryptionExcludeStatement);
            }
        }
    }

    public final ZkmScriptSimpleNode StringEncryptionUnexcludeStatement() throws ZkmScriptParseException {
        ASTStringEncryptionUnexcludeStatement aSTStringEncryptionUnexcludeStatement = new ASTStringEncryptionUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTStringEncryptionUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(STRING_ENCRYPTION_UNEXCLUDE);
            this.currentStatementKind = 170;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[23] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTStringEncryptionUnexcludeStatement);
                        bl = false;
                        aSTStringEncryptionUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTStringEncryptionUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTStringEncryptionUnexcludeStatement);
            }
        }
    }

    public boolean jj_3R_57() {
        return this.jj_3R_222();
    }

    public final ZkmScriptSimpleNode ObfuscateExceptionsExcludeStatement() throws ZkmScriptParseException {
        ASTObfuscateExceptionsExcludeStatement aSTObfuscateExceptionsExcludeStatement = new ASTObfuscateExceptionsExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateExceptionsExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_EXCEPTIONS_EXCLUDE);
            this.currentStatementKind = 173;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[20] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateExceptionsExcludeStatement);
                        bl = false;
                        aSTObfuscateExceptionsExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateExceptionsExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateExceptionsExcludeStatement);
            }
        }
    }

    public final void ResetAccessedByReflectionStatement() throws ZkmScriptParseException {
        ASTResetAccessedByReflectionStatement aSTResetAccessedByReflectionStatement = new ASTResetAccessedByReflectionStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetAccessedByReflectionStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_ACCESSED_BY_REFLECTION);
            this.currentStatementKind = 171;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetAccessedByReflectionStatement);
            bl = false;
            aSTResetAccessedByReflectionStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetAccessedByReflectionStatement);
            }
        }
    }

    public boolean jj_3R_58() {
        return this.jj_scan_token(OBFUSCATE);
    }

    public boolean jj_3R_59() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(BANG)) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_159();
    }

    public final void ContainingClause() throws ZkmScriptParseException {
        ASTContainingClause aSTContainingClause = new ASTContainingClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTContainingClause);
        try {
            this.jj_consume_token(88);
            this.jj_consume_token(30);
            switch (this.jj_nt.kind) {
                case 23:
                case 25:
                case 27:
                case 28:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 50:
                case 51:
                case 52:
                case 54:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 63:
                case 64:
                case 67:
                case 68:
                case 69:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 77:
                case 80:
                case 81:
                case 82:
                case 84:
                case 85:
                case 86:
                case 87:
                case 90:
                case 99:
                case 213:
                case 214: {
                    this.ComplexMemberSpecifier();
                    break;
                }
                default: {
                    this.jj_la1[108] = this.jj_gen;
                }
            }
            this.jj_consume_token(31);
            this.jjtree.closeNodeScope(aSTContainingClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTContainingClause);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_60() {
        return this.jj_scan_token(DELETE);
    }

    public final void ResetClassInitializationOrderStatement() throws ZkmScriptParseException {
        ASTResetClassInitializationOrderStatement aSTResetClassInitializationOrderStatement = new ASTResetClassInitializationOrderStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetClassInitializationOrderStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_CLASS_INITIALIZATION_ORDER);
            this.currentStatementKind = 191;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetClassInitializationOrderStatement);
            bl = false;
            aSTResetClassInitializationOrderStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetClassInitializationOrderStatement);
            }
        }
    }

    public final void ClassName() throws ZkmScriptParseException {
        ASTClassName aSTClassName = new ASTClassName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTClassName);

        try {
            switch (this.jj_nt.kind) {
                case STAR:
                    ZkmScriptToken zkmScriptToken29 = this.jj_consume_token(STAR);
                    aSTClassName.setValue(zkmScriptToken29.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken29.setParameterKind(3);
                    break;
                case 37:
                    ZkmScriptToken zkmScriptToken28 = this.jj_consume_token(37);
                    aSTClassName.setValue(zkmScriptToken28.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken28.setParameterKind(3);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken27 = this.jj_consume_token(OPEN);
                    aSTClassName.setValue(zkmScriptToken27.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken27.setParameterKind(3);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(ENUM);
                    aSTClassName.setValue(zkmScriptToken26.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken26.setParameterKind(3);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(KEEP);
                    aSTClassName.setValue(zkmScriptToken25.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken25.setParameterKind(3);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(43);
                    aSTClassName.setValue(zkmScriptToken24.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken24.setParameterKind(3);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(TRIM);
                    aSTClassName.setValue(zkmScriptToken23.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken23.setParameterKind(3);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(PRINT);
                    aSTClassName.setValue(zkmScriptToken22.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken22.setParameterKind(3);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(LIGHT);
                    aSTClassName.setValue(zkmScriptToken21.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken21.setParameterKind(3);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(48);
                    aSTClassName.setValue(zkmScriptToken20.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken20.setParameterKind(3);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(ASCII);
                    this.jj_consume_token(DOT);
                    aSTClassName.setValue(zkmScriptToken19.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken19.setParameterKind(3);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(BRIDGE);
                    aSTClassName.setValue(zkmScriptToken18.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken18.setParameterKind(3);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(SEARCH);
                    aSTClassName.setValue(zkmScriptToken17.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken17.setParameterKind(3);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(DELETE);
                    aSTClassName.setValue(zkmScriptToken16.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken16.setParameterKind(3);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(NORMAL);
                    aSTClassName.setValue(zkmScriptToken15.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken15.setParameterKind(3);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(RANDOM);
                    aSTClassName.setValue(zkmScriptToken14.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken14.setParameterKind(3);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(EXCLUDE);
                    aSTClassName.setValue(zkmScriptToken13.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken13.setParameterKind(3);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(EXECUTE);
                    aSTClassName.setValue(zkmScriptToken12.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken12.setParameterKind(3);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(SCRAMBLE);
                    aSTClassName.setValue(zkmScriptToken11.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken11.setParameterKind(3);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(ENHANCED);
                    aSTClassName.setValue(zkmScriptToken10.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken10.setParameterKind(3);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(SYNTHETIC);
                    aSTClassName.setValue(zkmScriptToken9.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken9.setParameterKind(3);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(CLASSPATH);
                    aSTClassName.setValue(zkmScriptToken8.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken8.setParameterKind(3);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(OBFUSCATE);
                    aSTClassName.setValue(zkmScriptToken7.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken7.setParameterKind(3);
                    break;
                case RANDOMIZE:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(RANDOMIZE);
                    aSTClassName.setValue(zkmScriptToken6.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken6.setParameterKind(3);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(GROUPINGS);
                    aSTClassName.setValue(zkmScriptToken5.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken5.setParameterKind(3);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(ANNOTATION);
                    aSTClassName.setValue(zkmScriptToken4.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken4.setParameterKind(3);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(90);
                    aSTClassName.setValue(zkmScriptToken3.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken3.setParameterKind(3);
                    break;
                case PACKAGE_INFO:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(PACKAGE_INFO);
                    aSTClassName.setValue(zkmScriptToken2.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken2.setParameterKind(3);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(NAME);
                    aSTClassName.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken1.setParameterKind(3);
                    break;
                case WILDCARD_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(WILDCARD_NAME);
                    aSTClassName.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(aSTClassName);
                    bl = false;
                    zkmScriptToken.setParameterKind(3);
                    break;
                default:
                    this.jj_la1[159] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTClassName);
            }
        }
    }

    public final void NewNamesPrefixParameter() throws ZkmScriptParseException {
        ASTNewNamesPrefixParameter aSTNewNamesPrefixParameter = new ASTNewNamesPrefixParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNewNamesPrefixParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NEW_NAMES_PREFIX);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            aSTNewNamesPrefixParameter.setParameterName(zkmScriptToken.image);
            this.jjtree.closeNodeScope(aSTNewNamesPrefixParameter);
            bl = false;
            aSTNewNamesPrefixParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNewNamesPrefixParameter);
            }
        }
    }

    public boolean jj_3R_61() {
        return this.jj_scan_token(TRIM);
    }

    public final ZkmScriptSimpleNode FixedClassesStatement() throws ZkmScriptParseException {
        ASTFixedClassesStatement aSTFixedClassesStatement = new ASTFixedClassesStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFixedClassesStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(FIXED_CLASSES);
            this.currentStatementKind = 98;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[29] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTFixedClassesStatement);
                        bl = false;
                        aSTFixedClassesStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTFixedClassesStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTFixedClassesStatement);
            }
        }
    }

    public boolean jj_3R_62() {
        return this.jj_scan_token(SCRAMBLE);
    }

    public final void LinkPackageName() throws ZkmScriptParseException {
        ASTLinkPackageName aSTLinkPackageName = new ASTLinkPackageName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLinkPackageName);
        try {
            this.LinkLiteralPackageComponent();
            while (this.jj_2_16()) {
                this.jj_consume_token(21);
                this.LinkLiteralPackageComponent();
            }
            switch (this.jj_nt.kind) {
                case 21: {
                    this.jj_consume_token(21);
                    break;
                }
                default: {
                    this.jj_la1[151] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(aSTLinkPackageName);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTLinkPackageName);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_63() {
        return this.jj_scan_token(OPEN) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_64() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public final void ModuleNameComponent() throws ZkmScriptParseException {
        ASTModuleNameComponent aSTModuleNameComponent = new ASTModuleNameComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTModuleNameComponent);

        try {
            switch (this.jj_nt.kind) {
                case STAR:
                    ZkmScriptToken zkmScriptToken30 = this.jj_consume_token(STAR);
                    aSTModuleNameComponent.setValue(zkmScriptToken30.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken30.setParameterKind(1);
                    break;
                case GC:
                    ZkmScriptToken zkmScriptToken29 = this.jj_consume_token(GC);
                    aSTModuleNameComponent.setValue(zkmScriptToken29.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken29.setParameterKind(1);
                    break;
                case 37:
                    ZkmScriptToken zkmScriptToken28 = this.jj_consume_token(37);
                    aSTModuleNameComponent.setValue(zkmScriptToken28.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken28.setParameterKind(1);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken27 = this.jj_consume_token(OPEN);
                    aSTModuleNameComponent.setValue(zkmScriptToken27.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken27.setParameterKind(1);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(ENUM);
                    aSTModuleNameComponent.setValue(zkmScriptToken26.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken26.setParameterKind(1);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(KEEP);
                    aSTModuleNameComponent.setValue(zkmScriptToken25.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken25.setParameterKind(1);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(43);
                    aSTModuleNameComponent.setValue(zkmScriptToken24.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken24.setParameterKind(1);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(TRIM);
                    aSTModuleNameComponent.setValue(zkmScriptToken23.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken23.setParameterKind(1);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(PRINT);
                    aSTModuleNameComponent.setValue(zkmScriptToken22.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken22.setParameterKind(1);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(LIGHT);
                    aSTModuleNameComponent.setValue(zkmScriptToken21.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken21.setParameterKind(1);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(48);
                    aSTModuleNameComponent.setValue(zkmScriptToken20.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken20.setParameterKind(1);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(ASCII);
                    aSTModuleNameComponent.setValue(zkmScriptToken19.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken19.setParameterKind(1);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(BRIDGE);
                    aSTModuleNameComponent.setValue(zkmScriptToken18.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken18.setParameterKind(1);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(SEARCH);
                    aSTModuleNameComponent.setValue(zkmScriptToken17.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken17.setParameterKind(1);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(DELETE);
                    aSTModuleNameComponent.setValue(zkmScriptToken16.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken16.setParameterKind(1);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(NORMAL);
                    aSTModuleNameComponent.setValue(zkmScriptToken15.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken15.setParameterKind(1);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(RANDOM);
                    aSTModuleNameComponent.setValue(zkmScriptToken14.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken14.setParameterKind(1);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(EXCLUDE);
                    aSTModuleNameComponent.setValue(zkmScriptToken13.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken13.setParameterKind(1);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(EXECUTE);
                    aSTModuleNameComponent.setValue(zkmScriptToken12.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken12.setParameterKind(1);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(SCRAMBLE);
                    aSTModuleNameComponent.setValue(zkmScriptToken11.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken11.setParameterKind(1);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(ENHANCED);
                    aSTModuleNameComponent.setValue(zkmScriptToken10.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken10.setParameterKind(1);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(SYNTHETIC);
                    aSTModuleNameComponent.setValue(zkmScriptToken9.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken9.setParameterKind(1);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(CLASSPATH);
                    aSTModuleNameComponent.setValue(zkmScriptToken8.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken8.setParameterKind(1);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(OBFUSCATE);
                    aSTModuleNameComponent.setValue(zkmScriptToken7.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken7.setParameterKind(1);
                    break;
                case RANDOMIZE:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(RANDOMIZE);
                    aSTModuleNameComponent.setValue(zkmScriptToken6.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken6.setParameterKind(1);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(GROUPINGS);
                    aSTModuleNameComponent.setValue(zkmScriptToken5.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken5.setParameterKind(1);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(ANNOTATION);
                    aSTModuleNameComponent.setValue(zkmScriptToken4.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken4.setParameterKind(1);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(90);
                    aSTModuleNameComponent.setValue(zkmScriptToken3.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken3.setParameterKind(1);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(NAME);
                    aSTModuleNameComponent.setValue(zkmScriptToken2.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken2.setParameterKind(1);
                    break;
                case WILDCARD_NAME:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(WILDCARD_NAME);
                    aSTModuleNameComponent.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken1.setParameterKind(1);
                    break;
                case MODULE_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(MODULE_NAME);
                    aSTModuleNameComponent.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(aSTModuleNameComponent);
                    bl = false;
                    zkmScriptToken.setParameterKind(1);
                    break;
                default:
                    this.jj_la1[149] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTModuleNameComponent);
            }
        }
    }

    public boolean jj_3R_65() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public boolean jj_3R_66() {
        return this.jj_scan_token(NORMAL);
    }

    public boolean jj_3R_67() {
        return this.jj_scan_token(LIGHT);
    }

    public final void MethodParametersParameterType() throws ZkmScriptParseException {
        ASTMethodParametersParameterType aSTMethodParametersParameterType = new ASTMethodParametersParameterType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParametersParameterType);

        try {
            switch (this.jj_nt.kind) {
                case KEEP:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(KEEP);
                    this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
                    bl = false;
                    aSTMethodParametersParameterType.setValue(zkmScriptToken5.image);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(DELETE);
                    this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
                    bl = false;
                    aSTMethodParametersParameterType.setValue(zkmScriptToken4.image);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(OBFUSCATE);
                    this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
                    bl = false;
                    aSTMethodParametersParameterType.setValue(zkmScriptToken3.image);
                    break;
                case KEEP_VISIBLE:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(KEEP_VISIBLE);
                    this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
                    bl = false;
                    aSTMethodParametersParameterType.setValue(zkmScriptToken2.image);
                    break;
                case KEEP_IF_NOT_OBFUSCATED:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(KEEP_IF_NOT_OBFUSCATED);
                    this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
                    bl = false;
                    aSTMethodParametersParameterType.setValue(zkmScriptToken1.image);
                    break;
                case KEEP_VISIBLE_IF_NOT_OBFUSCATED:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(KEEP_VISIBLE_IF_NOT_OBFUSCATED);
                    this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
                    bl = false;
                    aSTMethodParametersParameterType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[133] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParametersParameterType);
            }
        }
    }

    




    public final void RenameFilterParameter() throws ZkmScriptParseException {
        ASTRenameFilterParameter aSTRenameFilterParameter = new ASTRenameFilterParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRenameFilterParameter);
        try {
            block89:
            {
                if (this.jj_2_15()) {
                    this.ComplexModuleSpecifier();
                } else if (this.jj_2_9()) {
                    this.ComplexPackageSpecifier();
                } else if (this.jj_2_8()) {
                    this.StandAloneAnnotation();
                } else if (this.jj_2_19()) {
                    this.ReferencingAnnotation();
                    this.jj_consume_token(28);
                    this.ReferencingAnnotationComponentName();
                    this.jj_consume_token(15);
                    if (this.jj_2_26()) {
                        this.ComplexPackageSpecifier();
                    }
                    this.ComplexClassSpecifier();
                    this.jj_consume_token(29);
                    switch (this.jj_nt.kind) {
                        case 66: {
                            this.ExtendsClause();
                            break;
                        }
                        default: {
                            this.jj_la1[79] = this.jj_gen;
                        }
                    }
                    switch (this.jj_nt.kind) {
                        case 91: {
                            this.ImplementsClause();
                            break;
                        }
                        default: {
                            this.jj_la1[80] = this.jj_gen;
                        }
                    }
                    block8:
                    switch (this.jj_nt.kind) {
                        case 23:
                        case 25:
                        case 27:
                        case 28:
                        case 37:
                        case 39:
                        case 40:
                        case 42:
                        case 43:
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                        case 50:
                        case 51:
                        case 52:
                        case 54:
                        case 56:
                        case 57:
                        case 58:
                        case 59:
                        case 60:
                        case 61:
                        case 63:
                        case 64:
                        case 67:
                        case 68:
                        case 69:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 77:
                        case 80:
                        case 81:
                        case 82:
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 90:
                        case 99:
                        case 213:
                        case 214: {
                            if (this.jj_2_6()) {
                                this.MemberComplexAnnotationSpecifier();
                            }
                            block64:
                            while (this.jj_2_22()) {
                                switch (this.jj_nt.kind) {
                                    case 40:
                                    case 50:
                                    case 52:
                                    case 57:
                                    case 58:
                                    case 59:
                                    case 67:
                                    case 68:
                                    case 72:
                                    case 75:
                                    case 77:
                                    case 82:
                                    case 84:
                                    case 99: {
                                        this.MemberModifier();
                                        continue block64;
                                    }
                                    case 25: {
                                        this.NegatedMemberModifier();
                                        continue block64;
                                    }
                                }
                                this.jj_la1[81] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ZkmScriptParseException();
                            }
                            if (this.jj_2_4()) {
                                this.MethodSignature();
                                break;
                            }
                            switch (this.jj_nt.kind) {
                                case 23:
                                case 25:
                                case 28:
                                case 37:
                                case 39:
                                case 40:
                                case 42:
                                case 43:
                                case 45:
                                case 46:
                                case 47:
                                case 48:
                                case 51:
                                case 52:
                                case 56:
                                case 60:
                                case 61:
                                case 63:
                                case 64:
                                case 69:
                                case 71:
                                case 74:
                                case 77:
                                case 80:
                                case 81:
                                case 85:
                                case 86:
                                case 87:
                                case 90:
                                case 213:
                                case 214: {
                                    this.FieldSignature();
                                    break block8;
                                }
                            }
                            this.jj_la1[82] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ZkmScriptParseException();
                        }
                        default: {
                            this.jj_la1[83] = this.jj_gen;
                            break;
                        }
                    }
                } else {
                    block18:
                    switch (this.jj_nt.kind) {
                        case 23:
                        case 25:
                        case 27:
                        case 28:
                        case 36:
                        case 37:
                        case 39:
                        case 40:
                        case 42:
                        case 43:
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                        case 50:
                        case 51:
                        case 52:
                        case 56:
                        case 57:
                        case 60:
                        case 61:
                        case 63:
                        case 64:
                        case 67:
                        case 69:
                        case 71:
                        case 72:
                        case 74:
                        case 77:
                        case 80:
                        case 81:
                        case 83:
                        case 85:
                        case 86:
                        case 87:
                        case 90:
                        case 97:
                        case 210:
                        case 213:
                        case 214:
                        case 216: {
                            if (this.jj_2_1()) {
                                this.ClassComplexAnnotationSpecifier();
                            }
                            if (this.jj_2_2()) {
                                switch (this.jj_nt.kind) {
                                    case 210: {
                                        this.JarQualifier();
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[84] = this.jj_gen;
                                    }
                                }
                                switch (this.jj_nt.kind) {
                                    case 23:
                                    case 25:
                                    case 28:
                                    case 36:
                                    case 37:
                                    case 39:
                                    case 40:
                                    case 42:
                                    case 43:
                                    case 45:
                                    case 46:
                                    case 47:
                                    case 48:
                                    case 51:
                                    case 52:
                                    case 56:
                                    case 60:
                                    case 61:
                                    case 63:
                                    case 64:
                                    case 67:
                                    case 69:
                                    case 71:
                                    case 74:
                                    case 77:
                                    case 80:
                                    case 81:
                                    case 85:
                                    case 86:
                                    case 87:
                                    case 90:
                                    case 213:
                                    case 214: {
                                        this.ComplexPackageSpecifier();
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[85] = this.jj_gen;
                                    }
                                }
                                this.LinkClassName();
                                switch (this.jj_nt.kind) {
                                    case 66: {
                                        this.ExtendsClause();
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[86] = this.jj_gen;
                                    }
                                }
                                switch (this.jj_nt.kind) {
                                    case 91: {
                                        this.ImplementsClause();
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[87] = this.jj_gen;
                                    }
                                }
                                block33:
                                switch (this.jj_nt.kind) {
                                    case 56: {
                                        this.jj_consume_token(56);
                                        this.LinkPackageName();
                                        ZkmScriptToken zkmScriptToken = this.jj_nt;
                                        while (true) {
                                            switch (zkmScriptToken.kind) {
                                                case 20: {
                                                    break;
                                                }
                                                default: {
                                                    this.jj_la1[88] = this.jj_gen;
                                                    break block33;
                                                }
                                            }
                                            this.jj_consume_token(20);
                                            this.LinkPackageName();
                                            zkmScriptToken = this.jj_nt;
                                        }
                                    }
                                    default: {
                                        this.jj_la1[89] = this.jj_gen;
                                        break;
                                    }
                                }
                                break block89;
                            } else {
                                switch (this.jj_nt.kind) {
                                    case 23:
                                    case 25:
                                    case 28:
                                    case 36:
                                    case 37:
                                    case 39:
                                    case 40:
                                    case 42:
                                    case 43:
                                    case 45:
                                    case 46:
                                    case 47:
                                    case 48:
                                    case 50:
                                    case 51:
                                    case 52:
                                    case 56:
                                    case 57:
                                    case 60:
                                    case 61:
                                    case 63:
                                    case 64:
                                    case 67:
                                    case 69:
                                    case 71:
                                    case 72:
                                    case 74:
                                    case 77:
                                    case 80:
                                    case 81:
                                    case 83:
                                    case 85:
                                    case 86:
                                    case 87:
                                    case 90:
                                    case 97:
                                    case 210:
                                    case 213:
                                    case 214: {
                                        break block18;
                                    }
                                }
                                this.jj_la1[96] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ZkmScriptParseException();
                            }
                        }
                        default: {
                            this.jj_la1[97] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ZkmScriptParseException();
                        }
                    }
                    block66:
                    while (this.jj_2_25()) {
                        switch (this.jj_nt.kind) {
                            case 40:
                            case 50:
                            case 57:
                            case 67:
                            case 72:
                            case 77:
                            case 83:
                            case 87: {
                                this.ClassModifier();
                                continue block66;
                            }
                            case 25: {
                                this.NegatedClassModifier();
                                continue block66;
                            }
                        }
                        this.jj_la1[90] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ZkmScriptParseException();
                    }
                    if (this.jj_2_11()) {
                        this.JarQualifier();
                    }
                    if (this.jj_2_3()) {
                        this.ComplexPackageSpecifier();
                    }
                    this.ComplexClassSpecifier();
                    if (this.jj_2_28()) {
                        this.ContainingClause();
                    }
                    switch (this.jj_nt.kind) {
                        case 66: {
                            this.ExtendsClause();
                            break;
                        }
                        default: {
                            this.jj_la1[91] = this.jj_gen;
                        }
                    }
                    switch (this.jj_nt.kind) {
                        case 91: {
                            this.ImplementsClause();
                            break;
                        }
                        default: {
                            this.jj_la1[92] = this.jj_gen;
                        }
                    }
                    block52:
                    switch (this.jj_nt.kind) {
                        case 23:
                        case 25:
                        case 27:
                        case 28:
                        case 37:
                        case 39:
                        case 40:
                        case 42:
                        case 43:
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 56:
                        case 57:
                        case 58:
                        case 59:
                        case 60:
                        case 61:
                        case 63:
                        case 64:
                        case 67:
                        case 68:
                        case 69:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 77:
                        case 80:
                        case 81:
                        case 82:
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 90:
                        case 99:
                        case 213:
                        case 214:
                        case 217: {
                            if (this.jj_2_27()) {
                                this.MemberComplexAnnotationSpecifier();
                            }
                            block67:
                            while (this.jj_2_10()) {
                                switch (this.jj_nt.kind) {
                                    case 40:
                                    case 50:
                                    case 52:
                                    case 57:
                                    case 58:
                                    case 59:
                                    case 67:
                                    case 68:
                                    case 72:
                                    case 75:
                                    case 77:
                                    case 82:
                                    case 84:
                                    case 99: {
                                        this.MemberModifier();
                                        continue block67;
                                    }
                                    case 25: {
                                        this.NegatedMemberModifier();
                                        continue block67;
                                    }
                                }
                                this.jj_la1[93] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ZkmScriptParseException();
                            }
                            if (this.jj_2_7()) {
                                this.MethodSignature();
                                break;
                            }
                            if (this.jj_2_18()) {
                                this.LinkMethodSignature();
                                break;
                            }
                            switch (this.jj_nt.kind) {
                                case 23:
                                case 25:
                                case 28:
                                case 37:
                                case 39:
                                case 40:
                                case 42:
                                case 43:
                                case 45:
                                case 46:
                                case 47:
                                case 48:
                                case 51:
                                case 52:
                                case 56:
                                case 60:
                                case 61:
                                case 63:
                                case 64:
                                case 69:
                                case 71:
                                case 74:
                                case 77:
                                case 80:
                                case 81:
                                case 85:
                                case 86:
                                case 87:
                                case 90:
                                case 213:
                                case 214: {
                                    this.FieldSignature();
                                    break block52;
                                }
                            }
                            this.jj_la1[94] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ZkmScriptParseException();
                        }
                        default: {
                            this.jj_la1[95] = this.jj_gen;
                            break;
                        }
                    }
                }
            }
            this.jjtree.closeNodeScope(aSTRenameFilterParameter);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTRenameFilterParameter);
                throw throwable2;
            }
        }
    }

    public final void ResetExistingSerializedClassesStatement() throws ZkmScriptParseException {
        ASTResetExistingSerializedClassesStatement aSTResetExistingSerializedClassesStatement = new ASTResetExistingSerializedClassesStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetExistingSerializedClassesStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_EXISTING_SERIALIZED_CLASSES);
            this.currentStatementKind = 195;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetExistingSerializedClassesStatement);
            bl = false;
            aSTResetExistingSerializedClassesStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetExistingSerializedClassesStatement);
            }
        }
    }

    public final ZkmScriptSimpleNode MethodParameterObfuscationExcludeStatement() throws ZkmScriptParseException {
        ASTMethodParameterObfuscationExcludeStatement aSTMethodParameterObfuscationExcludeStatement = new ASTMethodParameterObfuscationExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterObfuscationExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_OBFUSCATION_EXCLUDE);
            this.currentStatementKind = 200;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[51] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTMethodParameterObfuscationExcludeStatement);
                        bl = false;
                        aSTMethodParameterObfuscationExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTMethodParameterObfuscationExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterObfuscationExcludeStatement);
            }
        }
    }

    public boolean jj_3_4() {
        return this.jj_scan_token(OR) ? true : this.jj_3R_100();
    }

    public boolean jj_3R_68() {
        return this.jj_scan_token(90) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_69() {
        return this.jj_scan_token(OPEN);
    }

    public boolean jj_3_5() {
        return this.jj_scan_token(AND_AND) ? true : this.jj_3R_32();
    }

    public final ZkmScriptSimpleNode TrimExcludeStatement() throws ZkmScriptParseException {
        ASTTrimExcludeStatement aSTTrimExcludeStatement = new ASTTrimExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTTrimExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(TRIM_EXCLUDE);
            this.currentStatementKind = 94;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[32] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTTrimExcludeStatement);
                        bl = false;
                        aSTTrimExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTTrimExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTTrimExcludeStatement);
            }
        }
    }

    public boolean jj_3R_70() {
        return this.jj_scan_token(43);
    }

    public boolean jj_2_9() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_89();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(15, Integer.MAX_VALUE);
        }

        return true;
    }

    public final void EnhancedIncrementalParameter() throws ZkmScriptParseException {
        ASTEnhancedIncrementalParameter aSTEnhancedIncrementalParameter = new ASTEnhancedIncrementalParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTEnhancedIncrementalParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ALL_CLASSES_OPENED);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTEnhancedIncrementalParameter);
            bl = false;
            aSTEnhancedIncrementalParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTEnhancedIncrementalParameter);
            }
        }
    }

    public boolean jj_3R_71() {
        return this.jj_scan_token(GROUPINGS);
    }

    public final void PackageName() throws ZkmScriptParseException {
        ASTPackageName aSTPackageName = new ASTPackageName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPackageName);
        try {
            do {
                this.LiteralPackageComponent();
            } while (this.jj_2_5());
            this.jjtree.closeNodeScope(aSTPackageName);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTPackageName);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_72() {
        return this.jj_scan_token(SCRAMBLE);
    }

    public boolean jj_3R_73() {
        return this.jj_scan_token(SEARCH);
    }

    public boolean jj_3R_74() {
        return this.jj_scan_token(EXECUTE);
    }

    public boolean jj_3R_75() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_134()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_114()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_10()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_63()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_218()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_122()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_180()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_112()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_228()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_169()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_153()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_260()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_5()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_13()) {
                                                                this.jj_scanpos = zkmScriptToken;
                                                                if (this.jj_3R_170()) {
                                                                    this.jj_scanpos = zkmScriptToken;
                                                                    if (this.jj_3R_268()) {
                                                                        this.jj_scanpos = zkmScriptToken;
                                                                        if (this.jj_3R_231()) {
                                                                            this.jj_scanpos = zkmScriptToken;
                                                                            if (this.jj_3R_120()) {
                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                if (this.jj_3R_179()) {
                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                    if (this.jj_3R_126()) {
                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                        if (this.jj_3R_144()) {
                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                            if (this.jj_3R_24()) {
                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                if (this.jj_3R_68()) {
                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                    if (this.jj_3R_274()) {
                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                        if (this.jj_3R_258()) {
                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                            if (this.jj_3R_246()) {
                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                if (this.jj_3R_254()) {
                                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                                    if (this.jj_3R_146()) {
                                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                                        if (this.jj_3R_35()) {
                                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                                            if (this.jj_3R_54()) {
                                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                                if (this.jj_3R_82()) {
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
        }

        return false;
    }

    public boolean jj_3R_76() {
        return this.jj_scan_token(BANG);
    }

    public final void LineNumbersParameter() throws ZkmScriptParseException {
        ASTLineNumbersParameter aSTLineNumbersParameter = new ASTLineNumbersParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLineNumbersParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LINE_NUMBERS);
            this.jj_consume_token(ASSIGN);
            this.LineNumberParameterType();
            this.jjtree.closeNodeScope(aSTLineNumbersParameter);
            bl = false;
            aSTLineNumbersParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLineNumbersParameter);
            }
        }
    }

    public final ZkmScriptSimpleNode RemoveMethodCallsExcludeStatement() throws ZkmScriptParseException {
        ASTRemoveMethodCallsExcludeStatement aSTRemoveMethodCallsExcludeStatement = new ASTRemoveMethodCallsExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRemoveMethodCallsExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(REMOVE_METHOD_CALLS_EXCLUDE);
            this.currentStatementKind = 166;
            switch (this.jj_nt.kind) {
                case CONTAINED_IN:
                    this.ContainedInClause();
                    break;
                default:
                    this.jj_la1[45] = this.jj_gen;
            }

            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        switch (this.jj_nt.kind) {
                            case CONTAINED_IN:
                                this.ContainedInClause();
                                break;
                            default:
                                this.jj_la1[47] = this.jj_gen;
                        }

                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[46] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTRemoveMethodCallsExcludeStatement);
                        bl = false;
                        aSTRemoveMethodCallsExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTRemoveMethodCallsExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTRemoveMethodCallsExcludeStatement);
            }
        }
    }

    public final void ChangeLogInParameter() throws ZkmScriptParseException {
        ASTChangeLogInParameter aSTChangeLogInParameter = new ASTChangeLogInParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTChangeLogInParameter);

        try {
            switch (this.jj_nt.kind) {
                case CHANGE_LOG_FILE_IN:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(CHANGE_LOG_FILE_IN);
                    this.jj_consume_token(ASSIGN);
                    this.StringLiteral();
                    ZkmScriptToken zkmScriptToken2 = this.jj_nt;

                    while (true) {
                        switch (zkmScriptToken2.kind) {
                            case COMMA:
                                this.jj_consume_token(COMMA);
                                this.StringLiteral();
                                zkmScriptToken2 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[100] = this.jj_gen;
                                this.jjtree.closeNodeScope(aSTChangeLogInParameter);
                                bl = false;
                                aSTChangeLogInParameter.setParameterName(zkmScriptToken1.image);
                                return;
                        }
                    }
                case LOOSE_CHANGE_LOG_FILE_IN:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(LOOSE_CHANGE_LOG_FILE_IN);
                    this.jj_consume_token(ASSIGN);
                    this.StringLiteral();
                    ZkmScriptToken zkmScriptToken3 = this.jj_nt;

                    while (true) {
                        switch (zkmScriptToken3.kind) {
                            case COMMA:
                                this.jj_consume_token(COMMA);
                                this.StringLiteral();
                                zkmScriptToken3 = this.jj_nt;
                                break;
                            default:
                                this.jj_la1[101] = this.jj_gen;
                                this.jjtree.closeNodeScope(aSTChangeLogInParameter);
                                bl = false;
                                aSTChangeLogInParameter.setParameterName(zkmScriptToken.image);
                                aSTChangeLogInParameter.setLoose();
                                return;
                        }
                    }
                default:
                    this.jj_la1[102] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTChangeLogInParameter);
            }
        }
    }

    public boolean jj_3_6() {
        return this.jj_3R_53() ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_77() {
        return this.jj_scan_token(CLASSPATH);
    }

    public boolean jj_3R_78() {
        return this.jj_scan_token(NORMAL);
    }

    public boolean jj_3R_79() {
        return this.jj_scan_token(ENUM);
    }

    public final void NewClassNameFileParameter() throws ZkmScriptParseException {
        ASTNewClassNameFileParameter aSTNewClassNameFileParameter = new ASTNewClassNameFileParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNewClassNameFileParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NEW_CLASS_NAME_FILE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTNewClassNameFileParameter);
            bl = false;
            aSTNewClassNameFileParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNewClassNameFileParameter);
            }
        }
    }

    public boolean jj_3R_80() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_14()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_96()) {
                return true;
            }
        }

        zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_165()) {
            this.jj_scanpos = zkmScriptToken;
        }

        return false;
    }

    




    public final void BracketedPackageSpecifier() throws ZkmScriptParseException {
        ASTBracketedPackageSpecifier aSTBracketedPackageSpecifier = new ASTBracketedPackageSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedPackageSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedPackageSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[146] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndPackageSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[147] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndPackageSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedPackageSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedPackageSpecifier);
                throw throwable2;
            }
        }
    }

    public final void OpenNestedArchivesParameter() throws ZkmScriptParseException {
        ASTOpenNestedArchivesParameter aSTOpenNestedArchivesParameter = new ASTOpenNestedArchivesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTOpenNestedArchivesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OPEN_NESTED_ARCHIVES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTOpenNestedArchivesParameter);
            bl = false;
            aSTOpenNestedArchivesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTOpenNestedArchivesParameter);
            }
        }
    }

    public final void ExtendsClause() throws ZkmScriptParseException {
        ZkmScriptASTExtendsClause zkmScriptASTExtendsClause = new ZkmScriptASTExtendsClause();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTExtendsClause);
        try {
            this.jj_consume_token(66);
            switch (this.jj_nt.kind) {
                case 25:
                case 27:
                case 28: {
                    this.ComplexAnnotationSpecifier();
                    break;
                }
                default: {
                    this.jj_la1[116] = this.jj_gen;
                }
            }
            this.QualifiedClassName();
            this.jjtree.closeNodeScope(zkmScriptASTExtendsClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(zkmScriptASTExtendsClause);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_81() {
        return this.jj_3R_249();
    }

    public final void DeleteExceptionAttributesParameter() throws ZkmScriptParseException {
        ASTDeleteExceptionAttributesParameter aSTDeleteExceptionAttributesParameter = new ASTDeleteExceptionAttributesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteExceptionAttributesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_EXCEPTION_ATTRIBUTES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteExceptionAttributesParameter);
            bl = false;
            aSTDeleteExceptionAttributesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteExceptionAttributesParameter);
            }
        }
    }

    public boolean jj_3R_82() {
        return this.jj_scan_token(ASCII) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_83() {
        return this.jj_scan_token(NAME);
    }

    public boolean jj_3R_84() {
        return this.jj_scan_token(MODULE_NAME);
    }

    public final void ComplexAnnotationSpecifier() throws ZkmScriptParseException {
        ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier = new ASTComplexAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexAnnotationSpecifier);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 28: {
                    this.BracketedAnnotationSpecifier();
                    break;
                }
                case 27: {
                    this.Annotation();
                    break;
                }
                default: {
                    this.jj_la1[134] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTComplexAnnotationSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexAnnotationSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_85() {
        return this.jj_scan_token(NORMAL);
    }

    public boolean jj_3R_86() {
        return this.jj_scan_token(LINK);
    }

    public boolean jj_3R_87() {
        return this.jj_scan_token(SEARCH);
    }

    public final ZkmScriptSimpleNode UnexcludeStatement() throws ZkmScriptParseException {
        ASTUnexcludeStatement aSTUnexcludeStatement = new ASTUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(UNEXCLUDE);
            this.currentStatementKind = 79;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[17] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTUnexcludeStatement);
                        bl = false;
                        aSTUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTUnexcludeStatement);
            }
        }
    }

    public boolean jj_3R_88() {
        return this.jj_scan_token(ENHANCED);
    }

    public ZkmScriptParser(Reader reader1) {
        this.jj_input_stream = new ZkmScriptSimpleCharStream(reader1);
        this.token_source = new ZkmScriptTokenManager(this.jj_input_stream);
        this.token = new ZkmScriptToken();
        this.token.next = this.jj_nt = this.token_source.getNextToken();
        this.jj_gen = 0;
        int ba = 0;
        int bc = 0;

        for (short bd = 187; bc < bd; bd = 187) {
            this.jj_la1[ba] = -1;
            bc = ++ba;
        }

        for (int i = 0; i < this.jj_2_rtns.length; i++) {
            this.jj_2_rtns[i] = new ZkmScriptParserJJCalls();
        }
    }

    public boolean jj_3R_89() {
        if (this.jj_3R_128()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(AND)) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_scan_token(SEMICOLON)) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_scan_token(RBRACE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean jj_3R_90() {
        return this.jj_scan_token(KEEP);
    }

    public boolean jj_3R_91() {
        return this.jj_scan_token(BRIDGE);
    }

    public boolean jj_3R_92() {
        return this.jj_scan_token(PACKAGE);
    }

    public final void ObfuscateReferencesParameter() throws ZkmScriptParseException {
        ASTObfuscateReferencesParameter aSTObfuscateReferencesParameter = new ASTObfuscateReferencesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferencesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_REFERENCES);
            this.jj_consume_token(ASSIGN);
            this.ObfuscateReferencesType();
            this.jjtree.closeNodeScope(aSTObfuscateReferencesParameter);
            bl = false;
            aSTObfuscateReferencesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferencesParameter);
            }
        }
    }

    public final void Boolean() throws ZkmScriptParseException {
        ASTBoolean aSTBoolean = new ASTBoolean();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBoolean);

        try {
            switch (this.jj_nt.kind) {
                case 41:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(41);
                    this.jjtree.closeNodeScope(aSTBoolean);
                    bl = false;
                    aSTBoolean.setValue(zkmScriptToken1.image);
                    break;
                case FALSE:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(FALSE);
                    this.jjtree.closeNodeScope(aSTBoolean);
                    bl = false;
                    aSTBoolean.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[184] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTBoolean);
            }
        }
    }

    public boolean jj_3R_93() {
        if (this.jj_3R_32()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_5());

        this.jj_scanpos = zkmScriptToken;
        return false;
    }

    public boolean jj_3R_94() {
        return this.jj_scan_token(OBFUSCATE);
    }

    public final ZkmScriptSimpleNode AccessedByReflectionExcludeStatement() throws ZkmScriptParseException {
        ASTAccessedByReflectionExcludeStatement aSTAccessedByReflectionExcludeStatement = new ASTAccessedByReflectionExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAccessedByReflectionExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ACCESSED_BY_REFLECTION_EXCLUDE);
            this.currentStatementKind = 182;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[41] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTAccessedByReflectionExcludeStatement);
                        bl = false;
                        aSTAccessedByReflectionExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTAccessedByReflectionExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAccessedByReflectionExcludeStatement);
            }
        }
    }

    public boolean jj_3R_95() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public boolean jj_3R_96() {
        return this.jj_3R_239();
    }

    public boolean jj_3R_97() {
        return this.jj_scan_token(SYNCHRONIZED);
    }

    public final void EncryptIntegerConstantsParameter() throws ZkmScriptParseException {
        ASTEncryptIntegerConstantsParameter aSTEncryptIntegerConstantsParameter = new ASTEncryptIntegerConstantsParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTEncryptIntegerConstantsParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ENCRYPT_INTEGER_CONSTANTS);
            this.jj_consume_token(ASSIGN);
            this.IntegerEncryptionType();
            this.jjtree.closeNodeScope(aSTEncryptIntegerConstantsParameter);
            bl = false;
            aSTEncryptIntegerConstantsParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTEncryptIntegerConstantsParameter);
            }
        }
    }

    public boolean jj_3R_98() {
        return this.jj_scan_token(SEARCH);
    }

    public final void ExceptionObfuscationType() throws ZkmScriptParseException {
        ASTExceptionObfuscationType aSTExceptionObfuscationType = new ASTExceptionObfuscationType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExceptionObfuscationType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTExceptionObfuscationType);
                    bl = false;
                    aSTExceptionObfuscationType.setValue(zkmScriptToken2.image);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(LIGHT);
                    this.jjtree.closeNodeScope(aSTExceptionObfuscationType);
                    bl = false;
                    aSTExceptionObfuscationType.setValue(zkmScriptToken1.image);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(48);
                    this.jjtree.closeNodeScope(aSTExceptionObfuscationType);
                    bl = false;
                    aSTExceptionObfuscationType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[121] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExceptionObfuscationType);
            }
        }
    }

    public final void NewMethodNameFileParameter() throws ZkmScriptParseException {
        ASTNewMethodNameFileParameter aSTNewMethodNameFileParameter = new ASTNewMethodNameFileParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNewMethodNameFileParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NEW_METHOD_NAME_FILE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTNewMethodNameFileParameter);
            bl = false;
            aSTNewMethodNameFileParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNewMethodNameFileParameter);
            }
        }
    }

    public boolean jj_3R_99() {
        return this.jj_scan_token(STATIC);
    }

    public final void LinkLiteralPackageComponent() throws ZkmScriptParseException {
        ASTLinkLiteralPackageComponent aSTLinkLiteralPackageComponent = new ASTLinkLiteralPackageComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLinkLiteralPackageComponent);

        try {
            switch (this.jj_nt.kind) {
                case STAR:
                    ZkmScriptToken zkmScriptToken27 = this.jj_consume_token(STAR);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken27.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken27.setParameterKind(7);
                    break;
                case 37:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(37);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken26.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken26.setParameterKind(7);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(OPEN);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken25.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken25.setParameterKind(7);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(ENUM);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken24.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken24.setParameterKind(7);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(KEEP);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken23.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken23.setParameterKind(7);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(43);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken22.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken22.setParameterKind(7);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(TRIM);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken21.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken21.setParameterKind(7);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(PRINT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken20.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken20.setParameterKind(7);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(LIGHT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken19.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken19.setParameterKind(7);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(48);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken18.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken18.setParameterKind(7);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(ASCII);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken17.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken17.setParameterKind(7);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(BRIDGE);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken16.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken16.setParameterKind(7);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(SEARCH);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken15.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken15.setParameterKind(7);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(DELETE);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken14.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken14.setParameterKind(7);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(NORMAL);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken13.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken13.setParameterKind(7);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(RANDOM);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken12.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken12.setParameterKind(7);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(EXCLUDE);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken11.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken11.setParameterKind(7);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(EXECUTE);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken10.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken10.setParameterKind(7);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(SCRAMBLE);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken9.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken9.setParameterKind(7);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(ENHANCED);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken8.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken8.setParameterKind(7);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(SYNTHETIC);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken7.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken7.setParameterKind(7);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(CLASSPATH);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken6.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken6.setParameterKind(7);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(OBFUSCATE);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken5.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken5.setParameterKind(7);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(GROUPINGS);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken4.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken4.setParameterKind(7);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(ANNOTATION);
                    this.jj_consume_token(DOT);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken3.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken3.setParameterKind(7);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(90);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken2.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken2.setParameterKind(7);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(NAME);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken1.setParameterKind(7);
                    break;
                case WILDCARD_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(WILDCARD_NAME);
                    aSTLinkLiteralPackageComponent.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken.setParameterKind(7);
                    break;
                default:
                    this.jj_la1[152] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLinkLiteralPackageComponent);
            }
        }
    }

    public final void LinkMethodName() throws ZkmScriptParseException {
        ASTLinkMethodName aSTLinkMethodName = new ASTLinkMethodName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLinkMethodName);

        try {
            switch (this.jj_nt.kind) {
                case LINK:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(LINK);
                    aSTLinkMethodName.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(aSTLinkMethodName);
                    bl = false;
                    zkmScriptToken1.setParameterKind(8);
                    break;
                case LINKED_METHOD_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(LINKED_METHOD_NAME);
                    aSTLinkMethodName.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(aSTLinkMethodName);
                    bl = false;
                    zkmScriptToken.setParameterKind(8);
                    break;
                default:
                    this.jj_la1[172] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLinkMethodName);
            }
        }
    }

    public final void AutoReflectionParameter() throws ZkmScriptParseException {
        ASTAutoReflectionParameter aSTAutoReflectionParameter = new ASTAutoReflectionParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAutoReflectionParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(AUTO_REFLECTION_HANDLING);
            this.jj_consume_token(ASSIGN);
            this.AutoReflectionType();
            this.jjtree.closeNodeScope(aSTAutoReflectionParameter);
            bl = false;
            aSTAutoReflectionParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAutoReflectionParameter);
            }
        }
    }

    public final void MemberSpecifierComplexAnnotationSpecifier() throws ZkmScriptParseException {
        ASTMemberSpecifierComplexAnnotationSpecifier aSTMemberSpecifierComplexAnnotationSpecifier = new ASTMemberSpecifierComplexAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMemberSpecifierComplexAnnotationSpecifier);
        try {
            this.ComplexAnnotationSpecifier();
            this.jjtree.closeNodeScope(aSTMemberSpecifierComplexAnnotationSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTMemberSpecifierComplexAnnotationSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_100() {
        if (this.jj_3R_222()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_12());

        this.jj_scanpos = zkmScriptToken;
        return false;
    }

    public final void ResetStringEncryptionExclusionsStatement() throws ZkmScriptParseException {
        ASTResetStringEncryptionExclusionsStatement aSTResetStringEncryptionExclusionsStatement = new ASTResetStringEncryptionExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetStringEncryptionExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_STRING_ENCRYPTION_EXCLUSIONS);
            this.currentStatementKind = 197;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetStringEncryptionExclusionsStatement);
            bl = false;
            aSTResetStringEncryptionExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetStringEncryptionExclusionsStatement);
            }
        }
    }

    public boolean jj_3R_101() {
        return this.jj_scan_token(OPEN);
    }

    public boolean jj_3R_102() {
        return this.jj_scan_token(GROUPINGS);
    }

    public boolean jj_3R_103() {
        return this.jj_scan_token(48);
    }

    public boolean jj_3R_104() {
        return this.jj_3R_75();
    }

    public final void JarQualifier() throws ZkmScriptParseException {
        ASTJarQualifier aSTJarQualifier = new ASTJarQualifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTJarQualifier);
        try {
            this.StringLiteral();
            this.jj_consume_token(25);
            this.jjtree.closeNodeScope(aSTJarQualifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTJarQualifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_105() {
        return this.jj_scan_token(37);
    }

    public boolean jj_3R_106() {
        return this.jj_scan_token(LIGHT);
    }

    public boolean jj_3R_107() {
        return this.jj_scan_token(SCRAMBLE);
    }

    public final void ResetIgnoreMissingReferencesStatement() throws ZkmScriptParseException {
        ASTResetIgnoreMissingReferencesStatement aSTResetIgnoreMissingReferencesStatement = new ASTResetIgnoreMissingReferencesStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetIgnoreMissingReferencesStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_IGNORE_MISSING_REFERENCES);
            this.currentStatementKind = 187;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetIgnoreMissingReferencesStatement);
            bl = false;
            aSTResetIgnoreMissingReferencesStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetIgnoreMissingReferencesStatement);
            }
        }
    }

    public final void MethodName() throws ZkmScriptParseException {
        ZkmScriptASTMethodName zkmScriptASTMethodName = new ZkmScriptASTMethodName();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTMethodName);

        try {
            switch (this.jj_nt.kind) {
                case STAR:
                    ZkmScriptToken zkmScriptToken30 = this.jj_consume_token(STAR);
                    zkmScriptASTMethodName.setValue(zkmScriptToken30.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken30.setParameterKind(5);
                    break;
                case 37:
                    ZkmScriptToken zkmScriptToken29 = this.jj_consume_token(37);
                    zkmScriptASTMethodName.setValue(zkmScriptToken29.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken29.setParameterKind(5);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken28 = this.jj_consume_token(OPEN);
                    zkmScriptASTMethodName.setValue(zkmScriptToken28.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken28.setParameterKind(5);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken27 = this.jj_consume_token(ENUM);
                    zkmScriptASTMethodName.setValue(zkmScriptToken27.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken27.setParameterKind(5);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(KEEP);
                    zkmScriptASTMethodName.setValue(zkmScriptToken26.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken26.setParameterKind(5);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(43);
                    zkmScriptASTMethodName.setValue(zkmScriptToken25.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken25.setParameterKind(5);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(TRIM);
                    zkmScriptASTMethodName.setValue(zkmScriptToken24.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken24.setParameterKind(5);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(PRINT);
                    zkmScriptASTMethodName.setValue(zkmScriptToken23.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken23.setParameterKind(5);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(LIGHT);
                    zkmScriptASTMethodName.setValue(zkmScriptToken22.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken22.setParameterKind(5);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(48);
                    zkmScriptASTMethodName.setValue(zkmScriptToken21.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken21.setParameterKind(5);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(ASCII);
                    zkmScriptASTMethodName.setValue(zkmScriptToken20.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken20.setParameterKind(5);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(BRIDGE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken19.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken19.setParameterKind(5);
                    break;
                case INIT:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(INIT);
                    zkmScriptASTMethodName.setValue(zkmScriptToken18.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken18.setParameterKind(5);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(SEARCH);
                    zkmScriptASTMethodName.setValue(zkmScriptToken17.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken17.setParameterKind(5);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(DELETE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken16.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken16.setParameterKind(5);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(NORMAL);
                    zkmScriptASTMethodName.setValue(zkmScriptToken15.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken15.setParameterKind(5);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(RANDOM);
                    zkmScriptASTMethodName.setValue(zkmScriptToken14.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken14.setParameterKind(5);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(EXCLUDE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken13.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken13.setParameterKind(5);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(EXECUTE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken12.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken12.setParameterKind(5);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(SCRAMBLE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken11.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken11.setParameterKind(5);
                    break;
                case CLINIT:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(CLINIT);
                    zkmScriptASTMethodName.setValue(zkmScriptToken10.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken10.setParameterKind(5);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(ENHANCED);
                    zkmScriptASTMethodName.setValue(zkmScriptToken9.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken9.setParameterKind(5);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(SYNTHETIC);
                    zkmScriptASTMethodName.setValue(zkmScriptToken8.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken8.setParameterKind(5);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(CLASSPATH);
                    zkmScriptASTMethodName.setValue(zkmScriptToken7.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken7.setParameterKind(5);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(OBFUSCATE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken6.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken6.setParameterKind(5);
                    break;
                case RANDOMIZE:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(RANDOMIZE);
                    zkmScriptASTMethodName.setValue(zkmScriptToken5.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken5.setParameterKind(5);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(GROUPINGS);
                    zkmScriptASTMethodName.setValue(zkmScriptToken4.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken4.setParameterKind(5);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(ANNOTATION);
                    zkmScriptASTMethodName.setValue(zkmScriptToken3.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken3.setParameterKind(5);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(90);
                    zkmScriptASTMethodName.setValue(zkmScriptToken2.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken2.setParameterKind(5);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(NAME);
                    zkmScriptASTMethodName.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken1.setParameterKind(5);
                    break;
                case WILDCARD_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(WILDCARD_NAME);
                    zkmScriptASTMethodName.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(zkmScriptASTMethodName);
                    bl = false;
                    zkmScriptToken.setParameterKind(5);
                    break;
                default:
                    this.jj_la1[177] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(zkmScriptASTMethodName);
            }
        }
    }

    public final ZkmScriptSimpleNode ObfuscateFlowUnexcludeStatement() throws ZkmScriptParseException {
        ASTObfuscateFlowUnexcludeStatement aSTObfuscateFlowUnexcludeStatement = new ASTObfuscateFlowUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateFlowUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_FLOW_UNEXCLUDE);
            this.currentStatementKind = 154;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[19] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateFlowUnexcludeStatement);
                        bl = false;
                        aSTObfuscateFlowUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateFlowUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateFlowUnexcludeStatement);
            }
        }
    }

    public boolean jj_3R_108() {
        return this.jj_scan_token(QUOTE_210);
    }

    public final void ResetLongEncryptionExclusionsStatement() throws ZkmScriptParseException {
        ASTResetLongEncryptionExclusionsStatement aSTResetLongEncryptionExclusionsStatement = new ASTResetLongEncryptionExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetLongEncryptionExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_LONG_ENCRYPTION_EXCLUSIONS);
            this.currentStatementKind = 190;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetLongEncryptionExclusionsStatement);
            bl = false;
            aSTResetLongEncryptionExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetLongEncryptionExclusionsStatement);
            }
        }
    }

    public final void MixedCaseClassNamesParameter() throws ZkmScriptParseException {
        ASTMixedCaseClassNamesParameter aSTMixedCaseClassNamesParameter = new ASTMixedCaseClassNamesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMixedCaseClassNamesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(MIXED_CASE_CLASS_NAMES);
            this.jj_consume_token(ASSIGN);
            this.BooleanOrIfInArchive();
            this.jjtree.closeNodeScope(aSTMixedCaseClassNamesParameter);
            bl = false;
            aSTMixedCaseClassNamesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMixedCaseClassNamesParameter);
            }
        }
    }

    public boolean jj_3R_109() {
        return this.jj_3R_233();
    }

    public final void MethodParameter() throws ZkmScriptParseException {
        ASTMethodParameter aSTMethodParameter = new ASTMethodParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameter);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 27:
                case 28: {
                    this.ParameterComplexAnnotationSpecifier();
                    break;
                }
                default: {
                    this.jj_la1[179] = this.jj_gen;
                }
            }
            switch (this.jj_nt.kind) {
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213: {
                    this.QualifiedType();
                    break;
                }
                case 23: {
                    this.WildcardType();
                    break;
                }
                case 32: {
                    this.ParameterPlaceHolder();
                    break;
                }
                default: {
                    this.jj_la1[180] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTMethodParameter);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTMethodParameter);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_110() {
        return this.jj_scan_token(LINKED_METHOD_NAME);
    }

    public final void NewFieldNameFileParameter() throws ZkmScriptParseException {
        ASTNewFieldNameFileParameter aSTNewFieldNameFileParameter = new ASTNewFieldNameFileParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNewFieldNameFileParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NEW_FIELD_NAME_FILE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTNewFieldNameFileParameter);
            bl = false;
            aSTNewFieldNameFileParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNewFieldNameFileParameter);
            }
        }
    }

    public final void ResetGroupingsStatement() throws ZkmScriptParseException {
        ASTResetGroupingsStatement aSTResetGroupingsStatement = new ASTResetGroupingsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetGroupingsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_GROUPINGS);
            this.currentStatementKind = 107;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetGroupingsStatement);
            bl = false;
            aSTResetGroupingsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetGroupingsStatement);
            }
        }
    }

    public boolean jj_3_7() {
        return this.jj_scan_token(OR) ? true : this.jj_3R_18();
    }

    public final ZkmScriptSimpleNode DefaultMethodParameterChangesExcludeStatement() throws ZkmScriptParseException {
        ASTDefaultMethodParameterChangesExcludeStatement aSTDefaultMethodParameterChangesExcludeStatement = new ASTDefaultMethodParameterChangesExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultMethodParameterChangesExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_CHANGES_EXCLUDE);
            this.currentStatementKind = 193;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[62] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTDefaultMethodParameterChangesExcludeStatement);
                        bl = false;
                        aSTDefaultMethodParameterChangesExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTDefaultMethodParameterChangesExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultMethodParameterChangesExcludeStatement);
            }
        }
    }

    public boolean jj_3R_111() {
        return this.jj_scan_token(ENHANCED);
    }

    public final void LegalIdsParameter() throws ZkmScriptParseException {
        ASTLegalIdsParameter aSTLegalIdsParameter = new ASTLegalIdsParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLegalIdsParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LEGAL_IDENTIFIERS);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTLegalIdsParameter);
            bl = false;
            aSTLegalIdsParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLegalIdsParameter);
            }
        }
    }

    public final void DeleteDeprecatedAttributesParameter() throws ZkmScriptParseException {
        ASTDeleteDeprecatedAttributesParameter aSTDeleteDeprecatedAttributesParameter = new ASTDeleteDeprecatedAttributesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteDeprecatedAttributesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_DEPRECATED_ATTRIBUTES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteDeprecatedAttributesParameter);
            bl = false;
            aSTDeleteDeprecatedAttributesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteDeprecatedAttributesParameter);
            }
        }
    }

    public boolean jj_3R_112() {
        return this.jj_scan_token(DELETE) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3_8() {
        return this.jj_scan_token(AT) ? true : this.jj_3R_213();
    }

    public boolean jj_3R_113() {
        return this.jj_scan_token(BRIDGE);
    }

    public boolean jj_3R_114() {
        return this.jj_scan_token(NAME) ? true : this.jj_scan_token(DOT);
    }

    public final void ParameterComplexAnnotationSpecifier() throws ZkmScriptParseException {
        ASTParameterComplexAnnotationSpecifier aSTParameterComplexAnnotationSpecifier = new ASTParameterComplexAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTParameterComplexAnnotationSpecifier);
        try {
            this.ComplexAnnotationSpecifier();
            this.jjtree.closeNodeScope(aSTParameterComplexAnnotationSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTParameterComplexAnnotationSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public final ZkmScriptSimpleNode MethodParameterChangesExcludeStatement() throws ZkmScriptParseException {
        ASTMethodParameterChangesExcludeStatement aSTMethodParameterChangesExcludeStatement = new ASTMethodParameterChangesExcludeStatement(38);
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterChangesExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_CHANGES_EXCLUDE);
            this.currentStatementKind = 193;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[49] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTMethodParameterChangesExcludeStatement);
                        bl = false;
                        aSTMethodParameterChangesExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTMethodParameterChangesExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterChangesExcludeStatement);
            }
        }
    }

    public boolean jj_3R_115() {
        return this.jj_scan_token(CONTAINING) ? true : this.jj_scan_token(LBRACE);
    }

    public final ZkmScriptSimpleNode ExcludeStatement() throws ZkmScriptParseException {
        ASTExcludeStatement aSTExcludeStatement = new ASTExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXCLUDE);
            this.currentStatementKind = 64;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[16] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTExcludeStatement);
                        bl = false;
                        aSTExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExcludeStatement);
            }
        }
    }

    public boolean jj_3R_116() {
        return this.jj_scan_token(BANG);
    }

    public final void FileFilter() throws ZkmScriptParseException {
        ASTFileFilter aSTFileFilter = new ASTFileFilter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFileFilter);
        try {
            block19:
            {
                this.jj_consume_token(30);
                switch (this.jj_nt.kind) {
                    case 210: {
                        this.FileFilterComponent();
                        break;
                    }
                    case 25: {
                        this.NegatedFileFilterComponent();
                        break;
                    }
                    default: {
                        this.jj_la1[11] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ZkmScriptParseException();
                    }
                }
                block15:
                while (true) {
                    switch (this.jj_nt.kind) {
                        case 34:
                        case 35: {
                            break;
                        }
                        default: {
                            this.jj_la1[12] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_nt.kind) {
                        case 35: {
                            this.AndFileFilterComponent();
                            continue block15;
                        }
                        case 34: {
                            this.OrFileFilterComponent();
                            continue block15;
                        }
                    }
                    break;
                }
                this.jj_la1[13] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ZkmScriptParseException();
            }
            this.jj_consume_token(31);
            this.jjtree.closeNodeScope(aSTFileFilter);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTFileFilter);
                }
                throw throwable2;
            }
        }
    }

    public final void ComplexMemberSpecifier() throws ZkmScriptParseException {
        ASTComplexMemberSpecifier aSTComplexMemberSpecifier = new ASTComplexMemberSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexMemberSpecifier);
        try {
            if (this.jj_2_20()) {
                this.BracketedMemberSpecifier();
            } else {
                switch (this.jj_nt.kind) {
                    case 23:
                    case 25:
                    case 27:
                    case 28:
                    case 37:
                    case 39:
                    case 40:
                    case 42:
                    case 43:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 50:
                    case 51:
                    case 52:
                    case 54:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 63:
                    case 64:
                    case 67:
                    case 68:
                    case 69:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 77:
                    case 80:
                    case 81:
                    case 82:
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 90:
                    case 99:
                    case 213:
                    case 214: {
                        this.MemberSpecifier();
                        break;
                    }
                    default: {
                        this.jj_la1[110] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ZkmScriptParseException();
                    }
                }
            }
            this.jjtree.closeNodeScope(aSTComplexMemberSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexMemberSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_2_10() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_59();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(10, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_117() {
        return this.jj_scan_token(STAR);
    }

    public boolean jj_2_11() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_150();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(6, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_118() {
        return this.jj_scan_token(ASCII) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_2_12() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_20();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(21, Integer.MAX_VALUE);
        }

        return true;
    }

    public final void NegatedMemberSpecifierModifier() throws ZkmScriptParseException {
        ASTNegatedMemberSpecifierModifier aSTNegatedMemberSpecifierModifier = new ASTNegatedMemberSpecifierModifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNegatedMemberSpecifierModifier);
        try {
            this.jj_consume_token(25);
            this.MemberModifierHelper();
            this.jjtree.closeNodeScope(aSTNegatedMemberSpecifierModifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTNegatedMemberSpecifierModifier);
                }
                throw throwable2;
            }
        }
    }

    public final void ObfuscateParametersParameter() throws ZkmScriptParseException {
        ASTObfuscateParametersParameter aSTObfuscateParametersParameter = new ASTObfuscateParametersParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateParametersParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_PARAMETERS);
            this.jj_consume_token(ASSIGN);
            this.ObfuscateParametersType();
            this.jjtree.closeNodeScope(aSTObfuscateParametersParameter);
            bl = false;
            aSTObfuscateParametersParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateParametersParameter);
            }
        }
    }

    public final void AutoReflectionHashParameter() throws ZkmScriptParseException {
        ASTAutoReflectionHashParameter aSTAutoReflectionHashParameter = new ASTAutoReflectionHashParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAutoReflectionHashParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(AUTO_REFLECTION_HASH);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTAutoReflectionHashParameter);
            bl = false;
            aSTAutoReflectionHashParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAutoReflectionHashParameter);
            }
        }
    }

    public boolean jj_3R_119() {
        return this.jj_scan_token(GROUPINGS);
    }

    public boolean jj_3R_120() {
        return this.jj_scan_token(TRIM) ? true : this.jj_scan_token(DOT);
    }

    public final void ChangeLogOutParameter() throws ZkmScriptParseException {
        ASTChangeLogOutParameter aSTChangeLogOutParameter = new ASTChangeLogOutParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTChangeLogOutParameter);

        try {
            switch (this.jj_nt.kind) {
                case CHANGE_LOG_FILE:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(CHANGE_LOG_FILE);
                    this.jj_consume_token(ASSIGN);
                    this.StringLiteral();
                    this.jjtree.closeNodeScope(aSTChangeLogOutParameter);
                    bl = false;
                    aSTChangeLogOutParameter.setParameterName(zkmScriptToken1.image);
                    break;
                case CHANGE_LOG_FILE_OUT:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(CHANGE_LOG_FILE_OUT);
                    this.jj_consume_token(ASSIGN);
                    this.StringLiteral();
                    this.jjtree.closeNodeScope(aSTChangeLogOutParameter);
                    bl = false;
                    aSTChangeLogOutParameter.setParameterName(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[103] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTChangeLogOutParameter);
            }
        }
    }

    public boolean jj_3R_121() {
        return this.jj_scan_token(GROUPINGS);
    }

    public boolean jj_2_13() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_6();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(23, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_122() {
        return this.jj_scan_token(GC) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_123() {
        if (this.jj_3R_128()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_21());

        this.jj_scanpos = zkmScriptToken;
        return false;
    }

    public boolean jj_3R_124() {
        return this.jj_scan_token(EXECUTE);
    }

    public boolean jj_3R_125() {
        if (this.jj_scan_token(AT)) {
            return true;
        }

        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_81()) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_133();
    }

    




    public final void AndAnnotationSpecifier() throws ZkmScriptParseException {
        ASTAndAnnotationSpecifier aSTAndAnnotationSpecifier = new ASTAndAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndAnnotationSpecifier);
        try {
            this.ComplexAnnotationSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[137] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexAnnotationSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndAnnotationSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndAnnotationSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_126() {
        return this.jj_scan_token(OBFUSCATE) ? true : this.jj_scan_token(DOT);
    }

    public final ZkmScriptSimpleNode Input() throws ZkmScriptParseException {
        ZkmScriptASTInput zkmScriptASTInput = new ZkmScriptASTInput();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTInput);

        try {
            ZkmScriptToken zkmScriptToken;
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    zkmScriptToken = this.jj_nt;
                    break;
                default:
                    this.jj_la1[0] = this.jj_gen;
                    zkmScriptToken = this.jj_nt;
            }

            while (true) {
                switch (zkmScriptToken.kind) {
                    case GC:
                    case OPEN:
                    case TRIM:
                    case PRINT:
                    case EXCLUDE:
                    case SAVE_ALL:
                    case EXECUTE:
                    case UNEXCLUDE:
                    case CLASSPATH:
                    case OBFUSCATE:
                    case GROUPINGS:
                    case TRIM_EXCLUDE:
                    case FIXED_CLASSES:
                    case TRIM_UNEXCLUDE:
                    case RESET_GROUPINGS:
                    case RESET_EXCLUSIONS:
                    case RESET_FIXED_CLASSES:
                    case REMOVE_METHOD_CALLS:
                    case RESET_TRIM_EXCLUSIONS:
                    case OBFUSCATE_FLOW_EXCLUDE:
                    case ACCESSED_BY_REFLECTION:
                    case LONG_ENCRYPTION_EXCLUDE:
                    case OBFUSCATE_FLOW_UNEXCLUDE:
                    case RESET_REMOVE_METHOD_CALLS:
                    case LONG_ENCRYPTION_UNEXCLUDE:
                    case IGNORE_MISSING_REFERENCES:
                    case STRING_ENCRYPTION_EXCLUDE:
                    case INTEGER_ENCRYPTION_EXCLUDE:
                    case REMOVE_METHOD_CALLS_INCLUDE:
                    case REMOVE_METHOD_CALLS_EXCLUDE:
                    case CLASS_INITIALIZATION_ORDER:
                    case EXISTING_SERIALIZED_CLASSES:
                    case STRING_ENCRYPTION_UNEXCLUDE:
                    case RESET_ACCESSED_BY_REFLECTION:
                    case OBFUSCATE_EXCEPTIONS_EXCLUDE:
                    case INTEGER_ENCRYPTION_UNEXCLUDE:
                    case OBFUSCATE_REFERENCES_INCLUDE:
                    case OBFUSCATE_REFERENCES_EXCLUDE:
                    case ACCESSED_BY_REFLECTION_EXCLUDE:
                    case OBFUSCATE_EXCEPTIONS_UNEXCLUDE:
                    case RESET_IGNORE_MISSING_REFERENCES:
                    case RESET_OBFUSCATE_FLOW_EXCLUSIONS:
                    case RESET_LONG_ENCRYPTION_EXCLUSIONS:
                    case RESET_CLASS_INITIALIZATION_ORDER:
                    case METHOD_PARAMETER_CHANGES_INCLUDE:
                    case METHOD_PARAMETER_CHANGES_EXCLUDE:
                    case RESET_EXISTING_SERIALIZED_CLASSES:
                    case RESET_STRING_ENCRYPTION_EXCLUSIONS:
                    case RESET_INTEGER_ENCRYPTION_EXCLUSIONS:
                    case METHOD_PARAMETER_OBFUSCATION_INCLUDE:
                    case METHOD_PARAMETER_OBFUSCATION_EXCLUDE:
                    case RESET_OBFUSCATE_REFERENCE_EXCLUSIONS:
                    case RESET_OBFUSCATE_EXCEPTIONS_EXCLUSIONS:
                    case RESET_METHOD_PARAMETER_CHANGES_EXCLUSIONS:
                    case RESET_METHOD_PARAMETER_OBFUSCATION_EXCLUSIONS:
                        this.NTStatement();
                        zkmScriptToken = this.jj_nt;
                        break;
                    case 37:
                    case AND:
                    case ENUM:
                    case 41:
                    case KEEP:
                    case 43:
                    case AS_IS:
                    case LIGHT:
                    case 48:
                    case FALSE:
                    case FINAL:
                    case ASCII:
                    case BRIDGE:
                    case LINK:
                    case INIT:
                    case SPARSE:
                    case SEARCH:
                    case PUBLIC:
                    case STATIC:
                    case NATIVE:
                    case DELETE:
                    case NORMAL:
                    case THROWS:
                    case RANDOM:
                    case EXTENDS:
                    case PACKAGE:
                    case PRIVATE:
                    case MODERATE:
                    case SCRAMBLE:
                    case ABSTRACT:
                    case CLINIT:
                    case ENHANCED:
                    case VOLATILE:
                    case NON_ASCII:
                    case SYNTHETIC:
                    case PREVERIFY:
                    case PROTECTED:
                    case INTERFACE:
                    case TRANSIENT:
                    case RANDOMIZE:
                    case ANNOTATION:
                    case CONTAINING:
                    case SAVE_ALL_OLD:
                    case 90:
                    case IMPLEMENTS:
                    case CONTAINED_IN:
                    case KEEP_VISIBLE:
                    case LINE_NUMBERS:
                    case IF_IN_ARCHIVE:
                    case PACKAGE_INFO:
                    case SYNCHRONIZED:
                    case OBFUSCATE_FLOW:
                    case FLOW_OBFUSCATE:
                    case CHANGE_LOG_FILE:
                    case LOCAL_VARIABLES:
                    case HIDE_FIELD_NAMES:
                    case NEW_NAMES_PREFIX:
                    case IN_SPECIAL_CLASS:
                    case EXTRA_AGGRESSIVE:
                    case CHANGE_LOG_FILE_IN:
                    case UNIQUE_CLASS_NAMES:
                    case METHOD_PARAMETERS:
                    case KEEP_GENERICS_INFO:
                    case CHANGE_LOG_FILE_OUT:
                    case LEGAL_IDENTIFIERS:
                    case ALL_CLASSES_OPENED:
                    case NEW_CLASS_NAME_FILE:
                    case NEW_FIELD_NAME_FILE:
                    case LAST_MODIFIED_TIME:
                    case MAKE_CLASSES_PUBLIC:
                    case UNIQUE_METHOD_NAMES:
                    case NEW_METHOD_NAME_FILE:
                    case KEEP_BALANCED_LOCKS:
                    case SIGNATURE_CLASSES:
                    case NEW_NAME_CHARACTERS:
                    case DELETE_XMLCOMMENTS:
                    case OPEN_NESTED_ARCHIVES:
                    case NEW_PACKAGE_NAME_FILE:
                    case ARCHIVE_COMPRESSION:
                    case KEEP_INNER_CLASS_INFO:
                    case AUTO_REFLECTION_HASH:
                    case OBFUSCATE_PARAMETERS:
                    case MIXED_CASE_CLASS_NAMES:
                    case IF_NAME_NOT_OBFUSCATED:
                    case OBFUSCATE_REFERENCES:
                    case KEEP_IF_NOT_OBFUSCATED:
                    case EXPECTED_FINAL_SHA256:
                    case IN_REFERENCING_CLASSES:
                    case LOOSE_CHANGE_LOG_FILE_IN:
                    case EXCEPTION_OBFUSCATION:
                    case ASSUME_RUNTIME_VERSION:
                    case ENCRYPT_LONG_CONSTANTS:
                    case HIDE_STATIC_METHOD_NAMES:
                    case ENCRYPT_STRING_LITERALS:
                    case AUTO_REFLECTION_PACKAGE:
                    case EXPECTED_INITIAL_SHA256:
                    case METHOD_PARAMETER_CHANGES:
                    case AUTO_REFLECTION_HANDLING:
                    case 157:
                    case ENCRYPT_INTEGER_CONSTANTS:
                    case DELETE_UNKNOWN_ATTRIBUTES:
                    case AGGRESSIVE_METHOD_RENAMING:
                    case DELETE_EXCEPTION_ATTRIBUTES:
                    case OBFUSCATE_REFERENCES_PACKAGE:
                    case KEEP_VISIBLE_IF_NOT_OBFUSCATED:
                    case DELETE_ANNOTATION_ATTRIBUTES:
                    case DELETE_SOURCE_FILE_ATTRIBUTES:
                    case DELETE_DEPRECATED_ATTRIBUTES:
                    case ALLOW_METHOD_PARAMETER_CHANGES:
                    case KEEP_VISIBLE_METHOD_PARAMETERS:
                    case COLLAPSE_PACKAGES_WITH_DEFAULT:
                    case OBFUSCATE_REFERENCE_STRUCTURES:
                    case METHOD_PARAMETER_CHANGES_PACKAGE:
                    case DELETE_DEBUG_EXTENSION_ATTRIBUTES:
                    case DERIVE_GROUPINGS_FROM_INPUT_CHANGE_LOG:
                    case DERIVE_SUBCLASS_NAMES_FROM_SUPERCLASS:
                    case KEEP_METHOD_PARAMETERS_IF_NOT_OBFUSCATED:
                    default:
                        this.jj_la1[1] = this.jj_gen;
                        this.jj_consume_token(EOF);
                        this.jjtree.closeNodeScope(zkmScriptASTInput);
                        bl = false;
                        return zkmScriptASTInput;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(zkmScriptASTInput);
            }
        }
    }

    public final void NewPackageNameFileParameter() throws ZkmScriptParseException {
        ASTNewPackageNameFileParameter aSTNewPackageNameFileParameter = new ASTNewPackageNameFileParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNewPackageNameFileParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NEW_PACKAGE_NAME_FILE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTNewPackageNameFileParameter);
            bl = false;
            aSTNewPackageNameFileParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNewPackageNameFileParameter);
            }
        }
    }

    public final void Annotation() throws ZkmScriptParseException {
        ASTAnnotation aSTAnnotation = new ASTAnnotation();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAnnotation);
        try {
            this.jj_consume_token(27);
            if (this.jj_2_17()) {
                this.PackageName();
            }
            this.ClassName();
            this.jjtree.closeNodeScope(aSTAnnotation);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTAnnotation);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_127() {
        return this.jj_scan_token(EXCLUDE);
    }

    public boolean jj_3R_128() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_109()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_26()) {
                return true;
            }
        }

        zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_51()) {
            this.jj_scanpos = zkmScriptToken;
        }

        zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_232()) {
            this.jj_scanpos = zkmScriptToken;
        }

        return false;
    }

    public boolean jj_3R_129() {
        return this.jj_scan_token(ENHANCED);
    }

    public boolean jj_3R_130() {
        return this.jj_scan_token(90);
    }

    public final void PrintStatement() throws ZkmScriptParseException {
        ASTPrintStatement aSTPrintStatement = new ASTPrintStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPrintStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(PRINT);
            this.currentStatementKind = 46;
            this.StringLiteral();
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTPrintStatement);
            bl = false;
            aSTPrintStatement.setLineNumber(zkmScriptToken.endLine);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPrintStatement);
            }
        }
    }

    public boolean jj_3R_131() {
        return this.jj_3R_222();
    }

    public boolean jj_3R_132() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public boolean jj_3R_133() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_202()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_3()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_203()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_17()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_139()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_2()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_19()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_164()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_196()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_211()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_189()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_50()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_276()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_66()) {
                                                                this.jj_scanpos = zkmScriptToken;
                                                                if (this.jj_3R_145()) {
                                                                    this.jj_scanpos = zkmScriptToken;
                                                                    if (this.jj_3R_142()) {
                                                                        this.jj_scanpos = zkmScriptToken;
                                                                        if (this.jj_3R_197()) {
                                                                            this.jj_scanpos = zkmScriptToken;
                                                                            if (this.jj_3R_244()) {
                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                if (this.jj_3R_188()) {
                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                    if (this.jj_3R_207()) {
                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                        if (this.jj_3R_77()) {
                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                            if (this.jj_3R_119()) {
                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                if (this.jj_3R_271()) {
                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                    if (this.jj_3R_175()) {
                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                        if (this.jj_3R_226()) {
                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                            if (this.jj_3R_209()) {
                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                if (this.jj_3R_113()) {
                                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                                    if (this.jj_3R_173()) {
                                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                                        if (this.jj_3R_42()) {
                                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                                            if (this.jj_3R_118()) {
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

    public final void ResetExclusionsStatement() throws ZkmScriptParseException {
        ASTResetExclusionsStatement aSTResetExclusionsStatement = new ASTResetExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_EXCLUSIONS);
            this.currentStatementKind = 111;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetExclusionsStatement);
            bl = false;
            aSTResetExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetExclusionsStatement);
            }
        }
    }

    public boolean jj_3R_134() {
        return this.jj_scan_token(STAR) ? true : this.jj_scan_token(DOT);
    }

    public final void LiteralPackageComponent() throws ZkmScriptParseException {
        ASTLiteralPackageComponent aSTLiteralPackageComponent = new ASTLiteralPackageComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLiteralPackageComponent);

        try {
            switch (this.jj_nt.kind) {
                case STAR:
                    ZkmScriptToken zkmScriptToken30 = this.jj_consume_token(STAR);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken30.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken30.setParameterKind(2);
                    break;
                case GC:
                    ZkmScriptToken zkmScriptToken29 = this.jj_consume_token(GC);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken29.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken29.setParameterKind(2);
                    break;
                case 37:
                    ZkmScriptToken zkmScriptToken28 = this.jj_consume_token(37);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken28.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken28.setParameterKind(2);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken27 = this.jj_consume_token(OPEN);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken27.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken27.setParameterKind(2);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(ENUM);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken26.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken26.setParameterKind(2);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(KEEP);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken25.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken25.setParameterKind(2);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(43);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken24.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken24.setParameterKind(2);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(TRIM);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken23.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken23.setParameterKind(2);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(PRINT);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken22.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken22.setParameterKind(2);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(LIGHT);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken21.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken21.setParameterKind(2);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(48);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken20.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken20.setParameterKind(2);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(ASCII);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken19.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken19.setParameterKind(2);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(BRIDGE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken18.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken18.setParameterKind(2);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(SEARCH);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken17.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken17.setParameterKind(2);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(DELETE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken16.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken16.setParameterKind(2);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(NORMAL);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken15.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken15.setParameterKind(2);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(RANDOM);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken14.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken14.setParameterKind(2);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(EXCLUDE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken13.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken13.setParameterKind(2);
                    break;
                case PACKAGE:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(PACKAGE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken12.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken12.setParameterKind(2);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(EXECUTE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken11.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken11.setParameterKind(2);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(SCRAMBLE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken10.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken10.setParameterKind(2);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(ENHANCED);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken9.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken9.setParameterKind(2);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(SYNTHETIC);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken8.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken8.setParameterKind(2);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(CLASSPATH);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken7.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken7.setParameterKind(2);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(OBFUSCATE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken6.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken6.setParameterKind(2);
                    break;
                case RANDOMIZE:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(RANDOMIZE);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken5.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken5.setParameterKind(2);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(GROUPINGS);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken4.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken4.setParameterKind(2);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(ANNOTATION);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken3.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken3.setParameterKind(2);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(90);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken2.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken2.setParameterKind(2);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(NAME);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken1.setParameterKind(2);
                    break;
                case WILDCARD_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(WILDCARD_NAME);
                    this.jj_consume_token(DOT);
                    aSTLiteralPackageComponent.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
                    bl = false;
                    zkmScriptToken.setParameterKind(2);
                    break;
                default:
                    this.jj_la1[150] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLiteralPackageComponent);
            }
        }
    }

    public final void FileFilterComponent() throws ZkmScriptParseException {
        ASTFileFilterComponent aSTFileFilterComponent = new ASTFileFilterComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFileFilterComponent);
        try {
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTFileFilterComponent);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTFileFilterComponent);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_135() {
        return this.jj_scan_token(OPEN);
    }

    public boolean jj_3_9() {
        return this.jj_scan_token(AND_AND) ? true : this.jj_3R_80();
    }

    public boolean jj_3R_136() {
        return this.jj_scan_token(KEEP);
    }

    public final void ObfuscateReferencesPackageParameter() throws ZkmScriptParseException {
        ASTObfuscateReferencesPackageParameter aSTObfuscateReferencesPackageParameter = new ASTObfuscateReferencesPackageParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferencesPackageParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_REFERENCES_PACKAGE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTObfuscateReferencesPackageParameter);
            bl = false;
            aSTObfuscateReferencesPackageParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferencesPackageParameter);
            }
        }
    }

    public final void MemberSpecifier() throws ZkmScriptParseException {
        ASTMemberSpecifier aSTMemberSpecifier = new ASTMemberSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMemberSpecifier);
        try {
            if (this.jj_2_14()) {
                this.MemberSpecifierComplexAnnotationSpecifier();
            }
            block11:
            while (this.jj_2_24()) {
                switch (this.jj_nt.kind) {
                    case 40:
                    case 50:
                    case 52:
                    case 57:
                    case 58:
                    case 59:
                    case 67:
                    case 68:
                    case 72:
                    case 75:
                    case 77:
                    case 82:
                    case 84:
                    case 99: {
                        this.MemberSpecifierModifier();
                        continue block11;
                    }
                    case 25: {
                        this.NegatedMemberSpecifierModifier();
                        continue block11;
                    }
                }
                this.jj_la1[114] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ZkmScriptParseException();
            }
            if (this.jj_2_12()) {
                this.BasicMethodSignature();
            } else {
                switch (this.jj_nt.kind) {
                    case 23:
                    case 25:
                    case 28:
                    case 37:
                    case 39:
                    case 40:
                    case 42:
                    case 43:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 51:
                    case 52:
                    case 56:
                    case 60:
                    case 61:
                    case 63:
                    case 64:
                    case 69:
                    case 71:
                    case 74:
                    case 77:
                    case 80:
                    case 81:
                    case 85:
                    case 86:
                    case 87:
                    case 90:
                    case 213:
                    case 214: {
                        this.FieldSignature();
                        break;
                    }
                    default: {
                        this.jj_la1[115] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ZkmScriptParseException();
                    }
                }
            }
            this.jjtree.closeNodeScope(aSTMemberSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTMemberSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_137() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_30()) {
            this.jj_scanpos = zkmScriptToken;
        }

        if (this.jj_scan_token(LPAREN)) {
            return true;
        }

        if (this.jj_3R_100()) {
            return true;
        }

        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_4());

        this.jj_scanpos = zkmScriptToken;
        return this.jj_scan_token(RPAREN);
    }

    public boolean jj_3R_138() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_171()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_92()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_272()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_152()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_224()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_95()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_28()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_25()) {
                                        return true;
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

    public final void ClassComplexAnnotationSpecifier() throws ZkmScriptParseException {
        ASTClassComplexAnnotationSpecifier aSTClassComplexAnnotationSpecifier = new ASTClassComplexAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTClassComplexAnnotationSpecifier);
        try {
            this.ComplexAnnotationSpecifier();
            this.jjtree.closeNodeScope(aSTClassComplexAnnotationSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTClassComplexAnnotationSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_139() {
        return this.jj_scan_token(OPEN);
    }

    public boolean jj_3R_140() {
        return this.jj_3R_249();
    }

    public boolean jj_3R_141() {
        return this.jj_3R_128();
    }

    public boolean jj_3R_142() {
        return this.jj_scan_token(LIGHT);
    }

    public final ZkmScriptSimpleNode LongEncryptionUnexcludeStatement() throws ZkmScriptParseException {
        ASTLongEncryptionUnexcludeStatement aSTLongEncryptionUnexcludeStatement = new ASTLongEncryptionUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLongEncryptionUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LONG_ENCRYPTION_UNEXCLUDE);
            this.currentStatementKind = 159;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[27] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTLongEncryptionUnexcludeStatement);
                        bl = false;
                        aSTLongEncryptionUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTLongEncryptionUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLongEncryptionUnexcludeStatement);
            }
        }
    }

    public boolean jj_3R_143() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_116()) {
            this.jj_scanpos = zkmScriptToken;
        }

        if (this.jj_scan_token(LPAREN)) {
            return true;
        }

        if (this.jj_3R_18()) {
            return true;
        }

        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_7());

        this.jj_scanpos = zkmScriptToken;
        return this.jj_scan_token(RPAREN);
    }

    public boolean jj_3R_144() {
        return this.jj_scan_token(CLASSPATH) ? true : this.jj_scan_token(DOT);
    }

    public final void EncryptParameter() throws ZkmScriptParseException {
        ASTEncryptParameter aSTEncryptParameter = new ASTEncryptParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTEncryptParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ENCRYPT_STRING_LITERALS);
            this.jj_consume_token(ASSIGN);
            this.EncryptionType();
            this.jjtree.closeNodeScope(aSTEncryptParameter);
            bl = false;
            aSTEncryptParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTEncryptParameter);
            }
        }
    }

    public final void ParameterPlaceHolder() throws ZkmScriptParseException {
        ASTParameterPlaceHolder aSTParameterPlaceHolder = new ASTParameterPlaceHolder();
        this.jjtree.openNodeScope(aSTParameterPlaceHolder);

        try {
            this.jj_consume_token(HOOK);
        } finally {
            this.jjtree.closeNodeScope(aSTParameterPlaceHolder);
        }
    }

    public final void ArchiveCompressionParameter() throws ZkmScriptParseException {
        ASTArchiveCompressionParameter aSTArchiveCompressionParameter = new ASTArchiveCompressionParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTArchiveCompressionParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ARCHIVE_COMPRESSION);
            this.jj_consume_token(ASSIGN);
            this.ArchiveCompressionType();
            this.jjtree.closeNodeScope(aSTArchiveCompressionParameter);
            bl = false;
            aSTArchiveCompressionParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTArchiveCompressionParameter);
            }
        }
    }

    public final void MethodParameterChangesParameter() throws ZkmScriptParseException {
        ASTMethodParameterChangesParameter aSTMethodParameterChangesParameter = new ASTMethodParameterChangesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterChangesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_CHANGES);
            this.jj_consume_token(ASSIGN);
            this.MethodParameterChangesType();
            this.jjtree.closeNodeScope(aSTMethodParameterChangesParameter);
            bl = false;
            aSTMethodParameterChangesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterChangesParameter);
            }
        }
    }

    public boolean jj_3R_145() {
        return this.jj_scan_token(43);
    }

    public boolean jj_3R_146() {
        return this.jj_scan_token(ENHANCED) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_147() {
        return this.jj_scan_token(TRIM);
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

    public final void ComplexFieldSpecifier() throws ZkmScriptParseException {
        ASTComplexFieldSpecifier aSTComplexFieldSpecifier = new ASTComplexFieldSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexFieldSpecifier);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 28: {
                    this.BracketedFieldSpecifier();
                    break;
                }
                case 23:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213:
                case 214: {
                    this.FieldName();
                    break;
                }
                default: {
                    this.jj_la1[161] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTComplexFieldSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexFieldSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public final void FieldName() throws ZkmScriptParseException {
        ASTFieldName aSTFieldName = new ASTFieldName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFieldName);

        try {
            switch (this.jj_nt.kind) {
                case STAR:
                    ZkmScriptToken zkmScriptToken28 = this.jj_consume_token(STAR);
                    aSTFieldName.setValue(zkmScriptToken28.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken28.setParameterKind(4);
                    break;
                case 37:
                    ZkmScriptToken zkmScriptToken27 = this.jj_consume_token(37);
                    aSTFieldName.setValue(zkmScriptToken27.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken27.setParameterKind(4);
                    break;
                case OPEN:
                    ZkmScriptToken zkmScriptToken26 = this.jj_consume_token(OPEN);
                    aSTFieldName.setValue(zkmScriptToken26.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken26.setParameterKind(4);
                    break;
                case ENUM:
                    ZkmScriptToken zkmScriptToken25 = this.jj_consume_token(ENUM);
                    aSTFieldName.setValue(zkmScriptToken25.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken25.setParameterKind(4);
                    break;
                case KEEP:
                    ZkmScriptToken zkmScriptToken24 = this.jj_consume_token(KEEP);
                    aSTFieldName.setValue(zkmScriptToken24.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken24.setParameterKind(4);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken23 = this.jj_consume_token(43);
                    aSTFieldName.setValue(zkmScriptToken23.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken23.setParameterKind(4);
                    break;
                case TRIM:
                    ZkmScriptToken zkmScriptToken22 = this.jj_consume_token(TRIM);
                    aSTFieldName.setValue(zkmScriptToken22.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken22.setParameterKind(4);
                    break;
                case PRINT:
                    ZkmScriptToken zkmScriptToken21 = this.jj_consume_token(PRINT);
                    aSTFieldName.setValue(zkmScriptToken21.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken21.setParameterKind(4);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken20 = this.jj_consume_token(LIGHT);
                    aSTFieldName.setValue(zkmScriptToken20.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken20.setParameterKind(4);
                    break;
                case 48:
                    ZkmScriptToken zkmScriptToken19 = this.jj_consume_token(48);
                    aSTFieldName.setValue(zkmScriptToken19.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken19.setParameterKind(4);
                    break;
                case ASCII:
                    ZkmScriptToken zkmScriptToken18 = this.jj_consume_token(ASCII);
                    aSTFieldName.setValue(zkmScriptToken18.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken18.setParameterKind(4);
                    break;
                case BRIDGE:
                    ZkmScriptToken zkmScriptToken17 = this.jj_consume_token(BRIDGE);
                    aSTFieldName.setValue(zkmScriptToken17.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken17.setParameterKind(4);
                    break;
                case SEARCH:
                    ZkmScriptToken zkmScriptToken16 = this.jj_consume_token(SEARCH);
                    aSTFieldName.setValue(zkmScriptToken16.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken16.setParameterKind(4);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken15 = this.jj_consume_token(DELETE);
                    aSTFieldName.setValue(zkmScriptToken15.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken15.setParameterKind(4);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken14 = this.jj_consume_token(NORMAL);
                    aSTFieldName.setValue(zkmScriptToken14.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken14.setParameterKind(4);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken13 = this.jj_consume_token(RANDOM);
                    aSTFieldName.setValue(zkmScriptToken13.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken13.setParameterKind(4);
                    break;
                case EXCLUDE:
                    ZkmScriptToken zkmScriptToken12 = this.jj_consume_token(EXCLUDE);
                    aSTFieldName.setValue(zkmScriptToken12.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken12.setParameterKind(4);
                    break;
                case EXECUTE:
                    ZkmScriptToken zkmScriptToken11 = this.jj_consume_token(EXECUTE);
                    aSTFieldName.setValue(zkmScriptToken11.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken11.setParameterKind(4);
                    break;
                case SCRAMBLE:
                    ZkmScriptToken zkmScriptToken10 = this.jj_consume_token(SCRAMBLE);
                    aSTFieldName.setValue(zkmScriptToken10.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken10.setParameterKind(4);
                    break;
                case ENHANCED:
                    ZkmScriptToken zkmScriptToken9 = this.jj_consume_token(ENHANCED);
                    aSTFieldName.setValue(zkmScriptToken9.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken9.setParameterKind(4);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken8 = this.jj_consume_token(SYNTHETIC);
                    aSTFieldName.setValue(zkmScriptToken8.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken8.setParameterKind(4);
                    break;
                case CLASSPATH:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(CLASSPATH);
                    aSTFieldName.setValue(zkmScriptToken7.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken7.setParameterKind(4);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(OBFUSCATE);
                    aSTFieldName.setValue(zkmScriptToken6.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken6.setParameterKind(4);
                    break;
                case RANDOMIZE:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(RANDOMIZE);
                    aSTFieldName.setValue(zkmScriptToken5.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken5.setParameterKind(4);
                    break;
                case GROUPINGS:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(GROUPINGS);
                    aSTFieldName.setValue(zkmScriptToken4.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken4.setParameterKind(4);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(ANNOTATION);
                    aSTFieldName.setValue(zkmScriptToken3.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken3.setParameterKind(4);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(90);
                    aSTFieldName.setValue(zkmScriptToken2.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken2.setParameterKind(4);
                    break;
                case NAME:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(NAME);
                    aSTFieldName.setValue(zkmScriptToken1.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken1.setParameterKind(4);
                    break;
                case WILDCARD_NAME:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(WILDCARD_NAME);
                    aSTFieldName.setValue(zkmScriptToken.image);
                    this.jjtree.closeNodeScope(aSTFieldName);
                    bl = false;
                    zkmScriptToken.setParameterKind(4);
                    break;
                default:
                    this.jj_la1[165] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTFieldName);
            }
        }
    }

    public final void ContainedInClause() throws ZkmScriptParseException {
        ASTContainedInClause aSTContainedInClause = new ASTContainedInClause();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTContainedInClause);
        try {
            this.jj_consume_token(92);
            this.jj_consume_token(30);
            switch (this.jj_nt.kind) {
                case 23:
                case 25:
                case 27:
                case 28:
                case 36:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 50:
                case 51:
                case 52:
                case 56:
                case 57:
                case 60:
                case 61:
                case 63:
                case 64:
                case 67:
                case 69:
                case 71:
                case 72:
                case 74:
                case 77:
                case 80:
                case 81:
                case 83:
                case 85:
                case 86:
                case 87:
                case 90:
                case 97:
                case 210:
                case 213:
                case 214:
                case 215:
                case 216: {
                    this.RenameFilterParameter();
                    break;
                }
                default: {
                    this.jj_la1[109] = this.jj_gen;
                }
            }
            this.jj_consume_token(31);
            this.jjtree.closeNodeScope(aSTContainedInClause);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTContainedInClause);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_148() {
        return this.jj_scan_token(TRIM);
    }

    public final void MemberModifier() throws ZkmScriptParseException {
        ZkmScriptASTMemberModifier zkmScriptASTMemberModifier = new ZkmScriptASTMemberModifier();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTMemberModifier);
        try {
            this.MemberModifierHelper();
            this.jjtree.closeNodeScope(zkmScriptASTMemberModifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(zkmScriptASTMemberModifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_149() {
        return this.jj_scan_token(PUBLIC);
    }

    public boolean jj_3R_150() {
        return this.jj_3_10();
    }

    public final void NegatedFileFilterComponent() throws ZkmScriptParseException {
        ASTNegatedFileFilterComponent aSTNegatedFileFilterComponent = new ASTNegatedFileFilterComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNegatedFileFilterComponent);
        try {
            this.jj_consume_token(25);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTNegatedFileFilterComponent);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTNegatedFileFilterComponent);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_151() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public final void QualifiedClassName() throws ZkmScriptParseException {
        ASTQualifiedClassName aSTQualifiedClassName = new ASTQualifiedClassName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTQualifiedClassName);
        try {
            if (this.jj_2_21()) {
                this.PackageName();
            }
            this.ClassName();
            this.jjtree.closeNodeScope(aSTQualifiedClassName);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTQualifiedClassName);
                }
                throw throwable2;
            }
        }
    }

    public void jj_rescan_token() {
        this.jj_rescan = true;
        int ba = 0;
        int bb = 0;

        for (byte bc = 28; bb < bc; bc = 28) {
            try {
                ZkmScriptParserJJCalls zkmScriptParserJJCalls = this.jj_2_rtns[ba];
                bb = zkmScriptParserJJCalls.gen;

                while (true) {
                    ZkmScriptParserJJCalls zkmScriptParserJJCalls1;
                    if (bb > this.jj_gen) {
                        this.jj_la = zkmScriptParserJJCalls.arg;
                        this.jj_lastpos = this.jj_scanpos = zkmScriptParserJJCalls.first;
                        switch (ba) {
                            case 0:
                                this.jj_3R_141();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 1:
                                this.jj_3R_131();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 2:
                                this.jj_3R_6();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 3:
                                this.jj_3_19();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 4:
                                this.jj_3R_57();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 5:
                                this.jj_3R_11();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 6:
                                this.jj_3R_150();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 7:
                                this.jj_3R_215();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 8:
                                this.jj_3R_115();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 9:
                                this.jj_3R_245();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 10:
                                this.jj_3R_59();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 11:
                                this.jj_3_18();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 12:
                                this.jj_3_2();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 13:
                                this.jj_3R_267();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 14:
                                this.jj_3R_240();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 15:
                                this.jj_3R_89();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 16:
                                this.jj_3R_154();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 17:
                                this.jj_3_13();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 18:
                                this.jj_3R_181();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 19:
                                this.jj_3R_12();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 20:
                                this.jj_3R_270();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 21:
                                this.jj_3_20();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 22:
                                this.jj_3R_182();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 23:
                                this.jj_3_6();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 24:
                                this.jj_3R_104();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 25:
                                this.jj_3_15();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 26:
                                this.jj_3R_192();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            case 27:
                                this.jj_3R_1();
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                                break;
                            default:
                                zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                        }
                    } else {
                        zkmScriptParserJJCalls1 = zkmScriptParserJJCalls.next;
                    }

                    zkmScriptParserJJCalls = zkmScriptParserJJCalls1;
                    if (zkmScriptParserJJCalls == null) {
                        break;
                    }

                    bb = zkmScriptParserJJCalls.gen;
                }
            } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
            }

            bb = ++ba;
        }

        this.jj_rescan = false;
    }

    public boolean jj_2_14() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_12();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(19, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_152() {
        return this.jj_scan_token(INTERFACE);
    }

    public boolean jj_3R_153() {
        return this.jj_scan_token(KEEP) ? true : this.jj_scan_token(DOT);
    }

    public final void AutoReflectionType() throws ZkmScriptParseException {
        ASTAutoReflectionType aSTAutoReflectionType = new ASTAutoReflectionType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAutoReflectionType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTAutoReflectionType);
                    bl = false;
                    aSTAutoReflectionType.setValue(zkmScriptToken1.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTAutoReflectionType);
                    bl = false;
                    aSTAutoReflectionType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[122] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAutoReflectionType);
            }
        }
    }

    public boolean jj_3R_154() {
        if (this.jj_3_11()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(AND)) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_scan_token(SEMICOLON)) {
                return true;
            }
        }

        return false;
    }

    public boolean jj_3_10() {
        return this.jj_3R_108() ? true : this.jj_scan_token(BANG);
    }

    public final void DeleteAnnotationsParameter() throws ZkmScriptParseException {
        ASTDeleteAnnotationsParameter aSTDeleteAnnotationsParameter = new ASTDeleteAnnotationsParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteAnnotationsParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_ANNOTATION_ATTRIBUTES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteAnnotationsParameter);
            bl = false;
            aSTDeleteAnnotationsParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteAnnotationsParameter);
            }
        }
    }

    public final void LastModifiedTime() throws ZkmScriptParseException {
        ASTLastModifiedTime aSTLastModifiedTime = new ASTLastModifiedTime();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLastModifiedTime);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LAST_MODIFIED_TIME);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTLastModifiedTime);
            bl = false;
            aSTLastModifiedTime.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLastModifiedTime);
            }
        }
    }

    public final void ReferencingAnnotation() throws ZkmScriptParseException {
        ASTReferencingAnnotation aSTReferencingAnnotation = new ASTReferencingAnnotation();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTReferencingAnnotation);
        try {
            this.jj_consume_token(27);
            this.QualifiedClassName();
            this.jjtree.closeNodeScope(aSTReferencingAnnotation);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTReferencingAnnotation);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_155() {
        return this.jj_scan_token(STAR);
    }

    public final void StringLiteral() throws ZkmScriptParseException {
        ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = new ZkmScriptASTStringLiteral();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTStringLiteral);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(QUOTE_210);
            this.jjtree.closeNodeScope(zkmScriptASTStringLiteral);
            bl = false;
            String string = zkmScriptToken.image;
            string = string.substring(1, string.length() - 1);
            string = ZkmStringUtils.replaceAll(string, ESCAPED_QUOTE, "\"");
            zkmScriptASTStringLiteral.setValue(string);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(zkmScriptASTStringLiteral);
            }
        }
    }

    public boolean jj_3R_156() {
        return this.jj_scan_token(EXECUTE);
    }

    public boolean jj_3R_157() {
        return this.jj_scan_token(LINKED_CLASS_NAME_WITH_SUFFIX);
    }

    public boolean jj_3R_158() {
        return this.jj_scan_token(43);
    }

    public boolean jj_3R_159() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_149()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_210()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_185()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_33()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_214()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_36()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_99()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_167()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_187()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_97()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_204()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_34()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_250()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_162()) {
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

        return false;
    }

    public boolean jj_3R_160() {
        return this.jj_scan_token(BRIDGE);
    }

    public boolean jj_3R_161() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_38()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_83()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_65()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_101()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_174()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_21()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_248()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_87()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_261()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_4()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_74()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_72()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_78()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_158()) {
                                                                this.jj_scanpos = zkmScriptToken;
                                                                if (this.jj_3R_46()) {
                                                                    this.jj_scanpos = zkmScriptToken;
                                                                    if (this.jj_3R_223()) {
                                                                        this.jj_scanpos = zkmScriptToken;
                                                                        if (this.jj_3R_147()) {
                                                                            this.jj_scanpos = zkmScriptToken;
                                                                            if (this.jj_3R_105()) {
                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                if (this.jj_3R_229()) {
                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                    if (this.jj_3R_190()) {
                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                        if (this.jj_3R_71()) {
                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                            if (this.jj_3R_273()) {
                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                if (this.jj_3R_132()) {
                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                    if (this.jj_3R_194()) {
                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                        if (this.jj_3R_43()) {
                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                            if (this.jj_3R_235()) {
                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                if (this.jj_3R_88()) {
                                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                                    if (this.jj_3R_40()) {
                                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                                        if (this.jj_3R_225()) {
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

        return false;
    }

    public boolean jj_3R_162() {
        return this.jj_scan_token(BRIDGE);
    }

    public boolean jj_3R_163() {
        return this.jj_scan_token(37);
    }

    public static void jj_la1_init_0() {
        jj_la1_0 = new int[]{
                16384,
                0,
                0,
                0,
                1073741824,
                0,
                16842752,
                1073741824,
                0,
                16842752,
                0,
                33554432,
                0,
                0,
                33554432,
                33554432,
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
                444596224,
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
                16384,
                0,
                0,
                16384,
                0,
                0,
                16384,
                0,
                0,
                16384,
                0,
                0,
                16384,
                0,
                16384,
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
                33554432,
                310378496,
                444596224,
                0,
                310378496,
                0,
                0,
                1048576,
                0,
                33554432,
                0,
                0,
                33554432,
                310378496,
                444596224,
                310378496,
                444596224,
                0,
                0,
                1048576,
                1048576,
                0,
                0,
                0,
                0,
                0,
                0,
                444596224,
                444596224,
                444596224,
                33554432,
                0,
                0,
                33554432,
                310378496,
                436207616,
                436207616,
                1048576,
                436207616,
                1048576,
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
                436207616,
                33554432,
                0,
                0,
                310378496,
                67108864,
                33554432,
                0,
                0,
                310378496,
                2097152,
                67108864,
                33554432,
                0,
                0,
                8388608,
                8388608,
                2097152,
                8388608,
                310378496,
                83886080,
                83886080,
                33554432,
                0,
                0,
                8388608,
                0,
                310378496,
                33554432,
                0,
                0,
                8388608,
                444596224,
                0,
                444596224,
                0,
                0,
                444596224,
                0,
                310378496,
                33554432,
                0,
                0,
                8388608,
                1048576,
                436207616,
                8388608,
                2097152,
                131072,
                0,
                0,
                0,
                0
        };
    }

    public boolean jj_3R_164() {
        return this.jj_scan_token(DELETE);
    }

    public boolean jj_3R_165() {
        return this.jj_scan_token(CARET);
    }

    public boolean jj_3R_166() {
        return this.jj_scan_token(ENUM);
    }

    public final ZkmScriptSimpleNode IntegerEncryptionUnexcludeStatement() throws ZkmScriptParseException {
        ASTIntegerEncryptionUnexcludeStatement aSTIntegerEncryptionUnexcludeStatement = new ASTIntegerEncryptionUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIntegerEncryptionUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(INTEGER_ENCRYPTION_UNEXCLUDE);
            this.currentStatementKind = 174;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[25] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTIntegerEncryptionUnexcludeStatement);
                        bl = false;
                        aSTIntegerEncryptionUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTIntegerEncryptionUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIntegerEncryptionUnexcludeStatement);
            }
        }
    }

    public boolean jj_3R_167() {
        return this.jj_scan_token(VOLATILE);
    }

    public boolean jj_3R_168() {
        return this.jj_scan_token(INIT);
    }

    public boolean jj_2_15() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_240();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(14, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3_11() {
        return this.jj_scan_token(AT) ? true : this.jj_3R_213();
    }

    




    public final void MethodArguments() throws ZkmScriptParseException {
        ZkmScriptASTMethodArguments zkmScriptASTMethodArguments = new ZkmScriptASTMethodArguments();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTMethodArguments);
        try {
            this.MethodParameter();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 20: {
                        break;
                    }
                    default: {
                        this.jj_la1[178] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(20);
                this.MethodParameter();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(zkmScriptASTMethodArguments);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(zkmScriptASTMethodArguments);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_169() {
        return this.jj_scan_token(EXCLUDE) ? true : this.jj_scan_token(DOT);
    }

    public final void ModuleName() throws ZkmScriptParseException {
        ASTModuleName aSTModuleName = new ASTModuleName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTModuleName);
        try {
            while (this.jj_2_13()) {
                this.ModuleNameComponent();
                this.jj_consume_token(21);
            }
            this.ModuleNameComponent();
            this.jj_consume_token(22);
            this.jjtree.closeNodeScope(aSTModuleName);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTModuleName);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_170() {
        return this.jj_scan_token(43) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_171() {
        return this.jj_scan_token(PUBLIC);
    }

    public final void LocalVariableParameterType() throws ZkmScriptParseException {
        ASTLocalVariableParameterType aSTLocalVariableParameterType = new ASTLocalVariableParameterType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLocalVariableParameterType);

        try {
            switch (this.jj_nt.kind) {
                case KEEP:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(KEEP);
                    this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
                    bl = false;
                    aSTLocalVariableParameterType.setValue(zkmScriptToken5.image);
                    break;
                case DELETE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(DELETE);
                    this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
                    bl = false;
                    aSTLocalVariableParameterType.setValue(zkmScriptToken4.image);
                    break;
                case OBFUSCATE:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(OBFUSCATE);
                    this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
                    bl = false;
                    aSTLocalVariableParameterType.setValue(zkmScriptToken3.image);
                    break;
                case KEEP_VISIBLE_METHOD_PARAMETERS:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(KEEP_VISIBLE_METHOD_PARAMETERS);
                    this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
                    bl = false;
                    aSTLocalVariableParameterType.setValue(zkmScriptToken2.image);
                    break;
                case KEEP_METHOD_PARAMETERS_IF_NOT_OBFUSCATED:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(KEEP_METHOD_PARAMETERS_IF_NOT_OBFUSCATED);
                    this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
                    bl = false;
                    aSTLocalVariableParameterType.setValue(zkmScriptToken1.image);
                    break;
                case KEEP_VISIBLE_METHOD_PARAMETERS_IF_NOT_OBFUSCATED:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(KEEP_VISIBLE_METHOD_PARAMETERS_IF_NOT_OBFUSCATED);
                    this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
                    bl = false;
                    aSTLocalVariableParameterType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[132] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLocalVariableParameterType);
            }
        }
    }

    public static void jj_la1_init_6() {
        jj_la1_6 = new int[]{
                0,
                55787,
                55787,
                0,
                0,
                0,
                262144,
                0,
                0,
                262144,
                0,
                262144,
                0,
                0,
                262144,
                262144,
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
                31719424,
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
                2,
                0,
                0,
                256,
                0,
                0,
                0,
                0,
                1310720,
                1310720,
                16,
                516,
                0,
                262144,
                0,
                0,
                1048576,
                0,
                0,
                0,
                0,
                6291456,
                6291456,
                262144,
                6291456,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                6291456,
                39845888,
                6553600,
                23330816,
                16,
                516,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                6291456,
                31719424,
                6291456,
                0,
                0,
                0,
                0,
                6291456,
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
                73728,
                0,
                0,
                0,
                0,
                0,
                14680064,
                0,
                0,
                0,
                0,
                6291456,
                0,
                0,
                0,
                0,
                0,
                14680064,
                6291456,
                0,
                6291456,
                6291456,
                0,
                0,
                0,
                0,
                0,
                6291456,
                2097152,
                6291456,
                0,
                0,
                0,
                6291456,
                2097152,
                0,
                2097152,
                0,
                0,
                2097152,
                33554432,
                6291456,
                0,
                0,
                0,
                6291456,
                0,
                0,
                2097152,
                0,
                0,
                2097152,
                0,
                0,
                0
        };
    }

    public final ZkmScriptSimpleNode ExistingSerializedClassesStatement() throws ZkmScriptParseException {
        ASTExistingSerializedClassesStatement aSTExistingSerializedClassesStatement = new ASTExistingSerializedClassesStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExistingSerializedClassesStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXISTING_SERIALIZED_CLASSES);
            this.currentStatementKind = 169;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[28] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTExistingSerializedClassesStatement);
                        bl = false;
                        aSTExistingSerializedClassesStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTExistingSerializedClassesStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExistingSerializedClassesStatement);
            }
        }
    }

    public boolean jj_3R_172() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public final void ObfuscateFlowParameter() throws ZkmScriptParseException {
        ASTObfuscateFlowParameter aSTObfuscateFlowParameter = new ASTObfuscateFlowParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateFlowParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE_FLOW);
            this.jj_consume_token(ASSIGN);
            this.FlowObfuscationType();
            this.jjtree.closeNodeScope(aSTObfuscateFlowParameter);
            bl = false;
            aSTObfuscateFlowParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateFlowParameter);
            }
        }
    }

    public boolean jj_2_16() {
        this.jj_la = 2;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_15();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(25, 2);
        }

        return true;
    }

    public boolean jj_3R_173() {
        return this.jj_scan_token(ENHANCED);
    }

    public final void AllowMethodParameterChangesParameter() throws ZkmScriptParseException {
        ASTAllowMethodParameterChangesParameter aSTAllowMethodParameterChangesParameter = new ASTAllowMethodParameterChangesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAllowMethodParameterChangesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ALLOW_METHOD_PARAMETER_CHANGES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTAllowMethodParameterChangesParameter);
            bl = false;
            aSTAllowMethodParameterChangesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAllowMethodParameterChangesParameter);
            }
        }
    }

    public boolean jj_3R_174() {
        return this.jj_scan_token(RANDOMIZE);
    }

    public boolean jj_3R_175() {
        return this.jj_scan_token(SYNTHETIC);
    }

    public boolean jj_3R_176() {
        if (this.jj_3R_80()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_9());

        this.jj_scanpos = zkmScriptToken;
        return false;
    }

    public boolean jj_3R_177() {
        return this.jj_scan_token(BANG);
    }

    public boolean jj_3R_178() {
        return this.jj_scan_token(SEARCH);
    }

    public boolean jj_3R_179() {
        return this.jj_scan_token(37) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3_12() {
        return this.jj_scan_token(AND_AND) ? true : this.jj_3R_222();
    }

    public boolean jj_3R_180() {
        return this.jj_scan_token(PRINT) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_181() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(BANG)) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_scan_token(LPAREN);
    }

    public final ZkmScriptSimpleNode DefaultMethodParameterObfuscationExcludeInput() throws ZkmScriptParseException {
        ASTDefaultMethodParameterObfuscationExcludeInput aSTDefaultMethodParameterObfuscationExcludeInput = new ASTDefaultMethodParameterObfuscationExcludeInput();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultMethodParameterObfuscationExcludeInput);

        try {
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[63] = this.jj_gen;
            }

            switch (this.jj_nt.kind) {
                case METHOD_PARAMETER_OBFUSCATION_EXCLUDE:
                    this.DefaultMethodParameterObfuscationExcludeStatement();
                    break;
                default:
                    this.jj_la1[64] = this.jj_gen;
            }

            this.jj_consume_token(EOF);
            this.jjtree.closeNodeScope(aSTDefaultMethodParameterObfuscationExcludeInput);
            bl = false;
            return aSTDefaultMethodParameterObfuscationExcludeInput;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultMethodParameterObfuscationExcludeInput);
            }
        }
    }

    public boolean jj_3R_182() {
        return this.jj_3R_75();
    }

    public boolean jj_3R_183() {
        return this.jj_scan_token(48);
    }

    public final void AssumeRuntimeVersionParameter() throws ZkmScriptParseException {
        ASTAssumeRuntimeVersionParameter aSTAssumeRuntimeVersionParameter = new ASTAssumeRuntimeVersionParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAssumeRuntimeVersionParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ASSUME_RUNTIME_VERSION);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTAssumeRuntimeVersionParameter);
            bl = false;
            aSTAssumeRuntimeVersionParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAssumeRuntimeVersionParameter);
            }
        }
    }

    public final void DeriveGroupingsFromChangeLogParameter() throws ZkmScriptParseException {
        ASTDeriveGroupingsFromChangeLogParameter aSTDeriveGroupingsFromChangeLogParameter = new ASTDeriveGroupingsFromChangeLogParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeriveGroupingsFromChangeLogParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DERIVE_GROUPINGS_FROM_INPUT_CHANGE_LOG);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeriveGroupingsFromChangeLogParameter);
            bl = false;
            aSTDeriveGroupingsFromChangeLogParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeriveGroupingsFromChangeLogParameter);
            }
        }
    }

    public final void MethodParameterChangesType() throws ZkmScriptParseException {
        ASTMethodParameterChangesType aSTMethodParameterChangesType = new ASTMethodParameterChangesType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterChangesType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTMethodParameterChangesType);
                    bl = false;
                    aSTMethodParameterChangesType.setValue(zkmScriptToken3.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTMethodParameterChangesType);
                    bl = false;
                    aSTMethodParameterChangesType.setValue(zkmScriptToken2.image);
                    break;
                case RANDOM:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(RANDOM);
                    this.jjtree.closeNodeScope(aSTMethodParameterChangesType);
                    bl = false;
                    aSTMethodParameterChangesType.setValue(zkmScriptToken1.image);
                    break;
                case FLOW_OBFUSCATE:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(FLOW_OBFUSCATE);
                    this.jjtree.closeNodeScope(aSTMethodParameterChangesType);
                    bl = false;
                    aSTMethodParameterChangesType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[123] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterChangesType);
            }
        }
    }

    public boolean jj_3R_184() {
        return this.jj_scan_token(RANDOM);
    }

    public boolean jj_3R_185() {
        return this.jj_scan_token(PACKAGE);
    }

    public boolean jj_3R_186() {
        return this.jj_scan_token(DELETE);
    }

    public boolean jj_2_17() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_182();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(22, Integer.MAX_VALUE);
        }

        return true;
    }

    




    public final void BracketedClassSpecifier() throws ZkmScriptParseException {
        ASTBracketedClassSpecifier aSTBracketedClassSpecifier = new ASTBracketedClassSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedClassSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedClassSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[156] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndClassSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[157] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndClassSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedClassSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedClassSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_187() {
        return this.jj_scan_token(TRANSIENT);
    }

    public boolean jj_3R_188() {
        return this.jj_scan_token(37);
    }

    public boolean jj_3R_189() {
        return this.jj_scan_token(KEEP);
    }

    public boolean jj_2_18() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_2();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(12, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_190() {
        return this.jj_scan_token(CLASSPATH);
    }

    public final void SkipArchivePath() throws ZkmScriptParseException {
        ASTSkipArchivePath aSTSkipArchivePath = new ASTSkipArchivePath();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTSkipArchivePath);
        try {
            this.jj_consume_token(16);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTSkipArchivePath);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTSkipArchivePath);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_191() {
        return this.jj_scan_token(48);
    }

    public boolean jj_3_13() {
        if (this.jj_3_8()) {
            return true;
        } else if (this.jj_scan_token(LPAREN)) {
            return true;
        } else {
            return this.jj_3R_44() ? true : this.jj_scan_token(ASSIGN);
        }
    }

    public boolean jj_3R_192() {
        if (this.jj_3R_32()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(RBRACE)) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_scan_token(AND_AND)) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_scan_token(OR)) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_scan_token(RPAREN)) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_scan_token(AND)) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_scan_token(SEMICOLON)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean jj_3R_193() {
        return this.jj_scan_token(EXECUTE);
    }

    public boolean jj_3R_194() {
        return this.jj_scan_token(ENUM);
    }

    public final void ObfuscateReferenceStructuresType() throws ZkmScriptParseException {
        ASTObfuscateReferenceStructuresType aSTObfuscateReferenceStructuresType = new ASTObfuscateReferenceStructuresType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateReferenceStructuresType);

        try {
            switch (this.jj_nt.kind) {
                case IN_SPECIAL_CLASS:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(IN_SPECIAL_CLASS);
                    this.jjtree.closeNodeScope(aSTObfuscateReferenceStructuresType);
                    bl = false;
                    aSTObfuscateReferenceStructuresType.setValue(zkmScriptToken1.image);
                    break;
                case IN_REFERENCING_CLASSES:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(IN_REFERENCING_CLASSES);
                    this.jjtree.closeNodeScope(aSTObfuscateReferenceStructuresType);
                    bl = false;
                    aSTObfuscateReferenceStructuresType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[125] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateReferenceStructuresType);
            }
        }
    }

    public boolean jj_2_19() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3_13();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(17, Integer.MAX_VALUE);
        }

        return true;
    }

    




    public final void AndMethodSpecifier() throws ZkmScriptParseException {
        ASTAndMethodSpecifier aSTAndMethodSpecifier = new ASTAndMethodSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndMethodSpecifier);
        try {
            this.ComplexMethodSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[176] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexMethodSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndMethodSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndMethodSpecifier);
                throw throwable2;
            }
        }
    }

    public final ZkmScriptSimpleNode MethodParameterChangesIncludeStatement() throws ZkmScriptParseException {
        ASTMethodParameterChangesIncludeStatement aSTMethodParameterChangesIncludeStatement = new ASTMethodParameterChangesIncludeStatement(37);
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterChangesIncludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_CHANGES_INCLUDE);
            this.currentStatementKind = 192;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[48] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTMethodParameterChangesIncludeStatement);
                        bl = false;
                        aSTMethodParameterChangesIncludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTMethodParameterChangesIncludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterChangesIncludeStatement);
            }
        }
    }

    public boolean jj_3R_195() {
        return this.jj_scan_token(BRIDGE);
    }

    public final ZkmScriptSimpleNode TrimUnexcludeStatement() throws ZkmScriptParseException {
        ASTTrimUnexcludeStatement aSTTrimUnexcludeStatement = new ASTTrimUnexcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTTrimUnexcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(TRIM_UNEXCLUDE);
            this.currentStatementKind = 100;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[33] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTTrimUnexcludeStatement);
                        bl = false;
                        aSTTrimUnexcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTTrimUnexcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTTrimUnexcludeStatement);
            }
        }
    }

    public final void MemberComplexAnnotationSpecifier() throws ZkmScriptParseException {
        ASTMemberComplexAnnotationSpecifier aSTMemberComplexAnnotationSpecifier = new ASTMemberComplexAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMemberComplexAnnotationSpecifier);
        try {
            this.ComplexAnnotationSpecifier();
            this.jjtree.closeNodeScope(aSTMemberComplexAnnotationSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTMemberComplexAnnotationSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_196() {
        return this.jj_scan_token(SEARCH);
    }

    public boolean jj_3R_197() {
        return this.jj_scan_token(48);
    }

    public final void OrFileFilterComponent() throws ZkmScriptParseException {
        ASTOrFileFilterComponent aSTOrFileFilterComponent = new ASTOrFileFilterComponent();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTOrFileFilterComponent);
        try {
            this.jj_consume_token(34);
            switch (this.jj_nt.kind) {
                case 210: {
                    this.FileFilterComponent();
                    break;
                }
                case 25: {
                    this.NegatedFileFilterComponent();
                    break;
                }
                default: {
                    this.jj_la1[15] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTOrFileFilterComponent);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTOrFileFilterComponent);
                }
                throw throwable2;
            }
        }
    }

    public final ZkmScriptSimpleNode SingleObfuscateReferencesIncludeInput() throws ZkmScriptParseException {
        ASTSingleObfuscateReferencesIncludeInput aSTSingleObfuscateReferencesIncludeInput = new ASTSingleObfuscateReferencesIncludeInput();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTSingleObfuscateReferencesIncludeInput);

        try {
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[66] = this.jj_gen;
            }

            switch (this.jj_nt.kind) {
                case OBFUSCATE_REFERENCES_INCLUDE:
                    this.ObfuscateReferencesIncludeStatement();
                    break;
                default:
                    this.jj_la1[67] = this.jj_gen;
            }

            this.jj_consume_token(EOF);
            this.jjtree.closeNodeScope(aSTSingleObfuscateReferencesIncludeInput);
            bl = false;
            return aSTSingleObfuscateReferencesIncludeInput;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTSingleObfuscateReferencesIncludeInput);
            }
        }
    }

    public boolean jj_3R_198() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_39()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_278()) {
                return true;
            }
        }

        return false;
    }

    public boolean jj_3R_199() {
        return this.jj_scan_token(43);
    }

    public final ZkmScriptSimpleNode SingleRenameFilterParameter() throws ZkmScriptParseException {
        ASTSingleRenameFilterParameter aSTSingleRenameFilterParameter = new ASTSingleRenameFilterParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTSingleRenameFilterParameter);

        try {
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[68] = this.jj_gen;
            }

            this.RenameFilterParameter();
            this.jj_consume_token(SEMICOLON);
            this.jj_consume_token(EOF);
            this.jjtree.closeNodeScope(aSTSingleRenameFilterParameter);
            bl = false;
            return aSTSingleRenameFilterParameter;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTSingleRenameFilterParameter);
            }
        }
    }

    public final void ResetRemoveMethodCallsStatement() throws ZkmScriptParseException {
        ASTResetRemoveMethodCallsStatement aSTResetRemoveMethodCallsStatement = new ASTResetRemoveMethodCallsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetRemoveMethodCallsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_REMOVE_METHOD_CALLS);
            this.currentStatementKind = 158;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetRemoveMethodCallsStatement);
            bl = false;
            aSTResetRemoveMethodCallsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetRemoveMethodCallsStatement);
            }
        }
    }

    public final void LinkMethodSignature() throws ZkmScriptParseException {
        ASTLinkMethodSignature aSTLinkMethodSignature = new ASTLinkMethodSignature();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLinkMethodSignature);
        try {
            this.LinkMethodName();
            this.jj_consume_token(28);
            switch (this.jj_nt.kind) {
                case 23:
                case 25:
                case 27:
                case 28:
                case 32:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213: {
                    this.MethodArguments();
                    break;
                }
                default: {
                    this.jj_la1[171] = this.jj_gen;
                }
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTLinkMethodSignature);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTLinkMethodSignature);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_200() {
        return this.jj_scan_token(GC);
    }

    public final void NegatedMemberModifier() throws ZkmScriptParseException {
        ASTNegatedMemberModifier aSTNegatedMemberModifier = new ASTNegatedMemberModifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNegatedMemberModifier);
        try {
            this.jj_consume_token(25);
            this.MemberModifierHelper();
            this.jjtree.closeNodeScope(aSTNegatedMemberModifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTNegatedMemberModifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_scan_token(int ba) {
        if (this.jj_scanpos == this.jj_lastpos) {
            this.jj_la--;
            if (this.jj_scanpos.next == null) {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }

        if (this.jj_rescan) {
            int bb = 0;

            ZkmScriptToken zkmScriptToken;
            for (zkmScriptToken = this.token; zkmScriptToken != null && zkmScriptToken != this.jj_scanpos; zkmScriptToken = zkmScriptToken.next) {
                bb++;
            }

            if (zkmScriptToken != null) {
                this.jj_add_error_token(ba, bb);
            }
        }

        if (this.jj_scanpos.kind != ba) {
            return true;
        } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        } else {
            return false;
        }
    }

    public final void SaveAllParameter() throws ZkmScriptParseException {
        ASTSaveAllParameter aSTSaveAllParameter = new ASTSaveAllParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTSaveAllParameter);
        try {
            switch (this.jj_nt.kind) {
                case 132: {
                    this.ArchiveCompressionParameter();
                    break;
                }
                case 157: {
                    this.DeleteEmptyDirectoriesParameter();
                    break;
                }
                case 129: {
                    this.DeleteXMLCommentsParameter();
                    break;
                }
                case 120: {
                    this.LastModifiedTime();
                    break;
                }
                default: {
                    this.jj_la1[75] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTSaveAllParameter);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTSaveAllParameter);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3_14() {
        return this.jj_scan_token(OR) ? true : this.jj_3R_176();
    }

    public boolean jj_3R_201() {
        return this.jj_scan_token(EXCLUDE);
    }

    public boolean jj_3R_202() {
        return this.jj_scan_token(STAR);
    }

    




    public final void BracketedFieldSpecifier() throws ZkmScriptParseException {
        ASTBracketedFieldSpecifier aSTBracketedFieldSpecifier = new ASTBracketedFieldSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedFieldSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedFieldSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[162] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndFieldSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[163] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndFieldSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedFieldSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedFieldSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_203() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public final void MakeClassesPublicParameter() throws ZkmScriptParseException {
        ASTMakeClassesPublicParameter aSTMakeClassesPublicParameter = new ASTMakeClassesPublicParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMakeClassesPublicParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(MAKE_CLASSES_PUBLIC);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTMakeClassesPublicParameter);
            bl = false;
            aSTMakeClassesPublicParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMakeClassesPublicParameter);
            }
        }
    }

    public final ZkmScriptSimpleNode DefaultExcludeInput() throws ZkmScriptParseException {
        ASTDefaultExcludeInput aSTDefaultExcludeInput = new ASTDefaultExcludeInput();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultExcludeInput);

        try {
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[54] = this.jj_gen;
            }

            switch (this.jj_nt.kind) {
                case EXCLUDE:
                    this.DefaultExcludeStatement();
                    break;
                default:
                    this.jj_la1[55] = this.jj_gen;
            }

            this.jj_consume_token(EOF);
            this.jjtree.closeNodeScope(aSTDefaultExcludeInput);
            bl = false;
            return aSTDefaultExcludeInput;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultExcludeInput);
            }
        }
    }

    public boolean jj_3_15() {
        return this.jj_scan_token(DOT) ? true : this.jj_3R_205();
    }

    public final void LocalVariablesParameter() throws ZkmScriptParseException {
        ASTLocalVariablesParameter aSTLocalVariablesParameter = new ASTLocalVariablesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLocalVariablesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(LOCAL_VARIABLES);
            this.jj_consume_token(ASSIGN);
            this.LocalVariableParameterType();
            this.jjtree.closeNodeScope(aSTLocalVariablesParameter);
            bl = false;
            aSTLocalVariablesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLocalVariablesParameter);
            }
        }
    }

    public boolean jj_3R_204() {
        return this.jj_scan_token(NATIVE);
    }

    public boolean jj_3R_205() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_117()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_264()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_22()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_221()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_266()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_56()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_73()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_212()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_136()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_124()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_243()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_252()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_70()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_67()) {
                                                                this.jj_scanpos = zkmScriptToken;
                                                                if (this.jj_3R_103()) {
                                                                    this.jj_scanpos = zkmScriptToken;
                                                                    if (this.jj_3R_61()) {
                                                                        this.jj_scanpos = zkmScriptToken;
                                                                        if (this.jj_3R_163()) {
                                                                            this.jj_scanpos = zkmScriptToken;
                                                                            if (this.jj_3R_208()) {
                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                if (this.jj_3R_262()) {
                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                    if (this.jj_3R_121()) {
                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                        if (this.jj_3R_130()) {
                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                            if (this.jj_3R_64()) {
                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                if (this.jj_3R_79()) {
                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                    if (this.jj_3R_251()) {
                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                        if (this.jj_3R_91()) {
                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                            if (this.jj_3R_206()) {
                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                if (this.jj_3R_48()) {
                                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                                    if (this.jj_3R_219()) {
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

        return false;
    }

    public boolean jj_3R_206() {
        return this.jj_scan_token(ENHANCED);
    }

    public boolean jj_3R_207() {
        return this.jj_scan_token(OBFUSCATE);
    }

    public final void ResetIntegerEncryptionExclusionsStatement() throws ZkmScriptParseException {
        ASTResetIntegerEncryptionExclusionsStatement aSTResetIntegerEncryptionExclusionsStatement = new ASTResetIntegerEncryptionExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetIntegerEncryptionExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_INTEGER_ENCRYPTION_EXCLUSIONS);
            this.currentStatementKind = 198;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetIntegerEncryptionExclusionsStatement);
            bl = false;
            aSTResetIntegerEncryptionExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetIntegerEncryptionExclusionsStatement);
            }
        }
    }

    public boolean jj_3R_208() {
        return this.jj_scan_token(OBFUSCATE);
    }

    public boolean jj_3R_209() {
        return this.jj_scan_token(ANNOTATION);
    }

    public final void ComplexPackageSpecifier() throws ZkmScriptParseException {
        ASTComplexPackageSpecifier aSTComplexPackageSpecifier = new ASTComplexPackageSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexPackageSpecifier);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 28: {
                    this.BracketedPackageSpecifier();
                    break;
                }
                case 23:
                case 36:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 67:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213:
                case 214: {
                    this.PackageName();
                    break;
                }
                default: {
                    this.jj_la1[143] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            switch (this.jj_nt.kind) {
                case 21: {
                    this.jj_consume_token(21);
                    aSTComplexPackageSpecifier.setIncludesSubpackages();
                    break;
                }
                default: {
                    this.jj_la1[144] = this.jj_gen;
                }
            }
            switch (this.jj_nt.kind) {
                case 26: {
                    this.jj_consume_token(26);
                    aSTComplexPackageSpecifier.setExcludesPackageName();
                    break;
                }
                default: {
                    this.jj_la1[145] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(aSTComplexPackageSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexPackageSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public final void ResetObfuscateExceptionsExclusionsStatement() throws ZkmScriptParseException {
        ASTResetObfuscateExceptionsExclusionsStatement aSTResetObfuscateExceptionsExclusionsStatement = new ASTResetObfuscateExceptionsExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetObfuscateExceptionsExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_OBFUSCATE_EXCEPTIONS_EXCLUSIONS);
            this.currentStatementKind = 204;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetObfuscateExceptionsExclusionsStatement);
            bl = false;
            aSTResetObfuscateExceptionsExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetObfuscateExceptionsExclusionsStatement);
            }
        }
    }

    public boolean jj_3R_210() {
        return this.jj_scan_token(PROTECTED);
    }

    public final void BooleanOrIfNameNotObfucated() throws ZkmScriptParseException {
        ASTBooleanOrIfNameNotObfucated aSTBooleanOrIfNameNotObfucated = new ASTBooleanOrIfNameNotObfucated();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBooleanOrIfNameNotObfucated);

        try {
            switch (this.jj_nt.kind) {
                case 41:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(41);
                    this.jjtree.closeNodeScope(aSTBooleanOrIfNameNotObfucated);
                    bl = false;
                    aSTBooleanOrIfNameNotObfucated.setValue(zkmScriptToken2.image);
                    break;
                case FALSE:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(FALSE);
                    this.jjtree.closeNodeScope(aSTBooleanOrIfNameNotObfucated);
                    bl = false;
                    aSTBooleanOrIfNameNotObfucated.setValue(zkmScriptToken1.image);
                    break;
                case IF_NAME_NOT_OBFUSCATED:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(IF_NAME_NOT_OBFUSCATED);
                    this.jjtree.closeNodeScope(aSTBooleanOrIfNameNotObfucated);
                    bl = false;
                    aSTBooleanOrIfNameNotObfucated.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[185] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTBooleanOrIfNameNotObfucated);
            }
        }
    }

    public boolean jj_3R_211() {
        return this.jj_scan_token(EXCLUDE);
    }

    public boolean jj_3R_212() {
        return this.jj_scan_token(EXCLUDE);
    }

    




    public final void Grouping() throws ZkmScriptParseException {
        ASTGrouping aSTGrouping = new ASTGrouping();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTGrouping);
        try {
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 38: {
                        break;
                    }
                    default: {
                        this.jj_la1[53] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(38);
                this.RenameFilterParameter();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTGrouping);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTGrouping);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_213() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_140()) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_133();
    }

    public final void ResetTrimExclusionsStatement() throws ZkmScriptParseException {
        ASTResetTrimExclusionsStatement aSTResetTrimExclusionsStatement = new ASTResetTrimExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetTrimExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_TRIM_EXCLUSIONS);
            this.currentStatementKind = 138;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetTrimExclusionsStatement);
            bl = false;
            aSTResetTrimExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetTrimExclusionsStatement);
            }
        }
    }

    public boolean jj_3R_214() {
        return this.jj_scan_token(FINAL);
    }

    public boolean jj_3R_215() {
        return this.jj_3R_128();
    }

    public boolean jj_2_20() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_181();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(18, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_216() {
        return this.jj_scan_token(NORMAL);
    }

    public boolean jj_3R_217() {
        return this.jj_scan_token(BANG);
    }

    public boolean jj_3R_218() {
        return this.jj_scan_token(RANDOMIZE) ? true : this.jj_scan_token(DOT);
    }

    public static void jj_la1_init_2() {
        jj_la1_2 = new int[]{
                0,
                1078165539,
                1078165539,
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
                82519465,
                0,
                0,
                268435456,
                0,
                268435456,
                268435456,
                0,
                268435456,
                0,
                0,
                268435456,
                0,
                268435456,
                268435456,
                0,
                268435456,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
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
                0,
                0,
                0,
                -2145370112,
                0,
                0,
                0,
                0,
                0,
                0,
                4,
                134217728,
                1321240,
                81994913,
                83308473,
                0,
                81994921,
                4,
                134217728,
                0,
                0,
                8921352,
                4,
                134217728,
                1321240,
                81994913,
                83308473,
                82519465,
                82519465,
                0,
                -2145370112,
                0,
                0,
                0,
                0,
                4096,
                8921352,
                8921352,
                1321240,
                83308473,
                82519465,
                83308473,
                0,
                0,
                0,
                1321240,
                81994913,
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
                67108864,
                0,
                67109888,
                67108864,
                0,
                128,
                131072,
                537001984,
                0,
                0,
                0,
                0,
                81994913,
                0,
                0,
                0,
                0,
                81994921,
                0,
                0,
                0,
                0,
                0,
                81994913,
                81994921,
                0,
                79897761,
                81994913,
                0,
                0,
                0,
                0,
                0,
                81994913,
                81994913,
                81994913,
                0,
                0,
                0,
                81994913,
                81994913,
                0,
                81994913,
                0,
                0,
                81994913,
                0,
                81995425,
                0,
                0,
                0,
                81995425,
                0,
                0,
                81994913,
                0,
                0,
                81994913,
                0,
                0,
                0
        };
    }

    public final void ExecuteStatement() throws ZkmScriptParseException {
        ASTExecuteStatement aSTExecuteStatement = new ASTExecuteStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExecuteStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXECUTE);
            this.currentStatementKind = 69;
            this.StringLiteral();
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTExecuteStatement);
            bl = false;
            aSTExecuteStatement.setLineNumber(zkmScriptToken.endLine);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExecuteStatement);
            }
        }
    }

    public boolean jj_3R_219() {
        return this.jj_scan_token(ASCII);
    }

    public boolean jj_3R_220() {
        return this.jj_scan_token(CLASSPATH);
    }

    public boolean jj_3R_221() {
        return this.jj_scan_token(OPEN);
    }

    public boolean jj_3R_222() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_27()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_15()) {
                return true;
            }
        }

        return false;
    }

    public final ZkmScriptSimpleNode StringEncryptionExcludeStatement() throws ZkmScriptParseException {
        ASTStringEncryptionExcludeStatement aSTStringEncryptionExcludeStatement = new ASTStringEncryptionExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTStringEncryptionExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(STRING_ENCRYPTION_EXCLUDE);
            this.currentStatementKind = 162;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[22] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTStringEncryptionExcludeStatement);
                        bl = false;
                        aSTStringEncryptionExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTStringEncryptionExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTStringEncryptionExcludeStatement);
            }
        }
    }

    public boolean jj_3R_223() {
        return this.jj_scan_token(48);
    }

    public boolean jj_3R_224() {
        return this.jj_scan_token(ABSTRACT);
    }

    public boolean jj_3R_225() {
        return this.jj_scan_token(ASCII);
    }

    public boolean jj_3R_226() {
        return this.jj_scan_token(ENUM);
    }

    public boolean jj_2_21() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_1();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(27, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_2_22() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_6();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(2, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_227() {
        return this.jj_scan_token(GROUPINGS);
    }

    public boolean jj_3R_228() {
        return this.jj_scan_token(SEARCH) ? true : this.jj_scan_token(DOT);
    }

    public final ZkmScriptSimpleNode MethodParameterObfuscationIncludeStatement() throws ZkmScriptParseException {
        ASTMethodParameterObfuscationIncludeStatement aSTMethodParameterObfuscationIncludeStatement = new ASTMethodParameterObfuscationIncludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterObfuscationIncludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_OBFUSCATION_INCLUDE);
            this.currentStatementKind = 199;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[50] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTMethodParameterObfuscationIncludeStatement);
                        bl = false;
                        aSTMethodParameterObfuscationIncludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTMethodParameterObfuscationIncludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterObfuscationIncludeStatement);
            }
        }
    }

    public boolean jj_3R_229() {
        return this.jj_scan_token(OBFUSCATE);
    }

    public boolean jj_3R_230() {
        return this.jj_scan_token(ASCII);
    }

    public boolean jj_3R_231() {
        return this.jj_scan_token(48) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3_16() {
        return this.jj_3R_53() ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_2_23() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_192();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(26, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_232() {
        return this.jj_scan_token(CARET);
    }

    public boolean jj_3R_233() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_217()) {
            this.jj_scanpos = zkmScriptToken;
        }

        if (this.jj_scan_token(LPAREN)) {
            return true;
        }

        if (this.jj_3R_123()) {
            return true;
        }

        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_3());

        this.jj_scanpos = zkmScriptToken;
        return this.jj_scan_token(RPAREN);
    }

    public boolean jj_3R_234() {
        return this.jj_scan_token(RANDOMIZE);
    }

    public boolean jj_3R_235() {
        return this.jj_scan_token(BRIDGE);
    }

    public final ZkmScriptSimpleNode DefaultMethodParameterObfuscationExcludeStatement() throws ZkmScriptParseException {
        ASTDefaultMethodParameterObfuscationExcludeStatement aSTDefaultMethodParameterObfuscationExcludeStatement = new ASTDefaultMethodParameterObfuscationExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultMethodParameterObfuscationExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_OBFUSCATION_EXCLUDE);
            this.currentStatementKind = 200;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[65] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTDefaultMethodParameterObfuscationExcludeStatement);
                        bl = false;
                        aSTDefaultMethodParameterObfuscationExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTDefaultMethodParameterObfuscationExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultMethodParameterObfuscationExcludeStatement);
            }
        }
    }

    public final void CollapsePackages() throws ZkmScriptParseException {
        ASTCollapsePackages aSTCollapsePackages = new ASTCollapsePackages();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTCollapsePackages);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(COLLAPSE_PACKAGES_WITH_DEFAULT);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            aSTCollapsePackages.setParameterName(zkmScriptToken.image);
            this.jjtree.closeNodeScope(aSTCollapsePackages);
            bl = false;
            aSTCollapsePackages.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTCollapsePackages);
            }
        }
    }

    public final void NewNameCharactersParameter() throws ZkmScriptParseException {
        ASTNewNameCharactersParameter aSTNewNameCharactersParameter = new ASTNewNameCharactersParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNewNameCharactersParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NEW_NAME_CHARACTERS);
            this.jj_consume_token(ASSIGN);
            this.CharacterType();
            this.jjtree.closeNodeScope(aSTNewNameCharactersParameter);
            bl = false;
            aSTNewNameCharactersParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTNewNameCharactersParameter);
            }
        }
    }

    public boolean jj_3R_236() {
        return this.jj_scan_token(WILDCARD_NAME);
    }

    public boolean jj_2_24() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_270();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(20, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_237() {
        return this.jj_3R_280();
    }

    public boolean jj_3R_238() {
        return this.jj_scan_token(90);
    }

    public final void FlowObfuscationType() throws ZkmScriptParseException {
        ASTFlowObfuscationType aSTFlowObfuscationType = new ASTFlowObfuscationType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFlowObfuscationType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTFlowObfuscationType);
                    bl = false;
                    aSTFlowObfuscationType.setValue(zkmScriptToken4.image);
                    break;
                case LIGHT:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(LIGHT);
                    this.jjtree.closeNodeScope(aSTFlowObfuscationType);
                    bl = false;
                    aSTFlowObfuscationType.setValue(zkmScriptToken3.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTFlowObfuscationType);
                    bl = false;
                    aSTFlowObfuscationType.setValue(zkmScriptToken2.image);
                    break;
                case 90:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(90);
                    this.jjtree.closeNodeScope(aSTFlowObfuscationType);
                    bl = false;
                    aSTFlowObfuscationType.setValue(zkmScriptToken1.image);
                    break;
                case EXTRA_AGGRESSIVE:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXTRA_AGGRESSIVE);
                    this.jjtree.closeNodeScope(aSTFlowObfuscationType);
                    bl = false;
                    aSTFlowObfuscationType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[126] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTFlowObfuscationType);
            }
        }
    }

    public final ZkmScriptSimpleNode DefaultExcludeStatement() throws ZkmScriptParseException {
        ASTDefaultExcludeStatement aSTDefaultExcludeStatement = new ASTDefaultExcludeStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultExcludeStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXCLUDE);
            this.currentStatementKind = 64;
            this.RenameFilterParameter();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.RenameFilterParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[56] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTDefaultExcludeStatement);
                        bl = false;
                        aSTDefaultExcludeStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTDefaultExcludeStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultExcludeStatement);
            }
        }
    }

    public final void MethodParameterChangePackageParameter() throws ZkmScriptParseException {
        ASTMethodParameterChangePackageParameter aSTMethodParameterChangePackageParameter = new ASTMethodParameterChangePackageParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParameterChangePackageParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETER_CHANGES_PACKAGE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTMethodParameterChangePackageParameter);
            bl = false;
            aSTMethodParameterChangePackageParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParameterChangePackageParameter);
            }
        }
    }

    public final void ResetMethodParameterObfuscationExclusionsStatement() throws ZkmScriptParseException {
        ASTResetMethodParameterObfuscationExclusionsStatement aSTResetMethodParameterObfuscationExclusionsStatement = new ASTResetMethodParameterObfuscationExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetMethodParameterObfuscationExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_METHOD_PARAMETER_OBFUSCATION_EXCLUSIONS);
            this.currentStatementKind = 207;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetMethodParameterObfuscationExclusionsStatement);
            bl = false;
            aSTResetMethodParameterObfuscationExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetMethodParameterObfuscationExclusionsStatement);
            }
        }
    }

    public boolean jj_2_25() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_11();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(5, Integer.MAX_VALUE);
        }

        return true;
    }

    public final void AggressiveOverloadParameter() throws ZkmScriptParseException {
        ASTAggressiveOverloadParameter aSTAggressiveOverloadParameter = new ASTAggressiveOverloadParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAggressiveOverloadParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(AGGRESSIVE_METHOD_RENAMING);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTAggressiveOverloadParameter);
            bl = false;
            aSTAggressiveOverloadParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAggressiveOverloadParameter);
            }
        }
    }

    public final void ObfuscateParametersType() throws ZkmScriptParseException {
        ASTObfuscateParametersType aSTObfuscateParametersType = new ASTObfuscateParametersType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateParametersType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTObfuscateParametersType);
                    bl = false;
                    aSTObfuscateParametersType.setValue(zkmScriptToken1.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTObfuscateParametersType);
                    bl = false;
                    aSTObfuscateParametersType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[127] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateParametersType);
            }
        }
    }

    public boolean jj_3R_239() {
        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_16());

        this.jj_scanpos = zkmScriptToken;
        return this.jj_3R_53() ? true : this.jj_scan_token(SLASH);
    }

    public boolean jj_3R_240() {
        if (this.jj_3R_80()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(AND)) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_scan_token(SEMICOLON)) {
                return true;
            }
        }

        return false;
    }

    public boolean jj_3R_241() {
        return this.jj_scan_token(37);
    }

    public boolean jj_3R_242() {
        return this.jj_scan_token(CLASSPATH);
    }

    public boolean jj_3R_243() {
        return this.jj_scan_token(SCRAMBLE);
    }

    public final ZkmScriptSimpleNode DefaultTrimExcludeInput() throws ZkmScriptParseException {
        ASTDefaultTrimExcludeInput aSTDefaultTrimExcludeInput = new ASTDefaultTrimExcludeInput();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDefaultTrimExcludeInput);

        try {
            switch (this.jj_nt.kind) {
                case BOM:
                    this.jj_consume_token(BOM);
                    break;
                default:
                    this.jj_la1[57] = this.jj_gen;
            }

            switch (this.jj_nt.kind) {
                case TRIM_EXCLUDE:
                    this.DefaultTrimExcludeStatement();
                    break;
                default:
                    this.jj_la1[58] = this.jj_gen;
            }

            this.jj_consume_token(EOF);
            this.jjtree.closeNodeScope(aSTDefaultTrimExcludeInput);
            bl = false;
            return aSTDefaultTrimExcludeInput;
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDefaultTrimExcludeInput);
            }
        }
    }

    public boolean jj_3R_244() {
        return this.jj_scan_token(TRIM);
    }

    




    public final void BracketedAnnotationSpecifier() throws ZkmScriptParseException {
        ASTBracketedAnnotationSpecifier aSTBracketedAnnotationSpecifier = new ASTBracketedAnnotationSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedAnnotationSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedAnnotationSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[135] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndAnnotationSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[136] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndAnnotationSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedAnnotationSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedAnnotationSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_2_26() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_141();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(0, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_245() {
        return this.jj_3R_222();
    }

    public final void ClassModifier() throws ZkmScriptParseException {
        ZkmScriptASTClassModifier zkmScriptASTClassModifier = new ZkmScriptASTClassModifier();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTClassModifier);

        try {
            switch (this.jj_nt.kind) {
                case ENUM:
                    ZkmScriptToken zkmScriptToken7 = this.jj_consume_token(ENUM);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken7.image);
                    break;
                case FINAL:
                    ZkmScriptToken zkmScriptToken6 = this.jj_consume_token(FINAL);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken6.image);
                    break;
                case PUBLIC:
                    ZkmScriptToken zkmScriptToken5 = this.jj_consume_token(PUBLIC);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken5.image);
                    break;
                case PACKAGE:
                    ZkmScriptToken zkmScriptToken4 = this.jj_consume_token(PACKAGE);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken4.image);
                    break;
                case ABSTRACT:
                    ZkmScriptToken zkmScriptToken3 = this.jj_consume_token(ABSTRACT);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken3.image);
                    break;
                case SYNTHETIC:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(SYNTHETIC);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken2.image);
                    break;
                case INTERFACE:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(INTERFACE);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken1.image);
                    break;
                case ANNOTATION:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(ANNOTATION);
                    this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
                    bl = false;
                    zkmScriptASTClassModifier.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[105] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(zkmScriptASTClassModifier);
            }
        }
    }

    public boolean jj_3_17() {
        return this.jj_scan_token(AND_AND) ? true : this.jj_3R_198();
    }

    public final void NTStatement() throws ZkmScriptParseException {
        ASTNTStatement aSTNTStatement = new ASTNTStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTNTStatement);
        try {
            switch (this.jj_nt.kind) {
                case 161: {
                    this.IgnoreMissingReferencesStatement();
                    break;
                }
                case 39: {
                    this.LoadStatement();
                    break;
                }
                case 64: {
                    this.ExcludeStatement();
                    break;
                }
                case 79: {
                    this.UnexcludeStatement();
                    break;
                }
                case 144: {
                    this.ObfuscateFlowExcludeStatement();
                    break;
                }
                case 154: {
                    this.ObfuscateFlowUnexcludeStatement();
                    break;
                }
                case 173: {
                    this.ObfuscateExceptionsExcludeStatement();
                    break;
                }
                case 186: {
                    this.ObfuscateExceptionsUnexcludeStatement();
                    break;
                }
                case 162: {
                    this.StringEncryptionExcludeStatement();
                    break;
                }
                case 170: {
                    this.StringEncryptionUnexcludeStatement();
                    break;
                }
                case 164: {
                    this.IntegerEncryptionExcludeStatement();
                    break;
                }
                case 174: {
                    this.IntegerEncryptionUnexcludeStatement();
                    break;
                }
                case 152: {
                    this.LongEncryptionExcludeStatement();
                    break;
                }
                case 159: {
                    this.LongEncryptionUnexcludeStatement();
                    break;
                }
                case 169: {
                    this.ExistingSerializedClassesStatement();
                    break;
                }
                case 98: {
                    this.FixedClassesStatement();
                    break;
                }
                case 94: {
                    this.TrimExcludeStatement();
                    break;
                }
                case 100: {
                    this.TrimUnexcludeStatement();
                    break;
                }
                case 180: {
                    this.ObfuscateReferencesIncludeStatement();
                    break;
                }
                case 181: {
                    this.ObfuscateReferencesExcludeStatement();
                    break;
                }
                case 165: {
                    this.RemoveMethodCallsIncludeStatement();
                    break;
                }
                case 166: {
                    this.RemoveMethodCallsExcludeStatement();
                    break;
                }
                case 86: {
                    this.GroupingsStatement();
                    break;
                }
                case 127: {
                    this.RemoveMethodCallsStatement();
                    break;
                }
                case 192: {
                    this.MethodParameterChangesIncludeStatement();
                    break;
                }
                case 193: {
                    this.MethodParameterChangesExcludeStatement();
                    break;
                }
                case 199: {
                    this.MethodParameterObfuscationIncludeStatement();
                    break;
                }
                case 200: {
                    this.MethodParameterObfuscationExcludeStatement();
                    break;
                }
                case 45: {
                    this.TrimStatement();
                    break;
                }
                case 80: {
                    this.ClasspathStatement();
                    break;
                }
                case 81: {
                    this.ObfuscateStatement();
                    break;
                }
                case 146: {
                    this.AccessedByReflectionStatement();
                    break;
                }
                case 182: {
                    this.AccessedByReflectionExcludeStatement();
                    break;
                }
                case 167: {
                    this.ClassInitializationOrderStatement();
                    break;
                }
                case 65: {
                    this.SaveAllStatement();
                    break;
                }
                case 111: {
                    this.ResetExclusionsStatement();
                    break;
                }
                case 189: {
                    this.ResetObfuscateFlowExclusionsStatement();
                    break;
                }
                case 204: {
                    this.ResetObfuscateExceptionsExclusionsStatement();
                    break;
                }
                case 197: {
                    this.ResetStringEncryptionExclusionsStatement();
                    break;
                }
                case 198: {
                    this.ResetIntegerEncryptionExclusionsStatement();
                    break;
                }
                case 190: {
                    this.ResetLongEncryptionExclusionsStatement();
                    break;
                }
                case 195: {
                    this.ResetExistingSerializedClassesStatement();
                    break;
                }
                case 126: {
                    this.ResetFixedClassesStatement();
                    break;
                }
                case 107: {
                    this.ResetGroupingsStatement();
                    break;
                }
                case 187: {
                    this.ResetIgnoreMissingReferencesStatement();
                    break;
                }
                case 138: {
                    this.ResetTrimExclusionsStatement();
                    break;
                }
                case 171: {
                    this.ResetAccessedByReflectionStatement();
                    break;
                }
                case 203: {
                    this.ResetObfuscateReferenceExclusionsStatement();
                    break;
                }
                case 158: {
                    this.ResetRemoveMethodCallsStatement();
                    break;
                }
                case 206: {
                    this.ResetMethodParameterChangesExclusionsStatement();
                    break;
                }
                case 207: {
                    this.ResetMethodParameterObfuscationExclusionsStatement();
                    break;
                }
                case 191: {
                    this.ResetClassInitializationOrderStatement();
                    break;
                }
                case 36: {
                    this.GarbageCollectStatement();
                    break;
                }
                case 69: {
                    this.ExecuteStatement();
                    break;
                }
                case 46: {
                    this.PrintStatement();
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            this.jjtree.closeNodeScope(aSTNTStatement);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTNTStatement);
                }
                throw throwable2;
            }
        }
    }

    public final void PreverifyParameter() throws ZkmScriptParseException {
        ASTPreverifyParameter aSTPreverifyParameter = new ASTPreverifyParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTPreverifyParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(PREVERIFY);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTPreverifyParameter);
            bl = false;
            aSTPreverifyParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTPreverifyParameter);
            }
        }
    }

    public final void HideStaticMethodNamesParameter() throws ZkmScriptParseException {
        ASTHideStaticMethodNamesParameter aSTHideStaticMethodNamesParameter = new ASTHideStaticMethodNamesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTHideStaticMethodNamesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(HIDE_STATIC_METHOD_NAMES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTHideStaticMethodNamesParameter);
            bl = false;
            aSTHideStaticMethodNamesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTHideStaticMethodNamesParameter);
            }
        }
    }

    public boolean jj_2_27() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_245();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(9, Integer.MAX_VALUE);
        }

        return true;
    }

    public boolean jj_3R_246() {
        return this.jj_scan_token(ANNOTATION) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_247() {
        return this.jj_3R_75();
    }

    public boolean jj_3R_248() {
        return this.jj_scan_token(DELETE);
    }

    public final void ArchiveCompressionType() throws ZkmScriptParseException {
        ASTArchiveCompressionType aSTArchiveCompressionType = new ASTArchiveCompressionType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTArchiveCompressionType);

        try {
            switch (this.jj_nt.kind) {
                case 37:
                    ZkmScriptToken zkmScriptToken2 = this.jj_consume_token(37);
                    this.jjtree.closeNodeScope(aSTArchiveCompressionType);
                    bl = false;
                    aSTArchiveCompressionType.setValue(zkmScriptToken2.image);
                    break;
                case 43:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTArchiveCompressionType);
                    bl = false;
                    aSTArchiveCompressionType.setValue(zkmScriptToken1.image);
                    break;
                case AS_IS:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(AS_IS);
                    this.jjtree.closeNodeScope(aSTArchiveCompressionType);
                    bl = false;
                    aSTArchiveCompressionType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[76] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTArchiveCompressionType);
            }
        }
    }

    public boolean jj_3R_249() {
        if (this.jj_3R_247()) {
            return true;
        }

        ZkmScriptToken zkmScriptToken;
        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3R_247());

        this.jj_scanpos = zkmScriptToken;
        return false;
    }

    public final ZkmScriptSimpleNode GroupingsStatement() throws ZkmScriptParseException {
        ASTGroupingsStatement aSTGroupingsStatement = new ASTGroupingsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTGroupingsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(GROUPINGS);
            this.currentStatementKind = 86;
            ZkmScriptParser zkmScriptParser1 = this;
            byte ba = 30;

            while (true) {
                zkmScriptParser1.jj_consume_token(ba);
                this.Grouping();
                this.jj_consume_token(RBRACE);
                switch (this.jj_nt.kind) {
                    case LBRACE:
                        zkmScriptParser1 = this;
                        ba = 30;
                        break;
                    default:
                        this.jj_la1[52] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTGroupingsStatement);
                        bl = false;
                        aSTGroupingsStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTGroupingsStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTGroupingsStatement);
            }
        }
    }

    public boolean jj_3R_250() {
        return this.jj_scan_token(ENUM);
    }

    public boolean jj_3R_251() {
        return this.jj_scan_token(ANNOTATION);
    }

    public final void ClassInitializationOrderStatement() throws ZkmScriptParseException {
        ASTClassInitializationOrderStatement aSTClassInitializationOrderStatement = new ASTClassInitializationOrderStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTClassInitializationOrderStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(CLASS_INITIALIZATION_ORDER);
            this.currentStatementKind = 167;
            this.ClassInitializationOrder();
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case AND:
                        this.jj_consume_token(AND);
                        this.ClassInitializationOrder();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[78] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTClassInitializationOrderStatement);
                        bl = false;
                        aSTClassInitializationOrderStatement.setLineNumber(zkmScriptToken.endLine);
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTClassInitializationOrderStatement);
            }
        }
    }

    public boolean jj_3R_252() {
        return this.jj_scan_token(NORMAL);
    }

    public final void KeepBalancedLocksParameter() throws ZkmScriptParseException {
        ASTKeepBalancedLocksParameter aSTKeepBalancedLocksParameter = new ASTKeepBalancedLocksParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepBalancedLocksParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(KEEP_BALANCED_LOCKS);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTKeepBalancedLocksParameter);
            bl = false;
            aSTKeepBalancedLocksParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepBalancedLocksParameter);
            }
        }
    }

    public boolean jj_3_18() {
        return this.jj_3R_198() ? true : this.jj_scan_token(LPAREN);
    }

    public boolean jj_3R_253() {
        return this.jj_scan_token(ENUM);
    }

    public final void DeleteXMLCommentsParameter() throws ZkmScriptParseException {
        ASTDeleteXMLCommentsParameter aSTDeleteXMLCommentsParameter = new ASTDeleteXMLCommentsParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteXMLCommentsParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_XMLCOMMENTS);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteXMLCommentsParameter);
            bl = false;
            aSTDeleteXMLCommentsParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteXMLCommentsParameter);
            }
        }
    }

    




    public final void AndModuleSpecifier() throws ZkmScriptParseException {
        ASTAndModuleSpecifier aSTAndModuleSpecifier = new ASTAndModuleSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAndModuleSpecifier);
        try {
            this.ComplexModuleSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block7:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[142] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(35);
                this.ComplexModuleSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(aSTAndModuleSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTAndModuleSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_254() {
        return this.jj_scan_token(BRIDGE) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_255() {
        return this.jj_3_10();
    }

    public boolean jj_3R_256() {
        return this.jj_3R_159();
    }

    public final void ReferencingAnnotationComponentName() throws ZkmScriptParseException {
        ASTReferencingAnnotationComponentName aSTReferencingAnnotationComponentName = new ASTReferencingAnnotationComponentName();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTReferencingAnnotationComponentName);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(NAME);
            this.jjtree.closeNodeScope(aSTReferencingAnnotationComponentName);
            bl = false;
            aSTReferencingAnnotationComponentName.setValue(zkmScriptToken.image);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTReferencingAnnotationComponentName);
            }
        }
    }

    public final void ResetMethodParameterChangesExclusionsStatement() throws ZkmScriptParseException {
        ASTResetMethodParameterChangesExclusionsStatement aSTResetMethodParameterChangesExclusionsStatement = new ASTResetMethodParameterChangesExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetMethodParameterChangesExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_METHOD_PARAMETER_CHANGES_EXCLUSIONS);
            this.currentStatementKind = 206;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetMethodParameterChangesExclusionsStatement);
            bl = false;
            aSTResetMethodParameterChangesExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetMethodParameterChangesExclusionsStatement);
            }
        }
    }

    public final void ResetObfuscateFlowExclusionsStatement() throws ZkmScriptParseException {
        ASTResetObfuscateFlowExclusionsStatement aSTResetObfuscateFlowExclusionsStatement = new ASTResetObfuscateFlowExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetObfuscateFlowExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_OBFUSCATE_FLOW_EXCLUSIONS);
            this.currentStatementKind = 189;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetObfuscateFlowExclusionsStatement);
            bl = false;
            aSTResetObfuscateFlowExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetObfuscateFlowExclusionsStatement);
            }
        }
    }

    public final void ResetObfuscateReferenceExclusionsStatement() throws ZkmScriptParseException {
        ASTResetObfuscateReferenceExclusionsStatement aSTResetObfuscateReferenceExclusionsStatement = new ASTResetObfuscateReferenceExclusionsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetObfuscateReferenceExclusionsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_OBFUSCATE_REFERENCE_EXCLUSIONS);
            this.currentStatementKind = 203;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetObfuscateReferenceExclusionsStatement);
            bl = false;
            aSTResetObfuscateReferenceExclusionsStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetObfuscateReferenceExclusionsStatement);
            }
        }
    }

    public final void MethodParametersParameter() throws ZkmScriptParseException {
        ASTMethodParametersParameter aSTMethodParametersParameter = new ASTMethodParametersParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTMethodParametersParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(METHOD_PARAMETERS);
            this.jj_consume_token(ASSIGN);
            this.MethodParametersParameterType();
            this.jjtree.closeNodeScope(aSTMethodParametersParameter);
            bl = false;
            aSTMethodParametersParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTMethodParametersParameter);
            }
        }
    }

    public final ZkmScriptSimpleNode TrimStatement() throws ZkmScriptParseException {
        ASTTrimStatement aSTTrimStatement = new ASTTrimStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTTrimStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(TRIM);
            this.currentStatementKind = 45;
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case DELETE_UNKNOWN_ATTRIBUTES:
                    case DELETE_EXCEPTION_ATTRIBUTES:
                    case DELETE_ANNOTATION_ATTRIBUTES:
                    case DELETE_SOURCE_FILE_ATTRIBUTES:
                    case DELETE_DEPRECATED_ATTRIBUTES:
                    case DELETE_DEBUG_EXTENSION_ATTRIBUTES:
                        this.TrimParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    default:
                        this.jj_la1[71] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTTrimStatement);
                        bl = false;
                        aSTTrimStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTTrimStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTTrimStatement);
            }
        }
    }

    public boolean jj_3R_257() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_49()) {
            this.jj_scanpos = zkmScriptToken;
            if (this.jj_3R_37()) {
                this.jj_scanpos = zkmScriptToken;
                if (this.jj_3R_236()) {
                    this.jj_scanpos = zkmScriptToken;
                    if (this.jj_3R_168()) {
                        this.jj_scanpos = zkmScriptToken;
                        if (this.jj_3R_23()) {
                            this.jj_scanpos = zkmScriptToken;
                            if (this.jj_3R_135()) {
                                this.jj_scanpos = zkmScriptToken;
                                if (this.jj_3R_234()) {
                                    this.jj_scanpos = zkmScriptToken;
                                    if (this.jj_3R_31()) {
                                        this.jj_scanpos = zkmScriptToken;
                                        if (this.jj_3R_186()) {
                                            this.jj_scanpos = zkmScriptToken;
                                            if (this.jj_3R_178()) {
                                                this.jj_scanpos = zkmScriptToken;
                                                if (this.jj_3R_127()) {
                                                    this.jj_scanpos = zkmScriptToken;
                                                    if (this.jj_3R_8()) {
                                                        this.jj_scanpos = zkmScriptToken;
                                                        if (this.jj_3R_156()) {
                                                            this.jj_scanpos = zkmScriptToken;
                                                            if (this.jj_3R_107()) {
                                                                this.jj_scanpos = zkmScriptToken;
                                                                if (this.jj_3R_216()) {
                                                                    this.jj_scanpos = zkmScriptToken;
                                                                    if (this.jj_3R_52()) {
                                                                        this.jj_scanpos = zkmScriptToken;
                                                                        if (this.jj_3R_106()) {
                                                                            this.jj_scanpos = zkmScriptToken;
                                                                            if (this.jj_3R_191()) {
                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                if (this.jj_3R_279()) {
                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                    if (this.jj_3R_241()) {
                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                        if (this.jj_3R_58()) {
                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                            if (this.jj_3R_220()) {
                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                if (this.jj_3R_102()) {
                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                    if (this.jj_3R_259()) {
                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                        if (this.jj_3R_151()) {
                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                            if (this.jj_3R_253()) {
                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                if (this.jj_3R_277()) {
                                                                                                                    this.jj_scanpos = zkmScriptToken;
                                                                                                                    if (this.jj_3R_160()) {
                                                                                                                        this.jj_scanpos = zkmScriptToken;
                                                                                                                        if (this.jj_3R_129()) {
                                                                                                                            this.jj_scanpos = zkmScriptToken;
                                                                                                                            if (this.jj_3R_263()) {
                                                                                                                                this.jj_scanpos = zkmScriptToken;
                                                                                                                                if (this.jj_3R_230()) {
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
        }

        return false;
    }

    public boolean jj_3R_258() {
        return this.jj_scan_token(ENUM) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_259() {
        return this.jj_scan_token(90);
    }

    public boolean jj_3R_260() {
        return this.jj_scan_token(EXECUTE) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_261() {
        return this.jj_scan_token(EXCLUDE);
    }

    public boolean jj_3R_262() {
        return this.jj_scan_token(CLASSPATH);
    }

    public final void ResetFixedClassesStatement() throws ZkmScriptParseException {
        ASTResetFixedClassesStatement aSTResetFixedClassesStatement = new ASTResetFixedClassesStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTResetFixedClassesStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(RESET_FIXED_CLASSES);
            this.currentStatementKind = 126;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTResetFixedClassesStatement);
            bl = false;
            aSTResetFixedClassesStatement.setLineNumber(zkmScriptToken.endLine);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTResetFixedClassesStatement);
            }
        }
    }

    public final void KeepGenericsParameter() throws ZkmScriptParseException {
        ASTKeepGenericsParameter aSTKeepGenericsParameter = new ASTKeepGenericsParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTKeepGenericsParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(KEEP_GENERICS_INFO);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTKeepGenericsParameter);
            bl = false;
            aSTKeepGenericsParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTKeepGenericsParameter);
            }
        }
    }

    public boolean jj_3R_263() {
        return this.jj_scan_token(RANDOM);
    }

    public boolean jj_3R_264() {
        return this.jj_scan_token(NAME);
    }

    public boolean jj_3R_265() {
        return this.jj_scan_token(NAME);
    }

    public final void DeleteSourceFileAttributesParameter() throws ZkmScriptParseException {
        ASTDeleteSourceFileAttributesParameter aSTDeleteSourceFileAttributesParameter = new ASTDeleteSourceFileAttributesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTDeleteSourceFileAttributesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(DELETE_SOURCE_FILE_ATTRIBUTES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTDeleteSourceFileAttributesParameter);
            bl = false;
            aSTDeleteSourceFileAttributesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTDeleteSourceFileAttributesParameter);
            }
        }
    }

    public final void EncryptLongConstantsParameter() throws ZkmScriptParseException {
        ASTEncryptLongConstantsParameter aSTEncryptLongConstantsParameter = new ASTEncryptLongConstantsParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTEncryptLongConstantsParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(ENCRYPT_LONG_CONSTANTS);
            this.jj_consume_token(ASSIGN);
            this.LongEncryptionType();
            this.jjtree.closeNodeScope(aSTEncryptLongConstantsParameter);
            bl = false;
            aSTEncryptLongConstantsParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTEncryptLongConstantsParameter);
            }
        }
    }

    public boolean jj_3R_266() {
        return this.jj_scan_token(PRINT);
    }

    public final void LongEncryptionType() throws ZkmScriptParseException {
        ASTLongEncryptionType aSTLongEncryptionType = new ASTLongEncryptionType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLongEncryptionType);

        try {
            switch (this.jj_nt.kind) {
                case 43:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(43);
                    this.jjtree.closeNodeScope(aSTLongEncryptionType);
                    bl = false;
                    aSTLongEncryptionType.setValue(zkmScriptToken1.image);
                    break;
                case NORMAL:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(NORMAL);
                    this.jjtree.closeNodeScope(aSTLongEncryptionType);
                    bl = false;
                    aSTLongEncryptionType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[130] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLongEncryptionType);
            }
        }
    }

    public boolean jj_3R_267() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_255()) {
            this.jj_scanpos = zkmScriptToken;
        }

        zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_29()) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_157();
    }

    public boolean jj_3_19() {
        return this.jj_3R_198() ? true : this.jj_scan_token(LPAREN);
    }

    public final void LoadStatement() throws ZkmScriptParseException {
        ASTLoadStatement aSTLoadStatement = new ASTLoadStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTLoadStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OPEN);
            this.currentStatementKind = 39;
            switch (this.jj_nt.kind) {
                case OPEN_NESTED_ARCHIVES:
                    this.OpenNestedArchivesParameter();
                    break;
                default:
                    this.jj_la1[3] = this.jj_gen;
            }

            this.StringLiteral();
            switch (this.jj_nt.kind) {
                case LBRACE:
                    this.FileFilter();
                    break;
                default:
                    this.jj_la1[4] = this.jj_gen;
            }

            while (true) {
                switch (this.jj_nt.kind) {
                    case EXPECTED_FINAL_SHA256:
                    case EXPECTED_INITIAL_SHA256:
                        this.LoadParameter();
                        break;
                    default:
                        this.jj_la1[5] = this.jj_gen;
                        ZkmScriptToken zkmScriptToken1 = this.jj_nt;

                        label122:
                        while (true) {
                            switch (zkmScriptToken1.kind) {
                                case MINUS:
                                case PLUS:
                                case QUOTE_210:
                                    label115:
                                    switch (this.jj_nt.kind) {
                                        case MINUS:
                                            this.SkipArchivePath();
                                            zkmScriptToken1 = this.jj_nt;
                                            continue;
                                        case PLUS:
                                            this.UnSkipArchivePath();
                                            zkmScriptToken1 = this.jj_nt;
                                            continue;
                                        case QUOTE_210:
                                            this.StringLiteral();
                                            switch (this.jj_nt.kind) {
                                                case LBRACE:
                                                    this.FileFilter();
                                                    break label115;
                                                default:
                                                    this.jj_la1[7] = this.jj_gen;
                                                    break label115;
                                            }
                                        default:
                                            this.jj_la1[9] = this.jj_gen;
                                            this.jj_consume_token(-1);
                                            throw new ZkmScriptParseException();
                                    }

                                    while (true) {
                                        switch (this.jj_nt.kind) {
                                            case EXPECTED_FINAL_SHA256:
                                            case EXPECTED_INITIAL_SHA256:
                                                this.LoadParameter();
                                                break;
                                            default:
                                                this.jj_la1[8] = this.jj_gen;
                                                zkmScriptToken1 = this.jj_nt;
                                                continue label122;
                                        }
                                    }
                                default:
                                    this.jj_la1[6] = this.jj_gen;
                                    this.jj_consume_token(SEMICOLON);
                                    this.jjtree.closeNodeScope(aSTLoadStatement);
                                    bl = false;
                                    aSTLoadStatement.setLineNumber(zkmScriptToken.endLine);
                                    return;
                            }
                        }
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTLoadStatement);
            }
        }
    }

    public final void AutoReflectionPackageParameter() throws ZkmScriptParseException {
        ASTAutoReflectionPackageParameter aSTAutoReflectionPackageParameter = new ASTAutoReflectionPackageParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTAutoReflectionPackageParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(AUTO_REFLECTION_PACKAGE);
            this.jj_consume_token(ASSIGN);
            this.StringLiteral();
            this.jjtree.closeNodeScope(aSTAutoReflectionPackageParameter);
            bl = false;
            aSTAutoReflectionPackageParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTAutoReflectionPackageParameter);
            }
        }
    }

    




    public final void QualifiedType() throws ZkmScriptParseException {
        ZkmScriptASTQualifiedType zkmScriptASTQualifiedType = new ZkmScriptASTQualifiedType();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTQualifiedType);
        try {
            this.Type();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 21: {
                        break;
                    }
                    default: {
                        this.jj_la1[181] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(21);
                this.Type();
                zkmScriptToken = this.jj_nt;
            }
            ZkmScriptToken zkmScriptToken2 = this.jj_nt;
            block11:
            while (true) {
                switch (zkmScriptToken2.kind) {
                    case 17: {
                        break;
                    }
                    default: {
                        this.jj_la1[182] = this.jj_gen;
                        break block11;
                    }
                }
                this.ArrayLevel();
                zkmScriptToken2 = this.jj_nt;
            }
            this.jjtree.closeNodeScope(zkmScriptASTQualifiedType);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(zkmScriptASTQualifiedType);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_268() {
        return this.jj_scan_token(LIGHT) ? true : this.jj_scan_token(DOT);
    }

    static {
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
        jj_la1_init_3();
        jj_la1_init_4();
        jj_la1_init_5();
        jj_la1_init_6();
    }

    public final void ComplexClassSpecifier() throws ZkmScriptParseException {
        ASTComplexClassSpecifier aSTComplexClassSpecifier = new ASTComplexClassSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTComplexClassSpecifier);
        try {
            switch (this.jj_nt.kind) {
                case 25:
                case 28: {
                    this.BracketedClassSpecifier();
                    break;
                }
                case 23:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 97:
                case 213:
                case 214: {
                    this.ClassName();
                    break;
                }
                default: {
                    this.jj_la1[153] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
            }
            block6:
            switch (this.jj_nt.kind) {
                case 24:
                case 26: {
                    switch (this.jj_nt.kind) {
                        case 26: {
                            this.jj_consume_token(26);
                            aSTComplexClassSpecifier.setExcludesClassName();
                            break block6;
                        }
                        case 24: {
                            this.jj_consume_token(24);
                            aSTComplexClassSpecifier.setIncludesMembers();
                            break block6;
                        }
                    }
                    this.jj_la1[154] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
                }
                default: {
                    this.jj_la1[155] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(aSTComplexClassSpecifier);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTComplexClassSpecifier);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_269() {
        return this.jj_scan_token(37);
    }

    public final ZkmScriptSimpleNode ObfuscateStatement() throws ZkmScriptParseException {
        ASTObfuscateStatement aSTObfuscateStatement = new ASTObfuscateStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTObfuscateStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(OBFUSCATE);
            this.currentStatementKind = 81;
            ZkmScriptToken zkmScriptToken1 = this.jj_nt;

            while (true) {
                switch (zkmScriptToken1.kind) {
                    case PREVERIFY:
                    case RANDOMIZE:
                    case LINE_NUMBERS:
                    case OBFUSCATE_FLOW:
                    case CHANGE_LOG_FILE:
                    case LOCAL_VARIABLES:
                    case HIDE_FIELD_NAMES:
                    case NEW_NAMES_PREFIX:
                    case CHANGE_LOG_FILE_IN:
                    case UNIQUE_CLASS_NAMES:
                    case METHOD_PARAMETERS:
                    case KEEP_GENERICS_INFO:
                    case CHANGE_LOG_FILE_OUT:
                    case LEGAL_IDENTIFIERS:
                    case ALL_CLASSES_OPENED:
                    case NEW_CLASS_NAME_FILE:
                    case NEW_FIELD_NAME_FILE:
                    case MAKE_CLASSES_PUBLIC:
                    case UNIQUE_METHOD_NAMES:
                    case NEW_METHOD_NAME_FILE:
                    case KEEP_BALANCED_LOCKS:
                    case NEW_NAME_CHARACTERS:
                    case NEW_PACKAGE_NAME_FILE:
                    case KEEP_INNER_CLASS_INFO:
                    case AUTO_REFLECTION_HASH:
                    case OBFUSCATE_PARAMETERS:
                    case MIXED_CASE_CLASS_NAMES:
                    case OBFUSCATE_REFERENCES:
                    case LOOSE_CHANGE_LOG_FILE_IN:
                    case EXCEPTION_OBFUSCATION:
                    case ASSUME_RUNTIME_VERSION:
                    case ENCRYPT_LONG_CONSTANTS:
                    case HIDE_STATIC_METHOD_NAMES:
                    case ENCRYPT_STRING_LITERALS:
                    case AUTO_REFLECTION_PACKAGE:
                    case METHOD_PARAMETER_CHANGES:
                    case AUTO_REFLECTION_HANDLING:
                    case ENCRYPT_INTEGER_CONSTANTS:
                    case AGGRESSIVE_METHOD_RENAMING:
                    case OBFUSCATE_REFERENCES_PACKAGE:
                    case ALLOW_METHOD_PARAMETER_CHANGES:
                    case COLLAPSE_PACKAGES_WITH_DEFAULT:
                    case OBFUSCATE_REFERENCE_STRUCTURES:
                    case METHOD_PARAMETER_CHANGES_PACKAGE:
                    case DERIVE_GROUPINGS_FROM_INPUT_CHANGE_LOG:
                        this.ObfuscateParameter();
                        zkmScriptToken1 = this.jj_nt;
                        break;
                    case UNEXCLUDE:
                    case CLASSPATH:
                    case OBFUSCATE:
                    case PROTECTED:
                    case INTERFACE:
                    case TRANSIENT:
                    case GROUPINGS:
                    case ANNOTATION:
                    case CONTAINING:
                    case SAVE_ALL_OLD:
                    case 90:
                    case IMPLEMENTS:
                    case CONTAINED_IN:
                    case KEEP_VISIBLE:
                    case TRIM_EXCLUDE:
                    case IF_IN_ARCHIVE:
                    case PACKAGE_INFO:
                    case FIXED_CLASSES:
                    case SYNCHRONIZED:
                    case TRIM_UNEXCLUDE:
                    case FLOW_OBFUSCATE:
                    case RESET_GROUPINGS:
                    case IN_SPECIAL_CLASS:
                    case EXTRA_AGGRESSIVE:
                    case RESET_EXCLUSIONS:
                    case LAST_MODIFIED_TIME:
                    case SIGNATURE_CLASSES:
                    case RESET_FIXED_CLASSES:
                    case REMOVE_METHOD_CALLS:
                    case DELETE_XMLCOMMENTS:
                    case OPEN_NESTED_ARCHIVES:
                    case ARCHIVE_COMPRESSION:
                    case IF_NAME_NOT_OBFUSCATED:
                    case RESET_TRIM_EXCLUSIONS:
                    case KEEP_IF_NOT_OBFUSCATED:
                    case EXPECTED_FINAL_SHA256:
                    case IN_REFERENCING_CLASSES:
                    case OBFUSCATE_FLOW_EXCLUDE:
                    case ACCESSED_BY_REFLECTION:
                    case LONG_ENCRYPTION_EXCLUDE:
                    case EXPECTED_INITIAL_SHA256:
                    case OBFUSCATE_FLOW_UNEXCLUDE:
                    case 157:
                    case RESET_REMOVE_METHOD_CALLS:
                    case LONG_ENCRYPTION_UNEXCLUDE:
                    case IGNORE_MISSING_REFERENCES:
                    case STRING_ENCRYPTION_EXCLUDE:
                    case DELETE_UNKNOWN_ATTRIBUTES:
                    case INTEGER_ENCRYPTION_EXCLUDE:
                    case REMOVE_METHOD_CALLS_INCLUDE:
                    case REMOVE_METHOD_CALLS_EXCLUDE:
                    case CLASS_INITIALIZATION_ORDER:
                    case EXISTING_SERIALIZED_CLASSES:
                    case STRING_ENCRYPTION_UNEXCLUDE:
                    case RESET_ACCESSED_BY_REFLECTION:
                    case DELETE_EXCEPTION_ATTRIBUTES:
                    case OBFUSCATE_EXCEPTIONS_EXCLUDE:
                    case INTEGER_ENCRYPTION_UNEXCLUDE:
                    case KEEP_VISIBLE_IF_NOT_OBFUSCATED:
                    case DELETE_ANNOTATION_ATTRIBUTES:
                    case DELETE_SOURCE_FILE_ATTRIBUTES:
                    case DELETE_DEPRECATED_ATTRIBUTES:
                    case OBFUSCATE_REFERENCES_INCLUDE:
                    case OBFUSCATE_REFERENCES_EXCLUDE:
                    case ACCESSED_BY_REFLECTION_EXCLUDE:
                    case KEEP_VISIBLE_METHOD_PARAMETERS:
                    case OBFUSCATE_EXCEPTIONS_UNEXCLUDE:
                    case RESET_IGNORE_MISSING_REFERENCES:
                    case RESET_OBFUSCATE_FLOW_EXCLUSIONS:
                    case RESET_LONG_ENCRYPTION_EXCLUSIONS:
                    case RESET_CLASS_INITIALIZATION_ORDER:
                    case METHOD_PARAMETER_CHANGES_INCLUDE:
                    case METHOD_PARAMETER_CHANGES_EXCLUDE:
                    case RESET_EXISTING_SERIALIZED_CLASSES:
                    case DELETE_DEBUG_EXTENSION_ATTRIBUTES:
                    case RESET_STRING_ENCRYPTION_EXCLUSIONS:
                    case RESET_INTEGER_ENCRYPTION_EXCLUSIONS:
                    case METHOD_PARAMETER_OBFUSCATION_INCLUDE:
                    case METHOD_PARAMETER_OBFUSCATION_EXCLUDE:
                    default:
                        this.jj_la1[72] = this.jj_gen;
                        this.jj_consume_token(SEMICOLON);
                        this.jjtree.closeNodeScope(aSTObfuscateStatement);
                        bl = false;
                        aSTObfuscateStatement.setLineNumber(zkmScriptToken.endLine);
                        return aSTObfuscateStatement;
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
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTObfuscateStatement);
            }
        }
    }

    public boolean jj_3R_270() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_scan_token(BANG)) {
            this.jj_scanpos = zkmScriptToken;
        }

        return this.jj_3R_256();
    }

    public boolean jj_3R_271() {
        return this.jj_scan_token(90);
    }

    public boolean jj_3R_272() {
        return this.jj_scan_token(FINAL);
    }

    public final void IntegerLiteral() throws ZkmScriptParseException {
        ASTIntegerLiteral aSTIntegerLiteral = new ASTIntegerLiteral();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTIntegerLiteral);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(INTEGER_LITERAL);
            this.jjtree.closeNodeScope(aSTIntegerLiteral);
            bl = false;
            aSTIntegerLiteral.setValue(zkmScriptToken.image);
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTIntegerLiteral);
            }
        }
    }

    public final ZkmScriptSimpleNode RemoveMethodCallsStatement() throws ZkmScriptParseException {
        ASTRemoveMethodCallsStatement aSTRemoveMethodCallsStatement = new ASTRemoveMethodCallsStatement();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTRemoveMethodCallsStatement);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(REMOVE_METHOD_CALLS);
            this.currentStatementKind = 127;
            this.jj_consume_token(SEMICOLON);
            this.jjtree.closeNodeScope(aSTRemoveMethodCallsStatement);
            bl = false;
            aSTRemoveMethodCallsStatement.setLineNumber(zkmScriptToken.endLine);
            return aSTRemoveMethodCallsStatement;
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTRemoveMethodCallsStatement);
            }
        }
    }

    public final void UniqueMethodNamesParameter() throws ZkmScriptParseException {
        ASTUniqueMethodNamesParameter aSTUniqueMethodNamesParameter = new ASTUniqueMethodNamesParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTUniqueMethodNamesParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(UNIQUE_METHOD_NAMES);
            this.jj_consume_token(ASSIGN);
            this.Boolean();
            this.jjtree.closeNodeScope(aSTUniqueMethodNamesParameter);
            bl = false;
            aSTUniqueMethodNamesParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTUniqueMethodNamesParameter);
            }
        }
    }

    




    public final void BracketedModuleSpecifier() throws ZkmScriptParseException {
        ASTBracketedModuleSpecifier aSTBracketedModuleSpecifier = new ASTBracketedModuleSpecifier();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTBracketedModuleSpecifier);
        try {
            int n;
            ZkmScriptParser zkmScriptParser;
            block13:
            {
                block12:
                {
                    switch (this.jj_nt.kind) {
                        case 25: {
                            this.jj_consume_token(25);
                            aSTBracketedModuleSpecifier.setNegated();
                            break;
                        }
                        default: {
                            this.jj_la1[140] = this.jj_gen;
                            break block12;
                        }
                    }
                    zkmScriptParser = this;
                    n = 28;
                    break block13;
                }
                zkmScriptParser = this;
                n = 28;
            }
            zkmScriptParser.jj_consume_token(n);
            this.AndModuleSpecifier();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block10:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[141] = this.jj_gen;
                        break block10;
                    }
                }
                this.jj_consume_token(34);
                this.AndModuleSpecifier();
                zkmScriptToken = this.jj_nt;
            }
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(aSTBracketedModuleSpecifier);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(aSTBracketedModuleSpecifier);
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_273() {
        return this.jj_scan_token(90);
    }

    public boolean jj_3R_274() {
        return this.jj_scan_token(SYNTHETIC) ? true : this.jj_scan_token(DOT);
    }

    public boolean jj_3R_275() {
        return this.jj_scan_token(ASCII);
    }

    public boolean jj_3_20() {
        return this.jj_3R_198() ? true : this.jj_scan_token(LPAREN);
    }

    public final void ExceptionObfuscationParameter() throws ZkmScriptParseException {
        ASTExceptionObfuscationParameter aSTExceptionObfuscationParameter = new ASTExceptionObfuscationParameter();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTExceptionObfuscationParameter);

        try {
            ZkmScriptToken zkmScriptToken = this.jj_consume_token(EXCEPTION_OBFUSCATION);
            this.jj_consume_token(ASSIGN);
            this.ExceptionObfuscationType();
            this.jjtree.closeNodeScope(aSTExceptionObfuscationParameter);
            bl = false;
            aSTExceptionObfuscationParameter.setParameterName(zkmScriptToken.image);
        } catch (Throwable throwable) {
            if (bl) {
                this.jjtree.clearNodeScope();
                bl = false;
            } else {
                this.jjtree.popNode();
            }

            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof ZkmScriptParseException) {
                throw (ZkmScriptParseException) throwable;
            } else {
                throw (Error) throwable;
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTExceptionObfuscationParameter);
            }
        }
    }

    public boolean jj_2_28() {
        this.jj_la = Integer.MAX_VALUE;
        this.jj_lastpos = this.jj_scanpos = this.token;

        try {
            return !this.jj_3R_115();
        } catch (ZkmScriptParserLookaheadSuccess zkmScriptParserLookaheadSuccess) {
        } finally {
            this.jj_save(8, Integer.MAX_VALUE);
        }

        return true;
    }

    public final void CharacterType() throws ZkmScriptParseException {
        ASTCharacterType aSTCharacterType = new ASTCharacterType();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTCharacterType);

        try {
            switch (this.jj_nt.kind) {
                case ASCII:
                    ZkmScriptToken zkmScriptToken1 = this.jj_consume_token(ASCII);
                    this.jjtree.closeNodeScope(aSTCharacterType);
                    bl = false;
                    aSTCharacterType.setValue(zkmScriptToken1.image);
                    break;
                case NON_ASCII:
                    ZkmScriptToken zkmScriptToken = this.jj_consume_token(NON_ASCII);
                    this.jjtree.closeNodeScope(aSTCharacterType);
                    bl = false;
                    aSTCharacterType.setValue(zkmScriptToken.image);
                    break;
                default:
                    this.jj_la1[104] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ZkmScriptParseException();
            }
        } finally {
            if (bl) {
                this.jjtree.closeNodeScope(aSTCharacterType);
            }
        }
    }

    public boolean jj_3R_276() {
        return this.jj_scan_token(SCRAMBLE);
    }

    public boolean jj_3_21() {
        return this.jj_scan_token(AND_AND) ? true : this.jj_3R_128();
    }

    public boolean jj_3R_277() {
        return this.jj_scan_token(ANNOTATION);
    }

    public boolean jj_3R_278() {
        return this.jj_3R_257();
    }

    public final void MethodSignature() throws ZkmScriptParseException {
        ZkmScriptASTMethodSignature zkmScriptASTMethodSignature = new ZkmScriptASTMethodSignature();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTMethodSignature);
        try {
            this.ComplexMethodSpecifier();
            this.jj_consume_token(28);
            switch (this.jj_nt.kind) {
                case 23:
                case 25:
                case 27:
                case 28:
                case 32:
                case 37:
                case 39:
                case 40:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 51:
                case 52:
                case 56:
                case 60:
                case 61:
                case 63:
                case 64:
                case 69:
                case 71:
                case 74:
                case 77:
                case 80:
                case 81:
                case 85:
                case 86:
                case 87:
                case 90:
                case 213: {
                    this.MethodArguments();
                    break;
                }
                default: {
                    this.jj_la1[168] = this.jj_gen;
                }
            }
            this.jj_consume_token(29);
            switch (this.jj_nt.kind) {
                case 62: {
                    this.ThrowsClause();
                    break;
                }
                default: {
                    this.jj_la1[169] = this.jj_gen;
                }
            }
            switch (this.jj_nt.kind) {
                case 125: {
                    this.PlusSignatureClasses();
                    break;
                }
                default: {
                    this.jj_la1[170] = this.jj_gen;
                }
            }
            this.jjtree.closeNodeScope(zkmScriptASTMethodSignature);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(zkmScriptASTMethodSignature);
                }
                throw throwable2;
            }
        }
    }

    




    public final void ImplementsClause() throws ZkmScriptParseException {
        ZkmScriptASTImplementsClause zkmScriptASTImplementsClause = new ZkmScriptASTImplementsClause();
        boolean bl = true;
        this.jjtree.openNodeScope(zkmScriptASTImplementsClause);
        try {
            this.jj_consume_token(91);
            switch (this.jj_nt.kind) {
                case 25:
                case 27:
                case 28: {
                    this.ComplexAnnotationSpecifier();
                    break;
                }
                default: {
                    this.jj_la1[117] = this.jj_gen;
                }
            }
            this.QualifiedClassName();
            ZkmScriptToken zkmScriptToken = this.jj_nt;
            block13:
            while (true) {
                switch (zkmScriptToken.kind) {
                    case 20: {
                        break;
                    }
                    default: {
                        this.jj_la1[118] = this.jj_gen;
                        break block13;
                    }
                }
                this.jj_consume_token(20);
                switch (this.jj_nt.kind) {
                    case 25:
                    case 27:
                    case 28: {
                        this.ComplexAnnotationSpecifier();
                        break;
                    }
                    default: {
                        this.jj_la1[119] = this.jj_gen;
                    }
                }
                this.QualifiedClassName();
                zkmScriptToken = this.jj_nt;
            }
            this.jjtree.closeNodeScope(zkmScriptASTImplementsClause);
            return;
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (!(throwable instanceof ZkmScriptParseException)) throw (Error) throwable;
                throw (ZkmScriptParseException) throwable;
            } catch (Throwable throwable2) {
                if (!bl) throw throwable2;
                this.jjtree.closeNodeScope(zkmScriptASTImplementsClause);
                throw throwable2;
            }
        }
    }

    public final void FieldSignature() throws ZkmScriptParseException {
        ASTFieldSignature aSTFieldSignature = new ASTFieldSignature();
        boolean bl = true;
        this.jjtree.openNodeScope(aSTFieldSignature);
        try {
            if (this.jj_2_23()) {
                this.ComplexFieldSpecifier();
            } else {
                switch (this.jj_nt.kind) {
                    case 37:
                    case 39:
                    case 40:
                    case 42:
                    case 43:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 51:
                    case 52:
                    case 56:
                    case 60:
                    case 61:
                    case 63:
                    case 64:
                    case 69:
                    case 71:
                    case 74:
                    case 77:
                    case 80:
                    case 81:
                    case 85:
                    case 86:
                    case 87:
                    case 90:
                    case 213: {
                        this.QualifiedType();
                        this.ComplexFieldSpecifier();
                        break;
                    }
                    default: {
                        this.jj_la1[160] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ZkmScriptParseException();
                    }
                }
            }
            this.jjtree.closeNodeScope(aSTFieldSignature);
        } catch (Throwable throwable) {
            try {
                this.jjtree.clearNodeScope();
                bl = false;
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                if (throwable instanceof ZkmScriptParseException) {
                    throw (ZkmScriptParseException) throwable;
                }
                throw (Error) throwable;
            } catch (Throwable throwable2) {
                if (bl) {
                    this.jjtree.closeNodeScope(aSTFieldSignature);
                }
                throw throwable2;
            }
        }
    }

    public boolean jj_3R_279() {
        return this.jj_scan_token(TRIM);
    }

    public boolean jj_3R_280() {
        ZkmScriptToken zkmScriptToken = this.jj_scanpos;
        if (this.jj_3R_177()) {
            this.jj_scanpos = zkmScriptToken;
        }

        if (this.jj_scan_token(LPAREN)) {
            return true;
        }

        if (this.jj_3R_93()) {
            return true;
        }

        do {
            zkmScriptToken = this.jj_scanpos;
        } while (!this.jj_3_1());

        this.jj_scanpos = zkmScriptToken;
        return this.jj_scan_token(RPAREN);
    }
}
