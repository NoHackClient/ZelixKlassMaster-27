package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NegatableClause;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTBangClause extends ProGuardConfigSimpleNode {
    @Override
    public final void translate(Object object, Object object1) throws ZkmException, IOException {
        ((NegatableClause) this.jjtGetParent()).setNegated();
    }

    public ASTBangClause() {
        super(79);
    }
}
