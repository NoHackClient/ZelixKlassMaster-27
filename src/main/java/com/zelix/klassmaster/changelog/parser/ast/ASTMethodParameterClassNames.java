package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ClassNameNodeSetter;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.NameValueHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASTMethodParameterClassNames extends ChangeLogEntryNode implements ClassNameNodeSetter, NameValueHolder {
    public String moduleName;
    public List classNames = new ArrayList();

    @Override
    public void beginEntry(Object object, Object object1) {
    }

    @Override
    public void acceptClassName(Object object) {
        this.classNames.add(object);
    }

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        AbstractChangeLog abstractChangeLog = (AbstractChangeLog) object;
        AbstractChangeLog abstractChangeLog1;
        List list1;
        if (this.moduleName != null) {
            if (this.moduleName.length() > 0) {
                abstractChangeLog.addModuleMethodParameterChangeClasses(this.classNames, this.moduleName);
                return;
            }

            abstractChangeLog1 = abstractChangeLog;
            list1 = this.classNames;
        } else {
            abstractChangeLog1 = abstractChangeLog;
            list1 = this.classNames;
        }

        abstractChangeLog1.setMethodParameterChangeClasses(list1);
    }

    public ASTMethodParameterClassNames() {
        super(3);
    }

    @Override
    public void setNameValue(String string) {
        this.moduleName = string;
    }
}
