package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantFloat;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatElementValue extends AnnotationElementValue {
    public ConstantFloat floatConstant;

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.floatConstant.getIndex());
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
    }

    @Override
    public int getByteLength() {
        return 3;
    }

    @Override
    public void collectReferencedClasses(Object object) {
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
    }

    @Override
    public String getValueTypeDescriptor() {
        return String.valueOf((char) this.getTag());
    }

    @Override
    public boolean isArrayOrClassValue() {
        return false;
    }

    @Override
    public void updateAfterFieldRename() {
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.floatConstant.getIndex());
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
    }

    @Override
    public String getClassValueName() {
        return null;
    }

    public FloatElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantFloat) {
            this.floatConstant = (ConstantFloat) constantPoolEntry;
        } else {
            this.setValid();
            this.setErrorMessage("Invalid component float constant index : " + bb + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : ""));
        }
    }

    @Override
    public void collectReferencedMembers(Object object, Object object1, Object object2, Object object3) {
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    @Override
    public void updateAfterMethodRename() {
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        long bd = ((long) ba << 48 | (long) bb << 32 >>> 16 | (long) bc << 48 >>> 48) ^ 126464898310994L;
        ConstantFloat constantFloat = this.floatConstant;
        ClassFileComponent classFileComponent = this.getParent();
        constantFloat.registerUsage(usedConstantsCollector, this, classFileComponent);
    }
}
