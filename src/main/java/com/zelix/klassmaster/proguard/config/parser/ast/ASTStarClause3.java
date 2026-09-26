package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;
import java.util.ArrayList;

public class ASTStarClause3 extends ProGuardConfigSimpleNode {
    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ASTProduction6 aSTProduction6 = (ASTProduction6) this.jjtGetParent();
        aSTProduction6.markAsMethod();
        aSTProduction6.markAsField();
        aSTProduction6.setName("*");
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("*");
        aSTProduction6.setArgumentTypes(arrayList);
    }

    public ASTStarClause3() {
        super(76);
    }
}
