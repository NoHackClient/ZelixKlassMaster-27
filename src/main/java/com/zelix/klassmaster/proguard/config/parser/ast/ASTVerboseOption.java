package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTVerboseOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-verbose";

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setVerbose();
    }

    public ASTVerboseOption() {
        super(58);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
