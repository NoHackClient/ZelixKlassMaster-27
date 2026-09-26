package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantLong;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class LongElementValue extends AnnotationElementValue {
    public ConstantLong longConstant;

    @Override
    public void updateAfterMethodRename() {
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.longConstant.getIndex());
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        long bd = ((long) ba << 48 | (long) bb << 32 >>> 16 | (long) bc << 48 >>> 48) ^ 126464898310994L;
        ConstantLong constantLong = this.longConstant;
        ClassFileComponent classFileComponent = this.getParent();
        constantLong.registerUsage(usedConstantsCollector, this, classFileComponent);
    }

    @Override
    public String getClassValueName() {
        return null;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
    }

    @Override
    public void updateAfterFieldRename() {
    }

    public LongElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantLong) {
            this.longConstant = (ConstantLong) constantPoolEntry;
        } else {
            this.setValid();
            this.setErrorMessage("Invalid component long constant index : " + bb + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : ""));
        }
    }

    @Override
    public void collectReferencedMembers(Object object, Object object1, Object object2, Object object3) {
    }

    @Override
    public String getValueTypeDescriptor() {
        return String.valueOf((char) this.getTag());
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.longConstant.getIndex());
    }

    @Override
    public boolean isArrayOrClassValue() {
        return false;
    }

    @Override
    public void collectReferencedClasses(Object object) {
    }

    @Override
    public int getByteLength() {
        return 3;
    }
}
