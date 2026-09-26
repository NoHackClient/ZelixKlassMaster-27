package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTSkipNonPublicLibraryClassesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-skipnonpubliclibraryclasses";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTSkipNonPublicLibraryClassesOption() {
        super(7);
    }
}
