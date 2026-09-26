package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTDefaultMethodParameterChangesExcludeInput extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object1, Object object) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }
    }

    public ASTDefaultMethodParameterChangesExcludeInput() {
        super(47);
    }

    public ParameterListStatement getParameterListStatement() {
        return this.jjtGetNumChildren() > 0 ? (ParameterListStatement) this.jjtGetChild(0) : null;
    }
}
