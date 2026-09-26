package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ClassNameNodeSetter;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.NameValueHolder;

import java.io.IOException;

public class ASTObfuscateReferencesClassName extends ChangeLogEntryNode implements ClassNameNodeSetter, NameValueHolder {
    public String moduleName;
    public String className;

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        AbstractChangeLog abstractChangeLog = (AbstractChangeLog) object;
        AbstractChangeLog abstractChangeLog1;
        String string;
        if (this.moduleName != null) {
            if (this.moduleName.length() > 0) {
                abstractChangeLog.addModuleReferenceObfuscationClass(this.className, this.moduleName);
                return;
            }

            abstractChangeLog1 = abstractChangeLog;
            string = this.className;
        } else {
            abstractChangeLog1 = abstractChangeLog;
            string = this.className;
        }

        abstractChangeLog1.setReferenceObfuscationClass(string);
    }

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

    public ASTObfuscateReferencesClassName() {
        super(2);
    }
}
