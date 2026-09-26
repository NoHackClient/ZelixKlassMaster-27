package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NegatableClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.io.IOException;

public class ASTProduction4 extends ProGuardValueNode implements NegatableClause {
    public boolean negated;

    public ASTProduction4() {
        super(69);
    }

    @Override
    public void setNegated() {
        this.negated = true;
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
        ((ASTKeepAttributesOption) this.jjtGetParent()).addAttributeName((this.negated ? "!" : "") + this.getValue());
    }
}
