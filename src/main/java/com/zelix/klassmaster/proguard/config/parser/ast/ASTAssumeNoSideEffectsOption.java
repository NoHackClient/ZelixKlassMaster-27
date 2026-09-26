package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAssumeNoSideEffectsOption extends ProGuardAssumeMethodOption {
    private static final String OPTION_NAME = "-assumenosideeffects";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTAssumeNoSideEffectsOption() {
        super(29);
    }
}
