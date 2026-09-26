package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.parser.ZkmScriptSimpleNode;

import java.io.IOException;

public class ZkmScriptASTArrayLevel extends ZkmScriptSimpleNode {
    public ZkmScriptASTArrayLevel() {
        super(220);
    }

    @Override
    public void execute(Object object, Object object1) throws ZkmException, IOException {
        ((ZkmScriptASTQualifiedType) object).incrementArrayDimensions();
    }
}
