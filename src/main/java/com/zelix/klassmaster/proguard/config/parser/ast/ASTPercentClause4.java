package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTPercentClause4 extends ProGuardValueNode {
    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).logWarning("Invalid syntax. Field type '" + this.getValue() + "' appears with '<fields>'. Field type ignored.");
    }

    public ASTPercentClause4() {
        super(86);
    }
}
