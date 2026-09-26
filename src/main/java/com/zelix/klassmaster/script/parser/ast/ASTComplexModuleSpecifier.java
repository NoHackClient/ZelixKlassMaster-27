package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;
import java.util.List;

public class ASTComplexModuleSpecifier extends NameMatchingSpecifierNode {
    public boolean excludesModuleName;

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        this.jjtGetChild(0).execute(this, scriptEnvironment1);
        if (zkmScriptNode instanceof ASTRenameFilterParameter) {
            ((ASTRenameFilterParameter) zkmScriptNode).setModuleSpecifier(this);
        }
    }

    @Override
    public String getSpecText() {
        int flowControlKey = ZkmScriptSimpleNode.getFlowControlKey();
        StringBuilder stringBuilder = new StringBuilder();
        String string = ((QualifiedNameMatcher) this.jjtGetChild(0)).getSpecText();
        if (flowControlKey != 0) {
            stringBuilder = stringBuilder.append(string);
            string = this.excludesModuleName ? "^" : "";
        }

        return stringBuilder.append(string).toString();
    }

    public void setExcludesModuleName() {
        this.excludesModuleName = true;
    }

    public double estimateMatchFraction() {
        double ba = 1.0;
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        if (qualifiedNameMatcher instanceof ASTModuleName) {
            List list1 = ((ASTModuleName) qualifiedNameMatcher).getNameComponents();
            if (list1.size() > 0) {
                if (ASTRenameFilterParameter.isAnyWildcard((String) list1.get(list1.size() - 1))) {
                    if (list1.size() > 1) {
                        ba *= 0.25;
                    }
                } else {
                    ba *= 0.1;
                }
            }
        }

        return ba;
    }

    public ASTComplexModuleSpecifier() {
        super(182);
    }

    @Override
    public boolean isLiteralName() {
        QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) this.jjtGetChild(0);
        return qualifiedNameMatcher instanceof NameMatchingSpecifierNode ? ((NameMatchingSpecifierNode) qualifiedNameMatcher).isLiteralName() : false;
    }
}
