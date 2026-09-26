package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTDontShrinkOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-dontshrink";

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setDontShrink();
    }

    public ASTDontShrinkOption() {
        super(22);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
