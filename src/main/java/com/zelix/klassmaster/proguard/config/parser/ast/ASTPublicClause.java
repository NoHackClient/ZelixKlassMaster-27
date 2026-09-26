package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NegatableClause;
import com.zelix.klassmaster.proguard.ProGuardModifierReceiver;

import java.io.IOException;

public class ASTPublicClause extends ProGuardValueNode implements NegatableClause {
    public boolean negated;

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ((ProGuardModifierReceiver) this.jjtGetParent()).addModifier((this.negated ? "!" : "") + this.getValue());
    }

    @Override
    public void setNegated() {
        this.negated = true;
    }

    public ASTPublicClause() {
        super(94);
    }
}
