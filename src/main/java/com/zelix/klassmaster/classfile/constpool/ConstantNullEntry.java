package com.zelix.klassmaster.classfile.constpool;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantNullEntry extends ConstantPoolEntry {
    public int slotCount = 1;

    private ConstantNullEntry(AbstractConstantPool abstractConstantPool) {
        super(0, abstractConstantPool);
    }

    @Override
    public String getValueString() {
        return this.getClass().getName();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
    }

    @Override
    public ConstantPoolTag getTag() {
        return ConstantPoolTag.NULL;
    }

    public static ConstantNullEntry getSharedInstance(AbstractConstantPool abstractConstantPool) {
        boolean bl = true;
        boolean bl1 = true;
        if (abstractConstantPool.nullEntries[0] == null) {
            abstractConstantPool.nullEntries[0] = new ConstantNullEntry(abstractConstantPool);
        }

        return abstractConstantPool.nullEntries[1 - 1];
    }

    @Override
    public int getSlotCount() {
        return this.slotCount;
    }
}
