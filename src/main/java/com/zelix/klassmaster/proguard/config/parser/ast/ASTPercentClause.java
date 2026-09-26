package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTPercentClause extends ProGuardValueNode {
    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ((ASTProduction3) this.jjtGetParent()).addArgumentType(this.getValue());
    }

    public ASTPercentClause() {
        super(84);
    }
}
