package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTClassComplexAnnotationSpecifier extends ZkmScriptSimpleNode {
    public ASTClassComplexAnnotationSpecifier() {
        super(171);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        ZkmScriptNode zkmScriptNode1 = this.jjtGetChild(0);
        zkmScriptNode1.execute(this, scriptEnvironment1);
        if (super.parent instanceof ASTRenameFilterParameter) {
            ((ASTRenameFilterParameter) zkmScriptNode).setClassAnnotation((ASTComplexAnnotationSpecifier) zkmScriptNode1);
        }
    }
}
