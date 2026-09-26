package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantInteger extends NumericConstantEntry implements ConstantKeyProvider {
    public static final ConstantPoolTag TAG = ConstantPoolTag.INTEGER;
    public int value;
    public boolean inProgramPool;

    @Override
    public String getValueString() {
        return String.valueOf(this.value);
    }

    public ConstantInteger(int ba, AbstractConstantPool abstractConstantPool, ConstantInteger constantInteger1) {
        super(ba, abstractConstantPool);
        this.value = constantInteger1.value;
        this.inProgramPool = constantInteger1.inProgramPool;
    }

    @Override
    public String getEditableValue() {
        return this.getValueString();
    }

    @Override
    public String getTypeName() {
        return "integer";
    }

    @Override
    public String getConstantKey() {
        return String.valueOf(this.value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ConstantInteger(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.value = classFileInputStream.readInt();
        this.inProgramPool = abstractConstantPool.isProgramPool();
    }

    public ConstantInteger(AbstractConstantPool abstractConstantPool, int value, boolean inProgramPool) {
        super(0, abstractConstantPool);
        this.value = value;
        this.inProgramPool = inProgramPool;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeInt(this.value);
    }

    public boolean hasValue(int ba) {
        return this.value == ba;
    }

    public boolean isInProgramPool() {
        return this.inProgramPool;
    }

    @Override
    public void setValueFromString(String string) throws ZkmException, IOException {
        int ba;
        try {
            ba = Integer.parseInt(string);
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidEditException(string + " is not a valid integer");
        }

        this.value = ba;
        this.notifyObservers();
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }
}
