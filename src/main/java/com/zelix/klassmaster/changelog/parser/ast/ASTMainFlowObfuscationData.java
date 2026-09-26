package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTMainFlowObfuscationData extends FlowObfuscationDataNode {
    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        if (super.className != null) {
            super.changeLog.addMainFlowObfuscationData(super.className, super.values);
        }
    }

    public ASTMainFlowObfuscationData() {
        super(41);
    }
}
