package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTForceProcessingOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-forceprocessing";

    public ASTForceProcessingOption() {
        super(12);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
