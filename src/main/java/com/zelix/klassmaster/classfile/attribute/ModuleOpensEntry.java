package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AccessFlags;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedConstantModule;
import com.zelix.klassmaster.classfile.constpool.ResolvedPackageConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.DataOutputStream;
import java.io.IOException;

public class ModuleOpensEntry extends ClassFileComponent {
    public ResolvedPackageConstant openedPackage;
    public final AccessFlags opensFlags;
    public final ResolvedConstantModule[] opensTo;

    public ModuleOpensEntry(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream) throws ClassFileFormatException, IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(ba);
        if (constantPoolEntry == null) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + ba + " : File is probably corrupt (Z)"
            );
        }

        if (!(constantPoolEntry instanceof ResolvedPackageConstant)) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName()
                            + " : Invalid attribute : "
                            + ba
                            + " : '"
                            + constantPoolEntry.getClass().getName()
                            + "' : File is probably corrupt (AA)"
            );
        }

        this.openedPackage = (ResolvedPackageConstant) constantPoolEntry;
        this.opensFlags = new AccessFlags(this, classFileInputStream);
        int bb = classFileInputStream.readUnsignedShort();
        this.opensTo = new ResolvedConstantModule[bb];

        for (int i = 0; i < bb; i++) {
            int bd = classFileInputStream.readUnsignedShort();
            constantPoolEntry = classFileComponent.getConstantPoolEntry(bd);
            if (constantPoolEntry == null) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Illegal constant pool index in attribute : "
                                + bd
                                + " : File is probably corrupt (AB)"
                );
            }

            if (!(constantPoolEntry instanceof ResolvedConstantModule)) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Invalid attribute : "
                                + bd
                                + " : '"
                                + constantPoolEntry.getClass().getName()
                                + "' : File is probably corrupt (AC)"
                );
            }

            this.opensTo[i] = (ResolvedConstantModule) constantPoolEntry;
        }

        if (!ZkmUtils.allUnique(this.opensTo)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid 'Module' attribute in class '");
            stringBuilder.append(this.getLocationName());
            stringBuilder.append("' : Duplicate 'open to' module names : ");
            int be = 0;
            int bf = 0;

            for (ResolvedConstantModule[] resolvedConstantModules = this.opensTo; bf < resolvedConstantModules.length; resolvedConstantModules = this.opensTo) {
                stringBuilder.append(this.opensTo[be].getValueString());
                if (be < this.opensTo.length - 1) {
                    stringBuilder.append(", ");
                }

                bf = ++be;
            }

            ZkmAssert.assertTrue(false, new String[]{stringBuilder.toString()});
        }
    }

    @Override
    public void collectUsedConstants(char bc, int bd, UsedConstantsCollector usedConstantsCollector, char be) {
        this.openedPackage.registerUsage(usedConstantsCollector, this, this.getParent());
        ResolvedConstantModule[] resolvedConstantModules = this.opensTo;
        int ba = resolvedConstantModules.length;

        for (int i = 0; i < ba; i++) {
            resolvedConstantModules[i].registerUsage(usedConstantsCollector, this, this.getParent());
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.openedPackage.getIndex());
        dataOutputStream.writeShort(this.opensFlags.getFlags());
        dataOutputStream.writeShort(this.opensTo.length);

        for (ResolvedConstantModule resolvedConstantModule : this.opensTo) {
            dataOutputStream.writeShort(resolvedConstantModule.getIndex());
        }
    }

    public void writeRemapped(DataOutputStream dataOutputStream) throws IOException {
        this.writeTo(dataOutputStream);
    }
}
