package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodEntry;
import com.zelix.klassmaster.util.ZkmAssert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ResolvedDynamicRef extends ResolvedDynamicConstantBase implements NameAndTypeHolder, ConstantReferenceVisitable {
    public BootstrapMethodEntry bootstrapMethod;
    public int bootstrapMethodIndex;
    public ResolvedNameAndType nameAndType;

    public String getStackReturnType() {
        return getStackReturnType(this.nameAndType);
    }

    public ConstantPoolEntry[] getBootstrapArguments() {
        return this.bootstrapMethod != null ? this.bootstrapMethod.copyArguments() : null;
    }

    public void collectReferencedClasses(Set set1, Set set2, Set set3, Set set4) {
        if (this.bootstrapMethod != null) {
            this.bootstrapMethod.collectReferencedClasses(set1, set2, set3, set4);
        }

        List list1 = this.nameAndType.getDescriptorClasses();
        if (this.nameAndType.getName().equals("<init>")) {
            set1.addAll(list1);
        } else {
            set2.addAll(list1);
        }
    }

    public void collectReferencedProgramClasses(Set set1, Set set2, Set set3, Set set4) {
        if (this.bootstrapMethod != null) {
            this.bootstrapMethod.collectReferencedProgramClasses(set1, set2, set3, set4);
        }

        List list1 = null;
        if (this.getTag().equals(ConstantPoolTag.INVOKE_DYNAMIC)) {
            list1 = this.nameAndType.getDescriptorProgramClasses();
        } else if (this.getTag().equals(ConstantPoolTag.CONSTANT_DYNAMIC)) {
            list1 = new ArrayList(1);
            ProgramClass programClass1 = ConstantPoolEntry.findProgramClassByDescriptor(this.nameAndType.getDescriptor());
            if (programClass1 != null) {
                list1.add(programClass1);
            }
        } else {
            ZkmAssert.assertTrue(false, new String[]{"Invalid tag : " + this.getTag() + " '" + this.getOwnerLocationDescription() + "'"});
        }

        ClassFileBase classFileBase = this.getOwnerClass();
        ResolvedNameAndType resolvedNameAndType;
        if (classFileBase.hasReleaseVersion()) {
            Integer integer = classFileBase.getReleaseVersion();
            ArrayList arrayList = new ArrayList(list1.size());
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator.next();
                if (programClass2.hasVersionedVariants()) {
                    arrayList.add((ProgramClass) programClass2.selectVersionForRelease(integer));
                } else {
                    arrayList.add(programClass2);
                }
            }

            list1 = arrayList;
            resolvedNameAndType = this.nameAndType;
        } else {
            resolvedNameAndType = this.nameAndType;
        }

        if (resolvedNameAndType.getName().equals("<init>")) {
            set1.addAll(list1);
        } else {
            set2.addAll(list1);
        }
    }

    public int getBootstrapMethodIndex() {
        return this.bootstrapMethod != null ? this.bootstrapMethod.getIndex() : this.bootstrapMethodIndex;
    }

    public BootstrapMethodEntry getBootstrapMethod() {
        return this.bootstrapMethod;
    }

    public ResolvedDynamicRef(int ba, AbstractConstantPool abstractConstantPool, int bootstrapMethodIndex, ResolvedNameAndType resolvedNameAndType) {
        super(ba, abstractConstantPool);
        this.bootstrapMethodIndex = bootstrapMethodIndex;
        this.nameAndType = resolvedNameAndType;
    }

    public boolean setBootstrapMethod(BootstrapMethodEntry bootstrapMethodEntry) {
        boolean bl = false;
        if (this.bootstrapMethod == null || this.bootstrapMethod != bootstrapMethodEntry) {
            bl = true;
        }

        this.bootstrapMethod = bootstrapMethodEntry;
        return bl;
    }

    public ResolvedDynamicRef(int ba, ResolvedNameAndType resolvedNameAndType, ResolvedInvokeDynamic resolvedInvokeDynamic) {
        super(ba, resolvedInvokeDynamic.constantPool);
        this.nameAndType = resolvedNameAndType;
        this.bootstrapMethod = resolvedInvokeDynamic.getBootstrapMethod();
        this.bootstrapMethodIndex = this.bootstrapMethod.getIndex();
    }

    public ResolvedDynamicRef(AbstractConstantPool abstractConstantPool, ResolvedNameAndType resolvedNameAndType, BootstrapMethodEntry bootstrapMethodEntry) {
        super(0, abstractConstantPool);
        this.nameAndType = resolvedNameAndType;
        this.bootstrapMethod = bootstrapMethodEntry;
        this.bootstrapMethodIndex = bootstrapMethodEntry.getIndex();
    }

    public int getResultValueCount() {
        return ConstantPoolEntry.getReturnDescriptor(this.nameAndType.getDescriptor()).equals("V") ? 0 : 1;
    }

    @Override
    public String getValueString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        stringBuilder.append(this.nameAndType.getValueString());
        stringBuilder.append(']');
        stringBuilder.append(" : ");
        if (this.bootstrapMethod != null) {
            stringBuilder.append(this.bootstrapMethod.describe());
        } else {
            stringBuilder.append(this.bootstrapMethodIndex);
        }

        return stringBuilder.toString();
    }

    public void renumberBootstrapMethods() {
        if (this.bootstrapMethod != null) {
            this.bootstrapMethod.renumberEntries();
        }
    }

    public String getNameAndTypeString() {
        return this.nameAndType.getValueString();
    }

    @Override
    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.getTag().getTagValue());
        if (this.bootstrapMethod != null) {
            dataOutputStream.writeShort(this.bootstrapMethod.getIndex());
        } else {
            dataOutputStream.writeShort(this.bootstrapMethodIndex);
        }

        dataOutputStream.writeShort(this.nameAndType.getIndex());
    }

    @Override
    public ResolvedNameAndType replaceNameAndType(ResolvedNameAndType resolvedNameAndType) {
        ResolvedNameAndType resolvedNameAndType1 = this.nameAndType;
        this.nameAndType = resolvedNameAndType;
        return resolvedNameAndType1;
    }

    public String getDescriptor() {
        return this.nameAndType.getDescriptor();
    }

    public ResolvedMethodHandleConstant getBootstrapMethodHandle() {
        return this.bootstrapMethod != null ? this.bootstrapMethod.getMethodHandle() : null;
    }

    @Override
    public void writeRemappedTo(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeByte(this.getTag().getTagValue());
        Map map2;
        ResolvedNameAndType resolvedNameAndType1;
        if (this.bootstrapMethod != null) {
            dataOutputStream.writeShort(this.bootstrapMethod.getIndex());
            map2 = map1;
            resolvedNameAndType1 = this.nameAndType;
        } else {
            dataOutputStream.writeShort(this.bootstrapMethodIndex);
            map2 = map1;
            resolvedNameAndType1 = this.nameAndType;
        }

        ResolvedNameAndType resolvedNameAndType = (ResolvedNameAndType) map2.get(resolvedNameAndType1);
        if (resolvedNameAndType != null) {
            dataOutputStream.writeShort(resolvedNameAndType.getIndex());
        } else {
            dataOutputStream.writeShort(this.nameAndType.getIndex());
        }
    }

    @Override
    public boolean registerUsage(UsedConstantsCollector usedConstantsCollector, Object object, Object object1) {
        return usedConstantsCollector.markUsed(this, object, object1);
    }

    public final ResolvedNameAndType getNameAndType() {
        return this.nameAndType;
    }
}
