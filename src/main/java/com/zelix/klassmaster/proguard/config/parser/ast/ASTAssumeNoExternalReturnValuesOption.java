package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAssumeNoExternalReturnValuesOption extends ProGuardAssumeMethodOption {
    private static final String OPTION_NAME = "-assumenoexternalreturnvalues";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTAssumeNoExternalReturnValuesOption() {
        super(31);
    }
}
