package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTDontSkipNonPublicLibraryClassesOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-dontskipnonpubliclibraryclasses";

    public ASTDontSkipNonPublicLibraryClassesOption() {
        super(8);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
