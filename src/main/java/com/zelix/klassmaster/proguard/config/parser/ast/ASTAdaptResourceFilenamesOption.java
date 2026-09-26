package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAdaptResourceFilenamesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-adaptresourcefilenames";

    public ASTAdaptResourceFilenamesOption() {
        super(53);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
