package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.classfile.ClassFileBase;

public class ClassListEntry {
    public final ClassFileBase classFile;
    public final boolean application;

    @Override
    public String toString() {
        return this.getClassName();
    }

    public String getClassName() {
        return this.classFile.getDottedClassName();
    }

    public ClassListEntry(ClassFileBase classFileBase, boolean application) {
        this.classFile = classFileBase;
        this.application = application;
    }

    public boolean isApplication() {
        return this.application;
    }

    public ClassFileBase getClassFile() {
        return this.classFile;
    }
}
