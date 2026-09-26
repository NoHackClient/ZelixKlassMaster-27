package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTFieldsClause extends ProGuardConfigSimpleNode {
    public ASTFieldsClause() {
        super(91);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ((ProGuardNameReceiver) this.jjtGetParent()).setName("*");
    }
}
