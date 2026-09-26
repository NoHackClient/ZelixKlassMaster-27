package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTPrintMappingOption extends ProGuardOptionNode {
    public ASTPrintMappingOption() {
        super(38);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        if (this.jjtGetNumChildren() > 0) {
            String string = ((ASTQuote122Clause) this.jjtGetChild(0)).getValue();
            proGuardConfigTranslator.setPrintMapping(string);
        } else {
            proGuardConfigTranslator.setPrintMapping("ChangeLog.txt");
        }
    }

    @Override
    public String getOptionName() {
        return "-printmapping";
    }
}
