package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTRepackageClassesOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-repackageclasses";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTRepackageClassesOption() {
        super(47);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        if (this.jjtGetNumChildren() > 0) {
            String string = ((ASTQuote122Clause) this.jjtGetChild(0)).getValue();
            proGuardConfigTranslator.setRepackageClasses(string.trim());
        }
    }
}
