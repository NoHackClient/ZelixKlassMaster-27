package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTPercentClause3 extends ProGuardValueNode {
    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ((ASTLparenClause) this.jjtGetParent()).setReturnType(this.getValue());
        if (!this.getValue().equals("***")) {
            proGuardConfigTranslator.logWarning("Method return types other than '***' are not supported in a method specification : '" + this.getValue() + "'");
        }
    }

    public ASTPercentClause3() {
        super(87);
    }
}
