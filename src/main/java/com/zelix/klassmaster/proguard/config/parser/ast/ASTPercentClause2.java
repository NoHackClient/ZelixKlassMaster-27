package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTPercentClause2 extends ProGuardValueNode {
    public ASTPercentClause2() {
        super(85);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ((ASTProduction1) this.jjtGetParent()).setFieldType(this.getValue());
    }
}
