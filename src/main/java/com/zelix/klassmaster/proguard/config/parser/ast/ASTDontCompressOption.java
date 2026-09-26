package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTDontCompressOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-dontcompress";

    public ASTDontCompressOption() {
        super(52);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
