package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.MemberNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class EnumElementValue extends AnnotationElementValue {
    public ClassFileBase enumClass;
    public AbstractFieldInfo enumConstantField;
    public ConstantUtf8 typeNameConstant;
    public ConstantUtf8 constNameConstant;

    public String getEnumTypeName() {
        return this.typeNameConstant.getValueString();
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        if (this.enumClass != null) {
            this.typeNameConstant.setValue(ClassFileBase.toTypeDescriptor(this.enumClass.getClassName(), 0));
        }
    }

    @Override
    public int getByteLength() {
        return 5;
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
        IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = (IgnoreMissingReferencesSpec) object1;
        ClassResolver classResolver1 = (ClassResolver) object;
        MutableInt mutableInt = new MutableInt(0);
        String string = ClassFileBase.extractClassName(this.typeNameConstant.getValue(), mutableInt);
        if (string != null) {
            ClassFileBase classFileBase = this.getOwningClass();
            Integer integer = classFileBase.hasReleaseVersion() ? classFileBase.getReleaseVersion() : null;
            this.enumClass = classResolver1.getVersionedClass(
                    string, integer, "analyzing annotations in class '" + this.getDisplayLocationName() + "' (B)", ignoreMissingReferencesSpec1
            );
        }

        if (this.enumClass != null) {
            this.enumConstantField = this.enumClass.findField(this.constNameConstant.getValue(), this.typeNameConstant.getValue());
            if (this.enumConstantField == null
                    && (!HiddenOptionFlags.IGNORE_MISSING_MEMBERS || this.enumClass.isProgramClass())
                    && (
                    ignoreMissingReferencesSpec1 == null
                            || !ignoreMissingReferencesSpec1.isMissingFieldIgnored(
                            this.enumClass, this.constNameConstant.getValue(), this.typeNameConstant.getValue(), new ObservableHolder()
                    )
            )) {
                throw new MemberNotFoundException(
                        "Could not find field '"
                                + ConstantPoolEntry.descriptorToJavaType(this.typeNameConstant.getValue())
                                + " "
                                + this.constNameConstant.getValue()
                                + "' in class '"
                                + this.enumClass.getDisplayLocationName()
                                + "'. Please check the classpath and reopen your classes (C)."
                );
            }
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        Map map1 = (Map) object;
        dataOutputStream.writeByte(this.getTag());
        MutableInt mutableInt = new MutableInt(0);
        ClassFileBase.extractClassName(this.typeNameConstant.getValue(), mutableInt);
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.typeNameConstant);
        Map map2;
        ConstantUtf8 constantUtf82;
        if (constantUtf8 != null) {
            ClassFileBase.extractClassName(constantUtf8.getValue(), mutableInt);
            dataOutputStream.writeShort(constantUtf8.getIndex());
            map2 = map1;
            constantUtf82 = this.constNameConstant;
        } else {
            dataOutputStream.writeShort(this.typeNameConstant.getIndex());
            map2 = map1;
            constantUtf82 = this.constNameConstant;
        }

        ConstantUtf8 constantUtf81 = (ConstantUtf8) map2.get(constantUtf82);
        if (constantUtf81 != null) {
            dataOutputStream.writeShort(constantUtf81.getIndex());
        } else {
            dataOutputStream.writeShort(this.constNameConstant.getIndex());
        }
    }

    @Override
    public boolean isArrayOrClassValue() {
        return false;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.typeNameConstant == constantUtf8) {
            this.typeNameConstant = constantUtf81;
        } else if (this.constNameConstant == constantUtf8) {
            this.constNameConstant = constantUtf81;
        }
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    @Override
    public String getValueTypeDescriptor() {
        return this.typeNameConstant.getValue();
    }

    public String getEnumConstantName() {
        return this.constNameConstant.getValueString();
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.typeNameConstant.getIndex());
        dataOutputStream.writeShort(this.constNameConstant.getIndex());
    }

    @Override
    public void updateAfterFieldRename() {
        if (this.enumConstantField != null && !this.enumConstantField.getSourceName().equals(this.constNameConstant.getValue())) {
            this.constNameConstant.setValue(this.enumConstantField.getJvmName());
        }
    }

    @Override
    public void collectReferencedClasses(Object object) {
        if (this.isValid() && this.enumClass != null) {
            ((Set) object).add(this.enumClass);
        }
    }

    @Override
    public void updateAfterMethodRename() {
    }

    @Override
    public void collectReferencedMembers(Object object, Object object2, Object object1, Object object3) {
        if (this.enumClass != null && this.enumClass.isProgramClass()) {
            ((Set) object).add((ProgramClass) this.enumClass);
            if (this.enumConstantField != null && this.enumConstantField.isProgramMember()) {
                ((Set) object1).add((FieldInfo) this.enumConstantField);
            }
        }
    }

    public EnumElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantUtf8) {
            this.typeNameConstant = (ConstantUtf8) constantPoolEntry;
            listMultimap.addValue(this.typeNameConstant, this);
            int bc = classFileInputStream.readUnsignedShort();
            constantPoolEntry = this.getConstantPoolEntry(bc);
            if (constantPoolEntry != null && constantPoolEntry instanceof ConstantUtf8) {
                this.constNameConstant = (ConstantUtf8) constantPoolEntry;
                listMultimap.addValue(this.constNameConstant, this);
            } else {
                this.setValid();
                this.setErrorMessage(
                        "Invalid component enum value constant index : " + bc + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : "")
                );
            }
        } else {
            this.setValid();
            this.setErrorMessage("Invalid component enum name constant index : " + bb + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : ""));
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.typeNameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
        this.constNameConstant.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public String getClassValueName() {
        return null;
    }
}
