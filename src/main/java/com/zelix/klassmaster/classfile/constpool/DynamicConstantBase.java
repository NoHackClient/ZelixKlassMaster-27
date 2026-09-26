package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DynamicConstantBase extends ResolvedDynamicConstantBase implements ResolvableConstant {
    public final int bootstrapMethodIndex;
    public final int nameAndTypeIndex;

    public DynamicConstantBase(int ba, ClassFileInputStream classFileInputStream, AbstractConstantPool abstractConstantPool) throws IOException {
        super(ba, abstractConstantPool);
        this.bootstrapMethodIndex = classFileInputStream.readUnsignedShort();
        this.nameAndTypeIndex = classFileInputStream.readUnsignedShort();
    }

    @Override
    public boolean isUnresolved() {
        return true;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.getTag().getTagValue());
        dataOutputStream.writeShort(this.bootstrapMethodIndex);
        dataOutputStream.writeShort(this.nameAndTypeIndex);
    }
}
