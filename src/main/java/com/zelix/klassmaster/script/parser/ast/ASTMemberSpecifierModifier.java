package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTMemberSpecifierModifier extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ASTMemberModifierHelper aSTMemberModifierHelper = (ASTMemberModifierHelper) this.jjtGetChild(0);
        aSTMemberModifierHelper.execute(this, scriptEnvironment1);
        String string = aSTMemberModifierHelper.getValue();
        ((ASTMemberSpecifier) object).addModifier(string);
    }

    public ASTMemberSpecifierModifier() {
        super(144);
    }
}
