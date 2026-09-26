package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.AccessFlags;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.constpool.ResolvedConstantModule;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.classfile.constpool.Utf8ConstantReplaceable;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.util.ListMultimap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ModuleRequiresEntry extends ClassFileComponent implements Utf8ConstantReplaceable {
    public ResolvedConstantModule requiredModule;
    public final AccessFlags requiresFlags;
    public ConstantUtf8 requiredVersion;

    public ModuleRequiresEntry(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream, ListMultimap listMultimap) throws ClassFileFormatException, IOException {
        super(classFileComponent);
        int ba = classFileInputStream.readUnsignedShort();
        ConstantPoolEntry constantPoolEntry = classFileComponent.getConstantPoolEntry(ba);
        if (constantPoolEntry == null) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName() + " : Illegal constant pool index in attribute : " + ba + " : File is probably corrupt (AJ)"
            );
        }

        if (!(constantPoolEntry instanceof ResolvedConstantModule)) {
            throw new ClassFileFormatException(
                    classFileComponent.getOwningClass().getLocationName()
                            + " : Invalid attribute : "
                            + ba
                            + " : '"
                            + constantPoolEntry.getClass().getName()
                            + "' : File is probably corrupt (AK)"
            );
        }

        this.requiredModule = (ResolvedConstantModule) constantPoolEntry;
        this.requiresFlags = new AccessFlags(this, classFileInputStream);
        int bb = classFileInputStream.readUnsignedShort();
        if (bb != 0) {
            ConstantPoolEntry constantPoolEntry1 = classFileComponent.getConstantPoolEntry(bb);
            if (constantPoolEntry1 == null) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Illegal constant pool index in attribute : "
                                + bb
                                + " : File is probably corrupt (AL)"
                );
            }

            if (!(constantPoolEntry1 instanceof ConstantUtf8)) {
                throw new ClassFileFormatException(
                        classFileComponent.getOwningClass().getLocationName()
                                + " : Invalid attribute : "
                                + bb
                                + " : '"
                                + constantPoolEntry1.getClass().getName()
                                + "' : File is probably corrupt (AM)"
                );
            }

            this.requiredVersion = (ConstantUtf8) constantPoolEntry1;
            listMultimap.addValue(this.requiredVersion, this);
        }
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
        usedConstantsCollector.markUsed(this.requiredModule, this, this.getParent());
        if (this.requiredVersion != null) {
            usedConstantsCollector.markUsed(this.requiredVersion, this, this.getParent());
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(this.requiredModule.getIndex());
        dataOutputStream.writeShort(this.requiresFlags.getFlags());
        dataOutputStream.writeShort(this.requiredVersion == null ? 0 : this.requiredVersion.getIndex());
    }

    public void writeRemapped(DataOutputStream dataOutputStream, Map map1) throws IOException {
        dataOutputStream.writeShort(this.requiredModule.getIndex());
        dataOutputStream.writeShort(this.requiresFlags.getFlags());
        if (this.requiredVersion != null) {
            ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) map1.get(this.requiredVersion);
            if (constantPoolEntry != null) {
                dataOutputStream.writeShort(constantPoolEntry.getIndex());
            } else {
                dataOutputStream.writeShort(this.requiredVersion.getIndex());
            }
        } else {
            dataOutputStream.writeShort(0);
        }
    }

    @Override
    public void replaceUtf8Constant(ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81) {
        if (this.requiredVersion != null && this.requiredVersion == constantUtf8) {
            this.requiredVersion = constantUtf81;
        }
    }
}
