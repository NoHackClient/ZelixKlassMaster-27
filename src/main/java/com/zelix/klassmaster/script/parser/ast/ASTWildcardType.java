package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.TypeTextHolder;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTWildcardType extends ZkmScriptSimpleNode {
    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((TypeTextHolder) object).setTypeText("*");
    }

    public ASTWildcardType() {
        super(221);
    }
}
