package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardClassNameClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ProGuardConfigASTExtendsClause extends ProGuardConfigSimpleNode implements ProGuardClassNameClause, ProGuardNameReceiver {
    public String annotationName;
    public String className;

    @Override
    public void setAnnotationName(String string) {
        this.annotationName = string;
    }

    @Override
    public void setName(String string) {
        this.className = string;
    }

    public ProGuardConfigASTExtendsClause() {
        super(81);
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ASTLbraceClause aSTLbraceClause = (ASTLbraceClause) this.jjtGetParent();
        aSTLbraceClause.setExtendsClassName(this.className, proGuardConfigTranslator);
        aSTLbraceClause.setExtendsAnnotation(this.annotationName, proGuardConfigTranslator);
    }
}
