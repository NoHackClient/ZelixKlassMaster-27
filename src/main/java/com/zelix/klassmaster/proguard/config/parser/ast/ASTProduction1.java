package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTProduction1 extends ProGuardConfigSimpleNode implements ProGuardNameReceiver {
    public String fieldType;
    public String fieldName;

    public void setFieldType(String string) {
        this.fieldType = string;
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ASTProduction6 aSTProduction6 = (ASTProduction6) this.jjtGetParent();
        aSTProduction6.markAsField();
        aSTProduction6.setName(this.fieldName);
        aSTProduction6.setMemberType(this.fieldType);
    }

    @Override
    public void setName(String string) {
        this.fieldName = string;
    }

    public ASTProduction1() {
        super(77);
    }
}
