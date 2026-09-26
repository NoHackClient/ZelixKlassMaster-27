package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAdaptResourceFileContentsOption extends PartiallySupportedProGuardOption {
    private static final String OPTION_NAME = "-adaptresourcefilecontents";

    public ASTAdaptResourceFileContentsOption() {
        super(54);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
