package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASTProduction3 extends ProGuardConfigSimpleNode {
    public List argumentTypes = new ArrayList();

    public void addArgumentType(Object object) {
        this.argumentTypes.add(object);
    }

    public ASTProduction3() {
        super(83);
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ASTLparenClause aSTLparenClause = (ASTLparenClause) this.jjtGetParent();
        aSTLparenClause.setArgumentTypes(this.argumentTypes);
    }
}
