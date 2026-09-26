package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class RemappedConstantPool extends AbstractConstantPool {
    private final Map replacementMap;

    @Override
    public boolean isProgramPool() {
        return true;
    }

    public RemappedConstantPool(AbstractConstantPool abstractConstantPool, ConstantPoolEntry[] constantPoolEntrys, Map map1) {
        super(abstractConstantPool.classFile);
        this.entries = constantPoolEntrys;
        this.replacementMap = map1;
        this.renumberEntries();
    }

    @Override
    public void renameThisClass(Object object) {
    }

    public void writeEntries(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.entries.length);

        for (int i = 1; i < this.entries.length; i++) {
            if (this.entries[i] != null) {
                this.entries[i].writeRemappedTo(dataOutputStream, this.replacementMap);
            }
        }
    }

    @Override
    public void applyPackageRenames(Object object) {
    }

    public Map getReplacementMap() {
        return this.replacementMap;
    }

    @Override
    public void applyClassRenames(Object object, Object object1) throws ZkmException, IOException {
    }
}
