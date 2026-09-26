package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardNameReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASTLparenClause extends ProGuardConfigSimpleNode implements ProGuardNameReceiver {
    public String methodName;
    public String returnType;
    public List argumentTypes = new ArrayList();

    public void setReturnType(String string) {
        this.returnType = string;
    }

    @Override
    public void setName(String string) {
        this.methodName = string;
    }

    public void setArgumentTypes(List list1) {
        this.argumentTypes = list1;
    }

    public ASTLparenClause() {
        super(78);
    }

    @Override
    public final void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ASTProduction6 aSTProduction6 = (ASTProduction6) this.jjtGetParent();
        aSTProduction6.markAsMethod();
        aSTProduction6.setName(this.methodName);
        aSTProduction6.setArgumentTypes(this.argumentTypes);
    }
}
