package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolvedNameAndType extends ConstantPoolEntry implements ConstantReferenceVisitable, Utf8ConstantReplaceable, ConstantKeyProvider {
    public ConstantUtf8 nameUtf8;
    public ConstantUtf8 descriptorUtf8;
    public static final ConstantPoolTag TAG = ConstantPoolTag.NAME_AND_TYPE;

    public ConstantUtf8 getDescriptorUtf8() {
        return this.descriptorUtf8;
    }

    public List getDescriptorClasses() {
        return ConstantPoolEntry.getReferencedClasses(this.getDescriptor());
    }

    public ConstantUtf8 getNameUtf8() {
        return this.nameUtf8;
    }

    @Override
    public void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.nameUtf8);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.nameUtf8.getIndex());
        }

        ConstantUtf8 constantUtf81 = (ConstantUtf8) map1.get(this.descriptorUtf8);
        if (constantUtf81 != null) {
            dataOutputStream.writeShort(constantUtf81.getIndex());
        } else {
            dataOutputStream.writeShort(this.descriptorUtf8.getIndex());
        }
    }

    public String getName() {
        return this.nameUtf8.getValue();
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    @Override
    public String getValueString() {
        String string = this.descriptorUtf8.getValueString();
        String string1 = this.nameUtf8.getValueString();
        String string2 = ConstantPoolEntry.descriptorToJavaType(string) + " " + string1;
        if (string.indexOf(40) == 0) {
            string2 = string2 + "(";
            List list1 = ConstantPoolEntry.getParameterTypes(string);

            for (int i = 0; i < list1.size(); i++) {
                string2 = string2 + ConstantPoolEntry.descriptorToJavaType((String) list1.get(i)) + (i < list1.size() - 1 ? ", " : "");
            }

            string2 = string2 + ")";
        }

        return string2;
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.nameUtf8 == constantUtf8) {
            this.nameUtf8 = constantUtf81;
        } else if (this.descriptorUtf8 == constantUtf8) {
            this.descriptorUtf8 = constantUtf81;
        }
    }

    public ResolvedNameAndType(ConstantNameAndType constantNameAndType, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        super(constantNameAndType.index, constantNameAndType.constantPool);
        this.nameUtf8 = constantUtf8;
        this.descriptorUtf8 = constantUtf81;
    }

    public List getDescriptorProgramClasses() {
        return ConstantPoolEntry.getReferencedProgramClasses(this.getDescriptor());
    }

    @Override
    public String getConstantKey() {
        return this.nameUtf8.getValueString() + '~' + this.descriptorUtf8.getValueString();
    }

    @Override
    public String getDisplayString() {
        String string = this.nameUtf8.getValueString();
        String string1 = this.descriptorUtf8.getValueString();
        String string2 = string;
        if (string1.indexOf(40) == 0) {
            string2 = string2 + "(";
            List list1 = ConstantPoolEntry.getParameterTypes(string1);

            for (int i = 0; i < list1.size(); i++) {
                string2 = string2 + ConstantPoolEntry.descriptorToJavaType((String) list1.get(i)) + (i < list1.size() - 1 ? ", " : "");
            }

            string2 = string2 + ")";
        }

        return string2;
    }

    public String getDescriptor() {
        return this.descriptorUtf8.getValue();
    }

    public void remapDescriptor(HashMap hashMap) {
        String string = this.descriptorUtf8.getValue();
        String string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap);
        if (string1 != string) {
            this.descriptorUtf8.setValue(string1);
        }
    }

    public ResolvedNameAndType(
            int ba, AbstractConstantPool abstractConstantPool, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, ListMultimap listMultimap
    ) {
        super(ba, abstractConstantPool);
        this.nameUtf8 = constantUtf8;
        this.descriptorUtf8 = constantUtf81;
        if (listMultimap != null) {
            listMultimap.addValue(constantUtf8, this);
            listMultimap.addValue(constantUtf81, this);
        }
    }

    public final void setDescriptor(String string) {
        this.descriptorUtf8.setValue(string);
    }

    public final void setName(String string) {
        this.nameUtf8.setValue(string);
    }

    @Override
    public ConstantPoolTag getTag() {
        return TAG;
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(TAG.getTagValue());
        dataOutputStream.writeShort(this.nameUtf8.getIndex());
        dataOutputStream.writeShort(this.descriptorUtf8.getIndex());
    }
}
