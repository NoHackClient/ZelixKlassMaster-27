package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTWhyAreYouKeepingOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-whyareyoukeeping";

    public ASTWhyAreYouKeepingOption() {
        super(24);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
