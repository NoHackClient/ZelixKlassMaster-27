package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTOverloadAggressivelyOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-overloadaggressively";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTOverloadAggressivelyOption() {
        super(42);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setOverloadAggressively();
    }
}
