package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ASTIgnoreWarningsOption extends ProGuardOptionNode {
    @Override
    public String getOptionName() {
        return "-ignorewarnings";
    }

    public ASTIgnoreWarningsOption() {
        super(61);
    }

    @Override
    public void translateOption(Object object) throws ZkmException, IOException {
        ((ProGuardConfigTranslator) object).addIgnoreMissingReference("*.* +");
    }
}
