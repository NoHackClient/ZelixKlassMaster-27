package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTTargetOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-target";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTTargetOption() {
        super(11);
    }
}
