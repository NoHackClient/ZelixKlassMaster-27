package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTParameterComplexAnnotationSpecifier extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = this.jjtGetChild(0);
        zkmScriptNode.execute(this, scriptEnvironment1);
        ((ASTMethodParameter) object).setAnnotationSpecifier((ASTComplexAnnotationSpecifier) zkmScriptNode);
    }

    public ASTParameterComplexAnnotationSpecifier() {
        super(174);
    }
}
