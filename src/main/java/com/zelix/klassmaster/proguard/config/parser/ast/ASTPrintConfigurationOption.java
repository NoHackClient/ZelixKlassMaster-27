package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTPrintConfigurationOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-printconfiguration";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTPrintConfigurationOption() {
        super(62);
    }
}
