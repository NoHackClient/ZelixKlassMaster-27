package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantDouble extends NumericConstantEntry {
    public static final ConstantPoolTag TAG = ConstantPoolTag.DOUBLE;
    public double value;

    public ConstantDouble(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.value = classFileInputStream.readDouble();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeDouble(this.value);
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    @Override
    public String getValueString() {
        return String.valueOf(this.value);
    }

    @Override
    public String getTypeName() {
        return "double";
    }

    @Override
    public void setValueFromString(String string) throws ZkmException, IOException {
        double ba;
        try {
            ba = Double.valueOf(string);
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidEditException(string + " is not a valid double");
        }

        this.value = ba;
        this.notifyObservers();
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public String getEditableValue() {
        return this.getValueString();
    }
}
