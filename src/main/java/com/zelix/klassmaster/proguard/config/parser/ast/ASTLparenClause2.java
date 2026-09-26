package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;
import com.zelix.klassmaster.proguard.ProGuardFilterReceiver;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public class ASTLparenClause2 extends ProGuardConfigSimpleNode implements ProGuardFilterReceiver {
    @Override
    public boolean addArchiveFilter(Object object) {
        String string = (String) object;
        return ((ASTTildeClause) this.jjtGetParent()).addArchiveFilter(string);
    }

    public ASTLparenClause2() {
        super(71);
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }
    }

    @Override
    public boolean addFileFilter(Object object) {
        String string = (String) object;
        return ((ASTTildeClause) this.jjtGetParent()).addFileFilter(string);
    }
}
