package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTBomClause extends ProGuardConfigSimpleNode {
    public ProGuardConfigOptionBase lastOption;

    public ASTBomClause() {
        super(0);
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ASTProduction8 aSTProduction8 = (ASTProduction8) this.jjtGetChild(i);
            aSTProduction8.translate(this, proGuardConfigTranslator);
            this.lastOption = (ProGuardConfigOptionBase) aSTProduction8.jjtGetChild(0);
        }
    }

    public ProGuardConfigOptionBase getLastOption() {
        return this.lastOption;
    }
}
