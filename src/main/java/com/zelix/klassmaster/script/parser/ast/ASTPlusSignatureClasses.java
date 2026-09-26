package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ASTPlusSignatureClasses extends ZkmScriptSimpleNode {
    public ASTPlusSignatureClasses() {
        super(156);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ZkmScriptASTMethodSignature) object).setPlusSignatureClasses();
    }
}
