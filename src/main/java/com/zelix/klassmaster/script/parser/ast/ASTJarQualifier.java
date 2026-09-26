package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTJarQualifier extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        this.jjtGetNumChildren();
        String string = ((ZkmScriptASTStringLiteral) this.jjtGetChild(0)).getValue();
        ((ASTRenameFilterParameter) object).setArchivePathPattern(string);
    }

    public ASTJarQualifier() {
        super(188);
    }
}
