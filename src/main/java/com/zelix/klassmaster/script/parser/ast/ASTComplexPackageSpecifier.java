package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.PackagePatternSpec;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASTComplexPackageSpecifier extends NameMatchingSpecifierNode implements PackagePatternSpec {
    public boolean includesSubpackages;
    public boolean excludesPackageName;

    @Override
    public boolean hasDotSuffix() {
        return this.includesSubpackages;
    }

    @Override
    public boolean hasCaretTag() {
        return this.excludesPackageName;
    }

    public final void setIncludesSubpackages() {
        this.includesSubpackages = true;
    }

    @Override
    public String getSpecText() {
        int flowControlKey = ZkmScriptSimpleNode.getFlowControlKey();
        StringBuilder stringBuilder = new StringBuilder();
        String string = ((QualifiedNameMatcher) this.jjtGetChild(0)).getSpecText();
        if (flowControlKey != 0) {
            stringBuilder = stringBuilder.append(string);
            string = this.includesSubpackages ? "." : "";
        }

        return stringBuilder.append(string).append(this.excludesPackageName ? "^" : "").toString();
    }

    public ASTComplexPackageSpecifier() {
        super(185);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        if (zkmScriptNode instanceof ASTRenameFilterParameter) {
            ((ASTRenameFilterParameter) zkmScriptNode).setPackagePattern(this);
        }
    }

    @Override
    public String getNamePattern() {
        return ((QualifiedNameMatcher) this.jjtGetChild(0)).getSpecText();
    }

    @Override
    public List getNameSegments() {
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        return qualifiedNameMatcher instanceof ASTPackageName ? ((ASTPackageName) qualifiedNameMatcher).getNameComponents() : new ArrayList();
    }

    @Override
    public boolean isLiteralName() {
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        return !this.includesSubpackages && qualifiedNameMatcher instanceof ASTPackageName ? ((ASTPackageName) qualifiedNameMatcher).isLiteralName() : false;
    }

    public final void setExcludesPackageName() {
        this.excludesPackageName = true;
    }

    @Override
    public double computeSpecificity() {
        double ba = 1.0;
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        if (qualifiedNameMatcher instanceof ASTPackageName) {
            List list1 = ((ASTPackageName) qualifiedNameMatcher).getNameComponents();
            if (list1.size() > 0) {
                if (!ASTRenameFilterParameter.isAnyWildcard((String) list1.get(list1.size() - 1)) && !this.includesSubpackages) {
                    ba *= 0.1;
                } else if (list1.size() > 1) {
                    ba *= 0.25;
                }
            }
        }

        return ba;
    }
}
