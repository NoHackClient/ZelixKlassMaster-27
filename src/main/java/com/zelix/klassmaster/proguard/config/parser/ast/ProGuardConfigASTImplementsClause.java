package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardClassNameClause;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ProGuardConfigASTImplementsClause extends ProGuardConfigSimpleNode implements ProGuardClassNameClause, ProGuardNameReceiver {
    public String className;
    public String annotationName;

    @Override
    public void setName(String string) {
        this.className = string;
    }

    @Override
    public void setAnnotationName(String string) {
        this.annotationName = string;
    }

    public ProGuardConfigASTImplementsClause() {
        super(82);
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ASTLbraceClause aSTLbraceClause = (ASTLbraceClause) this.jjtGetParent();
        aSTLbraceClause.setImplementsClassName(this.className);
        if (this.annotationName != null) {
            aSTLbraceClause.setImplementsAnnotation(this.annotationName, proGuardConfigTranslator);
        }
    }
}
