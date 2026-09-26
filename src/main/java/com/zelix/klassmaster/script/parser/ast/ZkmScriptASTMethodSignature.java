package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;

import java.io.IOException;

public class ZkmScriptASTMethodSignature extends MethodSpecifierChildNode {
    public ZkmScriptASTMethodSignature() {
        super(206);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) zkmScriptNode;
        aSTRenameFilterParameter.setMethodSpecifier(super.methodSpecifier);
        aSTRenameFilterParameter.setParameterAnnotations(super.parameterAnnotations);
        aSTRenameFilterParameter.setArgsPattern(super.argsPattern);
    }

    public void setPlusSignatureClasses() {
        ((ASTRenameFilterParameter) this.jjtGetParent()).setPlusSignatureClasses();
    }
}
