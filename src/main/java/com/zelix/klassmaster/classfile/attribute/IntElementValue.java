package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantInteger;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.DataOutputStream;
import java.io.IOException;

public class IntElementValue extends AnnotationElementValue {
    public ConstantInteger intConstant;

    public Integer getIntValue() {
        return this.isValid() ? this.intConstant.getValue() : null;
    }

    @Override
    public void collectReferencedClasses(Object object) {
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.intConstant.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public int getByteLength() {
        return 3;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
    }

    @Override
    public void collectReferencedMembers(Object object, Object object1, Object object2, Object object3) {
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
    }

    @Override
    public void updateAfterMethodRename() {
    }

    public IntElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantInteger) {
            this.intConstant = (ConstantInteger) constantPoolEntry;
        } else {
            this.setValid();
            this.setErrorMessage("Invalid component constant index : " + bb + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : ""));
        }
    }

    @Override
    public String getValueTypeDescriptor() {
        return String.valueOf((char) this.getTag());
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.intConstant.getIndex());
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    @Override
    public String getClassValueName() {
        return null;
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.intConstant.getIndex());
    }

    @Override
    public boolean isArrayOrClassValue() {
        return false;
    }

    @Override
    public void updateAfterFieldRename() {
    }
}
