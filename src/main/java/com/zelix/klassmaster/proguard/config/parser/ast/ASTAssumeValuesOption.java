package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAssumeValuesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-assumevalues";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTAssumeValuesOption() {
        super(65);
    }
}
