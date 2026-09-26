package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAllowAccessModificationOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-allowaccessmodification";

    public ASTAllowAccessModificationOption() {
        super(34);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
