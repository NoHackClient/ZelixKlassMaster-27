package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.exclude.QualifiedNameMatcher;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTQualifiedClassName extends ZkmScriptSimpleNode {
    public StringBuilder nameBuilder = new StringBuilder();

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ZkmScriptNode zkmScriptNode1 = this.jjtGetChild(i);
            zkmScriptNode1.execute(this, scriptEnvironment1);
            QualifiedNameMatcher qualifiedNameMatcher = (QualifiedNameMatcher) zkmScriptNode1;
            this.nameBuilder.append(qualifiedNameMatcher.getSpecText());
        }

        TypeTextHolder typeTextHolder = (TypeTextHolder) zkmScriptNode;
        typeTextHolder.setTypeText(this.getInternalName());
    }

    public String getInternalName() {
        return this.nameBuilder.toString().replace('.', '/');
    }

    public ASTQualifiedClassName() {
        super(217);
    }
}
