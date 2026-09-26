package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptNode;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTClassInitializationOrder extends ZkmScriptSimpleNode implements TypeTextHolder {
    public String precedingClassName;
    public String followingClassName;

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object1;
        ZkmScriptNode zkmScriptNode = (ZkmScriptNode) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).execute(this, scriptEnvironment1);
        }

        ASTClassInitializationOrderStatement aSTClassInitializationOrderStatement = (ASTClassInitializationOrderStatement) zkmScriptNode;
        aSTClassInitializationOrderStatement.addInitializationOrder(this.precedingClassName, this.followingClassName);
    }

    public ASTClassInitializationOrder() {
        super(84);
    }

    @Override
    public void setTypeText(Object object) {
        String string = (String) object;
        if (this.precedingClassName == null) {
            this.precedingClassName = string;
        } else {
            this.followingClassName = string;
        }
    }
}
