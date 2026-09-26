package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;
import java.util.ArrayList;

public class ASTMethodsClause extends ProGuardConfigSimpleNode {
    public ASTMethodsClause() {
        super(92);
    }

    @Override
    public void translate(Object object, Object object1) throws ZkmException, IOException {
        ASTLparenClause aSTLparenClause = (ASTLparenClause) this.jjtGetParent();
        aSTLparenClause.setName("*");
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("*");
        aSTLparenClause.setArgumentTypes(arrayList);
    }
}
