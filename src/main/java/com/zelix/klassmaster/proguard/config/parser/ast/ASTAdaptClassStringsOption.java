package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAdaptClassStringsOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-adaptclassstrings";

    public ASTAdaptClassStringsOption() {
        super(51);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
