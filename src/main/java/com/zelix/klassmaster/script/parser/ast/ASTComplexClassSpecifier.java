package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AccessFlagsSpec;
import com.zelix.klassmaster.obfuscator.exclude.ClassNamePattern;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTComplexClassSpecifier extends NameMatchingSpecifierNode implements ClassNamePattern {
    public boolean includesMembers;
    public boolean excludesClassName;
    private static final String MEMBERS_SUFFIX = " +";

    @Override
    public boolean hasCaretTag() {
        return this.excludesClassName;
    }

    @Override
    public String getNamePattern() {
        return ((QualifiedNameMatcher) this.jjtGetChild(0)).getSpecText();
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        if (zkmScriptNode instanceof ASTRenameFilterParameter) {
            ((ASTRenameFilterParameter) zkmScriptNode).setClassNamePattern(this);
        }
    }

    public ASTComplexClassSpecifier() {
        super(195);
    }

    @Override
    public boolean isLiteralName() {
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        return qualifiedNameMatcher instanceof ASTClassName ? ((ASTClassName) qualifiedNameMatcher).isExactName() : false;
    }

    @Override
    public boolean hasPlusTag() {
        return this.includesMembers;
    }

    public void setExcludesClassName() {
        this.excludesClassName = true;
    }

    public void setIncludesMembers() {
        this.includesMembers = true;
    }

    @Override
    public double computeSpecificity(Object object, Object object1) {
        ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier = (ASTComplexAnnotationSpecifier) object1;
        AccessFlagsSpec accessFlagsSpec1 = (AccessFlagsSpec) object;
        double ba = 1.0;
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        if (qualifiedNameMatcher instanceof ASTClassName) {
            String string = qualifiedNameMatcher.getSpecText();
            if (string != null && !ASTRenameFilterParameter.isAnyWildcard(string)) {
                if (!ASTRenameFilterParameter.containsWildcard(string)) {
                    ba *= 0.1;
                } else {
                    ba *= 0.5;
                }
            }
        }

        if (accessFlagsSpec1 != null) {
            if (accessFlagsSpec1.isPublicRequired()) {
                ba *= 0.5;
            } else if (accessFlagsSpec1.isPackageRequired()) {
                ba *= 0.5;
            }

            if (accessFlagsSpec1.isFinalRequired()) {
                ba *= 0.1;
            }

            if (accessFlagsSpec1.isInterfaceRequired()) {
                ba *= 0.1;
            } else if (accessFlagsSpec1.isAbstractRequired()) {
                ba *= 0.1;
            }
        }

        if (aSTComplexAnnotationSpecifier != null) {
            ba *= 0.1;
        }

        return ba;
    }

    @Override
    public String getSpecText() {
        int flowPredicate = ZkmScriptSimpleNode.getFlowPredicate();
        StringBuilder stringBuilder = new StringBuilder();
        String string = ((QualifiedNameMatcher) this.jjtGetChild(0)).getSpecText();
        if (flowPredicate == 0) {
            stringBuilder = stringBuilder.append(string);
            string = this.excludesClassName ? "^" : "";
        }

        return stringBuilder.append(string).append(this.includesMembers ? MEMBERS_SUFFIX : "").toString();
    }
}
