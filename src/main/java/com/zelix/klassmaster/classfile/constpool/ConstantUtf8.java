package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantUtf8 extends ConstantPoolEntry implements ConstantReferenceVisitable, ConstantKeyProvider {
    public static final ConstantPoolTag TAG = ConstantPoolTag.UTF8;
    public String value;

    public String getValue() {
        return this.value;
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    public ConstantUtf8(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        byte[] bb = new byte[classFileInputStream.readUnsignedShort()];
        classFileInputStream.read(bb);
        this.value = ZkmUtils.fromModifiedUtf8(bb);
        this.value = this.value.intern();
    }

    public ConstantUtf8 copyWithIndex(int ba) {
        return new ConstantUtf8(ba, this.constantPool, this.value);
    }

    @Override
    public String getAbbreviatedValue() {
        String string = ZkmUtils.escapeJavaString(this.getValueString());
        if (string.length() > 200) {
            string = string.substring(0, 50) + "...<" + (string.length() - 100) + " CHARS>..." + string.substring(string.length() - 50);
        }

        return string;
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    public final int getLength() {
        return this.value.length();
    }

    @Override
    public String getValueString() {
        return this.getValue();
    }

    @Override
    public String getConstantKey() {
        return this.value;
    }

    public ConstantUtf8(int ba, AbstractConstantPool abstractConstantPool, String string) {
        super(ba, abstractConstantPool);
        this.value = string;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        byte[] ba = ZkmUtils.toModifiedUtf8(this.value);
        ZkmAssert.assertTrue(
                ba.length <= 65535, new String[]{"String too long in class '" + this.getOwnerLocationDescription() + "' : " + ba.length + " : " + this.value.length()}
        );
        dataOutputStream.writeShort(ba.length);
        dataOutputStream.write(ba);
    }

    public void setValue(String string) {
        this.value = string;
        this.value = this.value.intern();
    }
}
