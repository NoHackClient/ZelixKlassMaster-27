package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;

import java.io.IOException;

public class ASTStarClause extends ProGuardValueNode {
    public ASTStarClause() {
        super(90);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ((ProGuardNameReceiver) this.jjtGetParent()).setName(this.getValue());
    }
}
