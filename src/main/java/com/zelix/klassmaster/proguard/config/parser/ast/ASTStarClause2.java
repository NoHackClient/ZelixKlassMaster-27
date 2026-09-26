package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NegatableClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigNode;

import java.io.IOException;

public class ASTStarClause2 extends ProGuardValueNode implements NegatableClause {
    public boolean negated;

    public boolean isNegated() {
        return this.negated;
    }

    public ASTStarClause2() {
        super(89);
    }

    @Override
    public void setNegated() {
        this.negated = true;
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ProGuardConfigNode proGuardConfigNode = this.jjtGetParent();
        if (proGuardConfigNode instanceof ProGuardNameReceiver) {
            ((ProGuardNameReceiver) proGuardConfigNode).setName(this.getValue());
        }
    }
}
