package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTZipAlignOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-zipalign";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTZipAlignOption() {
        super(66);
    }
}
