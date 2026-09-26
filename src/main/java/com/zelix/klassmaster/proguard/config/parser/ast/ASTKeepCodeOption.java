package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTKeepCodeOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-keepcode";

    public ASTKeepCodeOption() {
        super(64);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
