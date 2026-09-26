package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ClassConstantReplaceable;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ModuleProvidesEntry extends ClassFileComponent implements ClassConstantReplaceable {
    public ResolvedClassConstant serviceInterface;
    public ResolvedClassConstant[] implementations;

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.serviceInterface);
        DataOutputStream dataOutputStream1;
        ResolvedClassConstant[] resolvedClassConstants;
        if (constantPoolEntry != null) {
            dataOutputStream.writeShort(constantPoolEntry.getIndex());
            dataOutputStream1 = dataOutputStream;
            resolvedClassConstants = this.implementations;
        } else {
            dataOutputStream.writeShort(this.serviceInterface.getIndex());
            dataOutputStream1 = dataOutputStream;
            resolvedClassConstants = this.implementations;
        }

        dataOutputStream1.writeShort(resolvedClassConstants.length);

        for (ResolvedClassConstant resolvedClassConstant : this.implementations) {
            constantPoolEntry = (ConstantPoolEntry) map1.get(resolvedClassConstant);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(resolvedClassConstant.getIndex());
            }
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.serviceInterface, this, this.getParent());

        for (ResolvedClassConstant resolvedClassConstant : this.implementations) {
            usedConstantsCollector.markUsed(resolvedClassConstant, this, this.getParent());
        }
    }

    @Override
    public void replaceClassConstant(ResolvedClassConstant resolvedClassConstant, ResolvedClassConstant resolvedClassConstant1) {
        if (this.serviceInterface == resolvedClassConstant) {
            this.serviceInterface = resolvedClassConstant1;
        }

        int ba = 0;
        int bb = 0;

        for (ResolvedClassConstant[] resolvedClassConstants = this.implementations;
             bb < resolvedClassConstants.length;
             resolvedClassConstants = this.implementations
        ) {
            if (this.implementations[ba] == resolvedClassConstant) {
                this.implementations[ba] = resolvedClassConstant1;
            }

            bb = ++ba;
        }
    }

    public ModuleProvidesEntry(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(ba);
        if (constantPoolEntry == null) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + ba + " : File is probably corrupt (AF)"
            );
        }

        if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName()
                            + " : Invalid attribute : "
                            + ba
                            + " : '"
                            + constantPoolEntry.getClass().getName()
                            + "' : File is probably corrupt (AG)"
            );
        }

        this.serviceInterface = (ResolvedClassConstant) constantPoolEntry;
        listMultimap.addValue(this.serviceInterface, this);
        int bb = classFileInputStream.readUnsignedShort();
        this.implementations = new ResolvedClassConstant[bb];

        for (int i = 0; i < bb; i++) {
            int bd = classFileInputStream.readUnsignedShort();
            constantPoolEntry = classFileComponent.getConstantPoolEntry(bd);
            if (constantPoolEntry == null) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Illegal constant pool index in attribute : "
                                + bd
                                + " : File is probably corrupt (AH)"
                );
            }

            if (!(constantPoolEntry instanceof ResolvedClassConstant)) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Invalid attribute : "
                                + bd
                                + " : '"
                                + constantPoolEntry.getClass().getName()
                                + "' : File is probably corrupt (AI)"
                );
            }

            this.implementations[i] = (ResolvedClassConstant) constantPoolEntry;
            listMultimap.addValue(this.implementations[i], this);
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.serviceInterface.getIndex());
        dataOutputStream.writeShort(this.implementations.length);

        for (ResolvedClassConstant resolvedClassConstant : this.implementations) {
            dataOutputStream.writeShort(resolvedClassConstant.getIndex());
        }
    }
}
