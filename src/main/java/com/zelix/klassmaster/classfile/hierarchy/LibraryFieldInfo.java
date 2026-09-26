package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.IgnoredAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.IOException;
import java.util.Map;

public class LibraryFieldInfo extends AbstractFieldInfo {
    @Override
    public boolean isProgramMember() {
        return false;
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    public void applyFieldRename(Map map1) throws ZkmException, IOException {
        FieldSignature fieldSignature = this.getSignature();
        FieldSignature fieldSignature1 = (FieldSignature) map1.get(fieldSignature);
        if (fieldSignature1 != null && !fieldSignature1.equals(fieldSignature)) {
            this.setName(fieldSignature1.getName());
        }
    }

    public LibraryFieldInfo(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent, classFileInputStream, listMultimap);
        this.attributes = new Attribute[this.attributeCount];

        for (int i = 0; i < this.attributeCount; i++) {
            int bb = classFileInputStream.readUnsignedShort();
            ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(bb);
            if (constantPoolEntry == null) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Illegal constant pool index in attribute : "
                                + bb
                                + " : File is probably corrupt (H)"
                );
            }

            if (!(constantPoolEntry instanceof ConstantUtf8)) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Invalid attribute : "
                                + bb
                                + " : '"
                                + constantPoolEntry.getClass().getName()
                                + "' : File is probably corrupt (I)"
                );
            }

            String string = ((ConstantUtf8) constantPoolEntry).getValue();
            this.attributes[i] = new IgnoredAttribute(classFileComponent, bb, string, classFileInputStream, listMultimap);
        }
    }

    public LibraryFieldInfo(ClassFileBase classFileBase, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, Attribute[] attributes1) {
        super(classFileBase, constantUtf8, constantUtf81, attributes1, 2);

        for (int i = 0; i < attributes1.length; i++) {
            attributes1[i].setParent(this);
        }
    }
}
