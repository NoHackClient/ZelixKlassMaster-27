package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ASTPackageChange extends ChangeLogEntryNode {
    public String oldPackageName;
    public String newPackageName;
    public boolean packageNameNotChanged;

    public void setPackageNameNotChanged() {
        this.packageNameNotChanged = true;
    }

    public ASTPackageChange() {
        super(5);
    }

    @Override
    public void beginEntry(Object object, Object object1) {
        ((AbstractChangeLog) object).incrementPackageCount();
    }

    public void setOldPackageName(String string) {
        this.oldPackageName = string;
    }

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        if (this.newPackageName == null) {
            if (this.packageNameNotChanged) {
                this.newPackageName = this.oldPackageName;
            } else {
                this.newPackageName = "";
            }
        }

        super.changeLog.addPackageMapping(this.newPackageName, this.oldPackageName);
    }

    public void setNewPackageName(String string) {
        this.newPackageName = string;
    }
}
