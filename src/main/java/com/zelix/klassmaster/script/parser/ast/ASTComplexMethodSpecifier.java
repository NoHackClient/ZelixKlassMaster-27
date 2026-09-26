package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AccessFlagsSpec;
import com.zelix.klassmaster.obfuscator.exclude.MethodArgsPattern;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;

import java.io.IOException;

public class ASTComplexMethodSpecifier extends NameMatchingSpecifierNode {
    public ASTComplexMethodSpecifier() {
        super(209);
    }

    @Override
    public boolean isLiteralName() {
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        return qualifiedNameMatcher instanceof ZkmScriptASTMethodName ? ((ZkmScriptASTMethodName) qualifiedNameMatcher).isExactName() : false;
    }

    public double estimateMatchFraction(
            AccessFlagsSpec accessFlagsSpec1, MethodArgsPattern methodArgsPattern1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier
    ) {
        double ba = 1.0;
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        if (qualifiedNameMatcher instanceof ZkmScriptASTMethodName) {
            String string = qualifiedNameMatcher.getSpecText();
            if (string != null && !ASTRenameFilterParameter.isAnyWildcard(string)) {
                if (!ASTRenameFilterParameter.containsWildcard(string)) {
                    ba *= 0.1;
                } else {
                    ba *= 0.5;
                }
            }

            if (methodArgsPattern1 != null && !methodArgsPattern1.isAnyArgs()) {
                ba *= 0.1;
            }
        }

        if (accessFlagsSpec1 != null) {
            if (accessFlagsSpec1.isPublicRequired()) {
                ba *= 0.25;
            } else if (accessFlagsSpec1.isPackageRequired()) {
                ba *= 0.25;
            } else if (accessFlagsSpec1.isProtectedRequired()) {
                ba *= 0.25;
            } else if (accessFlagsSpec1.isPrivateRequired()) {
                ba *= 0.25;
            }

            if (accessFlagsSpec1.isStaticRequired()) {
                ba *= 0.15;
            }

            if (accessFlagsSpec1.isFinalRequired()) {
                ba *= 0.2;
            } else if (accessFlagsSpec1.isAbstractRequired()) {
                ba *= 0.2;
            }

            if (accessFlagsSpec1.isNativeRequired()) {
                ba *= 0.01;
            }

            if (accessFlagsSpec1.isSynchronizedRequired()) {
                ba *= 0.1;
            }
        }

        if (aSTComplexAnnotationSpecifier != null) {
            ba *= 0.1;
        }

        return ba;
    }

    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        if (super.parent instanceof MethodSpecifierChildNode) {
            ((MethodSpecifierChildNode) super.parent).setMethodSpecifier(this);
        }
    }
}
