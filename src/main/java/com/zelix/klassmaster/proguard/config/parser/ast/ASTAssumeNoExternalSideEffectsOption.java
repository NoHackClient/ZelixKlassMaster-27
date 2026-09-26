package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAssumeNoExternalSideEffectsOption extends ProGuardAssumeMethodOption {
    private static final String OPTION_NAME = "-assumenoexternalsideeffects";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTAssumeNoExternalSideEffectsOption() {
        super(32);
    }
}
