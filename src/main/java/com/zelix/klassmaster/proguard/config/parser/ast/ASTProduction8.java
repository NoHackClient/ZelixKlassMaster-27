package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTProduction8 extends ProGuardConfigSimpleNode {
    public ProGuardConfigOptionBase getPreviousOption() {
        return ((ASTBomClause) this.jjtGetParent()).getLastOption();
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ((ProGuardConfigOptionBase) this.jjtGetChild(0)).translate(this, proGuardConfigTranslator);
    }

    public ASTProduction8() {
        super(1);
    }
}
