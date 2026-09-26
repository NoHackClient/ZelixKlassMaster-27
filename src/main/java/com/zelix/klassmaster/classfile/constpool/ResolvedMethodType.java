package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResolvedMethodType extends MethodTypeConstantBase implements Utf8ConstantReplaceable, ConstantReferenceVisitable {
    public ConstantUtf8 descriptorUtf8;

    public String getDescriptor() {
        return this.descriptorUtf8.getValue();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(MethodTypeConstantBase.TAG.getTagValue());
        dataOutputStream.writeShort(this.descriptorUtf8.getIndex());
    }

    @Override
    public String getValueString() {
        return this.getDescriptor();
    }

    public void collectReferencedProgramClasses(Set set1) {
        List list1 = ConstantPoolEntry.getReferencedProgramClasses(this.getDescriptor());
        if (this.constantPool.getClassFile().isVersionedVariant()) {
            ArrayList arrayList = new ArrayList(list1.size());
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                arrayList.add((ProgramClass) this.selectVersionedClass(classFileBase));
            }

            list1 = arrayList;
        }

        set1.addAll(list1);
    }

    public void remapDescriptor(HashMap hashMap) {
        String string = this.descriptorUtf8.getValue();
        String string1 = ConstantPoolEntry.remapDescriptorClassNames(string, hashMap);
        if (string1 != string) {
            this.descriptorUtf8.setValue(string1);
        }
    }

    public ResolvedMethodType(int ba, AbstractConstantPool abstractConstantPool, ConstantUtf8 constantUtf8) {
        super(ba, abstractConstantPool);
        this.descriptorUtf8 = constantUtf8;
    }

    @Override
    public void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(MethodTypeConstantBase.TAG.getTagValue());
        ConstantUtf8 constantUtf8 = (ConstantUtf8) map1.get(this.descriptorUtf8);
        if (constantUtf8 != null) {
            dataOutputStream.writeShort(constantUtf8.getIndex());
        } else {
            dataOutputStream.writeShort(this.descriptorUtf8.getIndex());
        }
    }

    public ConstantUtf8 getDescriptorUtf8() {
        return this.descriptorUtf8;
    }

    public void collectReferencedClasses(Set set1) {
        List list1 = ConstantPoolEntry.getReferencedClasses(this.getDescriptor());
        set1.addAll(list1);
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.descriptorUtf8 == constantUtf8) {
            this.descriptorUtf8 = constantUtf81;
        }
    }
}
