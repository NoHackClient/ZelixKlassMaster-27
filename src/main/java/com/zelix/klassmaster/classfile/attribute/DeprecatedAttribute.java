package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class DeprecatedAttribute extends Attribute {
    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
    }

    public DeprecatedAttribute(
            ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
    }
}
