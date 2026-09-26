package com.zelix.klassmaster.proguard.config.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.config.parser.ProGuardConfigSimpleNode;

import java.io.IOException;

public abstract class ProGuardConfigOptionBase extends ProGuardConfigSimpleNode {
    public int lineNumber;

    public int getLineNumber() {
        return this.lineNumber;
    }

    public ProGuardConfigOptionBase getPreviousOption() {
        return ((ASTProduction8) this.jjtGetParent()).getPreviousOption();
    }

    public abstract String getOptionName();

    @Override
    public abstract void translate(Object object, Object object1) throws ZkmException, IOException;

    public ProGuardConfigOptionBase(int ba) {
        super(ba);
    }

    public abstract void translateOption(Object object) throws ZkmException, IOException;

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
