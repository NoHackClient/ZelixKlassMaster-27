package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTKeepDirectoriesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-keepdirectories";

    public ASTKeepDirectoriesOption() {
        super(10);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
