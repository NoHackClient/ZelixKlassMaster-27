package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTOptimizationPassesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-optimizationpasses";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTOptimizationPassesOption() {
        super(28);
    }
}
