package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTKeepClassesWithMembersOption extends ProGuardKeepOptionBase {
    private static final String OPTION_NAME = "-keepclasseswithmembers";

    @Override
    public boolean requiresMatchingMembers() {
        return true;
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public boolean appliesToClass() {
        return true;
    }

    public ASTKeepClassesWithMembersOption() {
        super(17);
    }
}
