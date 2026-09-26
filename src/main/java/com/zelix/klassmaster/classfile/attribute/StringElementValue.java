package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringElementValue extends AnnotationElementValue {
    public static TwoKeyMap specialElementKinds = new TwoKeyMap();
    public FieldInfo referencedField;
    public ConstantUtf8 stringConstant;

    @Override
    public void collectReferencedClasses(Object object) {
        if (this.isValid()) {
            String string = ConstantPoolEntry.parseClassNameCandidate(this.stringConstant.getValue());
            if (string != null) {
                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile(string);
                if (classFileBase != null) {
                    ((Set) object).add(classFileBase);
                }
            }
        }
    }

    @Override
    public void updateAfterClassRename(Object object, Object object1) {
        HashMap hashMap = (HashMap) object1;
        HashMap hashMap1 = (HashMap) object;
        AnnotationNameKind annotationNameKind = (AnnotationNameKind) specialElementKinds.getValue(this.getAnnotationTypeName(), this.getElementName());
        boolean bl = annotationNameKind != null && annotationNameKind.equals(AnnotationNameKind.PACKAGE);
        String string = this.stringConstant.getValue();
        if (bl) {
            String string1 = ConstantPoolEntry.remapQualifiedName(string, hashMap1, true);
            if (!string1.equals(string)) {
                this.stringConstant.setValue(string1);
            }
        } else if (annotationNameKind != AnnotationNameKind.FIELD) {
            BooleanFlag booleanFlag = new BooleanFlag();
            String string2 = ConstantPoolEntry.remapClassNameString(string, hashMap, booleanFlag);
            if (!string2.equals(string)) {
                this.stringConstant.setValue(string2);
            } else if (!booleanFlag.getValue()) {
                string2 = ConstantPoolEntry.remapQualifiedName(string, hashMap1, false);
                if (!string2.equals(string)) {
                    this.stringConstant.setValue(string2);
                }
            }
        }
    }

    @Override
    public void writeRemapped(DataOutputStream dataOutputStream, Object object, Object object1) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) ((Map) object).get(this.stringConstant);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.stringConstant.getIndex());
        }
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.stringConstant == constantUtf8) {
            this.stringConstant = constantUtf81;
        }
    }

    @Override
    public String getValueTypeDescriptor() {
        return "Ljava/lang/String;";
    }

    static {
        specialElementKinds.putValue("org/springframework/context/annotation/ComponentScan", "basePackages", AnnotationNameKind.PACKAGE);
        specialElementKinds.putValue("org/springframework/context/annotation/ComponentScan", "value", AnnotationNameKind.PACKAGE);
        specialElementKinds.putValue("javax/xml/bind/annotation/XmlType", "propOrder", AnnotationNameKind.FIELD);
    }

    @Override
    public void resolveReferences(Object object, Object object1, Object object2) throws ZkmException, IOException {
        AnnotationNameKind annotationNameKind = (AnnotationNameKind) specialElementKinds.getValue(this.getAnnotationTypeName(), this.getElementName());
        if (annotationNameKind != null && annotationNameKind == AnnotationNameKind.FIELD) {
            FieldInfo fieldInfo = this.findReferencedField();
            if (fieldInfo != null) {
                this.referencedField = fieldInfo;
            }
        }
    }

    @Override
    public int getByteLength() {
        return 3;
    }

    public String getStringValue() {
        return this.stringConstant.getValueString();
    }

    @Override
    public boolean isArrayOrClassValue() {
        return false;
    }

    @Override
    public boolean hasClassValue() {
        return false;
    }

    public FieldInfo findReferencedField() {
        ClassFileBase classFileBase = this.getOwningClass();
        if (classFileBase.isProgramClass()) {
            AbstractFieldInfo[] abstractFieldInfos = classFileBase.findFieldsByName(this.stringConstant.getValueString());
            if (abstractFieldInfos.length == 1) {
                return (FieldInfo) abstractFieldInfos[0];
            }
        }

        return null;
    }

    @Override
    public String getClassValueName() {
        return null;
    }

    @Override
    public void collectReferencedMembers(Object object, Object object2, Object object1, Object object3) {
        String string = ConstantPoolEntry.parseClassNameCandidate(this.stringConstant.getValue());
        TwoKeyMap twoKeyMap;
        if (string != null) {
            if (ClassHierarchyNode.isProgramClassName(string)) {
                ((Set) object).add(ClassHierarchyNode.findProgramClass(string));
                twoKeyMap = specialElementKinds;
            } else {
                twoKeyMap = specialElementKinds;
            }
        } else {
            twoKeyMap = specialElementKinds;
        }

        if ((AnnotationNameKind) twoKeyMap.getValue(this.getAnnotationTypeName(), this.getElementName()) == AnnotationNameKind.FIELD) {
            FieldInfo fieldInfo = this.findReferencedField();
            if (fieldInfo != null) {
                ((Set) object1).add(fieldInfo);
            }
        }
    }

    public void setStringValue(String string) {
        this.stringConstant.setValue(string);
    }

    @Override
    public void updateAfterFieldRename() {
        if (this.referencedField != null && !this.referencedField.getSourceName().equals(this.stringConstant.getValue())) {
            this.stringConstant.setValue(this.referencedField.getSourceName());
        }
    }

    public StringElementValue(ClassFileComponent classFileComponent, int ba, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws IOException {
        super(classFileComponent, ba);
        int bb = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = this.getConstantPoolEntry(bb);
        if (constantPoolEntry != null && constantPoolEntry instanceof ConstantUtf8) {
            this.stringConstant = (ConstantUtf8) constantPoolEntry;
            listMultimap.addValue(this.stringConstant, this);
        } else {
            this.setValid();
            this.setErrorMessage("Invalid component string constant index : " + bb + (constantPoolEntry != null ? " : tag=" + constantPoolEntry.getTag() : ""));
        }
    }

    @Override
    public void updateAfterMethodRename() {
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        this.stringConstant.registerUsage(usedConstantsCollector, this, this.getParent());
    }

    @Override
    public void write(int ba, DataOutputStream dataOutputStream, int bb, int bc) throws IOException {
        dataOutputStream.writeByte(this.getTag());
        dataOutputStream.writeShort(this.stringConstant.getIndex());
    }
}
