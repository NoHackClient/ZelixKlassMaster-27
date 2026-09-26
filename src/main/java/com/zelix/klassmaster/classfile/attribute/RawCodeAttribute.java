package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;

public class RawCodeAttribute extends Attribute {
    public int maxStack;
    public int maxLocals;
    public int codeLength;
    public byte[] code;
    public int exceptionTableLength;
    public byte[] exceptionTableBytes;
    public int attributeCount;
    public byte[] attributeBytes;

    public int getAttributeCount() {
        return this.attributeCount;
    }

    public RawCodeAttribute(ClassFileComponent classFileComponent, int ba, String string, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, listMultimap);
        this.maxStack = classFileInputStream.readUnsignedShort();
        this.maxLocals = classFileInputStream.readUnsignedShort();
        this.codeLength = classFileInputStream.readInt();
        this.code = new byte[this.codeLength];
        classFileInputStream.read(this.code);
        this.exceptionTableLength = classFileInputStream.readUnsignedShort();
        this.exceptionTableBytes = new byte[this.exceptionTableLength * 8];
        classFileInputStream.read(this.exceptionTableBytes);
        this.attributeCount = classFileInputStream.readUnsignedShort();
        int bb = this.length - 8 - this.codeLength - 2 - this.exceptionTableBytes.length - 2;
        this.attributeBytes = new byte[bb];
        classFileInputStream.read(this.attributeBytes);
    }

    public int getMaxStack() {
        return this.maxStack;
    }

    public byte[] getExceptionTableBytes() {
        return this.exceptionTableBytes;
    }

    public byte[] getAttributeBytes() {
        return this.attributeBytes;
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    public byte[] getCode() {
        return this.code;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.nameConstant, this, this.getParent());
    }

    public int getExceptionTableLength() {
        return this.exceptionTableLength;
    }
}
