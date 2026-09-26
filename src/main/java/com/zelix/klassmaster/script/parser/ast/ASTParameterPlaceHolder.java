package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTParameterPlaceHolder extends ZkmScriptSimpleNode {
    public ASTParameterPlaceHolder() {
        super(216);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((TypeTextHolder) object).setTypeText("?");
    }
}
