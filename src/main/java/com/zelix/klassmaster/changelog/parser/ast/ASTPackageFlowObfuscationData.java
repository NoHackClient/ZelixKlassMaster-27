package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTPackageFlowObfuscationData extends FlowObfuscationDataNode {
    public String packageName;

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        if (super.className != null) {
            if (this.packageName == null) {
                this.packageName = "";
            }

            super.changeLog.addPackageFlowObfuscationData(this.packageName, super.className, super.values);
        }
    }

    public void setPackageName(String string) {
        this.packageName = string;
    }

    public ASTPackageFlowObfuscationData() {
        super(42);
    }
}
