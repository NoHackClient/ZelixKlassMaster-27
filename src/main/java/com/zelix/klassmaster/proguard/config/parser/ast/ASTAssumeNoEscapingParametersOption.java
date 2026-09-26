package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTAssumeNoEscapingParametersOption extends ProGuardAssumeMethodOption {
    private static final String OPTION_NAME = "-assumenoescapingparameters";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    public ASTAssumeNoEscapingParametersOption() {
        super(30);
    }
}
