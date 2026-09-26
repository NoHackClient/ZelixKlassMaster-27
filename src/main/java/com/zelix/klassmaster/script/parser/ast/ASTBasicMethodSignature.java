package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;

import java.io.IOException;

public class ASTBasicMethodSignature extends MethodSpecifierChildNode {
    public ASTBasicMethodSignature() {
        super(205);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        ASTMemberSpecifier aSTMemberSpecifier = (ASTMemberSpecifier) zkmScriptNode;
        aSTMemberSpecifier.setMethodSpecifier(super.methodSpecifier);
        aSTMemberSpecifier.setParameterAnnotations(super.parameterAnnotations);
        aSTMemberSpecifier.setMethodArgsPattern(super.argsPattern);
    }
}
