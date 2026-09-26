package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTUseUniqueClassMemberNamesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-useuniqueclassmembernames";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTUseUniqueClassMemberNamesOption() {
        super(43);
    }
}
