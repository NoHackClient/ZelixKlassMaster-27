package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.AccessFlagsSpec;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;

import java.io.IOException;

public class ASTComplexFieldSpecifier extends NameMatchingSpecifierNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        if (zkmScriptNode instanceof ASTFieldSignature) {
            ((ASTFieldSignature) zkmScriptNode).setFieldSpecifier(this);
        }
    }

    public ASTComplexFieldSpecifier() {
        super(201);
    }

    @Override
    public boolean isLiteralName() {
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        return qualifiedNameMatcher instanceof ASTFieldName ? ((ASTFieldName) qualifiedNameMatcher).isExactName() : false;
    }

    public double estimateMatchFraction(AccessFlagsSpec accessFlagsSpec1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) {
        double ba = 1.0;
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        if (qualifiedNameMatcher instanceof ASTFieldName) {
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
                ba *= 0.2;
            } else if (accessFlagsSpec1.isPackageRequired()) {
                ba *= 0.25;
            } else if (accessFlagsSpec1.isProtectedRequired()) {
                ba *= 0.25;
            } else if (accessFlagsSpec1.isPrivateRequired()) {
                ba *= 0.25;
            }

            if (accessFlagsSpec1.isStaticRequired()) {
                ba *= 0.2;
            }

            if (accessFlagsSpec1.isFinalRequired()) {
                ba *= 0.2;
            } else if (accessFlagsSpec1.isVolatileRequired()) {
                ba *= 0.01;
            }

            if (accessFlagsSpec1.isTransientRequired()) {
                ba *= 0.01;
            }
        }

        if (aSTComplexAnnotationSpecifier != null) {
            ba *= 0.1;
        }

        return ba;
    }
}
