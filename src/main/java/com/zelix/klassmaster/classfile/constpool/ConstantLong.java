package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantLong extends NumericConstantEntry implements ConstantKeyProvider {
    public static final ConstantPoolTag TAG = ConstantPoolTag.LONG;
    public long value;
    public boolean inProgramPool;

    public ConstantLong(int ba, AbstractConstantPool abstractConstantPool, ConstantLong constantLong1) {
        super(ba, abstractConstantPool);
        this.value = constantLong1.value;
        this.inProgramPool = constantLong1.inProgramPool;
    }

    public boolean isInProgramPool() {
        return this.inProgramPool;
    }

    @Override
    public String getValueString() {
        return String.valueOf(this.value);
    }

    @Override
    public String getConstantKey() {
        return String.valueOf(this.value);
    }

    @Override
    public void setValueFromString(String string) throws ZkmException, IOException {
        long ba;
        try {
            ba = Long.valueOf(string);
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidEditException(string + " is not a valid long");
        }

        this.value = ba;
        this.notifyObservers();
    }

    @Override
    public String getEditableValue() {
        return this.getValueString();
    }

    public ConstantLong(AbstractConstantPool abstractConstantPool, long value, boolean inProgramPool) {
        super(0, abstractConstantPool);
        this.value = value;
        this.inProgramPool = inProgramPool;
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    public ConstantLong(AbstractConstantPool abstractConstantPool, long value) {
        super(0, abstractConstantPool);
        this.value = value;
        this.inProgramPool = false;
    }

    @Override
    public String getTypeName() {
        return "long";
    }

    public boolean hasValue(long ba) {
        return this.value == ba;
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeLong(this.value);
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public ConstantLong(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.value = classFileInputStream.readLong();
        this.inProgramPool = abstractConstantPool.isProgramPool();
    }
}
