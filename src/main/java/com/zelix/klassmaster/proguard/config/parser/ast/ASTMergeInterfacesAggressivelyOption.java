package com.zelix.klassmaster.proguard.config.parser.ast;

public class ASTMergeInterfacesAggressivelyOption extends UnsupportedProGuardOption {
    private static final String OPTION_NAME = "-mergeinterfacesaggressively";

    public ASTMergeInterfacesAggressivelyOption() {
        super(35);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }
}
