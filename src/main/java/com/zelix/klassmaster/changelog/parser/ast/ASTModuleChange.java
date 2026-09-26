package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.NameValueHolder;

import java.io.IOException;

public class ASTModuleChange extends ChangeLogEntryNode implements NameValueHolder {
    public String moduleName;

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
    }

    @Override
    public void setNameValue(String string) {
        this.moduleName = string;
    }

    @Override
    public void beginEntry(Object object, Object object1) {
    }

    public ASTModuleChange() {
        super(4);
    }
}
