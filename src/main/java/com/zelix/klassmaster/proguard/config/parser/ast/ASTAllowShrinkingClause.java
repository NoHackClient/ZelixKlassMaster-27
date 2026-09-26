package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.io.IOException;

public class ASTAllowShrinkingClause extends ProGuardValueNode {
    public ASTAllowShrinkingClause() {
        super(15);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ProGuardConfigNode proGuardConfigNode = (ProGuardConfigNode) object;
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object1;
        super.translate(proGuardConfigNode, proGuardConfigTranslator);
        ((ProGuardKeepOptionBase) this.jjtGetParent()).addItem(this.getValue());
    }
}
