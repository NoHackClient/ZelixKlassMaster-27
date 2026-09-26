package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTDontSkipNonPublicLibraryClassMembersOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-skipnonpubliclibraryclassmembers";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTDontSkipNonPublicLibraryClassMembersOption() {
        super(9);
    }
}
