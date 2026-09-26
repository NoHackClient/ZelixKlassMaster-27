package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantFloat extends NumericConstantEntry {
    public static final ConstantPoolTag TAG = ConstantPoolTag.FLOAT;
    public float value;

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    public ConstantFloat(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.value = classFileInputStream.readFloat();
    }

    @Override
    public String getEditableValue() {
        return this.getValueString();
    }

    @Override
    public void setValueFromString(String string) throws ZkmException, IOException {
        float ba;
        try {
            ba = Float.valueOf(string);
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidEditException(string + " is not a valid float");
        }

        this.value = ba;
        this.notifyObservers();
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeFloat(this.value);
    }

    @Override
    public String getValueString() {
        return String.valueOf(this.value);
    }

    @Override
    public String getTypeName() {
        return "float";
    }
}
