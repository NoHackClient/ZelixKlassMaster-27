package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAdaptKotlinMetadataOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-adaptkotlinmetadata";

    public ASTAdaptKotlinMetadataOption() {
        super(26);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
