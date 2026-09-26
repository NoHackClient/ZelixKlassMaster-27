package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTProduction5 extends ProGuardConfigSimpleNode {
    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        ASTLparenClause2 aSTLparenClause2 = (ASTLparenClause2) this.jjtGetParent();
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            ASTQuote122Clause aSTQuote122Clause = (ASTQuote122Clause) this.jjtGetChild(i);
            aSTQuote122Clause.translate(this, proGuardConfigTranslator);
            aSTLparenClause2.addArchiveFilter(aSTQuote122Clause.getValue());
        }
    }

    public ASTProduction5() {
        super(72);
    }
}
