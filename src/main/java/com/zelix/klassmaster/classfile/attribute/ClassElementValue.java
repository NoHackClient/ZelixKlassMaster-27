package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ClassElementValue extends AnnotationElementValue {
    public int arrayDimensions;
    public ClassFileBase referencedClass;
    public ConstantUtf8 classConstant;

    @Override
    public void updateAfterFieldRename() {
    }

    @Override
    public void updateAfterMethodRename() {
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.classConstant.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
        IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = (IgnoreMissingReferencesSpec) object1;
        ClassResolver classResolver1 = (ClassResolver) object;
        MutableInt mutableInt = new MutableInt(0);
        String string = ClassFileBase.extractClassName(this.classConstant.getValue(), mutableInt);
        this.arrayDimensions = mutableInt.getValue();
        if (string != null) {
            this.referencedClass = classResolver1.getClassFile(
                    string, "analyzing annotations in class '" + this.getDisplayLocationName() + "' (A)", ignoreMissingReferencesSpec1
            );
        }
    }

    public ClassElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantUtf8) {
            this.classConstant = (ConstantUtf8) constantPoolEntry;
            listMultimap.addValue(this.classConstant, this);
        } else {
            this.setValid();
            this.setErrorMessage("Invalid component string class index : " + bb + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : ""));
        }
    }

    @Override
    public String getValueTypeDescriptor() {
        return "Ljava/lang/Class;";
    }

    @Override
    public boolean isArrayOrClassValue() {
        return true;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.classConstant == constantUtf8) {
            this.classConstant = constantUtf81;
        }
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        if (this.referencedClass != null) {
            this.classConstant.setValue(ClassFileBase.toTypeDescriptor(this.referencedClass.getClassName(), this.arrayDimensions));
        }
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.classConstant.getIndex());
    }

    @Override
    public boolean hasClassValue() {
        return this.classConstant != null;
    }

    @Override
    public String getClassValueName() {
        if (this.classConstant != null) {
            String string = this.classConstant.getValue();
            return string.startsWith("[") ? string : string.substring(1, string.length() - 1);
        } else {
            return null;
        }
    }

    @Override
    public int getByteLength() {
        return 3;
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) ((Map) object).get(this.classConstant);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.classConstant.getIndex());
        }
    }

    @Override
    public void collectReferencedMembers(Object object, Object object1, Object object2, Object object3) {
        if (this.referencedClass != null && this.referencedClass.isProgramClass()) {
            ((Set) object).add((ProgramClass) this.referencedClass);
        }
    }

    @Override
    public void collectReferencedClasses(Object object) {
        if (this.isValid() && this.referencedClass != null) {
            ((Set) object).add(this.referencedClass);
        }
    }
}
