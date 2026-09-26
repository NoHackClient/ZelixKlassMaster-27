package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTKeepClassMembersOption extends ProGuardKeepOptionBase {
    private static final String OPTION_NAME = "-keepclassmembers";

    @Override
    public boolean appliesToClass() {
        return false;
    }

    @Override
    public boolean requiresMatchingMembers() {
        return false;
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTKeepClassMembersOption() {
        super(16);
    }
}
