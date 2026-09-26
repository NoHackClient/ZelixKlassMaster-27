package com.zelix.klassmaster.script.parser.ast;

import com.zelix.klassmaster.util.ArrayEnumeration;

import java.util.Enumeration;

public abstract class ParameterListStatement extends ParameterListStatementBase {
    public ParameterListStatement(int ba) {
        super(ba);
    }

    public final Enumeration getRenameFilterParameters() {
        int ba = super.parameters.size();
        ASTRenameFilterParameter[] aSTRenameFilterParameters = new ASTRenameFilterParameter[ba];

        for (int i = 0; i < ba; i++) {
            aSTRenameFilterParameters[i] = (ASTRenameFilterParameter) super.parameters.get(i);
        }

        return new ArrayEnumeration(aSTRenameFilterParameters);
    }
}
