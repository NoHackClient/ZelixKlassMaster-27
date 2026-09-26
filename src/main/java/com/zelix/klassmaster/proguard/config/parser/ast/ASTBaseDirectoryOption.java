package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTBaseDirectoryOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-basedirectory";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTBaseDirectoryOption() {
        super(6);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setBaseDirectory(((ASTQuote122Clause) this.jjtGetChild(0)).getValue());
    }
}
