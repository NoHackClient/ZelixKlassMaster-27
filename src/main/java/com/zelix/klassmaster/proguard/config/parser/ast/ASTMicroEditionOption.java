package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTMicroEditionOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-microedition";

    public ASTMicroEditionOption() {
        super(56);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
