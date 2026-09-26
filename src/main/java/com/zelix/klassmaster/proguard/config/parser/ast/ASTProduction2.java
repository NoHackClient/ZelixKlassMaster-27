package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.NamePatternEntry;
import com.zelix.klassmaster.proguard.ProGuardClassNameListOwner;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTProduction2 extends ProGuardConfigSimpleNode {
    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < this.jjtGetNumChildren(); i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }

        ProGuardClassNameListOwner proGuardClassNameListOwner = (ProGuardClassNameListOwner) this.jjtGetParent();

        for (int i = 0; i < ba; i++) {
            ASTStarClause2 aSTStarClause2 = (ASTStarClause2) this.jjtGetChild(i);
            String string1 = aSTStarClause2.getValue();
            boolean negated = aSTStarClause2.isNegated();
            boolean bl = ba > 1;
            boolean bl1 = negated;
            String string = string1;
            proGuardClassNameListOwner.addClassNamePattern(new NamePatternEntry(string, bl1, bl));
        }
    }

    public ASTProduction2() {
        super(88);
    }
}
