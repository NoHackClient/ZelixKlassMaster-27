package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTDontNoteOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-dontnote";

    public ASTDontNoteOption() {
        super(59);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
