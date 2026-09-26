package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.AccessFlagsSpec;
import com.zelix.klassmaster.obfuscator.exclude.AnnotationSpecifierHolder;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecCollector;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecifierHandler;
import com.zelix.klassmaster.obfuscator.exclude.MemberSpecifierMatcher;
import com.zelix.klassmaster.obfuscator.exclude.MethodArgsPattern;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ASTMemberSpecifier
        extends ZkmScriptSimpleNode
        implements MemberSpecifierMatcher,
        MemberSpecifierHandler,
        MemberSpecCollector,
        AnnotationSpecifierHolder {
    private ASTComplexMethodSpecifier methodSpecifier;
    public String fieldType;
    public ASTComplexAnnotationSpecifier[] parameterAnnotations;
    public ScriptEnvironment scriptEnvironment;
    private ASTComplexAnnotationSpecifier annotationSpecifier;
    public ASTComplexFieldSpecifier fieldSpecifier;
    private MethodArgsPattern methodArgsPattern;
    private AccessFlagsSpec accessFlagsSpec;
    public String accessLevel;
    public Map modifiers = ZkmUtils.createHashMap(19);
    public List thrownExceptionNames = new ArrayList();

    public void buildAccessFlagsSpec() {
        byte ba;
        ASTMemberSpecifier aSTMemberSpecifier1;
        Map map1;
        if (this.fieldSpecifier != null) {
            ba = 3;
            aSTMemberSpecifier1 = this;
            map1 = this.modifiers;
        } else {
            ba = 4;
            aSTMemberSpecifier1 = this;
            map1 = this.modifiers;
        }

        aSTMemberSpecifier1.accessFlagsSpec = ASTRenameFilterParameter.buildMemberAccessFlags(map1, ba);
    }

    public void setMethodSpecifier(ASTComplexMethodSpecifier aSTComplexMethodSpecifier) {
        this.methodSpecifier = aSTComplexMethodSpecifier;
    }

    @Override
    public void setFieldType(String string) {
        this.fieldType = string;
    }

    public void addModifier(String string) {
        String string1 = this.getSummarizingStatement().getStatementName();
        int statementLine = this.getSummarizingStatement().getStatementLine();
        if (string.equals("public") || string.equals("protected") || string.equals("private") || string.equals("package")) {
            if (this.accessLevel == null) {
                this.accessLevel = string;
            } else if (!this.accessLevel.equals(string)) {
                this.scriptEnvironment
                        .logWarning(
                                "\""
                                        + string
                                        + "\" appears after \""
                                        + this.accessLevel
                                        + "\" in \""
                                        + string1
                                        + "\" statement at line "
                                        + statementLine
                                        + ". \""
                                        + string
                                        + "\" ignored.",
                                true
                        );
                return;
            }
        }

        if (this.modifiers.put(string, string) != null) {
            this.scriptEnvironment.logWarning("\"" + string + "\" appears more than once in \"" + string1 + "\" statement at line " + statementLine + ".", true);
        }
    }

    public void setParameterAnnotations(ASTComplexAnnotationSpecifier[] aSTComplexAnnotationSpecifiers) {
        this.parameterAnnotations = aSTComplexAnnotationSpecifiers;
    }

    @Override
    public void setFieldSpecifier(ASTComplexFieldSpecifier aSTComplexFieldSpecifier) {
        this.fieldSpecifier = aSTComplexFieldSpecifier;
    }

    @Override
    public void addParameterType(Object object) {
        this.thrownExceptionNames.add(object);
    }

    @Override
    public final boolean hasMatchingMember(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException {
        if (this.fieldSpecifier != null) {
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.getFields();

            for (AbstractFieldInfo abstractFieldInfo : abstractFieldInfos) {
                if (ASTRenameFilterParameter.matchesAccessFlags(abstractFieldInfo.getAccessFlags(), this.accessFlagsSpec)
                        && this.fieldSpecifier.matchesName(AbstractExclusionSpec.getFieldMatchName(abstractFieldInfo))
                        && (
                        this.annotationSpecifier == null
                                || ASTRenameFilterParameter.matchesFieldAnnotations(abstractFieldInfo, this.annotationSpecifier, classHierarchyQuery)
                )
                        && (this.fieldType == null || this.fieldType.equals(AbstractExclusionSpec.getMemberMatchDescriptor(abstractFieldInfo)))) {
                    return true;
                }
            }
        } else {
            AbstractMethodInfo[] abstractMethodInfos = classFileBase.getDeclaredMethods();

            for (AbstractMethodInfo abstractMethodInfo : abstractMethodInfos) {
                if (ASTRenameFilterParameter.matchesAccessFlags(abstractMethodInfo.getAccessFlags(), this.accessFlagsSpec)
                        && this.methodSpecifier.matchesName(AbstractExclusionSpec.getMethodMatchName(abstractMethodInfo))
                        && ASTRenameFilterParameter.matchesArgs(AbstractExclusionSpec.getMemberMatchDescriptor(abstractMethodInfo), this.methodArgsPattern)
                        && (
                        this.annotationSpecifier == null
                                || ASTRenameFilterParameter.matchesMethodAnnotations(abstractMethodInfo, this.annotationSpecifier, classHierarchyQuery)
                )
                        && ASTRenameFilterParameter.matchesParameterAnnotations(
                        abstractMethodInfo, this.parameterAnnotations, this.methodArgsPattern, classHierarchyQuery
                )) {
                    ObservableHolder observableHolder = new ObservableHolder();
                    boolean bl = ASTRenameFilterParameter.matchesThrowsClause(classHierarchyQuery, abstractMethodInfo, this.thrownExceptionNames, observableHolder);
                    if (!observableHolder.isValueNull()) {
                        String string = (String) observableHolder.getValue();
                        SummarizingStatementNode summarizingStatementNode = this.getSummarizingStatement();
                        string = ZkmStringUtils.replaceAll(string, "<0>", this.getSpecText());
                        string = ZkmStringUtils.replaceAll(string, "<1>", summarizingStatementNode.getStatementName());
                        string = ZkmStringUtils.replaceAll(string, "<2>", String.valueOf(summarizingStatementNode.getStatementLine()));
                        this.scriptEnvironment.logWarning(string);
                    }

                    if (bl) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void setMethodArgsPattern(MethodArgsPattern methodArgsPattern1) {
        this.methodArgsPattern = methodArgsPattern1;
    }

    @Override
    public void setAnnotationSpecifier(ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) {
        this.annotationSpecifier = aSTComplexAnnotationSpecifier;
    }

    public ASTMemberSpecifier() {
        super(152);
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        this.scriptEnvironment = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, this.scriptEnvironment);
        }

        this.buildAccessFlagsSpec();
    }

    @Override
    public String getSpecText() {
        int flowPredicate = ZkmScriptSimpleNode.getFlowPredicate();
        ASTMemberSpecifier aSTMemberSpecifier1 = this;
        if (flowPredicate == 0) {
            if (this.fieldSpecifier != null) {
                return ASTRenameFilterParameter.formatFieldSpec(this.annotationSpecifier, this.accessFlagsSpec, this.fieldType, this.fieldSpecifier);
            }

            aSTMemberSpecifier1 = this;
        }

        return ASTRenameFilterParameter.formatMethodSpec(
                aSTMemberSpecifier1.annotationSpecifier,
                this.accessFlagsSpec,
                this.methodSpecifier,
                this.methodArgsPattern,
                this.parameterAnnotations,
                this.thrownExceptionNames
        );
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
