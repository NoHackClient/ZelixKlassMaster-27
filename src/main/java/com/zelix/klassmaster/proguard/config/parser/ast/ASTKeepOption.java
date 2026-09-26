package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTKeepOption extends ProGuardKeepOptionBase {
    private static final String OPTION_NAME = "-keep";

    @Override
    public boolean appliesToClass() {
        return true;
    }

    public ASTKeepOption() {
        super(14);
    }

    @Override
    public boolean requiresMatchingMembers() {
        return false;
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
