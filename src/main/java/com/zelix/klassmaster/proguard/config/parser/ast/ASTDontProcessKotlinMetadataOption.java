package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTDontProcessKotlinMetadataOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-dontprocesskotlinmetadata";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTDontProcessKotlinMetadataOption() {
        super(68);
    }
}
