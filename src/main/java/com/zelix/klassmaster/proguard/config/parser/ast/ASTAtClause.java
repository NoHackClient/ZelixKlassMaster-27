package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardClassNameClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTAtClause extends ProGuardConfigSimpleNode implements ProGuardNameReceiver {
    public String annotationName;

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ((ASTStarClause2) this.jjtGetChild(0)).translate(this, proGuardConfigTranslator);
        ((ProGuardClassNameClause) this.jjtGetParent()).setAnnotationName(this.annotationName);
    }

    public ASTAtClause() {
        super(80);
    }

    @Override
    public void setName(String string) {
        this.annotationName = string;
    }
}
