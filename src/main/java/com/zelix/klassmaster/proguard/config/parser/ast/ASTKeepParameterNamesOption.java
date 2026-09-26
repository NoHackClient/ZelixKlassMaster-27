package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTKeepParameterNamesOption extends ProGuardOptionNode {
    private static final String OPTION_NAME = "-keepparameternames";

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).setKeepParameterNames();
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTKeepParameterNamesOption() {
        super(49);
    }
}
