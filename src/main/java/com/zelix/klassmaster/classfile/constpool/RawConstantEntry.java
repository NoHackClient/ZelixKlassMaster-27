package com.zelix.klassmaster.classfile.constpool;

import java.io.DataOutputStream;
import java.io.IOException;

public class RawConstantEntry extends ConstantPoolEntry {
    



    private RawConstantEntry() {
        super(0, null);
    }

    public byte[] rawBytes;
    public ConstantPoolTag tag;
    public int slotCount;
    private static final String TAG_LABEL = " tag=";

    @Override
    public boolean isWellFormed() {
        return false;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.write(this.rawBytes);
    }

    @Override
    public String getValueString() {
        return this.getClass().getName() + TAG_LABEL + this.tag;
    }

    @Override
    public ConstantPoolTag getTag() {
        return this.tag;
    }

    @Override
    public int getSlotCount() {
        return this.slotCount;
    }
}
