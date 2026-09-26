package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTOptimizationsOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-optimizations";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTOptimizationsOption() {
        super(27);
    }
}
