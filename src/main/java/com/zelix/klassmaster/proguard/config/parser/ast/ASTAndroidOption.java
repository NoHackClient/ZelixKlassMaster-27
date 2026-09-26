package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAndroidOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-android";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTAndroidOption() {
        super(57);
    }
}
