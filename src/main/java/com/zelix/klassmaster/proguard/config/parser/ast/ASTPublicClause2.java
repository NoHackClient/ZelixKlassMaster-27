package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NegatableClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardModifierReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.io.IOException;

public class ASTPublicClause2 extends ProGuardValueNode implements NegatableClause {
    public boolean negated;

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
        ((ProGuardModifierReceiver) this.jjtGetParent()).addModifier((this.negated ? "!" : "") + this.getValue());
    }

    @Override
    public void setNegated() {
        this.negated = true;
    }

    public ASTPublicClause2() {
        super(95);
    }
}
