package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAddConfigurationDebuggingOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-addconfigurationdebugging";

    public ASTAddConfigurationDebuggingOption() {
        super(33);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
