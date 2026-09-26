package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTDumpOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-dump";

    public ASTDumpOption() {
        super(63);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
