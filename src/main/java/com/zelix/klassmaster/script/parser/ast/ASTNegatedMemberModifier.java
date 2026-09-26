package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTNegatedMemberModifier extends ZkmScriptSimpleNode {
    public ASTNegatedMemberModifier() {
        super(143);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ASTMemberModifierHelper aSTMemberModifierHelper = (ASTMemberModifierHelper) this.jjtGetChild(0);
        aSTMemberModifierHelper.execute(this, scriptEnvironment1);
        String string = aSTMemberModifierHelper.getValue();
        ((ASTRenameFilterParameter) object).addMemberModifier("!" + string);
    }
}
