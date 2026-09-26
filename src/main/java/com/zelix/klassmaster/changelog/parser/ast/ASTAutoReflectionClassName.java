package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ClassNameNodeSetter;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.NameValueHolder;

import java.io.IOException;

public class ASTAutoReflectionClassName extends ChangeLogEntryNode implements ClassNameNodeSetter, NameValueHolder {
    public String className;
    public String moduleName;

    @Override
    public void acceptClassName(Object object) {
        this.className = (String) object;
    }

    @Override
    public void setNameValue(String string) {
        this.moduleName = string;
    }

    @Override
    public void beginEntry(Object object, Object object1) {
    }

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        AbstractChangeLog abstractChangeLog = (AbstractChangeLog) object;
        AbstractChangeLog abstractChangeLog1;
        String string;
        if (this.moduleName != null) {
            if (this.moduleName.length() > 0) {
                abstractChangeLog.addModuleAutoReflectionClass(this.className, this.moduleName);
                return;
            }

            abstractChangeLog1 = abstractChangeLog;
            string = this.className;
        } else {
            abstractChangeLog1 = abstractChangeLog;
            string = this.className;
        }

        abstractChangeLog1.setAutoReflectionClass(string);
    }

    public ASTAutoReflectionClassName() {
        super(1);
    }
}
