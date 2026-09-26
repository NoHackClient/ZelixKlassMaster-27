package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTRenameSourceFileAttributeOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-renamesourcefileattribute";

    public ASTRenameSourceFileAttributeOption() {
        super(50);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
