package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;

import java.io.IOException;

public class ASTSkipArchivePath extends ScriptValueNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        if (this.jjtGetNumChildren() == 1) {
            ZkmScriptNode zkmScriptNode = this.jjtGetChild(0);
            if (zkmScriptNode instanceof ZkmScriptASTStringLiteral) {
                ZkmScriptASTStringLiteral zkmScriptASTStringLiteral = (ZkmScriptASTStringLiteral) zkmScriptNode;
                this.value = zkmScriptASTStringLiteral.getValue();
            }
        }
    }

    public ASTSkipArchivePath() {
        super(4);
    }
}
