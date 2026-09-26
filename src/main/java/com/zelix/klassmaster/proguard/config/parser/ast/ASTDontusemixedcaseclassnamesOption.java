package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTDontusemixedcaseclassnamesOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-dontusemixedcaseclassnames";

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setDontUseMixedCaseClassNames();
    }

    public ASTDontusemixedcaseclassnamesOption() {
        super(44);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
