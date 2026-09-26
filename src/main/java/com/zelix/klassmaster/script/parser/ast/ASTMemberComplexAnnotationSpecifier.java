package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTMemberComplexAnnotationSpecifier extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        ZkmScriptNode zkmScriptNode = this.jjtGetChild(0);
        zkmScriptNode.execute(this, scriptEnvironment1);
        if (super.parent instanceof ASTRenameFilterParameter) {
            ((ASTRenameFilterParameter) super.parent).setMemberAnnotation((ASTComplexAnnotationSpecifier) zkmScriptNode);
        }
    }

    public ASTMemberComplexAnnotationSpecifier() {
        super(172);
    }
}
