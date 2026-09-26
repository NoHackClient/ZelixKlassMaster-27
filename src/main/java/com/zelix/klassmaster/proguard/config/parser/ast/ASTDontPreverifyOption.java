package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTDontPreverifyOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-dontpreverify";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setDontPreverify();
    }

    public ASTDontPreverifyOption() {
        super(55);
    }
}
